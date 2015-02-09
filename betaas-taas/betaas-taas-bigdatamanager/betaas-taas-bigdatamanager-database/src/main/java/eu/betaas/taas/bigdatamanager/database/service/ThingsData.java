//Copyright 2014-2015 Tecnalia.
//
//Licensed under the Apache License, Version 2.0 (the "License");
//you may not use this file except in compliance with the License.
//You may obtain a copy of the License at
//
//       http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.



// BETaaS - Building the Environment for the Things as a Service
//
// Component: Context Manager, TaaS Module
// Responsible: Tecnalia
package eu.betaas.taas.bigdatamanager.database.service;

import java.io.Serializable;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class ThingsData implements Serializable
{
	/**
	 *
	 */
	private static final long serialVersionUID = 1069164342683047082L;
	/**
	 * true:sensor
	 * false:actuator
	 */
	private boolean output;

	/**
	 * true:digital
	 * false:analogue
	 */
	private boolean digital;

	/**
	 * The Maximum response time in Milliseconds
	 */
	private String maximumResponseTime;
	/**
	 * The DeviceID. The device which physically accommodates the Thing and might accommodate more
	 * than one Thing. Unique within a GW
	 */
	private String deviceID;
	/**
	 * The type of the Thing
	 */
	private String type;
	/**
	 * The value of the Thing measuring unit
	 */
	private String measurement;
	/**
	 * The unit used for the measurement
	 */
	private String unit;
	/**
	 * true:public scenario
	 * false:private scenario
	 */
	private boolean environment;
	/**
	 * The thingID. Unique within a GW
	 */
	private String thingId;
	/**
	 * The floor in which the Thing resides.(Only for Private Scenario, environment == true)
	 */
	private String floor;
	/**
	 * The altitude in which the Thing resides.(Only for Private Scenario, environment == true)
	 */
	private String altitude;
	/**
	 * The latitude in which the Thing resides.(Only for Private Scenario, environment == true)
	 */
	private String latitude;
	/**
	 * The longitude in which the Thing resides.(Only for Private Scenario, environment == true)
	 */
	private String longitude;
	/**
	 * The battery cost of keeping the Thing live
	 */
	private String batteryCost;
	/**
	 * The battery level of the Thing (configurable)
	 */
	private String batteryLevel;
	/**
	 * CPU time consumed by the Thing
	 */
	private String computationalCost;
	/**
	 * Available percentage of memory allocated to the Thing
	 */
	private String memoryStatus;
	/**
	 * The protocol used by the Thing, e.g. Zigbee, ETSI M2M
	 */
	private String protocol;
	/**
	 * Mandatory information which must be a single word specific to the location of the Thing
	 */
	private String locationKeyword;
	/**
	 * (Not Mandatory) Free text specific to the location of the Thing
	 */
	private String LocationIdentifier;

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



	public String getDeviceID() {
		return deviceID;
	}



	public void setDeviceID(String deviceID) {
		this.deviceID = deviceID;
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



	public boolean getEnvironment() {
		return environment;
	}



	public void setEnvironment(boolean environment) {
		this.environment = environment;
	}



	public String getThingId() {
		return thingId;
	}



	public void setThingId(String thingId) {
		this.thingId = thingId;
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
		return LocationIdentifier;
	}



	public void setLocationIdentifier(String locationIdentifier) {
		LocationIdentifier = locationIdentifier;
	}



	public boolean isNull()
	{
		boolean bCorrect = false;

		if (this.getThingId()==null&&this.getDeviceID()==null) return true;
		if (this.getType()==null) return true;

		return bCorrect;
	}

	public ThingsData()
	{

	}

	public String getData()
	{
		//Conversion to JSON
		Gson gson = new Gson();
	    return gson.toJson(this);
	}

	private String getNormalizedValue(JsonElement input){
		if (input==null)return "null";
		if (input.isJsonNull())return "null";
		return input.getAsString();
	}


	private boolean checkValue(JsonElement input){
		if (input==null)return false;
		if (input.isJsonNull())return false;
		return true;
	}


	public ThingsData(String jsonToParse){
		JsonParser jp=new JsonParser();
		JsonObject obj = (JsonObject)jp.parse(jsonToParse);

		if (checkValue(obj.get("Output")))this.setOutput((getNormalizedValue(obj.get("Output"))=="true"));
		if (checkValue(obj.get("Digital")))this.setDigital((getNormalizedValue(obj.get("Digital"))=="true"));
		if (checkValue(obj.get("MaximumResponseTime")))this.setMaximumResponseTime(getNormalizedValue(obj.get("MaximumResponseTime")));
		if (checkValue(obj.get("MemoryStatus")))this.setMemoryStatus(getNormalizedValue(obj.get("MemoryStatus")));
		if (checkValue(obj.get("ComputationalCost")))this.setComputationalCost(getNormalizedValue(obj.get("ComputationalCost")));
		if (checkValue(obj.get("BatteryLevel")))this.setBatteryLevel(getNormalizedValue(obj.get("BatteryLevel")));
		if (checkValue(obj.get("BatteryCost")))this.setBatteryCost(getNormalizedValue(obj.get("BatteryCost")));
		if (checkValue(obj.get("Measurement")))this.setMeasurement(getNormalizedValue(obj.get("Measurement")));
		if (checkValue(obj.get("Protocol")))this.setProtocol(getNormalizedValue(obj.get("Protocol")));
		if (checkValue(obj.get("DeviceID")))this.setDeviceID(getNormalizedValue(obj.get("DeviceID")));
		if (checkValue(obj.get("ThingId")))this.setThingId(getNormalizedValue(obj.get("ThingId")));
		if (checkValue(obj.get("Type")))this.setType(getNormalizedValue(obj.get("Type")));
		if (checkValue(obj.get("Unit")))this.setUnit(getNormalizedValue(obj.get("Unit")));
		if (checkValue(obj.get("Environment")))this.setEnvironment((getNormalizedValue(obj.get("Environment"))=="true"));
		if (checkValue(obj.get("Latitude")))this.setLatitude(getNormalizedValue(obj.get("Latitude")));
		if (checkValue(obj.get("Longitude")))this.setLongitude(getNormalizedValue(obj.get("Longitude")));
		if (checkValue(obj.get("Altitude")))this.setAltitude(getNormalizedValue(obj.get("Altitude")));
		if (checkValue(obj.get("Floor")))this.setFloor(getNormalizedValue(obj.get("Floor")));
		if (checkValue(obj.get("LocationKeyword")))this.setLocationKeyword(getNormalizedValue(obj.get("LocationKeyword")));
		if (checkValue(obj.get("LocationIdentifier")))this.setLocationIdentifier(getNormalizedValue(obj.get("LocationIdentifier")));


	}

	public String getJsonRepresentation(){
		JsonObject jsonData = new JsonObject();
		jsonData.addProperty("Output", this.isOutput());
		jsonData.addProperty("Digital", this.isDigital());
		jsonData.addProperty("MemoryStatus",this.getMemoryStatus());
		jsonData.addProperty("MaximumResponseTime",this.getMaximumResponseTime());
		jsonData.addProperty("ComputationalCost",this.getComputationalCost());
		jsonData.addProperty("BatteryLevel",this.getBatteryLevel());
		jsonData.addProperty("BatteryCost",this.getBatteryCost());
		jsonData.addProperty("Measurement",this.getMeasurement());
		jsonData.addProperty("Protocol",this.getProtocol());
		jsonData.addProperty("DeviceID",this.getDeviceID());
		jsonData.addProperty("ThingId",this.getThingId());
		jsonData.addProperty("Type",this.getType());
		jsonData.addProperty("Unit",this.getUnit());
		jsonData.addProperty("Environment",this.getEnvironment());
		jsonData.addProperty("Latitude",this.getLatitude());
		jsonData.addProperty("Longitude",this.getLongitude());
		jsonData.addProperty("Altitude",this.getAltitude());
		jsonData.addProperty("Floor",this.getFloor());
		jsonData.addProperty("LocationKeyword",this.getLocationKeyword());
		jsonData.addProperty("LocationIdentifier",this.getLocationIdentifier());
		return jsonData.toString();
	}


}
