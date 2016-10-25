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

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Vector;

import org.apache.log4j.Logger;

import eu.betaas.adaptation.contextmanager.api.SemanticParserAdaptator;
import eu.betaas.adaptation.thingsadaptor.clients.TaaSBDMClient;
import eu.betaas.taas.bigdatamanager.database.service.ThingsData;


public class ThingConstructor {

	static String TimeStamp;
	public static LinkedHashMap<String,ThingsData> listOfThings = new LinkedHashMap<String,ThingsData>();
	private volatile ArrayList<ThingsData> thingsList = new ArrayList<ThingsData>();
	Logger mLogger = Logger.getLogger("betaas.adaptation");
	private SemanticParserAdaptator adaptationcm;
	private TaaSBDMClient myBDMClient;

	public ThingConstructor(){
		myBDMClient = TaaSBDMClient.instance();
	}
	
	public ThingConstructor(SemanticParserAdaptator adaptationcm){
		this.adaptationcm = adaptationcm;
		myBDMClient = TaaSBDMClient.instance();
	}

	public ArrayList<ThingsData> constructSendThings(Vector<HashMap<String, String>> discoveredSensors) {

		try {

			final Enumeration<HashMap<String, String>> sensorsEnum = discoveredSensors.elements();
			while (sensorsEnum.hasMoreElements()){		
				
				final HashMap<String, String> sensorHash = sensorsEnum.nextElement();
				
				boolean digital=false;
				boolean output=false;
				boolean environment= false;
				if(sensorHash.get("digital")!=null && sensorHash.get("digital").equals("1")){
					digital = true;
				}
				if(sensorHash.get("output")!=null && sensorHash.get("output").equals("1")){
					output = true;
				}
				if(sensorHash.get("environment")!=null && sensorHash.get("environment").equals("1")){
					environment = true;
				}
				
				mLogger.info("Going to construct Thing with id:"+sensorHash.get("ID"));
				String thingId = sensorHash.get("ID");
				
				ThingsData thing = new ThingsData();
				thing.setDeviceID(sensorHash.get("deviceID"));
				thing.setDigital(digital);
				thing.setOutput(output);
				thing.setProtocol(sensorHash.get("protocol"));
				thing.setLatitude(sensorHash.get("latitude"));
				thing.setLongitude(sensorHash.get("longitude"));
				thing.setAltitude(sensorHash.get("altitude"));
				thing.setFloor(sensorHash.get("floor"));
				thing.setUnit(sensorHash.get("unit"));
				thing.setMeasurement(sensorHash.get("measurement"));		
				thing.setMaximumResponseTime(sensorHash.get("maximumResponseTime"));
				thing.setMemoryStatus(sensorHash.get("memoryStatus"));
				thing.setBatteryLevel(sensorHash.get("batteryLevel"));
				thing.setType(sensorHash.get("type"));
				thing.setEnvironment(environment);
				thing.setLocationKeyword(sensorHash.get("locationKeyword"));
				thing.setLocationIdentifier(sensorHash.get("locationIdentifier"));
				thing.setComputationalCost(sensorHash.get("computationalCost"));
				thing.setBatteryCost(sensorHash.get("batteryCost"));
				
				thing.setThingId(thingId);
				mLogger.info(thing);
				
				listOfThings.put(thing.getThingId(), thing);
				thingsList.add(thing);
			}

			mLogger.info("Going to talk to CM with a list of size:"+thingsList.size());			
			adaptationcm.publishThingInit(thingsList);

		} catch (Exception e) {
			mLogger.error("Exception while constructing Things:"+e.getMessage(),e);
		}
		return thingsList;
	}
	
	public ThingsData  constructSendThing(HashMap<String, String> sensorHash) {
		ThingsData thing = new ThingsData();
		
		try {

			boolean digital=false;
			boolean output=false;
			boolean environment= false;
			if(sensorHash.get("digital")!=null && sensorHash.get("digital").equals("1")){
				digital = true;
			}
			if(sensorHash.get("output")!=null && sensorHash.get("output").equals("1")){
				output = true;
			}
			if(sensorHash.get("environment")!=null && sensorHash.get("environment").equals("1")){
				environment = true;
			}
				mLogger.info("Going to construct Thing with id:"+sensorHash.get("ID"));
				String thingId = sensorHash.get("ID");
				
				
				thing.setDeviceID(sensorHash.get("deviceID"));
				thing.setDigital(digital);
				thing.setOutput(output);
				thing.setProtocol(sensorHash.get("protocol"));
				thing.setLatitude(sensorHash.get("latitude"));
				thing.setLongitude(sensorHash.get("longitude"));
				thing.setAltitude(sensorHash.get("altitude"));
				thing.setFloor(sensorHash.get("floor"));
				thing.setUnit(sensorHash.get("unit"));
				thing.setMeasurement(sensorHash.get("measurement"));		
				thing.setMaximumResponseTime(sensorHash.get("maximumResponseTime"));
				thing.setMemoryStatus(sensorHash.get("memoryStatus"));
				thing.setBatteryLevel(sensorHash.get("batteryLevel"));
				thing.setType(sensorHash.get("type"));
				thing.setEnvironment(environment);
				thing.setLocationKeyword(sensorHash.get("locationKeyword"));
				thing.setLocationIdentifier(sensorHash.get("locationIdentifier"));
				thing.setComputationalCost(sensorHash.get("computationalCost"));
				thing.setBatteryCost(sensorHash.get("batteryCost"));
				
				thing.setThingId(thingId);
				
				mLogger.info(thing);
				listOfThings.put(thing.getThingId(), thing);

		} catch (Exception e) {
			mLogger.error("Exception while constructing Things:"+e.getMessage(),e);
		}
		return thing;
	}
	
	public void notifyMeasurment(final String thingId, final HashMap<String, String> value){
		
		mLogger.info("listOfThings.size : " + listOfThings.size());
		if (listOfThings.size() > 0 && thingId != null && !thingId.equals("")){			
			if (listOfThings.containsKey(thingId)){
			 ThingsData existingThing = listOfThings.get(thingId);
			 existingThing.setMeasurement(value.get("measurement"));
			 if (value.get("latitude") != null) existingThing.setLatitude(value.get("latitude"));
			 if (value.get("longitude") != null) existingThing.setLongitude(value.get("longitude"));
			 if (value.get("altitude") != null) existingThing.setAltitude(value.get("altitude"));
			 if (value.get("batteryLevel") != null) existingThing.setBatteryLevel(value.get("batteryLevel"));
			 if (value.get("memoryStatus") != null) existingThing.setMemoryStatus(value.get("memoryStatus"));
			 
			 // Don't keep more than 500 measurements in memory
			 if (thingsList.size() >= 500)
			 {
				 thingsList.remove(0);				 
			 }			 
			 thingsList.add(existingThing); 
			 
			 ArrayList<ThingsData> tempList = new ArrayList<ThingsData>();
			 tempList.add(existingThing);
			 try {
				adaptationcm.publishThing(tempList);
			 } catch (Exception e) {
				mLogger.error("Exception while sending notification : "+e.getMessage(),e);
			 }
			 
			 // Store the current information in the BDM
			 myBDMClient.saveThingData(existingThing);
			} else {
			  mLogger.info("No Thing ID provided for notyfication");
			}
		}
	}


	public void setAdaptationcm(SemanticParserAdaptator adaptationcm) {
		this.adaptationcm = adaptationcm;
	}

}
