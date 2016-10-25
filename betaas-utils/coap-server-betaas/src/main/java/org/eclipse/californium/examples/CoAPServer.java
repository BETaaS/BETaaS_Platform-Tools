/*******************************************************************************
 * Copyright (c) 2014 Institute for Pervasive Computing, ETH Zurich and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v1.0 which accompany this distribution.
 * 
 * The Eclipse Public License is available at
 *    http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 *    http://www.eclipse.org/org/documents/edl-v10.html.
 * 
 * Contributors:
 *    Matthias Kovatsch - creator and main architect
 ******************************************************************************/
package org.eclipse.californium.examples;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.examples.resources.*;




public class CoAPServer extends CoapServer {
    
	public static final String DEVICEID = "deviceID";
	public static final String OUTPUT	= "output";
	public static final String DIGITAL	= "digital";
	public static final String MAXRESPONSETIME = "maximumResponseTime";
	public static final String MEMORYSTATUS	= "memoryStatus";
	public static final String BATTERYLEVEL	= "batteryLevel";
	public static final String PROTOCOL	= "protocol";
	public static final String TYPE	= "type";
	public static final String UNIT	= "unit";
	public static final String ENVIRONMENT = "environment";
	public static final String LATITUDE	= "latitude";
	public static final String LONGITUDE = "longitude";
	public static final String ALTITUDE	= "altitude";
	public static final String FLOOR	= "floor";
	public static final String LOCATIONKEYWORD	= "locationKeyword";
	public static final String LOCATIONIDENTIFIER = "locationIdentifier";
	public static final String COMPUTATIONALCOST = "computationalCost";
	public static final String BATTERYCOST = "batteryCost";
	public static final String MEASUREMENT = "measurement";
    /*
     * Application entry point.
     */
    public static void main(String[] args) {
        
        try {
            
            // create server
            CoAPServer server = new CoAPServer(Integer.parseInt(args[0]), args[1]);
            server.start();
            
        } catch (SocketException e) {
            
            System.err.println("Failed to initialize server: " + e.getMessage());
        }
    }
    
    /*
     * Constructor for a new Hello-World server. Here, the resources
     * of the server are initialized.
     */
    public CoAPServer(int port, String configPath ) throws SocketException {
    	
    	super(port);
    	XMLInputFactory factory = XMLInputFactory.newInstance();
	    List<BasicResource> resourcelist = new ArrayList<BasicResource>();
    	try {
	    	FileReader file = new FileReader(configPath);
			XMLStreamReader reader = factory.createXMLStreamReader(file);
			while(reader.hasNext()){
				int event = reader.next();
			    switch(event){
				    case XMLStreamConstants.START_ELEMENT: 
				    	if ("GPSResource".equals(reader.getLocalName())){
				    		BasicResource tr = null;
				    		String deviceId = BasicResource.readDeviceId(reader);
				    		if(deviceId != null)
				    		{
				    			tr = new GPSResource(deviceId);
			    				tr.setId(deviceId);
			    				tr.readXMLAttributes(reader);
					    		tr.setAttributes();
					    		resourcelist.add(tr);
				    		}
				    		
				    	}
				    	if ("StreetLampResource".equals(reader.getLocalName())){
				    		BasicResource tr = null;
				    		String deviceId = BasicResource.readDeviceId(reader);
				    		if(deviceId != null)
				    		{
				    			tr = new StreetLampResource(deviceId);
			    				tr.setId(deviceId);
			    				tr.readXMLAttributes(reader);
					    		tr.setAttributes();
					    		resourcelist.add(tr);
				    		}
				    		
				    	}
				    	if ("TrafficResource".equals(reader.getLocalName())){
				    		BasicResource tr = null;
				    		String deviceId = BasicResource.readDeviceId(reader);
				    		if(deviceId != null)
				    		{
				    			tr = new TrafficResource(deviceId);
			    				tr.setId(deviceId);
			    				tr.readXMLAttributes(reader);
					    		tr.setAttributes();
					    		resourcelist.add(tr);
				    		}

				    	}
				    	if ("PIRResource".equals(reader.getLocalName())){
				    		BasicResource tr = null;
				    		String deviceId = BasicResource.readDeviceId(reader);
				    		if(deviceId != null)
				    		{
				    			tr = new PIRResource(deviceId);
			    				tr.setId(deviceId);
			    				tr.readXMLAttributes(reader);
					    		tr.setAttributes();
					    		resourcelist.add(tr);
				    		}

				    	}
				    	if ("ActuatorResource".equals(reader.getLocalName())){
				    		BasicResource tr = null;
				    		String deviceId = BasicResource.readDeviceId(reader);
				    		if(deviceId != null)
				    		{
				    			tr = new ActuatorResource(deviceId);
			    				tr.setId(deviceId);
			    				tr.readXMLAttributes(reader);
					    		tr.setAttributes();
					    		resourcelist.add(tr);
				    		}

				    	}
				    	if ("HumidityResource".equals(reader.getLocalName())){
				    		BasicResource tr = null;
				    		String deviceId = BasicResource.readDeviceId(reader);
				    		if(deviceId != null)
				    		{
				    			tr = new HumidityResource(deviceId);
			    				tr.setId(deviceId);
			    				tr.readXMLAttributes(reader);
					    		tr.setAttributes();
					    		resourcelist.add(tr);
				    		}

				    	}
				    	if ("TemperatureResource".equals(reader.getLocalName())){
				    		BasicResource tr = null;
				    		String deviceId = BasicResource.readDeviceId(reader);
				    		if(deviceId != null)
				    		{
				    			tr = new TemperatureResource(deviceId);
			    				tr.setId(deviceId);
			    				tr.readXMLAttributes(reader);
					    		tr.setAttributes();
					    		resourcelist.add(tr);
				    		}

				    	}
				    	if ("WaterSimulatedResource".equals(reader.getLocalName())){
				    		BasicResource tr = null;
				    		String deviceId = BasicResource.readDeviceId(reader);
				    		if(deviceId != null)
				    		{
				    			tr = new WaterSimulatedResource(deviceId);
			    				tr.setId(deviceId);
			    				tr.readXMLAttributes(reader);
					    		tr.setAttributes();
					    		resourcelist.add(tr);
				    		}

				    	}
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
    	for(BasicResource tr : resourcelist)
    	{
    		add(tr);
    	}
    }
}
