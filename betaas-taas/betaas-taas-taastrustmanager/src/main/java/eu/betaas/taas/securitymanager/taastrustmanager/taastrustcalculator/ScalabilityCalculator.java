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

import org.apache.commons.math3.stat.StatUtils;
import org.apache.log4j.Logger;

import eu.betaas.taas.securitymanager.taastrustmanager.taasproxy.ThingTrust;
import eu.betaas.taas.securitymanager.taastrustmanager.taasproxy.ThingTrustData;

public class ScalabilityCalculator 
{
	private Logger logger= Logger.getLogger("betaas.taas");	
	private double[] tsData;	
		
	public ScalabilityCalculator ()
	{
		
	}
	
	public float calculateTrustAspect (String thingServiceId)
	{
		// Check enough data is available
		if (tsData.length<4)
		{
			logger.error("Not enough data is available -> Returning default value.");
			return 2.0f;
		}
		
		// 1 - Scalability of memory in time
		// 1.1 - Calculate slope of the line generated with linear regression
		float slope = linearRegressionSlope (tsData);
		logger.debug("Memory usage slope: " + slope);		
		
		// 1.2 - Calculate the angle corresponding to the slope
		double angle = Math.toDegrees(Math.atan(slope));
		logger.debug("Memory usage angle: " + angle);		
		
		// 1.3 - Determine trust value for that angle
		float memoryTrust = 2.5f;
		if (angle <= 0.0 )
		{
			memoryTrust = 4.8f;
		}
		else if (angle > 65.0)
		{
			memoryTrust = 0.05f;
		}
		else
		{
			memoryTrust = ((66.0f - (float)angle) * 4.5f) / 66.0f;
		}
		logger.debug("Memory Trust: " + memoryTrust);		
		
		// 2 - Scalability of response time as requests grow
		
		return memoryTrust;
	}
	
	public void setPreOperationData(ThingTrust information)
	{
		logger.debug("Converting Thing Service Data...");
				
		// Retrieve last data		
		ArrayList<ThingTrustData> baseData = information.getDataList();
		tsData = new double[baseData.size()];		
		// Build doubles list of memory level
		for (int i=0; i < baseData.size(); i++)
		{
			tsData[i] = Double.parseDouble(baseData.get(i).getMemoryLevel());
		}		
	}
	
	private float linearRegressionSlope (double[] data)
	{
		double sumXiYi = 0;		
		double sumXi = 0;
		double sumYi = 0;
		double sumXi2 = 0;
		double [] xList = new double[data.length];
		
		logger.debug ("Calculating linear regression...");
		
		for (int i=0; i<data.length; i++)
		{
			sumXi = sumXi + (double)i;			
			sumXi2 = sumXi2 + Math.pow((double)i, 2.0);			
			sumYi = sumYi + data[i];			
			sumXiYi = sumXiYi + ((double)i * data[i]);			
			xList[i] = (double)i;
		}
		
		logger.debug ("sumXi = " + sumXi);
		logger.debug ("sumYi = " + sumYi);
		logger.debug ("sumXi2 = " + sumXi2);
		logger.debug ("sumXiYi = " + sumXiYi);
		
		double n = data.length;
		double b1 = ((n * sumXiYi) - (sumXi * sumYi)) / ((n * sumXi2) - Math.pow(sumXi, 2.0)); 
		double b0 = StatUtils.mean(data) - b1 * StatUtils.mean(xList);
		
		logger.debug("b0=" + b0 + "; b1=" + b1);
		
		return (float)b1;
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
		
		ScalabilityCalculator myCalc = new ScalabilityCalculator ();
		
		ThingTrust myThing = new ThingTrust ("javiPru");
		myThing.setUnits("boolean");
		ThingTrustData data1 = new ThingTrustData (null, "false", "8", "12");
		myThing.addData(data1);
		ThingTrustData data2 = new ThingTrustData (null, "false", "8", "12");
		myThing.addData(data2);
		ThingTrustData data3 = new ThingTrustData (null, "false", "7", "13");
		myThing.addData(data3);
		ThingTrustData data4 = new ThingTrustData (null, "true", "7", "13");
		myThing.addData(data4);
		ThingTrustData data5 = new ThingTrustData (null, "true", "7", "13");
		myThing.addData(data5);
		ThingTrustData data6 = new ThingTrustData (null, "true", "7", "13");
		myThing.addData(data6);
		ThingTrustData data7 = new ThingTrustData (null, "true", "7", "14");
		myThing.addData(data7);
		ThingTrustData data8 = new ThingTrustData (null, "true", "7", "14");
		myThing.addData(data8);
		ThingTrustData data9 = new ThingTrustData (null, "true", "7", "14");
		myThing.addData(data9);
		ThingTrustData data10 = new ThingTrustData (null, "true", "6", "14");
		myThing.addData(data10);
		
		myCalc.setPreOperationData(myThing);
		System.out.println (myCalc.calculateTrustAspect("javiPru"));
	}
	*/
}
