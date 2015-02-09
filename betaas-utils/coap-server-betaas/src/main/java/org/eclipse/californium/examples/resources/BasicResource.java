package org.eclipse.californium.examples.resources;

import javax.xml.stream.XMLStreamReader;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.eclipse.californium.examples.CoAPServer;

public abstract class BasicResource extends CoapResource {
   	protected String id;
    protected String output;
    protected String digital;
    protected String maxresponsetime;
    protected String memorystatus;
    protected String batterylevel;
    protected String protocol;
    protected String type;
    protected String unit;
    protected String environment;
    protected String lat;
    protected String lon;
    protected String altitude;
    protected String floor;
    protected String locationkeywork;
    protected String locationIdentifier;
    protected String computationalcost;
    protected String batterycost;
    protected String measurement;
    
    public void setAttributes() {
        getAttributes().setTitle("Basic Resource");
        getAttributes().addAttribute(CoAPServer.DEVICEID, id);
        getAttributes().addAttribute(CoAPServer.OUTPUT, output);
        getAttributes().addAttribute(CoAPServer.DIGITAL, digital);
        getAttributes().addAttribute(CoAPServer.MAXRESPONSETIME, maxresponsetime);
        getAttributes().addAttribute(CoAPServer.MEMORYSTATUS, memorystatus);
        getAttributes().addAttribute(CoAPServer.BATTERYLEVEL, batterylevel);
        getAttributes().addAttribute(CoAPServer.PROTOCOL, protocol);
        getAttributes().addAttribute(CoAPServer.TYPE, type);
        getAttributes().addAttribute(CoAPServer.UNIT, unit);
        getAttributes().addAttribute(CoAPServer.ENVIRONMENT, environment);
        getAttributes().addAttribute(CoAPServer.LATITUDE, lat);
        getAttributes().addAttribute(CoAPServer.LONGITUDE, lon);
        getAttributes().addAttribute(CoAPServer.ALTITUDE, altitude);
        getAttributes().addAttribute(CoAPServer.FLOOR, floor);
        getAttributes().addAttribute(CoAPServer.LOCATIONKEYWORD, locationkeywork);
        getAttributes().addAttribute(CoAPServer.LOCATIONIDENTIFIER, locationIdentifier);
        getAttributes().addAttribute(CoAPServer.COMPUTATIONALCOST, computationalcost);
        getAttributes().addAttribute(CoAPServer.BATTERYCOST, batterycost);
        getAttributes().addAttribute(CoAPServer.MEASUREMENT, measurement);
    }
    public BasicResource(String name) {
		super(name);
		setOutput("");
		setDigital("");
		setMaxresponsetime("");
		setMemorystatus("");
		setBatterylevel("");
		setProtocol("coap");
		setType("");
		setUnit("");
		setEnvironment("");
		setLat("");
		setLon("");
		setAltitude("");
		setFloor("");
		setLocationkeywork("");
		setLocationIdentifier("");
		setComputationalcost("");
		setBatterycost("");
		setMeasurement("");
	}
    
    public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getLocationIdentifier() {
		return locationIdentifier;
	}

	public void setLocationIdentifier(String locationIdentifier) {
		this.locationIdentifier = locationIdentifier;
	}

	public String getLat() {
		return lat;
	}

	public void setLat(String lat) {
		this.lat = lat;
	}

	public String getLon() {
		return lon;
	}

	public void setLon(String lon) {
		this.lon = lon;
	}

	public String getOutput() {
		return output;
	}

	public void setOutput(String output) {
		this.output = output;
	}

	public String getDigital() {
		return digital;
	}

	public void setDigital(String digital) {
		this.digital = digital;
	}

	public String getMaxresponsetime() {
		return maxresponsetime;
	}

	public void setMaxresponsetime(String maxresponsetime) {
		this.maxresponsetime = maxresponsetime;
	}

	public String getMemorystatus() {
		return memorystatus;
	}

	public void setMemorystatus(String memorystatus) {
		this.memorystatus = memorystatus;
	}

	public String getBatterylevel() {
		return batterylevel;
	}

	public void setBatterylevel(String batterylevel) {
		this.batterylevel = batterylevel;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getEnvironment() {
		return environment;
	}

	public void setEnvironment(String environment) {
		this.environment = environment;
	}

	public String getAltitude() {
		return altitude;
	}

	public void setAltitude(String altitude) {
		this.altitude = altitude;
	}

	public String getFloor() {
		return floor;
	}

	public void setFloor(String floor) {
		this.floor = floor;
	}

	public String getLocationkeywork() {
		return locationkeywork;
	}

	public void setLocationkeywork(String locationkeywork) {
		this.locationkeywork = locationkeywork;
	}

	public String getComputationalcost() {
		return computationalcost;
	}

	public void setComputationalcost(String computationalcost) {
		this.computationalcost = computationalcost;
	}

	public String getBatterycost() {
		return batterycost;
	}

	public void setBatterycost(String batterycost) {
		this.batterycost = batterycost;
	}

	public String getMeasurement() {
		return measurement;
	}

	public void setMeasurement(String measurement) {
		System.out.print("Meas set "+measurement+"\n");
		this.measurement = measurement;
	}
	
	public void readXMLAttributes(XMLStreamReader reader){
		System.out.print("XML \n");
		for(int i = 0 ; i < reader.getAttributeCount(); i++){
			String attName = reader.getAttributeLocalName(i);
			System.out.print(attName+"\n");
			if(attName.equals(CoAPServer.OUTPUT))
				this.setOutput(reader.getAttributeValue(i));
			if(attName.equals(CoAPServer.DIGITAL))
				this.setDigital(reader.getAttributeValue(i));
			if(attName.equals(CoAPServer.MAXRESPONSETIME))
				this.setMaxresponsetime(reader.getAttributeValue(i));
			if(attName.equals(CoAPServer.MEMORYSTATUS))
				this.setMemorystatus(reader.getAttributeValue(i));
			if(attName.equals(CoAPServer.BATTERYLEVEL))
				this.setBatterylevel(reader.getAttributeValue(i));
			if(attName.equals(CoAPServer.PROTOCOL))
				this.setProtocol(reader.getAttributeValue(i));
			if(attName.equals(CoAPServer.TYPE))
				this.setType(reader.getAttributeValue(i));
			if(attName.equals(CoAPServer.UNIT))
				this.setUnit(reader.getAttributeValue(i));
			if(attName.equals(CoAPServer.ENVIRONMENT))
				this.setEnvironment(reader.getAttributeValue(i));
			if(attName.equals(CoAPServer.LATITUDE))
				this.setLat(reader.getAttributeValue(i));
			if(attName.equals(CoAPServer.LONGITUDE))
				this.setLon(reader.getAttributeValue(i));
			if(attName.equals(CoAPServer.ALTITUDE))
				this.setAltitude(reader.getAttributeValue(i));
			if(attName.equals(CoAPServer.FLOOR))
				this.setFloor(reader.getAttributeValue(i));
			if(attName.equals(CoAPServer.LOCATIONKEYWORD))
				this.setLocationkeywork(reader.getAttributeValue(i));
			if(attName.equals(CoAPServer.LOCATIONIDENTIFIER))
				this.setLocationIdentifier(reader.getAttributeValue(i));
			if(attName.equals(CoAPServer.COMPUTATIONALCOST))
				this.setComputationalcost(reader.getAttributeValue(i));
			if(attName.equals(CoAPServer.BATTERYCOST))
				this.setBatterycost(reader.getAttributeValue(i));
			if(attName.equals(CoAPServer.MEASUREMENT)){
				System.out.print("Meas found " + attName+"\n");
				this.setMeasurement(reader.getAttributeValue(i));
			}
			
		}
	}
	
	@Override
    public abstract void handleGET(CoapExchange exchange);
	
    @Override
    public abstract void handlePOST(CoapExchange exchange);
	
	@Override
    public abstract void handlePUT(CoapExchange exchange);
	
	public static String readDeviceId(XMLStreamReader reader) {
		for(int i = 0 ; i < reader.getAttributeCount(); i++){
			String attName = reader.getAttributeLocalName(i);
			if(attName.equals(CoAPServer.DEVICEID))
			{
				return reader.getAttributeValue(i);
			}
		}
		return null;
	}
}
