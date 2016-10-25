package eu.betaas.taas.taasvmmanager.openstack;

import java.util.List;
import java.util.UUID;

import com.google.gson.Gson;

import eu.betaas.taas.taasvmmanager.occi.datamodel.StorageType;
import eu.betaas.taas.taasvmmanager.occi.datamodel.Compute.Disk;
import eu.betaas.taas.taasvmmanager.openstack.gson.AuthRequest;
import eu.betaas.taas.taasvmmanager.openstack.gson.Flavor;
import eu.betaas.taas.taasvmmanager.openstack.gson.FlavorContainer;
import eu.betaas.taas.taasvmmanager.openstack.gson.NetworkRequestContainer;
import eu.betaas.taas.taasvmmanager.openstack.gson.ServerRequest;
import eu.betaas.taas.taasvmmanager.openstack.gson.ServerRequest.Server;
import eu.betaas.taas.taasvmmanager.openstack.gson.SubnetRequest;
import eu.betaas.taas.taasvmmanager.openstack.gson.AuthRequest.Auth;
import eu.betaas.taas.taasvmmanager.openstack.gson.AuthRequest.Auth.PasswordCredentials;
import eu.betaas.taas.taasvmmanager.openstack.gson.NetworkRequestContainer.Network;
import eu.betaas.taas.taasvmmanager.openstack.gson.SubnetRequest.Subnet;
import eu.betaas.taas.taasvmmanager.openstack.gson.VolumeRequest.Volume;
import eu.betaas.taas.taasvmmanager.openstack.gson.VolumeRequest;

public class OpenStackTemplates {
    private static String PAUSECOMPUTETEMPLATE = "{\"pause\": null}";
    private static String UNPAUSECOMPUTETEMPLATE = "{\"unpause\": null}";
    
    public static String getRequestTokenTemplate(String username,
                                                   String password,
                                                   String tenant) {
    	Gson builder = new Gson();
    	AuthRequest authRequest = new AuthRequest();
    	Auth auth = authRequest.new Auth();
    	PasswordCredentials credentials = auth.new PasswordCredentials();
    	
    	credentials.setUsername(username);
    	credentials.setPassword(password);
    	
    	auth.setTenantName(tenant);
    	auth.setPasswordCredentials(credentials);
    	
    	authRequest.setAuth(auth);
    	
    	System.out.println(builder.toJson(authRequest));
    	
        return builder.toJson(authRequest);
    }
    
    public static String getCreateFlavorTemplate(String name,
                                                  long   ram,
                                                  int    vcpus,
                                                  long   disk,
                                                  UUID    id) {
    	Gson builder = new Gson();
    	FlavorContainer flavorContainer = new FlavorContainer();
    	Flavor flavor = new Flavor();
    	
    	flavor.setDisk(disk);
    	flavor.setId(id.toString());
    	flavor.setPub(false);
    	flavor.setRam(ram);
    	flavor.setVcpus(vcpus);
    	flavor.setName(name);
    	flavor.setPub(true);
    	flavorContainer.setFlavor(flavor);
    	
    	return builder.toJson(flavorContainer);
    }
    
    public static String getCreateNetworkTemplate(String name) {
    	Gson builder = new Gson();
    	NetworkRequestContainer networkRequest = new NetworkRequestContainer();
    	Network network = networkRequest.new Network();
    	
    	network.setName(name);
    	network.setAdmin_state_up(true);
    	networkRequest.setNetwork(network);
    	
        return builder.toJson(networkRequest);
    }
    
    public static String getCreateSubnetTemplate(String name,
                                                   String networkId,
                                                   String cidr) {
    	Gson builder = new Gson();
    	SubnetRequest subnetRequest = new SubnetRequest();
    	Subnet subnet = subnetRequest.new Subnet();
    	
    	subnet.setName(name);
    	subnet.setIp_version(4);
    	subnet.setNetwork_id(networkId);
    	subnet.setCidr(cidr);
    	subnetRequest.setSubnet(subnet);
    	
        return builder.toJson(subnetRequest);
    }
    
    public static String getCreateStorageTemplate(String name,
                                                    String description,
                                                    int    size) {
    	Gson builder = new Gson();
    	VolumeRequest volumeRequest = new VolumeRequest();
    	Volume volume = volumeRequest.new Volume();
    	
    	volume.setName(name);
    	volume.setDescription(description);
    	volume.setSize(size);
    	volumeRequest.setVolume(volume);
    	
        return builder.toJson(volumeRequest);
    }
    
    public static String getCreateComputeTemplate(String imageRef,
                                                    String flavorRef,
                                                    String name,
                                                    String networkId,
                                                    List<Disk> disks) {
    	Disk disk;
    	Gson builder = new Gson();
    	ServerRequest serverRequest = new ServerRequest();
    	Server server = serverRequest.new Server();
    	Server.Network[] networks = new Server.Network[1];
    	Server.Network network = server.new Network();
    	
    	Server.BlockDevice[] devices = new Server.BlockDevice[disks.size()];
    	Server.BlockDevice device;
    	for (int i = 0 ; i < disks.size() ; i++) {
    		disk   = disks.get(i);
    		device = server.new BlockDevice();
    		
    		device.setUuid(disk.getStorage().getHref());
    		device.setDelete_on_termination("True");
    		device.setDevice_name(disk.getTarget());
    		device.setGuest_format("ephemeral");
    		if (disk.getType() == StorageType.OS) {
    			device.setBoot_index("-1");
    			device.setDestination_type("local");
    			device.setSource_type("image");
    		} else {
    			device.setBoot_index("0");
    			device.setDestination_type("volume");
    			device.setSource_type("volume");
    		}
    		devices[i] = device;
    	}
    	
    	network.setUuid(networkId);
    	networks[0] = network;
    	
    	server.setImageRef(imageRef);
    	server.setFlavorRef(flavorRef);
    	server.setName(name);
    	server.setNetworks(networks);
    	server.setBlock_device_mapping_v2(devices);
    	
    	serverRequest.setServer(server);
    	
        return builder.toJson(serverRequest);
    }
    
    public static String getPauseComputeTemplate() {
        return     PAUSECOMPUTETEMPLATE;
    }
    
    public static String getUnPauseComputeTemplate() {
        return     UNPAUSECOMPUTETEMPLATE;
    }
}
