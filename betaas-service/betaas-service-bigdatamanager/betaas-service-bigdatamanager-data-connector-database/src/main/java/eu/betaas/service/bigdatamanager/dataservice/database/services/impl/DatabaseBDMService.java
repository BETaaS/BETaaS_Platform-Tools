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
package eu.betaas.service.bigdatamanager.dataservice.database.services.impl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Properties;

import org.apache.log4j.Logger;
//import org.h2.jdbcx.JdbcConnectionPool;
//import org.mariadb.jdbc.JDBCUrl;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import eu.betaas.rabbitmq.publisher.interfaces.Publisher;
import eu.betaas.rabbitmq.publisher.interfaces.utils.Message;
import eu.betaas.rabbitmq.publisher.interfaces.utils.MessageBuilder;
import eu.betaas.rabbitmq.publisher.interfaces.utils.Message.Layer;
import eu.betaas.service.bigdatamanager.dataservice.IDatasourceBDMInterface;


public class DatabaseBDMService implements IDatasourceBDMInterface {

	private boolean enabledbus=false;
	private BundleContext context; 
	private String key = "betaasbus";
	private String jdbc_driver;
	private String jdbc_url;
	private String user;
	private String pwd;
	private String db_name;
	private String setup;
	private String id_instance;
	private int queue_max=50;
	private int queue_cur=0;
	private Logger logger;
	private Properties props;
	private boolean enabled=false;
	private Statement createDataStructureStmt,deleteStmt;
	private PreparedStatement insertDataStructureStmt;
	private final String CREATESQL="CREATE TABLE IF NOT EXISTS T_THING_DATA (gatewayID varchar(255) not null, thingID varchar(255) not null," +
			"timestamp  DATETIME(6) not null, location varchar(255), unit varchar(255), type varchar(255),  measurement varchar(255),"
			+ "floor varchar(255),room varchar(255), environment varchar(255),city_name varchar(255),latitude varchar(255), longitude varchar(255), protocol varchar(255),altitude varchar(255),location_keyword varchar(255),location_identifier varchar(255), primary key (gatewayID, thingID, timestamp))";
	private final String DELETESQL="DROP TABLE IF EXISTS T_THING_DATA";
	private final String DELETETSSQL="DROP TABLE IF EXISTS LAST_IMPORT";
	private final String CREATETSSQL="CREATE TABLE IF NOT EXISTS LAST_IMPORT (timestamp DATETIME)";
	private final String INSERTDATA="insert into T_THING_DATA (gatewayID,thingID,timestamp,location,unit,type,measurement,floor,room,environment,city_name,latitude,longitude,protocol,altitude,location_keyword,location_identifier)" +
			"values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	private int counter=0;
	private final String SELECTTIME = "SELECT * FROM LAST_IMPORT ";
	private MessageBuilder mb;
	private String gateway="GW-ALFA";

	public void setupService()  {
		logger = Logger.getLogger("betaas.service");
		logger.info(" BETaaS Service BDM Datasource");
		logger.debug("Setup has been started");
		logger.debug("Bus is "+enabledbus);
		if (!enabled){
			logger.info(" BETaaS Service BDM Datasource is disabled for this Gateway");
			return;
		}
		logger.debug("Drivers used: "+jdbc_driver);
		try {
			Class.forName(jdbc_driver);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		logger.debug("Creating database connection instance");
        logger.debug("Going to create an internal cpool for " + jdbc_url );
		logger.debug("Creating connection pool");
		
		props = new Properties();
		props.setProperty("user", user);
		
		props.setProperty("password", pwd);
		logger.debug("Creation is "+setup);
		if (setup.contains("create")){
			logger.debug("### Creating database");
			createDB();
		} else
		if (setup.contains("delete")){
			logger.debug("### Dropping table");
			deleteTable();
		} 
		logger.debug("Creating data table");
		createTable();
		logger.debug("Setup is complete");
		logger.debug("### Created tables");
		logger.debug("TEST 6.1.X Log system ");
		mb = new MessageBuilder();
		busMessage("Database service on gateway " +gateway+ " ready: "+jdbc_url);
	}
	

	
	
	public boolean isEnabled() {
		return enabled;
	}




	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}




	private void deleteTable(){
		try {
			logger.debug("Deleting old table if exists");
			Connection conn = DriverManager.getConnection(jdbc_url+db_name, props);
			deleteStmt = conn.createStatement();
			deleteStmt.executeUpdate(DELETESQL);
			deleteStmt.executeUpdate(DELETETSSQL);
			deleteStmt.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private void createTable(){
		try {
			Connection conn = DriverManager.getConnection(jdbc_url+db_name, props);
			createDataStructureStmt = conn.createStatement();
			createDataStructureStmt.executeUpdate(CREATESQL);
			createDataStructureStmt.executeUpdate(CREATETSSQL);
			createDataStructureStmt.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private void createDB(){		
		
		try {
			Connection conn;
			conn = DriverManager.getConnection(jdbc_url, props);
			Statement statement = conn.createStatement();
			int returnedVal = statement.executeUpdate("CREATE DATABASE IF NOT EXISTS "+db_name);
			logger.debug("Creation returned "+returnedVal);
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void closeService() {
		logger.debug("BDM Database stopped");
	}

	public void sendData(String data) {
		
		logger.debug("Received data from a Datasource");
		
		Connection conn=null;
		try {
			long ts1 = System.currentTimeMillis();
			if (data==null){ 
				logger.debug("Received from Core Service BDM a null input");
				return;
			}
			JsonParser jp=new JsonParser(); 
			JsonObject obj = (JsonObject)jp.parse(data);
			JsonArray arr = (JsonArray) obj.get("res");
			if (arr==null){ 
				logger.warn("Json from Core Service BDM is null");
				return;
			}
			if (arr.size()==0) {
				logger.warn("Data from Core Service BDM is null");
				return;
			}
			logger.debug("Parsing data "+data.toString());
			logger.debug("parse occurring for  "+ arr.size());
			try {
				conn = DriverManager.getConnection(jdbc_url+db_name, props);
			} catch (SQLException e) {
				logger.error("Error getting connection" + e.getMessage());
				e.printStackTrace();
			}
			busMessage("Database service storing data "+arr.size());
			logger.debug("TEST 6.1.26.	LAB-TP-120-040 - BDM Service Received  data " +arr.size());
			counter=counter+arr.size();
			
			logger.debug("TEST 6.1.26.	LAB-TP-120-040 - BDM Service Received total data " +counter);
			for(int i=0;i<arr.size();i++){
				
				logger.debug(" ### parsed item " + i);
				insertDataStructureStmt = conn.prepareStatement(INSERTDATA);
				logger.debug(" loading item into statement");
				JsonObject jo = (JsonObject) arr.get(i);
				insertDataStructureStmt.setString(1,"gw-0");
				insertDataStructureStmt.setString(2,getNormalizedValue(jo.get("id")));
				insertDataStructureStmt.setString(3,getNormalizedValue(jo.get("timestamp")));
				insertDataStructureStmt.setString(4,getNormalizedValue(jo.get("location")));
				insertDataStructureStmt.setString(5,getNormalizedValue(jo.get("unit")));
				insertDataStructureStmt.setString(6,getNormalizedValue(jo.get("type")));
				insertDataStructureStmt.setString(8,getNormalizedValue(jo.get("floor")));
				insertDataStructureStmt.setString(9,getNormalizedValue(jo.get("room")));
				insertDataStructureStmt.setString(10,getNormalizedValue(jo.get("environment")));
				insertDataStructureStmt.setString(11,getNormalizedValue(jo.get("city_name")));
				insertDataStructureStmt.setString(12,getNormalizedValue(jo.get("latitude")));
				insertDataStructureStmt.setString(13,getNormalizedValue(jo.get("longitude")));
				insertDataStructureStmt.setString(14,getNormalizedValue(jo.get("protocol")));
				insertDataStructureStmt.setString(15,getNormalizedValue(jo.get("altitude")));
				insertDataStructureStmt.setString(16,getNormalizedValue(jo.get("location_keyword")));
				insertDataStructureStmt.setString(17,getNormalizedValue(jo.get("location_identifier")));

				if (jo.get("measurement")!=null){
					insertDataStructureStmt.setString(7,jo.get("measurement").getAsString());
				}
				
				logger.debug("TEST 6.1.26.	LAB-TP-120-050 - BDM Service Storing data " );
				long tsu1 = System.currentTimeMillis();
				insertDataStructureStmt.executeUpdate();
				long tsu2 = System.currentTimeMillis();
				logger.debug("TEST 6.1.26.	LAB-TP-120-050 - BDM Service Stored data in  "+(tsu2-tsu1) );
				logger.debug(" updated");
				insertDataStructureStmt.close();
				logger.debug("## parsed and executed update");
			}
			logger.debug("BDM Service received :  "+ arr.size() + " things data");
			logger.debug("closing connection");
			conn.close();
			long ts2 = System.currentTimeMillis();
			logger.debug("BDM Service received :  "+ arr.size() + " things data and processed it in "+(ts2-ts1));
			
		} catch (SQLException e) {
			logger.error("SQL Error writing to datasource" + e.getMessage());
			e.printStackTrace();
		}
		finally{
			if (insertDataStructureStmt!=null)
				try {
					insertDataStructureStmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			if (conn!=null)
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			
		}
	}
	
	private String getNormalizedValue(JsonElement input){
		if (input==null)return "null";
		if (input.isJsonNull())return "null";
		return input.getAsString();
	}
	



	public HashMap<String,String> getInfo(String passcode) {
		//busMessage("Submitting info");
		HashMap<String, String> info = new HashMap<String, String>();
		info.put("db", this.db_name);
		info.put("url", this.jdbc_url);
		info.put("driver", this.jdbc_driver);
		info.put("user", this.user);
		info.put("pwd", this.pwd);
		info.put("id", this.id_instance);
		info.put("queue_cur", String.valueOf(this.queue_cur));
		info.put("queue_max", String.valueOf(this.queue_max));
		
		
		return info;
	}

	public void reportLastImportTime(Timestamp timestamp) {
		try {
			busMessage("Reporting to analytic destination last time data have been transferred  "+timestamp);
			Connection conn = DriverManager.getConnection(jdbc_url+db_name, props);
			createDataStructureStmt = conn.createStatement();
			createDataStructureStmt.executeUpdate("DELETE FROM LAST_IMPORT");
			createDataStructureStmt.executeUpdate("INSERT INTO LAST_IMPORT (timestamp) VALUES('"+timestamp+"')");
			createDataStructureStmt.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public Timestamp getLastImportTime() {
		try {
			Timestamp timestamp ;
			Connection conn = DriverManager.getConnection(jdbc_url+db_name, props);
			createDataStructureStmt = conn.createStatement();
			ResultSet rs = createDataStructureStmt.executeQuery(SELECTTIME);
			if (rs.next()){
				timestamp = (rs.getTimestamp(1));
			}else {
				timestamp=null;
			}
			createDataStructureStmt.close();
			conn.close();
			return timestamp;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private void busMessage(String message){
		
		logger.debug("Checking queue");
		if (!enabledbus)return;
		Message messageFormat = new Message();
		messageFormat.setLayer(Layer.SERVICE);
		messageFormat.setLevel("INFO");
		messageFormat.setOrigin("BD Manager");
		messageFormat.setDescritpion(message);
		logger.info("Sending to queue");
		ServiceReference serviceReference = context.getServiceReference(Publisher.class.getName());
		logger.debug("Sending to queue");
		if (serviceReference==null)return;
		
		Publisher service = (Publisher) context.getService(serviceReference); 
		logger.debug("Sending");

		service.publish("service.database",mb.getJsonEquivalent(messageFormat) );
		logger.info("Sent");
		
		
	}

	public String[][] getData(int column,String query) {
		try {
			if (!enabled)return null;
			Connection conn = DriverManager.getConnection(jdbc_url+db_name, props);
			createDataStructureStmt = conn.createStatement();
			ResultSet rs = createDataStructureStmt.executeQuery(query);
			if (rs == null) return null;
			int countrow=0;
			while (rs.next()){
				countrow++;
			}
			rs.beforeFirst();
			String [][] s = new String[countrow][column];
			int rown=0;
			while (rs.next()){
				for(int i=0;i<countrow;i++){
					s[rown][i]=rs.getString(i);
				}
				rown++;
			}
			createDataStructureStmt.close();
			conn.close();
			return s;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}	
	
	public void setJdbc(String jdbc) {
		this.jdbc_url=jdbc;
	}

	public void setDrivers(String drivers) {
		this.jdbc_driver=drivers;
	}

	public void setUser(String user) {
		this.user=user;		
	}

	public void setPwd(String pwd) {
		this.pwd=pwd;		
	}
		
	public String getId_instance() {
		return id_instance;
	}

	public void setId_instance(String id_instance) {
		this.id_instance = id_instance;
	}

	public int getQueue_max() {
		return queue_max;
	}

	public void setQueue_max(int queue_max) {
		this.queue_max = queue_max;
	}

	public int getQueue_cur() {
		return queue_cur;
	}

	public void setQueue_cur(int queue_cur) {
		this.queue_cur = queue_cur;
	}

	public boolean isEnabledbus() {
		return enabledbus;
	}

	public void setEnabledbus(boolean enabledbus) {
		this.enabledbus = enabledbus;
	}

	public BundleContext getContext() {
		return context;
	}

	public void setContext(BundleContext context) {
		this.context = context;
	}
	
	public void setMode(String mode) {
		if (mode=="off")this.enabled=false;
	}

	public void setDb(String db) {
		this.db_name=db;
	}

	public void setDBSetup(String setup) {
		this.setup=setup;
	}




	public String getGateway() {
		return gateway;
	}




	public void setGateway(String gateway) {
		this.gateway = gateway;
	}

}
