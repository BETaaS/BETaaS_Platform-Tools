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

package eu.betaas.taas.taasresourcesmanager.endpointsmanager;

import java.util.ArrayList;
import java.util.Iterator;

import org.apache.log4j.Logger;

import com.google.gson.JsonObject;

import eu.betaas.taas.qosmanager.monitoring.api.impl.SLACalculation;
import eu.betaas.taas.taasresourcesmanager.api.ThingServiceResult;
import eu.betaas.taas.taasresourcesmanager.catalogs.Application;
import eu.betaas.taas.taasresourcesmanager.catalogs.ApplicationsCatalog;
import eu.betaas.taas.taasresourcesmanager.catalogs.FeatureService;
import eu.betaas.taas.taasresourcesmanager.catalogs.Resource;
import eu.betaas.taas.taasresourcesmanager.catalogs.ResourcesCatalog;
import eu.betaas.taas.taasresourcesmanager.taasrmclient.AdaptTAClient;
import eu.betaas.taas.taasresourcesmanager.taasrmclient.ServiceSMClient;
import eu.betaas.taas.taasresourcesmanager.taasrmclient.TaaSCMClient;
import eu.betaas.taas.taasresourcesmanager.taasrmclient.TaaSQoSMClient;
import eu.betaas.taas.taasresourcesmanager.taasrmclient.TaaSRMClient;
import eu.betaas.taas.taasresourcesmanager.taasrmclient.TaaSTMClient;

/**
* 
* @author Francisco Javier Nieto De-Santos (francisco.nieto@atos.net)
* @author Sergio Garcia Villalonga (sergio.garciavillalonga@atos.net)
*/
public class EndpointsManager 
{
	private ApplicationsCatalog myServCatalog;
	private ResourcesCatalog myResCatalog;
	private Logger logger= Logger.getLogger("betaas.taas");
	private TaaSTMClient tmClient;
	private TaaSQoSMClient myQoSClient;	
	private String localGateway;
	
	public EndpointsManager ()
	{
		myServCatalog = ApplicationsCatalog.instance();
		myResCatalog = ResourcesCatalog.instance();
		tmClient = TaaSTMClient.instance();
		myQoSClient = TaaSQoSMClient.instance();		
	}
	
	public EndpointsManager (String gateway)
	{
		myServCatalog = ApplicationsCatalog.instance();
		myResCatalog = ResourcesCatalog.instance();
		tmClient = TaaSTMClient.instance();
		myQoSClient = TaaSQoSMClient.instance();		
		localGateway = gateway;
	}
	
	public ThingServiceResult invokeThingService (String idThingService, boolean realTime)
	{
		Resource theResource = myResCatalog.getResource(idThingService);
		
		// If the resource is not available, give null as result
		if (theResource==null)
		{
			logger.error("Thing to be invoked was not found!! " + idThingService);
			return null;
		}
		
		// Retrieve the information about this resource		
		String thingId = theResource.getPhysicalResourceId();
		String location = theResource.getGatewayId();		
		
		// Check thing location		
		if (!location.equalsIgnoreCase("localhost"))
		{
			// Invoke the remote Gateway for obtaining the value
			logger.info("The thing " + thingId + " is located at " + location + ". Invoking remotely...");
			
			TaaSRMClient myRMCli = TaaSRMClient.instance(localGateway, "EndpointsManager");
			ThingServiceResult invocationResult = myRMCli.remoteInvocation(idThingService, location, realTime);
			//myBDMClient.saveThingData(thingId, invocationResult);
			return invocationResult;			
		}
		
		logger.info("Invoking thing with ID: " + thingId);
		ThingServiceResult invocationResult = null;
		// Determine if it is real-time or non-real-time invocation
		if (realTime)
		{
			AdaptTAClient taClient = AdaptTAClient.instance();
			invocationResult = taClient.getFullMeasurement(thingId);
		}
		else
		{
			// Note that the CM gets the Thing Service ID as parameter
			TaaSCMClient cmClient = TaaSCMClient.instance(); 
			invocationResult = cmClient.getFullMeasurement(idThingService);
		}
				
		return invocationResult;		
	}
	
	public boolean setDataToThingService (String idThingService, String theValue)
	{
		Resource theResource = myResCatalog.getResource(idThingService);
		
		// If the resource is not available, give null as result
		if (theResource==null)
		{
			logger.error("Thing to be used was not found!! " + idThingService);
			return false;
		}
		
		// Retrieve the information about this resource		
		String thingId = theResource.getPhysicalResourceId();
		String location = theResource.getGatewayId();
		
		// Check thing location		
		if (!location.equalsIgnoreCase("localhost"))
		{
			// Invoke the remote Gateway for obtaining the value
			logger.info("The thing " + thingId + " is located at " + location + ". Setting data remotely...");
					
			TaaSRMClient myRMCli = TaaSRMClient.instance(localGateway, "EndpointsManager");					
			return myRMCli.remoteSetInvocation(idThingService, location, theValue);			
		}
		
		// Local invocation
		logger.info("Setting data in thing with ID: " + thingId);
		AdaptTAClient taClient = AdaptTAClient.instance();
		return taClient.setThingData(thingId, theValue);
	}
			
	public boolean registerSubscription (String idThingService, String idApplication, int period, boolean realTime, String gateway)
	{
		Resource theResource = myResCatalog.getResource(idThingService);
		
		// If the resource is not available, give null as result
		if (theResource==null)
		{
			logger.error("Thing to be invoked was not found!! " + idThingService);
			return false;
		}
		
		// Retrieve the information about this resource		
		String thingId = theResource.getPhysicalResourceId();
		String location = theResource.getGatewayId();		
				
		logger.info("Subscribing to thing with ID: " + thingId);
		boolean subscriptionResult = false;
		
		//Create Subscription object and register it
		Subscription mySubs = new Subscription (idApplication, gateway, idThingService, period);
		PushManager subsManager = PushManager.instance(localGateway);
		if (!gateway.equalsIgnoreCase(localGateway))
		{
			//When receiving remote subscriptions, we need to guarantee the feature is available in the PushManager
			subsManager.requestFeature(idApplication, "REMOTE");
		}		
		subsManager.addSubscription(mySubs, realTime);
		
		// Check thing location		
		if (!location.equalsIgnoreCase("localhost"))
		{
			// Invoke the remote Gateway for subscribing
			logger.info("The thing " + thingId + " is located at " + location + ". Subscribing remotely...");
			
			TaaSRMClient myRMCli = TaaSRMClient.instance(localGateway, "EndpointsManager");
			subscriptionResult = myRMCli.remoteSubscription(idThingService, location, idApplication, period, realTime);					
		}		
		// Determine if it is real-time or non-real-time subscription		
		else if (realTime)
		{
			AdaptTAClient taClient = AdaptTAClient.instance();
			subscriptionResult = taClient.subscribeToThing(thingId, period);
		}
		else
		{
			// Note that the CM gets the Thing Service ID as parameter
			TaaSCMClient cmClient = TaaSCMClient.instance(); 
			subscriptionResult = cmClient.subscribeToThing(idThingService);
		}		
		return subscriptionResult;		
	}
	
	
	public JsonObject invokeServiceThings (String idFeatureService)
	{
		String idApp = myServCatalog.getServiceFromFeatureId(idFeatureService);
		Application theApp = myServCatalog.getApplication(idApp);
		ArrayList<FeatureService> features = theApp.getFeatures();
		Iterator<FeatureService> myIter = features.iterator();		
		FeatureResult results = new FeatureResult (idFeatureService);
		while (myIter.hasNext())
		{
			// Invoke thing services for each feature
			FeatureService currentFeature = myIter.next();
			if (currentFeature.getFeatureServiceId().equalsIgnoreCase(idFeatureService))
			{
				// First, we invoke QoS Manager, in order to know if the Thing Services lists must be updated				
				ArrayList<String> thingServList = myQoSClient.registerServiceQoSPull(idFeatureService, currentFeature.getThingServices(), currentFeature.getEquivalents());
				currentFeature.setThingServices(thingServList);
				logger.debug("List of Thing Services updated with the QoS Manager!");
				ArrayList<SLACalculation> receivedSLAs = new ArrayList<SLACalculation>();
				
				// If info mode is PULL, we invoke the thing services
				int featureType = currentFeature.getType();
				if (featureType==FeatureService.RTPULL || featureType==FeatureService.NRTPULL)
				{					
					// Keep the list of results for applying the operator									
					Iterator<String> invocations = thingServList.iterator();
					
					// We activate the SLA monitoring for the invocations
					myQoSClient.activateSLAMonitoring(thingServList);					
					
					// We start the invocations
					while (invocations.hasNext())
					{
						// Register Thing Service in the Trust Manager for increasing priority
						String currentId = invocations.next();
						tmClient.registerThingsService(currentId);
						
						if (currentFeature.getType()==FeatureService.RTPULL)
						{
							// Invoke the Thing Service in Real-Time and put the result in the result structure	
							ThingServiceResult received = invokeThingService(currentId, true);
							logger.debug("Result: " + received.getMeasurement());				
							results.addTSResult(received);
							logger.debug("Result added.");
							
							// Calculate SLA --> An error should be notified in case SLA is null
							SLACalculation currentSLA = myQoSClient.retrieveSLACalculation(currentId);
							if (currentSLA != null)
							{
								receivedSLAs.add(currentSLA);
							}
							
						}
						else
						{
							// Invoke the Thing Service in Non-Real-Time and put the result in the result structure	
							ThingServiceResult received = invokeThingService(currentId, false);
							if (received==null)
							{
								// Report error but don't stop invocations (send partial results at least)
								logger.error("Null value retrieved when trying to access thing service: " + currentId);								
							}
							else
							{
								logger.debug("Result: " + received.getMeasurement());				
								results.addTSResult(received);
								logger.debug("Result added.");
							}							
						}
						
					}
				}
				else
				{
					logger.error("Features in PUSH mode should not be called in PULL mode!!");
					return null;
				}
				
				// Return the result (thing services results + operator)
				results.setOperator(currentFeature.getOperator());
				logger.debug("Invocations finished! -> Adding operator and sending results.");
				
				// If it's Real-Time mode, finish QoS actions
				if (currentFeature.getType()==FeatureService.RTPULL)
				{
					// Check SLA calculations
					if (receivedSLAs==null || receivedSLAs.size()<=0)
					{
						logger.error("SLA information could not be retrieved. Only invocation results have been sent to SM!");
						// Return data retrieved to the SM
						return results.getData();
					}
					
					//Check everything is fine
					Iterator<SLACalculation> slaIterator = receivedSLAs.iterator();
					while (slaIterator.hasNext())
					{
						SLACalculation currentSLA = slaIterator.next();
						if (currentSLA.getQoSparamsNoFulfill()>0)
						{
							//SLA has been violated -> Notify SM
							ServiceSMClient mySMClient = ServiceSMClient.instance();
							mySMClient.notifySLAData(idFeatureService);
							break;
						}
					}
					
				}
				
				// Return data retrieved to the SM
				return results.getData();				
			}			
		}
		
		return null;
	}	
	
	public boolean setData (String idFeatureService, String value)
	{
		String idApp = myServCatalog.getServiceFromFeatureId(idFeatureService);
		Application theApp = myServCatalog.getApplication(idApp);
		ArrayList<FeatureService> features = theApp.getFeatures();
		Iterator<FeatureService> myIter = features.iterator();	
		boolean result = true;
		while (myIter.hasNext())
		{
			// Invoke thing services for each feature
			FeatureService currentFeature = myIter.next();
			if (currentFeature.getFeatureServiceId().equalsIgnoreCase(idFeatureService))
			{				
				// If info mode is PUT, we send data to the thing services
				if (currentFeature.getType()==FeatureService.PUT)
				{					
					// Keep the list of results for applying the operator									
					Iterator<String> invocations = currentFeature.getThingServices().iterator();					
					while (invocations.hasNext())
					{
						// Register Thing Service in the Trust Manager for increasing priority
						String currentId = invocations.next();
						tmClient.registerThingsService(currentId);
						
						// Invoke the Thing Service and put the result in the result structure	
						boolean received = setDataToThingService(currentId, value);
						logger.debug("Result: " + received);				
						result = result & received;
						logger.debug("Result added.");
					}
				}
				else
				{
					logger.error("Features in other mode should not be called in PUT mode!!");
					return false;
				}
												
				logger.debug("Invocations finished! -> Global result: " + result);
				return result;				
			}			
		}
		
		logger.error("Features was not found and data could not be set!");
		return false;	
	}
	
	public boolean subscribeFeatureService (String idFeatureService)
	{
		logger.info("Starting the process of subscription to " + idFeatureService + "...");
		String idApp = myServCatalog.getServiceFromFeatureId(idFeatureService);
		Application theApp = myServCatalog.getApplication(idApp);
		ArrayList<FeatureService> features = theApp.getFeatures();
		Iterator<FeatureService> myIter = features.iterator();	
		
		while (myIter.hasNext())
		{
			// Invoke thing services subscription for the feature
			FeatureService currentFeature = myIter.next();
			if (currentFeature.getFeatureServiceId().equalsIgnoreCase(idFeatureService))
			{				
				// If info mode is PUSH, we subscribe to all the thing services in the list
				int featureType = currentFeature.getType();
				if (featureType==FeatureService.RTPUSH || featureType==FeatureService.NRTPUSH)
				{
					// In case of RT, first, we invoke QoS Manager, in order to know if the Thing Services lists must be updated
					if (currentFeature.getType()==FeatureService.RTPUSH)
					{				
						logger.info("Real-Time Push requested. Invoking to the QoSManager...");
						ArrayList<String> thingServList = myQoSClient.registerServiceQoSPush(idFeatureService, currentFeature.getThingServices(), currentFeature.getEquivalents());
						currentFeature.setThingServices(thingServList);
						logger.debug("List of Thing Services updated with the QoS Manager! " + thingServList.size() + " TSs to be used.");
					}				
					
					// Retrieve the period and prepare list of thing services
					// TODO it may change, if the period is calculated by the QoS Manager, instead of the ThingsAdaptor
					int thePeriod = currentFeature.getPeriod();
					
					// Clean previous feature subscriptions and subscribe for required Thing Services
					PushManager.instance(localGateway).requestFeature(idFeatureService, currentFeature.getOperator());
					Iterator<String> invocations = currentFeature.getThingServices().iterator();			
					while (invocations.hasNext())
					{
						// Register Thing Service in the Trust Manager for increasing priority
						String currentId = invocations.next();
						tmClient.registerThingsService(currentId);
						
						if (currentFeature.getType()==FeatureService.RTPUSH)
						{
							// Prepare Real-Time subscription and register it	
							registerSubscription(currentId, idFeatureService, thePeriod, true, "localhost");
							logger.info("Real-Time subscription to " + currentId + " processed!");							
						}
						else
						{
							// Prepare Non Real-Time subscription and register it
							registerSubscription(currentId, idFeatureService, thePeriod, false, "localhost");
							logger.info("Non real-time subscription to " + currentId + " processed!");							
						}						
						
						logger.debug("Result added.");
					}
				}
				else
				{
					logger.error("Features in other mode should not be called in PUSH mode!!");
					return false;
				}
			}
		}
		return true;
	}
	
	public boolean unsubscribeFeature (String idFeatureService)
	{
		//TODO detect when a thing service isn't local, so we can remove remote subscriptions as well
		return PushManager.instance(localGateway).removeSubscription(idFeatureService);
	}
}
