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

package eu.betaas.taas.securitymanager.taastrustmanager.taastrustcalculator;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;

import eu.betaas.taas.qosmanager.monitoring.api.impl.SLACalculation;
import eu.betaas.taas.securitymanager.taastrustmanager.taasproxy.TaaSQoSMClient;

public class QoSFulfillmentCalculator 
{
	private Logger logger= Logger.getLogger("betaas.taas");
	private TaaSQoSMClient qosmClient;
	private static HashMap<String, ArrayList<Integer>> slaHistory;
	
	public QoSFulfillmentCalculator()
	{
		qosmClient = TaaSQoSMClient.instance(); 
		if (slaHistory==null)	slaHistory = new HashMap<String, ArrayList<Integer>>();
		
	}

	public float calculateTrustAspect (String thingServiceId)
	{		
		// 1 - Retrieve QoS information about the Thing Service			
		SLACalculation myQoS = qosmClient.retrieveSLACalculations(thingServiceId);
		if (myQoS==null)
		{
			logger.error("No QoS information was retrieved for " + thingServiceId);
			return 2.5f;
		}
		logger.debug("SLA Calculations obtained! Fulfilled: " + myQoS.getQoSparamsFulfill());
		ArrayList<Integer> tsHistory = new ArrayList<Integer>();
		
		// Add the new QoS fulfillment to the history we keep			
		if (slaHistory.containsKey(thingServiceId))
		{
			tsHistory = slaHistory.get(thingServiceId);
		}			
		tsHistory.add(myQoS.getQoSparamsFulfill());
		slaHistory.put(thingServiceId, tsHistory);
		logger.debug("Number of records gathered: " + tsHistory.size());
		
		// 2 - Perform the hypothesis test for the QoS fulfillment variance
		double[] valuesList = new double[tsHistory.size()];
		for (int i=0; i < tsHistory.size(); i++)
		{
			valuesList[i] = tsHistory.get(i);
		}
		StatisticsCalculator myCalc = new StatisticsCalculator();
		boolean testResult = myCalc.calculateNumericVariance(valuesList, 0.05);
		logger.debug("Is the variance of SLA as expected? " + testResult);
		
		double uncertainty = 0.05;
		if (!testResult)
		{
			//Calculate the real p-value of the model
			uncertainty = 0.18;
		}
		
		// 3 - Create and evaluate Opinion Model
		OpinionModel myOpinion = new OpinionModel();
		myOpinion.setUncertainties(uncertainty);
		myOpinion.setNegativeEvidences(myQoS.getQoSparamsNoFulfill());
		myOpinion.setPositiveEvidences(myQoS.getQoSparamsFulfill());
		myOpinion.reCalculateModel();
		
		// 4 - Retrieve evaluation of the Expectation
		return (float)myOpinion.getExpectation();		
	}
}
