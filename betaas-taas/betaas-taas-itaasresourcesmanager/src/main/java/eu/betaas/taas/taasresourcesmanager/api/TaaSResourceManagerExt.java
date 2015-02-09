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

package eu.betaas.taas.taasresourcesmanager.api;


public interface TaaSResourceManagerExt 
{
	/**
	 * 
	 * @param credential
	 * @param serviceID
	 * @return
	 */
	public boolean checkAccessPolicies(String credential, String serviceID);
	
	/**
	 * 
	 * @param resroucesList
	 * @param gateway
	 * @return
	 */
	public String createNode(ResourceInfo[] resroucesList, String gateway);
	
	/**
	 * 
	 * @param request
	 * @return
	 */
	public String deployAppRemote(String request); 
	
	/**
	 * 
	 * @param selectedThingList
	 * @return
	 */
	public String[] getMeasurement(String[] selectedThingList);
	
	/**
	 * Provides the data for a remote thing.
	 * 
	 * @param idThing	 
	 * @return
	 */	
	public ThingServiceResult getRemoteData(String idThingService, boolean realTime);
	
	/**
	 * Subscribes this gateway for retrieving the data from a remote gateway
	 * 
	 * @param idThing	 
	 * @return
	 */	
	public boolean remoteSubscription (String idThingService, String idApplication, int period, boolean realTime, String gateway);
	
	/**
	 * Sends remotely the corresponding data to the corresponding gateway and application (for PUSH modes)
	 * 
	 * @param idThing	 
	 * @return
	 */	
	public boolean remoteDataNotification (String data, String idFeature, String idThingService);
	
	/**
	 * Sets the data for a remote thing.
	 * 
	 * @param idThing	 
	 * @return
	 */	
	public boolean setRemoteData(String idThingService, String value);
	
	/**
	 * 
	 * @param migInfo
	 * @return
	 */
	public boolean migrationConfirmation(String[] migInfo);
	
	/**
	 * 
	 * @param VMsReqs
	 * @return
	 */
	public String migrationRequest(String[] VMsReqs);
	
	/**
	 * 
	 * @param thingServiceList
	 * @return
	 */
	public boolean subscribe(String[] thingServiceList);
	
	/**
	 * 
	 * @param selectedThingList
	 * @param periodThings
	 * @return
	 */
	public boolean subscribe(String[] selectedThingList, String[] periodThings);
	
	/**
	 * 
	 * @param resourcesList
	 * @param gateway
	 * @return
	 */
	public boolean synchronizeVMs(ResourceInfo[] resourcesList, String gateway);
	
	/**
	 * Synchronizes with other TaaSRMs in the same BETaaS 
	 * instance (i.e. when joining an instance).
	 * 
	 * @param thingServicesList
	 * @return
	 */
	public ResourceInfo[] synchronizeThingServices(ResourceInfo[] thingServicesList, String gatewayID);
	
	/**
	 * Registers new Things Services when they are created. This call is received from a remote gateway
	 * 
	 * @param thingID
	 * @param thingServiceID	 
	 * @param gatewayID
	 * @return
	 */	
	public boolean registerThingsService(String thingID, String thingServiceID, String gatewayID);
	
	/**
	 * Removes a Things Service when it is not available anymore. This call is received from a remote gateway
	 * 
	 * @param thingServiceID	 
	 * @param gatewayID
	 * @return
	 */	
	public boolean removeThingsService(String thingServiceID, String gatewayID);
	
	/**
	 * Removes a Things Service when it is not available anymore. This call is received from a remote gateway
	 * 
	 * @param gatewayID
	 * @return
	 */
	public boolean removeThingsService(String gatewayID);
	
	/**
	 * 
	 * @param appId
	 * @return
	 */
	public boolean unDeployAppRemote(String appId);
}
