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

import java.util.ArrayList;
import java.util.Map;

import org.apache.log4j.Logger;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.util.tracker.ServiceTracker;

import eu.betaas.taas.qosmanager.api.QoSManagerInternalIF;
import eu.betaas.taas.qosmanager.monitoring.api.impl.SLACalculation;

public class TaaSQoSMClient 
{
	private QoSManagerInternalIF myClient;	
	private Logger logger= Logger.getLogger("betaas.taas");
	static private TaaSQoSMClient _instance = null;	
	
	private TaaSQoSMClient ()
	{
		// Retrieve the BundleContext from the OSGi Framework
		BundleContext context = FrameworkUtil.getBundle(TaaSQoSMClient.class).getBundleContext();
				
		// Retrieve the QoS Manager object
		// Open tracker in order to retrieve QoS Manager services
		ServiceTracker myTracker = new ServiceTracker(context, QoSManagerInternalIF.class.getName(), null); 
		myTracker.open();		
		Object [] providers = myTracker.getServices(); 
		
		// Select a provider
		int n = 0;
		if ( providers != null && providers.length > 0 ) 
		{ 		
			logger.debug("Number of providers found for TaaS QoSM: " + providers.length);			
			myClient = (QoSManagerInternalIF) providers[n];
			logger.info("Taas QoS Manager Service found!");
		}
		else
		{
			logger.error("No providers were found for the TaaS QoS Manager");			
		}
		
		// Close the tracker
		myTracker.close();		
	}
	
	public static synchronized TaaSQoSMClient instance() 
	{
		Logger myLogger= Logger.getLogger("betaas.taas");
		myLogger.info("Obtaining an instance of the TaaS QoSM Client...");
		if (null == _instance) 
		{
			_instance = new TaaSQoSMClient();				
			if (_instance.myClient == null)
			{
				myLogger.error("No TaaS QoSM client was created!");
				_instance = null;
				return null;
			}
			myLogger.info("A new instance of the QoS Manager Client was created!");
		}
		myLogger.info("Instance of the QoS Manager Client provided.");
		return _instance;
	}
	
	public ArrayList<String> registerServiceQoSPull(String serviceId, ArrayList<String> thingServicesList, ArrayList< ArrayList<String> > equivalentThingServices)
	{
		logger.info("Invoking 'registerServiceQoS' method before invoking Thing Services...");
		return (ArrayList<String>) myClient.registerServiceQoSPULL(serviceId, thingServicesList, equivalentThingServices);		
	}
	
	public ArrayList<String> registerServiceQoSPush (String serviceId, ArrayList<String> thingServicesList, ArrayList< ArrayList<String> > equivalentThingServices)
	{
		Map<String, Double> pushResult = myClient.registerServiceQoSPUSH(serviceId, thingServicesList, equivalentThingServices);
		ArrayList<String> myResult = new ArrayList<String>();
		myResult.addAll(pushResult.keySet());
		return myResult;
	}
	
	public boolean activateSLAMonitoring(ArrayList<String> selectedThingServicesList)
	{
		logger.debug("Invocation for activating SLA monitoring sent.");
		myClient.getMeasurementSLAMonitoring(selectedThingServicesList);
		logger.debug("Invocation for activating SLA monitoring finished.");
		return true;
	}
	
	public boolean activateSLAPushMonitoring(String thingServiceId, int period)
	{
		logger.debug("Invocation for activating SLA Push monitoring sent.");
		myClient.getMeasurementSLAMonitoring(thingServiceId, period);
		logger.debug("Invocation for activating SLA Push monitoring finished.");
		return true;
	}	
	
	public boolean activateSLAMonitoring(String thingServiceId)
	{
		logger.debug("Invocation for activating SLA monitoring sent.");
		myClient.getMeasurementSLAMonitoring(thingServiceId, -1);
		logger.debug("Invocation for activating SLA monitoring finished.");
		return true;
	}
	
	@Deprecated 
	public ArrayList<SLACalculation> retrieveSLACalculations(ArrayList<String> selectedThingServicesList)
	{
		logger.debug("Invocation for retrieving SLA information sent.");
		ArrayList<SLACalculation> result = myClient.calculateSLA(selectedThingServicesList);
		logger.debug("Invocation for retrieving SLA information finished.");
		return result;
	}
	
	public SLACalculation retrieveSLACalculation(String thingServiceId)
	{
		logger.debug("Invocation for retrieving individual SLA information sent.");
		SLACalculation result = myClient.calculateSLA(thingServiceId);
		logger.debug("Invocation for retrieving individual SLA information finished.");
		return result;
	}
	
	public SLACalculation retrieveSLAPushCalculation(String thingServiceId, int time)
	{
		logger.debug("Invocation for retrieving individual SLA Push information sent.");
		SLACalculation result = myClient.calculateSLAPush(thingServiceId, time);
		logger.debug("Invocation for retrieving individual SLA Push information finished.");
		return result;
	}
	
	public SLACalculation failureSLA(String thingServiceId)
	{
		logger.debug("Invocation for reporting SLA failure sent.");
		SLACalculation result = myClient.failureSLA(thingServiceId);
		logger.debug("Invocation for reporting SLA failure finished.");
		return result;
	}
	
	public void registerThingQoS (String idThing, String idThingService)
	{
		ArrayList<String> thingServicesList = new ArrayList<String> ();
		thingServicesList.add(idThingService);
		
		logger.info("Invoking 'writeThingsServicesQoS' method");
		myClient.writeThingsServicesQoS(thingServicesList);		
	}
	
	public void notifyThingServiceRemoval (String idThingService)
	{
		logger.info("Notifying QoS Manager about thing service removal");
		myClient.thingRemoved(idThingService);
	}
	
	
}
