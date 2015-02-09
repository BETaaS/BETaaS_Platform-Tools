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

package eu.betaas.service.extendedservice.api.impl;

import java.util.Vector;

import org.apache.log4j.Logger;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Manages incoming data (users' position and traffic density) to compute 
 * fees depending on it.
 */
public class LEZProcessor {
	
	private final static String JSON_FIELD_SERVICE_RESULT = "ServiceResult";
	private final static String JSON_FIELD_DATALIST = "dataList";
	private final static String JSON_FIELD_USER_ID = "locationIdentifier";
	private final static String JSON_FIELD_LAT = "lat";
	private final static String JSON_FIELD_LON = "lon";
	private final static String JSON_FIELD_MEASUREMENT = "measurement";
	
	// thresholds on cars/min
	private final static int TRAFFIC_THRESHOLD_1 = 5;
	private final static int TRAFFIC_THRESHOLD_2 = 10;
	private final static int TRAFFIC_THRESHOLD_3 = 15;
	private final static int TRAFFIC_THRESHOLD_4 = 20;
	
	// fee class to be applied when traffic data is not reliable
	public final static int DEFAULT_FEE_CLASS = -1;
	
	// fees in euro/h
	private final static float FEES[] = {0.0f, 2.0f, 4.0f, 8.0f, 15.0f};
	private final static float DEFAULT_FEE = 1.0f;
	
	public LEZProcessor(Map map) {
		mUsers = new Vector<User>();
		mMap = map;
		mIsTrafficDataReliable = true;
	}
	
	public static float getFee(int feeClass) {
		if (feeClass == DEFAULT_FEE_CLASS) return DEFAULT_FEE;
		if (feeClass < 0) return 0.0f;
		if (feeClass >= FEES.length) return FEES[FEES.length-1];
		return FEES[feeClass];
	}

	public synchronized void managePositionData(JsonObject data) {
		JsonObject jsonObj;
		JsonElement jsonElement;
		String lat, lon, userId;
		double latVal, lonVal;
		
		mLogger.info("Received position data");
		
		try {
			JsonObject serviceResult = data.getAsJsonObject(JSON_FIELD_SERVICE_RESULT);
			if (serviceResult == null) {
				mLogger.error("Cannot get ServiceResult");
				return;
			}
			
			JsonArray dataList = serviceResult.getAsJsonArray(JSON_FIELD_DATALIST);
			if (dataList == null) {
				mLogger.error("Cannot get dataList");
				return;
			}
			
			// scan all received points
		    for (int i=0; i < dataList.size(); i++) {
		    	jsonObj = dataList.get(i).getAsJsonObject();
		    	if (jsonObj == null) {
		    		mLogger.error("Cannot get data element n. " + i);
		    		return;
		    	}
		    	jsonElement = jsonObj.get(JSON_FIELD_USER_ID);
		    	if (jsonElement == null) {
		    		mLogger.error("Cannot get user ID from element n. " + i);
		    		return;
		    	}
		    	userId = jsonElement.toString();
		    	if (userId == null) {
		    		mLogger.error("Got null user ID from element n. " + i);
		    		return;
		    	}
		    	if (userId.startsWith("\"")) userId = userId.substring(1);
				if (userId.endsWith("\"")) userId = userId.substring(0, userId.length()-1);
				if (userId.isEmpty()) {
					mLogger.error("Got empty user ID from element n. " + i);
					return;
				}
		    	
		    	try {
			    	jsonElement = jsonObj.get(JSON_FIELD_LAT);
			    	if (jsonElement == null) {
			    		mLogger.error("Cannot get lat from element n. " + i);
			    		return;
			    	}
			    	lat = jsonElement.toString();
		    		latVal = Double.parseDouble(lat);
		    	
			    	jsonElement = jsonObj.get(JSON_FIELD_LON);
			    	if (jsonElement == null) {
			    		mLogger.error("Cannot get lon from element n. " + i);
			    		return;
			    	}
			    	lon = jsonElement.toString();
			    	lonVal = Double.parseDouble(lon);
			    	
		    	} catch (Exception efe) {
		    		throw new Exception("Invalid numberic conversion on element n." + i + ": " + efe.getMessage());
		    	}
			    
		    	processUserPosition(userId, latVal, lonVal);
		    }
		    
		} catch (Exception e) {
			mLogger.error("Cannot manage received position data: " + e.getMessage());
			e.printStackTrace();
		}

	}
	
	public synchronized void manageTrafficData(JsonObject data) {
		JsonObject jsonObj;
		JsonElement jsonElement;
		String carsMinute, lat, lon;
		double latVal, lonVal;
		float carsMinuteVal;
		
		mLogger.info("Received traffic data");
		
		try {
			JsonObject serviceResult = data.getAsJsonObject(JSON_FIELD_SERVICE_RESULT);
			if (serviceResult == null) {
				mLogger.error("Cannot get ServiceResult");
				return;
			}
			
			JsonArray dataList = serviceResult.getAsJsonArray(JSON_FIELD_DATALIST);
			if (dataList == null) {
				mLogger.error("Cannot get dataList");
				return;
			}
			
			// scan all received elements
		    for (int i=0; i < dataList.size(); i++) {
		    	jsonObj = dataList.get(i).getAsJsonObject();
		    	if (jsonObj == null) {
		    		mLogger.error("Cannot get data element n. " + i);
		    		return;
		    	}
		    	
		    	try {
			    	jsonElement = jsonObj.get(JSON_FIELD_MEASUREMENT);
			    	if (jsonElement == null) {
			    		mLogger.error("Cannot get measurement from element n. " + i);
			    		return;
			    	}
			    	carsMinute = jsonElement.toString();
			    	if (carsMinute.startsWith("\"")) carsMinute = carsMinute.substring(1);
			    	if (carsMinute.endsWith("\"")) carsMinute = carsMinute.substring(0, carsMinute.length()-1);
			    	carsMinuteVal = Float.parseFloat(carsMinute);
			    	
			    	jsonElement = jsonObj.get(JSON_FIELD_LAT);
			    	if (jsonElement == null) {
			    		mLogger.error("Cannot get lat from element n. " + i);
			    		return;
			    	}
			    	lat = jsonElement.toString();
		    		latVal = Double.parseDouble(lat);
		    	
			    	jsonElement = jsonObj.get(JSON_FIELD_LON);
			    	if (jsonElement == null) {
			    		mLogger.error("Cannot get lon from element n. " + i);
			    		return;
			    	}
			    	lon = jsonElement.toString();
			    	lonVal = Double.parseDouble(lon);

		    	} catch (Exception efe) {
		    		throw new Exception("Invalid numeric conversion on element n." + i + ": " + efe.getMessage());
		    	}
			    
		    	processTrafficIntensity(latVal, lonVal, carsMinuteVal);
		    }
		    
		} catch (Exception e) {
			mLogger.error("Cannot manage received traffic data: " + e.getMessage());
		}
	}
	
	
	/**
	 * Format the user history data and returns it
	 * @param userId
	 * @return
	 */
	public synchronized String getUserHistory(String userId) {
		//mLogger.info("mUsers (history)=" + (Object)mUsers);
		User user = getUser(userId);
		if (user == null) {
			return "No info for user: " + userId;
		}
		
		return user.getHistory();
	}
	
	
	public synchronized String getUserInfo(String userId) {
		User user = getUser(userId);
		if (user == null) {
			return "No info for user: " + userId;
		}
		
		return user.getInfo();
	}
	
	
	public synchronized boolean isTrafficDataReliable() {
		return mIsTrafficDataReliable;
	}

	public synchronized void setTrafficDataReliable(boolean reliable) {
		mIsTrafficDataReliable = reliable;
	}
	
	/**
	 * Updates user's info according to the current traffic 
	 * @param userId
	 * @param lat
	 * @param lon
	 * @throws Exception
	 */
	private synchronized void processUserPosition(String userId, double lat, double lon) throws Exception {
		
		mLogger.info("processUserPosition searching for: " + userId);
		mLogger.info("mUsers=" + (Object)mUsers);
		User user = getUser(userId);
		if (user == null) {
			user = new User(userId);
			mUsers.add(user);
			mLogger.info("Added user: " + userId);
		} else {
		  mLogger.info("Retrieved user: " + userId);
		}
		
		float traffic = mMap.getTrafficIntensity(lat, lon);
		mLogger.info("Retrieved traffic intensity for user position: " + traffic);
		if (traffic == -1.0f) {
			// no road found near the user. From now on the user won't pay
			user.setNoFee();
		} else {
//			if (isTrafficDataReliable()) {
//        mLogger.info("Traffic data is reliable");
				user.setCurrentFee(getFeeClass(traffic));
//			} else {
//        mLogger.info("Traffic data is not reliable");
//				user.setCurrentFee(getDefaultFeeClass());
//			}
		}
		mLogger.info("User history updated. History length: " + user.getHistoryLength());
		if (user.getHistoryLength() % 5 == 0) {
			System.out.println(user.getHistory());
		}
	}
	
	/**
	 * Updates the map with the received traffic intensity value for the road segments
	 * that are very close to the specified location
	 * @param lat
	 * @param lon
	 * @param carsMinute the measurement
	 */
	private synchronized void processTrafficIntensity(double lat, double lon, float carsMinute) throws Exception {
		// update the map
		mMap.updateIntensity(lat, lon, carsMinute);
		
		// Note: it is assumed that users' position arrival frequency is high enough to
		//       avoid updating here their current fee class
	}
	
	
	private synchronized User getUser(String userId) {
		User user = null;
		
//		mLogger.info("listing users searching for " + userId + "********************************");
		for (User u : mUsers) {
//			mLogger.info("u.getId(): " + u.getId());
			if (u.getId().equals(userId)) {
//				mLogger.info("FOUND FOUND FOUND FOUND FOUND FOUND FOUND FOUND");
//				mLogger.info("length=" + u.getHistoryLength());
//				mLogger.info("history="+u.getHistory());
				user = u;
				return user;
			}
		}
		
		return user;
	}
	
	
	/**
	 * Returns the default fee class to be applied when traffic info is not reliable
	 * @return
	 */
	private int getDefaultFeeClass() {
		return DEFAULT_FEE_CLASS;
	}
	
	/**
	 * Returns the fee class based on the traffic
	 * @param traffic
	 * @return
	 */
	private int getFeeClass(float traffic) {
		int fee;
		
		if (traffic < TRAFFIC_THRESHOLD_1) fee = 0;
		else if (traffic < TRAFFIC_THRESHOLD_2) fee = 1;
		else if (traffic < TRAFFIC_THRESHOLD_3) fee = 2;
		else if (traffic < TRAFFIC_THRESHOLD_4) fee = 3;
		else fee = 4;
		
		return fee;
	}

	/** Logger */
	private static Logger mLogger = Logger.getLogger(LEZExtendedServiceImpl.LOGGER_NAME);
	
	private Map mMap;
	
	/** Vector of managed users */
	private Vector<User> mUsers;
	
	private boolean mIsTrafficDataReliable;
}
