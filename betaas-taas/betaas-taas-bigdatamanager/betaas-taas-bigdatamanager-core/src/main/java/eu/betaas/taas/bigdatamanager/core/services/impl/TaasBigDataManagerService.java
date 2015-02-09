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
package eu.betaas.taas.bigdatamanager.core.services.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import org.apache.log4j.Logger;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;





import eu.betaas.taas.bigdatamanager.core.services.ITaasBigDataManager;
import eu.betaas.taas.bigdatamanager.database.hibernate.data.ThingData;
import eu.betaas.taas.bigdatamanager.database.hibernate.data.ThingInformation;
import eu.betaas.taas.bigdatamanager.database.service.IBigDataDatabaseService;

public class TaasBigDataManagerService implements ITaasBigDataManager {

	private IBigDataDatabaseService service;
	private Timestamp lastServiceReport;
	private Logger log;
	private PreparedStatement get_thing_info_pstmt;
	private static final String THING_TABLE_SQL="SELECT i.thingID,i.type,i.location,d.measurement,d.timestamp,i.unit,d.floor,d.room,d.environment,d.city_name,d.latitude,d.longitude,i.protocol,d.location_keyword,d.location_identifier  FROM T_THINGS i, T_THING_DATA d WHERE i.thingID = d.thingID AND d.timestamp >= ? AND d.timestamp <= ? ";
	
	public void startBDMCore() {
		log = Logger.getLogger("betaas.taas");
		log.info("TAAS BD Core Started");
		lastServiceReport = new Timestamp(0);
	}

	public void setThingsBDM(String thingID, JsonObject data) {
		// TODO Auto-generated method stub
		log.debug("Writing a new data for" + thingID);
		// check if think exists
		log.debug("Writing a new data " + data.toString());
		ThingInformation ti = new ThingInformation();
		log.debug("Searching for things ");
		ti.setThingID(thingID);

		if (service.searchThingInformation(ti)==null){
			
			log.info("New thing found "+ti.getThingID());
			ThingInformation newThingInformation = new ThingInformation();
			
			newThingInformation.setThingID(thingID);				
		
			// check if is location or environment
			//newThingInformation.setLocation(data.get("environment").getAsString());
			newThingInformation.setType(data.get("type").getAsString());
			newThingInformation.setUnit(data.get("unit").getAsString());
			if (checkValue(data.get("maker")))newThingInformation.setManufacturer(getNormalizedValue(data.get("maker")));
			if (checkValue(data.get("serial")))newThingInformation.setSerial(getNormalizedValue(data.get("serial")));
			if (checkValue(data.get("is_digital")))newThingInformation.setIs_digital(data.get("is_digital").getAsBoolean());
			if (checkValue(data.get("is_input")))newThingInformation.setIs_input(data.get("is_input").getAsBoolean());
			if (checkValue(data.get("room")))newThingInformation.setLocation(getNormalizedValue(data.get("room")));
			if (checkValue(data.get("protocol")))newThingInformation.setProtocol(getNormalizedValue(data.get("protocol")));
			if (checkValue(data.get("maximum_response_time")))newThingInformation.setMaximum_response_time(getNormalizedValue(data.get("maximum_response_time")));
			if (checkValue(data.get("computational_cost")))newThingInformation.setComputational_cost(getNormalizedValue(data.get("computational_cost")));			
			service.saveThingInformation(newThingInformation);
			log.info("Thing data Saved ");
		}
		
		//service.saveThingData(thingData);
		log.info("New data for this thing"+ti.getThingID());
		ThingData td = new ThingData();
		td.setThingID(thingID);
		
		td.setMeasurement(getNormalizedValue(data.get("measurement")));
		if (checkValue(data.get("room")))td.setRoom(getNormalizedValue(data.get("room")));
		if (checkValue(data.get("floor")))td.setFloor(getNormalizedValue(data.get("floor")));
		if (checkValue(data.get("environment")))td.setEnvironment(getNormalizedValue(data.get("environment")));
		if (checkValue(data.get("city_name")))td.setCity_name(getNormalizedValue(data.get("city_name")));
		if (checkValue(data.get("latitude")))td.setLatitude(getNormalizedValue(data.get("latitude")));
		if (checkValue(data.get("longitude")))td.setLongitude(getNormalizedValue(data.get("longitude")));
		if (checkValue(data.get("location")))td.setLocation(getNormalizedValue(data.get("location")));
		if (checkValue(data.get("altitude")))td.setAltitude(getNormalizedValue(data.get("altitude")));
		if (checkValue(data.get("memory_status")))td.setMemory_status(getNormalizedValue(data.get("memory_status")));
		if (checkValue(data.get("battery_cost")))td.setBattery_cost(getNormalizedValue(data.get("battery_cost")));
		if (checkValue(data.get("location_keyword")))td.setLocation_keyword(getNormalizedValue(data.get("location_keyword")));
		if (checkValue(data.get("location_identifier")))td.setLocation_identifier(getNormalizedValue(data.get("location_identifier")));
	
	 	    
	 
		Timestamp ts;
		Date date = new Date();
		ts = new Timestamp(date.getTime());
		td.setTimestamp(ts);
		log.debug("Save data " + data.get("measurement").getAsString());
		service.saveThingData(td);
		log.debug("Saved ");

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

	public void setService(IBigDataDatabaseService service) {
		this.service=service;
		
	}

	public JsonObject getThingsData(Timestamp ts)  {
		Connection db_conn=null;
		JsonObject jo=null;
		ResultSet rs = null;
		log.debug("Running the data retrieval at "+ts+" from "+ lastServiceReport);

		try {
			db_conn = service.getConnection();
			
			get_thing_info_pstmt = db_conn.prepareStatement(THING_TABLE_SQL);
			get_thing_info_pstmt.setString(1, lastServiceReport.toString());
			get_thing_info_pstmt.setString(2, ts.toString());
			rs = get_thing_info_pstmt.executeQuery();
			lastServiceReport = ts;
			log.debug("Processing data returned from database in to a Json response");
			jo = resultSetToByteJson(rs);
			
			return jo;
		} catch (Exception e) {
			log.error("Exception while executing sql");
			
			e.printStackTrace();
			
		} finally {
			
			if (get_thing_info_pstmt!=null)
				try {
					get_thing_info_pstmt.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			if (rs!=null)
				try {
					rs.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			if (db_conn!=null)
				try {
					db_conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
			
			
			
		}

		return null;
		
	}

	private JsonObject resultSetToByteJson(ResultSet rs){

		JsonObject jo = new JsonObject();
		log.debug("Building json response");
		int i=0;
		try {
			JsonArray response = new JsonArray();
			while (rs.next()){
				i++;
				// i.thingID,i.type,d.room,d.measurement,d.timestamp,i.unit,d.floor,d.room
				JsonObject thing = new JsonObject();
				thing.addProperty("id", rs.getString(1));
				thing.addProperty("type", rs.getString(2));
				thing.addProperty("location", rs.getString(3));
				thing.addProperty("measurement", rs.getString(4));
				thing.addProperty("timestamp", rs.getString(5));
				thing.addProperty("unit", rs.getString(6));
				thing.addProperty("floor", rs.getString(7));
				thing.addProperty("room", rs.getString(8));
				thing.addProperty("environment", rs.getString(9));
				thing.addProperty("city_name", rs.getString(10));
				thing.addProperty("latitude", rs.getString(11));
				thing.addProperty("longitude", rs.getString(12));
				thing.addProperty("protocol", rs.getString(13));
				thing.addProperty("location_keyword", rs.getString(14));
				thing.addProperty("location_identifier", rs.getString(15));

				response.add(thing);
				log.debug("Parsed the row " + thing.toString());
			}
			jo.add("res", response);
			if (i>0)log.debug("Built json response with data" + jo.toString());
		} catch (SQLException e) {
			log.error("Exception while managing sql result set");
			e.printStackTrace();
		}
		
		log.debug("Built json response");
		return jo;
		
	}
	

}
