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

import org.apache.commons.math3.distribution.ChiSquaredDistribution;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.stat.inference.TTest;
import org.apache.log4j.Logger;

public class StatisticsCalculator 
{
	private Logger logger= Logger.getLogger("betaas.taas");
	
	public StatisticsCalculator ()
	{
		
	}
		
	public boolean calculateNumericVariance (double[] values, double alpha)
	{
		// if there is only one record, just return variance is as expected
		if (values.length < 2) return true;
		
		// Start calculations				
		double mean = StatUtils.mean(values);
		double variance = StatUtils.variance(values);
		double stdDeviation = Math.sqrt(variance);		
		double degFreedom = values.length-1.0;
				
		logger.debug ("Mean = " + mean);
		logger.debug ("Current Variance = " + variance);
		logger.debug ("Standard Deviation = " + stdDeviation);
		logger.debug ("Expected variation = " + alpha);			
		
		// Retrieve Chi Square values from the inverse table
		ChiSquaredDistribution myDist = new ChiSquaredDistribution(degFreedom);
		double myXRight = myDist.inverseCumulativeProbability(alpha/2.0);
		double myXLeft = myDist.inverseCumulativeProbability(1.0 - alpha/2.0);
		
		// Calculate boundaries for the variance
		double myTLeft = (degFreedom * variance) / myXLeft;
		double myTRight = (degFreedom * variance) / myXRight;						
		logger.debug ("Boundaries: " + myTLeft + " to " + myTRight);		
		
		// Determine if the current variance is in the expected limits
		if ((myTLeft <= variance) && (variance <= myTRight))
		{
			// H0 -> Variance of the data is equal to the expected one
			return true;
		}
		
		// H1 -> Variance of the data is different to the expected one
		return false;
	}
	
	public boolean calculateRunsTest (double[] values)
	{
		double alpha = 0.05;		
		double n1 = 0.0;
		double n2 = 0.0;
		double runs = 1.0;	
		double median = StatUtils.percentile(values, 50);			
		boolean positive = true;
		
		//Starting variable for calculating runs (positive or negative)
		if (values[0]<median)
		{
			positive = false;
			n2++;
		}
		else
		{
			positive = true;
			n1++;
		}
		
		// Look for runs and count positive/negative values
		for (int i=1; i<values.length; i++)
		{
			if (values[i]<median)
			{
				n2++;
				if (positive)
				{			
					runs++;	
					positive = false;
				}
			}
			else
			{
				n1++;
				if (!positive)
				{			
					runs++;	
					positive = true;
				}
			}
		}
		
		// Calculate Z value
		double expectedRuns = (2.0 * n1 * n2 / (n1 + n2)) + 1.0;		
		double sR = Math.sqrt((2.0*n1*n2*(2.0*n1*n2-n1-n2)) / (Math.pow((n1+n2),2)*(n1+n2-1.0)));		
		double Z = (runs - expectedRuns) / sR;
		
		logger.debug ("Runs = " + runs);		
		logger.debug ("Positive values = " + n1);
		logger.debug ("Negative values = " + n2);
		logger.debug ("Expected Runs = " + expectedRuns);
		logger.debug ("sR = " + sR);
		logger.debug ("Z score = " + Z);		
		
		if ((runs - expectedRuns)==0.0)
		{
			//H1 -> Data was not produced in a random manner (because expected runs are ok)
			logger.debug ("Runs = Expected Runs --> Not random data");
			return false;
		}
		
		// Calculate region of acceptance
		NormalDistribution myNormal = new NormalDistribution(0,1);		
		double myZRight = Math.abs(myNormal.inverseCumulativeProbability(1-alpha/2));
							
		logger.debug ("Reject H0 if |Z|> " + myZRight);
		
		if (Math.abs(Z)>myZRight)
		{
			//H1 -> Data was not produced in a random manner
			return false;
		}
		
		//H0 -> Data was produced in a random manner
		return true;
	}
	
	public boolean calculateSimilarity (double[] values, double[] extDataset, int type)
	{
		boolean result = false;
			
		// Select the adequate test depending on the data type
		switch (type)
		{
		case DataStabilityCalculator.BOOLEAN:
			result = isSimilarProportion (values, extDataset);
			break;
		case DataStabilityCalculator.NUMERIC:
			result = isSimilarMean (values, extDataset);
			break;		
		}
		
		return result;
	}
	
	public boolean isSimilarProportion (double [] valuesA, double [] valuesB)
	{
		logger.debug("Calculating similar proportion...");
		double alpha = 0.05;	
		
		// Change data a bit for avoiding issues with booleans 0/1
		/*for (int i=0; i<valuesA.length; i++)
		{
			valuesA[i] = valuesA[i] + 1.0;
		}
		for (int i=0; i<valuesB.length; i++)
		{
			valuesB[i] = valuesB[i] + 1.0;
		}*/
		
		// Calculate region of acceptance
		NormalDistribution myNormal = new NormalDistribution(0,1);		
		double myZLeft = -1*Math.abs(myNormal.inverseCumulativeProbability(alpha/2));
		double myZRight = Math.abs(myNormal.inverseCumulativeProbability(alpha/2));
					
		logger.debug ("Boundaries: " + myZLeft + " to " + myZRight);
		
		// Calculate proportion for valuesA dataset
		int nA = valuesA.length;
		double successA = 0;
		for (int i=0; i<nA; i++)
		{
			successA = successA + valuesA[i];
		}
		
		logger.debug ("Success number for dataset A: " + successA);
		logger.debug ("Number of records for A: " + nA);
				
		double pA = successA/nA;
				
		// Calculate proportion for valuesB dataset
		int nB = valuesB.length;
		double successB = 0;
		for (int i=0; i<nB; i++)
		{
			successB = successB + valuesB[i];
		}
		
		logger.debug ("Success number for dataset B: " + successB);
		logger.debug ("Number of records for B: " + nB);
		
		double pB = successB/nB;
		
		// Calculate proportion similarity
		double pPool = (nA * pA + nB * pB) / (nA + nB);
		double zComp = (pA - pB) / Math.sqrt(pPool * (1.0 - pPool) * (1.0/nA + 1.0/nB));
		
		logger.debug ("pPooled = " + pPool);
		logger.debug ("Z value = " + zComp);
		logger.debug ("p-value = " + (1.0 - myNormal.cumulativeProbability(zComp))*2);		
		
		// Determine if z score is in the region of acceptance
		if ((myZLeft <= zComp) && (zComp <= myZRight))
		{
			return true;
		}
		
		return false;
	}
	
	public boolean isSimilarMean (double [] valuesA, double [] valuesB)
	{
		logger.debug("Calculating similar mean...");
		TTest studentTest = new TTest();
		boolean testResult = false;
		double error = 0;
		double tValue = 0;	
		
		double meanA = StatUtils.mean(valuesA);
		double meanB = StatUtils.mean(valuesB);
		
		try
		{			
			testResult = studentTest.tTest(valuesA, valuesB, 0.05);			
			error = studentTest.tTest(valuesA, valuesB);
			tValue = studentTest.t(valuesA, valuesB);
			logger.debug ("Test result --> MA = " + meanA + " -- MB = " + meanB);
			logger.debug ("Test result --> " + testResult + " with p " + error + " and tValue = " + tValue);
		}
		catch (Exception ex)
		{
			logger.error ("There was an error when trying to calculate Student's t test!");
			ex.printStackTrace();
			return false;
		}

		return testResult;
		
	}
	
	
	public static void main(String[] args) 
	{
		double [] dataSetA = new double [] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1};
		double [] dataSetB = new double [] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1};
		
		double [] dataSet = new double []
				{
				1.006,         
				  0.996,          
				  0.998,
				  1.000,          
				  0.992,          
				  0.993,          
				  1.002,          
				  0.999,          
				  0.994,          
				  1.000,          
				  0.998,          
				  1.006,          
				  1.000,          
				  1.002,          
				  0.997,          
				  0.998,          
				  0.996,          
				  1.000,          
				  1.006,          
				  0.988,          
				  0.991,          
				  0.987,          
				  0.997,          
				  0.999,          
				  0.995,          
				  0.994,          
				  1.000,          
				  0.999,          
				  0.996,          
				  0.996,          
				  1.005,          
				  1.002,          
				  0.994,          
				  1.000,          
				  0.995,          
				  0.994,          
				  0.998,          
				  0.996,          
				  1.002,          
				  0.996,          
				  0.998,          
				  0.998,          
				  0.982,          
				  0.990,          
				  1.002,          
				  0.984,          
				  0.996,          
				  0.993,          
				  0.980,          
				  0.996,          
				  1.009,          
				  1.013,          
				  1.009,          
				  0.997,          
				  0.988,          
				  1.002,          
				  0.995,          
				  0.998,          
				  0.981,          
				  0.996,          
				  0.990,          
				  1.004,          
				  0.996,          
				  1.001,          
				  0.998,          
				  1.000,          
				  1.018,          
				  1.010,          
				  0.996,          
				  1.002,          
				  0.998,          
				  1.000,          
				  1.006,          
				  1.000,          
				  1.002,          
				  0.996,          
				  0.998,          
				  0.996,          
				  1.002,          
				  1.006,          
				  1.002,          
				  0.998,          
				  0.996,          
				  0.995,          
				  0.996,          
				  1.004,          
				  1.004,          
				  0.998,          
				  0.999,          
				  0.991,          
				  0.991,        
				  0.995,         
				  0.984,         
				  0.994,        
				  0.997,         
				  0.997,         
				  0.991,         
				  0.998,         
				  1.004,         
				  0.997,
				};
		
		double [] dataSetRunTest = new double []
				{
				-213,
				  -564,
				   -35,
				   -15,
				   141,
				   115,
				  -420,
				  -360,
				   203,
				  -338,
				  -431,
				   194,
				  -220,
				  -513,
				   154,
				  -125,
				  -559,
				    92,
				   -21,
				  -579,
				   -52,
				    99,
				  -543,
				  -175,
				   162,
				  -457,
				  -346,
				   204,
				  -300,
				  -474,
				   164,
				  -107,
				  -572,
				    -8,
				    83,
				  -541,
				  -224,
				   180,
				  -420,
				  -374,
				   201,
				  -236,
				  -531,
				    83,
				    27,
				  -564,
				  -112,
				   131,
				  -507,
				  -254,
				   199,
				  -311,
				  -495,
				   143,
				   -46,
				  -579,
				   -90,
				   136,
				  -472,
				  -338,
				   202,
				  -287,
				  -477,
				   169,
				  -124,
				  -568,
				    17,
				    48,
				  -568,
				  -135,
				   162,
				  -430,
				  -422,
				   172,
				   -74,
				  -577,
				   -13,
				    92,
				  -534,
				  -243,
				   194,
				  -355,
				  -465,
				   156,
				   -81,
				  -578,
				   -64,
				   139,
				  -449,
				  -384,
				   193,
				  -198,
				  -538,
				   110,
				   -44,
				  -577,
				    -6,
				    66,
				  -552,
				  -164,
				   161,
				  -460,
				  -344,
				   205,
				  -281,
				  -504,
				   134,
				   -28,
				  -576,
				  -118,
				   156,
				  -437,
				  -381,
				   200,
				  -220,
				  -540,
				    83,
				    11,
				  -568,
				  -160,
				   172,
				  -414,
				  -408,
				   188,
				  -125,
				  -572,
				   -32,
				   139,
				  -492,
				  -321,
				   205,
				  -262,
				  -504,
				   142,
				   -83,
				  -574,
				     0,
				    48,
				  -571,
				  -106,
				   137,
				  -501,
				  -266,
				   190,
				  -391,
				  -406,
				   194,
				  -186,
				  -553,
				    83,
				   -13,
				  -577,
				   -49,
				   103,
				  -515,
				  -280,
				   201,
				   300,
				  -506,
				   131,
				   -45,
				  -578,
				   -80,
				   138,
				  -462,
				  -361,
				   201,
				  -211,
				  -554,
				    32,
				    74,
				  -533,
				  -235,
				   187,
				  -372,
				  -442,
				   182,
				  -147,
				  -566,
				    25,
				    68,
				  -535,
				  -244,
				   194,
				  -351,
				  -463,
				   174,
				  -125,
				  -570,
				    15,
				    72,
				  -550,
				  -190,
				   172,
				  -424,
				  -385,
				   198,
				  -218,
				  -536,
				    96,
				};
		
		double [] dataSetTemperature = new double []
				{
					21.01,
					21.15,
					21.05,
					21.1,
					21.3,
					21.35,
					21.40,
					21.45,
					21.50,
					21.55,
					21.7,
					21.75,
					21.7,
					21.65,
					21.7,
					21.75,
					21.85,
					21.70,
					21.65,
					21.55,
					21.50,
					21.40,
					21.35,
					21.30,
					21.25,
					21.30,
					21.35,
					21.30,
					21.20,
					21.15
				};
		
		StatisticsCalculator myCalc = new StatisticsCalculator();
		
		//System.out.println ("Proportion is similar? -> " + myCalc.isSimilarProportion(dataSetA, dataSetB));
		System.out.println ("Low variance in data? -> " + myCalc.calculateNumericVariance(dataSetTemperature, 0.05));
		System.out.println ("Is data random? -> " + myCalc.calculateRunsTest(dataSetTemperature));
		
		ChiSquaredDistribution myDist = new ChiSquaredDistribution(16, 0.05);
		//double[] myChiSquare = myDist.sample(100);
		//double myTLeft = new DescriptiveStatistics(myChiSquare).getPercentile(95);
		//double myTLeft = myDist.inverseCumulativeProbability(arg0);
		//System.out.println("Chi cuadrado: " + myTLeft);
	}
	
}
