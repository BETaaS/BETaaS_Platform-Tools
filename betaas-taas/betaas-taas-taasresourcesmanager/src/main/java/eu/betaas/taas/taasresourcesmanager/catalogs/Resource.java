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

package eu.betaas.taas.taasresourcesmanager.catalogs;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;

public class Resource 
{
	public static final int THINGSERVICE = 0;
	public static final int VIRTUALRESOURCE = 1;
	
	public static final int UNAVAILABLE = 0;
	public static final int AVAILABLE = 1;
		
	private int resourceType;
	private String resourceId;
	private String physicalResourceId;
	private int status;
	private String gatewayId;
	private HashMap<String, Integer> allocatedPushFeatures;
	private ArrayList<String> allocatedPullFeatures;
	private Logger logger= Logger.getLogger("betaas.taas");
	
	public Resource (String id, String physicalId, int type, String gateway)
	{
		resourceId = id;
		physicalResourceId = physicalId;
		resourceType = type;
		status = 1;	//We'll consider that the initial status is Available
		gatewayId = gateway;
		allocatedPushFeatures = new HashMap<String, Integer>();
		allocatedPullFeatures = new ArrayList<String> ();
		
	}
	
	public String getResourceId ()
	{
		return resourceId;
	}
	
	public String getPhysicalResourceId ()
	{
		return physicalResourceId;
	}
	
	public int getResourceType()
	{
		return resourceType;
	}
	
	public int getStatus()
	{
		return status;
	}
	
	public String getGatewayId ()
	{
		return gatewayId;
	}
	
	public ArrayList<String> getAllocatedFeatures()
	{
		ArrayList<String> result = new ArrayList<String>(allocatedPushFeatures.keySet());
		result.addAll(allocatedPullFeatures);
		return result;
	}
	
	public void setStatus (int newStatus)
	{
		status = newStatus;
	}
	
	public void addFeature (String idFeature, int period)
	{
		allocatedPushFeatures.put(idFeature, new Integer (period));
	}
	
	public void removeFeature (String idFeature)
	{
		if (allocatedPushFeatures.containsKey(idFeature))
		{
			allocatedPushFeatures.remove(idFeature);
		}
		else
		{
			allocatedPullFeatures.remove(idFeature);
		}
		
	}
	
	public void addFeature (String idFeature)
	{
		allocatedPullFeatures.add(idFeature);
	}
	
	public int getPeriod (String idFeature)
	{
		return allocatedPushFeatures.get(idFeature);
	}
	
	public int getCommonPeriod ()
	{
		ArrayList<Integer> periods = new ArrayList<Integer>(allocatedPushFeatures.values());
		//logger.debug("Number of registered periods: " + periods.size());
		System.out.println("Number of registered periods: " + periods.size());
		
	    long result = (long) Math.ceil(periods.get(0).longValue());
	    for(int i = 1; i < periods.size(); i++)
	    {
	    	result = gcd(result, (long) Math.ceil(periods.get(i).longValue()));
	    }
	    return (int)result;		
	}
	
	private static long gcd(long a, long b)
	{
	    while (b > 0)
	    {
	        long temp = b;
	        b = a % b; // % is remainder
	        a = temp;
	    }
	    return a;
	}

	private static long lcm(long a, long b)
	{
	    return a * (b / gcd(a, b));
	}

	/*
	public static void main(String[] args) 
	{
		Resource myRes = new Resource ("javiThingService", "javiThing", Resource.THINGSERVICE, "localhost");
		
		myRes.addFeature("javiFet1", 10);
		
		System.out.println("Common Period: " + myRes.getCommonPeriod());
		
		myRes.addFeature("javiFet2", 5);
		
		System.out.println("Common Period: " + myRes.getCommonPeriod());
		
		myRes.addFeature("javiFet3", 2);
		
		System.out.println("Common Period: " + myRes.getCommonPeriod());
	}
	*/
}
