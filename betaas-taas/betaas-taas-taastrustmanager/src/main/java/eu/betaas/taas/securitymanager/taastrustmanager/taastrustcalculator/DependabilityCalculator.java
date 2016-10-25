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

import java.sql.Timestamp;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import eu.betaas.taas.securitymanager.taastrustmanager.taasproxy.TaaSBDMClient;
import eu.betaas.taas.securitymanager.taastrustmanager.taasproxy.ThingTrust;
import eu.betaas.taas.securitymanager.taastrustmanager.taasproxy.ThingTrustData;

public class DependabilityCalculator 
{

	private Logger logger= Logger.getLogger("betaas.taas");
	private ArrayList<String> equivalents;
	private String thingId;
	private long activatedTime;
	
	public DependabilityCalculator()
	{
		equivalents = new ArrayList<String> ();
	}
	
	public float calculateTrustAspect (String thingServiceId)
	{
		// 1 - Retrieve required data
		logger.debug("Calculating dependability...");
		float result = 2.5f;
		
		// 2 - Calculate MTBF and Trustability
		result = (float) calculateTrustability (thingId);
		
		
		// 3 - Perform the Z-test
		if (equivalents.size()>1)
		{
			//String thingId = myCM.retrieveThingIdentifier(thingServiceId);
		}
		
		return result;
	}
	
	private double calculateTrustability (String thingServiceId)
	{
		double result = 0;
		
		// 1 - Retrieve failures detected in the period 
		TaaSBDMClient myBDMClient = TaaSBDMClient.instance();
		ArrayList<Long> activesList =  myBDMClient.getThingFailures(thingServiceId, thingId);
		
		if (activesList == null || activesList.size()==0)
		{
			logger.debug("No failures were detected for the thing " + thingServiceId + ". Max evaluation given!");
			return 3.0f;
		}
		
		// 2 - Calculate available time
		long sumFailuresTime = 0;
		for (Long active : activesList) 
		{ 
			sumFailuresTime = sumFailuresTime + active.longValue(); 
		}		
		long totalAvailableTime = activatedTime - sumFailuresTime;
		
		// 3 - Calculate MTBF
		double MTBF = totalAvailableTime / activesList.size();
		
		// 4 - Calculate Trustability related to 5 minutes of sensor life
		double trustability = Math.pow(Math.E, (-1 * (300/MTBF)));
		result = trustability * 3.0;
		
		return result; 
	}
	
	public void setPreOperationData(ThingTrust information)
	{
		logger.debug("Converting Thing Service Data...");
				
		// Retrieve list of equivalents		
		equivalents = information.getEquivalents();
				
		// Retrieve thing identifier
		thingId = information.getThingId();		
		
		// Determine how much time the thing was activated
		ArrayList<ThingTrustData> myData = information.getDataList();
		activatedTime = myData.get(myData.size()-1).getTimestamp().getTime() - myData.get(0).getTimestamp().getTime();
		logger.debug("Thing " + thingId + " was activated during " + activatedTime + " miliseconds.");
	}
}
