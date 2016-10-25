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
import java.util.Map;
import java.util.Set;

import eu.betaas.taas.bigdatamanager.database.hibernate.data.QoSMEquivalentThingServiceInternal;

/**
* This interface defines the QoS manager services exposed through DOSGI and used for communication with GW*.
*  
* @author Unipi
*/
public interface QoSManagerExternalIF {

	/**
	 * Used to forward an offer to the QoSM*. The function returns a valid AgreemntEPR
	 * in case the negotiation is successful. 
	 * @param serviceId is the ID of the service requiring the thing service
	 * @param rank is the list of equivalent thing services
	 * @param spec is the QoS specification
	 * @return is a valid AgreementEPR if the offer is accepted, an invalid value if the 
	 * 			offer is rejected (empty string).
	 * @throws WrongArgumentException 
	 */
	public QoSRankList createAgreement(String serviceId, ArrayList<ArrayList<String>> rank, 
			QoSrequirements req, QoSManagerInternalIF caller, boolean all) throws WrongArgumentException;
	
	/**
	 * Used to associate a thing service to a device. The information is also forwarder to the QoSM*.
	 * Used at time of thing's connection. The TaaSRM parse the JSON information provided by the CM 
	 * and invoke the function with the device ID and the list of thing services. 
	 * Moreover for each thing service the TaaSRM gets the MaximumResponseTime and the BatteryLevel.
	 *
	 * @param deviceID the device id
	 * @param thingServices the thing services list associated to the thing with the QoS parameters
	 * @return true, if successful
	 */
	public boolean writeThingsServicesQoS(String deviceId, double battery,
			HashMap<String, QoSspec> thingServices, String gatewayId);
	
	/**
	 * Modify things services. The information is also forwarder to the QoSM*.
	 * Used at time of thing's disconnection.
	 *
	 * @param thingServices the thing services list
	 * @return true, if successful
	 */
	public boolean modifyThingsServicesQoS(ArrayList<String> thingServices, QoSManagerInternalIF caller);
	
	
	/**
	 * Unregister service.
	 *
	 * @param serviceId the service id
	 */
	public void unregisterServiceQoS(String serviceId);
	
	
	/**
	 * Synchronize thing service QoS. 
	 * It is invoked by the QoSM to the QOSM* when a new GW join a BETaaS instance.
	 *
	 * @param thingServices the thing services <deviceID, <thingServiceID, QoS specification>
	 * @return true, if successful
	 */
	//This is a new API which is missing in the API deliverable. It should be added to the Fig. 5.1.
	public boolean synchronizeThingsServicesQoS(HashMap<String, HashMap<String, QoSspec>> thingServices);
	
	
	/**
	 * Removes the things services.
	 * It is invoked by the QoSM to the QOSM* when a GW lefts a BETaaS instance.
	 *
	 * @param deviceID the device id
	 * @return true, if successful
	 */
	//This is a new API which is missing in the API deliverable. It should be added to the Fig. 5.2.
	public boolean removeThingsServicesQoS(ArrayList<String> deviceID);
	
	/**
	 * Gets the module score.
	 *
	 * @return the module score
	 */
	public String getModuleScore();
	
	/**
	 * Promote as star.
	 *
	 * @return true, if successful
	 */
	public boolean promoteAsStar();
	
	/**
	 * Register new star.
	 *
	 * @param gatewayID the gateway id
	 * @return true, if successful
	 */
	public boolean registerNewStar(String gatewayID);
	
	public QoSRankList assignment(boolean requireResponse, QoSManagerInternalIF caller);
	
	public String getGatewayId();
	
	public void setGatewayId(String gatewayId);

	public void updateQoSMEquivalents(
			String serviceId, ArrayList<ArrayList<String>> equivalentThingServices);

	public QoSRankList createAgreementAssured(String serviceid,
			ArrayList<ArrayList<String>> rank, QoSrequirements req,
			QoSManagerInternalIF caller, boolean all) throws WrongArgumentException;

	public void reachable(String tsid);

	public void unreachable(String tsid);

}
