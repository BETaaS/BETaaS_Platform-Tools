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

package eu.betaas.service.servicemanager.application;

import java.util.ArrayList;

import org.apache.cxf.common.util.Base64Utility;
import org.apache.log4j.Logger;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import eu.betaas.service.servicemanager.ServiceManager;
import eu.betaas.taas.taasresourcesmanager.api.TaaSResourceManager;

/**
 * This class defines the operations for general interactions with applications
 * @author Intecs
 */
public class RequestManager {
	
	/**
	 * @param serviceID
	 * @param combineValues if true SM tries to combine (if possible) the values with the operator
	 * @param token the Base64 encoded token to access the service 
	 * @return the data provided by the thing service having the specified ID
	 */
	public String getThingServiceData(String serviceID, boolean combineValues, String token) {
		
		TaaSResourceManager taasRM = ServiceManager.getInstance().getResourceManagerIF();
		if (taasRM == null) {
			mLogger.error("TaaSRM not available to get data");
			return "";
		}
		
		byte[] decodedToken = null;
		try {
			if (token != null) decodedToken = Base64Utility.decode(token);
		} catch (Exception e) {
			mLogger.error("Cannot decode token: " + e.getMessage());
		}

		JsonObject data = taasRM.getData(serviceID, decodedToken);
				
		if (data == null) {
			mLogger.error("TaaSRM returned a null data");
			return "";
		}
		JsonObject serviceResult = data.getAsJsonObject("ServiceResult");
		if (serviceResult == null) {
			mLogger.error("ServiceResult not found in TaaSRM result");
			return "";
		}
		
		if (!combineValues) {
			return data.toString();
		}
		
		JsonElement operator = serviceResult.get("operator");
		JsonArray dataList = serviceResult.getAsJsonArray("dataList");
		try {	
			String combinedResult = combine(dataList, operator);
			return combinedResult;
		} catch (Exception e) {
			mLogger.warn("Cannot combine received values: " + e.getMessage());
			mLogger.warn("Returning uncombined data");
			return data.toString();
		}
	}
	
	
	/**
	 * @param serviceID the service to be used to control actuators
	 * @param data the data to be set
	 * @param token the Base64 encoded token to access the service 
	 * @return true on success, false otherwise
	 */
	public boolean setThingServiceData(String serviceID, String data, String token) {
		
		TaaSResourceManager taasRM = ServiceManager.getInstance().getResourceManagerIF();
		if (taasRM == null) {
			mLogger.error("TaaSRM not available to set data");
			return false;
		}
		
		byte[] decodedToken = null;
		try {
			if (token != null) decodedToken = Base64Utility.decode(token);
		} catch (Exception e) {
			mLogger.error("Cannot decode token: " + e.getMessage());
		}
		
		return taasRM.setData(serviceID, data, decodedToken);
	}
	
	
	/**
	 * Request TaaSRM to register for an allocated service
	 * @param appID
	 * @param serviceID
	 * @param token the Base64 encoded token to access the service 
	 * @return true on success, false otherwise
	 */
	public boolean register(String appID, String serviceID, String token) {
		TaaSResourceManager taasRM = ServiceManager.getInstance().getResourceManagerIF();
		if (taasRM == null) {
			mLogger.error("TaaSRM not available to register for a service");
			return false;
		}
		
		byte[] decodedToken = null;
		try {
			if (token != null) decodedToken = Base64Utility.decode(token);
		} catch (Exception e) {
			mLogger.error("Cannot decode token: " + e.getMessage());
		}
		
		return taasRM.registerService(serviceID, decodedToken);
	}
	
	
	/**
	 * Request TaaSRM to unregister for an allocated service
	 * @param appID
	 * @param serviceID
	 * @param token the Base64 encoded token to access the service 
	 * @return true on success, false otherwise
	 */
	public boolean unregister(String appID, String serviceID, String token) {
		TaaSResourceManager taasRM = ServiceManager.getInstance().getResourceManagerIF();
		if (taasRM == null) {
			mLogger.error("TaaSRM not available to unregister from service");
			return false;
		}
		
		byte[] decodedToken = null;
		try {
			if (token != null) decodedToken = Base64Utility.decode(token);
		} catch (Exception e) {
			mLogger.error("Cannot decode token: " + e.getMessage());
		}
		
		return taasRM.unRegisterService(serviceID, decodedToken);
	}
	
	
	/**
	 * @param dataList
	 * @param operator
	 * @return the combined value
	 * @throws Exception if the combination is not possible
	 */
	private String combine(JsonArray dataList, JsonElement operator) throws Exception {
		
		if ((dataList == null) || (operator == null)) {
			throw new Exception("null data list / operator");
		}
		
		int i;
		JsonObject measure;
		JsonElement el;
		String opr = operator.toString();
		opr = trim(opr);
		String value;
		
		if (opr == null) throw new Exception("null operator");
		
		if (opr.equalsIgnoreCase("OR")) {
			//////////////////////////////////// OR /////////////////////////////////
			boolean result = false;
			
			for (i=0; i < dataList.size(); i++) {
				measure = dataList.get(i).getAsJsonObject();
				if (measure == null) throw new Exception("One of the measures is null");
				el = measure.get("measurement");
				if (el == null) throw new Exception("One of the measures is null");
				value = el.toString();
				value = trim(value);
				if ((value.equalsIgnoreCase("TRUE")) || (value.equals("1"))) {
					result = true;
					break;
				} else if ((!value.equalsIgnoreCase("FALSE")) && (!value.equals("0"))) {
					throw new Exception("Unexpected value with operation OR: " + value);
				}
			}
			if (result) return "true";
			else return "false";
		} else if (opr.equalsIgnoreCase("AND")) {
			//////////////////////////////////// AND /////////////////////////////////
			boolean result = true;
			
			for (i=0; i < dataList.size(); i++) {
				measure = dataList.get(i).getAsJsonObject();
				if (measure == null) throw new Exception("One of the measures is null");
				el = measure.get("measurement");
				if (el == null) throw new Exception("One of the measures is null");
				value = el.toString();
				value = trim(value);
				if ((value.equalsIgnoreCase("FALSE")) || (value.equals("0"))) {
					result = false;
					break;
				} else if ((!value.equalsIgnoreCase("TRUE")) && (!value.equals("1"))) {					
				  throw new Exception("Unexpected value with operation OR: " + value);
				}
			}
			if (result) return "true";
			else return "false";
		} else if (opr.equalsIgnoreCase("AVERAGE")) {
			//////////////////////////////////// AVERAGE /////////////////////////////////
			float result = 0.0f;
			
			for (i=0; i < dataList.size(); i++) {
				measure = dataList.get(i).getAsJsonObject();
				if (measure == null) throw new Exception("One of the measures is null");
				el = measure.get("measurement");
				if (el == null) throw new Exception("One of the measures is null");
				value = el.toString();
				value = trim(value);
				try {
					result += Float.parseFloat(value);
				} catch (Exception e) {
					throw new Exception("Error parsing numeric value for operation AVERAGE: " + value);
				}
			}
			if (i > 0) return Float.toString(result / i);
			else throw new Exception("Zero values to be averaged");
		} else if (opr.equalsIgnoreCase("SUM")) {
			//////////////////////////////////// SUM /////////////////////////////////
			float result = 0.0f;
			
			for (i=0; i < dataList.size(); i++) {
				measure = dataList.get(i).getAsJsonObject();
				if (measure == null) throw new Exception("One of the measures is null");
				el = measure.get("measurement");
				if (el == null) throw new Exception("One of the measures is null");
				value = el.toString();
				value = trim(value);
				try {
					result += Float.parseFloat(value);
				} catch (Exception e) {
					throw new Exception("Error parsing numeric value for operation SUM: " + value);
				}
			}
			return Float.toString(result);
		}
		
		throw new Exception("Requested combination " + opr + " not supported for incoming data");
	}
	

  private String trim(String s) {
    if (s == null) return null;
    if (s.startsWith("\"")) s = s.substring(1);
		if (s.endsWith("\"")) s = s.substring(0, s.length()-1);
		
		return s;	
	}
	
	/** Logger */
	private static Logger mLogger = Logger.getLogger(ServiceManager.LOGGER_NAME);	

}
