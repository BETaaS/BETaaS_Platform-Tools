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

package eu.betaas.service.securitymanager.trustmanager.serviceaggregator;

import java.util.HashMap;

import org.apache.log4j.Logger;

import eu.betaas.service.securitymanager.trustmanager.serviceproxy.GatewayInfo;
import eu.betaas.service.securitymanager.trustmanager.serviceproxy.TaaSBDMClient;
import eu.betaas.service.securitymanager.trustmanager.serviceproxy.TaaSCMClient;
import eu.betaas.service.securitymanager.trustmanager.servicetrustcalculator.DependabilityCalculator;
import eu.betaas.service.securitymanager.trustmanager.servicetrustcalculator.EnergyCalculator;
import eu.betaas.service.securitymanager.trustmanager.servicetrustcalculator.HistoryCalculator;
import eu.betaas.service.securitymanager.trustmanager.servicetrustcalculator.PathCalculator;
import eu.betaas.service.securitymanager.trustmanager.servicetrustcalculator.ReputationCalculator;

public class GatewayTrustCalculator 
{
	private Logger logger= Logger.getLogger("betaas.service");
	private TaaSBDMClient myBDMClient;
	private TaaSCMClient myCM;
	private EnergyCalculator energyCalculator;
	private HistoryCalculator historyCalculator;
	private DependabilityCalculator dependabilityCalculator;
	private PathCalculator pathCalculator;
	private ReputationCalculator reputationCalculator;
	
	public GatewayTrustCalculator ()
	{
		myCM = TaaSCMClient.instance();
		myBDMClient = TaaSBDMClient.instance();		
		
	}
	
	public GatewayTrust calculateThingServiceTrust (String gatewayId)
	{
		// Step 1 -> Check the Thing Service exists and retrieve basic data
		GatewayInfo basicData = retrieveBasicData(gatewayId);
		if (basicData==null)
		{
			// If basic data isn't available, just return default value and don't store data
			logger.info("Not enough basic data found! Providing default values!");
			return new GatewayTrust(gatewayId, 2.5, 2.5, 2.5, 2.5, 2.5, 2.5);
		}
		
		logger.debug("Starting Calculators...");
		reputationCalculator = new ReputationCalculator();		
		energyCalculator = new EnergyCalculator();		
		historyCalculator = new HistoryCalculator();		
		pathCalculator = new PathCalculator();		
		logger.debug("Calculators Ready! Starting calculations...");
		
		// Step 2 -> Calculate Interactions History
		float history = historyCalculator.calculateTrustAspect(gatewayId);
		logger.debug("Reputation parameter calculated! -> " + history);
				
		// Step 3 -> Calculate Gateway Reliability		
		float reliability = dependabilityCalculator.calculateTrustAspect(gatewayId);
		logger.debug("Reliability parameter calculated! -> " + reliability);
		
		// Step 4 -> Calculate Path to the Gateway
		float path = pathCalculator.calculateTrustAspect(gatewayId);
		logger.debug("Path parameter calculated! -> " + path);
						
		// Step 5 -> Calculate Energy aspect
		energyCalculator.setPreOperationData(basicData);
		float energy = energyCalculator.calculateTrustAspect(gatewayId);
		logger.debug("Energy parameter calculated! -> " + energy);
		
		// Step 6 -> Calculate Gateway Reputation
		//reputationCalculator.setPreOperationData(basicData);
		float reputation = reputationCalculator.calculateTrustAspect(gatewayId);
		logger.debug("Data parameter calculated! -> " + reputation);
				
		// Step 7 -> Calculate fuzzy aggregation
		FuzzyCalculator myCalculator = new FuzzyCalculator ("Gateways.fcl");		
		HashMap<String, Double> myHash = new HashMap<String, Double>();
		myHash.put("history", new Double(history));
		myHash.put("reliability", new Double(reliability));
		myHash.put("path", new Double(path));
		myHash.put("energy", new Double(energy));	
		myHash.put("reputation", new Double(reputation));			
		float result = myCalculator.calculateTrustAggregation(myHash);
		logger.debug ("Received value: " + result);
		
		// Step 9 -> Generate trust result
		GatewayTrust fullTrustResult = new GatewayTrust(gatewayId, history, reliability, path, energy, reputation, result);
		
		// Step 10 -> Store generated information
		myBDMClient.storeTrustData(fullTrustResult);
		
		return fullTrustResult;
	}
	
	private GatewayInfo retrieveBasicData(String gatewayId)
	{
		GatewayInfo basicData = new GatewayInfo (gatewayId);
		logger.debug("Retrieving basic data for improving performance...");
		
		// Retrieve basic info from the database (for interactions history)
		
		// Contact gateway for other basic info (energy + dependability)
		
		
		
		logger.debug("Basic data retrieval finished!");
		
		return basicData;
	}
}
