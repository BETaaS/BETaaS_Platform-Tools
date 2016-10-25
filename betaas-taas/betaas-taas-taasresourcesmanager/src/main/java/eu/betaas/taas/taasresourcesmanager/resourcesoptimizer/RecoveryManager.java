package eu.betaas.taas.taasresourcesmanager.resourcesoptimizer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.log4j.Logger;

import eu.betaas.taas.taasresourcesmanager.catalogs.Application;
import eu.betaas.taas.taasresourcesmanager.catalogs.ApplicationsCatalog;
import eu.betaas.taas.taasresourcesmanager.catalogs.FeatureService;
import eu.betaas.taas.taasresourcesmanager.catalogs.Resource;
import eu.betaas.taas.taasresourcesmanager.catalogs.ResourcesCatalog;
import eu.betaas.taas.taasresourcesmanager.endpointsmanager.EndpointsManager;
import eu.betaas.taas.taasresourcesmanager.messaging.MessageManager;

public class RecoveryManager 
{
	private HashMap<String, Integer> failuresCatalog;
	private HashMap<String, Integer> gracePeriod;
	private HashMap<String, HashMap<String, Integer>> unreachableThingServices;
	private ApplicationsCatalog appCatalog;
	private ResourcesCatalog resCatalog;
	//private TaaSCMClient cmClient;
	//private TaaSQoSMClient myQoSClient;	
	private Logger logger= Logger.getLogger("betaas.taas");
	private String gwId;
	static private RecoveryManager _instance = null;
	
	private RecoveryManager (String gatewayId)
	{
		appCatalog = ApplicationsCatalog.instance();
		resCatalog = ResourcesCatalog.instance();	
		//myQoSClient = TaaSQoSMClient.instance();
		gracePeriod = new HashMap<String, Integer> ();
		failuresCatalog = new HashMap<String, Integer>();
		unreachableThingServices = new HashMap<String, HashMap<String, Integer>>();
		gwId = gatewayId;
	}
	
	public static synchronized RecoveryManager instance(String gateway) 
	{
		if (null == _instance) 
		{
			_instance = new RecoveryManager(gateway);			
			Logger myLogger= Logger.getLogger("betaas.taas");
			myLogger.info("A new instance of the Recovery Manager was created!");
		}
		return _instance;
	}
	
	public boolean removeThingService (String idThingService)
	{
		// Check if mitigation actions were already taken
		if (unreachableThingServices.containsKey(idThingService))
		{
			// Just remove the thing service from the unreachable map
			unreachableThingServices.remove(idThingService);
			return true;
		}
		
		// Look for those features using the thing service
		Resource myResource = resCatalog.getResource(idThingService);
		ArrayList<String> featuresList = myResource.getAllocatedFeatures();
		
		// For each feature, change to the next equivalent or notify SM about the issue
		EndpointsManager invokator = new EndpointsManager(gwId);
		Iterator<String> featuresIter = featuresList.iterator();
		while (featuresIter.hasNext())
		{
			// Retrieve the feature object
			String currentFeatureId = featuresIter.next();
			String myAppId = appCatalog.getServiceFromFeatureId(currentFeatureId);
			Application myApp = appCatalog.getApplication(myAppId);
			ArrayList<FeatureService> features = myApp.getFeatures();
			Iterator<FeatureService> myIter = features.iterator();	
			FeatureService currentFeature = null;
			
			while (myIter.hasNext())
			{
				// Invoke thing services subscription for the feature
				FeatureService foundFeature = myIter.next();
				if (foundFeature.getFeatureServiceId().equalsIgnoreCase(currentFeatureId))
				{				
					currentFeature = foundFeature;
				}
			}
			
			if (currentFeature!=null)
			{
				// Remove existing subscriptions and register subscription for equivalent thing services
				logger.info("Feature " + currentFeatureId + " found. Applying corrective action...");
				
				if (currentFeature.getType()==FeatureService.RTPUSH)
				{
					// Unregister the previous subscription
					invokator.unregisterSubscription(idThingService, currentFeatureId, true);
					logger.debug("Subscription removed!");
				}
				else if (currentFeature.getType()==FeatureService.NRTPUSH)
				{
					// Unregister the previous subscription
					invokator.unregisterSubscription(idThingService, currentFeatureId, false);
					logger.debug("Subscription removed!");
				}
				
				
			}
			else
			{
				logger.error("The feature object " + currentFeatureId + " could not be found. Recovery action wasn't done!!!!");
			}
			
			
			
		}
		
		return true;
		
		
	}
	
	public boolean unreachableThingService (String idThingService)
	{		
		logger.debug("TaaSRM starting replacement process...");
		// Look for those features using the thing service
		Resource myResource = resCatalog.getResource(idThingService);
		ArrayList<String> featuresList = myResource.getAllocatedFeatures();
		HashMap<String, Integer> featuresMap = new HashMap<String, Integer>();
		boolean result = true;
				
		if (myResource==null)
		{
			logger.error("Resource " + idThingService + " not found! Potential inconsistency in the resources catalog!");
			return false;
		}
		logger.debug("Resource found: " + myResource.getPhysicalResourceId());
		
		// For each feature, change to the next equivalent or notify SM about the issue
		Iterator<String> featuresIter = featuresList.iterator();
		while (featuresIter.hasNext())
		{
			// Retrieve the application
			String currentFeatureId = featuresIter.next();
			logger.debug("Checking feature " + currentFeatureId);
			String myAppId = appCatalog.getServiceFromFeatureId(currentFeatureId);
			Application myApp = appCatalog.getApplication(myAppId);
			
			// Look for the feature in the list
			ArrayList<FeatureService> features = myApp.getFeatures();
			Iterator<FeatureService> myIter = features.iterator();
			logger.debug("Look for the feature list and replace.");
			while (myIter.hasNext())
			{			
				FeatureService currentFeature = myIter.next();
				if (currentFeature.getFeatureServiceId().equalsIgnoreCase(currentFeatureId))
				{
					//  Get the feature information
					ArrayList<String> currentInvocationList = currentFeature.getThingServices();
					int position = currentInvocationList.indexOf(idThingService);
					ArrayList<ArrayList<String>> currentFeatureEquivalents  = currentFeature.getEquivalents();
					ArrayList<String> currentEquivalents = currentFeatureEquivalents.get(position);
					if (currentEquivalents==null)
					{
						logger.info("There are no equivalents!");
					}
					else
					{
						featuresMap.put(currentFeatureId, new Integer (position));
						logger.info("Number of available equivalents: " + currentEquivalents.size());
					}					
					
					if (currentEquivalents==null || currentEquivalents.size()<=1)
					{
						// If there are no equivalents, then indicate there is no option (we should invoke SM)
						result = result & false;
						
						logger.error("It was not possible to replace thing service -" + idThingService + "- in feature " + currentFeatureId);
						MessageManager.instance().monitoringPublish("Unreachable Thing Service " + idThingService + " couldn't be replaced.");
						
						// Remove subscriptions
						if (currentFeature.getType()==FeatureService.RTPUSH)
						{
							// Only unsubscribe, since the EndpointsManager and PushManager take care of the Catalog
							EndpointsManager invokator = new EndpointsManager(gwId);
							result = result & invokator.unregisterSubscription(idThingService, currentFeatureId, true);
							logger.info("Subscription removed for Thing Service " + idThingService + "!");
						}
						else if (currentFeature.getType()==FeatureService.NRTPUSH)
						{
							// Only unsubscribe, since the EndpointsManager and PushManager take care of the Catalog
							EndpointsManager invokator = new EndpointsManager(gwId);
							result = result & invokator.unregisterSubscription(idThingService, currentFeatureId, false);
							logger.info("Subscription removed for Thing Service " + idThingService + "!");
						}
					}
					else
					{
						logger.info("There is a replacement available! Replacing with " + currentEquivalents.get(1) + "...");
						
						// Change invocation thing services and equivalents lists
						currentEquivalents.remove(0);
						String firstEquivalent = currentEquivalents.get(0);
						currentEquivalents.add(idThingService);
						currentInvocationList.set(position, firstEquivalent);
						logger.debug("Invocation of the thing service was modified!");
										
						// Update the lists
						currentFeature.setThingServices(currentInvocationList);
						currentFeatureEquivalents.set(position, currentEquivalents);
						currentFeature.setEquivalentThingServices(currentFeatureEquivalents);
						logger.debug("The lists were updated!");
						
						// Modify resources catalog	& Activate/Disable corresponding subscriptions							
						if (currentFeature.getType()==FeatureService.RTPUSH)
						{
							// Only subscribe and unsubscribe, since the EndpointsManager and PushManager take care of the Catalog
							EndpointsManager invokator = new EndpointsManager(gwId);
							result = result & invokator.unregisterSubscription(idThingService, currentFeatureId, true);
							result = result & invokator.registerSubscription(firstEquivalent, currentFeatureId, currentFeature.getPeriod(), true, "localhost");
						}
						else if (currentFeature.getType()==FeatureService.NRTPUSH)
						{
							// Only subscribe and unsubscribe, since the EndpointsManager and PushManager take care of the Catalog
							EndpointsManager invokator = new EndpointsManager(gwId);
							result = result & invokator.unregisterSubscription(idThingService, currentFeatureId, false);
							result = result & invokator.registerSubscription(firstEquivalent, currentFeatureId, currentFeature.getPeriod(), false, "localhost");
						}
						else
						{
							myResource.removeFeature(currentFeatureId);
							myResource = resCatalog.getResource(firstEquivalent);
							myResource.addFeature(currentFeatureId);
						}						
						
						logger.info("Thing Service -" + idThingService + "- replaced by -" + firstEquivalent + "- in feature " + currentFeatureId);
						MessageManager.instance().monitoringPublish("Unreachable Thing Service " + idThingService + " replaced by " + firstEquivalent);
												
						// Report positive result
						result = result & true;	
					}				
									
				}
			}
		}	
		
		// Add the Thing Service to the list		
		if (!unreachableThingServices.containsKey(idThingService))
		{
			unreachableThingServices.put(idThingService, featuresMap);
		}
		return result;
	}
	
	public boolean reachableThingService (String idThingService)
	{			
		// Look for those features using the thing service
		boolean result = true;
		if (!unreachableThingServices.containsKey(idThingService))
		{
			return false;
		}
		HashMap<String, Integer> featuresList = unreachableThingServices.get(idThingService);
				
		// For each feature, change again the main candidate or notify SM about the issue
		Iterator<String> featuresIter = featuresList.keySet().iterator();
		while (featuresIter.hasNext())
		{
			// Retrieve the application
			String currentFeatureId = featuresIter.next();
			String myAppId = appCatalog.getServiceFromFeatureId(currentFeatureId);
			Application myApp = appCatalog.getApplication(myAppId);
					
			// Look for the feature in the list
			ArrayList<FeatureService> features = myApp.getFeatures();
			Iterator<FeatureService> myIter = features.iterator();	
			while (myIter.hasNext())
			{			
				FeatureService currentFeature = myIter.next();
				if (currentFeature.getFeatureServiceId().equalsIgnoreCase(currentFeatureId))
				{
					//  Get the feature information
					ArrayList<String> currentInvocationList = currentFeature.getThingServices();
					int position = featuresList.get(currentFeatureId).intValue();
					String currentCandidate = currentInvocationList.get(position);
					ArrayList<ArrayList<String>> currentFeatureEquivalents  = currentFeature.getEquivalents();
					ArrayList<String> currentEquivalents = currentFeatureEquivalents.get(position);
							
					// Check if the thing service was replaced before
					if (currentCandidate.equalsIgnoreCase(idThingService))
					{
						logger.info ("The thing service " + idThingService + " was not replaced for feature " + currentFeatureId);
						
						// Only re-activate the subscription in the TA
						if (currentFeature.getType()==FeatureService.RTPUSH)
						{
							// Only subscribe, since the EndpointsManager and PushManager take care of the Catalog
							EndpointsManager invokator = new EndpointsManager(gwId);							
							result = result & invokator.registerSubscription(idThingService, currentFeatureId, currentFeature.getPeriod(), true, "localhost");
							logger.info("Subscription for Thing Service " + idThingService + " restored!");
						}
						else if (currentFeature.getType()==FeatureService.NRTPUSH)
						{
							// Only subscribe, since the EndpointsManager and PushManager take care of the Catalog
							EndpointsManager invokator = new EndpointsManager(gwId);							
							result = result & invokator.registerSubscription(idThingService, currentFeatureId, currentFeature.getPeriod(), false, "localhost");
							logger.info("Subscription for Thing Service " + idThingService + " restored!");
						}
						
						logger.info("Subscription for Thing Service " + idThingService + " re-activated in the TA.");
					}
					else
					{
						// Change invocation thing services and equivalents lists						
						currentEquivalents.add(0, idThingService);
						currentInvocationList.set(position, idThingService);
													
						// Update the lists
						currentFeature.setThingServices(currentInvocationList);
						currentFeatureEquivalents.set(position, currentEquivalents);
						currentFeature.setEquivalentThingServices(currentFeatureEquivalents);
									
						// Modify resources catalog	& Activate/Disable corresponding subscriptions		
						Resource myResource = resCatalog.getResource(idThingService);
						if (currentFeature.getType()==FeatureService.RTPUSH)
						{
							// Only subscribe and unsubscribe, since the EndpointsManager and PushManager take care of the Catalog
							EndpointsManager invokator = new EndpointsManager(gwId);
							result = result & invokator.unregisterSubscription(currentCandidate, currentFeatureId, true);
							result = result & invokator.registerSubscription(idThingService, currentFeatureId, currentFeature.getPeriod(), true, "localhost");							
						}
						else if (currentFeature.getType()==FeatureService.NRTPUSH)
						{
							// Only subscribe and unsubscribe, since the EndpointsManager and PushManager take care of the Catalog
							EndpointsManager invokator = new EndpointsManager(gwId);
							result = result & invokator.unregisterSubscription(currentCandidate, currentFeatureId, false);
							result = result & invokator.registerSubscription(idThingService, currentFeatureId, currentFeature.getPeriod(), false, "localhost");
						}
						else
						{
							// Modify resources catalog
							myResource.addFeature(currentFeatureId);
							myResource = resCatalog.getResource(currentCandidate);
							myResource.removeFeature(currentFeatureId);
						}												
									
						logger.info("Thing Service -" + currentCandidate + "- replaced by -" + idThingService + "- in feature " + currentFeatureId);
												
					}	
							
					// Report positive result
					result = result & true;	
									
									
				}
			}
		}	
				
		return result;
	}
	
	
	// To be invoked by PushManager in PUSH mode
	public void unavailableThingService (String idThingService)
	{
		// Look for those features using the thing service
		Resource myResource = resCatalog.getResource(idThingService);
		ArrayList<String> featuresList = myResource.getAllocatedFeatures();
		
		// For each feature, change to the next equivalent or notify SM about the issue
		Iterator<String> featuresIter = featuresList.iterator();
		while (featuresIter.hasNext())
		{
			String currentFeatureId = featuresIter.next();
			String myAppId = appCatalog.getServiceFromFeatureId(currentFeatureId);
			Application myApp = appCatalog.getApplication(myAppId);
			
		}
		
		// Add the thing service to the 'grace period' list if not yet there
		if (gracePeriod.containsKey(idThingService))
		{
			int unavailableAlerts = gracePeriod.get(idThingService).intValue() + 1;
			if (unavailableAlerts > featuresList.size()*0.4f)
			{
				
			}
		}
		else
		{
			gracePeriod.put(idThingService, new Integer(1));
		}
		
		
	}
	
	// To be invoked by Endpoints Manager in PULL mode
	public String failedInvocation (String idThingService, String idFeature)
	{
		// Retrieve and update failures number
		Integer failures = failuresCatalog.get(idThingService);
		if (failures == null)
		{
			failures = new Integer(1);
		}
		else
		{
			failures = new Integer (failures.intValue()+1);
		}
		failuresCatalog.put(idThingService, failures);
		
		// Check failures number and decide whether to change the thing service or not
		if (failures.intValue()<3)
		{
			return idThingService;
		}
		
		// If the failure are too many, then use the next equivalent one
		// Get the application and the feature
		String myAppId = appCatalog.getServiceFromFeatureId(idFeature);
		Application myApp = appCatalog.getApplication(myAppId);
		ArrayList<FeatureService> features = myApp.getFeatures();
		Iterator<FeatureService> myIter = features.iterator();	
		while (myIter.hasNext())
		{			
			FeatureService currentFeature = myIter.next();
			if (currentFeature.getFeatureServiceId().equalsIgnoreCase(idFeature))
			{
				//  Get the feature information
				ArrayList<String> currentInvocationList = currentFeature.getThingServices();
				int position = currentInvocationList.indexOf(idThingService);
				ArrayList<ArrayList<String>> currentFeatureEquivalents  = currentFeature.getEquivalents();
				ArrayList<String> currentEquivalents = currentFeatureEquivalents.get(position);
				
				if (currentEquivalents.size()<=1)
				{
					// If there are no equivalents, then do nothing (we should invoke SM)
					return idThingService;
				}
				
				// Change invocation thing services and equivalents lists
				currentEquivalents.remove(0);
				String firstEquivalent = currentEquivalents.get(0);
				currentEquivalents.add(idThingService);
				currentInvocationList.set(position, firstEquivalent);
								
				// Update the lists
				currentFeature.setThingServices(currentInvocationList);
				currentFeatureEquivalents.set(position, currentEquivalents);
				currentFeature.setEquivalentThingServices(currentFeatureEquivalents);
				
				// Modify resources catalog
				Resource myResource = resCatalog.getResource(idThingService);
				myResource.removeFeature(idFeature);
				myResource = resCatalog.getResource(firstEquivalent);
				myResource.addFeature(idFeature, 0);
				
				// Return thing service to be invoked
				return firstEquivalent;
				
			}
		}
		return idThingService;
	}
}
