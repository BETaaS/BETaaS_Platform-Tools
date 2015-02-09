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
import java.util.Properties;
import java.util.UUID;

import org.apache.log4j.Logger;

import eu.betaas.taas.taasvmmanager.cloudsclients.VMRequest;
import eu.betaas.taas.taasvmmanager.configuration.TaaSVMMAnagerConfiguration;
import eu.betaas.taas.taasvmmanager.libvirt.LibVirtClient;
import eu.betaas.taas.taasvmmanager.occi.OCCIClient;
import eu.betaas.taas.taasvmmanager.occi.OCCIException;
import eu.betaas.taas.taasvmmanager.occi.client.OpenNebulaClient;
import eu.betaas.taas.taasvmmanager.occi.client.OpenStackClient;
import eu.betaas.taas.taasvmmanager.occi.datamodel.Compute;
import eu.betaas.taas.taasvmmanager.occi.datamodel.InstanceType;
import eu.betaas.taas.taasvmmanager.occi.datamodel.InstanceTypeCollection;
import eu.betaas.taas.taasvmmanager.occi.datamodel.Link;
import eu.betaas.taas.taasvmmanager.occi.datamodel.Network;
import eu.betaas.taas.taasvmmanager.occi.datamodel.NetworkCollection;
import eu.betaas.taas.taasvmmanager.occi.datamodel.StorageCollection;
import eu.betaas.taas.taasvmmanager.occi.datamodel.User;
import eu.betaas.taas.taasvmmanager.occi.datamodel.Compute.Disk;
import eu.betaas.taas.taasvmmanager.occi.datamodel.Compute.Nic;

public class VMsAllocatorManager 
{
	private static VMsAllocatorManager _instance = null;
	
	private HashMap<String, String> internalVMs;
	private HashMap<String, String> externalVMs;
	
	private static final String PROPFILE = 
			"/eu/betaas/taas/taasvmmanager/taasvmmanager.properties";
	private static final String TARGET = "/dev/sda";
	
	private String user, password, endpointUrl, networkUrl, storageUrl;
	private LibVirtClient libVirtClient;
	private OCCIClient occiClient;
	private static Logger log = Logger.getLogger("betaas.taas");
	
	private HashMap <UUID, OCCIClient> occiClients;
	
	public static synchronized VMsAllocatorManager instance (String idApplication) {
		if (_instance == null) {
			_instance = new VMsAllocatorManager(idApplication);
		}
		
		return _instance;
	}
	
	private VMsAllocatorManager (String idApplication) {
		user = idApplication;
	}
	
	public String createVM (VMRequest request) {
		//generate id
		UUID vmId = UUID.randomUUID();

		String imageName =
				TaaSVMMAnagerConfiguration.getInstantiatedImagesPath()
				+ vmId.toString() + ".img";
		
		//copy and rename image
		File originalImage = new File(TaaSVMMAnagerConfiguration.getBaseAppImagePath());
		File copiedImage = new File(imageName);
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
		
		//configure values from the request
		int cores = request.getCores();
		double memory = request.getMemory();
		
		//launch the vm
		libVirtClient.createVM("betaas-int-" + vmId.toString(),
				vmId, request.getMemory()*1024, request.getCores(), imageName);
		
		internalVMs.put("betaas-int-" + vmId.toString(), String.valueOf(vmId));
		
		return "betaas-" + vmId.toString();
	}
	
	public HashMap<InstanceType, Integer> getAvailability(boolean cpuPreference) {
		HashMap<InstanceType, Integer> available = new HashMap<InstanceType, Integer>();
		InstanceType[] instanceTypes;
		int avCpu, avMemory, cpuCapable, memoryCapable, capable, lastPos;
		
		try {
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
				
				available.put(instanceType, capable);
				avCpu    -= capable * instanceType.getCpu();
				avMemory -= capable * instanceType.getMemory();
			}
		} catch (OCCIException e) {
			e.printStackTrace();
			return null;
		}
		
		return available;
	}
	
	private class InstanceTypeComp implements Comparator<InstanceType> {
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

	public String createExtVM(VMRequest request) {
		UUID vmId = UUID.randomUUID();
		Compute compute = null;
		String  ip      = "";
		String  mac     = "";
		
		List <Compute.Nic> computeNics   = new ArrayList<Compute.Nic>();
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
			e.printStackTrace();
		}
		
		internalVMs.put("betaas-ext-" + vmId.toString(), compute.getHref());
		
		return "betaas-ext-" + vmId.toString();
	}

	public boolean deleteVM(String idVM) {
		Compute compute;
		String id;
		
		id = internalVMs.get(idVM);
		if (id != null) {
			
			internalVMs.remove(idVM);
			
			return true;
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
					} catch (OCCIException e) {	}
				}
				
				externalVMs.remove(idVM);
				
				return true;
			} else {
				// Not found
				return false;
			}
		}
	}

	public HashMap<InstanceType, Integer> getAvailability() {
		
		return null;
	}

	public boolean migrateVM(String vmId, String targetInfo) {
		
		return false;
	}

	public boolean sendVMs(List<String> vmIds) {
		
		return false;
	}
	
	public List<String> deleteAllVMs () {
		String id;
		ArrayList<String> pendingVMs = new ArrayList<String>();
		
		for (String idVM : internalVMs.keySet()) {
			id = internalVMs.get(idVM);
			if (!deleteVM(id)){
				pendingVMs.add(id);
			}
		}
		
		for (String idVM : externalVMs.keySet()) {
			id = externalVMs.get(idVM);
			if (!deleteVM(id)){
				pendingVMs.add(id);
			}
		}
		
		return pendingVMs;
	}
}
