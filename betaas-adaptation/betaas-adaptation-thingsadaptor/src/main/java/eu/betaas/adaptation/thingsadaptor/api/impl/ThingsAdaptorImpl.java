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
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import eu.betaas.adaptation.contextmanager.api.SemanticParserAdaptator;
import eu.betaas.adaptation.plugin.api.IAdaptorPlugin;
import eu.betaas.adaptation.thingsadaptor.api.ThingsAdaptor;
import eu.betaas.adaptation.thingsadaptor.clients.AdaptorClient;
import eu.betaas.adaptation.thingsadaptor.config.Thing;
import eu.betaas.adaptation.thingsadaptor.config.ThingWithContext;
import eu.betaas.adaptation.thingsadaptor.port.ThingConstructor;
import eu.betaas.adaptation.thingsadaptor.port.ThingsObserver;
import eu.betaas.rabbitmq.publisher.interfaces.Publisher;
import eu.betaas.taas.bigdatamanager.database.service.ThingsData;

public class ThingsAdaptorImpl implements ThingsAdaptor{
	
	private SemanticParserAdaptator adaptationcm;
	private List<IAdaptorPlugin> adaptationPlugin;
	private static BundleContext context;
	private String key = "monitoring.adaptation";
	Logger mLogger;
	Vector<HashMap<String, String>> discoveredSensors = new Vector<HashMap<String,String>>();
	private String sensorsFolder;
	private volatile ThingsObserver observer;
	private ScheduledExecutorService executor;
	
	
	public void startTAModule() {
		
		mLogger = Logger.getLogger("betaas.adaptation");
		mLogger.info("ThingsAdaptor Started..................................");
		AdaptorClient sClient = AdaptorClient.instance(context);
		adaptationPlugin = sClient.getApService();
		//Discover the available Sensors....
		executor = Executors.newScheduledThreadPool(1);
		observer = new ThingsObserver(adaptationPlugin, adaptationcm, this, sensorsFolder);
		observer.init();
//		readerThread = new Thread(observer);
		mLogger.info("FInished with Discovered Sensors:" + discoveredSensors.size());
		if (discoveredSensors.size() > 0){
			mLogger.info("SEARCHED AND FOUND "+discoveredSensors.size()+" DEVICES");
			busMessage("Found:"+discoveredSensors.size()+" Devices");
			ThingConstructor thingConstructor = new ThingConstructor(adaptationcm);
			thingConstructor.constructSendThings(discoveredSensors);			
		} else {
			mLogger.info("NO DEVICES FOUND!");
		}

		try {
			executor.schedule(observer, 10, TimeUnit.SECONDS);
		} catch (Exception e) {
			mLogger.error("While executing ThingObserver Thread");
			e.printStackTrace();
		}

	}

	public void stopTAModule() {
		mLogger.info("ThingsAdaptor STOPPED..................................");
		try {
			executor.shutdown();
			observer.stopThread();
		} catch (Exception e) {
			e.printStackTrace();
		}
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
		boolean result = false;
		for (int i = 0; i < adaptationPlugin.size(); i++) {
			result = result || adaptationPlugin.get(i).register(sensorId, 5);
		}
		return result;
	}

	public String getData(String thingId) {
		String result="";
		for (int i = 0; i < adaptationPlugin.size(); i++) {
			result += adaptationPlugin.get(i).getData(thingId);
		}
		return result;
	}
	
	public String setThingValue(String thingId, String value) {
		String result="";
		for (int i = 0; i < adaptationPlugin.size(); i++) {
			result += adaptationPlugin.get(i).setData(thingId,value);
		}
		return result;
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
				String measurement = this.getData(thingId);
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
		return null;
	}

	public boolean subscribe(String thingId, int seconds) {
		boolean result = false;
		for (int i = 0; i < adaptationPlugin.size(); i++) {
			result = result || adaptationPlugin.get(i).register(thingId, seconds);
		}
		return result;
	}

	public boolean unsubscribe(List<String> thingIdsList) {
		boolean result = true;
		for (String thingId : thingIdsList) {
			boolean partialResult = false;
			for (int i = 0; i < adaptationPlugin.size(); i++) {
				mLogger.info("UNSUBSCRIBE Thing:"+thingId);
				busMessage("UNSUBSCRIBE Thing:"+thingId);
				partialResult = partialResult || adaptationPlugin.get(i).unregister(thingId);
			}
			result = result && partialResult;
		}
		return result;
	}
	
	public void setSensorsFolder(String sensorsFolder) {
		this.sensorsFolder = sensorsFolder;
	}

	public void setDiscoveredSensors(
			Vector<HashMap<String, String>> discoveredSensors) {
		mLogger.info("Setting Discovered Sensors in TA!");
		this.discoveredSensors = discoveredSensors;
	}
	
	private void busMessage(String message){
		mLogger.debug("Checking queue");
		
		mLogger.debug("Sending to queue");
		ServiceReference serviceReference = context.getServiceReference(Publisher.class.getName());
		mLogger.debug("Sending to queue");
		if (serviceReference==null){
			return;
		}		
		Publisher service = (Publisher) context.getService(serviceReference); 
		mLogger.debug("Sending");
		service.publish(key,message);
		mLogger.debug("Sent");	
	}

}
