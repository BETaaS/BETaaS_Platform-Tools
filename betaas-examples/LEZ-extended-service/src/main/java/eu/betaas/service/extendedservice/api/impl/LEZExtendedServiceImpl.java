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
// Component: TaaSResourceManager
// Responsible: Atos

package eu.betaas.service.extendedservice.api.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;

import org.apache.log4j.Logger;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import eu.betaas.service.servicemanager.api.ServiceManagerInternalIF;
import eu.betaas.service.servicemanager.extended.api.IExtendedService;

public class LEZExtendedServiceImpl implements IExtendedService 
{
	
	public final static String LOGGER_NAME = "extendedService.LEZFee";
	
	// Number of requested services (must match the manifest content)
	private final static int SERVICES_NUMBER = 2;
	private final static int SERVICES_IDX_USER_POSITION = 0;
	private final static int SERVICES_IDX_TRAFFIC_DENSITY = 1;
	
//	// Number of traffic data reception after which a BDM task is executed on it
//	private final static int TRAFFIC_TASK_PERIOD = 30;
	
	// Delay used when installing the extended service
	private final static int START_DELAY_FOR_ZK_REGISTRATION = 5000;
	
	// number of traffic data notifications to wait before resetting reliability to true after a SLA violation
	private final static int TRAFFIC_MEASURES_FOR_RELIABILITY = 5;
		
	// used for injection from blueprint
	public void setExtendedServiceUniqueID(String id) {
		mExtendedServiceUniqueID = id;
		mLogger.info("mExtendedServiceUniqueID: " + mExtendedServiceUniqueID);
	}
	
	public void setMapFilePath(String filePath) {
		mMapFilePath = filePath;
		mLogger.info("Map file path: " + mMapFilePath);
	}
	
	public String getUniqueID() {
		return mExtendedServiceUniqueID;
	}
	
	// used for injection from blueprint
	public void setContext(BundleContext context)
	{
		mContext = context;
	}
	
	public void setReportFolder(String reportFolder) {
		mLogger.info("Report folder: " + reportFolder);
		mReportFolder = reportFolder;
	}
	
	// Bundle setup service
	public void setupService(){
		mLogger.info("Starting the service");
		
		mRegistered = false;
		setTrafficReliabilityCounter(0);
		mNManagedTrafficData = 0;
    
   	Thread installThread = new Thread() {
			
			public void run() {
				
				// Wait a little bit to let the extended service be registered by Zookeeper
				// The registration has to be done in Zookeeper before the install notification is sent
				// otherwise the extended service cannot be notified
				try {
					sleep(START_DELAY_FOR_ZK_REGISTRATION);
				} catch (InterruptedException e) {
					e.printStackTrace();
					return;
				}
				
				ServiceManagerInternalIF sm = getServiceManagerIF();
				if (sm != null) {
					if (!sm.installExtendedService(mManifest)) {
						mLogger.info("install extended service returned false");
					} else {
						mLogger.info("installing extended service");
					}
				} else {
					mLogger.error("SM not available");
				}
			}
		};
		
		mManifest = "";
		
		URL curr;
		@SuppressWarnings("unchecked")
		Enumeration<URL> entries = (Enumeration<URL>)mContext.getBundle().findEntries("/", "manifest.xml", false);
		if (entries.hasMoreElements()) {
			try {
				curr = entries.nextElement();
				InputStream is = curr.openStream();
				BufferedReader br = new BufferedReader(new InputStreamReader(is));
				String line;
				while ((line = br.readLine()) != null) {
					mManifest += line + "\n";
				}
			} catch (IOException e) {
				mLogger.error("Error loading manifest: " + e.getMessage());
				return;
			}
		}
		
		// Load the map
		mMap = new Map();
		try {
			mMap.load(mMapFilePath);
		} catch (Exception e) {
			mLogger.error("Cannot load the map from " + mMapFilePath + ": " + e.getMessage());
			return;
		}
		
		mLEZProcessor = new LEZProcessor(mMap);
		
		installThread.start();
	}
	
	// Used when the bundle is stopped
	public void closeService() {
		if (mRegistered) {
			ServiceManagerInternalIF sm = getServiceManagerIF();
			if (sm != null) {
				if (mServiceList != null) {
					for (int i=0; i < mServiceList.size(); i++) {
						mLogger.info("requesting to unregister from service: " + mServiceList.get(i));
						if (!sm.unregister(mExtServiceID, mServiceList.get(i), mTokenList.get(i))) {
							mLogger.error("unregister returned false");
						} else {
							mLogger.info("service unregistered");
						}
					}
				}
			} else {
				mLogger.error("SM not available to unregister");
			}
		}
	}
	
	// called by SM to notify the services installation result
	public void notifyInstallation(boolean success, 
			                       String msg, 
			                       String extServId, 
			                       ArrayList<String> servList,
			                       ArrayList<String> tokenList) {
		mLogger.info("notified installation");
		mLogger.info("success: " + success);
		mLogger.info("msg: " + msg);
		mLogger.info("ext service ID: " + extServId);
		
		mExtServiceID = extServId;
		mServiceList = servList;
		mTokenList = tokenList;
		
		if (mServiceList != null) {
			int i;
			for (i = 0; i < mServiceList.size(); i++) {
				mLogger.info("service " + i + ": " + mServiceList.get(i));
			}
			
			if (mServiceList.size() != SERVICES_NUMBER) {
				mLogger.error("Expected " + SERVICES_NUMBER + " service installed, got " + mServiceList.size());
				return;
			}
			
			///////////////////////// REGISTER FOR PUSH DATA //////////////////////////
			ServiceManagerInternalIF sm = getServiceManagerIF();
			if (sm != null) {
				for (i = 0; i < mServiceList.size(); i++) {
					
					if (mServiceList.get(i) == null) {
						mLogger.error("got null service ID");
						return;
					}
					
					mLogger.info("Registering to the service: " + mServiceList.get(i));
					if (!sm.register(extServId, mServiceList.get(i), mTokenList.get(i))) {
						mLogger.error("Error registering to service");
						return;
					} else {
						mLogger.info("Registered to service");
					}
				}
				mRegistered = true;
			} else {
				mLogger.error("SM not available for registration");
			}
			
		} else {
			mLogger.error("Got null service list");
		}
		
	}

	
	public void notifySLAViolation(String serviceID) {
		mLogger.warn("Received SLA violation notification for service: " + serviceID);
		
		// start applying default fee
		mLEZProcessor.setTrafficDataReliable(false);
		// after a certain number of traffic data notification, the reliability will be reset to true
		setTrafficReliabilityCounter(TRAFFIC_MEASURES_FOR_RELIABILITY);
	}
	
	// called by SM when new data for registered services is available
	public void notifyData(String serviceID, JsonObject data) {
		mLogger.info("data received for service (" + serviceID +"): " + data.toString());
		
		if (serviceID.equals(mServiceList.get(SERVICES_IDX_USER_POSITION))) {
			
			mLEZProcessor.managePositionData(data);
			
		} else if (serviceID.equals(mServiceList.get(SERVICES_IDX_TRAFFIC_DENSITY))) {
			
			if (getTrafficReliabilityCounter() > 0) decreaseTrafficReliabilityCounter();
			
			if ((!mLEZProcessor.isTrafficDataReliable()) &&
				(getTrafficReliabilityCounter() == 0)) {
		
				mLEZProcessor.setTrafficDataReliable(true);
			}
			
			mLEZProcessor.manageTrafficData(data);
			
			mNManagedTrafficData++;
//			if (mNManagedTrafficData >= TRAFFIC_TASK_PERIOD) {
//				mNManagedTrafficData = 0;
//				
//				executeBDMTask();
//			}
			
			
		} else {
			mLogger.error("Unexpected service ID");
			return;
		}
	}	
	
	// used to get the reference to the SM service
	public ServiceManagerInternalIF getServiceManagerIF() {
		if (mContext == null) {
			mLogger.error("Cannot get resource manager IF: null context");
			return null;
		}
		
		try {
			ServiceReference ref = mContext.getServiceReference(ServiceManagerInternalIF.class.getName());
				
			if (ref != null) {
				return ((ServiceManagerInternalIF)mContext.getService(ref));
			}
		} catch (java.lang.NoClassDefFoundError e) {
			mLogger.error("No class definition found for SM");
		    return null;
		} catch (Exception e) {
			mLogger.error("SM not available: " + e.getMessage());
			return null;
		}
			
		return null;
	}

	public String getResult(String additionalInfo) {
		mLogger.info("Called getResult(" + additionalInfo + ")");

		if (mLEZProcessor == null) return "Not ready to serve requests";
		
		if (additionalInfo == null) return "User id needed (id=<id>)";
		if (additionalInfo.startsWith("id=")) {
			String id = additionalInfo.substring(3);
			mLogger.info("Requested info for user with id: " + id);
			return mLEZProcessor.getUserInfo(id);
		} else {
			return "User id needed (id=<id>)";
		}
	}
	
	/*
	private void executeBDMTask() {
		mLogger.info("Requesting BDM task on traffic data");
		ServiceManagerInternalIF sm = getServiceManagerIF();
		if (sm == null) {
			mLogger.error("Cannot get reference to SM to execute the BDM task");
			return;
		}
		String result = sm.getTaskData(mExtServiceID, "TrafficQueryTask");
		if (result == null) {
			mLogger.warn("The task returned null");
			return;
		}
		mLogger.info("Task result: " + result);
		
		try {
			// Compose a page with the result
			String location_identifier, count, min, max, timepeak, timestamp, lastvalue, lasttime;

	    	JsonElement jelement = new JsonParser().parse(result);
	    	JsonObject  jobject = jelement.getAsJsonObject();
	    	int size = jobject.getAsJsonArray("res").size();
	    	JsonArray array = jobject.getAsJsonArray("res");
	    	if (size <= 0) {
	    		mLogger.warn("No record in the task result");
	    		return;
	    	}
        mLogger.info(size + " records in the task result");
	    	
	    	ReportWriter report = new ReportWriter(mReportFolder);
	    	try {
          mLogger.info("Writing report header");
		    	mLogger.info("Output file: " + report.writeOutputHeader());
		    	mLogger.info("Writing records");
		    	for (int i=0; i<size; i++) {
			    	JsonObject element = array.get(i).getAsJsonObject();
			    	location_identifier = element.get("location_identifier").getAsString();
			    	count = element.get("count").getAsString();
			    	min = element.get("min").getAsString();
			    	max = element.get("max").getAsString();
			    	timepeak = element.get("timepeak").getAsString();
			    	timestamp = element.get("timestamp").getAsString();
			    	lastvalue = element.get("lastvalue").getAsString();
			    	lasttime = element.get("lasttime").getAsString();
			    	report.writeOutputRecord(location_identifier, count, min, max, timepeak, timestamp, lastvalue, lasttime);
		    	}
          mLogger.info("Writing report footer");
		    	report.writeOutputFooter();
	    	} catch (Exception e) {
	    		mLogger.error("Cannot write the report: " + e.getMessage());
	    	} finally {
	    		try {
	    			report.close();
	    		} catch (Exception e) {
	    			mLogger.error("Cannot close the report: " + e.getMessage());
	    		}
	    	}
	    } catch (Exception e) {
	    	mLogger.error("Cannot parse the JSON task result: " + e.getMessage());
	    }
	}
	*/
	
	private synchronized void setTrafficReliabilityCounter(int value) {
		mTrafficMeasuresToWaitBeforeReliabilityReset = value;
	}
	
	private synchronized void decreaseTrafficReliabilityCounter() {
		mTrafficMeasuresToWaitBeforeReliabilityReset--;
	}
	
	private synchronized int getTrafficReliabilityCounter() {
		return mTrafficMeasuresToWaitBeforeReliabilityReset;
	}
	
	/** Logger */
	private static Logger mLogger = Logger.getLogger(LOGGER_NAME);

	// bundle context
	private BundleContext mContext;
	
	// the ID unique in the whole instance for this extended service
	private String mExtendedServiceUniqueID;
	
	// Path of the map file
	private String mMapFilePath;
	
	// the ID of this extended service returned by TaaSRM
	private String mExtServiceID;
	
	// list of identifiers for the services in the manifest
	private ArrayList<String> mServiceList;
	
	// list of tokens to be used to access services
	private ArrayList<String> mTokenList;
	
	// true iff registered to all services that were requested to install
	private boolean mRegistered;
	
	// the manifest content
	private String mManifest;
	
	// the business logic object
	private LEZProcessor mLEZProcessor = null;
	
	// the roads map
	private Map mMap;
	
	// counter for traffic data reliability reset
	private int mTrafficMeasuresToWaitBeforeReliabilityReset;
	
	// counter for managed traffic data, used to periodically request BDM tasks execution on traffic
	private int mNManagedTrafficData;
	
	private String mReportFolder;
}
