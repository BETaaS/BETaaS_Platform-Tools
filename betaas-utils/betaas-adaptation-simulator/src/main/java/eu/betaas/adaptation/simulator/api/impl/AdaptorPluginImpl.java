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
package eu.betaas.adaptation.simulator.api.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;

import eu.betaas.adaptation.plugin.api.IAdaptorListener;
import eu.betaas.adaptation.plugin.api.IAdaptorPlugin;
import eu.betaas.adaptation.simulator.utils.FileUtils;

public class AdaptorPluginImpl implements IAdaptorPlugin {

	private IAdaptorListener listener;
	private LinkedHashMap<String,Thread> listOfThreads = new LinkedHashMap<String,Thread>();
	protected Vector<HashMap<String, String>> sensors = new Vector<HashMap<String, String>>();
	private String sensorsFolder;
	private LinkedHashMap<String, Integer> counters = new LinkedHashMap<String,Integer>();
	Logger mLogger = Logger.getLogger("betaas.thingsadaptor");
	
	public void setListener(IAdaptorListener listener) {
		this.listener = listener;
	}

	public Vector<HashMap<String, String>> discover() {
		sensors = new Vector<HashMap<String, String>>();
		final File folder = new File(sensorsFolder);
		for (final File fileEntry : folder.listFiles()) {
			HashMap<String, String> hash = new HashMap<String, String>();
	        if (!fileEntry.isDirectory() && fileEntry.getName() != null 
	        		&& (fileEntry.getName().endsWith(".csv") || fileEntry.getName().endsWith(".CSV"))) {
	        	final int extensionPosition = fileEntry.getName().indexOf(".");        	
	        	//Get only the name of the file which corresponds to the Sensor ID
	        	hash.put("ID",fileEntry.getName().substring(0,extensionPosition));
	        	//read the rest of the file...	
	        	Integer counter = counters.get(hash.get("ID"));
	        	if(counter == null){
	        		counter = 0;
	        	}
	        	else{
	        		counter++;
	        	}
        		counters.put(hash.get("ID"), counter);
        		hash = FileUtils.readSensorFile(hash,fileEntry, counter);
	        	this.sensors.add(hash);
	        }
	    }		
		
		return this.sensors;
	}
	

	public boolean register(String sensorID) {

		mLogger.info("Register called with sensorId : " + sensorID);
		if(this.listener == null){
			mLogger.info("Listener is null");
			return false;
		}
		Reader reader= new Reader(sensorID, this.sensorsFolder, this.listener);
		mLogger.info("new Reader");
		Thread readerThread = new Thread(reader);
		if(listOfThreads.containsKey(sensorID)){
//			mLogger.info("ETSIPluginImpl sensor already registered :" + sensorID);
			listOfThreads.get(sensorID).interrupt();	
//			mLogger.info("ETSIPluginImpl sensor thread killed :" + sensorID);		
		}
//		mLogger.info("before put");
		listOfThreads.put(sensorID, readerThread);
		readerThread.start();
		
		return true;

	}
	
	public String getData(String sensorID) {

		mLogger.info("Get data from sensorId : " + sensorID);
		String output = "";
		
		if (this.sensors.size() > 0){
			final Enumeration<HashMap<String, String>> sensorsEnum = this.sensors.elements();
			while (sensorsEnum.hasMoreElements()){
				HashMap<String, String> sensor = sensorsEnum.nextElement();
				if (sensor.get("ID").equals(sensorID)){
					output = sensor.get("measurement");
					mLogger.info("Data is:"+output);
				}
			}
		}
		
		return output;				

	}

	public boolean unregister(String sensorID) {
		
		if(listOfThreads.containsKey(sensorID)){
//			mLogger.info("ETSIPluginImpl sensor already registered :" + sensorID);
			listOfThreads.get(sensorID).interrupt();	
//			mLogger.info("ETSIPluginImpl sensor thread killed :" + sensorID);	
			return true;
		}
		return false;
	}
	
	public void start() {
		

	}
	
	public void stop() {
		
	}

	public void setSensorsFolder(String sensorsFolder) {
		this.sensorsFolder = sensorsFolder;
	}
	
	
	public String setData(String sensorID, String value) {
		
		mLogger.info("Set data for sensorId : " + sensorID);
		String output = "";
		
		if (this.sensors.size() > 0){
			final Enumeration<HashMap<String, String>> sensorsEnum = this.sensors.elements();
			while (sensorsEnum.hasMoreElements()){
				final HashMap<String, String> sensor = sensorsEnum.nextElement();
				if (sensor.get("ID").equals(sensorID)){
					final File particularSensor = new File(sensorsFolder+sensor.get("ID")+".csv");
					Integer counter = counters.get(sensor.get("ID"));
					FileUtils.writeSensorFile(sensor,particularSensor,value,counter);
					output = sensor.get("Measurement");
					mLogger.info("Data is:"+output);
				}
			}
		}
		
		return output;
	}

}
