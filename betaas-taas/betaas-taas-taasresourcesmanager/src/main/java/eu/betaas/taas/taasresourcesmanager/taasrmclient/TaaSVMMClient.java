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

import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.util.tracker.ServiceTracker;

import eu.betaas.taas.taasvmmanager.api.TaaSVMManager;
import eu.betaas.taas.taasvmmanager.api.datamodel.Availability;
import eu.betaas.taas.taasvmmanager.api.datamodel.InstanceType;
import eu.betaas.taas.taasvmmanager.api.datamodel.VMRequest;

public class TaaSVMMClient 
{
	private TaaSVMManager myClient;
	private Logger logger= Logger.getLogger("betaas.taas");
	static private TaaSVMMClient _instance = null;
	
	private TaaSVMMClient ()
	{
		// Retrieve the BundleContext from the OSGi Framework
		BundleContext context = FrameworkUtil.getBundle(TaaSVMMClient.class).getBundleContext();
				
		// Open tracker in order to retrieve VM Manager services
		ServiceTracker myTracker = new ServiceTracker(context, TaaSVMManager.class.getName(), null); 
		myTracker.open();		
		Object [] providers = myTracker.getServices(); 
		
		// Select a provider
		int n = 0;
		if ( providers != null && providers.length > 0 ) 
		{ 		
			logger.info("Number of providers found for TaaS VMM: " + providers.length);			
			myClient = (TaaSVMManager) providers[n];
			logger.debug("Taas VM Manager Service found!");
		}
		else
		{
			logger.error("No providers were found for the TaaS VM Manager");			
		}
		
		// Close the tracker
		myTracker.close();
	}
	
	public static synchronized TaaSVMMClient instance() 
	{
		Logger myLogger= Logger.getLogger("betaas.taas");
		myLogger.info("Obtaining an instance of the TaaS VMM Client...");
		if (null == _instance) 
		{
			_instance = new TaaSVMMClient();				
			if (_instance.myClient == null)
			{
				myLogger.error("No TaaS VMM client was created!");
				_instance = null;
				return null;
			}
			myLogger.info("A new instance of the VM Manager Client was created!");
		}
		return _instance;
	}
	
	public String createLocalVM(VMRequest request)
	{
		logger.info("Local VM requested!");		
		try {
			return myClient.createVM(request);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	public void getCurrentResources ()
	{
		logger.info("Retrieve available resources for VM allocation");
		List<Availability> result = myClient.getAvailability();
	}
}
