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
package eu.betaas.service.bigdatamanager.service.datatask.analytic;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import eu.betaas.service.bigdatamanager.service.datatask.AnalyticTask;
import eu.betaas.service.bigdatamanager.service.datatask.data.Descriptor;
import eu.betaas.service.bigdatamanager.service.datatask.data.Descriptor.FiledDomain;
import eu.betaas.service.bigdatamanager.service.datatask.data.TaskData;
import eu.betaas.service.bigdatamanager.service.datatask.data.TaskData.TaskStatus;
import eu.betaas.service.bigdatamanager.service.datatask.data.TaskInfo;
import eu.betaas.service.bigdatamanager.service.datatask.data.TaskInfo.TaskDataType;
import eu.betaas.service.bigdatamanager.service.datatask.data.TaskInfo.TaskSource;
import eu.betaas.service.bigdatamanager.service.datatask.data.TaskInfo.TaskType;

public class PrestoLastDataQueryTask implements AnalyticTask {
	
// regexp_replace(string, pattern) 
	private String prestoJdbc = "jdbc:presto://betaashadoop:18080/hive/default";

	private String prestodrivers = "com.facebook.presto.jdbc.PrestoDriver";
	//private final String TASK_QUERY_LAST = "SELECT thingID,location,measurement,max(timestamp) from T_THING_DATA GROUP BY thingID,location,measurement ORDER BY thingID";
	//private final String TASK_QUERY_THING_LOCATION_AGGR = "SELECT location,measurement,max(timestamp) from T_THING_DATA GROUP BY thingID,location,measurement ORDER BY thingID";
	//private final String TASK_QUERY_THING_TYPE_AGGR = "SELECT thingID,location,measurement,max(timestamp) from T_THING_DATA GROUP BY thingID,location,measurement ORDER BY thingID";
	private final String TASK_QUERY_AGGREGATE = "SELECT loc,type,measurement from betaasbd";
	
			

	private TaskData taskdata;
	private TaskInfo taskInfo;
	private static String taskDescription="This task run aggregated max min and avg for each type of thing in each location";
	private Logger logger;
	
	private String user="test";
	private String pwd="";
	private Connection conn;
	
	public void setupTask(){
		logger = Logger.getLogger("betaas.service");
		logger.info("BDM Task DatabaseRecentDataTask ");
		buildTaskInfo();
		buildTaskData();
		logger = Logger.getLogger("betaas.service");
		logger.debug(" #### Starting BETaaS Service BDM Datasource ###");
		logger.debug("Setup has been started");
		logger.debug("Presto drivers loading: "+prestodrivers);
		try {
			Class.forName(prestodrivers);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger.debug("Creating database connection ");
        logger.debug("Going to create an internal cpool for " + prestoJdbc );
        Properties props = new Properties();
		props.setProperty("user", user);
		props.setProperty("password", pwd);
		logger.debug("### Task ready to serve");
		try {
			conn = DriverManager.getConnection(prestoJdbc, props);
		} catch (Exception e) {
			logger.debug("### Task Problem");
			e.printStackTrace();
		}
	}
	
	public void removeTask(){
		if (conn!=null)
			try {
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
		taskdata.setTaskOutput(getData());
		
	}

	public TaskData getTaskData(String taskId) {
		// TODO Auto-generated method stub
		logger.debug("### getdata ");
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
		 *location,type,min(measurement),max(measurement),avg(measurement)
		*/		
		
		List<Descriptor> inputList = new ArrayList<Descriptor>(); 
		List<Descriptor> outputList =  new ArrayList<Descriptor>(); 
		outputList.add(new Descriptor("location", FiledDomain.STRING, false, false, "location"));
		outputList.add(new Descriptor("type", FiledDomain.STRING, false, false, "thing type"));
		outputList.add(new Descriptor("min", FiledDomain.STRING, false, false, "max value"));
		outputList.add(new Descriptor("max", FiledDomain.STRING, false, false, "min value "));
		outputList.add(new Descriptor("avg", FiledDomain.STRING, false, false, "avg value"));
		logger.debug("### tasksimplename  "+PrestoLastDataQueryTask.class.getSimpleName());
		taskInfo = new TaskInfo(PrestoLastDataQueryTask.class.getSimpleName(),taskDescription, TaskSource.NOSQLDATABASE, TaskType.SYNCRONOUS, TaskDataType.ALL, inputList, outputList);	
				
	}
	
	private void buildTaskData(){
		taskdata = new TaskData(0, null, null, taskInfo, TaskStatus.INITIALIZED, null);
	}
	
	
	
	private String getData(){
		
	    JsonArray array = new JsonArray();

		try {
			
			Statement taskDataStmt = conn.createStatement();
			ResultSet rs = taskDataStmt.executeQuery(TASK_QUERY_AGGREGATE);
			JsonArray response_test = new JsonArray();
			JsonObject thing_test;
			
			
			while (rs.next()){
		
				String loc = rs.getString(1);
				String type = rs.getString(2);
				String max = rs.getString(3);
				String min = rs.getString(4);
				String avg = rs.getString(5);
				thing_test = new JsonObject();
				logger.debug("generated a row");
				thing_test.addProperty("location", loc);
				thing_test.addProperty("type", type);
				thing_test.addProperty("max", max);
				thing_test.addProperty("min", min);
				thing_test.addProperty("avg", avg);
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

	public String getPrestoJdbc() {
		return prestoJdbc;
	}

	public void setPrestoJdbc(String prestoJdbc) {
		this.prestoJdbc = prestoJdbc;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
	
}
