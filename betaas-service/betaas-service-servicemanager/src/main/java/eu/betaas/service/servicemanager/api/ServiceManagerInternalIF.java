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

package eu.betaas.service.servicemanager.api;

import com.google.gson.JsonObject;

/**
* This interface defines the SM service exposed through OSGI and visible only
* inside the gateway.
*  
* @author Intecs
*/
public interface ServiceManagerInternalIF {

	/**
	 * It is called by TaaSRM to communicate that the resources for a service requested by an 
	 * application or an external service that is being installed have been allocated.
	 * This version also associates a security token to to the service.
	 * @param serviceID the identifier assigned by TaaSRM to the service
	 * @param token to be used by apps to access the service
	 * @return true in case of success
	 */
	public boolean notifyServiceInstallation(String serviceID, byte[] token);
	
	/**
	 * It is called by TaaSRM to communicate that the resources for a service requested by an 
	 * application or an external service that is being installed have been allocated.
	 * This version considers a null token to be associated to the service.
	 * @param serviceID the identifier assigned by TaaSRM to the service
	 * @return true in case of success
	 */
	public boolean notifyServiceInstallation(String serviceID);
	
	/**
	 * It is called by TaasRM in order to provide a new measurement.
	 * It forwards the data to the application or to the extended service associated
	 * to the service. In case of application, the corresponding notification address is used.
	 * In case of extended service, the bundle interface is used.
	 * @param serviceID is the identifier received by the installation procedure.
	 * @param data is the value to be forwarded to the application or the extended service bundle.
	 * @return true in case of success
	 */
	public boolean notifyNewMeasurement(String serviceID, JsonObject data);

	/**
	 * It is called by QoSM to notify the agreement EPR during the installation
	 * of a service.
	 * @param serviceID the ID of the service to which the EPR refers
	 * @param agreementEPR the EPR created by QoSM
	 */
	public void notifyAgreementEPR(String serviceID, String agreementEPR);
	
	/**
	 * It is called by QoSM to notify the violation of SLA for the specified service.
	 * SM forward such notification to the application or extended service that installed the service.
	 * @param serviceID identifier of the service assigned during installation
	 */
	public void notifySLAViolation(String serviceID);

	// TODO: interfaces to BDM

	/**
	 * It is called by the extended service in order to request the internal GW
	 * modules to prepare for the service execution and receive an ID that
	 * uniquely identifies the installation request.
	 * 
	 * @param manifestFileName
	 *            This is the filename of the Manifest containing the
	 *            information to be forwarded to TaasRM: semantic, QoS
	 *            requirements credential and the Betaas Extended Service ID.
	 * @return true in case of success
	 */
	public boolean installExtendedService(String manifestFileName);
	
	/**
	 * It is used by extended services to request data with the pull mechanism.
	 * @param extServID identifier assigned by SM at the time of the extended service installation
	 * @param serviceID identifier assigned by TaasRM to the on-the-fly service created
	 *                  at the time of the extended service installation
	 * @param token the Base64 encoded token to access the service 
	 * @return the data returned by the service having the specified serviceID
	 */
	public String getThingServiceData(String extServID, String serviceID, String token);

	/**
	 * Used by extended services to control actuators via the allocated thing services
	 * @param extServID identifier assigned by SM at the time of the extended service installation
	 * @param serviceID the service installed to control thing services 
	 * @param data the data to be set
	 * @param token the Base64 encoded token to access the service 
	 * @return true if the command is successfully sent to TaaS layer
	 */
	public boolean setThingServiceData(String extServID, String serviceID, String data, String token);
	
	/**
	 * Allows extended services to register for a service in the same way applications do
	 * with the SM external interface
	 * @param extServiceID of the extended service requesting the registration
	 * @param serviceID to be registered for
	 * @param token the Base64 encoded token to access the service 
	 * @return true on success
	 */
	public boolean register(String extServiceID, String serviceID, String token);
	
	/**
	 * Allows extended services to unregister for a service in the same way applications do
	 * with the SM external interface
	 * @param extServiceID of the extended service requesting the un-registration
	 * @param serviceID to be registered for
	 * @param token the Base64 encoded token to access the service 
	 * @return true on success
	 */
	public boolean unregister(String extServiceID, String serviceID, String token);
	
	/**
	 * Allows extended services to call synchronous BDM tasks
	 * @param extServID the extended service identifier
	 * @param taskName
	 * @return the json structure representing the task result
	 */
	public String getTaskData(String extServID, String taskName);
}
