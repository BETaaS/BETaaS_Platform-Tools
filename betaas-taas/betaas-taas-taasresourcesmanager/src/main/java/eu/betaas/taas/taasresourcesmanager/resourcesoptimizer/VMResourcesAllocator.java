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

Authors :
Francisco Javier Nieto. Atos Research and Innovation, Atos SPAIN SA
@email francisco.nieto@atos.net 

**/

package eu.betaas.taas.taasresourcesmanager.resourcesoptimizer;

import java.util.Random;

import org.apache.log4j.Logger;

import eu.betaas.taas.taasresourcesmanager.taasrmclient.TaaSVMMClient;
import eu.betaas.taas.taasvmmanager.api.datamodel.InstanceType;
import eu.betaas.taas.taasvmmanager.api.datamodel.VMRequest;

public class VMResourcesAllocator 
{
	private TaaSVMMClient vmmClient;
	private Logger logger= Logger.getLogger("betaas.taas");
	
	public static final int GWIMAGE = 0;
	public static final int APPIMAGE = 1;
	public static final int BDIMAGE = 2;
	
	public VMResourcesAllocator ()
	{
		//vmmClient = TaaSVMMClient.instance();
	}
	
	public String createNode(int type)
	{
		logger.info("Requesting a new VM of type " + type);
		VMRequest flavorRequest = new VMRequest();
		flavorRequest.setCores(2);
		flavorRequest.setInstances(1);
		flavorRequest.setMemory(256);
		flavorRequest.setSpeed(1200);
		flavorRequest.setImage("BIGDATA");
		String vmId = vmmClient.createLocalVM(flavorRequest);
		return vmId;
	}
	
	public boolean deleteNode (String vmIdentifier)
	{
		return true;
	}
	
	private int calculateLocalFeasibility (InstanceType basicReqs)
	{
		// Retrieve historical data about memory
		Double[] memList = {1024.0, 1024.0, 1001.0};
		
		// Retrieve historical data about cpu
		Double[] cpuList = {55.0, 59.0, 56.3};
		
		// Perform forecasting for memory
		double expectedMem = doubleExponentialSmoothing (0.5, 0.1, memList, 10);
		
		// Perform forecasting for cpu
		double expectedCpu = doubleExponentialSmoothing (0.5, 0.1, cpuList, 10);
		
		// Compare resources
		int minimumCpu = basicReqs.getCpu();
		int minimumMem = basicReqs.getMemory();
		
		
		
		return -1;
	}
	
	private double doubleExponentialSmoothing (double alpha, double beta, Double[] valuesList, int m)
	{
		logger.debug("Starting Holt's Linear forecast calculation with values...");
		logger.debug("Alpha: " + alpha + "; Beta: " + beta + "; M: " + m);
		
		// Calculate initial values for the Holt's Linear		
		double L0 = valuesList[0].doubleValue();
		double b0 = valuesList[1].doubleValue() - valuesList[0].doubleValue();		

		logger.debug("Total values: " + valuesList.length);
        logger.debug("Initial level value L0: " + L0);
        logger.debug("Initial trend value b0: " + b0);        
		
		double forecast = calculateHoltLinear(valuesList, L0, b0, alpha, beta, m);

		return forecast;		
	}
	
	// Multiplicative Holt (Double Exponential Smoothing)
	private double calculateHoltLinear(Double[] y, double a0, double b0, double alpha, double beta, int k) 
	{
		double[] Lt = new double[y.length];
	    double[] Bt = new double[y.length];	        
	    double[] Ft = new double[y.length + k];

	    // Initialize base values	        
	    Lt[0] = a0;
	    Bt[0] = b0;
	    Ft[0] = a0;
	        
	    // Start calculations
	    for (int i = 1; i < y.length ; i++) 
	    {
	    	logger.debug ("---Iteration n. " + i + " --");
	        // Calculate overall smoothing
	        Lt[i] = alpha * y[i] + (1.0 - alpha) * (Lt[i-1] + Bt[i-1]);
	        logger.debug ("Overall Smoothing: " + Lt[i]);
	            
	        // Calculate trend smoothing
	        Bt[i] = beta * (Lt[i] - Lt[i - 1]) + (1.0 - beta) * Bt[i - 1];
	        logger.debug ("Trend Smoothing: " + Bt[i]);            
	            
	        // Calculate forecast
	        Ft[i + 1] = Lt[i] + Bt[i];	                
	        logger.debug ("Prediction done: " + Ft[i + k]);
	        logger.debug ("----------------------------");
	    }
	    
	    Ft[y.length-1 + k ] = Lt[y.length-1] + k * Bt[y.length-1];

	    // Return only the last value
	    return Ft[Ft.length-1];
	}
	
	public int allocateRandom (int [] vms, int cost)
	{
		boolean done = false;
		int iterations = 0;
		Random randomGenerator = new Random();
		
		while (!done)
		{
			int randomInt = randomGenerator.nextInt(vms.length);
		    System.out.println("Generated : " + randomInt);
		    
		    if (vms[randomInt]-cost>0)
		    {
		    	System.out.println("Valid! -> " + randomInt);
		    	return randomInt;
		    }
		    
		    iterations++;
		    if (iterations>vms.length*2)
		    {
		    	done = true;
		    }
		}
		return -1;
	}
	
	public int allocateMostFree (int [] vms, int cost)
	{
		boolean feasible = false;
		int index = 0;
		
		while (!feasible)
		{
			if (vms[index] > cost)
			{
				feasible = true;
				break;
			}
			
			index++;
			
			if (index==vms.length)
			{
				return -1;
			}
		}
		
		if (index>vms.length)
		{
			return -1;
		}
		
		for (int i=index+1; i<vms.length; i++)
		{			
			if (vms[index]<vms[i])
			{
				index = i;
			}
		}
		
		 System.out.println("GW selected: " + index + " with " + vms[index] + " free!");
		
		return index;
	}
	
	public int allocateLocalVM (int [] vms, int cost)
	{
		boolean feasible = false;
		int index = 0;
		
		while (!feasible)
		{
			if (vms[index] > cost)
			{
				feasible = true;
				break;
			}
			
			index++;
			
			if (index==vms.length)
			{
				return -1;
			}
		}
		
		if (index>vms.length)
		{
			return -1;
		}
		
		int selectedMem = vms[index];
		for (int i=index+1; i<vms.length; i++)
		{
			int currentMem = vms[i];
			if (currentMem > cost)
			{				
				if (currentMem-1024<0 && currentMem % 384 > selectedMem % 384)
				{
					index = i;
					selectedMem = currentMem;
				}
			}
		}
		
		 System.out.println("GW selected: " + index + " with " + vms[index] + " free!");
		
		return index;
	}
	
	public static void main(String args[]) 
	{
		VMResourcesAllocator myAllocator = new VMResourcesAllocator ();
		
		int[] memory = new int[10];
		int maxAllocation = 50;
		int tries = 1;
		
		for (int i=0; i<memory.length; i++)
		{
			memory[i] = 1664;
		}
		
		boolean stop = false;
		int vmsBDCounter = 0;
		int vmsNCounter = 0;
		int vmsSelector = 0;
		int firstFailure = -1;
		int secondFailure = -1;
		int thirdFailure = -1;
		
		while (tries < maxAllocation)
		{
			if (vmsSelector==0)
			{
				//int selectedVM = myAllocator.allocateRandom (memory, 1024);
				//int selectedVM = myAllocator.allocateMostFree (memory, 1024);
				int selectedVM = myAllocator.allocateLocalVM (memory, 1024);
				
				if (selectedVM != -1)
				{
					//memory[selectedVM] = memory[selectedVM] - 384;
					//vmsNCounter ++;
					//System.out.println ("Normal VM allocated!");
					
					memory[selectedVM] = memory[selectedVM] - 1024;
					vmsBDCounter ++;
					System.out.println ("Big Data VM allocated!");					
				}	
				else
				{
					if (firstFailure==-1)
					{
						firstFailure = tries;
					}
					else if (secondFailure==-1)
					{
						secondFailure = tries;
					}
					else if (thirdFailure==-1)
					{
						thirdFailure = tries;
					}
				}
				
				vmsSelector++;				
			}
			else
			{
				//int selectedVM = myAllocator.allocateRandom (memory, 384);
				//int selectedVM = myAllocator.allocateMostFree (memory, 384);
				int selectedVM = myAllocator.allocateLocalVM (memory, 384);
				if (selectedVM != -1)
				{
					//memory[selectedVM] = memory[selectedVM] - 1024;
					//vmsBDCounter ++;
					//System.out.println ("Big Data VM allocated!");
					
					memory[selectedVM] = memory[selectedVM] - 384;
					vmsNCounter ++;
					System.out.println ("Normal VM allocated!");
				}		
				else
				{
					if (firstFailure==-1)
					{
						firstFailure = tries;
					}
					else if (secondFailure==-1)
					{
						secondFailure = tries;
					}
					else if (thirdFailure==-1)
					{
						thirdFailure = tries;
					}
				}
								
				vmsSelector++;
				if (vmsSelector==3)
				{
					vmsSelector=0;
				}				
			}
			tries++;
		}
		
		// Calculate statistics
		int freeMem = 0;
		for (int i=0; i<memory.length; i++)
		{
			System.out.println ("Free memory in GW " + i + ": " + memory[i]);
			freeMem = freeMem + memory[i];
		}
		double average = freeMem / memory.length;
		double variance,temp = 0.0;

		for(double i : memory)
		{
			temp += Math.pow((i - average),2);
		}
		variance = temp/(memory.length);
		double deviation = Math.sqrt(variance);
		
		System.out.println ("Number of VMs allocated: " + (vmsBDCounter+vmsNCounter));
		System.out.println ("Number of Big Data VMs allocated: " + vmsBDCounter);
		System.out.println ("Number of Normal VMs allocated: " + vmsNCounter);
		System.out.println ("Memory free: " + freeMem);
		System.out.println("Average free memory: " + average);
		System.out.println("Variance free memory: " + variance);
		System.out.println("Std Deviation free memory: " + deviation);
		System.out.println ("First Failure: " + firstFailure);
		System.out.println ("Second Failure: " + secondFailure);
		System.out.println ("Third Failure: " + thirdFailure);
	}
}
