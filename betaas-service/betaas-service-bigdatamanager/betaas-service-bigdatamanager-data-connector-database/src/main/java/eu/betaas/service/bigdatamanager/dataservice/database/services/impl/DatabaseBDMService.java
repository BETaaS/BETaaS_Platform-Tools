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
	private boolean test=false;
	//private JdbcConnectionPool connectionPool;
	private Statement createDataStructureStmt,deleteStmt;
	private PreparedStatement insertDataStructureStmt;
	private PreparedStatement taskDataStmt;
	private final String CREATESQL="CREATE TABLE IF NOT EXISTS T_THING_DATA (gatewayID varchar(255) not null, thingID varchar(255) not null," +
			"timestamp DATETIME not null, location varchar(255), unit varchar(255), type varchar(255),  measurement varchar(255),"
			+ "floor varchar(255),room varchar(255), environment varchar(255),city_name varchar(255),latitude varchar(255), longitude varchar(255), protocol varchar(255),altitude varchar(255),location_keyword varchar(255),location_identifier varchar(255), primary key (gatewayID, thingID, timestamp))";
	private final String DELETESQL="DROP TABLE IF EXISTS T_THING_DATA";
	private final String DELETETSSQL="DROP TABLE IF EXISTS LAST_IMPORT";
	private final String CREATETSSQL="CREATE TABLE IF NOT EXISTS LAST_IMPORT (timestamp DATETIME)";
	private final String INSERTDATA="insert into T_THING_DATA (gatewayID,thingID,timestamp,location,unit,type,measurement,floor,room,environment,city_name,latitude,longitude,protocol,altitude,location_keyword,location_identifier)" +
			"values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	
	

	
	
	
	private final String TASK_1_QUERY = "SELECT thingID,location,measurement,max(timestamp) from T_THING_DATA GROUP BY thingID,location,measurement ORDER BY thingID";
	private final String SELECTTIME = "SELECT * FROM LAST_IMPORT ";
	

	public void setupService()  {
		logger = Logger.getLogger("betaas.service");
		logger.info(" BETaaS Service BDM Datasource");
		logger.debug("Setup has been started");
		logger.debug("Bus is "+enabledbus);
		logger.debug("Drivers used: "+jdbc_driver);
		try {
			Class.forName(jdbc_driver);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		logger.debug("Creating database connection instance");
		
        logger.debug("Going to create an internal cpool for " + jdbc_url );
		logger.debug("Creating connection pool");
		
		props = new Properties();
		//String url = "jdbc:mysql://" + host + ":" + port + "/" + database;
		props.setProperty("user", user);
		
		props.setProperty("password", pwd);
		logger.debug("Creation is "+setup);
		// with create we are going to create the database
		if (setup.contains("create")){
			logger.debug("### Creating database");
			createDB();
		} else
		// with deleate we are going to delete the table, it suppose that table exists
		if (setup.contains("delete")){
			logger.info("### Dropping table");
			deleteTable();
		} 

		logger.debug("Creating data table");
		
		createTable();
		
		logger.debug("Setup is complete");
		
		logger.debug("### Created tables");
		
		busMessage("Database service ready: "+jdbc_url);
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
			// TODO Auto-generated catch block
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
			// TODO Auto-generated catch block
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
			// TODO Auto-generated catch block			
			e.printStackTrace();
		}
	}
	
	public void closeService() {
		//if (connectionPool!=null)connectionPool.dispose();
	}

	public void sendData(String data) {
		
		logger.debug("Received data from a Datasource");
		busMessage("Data Received");
		Connection conn=null;
		try {
			
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
			//conn = connectionPool.getConnection();
			try {
				conn = DriverManager.getConnection(jdbc_url+db_name, props);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				logger.error("Error getting connection" + e.getMessage());
				e.printStackTrace();
			}
			busMessage("Database service storing: "+arr.size());
			for(int i=0;i<arr.size();i++){
				logger.debug(" ### parsed item " + i);
				insertDataStructureStmt = conn.prepareStatement(INSERTDATA);
				// miss unit
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
				insertDataStructureStmt.executeUpdate();
				logger.debug(" updated");
				insertDataStructureStmt.close();
				logger.debug("## parsed and executed update");
			}
			logger.info("BDM Service received :  "+ arr.size() + " things data");
			// parse and fore each execute
			logger.debug("closing connection");
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			logger.error("SQL Error writing to datasource" + e.getMessage());
			e.printStackTrace();
		}
		finally{
			if (insertDataStructureStmt!=null)
				try {
					insertDataStructureStmt.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			if (conn!=null)
				try {
					conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
		}
	}
	
	private String getNormalizedValue(JsonElement input){
		if (input==null)return "null";
		if (input.isJsonNull())return "null";
		return input.getAsString();
	}
	
	 /*
	public JsonObject getTaskData(JsonObject taskData) {
		// TODO Auto-generated method stub
		
		// TODO do a call to get data from db mock the real call for the moment
		
		JsonObject jo = new JsonObject();
		Connection conn=null;
		ResultSet rs=null;
		//logger.info("Parsing "+taskData.toString());
		//JsonArray arr = (JsonArray) taskData.get("name");
		logger.info(" #### Received data from a Datasource");
		
		if (!this.test){
			
			try {
				//conn = connectionPool.getConnection();
				conn = DriverManager.getConnection(jdbc_url+db_name, props);
				taskDataStmt = conn.prepareStatement(TASK_1_QUERY);
				rs = taskDataStmt.executeQuery();
				JsonArray response_test = new JsonArray();
				JsonObject thing_test;
				HashMap<String, String> location = new HashMap<String, String>();
				HashMap<String, String> timestamp = new HashMap<String, String>();
				
				while (rs.next()){
			
					String id = rs.getString(1);
					logger.info("This id row is  "+id);
					
					if  (!location.containsKey(id)){
						logger.info("this is a new t_id "+id + " with location "+rs.getString(2) + " and ts "+rs.getString(4)+ " and state "+rs.getString(3));
						
						location.put(id, rs.getString(2));
						
						if (rs.getString(3).contentEquals("false")){
							logger.info("not a presence ");
							timestamp.put(id, "");
						} else{
							logger.info(" a presence at "+rs.getString(4));
							timestamp.put(id, rs.getString(4));
						}
						
					}else {
						// things already inserted check if it was a non presence
						logger.info(" things already exists "+rs.getString(1));
						if (timestamp.get(id)==""){
							logger.info(" was a non presence now "+ rs.getString(3));
							if (rs.getString(3).contentEquals("true")){
								logger.info(" things now has a presence at "+rs.getString(4));
								timestamp.remove(id);
								timestamp.put(id,  rs.getString(4));
							} else {
								logger.info(" things now has a presence  ");
							}
							
							
						}
						
					}
					
				}
				for (String key: timestamp.keySet()) {
					thing_test = new JsonObject();
					logger.info("generated a row");
					thing_test.addProperty("id", key);
					thing_test.addProperty("location", location.get(key));
					thing_test.addProperty("timestamp", timestamp.get(key));
					response_test.add(thing_test);	
				}
				// NEW PART START
			
				
				
				
				
				
				// NEW PART END
				// TODO build the json object to be returned
				jo.add("res", response_test);
				
				
				logger.info("from database I generated the json: "+jo.toString());
				return jo;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error("Error writing to datasource");
				e.printStackTrace();
				return null;
			} finally {
				if (taskDataStmt!=null)
					try {
						taskDataStmt.close();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				if(rs!=null)
					try {
						rs.close();
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				if(conn!=null)
					try {
						conn.close();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			}
		
		} else {
		
			logger.info("Return data for application home automation on TEST mode");
			JsonArray response = new JsonArray();
			
			JsonObject thing = new JsonObject();
			thing.addProperty("id", "thingA");
			thing.addProperty("location", "kitchen");
			thing.addProperty("timestamp", "2013-11-11 12:00:00");
	
			response.add(thing);
			thing = new JsonObject();
			thing.addProperty("id",  "thingB");
			thing.addProperty("location",  "living room");
			thing.addProperty("timestamp", "2013-10-10 18:52:00");
			response.add(thing);
			thing = new JsonObject();
			thing.addProperty("id",  "thingC");
			thing.addProperty("location",  "bedroom");
			thing.addProperty("timestamp", "");
			response.add(thing);
			
			jo.add("res", response);
			logger.info("Returned data for application " + jo.toString());
			return jo;
		
		}
	}
	  */
	public void setMode(String mode) {
		
		if (mode=="test")this.test=true;
	}

	public void setDb(String db) {
		// TODO Auto-generated method stub
		this.db_name=db;
	}

	public void setDBSetup(String setup) {
		// TODO Auto-generated method stub
		this.setup=setup;
	}
	/*
	public String taskData(String idTask) {
		// TODO Auto-generated method stub
		logger.info("Returning data");
		return getTaskData(null).toString();
	}

	public List<String> getTaskList() {
		// TODO Auto-generated method stub
		return null;
	}
	 */
	public HashMap<String,String> getInfo(String passcode) {
		// TODO Auto-generated method stub
		busMessage("Submitting info");
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
	/*
	public List<String> getTaskList(String passcode) {
		// TODO Auto-generated method stub
		// get list of task offered by this instance of database
		
		return null;
	}
	 */
	public void reportLastImportTime(Timestamp timestamp) {
		// TODO Auto-generated method stub
		try {
			busMessage("Reporting to analytic destination "+timestamp);
			Connection conn = DriverManager.getConnection(jdbc_url+db_name, props);
			createDataStructureStmt = conn.createStatement();
			createDataStructureStmt.executeUpdate("DELETE FROM LAST_IMPORT");
			createDataStructureStmt.executeUpdate("INSERT INTO LAST_IMPORT (timestamp) VALUES('"+timestamp+"')");
			createDataStructureStmt.close();
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
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
			// TODO Auto-generated catch block
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
	
	private void busMessage(String message){
		logger.debug("Checking queue");
		if (!enabledbus)return;
		logger.debug("Sending to queue");
		ServiceReference serviceReference = context.getServiceReference(Publisher.class.getName());
		logger.debug("Sending to queue");
		if (serviceReference==null)return;
		
		Publisher service = (Publisher) context.getService(serviceReference); 
		logger.debug("Sending");
		service.publish(key,message);
		logger.debug("Sent");
		
		
	}

	public String[][] getData(int column,String query) {
		try {
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
