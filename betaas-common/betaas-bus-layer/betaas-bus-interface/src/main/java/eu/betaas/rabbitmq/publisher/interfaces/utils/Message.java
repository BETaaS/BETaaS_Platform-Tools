package eu.betaas.rabbitmq.publisher.interfaces.utils;


public class Message {

	private long timestamp;
	public enum Layer {ADAPTATION,TAAS,SERVICE,INSTANCE};
	private Layer layer;
	private String origin;
	private String level;
	private String descritpion; 
	private String identity_number;

	public long getTimestamp() {
		return timestamp;
	}



	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}



	public Layer getLayer() {
		return layer;
	}



	public void setLayer(Layer layer) {
		this.layer = layer;
	}



	public String getOrigin() {
		return origin;
	}



	public void setOrigin(String origin) {
		this.origin = origin;
	}



	public String getLevel() {
		return level;
	}



	public void setLevel(String level) {
		this.level = level;
	}



	public String getDescritpion() {
		return descritpion;
	}



	public void setDescritpion(String descritpion) {
		this.descritpion = descritpion;
	}



	public String getIdentity_number() {
		return identity_number;
	}



	public void setIdentity_number(String identity_number) {
		this.identity_number = identity_number;
	}
	
	
}


