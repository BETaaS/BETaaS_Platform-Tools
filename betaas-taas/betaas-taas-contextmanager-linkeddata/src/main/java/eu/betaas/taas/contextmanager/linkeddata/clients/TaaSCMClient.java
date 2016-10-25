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

package eu.betaas.taas.contextmanager.linkeddata.clients;

import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.util.tracker.ServiceTracker;

import com.google.gson.Gson;
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
	
	public Thing getFullMeasurement (String idThingService)
	{	
		// Retrieve the result
		Gson gson = new Gson();
		String adaptorResult = myClient.getContextualMeasurement(idThingService);
		return gson.fromJson(adaptorResult, Thing.class);
	}
	
	public Thing getThing(String thingId) {
		Thing thing;
		for (String thingServiceId : getThingServices()) {
			thing = getFullMeasurement(thingServiceId);
			if (thing.getThingId().equals(thingId)) {
				return thing;
			}
		}
		
		return null;
	}
}
