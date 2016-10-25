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
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import eu.betaas.rabbitmq.publisher.interfaces.Publisher;
import eu.betaas.rabbitmq.publisher.interfaces.utils.Message;
import eu.betaas.rabbitmq.publisher.interfaces.utils.Message.Layer;
import eu.betaas.rabbitmq.publisher.interfaces.utils.MessageBuilder;
import eu.betaas.taas.bigdatamanager.core.services.ITaasBigDataManager;
import eu.betaas.taas.bigdatamanager.database.hibernate.data.ThingData;
import eu.betaas.taas.bigdatamanager.database.hibernate.data.ThingInformation;
import eu.betaas.taas.bigdatamanager.database.service.IBigDataDatabaseService;

public class TaasBigDataManagerService implements ITaasBigDataManager {

	private IBigDataDatabaseService service;
	private Timestamp lastServiceReport;
	
	private Logger log;
	private PreparedStatement get_thing_info_pstmt;
	private static final String THING_TABLE_SQL="SELECT i.thingID,i.type,i.location,d.measurement,d.timestamp,i.unit,d.floor,d.room,d.environment,d.city_name,d.latitude,d.longitude,i.protocol,d.location_keyword,d.location_identifier  FROM T_THINGS i, T_THING_DATA d WHERE i.thingID = d.thingID AND d.timestamp >= ? ";
	//private static final String THING_TABLE_SQL="SELECT i.thingID,i.type,i.location,d.measurement,d.timestamp,i.unit,d.floor,d.room,d.environment,d.city_name,d.latitude,d.longitude,i.protocol,d.location_keyword,d.location_identifier  FROM T_THINGS i, T_THING_DATA d WHERE i.thingID = d.thingID AND d.timestamp >= ? AND d.timestamp <= ? ";
	private int counter = 0;
	private boolean busenabled;
	// setup of queue for streaming
	private boolean streaming=false;
	private BundleContext context; 
	//private String mode="direct";
	private String gateway="GW-ALFA";
	private String routingKey="";
	//private String ename="";
	MessageBuilder mb;
	private static String SEPARATOR_FIELD = "&#&";
	private static String SEPARATOR_VALUE = "-#-";
	//private static String SEPARATOR_FIELD = "";
	private List<String> messageBuffer = new Vector<String>();
	
	public void startBDMCore() {
		log = Logger.getLogger("betaas.taas");
		log.info("TAAS BD Core Started");
		lastServiceReport = new Timestamp(0);
		mb = new MessageBuilder();
		busInfoMessage("Initialized TaaS DB Core on "+gateway);		
		
	}

	public void setThingsBDM(String thingID, JsonObject data) {
		// TODO Auto-generated method stub
		log.debug("Writing a new data for" + thingID);
		// check if think exists
		log.debug("Writing a new data " + data.toString());
		ThingInformation ti = new ThingInformation();
		log.debug("Searching for things ");
		ti.setThingID(thingID);
		byte[] buf = data.toString().getBytes();
		
		if (service.searchThingInformation(ti)==null){
			
			log.debug("New thing found "+ti.getThingID());
			ThingInformation newThingInformation = new ThingInformation();
			busInfoMessage("TaaS DB storibg Thing ID "+ti.getThingID());		
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
			
		}
		
		//service.saveThingData(thingData);
		log.debug("New data for this thing"+ti.getThingID());
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
		if (checkValue(data.get("battery_level")))td.setBattery_level(getNormalizedValue(data.get("battery_level")));
		if (checkValue(data.get("location_keyword")))td.setLocation_keyword(getNormalizedValue(data.get("location_keyword")));
		if (checkValue(data.get("location_identifier")))td.setLocation_identifier(getNormalizedValue(data.get("location_identifier")));
	
		Timestamp ts;
		Date date = new Date();
		ts = new Timestamp(date.getTime());
		td.setTimestamp(ts);
		service.saveThingData(td);
		// streaming
		if (streaming){
			this.streamData(thingID, ts, data);
		}
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
		//log.info("TEST 6.1.25 Running the data retrieval now "+ts+" from "+ lastServiceReport);
		if (streaming)return null;
		try {
			db_conn = service.getConnection();
			
			get_thing_info_pstmt = db_conn.prepareStatement(THING_TABLE_SQL);
			get_thing_info_pstmt.setString(1, lastServiceReport.toString());
			//get_thing_info_pstmt.setString(2, ts.toString());
			rs = get_thing_info_pstmt.executeQuery();
			lastServiceReport = ts;
			log.debug("Processing data returned from database in to a Json response");
			jo = resultSetToByteJson(rs);
			//busInfoMessage("TaaS DB provided new data since  "+lastServiceReport.toString());			
			
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
				
			}
			jo.add("res", response);

			if (i>0)log.debug("Built json response with data" + jo.toString());
			counter = counter + response.size();

		} catch (SQLException e) {
			log.error("Exception while managing sql result set");
			e.printStackTrace();
		}
		
		log.debug("Built json response");
		return jo;
		
	}
	
	private void streamData(String thingID, Timestamp td, JsonObject data){
		
		String message="";
		
		message = addMessageField("GATEWAY",gateway,message);
		message = addMessageField("ID",thingID,message);
		message = addMessageField("TYPE",getNormalizedValue(data.get("type")),message);
		message = addMessageField("MAKER",getNormalizedValue(data.get("maker")),message);
		message = addMessageField("ROOM",getNormalizedValue(data.get("room")),message);
		message = addMessageField("FLOOR",getNormalizedValue(data.get("flooe")),message);
		message = addMessageField("ENVIRONMENT",getNormalizedValue(data.get("environment")),message);
		message = addMessageField("CITY",getNormalizedValue(data.get("city_name")),message);
		message = addMessageField("TIMESTAMP",Long.toString(td.getTime()),message);
		message = addMessageField("LAT",getNormalizedValue(data.get("latitude")),message);
		message = addMessageField("LON",getNormalizedValue(data.get("longitude")),message);
		message = addMessageField("ALT",getNormalizedValue(data.get("altitude")),message);
		message = addMessageField("LOC",getNormalizedValue(data.get("location")),message);
		
		message = addMessageField("LOC_K",getNormalizedValue(data.get("location_keyword")),message);
		message = addMessageField("LOC_ID",getNormalizedValue(data.get("location_identifier")),message);
		message = addMessageField("MEASUREMENT",getNormalizedValue(data.get("measurement")),message);
		log.debug("Built queue message");
		busMessage(message);
 	    

		
	}
	
	private String addMessageField(String name, String value , String message){
		if (message==""){
			return name+SEPARATOR_VALUE+value;
		}else {
			return message + SEPARATOR_FIELD+name+SEPARATOR_VALUE+value;
		}
		
	}
	
	private void busMessage(String message){
		log.info("Sending to queue");
		ServiceReference serviceReference = context.getServiceReference(Publisher.class.getName());
		log.info("Sending to queue");
		if (serviceReference==null){
			log.warn("Requested to publish data to queue, but service betaas publisher not found");
			messageBuffer.add(message);
			return;
		}
		Publisher service = (Publisher) context.getService(serviceReference); 
		if (service==null){
			log.warn("Requested to publish data to queue, but service betaas publisher not found");
			messageBuffer.add(message);
			return;
		}
		if (messageBuffer.size()>0){
			log.warn("Buffered data available, publishing this data now with key ");
			for (int i =0 ; i<messageBuffer.size();i++){
				service.publish(routingKey,messageBuffer.get(i));
				messageBuffer.remove(i);
			}
			
		}
	
		log.debug("this is the message built "+message);
		
		
		log.debug("Sending to "); 
		service.publish(routingKey,message);
		log.debug("Sent to queue" + routingKey);
		
		
	}
	
	private void busInfoMessage(String message){
		log.debug("Checking queue");
		if (!busenabled)return;
		Message messageFormat = new Message();
		messageFormat.setLayer(Layer.TAAS);
		messageFormat.setLevel("INFO");
		messageFormat.setOrigin("BD Manager");
		messageFormat.setDescritpion(message);
		
		log.debug("Sending to queue");
		ServiceReference serviceReference = context.getServiceReference(Publisher.class.getName());
		log.debug("Sending to queue");
		if (serviceReference==null)return;
		
		Publisher service = (Publisher) context.getService(serviceReference); 
		log.debug("Sending");
		
		
		service.publish("taas.database",mb.getJsonEquivalent(messageFormat) );
		log.debug("Sent");
		
		
	}

	public boolean isStreaming() {
		return streaming;
	}

	public void setStreaming(boolean streaming) {
		this.streaming = streaming;
	}

	public BundleContext getContext() {
		return context;
	}

	public void setContext(BundleContext context) {
		this.context = context;
	}

	public String getGateway() {
		return gateway;
	}

	public void setGateway(String gateway) {
		this.gateway = gateway;
	}

	public String getRoutingKey() {
		return routingKey;
	}

	public void setRoutingKey(String routingKey) {
		this.routingKey = routingKey;
	}

	public boolean isBusenabled() {
		return busenabled;
	}

	public void setBusenabled(boolean busenabled) {
		this.busenabled = busenabled;
	}
	

}
