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

package eu.betaas.adaptation.thingsadaptor.api.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.osgi.framework.BundleContext;

import eu.betaas.adaptation.contextmanager.api.SemanticParserAdaptator;
import eu.betaas.adaptation.plugin.api.IAdaptorPlugin;
import eu.betaas.adaptation.thingsadaptor.api.ThingsAdaptor;
import eu.betaas.adaptation.thingsadaptor.clients.AdaptorClient;
import eu.betaas.adaptation.thingsadaptor.config.Thing;
import eu.betaas.adaptation.thingsadaptor.config.ThingWithContext;
import eu.betaas.adaptation.thingsadaptor.port.ThingConstructor;
import eu.betaas.adaptation.thingsadaptor.port.ThingsObserver;
import eu.betaas.taas.bigdatamanager.database.service.ThingsData;

public class ThingsAdaptorImpl implements ThingsAdaptor{
	
	private SemanticParserAdaptator adaptationcm;
	private IAdaptorPlugin adaptationPlugin;
	private static BundleContext context;
	Logger mLogger;
	Vector<HashMap<String, String>> discoveredSensors = new Vector<HashMap<String,String>>();
	private String sensorsFolder;
	Thread readerThread;
	
	
	public void startTAModule() {
		
		mLogger = Logger.getLogger("betaas.adaptation");
		mLogger.info("ThingsAdaptor Started..................................");
		AdaptorClient sClient = AdaptorClient.instance(context);
		adaptationPlugin = sClient.getApService();		
		//service = null;
		//DISCOver the available Sensors....
		final ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);
		ThingsObserver observer = new ThingsObserver(adaptationPlugin, adaptationcm, this, sensorsFolder);
		observer.init();
		readerThread = new Thread(observer);
		mLogger.info("FInished with Discovered Sensors:" + discoveredSensors.size());
		if (discoveredSensors.size() > 0){
			mLogger.info("SEARCHED AND FOUND "+discoveredSensors.size()+" DEVICES");
			ThingConstructor thingConstructor = new ThingConstructor(adaptationcm);
			thingConstructor.constructSendThings(discoveredSensors);			
		} else {
			mLogger.info("NO DEVICES FOUND!");
		}
		executor.schedule(readerThread, 1, TimeUnit.MINUTES);
		//readerThread.start();
	}

	public void stopTAModule() {
		readerThread.interrupt();
	}
	

	public Vector<HashMap<String, String>> getThingsLocal() {
		return discoveredSensors;
	}

	public List<Thing> getPhysicalThingsData() {
		List<Thing> result = new ArrayList<Thing>();
		try {
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public ThingWithContext translateThingsInformation(Thing theThing) {
		ThingWithContext result = new ThingWithContext();
		try {
			
			if (theThing.isHasContext()){
			} else {
				result = (ThingWithContext) theThing;	
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public void setAdaptationcm(SemanticParserAdaptator adaptationcm) {
		this.adaptationcm = adaptationcm;
	}
	

	public List<ThingsData> getRealTimeInformation() {
		ArrayList<ThingsData> thingsList = new ArrayList<ThingsData>();
		
		if (discoveredSensors.size() > 0){
			mLogger.info("SEARCHED AND FOUND "+discoveredSensors.size()+" DEVICES");
			ThingConstructor thingConstructor = new ThingConstructor(adaptationcm);
			thingsList = thingConstructor.constructSendThings(discoveredSensors);
		} else {
			mLogger.info("NO DEVICES FOUND!");	
		}
		return thingsList;
	}
	
	public void setContext(BundleContext context) {
		this.context = context;
	}

	public boolean register(String sensorId) {
		return adaptationPlugin.register(sensorId);
	}

	public String getData(String thingId) {
		
		return adaptationPlugin.getData(thingId);
	}
	
	public String setThingValue(String thingId, String value) {
		
		return adaptationPlugin.setData(thingId,value);
	}

	public List<ThingsData> getMeasurement(List<ThingsData> selectedThingsList) {
		for (ThingsData thingsData : selectedThingsList) {
			String measurment = getData(thingsData.getThingId());
			thingsData.setMeasurement(measurment);
		}
		return selectedThingsList;
	}

	public void setMeasurement(List<ThingsData> selectedThingsList) {
		for (ThingsData thingsData : selectedThingsList) {
			setThingValue(thingsData.getThingId(), thingsData.getMeasurement());
		}
	}

	public ThingsData getMeasurement(String thingId) {
		ThingsData result = null;
		ThingConstructor thingConstructor = new ThingConstructor(adaptationcm);
		for (HashMap<String, String> map : discoveredSensors) {
			if(thingId.equals(map.get("ID"))){
				String measurement = adaptationPlugin.getData(thingId);
				map.put("measurement", measurement);
				mLogger.info("getMeasurement will return : " + map);
				result = thingConstructor.constructSendThing(map);
				return result;
			}
		}
		return result;
	}

	public List<ThingsData> getMeasurementThingsMonitoring(
			List<ThingsData> thingsList) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean subscribe(String thingId, int seconds) {
		boolean result = adaptationPlugin.register(thingId);
		return result;
	}

	public boolean unsubscribe(List<String> thingIdsList) {
		for (String thingId : thingIdsList) {
			adaptationPlugin.unregister(thingId);
		}
		return true;
	}

	public boolean subscribe(String thingId) {
		boolean result = adaptationPlugin.register(thingId);
		return result;
	}
	
	public void setSensorsFolder(String sensorsFolder) {
		this.sensorsFolder = sensorsFolder;
	}

	public void setDiscoveredSensors(
			Vector<HashMap<String, String>> discoveredSensors) {
		this.discoveredSensors = discoveredSensors;
	}
	

}
