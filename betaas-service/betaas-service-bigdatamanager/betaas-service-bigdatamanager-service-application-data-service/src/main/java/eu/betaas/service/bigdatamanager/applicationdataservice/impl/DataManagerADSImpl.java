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
package eu.betaas.service.bigdatamanager.applicationdataservice.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import eu.betaas.rabbitmq.publisher.interfaces.Publisher;
import eu.betaas.rabbitmq.publisher.interfaces.utils.Message;
import eu.betaas.rabbitmq.publisher.interfaces.utils.MessageBuilder;
import eu.betaas.rabbitmq.publisher.interfaces.utils.Message.Layer;
import eu.betaas.service.bigdatamanager.applicationdataservice.IDataManagerADService;
import eu.betaas.service.bigdatamanager.service.datatask.AnalyticTask;
import eu.betaas.service.bigdatamanager.service.datatask.data.TaskInfo;

public class DataManagerADSImpl implements IDataManagerADService {


	private Logger logger;
	private BundleContext context;
	private ServiceTracker tracker; 
	private boolean enabledbus;
	private String gateway;
	private MessageBuilder mb;
	
	public void setupService()  {
		logger = Logger.getLogger("betaas.service");
		logger.info("BETaaS ADT Datasource");
		mb = new MessageBuilder();
		tracker = new ServiceTracker( context, AnalyticTask.class.getName(),	null ); 
		busMessage("ADS Started on GateWay "+gateway);
		tracker.open(); 
		
	}
	
	
	public String taskData(String idTask) {
		// rework to run tasks and also to have a tracker for this
		// also add a test case
		logger.debug("### ADS Task list requested with no input");
		
		if ((idTask=="")||(idTask=="test")) return dummyTask();
		
		Object [] providers = tracker.getServices(); 
		if ( providers != null && providers.length > 0 ) {
			logger.debug("TEST 6.1.X ADS Searching tasks");
			logger.debug("### providers "+providers.length );
			for(int i=0;i<providers.length;i++){
				AnalyticTask task = (AnalyticTask) providers[i];
				if (task.getTaskInfo().getTaskname().equals(idTask)){
					logger.debug("ADS has found the task, now invoking it");
					logger.debug("TEST 6.1.X ADS Found task now invoking it");
					busMessage("Returned results for task "+idTask);
					return task.getTaskData(idTask).getTaskOutput();
				}
			}
		}
		
		return null;	
		
	}

	public String taskData(String idTask,HashMap<String,String> input) {
		// rework to run tasks and also to have a tracker for this
		// also add a test case
		logger.debug("### ADS Task list requested");
		logger.debug("### searched name "+idTask );
	
		if ((idTask=="")||(idTask=="test")) return dummyTask();
		
		Object [] providers = tracker.getServices(); 
		if ( providers != null && providers.length > 0 ) {
			for(int i=0;i<providers.length;i++){
				logger.debug("### providers "+providers.length );
				AnalyticTask task = (AnalyticTask) providers[i];
				logger.debug("### providers name "+task.getTaskInfo().getTaskname() );
				logger.debug("### searched name "+idTask );
				if (task.getTaskInfo().getTaskname().equals(idTask)){
					logger.info("TEST 6.1.X ADS Found task now invoking it");
					logger.debug("### ADS Task found now invoking it");
					long ts1 = System.currentTimeMillis();
					task.runTask(input);
					long ts2 = System.currentTimeMillis();
					logger.debug("TEST 6.1.X ADS reporting a task in "+(ts2-ts1));
					busMessage("Returned results for task "+idTask);
					return task.getTaskData(idTask).getTaskOutput();
				}
			}
		}
		
		return null;
	}

	public void setContext(BundleContext setup) {
		// TODO Auto-generated method stub
		this.context=setup;
	}



	public void close() {
		logger.info("Closing ADT service");
		tracker.close();
	}

	public List<TaskInfo> getTaskList(String passcode) {
		// TODO Auto-generated method stub
		logger.debug("Building task list");
		
		List<TaskInfo> catalogue = new  ArrayList<TaskInfo>();
		Object [] providers = tracker.getServices(); 
		if ( providers != null && providers.length > 0 ) {
			for(int i=0;i<providers.length;i++){
				AnalyticTask task = (AnalyticTask) providers[i];
				catalogue.add(task.getTaskInfo());
			}
		}
		logger.debug("TEST 6.1.X ADS provide the task list");
		logger.debug("Built task list");
		busMessage("Retrieved TaskList");
		return catalogue;
	}	
	
	private String dummyTask(){
		logger.debug("Return data for application home automation on TEST mode");
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
		JsonObject jo = new JsonObject(); 
		jo.add("res", response);
		logger.debug("Returned data for application " + jo.toString());
		return jo.toString();
	}


	public String getTaskData(String taskId) {
		// TODO Auto-generated method stub
		return null;
	}
	
	private void busMessage(String message){
		
		logger.debug("Checking queue");
		if (!enabledbus)return;
		Message messageFormat = new Message();
		messageFormat.setLayer(Layer.SERVICE);
		messageFormat.setLevel("INFO");
		messageFormat.setOrigin("Application Data Manager");
		messageFormat.setDescritpion(message);
		logger.info("Sending to queue");
		ServiceReference serviceReference = context.getServiceReference(Publisher.class.getName());
		logger.debug("Sending to queue");
		if (serviceReference==null)return;
		
		Publisher service = (Publisher) context.getService(serviceReference); 
		logger.debug("Sending");

		service.publish("service.database",mb.getJsonEquivalent(messageFormat) );
		logger.debug("Sent");
		
		
	}

	public boolean isEnabledbus() {
		return enabledbus;
	}


	public void setEnabledbus(boolean enabledbus) {
		this.enabledbus = enabledbus;
	}


	public String getGateway() {
		return gateway;
	}


	public void setGateway(String gateway) {
		this.gateway = gateway;
	}


	
}
