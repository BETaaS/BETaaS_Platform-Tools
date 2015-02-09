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

package eu.betaas.adaptation.coap.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Server {
	private String name;
	private String ip;
	private String port;
	private String context;
	private List<Resource> resources;
	
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
	public static final String LOCATIONIDENTIFIER = "LocationIdentifier";
	public static final String COMPUTATIONALCOST = "ComputationalCost";
	public static final String BATTERYCOST = "BatteryCost";
	public static final String MEASUREMENT = "measurement";
	
	
	// for parsing
	public static final Pattern DELIMITER      = Pattern.compile("\\s*,+\\s*");
	public static final Pattern SEPARATOR      = Pattern.compile("\\s*;+\\s*");
	public static final Pattern WORD           = Pattern.compile("\\w+");
	public static final Pattern QUOTED_STRING  = Pattern.compile("\\G\".*?\"");
	public static final Pattern CARDINAL       = Pattern.compile("\\G\\d+");
	
	public Server(){
		resources = new ArrayList<Resource>();
		context = null;
	}
	
	@Override
	public String toString() {
		return getName()+" - "+getIp()+":"+getPort();
	}

	public String getContext() {
		return context;
	}

	public void setContext(String ctx) {
		context = ctx;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}
	
	public List<Resource> parseCoreLinkFormat(String linkFormat) {
		List<Resource> list = new  ArrayList<Resource>();
		Pattern DELIMITER = Pattern.compile("\\s*,+\\s*");
		if (linkFormat!=null) {
			Scanner scanner = new Scanner(linkFormat);
			String path = null;
			while ((path = scanner.findInLine("</[^>]*>")) != null) {
				// Trim <...>
				path = path.substring(1, path.length() - 1);
				Resource res = new Resource();
				res.setPath(path);
				// Read link format attributes
				String attr = null;
				while (scanner.findWithinHorizon(DELIMITER, 1)==null && (attr = scanner.findInLine(WORD))!=null) {
					if (scanner.findWithinHorizon("=", 1) != null) {
						String value = null;
						if ((value = scanner.findInLine(QUOTED_STRING)) != null)
							value = value.substring(1, value.length()-1); // trim " "
						else
							value = scanner.findInLine(WORD);
						if(attr.equals(DEVICEID))
							res.setDeviceID(value);
						if(attr.equals(OUTPUT))
							res.setOutput(Integer.parseInt(value));
						if(attr.equals(DIGITAL))
							res.setDigital(Integer.parseInt(value));
						if(attr.equals(MAXRESPONSETIME))
							res.setMaximumResponseTime(Integer.parseInt(value));
						if(attr.equals(MEMORYSTATUS))
							res.setMemoryStatus(Double.parseDouble(value));
						if(attr.equals(BATTERYLEVEL))
							res.setBatteryLevel(Double.parseDouble(value));
						if(attr.equals(PROTOCOL))
							res.setProtocol(value);
						if(attr.equals(TYPE))
							res.setType(value);
						if(attr.equals(UNIT))
							res.setUnit(value);
						if(attr.equals(ENVIRONMENT))
							res.setEnvironment(Integer.parseInt(value));
						if(attr.equals(LATITUDE))
							res.setLatitude(Double.parseDouble(value));
						if(attr.equals(LONGITUDE))
							res.setLongitude(Double.parseDouble(value));
						if(attr.equals(ALTITUDE))
							res.setAltitude(Double.parseDouble(value));
						if(attr.equals(FLOOR))
							res.setFloor(Integer.parseInt(value));
						if(attr.equals(LOCATIONKEYWORD))
							res.setLocationKeyword(value);
						if(attr.equals(LOCATIONIDENTIFIER))
							res.setLocationIdentifier(value);
						if(attr.equals(COMPUTATIONALCOST))
							res.setComputationalCost(Double.parseDouble(value));
						if(attr.equals(BATTERYCOST))
							res.setBatteryCost(Double.parseDouble(value));
						if(attr.equals(MEASUREMENT))
							res.setMeasurement(value);
					} else {
						// flag attribute without value
						res.getAttributes().addAttribute(attr);
					}
					
				}
				if(res.getDeviceID()!=null){
					res.setServer(this);
					list.add(res);
				}
			}
			scanner.close();
		}
		return list;
	}

	public List<Resource> getResources() {
		return resources;
	}

	public void setResources(List<Resource> resources) {
		this.resources = resources;
	}

	public List<HashMap<String, String>> getParameters() {
		List<HashMap<String, String>> params = new ArrayList<HashMap<String,String>>();
		for(Resource res : getResources()){
			params.add(getParam(res));
		}
		return params;
	}
	
	public static HashMap<String, String> getParam(Resource res){
		final HashMap<String, String> hash = new HashMap<String, String>();
		hash.put("ID",res.getDeviceID());
		hash.put(DEVICEID, res.getDeviceID());
		if(res.getOutput() != null)
			hash.put(OUTPUT, res.getOutput().toString());
		else
			hash.put(OUTPUT, null);
		if(res.getDigital() != null)
			hash.put(DIGITAL, res.getDigital().toString());
		else
			hash.put(DIGITAL, null);
		if(res.getMaximumResponseTime() != null)
			hash.put(MAXRESPONSETIME, res.getMaximumResponseTime().toString());
		else
			hash.put(MAXRESPONSETIME, null);
		if(res.getMemoryStatus() != null)
			hash.put(MEMORYSTATUS, res.getMemoryStatus().toString());
		else
			hash.put(MEMORYSTATUS, null);
		if(res.getBatteryLevel() != null)
			hash.put(BATTERYLEVEL, res.getBatteryLevel().toString());
		else
			hash.put(BATTERYLEVEL, null);
		hash.put(PROTOCOL, res.getProtocol());
		hash.put(TYPE, res.getType());
		hash.put(UNIT, res.getUnit());
		if(res.getEnvironment() != null)
			hash.put(ENVIRONMENT, res.getEnvironment().toString());
		else
			hash.put(ENVIRONMENT, null);
		if(res.getLatitude() != null)
			hash.put(LATITUDE, res.getLatitude().toString());
		else
			hash.put(LATITUDE, null);
		if(res.getLongitude() != null)
			hash.put(LONGITUDE, res.getLongitude().toString());
		else
			hash.put(LONGITUDE, null);
		if(res.getAltitude() != null)
			hash.put(ALTITUDE, res.getAltitude().toString());
		else
			hash.put(ALTITUDE, null);
		if(res.getFloor() != null)
			hash.put(FLOOR, res.getFloor().toString());
		else
			hash.put(FLOOR, null);
		hash.put(LOCATIONKEYWORD, res.getLocationKeyword());
		hash.put(LOCATIONIDENTIFIER, res.getLocationIdentifier());
		if(res.getComputationalCost() != null)
			hash.put(COMPUTATIONALCOST, res.getComputationalCost().toString());
		else
			hash.put(COMPUTATIONALCOST, null);
		if(res.getBatteryCost() != null)
			hash.put(BATTERYCOST, res.getBatteryCost().toString());
		else
			hash.put(BATTERYCOST, null);
		if(res.getMeasurement() != null)
			hash.put(MEASUREMENT, res.getMeasurement().toString());
		else
			hash.put(MEASUREMENT, null);
		
		return hash;
	}
}
