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

import eu.betaas.taas.securitymanager.taastrustmanager.api.TaaSTrustManager;

public class TaaSTMClient 
{
	private TaaSTrustManager myClient;
	private Logger logger= Logger.getLogger("betaas.taas");
	static private TaaSTMClient _instance = null;
	
	private TaaSTMClient ()
	{
		// Retrieve the BundleContext from the OSGi Framework
		BundleContext context = FrameworkUtil.getBundle(TaaSTMClient.class).getBundleContext();
		
		// Open tracker in order to retrieve Trust Manager services
		ServiceTracker myTracker = new ServiceTracker(context, TaaSTrustManager.class.getName(), null); 
		myTracker.open();		
		Object [] providers = myTracker.getServices(); 
		
		// Select a provider
		int n = 0;
		if ( providers != null && providers.length > 0 ) 
		{ 		
			logger.debug("Number of providers for the TaaS TM: " + providers.length);
			myClient = (TaaSTrustManager) providers[n];	
			logger.info("Taas Trust Manager Service found!");
		}
		else
		{
			logger.error("No providers were found for the TaaS Trust Manager");			
		}
		
		// Close the tracker
		myTracker.close();
	}
	
	public static synchronized TaaSTMClient instance() 
	{
		Logger myLogger= Logger.getLogger("betaas.taas");
		myLogger.info("Obtaining an instance of the TaaS TM Client...");
		if (null == _instance) 
		{
			_instance = new TaaSTMClient();			
			if (_instance.myClient == null)
			{
				myLogger.error("No TaaS TM client was created!");
				_instance=null;
				return null;
			}
			myLogger.info("A new instance of the Trust Manager Client was created!");
		}
		return _instance;
	}
	
	public double getTrust (String idThingsService)
	{
		return myClient.getTrust(idThingsService);		
	}
	
	public boolean registerThingsService (String idThingsService)
	{
		return myClient.registerThingsService(idThingsService);
	}
	
	public boolean removeThingsService (String idThingsService)
	{
		return myClient.removeThingsService(idThingsService);
	}
}
