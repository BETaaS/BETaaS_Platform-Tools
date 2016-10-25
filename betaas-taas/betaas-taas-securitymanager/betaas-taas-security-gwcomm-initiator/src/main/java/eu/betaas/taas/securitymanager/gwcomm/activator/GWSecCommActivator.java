package eu.betaas.taas.securitymanager.gwcomm.activator;

//import java.io.IOException;
//import java.io.InputStream;
//import java.util.Properties;

import org.apache.log4j.Logger;
//import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

import eu.betaas.taas.securitymanager.certificate.service.IGatewayStarCertificateExtService;
import eu.betaas.taas.securitymanager.core.service.IInitGWStarService;
import eu.betaas.taas.securitymanager.core.service.IJoinInstanceService;
import eu.betaas.taas.securitymanager.core.service.ISecGWCommService;
import eu.betaas.taas.securitymanager.encrypttest.api.IAddStringService;

public class GWSecCommActivator {
	Logger log = Logger.getLogger("betaas.taas.securitymanager");	
	
//	private static Properties props = new Properties();
	
	/** IInitGWStarService from Blueprint */
	private IInitGWStarService gwStarCoreService;
	/** IJoinInstanceService from blueprint */
	private IJoinInstanceService joinCoreService;
	/** ISecGWCommService from blueprint */
	private ISecGWCommService secCommCoreService;
	/** IAddStringService from blueprint */
	private IAddStringService addStringService;
	/** bundle context from blueprint*/
	private BundleContext context;
	/** The GWstarCertificateExtService tracker */
	private ServiceTracker extCertTracker;
	/** AddStringService tracker */
	private ServiceTracker strTracker;
	/**  This GW ID */
	private String mGwId;
	/** GW Destination ID --> for secure communication (at normal GW) */
	private String gwDestId;
	/** to indicate whether it is a GW* */
	boolean isGWStar=false;
	
	/** Certificate's parameter - Country Code */
	private String countryCode;
	/** Certificate's parameter - State */
	private String state;
	/** Certificate's parameter - location */
	private String location;
	/** Certificate's parameter - orgName */
	private String orgName;
	
	public void start() throws InterruptedException {
		
//		log.info("loading the properties file");
//		loadProperties();
		
		// check if this is a GW*
		extCertTracker = new ServiceTracker(context, 
			IGatewayStarCertificateExtService.class.getName(), null);
	extCertTracker.open();
	
	// wait for the tracker to gather info on the available extCertTracker services
	try {
		Thread.sleep(4000);
	} catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
	ServiceReference[] refs = extCertTracker.getServiceReferences();
	
	// Check other external certificate manager, if one or more already exists,
	// it means that this is not GW*, else this is a GW*
	if(refs!=null && refs.length > 0){
		log.debug("Other certificate manager(s) is found...");
		// check whether it is this GW or not
		for(ServiceReference ref : refs){
			// compare the GW ID of the found GW with this own ID
			if(!ref.getProperty("gwId").equals(mGwId)){
				this.isGWStar = false;
				this.gwDestId = (String) ref.getProperty("gwId");
				log.debug("GW* with ID: "+gwDestId+" is found!");
				break;
			}
			else
				this.isGWStar = true;
		}
	}
	else{
		log.debug("Found no other certificate manager(s)...");
		this.isGWStar = true;
	}
		
		// if this is a GW*, then initiate the GW* credentials
		if(this.isGWStar){
			log.info("Star to initiate the GW* credentials service...");
			gwStarCoreService.initGwStar(countryCode, state, location, orgName, mGwId);
			log.info("Successfully creating GW* credentials!!");
		}
		
		boolean isJoin = false;
		// if it is not a GW*, try to join an instance
		if(!this.isGWStar){
			log.info("Starting the join instance service...");
			try {
				isJoin = joinCoreService.requestGwCertificate(
						countryCode, state, location, orgName, mGwId);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(isJoin)
				log.info("Successfully join the instance and get instance certificate!!");
		}
		
		// initiating the shared key derivation with other GW, if join request is 
		// successful
		boolean isSecCommOk = false;
		
		Thread.sleep(3000);
		
		// if it is normal GW (that successfully join the instance)
		if(isJoin){
			log.info("Starting the secure GW communication service...");
			try {
				isSecCommOk = secCommCoreService.deriveSharedKeys(gwDestId);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if(isSecCommOk){
				log.info("Successfully deriving shared keys!!");
				// one of the GW (e.g. GW*) starts invoking AddStringService with encryption
//				 start tracker of AddStringService
				strTracker = new ServiceTracker(context, 
						IAddStringService.class.getName(), null);
				strTracker.open();
				// give time to trakcer to find IAddStringService from other GW
				Thread.sleep(3000);
				
				ServiceReference[] refsStr = strTracker.getServiceReferences();
				
				for(ServiceReference ref: refsStr){
					// search for the service from GW destination ID
					if(ref.getProperty("gwId").equals(gwDestId)){
						log.debug("found GW Destination: "+gwDestId);
						addStringService = (IAddStringService)context.getService(ref);
					}
				}
				
				String mName = "Bayu";
				// encrypt my name
				String mNameEncrypt = encryptData(mName);
				// invoke the helloName method from other GW
				String helloName = sayHello(mNameEncrypt, mGwId);
				// decrypt the received helloName
				String helloNameDecrypt = decryptData(helloName);
				log.info("The received helloName: "+helloNameDecrypt);
				
				String one = "Bayu";
				String two = "Anggorojati";
				// encrypt both one and two
				String oneEncrypt = encryptData(one);
				String twoEncrypt = encryptData(two);
				// invoke concatenateString method from other GW
				String concatenated = concatenateString(oneEncrypt, twoEncrypt, mGwId);
				// decrypt the received concatenated
				String concatenateDecrypt = decryptData(concatenated);
				log.info("The received concatenated: "+concatenateDecrypt);
			}
		}else{
			// create some delay, just to make sure that the other GW has already joined 
			Thread.sleep(120000);
		}
		
	}

	public void stop() throws Exception {
		log.info("Stopping the GWSecureCommunicationServie...");
		if(extCertTracker != null)
			extCertTracker.close();
		if(strTracker != null)
			strTracker.close();
	}
		
	private String sayHello(String name, String myGwId){
		String helloName = null;
		
		if(addStringService!=null){
			// myGwId gives information about the sender of this message, i.e. this GW
			helloName = addStringService.helloName(name, myGwId);
		}
		
		return helloName;
	}
	
	private String concatenateString(String one, String two, String myGwId){
		String concatenate = null;
		
		if(addStringService!=null){
			concatenate = addStringService.concatenateString(one, two, myGwId);
		}
		
		return concatenate;
	}
	
	private String encryptData(String data){
		log.info("Will encrypt this data: "+data + " to GW: "+gwDestId);
		return secCommCoreService.doEncryptData(gwDestId, data);
	}
	
	private String decryptData(String data){
		log.info("Will decrypt this: "+data+ " from GW: "+gwDestId);
		// here the sender of the message is also gwDestId
		return secCommCoreService.doDecryptData(gwDestId, data);
	}
	
//	/**
//	 * Method to load properties file related to gateway info
//	 */
//	private void loadProperties(){
//		try {
//			InputStream ins = GWSecCommActivator.class.getResourceAsStream(
//					"/gateway.properties");
//			props.load(ins);
//			ins.close();
//		} catch (IOException e) {
//			log.error("Error loading the properties file!!");
//			e.printStackTrace();
//		}	
//	}
	
	public void setGwStarCoreService(IInitGWStarService gwStarCoreService){
		this.gwStarCoreService = gwStarCoreService;
	}
	
	public void setJoinCoreService(IJoinInstanceService joinCoreService){
		this.joinCoreService = joinCoreService;
	}
	
	public void setSecCommCoreService(ISecGWCommService secCommCoreService){
		this.secCommCoreService = secCommCoreService;
	}
	
	public void setAddStringService(IAddStringService addStringService){
		this.addStringService = addStringService;
	}
		
	public void setContext(BundleContext context){
		this.context = context;
	}
	
	public void setGwId(String gwId){
		this.mGwId = gwId;
	}
	
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public void setState(String state) {
		this.state = state;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}
}
