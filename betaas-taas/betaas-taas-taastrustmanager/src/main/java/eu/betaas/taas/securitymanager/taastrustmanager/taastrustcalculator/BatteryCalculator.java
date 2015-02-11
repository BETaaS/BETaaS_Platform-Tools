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

import org.apache.log4j.Logger;

import eu.betaas.taas.securitymanager.taastrustmanager.taasproxy.TaaSBDMClient;
import eu.betaas.taas.securitymanager.taastrustmanager.taasproxy.TaaSCMClient;
import eu.betaas.taas.securitymanager.taastrustmanager.taasproxy.ThingTrust;
import eu.betaas.taas.securitymanager.taastrustmanager.taasproxy.ThingTrustData;

public class BatteryCalculator 
{

	private Logger logger= Logger.getLogger("betaas.taas");
		
	private TaaSBDMClient myBDMClient;
	private Double[] tsData;	
	private TaaSCMClient myCM;
	private String thingId;
	
	public BatteryCalculator ()
	{		
		//myBDMClient = TaaSBDMClient.instance();
	}
	
	public float calculateTrustAspect (String thingServiceId)
	{		
		// Check whether data is enough
		if (tsData == null || tsData.length < 2)
		{
			logger.debug ("Not enough data is available for forecasting -> Returning default value.");
			return 2.0f;
		}
		
		// Determine future battery level in the following minutes
		ExponentialSmoothingCalculator myCalculator = new ExponentialSmoothingCalculator();
		double result = myCalculator.doubleExponentialSmoothing(0.5, 0.1, tsData, 10);
		logger.debug ("Battery forecast for " + thingServiceId + ": " + result);
		
		// If device is plugged in or battery is higher than 90% trust is maximum
		if (result>=90.00)
		{
			return 5.0f;
		}		
		// If battery is below 5%, then we calculate the time until a re-charge is detected
		else if (result<=10.0)
		{
			Double[] valuesList = retrieveDataBatteryCharge();
			double rechargeForecast = myCalculator.simpleExponentialSmoothing(0.5, valuesList);
			logger.debug ("Re-charge forecast: " + rechargeForecast);
			if (rechargeForecast>600)
			{
				// If re-charge isn't done before 10 minutes, then this is very problematic
				return 0.01f;
			}
			else
			{
				// Otherwise, we give a value related to the time it takes the charge to start
				return (float) ((600.0-rechargeForecast)/600.0);
			}
			
		}
		
		// In any other case, provide the level of the battery			
		return (float)(result / 20.0);
	}
	
	public void setPreOperationData(ThingTrust information)
	{
		logger.debug("Converting Thing Service Data...");
				
		// Retrieve last data		
		ArrayList<ThingTrustData> baseData = information.getDataList();
		tsData = new Double[baseData.size()];		
		// Build doubles list of battery level
		for (int i=0; i < baseData.size(); i++)
		{
			tsData[i] = new Double (baseData.get(i).getBatteryLevel());
		}
				
		// Retrieve thing identifier
		thingId = information.getThingId();		
	}
	
	private Double[] retrieveDataBatteryCharge ()
	{
		//Newer values go first
		return new Double[] {610.0, 610.0, 580.45, 601.0, 560.0, 580.0, 590.0, 575.0, 580.0, 580.0, 588.0, 568.0};
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
		
		BatteryCalculator myCalc = new BatteryCalculator ();
		
		ThingTrust myThing = new ThingTrust ("javiPru");
		myThing.setUnits("boolean");
		ThingTrustData data1 = new ThingTrustData (null, "false", "8", "12");
		myThing.addData(data1);
		ThingTrustData data2 = new ThingTrustData (null, "false", "8", "13");
		myThing.addData(data2);
		ThingTrustData data3 = new ThingTrustData (null, "false", "7", "13");
		myThing.addData(data3);
		ThingTrustData data4 = new ThingTrustData (null, "true", "7", "14");
		myThing.addData(data4);
		ThingTrustData data5 = new ThingTrustData (null, "true", "7", "14");
		myThing.addData(data5);
		ThingTrustData data6 = new ThingTrustData (null, "true", "7", "14");
		myThing.addData(data6);
		ThingTrustData data7 = new ThingTrustData (null, "true", "7", "15");
		myThing.addData(data7);
		ThingTrustData data8 = new ThingTrustData (null, "true", "7", "15");
		myThing.addData(data8);
		
		myCalc.setPreOperationData(myThing);
		System.out.println (myCalc.calculateTrustAspect("javiPru"));
	}
	*/
}
