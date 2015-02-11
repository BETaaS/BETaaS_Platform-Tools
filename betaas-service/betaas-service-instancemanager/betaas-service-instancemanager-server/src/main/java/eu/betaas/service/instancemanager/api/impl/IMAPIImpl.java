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
// Component: IM
// Responsible: Intecs

package eu.betaas.service.instancemanager.api.impl;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
//import java.util.Dictionary;
import java.util.HashMap;
//import java.util.Hashtable;
//import java.util.Map;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
//import org.osgi.util.tracker.ServiceTracker;

import eu.betaas.service.instancemanager.ContextProvider;
import eu.betaas.service.instancemanager.GWRegistryRow;
import eu.betaas.service.instancemanager.IMInfo;
import eu.betaas.service.instancemanager.InstanceManager;
import eu.betaas.service.instancemanager.JoinThread;
//import eu.betaas.service.instancemanager.TrackerIMCustomizer;
import eu.betaas.service.instancemanager.api.InstanceManagerExternalIF;
import eu.betaas.service.instancemanager.config.Configuration;
import eu.betaas.service.instancemanager.config.Gateway;
import eu.betaas.taas.securitymanager.core.service.IInitGWStarService;
import eu.betaas.taas.securitymanager.core.service.IJoinInstanceService;
//import eu.betaas.taas.bigdatamanager.database.service.IBigDataDatabaseService;

import org.osgi.framework.ServiceReference;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * Implementation of IM interface exposed by OSGi bundles
 * @author Intecs
 */
public class IMAPIImpl implements InstanceManagerExternalIF, ContextProvider {

	public final static String GW_TYPE_STAR = "star";
	public final static String GW_TYPE_BACKUP_STAR = "backup-IM*";
	public final static String GW_TYPE_NORMAL = "normal";

	/** Setter methods for configuration parameters */
	
	public void setGwId(String gwId) {
		mGWID = gwId;
	}
	
	public void setIsStar(String isStar) {
		mIsStar = (isStar.equalsIgnoreCase("1") ? true : false);
	}
	
	public void setIsBackupStar(String isBackupStar) {
		mIsBackupStar = (isBackupStar.equalsIgnoreCase("1") ? true : false);
	}
	
	public void setCredentials(String credentials) {
		mCredentials = credentials;
	}
	
	public void setInstanceDescription(String description) {
		mDescription = description;
	}
	
	public void setAdminAddress(String adminAddress) {
		mAdminAddress = adminAddress;
	}
	
	public void setInstanceId(String instanceId) {
		mInstanceId = instanceId;
	}
	
	public void setContext(BundleContext bundleContext) {
		if (bundleContext==null) mLogger.warn("setContext got null");
		mBundleContext = bundleContext;
	}
	
	public void setAutomaticJoin(String automaticJoin) {
		if ((automaticJoin != null) && (automaticJoin.trim().equals("1"))) mAutomaticJoin = true;
		else mAutomaticJoin = false;
	}
	
	public void setCountryCode(String countryCode) {
		mCountryCode = countryCode;
	}
	
	public void setState(String state) {
		mState = state;
	}
	
	public void setLocation(String location) {
		mLocation = location;
	}
	
	public void setOrgName(String orgName) {
		mOrgName = orgName;
	}
	
	public void setTrackerWaitTime(String time) {
		int waitTime = InstanceManager.DEFAULT_TIME_TO_WAIT_FOR_TRACKING_MILLIS;
		try {
			waitTime = Integer.parseInt(time);
		} catch (Exception e) {}
		
		InstanceManager.setWaitTime(waitTime);
	}
		
	/** Bundle setup method */	
	public void setup() {
		mLogger.info("Setting up INSTANCE MANAGER");
		
		mConfiguration = new Configuration();
		mConfiguration.mGW = new Gateway();
		mConfiguration.mGW.mIM = new eu.betaas.service.instancemanager.config.InstanceManager();
		
		mConfiguration.mGW.mInstanceId = mInstanceId;
		mConfiguration.mGW.mID = mGWID;
		mConfiguration.mGW.mCredentials = mCredentials;
		mConfiguration.mGW.mDescription = mDescription;
		mConfiguration.mGW.mAdminAddress = mAdminAddress;
		mConfiguration.mGW.mIM.mIsStar = mIsStar;
		mConfiguration.mGW.mIM.mIsBackupStar = mIsBackupStar;
		mConfiguration.mGW.mCountryCode = mCountryCode;
		mConfiguration.mGW.mState = mState;
		mConfiguration.mGW.mLocation = mLocation;
		mConfiguration.mGW.mOrgName = mOrgName;

		mIM = new InstanceManager(mConfiguration, this, this);

		if (mBundleContext == null) {
			mLogger.warn("Null bundle context");
		}
//		mIMStarTracker = null;
//		mIMTrackerCustomizer = null;	
		
		if (!mAutomaticJoin) {
			mLogger.warn("AUTOMATIC JOIN NOT CONFIGURED");
		}
		
		// In case no other IM is visible, set itself as IM*
		Thread startInitThread = new Thread() {
			public void run() {
				//ServiceReference[] ims = mIM.getAvailableIMs(null, false);
				//mLogger.info("ims.size="+ims.length);
				//for (int i=0; i<ims.length; i++) {
				//	InstanceManagerExternalIF serv = 
				//			((InstanceManagerExternalIF)mBundleContext.getService(ims[i]));
				//	if (serv == null) mLogger.info("id["+i+"]=null");
				//	else mLogger.info("id["+i+"]=" + serv.getGWID());
				//}
				String starGWIDFound = mIM.manageAloneIM();

				if ((mAutomaticJoin) && (starGWIDFound != null) && (starGWIDFound.length() > 0)) {
					mLogger.info("Requesting automatic join to GW: " + starGWIDFound);
					requestJoin(starGWIDFound);
				}
			}
		};
		mLogger.info("Starting a thread for startup * management");
		startInitThread.start();
	}
	
	/** Bundle shut-down method */
	public void close() {
		mLogger.info("Instance Manager is stopping");
		
		// Close the tracker, if it has been opened
        
//        // Close the ServiceTracker
//		if (mIMStarTracker != null) {
//			mIMStarTracker.close();
//		}

//		// set to null to allow garbage collection on referenced objs
//		mIMStarTracker = null;
//		mIMTrackerCustomizer = null;		
		
		if (mConfiguration != null) {
			mConfiguration.mGW = null;
			mConfiguration = null;
		}
		mIM = null;
		
		mLogger.info("Instance Manager stopped");
	}
	
	public IMAPIImpl() {
	}

//	@Override
	public BundleContext getBundleContext() {
		return mBundleContext;
	}
	
	public boolean joinInstance(String info, String credentials) {
		
		IMInfo IMinfo = new IMInfo();
		try {
			IMinfo.build(info);
		} catch (Exception e) {
			mLogger.error("Error parsing info received from a joining IM: " + info);
			return false;
		}
		
		mLogger.info("IM from GW (" + IMinfo.mGWId + ") is requesting to join the BETaaS instance");
		
		if (!isGWStar()) {
			mLogger.info("Cannot manage the join request because this IM is not star");
			return false;
		}
		
		// check if GWID is present in the GW registry, i.e. if the GW has already joined the instance
		if (mIM.mGWRegistry.hasJoined(IMinfo.mGWId)) {
			mLogger.warn("GW (" + IMinfo.mGWId + ") already joined the instance.");
			return true;
		}
		
		//TODO: check credentials
		
		// Try to update the joined flag in case the GW is already in the registry
		if (!mIM.mGWRegistry.setJoined(IMinfo.mGWId, true)) {
			// Otherwise add the joined GW to the GW registry
			GWRegistryRow gw = new GWRegistryRow();
			gw.mGWID = IMinfo.mGWId;
			gw.mJoined = true;
			mIM.mGWRegistry.addGW(gw);
		}
		
		mLogger.info("Accepted the join of a new GW");
		
		return true;
	}
	
	public String getInstanceID() {
		return mConfiguration.mGW.mInstanceId;
	}
	
	public boolean isBackupStar() {
		return mConfiguration.mGW.mIM.mIsBackupStar;
	}
	
	public boolean disjoinInstance(String info, String credentials) {
		IMInfo IMinfo = new IMInfo();
		try {
			IMinfo.build(info);
		} catch (Exception e) {
			mLogger.error("Error parsing info received from a disjoining IM: " + info);
			return false;
		}
		
		mLogger.info("IM from GW (" + IMinfo.mGWId + ") is requesting to disjoin from the BETaaS instance");
		
		if (!isGWStar()) {
			mLogger.info("Cannot manage the disjoin request because this IM is not star");
			return false;
		}
		
		// check if GWID is not present in the GW registry, i.e. if the GW has not joined the instance yet
		if (!mIM.mGWRegistry.hasJoined(IMinfo.mGWId)) {
			mLogger.warn("GW (" + IMinfo.mGWId + ") did not join the instance.");
			return false;
		}
		
		//TODO: check credentials
		
		// Remove the GW from the GW registry
		if (!mIM.mGWRegistry.setJoined(IMinfo.mGWId, false)) {
			mLogger.warn("GW (" + IMinfo.mGWId + ") cannot be removed from the registry.");
			return false;
		}
		
		mLogger.info("Accepted the disjoin of the requesting GW");
		
		return true;
	}
	
	public String getGWID() {
		if (mConfiguration == null) mLogger.error("Requested GW Id, Configuration null");
		else if (mConfiguration.mGW == null) mLogger.error("Requested GW Id, Configuration mGW null");
		else if (mConfiguration.mGW.mID == null) mLogger.error("Requested GW Id, it is null");
		
		if ((mConfiguration != null) &&
			(mConfiguration.mGW != null)) {
			return mConfiguration.mGW.mID;
		}
		return null;
	}
	
	public String getAdminAddress() {
		if ((mConfiguration != null) &&
			(mConfiguration.mGW != null)) {
			return mConfiguration.mGW.mAdminAddress;
		}
		return null;
	}
	
	public String getDescription() {
		if ((mConfiguration != null) &&
			(mConfiguration.mGW != null)) {
			return mConfiguration.mGW.mDescription;
		}
		return null;
	}
	
	public boolean isGWStar() {
		if ((mConfiguration != null) &&
			(mConfiguration.mGW != null) &&
			(mConfiguration.mGW.mIM != null)) {
			return mConfiguration.mGW.mIM.mIsStar;
		}
		return false;
	}
	
//	public boolean setGWStar(boolean star) {
//		if (isGWStar() && !star) {
//			// Trying to revoke the star property to this GW. Check if there are joined GW
//			Vector<String> joinedGW = getJoinedGWs();
//			if ((joinedGW != null) &&
//				(joinedGW.size() > 0)) {
//				for (int i=0; i<joinedGW.size(); i++) {
//					if ((joinedGW.get(i) != null) &&
//						(!joinedGW.get(i).equals(mConfiguration.mGW.mID))) {
//						mLogger.warn("Cannot revoke the star property because there are joined GW");
//						return false;
//					}					
//				}
//			}
//		}
//		
//		if (mServiceRegistration == null) {
//			mLogger.warn("Cannot access service registration to change the star property");
//			return false;
//		}
//		// retrieve the current properties
//		ServiceReference reference = mServiceRegistration.getReference();
//		Dictionary<String,Object> dictionary = new Hashtable<String,Object>();
//		String keys[] = reference.getPropertyKeys();
//		for (int i=0; i<keys.length; i++) {
//			if (keys[i].equals(DOSGI_PROP_STAR)) {
//				// Update the star property
//				dictionary.put(keys[i], star ? "1" : "0");
//			} else {
//				dictionary.put(keys[i], reference.getProperty(keys[i]));
//			}			
//		}
//		// update properties
//		mServiceRegistration.setProperties(dictionary);
//		
//		mConfiguration.mGW.mIM.mIsStar = star;
//		mLogger.info("Requested star property change. Now it is: " + star);
//		
//		return true;
//	}
	
	public String getGWStarID() {
		if (mBundleContext == null) return null;
		
		return "<getGWStarID not implemented anymore>";
		
//		try {			
//			ServiceReference[] ref = 
//					mBundleContext.getServiceReferences(InstanceManagerExternalIF.class.getName(),
//												        "(&(objectClass=" + InstanceManagerExternalIF.class.getName() + ")" +
//											            "(" + InstanceManager.DOSGI_PROP_STAR + "=1))");
//				
//			if ((ref != null) && (ref.length > 0)) {
//				InstanceManagerExternalIF serv = ((InstanceManagerExternalIF)mBundleContext.getService(ref[0]));
//				return serv.getGWID();
//			}
//		} catch (java.lang.NoClassDefFoundError e) {
//		    mLogger.error("No class definition found for InstanceManagerExternalIF Service");
//		    return null;
//		} catch (Exception e) {
//			mLogger.error("Cannot get GW star ID: " + e.getMessage());
//			return null;
//		}
//			
//		return null;
	}		
	
	public Vector<String> getJoinedGWs() {
		if ((mIM != null) &&
			(mIM.mGWRegistry != null)) {
			return mIM.mGWRegistry.getGWList();
		}
		return null;
	}
	
	public boolean requestJoin(String gwId) {
		mLogger.info("Requested to start the join procedure with the IM having ID: " + gwId);
//		if (mConfiguration == null) mLogger.warn("null config");
//		if (mConfiguration.mGW == null) mLogger.warn("null gw");
//		if (mConfiguration.mGW.mIM == null) mLogger.warn("null im");
		if (mConfiguration.mGW.mIM.mIsStar) {
			mLogger.warn("This IM is star and cannot start the join procedure");
			return false;
		}
		
		//TODO: CHECK THAT THIS IM DOES NOT BELONG TO AN INSTANCE
		
		if (mBundleContext == null) return false;
		
		try {			
			InstanceManagerExternalIF service = (InstanceManagerExternalIF)(mBundleContext.getService(mIM.getIM(gwId)));
			if (service != null) {
				// Start the join procedure in a separate thread
				JoinThread jt = new JoinThread(mIM, 
						                       service, 
						                       mConfiguration.mGW.mID, 
						                       mConfiguration.mGW.mCredentials);
				mLogger.info("Starting the join thread");
				jt.start();
			} else {
				mLogger.warn("Cannot find the IM(" + gwId + ") to request the join");
				return false;
			}
		} catch (java.lang.NoClassDefFoundError e) {
		    mLogger.error("No class definition found for InstanceManagerExternalIF Service");
		    return false;
		} catch (Exception e) {
			mLogger.error("Cannot request join: " + e.getMessage());
			return false;
		}	
		
		return true;
	}
	
	public boolean requestDisjoin() {
		mLogger.info("Requested to start the disjoin procedure with IM*");
		if (mConfiguration.mGW.mIM.mIsStar) {
			mLogger.warn("This IM is star and cannot start the disjoin procedure");
			return false;
		}
		
		if (mBundleContext == null) return false;

		try {			
			// search for the IM*
			InstanceManagerExternalIF serv = 
					(InstanceManagerExternalIF)(mBundleContext.getService(mIM.getIMStar(mConfiguration.mGW.mInstanceId)));
			if (serv != null) {
				// Start the join procedure in a separate thread
				JoinThread jt = new JoinThread(mIM, 
						                       serv, 
						                       mConfiguration.mGW.mID, 
						                       mConfiguration.mGW.mCredentials, 
						                       false);
				mLogger.info("Starting the disjoin thread");
				jt.start();
			} else {
				mLogger.warn("Cannot find the IM* to request the disjoin");
				return false;
			}
		} catch (java.lang.NoClassDefFoundError e) {
		    mLogger.error("No class definition found for InstanceManagerExternalIF Service");
		    return false;
		} catch (Exception e) {
			mLogger.error("Cannot request the disjoin: " + e.getMessage());
			return false;
		}	
		
		return true;
	}
	
	
	public String getInstanceInfo() {
		JsonObject instanceInfo;
		HashMap<String,String> map;
		ArrayList<HashMap<String,String>> gwList;
		Gson gson = new Gson();
		String result = "";
		ServiceReference IMs[];
		int i;
				
		mLogger.info("Requested instance info");
		
		JsonObject jsonRequest = new JsonObject();
		try {
			// info about this GW
			instanceInfo = new JsonObject();
			instanceInfo.addProperty("gwid", mConfiguration.mGW.mID);
			if (mConfiguration.mGW.mIM.mIsStar) instanceInfo.addProperty("gwtype", GW_TYPE_STAR);
			else if (mConfiguration.mGW.mIM.mIsBackupStar) instanceInfo.addProperty("gwtype", GW_TYPE_BACKUP_STAR);
			else instanceInfo.addProperty("gwtype", GW_TYPE_NORMAL);
			
			instanceInfo.addProperty("gwdescription", mConfiguration.mGW.mDescription);
			if ((mConfiguration.mGW.mInstanceId != null) && (mConfiguration.mGW.mInstanceId.length() > 0)) {
				instanceInfo.addProperty("instanceid", mConfiguration.mGW.mInstanceId);
			}
		} catch (Exception e) {
			mLogger.error("Error composing GW info: " + e.getMessage());
			return "error:composing GW info: " + e.getMessage();
		}			
		
		try {
			// info about the GWs belonging to the instance
			if (mIM == null) mLogger.info("IM is null");
            IMs = mIM.getAvailableIMs(mConfiguration.mGW.mInstanceId, false);
		} catch (Exception e) {
			mLogger.error("Error getting IMs in the instance: " + e.getMessage());
			return "error:getting IMs in the instance: " + e.getMessage();
		}

		if (IMs != null) {
	        try {
			    gwList = new ArrayList<HashMap<String,String>>();
	            for (i=0; i < IMs.length; i++) {
					InstanceManagerExternalIF serv = 
							((InstanceManagerExternalIF)mBundleContext.getService(IMs[i]));
					if (serv != null) {
						map = new HashMap<String,String>();
					    map.put("gwid", serv.getGWID());
					    map.put("address", serv.getAdminAddress());
					    map.put("description", serv.getDescription());
					    gwList.add(map);
					} else {
						mLogger.warn("Service n." + i + " is null");
					}
	            }
			    instanceInfo.add("gwlist", gson.toJsonTree(gwList));
	        } catch (Exception e) {
				mLogger.error("Error composing instance info: " + e.getMessage());
				return "error:composing instance info: " + e.getMessage();
			}
		}
		    
	    try {
		    // info about the visible GW*s
		    IMs = mIM.getAvailableIMs(null, true);
	    } catch (Exception e) {
			mLogger.error("Error getting visible IM*s: " + e.getMessage());
			return "error:getting visible IM*s: " + e.getMessage();
		}

	    if (IMs != null) {
		    try {
			    gwList = new ArrayList<HashMap<String,String>>();
	            for (i=0; i < IMs.length; i++) {
					InstanceManagerExternalIF serv = 
							((InstanceManagerExternalIF)mBundleContext.getService(IMs[i]));
					if (serv != null) {
						map = new HashMap<String,String>();
					    map.put("gwid", serv.getGWID());
					    gwList.add(map);
					}
	            }		    
			    instanceInfo.add("gwstarlist", gson.toJsonTree(gwList));
		    } catch (Exception e) {
				mLogger.error("Error composing instance info: " + e.getMessage());
				return "error:composing instance info: " + e.getMessage();
			}
	    }
		    
	    try {
			jsonRequest.add("instanceInfo", instanceInfo);
			result = jsonRequest.toString();
		} catch (Exception e) {
			mLogger.error("Error returning the instance info: "	+ e.getMessage());
			return "error:returning the instance info: "	+ e.getMessage();
		}

		return result;
	}

	
	public boolean requestGwCertificate(String countryCode, 
			String state, 
			String location, 
			String orgName, 
			String gwId) {
		
		IJoinInstanceService secm = mIM.getSECMJoinService();
		if (secm == null) {
			mLogger.error("Got null SECM during GW certificate request");
			return false;
		}
		
		try {
			return secm.requestGwCertificate(countryCode, state, location, orgName, gwId);
		} catch (Exception e) {
			mLogger.error("Exception requesting the GW certificate: " + e.getMessage());
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			mLogger.error("Stack trace: " + sw.toString());
			return false;
		}
	}
	
	
	/** Logger */
	private static Logger mLogger = Logger.getLogger(InstanceManager.LOGGER_NAME);
	
	/** Configuration */
	private static Configuration mConfiguration;

	/** The OSGi context */
	private static BundleContext mBundleContext;

//	/** The injected service registration */
//	private static ServiceRegistration mServiceRegistration;
	
	/** The IM exposing this InstanceManagerIF */
	private static InstanceManager mIM;

//	/** The tracker to discover the IM* (used only by non-star IMs) */
//	private static ServiceTracker mIMStarTracker;
	
//	/** The tracker customizer to define actions on IM* service change events */
//	private static TrackerIMCustomizer mIMTrackerCustomizer;
	
	private static String mGWID;
	private static boolean mIsStar;
	private static boolean mIsBackupStar;
	private static String mCredentials;
	private static String mDescription;
	private static String mAdminAddress;
	private static String mInstanceId;
	private static boolean mAutomaticJoin;
	private static String mCountryCode;
	private static String mState;
	private static String mLocation;
	private static String mOrgName;
}
