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

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.FunctionBlock;
import net.sourceforge.jFuzzyLogic.rule.Variable;

import org.apache.log4j.Logger;

import eu.betaas.service.securitymanager.trustmanager.serviceproxy.TaaSBDMClient;

public class FuzzyCalculator 
{
	private FIS currentFis = null;
	private String fileName = "";
	Logger log = Logger.getLogger("betaas.service");
		
	public FuzzyCalculator () 
	{		       
       fileName = "fuzzy/Gateways.fcl";       
	}
	
	public FuzzyCalculator (String rulesFile)
	{
		// Load from 'FCL' file
        String fileName = "fuzzy/" + rulesFile;
     
      	// Get class loader, for avoiding problems with Tomcat (loading local files)
        ClassLoader loader = FuzzyCalculator.class.getClassLoader();
        if(loader==null)
            loader = ClassLoader.getSystemClassLoader();

        // We want to load file located in WEB-INF/classes/fuzzy/               
        java.net.URL url = loader.getResource(fileName);
              	
        try
        {        	
        	currentFis = FIS.load(url.openStream(), true);
        }
        catch (IOException ex)
        {
        	log.error ("Problem loading the file for Fuzzy!");
            log.error(url.getPath());
      		ex.printStackTrace();
        }       
        
        // Error while loading?
        if( currentFis == null ) 
        { 
            log.error("It was not possible to load fuzzy file: '" + fileName + "'");
            return;
        }

        // Show 
        //currentFis.chart();
	}
	
	public float calculateTrustAggregation (HashMap<String, Double> parameters)
	{	                        
		//Step 1 --> Retrieve necessary parameters
        HashMap<String, Double> myHash = new HashMap<String, Double>();
		
		//Table with parameter to be retrieved        
        FunctionBlock fuzzySet = currentFis.iterator().next();
        log.debug ("FunctionBlock " + fuzzySet.getName() + " opened. Retrieving input variables...");
        Iterator<Variable> varList = fuzzySet.variables().iterator();
        while (varList.hasNext())
        {
        	Variable currentVar = varList.next();
        	if (!currentVar.isOutput())
        	{
        		//Get variable info
        		String varName = currentVar.getName();
        		log.debug("Looking for variable: " + varName + "... ");
        		
        		//Retrieve variable value from DB
        		String varValue = "0.0";
        		try
        		{        	
        			if (parameters.containsKey(varName))
    				{
    					//Assign value to the variable as input
    					varValue = parameters.get(varName).toString();
    				}
        			else
        			{
        				throw new Exception ();
        			}
                    
            		log.debug (" Done! --> " + varValue);
        		}
        		catch (Exception ex)
        		{
        			log.debug (" Not Found!! --> Skipping!");
        		}
        		
        		//Push value to the Fuzzy Set (only if found)
        		if (!varValue.equalsIgnoreCase("0.0"))
        		{
        			myHash.put(varName, new Double (varValue));
        		}        		
        	}        	
        }
        		
		//Step 2 --> Calculate prediction		
		float trustValue = 0.0f;		
		trustValue = (float)calculateTrust(myHash);
		log.debug ("Calculating trust... " + trustValue);
						
		return trustValue;
	}
	
	public float calculateTrustAggregation (String idEntity)
	{		
		//Configure fuzzy file
		//Step 1 --> Get file name corresponding to the aspect		
		// Get class loader, for avoiding problems with Tomcat (loading local files)
        ClassLoader loader = FuzzyCalculator.class.getClassLoader();
        if(loader==null)
          loader = ClassLoader.getSystemClassLoader();

        // We want to load file located in WEB-INF/classes/fuzzy/               
        java.net.URL url = loader.getResource(fileName);
                		
		// Step 2 --> Load from 'FCL' file        
        //currentFis = FIS.load(fileName,true); Removed, as it is not enough for Tomcat
        try
        {        	
        	currentFis = FIS.load(url.openStream(), true);
        }
        catch (IOException ex)
        {
        	log.error ("Problem loading the file for Fuzzy!");
        	log.error(url.getPath());
			ex.printStackTrace();
        }
        
        // Error while loading?
        if( currentFis == null ) 
        { 
        	log.error("It was not possible to load fuzzy file: '" + fileName + "'");
            log.error(url.getPath());
            return -1;
        }

        // Show 
        //currentFis.chart();
                        
		//Step 3 --> Retrieve necessary parameters and values from DB
        HashMap<String, Double> myHash = new HashMap<String, Double>();
        try
		{    
        	// Retrieve the client instance and load data to the fuzzy model
        	TaaSBDMClient myBDM = TaaSBDMClient.instance();
        	GatewayTrust currentTrust = myBDM.getTrustData(idEntity);
        	myHash.put("history", new Double (currentTrust.getInteractionHistory()));
    		myHash.put("dependability", new Double (currentTrust.getDependability()));
    		myHash.put("path", new Double (currentTrust.getPath()));
    		myHash.put("energy", new Double (currentTrust.getEnergy()));
    		myHash.put("reputation", new Double (currentTrust.getReputation()));    		
		}
		catch (Exception ex)
		{
			log.error ("Problems retrieving data from DB!!");
			ex.printStackTrace();
		}
                
		//Table with parameters to be retrieved        
        FunctionBlock fuzzySet = currentFis.iterator().next();
        log.debug ("FunctionBlock " + fuzzySet.getName() + " opened. Retrieving input variables...");
        Iterator<Variable> varList = fuzzySet.variables().iterator();
        while (varList.hasNext())
        {
        	Variable currentVar = varList.next();
        	if (!currentVar.isOutput())
        	{
        		//Get variable info
        		String varName = currentVar.getName();
        		log.debug("Looking for variable: " + varName + "... ");
        		
        		//Check if this value is already provided or is missing       		       
        		if (myHash.containsKey(varName))
    			{
        			String varValue = myHash.get(varName).toString();
        			log.debug (" Done! --> " + varValue);
    			}
        		else
        		{
        			log.debug (" NOT AVAILABLE!!!");
        		}        		
        	}        	
        }
        		
		//Step 4 --> Calculate prediction		
		float trustValue = 0.0f;		
		trustValue = (float)calculateTrust(myHash);
		log.debug ("Calculating trust... " + trustValue);
						
		return trustValue;
	}
	
	private double calculateTrust (HashMap<String, Double> arguments)
	{
		//Check inputs required and available
		Iterator<Variable> variablesList = currentFis.getFunctionBlock(null).variablesSorted().iterator();
		int varFound = 0;
		int varTotal = 0;
				
		if (arguments != null)
		{
			while (variablesList.hasNext())
			{
				//Get variable name
				Variable currentVar = variablesList.next();
				String varName = currentVar.getName();
				varTotal++;
				double value = 0.0;
					
				//Look for the variable in the arguments list
				if (arguments.containsKey(varName))
				{
					//Assign value to the variable as input
					value = (Double)(arguments.get(varName)).doubleValue();					
					currentFis.setVariable(varName, value);
					varFound++;
					log.debug ("Variable --" + varName + "-- found: " + value);
				}			
			}
		}
						
		//Warning messages
		if (arguments == null || varTotal-1 != varFound)
		{
			log.debug ("WARNING! Input not set for all the required variables!");
		}
				
        //Evaluate the result
        currentFis.evaluate();

        //Show output variable's chart 
        //currentFis.getVariable("trust").chartDefuzzifier(true);
        
		return currentFis.getVariable("trust").getLatestDefuzzifiedValue();		
	}
						
	/*
	public static void main(String[] args) 
	{
		//FuzzyCalculator myCalculator = new FuzzyCalculator();
		//FuzzyCalculator myCalculator = new FuzzyCalculator ("ThingServices.fcl");
		
		FuzzyCalculator myCalculator = new FuzzyCalculator ("DataStability.fcl");
		
		HashMap<String, Double> myHash = new HashMap<String, Double>();
		myHash.put("random", new Double ("2"));
		myHash.put("variance", new Double ("2"));
		myHash.put("coherence", new Double ("3"));
		
		/*
		HashMap<String, Double> myHash = new HashMap<String, Double>();
		myHash.put("security", new Double ("1.75"));
		myHash.put("qos", new Double ("3.5"));
		myHash.put("dependability", new Double ("3.25"));
		myHash.put("scalability", new Double ("2.15"));
		myHash.put("battery", new Double ("4.45"));
		myHash.put("data", new Double ("4.5"));
		*/
		
	/*
		double result = myCalculator.calculateTrust(myHash);
		System.out.println ("Received value: " + result);
		//TTest javi = new TTestImpl();
		
		/*
		//double result = myCalculator.calculateTrustAggregation(myHash);
		double result = myCalculator.calculateTrustAggregation("16cd84b6-5689-47d0-a7b0-174cacb54959");
		System.out.println ("Received value (CoG): " + result);
		*/
	/*
	}
	*/
	
}
