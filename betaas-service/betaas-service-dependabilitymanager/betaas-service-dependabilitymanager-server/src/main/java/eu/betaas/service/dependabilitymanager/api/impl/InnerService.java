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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.log4j.Logger;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import eu.betaas.service.dependabilitymanager.api.IInner;
import eu.betaas.service.dependabilitymanager.DependabilityManager;
import eu.betaas.taas.bigdatamanager.database.service.IBigDataDatabaseService;

/**
 * This class implements the interfaces of DM visible from the same GW components
 * @author Intecs
 */
public class InnerService implements IInner {
		
	/**
	 * Startup the service exposing internal IF to the gateway
	 */
	public void startService() {
		mLogger.info("Starting Dependability Manager local IF bundle");
		
		mConnection = null;
	}
	
	public void closeService() {
		mLogger.info("Stopping Dependability Manager local IF bundle");
		
		try {
			if ((mConnection != null) && (!mConnection.isClosed())) {
				mConnection.close();
			}
		} catch (SQLException e) {
			mLogger.warn("Cannot close DB connection: " + e.getMessage());
		}
	}
	
	/**
	 * Setter for the OSGi context
	 * @param context
	 */
	public void setContext(BundleContext context) {
		mContext = context;	
	}
	

//	public void notifyFailure(FAILURE_LAYER layer, 
//			                  FAILURE_CODE code,
//			                  FAILURE_LEVEL level, 
//			                  String originator, 
//			                  String description) {
//
//		mLogger.info("Got failure (LEVEL=" + FAILURE_LEVEL.getID(level) + ") from <" + originator + ">: " + description);
//		
//		GregorianCalendar gc = new GregorianCalendar();
//		gc.setTime(new Date());
//		String when = format(gc);
//		
//		Connection conn = getConnection();
//		if (conn == null) {
//			mLogger.error("Cannot get DB connection to store failure info");
//			return;
//		}
//		
//		PreparedStatement psInsert = null;
//		
//		try {
//			psInsert = conn.prepareStatement("INSERT INTO T_NOTIFIED_FAILURES " + 
//			                                 "(NOTIFICATION_TIME, LAYER, CODE, LEVEL, ORIGINATOR, DESCRIPTION) " +
//					                         "VALUES (?, ?, ?, ?, ?, ?)");
//			if (psInsert == null) {
//				mLogger.error("Cannot prepare the statement to insert failure info into the DB");
//				return;
//			}
//
//			psInsert.setString(1, when);
//			psInsert.setInt(2, FAILURE_LAYER.getID(layer));
//			psInsert.setInt(3, FAILURE_CODE.getID(code));
//			psInsert.setInt(4, FAILURE_LEVEL.getID(level));
//			psInsert.setString(5, originator);
//			psInsert.setString(6, description);
//			if (psInsert.executeUpdate() != 1) {
//				mLogger.error("Cannot insert failure info into the DB");
//			}
//
//			mLogger.info("Failure info stored");			
//			
//		} catch (Exception e) {
//			
//			// If any error occurs, check if it is necessary to reset the connection
//			mLogger.error("Exception occurred: " + e.getMessage());
//			try {
//				if (!conn.isValid(3)) {
//					mLogger.warn("Connection not valid: resetting");
//					if (!mConnection.isClosed()) {
//						mConnection.close();
//					}
//					mConnection = null;
//				}
//			} catch (SQLException e2) {
//				mLogger.error("SQL Exception occurred: " + e2.getMessage());
//			}
//			
//		} finally {
//			if (psInsert != null) {
//				try {
//					psInsert.close();
//				} catch (SQLException e) {}
//			}
//		}	
//	
//	}

	
	private Connection getConnection() {		
		if (mConnection == null) {
			
			IBigDataDatabaseService dbService = DependabilityManager.getInstance().getDatabaseServiceIF();
			if (dbService == null) {
				mLogger.error("Cannot get the BD service");
			}
			
			try {
				mConnection = dbService.getConnection();
				if (mConnection != null) mConnection.setAutoCommit(true);
			} catch (SQLException e) {
				mConnection = null;
			}
		}
		
		return mConnection;
	}

	
	private String format(GregorianCalendar cal) {
		return cal.get(Calendar.YEAR) + "-" +
				String.format("%02d",(cal.get(Calendar.MONTH)+1)) + "-" +
				String.format("%02d",cal.get(Calendar.DAY_OF_MONTH)) + " " +
				String.format("%02d",cal.get(Calendar.HOUR_OF_DAY)) + ":" +
				String.format("%02d",cal.get(Calendar.MINUTE)) + ":" + 
				String.format("%02d",cal.get(Calendar.SECOND));
	}
	
	/** Logger */
	private static Logger mLogger = Logger.getLogger(DependabilityManager.LOGGER_NAME);
	
	/** OSGi context */
	private BundleContext mContext;
	
	/** The connection to the DB */
	private Connection mConnection;
}
