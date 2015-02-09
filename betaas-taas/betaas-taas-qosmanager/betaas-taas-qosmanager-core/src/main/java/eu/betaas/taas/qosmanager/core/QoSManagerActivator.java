/*
 *
Copyright 2014-2015 Department of Information Engineering, University of Pisa

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 
 */

// BETaaS - Building the Environment for the Things as a Service
//
// Component: TaaS QoS Manager
// Responsible: Carlo Vallati & Giacomo Tanganelli

package eu.betaas.taas.qosmanager.core;


import java.util.Calendar;
import java.util.Date;
import java.util.Dictionary;
import java.util.GregorianCalendar;
import java.util.Hashtable;

import org.apache.log4j.Logger;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTracker;

import eu.betaas.taas.bigdatamanager.database.service.IBigDataDatabaseService;
import eu.betaas.taas.contextmanager.api.ThingsServiceManager;
import eu.betaas.taas.qosmanager.api.QoSManagerExternalIF;
import eu.betaas.taas.qosmanager.api.QoSManagerNotificationIF;
import eu.betaas.taas.qosmanager.api.impl.ExternalAPIImpl;
import eu.betaas.taas.qosmanager.api.impl.NotificationAPIImpl;

import eu.betaas.taas.qosmanager.api.QoSManagerInternalIF;
import eu.betaas.taas.qosmanager.api.impl.InternalAPIImpl;
import eu.betaas.taas.qosmanager.monitoring.api.QoSManagerMonitoring;
import eu.betaas.taas.qosmanager.negotiation.NegotiationInterface;
import eu.betaas.taas.taasresourcesmanager.api.TaaSResourceManager;
import eu.betaas.service.servicemanager.api.ServiceManagerInternalIF;

/**
* Activator class for the QoSManager into the TaaS layer
**/

public class QoSManagerActivator{
	private static Logger LOG = Logger.getLogger("betaas.taas");
	
	/** It manages the registration of the internal GW interface */
	private ServiceRegistration mInternalServReg;
	
	/** It manages the registration of the notification GW interface */
	private ServiceRegistration mNotificationServReg;
	
	/** It manages the registration of the external GW interface */
	private ServiceRegistration mExternalServReg;

	/**  This the OSGI context*/
	private BundleContext context;
	
	/**  Reference to the TaaS RM */
	TaaSResourceManager taasrm;
	
	/**  Reference to the Service RM */
	ServiceManagerInternalIF servicerm;
	
	/**  Reference to the Negotiator engine */
	NegotiationInterface negotiator; 
	
	/**  Reference to the QoSMonitoring*/
	QoSManagerMonitoring qosMonitoring;
	
	/**  Reference to the ContextManager*/
	ThingsServiceManager cmservice;
	
	private static QoSManager qosM;
	private static QoSManagerExternalIF qosm_star;
	//private static Thread thread;
	private static ServiceTracker tracker;
	//private static QoSServiceTracker serviceTracker;
	//private static Whiteboard listener = null;
	//private static boolean stopped;
	//private static boolean registered;
	
	/** Config parameters **/
	private String gwId;
	private String WSAddress;
	private String WSAddressInternal;
	private static boolean mIsStar;
	private static IBigDataDatabaseService service;
	
	
	/** Time to wait before the IM tracker can be used after opening */
	private final static int TIME_TO_WAIT_FOR_TRACKING_MILLIS = 5000;

    
	public void start() throws Exception {
		
		qosM = new QoSManager();
		qosM.setService(service);
		mInternalServReg = null;
		mExternalServReg = null;
		//registered = false;
		//stopped = false;
		
		// Get reference to the QOSM*
		tracker = new ServiceTracker( context, QoSManagerExternalIF.class.getName(), null );
		tracker.open(); 
		GregorianCalendar mTrackerOpenDate = new GregorianCalendar();
		mTrackerOpenDate.setTime(new Date());
		waitForTracker(mTrackerOpenDate);
		Object [] providers = tracker.getServices();
		
		// Select a provider
		if ( providers != null && providers.length > 0) 
		{ 		
			LOG.debug("Number of providers found for QoSM*: " + providers.length);			
			qosm_star = (QoSManagerExternalIF) providers[0];
			LOG.debug("QoSM star found: " + qosm_star);
			mIsStar = false;
		}
		else
		{
			LOG.debug("No providers were found for the QoSM*");		
			mIsStar = true;
		}
		
		
		if(mIsStar){
			LOG.info("QOSM promoted to QOSM*");
			qosm_star = registerExternalInterface(providers, tracker);
		}
		else
			LOG.info("QOSM is not promoted");
		tracker.close();
		qosM.setQosm_star(qosm_star);
		LOG.debug("QoSM* is:" + qosM.getQosm_star());
		/*registered = true;
		serviceTracker = new QoSServiceTracker();
		serviceTracker.activate();*/

		/*listener = new Whiteboard();
		context.addServiceListener(listener);*/

		registerInternalInterface();
			
	}

	public void stop() throws Exception {
        // Unregister the services
    	mInternalServReg.unregister();
    	mInternalServReg = null;
    	LOG.info("UnRegistering internal QoSM services");
    	if(mIsStar){
    		LOG.info("UnRegistering external QoSM services");
    		mExternalServReg.unregister();
    		mExternalServReg = null;
    	}
    	//context.removeServiceListener(listener);
	}
	
	/**
	 * Check if enough time elapsed since tracker opening. If not, wait.
	 * @param object 
	 */
	private void waitForTracker(GregorianCalendar opentime) {
		GregorianCalendar now = new GregorianCalendar();
		now.setTime(new Date());
		
		now.add(Calendar.MILLISECOND, -TIME_TO_WAIT_FOR_TRACKING_MILLIS);
		if (now.before(opentime)) {
			try {
				LOG.debug("Waiting to let tracker gathering info on QoSMs");
				Thread.sleep(TIME_TO_WAIT_FOR_TRACKING_MILLIS);
			} catch (InterruptedException e) {}
		}		
	}

	private QoSManagerExternalIF registerExternalInterface(Object [] providers, ServiceTracker tracker){
		LOG.info("Registering External Interface");
		Dictionary<String,String> props = new Hashtable<String,String>();        
        
        props.put("service.exported.interfaces", "*");
        props.put("service.exported.configs", "org.apache.cxf.ws");
        props.put("org.apache.cxf.ws.address", WSAddress);
        QoSManagerExternalIF ret = new ExternalAPIImpl(this, qosM, gwId, context);
        mExternalServReg = context.registerService(QoSManagerExternalIF.class.getName(), 
        		ret, props);
        
        providers = tracker.getServices(); 
        while (true){
	        if ( providers != null && providers.length > 0) 
			{ 		
				LOG.debug("Number of providers found for QoSM*: " + providers.length);			
				qosm_star = (QoSManagerExternalIF) providers[0];
				LOG.debug("QoSM star found: " + qosm_star);
				break;
			}
        }
		return ret;
	}
	
	private void registerInternalInterface(){
		LOG.info("Registering Internal Interface");
		
        
        // Register its own exposed interfaces
        InternalAPIImpl innerService = new InternalAPIImpl(this, qosM, negotiator, taasrm, servicerm, 
 				qosMonitoring, cmservice, gwId);
        
        mInternalServReg = context.registerService(QoSManagerInternalIF.class.getName(), innerService, null);
        
        Dictionary<String,String> props = new Hashtable<String,String>();        
        
        props.put("service.exported.interfaces", "*");
        props.put("service.exported.configs", "org.apache.cxf.ws");
        props.put("org.apache.cxf.ws.address", WSAddressInternal);
        
        NotificationAPIImpl notification_endpoint = new NotificationAPIImpl(qosM, gwId, cmservice, innerService);

        tracker = new ServiceTracker( context, QoSManagerNotificationIF.class.getName(), null );
		tracker.open(); 
		GregorianCalendar mTrackerOpenDate = new GregorianCalendar();
		mTrackerOpenDate.setTime(new Date());
		waitForTracker(mTrackerOpenDate);
		Object [] providers = tracker.getServices();

		int number = 0;
		// Select a provider
		
        if ( providers != null && providers.length >= 0) 
		{
        	number = providers.length;	
        	
		}

		mNotificationServReg = context.registerService(QoSManagerNotificationIF.class.getName(), 
	       		 notification_endpoint, props);
		providers = null;
		// Select a provider
		while (true){
			providers = tracker.getServices();
	        if ( providers != null && providers.length > number) 
			{
	        	LOG.debug("Internal Interface registered");
	        	number = providers.length;	
	        	break;
			}
		}
		tracker.close();
		
	}
	
	// Bluprint set reference to negotiator
	public void setNegotiator(NegotiationInterface negotiator) throws Exception {
		this.negotiator = negotiator;
	}
	
	// Bluprint set reference to taasrm
	public void setTaasrm(TaaSResourceManager taasrm) throws Exception {
		this.taasrm = taasrm;
	}
	
	// Bluprint set reference to taasrm
	public void setServicerm(ServiceManagerInternalIF servicerm) throws Exception {
		this.servicerm = servicerm;
	}
	
	// Bluprint set reference to QoSMonitoring
	public void setQosMonitoring(QoSManagerMonitoring qosMonitoring) throws Exception {
		this.qosMonitoring = qosMonitoring;
	}
	
	// Bluprint set reference to taasrm
	public void setCmservice(ThingsServiceManager cm) throws Exception {
		this.cmservice = cm;
	}
	
	
	// Bluprint set context
	public void setContext(BundleContext context) {
		this.context = context;
	}
	
	/** Setter methods for configuration parameters */
	public void setWSAddress(String vWSAddress) {
		this.WSAddress = vWSAddress;
	} 
	
	public void setService(IBigDataDatabaseService db) {
		this.service = db;
	}

	public void setGwId(String cgwId) {
		this.gwId = String.format("%02d", Integer.parseInt(cgwId));
	}

	public String getWSAddressInternal() {
		return WSAddressInternal;
	}

	public void setWSAddressInternal(String wSAddressInternal) {
		this.WSAddressInternal = wSAddressInternal;
	}
	
	/*private class QoSServiceTracker implements Runnable {

	    protected void activate() {
	 
	        try {
	            tracker = new ServiceTracker(context, QoSManagerExternalIF.class.getName(),
	                                          null );
	            tracker.open();
	            GregorianCalendar mTrackerOpenDate = new GregorianCalendar();
				mTrackerOpenDate.setTime(new Date());
		        waitForTracker(mTrackerOpenDate);
	            thread = new Thread( this, "QoSM* Whiteboard" );
	            thread.start();
	            LOG.info("QoS tracker started");
	        } catch (RuntimeException e) {
	            LOG.error("Can not create Tracker ", e);
	        }
	    }
	 
	    public synchronized void run() {
	        Thread   current = Thread.currentThread();

	        while ( current == thread && !stopped) {
	            Object [] providers = tracker.getServices();
	            LOG.error("Check QoSM*");
	            // Select a provider
				if ( providers == null || providers.length <= 0) 
				{
					LOG.error("No QoSM* found");
					mIsStar = true;
					registered = false;
				}

				if(!registered && mIsStar){
					registerExternalInterface(providers, tracker);
					registered = true;
				}
	 
	            try { wait( 5000 ); } catch( InterruptedException e ) {}
	        }
	    }
	}*/
	
}


