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
package eu.betaas.adaptation.inter.api.impl;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

import eu.betaas.adaptation.inter.db.ServiceDB;
import eu.betaas.adaptation.plugin.api.IAdaptorListener;
import eu.betaas.adaptation.plugin.api.IAdaptorPlugin;
import eu.betaas.taas.bigdatamanager.database.hibernate.data.SimulatedThing;

public class AdaptorPluginImpl implements IAdaptorPlugin {

	private IAdaptorListener listener;
	private LinkedHashMap<String,Thread> listOfThreads = new LinkedHashMap<String,Thread>();
	protected Vector<HashMap<String, String>> sensors = new Vector<HashMap<String, String>>();
	private int minutes;
	private ServiceDB service;
//	private LinkedHashMap<String, Integer> counters = new LinkedHashMap<String,Integer>();
	Logger mLogger = Logger.getLogger("betaas.adaptation");
	
	/**
	 * Set the Listener for the automatic notifications subscriptions
	 */
	public void setListener(IAdaptorListener listener) {
		this.listener = listener;
	}

	public void setService(ServiceDB service) {
		this.service = service;
	}

	public Vector<HashMap<String, String>> discover() {
		sensors.clear();
		mLogger.info("Discover called from REST Simulator...");
		//Discover any things saved in the DB...
		List<String> savedThings = service.listAllThingsObjects();
		if (savedThings != null && savedThings.size() > 0){	
			//Iterate through the List of JSON Things
			for (Iterator<String> iter=savedThings.iterator(); iter.hasNext();){
				String jsonThing = iter.next();
				try {
					HashMap<String,Object> thingHash =new ObjectMapper().readValue(jsonThing, HashMap.class);
					//Iterate through EACH JSON object's fields...
					HashMap<String, String> hash = new HashMap<String, String>();
					for (Iterator iterator = thingHash.keySet().iterator(); iterator.hasNext();) {					
						try {							
							final String key = (String) iterator.next();
							if (key.equals("id")){
								hash.put(key.toUpperCase(), String.valueOf(thingHash.get(key)));
							} else {
								hash.put(key, String.valueOf(thingHash.get(key)));	
							}							
						} catch (Exception e) {
							mLogger.error("While Parsing Thing JSON..."+e.getMessage());
							e.printStackTrace();
						}
					}
					this.sensors.add(hash);
				} catch (Exception ex) {
					
					ex.printStackTrace();
				}				
			}
		}
		mLogger.info("Returning to TA:"+this.sensors.size()+" of Things from DB");
		return this.sensors;
	}
	
	/**
	 * Register for automatic notifications every
	 * @param sensorID , The Thing ID you want to subscribe to 
	 * @param seconds , you want to recieve notifications.
	 */
	public boolean register(String sensorID, int seconds) {

		mLogger.info("Register called with sensorId : " + sensorID);
		if (this.listener == null) {
			mLogger.info("Listener is null");
			return false;
		}
		// Search for Thing in the sensors hash and then start the Reader that searches in the DB...
		if (this.sensors.size() > 0){
			final Enumeration<HashMap<String, String>> sensorsEnum = this.sensors.elements();
			while (sensorsEnum.hasMoreElements()){
				HashMap<String, String> sensor = sensorsEnum.nextElement();
				if (sensor.get("ID").equals(sensorID)){
					Reader reader = new Reader(sensorID, this.listener, this.minutes, this.service, sensor);
					Thread readerThread = new Thread(reader);
					if (listOfThreads.containsKey(sensorID)) {
						listOfThreads.get(sensorID).interrupt();
					}
					listOfThreads.put(sensorID, readerThread);
					readerThread.start();
				}
			}
		}		

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
					SimulatedThing sim = this.service.getThing(Integer.valueOf(sensorID));
					mLogger.info("Data is:"+sim.getMeasurement());
				}
			}
		}
		return output;
	}

	public boolean unregister(String sensorID) {
		
		if(listOfThreads.containsKey(sensorID)){
			listOfThreads.get(sensorID).interrupt();	
			return true;
		}
		return false;
	}
	
	public void start() {

	}
	
	public void stop() {
		
	}
	

	public String setData(String sensorID, String value) {
		
		mLogger.info("Set data for sensorId : " + sensorID);
		String output = "";
		//Connect to DB and set the output...
		if (this.sensors.size() > 0){
			final Enumeration<HashMap<String, String>> sensorsEnum = this.sensors.elements();
			while (sensorsEnum.hasMoreElements()){
				HashMap<String, String> sensor = sensorsEnum.nextElement();
				if (sensor.get("ID").equals(sensorID)){
					SimulatedThing sim = this.service.getThing(Integer.valueOf(sensorID));
					sim.setMeasurement(value);
					this.service.saveThing(sim);
					mLogger.info("New value for Thing:"+sensorID+" is:"+sim.getMeasurement());
				}
			}
		}
		return output;
	}

}
