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
import eu.betaas.taas.taasresourcesmanager.endpointsmanager.FeatureResult;
import eu.betaas.taas.taasresourcesmanager.taasrmclient.TaaSCMClient;
import eu.betaas.taas.taasresourcesmanager.taasrmclient.TaaSQoSMClient;

public class RecoveryManager 
{
	private HashMap<String, Integer> failuresCatalog;
	private ApplicationsCatalog appCatalog;
	private ResourcesCatalog resCatalog;
	private TaaSCMClient cmClient;
	private TaaSQoSMClient myQoSClient;
	private String localGateway;
	private Logger logger= Logger.getLogger("betaas.taas");
	
	public RecoveryManager ()
	{
		appCatalog = ApplicationsCatalog.instance();
		resCatalog = ResourcesCatalog.instance();	
		myQoSClient = TaaSQoSMClient.instance();
	}
	
	public void removeThingService (String idThingService)
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
		
	}
	
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
				myResource.addFeature(idFeature);
				
				// Return thing service to be invoked
				return firstEquivalent;
				
			}
		}
		return idThingService;
	}
}
