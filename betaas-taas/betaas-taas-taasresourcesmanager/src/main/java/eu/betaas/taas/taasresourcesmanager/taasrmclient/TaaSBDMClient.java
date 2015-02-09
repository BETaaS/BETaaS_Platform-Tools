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

package eu.betaas.taas.taasresourcesmanager.taasrmclient;

import org.apache.log4j.Logger;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.util.tracker.ServiceTracker;

import com.google.gson.JsonObject;

import eu.betaas.taas.bigdatamanager.core.services.ITaasBigDataManager;
import eu.betaas.taas.bigdatamanager.database.hibernate.data.ThingInformation;
import eu.betaas.taas.bigdatamanager.database.service.IBigDataDatabaseService;

public class TaaSBDMClient 
{
	private IBigDataDatabaseService myClient;
	private Logger logger= Logger.getLogger("betaas.taas");
	static private TaaSBDMClient _instance = null;
	
	private TaaSBDMClient ()
	{
		// Retrieve the BundleContext from the OSGi Framework
		BundleContext context = FrameworkUtil.getBundle(TaaSBDMClient.class).getBundleContext();
		
		// Open tracker in order to retrieve BD Manager services		
		ServiceTracker myTracker = new ServiceTracker(context, IBigDataDatabaseService.class.getName(), null); 
		myTracker.open();		
		Object [] providers = myTracker.getServices(); 
		
		// Select a provider
		int n = 0;
		if ( providers != null && providers.length > 0 ) 
		{ 		
			logger.debug("Number of providers found for TaaS BDM: " + providers.length);			
			myClient = (IBigDataDatabaseService) providers[n];	
			logger.info("Taas Big Data Manager Service found!");
		}
		else
		{
			logger.error("No providers were found for the TaaS BD Manager");			
		}
		
		// Close the tracker
		myTracker.close();
	}
	
	public static synchronized TaaSBDMClient instance() 
	{
		Logger myLogger= Logger.getLogger("betaas.taas");
		if (null == _instance) 
		{			
			_instance = new TaaSBDMClient();				
			if (_instance.myClient == null)
			{
				myLogger.error("No TaaS BDM client was created!");
				_instance = null;
				return null;
			}			
			myLogger.info("A new instance of the Big Data Manager Client was created!");
		}
		return _instance;
	}		
	
	public ThingInformation getThingInfo (String idThing)
	{
		ThingInformation myThing = new ThingInformation();
		myThing.setThingID(idThing);
		
		return myClient.searchThingInformation(myThing);
	}
	
	public void saveThingData (String thingId, String value)
	{
		// Retrieve the client for the DB Core
		ITaasBigDataManager myCoreClient = getBDCoreClient();
		if (myCoreClient==null)
		{
			logger.error("It was not possible to retrieve the Core DB client. Skipping data storage...");
			return;
		}
		
		// Send data to the DB
		JsonObject thing = new JsonObject();
		thing.addProperty("id", thingId);		
		thing.addProperty("measurement", value);		

		logger.debug("Invoking ITaasBigDataManager for storing Thing Data info.");
		myCoreClient.setThingsBDM(thingId, thing);

	}
	
	private ITaasBigDataManager getBDCoreClient ()
	{
		// Retrieve the BundleContext from the OSGi Framework
		BundleContext context = FrameworkUtil.getBundle(TaaSBDMClient.class).getBundleContext();
		ITaasBigDataManager coreClient=null;
				
		// Open tracker in order to retrieve Core BD Manager services		
		ServiceTracker myTracker = new ServiceTracker(context, ITaasBigDataManager.class.getName(), null); 
		myTracker.open();		
		Object [] providers = myTracker.getServices(); 
				
		// Select a provider
		int n = 0;
		if ( providers != null && providers.length > 0 ) 
		{ 		
			logger.debug("Number of providers found for Core BDM: " + providers.length);			
			coreClient = (ITaasBigDataManager) providers[n];	
			logger.debug("Core Big Data Manager Service found!");
		}
		else
		{
			logger.error("No providers were found for the Core TaaS BD Manager");			
		}
			
		// Close the tracker
		myTracker.close();
		return coreClient;
	}
}
