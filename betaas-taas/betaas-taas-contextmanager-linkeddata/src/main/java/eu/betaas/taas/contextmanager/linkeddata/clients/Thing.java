package eu.betaas.taas.contextmanager.linkeddata.clients;

import com.google.gson.annotations.SerializedName;

public class Thing {
	@SerializedName("Altitude")
	private float altitude;
	@SerializedName("Environment")
	private boolean environment;
	@SerializedName("Floor")
	private int floor;
	@SerializedName("Latitude")
	private float latitude;
	@SerializedName("LocationIdentifier")
	private String locationIdentifier;
	@SerializedName("LocationKeyword")
	private String locationKeyword;
	@SerializedName("Longitude")
	private float longitude;
	@SerializedName("Measurement")
	private String measurement;
	@SerializedName("ThingId")
	private String thingId;
	@SerializedName("Type")
	private String type;
	@SerializedName("Unit")
	private String unit;
	
	public Thing() {}

	public float getAltitude() {
		return altitude;
	}

	public void setAltitude(float altitude) {
		this.altitude = altitude;
	}

	public boolean isEnvironment() {
		return environment;
	}

	public void setEnvironment(boolean environment) {
		this.environment = environment;
	}

	public int getFloor() {
		return floor;
	}

	public void setFloor(int floor) {
		this.floor = floor;
	}

	public float getLatitude() {
		return latitude;
	}

	public void setLatitude(float latitude) {
		this.latitude = latitude;
	}

	public float getLongitude() {
		return longitude;
	}

	public void setLongitude(float longitude) {
		this.longitude = longitude;
	}

	public String getLocationIdentifier() {
		return locationIdentifier;
	}

	public void setLocationIdentifier(String locationIdentifier) {
		this.locationIdentifier = locationIdentifier;
	}

	public String getLocationKeyword() {
		return locationKeyword;
	}

	public void setLocationKeyword(String locationKeyword) {
		this.locationKeyword = locationKeyword;
	}

	public String getMeasurement() {
		return measurement;
	}

	public void setMeasurement(String measurement) {
		this.measurement = measurement;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getThingId() {
		return thingId;
	}

	public void setThingId(String thingId) {
		this.thingId = thingId;
	}
}
