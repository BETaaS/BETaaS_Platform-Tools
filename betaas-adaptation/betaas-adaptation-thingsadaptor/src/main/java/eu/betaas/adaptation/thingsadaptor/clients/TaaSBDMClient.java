/**

Copyright 2016 ATOS SPAIN S.A.

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

package eu.betaas.adaptation.thingsadaptor.clients;

import java.sql.Timestamp;
import java.util.Date;

import org.apache.log4j.Logger;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.util.tracker.ServiceTracker;

import eu.betaas.taas.bigdatamanager.database.hibernate.data.ThingData;
import eu.betaas.taas.bigdatamanager.database.hibernate.data.ThingInformation;
import eu.betaas.taas.bigdatamanager.database.service.IBigDataDatabaseService;
import eu.betaas.taas.bigdatamanager.database.service.ThingsData;

public class TaaSBDMClient 
{
	private IBigDataDatabaseService myClient;
	private Logger logger= Logger.getLogger("betaas.adaptation");
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
		Logger myLogger= Logger.getLogger("betaas.adaptation");
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
	
	public void saveThingData (ThingsData newData)
	{
		ThingData myData = new ThingData();
		myData.setAltitude(newData.getAltitude());
		myData.setBattery_cost(newData.getBatteryCost());
		myData.setBattery_level(newData.getBatteryLevel());
		myData.setCity_name(newData.getLocationKeyword());
		myData.setEnvironment(Boolean.toString(newData.getEnvironment()));
		myData.setFloor(newData.getFloor());
		myData.setLatitude(newData.getLatitude());
		myData.setLocation(newData.getLocationIdentifier());
		myData.setLocation_identifier(newData.getLocationIdentifier());
		myData.setLocation_keyword(newData.getLocationKeyword());
		myData.setLongitude(newData.getLongitude());
		myData.setMeasurement(newData.getMeasurement());
		myData.setMemory_status(newData.getMemoryStatus());
		myData.setRoom(newData.getLocationIdentifier());
		myData.setThingID(newData.getThingId());		
		Date date = new Date();
		Timestamp ts = new Timestamp(date.getTime());
		myData.setTimestamp(ts);
		myClient.saveThingData(myData);

	}
}
