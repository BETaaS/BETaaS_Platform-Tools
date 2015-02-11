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
Sergio García Villalonga. Atos Research and Innovation, Atos SPAIN SA
@email sergio.garciavillalonga@atos.net
**/

package eu.betaas.taas.taasresourcesmanager.api;

public class Feature 
{
	public static final int RTPULL = 0;
	public static final int NRTPULL = 1;
	public static final int RTPUSH = 2;
	public static final int NRTPUSH = 3;
	public static final int PUT = 4;
	
	private String featureCovered;
	private Location location;
	private int accessType;
	private double trustLevel;
	private String appId;
	private int period;
	
	//Attributes for the location
	
	
	public Feature (String applicationId, String feature, Location theLocation, int type, double trust, int frequency)
	{
		featureCovered = feature;
		location = theLocation;
		accessType = type;
		trustLevel = trust;	
		appId = applicationId;
		period = frequency;
	}	
	
	public String getFeature ()
	{
		return featureCovered;
	}
	
	public Location getLocation()
	{
		return location;
	}
	
	public int getType()
	{
		return accessType;
	}
	
	public double getTrustValue()
	{
		return trustLevel;
	}
	
	public String getAppId()
	{
		return appId;
	}
	
	public int getPeriod ()
	{
		return period;
	}
	
}
