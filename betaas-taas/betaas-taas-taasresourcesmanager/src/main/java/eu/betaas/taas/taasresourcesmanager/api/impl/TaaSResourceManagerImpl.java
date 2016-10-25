/**

Copyright 2015 ATOS SPAIN S.A.

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

package eu.betaas.taas.taasresourcesmanager.api.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.osgi.framework.BundleContext;

import com.google.gson.JsonObject;

import eu.betaas.taas.bigdatamanager.database.service.ThingsData;
import eu.betaas.taas.taasresourcesmanager.api.Feature;
import eu.betaas.taas.taasresourcesmanager.api.Location;
import eu.betaas.taas.taasresourcesmanager.api.TaaSResourceManager;
import eu.betaas.taas.taasresourcesmanager.catalogs.Application;
import eu.betaas.taas.taasresourcesmanager.catalogs.ApplicationsCatalog;
import eu.betaas.taas.taasresourcesmanager.catalogs.FeatureService;
import eu.betaas.taas.taasresourcesmanager.catalogs.Resource;
import eu.betaas.taas.taasresourcesmanager.catalogs.ResourcesCatalog;
import eu.betaas.taas.taasresourcesmanager.endpointsmanager.EndpointsManager;
import eu.betaas.taas.taasresourcesmanager.endpointsmanager.PushManager;
import eu.betaas.taas.taasresourcesmanager.messaging.MessageManager;
import eu.betaas.taas.taasresourcesmanager.resourcesoptimizer.RecoveryManager;
import eu.betaas.taas.taasresourcesmanager.resourcesoptimizer.ResourcesAllocator;
import eu.betaas.taas.taasresourcesmanager.taasrmclient.ServiceSECMClient;
import eu.betaas.taas.taasresourcesmanager.taasrmclient.TaaSQoSMClient;
import eu.betaas.taas.taasresourcesmanager.taasrmclient.TaaSRMClient;

/**
* 
* @author Francisco Javier Nieto De-Santos (francisco.nieto@atos.net)
* @author Sergio Garcia Villalonga (sergio.garciavillalonga@atos.net)
*/
public class TaaSResourceManagerImpl implements TaaSResourceManager 
{
	private Logger logger= Logger.getLogger("betaas.taas");
	private String gwId;
	private BundleContext mContext;
	private MessageManager mManager;
	private String mDelimiter;
	
	public void setupService(){
		logger.debug("[TaaSResourceManagerImpl] Starting the service");
		mManager = MessageManager.instance();
		TaaSRMClient.instance(gwId, "TaaSResourceManagerImpl");
		logger.debug("[TaaSResourceManagerImpl] Service started");
		mManager.monitoringPublish("[TaaSResourceManagerImpl] Service started");
	}
	
	public void setGwId(String id)
	{
		gwId = id;
		//BundleContext context = FrameworkUtil.getBundle(TaaSResourceManagerImpl.class).getBundleContext();
		//context.getBundle().getHeaders().put("gwId", id);
	}
	
	public void setContext(BundleContext context)
	{
		mContext = context;
		//BundleContext context = FrameworkUtil.getBundle(TaaSResourceManagerImpl.class).getBundleContext();
		//context.getBundle().getHeaders().put("gwId", id);
	}
	
	public void setDelimiter(String delimiter)
	{
		mDelimiter = delimiter;
	}
	
	public String getGwId ()
	{
		return gwId;
	}
		
	public String allocateResources(Feature appFeature) 
	{		
		logger.info ("Allocate resources method (with feature) invoked!");
		logger.info ("Look for " + appFeature.getFeature() + " in " + appFeature.getLocation().getLocationIdentifier() + "-" + appFeature.getLocation().getLocationKeyword() + " with period " + appFeature.getPeriod());
		ResourcesAllocator myAllocator = new ResourcesAllocator(gwId, mDelimiter);
		String result = myAllocator.allocateResources(appFeature);
		MessageManager.instance().monitoringPublish("Feature " + result + " has started installation process.");
		return result;
	}

	public boolean freeLocalResources(String serviceID) 
	{
		// TODO Auto-generated method stub
		logger.info ("Free local resources method invoked!");
		ResourcesAllocator myAllocator = new ResourcesAllocator();
		return myAllocator.freeLocalResources(serviceID);
		
	}

	public boolean synchronizeThingServices(String instanceInfo) 
	{
		// Create remote client
		logger.info("Starting synchronization with gateway " + instanceInfo);
		TaaSRMClient myRemoteClient = TaaSRMClient.instance(gwId, "TaaSResourceManagerImpl");		
		
		// Synchronize remote and local resources
		return myRemoteClient.synchronizeThingServices();		
	}
	
	public boolean removeThingServices(String idGateway) 
	{		
		logger.info ("Remove Thing Services for gateway " + idGateway + " method invoked!");
		boolean result = ResourcesCatalog.instance().removeResources(idGateway);
		MessageManager.instance().monitoringPublish("Thing Services at gateway " + idGateway + " removed!");
		return result;
	}

	public boolean removeThingServices() 
	{
		
		logger.info ("Remove all Thing Services method invoked!");
		return ResourcesCatalog.instance().removeResources();
	}
		
	public boolean registerThingsServices(String thingID, String thingServiceID) 
	{		
		logger.info("Register Thing Services method invoked! -> " + thingID);
		
		// Check that the thing is not already registered --> Avoid issues!
		if (ResourcesCatalog.instance().getResource(thingServiceID)!=null)
		{
			logger.error ("Thing Service already available! Not registering...");
			return false;
		}
		Resource newResource = new Resource (thingServiceID, thingID, Resource.THINGSERVICE, "localhost");		
		ResourcesCatalog.instance().addResource(newResource);
		
		//Notify to the QoSM
		TaaSQoSMClient myQoSClient = TaaSQoSMClient.instance();
		myQoSClient.registerThingQoS(thingID, thingServiceID);
		logger.info("QoS Manager notified for the new Thing Service " + thingServiceID);
		
		// Notify to the rest of TaaSRM in the instance
		TaaSRMClient myRMCli = TaaSRMClient.instance(gwId, "TaaSResourceManagerImpl");
		myRMCli.registerThingAllRemote(newResource);
		
		MessageManager.instance().monitoringPublish("Thing " + thingID + " registered as " + thingServiceID + ".");
		
		return true;
	}	

	public boolean removeThingsService(String thingServiceID) 
	{		
		logger.info ("Remove Thing Services method invoked! -> " + thingServiceID);
				
		// Take mitigation actions for allocated features
		RecoveryManager myRecoManager = RecoveryManager.instance(gwId);
		boolean result = myRecoManager.removeThingService(thingServiceID);
		
		// Notify the QoSM
		TaaSQoSMClient myQoSClient = TaaSQoSMClient.instance();
		myQoSClient.notifyThingServiceRemoval(thingServiceID);
		
		// Notify to the rest of TaaSRM in the instance
		TaaSRMClient myRMCli = TaaSRMClient.instance(gwId, "TaaSResourceManagerImpl");
		result = result & myRMCli.removeThingAllRemote(thingServiceID);
		
		// Remove the thing service from the catalog
		result = result & ResourcesCatalog.instance().removeResource(thingServiceID);
		
		MessageManager.instance().monitoringPublish("Thing Service " + thingServiceID + " removed!");
		
		return result;
	}
	
	public boolean unreachableThingService(String thingServiceID)
	{
		logger.info ("Thing Service not available reported! -> " + thingServiceID);
		RecoveryManager myRecoManager = RecoveryManager.instance(gwId);
		boolean result = myRecoManager.unreachableThingService(thingServiceID);
		
		MessageManager.instance().monitoringPublish("Thing Service " + thingServiceID + " reported as not reachable! Actions taken.");
		MessageManager.instance().dependabilityPublish(thingServiceID);
		
		return result;
	}
	
	public boolean reachableThingService(String thingServiceID)
	{
		logger.info ("Existing Thing Service available again reported! -> " + thingServiceID);
		RecoveryManager myRecoManager = RecoveryManager.instance(gwId);
		boolean result = myRecoManager.reachableThingService(thingServiceID);
		
		MessageManager.instance().monitoringPublish("Thing Service " + thingServiceID + " reachable again. Subscriptions restored.");
		
		return result;
	}
			
	public ArrayList<ArrayList<String>> getSecurityRank (String serviceID)
	{
		logger.info ("Get Security Rank method invoked! -> " + serviceID);
		ResourcesAllocator myAllocator = new ResourcesAllocator();
		return myAllocator.getSecurityRank(serviceID);
	}
	
	public void putQoSRank(String serviceID, ArrayList<ArrayList<String>> equivalentThingServicesQoSRank)
	{		
		// Notify the Service Manager that the installation process was finished
		ResourcesAllocator myAllocator = new ResourcesAllocator();
		myAllocator.putQoSRank(serviceID, equivalentThingServicesQoSRank);
		
		MessageManager.instance().monitoringPublish("Feature " + serviceID + " has completed the installation!");
	}
			
	public void revokeService(String serviceID) 
	{
		// Remove all data about the installation of the feature
		logger.info ("Allocation process for " + serviceID + " failed!");
		ResourcesAllocator myAllocator = new ResourcesAllocator();
		myAllocator.revokeService(serviceID);
	}

	public String allocateResources(String appID, Feature[] featuresList) 
	{		
		logger.info ("Allocate resources method invoked!");
		ResourcesAllocator myAllocator = new ResourcesAllocator();
		
		if (featuresList.length > 0 && appID != null) {
			return myAllocator.allocateResources(featuresList[0]);
		}
		
		return null;
	}

	public boolean deleteThingServices(List<String> thingServicesList) {
		// TODO Auto-generated method stub
		return true;
	}

	

	public boolean notifyTrustAlert(String thingServiceId, int trust) {
		// TODO Auto-generated method stub
		return true;
	}

	/*
	// GET and SET data methods: NRT-PULL, RT-PULL, NRT-PUSH, RT-PUSH and PUT modes
	*/
	
	public JsonObject getData(String featureServiceID, byte[] token)
	{
		logger.info ("Get Data method invoked! -> " + featureServiceID);
		logger.info("Checking received token...");
		ServiceSECMClient secCli = ServiceSECMClient.instance();
		if (!secCli.verifyToken(new String (token)))
		{
			logger.error("The token was not valid! Operation aborted!");
			return null;
		}
		logger.info("Done! Invocation accepted! Processing...");
		EndpointsManager invokator = new EndpointsManager(gwId);
		JsonObject result = invokator.invokeServiceThings(featureServiceID);
		
		MessageManager.instance().monitoringPublish("Feature " + featureServiceID + " has performed a get data operation.");
		
		return result;
	}
	
	public boolean setData(String featureServiceID, String value, byte[] token) 
	{
		logger.info ("Set Data method invoked! -> " + featureServiceID);
		logger.info("Checking received token...");
		ServiceSECMClient secCli = ServiceSECMClient.instance();
		if (!secCli.verifyToken(new String (token)))
		{
			logger.error("The token was not valid! Operation aborted!");
			return false;
		}
		logger.info("Done! Invocation accepted! Processing...");
		EndpointsManager invokator = new EndpointsManager(gwId);
		boolean result = invokator.setData(featureServiceID, value);
		
		MessageManager.instance().monitoringPublish("Feature " + featureServiceID + " has performed a set data operation.");
				
		return result;			
	}
	
	public boolean registerService(String featureServiceID, byte[] token) 
	{		
		logger.info ("Subscription requested for a feature! -> " + featureServiceID);
		logger.info("Checking received token...");
		ServiceSECMClient secCli = ServiceSECMClient.instance();
		if (token==null || token.length==0 || !secCli.verifyToken(new String (token)))
		{
			logger.error("The token was not valid! Operation aborted!");
			return false;
		}
		logger.info("Done! Invocation accepted! Processing...");
		EndpointsManager invokator = new EndpointsManager(gwId);
		boolean result = invokator.subscribeFeatureService (featureServiceID);
		
		MessageManager.instance().monitoringPublish("Feature " + featureServiceID + " has activated its subscriptions.");
		
		return result;
		
	}

	public boolean unRegisterService(String featureServiceID, byte[] token) 
	{
		logger.info ("Unsubscription requested for a feature! -> " + featureServiceID);
		logger.info("Checking received token...");
		ServiceSECMClient secCli = ServiceSECMClient.instance();
		if (!secCli.verifyToken(new String (token)))
		{
			logger.error("The token was not valid! Operation aborted!");
			return false;
		}
		logger.info("Done! Invocation accepted! Processing...");
		EndpointsManager invokator = new EndpointsManager(gwId);
		boolean result = invokator.unsubscribeFeatureService(featureServiceID);
		
		MessageManager.instance().monitoringPublish("Feature " + featureServiceID + " has stopped its subscriptions.");
		
		return result;
	}
	
	public boolean startFullApplication (String idApplication, byte[] token)
	{
		logger.info ("Subscription requested for a full application! -> " + idApplication);
		logger.info("Checking received token...");
		ServiceSECMClient secCli = ServiceSECMClient.instance();
		if (token==null || token.length==0 || !secCli.verifyToken(new String (token)))
		{
			logger.error("The token was not valid! Operation aborted!");
			return false;
		}
		logger.info("Done! Invocation accepted! Processing...");
		
		// Retrieve the application and iterate through all the features
		EndpointsManager invokator = new EndpointsManager(gwId);
		Application myApp = ApplicationsCatalog.instance().getApplication(idApplication);
		ArrayList<FeatureService> featuresList = myApp.getFeatures();
		for (int i=0; i<featuresList.size(); i++)
		{
			FeatureService currentFeature = featuresList.get(i);
			int featureType = currentFeature.getType();
			if (featureType==FeatureService.RTPUSH || featureType==FeatureService.NRTPUSH)
			{
				invokator.subscribeFeatureService (currentFeature.getFeatureServiceId());
			}
		}
		
		return true;
	}
	
	public boolean stopFullApplication (String idApplication, byte[] token)
	{
		logger.info ("Unsubscription requested for a full application! -> " + idApplication);
		logger.info("Checking received token...");
		ServiceSECMClient secCli = ServiceSECMClient.instance();
		if (token==null || token.length==0 || !secCli.verifyToken(new String (token)))
		{
			logger.error("The token was not valid! Operation aborted!");
			return false;
		}
		logger.info("Done! Invocation accepted! Processing...");
		
		// Retrieve the application and iterate through all the features
		EndpointsManager invokator = new EndpointsManager(gwId);
		Application myApp = ApplicationsCatalog.instance().getApplication(idApplication);
		ArrayList<FeatureService> featuresList = myApp.getFeatures();
		for (int i=0; i<featuresList.size(); i++)
		{
			FeatureService currentFeature = featuresList.get(i);
			int featureType = currentFeature.getType();
			if (featureType==FeatureService.RTPUSH || featureType==FeatureService.NRTPUSH)
			{
				logger.info("Unsubscribing thing services for feature " + currentFeature.getFeatureServiceId() + "...");
				invokator.unsubscribeFeatureService (currentFeature.getFeatureServiceId());
			}
		}
		
		return true;
	}
	
	// GET/SET with dynamic location
	
	public boolean setData(String featureServiceID, String value, Location newLocation, byte[] token) 
	{
		logger.info ("Set Data with new Location method invoked! -> " + featureServiceID);
		logger.info("Checking received token...");
		ServiceSECMClient secCli = ServiceSECMClient.instance();
		if (!secCli.verifyToken(new String (token)))
		{
			logger.error("The token was not valid! Operation aborted!");
			return false;
		}
		logger.info("Done! Invocation accepted! Processing...");
		
		//Update feature location
		logger.info("Update feature service location to the new provided one.");
		ResourcesAllocator myAllocator = new ResourcesAllocator();
		myAllocator.updateFeatureLocation(featureServiceID, newLocation);
		
		//Invoke the thing services allocated after the updating
		logger.info("Setting data for the thing services in the new location.");
		EndpointsManager invokator = new EndpointsManager(gwId);
		return invokator.setData(featureServiceID, value);			
	}
	
	public JsonObject getData(String featureServiceID, Location newLocation, byte[] token)
	{
		logger.info ("Get Data with new Location method invoked! -> " + featureServiceID);
		logger.info("Checking received token...");
		ServiceSECMClient secCli = ServiceSECMClient.instance();
		if (!secCli.verifyToken(new String (token)))
		{
			logger.error("The token was not valid! Operation aborted!");
			return null;
		}
		logger.info("Done! Invocation accepted! Processing...");
		
		//Update feature location
		logger.info("Update feature service location to the new provided one.");
		ResourcesAllocator myAllocator = new ResourcesAllocator();
		myAllocator.updateFeatureLocation(featureServiceID, newLocation);
		
		//Invoke the thing services allocated after the updating
		logger.info("Invoke thing services in the new location.");
		EndpointsManager invokator = new EndpointsManager(gwId);
		return invokator.invokeServiceThings(featureServiceID);
	}
	
	public boolean updateFeatureLocation (String idFeature, Location newLocation, byte[] token)
	{
		return true;
	}
	
	// End of methods for GET/SET data
	
	// Methods for notifications reception (PUSH modes)
	public void notifyNewMeasurement(String thingServiceID, ThingsData data) 
	{
		logger.info ("Notification with data received from CM! -> " + thingServiceID);
		PushManager manager = PushManager.instance(gwId);
		if (data == null)
		{
			logger.warn("Data received is null!!!!");			
		}
		else
		{
			logger.info("Measurement received: " + data.getMeasurement());
		}
		
		manager.receiveCMMeasurement(thingServiceID, data);
	}
	
	public boolean notifyMeasurement(String thingServiceID, ThingsData data) 
	{
		logger.info ("Notification with data received from TA! -> " + thingServiceID);
		PushManager manager = PushManager.instance(gwId);
		if (data == null)
		{
			logger.warn("Data received is null!!!!");			
		}
		else
		{
			logger.info("Measurement received: " + data.getMeasurement());
		}
		return manager.receiveTAMeasurement(thingServiceID, data);
	}
			
	public String createCompNode(int authLevel) {
		// TODO Auto-generated method stub
		return "compute-b170294b-a9ad-44f7-9ed7-62707c31cf81";
	}

	public boolean deleteNode(String nodeId) {
		// TODO Auto-generated method stub
		return true;
	}

	public int getAuthorizationLevels() {
		// TODO Auto-generated method stub
		return 0;
	}

	public List<String> removeVMs() {
		// TODO Auto-generated method stub
		return new ArrayList<String>();
	}

	public String deployApp(String deploymentRequest) {
		// TODO Auto-generated method stub
		return "app-b170294b-a9ad-44f7-9ed7-62707c31cf81";
	}

	public boolean unDeployApp(String appId) {
		// TODO Auto-generated method stub
		return true;
	}
	
	/* Added methods for enabling trust calculations
	 * Service Trust: Provide info about other gateways
	 * TaaS Trust: Provide info about installed applications
	 */
	
	 public int getGatewayTime (int idGateway)
	 {
		 return 0;
	 }
	
}
