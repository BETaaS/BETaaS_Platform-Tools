/**

Copyright 2014 ATOS SPAIN S.A.

Licensed under the Apache License, Version 2.0 (the License);
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

Authors :
Francisco Javier Nieto. Atos Research and Innovation, Atos SPAIN SA
@email francisco.nieto@atos.net 
Sergio García Villalonga. Atos Research and Innovation, Atos SPAIN SA
@email sergio.garciavillalonga@atos.net
**/

package eu.betaas.taas.taasvmmanager.vmsallocator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.apache.log4j.Logger;

import eu.betaas.taas.taasvmmanager.api.datamodel.Availability;
import eu.betaas.taas.taasvmmanager.api.datamodel.Availability.Situation;
import eu.betaas.taas.taasvmmanager.api.datamodel.Flavor;
import eu.betaas.taas.taasvmmanager.api.datamodel.InstanceType;
import eu.betaas.taas.taasvmmanager.api.datamodel.VMRequest;
import eu.betaas.taas.taasvmmanager.api.datamodel.Flavor.FlavorType;
import eu.betaas.taas.taasvmmanager.configuration.TaaSVMMAnagerConfiguration;
import eu.betaas.taas.taasvmmanager.libvirt.LibVirtClient;
import eu.betaas.taas.taasvmmanager.messaging.MessageManager;
import eu.betaas.taas.taasvmmanager.occi.OCCIClient;
import eu.betaas.taas.taasvmmanager.occi.OCCIException;
import eu.betaas.taas.taasvmmanager.occi.datamodel.Compute;
import eu.betaas.taas.taasvmmanager.occi.datamodel.InstanceTypeCollection;
import eu.betaas.taas.taasvmmanager.occi.datamodel.Link;
import eu.betaas.taas.taasvmmanager.occi.datamodel.Network;
import eu.betaas.taas.taasvmmanager.occi.datamodel.User;
import eu.betaas.taas.taasvmmanager.occi.datamodel.Compute.Disk;
import eu.betaas.taas.taasvmmanager.occi.datamodel.Compute.Nic;
import eu.betaas.taas.taasvmmanager.openstack.OpenStackClient;
import eu.betaas.taas.taasvmmanager.util.Quota;

public class VMsAllocatorManager {
	private static HashMap<String, String> internalVMs;
	private static HashMap<String, String> externalVMs;
	
	private static final String TARGET = "/dev/sda";
	
	private static String user, networkUrl, storageUrl;
	private static LibVirtClient libVirtClient;
	private static OCCIClient occiClient;
	private static Logger log = Logger.getLogger("betaas.taas");
	
	private static HashMap <UUID, OCCIClient> occiClients;
	private static MessageManager mManager;
	
	public static void init() {
		mManager = MessageManager.instance();
		try {
			internalVMs   = new HashMap<String, String>();
			externalVMs   = new HashMap<String, String>();
			libVirtClient = new LibVirtClient();
			occiClient    = new OpenStackClient();
		} catch (OCCIException e) {
			mManager.dependabilityPublish("Remote cloud is not available. "
					+ "Please check it is up, it is reachable, your credentials"
					+ " are ok and you have enough permissions."
					+ "The error is: \n" + e.getMessage());
			log.error("[VMsAllocatorManager] Remote cloud is not available. "
					+ "Please check it is up, it is reachable, your credentials"
					+ " are ok and you have enough permissions."
					+ "The error is: \n" + e.getMessage());
		} catch (Exception e) {
			mManager.dependabilityPublish("Local hypervisor not available, "
					+ "please check it is running and you have enough "
					+ "permissions. The error is: \n" + e.getMessage());
			log.error("[VMsAllocatorManager] Local hypervisor not available, "
					+ "please check it is running and you have enough "
					+ "permissions. The error is: \n" + e.getMessage());
		}
	}
	
	public static String createVM (VMRequest request) throws Exception {
		mManager.monitoringPublish("Creating virtual machine...");
		//generate id
		UUID uuid = UUID.randomUUID();

		String imageName =
				TaaSVMMAnagerConfiguration.getInstantiatedImagesPath() + "/"
				+ uuid.toString() + ".img";
		
		//copy and rename image
		String originalImageName;
		if (request.getImage().equals("app")) {
			originalImageName =
					TaaSVMMAnagerConfiguration.getBaseAppImagePath();
		} else if (request.getImage().equals("gateway")) {
			originalImageName =
					TaaSVMMAnagerConfiguration.getBaseGatewayImagePath();
		} else if (request.getImage().equals("bigdata")) {
			originalImageName =
					TaaSVMMAnagerConfiguration.getBaseBigDataImagePath();
		} else if (request.getImage().equals("storage")) {
			originalImageName =
					TaaSVMMAnagerConfiguration.getBaseStorageImagePath();
		} else {
			Exception e = new Exception("Bad image type");
			throw e;
		}
		
		File originalImage = new File(originalImageName);
		File copiedImage   = new File(imageName);
		try {
			InputStream in = new FileInputStream(originalImage);
			OutputStream out = new FileOutputStream(copiedImage);
			
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			
			in.close();
			out.close();
		} catch (FileNotFoundException e) {			
			log.error(e.getMessage());
		} catch (IOException e) {			
			log.error(e.getMessage());
		}
		
		// Configure values from the request. The flavor values 
		// are fixed, so the TaaSRM should know its parameters
		int cores = request.getCores();
		long memory = request.getMemory();
		
		Flavor flavor, selected, current;
		selected = null;
		current  = new Flavor(null, cores, memory, (long)0);
		for (FlavorType type: FlavorType.values()) {
			flavor = TaaSVMMAnagerConfiguration.getFlavor(type);
			
			if (current.equals(flavor))
				selected = flavor;
		}
		
		//launch the vm
		String vmId = "betaas-" + selected.getType().toString() + "-" + uuid;
		libVirtClient.createVM(vmId, uuid, selected, imageName);
			
		internalVMs.put(vmId, String.valueOf(uuid));
			
		mManager.monitoringPublish(
					"Virtual machine " + vmId + " created");
		return vmId;
	}
	
	public static List<Availability> getAvailability(boolean cpuPreference) {
		List<Availability> ret = new ArrayList<Availability>();
		Availability ext = new Availability();
		Availability loc = new Availability();
		HashMap<InstanceType, Long> available = new HashMap<InstanceType, Long>();
		HashMap<Flavor, Long> locAvailable = new HashMap<Flavor, Long>();
		InstanceType[] instanceTypes;
		Flavor[] flavors;
		int lastPos;
		long avCpu, avMemory, cpuCapable, memoryCapable, capable;
		
		try {
			if (occiClient != null) {
				List <Link> links = occiClient.getInstanceTypes().getInstanceType();
				instanceTypes = new InstanceType[links.size()];
				lastPos = 0;
				for (Link link : occiClient.getInstanceTypes().getInstanceType()) {
					instanceTypes[lastPos++] =
						occiClient.getInstanceType(link.getHref());
				}
				
				Arrays.sort(instanceTypes, new InstanceTypeComp(cpuPreference));
				
				User oneUser = occiClient.getUser(user);
				avCpu = oneUser.getQuota().getCpu() - oneUser.getUsage().getCpu();
				avMemory = oneUser.getQuota().getMemory() -
						   oneUser.getUsage().getMemory();
				
				for (InstanceType instanceType : instanceTypes) {
					cpuCapable    = avCpu    / instanceType.getCpu();
					memoryCapable = avMemory / instanceType.getMemory();				
					capable       = (cpuCapable < memoryCapable) ? cpuCapable :
					                                               memoryCapable;
					
					available.put(instanceType, capable);/*
					avCpu    -= capable * instanceType.getCpu();
					avMemory -= capable * instanceType.getMemory();*/
				}
				
				ext.setSituation(Situation.EXTERNAL_CLOUD);
				ext.setAvailableInstances(available);
				ret.add(ext);
			}
		} catch (OCCIException e) {
			e.printStackTrace();
			return null;
		}
		
		if (libVirtClient != null) {
			flavors= new Flavor[FlavorType.values().length];
			lastPos = 0;
			
			for (FlavorType flavor : FlavorType.values()) {
				flavors[lastPos++] =
						TaaSVMMAnagerConfiguration.getFlavor(flavor);
			}
			
			Arrays.sort(flavors, new FlavorComp(cpuPreference));
			
			Quota quota = libVirtClient.getQuota();
			
			avCpu    = (long) quota.getvCpu();
			avMemory = (long) quota.getMemory();
			
			for (Flavor flavor : flavors) {
				cpuCapable    = avCpu    / flavor.getvCpu();
				memoryCapable = avMemory / flavor.getMemory();
				capable       = (cpuCapable < memoryCapable) ? cpuCapable :
					memoryCapable;
				
				locAvailable.put(flavor, capable);/*
				avCpu    -= capable * flavor.getvCpu();
				avMemory -= capable * flavor.getMemory();*/
			}
			
			loc.setSituation(Situation.INTERNAL_CLOUD);
			loc.setAvailableFlavors(locAvailable);
			ret.add(loc);
		}
		
		return ret;
	}
	
	private static class InstanceTypeComp implements Comparator<InstanceType> {
		private boolean compareByCPU;
		
		public InstanceTypeComp (boolean compareByCPU) {
			this.compareByCPU = compareByCPU;
		}
		
		//@Override
		public int compare(InstanceType o1, InstanceType o2) {
			return (compareByCPU) ?
			    (new Integer(o1.getCpu())).compareTo(o2.getCpu()) :
			    (new Integer(o1.getMemory())).compareTo(o2.getMemory()) ;
		}
		
	}
	
	private static class FlavorComp implements Comparator<Flavor> {
		private boolean compareByCPU;
		
		public FlavorComp (boolean compareByCPU) {
			this.compareByCPU = compareByCPU;
		}
		
		//@Override
		public int compare(Flavor o1, Flavor o2) {
			return (compareByCPU) ?
			    (new Integer(o1.getvCpu())).compareTo(o2.getvCpu()) :
			    (new Long(o1.getMemory())).compareTo(o2.getMemory()) ;
		}
		
	}

	public static String createExtVM(VMRequest request) {
		UUID vmId = UUID.randomUUID();
		Compute compute = null;
		String  ip      = "";
		String  mac     = "";
		
		mManager.monitoringPublish("Creating external virtual machine...");
		
		List <Compute.Nic>  computeNics   = new ArrayList<Compute.Nic>();
		List <Compute.Disk> computeDisks = new ArrayList<Compute.Disk>();
		
		Network network;
		try {
			network = occiClient.getNetwork(networkUrl);
		
			Compute.Nic nic = occiClient.createComputeNic(network, ip, mac);
			computeNics.add(nic);
			
			Compute.Disk osDisk = occiClient.createComputeDisk(
					occiClient.getStorage(storageUrl), TARGET);
			computeDisks.add(osDisk);
			
			InstanceTypeCollection computeInstanceTypes =
					occiClient.getInstanceTypes();
			Link insTypeLink;
			InstanceType instanceType;
			InstanceType bestFit = null;
			for (int i = 0; i < computeInstanceTypes.getInstanceType().size()
							|| bestFit != null; i++) {
				insTypeLink = computeInstanceTypes.getInstanceType().get(i);
				instanceType = occiClient.getInstanceType(insTypeLink.getHref());
				
				if (instanceType.getCpu()   > request.getCores() &&
					instanceType.getMemory() > request.getMemory()) {
					bestFit = instanceType;
				}
			}
			
			compute = occiClient.createCompute("", 0, 0, bestFit,
			                                  computeDisks, computeNics);
		} catch (OCCIException e) {
			mManager.monitoringPublish("Problem creating external virtual machine...");
			e.printStackTrace();
		}
		
		mManager.monitoringPublish(
				"betaas-ext-" + vmId.toString()+ " created");
		
		return "betaas-ext-" + vmId.toString();
	}

	public static boolean deleteVM(String idVM) {
		Compute compute;
		String id;
		
		mManager.monitoringPublish("Deleting " + idVM + " virtual machine...");
		
		id = internalVMs.get(idVM);
		if (id != null) {
			internalVMs.remove(idVM);
			libVirtClient.deleteVM(UUID.fromString(id));
			
			String imageName =
					TaaSVMMAnagerConfiguration.getInstantiatedImagesPath() + "/"
					+ id.toString() + ".img";
			
			File f = new File(imageName);
			if (!f.exists())
			      throw new IllegalArgumentException(
			          "Delete: no such file or directory: " + imageName);
			boolean success = f.delete();

		    if (!success)
		      throw new IllegalArgumentException("Delete: deletion failed");
		} else {
			id = externalVMs.get(idVM);
			if (id != null) {
				for (OCCIClient client : occiClients.values()) {
					try	{
						compute = client.getCompute(id);
						client.deleteCompute(id);
						
						//TODO check if when a VM is deleted the storages and nets are
						for (Disk disk : compute.getDisk()) {
							client.deleteStorage(disk.getStorage().getHref());
						}
						
						for (Nic nic : compute.getNic()) {
							client.deleteNetwork(nic.getNetwork().getHref());
						}
					} catch (OCCIException e) {	
						mManager.monitoringPublish("Problem deleting external virtual machine...");
					}
				}
				
				externalVMs.remove(idVM);
			} else {
				mManager.monitoringPublish("Bad VM id to delete...");
				return false;
			}
		}
		
		mManager.monitoringPublish(
				idVM + " virtual machine successfully deleted...");
		
		return true;
	}

	public static List<Availability> getAvailability() {
		
		return null;
	}

	public static boolean migrateVM(String vmId, String targetInfo) {
		mManager.monitoringPublish(
			"Migrating " + vmId + " virtual machine to " + targetInfo + " ...");
		return libVirtClient.migrate(vmId, targetInfo);
	}

	public static boolean sendVMs(List<String> vmIds, String targetInfo) {
		for (String vmId : vmIds) {
			mManager.monitoringPublish(
					"Migrating " + vmId + " virtual machine to " + targetInfo + " ...");
		    libVirtClient.migrate(vmId, targetInfo);
		}
		
		return true;
	}
	
	public static List<String> deleteAllVMs () {
		String id;
		ArrayList<String> pendingVMs = new ArrayList<String>();
		
		mManager.monitoringPublish("Deleting all virtual machines...");
		
		List<String> ids = new ArrayList<String>(internalVMs.keySet());
		for (String idVM : ids) {
			if (!deleteVM(idVM)){
				pendingVMs.add(idVM);
			}
		}
		
		for (String idVM : externalVMs.keySet()) {
			id = externalVMs.get(idVM);
			if (!deleteVM(id)){
				pendingVMs.add(id);
			}
		}
		
		if (pendingVMs.isEmpty()) {
			mManager.monitoringPublish(
				"All virtual machines successfully deleted...");
			log.info("All virtual machines successfully deleted...");
		} else {
			mManager.monitoringPublish(
					"Unable to delete all virtual machines. "
				  + "Pending virtual machines: " + pendingVMs.toString());
			log.info("Unable to delete all virtual machines. "
				  + "Pending virtual machines: " + pendingVMs.toString());
		}
		
		return pendingVMs;
	}
}
