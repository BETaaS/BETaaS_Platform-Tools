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

package eu.betaas.adaptation.thingsadaptor.port;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;

import eu.betaas.adaptation.contextmanager.api.SemanticParserAdaptator;
import eu.betaas.adaptation.plugin.api.IAdaptorPlugin;
import eu.betaas.adaptation.thingsadaptor.api.impl.ThingsAdaptorImpl;
import eu.betaas.adaptation.thingsadaptor.config.ContextUtils;
import eu.betaas.taas.bigdatamanager.database.service.ThingsData;

public class ThingsObserver extends Thread {

	Logger mLogger;
	private List<IAdaptorPlugin> adaptationPlugin;
	private SemanticParserAdaptator adaptationcm;
	List<String> ids = new ArrayList<String>();
	List<String> current = new ArrayList<String>();
	Vector<HashMap<String, String>> discoveredSensors;
	Vector<HashMap<String, String>> things;
	ThingsAdaptorImpl ta;
	String sensorsFolder;

	public ThingsObserver(List<IAdaptorPlugin> adaptationPlugin,
			SemanticParserAdaptator adaptationcm, ThingsAdaptorImpl ta,
			String sensorsFolder) {
		super();
		mLogger = Logger.getLogger("betaas.adaptation");
		this.adaptationPlugin = adaptationPlugin;
		this.adaptationcm = adaptationcm;
		this.ta = ta;
		this.sensorsFolder = sensorsFolder;
	}

	public void init() {
		discoveredSensors = new Vector<HashMap<String, String>>();
		things = new Vector<HashMap<String, String>>();
		ta.setDiscoveredSensors(things);
		ids = new ArrayList<String>();
		current = new ArrayList<String>();
	}
	
	public void stopThread() {
		this.interrupt();
	}


	public synchronized void run() {
		
		try {
			while (true) {
				discoveredSensors = this.discover();
				mLogger.info("Repeat after 5000ms discover and got:"+discoveredSensors.size()+" Things");
				if (null != discoveredSensors && discoveredSensors.size() > 0){
					current = getIds();
					mLogger.info("Current Things:"+current.size());
					this.checkConnected();
					this.checkDisconnected();
					ta.setDiscoveredSensors(things);
					ids = current;	
				}
				Thread.sleep(5000);
			}
		} catch (InterruptedException e) {
			mLogger.error("InterruptedException While Running Discovery!!!"+e.getMessage());			
			e.printStackTrace();
			Thread.currentThread().interrupt();
		}
		catch (RuntimeException rex) {
			mLogger.error("General Exception While Running Discovery!!!"+rex.getMessage());			
			rex.printStackTrace();
			Thread.currentThread().interrupt();
		}
		catch (Throwable ex) {
			mLogger.error("General Exception While Running Discovery!!!"+ex.getMessage());			
			ex.printStackTrace();
			Thread.currentThread().interrupt();
		}
	}

	private void checkDisconnected() {
		List<String> removed = new ArrayList<String>();
		
		try {
			for (String id : ids) {
				if (!current.contains(id)) {
					mLogger.info("DeviceID:" + id + " was removed");
					removed.add(id);
				}
			}
			if (removed.size() > 0) {
				try {
					adaptationcm.removeThing(removed);
				} catch (Exception e) {
					mLogger.error("Error removing thing");
				}
			}
			for (String string : removed) {
				removeIdFromThings(string);
			}
		} catch (Exception e) {
			mLogger.error("WHILE Checking Disconnected Things...");
			e.printStackTrace();
		}
		mLogger.error("Out of checkDisconnected()");
	}

	private void checkConnected(){
		ArrayList<ThingsData> discovered = new ArrayList<ThingsData>();
		ThingConstructor thingConstructor = new ThingConstructor();
		try {
			for (int i = 0; i < discoveredSensors.size(); i++) {
				HashMap<String, String> hashMap = discoveredSensors.get(i);
				String id = hashMap.get("ID");
				if (!ids.contains(id)) {
					mLogger.info("DeviceID:" + id + " was discovered");
					if(containsEmptyFields(hashMap)){
						File file = new File(sensorsFolder + id + ".csv");
						if (file.exists() && !file.isDirectory()) {
							hashMap = ContextUtils.readSensorFile(hashMap, file);
							things.add(hashMap);
							ThingsData thing = thingConstructor.constructSendThing(hashMap);
							discovered.add(thing);
							mLogger.info("FOUND FILE EMPTY AND PRODUCED:"+hashMap);
						}
					}else{
						ThingsData thing = thingConstructor
								.constructSendThing(hashMap);
						things.add(hashMap);
						discovered.add(thing);
						mLogger.info("FOUND FILE AND PRODUCED:"+hashMap);
					}
				}
			}
		} catch (Exception e) {
			mLogger.error("WHILE Checking connected Things...");
			e.printStackTrace();
		}
		if (discovered.size() > 0) {
			try {
				adaptationcm.publishThingInit(discovered);
			} catch (Exception e) {
				mLogger.error("Error publishing thing");
			}
		}
		mLogger.error("Out of checkConnected()");
	}

	private boolean containsEmptyFields(HashMap<String, String> hashMap){
		if(hashMap.size() < 15){
			return true;
		}
		for (Object value : hashMap.values()) {
		    if(value==null){
		    	return true;
		    }
		}
		return false;
	}


	public List<String> getIds() {
		List<String> ids = new ArrayList<String>();
		for (HashMap<String, String> map : discoveredSensors) {
			if (map.get("ID") != null) {
				ids.add(map.get("ID"));
			}
		}
		return ids;
	}
	
	private boolean removeIdFromThings(String id){		
		try {
			mLogger.error("Removing ID:"+id+" from list size:"+things.size());
			for (int i = 0; i < things.size(); i++) {
				HashMap<String, String> map = things.get(i);
				if (map.get("ID") != null && map.get("ID").equals(id)) {
					things.remove(i);
					return true;
				}
			}
			return false;
		} catch (Exception e) {
			mLogger.error("WHILE removing Things...");
			e.printStackTrace();
			return false;
		}
	}
	
	public Vector<HashMap<String, String>> discover() {
		Vector<HashMap<String, String>> sensors = new Vector<HashMap<String, String>>();
		for (int i = 0; i < adaptationPlugin.size(); i++) {
			Vector<HashMap<String, String>> sensorsi = adaptationPlugin.get(i).discover();
			sensors.addAll(sensorsi);
		}
		return sensors;
	}

}
