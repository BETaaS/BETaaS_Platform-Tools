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

package eu.betaas.taas.taasvmmanager.occi.client;

import java.math.BigInteger;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.openstack4j.api.Builders;
import org.openstack4j.api.OSClient;
import org.openstack4j.api.compute.ServerService;
import org.openstack4j.model.compute.Action;
import org.openstack4j.model.compute.Flavor;
import org.openstack4j.model.compute.Image;
import org.openstack4j.model.compute.Limits;
import org.openstack4j.model.compute.Server;
import org.openstack4j.model.compute.Server.Status;
import org.openstack4j.model.compute.builder.ServerCreateBuilder;
import org.openstack4j.model.identity.Role;
import org.openstack4j.model.identity.Tenant;
import org.openstack4j.model.network.IPVersionType;
import org.openstack4j.model.network.Subnet;
import org.openstack4j.model.storage.block.Volume;
import org.openstack4j.openstack.OSFactory;

import eu.betaas.taas.taasvmmanager.occi.OCCIClient;
import eu.betaas.taas.taasvmmanager.occi.OCCIException;
import eu.betaas.taas.taasvmmanager.occi.datamodel.Compute;
import eu.betaas.taas.taasvmmanager.occi.datamodel.Compute.Disk;
import eu.betaas.taas.taasvmmanager.occi.datamodel.Compute.Nic;
import eu.betaas.taas.taasvmmanager.occi.datamodel.ComputeCollection;
import eu.betaas.taas.taasvmmanager.occi.datamodel.ComputeState;
import eu.betaas.taas.taasvmmanager.occi.datamodel.InstanceType;
import eu.betaas.taas.taasvmmanager.occi.datamodel.InstanceTypeCollection;
import eu.betaas.taas.taasvmmanager.occi.datamodel.Link;
import eu.betaas.taas.taasvmmanager.occi.datamodel.Network;
import eu.betaas.taas.taasvmmanager.occi.datamodel.NetworkCollection;
import eu.betaas.taas.taasvmmanager.occi.datamodel.Storage;
import eu.betaas.taas.taasvmmanager.occi.datamodel.StorageCollection;
import eu.betaas.taas.taasvmmanager.occi.datamodel.StorageType;
import eu.betaas.taas.taasvmmanager.occi.datamodel.User;
import eu.betaas.taas.taasvmmanager.occi.datamodel.User.Quota;
import eu.betaas.taas.taasvmmanager.occi.datamodel.UserCollection;

import org.apache.log4j.Logger;

public class OpenStackClient implements OCCIClient {
	
	private static final String BETAAS_NETWORK = "betaas_network";
	private static final String BETAAS_TENANT = "betaas";
	private static final String BETAAS_TENANT_DESCRIPTION = 
			"BETaaS tenant for OpenStack instances";
	private static final String BETAAS_APP_ROLE = "app";
	
	private static Logger log = Logger.getLogger("betaas.taas");
	
	private OCCIClientStatus status;
	
	private OSClient os;
	private Tenant tenant;
	
	private HashMap <String, InstanceType> instanceTypes = new HashMap <String, InstanceType>();
	private HashMap <OCCIClient.BETAAS_IMAGES, String> imageIds = new HashMap <OCCIClient.BETAAS_IMAGES, String>();
	
	private org.openstack4j.model.network.Network betaasNetwork = null;
	private org.openstack4j.model.identity.User   user = null;
	
	public OpenStackClient (String endpointUrl, String username, String password) {
		InstanceType instanceType;
		Role memberRole;
		status = new OCCIClientStatus();
		
		os = OSFactory.builder()
				.endpoint(endpointUrl)
				.credentials(username, password)
				.tenantName(username)
				.authenticate();
		
		tenant = os.identity().tenants().getByName(BETAAS_TENANT);
		if (tenant == null) {
			tenant = os.identity().tenants().create(
					Builders.tenant()
					        .name(BETAAS_TENANT)
					        .description(BETAAS_TENANT_DESCRIPTION)
					        .build());
		}
		
		user = os.identity().users().get(username);
		
		if (user == null) {
			user = os.identity().users()
		              .create(Builders.user()
		                              .name(username)
		                              .password(password)
		                              .tenant(tenant).build());
			
			memberRole = os.identity().roles().getByName(BETAAS_APP_ROLE);
			os.identity().roles().addUserRole(tenant.getId(), user.getId(), memberRole.getId());
		} else {
			List<? extends Role> roles =
					os.identity().users().listRolesOnTenant(username, BETAAS_TENANT);
			if (roles.isEmpty()) {
				memberRole = os.identity().roles().getByName(BETAAS_APP_ROLE);
				os.identity().roles().addUserRole(tenant.getId(), user.getId(), memberRole.getId());
			}
		}
		
		for (org.openstack4j.model.network.Network network : os.networking().network().list()) {
			if (network.getName().equals(BETAAS_NETWORK)) {
				betaasNetwork = network;
			}
		}
		
		if (betaasNetwork == null) {
			betaasNetwork = os.networking().network()
					.create(Builders.network()
                    		        .name(BETAAS_NETWORK)
                    		        .tenantId(tenant.getId()).build());
		}
		
		instanceType = new InstanceType();
		instanceType.setCpu(APP_COMPUTE_VCPUS);
		instanceType.setMemory(APP_COMPUTE_RAM);
		instanceType.setName(APP_COMPUTE_NAME);
		instanceTypes.put(APP_COMPUTE_NAME, instanceType);
		instanceType = new InstanceType();
		instanceType.setCpu(APP_HIGH_COMPUTE_VCPUS);
		instanceType.setMemory(APP_HIGH_COMPUTE_RAM);
		instanceType.setName(APP_HIGH_COMPUTE_NAME);
		instanceTypes.put(APP_HIGH_COMPUTE_NAME, instanceType);
		instanceType = new InstanceType();
		instanceType.setCpu(COMPUTE_COMPUTE_VCPUS);
		instanceType.setMemory(COMPUTE_COMPUTE_RAM);
		instanceType.setName(COMPUTE_COMPUTE_NAME);
		instanceTypes.put(COMPUTE_COMPUTE_NAME, instanceType);
		instanceType = new InstanceType();
		instanceType.setCpu(STORAGE_COMPUTE_VCPUS);
		instanceType.setMemory(STORAGE_COMPUTE_RAM);
		instanceType.setName(STORAGE_COMPUTE_NAME);
		instanceTypes.put(STORAGE_COMPUTE_NAME, instanceType);
		
		for (Flavor flavor : os.compute().flavors().list()) {
			if (instanceTypes.keySet().contains(flavor.getName())) {
				instanceTypes.remove(flavor.getName());
			}
		}
		createInstanceTypes(instanceTypes.values());
		
		/**We assume that the VMs have been somehow already imported**/
		for (Image image : os.compute().images().list()) {
			if (BETAAS_IMAGES.valueOf(image.getName()) != null) {
				imageIds.put(BETAAS_IMAGES.valueOf(image.getName()), image.getId());
			}
		}
		
		status.setStatus(CloudStatus.OK);
	}
	
	public UserCollection getUsers() throws OCCIException {
		Link link;
		UserCollection ret = new UserCollection();
		List<? extends org.openstack4j.model.identity.User> users =
				os.identity().users().listTenantUsers("tenantId");
		
		for (org.openstack4j.model.identity.User user : users) {
			link = new Link();
			link.setHref(user.getId());
			link.setName(user.getName());
			ret.getUser().add(link);
		}
		
		return ret;
	}

	public User getUser(String id) throws OCCIException {
		User ret = null;
		Quota quota =new Quota();
		org.openstack4j.model.identity.User user =
				os.identity().users().get(id);
		Limits limits = os.compute().quotaSets().limits();
		quota.setCpu(limits.getAbsolute().getMaxTotalCores() - 
					 limits.getAbsolute().getTotalCoresUsed());
		quota.setNumVms(limits.getAbsolute().getMaxImageMeta() -
						limits.getAbsolute().getImageMetaUsed());
		quota.setMemory(limits.getAbsolute().getMaxTotalRAMSize() -
						limits.getAbsolute().getTotalRAMUsed());
		quota.setStorage(limits.getAbsolute().getMaxTotalVolumeGigabytes() -
						 limits.getAbsolute().getTotalVolumeGigabytesUsed());
		
		ret.setGroup(BETAAS_TENANT);
		ret.setHref(user.getId());
		//ret.setId(user.getId());
		ret.setName(user.getName());
		ret.setQuota(quota);
		
		return ret;
	}

	public NetworkCollection getNetworks() throws OCCIException {
		Link link;
		Subnet subnet;
		NetworkCollection ret = new NetworkCollection();
		List<? extends org.openstack4j.model.network.Network> networks =
				os.networking().network().list();
		
		for (org.openstack4j.model.network.Network network : networks) {
			for (String subnetId : network.getSubnets()) {
				subnet = os.networking().subnet().get(subnetId);
				link = new Link();
				link.setHref(subnet.getId());
				link.setName(subnet.getName());
				ret.getNetwork().add(link);
			}
		}
		
		return ret;
	}

	public Network getNetwork(String id) throws OCCIException {
		Link retUser;
		Network ret = null;
		
		retUser = new Link();
		retUser.setHref(user.getId());
		retUser.setName(user.getName());
		
		Subnet subnet = os.networking().subnet().get(id);
		
		ret = new Network();
		ret.setId(BigInteger.valueOf(Long.valueOf(subnet.getId())));
		ret.setAddress(subnet.getAllocationPools().get(0).getStart());
		ret.setDescription(subnet.getName());
		ret.setGroup(BETAAS_TENANT);
		ret.setHref(subnet.getId());
		ret.setName(subnet.getName());
		
		if (subnet.getCidr().endsWith("/8")) {
			ret.setSize("A");
		} else if (subnet.getCidr().endsWith("/16")) {
			ret.setSize("B");
		} else if (subnet.getCidr().endsWith("/24")) {
			ret.setSize("C");
		}
		ret.setUser(retUser);
		
		return ret;
	}

	public Network createNetwork(String name, String description,
			String address, String size) throws OCCIException {
		String mask, cidr, firstIP, lastIP;
		String[] ipComponents;
		
		ipComponents = address.split(".");
		
		if (ipComponents.length != 4) {
			OCCIException e = new OCCIException();
			e.setMessage("Malformed address network.");
			throw e;
		} else if (size.equals("A")) {
			mask = "8";
			cidr = ipComponents[0] + ".0.0.0/" + mask;
			firstIP = ipComponents[0] + ".0.0.1";
			lastIP = ipComponents[0] + ".255.255.254";
		} else if (size.equals("B")) {
			mask = "16";
			cidr = ipComponents[0] + ipComponents[1] + ".0.0/" + mask;
			firstIP = ipComponents[0] + ipComponents[1] + ".0.1";
			lastIP = ipComponents[0] + ipComponents[1] + ".255.254";
		} else if (size.equals("C")) {
			mask = "24";
			cidr = ipComponents[0] +
				   ipComponents[1] +
				   ipComponents[2] + ".0/" + mask;
			firstIP = ipComponents[0] + ipComponents[1] + ipComponents[2] + ".1";
			lastIP = ipComponents[0] + ipComponents[1] + ipComponents[2] + ".254";
		} else {
			OCCIException e = new OCCIException();
			e.setMessage("Bad network size, it must be A, B or C.");
			throw e;
		}
		
		
		
		Subnet subnet = os.networking().subnet().create(Builders.subnet()
                .name(name)
                .networkId(betaasNetwork.getId())
                .tenantId(tenant.getId())
                .addPool(firstIP, lastIP)
                .ipVersion(IPVersionType.V4)
                .cidr(cidr)
                .build());
		
		return null;
	}

	public void deleteNetwork(String id) throws OCCIException {
		os.networking().subnet().delete(id);
	}

	public StorageCollection getStorages() throws OCCIException {
		Storage storage;
		Link retUser;
		List<? extends Volume> volumes = os.blockStorage().volumes().list();
		StorageCollection ret = new StorageCollection();
		
		retUser = new Link();
		retUser.setHref(user.getId());
		retUser.setName(user.getName());
		
		for (Volume volume : volumes) {
			storage = new Storage();
			storage.setDescription(volume.getDescription());
			storage.setFstype(volume.getVolumeType()); //check
			storage.setGroup(volume.getZone()); //check
			storage.setHref(volume.getId()); //check
			//storage.setId(volume.getId());
			storage.setName(volume.getName());
			storage.setSize(String.valueOf(volume.getSize()));
			//storage.setType(volume.getVolumeType());
			storage.setUser(retUser);
		}
		
		return ret;
	}

	public Storage getStorage(String id) throws OCCIException {
		Link retUser;
		Storage ret = null;
		
		Volume volume = os.blockStorage().volumes().get(id);
		
		retUser = new Link();
		retUser.setHref(user.getId());
		retUser.setName(user.getName());
		
		ret = new Storage();
		ret.setDescription(volume.getDescription());
		ret.setFstype(volume.getVolumeType()); //check
		ret.setGroup(volume.getZone()); //check
		ret.setHref(volume.getId()); //check
		//storage.setId(volume.getId());
		ret.setName(volume.getName());
		ret.setSize(String.valueOf(volume.getSize()));
		//storage.setType(volume.getVolumeType());
		ret.setUser(retUser);
		
		return ret;
	}

	public Storage createStorage(String name, String description,
			StorageType type, int size, String fstype) throws OCCIException {
		Link retUser;
		Storage ret;
		Volume volume = Builders.volume()
		                           .bootable(type == StorageType.OS)
		                           .name(name)
		                           .description(description)
		                           .volumeType(fstype) //check
		                           .size(size)
		                           .build();
		
		volume = os.blockStorage().volumes().create(volume);
		
		retUser = new Link();
		retUser.setHref(user.getId());
		retUser.setName(user.getName());
		
		ret = new Storage();
		ret.setDescription(volume.getDescription());
		ret.setFstype(volume.getVolumeType()); //check
		ret.setGroup(volume.getZone()); //check
		ret.setHref(volume.getId()); //check
		//storage.setId(volume.getId());
		ret.setName(volume.getName());
		ret.setSize(String.valueOf(volume.getSize()));
		//storage.setType(volume.getVolumeType());
		ret.setUser(retUser);
		
		return ret;
	}

	public void deleteStorage(String id) throws OCCIException {
		os.blockStorage().volumes().delete(id);
	}

	public InstanceTypeCollection getInstanceTypes() throws OCCIException {
		InstanceType instanceType;
		InstanceTypeCollection ret = new InstanceTypeCollection();
		List<? extends Flavor> flavors = os.compute().flavors().list();
		
		for (Flavor flavor : flavors) {
			instanceType = new InstanceType();
			instanceType.setCpu(flavor.getVcpus());
			instanceType.setHref(flavor.getId());
			//instanceType.setId(value);
			instanceType.setMemory(flavor.getRam());
			instanceType.setName(flavor.getName());
		}
		
		return ret;
	}

	public InstanceType getInstanceType(String id) throws OCCIException {
		InstanceType ret;
		Flavor flavor = os.compute().flavors().get(id);
		
		ret = new InstanceType();
		ret.setCpu(flavor.getVcpus());
		ret.setHref(flavor.getId());
		//ret.setId(value);
		ret.setMemory(flavor.getRam());
		ret.setName(flavor.getName());
		
		return ret;
	}

	public Disk createComputeDisk(Storage storage, String target)
			throws OCCIException {
		Link storLink, saveAsLink;
		Disk ret = new Disk();
		
		storLink = new Link();
		storLink.setHref(storage.getHref());
		storLink.setName(storage.getName());
		
		//check
		saveAsLink = new Link();
		saveAsLink.setHref(storage.getHref());
		saveAsLink.setName(storage.getName());
		
		ret.setSaveAs(saveAsLink);
		ret.setStorage(storLink);
		ret.setTarget(target);
		ret.setType(storage.getType());
		
		return ret;
	}

	public Nic createComputeNic(Network network, String ip, String mac)
			throws OCCIException {
		Link netLink;
		Nic ret = new Nic();
		
		netLink = new Link();
		netLink.setHref(network.getHref());
		netLink.setName(network.getName());
		
		ret.setIp(ip);
		ret.setMac(mac);
		ret.setNetwork(netLink);
		
		return ret;
	}

	public ComputeCollection getComputes() throws OCCIException {
		Link link;
		ComputeCollection computeCollection = new ComputeCollection();
		List <? extends Image> computes = os.compute().images().list();
		
		for (Image image : computes) {
			link = new Link();
			
			link.setHref(image.getId());
			link.setName(image.getName());
		}
		
		return computeCollection;
	}

	public Compute getCompute(String id) throws OCCIException {
		Status status;
		ComputeState computeState;
		Link userLink = new Link();
		Compute compute = new Compute();
		Server server = os.compute().servers().get(id);

		userLink.setHref(user.getId());
		userLink.setName(user.getName());
		
		status = server.getStatus();
		
		switch (status) {
		case ACTIVE:
			computeState = ComputeState.ACTIVE;
			break;
		case BUILD:
			computeState = ComputeState.INIT;
			break;
		case DELETED:
			computeState = ComputeState.DONE;
			break;
		case ERROR:
			computeState = ComputeState.FAILED;
			break;
		case HARD_REBOOT:
			computeState = ComputeState.ACTIVE;
			break;
		case PASSWORD:
			computeState = ComputeState.ACTIVE;
			break;
		case PAUSED:
			computeState = ComputeState.STOPPED;
			break;
		case REBOOT:
			computeState = ComputeState.ACTIVE;
			break;
		case REBUILD:
			computeState = ComputeState.ACTIVE;
			break;
		case RESIZE:
			computeState = ComputeState.ACTIVE;
			break;
		case REVERT_RESIZE:
			computeState = ComputeState.ACTIVE;
			break;
		case STOPPED:
			computeState = ComputeState.STOPPED;
			break;
		case SUSPENDED:
			computeState = ComputeState.SUSPENDED;
			break;
		case UNKNOWN:
			computeState = ComputeState.FAILED;
			break;
		case UNRECOGNIZED:
			computeState = ComputeState.FAILED;
			break;
		case VERIFY_RESIZE:
			computeState = ComputeState.ACTIVE;
			break;
		default:
			computeState = ComputeState.FAILED;
			break;
		}
		
		//compute.setId(id);
		compute.setCpu(server.getFlavor().getVcpus());
		compute.setGroup(user.getTenantId());
		compute.setHref(id);
		compute.setInstanceType(server.getFlavor().getName());
		compute.setMemory(server.getFlavor().getRam());
		compute.setName(server.getName());
		compute.setState(computeState);
		compute.setUser(userLink);
		
		return null;
	}

	public Compute createCompute(String name, int cpu, int memory,
			InstanceType instanceType, List<Disk> disks, List<Nic> nic)
			throws OCCIException {
		ServerCreateBuilder builder = os.compute().servers().serverBuilder();
		Flavor flavor = os.compute().flavors().get(instanceType.getHref());
		
		builder.flavor(instanceType.getHref()).image(disks.get(0).getStorage().getHref()).name(name);
		
		return null;
	}

	public void changeComputeState(String id, BETaaSComputeState newState)
			throws OCCIException {
		Compute compute;
		ServerService service = os.compute().servers();
		OCCIException exception;
		if (newState == BETaaSComputeState.INIT ||
				newState == BETaaSComputeState.DONE ||
				newState == BETaaSComputeState.FAILED) {
			exception = new OCCIException();
			exception.setMessage(newState.toString() +
					" is not a valid state.");
		}
		
		compute = getCompute(id);
		
		switch (newState) {
		case ACTIVE:
			if (compute.getState().toString().equals(BETaaSComputeState.ACTIVE.toString())) {
				exception = new OCCIException();
				exception.setMessage(newState.toString() +
						" is not a valid state.");
			}
			service.action(id, Action.RESUME);
			
			break;
		case STOPPED:
			if (!compute.getState().toString().equals(BETaaSComputeState.ACTIVE.toString())) {
				exception = new OCCIException();
				exception.setMessage(newState.toString() +
						" is not a valid state.");
			}
			service.action(id, Action.STOP);
			
			break;
		case SUSPENDED:
			if (!compute.getState().toString().equals(BETaaSComputeState.ACTIVE.toString())) {
				exception = new OCCIException();
				exception.setMessage(newState.toString() +
						" is not a valid state.");
			}
			service.action(id, Action.SUSPEND);
			
			break;
		}
	}

	public void saveComputeDisk(Compute compute, String storageId, String name)
			throws OCCIException {
		OCCIException exception;
		if (os.compute().servers().get(compute.getHref()) != null) {
			os.compute().servers().createSnapshot(compute.getHref(), name);
		} else {
			exception = new OCCIException();
			exception.setMessage("The compute does not exist.");
			throw exception;
		}
	}

	public void deleteCompute(String id) throws OCCIException {
		OCCIException exception;
		if (os.compute().servers().get(id) != null) {
			os.compute().servers().delete(id);
		} else {
			exception = new OCCIException();
			exception.setMessage("The compute does not exist.");
			throw exception;
		}
	}

	private void createInstanceTypes (Collection<InstanceType> instanceTypes) {
		Flavor newFlavor;
		for (InstanceType instanceType : instanceTypes) {
			if (instanceType.getName().equals(APP_COMPUTE_NAME)) {
				newFlavor = os.compute().flavors().create(instanceType.getName(),
						instanceType.getMemory(), instanceType.getCpu(),
						APP_COMPUTE_DISK, 0, APP_COMPUTE_RAM, (float)1.0, true);
			} else if (instanceType.getName().equals(APP_HIGH_COMPUTE_NAME)) {
				newFlavor = os.compute().flavors().create(instanceType.getName(),
						instanceType.getMemory(), instanceType.getCpu(),
						APP_COMPUTE_DISK, 0, APP_COMPUTE_RAM, (float)1.0, true);
			} else if (instanceType.getName().equals(COMPUTE_COMPUTE_NAME)) {
				newFlavor = os.compute().flavors().create(instanceType.getName(),
						instanceType.getMemory(), instanceType.getCpu(),
						APP_COMPUTE_DISK, 0, APP_COMPUTE_RAM, (float)1.0, true);
			} else {
				newFlavor = os.compute().flavors().create(instanceType.getName(),
						instanceType.getMemory(), instanceType.getCpu(),
						APP_COMPUTE_DISK, 0, APP_COMPUTE_RAM, (float)1.0, true);
			}
			instanceType.setHref(newFlavor.getId());
		}
	}
	
	public OCCIClientStatus getStatus() {
		return status;
	}
}
