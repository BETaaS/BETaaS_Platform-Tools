/*
 *
Copyright 2014-2015 Department of Information Engineering, University of Pisa

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

// BETaaS - Building the Environment for the Things as a Service
//
// Component: CoAP Adaptation Plugin
// Responsible: Carlo Vallati & Giacomo Tanganelli

package eu.betaas.adaptation.coap.utils;

import org.eclipse.californium.core.server.resources.ResourceAttributes;

import eu.betaas.taas.bigdatamanager.database.service.ThingsData;

public class Resource {
	private String path;
	private String deviceID;
	private Integer output;
	private Integer digital;
	private Integer maximumResponseTime;
	private Double memoryStatus;
	private Double batteryLevel;
	private String protocol;
	private String type;
	private String unit;
	private Integer environment;
	private Double latitude;
	private Double longitude;
	private Double altitude;
	private Integer floor;
	private String locationKeyword;
	private String LocationIdentifier;
	private Double ComputationalCost;
	private Double BatteryCost;
	private String measurement;
	private Server server;
	
	private final ResourceAttributes attributes;
	
	public Resource(){
		this.attributes = new ResourceAttributes();
	}
	
	public ThingsData covert(){
		ThingsData td = new ThingsData();
		td.setAltitude(altitude.toString());
		td.setBatteryCost(BatteryCost.toString());
		td.setBatteryLevel(batteryLevel.toString());
		td.setComputationalCost(ComputationalCost.toString());
		td.setDeviceID(deviceID);
		td.setDigital(digital == 1 ? true : false);
		td.setEnvironment(environment == 1 ? true : false);
		td.setFloor(floor.toString());
		td.setLatitude(latitude.toString());
		td.setLocationIdentifier(LocationIdentifier);
		td.setLocationKeyword(locationKeyword);
		td.setLongitude(longitude.toString());
		td.setMaximumResponseTime(maximumResponseTime.toString());
		td.setMeasurement(measurement);
		td.setMemoryStatus(memoryStatus.toString());
		td.setOutput(output == 1 ? true : false);
		td.setProtocol(protocol);
		td.setType(type);
		td.setUnit(unit);
		td.setThingId(deviceID);
		return td;
	}
	
	public String getDeviceID() {
		return deviceID;
	}
	public void setDeviceID(String deviceID) {
		this.deviceID = deviceID;
	}
	public Integer getOutput() {
		return output;
	}
	public void setOutput(Integer output) {
		this.output = output;
	}
	public Integer getDigital() {
		return digital;
	}
	public void setDigital(Integer digital) {
		this.digital = digital;
	}
	public Integer getMaximumResponseTime() {
		return maximumResponseTime;
	}
	public void setMaximumResponseTime(Integer maximumResponseTime) {
		this.maximumResponseTime = maximumResponseTime;
	}
	public Double getMemoryStatus() {
		return memoryStatus;
	}
	public void setMemoryStatus(Double memoryStatus) {
		this.memoryStatus = memoryStatus;
	}
	public Double getBatteryLevel() {
		return batteryLevel;
	}
	public void setBatteryLevel(Double batteryLevel) {
		this.batteryLevel = batteryLevel;
	}
	public String getProtocol() {
		return protocol;
	}
	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	public Integer getEnvironment() {
		return environment;
	}
	public void setEnvironment(Integer environment) {
		this.environment = environment;
	}
	public Double getLatitude() {
		return latitude;
	}
	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}
	public Double getLongitude() {
		return longitude;
	}
	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}
	public Double getAltitude() {
		return altitude;
	}
	public void setAltitude(Double altitude) {
		this.altitude = altitude;
	}
	public Integer getFloor() {
		return floor;
	}
	public void setFloor(Integer floor) {
		this.floor = floor;
	}
	public String getLocationKeyword() {
		return locationKeyword;
	}
	public void setLocationKeyword(String locationKeyword) {
		this.locationKeyword = locationKeyword;
	}
	public String getLocationIdentifier() {
		return LocationIdentifier;
	}
	public void setLocationIdentifier(String locationIdentifier) {
		LocationIdentifier = locationIdentifier;
	}
	public Double getComputationalCost() {
		return ComputationalCost;
	}
	public void setComputationalCost(Double computationalCost) {
		ComputationalCost = computationalCost;
	}
	public Double getBatteryCost() {
		return BatteryCost;
	}
	public void setBatteryCost(Double batteryCost) {
		BatteryCost = batteryCost;
	}
	public String getMeasurement() {
		return measurement;
	}
	public void setMeasurement(String value) {
		this.measurement = value;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}

	public ResourceAttributes getAttributes() {
		return attributes;
	}
	
	@Override
	public String toString(){
		String msg = "";
		msg += "\n" + getPath();
		msg += "\n\tDeviceId: " + getDeviceID();
		msg += "\n\tOutput: " + getOutput();
		msg += "\n\tDigital: " + getDigital();
		msg += "\n\tMaximumResponseTime: " + getMaximumResponseTime();
		msg += "\n\tMemoryStatus: " + getMemoryStatus();
		msg += "\n\tBatteryLevel: " + getBatteryLevel();
		msg += "\n\tProtocol: " + getProtocol();
		msg += "\n\tType: " + getType();
		msg += "\n\tUnit: " + getUnit();
		msg += "\n\tEnvironment: " + getEnvironment();
		msg += "\n\tLatitude: " + getLatitude();
		msg += "\n\tLongitude: " + getLongitude();
		msg += "\n\tAltitude: " + getAltitude();
		msg += "\n\tFloor: " + getFloor();
		msg += "\n\tLocationKeyword: " + getLocationKeyword();
		msg += "\n\tLocationIdentifier: " + getLocationIdentifier();
		msg += "\n\tComuptationalCost: " + getComputationalCost();
		msg += "\n\tBatteryCost: " + getBatteryCost();
		msg += "\n\tMeasurement: " + getMeasurement();
		msg += "\n\tAttributes:" + getAttributes().toString();
		return msg;
	}

	public Server getServer() {
		return server;
	}

	public void setServer(Server server) {
		this.server = server;
	}

}
