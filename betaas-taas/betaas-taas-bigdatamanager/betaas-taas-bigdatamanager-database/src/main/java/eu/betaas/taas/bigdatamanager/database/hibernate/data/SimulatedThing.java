/**
* Copyright 2014-2015 Converge ICT
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*
*/
package eu.betaas.taas.bigdatamanager.database.hibernate.data;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="SIM_THING")
public class SimulatedThing implements Serializable {

	private static final long serialVersionUID = 2479190792077541953L;

	@Id
	@GeneratedValue
	private int id;
	
	@Column(name = "output",columnDefinition="TINYINT")
	private boolean output;
	
	@Column(name = "digital",columnDefinition="TINYINT")
	private boolean digital;

	@Column(name = "max_resp_time")
	private String maximumResponseTime;

	@Column(name = "type")
	private String type;

	@Column(name = "measurement")
	private String measurement;

	@Column(name = "unit")
	private String unit;

	@Column(name = "environment")
	private boolean environment;

	@Column(name = "device_id")	
	private String deviceID;

	@Column(name = "floor")
	private String floor;

	@Column(name = "altitude")
	private String altitude;

	@Column(name = "latitude")
	private String latitude;

	@Column(name = "longitude")
	private String longitude;

	@Column(name = "batt_cost")
	private String batteryCost;

	@Column(name = "batt_level")
	private String batteryLevel;

	@Column(name = "computation")
	private String computationalCost;
	
	@Column(name = "memory")
	private String memoryStatus;
	
	@Column(name = "protocol")
	private String protocol;

	@Column(name = "location_key")
	private String locationKeyword;

	@Column(name = "location_id")
	private String locationIdentifier;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public boolean isOutput() {
		return output;
	}

	public void setOutput(boolean output) {
		this.output = output;
	}

	public boolean isDigital() {
		return digital;
	}

	public void setDigital(boolean digital) {
		this.digital = digital;
	}

	public String getMaximumResponseTime() {
		return maximumResponseTime;
	}

	public void setMaximumResponseTime(String maximumResponseTime) {
		this.maximumResponseTime = maximumResponseTime;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getMeasurement() {
		return measurement;
	}

	public void setMeasurement(String measurement) {
		this.measurement = measurement;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public boolean isEnvironment() {
		return environment;
	}

	public void setEnvironment(boolean environment) {
		this.environment = environment;
	}

	public String getDeviceID() {
		return deviceID;
	}

	public void setDeviceID(String deviceID) {
		this.deviceID = deviceID;
	}

	public String getFloor() {
		return floor;
	}

	public void setFloor(String floor) {
		this.floor = floor;
	}

	public String getAltitude() {
		return altitude;
	}

	public void setAltitude(String altitude) {
		this.altitude = altitude;
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

	public String getBatteryCost() {
		return batteryCost;
	}

	public void setBatteryCost(String batteryCost) {
		this.batteryCost = batteryCost;
	}

	public String getBatteryLevel() {
		return batteryLevel;
	}

	public void setBatteryLevel(String batteryLevel) {
		this.batteryLevel = batteryLevel;
	}

	public String getComputationalCost() {
		return computationalCost;
	}

	public void setComputationalCost(String computationalCost) {
		this.computationalCost = computationalCost;
	}

	public String getMemoryStatus() {
		return memoryStatus;
	}

	public void setMemoryStatus(String memoryStatus) {
		this.memoryStatus = memoryStatus;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public String getLocationKeyword() {
		return locationKeyword;
	}

	public void setLocationKeyword(String locationKeyword) {
		this.locationKeyword = locationKeyword;
	}

	public String getLocationIdentifier() {
		return locationIdentifier;
	}

	public void setLocationIdentifier(String locationIdentifier) {
		this.locationIdentifier = locationIdentifier;
	}

}
