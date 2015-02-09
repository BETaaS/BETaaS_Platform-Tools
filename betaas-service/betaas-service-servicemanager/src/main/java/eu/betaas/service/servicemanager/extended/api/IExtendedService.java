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

package eu.betaas.service.servicemanager.extended.api;

import java.util.ArrayList;

import com.google.gson.JsonObject;

/**
 * This interface must be implemented by extended services so that they can:
 * - receive install notifications
 * - receive data from the TaaS layer through the service manager
 * @author Intecs
 */
public interface IExtendedService {

	/**
	 * This is the name of the property that every extended service must associate with itself
	 * (as a bundle property) to contain a unique identifier within the instance. So there should not
	 * be more than one extended service with the same value for the property with this name.
	 * When an extended service requires SM to install, SM will take the value of this property
	 * to be able in the future to retrieve the extended service and use its IExtendedService interface.
	 */
	public final static String EXTENDED_SERVICE_PROPERTY_NAME_UNIQUE_ID = "ExtendedServiceUniqueID";
	
	
	/**
	 * It is used during discovery of extended services as an alternative to DOSGi filtering.
	 * @return the unique identifier of the extended service.
	 */
	public String getUniqueID();
	
	/**
	 * This method allows the extended service to receive the result of an
	 * installation request.
	 * @param success true iff the ext service has been succesfully installed
	 * @param msg additional info on result
	 * @param extServId identifier associated to the installed extended service
	 * @param servList the list of identifiers for allocated services. The extended service
	 *                 shall use these identifiers to refer its services in the requests to the local SM
	 * @param tokenList the list of Base64 encoded tokens to be used for the allocated services. It has
	 *                  a 1-to-1 correspondece with elements of servList
	 */
	public void notifyInstallation(boolean success, 
			                       String msg, 
			                       String extServId, 
			                       ArrayList<String> servList,
			                       ArrayList<String> tokenList);
	
	/**
	 * Used by SM to notify a SLA violation on a specific service
	 * @param serviceID
	 */
	public void notifySLAViolation(String serviceID);
	
	/**
	 * This method allows the extended service to receive data
	 * @param serviceID the ID of the service to which data refers
	 * @param data
	 */
	public void notifyData(String serviceID, JsonObject data);	
	
	/**
	 * It is called by SM when an application that uses the extended service requests 
	 * an output from it. The kind of output depends on the extended service logic (it could
	 * also provide no output at all).
	 * @param additionalInfo optional information that could be used to pass parameters (e.g.
	 *        as XML) to request a specific result.
	 * @return the extended service output in the format envisaged by the specific extended 
	 *         service design
	 */
	public String getResult(String additionalInfo);
}
