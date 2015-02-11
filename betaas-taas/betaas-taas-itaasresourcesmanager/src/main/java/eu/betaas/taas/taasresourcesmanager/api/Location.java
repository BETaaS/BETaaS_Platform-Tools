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

public class Location 
{
	/**
	 * true:public scenario
	 * false:private scenario
	 */
	private boolean environment;
	/**
	 * The floor in which the feature is requested.(Only for Private Scenario, environment == true)
	 */
	private String floor;
	
	/**
	 * The altitude in which the feature is requested.(Only for Private Scenario, environment == true)
	 */
	private String altitude;
	/**
	 * The latitude in which the feature is requested.(Only for Private Scenario, environment == true)
	 */
	private String latitude;
	/**
	 * The longitude in which the feature is requested.(Only for Private Scenario, environment == true)
	 */
	private String longitude;
	/**
	 * The radius which affects to the feature, based on the GPS coordinates.(Only for Private Scenario, environment == true)
	 */
	private float radius;
	
	/**
	 * Mandatory information which must be a single word specific to the location of the feature
	 */
	private String locationKeyword;
	/**
	 * (Not Mandatory) Free text specific to the location of the feature  
	 */
	private String locationIdentifier;

	
	public Location (String theFloor, String theLocKeyword, String theLocIdentifier)
	{
		environment = false;
		floor = theFloor;
		locationKeyword = theLocKeyword;
		locationIdentifier = theLocIdentifier;
	}
	
	public Location (String theLatitude, String theLongitude, String theAltitude, float theRadius, String theLocKeyword, String theLocIdentifier)
	{
		environment = true;
		latitude = theLatitude;
		longitude = theLongitude;
		altitude = theAltitude;
		radius = theRadius;
		locationKeyword = theLocKeyword;
		locationIdentifier = theLocIdentifier;
	}
	
	public boolean getEnvironment() 
	{
		return environment;
	}

	public void setEnvironment(boolean environment) 
	{
		this.environment = environment;
	}
	
	public String getFloor() 
	{
		return floor;
	}

	public void setFloor(String floor) 
	{
		this.floor = floor;
	}

	public String getAltitude() 
	{
		return altitude;
	}

	public void setAltitude(String altitude) 
	{
		this.altitude = altitude;
	}

	public String getLatitude() 
	{
		return latitude;
	}

	public void setLatitude(String latitude) 
	{
		this.latitude = latitude;
	}

	public String getLongitude() 
	{
		return longitude;
	}

	public void setLongitude(String longitude) 
	{
		this.longitude = longitude;
	}
	
	public float getRadius ()
	{
		return radius;
	}
	
	public void setRadius (float radius)
	{
		this.radius = radius;
	}
	
	public String getLocationKeyword() 
	{
		return locationKeyword;
	}

	public void setLocationKeyword(String locationKeyword) 
	{
		this.locationKeyword = locationKeyword;
	}

	public String getLocationIdentifier() 
	{
		return locationIdentifier;
	}

	public void setLocationIdentifier(String locationIdentifier) 
	{
		this.locationIdentifier = locationIdentifier;
	}
}
