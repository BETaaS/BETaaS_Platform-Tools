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
Sergio García Villalonga. Atos Research and Innovation, Atos, SPAIN SA
@email sergio.garciavillalonga@atos.net
**/

package eu.betaas.taas.taasresourcesmanager.endpointsmanager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.log4j.Logger;

import eu.betaas.taas.bigdatamanager.database.service.ThingsData;
import eu.betaas.taas.qosmanager.monitoring.api.impl.SLACalculation;
import eu.betaas.taas.taasresourcesmanager.catalogs.ResourcesCatalog;
import eu.betaas.taas.taasresourcesmanager.taasrmclient.AdaptTAClient;
import eu.betaas.taas.taasresourcesmanager.taasrmclient.ServiceSMClient;
import eu.betaas.taas.taasresourcesmanager.taasrmclient.TaaSQoSMClient;
import eu.betaas.taas.taasresourcesmanager.taasrmclient.TaaSRMClient;



public class PushManager 
{
	private Logger logger= Logger.getLogger("betaas.taas");
	private String localGateway;
	static private PushManager _instance = null;
	private TaaSQoSMClient myQoSClient;
	
	private HashMap<String, ArrayList<Subscription>> rtSubsList;
	private HashMap<String, ArrayList<Subscription>> nrtSubsList;
	
	private HashMap<String, ActiveFeature> activeFeatures;
	
	private PushManager (String gateway)
	{			
		localGateway = gateway;
		rtSubsList = new HashMap<String, ArrayList<Subscription>>();
		nrtSubsList = new HashMap<String, ArrayList<Subscription>>();
		activeFeatures = new HashMap<String, ActiveFeature>();
		myQoSClient = TaaSQoSMClient.instance();
	}
	
	public static synchronized PushManager instance(String gateway) 
	{
		if (null == _instance) 
		{
			_instance = new PushManager(gateway);			
			Logger myLogger= Logger.getLogger("betaas.taas");
			myLogger.info("A new instance of the Push Manager was created!");
		}
		return _instance;
	}
	
	public boolean receiveRemoteTAMeasurement (String thingServiceID, ThingsData data, String featureId)
	{
		// Check the feature identifier is valid
		if (!activeFeatures.containsKey(featureId))
		{
			logger.error("The remote measurement received is not valid!! -> " + featureId);
			return false;
		}
		
		//Get the corresponding ActiveFeature
		ActiveFeature currentActFeature = activeFeatures.get(featureId);
		currentActFeature.notifyReceived(thingServiceID, data);
		
		//Send local notification (to the SM)
		ServiceSMClient mySMClient = ServiceSMClient.instance();				
		boolean done = mySMClient.notifyServiceData(currentActFeature.getFeatureResult(), currentActFeature.getFeatureServiceId());
		logger.info("Local notification with remote data sent to SM!");
		
		return done;
	}
	
	public boolean receiveTAMeasurement(String thingServiceID, ThingsData data)
	{		
		return receiveMeasurement(thingServiceID, data, rtSubsList);
	}
	
	public boolean receiveCMMeasurement(String thingServiceID, ThingsData data)
	{
		//TODO change this after demo!
		return receiveMeasurement(thingServiceID, data, rtSubsList);
		//return receiveMeasurement(thingServiceID, data, nrtSubsList);
	}
	
	public void addSubscription (Subscription newSubscription, boolean realTime)
	{
		//Determine the correct list
		if (realTime)
		{
			//Retrieve the adequate list for the thing service
			ArrayList<Subscription> currentList = rtSubsList.get(newSubscription.getThingServiceId());
			if (currentList == null)
			{
				currentList = new ArrayList<Subscription>();
			}
			//Add the new subscription, sort and put the new list
			currentList.add(newSubscription);
			Collections.sort(currentList);
			rtSubsList.put(newSubscription.getThingServiceId(), currentList);
			logger.info("Real time subscription added in the PushManager for: " + newSubscription.getThingServiceId());
			
			//Activate SLA monitoring
			myQoSClient.activateSLAPushMonitoring(newSubscription.getThingServiceId(), (int)newSubscription.getPeriod());
			logger.info("SLA activated for thing service: " + newSubscription.getThingServiceId());
		}
		else
		{
			//Retrieve the adequate list for the thing service
			ArrayList<Subscription> currentList = nrtSubsList.get(newSubscription.getThingServiceId());
			if (currentList == null)
			{
				currentList = new ArrayList<Subscription>();
			}
			//Add the new subscription, sort and put the new list
			currentList.add(newSubscription);
			Collections.sort(currentList);
			nrtSubsList.put(newSubscription.getThingServiceId(), currentList);
			logger.info("Non real time subscription added in the PushManager for: " + newSubscription.getThingServiceId());
		}
		
		//Manage the active feature
		ActiveFeature theFeature = activeFeatures.get(newSubscription.getApplicationId());
		theFeature.addSubscription(newSubscription.getThingServiceId());
		activeFeatures.put(newSubscription.getApplicationId(), theFeature);
		logger.info("Active features list updated!");
	}
	
	public void requestFeature (String idFeature, String operation)
	{
		// Create the new active feature only if it doesn't exist
		if (!activeFeatures.containsKey(idFeature))
		{
			activeFeatures.put(idFeature, new ActiveFeature(idFeature, operation));
		}
		else
		{
			//activeFeatures.get(idFeature).clean();
		}
	}
	
	public boolean removeSubscription (String idFeature)
	{
		if (activeFeatures.containsKey(idFeature))
		{
			//Retrieve thing services to unsubscribe from the Active Feature
			ActiveFeature myFeature = activeFeatures.get(idFeature);
			ArrayList<String> listToRemove = myFeature.getThingServicesList();
			
			//Look for each thing service subscription, in order to remove it
			logger.info("Removing subscriptions for feature: " + idFeature);
			for (int i=0; i<listToRemove.size(); i++)
			{
				String currentThingServiceId = listToRemove.get(i);
				ArrayList<Subscription> subsList = rtSubsList.get(currentThingServiceId);
				int position = 0;
				
				//Retrieve all subscriptions for one thing service 
				Iterator<Subscription> myIter = subsList.iterator();
				while (myIter.hasNext())
				{
					//Check if the current subscription corresponds to our feature
					if (myIter.next().getApplicationId().equalsIgnoreCase(idFeature))
					{
						//Remove and stop the iteration
						logger.debug("Subscription to " + currentThingServiceId + " removed!");
						subsList.remove(position);
						break;
					}
					position++;
				}
				
				//If all subscriptions for a thing service were removed, remove the TS from the maps and TA subs
				if (subsList.size()==0)
				{
					logger.info("Thing Service " + currentThingServiceId + " has no more active subscriptions -> Removing all data!");
					rtSubsList.remove(currentThingServiceId);
					AdaptTAClient taClient = AdaptTAClient.instance();
					String idThing = ResourcesCatalog.instance().getResource(currentThingServiceId).getPhysicalResourceId();
					taClient.unSubscribeToThing(idThing);
				}
			}
			
			//Once subscriptions are removed, remove ActiveFeature
			activeFeatures.remove(idFeature);
			
			//Finalize operation
			logger.info("Operation completed!");
			return true;
		}
		
		// If the feature is not active, the operation can't be done
		logger.error("No active subscriptions were found for feature " + idFeature);
		return false;
	}
	
	private boolean receiveMeasurement (String thingServiceID, ThingsData data, HashMap<String, ArrayList<Subscription>> subscriptionList) {
		
		//Get current date		
		Date nowDate = Calendar.getInstance().getTime();
		boolean done = false;
		
		if (subscriptionList == rtSubsList) {
			logger.info("Real time measurement received");
		} else if (subscriptionList == nrtSubsList) {
			logger.info("Non real time measurement received");
		}
		
		logger.debug("Looking for subscriptions waiting for notifications");
		//Retrieve appropriate subscription element
		ArrayList<Subscription> currentList = subscriptionList.get(thingServiceID);
		ArrayList<Subscription> notifList = new ArrayList<Subscription>();
		Iterator<Subscription> myIter = currentList.iterator();
		while (myIter.hasNext() && !done)
		{
			Subscription currentSubs = myIter.next();
			logger.debug("Checking expected date for: " + currentSubs.getThingServiceId());
			if (currentSubs.getExpectedDate().compareTo(nowDate)<=0)
			{
				//Get subscriptions waiting for notifications
				currentSubs.setReceived();
				notifList.add(currentSubs);				
				logger.info("Get subscriptions waiting for notifications");
			}
			else
			{
				//Too early for more notifications
				//break;
				done = true;
				logger.info("Too early for more notifications");
			}
		}
		
		logger.info("Found " + notifList.size() + " subscriptions");
		
		//Sort again the subscriptions list
		Collections.sort(currentList);
		
		//Send notifications
		myIter = notifList.iterator();
		while (myIter.hasNext())
		{
			Subscription currentNotif = myIter.next();
			//Check application location
			if (currentNotif.getApplicationLocation().equalsIgnoreCase("localhost"))
			{
				//Get the corresponding ActiveFeature
				ActiveFeature currentActFeature = activeFeatures.get(currentNotif.getApplicationId());
				currentActFeature.notifyReceived(thingServiceID, data);
				
				//Send local notification (to the SM)
				ServiceSMClient mySMClient = ServiceSMClient.instance();				
				done = done & mySMClient.notifyServiceData(currentActFeature.getFeatureResult(), currentActFeature.getFeatureServiceId());
				logger.debug("Data sent: " + currentActFeature.getFeatureResult().getData());				
				logger.info("Local notification sent");			
				
				//Calculate SLA
				SLACalculation currentSLA = myQoSClient.retrieveSLAPushCalculation(currentNotif.getThingServiceId(), currentNotif.getLastResponseTime());
				logger.info("SLA retrieved for the thing service " + currentNotif.getThingServiceId() + " -> RT=" + currentNotif.getLastResponseTime() + " secs.");
				if (currentSLA.getQoSparamsNoFulfill()>0)
				{
					//SLA has been violated -> Notify SM					
					mySMClient.notifySLAData(currentActFeature.getFeatureServiceId());					
				}				
			}
			else
			{
				//Send remote notification to another gateway (to a remote TaaSRM)
				TaaSRMClient myRMCli = TaaSRMClient.instance(localGateway, "PushManager");					
				done = done & myRMCli.remoteDataNotification(data, currentNotif.getApplicationId(), thingServiceID, currentNotif.getApplicationLocation());
				logger.info("Remote notification sent");
			}
		}
				
		return done;
	}
}
