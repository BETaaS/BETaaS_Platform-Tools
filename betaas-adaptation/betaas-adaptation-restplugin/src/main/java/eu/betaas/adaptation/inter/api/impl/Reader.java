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

import java.util.HashMap;
import java.util.Vector;

import org.apache.log4j.Logger;

import eu.betaas.adaptation.inter.db.ServiceDB;
import eu.betaas.adaptation.plugin.api.IAdaptorListener;
import eu.betaas.taas.bigdatamanager.database.hibernate.data.SimulatedThing;

public class Reader implements Runnable {

	IAdaptorListener listener;
	ServiceDB service;
	String thingId;
	int seconds;
	HashMap<String, String> sensor;
	Logger mLogger = Logger.getLogger("betaas.adaptation");
	
	public Reader(final String thingId, IAdaptorListener listener, int seconds, ServiceDB service, HashMap<String, String> sensor) {
		super();
		this.thingId = thingId;
		this.listener = listener;
		this.seconds = seconds;
		this.service = service;
		this.sensor = sensor;
	}


	public void run() {
		try {
			
			while (true) {				
				//Connect and read every *seconds* interval
				if (thingId != null && Integer.valueOf(thingId) > 0){
					SimulatedThing sim = this.service.getThing(Integer.valueOf(thingId));
					sensor.put("measurement", sim.getMeasurement());					
					listener.notify(sim.getType(), thingId, sensor);
					Thread.sleep(this.seconds*1000);
				}
			}			
			
		} catch (InterruptedException e) {
			mLogger.info("Thread interrupted");
			listener.removeThing(thingId);
			
		}		

	}


	public void stop() {    		
		
	}

}
