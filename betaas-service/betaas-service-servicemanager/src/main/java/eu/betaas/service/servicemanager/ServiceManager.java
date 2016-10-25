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
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

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
import eu.betaas.rabbitmq.publisher.interfaces.Publisher;
import eu.betaas.rabbitmq.publisher.interfaces.utils.Message;
import eu.betaas.rabbitmq.publisher.interfaces.utils.MessageBuilder;

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
	
	public void setGcmKey(String gcmKey) {
		mGcmKey = gcmKey;
	}
	
	public String getGcmKey() {
		return mGcmKey;
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
	 * @param isInstalled if installation succeeded
	 * @param errorMessage the error that caused installation failure
	 * @param appName Application that failed to be installed
	 * @throws Exception
	 */
	public void notifyAssignedAppID(String appID, boolean isInstalled, String errorMessage, String serviceDescription) throws Exception {
		
		mLogger.info("Notifying the assigned ID (" + appID + ") to the application");
		
		// Get the install information
		AppRegistryRow regRow = mApplicationReg.getApp(appID, isInstalled);
		if (regRow == null) throw new Exception("Application not found in the registry (notifyAssignedAppID)");
		
		if ((regRow.mNotificationAddress == null) || (regRow.mNotificationAddress.isEmpty())) {
			mLogger.warn("Notification address not specified, notification will not be sent");
			return;
		}
		
		// Create the install notification
		InstallNotification notification = new InstallNotification();
		if(isInstalled){
			notification.setInstallSuccess(1);
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
		}else{
			notification.setInstallSuccess(0);
			notification.setMessage("Application installation failed. Service Description: " + serviceDescription + " Error Message = " + errorMessage);
		}
			
		notification.setAppID(regRow.mAppID);
		
		
		
			
		if ((regRow.mNotificationAddress != null) && (!regRow.mNotificationAddress.isEmpty())) {
			
			String dest;
			if (regRow.mNotificationAddress.startsWith(ApplicationRegistry.NOTIFICATION_TYPE_GCM)) {
				dest = regRow.mNotificationAddress.substring(ApplicationRegistry.NOTIFICATION_TYPE_GCM.length());
				sendGCMNotification(notification.toString(), dest);
			} else if (regRow.mNotificationAddress.startsWith(ApplicationRegistry.NOTIFICATION_TYPE_REST)){
				dest = regRow.mNotificationAddress.substring(ApplicationRegistry.NOTIFICATION_TYPE_REST.length());
				sendRESTNotification(notification, dest);
			} else {
				mLogger.warn("Unexpected notification address: " + regRow.mNotificationAddress);
			}
		}
		
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
			
		if ((regRow.mNotificationAddress != null) && (!regRow.mNotificationAddress.isEmpty())) {
			
			String dest;
			if (regRow.mNotificationAddress.startsWith(ApplicationRegistry.NOTIFICATION_TYPE_GCM)) {
				dest = regRow.mNotificationAddress.substring(ApplicationRegistry.NOTIFICATION_TYPE_GCM.length());
				sendGCMNotification(notification.toString(), dest);
			} else if (regRow.mNotificationAddress.startsWith(ApplicationRegistry.NOTIFICATION_TYPE_REST)){
				dest = regRow.mNotificationAddress.substring(ApplicationRegistry.NOTIFICATION_TYPE_REST.length());
				sendRESTNotification(notification, dest);
			} else {
				mLogger.warn("Unexpected notification address: " + regRow.mNotificationAddress);
			}
		}	
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

		if ((regRow.mNotificationAddress != null) && (!regRow.mNotificationAddress.isEmpty())) {
			
			String dest;
			if (regRow.mNotificationAddress.startsWith(ApplicationRegistry.NOTIFICATION_TYPE_GCM)) {
				dest = regRow.mNotificationAddress.substring(ApplicationRegistry.NOTIFICATION_TYPE_GCM.length());
				sendGCMNotification(notification.toString(), dest);
			} else if (regRow.mNotificationAddress.startsWith(ApplicationRegistry.NOTIFICATION_TYPE_REST)){
				dest = regRow.mNotificationAddress.substring(ApplicationRegistry.NOTIFICATION_TYPE_REST.length());
				sendRESTNotification(notification, dest);
			} else {
				mLogger.warn("Unexpected notification address: " + regRow.mNotificationAddress);
			}
		}
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
		
		if ((regRow.mNotificationAddress != null) && (!regRow.mNotificationAddress.isEmpty())) {
			
			String dest;
			if (regRow.mNotificationAddress.startsWith(ApplicationRegistry.NOTIFICATION_TYPE_GCM)) {
				dest = regRow.mNotificationAddress.substring(ApplicationRegistry.NOTIFICATION_TYPE_GCM.length());
				sendGCMNotification(notification.toString(), dest);
			} else if (regRow.mNotificationAddress.startsWith(ApplicationRegistry.NOTIFICATION_TYPE_REST)){
				dest = regRow.mNotificationAddress.substring(ApplicationRegistry.NOTIFICATION_TYPE_REST.length());
				sendRESTNotification(notification, dest);
			} else {
				mLogger.warn("Unexpected notification address: " + regRow.mNotificationAddress);
			}
		}
		
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

	
	private void sendRESTNotification(InstallNotification msg, String address) throws Exception {
		mLogger.debug("The notification to send is:");
		String content = msg.toString();
		mLogger.debug(content);
		sendRESTNotification(content, address, ServiceManager.NotificationType.INSTALLATION);
	}
	
	
	private void sendRESTNotification(DataNotification msg, String address) throws Exception {
		String content = msg.toString();
		sendRESTNotification(content, address, ServiceManager.NotificationType.DATA);
	}
	
	private void sendRESTNotification(SLAViolationNotification msg, String address) throws Exception {
		String content = msg.toString();
		sendRESTNotification(content, address, ServiceManager.NotificationType.SLA_VIOLATION);
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
	
	
	/**
	 * Send a message through the Google Cloud Messaging
	 * @param notification the message content
	 * @param GCMId the registration ID specified by the application that sent the manifest
	 */
	private void sendGCMNotification(String notification, String GCMId) {
		GCMNotifier gcm = new GCMNotifier(notification, GCMId);
		gcm.start();
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
	
	public static void busMessage(String message, String level, String type){
		
		String key = type + ".service";
				
		mLogger.info("Checking queue");
		mLogger.info("Sending to queue");
		ServiceReference serviceReference = mContext.getServiceReference(Publisher.class.getName());
		mLogger.info("Sending to queue");
		if (serviceReference==null)return;
		
		Publisher service = (Publisher) mContext.getService(serviceReference); 

		Message infoMessage = new Message();
		
		Calendar calendar = Calendar.getInstance();
		java.util.Date now = calendar.getTime();

		
		infoMessage.setTimestamp(now.getTime());
		infoMessage.setLayer(Message.Layer.SERVICE);
		infoMessage.setLevel(level);
		infoMessage.setOrigin("service-manager");
		infoMessage.setDescritpion(message);
		
		MessageBuilder newMessage = new MessageBuilder();
		
		
		mLogger.info("Sending key: " + key + " message: "+ newMessage.getJsonEquivalent(infoMessage));
		service.publish(key, newMessage.getJsonEquivalent(infoMessage));
		mLogger.info("Message sent to queue");
		
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
	
	/** The Google Cloud Messaging key used by SM to authenticate to GCM services */
	private String mGcmKey;
	
	public final static String MONITORING = "monitoring";
	public final static String DEPENDABILITY = "dependability";

}
