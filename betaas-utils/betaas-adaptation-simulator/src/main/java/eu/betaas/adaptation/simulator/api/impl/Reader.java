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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

import org.apache.log4j.Logger;

import eu.betaas.adaptation.plugin.api.IAdaptorListener;

public class Reader implements Runnable {

	IAdaptorListener listener;
	String thingId;
	String sensorsFolder;
	Logger mLogger = Logger.getLogger("betaas.thingsadaptor");
	int counter = 0;
	
	public Reader(final String thingId, final String sensorsFolder, IAdaptorListener listener) {
		super();
		
		this.thingId = thingId;
		this.listener = listener;
		this.sensorsFolder = sensorsFolder;
	}


	public void run() {
		try {
			mLogger.info("Sensor Folder is : " + this.sensorsFolder);
			int counter =0;
			
			while (true) {
				InputStream file = new FileInputStream(new File(this.sensorsFolder+ thingId + ".csv"));
				BufferedReader in = new BufferedReader(new InputStreamReader(file));
				String headers = "";
				String data = "";
				String line = null;
				if((line = in.readLine()) != null){
					headers = line;
				}
				int temp = 0;
				while ((line = in.readLine()) != null && temp <= counter) {
					data = line;
					temp++;
				}
				in.close();
				
				final String[] tempRead = data.split(",", -1);
				final String[] header = headers.split(",");
				HashMap<String, String> sensorHash = new HashMap<String, String>();
				if (tempRead.length > 1) {
					for (int i = 0; i < header.length; i++) {
						sensorHash.put(header[i], tempRead[i]);
					}
				}
				String type = "";
				if(sensorHash.get("type")!=null){
					type = sensorHash.get("type");
				}
				listener.notify(type, thingId, sensorHash);	
				counter++;
				Thread.sleep(10000);
			}			
			
		} catch (InterruptedException e) {
			mLogger.info("Thread interrupted");
			listener.removeThing(thingId);
			
		}
		catch (IOException e) {
			listener.removeThing(thingId);
		}

	}


	public void stop() {    		
		
	}

}
