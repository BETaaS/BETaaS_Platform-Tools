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

package eu.betaas.service.securitymanager.trustmanager.serviceproxy;

public class ThingLocation 
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
	 * The floor in which the feature is requested.(Only for Private Scenario, environment == true)
	 */
	private String room;
	
	/**
	 * The floor in which the feature is requested.(Only for Private Scenario, environment == true)
	 */
	private String cityName;
	
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
	 * Mandatory information which must be a single word specific to the location of the feature
	 */
	private String locationKeyword;
	/**
	 * (Not Mandatory) Free text specific to the location of the feature  
	 */
	private String locationIdentifier;
	
	public ThingLocation (String theFloor, String theRoom, String theLocKeyword, String theLocIdentifier)
	{
		environment = false;
		floor = theFloor;
		room = theRoom;
		locationKeyword = theLocKeyword;
		locationIdentifier = theLocIdentifier;
	}
	
	public ThingLocation (String theLatitude, String theLongitude, String theAltitude, String theCity, String theLocKeyword, String theLocIdentifier)
	{
		environment = true;
		latitude = theLatitude;
		longitude = theLongitude;
		altitude = theAltitude;		
		cityName = theCity;
		locationKeyword = theLocKeyword;
		locationIdentifier = theLocIdentifier;
	}
	
	public boolean getEnvironment() 
	{
		return environment;
	}
	
	public String getFloor() 
	{
		return floor;
	}
	
	public String getRoom()
	{
		return room;
	}

	public String getAltitude() 
	{
		return altitude;
	}

	public String getLatitude() 
	{
		return latitude;
	}

	public String getLongitude() 
	{
		return longitude;
	}
	
	public String getCityName()
	{
		return cityName;
	}
	
	public String getLocationKeyword() 
	{
		return locationKeyword;
	}

	public String getLocationIdentifier() 
	{
		return locationIdentifier;
	}	
}
