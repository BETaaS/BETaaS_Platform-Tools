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
package eu.betaas.service.bigdatamanager.service.datatask.impl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import eu.betaas.service.bigdatamanager.dataservice.IDatasourceBDMInterface;
import eu.betaas.service.bigdatamanager.service.datatask.AnalyticTask;
import eu.betaas.service.bigdatamanager.service.datatask.data.Descriptor;
import eu.betaas.service.bigdatamanager.service.datatask.data.Descriptor.FiledDomain;
import eu.betaas.service.bigdatamanager.service.datatask.data.TaskData;
import eu.betaas.service.bigdatamanager.service.datatask.data.TaskData.TaskStatus;
import eu.betaas.service.bigdatamanager.service.datatask.data.TaskInfo;
import eu.betaas.service.bigdatamanager.service.datatask.data.TaskInfo.TaskDataType;
import eu.betaas.service.bigdatamanager.service.datatask.data.TaskInfo.TaskSource;
import eu.betaas.service.bigdatamanager.service.datatask.data.TaskInfo.TaskType;

public class DatabaseRecentdataTask implements AnalyticTask {

	
	private final String TASK_1_QUERY = "SELECT thingID,location,measurement,max(timestamp) from T_THING_DATA GROUP BY thingID,location,measurement ORDER BY thingID";
	private TaskData taskdata;
	private TaskInfo taskInfo;
	private static String taskDescription="This task return from the databases service the last reported value for each thing";
	private Logger logger;
	private BundleContext context;
	private ServiceTracker tracker; 
	
	public void setupTask(){
		logger = Logger.getLogger("betaas.service");
		logger.info("BDM Task DatabaseRecentDataTask ");
		buildTaskInfo();
		buildTaskData();
		tracker = new ServiceTracker( context, IDatasourceBDMInterface.class.getName(),	null ); 
		tracker.open(); 
		
	}
	
	public void removeTask(){
		tracker.close();
	}
	
	public void setContext(BundleContext context) {
		this.context=context;
		
	}
	
	public TaskInfo getTaskInfo() {
		return taskInfo;
	}

	public void runTask(HashMap<String,String> input) {
		// TODO Auto-generated method stub
		logger.debug("### Run DatabaseRecentDataTask ");
		Timestamp time=new Timestamp(System.currentTimeMillis());
		taskdata.setTasktarttime(time);
		taskdata.setActualinput(input);
		taskdata.setStatus(TaskStatus.RUNNING);		
		taskdata.setTaskOutput(getAllData());
		
	}

	public TaskData getTaskData(String taskId) {
		// TODO Auto-generated method stub
		logger.debug("### getdata ");
		getAllData();
		taskdata.setStatus(TaskStatus.ENDED);
		return taskdata;
	}

	public boolean taskCompleted(String taskId) {
		// TODO Auto-generated method stub
		if (taskdata == null) return false;
		return true;
	}
	
	private void buildTaskInfo(){
		
		/*SELECT 
		 * thingID,
		 * location,
		 * measurement,
		 * max(timestamp) 
		 * from T_THING_DATA 
		 * GROUP BY thingID,location,measurement ORDER BY thingID;
		*/		
		
		List<Descriptor> inputList = new ArrayList<Descriptor>(); 
		List<Descriptor> outputList =  new ArrayList<Descriptor>(); 
		outputList.add(new Descriptor("thingID", FiledDomain.STRING, false, false, "Identifier for the thing"));
		outputList.add(new Descriptor("location", FiledDomain.STRING, false, false, "thing location"));
		outputList.add(new Descriptor("measurement", FiledDomain.STRING, false, false, "measurement type"));
		outputList.add(new Descriptor("timestamp", FiledDomain.STRING, false, false, "time of measurement"));
		logger.debug("### tasksimplename  "+DatabaseRecentdataTask.class.getSimpleName());
		taskInfo = new TaskInfo(DatabaseRecentdataTask.class.getSimpleName(),taskDescription, TaskSource.SQLDATABASE, TaskType.SYNCRONOUS, TaskDataType.ALL, inputList, outputList);	
				
	}
	
	private void buildTaskData(){
		taskdata = new TaskData(0, null, null, taskInfo, TaskStatus.INITIALIZED, null);
	}
	
	private String getAllData(){
		
		// for each service found get its data
		String response = null;
		JsonArray ja = new JsonArray();
		response = ja.toString();
		
		Object [] providers = tracker.getServices(); 
		if ( providers != null && providers.length > 0 ) {
			for(int i=0;i<providers.length;i++){
				IDatasourceBDMInterface ibds = (IDatasourceBDMInterface) providers[i];
				response = getFromDataSource(response,ibds.getInfo("").get("url"),ibds.getInfo("").get("driver"),ibds.getInfo("").get("user"),ibds.getInfo("").get("pwd"),ibds.getInfo("").get("db"));
			}
		}
		JsonArray resultsarray = new JsonParser().parse(response).getAsJsonArray();
		JsonObject taskResults= new JsonObject();
		taskResults.add("res", resultsarray);
		
		return taskResults.toString();
	}
	
	private String getFromDataSource(String data,String jdbc_url,String jdbc_driver,String user,String pwd,String db){
		logger.info("Drivers used: "+jdbc_driver);

	    JsonArray array = new JsonParser().parse(data).getAsJsonArray();
 
		
		try {
			Class.forName(jdbc_driver);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger.debug("Creating database connection instance");
		
	    logger.debug("Going to create an internal cpool for " + jdbc_url +db );
		logger.debug("Creating connection pool");
		
		Properties props = new Properties();
		//String url = "jdbc:mysql://" + host + ":" + port + "/" + database;
		props.setProperty("user", user);
		props.setProperty("password", pwd);
		try {
			Connection conn = DriverManager.getConnection(jdbc_url+db, props);
			PreparedStatement taskDataStmt = conn.prepareStatement(TASK_1_QUERY);
			ResultSet rs = taskDataStmt.executeQuery();
			JsonArray response_test = new JsonArray();
			JsonObject thing_test;
			HashMap<String, String> location = new HashMap<String, String>();
			HashMap<String, String> timestamp = new HashMap<String, String>();
			
			while (rs.next()){
		
				String id = rs.getString(1);
				logger.debug("This id row is  "+id);
				
				if  (!location.containsKey(id)){
					logger.debug("this is a new t_id "+id + " with location "+rs.getString(2) + " and ts "+rs.getString(4)+ " and state "+rs.getString(3));
					
					location.put(id, rs.getString(2));
					
					if (rs.getString(3).contentEquals("false")){
						logger.debug("not a presence ");
						timestamp.put(id, "");
					} else{
						logger.debug(" a presence at "+rs.getString(4));
						timestamp.put(id, rs.getString(4));
					}
					
				}else {
					// things already inserted check if it was a non presence
					logger.debug(" things already exists "+rs.getString(1));
					if (timestamp.get(id)==""){
						logger.debug(" was a non presence now "+ rs.getString(3));
						if (rs.getString(3).contentEquals("true")){
							logger.debug(" things now has a presence at "+rs.getString(4));
							timestamp.remove(id);
							timestamp.put(id,  rs.getString(4));
						} else {
							logger.debug(" things now has a presence  ");
						}
						
						
					}
					
				}
				
			}
			for (String key: timestamp.keySet()) {
				thing_test = new JsonObject();
				logger.debug("generated a row");
				thing_test.addProperty("id", key);
				thing_test.addProperty("location", location.get(key));
				thing_test.addProperty("timestamp", timestamp.get(key));
				response_test.add(thing_test);	
			}
			logger.debug("generated from this source row "+response_test.size());
			for (int k=0;k<response_test.size();k++){
				array.add(response_test.get(k));
			}
			
			conn.close();
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return array.toString();
	}


}
