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

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Vector;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.log4j.Logger;
import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.coap.Request;

import eu.betaas.adaptation.coap.utils.Resource;
import eu.betaas.adaptation.coap.utils.Server;
import eu.betaas.adaptation.plugin.api.IAdaptorListener;
import eu.betaas.adaptation.plugin.api.IAdaptorPlugin;

public class CoAPAdaptorPluginImpl implements IAdaptorPlugin {
	
	private static final int NUMBER_OF_ATTEMP = 2;
	private IAdaptorListener listener;
	private LinkedHashMap<String,Thread> listOfThreads = new LinkedHashMap<String,Thread>();
	protected Vector<HashMap<String, String>> sensors = new Vector<HashMap<String, String>>();
	private String serversConfig;
	Logger mLogger = Logger.getLogger("betaas.thingsadaptor");
	List<Server> serverList = null;
	
	public void setListener(IAdaptorListener listener) {
		this.listener = listener;
	}

	public synchronized Vector<HashMap<String, String>> discover() {
		mLogger.info("CoAPAdaptorPluginImpl - discover");
		Vector<HashMap<String, String>> sensorsList = discoverRemote();
		for(HashMap<String, String> sensorMap : sensors){
			if(!contains(sensorMap.get(Server.DEVICEID), sensorsList))
				listener.removeThing(sensorMap.get(Server.DEVICEID));
		}
		sensors = sensorsList;
		return sensors;
	}

	private boolean contains(String deviceId,
			Vector<HashMap<String, String>> sensorsList) {
		for(HashMap<String, String> sensor : sensorsList){
			if(sensor.get(Server.DEVICEID).equals(deviceId)) return true;
		}
		return false;
	}

	private Vector<HashMap<String, String>> discoverRemote() {
		XMLInputFactory factory = XMLInputFactory.newInstance();
		
		String context = null;
		
		Server currServer = null;
		Vector<HashMap<String, String>> sensorsList = new Vector<HashMap<String, String>>();
	    try {
	    	FileReader file = new FileReader(serversConfig);
			XMLStreamReader reader = factory.createXMLStreamReader(file);
			String tagContent = null;
			String cdataContent = null;
			while(reader.hasNext()){
				int event = reader.next();
			    switch(event){
				    case XMLStreamConstants.START_ELEMENT: 
				    	if ("server".equals(reader.getLocalName())){
				    		currServer = new Server();
				    	}
				    	if ("servers".equals(reader.getLocalName())){
				    		serverList = new ArrayList<Server>();
				    	}
				        break;

			        case XMLStreamConstants.CHARACTERS:
			        	// TODO check empty characters in output
			        	tagContent = reader.getText().trim();
			        	mLogger.debug("Content: " + tagContent);
			        	break;

			        case XMLStreamConstants.CDATA:
			        	// WARN we assume that the tag <server> has only one CDATA field 
			        	cdataContent = reader.getText().trim();
			        	mLogger.debug("Context: " + cdataContent);
			        	currServer.setContext(cdataContent);
			        	break; 
			        	
			        case XMLStreamConstants.END_ELEMENT:
			        	if(reader.getLocalName().equals("server"))
			        		serverList.add(currServer);
			        	if(reader.getLocalName().equals("name"))
			        		currServer.setName(tagContent);
			        	if(reader.getLocalName().equals("ip"))
			        		currServer.setIp(tagContent);
			            if(reader.getLocalName().equals("port"))
			            	currServer.setPort(tagContent);
			            break;

			        case XMLStreamConstants.START_DOCUMENT:
			          serverList = new ArrayList<Server>();
			          break;
			      
			    }
			}
		} catch (XMLStreamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	    mLogger.debug("Servers ");
	    for (Server s : serverList){
	    	URI uri = null; // URI parameter of the request
	    	try {
	    		mLogger.debug("URI: coap://"+ s.getIp() + ":" + s.getPort() + "/.well-known/core");
				uri = new URI("coap://" + s.getIp() + ":" + s.getPort() + "/.well-known/core");
				
			} catch (URISyntaxException e) {
				mLogger.error("Exception on URI: " + e.getMessage());
				continue;
			} 
	    	CoapResponse response=null;
	    	int attempt = 0;
			while(response==null && attempt <= NUMBER_OF_ATTEMP)
	    	{
				attempt++;
		    	CoapClient client = new CoapClient(uri);
	
				response = client.get();
				
				if (response!=null || s.getContext() != null) {
					
					// Some very constrained devices implementing CoAP can not provide context directly using CoRE Link format during discovery
					// For those, the context can be specified directly in the XML file in the tag <context>, hence the payload in the response is ignored
					// For the others we parse the description in the response
					if( s.getContext() == null ){
						context = response.getResponseText();
						s.setContext(context);
					} else {
						context = s.getContext();
					}
					
					mLogger.debug("Context retrieved: " + context);
									
					List<Resource> ResourceList = s.parseCoreLinkFormat(context);
					s.setResources(ResourceList);
					
					
					List<HashMap<String, String>> params = s.getParameters();
					mLogger.debug("PARAMS: "+ params);
					for(HashMap<String, String> hash : params){
						sensorsList.add(hash);
					}
					
				} else {
					mLogger.error("No response received from " + s.getIp() + ":" + s.getPort());
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
	    	}
			if(attempt > NUMBER_OF_ATTEMP){
				mLogger.error("Give up on " + s.getIp() + ":" + s.getPort());
			}
			
	    }
		return sensorsList;
	}

	public boolean register(String sensorID, int seconds) {
		mLogger.info("CoAPAdaptorPluginImpl - Register called with sensorId : " + sensorID + " seconds: " + seconds);
		boolean found = false;
		Resource r= null;
		if(this.listener == null){
			mLogger.error("Listener is null");
			return false;
		}
		for (Server s : serverList){
			List<Resource> resourceList = s.getResources();
			for(Resource res : resourceList){
				if(res.getDeviceID().equals(sensorID))
				{
					r = res;
					found = true;
					break;
				}
			}
			if(found)
				break;
		}
		if(!found){
			mLogger.error(sensorID + " not found");
			return false;
		}
		Reader reader= new Reader(r, this.listener, seconds);
		mLogger.debug("new Reader");
		Thread readerThread = new Thread(reader);
		if(listOfThreads.containsKey(sensorID)){
			listOfThreads.get(sensorID).interrupt();	
		}
		listOfThreads.put(sensorID, readerThread);
		readerThread.start();
		return true;

	}
	
	public String getData(String sensorID) {
		mLogger.info("CoAPAdaptorPluginImpl - Get data from sensorId : " + sensorID);
		boolean found = false;
		Resource r= null;
		String output = "";
		for (Server s : serverList){
			List<Resource> resourceList = s.getResources();
			for(Resource res : resourceList){
				if(res.getDeviceID().equals(sensorID))
				{
					r = res;
					found = true;
					break;
				}
			}
			if(found)
				break;
		}
		if(!found){
			mLogger.error( sensorID + " not found");
			return output;
		}
		
		Server s = r.getServer();
		URI uri = null; // URI parameter of the request
    	try {
    		mLogger.debug("URI: coap://"+ s.getIp() + ":" + s.getPort() + r.getPath());
			uri = new URI("coap://" + s.getIp() + ":" + s.getPort() + r.getPath());
			
		} catch (URISyntaxException e) {
			mLogger.error("Exception on URI: " + e.getMessage());
			return output;
		} 
    	CoapResponse response = null;
    	int attempt=0;
		while(response == null && attempt <= NUMBER_OF_ATTEMP)
		{
			attempt++;
	    	CoapClient client = new CoapClient(uri);
	
			response = client.get();
			if (response!=null) {
				output = response.getResponseText();
				mLogger.debug("Data is:"+output);
			} else {
				mLogger.error("No response received from "+ s.getIp() + ":" + s.getPort());
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		if(attempt > NUMBER_OF_ATTEMP){
			mLogger.error("Give up on " + s.getIp() + ":" + s.getPort());
			listener.removeThing(sensorID);
		}
		return output;				

	}

	public boolean unregister(String sensorID) {
		mLogger.info("CoAPAdaptorPluginImpl - Unregister called with sensorId : " + sensorID);
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
		mLogger.info("CoAPAdaptorPluginImpl - Set data for sensorId : " + sensorID);
		String output = "";
		boolean found = false;
		Resource r= null;
		for (Server s : serverList){
			List<Resource> resourceList = s.getResources();
			for(Resource res : resourceList){
				if(res.getDeviceID().equals(sensorID))
				{
					r = res;
					found = true;
					break;
				}
			}
			if(found)
				break;
		}
		if(!found){
			mLogger.error("deviceId not found");
			return output;
		}
		
		Server s = r.getServer();
		URI uri = null; // URI parameter of the request
    	try {
    		mLogger.debug("URI: coap://"+ s.getIp() + ":" + s.getPort() + r.getPath());
			uri = new URI("coap://" + s.getIp() + ":" + s.getPort() + r.getPath());
			
		} catch (URISyntaxException e) {
			mLogger.error("Exception on URI: " + e.getMessage());
			return output;
		} 
    	CoapClient client = new CoapClient(uri);
    	
    	// WARN we assume that the sensor retrieve the data in the format value=XXX
    	String outValue = new String("value="+value);
    	mLogger.debug("POST payload: "+ outValue);
    	
		CoapResponse response = client.post(outValue, MediaTypeRegistry.TEXT_PLAIN);
		if (response!=null) {
			output = response.getResponseText();
			mLogger.debug("Data is:"+output);
		} else {
			mLogger.error("No response received.");
		}
		return output;
	}

	public void setServersConfig(String serversConfig) {
		this.serversConfig = serversConfig;
	}

}
