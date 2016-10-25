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

import java.util.ArrayList;

import org.apache.commons.math3.stat.StatUtils;
import org.apache.log4j.Logger;

import eu.betaas.service.securitymanager.trustmanager.serviceproxy.GatewayInfo;
import eu.betaas.service.securitymanager.trustmanager.serviceproxy.GatewayTrustData;

public class PathCalculator 
{
	private Logger logger= Logger.getLogger("betaas.taas");	
			
	public PathCalculator ()
	{
		
	}
	
	public float calculateTrustAspect (String thingServiceId)
	{
		// 1 - Obtain network topology
		
		
		// 2 - Calculate Dijkstra
		
		
		return 2.5f;
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
		
		PathCalculator myCalc = new PathCalculator ();
		
		GatewayInfo myThing = new GatewayInfo ("javiPru");
		myThing.setUnits("boolean");
		GatewayTrustData data1 = new GatewayTrustData (null, "false", "8", "12");
		myThing.addData(data1);
		GatewayTrustData data2 = new GatewayTrustData (null, "false", "8", "12");
		myThing.addData(data2);
		GatewayTrustData data3 = new GatewayTrustData (null, "false", "7", "13");
		myThing.addData(data3);
		GatewayTrustData data4 = new GatewayTrustData (null, "true", "7", "13");
		myThing.addData(data4);
		GatewayTrustData data5 = new GatewayTrustData (null, "true", "7", "13");
		myThing.addData(data5);
		GatewayTrustData data6 = new GatewayTrustData (null, "true", "7", "13");
		myThing.addData(data6);
		GatewayTrustData data7 = new GatewayTrustData (null, "true", "7", "14");
		myThing.addData(data7);
		GatewayTrustData data8 = new GatewayTrustData (null, "true", "7", "14");
		myThing.addData(data8);
		GatewayTrustData data9 = new GatewayTrustData (null, "true", "7", "14");
		myThing.addData(data9);
		GatewayTrustData data10 = new GatewayTrustData (null, "true", "6", "14");
		myThing.addData(data10);
		
		myCalc.setPreOperationData(myThing);
		System.out.println (myCalc.calculateTrustAspect("javiPru"));
	}
	*/
}
