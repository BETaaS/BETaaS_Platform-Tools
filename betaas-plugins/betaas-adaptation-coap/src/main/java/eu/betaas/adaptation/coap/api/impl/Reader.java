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
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;

import eu.betaas.adaptation.coap.utils.Resource;
import eu.betaas.adaptation.coap.utils.Server;
import eu.betaas.adaptation.plugin.api.IAdaptorListener;

public class Reader implements Runnable {

	IAdaptorListener listener;
	Resource res;
	Logger mLogger = Logger.getLogger("betaas.thingsadaptor");


	public Reader(Resource r, IAdaptorListener listener) {
		super();
		this.listener = listener;
		this.res = r;
	}


	public void run() {
		try {
			mLogger.debug("Sensor is : " + res.getDeviceID() + " hosted on: " + res.getServer().toString());
			while (true) {
				HashMap<String, String> hash = Server.getParam(res);
				String type = "";
				if(hash.get(Server.TYPE)!=null){
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

		    	
		    	CoapClient client = new CoapClient(uri);

				CoapResponse response = client.get();
				if (response!=null) {
					hash.put(Server.MEASUREMENT, response.getResponseText());
				} else {
					mLogger.error("No response received.");
				}
				listener.notify(type, res.getDeviceID(), hash);	
				Thread.sleep(10000);
			}			
			
		} catch (InterruptedException e) {
			mLogger.debug("Thread interrupted");
			listener.removeThing(res.getDeviceID());
			
		}
		

	}


	public void stop() {    		
		
	}

}
