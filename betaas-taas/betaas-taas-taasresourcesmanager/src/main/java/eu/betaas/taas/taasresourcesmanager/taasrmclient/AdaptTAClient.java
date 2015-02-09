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

import eu.betaas.adaptation.thingsadaptor.api.ThingsAdaptor;
import eu.betaas.taas.bigdatamanager.database.service.ThingsData;
import eu.betaas.taas.taasresourcesmanager.api.ThingServiceResult;

public class AdaptTAClient 
{
	private ThingsAdaptor myClient;
	private Logger logger= Logger.getLogger("betaas.taas");
	static private AdaptTAClient _instance = null;
	
	private AdaptTAClient ()
	{
		// Retrieve the BundleContext from the OSGi Framework
		BundleContext context = FrameworkUtil.getBundle(AdaptTAClient.class).getBundleContext();
				
		// Open tracker in order to retrieve Context Manager services
		ServiceTracker myTracker = new ServiceTracker(context, ThingsAdaptor.class.getName(), null); 
		myTracker.open();		
		Object [] providers = myTracker.getServices(); 
		
		// Select a provider
		int n = 0;
		if ( providers != null && providers.length > 0 ) 
		{ 		
			logger.debug("Number of providers found for Things Adaptor: " + providers.length);			
			myClient = (ThingsAdaptor) providers[n];
			logger.info("Taas Things Adaptor Service found!");
		}
		else
		{
			logger.error("No providers were found for the Things Adaptor");			
		}
		
		// Close the tracker
		myTracker.close();
	}
	
	public static synchronized AdaptTAClient instance() 
	{
		Logger myLogger= Logger.getLogger("betaas.taas");
		myLogger.info("Obtaining an instance of the Things Adaptor Client...");
		if (null == _instance) 
		{
			_instance = new AdaptTAClient();				
			if (_instance.myClient == null)
			{
				myLogger.error("No Things Adaptor client was created!");
				_instance = null;
				return null;
			}
			myLogger.info("A new instance of the Things Adaptor Client was created!");
		}
		return _instance;
	}
		
	public ThingServiceResult getFullMeasurement (String idThing)
	{
		ThingServiceResult myResult = null;
		
		try
		{
			// Retrieve the result
			ThingsData adaptorResult = myClient.getMeasurement(idThing);
			String measurement = adaptorResult.getMeasurement();
			String unit = adaptorResult.getUnit();
			boolean environment = adaptorResult.getEnvironment();
			float lat = Float.parseFloat(adaptorResult.getLatitude());
			float lon = Float.parseFloat(adaptorResult.getLongitude());
			float alt = Float.parseFloat(adaptorResult.getAltitude());
			int floor = Integer.parseInt(adaptorResult.getFloor());
			String locationKeyword = adaptorResult.getLocationKeyword();
			String locationIdentifier = adaptorResult.getLocationIdentifier();
			
			// Construct the result with the local format
			myResult = new ThingServiceResult(measurement, unit, environment, lat, lon, alt, floor, locationKeyword, locationIdentifier);
		}
		catch (Exception ex)
		{
			logger.error(ex.getMessage());
			return null;
		}		
		
		return myResult;
	}
	
	public boolean getThingConnected (String idThing)
	{
		// TODO Change this call so we can check things are available --> Method needs to be added at the TA side
		
		return true;
	}
	
	public boolean setThingData (String idThing, String value)
	{
		try
		{
			// Send data to the corresponding thing
			myClient.setThingValue(idThing, value);
		}
		catch (Exception ex)
		{
			logger.error(ex.getMessage());
			return false;
		}
		return true;
	}
	
	public boolean subscribeToThing (String thingID, int seconds)
	{
		return myClient.subscribe(thingID, seconds);
	}
	
	public boolean unSubscribeToThing (String thingID)
	{
		ArrayList<String> thingIDList = new ArrayList<String>();
		thingIDList.add(thingID);
		return myClient.unsubscribe(thingIDList);
	}
}
