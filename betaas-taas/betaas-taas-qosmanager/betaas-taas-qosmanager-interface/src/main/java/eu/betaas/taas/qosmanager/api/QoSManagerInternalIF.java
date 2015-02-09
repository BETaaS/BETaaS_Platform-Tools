/*
 *
Copyright 2014-2015 Department of Information Engineering, University of Pisa

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
// Component: TaaS QoS Manager
// Responsible: Carlo Vallati & Giacomo Tanganelli

package eu.betaas.taas.qosmanager.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.betaas.taas.bigdatamanager.database.hibernate.data.QoSMAssignmentInternal;
import eu.betaas.taas.bigdatamanager.database.hibernate.data.QoSMRequestInternal;
import eu.betaas.taas.bigdatamanager.database.hibernate.data.QoSMThingInternal;
import eu.betaas.taas.bigdatamanager.database.hibernate.data.QoSMThingServiceInternal;
import eu.betaas.taas.qosmanager.monitoring.api.impl.SLACalculation;


/**
* This interface defines the QoS manager services exposed through OSGI and visible only
* inside the gateway.
*  
* @author Unipi
*/
public interface QoSManagerInternalIF{
	/**
	 * It is called by SM to get the standard template for the BETaaS platform
	 * @return The template as a string
	 */
	public String getTemplate();
	
	/**
	 * It is called by SM to send the Offer. The function returns a valid AgreemntEPR
	 * in case the negotiation is successful.
	 * The return is performed asynchronously through the call notifyAgreementEPR
	 * The agreementEPR is a valid AgreementEPR if the offer is accepted, otherwise an invalid value is returned 
	 * if the offer is rejected (empty string). 
	 * @param offer is the offer
	 * 
	 */
	public void createAgreement(String offer);
	
	/**
	 *  It is used by TaaSRM at the time of service invocation for selecting for each thing service required one
	 *	among the equivalent thing services. The listOfEquivalentThingServices has been already checked for
	 *	security, dependability, etc... requirements.
	 * @param serviceId is the service ID
	 * @param thingServicesList list of required thing services
	 * @param equivalentThingServices list of equivalent thing services for each thing service required 
	 * @return is the list of selected thing services 
	 */
	
	public Map<String, Double> registerServiceQoSPUSH(String serviceId,
			ArrayList<String> thingServicesList,
			ArrayList<ArrayList<String>> equivalentThingServices);
	
	public List<String> registerServiceQoSPULL(String serviceId,
				ArrayList<String> thingServicesList,
				ArrayList<ArrayList<String>> equivalentThingServices);
	
	/**
	 * Unregister service.
	 *
	 * @param serviceId the service id
	 * @param selectedThingServicesList the selected thing services list
	 */
	//selectedThingServicesList is unnecessary due the fact that it can be retrieved with the serviceID
	public void unregisterServiceQoS(String serviceId);

	/**
	 * Used to associate a thing service to a device. The information is also forwarder to the QoSM*.
	 * Used at time of thing's connection. The TaaSRM parse the JSON information provided by the CM 
	 * and invoke the function with the device ID and the list of thing services. 
	 * Moreover for each thing service the TaaSRM gets the MaximumResponseTime and the BatteryLevel.
	 *
	 * @param thingServiceList -> List<thingServiceId>
	 * @return true, if successful
	 */
	public boolean writeThingsServicesQoS(ArrayList<String> thingServiceList);
	
	/**
	 * Modify things services. The information is also forwarder to the QoSM*.
	 * Used at time of thing's disconnection.
	 *
	 * @param thingServices the thing services list
	 * @return true, if successful
	 */
	public boolean modifyThingsServicesQoS(ArrayList<String> thingServices);
	
	public boolean thingRemoved(String thingServiceId);
	
	/**
	 * Delete already committed services.
	 *
	 * @param serviceList the service list
	 */
	public void deleteAlreadyCommittedServices(ArrayList<String> serviceList);
	
	/**
	 * Removes the reservations.
	 *
	 * @param serviceID the service id
	 * @return true, if successful
	 */
	public boolean removeReservations(String serviceID);
	
	/**
	 * Calculate sla.
	 *
	 * @param selectedThingServicesList the selected thing services list
	 * @return the SLA results
	 */
	@Deprecated
	public ArrayList<SLACalculation> calculateSLA(ArrayList<String> selectedThingService);
	
	public SLACalculation calculateSLA(String selectedThingService);
	public SLACalculation calculateSLAPush(String sThingServiceName, int isgTaaSRequestRate);
	public SLACalculation failureSLA(String sThingServiceName);
	
	/**
	 * Gets the measurement sla monitoring.
	 *
	 * @param selectedThingServicesList the selected thing services list
	 * @return the measurement sla monitoring
	 */
	
	public boolean getMeasurementSLAMonitoring(String sThingServiceName, int iOptimalRequestRate);
	@Deprecated
	public boolean getMeasurementSLAMonitoring(ArrayList<String> sThingServiceName);

	public String getGatewayId();
	
	public void setGatewayId(String gatewayId);
	
	public Map<String, Double> notifyAllocation(String serviceId, ArrayList<String> thingServicesList, 
			ArrayList<ArrayList<String>> eqtsList);
	
	public void update_db(QoSRankResults ret);
	
	public Map<String, Double> getBatteryLevels();

}
