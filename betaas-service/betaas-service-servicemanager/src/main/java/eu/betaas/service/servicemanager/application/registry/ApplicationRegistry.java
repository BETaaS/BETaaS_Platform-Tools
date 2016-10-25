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

package eu.betaas.service.servicemanager.application.registry;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Vector;

import org.apache.log4j.Logger;

import eu.betaas.service.servicemanager.ServiceManager;
import eu.betaas.service.servicemanager.application.registry.AppRegistryRow.ApplicationInstallationStatus;
import eu.betaas.service.servicemanager.application.registry.AppService.ServiceInstallationStatus;
import eu.betaas.taas.bigdatamanager.database.service.IBigDataDatabaseService;


/**
 * It manages the Application registry 
 * @author Intecs
 */
public class ApplicationRegistry {
    /** Prefix automatically given to services in the registry */
    public final static String APP_SERVICE_NAME_PREFIX = "appService";
    
    
    public final static String NOTIFICATION_TYPE_GCM = "type:gcm:";
    public final static String NOTIFICATION_TYPE_REST = "type:rest:";
    
    public enum NotificationAddressType {
    	REST_NOTIFICATION_ADDRESS,
    	GCM_NOTIFICATION_ADDRESS
    };
	
	/**
	 * Class Constructor
	 */
	public ApplicationRegistry(){
		mApplicationRegistry =  new Vector<AppRegistryRow>();	
		mLastAppID = 0;
		
//		//TODO: remove the test application from registry
//		// Add a test application
//		addTestApplication();
	}
	
	/**
	 * Adds a new entry into the application registry, assigning it a new 
	 * unique identifier
	 * @param appName the application name
	 * @param credentials Base64-encoded
	 * @param notificationAddress is the HTTP address prefix to be used to provide back 
	 *                            the data to the application.
	 * @param type specifies the type of notifications to be issued (GCM/REST)
	 * @return the new registry row
	 */
	public AppRegistryRow addNewApplication(String appName, 
			                                String credentials,
                                            String notificationAddress,
                                            NotificationAddressType type) {
		
		String appId = getNewID();
		
		// Add a prefix to the notification address to be stored in the registry
		if (notificationAddress != null) {
			if (type == NotificationAddressType.GCM_NOTIFICATION_ADDRESS) {
				notificationAddress = NOTIFICATION_TYPE_GCM + notificationAddress;
			} else {
				notificationAddress = NOTIFICATION_TYPE_REST + notificationAddress;
			}
		}
		
		AppRegistryRow res = new AppRegistryRow(appId, appName, credentials, notificationAddress);
		
		// Update the registry in RAM
		mApplicationRegistry.add(res);
		
		return res;
	}
	
//	/**
//	 * Adds a test application to the application registry
//	 * @return the new registry row
//	 */
//	public AppRegistryRow addTestApplication() {
//		AppRegistryRow res = new AppRegistryRow("testAppID", 
//				                                "testAppName", 
//				                                "http://localhost:8080/IntrusionDetection/");
//		res.mServiceList = new Vector<AppService>();
//		AppService serv = new AppService();
//		serv.mName = "test service";
//		serv.mRequirements = new ServiceRequirement();
//		serv.mRequirements.mCredentials = "cred";
//		serv.mRequirements.mQoSRequirements = "qos";
//		serv.mRequirements.mSemanticDescription = "semantic";
//		serv.mServiceID = "serv id";
//		serv.mStatus = AppService.ServiceInstallationStatus.ALLOCATED;
//		res.mServiceList.add(serv);
//		mApplicationRegistry.add(res);
//		
//		return res;
//	}
	/**
	 * @param position 
	 * @return the registry row corresponding to the specified position in the vector
	 */
	public AppRegistryRow getAppAt(int position) {
		if(position >= mApplicationRegistry.size())
			return null;
		else
				return mApplicationRegistry.get(position);
	}
	
	/**
	 * @param appID
	 * @param mustBeInstalled to filter on the installation status 
	 * @return the registry row corresponding to the specified appID or null if 
	 *         it does not exist or it is not fully installed yet
	 */
	public AppRegistryRow getApp(String appID, boolean mustBeInstalled) {
		for (int i=0; i<mApplicationRegistry.size(); i++) {
			if (mApplicationRegistry.get(i).mAppID.equals(appID) && 
				((!mustBeInstalled) || (mApplicationRegistry.get(i).mStatus == AppRegistryRow.ApplicationInstallationStatus.INSTALLED))) {
				return mApplicationRegistry.get(i);
			}
		}
		
		return null;
	}
	
	/**
	 * Removes the specified application from the registry (RAM)
	 * @param appID
	 */
	public void remove(String appID) throws Exception {
		
		// Remove the app from the RAM registry
		for (int i=0; i<mApplicationRegistry.size(); i++) {
			if (mApplicationRegistry.get(i).mAppID.equals(appID) ) {
		
				mApplicationRegistry.remove(i);
			}
		}
	}
	
	/**
	 * Searches for an application that requested a service having the specified serviceID.
	 * The application must also be in the INSTALLING status and the service in the
	 * QOS_NEGOTIATION status.
	 * @param serviceID the identifier to be searched within the registry
	 * @return the registry row if found, null otherwise
	 */
	public AppRegistryRow searchInstallingServiceID(String serviceID) {
		int i, j;
		AppRegistryRow res = null;
		AppService serv = null;
		
		for (i=0; i<mApplicationRegistry.size(); i++) {
			res = mApplicationRegistry.get(i);
			if (res.mStatus != AppRegistryRow.ApplicationInstallationStatus.INSTALLING) {
				continue;
			}
				
			for (j=0; j<res.mServiceList.size(); j++) {
				serv = res.mServiceList.get(j);
				if ((serv.mStatus == AppService.ServiceInstallationStatus.QOS_NEGOTIATION) && 
					(serv.mServiceID.equals(serviceID))) {
					return res;
				}				
			}
		}
		
		return null;
	}
	
	
	/**
	 * Searches for an application that requested an INSTALLED service having the specified serviceID.
	 * @param serviceID the identifier to be searched within the registry
	 * @return the registry row if found, null otherwise
	 */
	public AppRegistryRow searchInstalledServiceID(String serviceID) {
		int i, j;
		AppRegistryRow res = null;
		AppService serv = null;
		
		for (i=0; i<mApplicationRegistry.size(); i++) {
			res = mApplicationRegistry.get(i);
			if (res.mStatus != AppRegistryRow.ApplicationInstallationStatus.INSTALLED) {
				continue;
			}
				
			for (j=0; j<res.mServiceList.size(); j++) {
				serv = res.mServiceList.get(j);
				if (serv.mServiceID.equals(serviceID)) {
					return res;
				}				
			}
		}
		
		return null;
	}
	
	/**
	 * Stores the registry content into the DB. It first removes from DB the old registry content
	 * @throws Exception
	 */
	public void store() throws Exception {
		int i, j;
		AppRegistryRow app = null;
		AppService serv = null;
		PreparedStatement psApp = null;
		PreparedStatement psServ = null;
		
		mLogger.info("Updating the application registry to DB");
		
		IBigDataDatabaseService dbService = ServiceManager.getInstance().getDatabaseServiceIF();
		if (dbService == null) {
			throw new Exception("Cannot access DB to store the application registry");
		}
		
		Connection conn = dbService.getConnection();
		if (conn == null) {
			throw new Exception("Cannot get a valid DB connection to store the application registry");
		}
		
		try {
			// Disable auto commit to be able to undo the operation in case of failure
			conn.setAutoCommit(false);
			
			// Remove the DB content
			
			psServ = conn.prepareStatement("delete from T_APP_SERVICE");
			if (psServ == null) {
				throw new Exception("Error preparing the statement to remove services");
			}
			mLogger.info("Removed " + psServ.executeUpdate() + " services from DB");
			psServ.close();
			psServ = null;
			
			psApp = conn.prepareStatement("delete from T_APPLICATION_REGISTRY");
			if (psApp == null) {
				throw new Exception("Error preparing the statement to remove the application");
			}
			mLogger.info("Removed " + psApp.executeUpdate() + " applications from DB");
			psApp.close();
			psApp = null;
			
			// Store the new registry content
			
			psApp = conn.prepareStatement("INSERT INTO T_APPLICATION_REGISTRY (ID, NAME, ADDRESS, STATUS) VALUES (?, ?, ?, ?)");
			if (psApp == null) {
				throw new Exception("Cannot prepare the statement to insert a new application");
			}
			psServ = conn.prepareStatement("insert into T_APP_SERVICE (ID, APP_ID, NAME, STATUS) values (?, ?, ?, ?) ");
			if (psServ == null) {
				throw new Exception("Cannot prepare the statement to insert a new service");
			}
						
			for (i=0; i < mApplicationRegistry.size(); i++) {
				app = mApplicationRegistry.get(i);
				if (app == null) {
					continue;
				}
				psApp.setString(1, app.mAppID);
				psApp.setString(2, app.mAppName);
				psApp.setString(3, app.mNotificationAddress);
				psApp.setInt(4, ApplicationInstallationStatus.getCode(app.mStatus));
				if (psApp.executeUpdate() != 1) {
					throw new Exception("Cannot insert the application in the DB registry");
				}
				
				if (app.mServiceList == null) {
					continue;
				}
				for (j=0; j < app.mServiceList.size(); j++) {
					serv = app.mServiceList.get(j);
					if (serv == null) {
						continue;
					}
					psServ.setString(1, serv.mServiceID);
					psServ.setString(2, app.mAppID);
					psServ.setString(3, serv.mName);
					psServ.setInt(4, ServiceInstallationStatus.getCode(serv.mStatus));

					if (psServ.executeUpdate() != 1) {
						throw new Exception("Cannot insert the service in the DB registry");
					}
				}
			}
			
			// commit changes
			conn.commit();
			
			mLogger.info("Stored " + mApplicationRegistry.size() + " applications into the registry");			
			
		} catch (Exception e) {
			
			// If any error occurs, undo the registry updates
			mLogger.info("Exception occurred: rolling back to undo DB updates");
			conn.rollback();
			throw e;
			
		} finally {
			// first of all restore autocommit
			if (conn != null) {
				conn.setAutoCommit(true);
			}
			if (psApp != null) {
				psApp.close();
			}
			if (psServ != null) {
				psServ.close();
			}
			if (conn != null) {
				conn.close();
			}
		}
	}
	
	/**
	 * Load the registry from the DB (after clearing it)
	 * @throws Exception on errors
	 */
	public void loadFromDB() throws Exception {
		int maxAppId, intId;
		String appId;
		ApplicationInstallationStatus status;
		AppService service;
		PreparedStatement psApp = null;
		PreparedStatement psServ = null;
		
		mApplicationRegistry.clear();
		
		IBigDataDatabaseService dbService = ServiceManager.getInstance().getDatabaseServiceIF();
		if (dbService == null) {
			throw new Exception("Cannot get Database Service to load the application registry");
		}
		
		Connection conn = dbService.getConnection();
		if (conn == null) {
			throw new Exception("Cannot get a valid connection to the DB while loading application registry");
		}
		
		try {
			psApp = conn.prepareStatement("select ID, NAME, ADDRESS, STATUS from T_APPLICATION_REGISTRY");
			if (psApp == null) {
				throw new Exception("Cannot build the application prepared statement while loading the application registry");
			}
			
			//TODO: retrieve also the other service fields
			psServ = conn.prepareStatement("select ID, NAME, STATUS from T_APP_SERVICE where APP_ID=?");
			if (psServ == null) {
				throw new Exception("Cannot build the service prepared statement while loading the application registry");
			}
			
			// Retrieve the list of registered applications
			ResultSet rsApp = psApp.executeQuery();
			
			// get the maximum numeric identifier to be able to generate new ID's
			maxAppId = 0;
			while (rsApp.next()) {
				
				appId = rsApp.getString(1);
				if (appId == null) {
					throw new Exception("Found null application ID in the registry. Skipping registry load");
				}
				
				try {
					intId = Integer.parseInt(appId);
					if (intId > maxAppId) {
						maxAppId = intId;
					}
				} catch (NumberFormatException nfe) {
					// non-numeric ID. Ignore it to search for the max
				}

				//TODO: load/store credentials in the DB (not necessary as they are used only during installation)
				
				status = ApplicationInstallationStatus.getEnum(rsApp.getInt(4));
				AppRegistryRow app = new AppRegistryRow(appId, rsApp.getString(2), "", rsApp.getString(3), status);
				
				// Retrieve the list of services associated to the registered application
				psServ.setString(1, appId);
				ResultSet rsServ = psServ.executeQuery();
				
				while (rsServ.next()) {
					service = new AppService();
					service.mServiceID = rsServ.getString(1);
					if (service.mServiceID == null) {
						throw new Exception("Found null service ID in the registry. Skipping registry load");
					}
					service.mName = rsServ.getString(2);
					service.mStatus = AppService.ServiceInstallationStatus.getEnum(rsServ.getInt(3));
					service.mRequirements = null;
					
					app.mServiceList.add(service);
				}
				
				mApplicationRegistry.add(app);
			}
			
			mLastAppID = maxAppId;
			
		} finally {
			if (psApp != null) {
				psApp.close();
			}
			if (psServ != null) {
				psServ.close();
			}
			if (conn != null) {
				conn.close();
			}
		}

	}
	
	
	/**
	 * @return the number of registered applications
	 */
	public int getSize() {
		if (mApplicationRegistry == null) return 0;
		
		return mApplicationRegistry.size();
	}
	

	/**
	 * Creates a new unique application ID
	 * @return the new identifier
	 */
	private String getNewID() {
		mLastAppID += 1;
		
		return ServiceManager.getInstance().getGWId() + "::" + mLastAppID;
	}
	
	/** This is the Application Registry */
	private Vector<AppRegistryRow> mApplicationRegistry; 

	/** It is the last generated application unique identifier */
	private static int mLastAppID;
	
	/** Logger */
	private static Logger mLogger = Logger.getLogger(ServiceManager.LOGGER_NAME);

}
