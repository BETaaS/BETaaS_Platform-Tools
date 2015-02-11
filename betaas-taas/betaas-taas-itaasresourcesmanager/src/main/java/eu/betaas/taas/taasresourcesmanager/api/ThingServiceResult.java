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

public class ThingServiceResult 
{
	private String measurement;
	//private String deviceID;
	private String unit;
	private boolean environment;
	private float latitude;
	private float longitude;
	private float altitude;
	private int floor;
	private String locationKeyword;
	private String locationIdentifier;

	public ThingServiceResult ()
	{}
	
	public ThingServiceResult (String theMeasure, String theUnit, boolean theEnv, float theLat, float theLon, float theAlt, int theFloor, String keyLoc, String locId)
	{
		measurement = theMeasure;
		unit = theUnit;
		environment = theEnv;
		latitude = theLat;
		longitude = theLon;
		altitude = theAlt;
		floor = theFloor;
		locationKeyword = keyLoc;
		locationIdentifier = locId;
	}
	
	public void setMeasurement (String theMeasure)
	{
		measurement = theMeasure;
	}
	
	public void setUnit (String theUnit)
	{
		unit = theUnit;
	}
	
	public void setEnvironment (boolean theEnv)
	{
		environment = theEnv;
	}
	
	public void setLatitude (float theLat)
	{
		latitude = theLat;
	}
	
	public void setLongitude (float theLon)
	{
		longitude = theLon;
	}
	
	public void setAltitude (float theAlt)
	{
		altitude = theAlt;
	}
	
	public void setFloor (int theFloor)
	{
		floor = theFloor;
	}
	
	public void setLocationKeyword (String keyLoc)
	{
		locationKeyword = keyLoc;
	}
	
	public void setLocationIdentifier (String locId)
	{
		locationIdentifier = locId;
	}
	
	public String getMeasurement ()
	{
		return measurement;
	}
	
	public String getUnit()
	{
		return unit;
	}
	
	public boolean getEnvironment()
	{
		return environment;
	}
	
	public float getLatitude()
	{
		return latitude;		
	}
	
	public float getLongitude()
	{
		return longitude;		
	}
	
	public float getAltitude()
	{
		return altitude;		
	}
	
	public int getFloor()
	{
		return floor;
	}
	
	public String getLocationKeyword()
	{
		return locationKeyword;
	}
	
	public String getLocationIdentifier()
	{
		return locationIdentifier;
	}
	
	/*
	public String getData() 
	{			
		//Conversion to JSON
		Gson gson = new Gson();		
	    return gson.toJson(this);
	}
	*/
}
