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

package eu.betaas.taas.taasresourcesmanager.taasrmclient;

import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.util.tracker.ServiceTracker;

import eu.betaas.service.securitymanager.service.IAuthorizationService;

public class ServiceSECMClient 
{
	private IAuthorizationService myClient;
	private Logger logger= Logger.getLogger("betaas.taas");
	static private ServiceSECMClient _instance = null;
	
	private ServiceSECMClient ()
	{
		// Retrieve the BundleContext from the OSGi Framework
		BundleContext context = FrameworkUtil.getBundle(ServiceSECMClient.class).getBundleContext();
				
		// Open tracker in order to retrieve Service Manager services
		ServiceTracker myTracker = new ServiceTracker(context, IAuthorizationService.class.getName(), null); 
		myTracker.open();		
		Object [] providers = myTracker.getServices(); 
		
		// Select a provider
		int n = 0;
		if ( providers != null && providers.length > 0 ) 
		{ 		
			logger.debug("Number of providers found for Security Manager: " + providers.length);			
			myClient = (IAuthorizationService) providers[n];
			logger.info("Security Manager Service found!");
		}
		else
		{
			logger.error("No providers were found for the Security Manager");			
		}
		
		// Close the tracker
		myTracker.close();
	}
	
	public static synchronized ServiceSECMClient instance() 
	{
		Logger myLogger= Logger.getLogger("betaas.taas");
		myLogger.info("Obtaining an instance of the Security Manager Client...");
		if (null == _instance) 
		{
			_instance = new ServiceSECMClient();				
			if (_instance.myClient == null)
			{
				myLogger.error("No Security Manager client was created!");
				_instance = null;
				return null;
			}
			myLogger.info("A new instance of the Security Manager Client was created!");
		}
		return _instance;
	}
	
	public boolean verifyToken (String theToken)
	{	
		
		boolean verified = false;
		try
		{			
			verified = myClient.verifyToken(theToken);
		}
		catch (Exception ex)
		{
			logger.error ("There was an error when validating a token! " + ex.toString());
		}
		
		return verified;		
	}
	
	public String getFeatureToken (String featureServiceId, ArrayList<String> thingServicesList)
	{
		
		String myToken = null;
		try
		{
			logger.debug("Thing Services received: " + thingServicesList.size());
			thingServicesList.trimToSize();
			String [] myTSList = new String[thingServicesList.size()];
			thingServicesList.toArray(myTSList);
			logger.info("Ready to request the token. Requesting...");
			myToken = myClient.getTokenApp(myTSList, "APPS", featureServiceId);
			logger.info("Token received!");			
		}
		catch (Exception ex)
		{
			logger.error ("There was an error when requesting the token! " + ex.toString());
		}
		return myToken;			
	}
}
