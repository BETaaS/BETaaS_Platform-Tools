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

/**
 * This class represent the Dependability Manager main object
 * @author Intecs
 */
public class DependabilityManager {

	public final static String LOGGER_NAME = "betaas.service";
	
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
	}
	
	private synchronized void init() throws Exception {
		stop();
		mChecker = new VitalityChecker();
		mChecker.start();
		mLogger.info("Dependability Manager initialized");
	}
	
	/** Logger */
	private static Logger mLogger = Logger.getLogger(DependabilityManager.LOGGER_NAME);

	/** Singleton */
	private static DependabilityManager mDependabilityManager = null;
	
	/** The thread that performs vitality checks */
	private VitalityChecker mChecker;
	
	private String mGwId;
	
	private BundleContext mContext;
}
