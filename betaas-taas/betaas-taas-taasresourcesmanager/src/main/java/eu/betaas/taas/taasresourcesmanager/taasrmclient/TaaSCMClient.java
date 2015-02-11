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

package eu.betaas.taas.taasresourcesmanager.taasrmclient;

import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.util.tracker.ServiceTracker;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import eu.betaas.taas.bigdatamanager.database.service.ThingsData;
import eu.betaas.taas.contextmanager.api.ThingsServiceManager;
import eu.betaas.taas.contextmanager.api.impl.ThingServiceList;
import eu.betaas.taas.taasresourcesmanager.api.Location;
import eu.betaas.taas.taasresourcesmanager.api.ThingServiceResult;

public class TaaSCMClient 
{
	private ThingsServiceManager myClient;
	private Logger logger= Logger.getLogger("betaas.taas");
	static private TaaSCMClient _instance = null;
	
	private TaaSCMClient ()
	{
		// Retrieve the BundleContext from the OSGi Framework
		BundleContext context = FrameworkUtil.getBundle(TaaSCMClient.class).getBundleContext();
				
		// Open tracker in order to retrieve Context Manager services
		ServiceTracker myTracker = new ServiceTracker(context, ThingsServiceManager.class.getName(), null); 
		myTracker.open();		
		Object [] providers = myTracker.getServices(); 
		
		// Select a provider
		int n = 0;
		if ( providers != null && providers.length > 0 ) 
		{ 		
			logger.debug("Number of providers found for TaaS CM: " + providers.length);			
			myClient = (ThingsServiceManager) providers[n];
			logger.info("Taas Context Manager Service found!");
		}
		else
		{
			logger.error("No providers were found for the TaaS Context Manager");			
		}
		
		// Close the tracker
		myTracker.close();
	}
	
	public static synchronized TaaSCMClient instance() 
	{
		Logger myLogger= Logger.getLogger("betaas.taas");
		myLogger.info("Obtaining an instance of the TaaS CM Client...");
		if (null == _instance) 
		{
			_instance = new TaaSCMClient();				
			if (_instance.myClient == null)
			{
				myLogger.error("No TaaS CM client was created!");
				_instance = null;
				return null;
			}
			myLogger.info("A new instance of the Context Manager Client was created!");
		}
		return _instance;
	}
	
	public ThingServiceList getThingServices (String feature, Location location, String mode)
	{
		logger.info ("Calling Get Thing Services to the CM with feature " + feature + " in " + location.getLocationIdentifier());
		ThingServiceList myList = null;
		try
		{
			// Retrieve the JasonArray object with the list			
			String resList = null;
			if (location.getEnvironment())
			{
				resList = myClient.getContextThingServices(feature, location.getLocationIdentifier(), location.getLocationKeyword(), location.getLatitude(), location.getLongitude(), location.getAltitude(), Float.toString(location.getRadius()));
			}
			else
			{
				resList = myClient.getContextThingServices(feature, location.getLocationIdentifier(), location.getLocationKeyword(), location.getFloor());
			}
			
			logger.debug("Data received: " + resList);
			
			JsonElement jelement = new JsonParser().parse(resList);
			JsonObject parsedRes = jelement.getAsJsonObject();
			JsonArray listArray = parsedRes.getAsJsonArray("list");
			JsonArray listEqArray = parsedRes.getAsJsonArray("eq_list");
			String operator = parsedRes.get("operator").getAsString();
						
			// Transform the JasonArray in an ArrayList
			ArrayList<String> myThingServicesList = new ArrayList<String>();
			for (int i=0; i<listArray.size(); i++)
			{
				myThingServicesList.add(listArray.get(i).getAsString());
			}	
			
			// Transform the Equivalent Services matrix in an ArrayList of ArrayList
			ArrayList<ArrayList<String>> myEqThingServicesList = new ArrayList<ArrayList<String>>();
			for (int i=0; i<listEqArray.size(); i++)
			{
				JsonArray currentList = listEqArray.get(i).getAsJsonArray();
				ArrayList<String> currentResList = new ArrayList<String>();
				for (int j=0; j<currentList.size(); j++)
				{
					currentResList.add(currentList.get(j).getAsString());
				}
				myEqThingServicesList.add(currentResList);
			}	
						
			// Create ThingServiceList object
			myList = new ThingServiceList();
			myList.setsOperator(operator);
			myList.setCMThingServicesList(myThingServicesList);
			myList.setCMThingServicesListEq(myEqThingServicesList);
			
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return null;
		}
		
		logger.info("Invocation done!");
		logger.info("Operator: " + myList.getsOperator());		
		return myList;		
	}
	
	public ArrayList<String> getThingServices ()
	{
		logger.info ("Calling Get Thing Services to the CM for getting the full list");
		ArrayList<String> myList = null;
		try
		{
			// Retrieve the JasonArray object with the list
			String resList = myClient.getContextThingServices();
			JsonElement jelement = new JsonParser().parse(resList);
			JsonObject parsedRes = jelement.getAsJsonObject();
			JsonArray listArray = parsedRes.getAsJsonArray("list");
						
			// Transform the JasonArray in an ArrayList
			myList = new ArrayList<String>();
			for (int i=0; i<listArray.size(); i++)
			{
				myList.add(listArray.get(i).getAsString());
			}				
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return null;
		}
		
		logger.info("Invocation done! Retrieved: " + myList.size());
		return myList;
	}
	
	public ThingServiceResult getFullMeasurement (String idThingService)
	{
		// Retrieve the result
		String adaptorResult = myClient.getContextualMeasurement(idThingService);
		
		// Extract data from the JSON structure
		JsonElement jelement = new JsonParser().parse(adaptorResult);	
		JsonObject parsedRes = jelement.getAsJsonObject();
		String measurement = parsedRes.get("Measurement").getAsString();
		String unit = parsedRes.get("Unit").getAsString();
		boolean environment = parsedRes.get("Environment").getAsBoolean();
		float lat = parsedRes.get("Latitude").getAsFloat();
		float lon = parsedRes.get("Longitude").getAsFloat();
		float alt = parsedRes.get("Altitude").getAsFloat();
		int floor = parsedRes.get("Floor").getAsInt();
		String locationKeyword = parsedRes.get("LocationKeyword").getAsString();
		String locationIdentifier = parsedRes.get("LocationIdentifier").getAsString();
				
		// Construct the result with the local format
		ThingServiceResult myResult = new ThingServiceResult(measurement, unit, environment, lat, lon, alt, floor, locationKeyword, locationIdentifier); 
		
		return myResult;
	}
	
	public boolean subscribeToThing (String idThingService)	
	{
		
		return true;
	}
}
