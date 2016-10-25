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

package eu.betaas.service.instancemanager;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Calendar;
import java.util.Date;
import java.util.Dictionary;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTracker;

import eu.betaas.service.instancemanager.api.InstanceManagerExternalIF;
import eu.betaas.service.instancemanager.api.impl.ServicePropertiesHolder;
import eu.betaas.service.instancemanager.config.Configuration;
import eu.betaas.taas.taasresourcesmanager.api.TaaSResourceManager;
import eu.betaas.taas.securitymanager.core.service.IInitGWStarService;
import eu.betaas.taas.securitymanager.core.service.IJoinInstanceService;
import eu.betaas.rabbitmq.publisher.interfaces.Publisher;
import eu.betaas.rabbitmq.publisher.interfaces.utils.Message;
import eu.betaas.rabbitmq.publisher.interfaces.utils.MessageBuilder;

/**
 * This class represents the IM and implements its inner functionalities
 * @author Intecs
 */
public class InstanceManager implements IMStarHandler {

	public final static String DOSGI_PROP_INSTANCE_ID = "instanceId";
	public final static String DOSGI_PROP_GWID = "gwId";
	public final static String DOSGI_PROP_STAR = "isStar";
	public final static String DOSGI_PROP_BACKUP_STAR = "isBackupStar";
	public final static String DOSGI_PROP_ADMIN_ADDRESS = "adminAddress";
	public final static String DOSGI_PROP_ADMIN_DESCRIPTION = "description";
	
	public final static String MONITORING = "monitoring";
	public final static String DEPENDABILITY = "dependability";
		
	/** The name of the log4j logger to be used by IM */
	public final static String LOGGER_NAME = "betaas.service";
	
	/** Default time to wait before the IM tracker can be used after opening */
	public final static int DEFAULT_TIME_TO_WAIT_FOR_TRACKING_MILLIS = 5000;	
	
	
	public static void setWaitTime(int time) {
		mLogger.info("Tracker wait time=" + time);
		mWaitTime = time;
	}
	
	/**
	 * Constructor
	 * @param config the bundle configuration
	 * @param contextProvider the object providing the OSGi bundle context
	 * @param service the service provided by this IM
	 * @param serviceRegistration the OSGi service registration
	 */
	public InstanceManager(Configuration config, 
			               ContextProvider contextProvider, 
			               InstanceManagerExternalIF service) {
		mIMInfo = new IMInfo();
		
		mConfiguration = config;
		mContextProvider = contextProvider;
		mService = service;
				
		mGWRegistry = new GWRegistry();
		// Put this IM into the registry
		GWRegistryRow gw = new GWRegistryRow();
		gw.mGWID = config.mGW.mID;
		if (!config.mGW.mIM.mIsStar) {
			// Set Joined to false so that this IM will request the join to IM*.
			// In case this IM already joined the instance in the past, IM* will just skip the request
			gw.mJoined = false;
		} else {
			// the GW itself is the star so in some sense it has joined the instance
			gw.mJoined = true;
		}
		mGWRegistry.addGW(gw);
		
		mIMTracker = new ServiceTracker(contextProvider.getBundleContext(), InstanceManagerExternalIF.class.getName(), null);
		mIMTracker.open();
		
		mTrackerOpenDate = new GregorianCalendar();
		mTrackerOpenDate.setTime(new Date());
	}
	
	/**
	 * Checks if this IM is the only one visible. If so, promote itself to IM*
	 * @return String if another GW* is found, return its ID, null otherwise
	 */
	public String manageAloneIM() {
		ServiceReference[] IMs = getAvailableIMs(null, false);
		String id = null;
		boolean promote = false;
		String result = null;
		
		if (IMs == null) {
			mLogger.warn("No IM found");
			promote = true;
		} else if (IMs.length == 1) {
			
			mLogger.info("One IM found. Checking if it is this IM.");
			
			// It seems the only IM found is this IM itself. Check also the GW ID			
			InstanceManagerExternalIF serv = 
					((InstanceManagerExternalIF)(mContextProvider.getBundleContext()).getService(IMs[0]));
			
			if (serv != null) {
				id = serv.getGWID();
				mLogger.info("The found IM has ID: " + id); 
			
				if ((id == null) || (id.compareTo(mConfiguration.mGW.mID) == 0)) {
					promote = true;
					mLogger.info("Promoting to IM*");
				} else {
					mLogger.info("This IM is not promoted to IM*");
				}
			}
		} else if (IMs.length == 0) {
			promote = true;
		} else {
			// more than 1 GW, check if there is a IM*
			for (int gwi=0; gwi < IMs.length; gwi++) {
				InstanceManagerExternalIF serv = 
					((InstanceManagerExternalIF)(mContextProvider.getBundleContext()).getService(IMs[gwi]));
			
				if (serv != null) {
					String idtmp = serv.getGWID();
					boolean isStar = serv.isGWStar();
					mLogger.info("The found IM has ID: " + idtmp + " STAR: " + isStar); 
					if (isStar) id = idtmp;
				}
			}
		}
		
		if (promote) {
			mConfiguration.mGW.mIM.mIsStar = true;;
			if (!setDOSGIProperty(DOSGI_PROP_STAR, "1")) {
				mLogger.error("Cannot change star property");
				return null;
			}
			mLogger.info("Updated the DOSGi star property");
			
			// Change the instance identifier to the GW id as the IM* of the instance is the IM itself
			// Note that in general the instance id is the identifier of the GW to which IM* belongs 
			mConfiguration.mGW.mInstanceId = mConfiguration.mGW.mID;
			if (!setDOSGIProperty(DOSGI_PROP_INSTANCE_ID, mConfiguration.mGW.mID)) {
				mLogger.error("Cannot change instance identifier property");
				return null;
			}
			mLogger.info("Updated the DOSGi instance ID property");
			
			mLogger.info("IM promoted to IM*");
			
			initGWCertificate();
			
			result = null;
		} else {
			mLogger.info("IM not promoted");
			result = id;
		}
		
		return result;
	}
	
	public void handleNewServiceNotification(InstanceManagerExternalIF IMStarService) {
		
		mLogger.info("IM* discovered");
		
		if (mConfiguration.mGW.mIM.mIsStar) {
			mLogger.info("IM is the star instance: skipping join.");
			return;
		}
		if (IMStarService == null) {
			mLogger.warn("Got null IM* service: skipping join.");
			return;
		}
		
		// Start the join procedure in a separate thread
		JoinThread jt = new JoinThread(this, IMStarService, mConfiguration.mGW.mID, mConfiguration.mGW.mCredentials);
		mLogger.info("Starting the join thread");
		jt.start();
	}
	
	/**
	 * Request TaaSRM, SM and BDM to synchronize with the corresponding 
	 * components of IM*
	 */
	public void synchronize() {
		
		// Request synchronization only if this IM belongs to a joined GW
		// and it is not IM*
		if (!isJoined()) {
			mLogger.info("Skipping synch request (either IM is star or does not belong to an instance)");
			return;
		}
		mLogger.info("Requesting components to synchronize");
		
		TaaSResourceManager taasrm = getTaaSRM();
		if (taasrm == null) {
			mLogger.error("TaaSRM not available for synchronization");
			return;
		}
		try {
			//TODO: param of TaaSRM::synchronize to be agreed, instanceid should be passed
			taasrm.synchronizeThingServices(mConfiguration.mGW.mID/*, mConfiguration.mGW.mInstanceId*/);
		} catch (Exception e) {
			mLogger.error("Error requesting TaaSRM synchronization: " + e.getMessage());
		}
		
		//TODO: call synchronize of BDM and SM if necessary
		
	}
	
	/**
	 * Updates the GW registry to set the IM itself as joined to an instance, update
	 * the DOSGi property that contains the instance identifier and check
	 * if there is no backup IM* in the joined instance. In that case, promote itself as
	 * backup-IM*.
	 * @param joined true if IM has joined the instance, false if IM disjoined
	 * @param instanceId the identifier of the joined instance, provided by its IM*
	 */
	public synchronized void setJoined(boolean joined, String instanceId) {
		mGWRegistry.setJoined(mConfiguration.mGW.mID, joined);
		
		if (!joined) {
			// Disjoined
			mLogger.info("Clearing instance identifier DOSGi property to empty string");
			mConfiguration.mGW.mInstanceId = "";
			if (!setDOSGIProperty(DOSGI_PROP_INSTANCE_ID, "")) {
				mLogger.info("Cannot update instance ID");
			}
			
			if (mConfiguration.mGW.mIM.mIsBackupStar) {
				mConfiguration.mGW.mIM.mIsBackupStar = false;
				if (!setDOSGIProperty(DOSGI_PROP_BACKUP_STAR, "0")) {
					mLogger.error("Cannot reset the backup star property of DOSGi");
					return;
				} else {
					mLogger.info("Backup star DOSGi property reset");
				}
			}
			
			return;
		}
			
		// Joined
		mLogger.info("Updating instance identifier DOSGi property to: " + instanceId);
		mConfiguration.mGW.mInstanceId = instanceId;
		setDOSGIProperty(DOSGI_PROP_INSTANCE_ID, instanceId);			
		
		// Check if there is no backup-IM* for the joined instance
		boolean backupExists = false;
		ServiceReference IMs[] = getAvailableIMs(instanceId, false);
		if (IMs != null) {
			int i;
			
			mLogger.info("Found " + IMs.length + " IMs belonging to the joined instance");
			
			i=0; 
			while ((!backupExists) && (i < IMs.length)) {
				InstanceManagerExternalIF serv = 
						((InstanceManagerExternalIF)(mContextProvider.getBundleContext()).getService(IMs[i]));
				if ((serv != null) && (serv.isBackupStar())) {
					backupExists = true;
				}
				i++;
			}
			
		} else {
			mLogger.error("No IM found while checking for backup-IM* existence");
			return;
		}

		if (!backupExists) {
			mLogger.info("No backup-IM* found. Promoting.");
			// Promote itself to backup-IM*
			mConfiguration.mGW.mIM.mIsBackupStar = true;
			if (!setDOSGIProperty(DOSGI_PROP_BACKUP_STAR, "1")) {
				mLogger.error("Cannot set the backup star property of DOSGi");
				return;
			} else {
				mLogger.info("Self-promoted to backup-IM*");
				
				//TODO: start tracker for IM* of this instance in order to substitute it if it run away
			}
		} else {
			mLogger.info("Found backup-IM*");
		}
	}
	
	/**
	 * @return the information about this Instance Manager
	 */
	public IMInfo getInfo() {
		mIMInfo.mGWId = mConfiguration.mGW.mID;
		mIMInfo.mIsStar = mConfiguration.mGW.mIM.mIsStar;
		mIMInfo.mCredentials = mConfiguration.mGW.mCredentials;
		mIMInfo.mAdminAddress = mConfiguration.mGW.mAdminAddress;
		mIMInfo.mDescription = mConfiguration.mGW.mDescription;
		
		return mIMInfo;
	}
	
	
	/**
	 * @param gwId the GW id to get the desired IM
	 * @return the IM with the specified gateway identifier, null if it is not found
	 */
	public ServiceReference getIM(String gwId) {
		mLogger.info("Retrieving IM with ID " + gwId);
		ServiceReference result = null;
		
		waitForTracker();
		
		if (mContextProvider == null) {
			mLogger.warn("Cannot get context provider");
			return null;
		}
		BundleContext context = mContextProvider.getBundleContext();
		if (context == null) {
			mLogger.warn("Null bundle context");
			return null;
		}
		
		try {
			String id;
			ServiceReference ref[];
			InstanceManagerExternalIF serv;
			ref = mIMTracker.getServiceReferences();
			
			if (ref != null) {
				mLogger.info("Got " + ref.length + " elements from the tracker");
				int i=0; 
				boolean found = false;
				while ((!found) && (i < ref.length)) {
					if (ref[i] != null) {
						serv = (InstanceManagerExternalIF)(context.getService(ref[i]));
	
						if (serv != null) {
							id = serv.getGWID();
							if ((id != null) && (id.compareTo(gwId) == 0)) {
								found = true;
								result = ref[i];
							}
						}
					}
					i++;
				}
			} else {
				mLogger.warn("Got null service references");
				return null;
			}

			return result;
			
		} catch (java.lang.NoClassDefFoundError e) {
			mLogger.error("Error retrieving IM (no class found): " + e.getMessage());
			return null;
		} catch (Exception e) {
			mLogger.error("Error retrieving IM (exception): " + e.getMessage());
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			mLogger.info("Stack trace: " + sw.toString());
			return null;
		}
	}
	
	
	/**
	 * @param instanceId the instance id to get the desired IM*
	 * @return the IM* of the specified instance, null if it is not found
	 */
	public ServiceReference getIMStar(String instanceId) {
		mLogger.info("Retrieving IM* for instance " + instanceId);
		ServiceReference result = null;
		
		if (mContextProvider == null) {
			mLogger.warn("Cannot get context provider");
			return null;
		}
		BundleContext context = mContextProvider.getBundleContext();
		if (context == null) {
			mLogger.warn("Null bundle context");
			return null;
		}
		
		waitForTracker();
		
		try {
			String id;
			boolean isStar;
			ServiceReference ref[];
			InstanceManagerExternalIF serv;
			ref = mIMTracker.getServiceReferences();
			
			if (ref != null) {
				mLogger.info("Got " + ref.length + " elements from the tracker");
				int i=0; 
				boolean found = false;
				while ((!found) && (i < ref.length)) {
					if (ref[i] != null) {
						serv = (InstanceManagerExternalIF)(context.getService(ref[i]));
	
						if (serv != null) {
							id = serv.getInstanceID();
							isStar = serv.isGWStar();
							if ((id != null) && (id.compareTo(instanceId) == 0) && (isStar)) {
								found = true;
								result = ref[i];
							}
						}
					}
					i++;
				}
			} else {
				mLogger.warn("Got null service references");
				return null;
			}

			return result;
			
		} catch (java.lang.NoClassDefFoundError e) {
			mLogger.error("Error retrieving IM*: " + e.getMessage());
			return null;
		} catch (Exception e) {
			mLogger.error("Error retrieving IM*: " + e.getMessage());
			return null;
		}
	}

	
	/**
	 * @param instanceId if null no filter is performed on instances
	 * @param star if true, a filter to select only star IMs is applied, if false no filter is applied on star property
	 * @return the IM available services with the specified filter or null
	 * @throws InterruptedException 
	 */
	public ServiceReference[] getAvailableIMs(String instanceId, boolean star) {
		mLogger.info("Retrieving available IMs (filter on instanceId=" + instanceId + ", filter on star=" + star + ")");
		
		if (mContextProvider == null) {
			mLogger.warn("Cannot get context provider");
			return null;
		}
		BundleContext context = mContextProvider.getBundleContext();
		if (context == null) {
			mLogger.warn("Null bundle context");
			return null;
		}
		
		waitForTracker();
		
		try {
			ServiceReference ref[];
			
			if ((instanceId == null) && (!star)) {
				ref = mIMTracker.getServiceReferences();
				mLogger.info("Got " + ref.length + " elements from the tracker");
			} else {
				boolean add;
				String id;
				InstanceManagerExternalIF serv;
				Vector<ServiceReference> refArr = new Vector<ServiceReference>();
				ref = mIMTracker.getServiceReferences();
				if (ref != null) {
					mLogger.info("Got " + ref.length + " elements from the tracker");
					for (int i=0; i < ref.length; i++) {
						if (ref[i] != null) {
							serv = (InstanceManagerExternalIF)(context.getService(ref[i]));
	
							if (serv != null) {
								id = serv.getInstanceID();
								if (id != null) {
									add = true;
									
									if ((instanceId != null) && (id.compareTo(instanceId) != 0)) add = false;
									if (star && !(serv.isGWStar())) add = false;
									
									if (add) refArr.add(ref[i]);
								} else {
									mLogger.warn("ID n." + i + " is null");
								}
							} else {
								mLogger.warn("Service " + i + " is null");
							}
						} else {
							mLogger.warn("Service reference " + i + " is null");
						}
					}
				}
				if (refArr.size() == 0) return null;
				
				ref = new ServiceReference[refArr.size()];
				for (int i=0; i < refArr.size(); i++) {
					ref[i] = refArr.elementAt(i);
				}
			}

			return ref;
			
		} catch (java.lang.NoClassDefFoundError e) {
			mLogger.error("Error retrieving available IMs: " + e.getMessage());
			return null;
		} catch (Exception e) {
			mLogger.error("Error retrieving available IMs: " + e.getMessage());
			return null;
		}
	}
	
	
	public Configuration getConfiguration() {
		return mConfiguration;
	}
	

	/**
	 * @return the SECM interface
	 */
	public IInitGWStarService getSECM() {
		if (mContextProvider == null) {
			mLogger.warn("Cannot get context provider for SECM");
			return null;
		}
		BundleContext context = mContextProvider.getBundleContext();
		if (context == null) {
			mLogger.warn("Null bundle context for SECM");
			return null;
		}
		
		try {
			ServiceReference ref = context.getServiceReference(IInitGWStarService.class.getName());
			if (ref != null) {
				return ((IInitGWStarService)context.getService(ref));
			} else {
				mLogger.warn("Null service Reference for SECM");
				return null;
			}
		} catch (java.lang.NoClassDefFoundError e) {
		  return null;
		}
	}
	
	
	/**
	 * @return the SECM interface for GW certificate creation
	 */
	public IJoinInstanceService getSECMJoinService() {
		if (mContextProvider == null) {
			mLogger.warn("Cannot get context provider for SECM join service");
			return null;
		}
		BundleContext context = mContextProvider.getBundleContext();
		if (context == null) {
			mLogger.warn("Null bundle context for SECM join service");
			return null;
		}
		
		try {
			ServiceReference ref = context.getServiceReference(IJoinInstanceService.class.getName());
			if (ref != null) {
				return ((IJoinInstanceService)context.getService(ref));
			} else {
				mLogger.warn("Null service Reference for SECM join service");
				return null;
			}
		} catch (java.lang.NoClassDefFoundError e) {
		  return null;
		}
	}
	
	/**
	 * @return the service provided by this IM
	 */
	public InstanceManagerExternalIF getService() {
		return mService;
	}
	
		
	/** In case of IM* it is the registry of joined GWs. For the other IMs 
	 * it is a one row-registry just to register the IM itself */
	public GWRegistry mGWRegistry;
	


	/**
	 * Check if enough time elapsed since tracker opening. If not, wait.
	 */
	private void waitForTracker() {
		GregorianCalendar now = new GregorianCalendar();
		now.setTime(new Date());
		
		now.add(Calendar.MILLISECOND, -mWaitTime);
		if (now.before(mTrackerOpenDate)) {
			try {
				mLogger.info("Waiting to let tracker gathering info on IMs");
				Thread.sleep(mWaitTime);
			} catch (InterruptedException e) {}
		}		
	}
	
	
	/**
	 * Sets a DOSGi property of IM
	 * @param propertyName
	 * @param value
	 * @param true on success
	 */
	private boolean setDOSGIProperty(String propertyName, String value) {
		ServiceRegistration serviceRegistration = ServicePropertiesHolder.getServiceRegistration();
		if (serviceRegistration == null) {
			mLogger.warn("Cannot access service registration to change the star property");
			return false;
		}
		// retrieve the current properties
		ServiceReference reference = serviceRegistration.getReference();
		Dictionary<String,Object> dictionary = new Hashtable<String,Object>();
		String keys[] = reference.getPropertyKeys();
		for (int i=0; i<keys.length; i++) {
			if (keys[i].equals(propertyName)) {
				// Update the star property
				dictionary.put(keys[i], value);
			} else {
				dictionary.put(keys[i], reference.getProperty(keys[i]));
			}			
		}
		// update properties
		serviceRegistration.setProperties(dictionary);
		
		return true;
	}
	
	
	private void initGWCertificate() {
		mLogger.info("Initializing the GW certificate");
		
		IInitGWStarService secm = getSECM();
		secm.initGwStar(mConfiguration.mGW.mCountryCode,
				mConfiguration.mGW.mState, 
				mConfiguration.mGW.mLocation, 
				mConfiguration.mGW.mOrgName, 
				mConfiguration.mGW.mID);
	}
	
	
	/**
	 * @return the TaaSRM interface
	 */
	private TaaSResourceManager getTaaSRM() {
		if (mContextProvider == null) {
			mLogger.warn("Cannot get context provider for TaaSRM");
			return null;
		}
		BundleContext context = mContextProvider.getBundleContext();
		if (context == null) {
			mLogger.warn("Null bundle context for TaaSRM");
			return null;
		}
		
		try {
			ServiceReference ref = context.getServiceReference(TaaSResourceManager.class.getName());
			if (ref != null) {
				return ((TaaSResourceManager)context.getService(ref));
			} else {
				mLogger.warn("Null service Reference for TaaSRM");
				return null;
			}
		} catch (java.lang.NoClassDefFoundError e) {
		  return null;
		}
	}
	
	private synchronized boolean isJoined() {
		return mGWRegistry.hasJoined(mConfiguration.mGW.mID);
	}
	
	public void busMessage(String message, String level, String type){
		
		String key = type + ".service";
		if (mContextProvider == null) {
			mLogger.warn("Cannot get context provider for TaaSRM");
			return;
		}
		BundleContext context = mContextProvider.getBundleContext();
		if (context == null) {
			mLogger.warn("Null bundle context for TaaSRM");
			return;
		}
				
		mLogger.info("Checking queue");
		mLogger.info("Sending to queue");
		ServiceReference serviceReference = context.getServiceReference(Publisher.class.getName());
		mLogger.info("Sending to queue");
		if (serviceReference==null)return;
		
		Publisher service = (Publisher) context.getService(serviceReference); 
		Message infoMessage = new Message();
		
		Calendar calendar = Calendar.getInstance();
		java.util.Date now = calendar.getTime();

		
		infoMessage.setTimestamp(now.getTime());
		infoMessage.setLayer(Message.Layer.SERVICE);
		infoMessage.setLevel(level);
		infoMessage.setOrigin("instance-manager");
		infoMessage.setDescritpion(message);
		
		MessageBuilder newMessage = new MessageBuilder();
		
		
		mLogger.info("Sending key: " + key + " message: "+ newMessage.getJsonEquivalent(infoMessage));
		service.publish(key, newMessage.getJsonEquivalent(infoMessage));
		mLogger.info("Message sent to queue");
		
	}

	/** Logger */
	private static Logger mLogger = Logger.getLogger(InstanceManager.LOGGER_NAME);	
	
	/** info to be passed to other bundles */
	private static IMInfo mIMInfo;
	
	/** time to wait before the tracker can be used*/
	private static int mWaitTime = InstanceManager.DEFAULT_TIME_TO_WAIT_FOR_TRACKING_MILLIS;

	/** The loaded IM configuration */
	private Configuration mConfiguration;

	/** The bundle context provider */
	private ContextProvider mContextProvider;
	
	/** The IM service */
	private InstanceManagerExternalIF mService;
	
//	/** The IM service registration */
//	private ServiceRegistration mServiceRegistration;

	/** The tracker for other IMs of other GWs */
	private ServiceTracker mIMTracker;
	
	/** Used to wait before using the tracker */
	private GregorianCalendar mTrackerOpenDate;
}
