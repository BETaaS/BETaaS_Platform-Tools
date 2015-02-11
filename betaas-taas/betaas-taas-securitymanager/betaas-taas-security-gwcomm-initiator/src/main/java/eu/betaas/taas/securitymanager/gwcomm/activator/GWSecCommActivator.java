package eu.betaas.taas.securitymanager.gwcomm.activator;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

import eu.betaas.taas.securitymanager.core.service.IInitGWStarService;
import eu.betaas.taas.securitymanager.core.service.IJoinInstanceService;
import eu.betaas.taas.securitymanager.core.service.ISecGWCommService;

public class GWSecCommActivator implements BundleActivator {
	Logger log = Logger.getLogger("betaas.taas.securitymanager");	
	
	private static Properties props = new Properties();
	
	ServiceTracker starTracker;
	ServiceTracker joinTracker;
	ServiceTracker commTracker;
	
	BundleContext bc;
	
	public void start(BundleContext context) throws InterruptedException {
		bc = context;
		
		log.info("loading the properties file");
		loadProperties();
		
		// if this is a GW*, then initiate the GW* credentials
		if(props.get("gatewayId").equals("gwStar")){
			log.info("Star to initiate the GW* credentials service...");
			starTracker = new ServiceTracker(context, 
					IInitGWStarService.class.getName(), null);
			starTracker.open();
			
			try {
				initGwStar();
			} catch (Exception e) {
				log.error("Failed in creating credentials for the GW*...");
				e.printStackTrace();
			}
			log.info("Successfully creating GW* credentials!!");
		}
		
		boolean isJoin = false;
		// if it is not a GW*, try to join an instance
		if(!props.get("gatewayId").equals("gwStar")){
			log.info("Starting the join instance service...");
			joinTracker = new ServiceTracker(context, 
					IJoinInstanceService.class.getName(), null);
			joinTracker.open();
			
			// try to join the instance, by requesting a certificate to its GW*
			try {
				isJoin = getInstanceCertificate();
			} catch (Exception e) {
				log.error("Error in requesting certificate to GW* prior to joining the instance");
				e.printStackTrace();
			}
			if(isJoin)
				log.info("Successfully join the instance and get instance certificate!!");
		}
		
		// initiating the shared key derivation with other GW, if join request is 
		// successful
		boolean isSecCommOk = false;
		
		// create some delay, just to make sure that the other GW has already joined
		Thread.sleep(30000);
		
		// either it is a GW* or normal GW (that successfully join the instance)
		if(props.get("gatewayId").equals("gwStar") || isJoin){
			log.info("Starting the secure GW communication service...");
			commTracker = new ServiceTracker(context,ISecGWCommService.class.getName(), 
					null);
			commTracker.open();
			
			try {
				isSecCommOk = startSecureComm();
			} catch (Exception e) {
				log.error("Error in deriving shared keys to start secure " +
						"communication with other GW!!");
				e.printStackTrace();
			}
			if(isSecCommOk)
				log.info("Successfully deriving shared keys!!");
		}
	}

	public void stop(BundleContext context) throws Exception {
		log.info("Stopping the GWSecureCommunicationServie...");
		if(starTracker!=null)
			starTracker.close();
		if(joinTracker != null)
			joinTracker.close();
		commTracker.close();
	}
	
	private void initGwStar() {
		int n = 0;
		Object[] starProv = starTracker.getServices();
		if(starProv != null && starProv.length > 0){
			if(n >= starProv.length)
				n=0;
			
			IInitGWStarService serv = (IInitGWStarService) starProv[n++];
			serv.initGwStar(props.getProperty("country"), props.getProperty("state"), 
					props.getProperty("location"), props.getProperty("org"), 
					props.getProperty("gatewayId"));
		}
	}
	
	/** Method to get a certificate from GW* before joining an instance **/
	private boolean getInstanceCertificate() throws Exception{
		boolean isOk = false;
		log.info("trying to get a certificate from GW*");
		Object serv = joinTracker.getService();
		
//		if(serv instanceof IJoinInstanceService){
			isOk = ((IJoinInstanceService) serv).requestGwCertificate(
					props.getProperty("country"), props.getProperty("state"), 
					props.getProperty("location"), props.getProperty("org"), 
					props.getProperty("gatewayId"));
//		}
		
		return isOk;
	}
	
	/** method to derive shared key with other GW before starting a session */
	private boolean startSecureComm() throws Exception{
		boolean isOk = false;
		
		Object serv = commTracker.getService();
		
//		if(serv instanceof ISecGWCommService){
			isOk = ((ISecGWCommService) serv).deriveSharedKeys(
					props.getProperty("gwDestId"));
//		}
			
		return isOk;
	}
	
	/**
	 * Method to load properties file related to gateway info
	 */
	private void loadProperties(){
		try {
			InputStream ins = GWSecCommActivator.class.getResourceAsStream(
					"/gateway.properties");
			props.load(ins);
			ins.close();
		} catch (IOException e) {
			log.error("Error loading the properties file!!");
			e.printStackTrace();
		}	
	}
}
