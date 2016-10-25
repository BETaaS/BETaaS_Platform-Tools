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

// BETaaS - Building the Environment for the Things as a Service
//
// Component: TaaSResourceManager
// Responsible: Atos

package eu.betaas.taas.taasresourcesmanager.taasrmclient;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

import eu.betaas.taas.bigdatamanager.database.service.ThingsData;
import eu.betaas.taas.taasresourcesmanager.api.ResourceInfo;
import eu.betaas.taas.taasresourcesmanager.api.TaaSResourceManagerExt;
import eu.betaas.taas.taasresourcesmanager.api.ThingServiceResult;
import eu.betaas.taas.taasresourcesmanager.catalogs.Resource;
import eu.betaas.taas.taasresourcesmanager.catalogs.ResourcesCatalog;

/**
* 
* @author Francisco Javier Nieto De-Santos (francisco.nieto@atos.net)
* @author Sergio Garcia Villalonga (sergio.garciavillalonga@atos.net)
*/
public class TaaSRMClient 
{
	private TaaSResourceManagerExt myClient;
	private static Logger logger= Logger.getLogger("betaas.taas");
	private String gatewayId;
	private String thisGateway;
	private BundleContext dContext;
	private HashMap<String, TaaSResourceManagerExt> clientsList;
	private static TaaSRMClient _instance = null;
	private ServiceTracker myTracker;
		
	private TaaSRMClient (String localGw, String caller)
	{
		// Retrieve the BundleContext and the local gateway property
		dContext = FrameworkUtil.getBundle(TaaSRMClient.class).getBundleContext();
		thisGateway = localGw;
		clientsList = new HashMap<String, TaaSResourceManagerExt>();
		logger.info("[" + caller + "] TaaSRM Client created for gateway " + localGw);
		
		// Open tracker in order to retrieve all TaaS Resource Manager services references in the instance
		myTracker = new ServiceTracker(dContext, TaaSResourceManagerExt.class.getName(), null); 
		logger.debug("[" + caller + "] Opening tracker");
		myTracker.open();
		logger.debug("[" + caller + "] Tracker opened");
	}
	
	public static synchronized TaaSRMClient instance(String localGw, String caller) 
	{
		//Logger myLogger= Logger.getLogger("betaas.taas");
		logger.info("[" + caller + "] Obtaining an instance of the TaaS RM Client...");
		if (null == _instance) 
		{
			_instance = new TaaSRMClient(localGw, caller);
			if (_instance.thisGateway == null)
			{
				logger.error("[" + caller + "] No TaaS RM client was created, as the local GW ID is null!");
				_instance = null;
				return null;
			}
			logger.info("[" + caller + "] A new instance of the TaaS Resources Manager Client was created!");
		} else {
			logger.info("[" + caller + "] An instance of the TaaS Resources Manager Client was already created!");
		}
		return _instance;
	}
	
	private TaaSResourceManagerExt findRemoteTaaSRM (String gateway)
	{					
		// Open tracker in order to retrieve TaaS Resource Manager services		
		logger.info("Looking for gateway " + gateway + " TaaSRM service...");
		Object [] providers=null;
		try
		{
			// Retrieve the services filtered by gateway
			ServiceTracker myTracker = new ServiceTracker(dContext, dContext.createFilter("(&(objectClass=" + TaaSResourceManagerExt.class.getName() + ")" +
					"(gatewayId=" + gateway + "))"), null);
			myTracker.open();
			
			try {
				  Thread.sleep(2000);
				} catch(InterruptedException ex) {
				    Thread.currentThread().interrupt();
			}
						
			providers = myTracker.getServices(); 
			
			// Close the tracker
			myTracker.close();
		}
		catch (Exception ex)
		{
			logger.error("Error when creating the tracker: " + ex.getMessage());
			return null;
		}		
						
		// Select a provider
		int n = 0;
		if ( providers != null && providers.length > 0 ) 
		{ 		
			logger.debug("Number of providers found for TaaSRM: " + providers.length);	
			for (int i=0; i<providers.length; i++)
			{
				myClient = (TaaSResourceManagerExt) providers[n];
				clientsList.put(gateway, myClient);
				logger.info("Taas Resources Manager Service found for the gateway" + gatewayId + "!");
				return myClient;
			}			
		}
		else
		{
			logger.error("No providers were found for remote TaaSRM at gateway " + gatewayId);				
		}			
		return null;
	}
	
	public boolean synchronizeThingServices() 
	{		
		logger.info ("Sending Thing Services to remote TaaSRMs...");
		
		// 1- Retrieve the local list of resources
		ResourcesCatalog myResCatalog = ResourcesCatalog.instance();
		ArrayList<ResourceInfo> localRes = myResCatalog.getResourcesForSynchronizing();
		
		// 2- Prepare our local resources to be sent
		logger.debug("Sending list of resources...");
		ResourceInfo [] myResList = null;
		if (localRes!=null)
		{
			localRes.trimToSize();
			myResList = new ResourceInfo[localRes.size()];
			localRes.toArray(myResList);
		}
				
		// 3- Retrieve all the TaaSRMs in the Instance and synchronize		
		// Clean the list of known remote TaaSRMs
		clientsList = new HashMap<String, TaaSResourceManagerExt>();		
		ServiceReference [] providers = myTracker.getServiceReferences(); 		
				
		// Check if service references were found
		if ( providers != null && providers.length > 0 ) 
		{ 		
			logger.info("Number of providers found for TaaSRM: " + providers.length);	
			// 4- Synchronize one by one, except to the current gateway
			for (int i=0; i<providers.length; i++)
			{
				String providerId = (String)providers[i].getProperty("gatewayId");
				if (!providerId.equalsIgnoreCase(thisGateway))
				{
					// Send our resources to the remote RM
					TaaSResourceManagerExt currentClient = (TaaSResourceManagerExt)dContext.getService(providers[i]);
					ResourceInfo[] remoteRes = currentClient.synchronizeThingServices(myResList, thisGateway);
					
					// Register the remote resources
					if (remoteRes==null || remoteRes.length<=0)
					{
						logger.warn("List sent by " + providerId + " is empty!");
					}
					else
					{
						logger.info("Adding remote resources...");
						for (int j=0; j<remoteRes.length; j++)
						{
							ResourceInfo current = remoteRes[j];
							Resource newRes = new Resource (current.getResourceId(), current.getPhysicalResourceId(), current.getResourceType(), providerId);
							newRes.setStatus(current.getStatus());
							myResCatalog.addResource(newRes);
							logger.debug("Resource " + newRes.getResourceId() + " added.");
						}
					}					
					logger.info("Taas RM at " + providerId + " synchronized!");
					
					// Include the current TaaSRM in the list of known clients
					clientsList.put(providerId, currentClient);
				}	
				else
				{
					logger.debug("Gateway " + providerId + " found. Skipping...");
				}
			}			
		}
		else
		{
			logger.error("No providers were found for remote TaaSRM");
			return false;
		}				
			
		logger.info("Remote synchronization finished!");
		return true;
	}
	
	public ThingServiceResult remoteInvocation (String idThingService, String gateway, boolean realTime)
	{	
		// Try to recover the client from the known list, to save time
		TaaSResourceManagerExt remoteClient=clientsList.get(gateway);
		if (remoteClient==null)
		{
			// Try to find the client actively
			remoteClient = findRemoteTaaSRM(gateway);
			if (remoteClient==null)
			{
				logger.error("No TaaSRM was found for the required gateway!");
				return null;
			}
		}		
		logger.info("Invoking remote Thing Service" + idThingService +  " at " + gateway);
		return remoteClient.getRemoteData(idThingService, realTime);
	}
	
	public boolean remoteSubscription (String idThingService, String gateway, String idApplication, int period, boolean realTime)
	{
		// Try to recover the client from the known list, to save time
		TaaSResourceManagerExt remoteClient=clientsList.get(gateway);
		if (remoteClient==null)
		{
			// Try to find the client actively
			remoteClient = findRemoteTaaSRM(gateway);
			if (remoteClient==null)
			{
				logger.error("No TaaSRM was found for the required gateway!");
				return false;
			}
		}		
		
		logger.info("Invoking remote subscription for Thing Service" + idThingService +  " at " + gateway);
		return remoteClient.remoteSubscription(idThingService, idApplication, period, realTime, thisGateway);		
	}
	
	public boolean remoteUnsubscription (String idThingService, String idFeature, String gateway, boolean realTime)
	{
		// Try to recover the client from the known list, to save time
		TaaSResourceManagerExt remoteClient=clientsList.get(gateway);
		if (remoteClient==null)
		{
			// Try to find the client actively
			remoteClient = findRemoteTaaSRM(gateway);
			if (remoteClient==null)
			{
				logger.error("No TaaSRM was found for the required gateway!");
				return false;
			}
		}		
		
		logger.info("Invoking remote unsubscription for Thing Service" + idThingService +  " at " + gateway);
		return remoteClient.remoteUnsubscription(idThingService, idFeature, realTime);		
	}
	
	public boolean remoteDataNotification (ThingsData data, String idFeature, String idThingService, String remoteGW)
	{
		// Try to recover the client from the known list, to save time
		TaaSResourceManagerExt remoteClient=clientsList.get(remoteGW);
		if (remoteClient==null)
		{
			// Try to find the client actively
			remoteClient = findRemoteTaaSRM(remoteGW);
			if (remoteClient==null)
			{
				logger.error("No TaaSRM was found for the required gateway!");
				return false;
			}
		}		
				
		logger.info("Send remote notification for Thing Service" + idThingService +  " at " + remoteGW);
		logger.debug("Data to be sent is: " + data.getMeasurement());
		return remoteClient.remoteDataNotification(data.getData(), idFeature, idThingService);
	}
	
	public boolean remoteSetInvocation (String idThingService, String gateway, String value)
	{	
		// Try to recover the client from the known list, to save time
		TaaSResourceManagerExt remoteClient=clientsList.get(gateway);
		if (remoteClient==null)
		{
			// Try to find the client actively
			remoteClient = findRemoteTaaSRM(gateway);
			if (remoteClient==null)
			{
				logger.error("No TaaSRM was found for the required gateway!");
				return false;
			}
		}		
		logger.info("Invoking remote set data Thing Service" + idThingService +  " at " + gateway);
		return remoteClient.setRemoteData(idThingService, value);
	}		
	
	public boolean registerThingAllRemote(Resource theResource)
	{
		// 1- Prepare data
		String myThingServiceId = theResource.getResourceId();
		String myThingId = theResource.getPhysicalResourceId();
		logger.info("Broadcasting the register of a new resource...");
		
		// 2- Retrieve providers
		// Retrieve the BundleContext from the OSGi Framework		
		BundleContext context = dContext;
		
		// Clean the list of known remote TaaSRMs
		clientsList = new HashMap<String, TaaSResourceManagerExt>();
		
		// Open tracker in order to retrieve all TaaS Resource Manager services references in the instance
		ServiceTracker myTracker = new ServiceTracker(context, TaaSResourceManagerExt.class.getName(), null); 
		myTracker.open();
		ServiceReference [] providers = myTracker.getServiceReferences(); 
		
		// Check if service references were found
		if ( providers != null && providers.length > 0 ) 
		{ 		
			logger.debug("Number of providers found for TaaSRM: " + providers.length);	
			// 3- Notify one by one, except to the current gateway
			for (int i=0; i<providers.length; i++)
			{
				String providerId = (String)providers[i].getProperty("gatewayId");
				if (!providerId.equalsIgnoreCase(thisGateway))
				{
					TaaSResourceManagerExt currentClient = (TaaSResourceManagerExt)context.getService(providers[i]);
					currentClient.registerThingsService(myThingId, myThingServiceId, thisGateway);
					logger.info("Taas RM at " + providerId + " notified for a new resource!");
					
					// Include the current TaaSRM in the list of known clients
					clientsList.put(providerId, currentClient);
				}
				else
				{
					logger.info("Gateway " + providerId + " found. Skipping...");
				}
			}			
		}
		else
		{
			logger.error("No providers were found for remote TaaSRM");
			return false;
		}
		
		// 4- Close the tracker
		myTracker.close();		
		logger.info("All the gateways were notified.");
		return true;
	}
	
	public boolean removeThingAllRemote(String thingServiceID)
	{		
		logger.info("Broadcasting the removal of a Thing Service...");
		
		// 1- Retrieve providers
		// Retrieve the BundleContext from the OSGi Framework		
		BundleContext context = dContext;
		
		// Clean the list of known remote TaaSRMs
		clientsList = new HashMap<String, TaaSResourceManagerExt>();
		
		// Open tracker in order to retrieve all TaaS Resource Manager services references in the instance
		ServiceTracker myTracker = new ServiceTracker(context, TaaSResourceManagerExt.class.getName(), null); 
		myTracker.open();
		ServiceReference [] providers = myTracker.getServiceReferences(); 
		
		// Check if service references were found
		if ( providers != null && providers.length > 0 ) 
		{ 		
			logger.debug("Number of providers found for TaaSRM: " + providers.length);	
			// 3- Notify one by one, except to the current gateway
			for (int i=0; i<providers.length; i++)
			{
				String providerId = (String)providers[i].getProperty("gatewayId");
				if (!providerId.equalsIgnoreCase(thisGateway))
				{
					TaaSResourceManagerExt currentClient = (TaaSResourceManagerExt)context.getService(providers[i]);
					currentClient.removeThingsService(thingServiceID, thisGateway);
					logger.info("Taas RM at " + gatewayId + " notified for resource removal!");
					
					// Include the current TaaSRM in the list of known clients
					clientsList.put(providerId, currentClient);
				}				
			}			
		}
		else
		{
			logger.error("No providers were found for remote TaaSRM");
			return false;
		}
		
		// 4- Close the tracker
		myTracker.close();		
		logger.info("All the gateways were notified.");
		return true;
	}
}
