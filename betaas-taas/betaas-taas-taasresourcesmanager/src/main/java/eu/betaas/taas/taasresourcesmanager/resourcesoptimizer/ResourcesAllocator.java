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

package eu.betaas.taas.taasresourcesmanager.resourcesoptimizer;

import java.util.ArrayList;
import java.util.Iterator;

import org.apache.log4j.Logger;

import eu.betaas.taas.contextmanager.api.impl.ThingServiceList;
import eu.betaas.taas.taasresourcesmanager.api.Feature;
import eu.betaas.taas.taasresourcesmanager.api.Location;
import eu.betaas.taas.taasresourcesmanager.catalogs.Application;
import eu.betaas.taas.taasresourcesmanager.catalogs.ApplicationsCatalog;
import eu.betaas.taas.taasresourcesmanager.catalogs.FeatureService;
import eu.betaas.taas.taasresourcesmanager.catalogs.ResourcesCatalog;
import eu.betaas.taas.taasresourcesmanager.endpointsmanager.EndpointsManager;
import eu.betaas.taas.taasresourcesmanager.taasrmclient.ServiceSECMClient;
import eu.betaas.taas.taasresourcesmanager.taasrmclient.ServiceSMClient;
import eu.betaas.taas.taasresourcesmanager.taasrmclient.TaaSCMClient;
import eu.betaas.taas.taasresourcesmanager.taasrmclient.TaaSQoSMClient;
import eu.betaas.taas.taasresourcesmanager.taasrmclient.TaaSTMClient;


public class ResourcesAllocator 
{
	private TaaSTMClient tmClient;	
	private ApplicationsCatalog appCatalog;
	private ResourcesCatalog resCatalog;
	private TaaSCMClient cmClient;
	private TaaSQoSMClient myQoSClient;
	private String localGateway;
	private Logger logger= Logger.getLogger("betaas.taas");
	private String idDelimiter;
			
	public ResourcesAllocator ()
	{		
		tmClient = TaaSTMClient.instance();				
		cmClient = TaaSCMClient.instance();
		appCatalog = ApplicationsCatalog.instance();
		resCatalog = ResourcesCatalog.instance();	
		myQoSClient = TaaSQoSMClient.instance();
		idDelimiter = "_";		
	}
	
	public ResourcesAllocator (String gatewayId, String delimiter)
	{
		tmClient = TaaSTMClient.instance();				
		cmClient = TaaSCMClient.instance();
		appCatalog = ApplicationsCatalog.instance();
		resCatalog = ResourcesCatalog.instance();	
		myQoSClient = TaaSQoSMClient.instance();
		localGateway = gatewayId;
		idDelimiter = delimiter;
	}
	
	public String allocateResources (Feature appFeature)
	{
		// Check if the application is already registered
		Application currentApp = appCatalog.getApplication(appFeature.getAppId());
		if (currentApp==null)
		{
			// Create a new one
			currentApp = appCatalog.addApplication(appFeature.getAppId());
		}
		
		// Check if the feature is already included in our catalog
		ArrayList<FeatureService> features = currentApp.getFeatures();
		Iterator<FeatureService> myIter = features.iterator();		
		FeatureService myFeature = null;
		while (myIter.hasNext())
		{
			// Check if one of the features is the requested one
			FeatureService currentFeature = myIter.next();
			if (currentFeature.getFeature().equalsIgnoreCase(appFeature.getFeature()) && currentFeature.getLocation().equals(appFeature.getLocation()))
			{
				myFeature = currentFeature;				
				// If the feature exists and the mapping is completed, then we don't re-allocate
				if (currentFeature.isMappingCompleted())
				{
					return currentFeature.getFeatureServiceId();
				}				
			}		
		}
		
		// Create and add the new Feature Service if it was not already in the system
		if (myFeature == null)
		{
			String featServId = appFeature.getAppId() + idDelimiter + appFeature.getFeature() + idDelimiter + appFeature.getLocation().getLocationKeyword();
			myFeature = new FeatureService (appFeature.getFeature(), appFeature.getLocation(), appFeature.getType(), null, featServId, appFeature.getPeriod());
			currentApp.addFeature(myFeature);
			appCatalog.addFeatureService(appFeature.getAppId(), featServId);
		}		
		
		// Start the allocation process
		// Step 1 -> Look for adequate Thing Services in the Context Manager
		ThingServiceList cmProposal = cmClient.getThingServices(appFeature.getFeature(), appFeature.getLocation(), appFeature.getType()+"");
		if (cmProposal == null || cmProposal.getCMThingServicesList()==null)
		{
			logger.error("No Thing Services were found for the required feature!!");
			return null;
		}
		logger.info("Number of Thing Services Retrieved: " + cmProposal.getCMThingServicesList().size());
		myFeature.setThingServices(cmProposal.getCMThingServicesList());
		logger.info("Number of Equivalent groups Retrieved for the app " + appFeature.getAppId() + ": " + cmProposal.getCMThingServicesListEq().size());
		myFeature.setEquivalentThingServices(cmProposal.getCMThingServicesListEq());		
		myFeature.setOperator(cmProposal.getsOperator());
		
		// Step 2 -> Check security constraints (at this stage, only Trust)
		for (int i=0; i<cmProposal.getCMThingServicesListEq().size(); i++)
		{
			ArrayList<String> currentList = cmProposal.getCMThingServicesListEq().get(i);
			for (int j=0; j<currentList.size(); j++)
			{
				double currentTrust = tmClient.getTrust(currentList.get(j));
				if (currentTrust >= appFeature.getTrustValue())
				{
					
				}
			}
		}
		
		
		// Step 3 -> Return the id and wait for the QoS Manager
		logger.info("Id generated for the new feature: " + myFeature.getFeatureServiceId());
		logger.info("Period for the new feature: " + appFeature.getPeriod());
		return myFeature.getFeatureServiceId();
	}
	
	public boolean freeLocalResources (String idApp)
	{
		// Retrieve the Application object with that identifier
		
		// Remove all the Feature Services in the application
		
		// Change status of the resources
		return true;
	}
	
	public ArrayList<ArrayList<String>> getSecurityRank (String idFeatureService)
	{
		// Retrieve the Application object with that Identifier
		String appId = appCatalog.getServiceFromFeatureId(idFeatureService);
		if (appId==null || appId.equalsIgnoreCase(""))
		{
			// That identifier was not recognized and we must finish --> Throw an exception
			logger.error("It was not possible to find the application with id: " + idFeatureService);
			return null;
		}		
				
		Application myApp = appCatalog.getApplication(appId);
		
		// Look for the right Feature Service object and assign the resources
		ArrayList<FeatureService> featuresList = myApp.getFeatures();
		for (int i=0; i<featuresList.size(); i++)
		{
			FeatureService currentFeature = featuresList.get(i);
			if (currentFeature.getFeatureServiceId().equalsIgnoreCase(idFeatureService))
			{
				logger.info("Feature Service found -> Candidate and Equivalent Thing Services sent! (serviceId : " + idFeatureService + " - size: " + currentFeature.getEquivalents().size() + ")");
				ArrayList<ArrayList<String>> result = currentFeature.getEquivalents();
				if (result==null)
				{
					result = new ArrayList<ArrayList<String>>();
				}
				ArrayList<String> candidates = currentFeature.getThingServices();
				for (int j=0; j<candidates.size(); j++)
				{
					result.get(j).add(0, candidates.get(j));
				}				
				
				printThingServicesMatrix (result);
				
				return result;
			}
		}
		
		logger.error("Given feature associated to the application id was found!");
		return null;
	}
	
	public void putQoSRank(String serviceID, ArrayList<ArrayList<String>> equivalentThingServicesQoSRank)
	{
		// Retrieve the Application object with that Identifier
		String appId = appCatalog.getServiceFromFeatureId(serviceID);
		if (appId==null || appId.equalsIgnoreCase(""))
		{
			// That identifier was not recognized and we must finish --> Throw an exception
			logger.error("It was not possible to find the application with id: " + serviceID);
			return;
		}
		
		Application myApp = appCatalog.getApplication(appId);
		
		// Look for the right Feature Service object and assign the resources
		ArrayList<FeatureService> featuresList = myApp.getFeatures();
		for (int i=0; i<featuresList.size(); i++)
		{
			FeatureService currentFeature = featuresList.get(i);
			if (currentFeature.getFeatureServiceId().equalsIgnoreCase(serviceID))
			{
				// Update list of equivalent thing services
				logger.info("Updating the list of equivalent thing services for the service " + serviceID + " with " + equivalentThingServicesQoSRank.size() + " more thing services");
				currentFeature.setEquivalentThingServices(equivalentThingServicesQoSRank);
				int featureType = currentFeature.getType();
				
				// For the thing services list, take the ones in the first position
				ArrayList<String> finalServices = new ArrayList<String>();
				for (int j=0; j<equivalentThingServicesQoSRank.size(); j++)
				{
					// Get the first thing service to be invoked
					String tsIdentifier = equivalentThingServicesQoSRank.get(j).get(0);
					finalServices.add(tsIdentifier);
					
					// Notify resource allocation (only NRT PULL mode, since it doesn't reallocate continuously)
					// PUSH modes are notified when the subscription is requested, for efficiency purposes
					if (featureType==FeatureService.NRTPULL)
					{
						resCatalog.getResource(tsIdentifier).addFeature(currentFeature.getFeatureServiceId());
					}										
				}
				currentFeature.setThingServices(finalServices);
				
				// Notify the Service Manager that the installation process was finished
				if (currentFeature.isMappingCompleted())
				{
					// Request the security token to the SECM
					ServiceSECMClient secCli = ServiceSECMClient.instance();
					String tokenReceived = secCli.getFeatureToken(appId, currentFeature.getThingServices());
					
					// Send notification to the SM
					ServiceSMClient mySMClient = ServiceSMClient.instance();
					mySMClient.notifyServiceInstallation(serviceID, tokenReceived);
				}
								
				return;
			}
		}
		
		
	}
	
	public void updateFeatureLocation (String idFeatureService, Location newLocation)
	{
		// Retrieve the Application object with that Identifier
		String appId = appCatalog.getServiceFromFeatureId(idFeatureService);
		if (appId==null || appId.equalsIgnoreCase(""))
		{
			// That identifier was not recognized and we must finish --> Throw an exception
			logger.error("It was not possible to find the application with id: " + idFeatureService);
			return;
		}		
				
		Application myApp = appCatalog.getApplication(appId);
		// Look for the right Feature Service object and assign the resources
		ArrayList<FeatureService> featuresList = myApp.getFeatures();
		for (int i=0; i<featuresList.size(); i++)
		{
			FeatureService currentFeature = featuresList.get(i);
			if (currentFeature.getFeatureServiceId().equalsIgnoreCase(idFeatureService))
			{
				// Start the allocation process
				// Step 1 -> Look for adequate Thing Services in the Context Manager
				ThingServiceList cmProposal = cmClient.getThingServices(currentFeature.getFeature(), newLocation, currentFeature.getType()+"");
				
				if (cmProposal == null || cmProposal.getCMThingServicesList()==null)
				{
					logger.error("No Thing Services were found for the required feature update!!");
					return;
				}
				
				logger.info("Number of Thing Services Retrieved: " + cmProposal.getCMThingServicesList().size());
				currentFeature.setThingServices(cmProposal.getCMThingServicesList());
				logger.info("Number of Equivalent groups Retrieved for the app " + appId + ": " + cmProposal.getCMThingServicesListEq().size());
				currentFeature.setEquivalentThingServices(cmProposal.getCMThingServicesListEq());		
				currentFeature.setOperator(cmProposal.getsOperator());
								
				// Step 2 -> Check security constraints (at this stage, only Trust)
				ArrayList<ArrayList<String>> clonedEquivalents = new ArrayList<ArrayList<String>>();
				for (int j=0; j<cmProposal.getCMThingServicesListEq().size(); j++)
				{
					ArrayList<String> currentList = cmProposal.getCMThingServicesListEq().get(j);
					ArrayList<String> clonedList = new ArrayList<String>();
					for (int h=0; h<currentList.size(); h++)
					{
						double currentTrust = tmClient.getTrust(currentList.get(h));
						if (currentTrust >= currentFeature.getRequiredTrust())
						{
							//If the thing service trust is OK, then add it to our new list
							clonedList.add(currentList.get(h));
						}
					}
					clonedEquivalents.add(clonedList);
				}
				//Modify the list of equivalent thing services
				currentFeature.setEquivalentThingServices(clonedEquivalents);
				
				//Step 3 -> Invoke the QoS Manager in order to update the list again (if Real-Time exec.)
				if (currentFeature.getType()==FeatureService.RTPUSH)
				{
					//Update the list of thing services
					//TODO this call should be to the QoSPush method
					ArrayList<String> thingServList = myQoSClient.registerServiceQoSPull(idFeatureService, currentFeature.getThingServices(), currentFeature.getEquivalents());
					currentFeature.setThingServices(thingServList);
					logger.info("List of Thing Services updated with the QoS Manager!");
					
					//Step 4 -> Re-subscribe if previous subscriptions were active
					EndpointsManager invokator = new EndpointsManager(localGateway);
					if (invokator.unsubscribeFeatureService(idFeatureService))
					{
						invokator.subscribeFeatureService(idFeatureService);
					}					
					logger.info("List of Thing Services subscriptions updated!");
					
				}
				else if (currentFeature.getType()==FeatureService.RTPULL)
				{
					ArrayList<String> thingServList = myQoSClient.registerServiceQoSPull(idFeatureService, currentFeature.getThingServices(), currentFeature.getEquivalents());
					currentFeature.setThingServices(thingServList);
					logger.info("List of Thing Services updated with the QoS Manager!");
				}
				
				return;
			}
		}				
				
	}
	
	public void revokeService (String idFeatureService)
	{
		// Retrieve the Application object with that Identifier
		String appId = appCatalog.getServiceFromFeatureId(idFeatureService);
		if (appId==null || appId.equalsIgnoreCase(""))
		{
			// That identifier was not recognized and we must finish --> Throw an exception
			logger.error("It was not possible to find the application with id: " + idFeatureService);
			return;
		}		
						
		Application myApp = appCatalog.getApplication(appId);
		// Look for the right Feature Service object and assign the resources
		ArrayList<FeatureService> featuresList = myApp.getFeatures();
		for (int i=0; i<featuresList.size(); i++)
		{
			FeatureService currentFeature = featuresList.get(i);
			if (currentFeature.getFeatureServiceId().equalsIgnoreCase(idFeatureService))
			{
				featuresList.remove(i);
				logger.info("Feature " + idFeatureService + " could not be installed. Removed!");
				return;
			}
		}
		
		logger.error("Feature " + idFeatureService + " could not be found for its removal!");
	}
	
	private void printThingServicesMatrix(ArrayList<ArrayList<String>> input)
	{
		input.trimToSize();
		String content = "[";
		
		for (int i=0; i<input.size(); i++)
		{
			input.get(i).trimToSize();
			content = content + "[";
			for (int j=0; j<input.get(i).size(); j++)
			{
				content = content + input.get(i).get(j);
				if (j<input.get(i).size()-1)
				{
					content = content + ", ";
				}
			}
			content = content + "]";
		}
		
		content = content + "]";
		logger.info("Matrix Sent: " + content);
		//System.out.println(content);
	}
	
	/*
	public static void main(String args[]) 
	{
		ArrayList<ArrayList<String>> myArray = new ArrayList<ArrayList<String>>();
		ArrayList<String> array1 = new ArrayList<String>();
		array1.add("Javi1");
		array1.add("Javi2");
		
		ArrayList<String> array2 = new ArrayList<String>();
		array2.add("Javi1");
		array2.add("Javi2");
		
		ArrayList<String> array3 = new ArrayList<String>();
		array3.add("Javi1");
		array3.add("Javi2");
		array3.add("Javi3");
		
		myArray.add(array1);
		myArray.add(array2);
		myArray.add(array3);
		
		ResourcesAllocator javi = new ResourcesAllocator();
		javi.printThingServicesMatrix(myArray);
	}
	*/
}
