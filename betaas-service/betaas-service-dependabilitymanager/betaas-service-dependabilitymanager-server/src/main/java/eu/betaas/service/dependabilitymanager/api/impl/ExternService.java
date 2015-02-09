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

package eu.betaas.service.dependabilitymanager.api.impl;

import org.apache.log4j.Logger;
import org.osgi.framework.BundleContext;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

//import eu.betaas.service.bigdatamanager.applicationdataservice.IDataManagerADService;
import eu.betaas.service.dependabilitymanager.api.IExtern;
import eu.betaas.service.dependabilitymanager.DependabilityManager;
import eu.betaas.service.dependabilitymanager.FailureReport;
import eu.betaas.service.dependabilitymanager.VitalityReport;

/**
 * This class implements the interfaces of DM visible to external GW components
 * @author Intecs
 */
public class ExternService implements IExtern {
	
	/**
	 * Startup the service exposing internal IF to the gateway
	 */
	public void startService() {
		mLogger.info("Starting Dependability Manager external IF bundle");
		DependabilityManager.getInstance().setContext(mContext);
		DependabilityManager.getInstance().setGwId(mId);
	}
	
	public void closeService() {
		mLogger.info("Stopping Dependability Manager external IF bundle");
		DependabilityManager.getInstance().stop();
	}
	
	/**
	 * Setter for the GW id
	 * @param id
	 */
	public void setGwId(String id) {
		mLogger.info("Setting the GW Id on external IF bundle");
		mId = id;
	}
	
	/**
	 * Setter for the OSGi context
	 * @param context
	 */
	public void setContext(BundleContext context) {
		mLogger.info("Setting the context on external IF bundle");
		mContext = context;
	}
	
//	/** 
//	 * Setter for BDM service
//	 * @param service
//	 */
//	public void setBDMService(IDataManagerADService service) {
//		mLogger.info("Setting BDM service on external IF bundle");
//		mBDMService = service;
//	}

	public String getVitalityReport() {
		Gson gson = new Gson();		
	    JsonObject jsonResult = new JsonObject();
	    
	    VitalityReport vitalityReport = DependabilityManager.getInstance().getVitalityReport();
	    
	    if (vitalityReport == null) return "Vitality report is currently not available";
	    
	    jsonResult.add("VitalityReport", gson.toJsonTree(vitalityReport));
	    
	    return jsonResult.toString();
	}

	public String getFailureReport(int maxRecords) {
		Gson gson = new Gson();		
	    JsonObject jsonResult = new JsonObject();
	    
	    FailureReport failureReport = new FailureReport();
	    
	    //TODO: read from DB, fill in the report
	    
	    jsonResult.add("FailureReport", gson.toJsonTree(failureReport));
	    
	    return jsonResult.toString();
	}

	public boolean checkVitality() {
		return true;
	}
	
	/** Logger */
	private static Logger mLogger = Logger.getLogger(DependabilityManager.LOGGER_NAME);

	/** OSGi context */
	private BundleContext mContext;
	
//	/** The BDM service to use tasks */
//	private IDataManagerADService mBDMService;
	
	/** The GW id */
	private String mId;

}
