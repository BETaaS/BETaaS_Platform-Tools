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

package eu.betaas.service.securitymanager.trustmanager.servicetrustcalculator;

import java.util.HashMap;

import org.apache.log4j.Logger;

import eu.betaas.service.securitymanager.trustmanager.serviceaggregator.FuzzyCalculator;

public class ReputationCalculator 
{

	private Logger logger= Logger.getLogger("betaas.taas");
	
	public ReputationCalculator()
	{
		
	}
	
	public float calculateTrustAspect (String thingServiceId)
	{
		// Step 1 - Retrieve security data
		// Right now, we don't use any encryption or security measurement, so we put some fixed values
		Double algorithm = new Double ("4.0");
		Double key = new Double ("4.0");
		Double certificates = new Double ("3.0");
		
		// Step 2 - Evaluate fuzzy model
		FuzzyCalculator myCalculator = new FuzzyCalculator ("Security.fcl");		
		HashMap<String, Double> myHash = new HashMap<String, Double>();
		myHash.put("algorithm", algorithm);
		myHash.put("key", key);
		myHash.put("certificates", certificates);		
		float result = myCalculator.calculateTrustAggregation(myHash);
		logger.debug ("Received value: " + result);
				
		return result;
	}
	
	/*
	public static void main(String[] args) 
	{
		SimpleLayout layout = new SimpleLayout();
		ConsoleAppender appender = new ConsoleAppender(new SimpleLayout());		
		Logger.getRootLogger().addAppender(appender);
		Logger.getRootLogger().setLevel((Level) Level.WARN);
		Logger.getLogger("betaas.taas").addAppender(appender);
		Logger.getLogger("betaas.taas").setLevel((Level) Level.WARN);
		
		ReputationCalculator myCalc = new ReputationCalculator ();
		System.out.println (myCalc.calculateTrustAspect("javiPru"));
	}
	*/
	
}
