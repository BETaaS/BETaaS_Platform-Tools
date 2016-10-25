package eu.betaas.taas.taasvmmanager.openstack.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import com.google.gson.Gson;

import eu.betaas.taas.taasvmmanager.api.datamodel.InstanceType;
import eu.betaas.taas.taasvmmanager.occi.OCCIException;
import eu.betaas.taas.taasvmmanager.occi.datamodel.Compute;
import eu.betaas.taas.taasvmmanager.occi.datamodel.Compute.Disk;
import eu.betaas.taas.taasvmmanager.occi.datamodel.Compute.Nic;
import eu.betaas.taas.taasvmmanager.occi.datamodel.ComputeCollection;
import eu.betaas.taas.taasvmmanager.occi.datamodel.InstanceTypeCollection;
import eu.betaas.taas.taasvmmanager.occi.datamodel.Link;
import eu.betaas.taas.taasvmmanager.occi.datamodel.Network;
import eu.betaas.taas.taasvmmanager.occi.datamodel.NetworkCollection;
import eu.betaas.taas.taasvmmanager.occi.datamodel.Storage;
import eu.betaas.taas.taasvmmanager.occi.datamodel.StorageCollection;
import eu.betaas.taas.taasvmmanager.occi.datamodel.StorageType;
import eu.betaas.taas.taasvmmanager.occi.datamodel.User.Quota;
import eu.betaas.taas.taasvmmanager.openstack.gson.AccessContainer;
import eu.betaas.taas.taasvmmanager.openstack.gson.Flavor;
import eu.betaas.taas.taasvmmanager.openstack.gson.FlavorContainer;
import eu.betaas.taas.taasvmmanager.openstack.gson.FlavorsContainer;
import eu.betaas.taas.taasvmmanager.openstack.gson.Image;
import eu.betaas.taas.taasvmmanager.openstack.gson.ImageCollection;
import eu.betaas.taas.taasvmmanager.openstack.gson.InterfaceAttachmentsContainer;
import eu.betaas.taas.taasvmmanager.openstack.gson.LimitsContainer;
import eu.betaas.taas.taasvmmanager.openstack.gson.QuotaSetContainer;
import eu.betaas.taas.taasvmmanager.openstack.gson.InterfaceAttachmentsContainer.InterfaceAttachment;
import eu.betaas.taas.taasvmmanager.openstack.gson.NetworkContainer;
import eu.betaas.taas.taasvmmanager.openstack.gson.NetworksContainer;
import eu.betaas.taas.taasvmmanager.openstack.gson.ServerContainer;
import eu.betaas.taas.taasvmmanager.openstack.gson.ServersContainer;
import eu.betaas.taas.taasvmmanager.openstack.gson.Subnet;
import eu.betaas.taas.taasvmmanager.openstack.gson.SubnetContainer;
import eu.betaas.taas.taasvmmanager.openstack.gson.SubnetsContainer;
import eu.betaas.taas.taasvmmanager.openstack.gson.AccessContainer.Access.ServiceCatalog;
import eu.betaas.taas.taasvmmanager.openstack.gson.AccessContainer.Access.ServiceCatalog.Endpoint;
import eu.betaas.taas.taasvmmanager.openstack.gson.TenantsContainer;
import eu.betaas.taas.taasvmmanager.openstack.gson.TenantsContainer.Tenant;
import eu.betaas.taas.taasvmmanager.openstack.gson.Volume;
import eu.betaas.taas.taasvmmanager.openstack.gson.VolumeAttachmentsContainer;
import eu.betaas.taas.taasvmmanager.openstack.gson.VolumeAttachmentsContainer.VolumeAttachment;
import eu.betaas.taas.taasvmmanager.openstack.gson.VolumeContainer;
import eu.betaas.taas.taasvmmanager.openstack.gson.VolumesContainer;

public class OpenStackJSONParser extends OpenStackParser{
	private static Logger log = Logger.getLogger("betaas.taas");

	@Override
	public String[] parseAuthorizationResponse(String response)
			                                          throws OCCIException {
		String[] ret = new String[6];
		HashMap<String, String> urls = new HashMap<String, String>();
		String url;
		Gson gson = new Gson();
		AccessContainer accessContainer =
				gson.fromJson(response, AccessContainer.class);
		OCCIException exception;
		
		ret[0] = accessContainer.getAccess().getToken().getId();
		ret[1] = accessContainer.getAccess().getUser().getUsername();
		ret[2] = accessContainer.getAccess().getUser().getId();
		
		for (ServiceCatalog sc :
				accessContainer.getAccess().getServiceCatalog()) {
			for (Endpoint endpoint : sc.getEndpoints()) {
				url = endpoint.getPublicURL();
				if (url != null &&
                        !url.contains("/v3") &&
                        !url.contains("/v1")) {
					urls.put(sc.getName(), url);
				}
			}
		}
		
		ret[3] = urls.get("nova");
        ret[4] = urls.get("cinder");
        ret[5] = urls.get("neutron");
        
        if (ret[3] == null) {
        	ret[3] = urls.get("novav2");
        	
        	if (ret[3] == null) {
				exception = new OCCIException();
				exception.setMessage(
						"There is no compute service available publicly");
				throw exception;
        	}
        }
        ret[3] = ret[3].replaceAll("/v2/.*", "");
        
		if (ret[4] == null) {
			ret[4] = urls.get("cinderv2");
			
			if (ret[4] == null) {
				exception = new OCCIException();
				exception.setMessage(
						"There is no storage service available publicly");
				throw exception;
			}
		}
		ret[4] = ret[4].replaceAll("/v2/.*", "");
		
		if (ret[5] == null) {
			ret[5] = urls.get("neutronv2");
			
			if (ret[5] == null) {
				exception = new OCCIException();
				exception.setMessage(
						"There is no network service available publicly");
				throw exception;
			}
		}
		ret[5] = ret[5].replaceAll("/v2/.*", "");
		
		return ret;
	}

	public String parseTenantsResponse (String response, String tenantName) 
	                                                     throws OCCIException {
		String ret = null;
		Gson gson = new Gson();
		TenantsContainer container =
				gson.fromJson(response, TenantsContainer.class);
		
		for (Tenant tenant : container.getTenants()) {
			if (tenant.getName().equals(tenantName)) {
				ret = tenant.getId();
			}
		}
		
		if (ret == null) {
			OCCIException exception = new OCCIException();
            exception.setMessage(
            		String.format(BADRESOURCENAME, "tenant", tenantName));
            log.error(String.format(BADRESOURCENAME, "tenant", tenantName));
            throw exception;
		}
		
		return ret;
	}
	
	@Override
	public String[][] parseNetworkCollectionResponse(String response)
			throws OCCIException {
		String[][] ret;
		Gson gson = new Gson();
		NetworksContainer networksContainer =
				gson.fromJson(response, NetworksContainer.class);
		
		ret = new String[networksContainer.getNetworks().length][];
		for (int i = 0 ; i < networksContainer.getNetworks().length ; i++) {
			eu.betaas.taas.taasvmmanager.openstack.gson.Network network =
					networksContainer.getNetworks()[i];
					
			ret[i] = new String[network.getSubnets().length + 2];
			
			ret[i][0] = network.getId();
			ret[i][1] = network.getName();
			
			if (ret[i][0] == null || ret[i][1] == null) {
				OCCIException exception = new OCCIException();
	            exception.setMessage(MALFORMEDRESPONSE);
	            log.error(MALFORMEDRESPONSE);
	            throw exception;
			}
			
			for (int j = 0 ; j < network.getSubnets().length ; j++) {
				ret[i][j+2] = network.getSubnets()[j];
			}
		}
		
		return ret;
	}

	@Override
	public String[] parseCreatedNetworkResponse(String response)
			throws OCCIException {
		String[] ret = new String[2];
		Gson gson = new Gson();
		NetworkContainer networkContainer =
				gson.fromJson(response, NetworkContainer.class);
		
		ret[0] = networkContainer.getNetwork().getId();
		ret[1] = networkContainer.getNetwork().getName();
		
		if (ret[0] == null || ret[1] == null) {
			OCCIException exception = new OCCIException();
            exception.setMessage(MALFORMEDRESPONSE);
            log.error(MALFORMEDRESPONSE);
            throw exception;
		}
		
		return ret;
	}

	@Override
	public NetworkCollection parseSubnetCollectionResponse(String response)
			throws OCCIException {
		Link link;
		ArrayList<Link> links = new ArrayList<Link>();
		NetworkCollection ret = new NetworkCollection();
		Gson gson = new Gson();
		SubnetsContainer subnetsContainer =
				gson.fromJson(response, SubnetsContainer.class);
		
		for (Subnet subnet : subnetsContainer.getSubnets()) {
			link = new Link();
			
			if (subnet.getId() == null || subnet.getName() == null) {
				OCCIException exception = new OCCIException();
                exception.setMessage(MALFORMEDRESPONSE);
                log.error(MALFORMEDRESPONSE);
                throw exception;
        	}
			
			link.setHref(subnet.getId());
			link.setName(subnet.getName());
			links.add(link);
		}
		ret.getNetwork().addAll(links);
		
		return ret;
	}

	@Override
	public Network parseSubnetResponse(String response) throws OCCIException {
		Network ret = new Network();
		Gson gson = new Gson();
		SubnetContainer subnetContainer =
				gson.fromJson(response, SubnetContainer.class);
		
		String href, address, name;
        String size;
        int netClass;
		
        href = subnetContainer.getSubnet().getId();
        address = subnetContainer.getSubnet().getCidr();
        name = subnetContainer.getSubnet().getName();
        
        if (href != null && address != null && name != null) {
        	ret.setAddress(address);
        	ret.setHref(href);
        	ret.setName(name);
        	
        	netClass = Integer.parseInt(ret.getAddress().split("\\.")[0]);
            size     = null;
            if (netClass < 128) {
            	size = "A";
            } else if (netClass < 192) {
            	size = "B";
            } else if (netClass < 224) {
            	size = "C";
            } else if (netClass < 240) {
            	size = "D";
            } else if (netClass < 256) {
            	size = "E";
            } else {
            	OCCIException exception = new OCCIException();
                exception.setMessage(BADADDRESS);
                log.error(BADADDRESS);
                throw exception;
            }
        	
        	ret.setSize(size);
        } else {
        	OCCIException exception = new OCCIException();
            exception.setMessage(MALFORMEDRESPONSE);
            log.error(MALFORMEDRESPONSE);
            throw exception;
        }
        
		return ret;
	}

	@Override
	public StorageCollection parseStorageCollectionResponse(String response)
			throws OCCIException {
		Link link;
		ArrayList<Link> links = new ArrayList<Link>();
		StorageCollection ret = new StorageCollection();
		Gson gson = new Gson();
		VolumesContainer volumesContainer =
				gson.fromJson(response, VolumesContainer.class);
		
		for (Volume volume : volumesContainer.getVolumes()) {
			link = new Link();
			
			if (volume.getId() == null || volume.getDisplay_name() == null) {
        		OCCIException exception = new OCCIException();
                exception.setMessage(MALFORMEDRESPONSE);
                log.error(MALFORMEDRESPONSE);
                throw exception;
        	}
			
			link.setHref(volume.getId());
			link.setName(volume.getDisplay_name());
			links.add(link);
		}
		ret.getStorage().addAll(links);
		
		return ret;
	}

	@Override
	public Storage parseStorageResponse(String response) throws OCCIException {
		String id, name, description;
		int size;
		Storage ret = new Storage();
		Gson gson = new Gson();
		VolumeContainer volumeContainer =
				gson.fromJson(response, VolumeContainer.class);
		
		id = volumeContainer.getVolume().getId();
		name = volumeContainer.getVolume().getDisplay_name();
		size = volumeContainer.getVolume().getSize();
		description = volumeContainer.getVolume().getDisplay_description();
		
		
		if (id == null || name == null || description == null || size <= 0) {
			OCCIException exception = new OCCIException();
            exception.setMessage(MALFORMEDRESPONSE);
            log.error(MALFORMEDRESPONSE);
            throw exception;
		}
		
		ret.setName(name);
		ret.setDescription(description);
		ret.setHref(id);
		ret.setSize(String.valueOf(size));
		ret.setType(StorageType.DATABLOCK);
		
		return ret;
	}

	@Override
	public StorageCollection parseImageCollectionResponse(String response)
			throws OCCIException {
		StorageCollection ret = new StorageCollection();
        Gson gson = new Gson();
        Link link;
        
        log.info("[OpenStackParser] Parsing image collection response...");
        ImageCollection col = gson.fromJson(response, ImageCollection.class);
        
        for (Image img : col.getImages()) {
        	link = new Link();
        	link.setHref(img.getId().toString());
        	link.setName(img.getName());
        	
        	if (img.getId() == null || img.getName() == null) {
        		OCCIException exception = new OCCIException();
                exception.setMessage(MALFORMEDRESPONSE);
                log.error(MALFORMEDRESPONSE);
                throw exception;
        	}
        	
        	ret.getStorage().add(link);
        }
        log.debug("[OpenStackParser] Image collection response parsed...");
        
        return ret;
	}

	@Override
	public Storage parseImageResponse(String response) throws OCCIException {
		String id, name;
    	int size;
		Storage ret = new Storage();
		Gson gson = new Gson();
		
		log.info("[OpenStackParser] Parsing image response...");
		Image img = gson.fromJson(response, Image.class);
		
		id   = img.getId();
		name = img.getName();
		size = img.getSize();
		
		if (id == null || name == null || size <= 0) {
			OCCIException exception = new OCCIException();
            exception.setMessage(MALFORMEDRESPONSE);
            log.error(MALFORMEDRESPONSE);
            throw exception;
		}
		
		ret.setName(name);
		ret.setSize(String.valueOf(size));
		ret.setHref(id);
		ret.setType(StorageType.OS);
		
		log.debug("[OpenStackParser] Image response parsed...");
		
		return ret;
	}

	@Override
	public ComputeCollection parseComputeCollectionResponse(String response)
			throws OCCIException {
		ArrayList<Link> links = new ArrayList<Link> ();
		Link link;
		ComputeCollection ret = new ComputeCollection();
		Gson gson = new Gson();
		ServersContainer serversContainer =
				gson.fromJson(response, ServersContainer.class);
		
		for(ServersContainer.Server server : serversContainer.getServers()) {
			link = new Link();
			
			link.setName(server.getName());
			link.setHref(server.getId());
			
			if (link.getName() == null || link.getHref() == null) {
				OCCIException exception = new OCCIException();
	            exception.setMessage(MALFORMEDRESPONSE);
	            log.error(MALFORMEDRESPONSE);
	            throw exception;
			}
			
			links.add(link);
		}
		ret.getCompute().addAll(links);
		
		return ret;
	}

	@Override
	public Compute parseComputeResponse(String response) throws OCCIException {
		String id, name, state, flavorId;
		Compute ret= new Compute();
		Gson gson = new Gson();
		ServerContainer serverContainer =
				gson.fromJson(response, ServerContainer.class);
		
		id = serverContainer.getServer().getId();
		name = serverContainer.getServer().getName();
		flavorId = serverContainer.getServer().getFlavor().getId();
		state = serverContainer.getServer().getStatus();
		
		if (id == null || name == null ||
				flavorId == null || state == null) {
			OCCIException exception = new OCCIException();
            exception.setMessage(MALFORMEDRESPONSE);
            log.error(MALFORMEDRESPONSE);
            throw exception;
		}
		
		ret.setHref(id);
        ret.setName(name);
        ret.setState(fromOpenStackComputeState(state));
        ret.setInstanceType(flavorId);
		
		return ret;
	}

	@Override
	public String parseComputeInstanceTypeId(String response,
			HashMap<String, InstanceType> instanceTypes) throws OCCIException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Disk> parseAttachedStoragesResponse(String response)
			throws OCCIException {
		List<Disk> ret = new ArrayList<Disk>();
		Disk disk;
		Link link;
		Gson gson = new Gson();
		VolumeAttachmentsContainer container =
			gson.fromJson(response, VolumeAttachmentsContainer.class);
		
		for (VolumeAttachment attachment : container.getVolumeAttachments()) {
			disk = new Disk();
			link = new Link();
			
			if (attachment.getVolumeId() == null ||
					attachment.getDevice() == null) {
				OCCIException exception = new OCCIException();
	            exception.setMessage(MALFORMEDRESPONSE);
	            log.error(MALFORMEDRESPONSE);
	            throw exception;
			}
			
			link.setHref(attachment.getVolumeId());
			disk.setStorage(link);
			disk.setType(StorageType.DATABLOCK);
			disk.setTarget(attachment.getDevice());
			
			ret.add(disk);
		}
		
		return ret;
	}

	@Override
	public List<Nic> parseVirtualInterfacesResponse(String response)
			throws OCCIException {
		ArrayList<Nic> ret = new ArrayList<Nic>();
		Nic nic;
		Link link;
		Gson gson = new Gson();
		InterfaceAttachmentsContainer container =
				gson.fromJson(response, InterfaceAttachmentsContainer.class);
		
		for (InterfaceAttachment at : container.getInterfaceAttachments()) {
			link = new Link();
			nic = new Nic();
			nic.setIp(at.getFixed_ips()[0].getIp_address());
			
			link.setHref(at.getFixed_ips()[0].getSubnet_id());
			nic.setNetwork(link);
			
			nic.setMac(at.getMac_addr());
			
			if (nic.getIp() == null || nic.getMac() == null ||
					nic.getNetwork().getHref() == null) {
				OCCIException exception = new OCCIException();
	            exception.setMessage(MALFORMEDRESPONSE);
	            log.error(MALFORMEDRESPONSE);
	            throw exception;
			}
			
			ret.add(nic);
		}
		
		return ret;
	}

	@Override
	public InstanceTypeCollection parseInstanceTypeCollectionResponse(
			String response) throws OCCIException {
		InstanceTypeCollection ret = new InstanceTypeCollection();
        Gson gson = new Gson();
        Link link;
        
        log.info("[OpenStackParser] "
        		+ "Parsing instance type collection response...");
        FlavorsContainer col = gson.fromJson(response, FlavorsContainer.class);
        
        for (Flavor flavor : col.getFlavors()) {
        	link = new Link();
        	link.setHref(flavor.getId().toString());
        	link.setName(flavor.getName());
        	
        	if (flavor.getId() != null && flavor.getName() != null) {
        		ret.getInstanceType().add(link);
        	} else {
        		OCCIException exception = new OCCIException();
                exception.setMessage(MALFORMEDRESPONSE);
                log.error(MALFORMEDRESPONSE);
                throw exception;
        	}
        }
        log.debug("[OpenStackParser] "
        		+ "Instance type collection response parsed...");
        
		return ret;
	}

	@Override
	public InstanceType parseInstanceTypeResponse(String response)
			throws OCCIException {
		String name, id;
		int vcpu;
		long ram;
		Gson gson = new Gson();
		FlavorContainer container =
				gson.fromJson(response, FlavorContainer.class);
		InstanceType ret = new InstanceType();
		
		log.info("[OpenStackParser] Parsing instance type response...");
		
		name = container.getFlavor().getName();
		id   = container.getFlavor().getId();
		vcpu = container.getFlavor().getVcpus();
		ram  = container.getFlavor().getRam();
		
		if (vcpu > 0 && ram > 0 && name!= null && id != null) {
        	ret.setCpu(vcpu);
            ret.setHref(id);
            ret.setMemory(Integer.parseInt(String.valueOf(ram)));
            ret.setName(name);
        } else {
        	OCCIException exception = new OCCIException();
            exception.setMessage(MALFORMEDRESPONSE);
            log.error(MALFORMEDRESPONSE);
            throw exception;
        }
		log.debug("[OpenStackParser] Instance type response parsed...");
        
        return ret;
	}

	@Override
	public Quota parseComputeQuotaResponse(String response)
			throws OCCIException {
		int vcpu, ram, servers;
        Quota ret = new Quota();
        Gson gson = new Gson();
        
        QuotaSetContainer cont =
        		gson.fromJson(response, QuotaSetContainer.class);
        
        vcpu = cont.getQuota_set().getCores();
        ram  = cont.getQuota_set().getRam();
        servers = cont.getQuota_set().getInstances();
        
        if (vcpu > 0 && ram > 0 && servers > 0 ) {
        	ret.setCpu(vcpu);
        	ret.setMemory(ram);
        	ret.setNumVms(servers);
        } else {
        	OCCIException exception = new OCCIException();
            exception.setMessage(MALFORMEDRESPONSE);
            log.error(MALFORMEDRESPONSE);
            throw exception;
        }
        
        return ret;
	}

	@Override
	public int parseStorageQuotaResponse(String response) throws OCCIException {
		int totalPerStorage, maxStorages;
        Gson gson = new Gson();
        
        LimitsContainer cont =
        		gson.fromJson(response, LimitsContainer.class);
        
        totalPerStorage =
        		cont.getLimits().getAbsolute().getMaxTotalVolumeGigabytes();
        maxStorages = cont.getLimits().getAbsolute().getMaxTotalVolumes();
        
        if (totalPerStorage > 0 && maxStorages > 0) {
        	return totalPerStorage * maxStorages;
        } else {
        	OCCIException exception = new OCCIException();
            exception.setMessage(MALFORMEDRESPONSE);
            log.error(MALFORMEDRESPONSE);
            throw exception;
        }
	}

}
