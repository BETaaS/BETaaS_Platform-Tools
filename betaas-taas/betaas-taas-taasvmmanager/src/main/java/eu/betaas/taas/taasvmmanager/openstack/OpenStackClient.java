package eu.betaas.taas.taasvmmanager.openstack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.log4j.Logger;

import eu.betaas.taas.taasvmmanager.api.datamodel.Flavor;
import eu.betaas.taas.taasvmmanager.api.datamodel.InstanceType;
import eu.betaas.taas.taasvmmanager.api.datamodel.Flavor.FlavorType;
import eu.betaas.taas.taasvmmanager.configuration.TaaSVMMAnagerConfiguration;
import eu.betaas.taas.taasvmmanager.messaging.MessageManager;
import eu.betaas.taas.taasvmmanager.occi.OCCIClient;
import eu.betaas.taas.taasvmmanager.occi.OCCIException;
import eu.betaas.taas.taasvmmanager.occi.datamodel.Compute;
import eu.betaas.taas.taasvmmanager.occi.datamodel.Compute.Disk;
import eu.betaas.taas.taasvmmanager.occi.datamodel.Compute.Nic;
import eu.betaas.taas.taasvmmanager.occi.datamodel.ComputeCollection;
import eu.betaas.taas.taasvmmanager.occi.datamodel.ComputeState;
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
import eu.betaas.taas.taasvmmanager.openstack.parser.OpenStackJSONParser;
import eu.betaas.taas.taasvmmanager.rest.RestClient;
import eu.betaas.taas.taasvmmanager.rest.RestClient.HeaderType;
import eu.betaas.taas.taasvmmanager.rest.RestClient.StatusCode;

public class OpenStackClient implements OCCIClient{

	/******** Resource paths (%s == tenant ID) ********/
	private static final String TOKENS     = "/v2.0/tokens";
	private static final String TENANTS    = "/v2.0/tenants";
	private static final String FLAVORS    = "/v2/%s/flavors";
	private static final String NETWORKS   = "/v2.0/networks";
	private static final String SUBNETS    = "/v2.0/subnets";
	private static final String VOLUMES    = "/v2/v2.0/%s/volumes/detail";
	private static final String SERVERS    = "/v2/%s/servers/detail";
	private static final String IMAGES     = "/v2/%s/images";
	private static final String USER_STORAGE_QUOTA = "/v2/%s/limits";
	private static final String USER_COMPUTE_QUOTA =
			"/v2/%s/os-quota-sets";
	private static final String SERVER_CHANGE_STATE =
			"/v2/%s/servers/%s/action";
	private static final String DISK_ATTACHMENTS =
			"/v2/%s/servers/%s/os-volume_attachments";
	private static final String VIRTUAL_INTERFACES =
			"/v2/%s/servers/%s/os-virtual-interfaces";
	/**************************************************/
	
	/******** Content types ********/
	private static final String APPLICATIONXML  = "application/xml";
	private static final String APPLICATIONJSON = "application/json";
	/*******************************/
	
	/******** Default resource names ********/
	private static final String BETAAS_NETWORK     = "betaas-network";
	private static final String BETAAS_SUBNET      = "betaas-subnet";
	private static final String BETAAS_GATEWAY_IMG = "betaas-gateway";
	private static final String BETAAS_APP_IMG     = "betaas-app";
	private static final String BETAAS_BIGDATA_IMG = "betaas-bd";
	/****************************************/
	
	/******** Default resource IDs ********/
	private String  betaasNetworkId;
	private Network betaasSubNet;
	private User    osUser;
	/**************************************/
	
	/******** OpenStack component Endpoints ********/
	private String keystoneUrl;
	private String novaUrl;
	private String cinderUrl;
	private String neutronUrl;
	/***********************************************/
	
	/*************** Error messages ****************/
	private static final String ERROR_NOIMAGE =
			"You have not chosen an OS image";
	/***********************************************/
	
	private static Logger log = Logger.getLogger("betaas.taas");
	
	private String tenant;
	private String tenantId;
	private String username;
	private String password;
	private String token;
	private MessageManager mManager;
	//TODO expiration date of the token
	
	public OpenStackClient () throws OCCIException{
		OCCIException exception;
		Map<HeaderType, String> headers;
		String body;
		String[] result, parsedResult;
		
		mManager = MessageManager.instance();
		
		keystoneUrl = TaaSVMMAnagerConfiguration.getKeystoneEndpoint();
		novaUrl     = TaaSVMMAnagerConfiguration.getNovaEndpoint();
		cinderUrl   = TaaSVMMAnagerConfiguration.getCinderEndpoint();
		neutronUrl  = TaaSVMMAnagerConfiguration.getNeutronEndpoint();
		tenant      = TaaSVMMAnagerConfiguration.getOpenStackTenant();
		username    = TaaSVMMAnagerConfiguration.getOpenStackUser();
		password    = TaaSVMMAnagerConfiguration.getOpenStackPass();
		
		log.info("[OpenStackClient] Initializing OpenStack client");
		log.info("[OpenStackClient] Keystone URL is " + keystoneUrl);
		log.info("[OpenStackClient] tenant is " + tenant);
		log.info("[OpenStackClient] username is " + username);
		
		RestClient restClient = new RestClient(keystoneUrl);
		
		/* Get authentication token and endpoint URLs */
		mManager.monitoringPublish("Getting authentication token.");
		log.info("[OpenStackClient] Getting authentication token.");
		
		body = OpenStackTemplates.getRequestTokenTemplate(
				username, password, tenant);
		parsedResult = (String[]) makeGenericPostResourceCall(
				keystoneUrl, TOKENS, body, StatusCode.OK);
		
		token = parsedResult[0];
		osUser = new User();
		osUser.setName(parsedResult[1]);
		osUser.setHref(parsedResult[2]);
			
		if (novaUrl == null || novaUrl.equals("")) {
			novaUrl = parsedResult[3];
		}
		
		if (cinderUrl == null || cinderUrl.equals("")) {
			cinderUrl = parsedResult[4];
		}
			
		if (neutronUrl == null || neutronUrl.equals("")) {
			neutronUrl = parsedResult[5];
		}
			
		log.info("[OpenStackClient] Nova URL is " + novaUrl);
		log.info("[OpenStackClient] Cinder URL is " + cinderUrl);
		log.info("[OpenStackClient] Neutron URL is " + neutronUrl);
		log.info("[OpenStackClient] Token succesfully retrieved.");
		mManager.monitoringPublish("Token succesfully retrieved.");
		
		/* Get tenant id */
		tenantId = (String) makeGenericGetCollectionCall(
				keystoneUrl, TENANTS, StatusCode.OK);
		
		
		/* Check that the BETaaS default images exist.
		 * If not, exit and warn the user */
		log.info("[OpenStackClient] Checking default images.");
		mManager.monitoringPublish("Checking default images.");
		List<String> stringNames =  new ArrayList<String>(
				Arrays.asList("betaas-gateway", "betaas-app", "betaas-bd")
			);
		StorageCollection images =
				(StorageCollection) makeGenericGetCollectionCall(novaUrl,
						                                         IMAGES,
						                                         StatusCode.OK,
						                                         tenantId);
		for (Link link : images.getStorage()) {
			stringNames.remove(link.getName());
		}
		
		if (!stringNames.isEmpty()) {
			log.error("[OpenStackClient] Missing images.");
			mManager.monitoringPublish("Error: missing images.");
			String message = "The following needed images are not in the "
					+ "OpenStack infrastructure, please import them:";
			for (String name : stringNames) {
				message += "\n\t- " + name;
			}
			exception = new OCCIException();
			exception.setMessage(message);
			throw exception;
		}
		log.info("[OpenStackClient] Default images checked.");
		mManager.monitoringPublish("Default images checked.");
		
		
		//TODO get user's quota
		log.info("[OpenStackClient] Getting user's quota.");
		Quota quota = (Quota) makeGenericGetResourceCall(
				novaUrl,
				USER_COMPUTE_QUOTA,
				username,
				StatusCode.OK,
				tenantId);
		
		quota.setStorage((Integer) makeGenericGetCollectionCall(
				cinderUrl,
				USER_STORAGE_QUOTA, 
				StatusCode.OK,
				tenantId));
		
		log.info("[OpenStackClient] User's quota gotten.");
		
		/* Check that the BETaaS default flavors exist.
		 * If not, create them */
		log.info("[OpenStackClient] Checking list of flavors.");
		List<String> instanceTypeNames = new LinkedList<String>(
				Arrays.asList(new String[]{
				"BETaaS." + FlavorType.tiny.toString(),
				"BETaaS." + FlavorType.small.toString(),
				"BETaaS." + FlavorType.standard.toString()
				}));
		
		InstanceTypeCollection instanceTypes = getInstanceTypes();
		for (Link link : instanceTypes.getInstanceType()) {
			if (instanceTypeNames.contains(link.getName())) {
				instanceTypeNames.remove(link.getName());
			}
		}
		
		for (String instanceName : instanceTypeNames) {
			FlavorType type =
					FlavorType.valueOf(instanceName.split("BETaaS.")[1]);
			
			log.info("[OpenStackClient] Need to create " + type + " flavor.");
			
			Flavor flavor = TaaSVMMAnagerConfiguration.getFlavor(type);
			
			InstanceType instanceType =
				(InstanceType) makeGenericPostResourceCall(
					novaUrl,
					FLAVORS,
					OpenStackTemplates.getCreateFlavorTemplate(
							instanceName,
							flavor.getMemory(),
							flavor.getvCpu(),
							flavor.getDisk(),
							UUID.randomUUID()),
					StatusCode.OK,
					tenantId);
		}
		log.info("[OpenStackClient] Flavors checked.");
		
		
		
		/* Check that the BETaaS network exists */
		/* We use a String list of [<id>, name] tuples */
		log.info("[OpenStackClient] Checking default BETaaS networks.");
		mManager.monitoringPublish("Checking default BETaaS networks.");
		
		String[][] networksInfo =
			(String[][]) makeGenericGetCollectionCall(neutronUrl,
                                                      NETWORKS,
                                                      StatusCode.OK);
		String [] betaasNetwork = null;
		if (networksInfo != null) {
			for (String[] networkData : networksInfo) {
				if (networkData[1].equals(BETAAS_NETWORK)) {
					betaasNetwork = networkData;
				}
			}
		}
		
		if (betaasNetwork == null) {
			log.info("[OpenStackClient] Need to create BETaaS network.");
			mManager.monitoringPublish("Need to create BETaaS network.");
			betaasNetwork = (String[]) makeGenericPostResourceCall(
				neutronUrl,
				NETWORKS,
				OpenStackTemplates.getCreateNetworkTemplate(BETAAS_NETWORK),
				StatusCode.CREATED);
		}
		betaasNetworkId = betaasNetwork[0];
		
		/* If network information length is 2, it means the network
		   has been created, but has no subnet associated to it.    */
		if (betaasNetwork.length == 2) {
			log.info("[OpenStackClient] Need to create BETaaS subnets.");
			betaasSubNet = this.createNetwork(
					BETAAS_SUBNET,
					"Subnet for the BETaaS VMs", "10.0.23.0/24", "");
		}
		
		log.info("[OpenStackClient] Default BETaaS networks checked.");
		log.info("[OpenStackClient] OpenStack client initialized.");
		mManager.monitoringPublish("Default BETaaS networks checked.");
		mManager.monitoringPublish("OpenStack client initialized.");
	}
	
	public UserCollection getUsers() throws OCCIException {
		// TODO Auto-generated method stub
		return null;
	}

	public User getUser(String id) throws OCCIException {
		User user = new User();
		
		Quota quota = (Quota) makeGenericGetResourceCall(
				novaUrl,
				USER_COMPUTE_QUOTA,
				username,
				StatusCode.OK,
				tenantId);
		
		quota.setStorage((Integer) makeGenericGetCollectionCall(
				cinderUrl,
				USER_STORAGE_QUOTA, 
				StatusCode.OK,
				tenantId));
		
		user.setQuota(quota);
		
		return user;
	}

	public NetworkCollection getNetworks() throws OCCIException {
		log.info("[OpenStackClient] Getting subnet...");
		return (NetworkCollection) makeGenericGetCollectionCall(neutronUrl,
				SUBNETS, StatusCode.OK);
	}

	public Network getNetwork(String id) throws OCCIException {
		log.info("[OpenStackClient] Getting subnet info...");
		Link    user = new Link();
		Network ret = (Network) makeGenericGetResourceCall(neutronUrl,
												  SUBNETS,
												  id,
												  StatusCode.OK);
		
		user.setName(osUser.getName());
		user.setHref(osUser.getHref());
		
		ret.setDescription(ret.getName() + " BETaaS network");
		ret.setUser(user);
		
		return ret;
	}

	public Network createNetwork(String name, String description,
			String address, String size) throws OCCIException {
		log.info("[OpenStackClient] Creating subnet...");
		Link    user = new Link();
		Network ret = (Network) makeGenericPostResourceCall(
				neutronUrl,
				SUBNETS,
				OpenStackTemplates.getCreateSubnetTemplate(
						name,betaasNetworkId, address),
				StatusCode.CREATED);
		
		user.setName(osUser.getName());
		user.setHref(osUser.getHref());
		
		ret.setDescription(ret.getName() + " BETaaS network");
		ret.setUser(user);
		
		return ret;
	}

	public void deleteNetwork(String id) throws OCCIException {
		log.info("[OpenStackClient] Deleting subnet...");
		makeGenericDeleteCall(neutronUrl, SUBNETS, id, StatusCode.NOCONTENT);
	}

	public StorageCollection getStorages() throws OCCIException {
		StorageCollection ret;
		log.info("[OpenStackClient] Getting storages...");
		
		ret = (StorageCollection) makeGenericGetCollectionCall(cinderUrl,
				VOLUMES, StatusCode.OK, tenantId);
		
		List<String> stringNames =  new ArrayList<String>(
				Arrays.asList("betaas-gateway", "betaas-app", "betaas-bd")
			);
		StorageCollection images =
				(StorageCollection) makeGenericGetCollectionCall(novaUrl,
						                                         IMAGES,
						                                         StatusCode.OK,
						                                         tenantId);
		for (Link link : images.getStorage()) {
			if (link.getName().equals(BETAAS_APP_IMG) ||
					link.getName().equals(BETAAS_GATEWAY_IMG) ||
					link.getName().equals(BETAAS_BIGDATA_IMG)) {
				ret.getStorage().add(link);
			}
		}
		
		return ret;
	}

	public Storage getStorage(String id) throws OCCIException {
		log.info("[OpenStackClient] Getting storage info...");
		
		/* We suppose it's a volume first */ 
		Link    user = new Link();
		Storage ret  = null;
		
		try {
			ret = (Storage) makeGenericGetResourceCall(cinderUrl,
					                                  VOLUMES,
					                                  id,
					                                  StatusCode.OK,
					                                  tenantId);
		} catch (OCCIException e) {
			/* In that case, it could be an Image */ 
			ret = (Storage) makeGenericGetResourceCall(novaUrl,
									                    IMAGES,
									                    id,
									                    StatusCode.OK,
									                    tenantId);
			
		}
		user.setName(osUser.getName());
		user.setHref(osUser.getHref());
								
		ret.setUser(user);
		
		return ret;
	}

	public Storage createStorage(String name, String description,
			StorageType type, int size, String fstype) throws OCCIException {
		log.info("[OpenStackClient] Creating storage...");
		Link    user = new Link();
		Storage ret = (Storage) makeGenericPostResourceCall(
				cinderUrl,
				VOLUMES,
				OpenStackTemplates.getCreateStorageTemplate(
						name, description, size),
				StatusCode.CREATED,
				tenantId);
		
		user.setName(osUser.getName());
		user.setHref(osUser.getHref());
		
		ret.setType(type);
		ret.setUser(user);
		
		return ret;
	}

	public void deleteStorage(String id) throws OCCIException {
		log.info("[OpenStackClient] Deleting storage...");
		makeGenericDeleteCall(
				cinderUrl, VOLUMES, id, StatusCode.ACCEPTED, tenantId);
	}

	public InstanceTypeCollection getInstanceTypes() throws OCCIException {
		log.info("[OpenStackClient] Getting instance types...");
		return (InstanceTypeCollection) makeGenericGetCollectionCall(novaUrl,
				FLAVORS, StatusCode.OK, tenantId);
	}

	public InstanceType getInstanceType(String id) throws OCCIException {
		log.info("[OpenStackClient] Getting instance type info...");
		return (InstanceType) makeGenericGetResourceCall(novaUrl,
				FLAVORS, id, StatusCode.OK, tenantId);
	}

	public Disk createComputeDisk(Storage storage, String target)
			throws OCCIException {
		log.info("[OpenStackClient] Creating compute disk...");
		Disk ret = new Disk();
		Link storageLink = new Link();
		
		storageLink.setName(storage.getName());
		storageLink.setHref(storage.getHref());
		
		ret.setStorage(storageLink);
		ret.setTarget(target);
		ret.setType(storage.getType());

		return ret;
	}

	public Nic createComputeNic(Network network, String ip, String mac)
			throws OCCIException {
		log.info("[OpenStackClient] Creating compute NIC...");
		Nic ret = new Nic();
		Link networkLink = new Link();
		
		networkLink.setName(network.getName());
		networkLink.setHref(network.getHref());
		
		ret.setIp(ip);
		ret.setMac(mac);
		ret.setNetwork(networkLink);
		
		return ret;
	}

	public ComputeCollection getComputes() throws OCCIException {
		log.info("[OpenStackClient] Getting computes...");
		return (ComputeCollection) makeGenericGetCollectionCall(
				novaUrl, SERVERS, StatusCode.OK);
	}

	public Compute getCompute(String id) throws OCCIException {
		log.info("[OpenStackClient] Getting compute info...");
		Link    user = new Link();
		Compute ret = (Compute) makeGenericGetResourceCall(
				novaUrl, SERVERS, id, StatusCode.OK, tenantId);
		
		user.setName(osUser.getName());
		user.setHref(osUser.getHref());
		
		ret.setUser(user);
		
		List<Disk> disks = (List<Disk>) makeGenericGetCollectionCall(
				novaUrl,
				DISK_ATTACHMENTS,
				StatusCode.ACCEPTED,
				tenantId,
				id);
		
		for (Disk disk : disks) {
			String storageId = disk.getStorage().getHref();
			Storage storage = getStorage(storageId);
			
			disk.getStorage().setName(storage.getName());
		}
		ret.getDisk().addAll(disks);
		
		List<Nic> nics = (List<Nic>) makeGenericGetCollectionCall(
				novaUrl, 
				VIRTUAL_INTERFACES,
				StatusCode.ACCEPTED,
				tenantId,
				id);
		
		for (Nic nic : nics) {
			String  networkId = nic.getNetwork().getHref();
			Network network = getNetwork(networkId);
			
			nic.getNetwork().setName(network.getName());
		}
		ret.getNic().addAll(nics);
		
		return ret;
	}

	public Compute createCompute(String name, int cpu, int memory,
			InstanceType instanceType, List<Disk> disks, List<Nic> nic)
			throws OCCIException {
		log.info("[OpenStackClient] Creating compute...");
		String  imageRef = null;
		Link    user = new Link();
		
		log.info("[OpenStackClient] Locating VM image...");
		for (Disk disk : disks) {
			if (disk.getStorage().getName().equals(BETAAS_APP_IMG) ||
					disk.getStorage().getName().equals(BETAAS_BIGDATA_IMG) ||
					disk.getStorage().getName().equals(BETAAS_GATEWAY_IMG)) {
				imageRef = disk.getStorage().getHref();
				disks.remove(disk);
			}
		}
		
		if (imageRef == null) {
			OCCIException exception = new OCCIException();
            exception.setMessage("[OpenStackClient] " + ERROR_NOIMAGE);
            log.error("[OpenStackClient] " + ERROR_NOIMAGE);
            throw exception;
		}
		
		Compute ret = (Compute) makeGenericPostResourceCall(
				novaUrl,
				SERVERS,
                OpenStackTemplates.getCreateComputeTemplate(
                		imageRef,
                		instanceType.getHref(),
                		name,
                		nic.get(0).getNetwork().getHref(),
                		disks),
                StatusCode.OK,
                tenant);
		
		user.setName(osUser.getName());
		user.setHref(osUser.getHref());
		
		ret.setUser(user);
		
		/* get disks and nics */
		ret.getDisk().addAll(disks);
        ret.getNic().addAll(nic);
		
		return ret;
	}

	public void changeComputeState(String id, BETaaSComputeState newState)
			throws OCCIException {
		String body; 
		log.info("[OpenStackClient] Changing compute state to "
				+ newState + "...");
		/* We only accept pause and resume */ 
		switch (newState) {
			case ACTIVE:
				body = OpenStackTemplates.getUnPauseComputeTemplate();
				break;
			case STOPPED:
				body = OpenStackTemplates.getPauseComputeTemplate();
				break;
			default:
				OCCIException exception = new OCCIException();
				exception.setMessage("This cloud platform does not allow"
                                   + " to change to this state manually.");
				throw exception;
		}
		
		makeGenericPostResourceCall(
				novaUrl,
				SERVER_CHANGE_STATE,
				body,
				StatusCode.ACCEPTED,
				tenantId,
				id);
		log.info("[OpenStackClient] Compute state changed to " + newState);
	}

	public void saveComputeDisk(Compute compute, String storageId, String name)
			throws OCCIException {
		// TODO Auto-generated method stub
		
	}

	public void deleteCompute(String id) throws OCCIException {
		log.info("[OpenStackClient] Deleting compute...");
		makeGenericDeleteCall(
				novaUrl, SERVERS, id, StatusCode.NOCONTENT, tenantId);
	}

	public OCCIClientStatus getStatus() throws OCCIException {
		log.info("[OpenStackClient] Getting cloud status...");
		String   errorMessage = "Problems with %s: %s\n";
		String   formattedErrorMessage = "";
		String[] result;
		String[] serviceURLs  = {keystoneUrl, novaUrl , cinderUrl, neutronUrl};
		String[] serviceNames = {"Keystone", "Nova" , "Cinder", "Neutron"};
		List<String[]> failed   = new ArrayList<String[]>();
		OCCIClientStatus status = new OCCIClientStatus();
		
		status.setStatus(CloudStatus.OK);
		
		result = (String[]) makeGenericGetResourceCall(
				keystoneUrl, "/v2.0", "", StatusCode.OK);
		
		for (int i = 0; i < serviceURLs.length; i++) {
			log.info("[OpenStackClient] Checking " + 
					serviceNames[i] + " status...");
			result = (String[]) makeGenericGetResourceCall(
					serviceURLs[i], "/v2.0", "", StatusCode.OK);
			
			if (RestClient.getStatusCode(result[0]) != StatusCode.OK &&
					RestClient.getStatusCode(result[0]) !=
					StatusCode.NONAUTHORITATIVEINFORMATION) {
				log.info("[OpenStackClient] " +
					serviceNames[i] + " is not ok.");
				failed.add(new String[] {serviceNames[i], result[1]});
			}
		}
		
		if (!failed.isEmpty()) {
			for (String[] failedService : failed) {
				formattedErrorMessage += String.format(
						errorMessage, failedService[0], failedService[1]); 
			}
			status.setStatus(CloudStatus.FAILED);
			status.setErrorMessage(formattedErrorMessage);
		}
		
		return status;
	}
	
	private Object makeGenericGetCollectionCall(String endpointUrl,
                                                 String resourceType,
                                                 StatusCode expectedStatusCode,
                                                 Object ... parameters) 
                                                 		throws OCCIException {
		String[] result;
		Object ret = null;
		RestClient restClient = new RestClient(endpointUrl);
		Map<HeaderType, String> headers = new HashMap<HeaderType, String>();
		
		headers.put(HeaderType.XAUTHTOKEN, token);
		result = restClient.getResource(
				String.format(resourceType, parameters),
				headers);
		
		if (RestClient.getStatusCode(result[0]) == expectedStatusCode) {
			if (resourceType.equals(SUBNETS)) {
				ret = new OpenStackJSONParser()
                          .parseSubnetCollectionResponse(result[1]);
			} else if (resourceType.equals(NETWORKS)) {
				ret = new OpenStackJSONParser()
                          .parseNetworkCollectionResponse(result[1]);
			} else if (resourceType.equals(VOLUMES)) {
				ret = new OpenStackJSONParser()
                          .parseStorageCollectionResponse(result[1]);
			} else if (resourceType.equals(IMAGES)) {
				ret = new OpenStackJSONParser()
                          .parseImageCollectionResponse(result[1]);
			} else if (resourceType.equals(FLAVORS)) {
				ret = new OpenStackJSONParser()
                          .parseInstanceTypeCollectionResponse(result[1]);
			} else if (resourceType.equals(SERVERS)) {
				ret = new OpenStackJSONParser()
                          .parseComputeCollectionResponse(result[1]);
			} else if (resourceType.equals(DISK_ATTACHMENTS)) { 
				ret = new OpenStackJSONParser()
				          .parseAttachedStoragesResponse(result[1]);
			} else if (resourceType.equals(VIRTUAL_INTERFACES)) { 
				ret = new OpenStackJSONParser()
		                  .parseVirtualInterfacesResponse(result[1]);
			} else if (resourceType.equals(USER_STORAGE_QUOTA)) { 
				ret = new OpenStackJSONParser()
		                  .parseStorageQuotaResponse(result[1]);
			} else if (resourceType.equals(TENANTS)) { 
				ret = new OpenStackJSONParser()
		                  .parseTenantsResponse(result[1], tenant);
			} else {
				ret = result;
			}
		}else {
			OCCIException exception = new OCCIException();
			exception.setMessage(result[0] + "-" + result[1]);
			throw exception;
		}
		
		return ret;
	}
	
	private Object makeGenericGetResourceCall(String endpointUrl,
            								String resourceType,
            								String resourceId,
            								StatusCode expectedStatusCode,
            								Object ... parameters) 
                                            		throws OCCIException {
		String[] result;
		Object ret = null;
		RestClient restClient = new RestClient(endpointUrl);
		Map<HeaderType, String> headers = new HashMap<HeaderType, String>();
		
		headers.put(HeaderType.XAUTHTOKEN, token);
		result = restClient.getResource(
				String.format(resourceType, parameters) + "/" + resourceId,
				headers);
		
		if (RestClient.getStatusCode(result[0]) == expectedStatusCode) {
			if (resourceType.equals(SUBNETS)) {
				ret = new OpenStackJSONParser()
                          .parseSubnetResponse(result[1]);
			} else if (resourceType.equals(VOLUMES)) {
				ret = new OpenStackJSONParser()
                          .parseStorageResponse(result[1]);
			} else if (resourceType.equals(IMAGES)) {
				ret = new OpenStackJSONParser()
                          .parseImageResponse(result[1]);
			} else if (resourceType.equals(FLAVORS)) {
				ret = new OpenStackJSONParser()
                          .parseInstanceTypeResponse(result[1]);
			} else if (resourceType.equals(SERVERS)) {
				ret = new OpenStackJSONParser()
                          .parseComputeResponse(result[1]);
			} else if (resourceType.equals(USER_COMPUTE_QUOTA)) {
				ret = new OpenStackJSONParser()
				          .parseComputeQuotaResponse(result[1]);
			}
		} else {
			OCCIException exception = new OCCIException();
			exception.setMessage(result[0] + "-" + result[1]);
			throw exception;
		}
		
		return ret;
	}
	
	private Object makeGenericPostResourceCall(String endpointUrl,
                                                String resourceType,
                                                String body,
                                                StatusCode expectedStatusCode,
                                                Object ... parameters) 
                                                		throws OCCIException {
		String[] result;
		Object ret = null;
		RestClient restClient = new RestClient(endpointUrl);
		Map<HeaderType, String> headers = new HashMap<HeaderType, String>();
		headers.put(HeaderType.XAUTHTOKEN, token);
		headers.put(HeaderType.ACCEPT, APPLICATIONJSON);
		headers.put(HeaderType.CONTENTTYPE, APPLICATIONJSON);
		
		result =
			restClient.postResource(
					String.format(resourceType, parameters), headers, body);
		
		if (RestClient.getStatusCode(result[0]) == expectedStatusCode) {
			if (resourceType.equals(SUBNETS)) {
				ret = new OpenStackJSONParser()
                          .parseSubnetResponse(result[1]);
			} else if (resourceType.equals(NETWORKS)) {
				ret = new OpenStackJSONParser()
                          .parseCreatedNetworkResponse(result[1]);
			} else if (resourceType.equals(VOLUMES)) {
				ret = new OpenStackJSONParser()
                          .parseStorageResponse(result[1]);
			} else if (resourceType.equals(FLAVORS)) {
				ret = new OpenStackJSONParser()
                          .parseInstanceTypeResponse(result[1]);
			} else if (resourceType.equals(SERVERS) ||
					resourceType.equals(SERVER_CHANGE_STATE)) {
				ret = new OpenStackJSONParser()
                          .parseComputeResponse(result[1]);
			} else if (resourceType.equals(TOKENS)) {
				ret = new OpenStackJSONParser()
				          .parseAuthorizationResponse(result[1]);
			}
		} else {
			OCCIException exception = new OCCIException();
			exception.setMessage(result[0] + "-" + result[1]);
			throw exception;
		}
		
		return ret;
	}
	
	private void makeGenericDeleteCall(String endpointUrl,
                                         String resourceType,
                                         String resourceId,
                                         StatusCode expectedStatusCode,
                                         Object ... parameters)
                                        		 throws OCCIException {
		String[] result;
		RestClient restClient = new RestClient(endpointUrl);
		Map<HeaderType, String> headers = new HashMap<HeaderType, String>();
		String path = String.format(resourceType, parameters)
				+ "/" + resourceId;
		
		
		headers.put(HeaderType.XAUTHTOKEN, token);
		result =
			restClient.deleteResource(
					String.format(resourceType, parameters) + "/" + resourceId,
					headers);
		
		if (RestClient.getStatusCode(result[0]) != expectedStatusCode) {
			OCCIException exception = new OCCIException();
			exception.setMessage(result[0] + " - " + result[1]);
			throw exception;
		}
	}
}
