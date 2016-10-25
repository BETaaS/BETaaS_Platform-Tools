/*
Copyright 2014-2015 Intecs Spa

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
// Component: SM
// Responsible: Intecs

package eu.betaas.service.dependabilitymanager;

import org.apache.log4j.Logger;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import eu.betaas.taas.bigdatamanager.database.service.IBigDataDatabaseService;
import eu.betaas.rabbitmq.publisher.interfaces.Publisher;
import eu.betaas.rabbitmq.publisher.interfaces.utils.Message;

/**
 * This class represent the Dependability Manager main object
 * @author Intecs
 */
public class DependabilityManager {

	public final static String LOGGER_NAME = "betaas.service";
	public final static String MONITORING = "monitoring";
	public final static String DEPENDABILITY = "dependability";
	
	public void setGwId(String id) {
		mGwId = id;
	}
	
	public void setContext(BundleContext context) {
		mContext = context;
	}
	
	public BundleContext getContext() {
		return mContext;
	}
	
	/**
	 * @return this class singleton
	 */
	public static DependabilityManager getInstance() {
		
		if (mDependabilityManager == null) {
			mDependabilityManager = new DependabilityManager();
			try {
				mDependabilityManager.init();
			} catch (Exception e) {
				mLogger.error("Error during DependabilityManager initialization: " + e.getMessage());
			}
		}
		
		return mDependabilityManager;
	}	
	
	/** 
	 * @return the object that implements the database service interface 
	 */
	public IBigDataDatabaseService getDatabaseServiceIF() {
		if (mContext == null) {
			mLogger.error("Cannot get database service IF: null context");
			return null;
		}
		
		try {			
			ServiceReference ref = mContext.getServiceReference(IBigDataDatabaseService.class.getName());
				
			if (ref != null) {
				return ((IBigDataDatabaseService)mContext.getService(ref));
			}
		} catch (java.lang.NoClassDefFoundError e) {
		    mLogger.error("No class definition found for Database Service");
		    return null;
		} catch (Exception e) {
			mLogger.error("Database Service not available: " + e.getMessage());
			return null;
		}
			
		return null;
	}	
	
	public VitalityReport getVitalityReport() {
		if (mChecker == null) return null;
		
		return mChecker.getVitalityReport(mGwId);		
	}
	
	public synchronized void stop() {
		if (mChecker == null) return;
		
		mChecker.setRunning(false);
		try {
			mChecker.join();
		} catch (InterruptedException e) {
		} finally {
			mChecker = null;
		}
		
		if (mFailChecker == null) return;
		
		mFailChecker.setRunning(false);
		try {
			mFailChecker.join();
		} catch (InterruptedException e) {
		} finally {
			mFailChecker = null;
		}
	}
	
	private synchronized void init() throws Exception {
		stop();
		mChecker = new VitalityChecker();
		mChecker.start();
		
		mFailChecker = new FailureChecker();
		mFailChecker.start();
		mLogger.info("Dependability Manager initialized");
		
		
	}
	
public void busMessage(String message){
		
		String key = "monitoring.service";
		if (mContext == null) {
			mLogger.warn("Cannot get context provider");
			return;
		}
		
				
		mLogger.info("Checking queue");
		//if (!enabledbus)return;
		mLogger.info("Sending to queue");
		ServiceReference serviceReference = mContext.getServiceReference(Publisher.class.getName());
		mLogger.info("Sending to queue");
		if (serviceReference==null)return;
		
		Publisher service = (Publisher) mContext.getService(serviceReference); 
		mLogger.info("Sending key: " + key + " message: "+message);
		service.publish(key, message);
		mLogger.info("Sent");
		
	}
	
	/** Logger */
	private static Logger mLogger = Logger.getLogger(DependabilityManager.LOGGER_NAME);

	/** Singleton */
	private static DependabilityManager mDependabilityManager = null;
	
	/** The thread that performs vitality checks */
	private VitalityChecker mChecker;
	
	private FailureChecker mFailChecker = null;
	
	private String mGwId;
	
	private BundleContext mContext;
}
