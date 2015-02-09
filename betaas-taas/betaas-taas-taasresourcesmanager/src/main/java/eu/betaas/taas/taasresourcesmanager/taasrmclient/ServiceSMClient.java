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

import eu.betaas.service.servicemanager.api.ServiceManagerInternalIF;
import eu.betaas.taas.taasresourcesmanager.endpointsmanager.FeatureResult;


public class ServiceSMClient 
{
	private ServiceManagerInternalIF myClient;
	private Logger logger= Logger.getLogger("betaas.taas");
	static private ServiceSMClient _instance = null;
	
	private ServiceSMClient ()
	{
		// Retrieve the BundleContext from the OSGi Framework
		BundleContext context = FrameworkUtil.getBundle(ServiceSMClient.class).getBundleContext();
				
		// Open tracker in order to retrieve Service Manager services
		ServiceTracker myTracker = new ServiceTracker(context, ServiceManagerInternalIF.class.getName(), null); 
		myTracker.open();		
		Object [] providers = myTracker.getServices(); 
		
		// Select a provider
		int n = 0;
		if ( providers != null && providers.length > 0 ) 
		{ 		
			logger.debug("Number of providers found for Service Manager: " + providers.length);			
			myClient = (ServiceManagerInternalIF) providers[n];
			logger.info("Service Manager Service found!");
		}
		else
		{
			logger.error("No providers were found for the Service Manager");			
		}
		
		// Close the tracker
		myTracker.close();
	}
	
	public static synchronized ServiceSMClient instance() 
	{
		Logger myLogger= Logger.getLogger("betaas.taas");
		myLogger.info("Obtaining an instance of the Service Manager Client...");
		if (null == _instance) 
		{
			_instance = new ServiceSMClient();				
			if (_instance.myClient == null)
			{
				myLogger.error("No Service Manager client was created!");
				_instance = null;
				return null;
			}
			myLogger.info("A new instance of the Service Manager Client was created!");
		}
		return _instance;
	}
	
	public boolean notifyServiceInstallation (String featureServiceId, String token)
	{
		byte[] tokenBytes = (token != null) ? token.getBytes() : null; 
		myClient.notifyServiceInstallation(featureServiceId,  tokenBytes);
		return true;
	}	
	
	public boolean notifyServiceData (FeatureResult data, String idFeatureService)
	{
		logger.info("Data notification received. Sending to the Service Manager data from " + idFeatureService);		
		return myClient.notifyNewMeasurement(idFeatureService, data.getData());		
	}
	
	public boolean notifySLAData (String idFeatureService)
	{
		logger.info("SLA calculated after invocations. Sending SLA violation to the Service Manager for " + idFeatureService);
		myClient.notifySLAViolation(idFeatureService);
		return true;
	}
}
