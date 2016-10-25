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

package eu.betaas.service.securitymanager.trustmanager.servicethread;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import eu.betaas.service.securitymanager.trustmanager.serviceaggregator.GatewayTrust;
import eu.betaas.service.securitymanager.trustmanager.serviceaggregator.GatewayTrustCalculator;
import eu.betaas.service.securitymanager.trustmanager.serviceproxy.TaaSCMClient;

public class TrustServiceThread 
{
	// A handle to the unique Singleton instance.
	private Logger logger= Logger.getLogger("betaas.service");
	private final ScheduledExecutorService scheduler;
	private static TrustServiceThread _instance = null;
	//private long interval = 300;
	private long interval = 200;
	private long initialDelay = 30;
	//final TaaSMonitoringThread myTask;
	private volatile TaaSMonitoringThread myTask;
	
	private TrustServiceThread() 
	{
		/*
		// Configure interval and initial delay
		PropertyConfigurator.configure(PropertiesUtils.getLogConfig());
		interval = Long.valueOf(PropertiesUtils.getProperty("TRUST","interval"));
		initialDelay = Long.valueOf(PropertiesUtils.getProperty("TRUST","initial.delay"));
		*/
		
		// Build elements for thread and tasks scheduling
		scheduler = Executors.newScheduledThreadPool(1);
		myTask = new TaaSMonitoringThread();
		scheduler.scheduleWithFixedDelay(myTask, initialDelay, interval, TimeUnit.SECONDS);		
	}
	
	public static synchronized TrustServiceThread instance() {
		if (null == _instance) 
		{
			_instance = new TrustServiceThread();
			Logger log= Logger.getLogger("betaas.service");
			log.info ("A new instance of the Trust thread was created!");
		}
		return _instance;
	}
	
	public void stopThread ()
	{
		logger.info("Destroying the trust thread...");
		scheduler.shutdown();
		myTask=null;
	}
	
	class TaaSMonitoringThread extends Thread 
	{		
		private Logger logger= Logger.getLogger("betaas.service");
		private ArrayList<String> thingsServList;
		private ArrayList<String> priorityList;
		private int iterations;
				
		public TaaSMonitoringThread() 
		{
			thingsServList = new ArrayList<String>();
			priorityList = new ArrayList<String>();
			getActiveThingsServices();
			iterations = 0;
		}
		
		public void run() 
		{			
			try
			{
				logger.info ("Calculating trust...");
				
				// Create the Trust Calculator to be used
				GatewayTrustCalculator myCalculator = new GatewayTrustCalculator ();
				
				// Perform trust calculation for the priority list
				logger.info("Priority Things Services: " + priorityList.size());
								
				iterations ++;
				//if (iterations==12)
				if (iterations==1)
				{
					logger.info("Rest of Active Things Services: " + thingsServList.size());
					getActiveThingsServices();
					// Calculate trust for the non-priority list
					for (int i=0; i<thingsServList.size(); i++)
					{
						logger.info("Calculating trust for thing service: " + thingsServList.get(i));
						GatewayTrust trustResult = myCalculator.calculateThingServiceTrust(thingsServList.get(i));
						logger.info("Value calculated: " + trustResult.getGatewayTrust());
					}								
					
					iterations = 0;
				}
				
				// Perform trust calculation for the priority list
				logger.info("Active Things Services: " + priorityList.size());
			}
			catch (Exception ex)
			{
				logger.error("Failure when recalculating Trust for Things Services!");
				logger.error(ex.getMessage());
				if (iterations==1) iterations=0;
			}			
			
			logger.info ("Trust thread finished!");
		}
		
		private void getActiveThingsServices() 
		{
			// Retrieve list of active Things Services in the BETaaS Instance/Gateway
			ArrayList<String> fullThingsServList = new ArrayList<String>();
			thingsServList = new ArrayList<String>();
			
			//Load the list of Thing Services from the Context Manager
			TaaSCMClient myCM = TaaSCMClient.instance();
			fullThingsServList = myCM.getThingServices();
			logger.debug("Total thing services available: " + fullThingsServList.size());
			
			//Take one by one each Thing Service and check they aren't contained in the priority list.
			//Add to the thingsServList only those which are not in the priority list			
			for (int i=0; i<fullThingsServList.size(); i++)
			{
				String currentTS = fullThingsServList.get(i);
				if (!priorityList.contains(currentTS))
				{
					logger.debug("Thing Service added to the normal list: " + currentTS);
					thingsServList.add(currentTS);
				}
			}			
		}
		
		/*
		private void testingTrust ()
		{			
			// Initialize external clients
			TaaSBDMClient bdmClient = TaaSBDMClient.instance();
			//TaaSCMClient cmClient = TaaSCMClient.instance();
			TaaSQoSMClient qosmClient = TaaSQoSMClient.instance(); 
			
			ArrayList<GatewayTrustData> thingsList = bdmClient.getThingData("6672338-3086729-1249391281205");
			logger.info ("Thing Data in the list received: " + thingsList.size());
			for (int i=0; i<thingsList.size(); i++)
			{
				logger.info("Thing Data " + i + ": ");
				logger.info("Timestamp: " + thingsList.get(i).getTimestamp());
				logger.info("Measurement: " + thingsList.get(i).getMeasurement());
				logger.info("Battery: " + thingsList.get(i).getBatteryLevel());
				logger.info("Memory: " + thingsList.get(i).getMemoryLevel());
			}
			
			/*
			ArrayList<String> tsList = cmClient.getThingServices();
			logger.info ("Thing Services in the list received: " + tsList.size());
			for (int i=0; i<tsList.size(); i++)
			{
				logger.info("Thing Service " + i + ": " + tsList.get(i));
			}
			*/
				
			/*
			GatewayTrust myPru = new GatewayTrust ("6672338-3086729-1249391281205", 2.5, 4.75, 2.5, 2.5, 4.0, 4.25, 3.80);
			bdmClient.storeTrustData(myPru);
			GatewayTrust myPru2 = new GatewayTrust ("6672338-3086729-1249391281205", 2.5, 4.0, 2.5, 2.5, 3.7, 3.25, 3.05);
			bdmClient.storeTrustData(myPru2);	
			*/	
			/*
			
			logger.info("Units used in thing " + "6672338-3086729-1249391281205" + " are: " + bdmClient.getThingInformation("6672338-3086729-1249391281205").getUnit());
			
			logger.info("QoS Info for Thing Service: " + "set3GardenValve_6672338-3086729-1249391281205_01");
			ArrayList<String> myTSs = new ArrayList<String>();
			myTSs.add("set3GardenValve_6672338-3086729-1249391281205_01");
			ArrayList<SLACalculation> myQoS = qosmClient.retrieveSLACalculations(myTSs);
			logger.info("SLA Calculations obtained: " + myQoS.size());
		}
		*/
	}	
}
