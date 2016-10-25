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

import eu.betaas.taas.securitymanager.taastrustmanager.taasaggregator.FuzzyCalculator;
import eu.betaas.taas.securitymanager.taastrustmanager.taasproxy.TaaSBDMClient;
import eu.betaas.taas.securitymanager.taastrustmanager.taasproxy.TaaSCMClient;
import eu.betaas.taas.securitymanager.taastrustmanager.taasproxy.ThingTrust;
import eu.betaas.taas.securitymanager.taastrustmanager.taasproxy.ThingTrustData;

public class DataStabilityCalculator 
{

	private Logger logger= Logger.getLogger("betaas.taas");
	public static final int BOOLEAN = 0;
	public static final int NUMERIC = 1;
	
	private double[] tsData;
	private ArrayList<String> equivalents;
	private TaaSBDMClient myBDMClient;
	private TaaSCMClient myCM;
	private String thingId;
	private int units;
	
	public DataStabilityCalculator ()
	{		
		units = -1;
		//myBDMClient = TaaSBDMClient.instance();
		//myCM = TaaSCMClient.instance();
	}
	
	public float calculateTrustAspect (String thingServiceId)
	{
		// Step 1 - Check information about the data provided by the thing --> Numeric or boolean
		if (units==-1 || tsData.length<2)
		{
			logger.error("Not enough data is available for performing the calculation --> Default value returned!");
			return 2.0f;
		}		
				
		// Step 2 - Check Runs Test for randomness
		logger.debug("Performing Runs Test...");
		StatisticsCalculator myCalc = new StatisticsCalculator();
		boolean runsResult = myCalc.calculateRunsTest(tsData);
		Double random = new Double ("1");
		if (!runsResult) random = new Double ("2");		
		logger.debug("Random data? " + runsResult);				
		
		// Step 3 - Check data variance, depending on each case
		logger.debug("Performing Variance Test...");
		boolean varianceResult = true;
		Double variance = new Double ("2");
		if (units!=DataStabilityCalculator.BOOLEAN)
		{
			varianceResult = myCalc.calculateNumericVariance(tsData, 0.05);
			if (varianceResult) variance = new Double ("1");
		}	
		logger.debug("Low variance? " + varianceResult);			
		
		// Step 4 - Check data similarity in equivalent Thing Services
		logger.debug("Performing Similarity Test...");
		double [] equivalentsData = getEquivalentsMean();
		Double coherence = new Double ("1");
		if (equivalentsData==null || equivalentsData.length<tsData.length)
		{
			//We set the parameter to UNKNOWN
			logger.debug("Similar data? Not enough data --> Unknown");
			coherence = new Double ("2"); 
		}
		else
		{
			boolean equivalentResult = myCalc.calculateSimilarity(tsData, equivalentsData, units);
			logger.debug("Similar data? " + equivalentResult);
			if (equivalentResult) coherence = new Double ("3");
		}			
		
		// Step 5 - Evaluate fuzzy model
		FuzzyCalculator myCalculator = new FuzzyCalculator ("DataStability.fcl");		
		HashMap<String, Double> myHash = new HashMap<String, Double>();
		myHash.put("random", random);
		myHash.put("variance", variance);
		myHash.put("coherence", coherence);		
		float result = myCalculator.calculateTrustAggregation(myHash);
		logger.debug ("Received value: " + result);
		
		return result;
	}
	
	public void setPreOperationData(ThingTrust information)
	{
		logger.debug("Converting Thing Service Data...");
		
		// Retrieve units information (boolean/numeric)
		if (information.getUnits().equalsIgnoreCase("boolean"))
		{
			units = DataStabilityCalculator.BOOLEAN;
		}
		else
		{
			units = DataStabilityCalculator.NUMERIC;
		}
		
		// Retrieve last data		
		ArrayList<ThingTrustData> baseData = information.getDataList();
		tsData = new double[baseData.size()];
		if (units==DataStabilityCalculator.BOOLEAN)
		{
			// Build booleans list
			for (int i=0; i < baseData.size(); i++)
			{
				if (baseData.get(i).getMeasurement().equalsIgnoreCase("true"))
				{
					tsData[i] = 1;
				}
				else
				{
					tsData[i] = 0;
				}
			}
		}		
		else
		{
			// Build doubles list
			for (int i=0; i < baseData.size(); i++)
			{
				tsData[i] = Double.parseDouble(baseData.get(i).getMeasurement());
			}
		}
		
		// Retrieve thing identifier
		thingId = information.getThingId();	
		
		// Retrieve equivalent thing services
		equivalents = information.getEquivalents();
	}
	
	private double[] getEquivalentsMean ()
	{		
		logger.debug("Gathering data from equivalent things...");
		// Check available Thing Services
		if (equivalents == null || equivalents.size() == 0)
		{
			logger.debug("There are no equivalent Thing Services!");
			return null;
		}
		
		// Contact with the BDM for getting measurements of equivalent thing services
		ArrayList<ThingTrustData> thingDataList = myBDMClient.getThingData(equivalents.get(0));
		if (thingDataList == null || thingDataList.size() < 2)
		{
			logger.debug("There are no enough data about equivalent Thing Services.");
			return null;
		}
		
		// Build result	
		logger.debug("Available measurements in eq Thing Services: " + thingDataList.size());
		double[] eqMean = new double [thingDataList.size()];
		if (units==DataStabilityCalculator.BOOLEAN)
		{
			// Build booleans list
			for (int i=0; i < thingDataList.size(); i++)
			{
				if (thingDataList.get(i).getMeasurement().equalsIgnoreCase("true"))
				{
					eqMean[i] = 1;
				}
				else
				{
					eqMean[i] = 0;
				}
			}
		}		
		else
		{
			// Build doubles list
			for (int i=0; i < thingDataList.size(); i++)
			{
				eqMean[i] = Double.parseDouble(thingDataList.get(i).getMeasurement());
			}
		}
		
		logger.debug("Equivalents data retrieval finished!");
		return eqMean;
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
		
		DataStabilityCalculator myCalc = new DataStabilityCalculator ();
		
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
		myCalc.calculateTrustAspect("javiPru");
	}
	*/
}
