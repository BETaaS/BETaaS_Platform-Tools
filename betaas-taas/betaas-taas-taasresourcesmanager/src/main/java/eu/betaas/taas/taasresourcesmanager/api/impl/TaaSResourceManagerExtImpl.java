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

package eu.betaas.taas.taasresourcesmanager.api.impl;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import eu.betaas.taas.bigdatamanager.database.service.ThingsData;
import eu.betaas.taas.taasresourcesmanager.messaging.MessageManager;
import eu.betaas.taas.taasresourcesmanager.api.ResourceInfo;
import eu.betaas.taas.taasresourcesmanager.api.TaaSResourceManagerExt;
import eu.betaas.taas.taasresourcesmanager.api.ThingServiceResult;
import eu.betaas.taas.taasresourcesmanager.catalogs.Resource;
import eu.betaas.taas.taasresourcesmanager.catalogs.ResourcesCatalog;
import eu.betaas.taas.taasresourcesmanager.endpointsmanager.EndpointsManager;
import eu.betaas.taas.taasresourcesmanager.endpointsmanager.PushManager;
import eu.betaas.taas.taasresourcesmanager.taasrmclient.TaaSRMClient;

public class TaaSResourceManagerExtImpl implements TaaSResourceManagerExt 
{
	private Logger logger= Logger.getLogger("betaas.taas");
	private String gwId;
	private MessageManager mManager;
	private String mDelimiter;
	
	public void setupService(){
		logger.debug("[TaaSResourceManagerExtImpl] Starting the service");
		mManager = MessageManager.instance();
		TaaSRMClient.instance(gwId, "TaaSResourceManagerExtImpl");
		logger.debug("[TaaSResourceManagerExtImpl] Service started");
		mManager.monitoringPublish("[TaaSResourceManagerExtImpl] Service started");
	}
	
	
	
	public void setGwId(String id)
	{
		gwId = id;
	}
	
	public String getGwId ()
	{
		return gwId;
	}
	
	public void setDelimiter(String delimiter)
	{
		mDelimiter = delimiter;
	}
	
	public ResourceInfo[] synchronizeThingServices(ResourceInfo[] thingServicesList, String gateway) 
	{
		logger.info ("Remote synchronization requested by gateway " + gateway);
		logger.info ("Receiving Thing Services from the remote TaaSRM...");
		
		// First, retrieve the local list of resources for avoiding duplicates
		ResourcesCatalog myResCatalog = ResourcesCatalog.instance();
		ArrayList<ResourceInfo> localRes = myResCatalog.getResourcesForSynchronizing();
				
		// Register the remote resources
		logger.debug("Adding remote resources...");
		if (thingServicesList==null || thingServicesList.length==0)
		{
			logger.warn("The list of remote Thing Services is empty!");
		}
		else
		{
			// Received resources list is not empty --> Add them
			for (int i=0; i<thingServicesList.length; i++)
			{
				ResourceInfo current = thingServicesList[i];
				Resource newRes = new Resource (current.getResourceId(), current.getPhysicalResourceId(), current.getResourceType(), gateway);
				newRes.setStatus(current.getStatus());
				myResCatalog.addResource(newRes);
				logger.debug("Resource " + newRes.getResourceId() + " added.");
			}
		}
				
		// Send our local resources				
		if (localRes==null || localRes.size()==0)
		{
			logger.warn("Our list of resources is empty, sending null list.");
			logger.info("Synchronization finished.");
			return null;
		}
		logger.info("Sending list of resources and finishing synchronization.");
		localRes.trimToSize();
		ResourceInfo [] resultList = new ResourceInfo[localRes.size()];
		localRes.toArray(resultList);

		return resultList;
	}
	
	public ThingServiceResult getRemoteData(String idThingService, boolean realTime)
	{
		logger.info ("Get Remote Data method invoked! -> " + idThingService);
		EndpointsManager invokator = new EndpointsManager();
		return invokator.invokeThingService(idThingService, realTime);		
	}
	
	public boolean setRemoteData(String idThingService, String value)
	{
		logger.info ("Set Remote Data method invoked! -> " + idThingService);
		EndpointsManager invokator = new EndpointsManager();
		return invokator.setDataToThingService(idThingService, value);
	}
	
	public boolean remoteSubscription (String idThingService, String idApplication, int period, boolean realTime, String gateway)
	{
		logger.info ("Remote Subscription method invoked! -> " + idThingService);
		EndpointsManager invokator = new EndpointsManager(gwId);
		return invokator.registerSubscription(idThingService, idApplication, period, realTime, gateway);
	}
	
	public boolean remoteUnsubscription (String idThingService, String idFeature, boolean realTime)
	{
		logger.info ("Remote Unsubscription method invoked! -> " + idThingService);
		EndpointsManager invokator = new EndpointsManager(gwId);
		return invokator.unregisterSubscription(idThingService, idFeature, realTime);
	}
	
	public boolean remoteDataNotification (String data, String idFeature, String idThingService)
	{
		logger.info ("Remote data received for the PUSH mode! -> " + idThingService);
		logger.debug("Data: " + data);
		//Extract ThingsData object from the JSON
		ThingsData receivedData = new ThingsData();
		JsonElement jelement = new JsonParser().parse(data);
		JsonObject parsedRes = jelement.getAsJsonObject();	
		String maximumResponseTime = parsedRes.get("maximumResponseTime").getAsString();
		receivedData.setMaximumResponseTime(maximumResponseTime);
		String deviceID = parsedRes.get("deviceID").getAsString();
		receivedData.setDeviceID(deviceID);
		String type = parsedRes.get("type").getAsString();
		receivedData.setType(type);		
		String measurement = parsedRes.get("measurement").getAsString();
		receivedData.setMeasurement(measurement);
		String unit = parsedRes.get("unit").getAsString();
		receivedData.setUnit(unit);;
		String thingId = parsedRes.get("thingId").getAsString();
		receivedData.setThingId(thingId);
		String altitude = parsedRes.get("altitude").getAsString();
		receivedData.setAltitude(altitude);
		String latitude = parsedRes.get("latitude").getAsString();
		receivedData.setLatitude(latitude);
		String longitude = parsedRes.get("longitude").getAsString();
		receivedData.setLongitude(longitude);
		String batteryLevel = parsedRes.get("batteryLevel").getAsString();
		receivedData.setBatteryLevel(batteryLevel);
		String memoryStatus = parsedRes.get("memoryStatus").getAsString();
		receivedData.setMemoryStatus(memoryStatus);
		String locationKeyword = parsedRes.get("locationKeyword").getAsString();
		receivedData.setLocationKeyword(locationKeyword);
		String locationIdentifier = parsedRes.get("LocationIdentifier").getAsString();
		receivedData.setLocationIdentifier(locationIdentifier);
		
		//Provide data to the PushManager, to process it and determine whether to notify the SM		
		PushManager myManager = PushManager.instance(gwId);
		return myManager.receiveRemoteTAMeasurement(idThingService, receivedData, idFeature);		
	}

	public boolean registerThingsService(String thingID, String thingServiceID, String gatewayID) 
	{		
		logger.info("Remote Register Thing Services method invoked from " + gatewayID + "! -> " + thingID);
		Resource newResource = new Resource (thingServiceID, thingID, Resource.THINGSERVICE, gatewayID);		
		return ResourcesCatalog.instance().addResource(newResource);		
	}

	public boolean removeThingsService(String thingServiceID, String gatewayID) 
	{		
		logger.info("Remote Remove Thing Service method invoked from " + gatewayID + "! -> " + thingServiceID);				
		return ResourcesCatalog.instance().removeResource(thingServiceID);		
	}



	public boolean checkAccessPolicies(String credential, String serviceID) {
		// TODO Auto-generated method stub
		return true;
	}



	public String createNode(ResourceInfo[] resroucesList, String gateway) {
		// TODO Auto-generated method stub
		return "compute-1df1061b-3e96-470e-9b99-b3512b66efc1";
	}



	public String deployAppRemote(String request) {
		// TODO Auto-generated method stub
		return "app-1df1061b-3e96-470e-9b99-b3512b66efc1";
	}



	public String[] getMeasurement(String[] selectedThingList) {
		// TODO Auto-generated method stub
		String[] ret = new String[1];
		ret[0] = "";
		return ret;
	}



	public boolean migrationConfirmation(String[] migInfo) {
		// TODO Auto-generated method stub
		return true;
	}



	public String migrationRequest(String[] VMsReqs) {
		// TODO Auto-generated method stub
		return "";
	}



	public boolean subscribe(String[] thingServiceList) {
		// TODO Auto-generated method stub
		return true;
	}



	public boolean subscribe(String[] selectedThingList, String[] periodThings) {
		// TODO Auto-generated method stub
		return true;
	}



	public boolean synchronizeVMs(ResourceInfo[] resourcesList, String gateway) {
		// TODO Auto-generated method stub
		return true;
	}



	public boolean removeThingsService(String gatewayID) {
		// TODO Auto-generated method stub
		return true;
	}



	public boolean unDeployAppRemote(String appId) {
		// TODO Auto-generated method stub
		return true;
	}
}
