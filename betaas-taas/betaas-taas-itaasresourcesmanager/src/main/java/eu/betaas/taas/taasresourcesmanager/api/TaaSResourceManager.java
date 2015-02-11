/**

Copyright 2014 ATOS SPAIN S.A.

Licensed under the Apache License, Version 2.0 (the License);
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

Authors :
Francisco Javier Nieto. Atos Research and Innovation, Atos SPAIN SA
@email francisco.nieto@atos.net 
Sergio García Villalonga. Atos Research and Innovation, Atos SPAIN SA
@email sergio.garciavillalonga@atos.net
**/

// BETaaS - Building the Environment for the Things as a Service
//
// Component: TaaSResourceManager
// Responsible: Atos

package eu.betaas.taas.taasresourcesmanager.api;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonObject;
import eu.betaas.taas.bigdatamanager.database.service.ThingsData;

/**
* 
* @author Francisco Javier Nieto De-Santos (francisco.nieto@atos.net)
* @author Sergio Garcia Villalonga (sergio.garciavillalonga@atos.net)
*/
public interface TaaSResourceManager 
{	
	//Methods for applications installation and resources allocation
	
	/**
	 * Allocates the required resources to applications and services.
	 * 
	 * @param appFeature
	 * @return
	 */	
	public String allocateResources(Feature appFeature);
	
	/**
	 * Releases resources associated to applications or
	 * services, once they are not necessary anymore.
	 * 
	 * @param serviceID
	 * @return true if the resources have been released successfully, false otherwise
	 */
	public boolean freeLocalResources(String serviceID);
	
	// End of methods for applications management
	
	// Methods for managing thing services
	
	/**
	 * Registers new Things Services when they are created.
	 * 
	 * @param thingID
	 * @param thingServiceID	 
	 * @return
	 */
	public boolean registerThingsServices(String thingID, String thingServiceID);	
	
	/**
	 * Synchronizes with other TaaSRMs in the same BETaaS 
	 * instance (i.e. when joining an instance).
	 * 
	 * @param instanceInfo
	 * @return
	 */
	public boolean synchronizeThingServices(String instanceInfo);
	
	/**
	 * Deletes the information about the Things that have
	 * been disconnected, and their associated Thing Services
	 * 	 
	 * @return
	 */
	public boolean deleteThingServices(List <String> thingServicesList);
			
	/**
	 * Removes the thing services belonging to a gateway that is not available anymore (it left the instance)
	 * 
	 * @param idGateway
	 * @return
	 */	
	public boolean removeThingServices(String idGateway);
	
	/**
	 * 
	 * @return
	 */
	public boolean removeThingServices();
	
	/**
	 * Removes a Thing Service, as it is not available anymore.
	 *	
	 * @param thingServiceID	 
	 * @return
	 */
	public boolean removeThingsService(String thingServiceID);
	
	// End methods for managing thing services
	
	// Methods for receiving notifications
	
	/**
	 * 
	 * Notifies the TaaSRM about the new measurement made by a Thing, used by the TA
	 * 
	 * @param thingServiceID
	 * @param data
	 * @return
	 */
	public boolean notifyMeasurement(String thingServiceID, ThingsData data);
	
	/**
	 * 
	 * @param thingServiceId
	 * @param trust
	 * @return
	 */
	public boolean notifyTrustAlert(String thingServiceId, int trust);
	
	/**
	 * Notifies the TaaSRM about the new measurement made by a Thing, used by the CM
	 * 
	 * @param thingServiceID
	 * @param data
	 * @return
	 */
	public void notifyNewMeasurement(String thingServiceID, ThingsData data);
		
	// End of methods for receiving notifications
	
	/*
	// GET and SET data methods: NRT-PULL, RT-PULL, NRT-PUSH, RT-PUSH and PUT modes
	*/
	
	/**
	 * Registers a subscription for a concrete feature service, so notifications will be sent
	 * 	
	 * @param serviceID	 
	 * @return
	 */
	public boolean registerService(String serviceID, byte[] token);
	
	/**
	 * Unregisters an existing subscription for a concrete feature service. Notifications won't be sent anymore.
	 * 	
	 * @param serviceID	 
	 * @return
	 */
	public boolean unRegisterService(String serviceID, byte[] token);
		
	/**
	 * Provides the data for a concrete service.
	 * 
	 * @param idFeatureService
	 * @return
	 */
	public JsonObject getData(String idFeatureService, byte[] token);
	
	/**
	 * Provides the data for a concrete service.
	 *	 
	 * @param idFeatureService
	 * @param newLocation
	 * @param token
	 * @return
	 */
	public JsonObject getData(String idFeatureService, Location newLocation, byte[] token);
	
	/**
	 * Assigns an specific value to a service
	 * 
	 * @param idFeatureService
	 * @param value
	 * @param token
	 * @return
	 */
	public boolean setData(String idFeatureService, String value, byte[] token);
	
	/**
	 * Assigns an specific value to a service
	 * 
	 * @param idFeatureService
	 * @param value
	 * @param newLocation
	 * @param token
	 * @return
	 */
	public boolean setData(String idFeatureService, String value, Location newLocation, byte[] token);
	
	/**
	 * Updates the location of a feature which is working on PUSH mode
	 * 
	 * @param idFeatureService	
	 * @param newLocation
	 * @param token
	 * @return
	 */
	public boolean updateFeatureLocation (String idFeature, Location newLocation, byte[] token);
		
	// Methods for the QoS Manager
	
	/**
	 * Provides a list of equivalent Thing Services for a given service
	 * 
	 * @param serviceID
	 * @return
	 */
	public ArrayList<ArrayList<String>> getSecurityRank (String serviceID);
	
	/**
	 * Provides a list of valid equivalent Thing Services for a given service, according to QoS requirements
	 * 
	 * @param Equivalent Things services filtered by QoS
	 * @return
	 */	
	public void putQoSRank(String serviceID, ArrayList<ArrayList<String>> equivalentThingServicesQoSRank);
	
	// End of methods for QoS Manager
	
	// Methods for VMs Management
	
	/**
	 * 
	 * @param authLevel
	 * @return
	 */
	public String createCompNode(int authLevel);
	
	/**
	 * 
	 * @param nodeId
	 * @return
	 */
	public boolean deleteNode(String nodeId);
		
	/**
	 * 
	 * @return notifications about any issue after trying to migrate VMs through the VMM
	 */
	public List<String> removeVMs();
	
	/**
	 * 
	 * @param deploymentRequest
	 * @return
	 */
	public String deployApp(String deploymentRequest); 
	
	/**
	 * 
	 * @param appId
	 * @return
	 */
	public boolean unDeployApp(String appId);
	
	// End of methods for VMs Management
	
	/**
	 * Revokes the service identified by serviceID
	 * 
	 * @param serviceID the id of the service to revoke.
	 */
	public void revokeService(String serviceID);
	
	/**
	 * 
	 * @return
	 */
	public int getAuthorizationLevels();
}
