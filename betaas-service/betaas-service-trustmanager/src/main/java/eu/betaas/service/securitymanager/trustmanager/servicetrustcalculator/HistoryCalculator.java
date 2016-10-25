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

import org.apache.log4j.Logger;

import eu.betaas.service.securitymanager.trustmanager.serviceproxy.GatewayInfo;
import eu.betaas.service.securitymanager.trustmanager.serviceproxy.TaaSBDMClient;

public class HistoryCalculator 
{

	private Logger logger= Logger.getLogger("betaas.taas");
	private TaaSBDMClient bdmClient;
		
	public HistoryCalculator()
	{
		bdmClient = TaaSBDMClient.instance(); 				
	}

	public float calculateTrustAspect (String gatewayId)
	{		
		// 1 - Retrieve interaction information about the Gateway			
		GatewayInfo gwInfo = getBasicInfo(gatewayId);		 
		int r = gwInfo.getKnownSuccess();
		int total = gwInfo.getTotalInteractions();
		int s = gwInfo.getKnownFailures();
		int n = total - s - r;		
		
		if (r==-1 || s==-1 || n==-1)
		{
			logger.error("No interactions information was retrieved for " + gatewayId);
			return 2.5f;
		}
		logger.debug("Interactions obtained! Positive interactions: " + r + " out of " + total);
				
		// 2 - Calculate each parameter
		double belief = r / total;
		double disbelief = s / total;
		double uncertainty = n / total;
		double atomicity = 1;
		
		// 3 - Create and evaluate Opinion Model
		OpinionModel myOpinion = new OpinionModel(belief, disbelief, uncertainty, atomicity);
		
		// 4 - Retrieve evaluation of the Expectation
		return (float)myOpinion.getExpectation();		
	}
	
	private GatewayInfo getBasicInfo (String idGateway)
	{
		return new GatewayInfo(idGateway);
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
		
		HistoryCalculator myCalc = new HistoryCalculator ();
		
		GatewayInfo myThing = new GatewayInfo ("javiPru");
		myThing.setUnits("boolean");
		GatewayTrustData data1 = new GatewayTrustData (null, "false", "8", "12");
		myThing.addData(data1);
		GatewayTrustData data2 = new GatewayTrustData (null, "false", "8", "13");
		myThing.addData(data2);
		GatewayTrustData data3 = new GatewayTrustData (null, "false", "7", "13");
		myThing.addData(data3);
		GatewayTrustData data4 = new GatewayTrustData (null, "true", "7", "14");
		myThing.addData(data4);
		GatewayTrustData data5 = new GatewayTrustData (null, "true", "7", "14");
		myThing.addData(data5);
		GatewayTrustData data6 = new GatewayTrustData (null, "true", "7", "14");
		myThing.addData(data6);
		GatewayTrustData data7 = new GatewayTrustData (null, "true", "7", "15");
		myThing.addData(data7);
		GatewayTrustData data8 = new GatewayTrustData (null, "true", "7", "15");
		myThing.addData(data8);
		
		myCalc.setPreOperationData(myThing);
		myCalc.calculateTrustAspect("javiPru");
	}
	*/
}
