/*
 Copyright 2014-2015 Hewlett-Packard Development Company, L.P.

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */
package eu.betaas.taas.bigdatamanager.database.hibernate.data;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


@Entity
@Table(name = "T_THING_DATA")
@IdClass(ThingDatatId.class)
public class ThingData {


	@Id
    private String thingID;
	
	@Id
    private Timestamp timestamp;
	
	private String environment;
	 
	private String room;
	 
	private String floor;
	 
	private String city_name;
	
	private String latitude;
	 
	private String longitude;
	 
	private String location;
	
	private String altitude;

	private String measurement;
    
    private String memory_status;
     
    private String battery_cost;
    
    private String battery_level;
    
    private String location_keyword;
    
    private String location_identifier;
    
       
	public String getMemory_status() {
		return memory_status;
	}

	public void setMemory_status(String memory_status) {
		this.memory_status = memory_status;
	}

	public String getBattery_cost() {
		return battery_cost;
	}

	public void setBattery_cost(String battery_cost) {
		this.battery_cost = battery_cost;
	}


	public String getLocation_keyword() {
		return location_keyword;
	}

	public void setLocation_keyword(String location_keyword) {
		this.location_keyword = location_keyword;
	}


	public String getLocation_identifier() {
		return location_identifier;
	}


	public void setLocation_identifier(String location_identifier) {
		this.location_identifier = location_identifier;
	}


	public String getEnvironment() {
		return environment;
	}

	public void setEnvironment(String environment) {
		this.environment = environment;
	}


	public String getRoom() {
		return room;
	}


	public void setRoom(String room) {
		this.room = room;
	}


	public String getFloor() {
		return floor;
	}

	public void setFloor(String floor) {
		this.floor = floor;
	}

	public String getCity_name() {
		return city_name;
	}


	public void setCity_name(String city_name) {
		this.city_name = city_name;
	}


	public String getLatitude() {
		return latitude;
	}


	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}


	public String getLongitude() {
		return longitude;
	}


	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}


	public String getLocation() {
		return location;
	}


	public void setLocation(String location) {
		this.location = location;
	}


	public String getThingID() {
		return thingID;
	}


	public void setThingID(String thingID) {
		this.thingID = thingID;
	}


	public Timestamp getTimestamp() {
		return timestamp;
	}


	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}


	public String getMeasurement() {
		return measurement;
	}


	public void setMeasurement(String measurement) {
		this.measurement = measurement;
	}	
	
    public String getAltitude() {
		return altitude;
	}


	public void setAltitude(String altitude) {
		this.altitude = altitude;
	}

	public String getBattery_level() {
		return battery_level;
	}

	public void setBattery_level(String battery_level) {
		this.battery_level = battery_level;
	}
	
	
	
}

@Embeddable
class ThingDatatId implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String thingID;
	Timestamp timestamp;
}
