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

Authors Contact:
Francisco Javier Nieto. Atos Research and Innovation, Atos SPAIN SA
@email francisco.nieto@atos.net
**/

package eu.betaas.taas.securitymanager.taastrustmanager.taasaggregator;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;

import eu.betaas.taas.bigdatamanager.database.hibernate.data.ThingInformation;
import eu.betaas.taas.securitymanager.taastrustmanager.taasproxy.TaaSBDMClient;
import eu.betaas.taas.securitymanager.taastrustmanager.taasproxy.TaaSCMClient;
import eu.betaas.taas.securitymanager.taastrustmanager.taasproxy.ThingLocation;
import eu.betaas.taas.securitymanager.taastrustmanager.taasproxy.ThingTrust;
import eu.betaas.taas.securitymanager.taastrustmanager.taasproxy.ThingTrustData;
import eu.betaas.taas.securitymanager.taastrustmanager.taastrustcalculator.BatteryCalculator;
import eu.betaas.taas.securitymanager.taastrustmanager.taastrustcalculator.DataStabilityCalculator;
import eu.betaas.taas.securitymanager.taastrustmanager.taastrustcalculator.QoSFulfillmentCalculator;
import eu.betaas.taas.securitymanager.taastrustmanager.taastrustcalculator.ScalabilityCalculator;
import eu.betaas.taas.securitymanager.taastrustmanager.taastrustcalculator.SecurityMechanismsCalculator;

public class ThingServiceTrustCalculator 
{
	private Logger logger= Logger.getLogger("betaas.taas");
	private TaaSBDMClient myBDMClient;
	private TaaSCMClient myCM;
	private QoSFulfillmentCalculator qosCalculator;
	private BatteryCalculator batteryCalculator;
	private DataStabilityCalculator dataCalculator;
	private ScalabilityCalculator scalabilityCalculator;
	private SecurityMechanismsCalculator securityCalculator;
	
	public ThingServiceTrustCalculator ()
	{
		myCM = TaaSCMClient.instance();
		myBDMClient = TaaSBDMClient.instance();
		qosCalculator = new QoSFulfillmentCalculator();
		
		
	}
	
	public ThingServiceTrust calculateThingServiceTrust (String thingServiceId)
	{
		// Step 1 -> Check the Thing Service exists and retrieve basic data
		ThingTrust basicData = retrieveBasicData(thingServiceId);
		if (basicData==null)
		{
			// If basic data isn't available, just return default value and don't store data
			logger.info("Not enough basic data found! Providing default values!");
			return new ThingServiceTrust(thingServiceId, 2.5, 2.5, 2.5, 2.5, 2.5, 2.5, 2.5);
		}
		
		logger.debug("Starting Calculators...");
		securityCalculator = new SecurityMechanismsCalculator();		
		batteryCalculator = new BatteryCalculator();		
		dataCalculator = new DataStabilityCalculator();		
		scalabilityCalculator = new ScalabilityCalculator();		
		logger.debug("Calculators Ready! Starting calculations...");
		
		// Step 2 -> Calculate Security Mechanisms
		float security = securityCalculator.calculateTrustAspect(thingServiceId);
		logger.debug("Security parameter calculated! -> " + security);
				
		// Step 3 -> Calculate QoS fulfillment		
		float qos = qosCalculator.calculateTrustAspect(thingServiceId);
		logger.debug("QoS parameter calculated! -> " + qos);
		
		// Step 4 -> Calculate Dependability
		float dependability = 2.5f;
		logger.debug("Dependability parameter calculated! -> " + dependability);
		
		// Step 5 -> Calculate Scalability
		scalabilityCalculator.setPreOperationData(basicData);
		float scalability = scalabilityCalculator.calculateTrustAspect(thingServiceId);
		logger.debug("Scalability parameter calculated! -> " + scalability);
		
		// Step 6 -> Calculate Battery Load
		batteryCalculator.setPreOperationData(basicData);
		float battery = batteryCalculator.calculateTrustAspect(thingServiceId);
		logger.debug("Battery parameter calculated! -> " + battery);
		
		// Step 7 -> Calculate Data Stability
		dataCalculator.setPreOperationData(basicData);
		float data = dataCalculator.calculateTrustAspect(thingServiceId);
		logger.debug("Data parameter calculated! -> " + data);
				
		// Step 8 -> Calculate fuzzy aggregation
		FuzzyCalculator myCalculator = new FuzzyCalculator ("ThingServices.fcl");		
		HashMap<String, Double> myHash = new HashMap<String, Double>();
		myHash.put("security", new Double(security));
		myHash.put("qos", new Double(qos));
		myHash.put("dependability", new Double(dependability));
		myHash.put("scalability", new Double(scalability));	
		myHash.put("battery", new Double(battery));	
		myHash.put("data", new Double(data));
		float result = myCalculator.calculateTrustAggregation(myHash);
		logger.debug ("Received value: " + result);
		
		// Step 9 -> Generate trust result
		ThingServiceTrust fullTrustResult = new ThingServiceTrust(thingServiceId, security, qos, dependability, scalability, battery, data, result);
		
		// Step 10 -> Store generated information
		myBDMClient.storeTrustData(fullTrustResult);
		
		return fullTrustResult;
	}
	
	private ThingTrust retrieveBasicData(String thingServiceId)
	{
		ThingTrust basicData = new ThingTrust (thingServiceId);
		logger.debug("Retrieving basic data for improving performance...");
		
		// Retrieving thing identifier for that thing service
		String thingId = myCM.retrieveThingIdentifier(thingServiceId);
		if (thingId == null || thingId.equalsIgnoreCase(""))
		{
			logger.error ("It was not possible to retrieve basic data for thing service " + thingServiceId);
			return null;
		}
		basicData.setThingId(thingId);
		
		// Retrieve all basic data: units
		ThingInformation fullInfo = myBDMClient.getThingInformation(thingId);
		if (fullInfo==null)
		{
			logger.error ("It was not possible to retrieve basic data for thing info " + thingId);
			return null;
		}
		basicData.setUnits(fullInfo.getUnit());
		String type = fullInfo.getType();
				
		// Retrieve last data generated
		ArrayList<ThingTrustData> thingDataList = myBDMClient.getThingData(thingId);
		basicData.setDataList(thingDataList);
		logger.debug ("Thing Data in the list received: " + thingDataList.size());
		
		// Retrieve equivalent thing services by using location
		ThingLocation location = myBDMClient.getThingLastLocation(thingId);
		if (location==null)
		{
			logger.error ("It was not possible to retrieve thing location for equivalents " + thingServiceId);
			return null;
		}
		ArrayList<String> equivalentsList = myCM.getEquivalentThingServices(type, location, thingServiceId);
		if (equivalentsList!=null && equivalentsList.size() > 0)
		{
			for (int j=0; j<equivalentsList.size(); j++)
			{
				// Retrieve things ids, since they are the useful ones
				basicData.addEquivalent(myCM.retrieveThingIdentifier(equivalentsList.get(j)));
			}	
		}			
		logger.debug ("Equivalent Thing Services received: " + equivalentsList.size());
		
		logger.debug("Basic data retrieval finished!");
		
		return basicData;
	}
}
