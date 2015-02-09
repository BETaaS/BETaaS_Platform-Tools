package eu.betaas.taas.securitymanager.requirements.activator;

import org.apache.log4j.Logger;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTracker;

import eu.betaas.adaptation.thingsadaptor.api.ThingsAdaptor;
import eu.betaas.taas.securitymanager.requirements.service.IThingsRequirementsService;
import eu.betaas.taas.securitymanager.requirements.service.impl.ThingsRequirementsService;
import eu.betaas.taas.taastrustmanager.api.TaaSTrustManager;

public class SecurityRequirementsActivator implements BundleActivator {
	/** Handling Service Requirements Service Registration */
	ServiceRegistration sReqReg;
	
	/** Thing adaptor service tracker */
	ServiceTracker thingTracker;
	
	/** Trust manager service tracker */
	ServiceTracker trustTracker;
	
	Logger log = Logger.getLogger("betaas.taas.securitymanager");
	
	public void start(BundleContext bc) throws Exception {
		log.info("Tracking the ThingAdaptor service");
		thingTracker = new ServiceTracker(bc, ThingsAdaptor.class.getName(), null);
		thingTracker.open();
		
		log.info("Tracking the TrustManager service");
		trustTracker = new ServiceTracker(bc,TaaSTrustManager.class.getName(),null);
		trustTracker.open();
		
		// register the SecurityRequirements service
		log.info("Registering the SecurityRequirements service");
		sReqReg = bc.registerService(IThingsRequirementsService.class.getName(), 
				new ThingsRequirementsService(this), null);
	}

	public void stop(BundleContext bc) throws Exception {
		thingTracker.close();
		trustTracker.close();
		sReqReg.unregister();
	}
	
	public ServiceTracker getThingTracker() {
		return thingTracker;
	}
	
	public ServiceTracker getTrustTracker(){
		return trustTracker;
	}
}
