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

package eu.betaas.service.servicemanager;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import com.google.gson.JsonObject;

import eu.betaas.service.securitymanager.service.IAuthorizationService;
import eu.betaas.service.servicemanager.application.messages.DataNotification;
import eu.betaas.service.servicemanager.application.messages.InstallNotification;
import eu.betaas.service.servicemanager.application.messages.SLAViolationNotification;
import eu.betaas.service.servicemanager.application.messages.ServiceInstallation;
import eu.betaas.service.servicemanager.application.registry.AppRegistryRow;
import eu.betaas.service.servicemanager.application.registry.AppService;
import eu.betaas.service.servicemanager.application.registry.ApplicationRegistry;
import eu.betaas.service.servicemanager.extended.registry.ExtendedRegistry;
import eu.betaas.taas.bigdatamanager.database.service.IBigDataDatabaseService;
import eu.betaas.taas.qosmanager.api.QoSManagerInternalIF;
import eu.betaas.taas.taasresourcesmanager.api.TaaSResourceManager;

/**
 * This class represent the Service Manager and implements its inner functionalities
 * @author Intecs
 */
public class ServiceManager {
	/** The name of the log4j logger to be used by SM */
	public final static String LOGGER_NAME = "betaas.service";
	
    public enum NotificationType {
    	INSTALLATION,
    	DATA,
    	SLA_VIOLATION;
    }
	
	/**
	 * @return this class singleton
	 */
	public static ServiceManager getInstance() {
		
		if (mServiceManager == null) {
			mServiceManager = new ServiceManager();
			try {
				mServiceManager.init();
			} catch (Exception e) {
				mLogger.error("Error during SM initialization: " + e.getMessage());
			}
		}
		
		return mServiceManager;
	}	
	
	
	/**
	 * Setter for the OSGi context. Only the first call with non-null value has effect.
	 * @param context
	 */
	public static synchronized void setContext(BundleContext context) {
		if (mContext == null) {
			mContext = context;
		}
	}
	
	
	/**
	 * Setter for the GW identifier
	 * @param gwId
	 */
	public void setGwId(String gwId) {
		mGWId = gwId;
	}
	
	/** 
	 * Getter for GW identifier 
	 */
	public String getGWId() {
		return mGWId;
	}
	
	/**
	 * @return the application registry of this SM
	 */
	public ApplicationRegistry getAppRegistry() {
		return mApplicationReg;
	}
	
	/**
	 * @return the application registry of this SM
	 */
	public ExtendedRegistry getExtendedRegistry() {
		return mExtendedReg;
	}
	
	/**
	 * Notifies to the application that it has been installed and it has been assigned an ID
	 * @param appID the assigned application identifier
	 * @throws Exception
	 */
	public void notifyAssignedAppID(String appID) throws Exception {
		
		mLogger.info("Notifying the assigned ID (" + appID + ") to the application");
		
		// Get the install information
		AppRegistryRow regRow = mApplicationReg.getApp(appID, true);
		if (regRow == null) throw new Exception("Application not found in the registry (notifyAssignedAppID)");
		if ((regRow.mNotificationAddress == null) || (regRow.mNotificationAddress.isEmpty())) {
			mLogger.warn("Notification address not specified, notification will not be sent");
			return;
		}
		
		// Create the install notification
		InstallNotification notification = new InstallNotification();
		notification.setInstallSuccess(1);
		notification.setAppID(regRow.mAppID);
		notification.setMessage("Application successfully installed");
		
		ArrayList<ServiceInstallation> list = new ArrayList<ServiceInstallation>();
		for (AppService serv : regRow.mServiceList) {
			ServiceInstallation servInst = new ServiceInstallation();
			
			// NOTE: the service ID is composed by TaaSRM starting with the Application ID that
			//       starts with the GW ID.
			servInst.setServiceID(serv.mServiceID);
			servInst.setToken(serv.mRequirements.mCredentials);
			
			list.add(servInst);
		}
		notification.setServiceList(list);
	
		sendRESTNotification(notification, regRow.mNotificationAddress, ServiceManager.NotificationType.INSTALLATION);
		
		mLogger.info("Application ID notified");
	}
	
	/**
	 * Notifies a SLA violation on a service to the application that installed it
	 */
	public void notifySLAViolation(String appID, String serviceID) throws Exception {
		// Get the install information
		AppRegistryRow regRow = mApplicationReg.getApp(appID, true);
		if (regRow == null) throw new Exception("Application not found in the registry (notifySLAViolation)");
		if ((regRow.mNotificationAddress == null) || (regRow.mNotificationAddress.isEmpty())) {
			mLogger.warn("Notification address not specified, notification will not be sent");
			return;
		}
		
		// Create the SLA violation notification
		SLAViolationNotification notification = new SLAViolationNotification();
		notification.setServiceID(serviceID);
			
		//TODO: Based on the notification address, use REST or other mechanisms like Google Cloud Messaging
		sendRESTNotification(notification, regRow.mNotificationAddress, ServiceManager.NotificationType.SLA_VIOLATION);		
	}
	
	
	/**
	 * Notifies data from Thing Services to the application
	 * @param appID
	 * @param serviceID
	 * @param data
	 * @throws Exception
	 */
	public void notifyAppData(String appID, String serviceID, JsonObject data) throws Exception {
		// Get the install information
		AppRegistryRow regRow = mApplicationReg.getApp(appID, true);
		if (regRow == null) throw new Exception("Application not found in the registry (notifyAppData)");
		if ((regRow.mNotificationAddress == null) || (regRow.mNotificationAddress.isEmpty())) {
			mLogger.warn("Notification address not specified, notification will not be sent");
			return;
		}
		
		// Create the data notification
		DataNotification notification = new DataNotification();
		notification.setServiceID(serviceID);
		if (data == null) notification.setData(null);
		else notification.setData(data.toString());
	
		//TODO: Based on the notification address, use REST or other mechanisms like Google Cloud Messaging
		sendRESTNotification(notification, regRow.mNotificationAddress, ServiceManager.NotificationType.DATA);		
	}
	
	
	/**
	 * Notifies the application that the installation could not be completed
	 * @param appID the assigned application identifier
	 * @param msg the message to be sent
	 * @throws Exception
	 */
	public void notifyInstallError(String appID, String msg) throws Exception {
		String appName = null;
		
		// Get the install information
		AppRegistryRow regRow = mApplicationReg.getApp(appID, false);
		if (regRow == null) throw new Exception("Application not found in the registry (notifyInstallError)");
		if ((regRow.mNotificationAddress == null) || (regRow.mNotificationAddress.isEmpty())) {
			mLogger.warn("Notification address not specified, notification will not be sent");
			return;
		}
		
		// Create the install notification
		InstallNotification notification = new InstallNotification();
		notification.setInstallSuccess(0);
		notification.setAppID("-");
		notification.setMessage("Error installing " + appName + ": " + msg);
		
		sendRESTNotification(notification, regRow.mNotificationAddress, NotificationType.INSTALLATION);
		
		mLogger.info("Install error notification sent");
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
	
	/** 
	 * @return the object that implements the TaaSRM interface 
	 */
	public TaaSResourceManager getResourceManagerIF() {
		if (mContext == null) {
			mLogger.error("Cannot get resource manager IF: null context");
			return null;
		}
		
		try {
			ServiceReference ref = mContext.getServiceReference(TaaSResourceManager.class.getName());
				
			if (ref != null) {
				return ((TaaSResourceManager)mContext.getService(ref));
			}
		} catch (java.lang.NoClassDefFoundError e) {
		    mLogger.error("No class definition found for TaaSRM");
		    return null;
		} catch (Exception e) {
			mLogger.error("TaaSRM not available: " + e.getMessage());
			return null;
		}
			
		return null;
	}
	
	/**
	 * @return the object that implements the QoSM interface 
	 */
	public QoSManagerInternalIF getQoSMIF() {
		if (mContext == null) {
			mLogger.error("Cannot get QoSM IF: null context");
			return null;
		}
		
		try {
			ServiceReference ref = mContext.getServiceReference(QoSManagerInternalIF.class.getName());
			
			if (ref != null) {
				return ((QoSManagerInternalIF)mContext.getService(ref));
			}
		} catch (java.lang.NoClassDefFoundError e) {
		    mLogger.error("No class definition found for QoSM");
		    return null;
		} catch (Exception e) {
			mLogger.error("QoSM not available: " + e.getMessage());
			return null;
		}
		
		return null;
	}
	
	
	/**
	 * @return the object that implements the Security interface 
	 */
	public IAuthorizationService getSECMIF() {
		if (mContext == null) {
			mLogger.error("Cannot get SECM IF: null context");
			return null;
		}
		
		try {
			ServiceReference ref = mContext.getServiceReference(IAuthorizationService.class.getName());
			
			if (ref != null) {
				return ((IAuthorizationService)mContext.getService(ref));
			}
		} catch (java.lang.NoClassDefFoundError e) {
		    mLogger.error("No class definition found for SECM");
		    return null;
		} catch (Exception e) {
			mLogger.error("SECM not available: " + e.getMessage());
			return null;
		}
		
		return null;
	}

	
	private void sendRESTNotification(InstallNotification msg, String address, NotificationType type) throws Exception {
		String content = "<InstallNotification>"+
							 "<message>" + msg.getMessage() + "</message>"+
							 "<installSuccess>" + msg.getInstallSuccess() + "</installSuccess>"+
							 "<appID>" + msg.getAppID() + "</appID>";
//+ "<serviceList>";
		if (msg.getServiceList() != null) {
			for (int i=0; i < msg.getServiceList().size(); i++) {
				content += "<ServiceInstallation>";
				  content += "<serviceID>" + msg.getServiceList().get(i).getServiceID() + "</serviceID>";
				  content += "<token>" + msg.getServiceList().get(i).getToken() + "</token>";
				content += "</ServiceInstallation>";
			}
		}
							  
//content += "</serviceList>";
		content += "</InstallNotification>";
		mLogger.debug("The notification to send is:");
		mLogger.debug(content);
		sendRESTNotification(content, address, type);
	}
	
	
	private void sendRESTNotification(DataNotification msg, String address, NotificationType type) throws Exception {
		String content = "<DataNotification>"+
							 "<serviceID>" + msg.getServiceID() + "</serviceID>"+
							 "<data>" + msg.getData() + "</data>" +
						 "</DataNotification>";
		sendRESTNotification(content, address, type);
	}
	
	private void sendRESTNotification(SLAViolationNotification msg, String address, NotificationType type) throws Exception {
		String content = "<SLAViolationNotification>"+
							 "<serviceID>" + msg.getServiceID() + "</serviceID>"+
						 "</SLAViolationNotification>";
		sendRESTNotification(content, address, type);
	}
	
	
	private void sendRESTNotification(String content, String address, NotificationType type) throws Exception {
		
		String completeAddress = address;
		OutputStreamWriter out = null;
		BufferedReader br = null;
		
		try {
			switch (type) {
			
				case INSTALLATION:
					completeAddress = address + "rest/installNotification";
					mLogger.debug("Sending install notification to the application (" + completeAddress + ")");
					break;
					
				case DATA:
					completeAddress = address + "rest/dataNotification";
					mLogger.debug(completeAddress);
					break;
					
				case SLA_VIOLATION:
					completeAddress = address + "rest/SLAViolationNotification";
					mLogger.debug(completeAddress);
					break;
					
				default:
					mLogger.warn("Requested to send a not managed notification type to the app");
					return;
			}			
			
			URL url = new URL(completeAddress);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("PUT");
			conn.setDoOutput(true);
			conn.setRequestProperty("Content-Type", "application/xml; charset=utf-8");
			
			out = new OutputStreamWriter(conn.getOutputStream());
			out.write(content);
			out.close();
			br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line;
	
			mLogger.info("Notification sent. Response:");
			while ((line = br.readLine()) != null) {
				mLogger.info(line);
			}
		} catch (Exception e) {
			mLogger.error("Cannot send REST notification");
			e.printStackTrace();
			throw e;
		} finally {
			if (out != null) out.close();
			if (br != null) br.close();
		}
	}	

	/** This class singleton */
	private static ServiceManager mServiceManager;
	
	/**
	 * Constructor
	 */
	private ServiceManager() {
	}
	
	/**
	 * Perform the SM initialization. It is done separately from the constructor to avoid 
	 * problems with OSGi constructor calls.
	 */
	public void init() throws Exception {
		mApplicationReg = new ApplicationRegistry();
		try {
			mLogger.info("Loading Application Registry from DB");
			mApplicationReg.loadFromDB();
			mLogger.info(mApplicationReg.getSize() + " registered application found");
		} catch (Exception e) {
			mLogger.error("Cannot load application registry: " + e.getMessage());
		}
		
		mExtendedReg = new ExtendedRegistry();
		try {
			mLogger.info("Loading Extended Registry from DB");
			mExtendedReg.loadFromDB();
			mLogger.info(mExtendedReg.getSize() + " registered extended services found");
		} catch (Exception e) {
			mLogger.error("Cannot load extended services registry: " + e.getMessage());
		}
	}
	
	/** Logger */
	private static Logger mLogger = Logger.getLogger(ServiceManager.LOGGER_NAME);

	/** The OSGi context */
	private static BundleContext mContext = null;

	/** This is the service Registry */
	private ApplicationRegistry mApplicationReg;
	
	/** The extended service registry */
	private ExtendedRegistry mExtendedReg;
	
	/** The GW identifier */
	private String mGWId;
	
}
