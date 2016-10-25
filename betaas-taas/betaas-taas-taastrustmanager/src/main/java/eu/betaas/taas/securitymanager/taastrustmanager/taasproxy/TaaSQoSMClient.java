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
		return _instance;
	}
	
	public SLACalculation retrieveSLACalculations(String thingServiceId)
	{
		logger.debug("Retrieving SLA data for Thing Services...");
		SLACalculation result = null;
		try
		{				
			result = myClient.calculateSLA(thingServiceId);			
		}
		catch (Exception ex)
		{
			logger.error("An error occurred when retrieving QoS data: " + ex.toString());
		}
		
		return result;
	}
}
