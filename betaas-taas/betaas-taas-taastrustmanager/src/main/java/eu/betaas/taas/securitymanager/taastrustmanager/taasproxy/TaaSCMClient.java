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

Authors Contact:
Francisco Javier Nieto. Atos Research and Innovation, Atos SPAIN SA
@email francisco.nieto@atos.net
**/

package eu.betaas.taas.securitymanager.taastrustmanager.taasproxy;

import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.util.tracker.ServiceTracker;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import eu.betaas.taas.contextmanager.api.ThingsServiceManager;

public class TaaSCMClient 
{
	private ThingsServiceManager myClient;
	private Logger logger= Logger.getLogger("betaas.taas");
	static private TaaSCMClient _instance = null;
	
	private TaaSCMClient ()
	{
		// Retrieve the BundleContext from the OSGi Framework
		BundleContext context = FrameworkUtil.getBundle(TaaSCMClient.class).getBundleContext();
				
		// Open tracker in order to retrieve Trust Manager services
		ServiceTracker myTracker = new ServiceTracker(context, ThingsServiceManager.class.getName(), null); 
		myTracker.open();		
		Object [] providers = myTracker.getServices(); 
		
		// Select a provider
		int n = 0;
		if ( providers != null && providers.length > 0 ) 
		{ 		
			logger.debug("Number of providers found for TaaS CM: " + providers.length);
			//myClient = (ThingsServiceManager) providers[n++];
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
	
	public ArrayList<String> getThingServices ()
	{
		logger.debug ("Calling Get Thing Services to the CM for getting the full list");
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
			return new ArrayList<String>();
		}
		
		logger.debug("Invocation done! Retrieved: " + myList.size());
		return myList;
	}
	
	public String retrieveThingIdentifier (String thingServiceId)
	{
		String data = myClient.getContextualMeasurement(thingServiceId);
		JsonElement jelement = new JsonParser().parse(data);
		JsonObject parsedRes = jelement.getAsJsonObject();
		String thingId = parsedRes.get("ThingId").getAsString();
		
		return thingId;
	}
	
	public ArrayList<String> getEquivalentThingServices (String feature, ThingLocation location, String thingServiceId)
	{
		logger.debug("Loooking for equivalents related to " + thingServiceId);
		logger.debug ("Calling Get Thing Services to the CM with feature " + feature + " in " + location.getLocationIdentifier());
		ArrayList<String> myList = new ArrayList<String>();
		try
		{
			// Retrieve the JasonArray object with the list			
			String resList = null;
			if (location.getEnvironment())
			{
				resList = myClient.getContextThingServices(feature, location.getLocationIdentifier(), location.getLocationKeyword(), location.getLatitude(), location.getLongitude(), location.getAltitude(), Float.toString(100.0f));
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
						
			// Transform the JasonArray in an ArrayList and look for the current thing service			
			int position=-1;
			for (int i=0; i<listArray.size(); i++)
			{
				if (listArray.get(i).getAsString().equalsIgnoreCase(thingServiceId))
				{
					// We found our thing service, so we take the position for the equivalents list
					position = i;
					break;
				}
				
			}	
			
			if (position==-1)
			{
				// If the thing service didn't appear, then provide empty list
				logger.error("The thing service wasn't retrieved. Unable to find equivalents!");
				return myList;
			}
			
			// Transform the Equivalent Services matrix in an ArrayList of ArrayList
			JsonArray currentList = listEqArray.get(position).getAsJsonArray();			
			for (int j=0; j<currentList.size(); j++)
			{
				String currentEquivalent = currentList.get(j).getAsString();
				if (!currentEquivalent.equalsIgnoreCase(thingServiceId))
				{
					myList.add(currentEquivalent);
				}				
			}		
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return null;
		}
		
		logger.debug("Invocation done! Equivalents found: " + myList.size());
			
		return myList;		
	}
	
}
