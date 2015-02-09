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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

import org.apache.log4j.Logger;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.util.tracker.ServiceTracker;

import eu.betaas.taas.bigdatamanager.database.hibernate.data.ThingInformation;
import eu.betaas.taas.bigdatamanager.database.hibernate.data.TrustManagerService;
import eu.betaas.taas.bigdatamanager.database.service.IBigDataDatabaseService;
import eu.betaas.taas.securitymanager.taastrustmanager.taasaggregator.ThingServiceTrust;

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
	
	public void storeTrustData (ThingServiceTrust trustObject)
	{
		TrustManagerService myTrust = new TrustManagerService();
		myTrust.setThingServiceId(trustObject.getThingSerivceId());
		myTrust.setBatteryLoad(new Double (trustObject.getBatteryLoad()));
		myTrust.setDataStability(new Double(trustObject.getDataStability()));
		myTrust.setDependability(new Double (trustObject.getDependability()));
		myTrust.setQoSFulfillment(new Double (trustObject.getQoSFulfillment()));
		myTrust.setScalability(new Double (trustObject.getScalability()));
		myTrust.setSecurityMechanisms(new Double (trustObject.getSecurityMechanisms()));
		myTrust.setThingServiceTrust(new Double (trustObject.getThingServiceTrust()));
		Date date = new Date();
		Timestamp ts = new Timestamp(date.getTime());
		myTrust.setTimestamp(ts);
		myClient.saveTrustManagerService(myTrust);
	}	
	
	public ThingServiceTrust getTrustData (String idThingService)
	{
		TrustManagerService myTrust = new TrustManagerService();
		myTrust.setThingServiceId(idThingService);
		TrustManagerService result = myClient.searchTrustManagerService(myTrust);
		if (result==null)
		{
			return null;
		}
		double batteryLoad = result.getBatteryLoad().doubleValue();
		double dataStability = result.getDataStability().doubleValue();
		double dependability = result.getDependability().doubleValue();
		double qosFulfillment = result.getQoSFulfillment().doubleValue();
		double scalability = result.getScalability().doubleValue();
		double security = result.getSecurityMechanisms().doubleValue();
		double trustValue = result.getThingServiceTrust().doubleValue();
		
		return new ThingServiceTrust (idThingService, security, qosFulfillment, dependability, scalability, batteryLoad, dataStability, trustValue);
	}
	
	public ArrayList<ThingTrustData> getThingData (String idThing)
	{
		ArrayList<ThingTrustData> myResult = new ArrayList<ThingTrustData>();
		
		// We need to access directly to the connection and send a SQL query to the DB for getting data generated the last 24h 
		try
		{
			Connection myConn = myClient.getConnection();
			PreparedStatement myQuery = myConn.prepareStatement("SELECT timestamp, measurement, battery_cost, memory_status FROM T_THING_DATA WHERE thingID='" + idThing + "'");			
			ResultSet results = myQuery.executeQuery();
			while(results.next())
			{
				// Add all the results to the array list			   
				Timestamp theTime = results.getTimestamp(1);
				String theValue = results.getString(2);
				String battery = results.getString(3);
				String memory = results.getString(4);
				ThingTrustData currentData = new ThingTrustData (theTime, theValue, battery, memory);
				myResult.add(currentData);
			}
			myConn.close();
			logger.debug("Data retreived from " + myResult.size() + " records.");
		}
		catch (Exception ex)
		{
			logger.error("Error retrieving data generated by " + idThing + " during the last 24 hours!");
			logger.error(ex.toString());
			return null;
		}
		
		return myResult;
	}	
	
	public ThingInformation getThingInformation (String idThing)
	{
		logger.debug("Retrieving basic data for thing " + idThing);
		try
		{
			ThingInformation mySearch = new ThingInformation ();
			mySearch.setThingID(idThing);
			return myClient.searchThingInformation(mySearch);
		}
		catch (Exception ex)
		{
			logger.error("An error occurred when retrieving thing data: " + ex.toString());
		}
		return null;
	}
	
	public ArrayList<Double> getThingFailures (String idThing)
	{
		ArrayList<Double> failuresResults = new ArrayList<Double>();
		logger.debug("Retrieving thing failures for " + idThing);
		
		// Extract failures from Dependability Manager and data received
		try
		{
			Connection myConn = myClient.getConnection();
			//TODO Modify statement as soon as the DM specifies the data to be stored
			PreparedStatement myQuery = myConn.prepareStatement("SELECT notification_time FROM T_NOTIFIED_FAILURES WHERE thingID='" + idThing + "'");			
			ResultSet results = myQuery.executeQuery();
			while(results.next())
			{
				// For each failure, find the next time we received data from the thing (that means it was recovered)			   
				String receivedDate = results.getString(1);
				Timestamp failureTime;
				
				//We'll do something here in the next version, for retrieving failures data
				
			}
			myConn.close();			
		}
		catch (Exception ex)
		{
			logger.error("Error retrieving data generated by " + idThing + " during the last 24 hours!");
			logger.error(ex.toString());
			return null;
		}
		
		
		return failuresResults;
	}
	
	public ThingLocation getThingLastLocation (String idThing)
	{
		ThingLocation myResult = null;
		
		// We need to access directly to the connection and send a SQL query to the DB for getting data with location 
		try
		{
			Connection myConn = myClient.getConnection();
			PreparedStatement myQuery = myConn.prepareStatement("SELECT environment, room, floor, city_name, altitude, latitude, longitude, location_keyword, location_identifier FROM T_THING_DATA WHERE thingID='" + idThing + "' ORDER BY timestamp DESC");			
			ResultSet results = myQuery.executeQuery();
			while(results.next())
			{
				// Add all the results to the array list			   
				String environment = results.getString(1);
				String locationKeyword = results.getString(8);
				String locationIdentifier = results.getString(9);
				if (Boolean.parseBoolean(environment))
				{
					String room = results.getString(2);
					String floor = results.getString(3);					
					myResult = new ThingLocation (floor, room, locationKeyword, locationIdentifier);
				}
				else
				{
					String cityName = results.getString(4);
					String altitude = results.getString(5);
					String latitude = results.getString(6);
					String longitude = results.getString(7);
					myResult = new ThingLocation (latitude, longitude, altitude, cityName, locationKeyword, locationIdentifier);
				}				
			}
			myConn.close();
			logger.debug("Data location retreived from " + idThing);
		}
		catch (Exception ex)
		{
			logger.error("Error retrieving data generated by " + idThing + " during the last 24 hours!");
			logger.error(ex.toString());
			return null;
		}
		
		return myResult;
	}
}
