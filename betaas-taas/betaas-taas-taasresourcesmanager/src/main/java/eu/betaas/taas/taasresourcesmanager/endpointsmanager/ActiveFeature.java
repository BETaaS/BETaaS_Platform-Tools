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

**/

package eu.betaas.taas.taasresourcesmanager.endpointsmanager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.log4j.Logger;

import eu.betaas.taas.bigdatamanager.database.service.ThingsData;
import eu.betaas.taas.taasresourcesmanager.api.ThingServiceResult;

public class ActiveFeature 
{
	private ArrayList<String> invoked;
	private ArrayList<String> awaiting;
	private HashMap<String, ThingsData> lastValues;
	
	private String featureId;
	private String operation;
	private boolean completed;
	
	private Logger logger= Logger.getLogger("betaas.taas");
	
	public ActiveFeature (String idFeatureService, String theOperation)
	{
		featureId = idFeatureService;
		operation = theOperation;
		invoked = new ArrayList<String>();
		awaiting = new ArrayList<String>();
		lastValues = new HashMap<String, ThingsData>();
		completed = false;
	}
	
	public void addSubscription (String idThingService)
	{
		logger.info("New subscription for " + featureId + ": " + idThingService);
		
		awaiting.add(idThingService);		
		completed = false;
		
		logger.debug("Number of subscriptions requested: " + (awaiting.size()+invoked.size()));
	}
	
	public void removeSubscription (String idThingService)
	{
		logger.info("Remove subscription for " + featureId + ": " + idThingService);
		
		awaiting.remove(idThingService);
		invoked.remove(idThingService);
		
		logger.debug("Number of subscriptions registered: " + (awaiting.size()+invoked.size()));
	}
	
	public void notifyReceived (String idThingService, ThingsData data)
	{		
		// Check if the received data belongs to a pending thing service
		if (awaiting.contains(idThingService))
		{			
			// Put the service in the invoked list
			awaiting.remove(idThingService);
			invoked.add(idThingService);
						
			// Check if the list is completed
			if (awaiting.isEmpty())
			{
				completed = true;
				awaiting = invoked;
				invoked = new ArrayList<String>();
			}
		}	
		else if (!invoked.contains(idThingService))
		{
			logger.error("Thing service " + idThingService + " isn't subscribed to this feature!!!");
			return;
		}
		
		// Finally, update the value
		lastValues.put(idThingService, data);
						
	}
	
	public boolean isCompleted ()
	{		
		return completed;
	}
	
	public String getFeatureServiceId()
	{
		return featureId;
	}
	
	public FeatureResult getFeatureResult()
	{
		//Create the basic object
		FeatureResult myResult = new FeatureResult(featureId, operation);
		
		logger.debug("Thing Services expected: " + (awaiting.size()+invoked.size()) + " in feature " + featureId);
		
		Iterator<ThingsData> myIter = lastValues.values().iterator();
		while (myIter.hasNext())
		{
			ThingsData data = myIter.next();
			
			//Extract information and create a ThingServiceResult object
			ThingServiceResult thisResult = new ThingServiceResult();
			thisResult.setMeasurement(data.getMeasurement());			
			thisResult.setUnit(data.getUnit());
			thisResult.setEnvironment(data.getEnvironment());
			if (data.getLatitude()!=null)
			thisResult.setLatitude(Float.parseFloat(data.getLatitude()));
			if (data.getLongitude()!=null)
			thisResult.setLongitude(Float.parseFloat(data.getLongitude()));
			if (data.getAltitude()!=null)
			thisResult.setAltitude(Float.parseFloat(data.getAltitude()));
			if (data.getFloor()!=null)
			thisResult.setFloor(Integer.parseInt(data.getFloor()));
			thisResult.setLocationKeyword(data.getLocationKeyword());
			thisResult.setLocationIdentifier(data.getLocationIdentifier());
			
			myResult.addTSResult(thisResult);
		}
		
		//Once the values are retrieved, we consider it isn't completed until all the TSs are invoked again 
		completed = false;
		return myResult;
	}
	
	public void clean()
	{
		invoked = new ArrayList<String>();
		awaiting = new ArrayList<String>();
		lastValues = new HashMap<String, ThingsData>();
		completed = false;
	}
	
	public ArrayList<String> getThingServicesList()
	{
		ArrayList<String> myList = new ArrayList<String>();
		myList.addAll(invoked);
		myList.addAll(awaiting);
		return myList;
	}
	
	/*
	public static void main(String args[]) 
	{
		ActiveFeature myFeature = new ActiveFeature ("PresenceSensors", "AND");
		myFeature.addSubscription("Presencia1");
		myFeature.addSubscription("Presencia2");
		myFeature.addSubscription("MiPresencia");
		
		ThingsData firstThing = new ThingsData();
		firstThing.setAltitude("3");
		firstThing.setLatitude("48.35");
		firstThing.setLongitude("200.12");
		firstThing.setMeasurement("true");
		myFeature.notifyReceived("Presencia1", firstThing);
		
		ThingsData secondThing = new ThingsData();
		secondThing.setAltitude("3");
		secondThing.setLatitude("48.35");
		secondThing.setLongitude("200.12");
		secondThing.setMeasurement("true");
		myFeature.notifyReceived("Presencia2", secondThing);
		
		System.out.println ("Estado: " + myFeature.isCompleted());
		
		ThingsData thirdThing = new ThingsData();
		thirdThing.setAltitude("3");
		thirdThing.setLatitude("48.35");
		thirdThing.setLongitude("200.12");
		thirdThing.setMeasurement("true");
		myFeature.notifyReceived("MiPresencia", thirdThing);
		
		System.out.println ("Estado: " + myFeature.isCompleted());
		
		System.out.println ("Resultado: ");
		System.out.println (myFeature.getFeatureResult().getData());
	}
	*/
}
