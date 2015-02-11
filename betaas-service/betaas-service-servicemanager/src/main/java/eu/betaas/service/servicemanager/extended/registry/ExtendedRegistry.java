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

package eu.betaas.service.servicemanager.extended.registry;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Vector;

import org.apache.log4j.Logger;

import eu.betaas.service.servicemanager.ServiceManager;
import eu.betaas.taas.bigdatamanager.database.service.IBigDataDatabaseService;
import eu.betaas.service.servicemanager.extended.registry.ExtServService.ServiceInstallationStatus;
import eu.betaas.service.servicemanager.extended.registry.ExtServiceRegistryRow.ExtServiceInstallationStatus;

/**
 * It manages the Extended service registry. Be carefull of the kind of services are
 * referred each time: when an extended service is installed, as for applications, it
 * requests the allocation of ("on-the-fly") services to the Taas layer.  
 * @author Intecs
 */
public class ExtendedRegistry {
	
    /** Prefix automatically given to services in the registry */
    public final static String EXT_SERVICE_NAME_PREFIX = "extService";
    
    /** Prefix automatically given to service IDs in the registry */
    public final static String EXTENDED_SERVICE_ID_PREFIX = "es_";
	
	/**
	 * Class Constructor
	 */
	public ExtendedRegistry(){
		mExtendedRegistry =  new Vector<ExtServiceRegistryRow>();	
		mLastExtID = 0;
	}
	
	/**
	 * Adds a new entry into the extended service registry, assigning it a new 
	 * unique identifier
	 * @param extServiceUniqueName the extended service name. It must be unique in the instance
	 * @param credentials Base64-encoded
	 * @return the new registry row
	 */
	public ExtServiceRegistryRow addNewExtendedService(String extServiceUniqueName, String credentials) {
		
		String extServiceId = EXTENDED_SERVICE_ID_PREFIX + ServiceManager.getInstance().getGWId() + "_" + Integer.toString(getNewID());
		ExtServiceRegistryRow res = new ExtServiceRegistryRow(extServiceId, extServiceUniqueName, credentials);
		
		// Update the registry in RAM
		mExtendedRegistry.add(res);
		
		return res;
	}
	
//	/**
//	 * Adds a test extended service to the extRegistry
//	 * @return the new registry row
//	 */
//	public ExtServiceRegistryRow addTestApplication() {
//		ExtServiceRegistryRow res = new ExtServiceRegistryRow("testAppID", "testExtendedService");
//		res.mServiceList = new Vector<ExtServService>();
//		ExtServService serv = new ExtServService();
//		serv.mName = "test service";
//		serv.mRequirements = new ServiceRequirement();
//		serv.mRequirements.mCredentials = "cred";
//		serv.mRequirements.mQoSRequirements = "qos";
//		serv.mRequirements.mSemanticDescription = "semantic";
//		serv.mServiceID = "serv id";
//		serv.mStatus = ExtServService.ServiceInstallationStatus.ALLOCATED;
//		res.mServiceList.add(serv);
//		mExtendedRegistry.add(res);
//		
//		return res;
//	}
	
	/**
	 * @param extServiceID
	 * @return the registry row corresponding to the specified extended service ID or null 
	 *         if it does not exist or it is not fully installed yet
	 */
	public ExtServiceRegistryRow getExtService(String extServiceID) {
		for (int i=0; i<mExtendedRegistry.size(); i++) {
			if (mExtendedRegistry.get(i).mExtServiceID.equals(extServiceID) &&
				mExtendedRegistry.get(i).mStatus == ExtServiceRegistryRow.ExtServiceInstallationStatus.INSTALLED) {
				return mExtendedRegistry.get(i);
			}
		}
		
		return null;
	}
	
	/**
	 * Removes the specified application from the registry (RAM)
	 * @param extServiceID
	 */
	public void remove(String extServiceID) throws Exception {
		
		// Remove the app from the RAM registry
		for (int i=0; i<mExtendedRegistry.size(); i++) {
			if (mExtendedRegistry.get(i).mExtServiceID.equals(extServiceID)) {
				mExtendedRegistry.remove(i);
			}
		}
	}
	
	/**
	 * Searches for an extended service that requested a service having the specified serviceID.
	 * The application must also be in the INSTALLING status and the service in the
	 * QOS_NEGOTIATION status.
	 * @param serviceID the identifier to be searched within the registry
	 * @return the registry row if found, null otherwise
	 */
	public ExtServiceRegistryRow searchInstallingServiceID(String serviceID) {
		int i, j;
		ExtServiceRegistryRow res = null;
		ExtServService serv = null;

		if (mExtendedRegistry == null) return null;
		
		for (i=0; i<mExtendedRegistry.size(); i++) {
			res = mExtendedRegistry.get(i);
			if (res.mStatus != ExtServiceRegistryRow.ExtServiceInstallationStatus.INSTALLING) {
				continue;
			}
				
			for (j=0; j<res.mServiceList.size(); j++) {
				serv = res.mServiceList.get(j);
				if ((serv.mStatus == ExtServService.ServiceInstallationStatus.QOS_NEGOTIATION) && 
					(serv.mServiceID.equals(serviceID))) {
					return res;
				}				
			}
		}
		
		return null;
	}
	
	
	
	/**
	 * Searches for an extended service that requested an INSTALLED service having the specified serviceID.
	 * @param serviceID the identifier to be searched within the registry
	 * @return the registry row if found, null otherwise
	 */
	public ExtServiceRegistryRow searchInstalledServiceID(String serviceID) {
		int i, j;
		ExtServiceRegistryRow res = null;
		ExtServService serv = null;
		
		if (mExtendedRegistry == null) return null;
		
		for (i=0; i<mExtendedRegistry.size(); i++) {
			res = mExtendedRegistry.get(i);
			if (res.mStatus != ExtServiceRegistryRow.ExtServiceInstallationStatus.INSTALLED) {
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
		ExtServiceRegistryRow extServ = null;
		ExtServService serv = null;
		PreparedStatement psExtServ = null;
		PreparedStatement psServ = null;
		
		mLogger.info("Updating the extended service registry to DB");
		
		IBigDataDatabaseService dbService = ServiceManager.getInstance().getDatabaseServiceIF();
		if (dbService == null) {
			throw new Exception("Cannot access DB to store the extended service registry");
		}
		
		Connection conn = dbService.getConnection();
		if (conn == null) {
			throw new Exception("Cannot get a valid DB connection to store the extended service registry");
		}
		
		try {
			// Disable auto commit to be able to undo the operation in case of failure
			conn.setAutoCommit(false);
			
			// Remove the DB content
			psServ = conn.prepareStatement("delete from T_EXT_SERV_SERVICE");
			if (psServ == null) {
				throw new Exception("Error preparing the statement to remove services");
			}
			mLogger.info("Removed " + psServ.executeUpdate() + " services from DB");
			psServ.close();
			psServ = null;
			
			psExtServ = conn.prepareStatement("delete from T_EXT_SERV_REGISTRY");
			if (psExtServ == null) {
				throw new Exception("Error preparing the statement to remove the extended services");
			}
			mLogger.info("Removed " + psExtServ.executeUpdate() + " extended services from DB");
			psExtServ.close();
			psExtServ = null;
			
			// Store the new registry content
			
			psExtServ = conn.prepareStatement("INSERT INTO T_EXT_SERV_REGISTRY (ID, NAME, STATUS) VALUES (?, ?, ?)");
			if (psExtServ == null) {
				throw new Exception("Cannot prepare the statement to insert a new extended service");
			}
			psServ = conn.prepareStatement("insert into T_EXT_SERV_SERVICE (ID, EXT_SERV_ID, NAME, STATUS) values (?, ?, ?, ?) ");
			if (psServ == null) {
				throw new Exception("Cannot prepare the statement to insert a new service");
			}
						
			for (i=0; i < mExtendedRegistry.size(); i++) {
				extServ = mExtendedRegistry.get(i);
				if (extServ == null) {
					continue;
				}
				psExtServ.setString(1, extServ.mExtServiceID);
				psExtServ.setString(2, extServ.mExtServiceUniqueName);
				psExtServ.setInt(3, ExtServiceInstallationStatus.getCode(extServ.mStatus));
				if (psExtServ.executeUpdate() != 1) {
					throw new Exception("Cannot insert the extended service in the DB registry");
				}
				
				if (extServ.mServiceList == null) {
					continue;
				}
				for (j=0; j < extServ.mServiceList.size(); j++) {
					serv = extServ.mServiceList.get(j);
					if (serv == null) {
						continue;
					}
					psServ.setString(1, serv.mServiceID);
					psServ.setString(2, extServ.mExtServiceID);
					psServ.setString(3, serv.mName);
					psServ.setInt(4, ServiceInstallationStatus.getCode(serv.mStatus));
					
					if (psServ.executeUpdate() != 1) {
						throw new Exception("Cannot insert the service in the DB registry");
					}
				}
			}
			
			conn.commit();
			
			mLogger.info("Stored " + mExtendedRegistry.size() + " extended services into the registry");			
			
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
			if (psExtServ != null) {
				psExtServ.close();
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
		int maxExtServId, intId;
		String extServId;
		ExtServiceInstallationStatus status;
		ExtServService service;
		PreparedStatement psExtServ = null;
		PreparedStatement psServ = null;
		
		mExtendedRegistry.clear();
		
		IBigDataDatabaseService dbService = ServiceManager.getInstance().getDatabaseServiceIF();
		if (dbService == null) {
			throw new Exception("Cannot get Database Service to load the extended services registry");
		}
		
		Connection conn = dbService.getConnection();
		if (conn == null) {
			throw new Exception("Cannot get a valid connection to the DB while loading extended services registry");
		}
		
		try {
			psExtServ = conn.prepareStatement("select ID, NAME, STATUS from T_EXT_SERV_REGISTRY");
			if (psExtServ == null) {
				throw new Exception("Cannot build the ext service prepared statement while loading the extended services registry");
			}
			
			//TODO: retrieve also the other service fields like requirements
			psServ = conn.prepareStatement("select ID, NAME, STATUS from T_EXT_SERV_SERVICE where EXT_SERV_ID=?");
			if (psServ == null) {
				throw new Exception("Cannot build the service prepared statement while loading the extended services registry");
			}
			
			// Retrieve the list of registered extended services
			ResultSet rsExtServ = psExtServ.executeQuery();
			
			// get the maximum numeric identifier to be able to generate new ID's
			maxExtServId = 0;
			while (rsExtServ.next()) {
				
				extServId = rsExtServ.getString(1);
				if (extServId == null) {
					throw new Exception("Found null extended service ID in the registry. Skipping registry load");
				}
				
				try {
					String numId = extServId;
					if (extServId.startsWith(EXTENDED_SERVICE_ID_PREFIX)) {
						numId = extServId.substring(EXTENDED_SERVICE_ID_PREFIX.length());
					}
					intId = Integer.parseInt(numId);
					if (intId > maxExtServId) {
						maxExtServId = intId;
					}
				} catch (NumberFormatException nfe) {
					// non-numeric ID. Ignore it to search for the max
				}
				
				//TODO: load/store credentials in the DB (not necessary as they are used only during installation)				
				status = ExtServiceInstallationStatus.getEnum(rsExtServ.getInt(4));
				ExtServiceRegistryRow extService = new ExtServiceRegistryRow(extServId, rsExtServ.getString(2), status, "");
				
				// Retrieve the list of services associated to the registered extended service
				psServ.setString(1, extServId);
				ResultSet rsServ = psServ.executeQuery();
				
				while (rsServ.next()) {
					service = new ExtServService();
					service.mServiceID = rsServ.getString(1);
					if (service.mServiceID == null) {
						throw new Exception("Found null service ID in the registry. Skipping registry load");
					}
					service.mName = rsServ.getString(2);
					service.mStatus = ExtServService.ServiceInstallationStatus.getEnum(rsServ.getInt(3));
					service.mRequirements = null;
					
					extService.mServiceList.add(service);
				}
				
				mExtendedRegistry.add(extService);
			}
			
			mLastExtID = maxExtServId;
			
		} finally {
			if (psExtServ != null) {
				psExtServ.close();
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
	 * @return the number of registered extended services
	 */
	public int getSize() {
		if (mExtendedRegistry == null) return 0;
		
		return mExtendedRegistry.size();
	}
	

	/**
	 * Creates a new unique extended service ID
	 * @return the new identifier
	 */
	private int getNewID() {
		mLastExtID += 1;
		
		return mLastExtID;
	}
	
	/** This is the Extended Service Registry */
	private Vector<ExtServiceRegistryRow> mExtendedRegistry; 

	/** It is the last generated extended service unique identifier */
	private static int mLastExtID;
	
	/** Logger */
	private static Logger mLogger = Logger.getLogger(ServiceManager.LOGGER_NAME);

}
