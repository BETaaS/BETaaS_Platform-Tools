/*
 *
Copyright 2014-2015 Department of Information Engineering, University of Pisa

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

// BETaaS - Building the Environment for the Things as a Service
//
// Component: CoAP Adaptation Plugin
// Responsible: Carlo Vallati & Giacomo Tanganelli

package eu.betaas.adaptation.coap.api.impl;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;

import eu.betaas.adaptation.coap.utils.Resource;
import eu.betaas.adaptation.coap.utils.Server;
import eu.betaas.adaptation.plugin.api.IAdaptorListener;

public class Reader implements Runnable {

	private static final int NUMBER_OF_ATTEMP = 2;
	IAdaptorListener listener;
	Resource res;
	int milliseconds;
	Logger mLogger = Logger.getLogger("betaas.thingsadaptor");



	public Reader(Resource r, IAdaptorListener listener, int seconds) {
		super();
		this.listener = listener;
		this.res = r;
		this.milliseconds = seconds * 1000;
		mLogger.info("Milliseconds:" + this.milliseconds);
		if(this.milliseconds <= 0)
			this.milliseconds = 5000;
	}


	public void run() {
		try {
			mLogger.debug("Sensor is : " + res.getDeviceID() + " hosted on: " + res.getServer().toString());
			while (true) {
				HashMap<String, String> hash = Server.getParam(res);
				String type = "";
				if(hash.containsKey(Server.TYPE) && hash.get(Server.TYPE) != null){
					type = hash.get(Server.TYPE);
				}
				Server s = res.getServer();
				URI uri = null; // URI parameter of the request
		    	try {
		    		mLogger.debug("URI: coap://"+ s.getIp() + ":" + s.getPort() + res.getPath());
					uri = new URI("coap://" + s.getIp() + ":" + s.getPort() + res.getPath());
					
				} catch (URISyntaxException e) {
					mLogger.error("Exception on URI: " + e.getMessage());
					throw new InterruptedException();
				} 
		    	CoapResponse response = null;
		    	int attempt = 0;
		    	while(response == null && attempt <= NUMBER_OF_ATTEMP)
		    	{
		    		attempt++;
			    	try{
			    		CoapClient client = new CoapClient(uri);
			    		response = client.get();
			    	}catch (Exception e)
			    	{
			    	}
			    	
					if (response!=null) {
						hash.put(Server.MEASUREMENT, response.getResponseText());
					} else {
						mLogger.error("No response received by " + res.getDeviceID());
						Thread.sleep(1000);
					}
		    	}
		    	if(response == null)
		    		throw new InterruptedException();
		    	
				listener.notify(type, res.getDeviceID(), hash);	
				mLogger.info(res.getDeviceID() +" Sleep:" + this.milliseconds);
				Thread.sleep(milliseconds);
				mLogger.info(res.getDeviceID() +" EndSleep:" + this.milliseconds);
			}			
			
		} catch (InterruptedException e) {
			mLogger.debug("Thread interrupted - Remove Thing " + res.getDeviceID());
			listener.removeThing(res.getDeviceID());
			
		}
		catch (Exception e)
		{
			mLogger.error("Problem with Running the Reader!!");
			e.printStackTrace();
		}

	}


	public void stop() {    		
		
	}

}
