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

package eu.betaas.taas.taasresourcesmanager.endpointsmanager;

import java.util.Calendar;
import java.util.Date;

public class Subscription implements Comparable<Subscription>
{

	private String featureId;
	private String featureGateway;
	private String thingServiceId;
	
	// The period is considered in seconds
	private int period;
	private Date startDate;
	private Calendar lastReceived;
	private int lastResponseTime;
	
	public Subscription (String feature, String appLocation, String thingService, int thePeriod)
	{
		featureId = feature;
		featureGateway = appLocation;
		thingServiceId = thingService;
		period = thePeriod;
		
		lastReceived = Calendar.getInstance();
		startDate = lastReceived.getTime();		
	}
	
	public String getFeatureId()
	{
		return featureId;
	}
	
	public String getFeatureLocation()
	{
		return featureGateway;
	}
	
	public String getThingServiceId()
	{
		return thingServiceId;
	}
	
	public float getPeriod()
	{
		return period;
	}
	
	public Date getStartDate()
	{
		return startDate;
	}
	
	public int getLastResponseTime()
	{
		return lastResponseTime;
	}
	
	public Date getExpectedDate()
	{
		Calendar nextDate = (Calendar)lastReceived.clone();
		// Here, we can receive notifications earlier than expected!!!! Remove in case we don't want it1
		int expectedPeriod = (int) (period * 0.9);
		nextDate.add(Calendar.SECOND, expectedPeriod);
		return nextDate.getTime();
	}
	
	public void setReceived()
	{
		// Calculate real response time in the last notification (in seconds)
		Calendar now = Calendar.getInstance();
		lastResponseTime = (int) (now.getTimeInMillis() - lastReceived.getTimeInMillis())/1000;
		
		// Set current last received time
		lastReceived = Calendar.getInstance();
	}
	
	public int compareTo(Subscription subs2) 
	{
		return this.getExpectedDate().compareTo(subs2.getExpectedDate());
	}
	
}
