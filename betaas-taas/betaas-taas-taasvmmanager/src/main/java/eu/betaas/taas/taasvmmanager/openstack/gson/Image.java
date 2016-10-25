package eu.betaas.taas.taasvmmanager.openstack.gson;

import java.util.UUID;

public class Image {

	private String name;
	private String id;
	private int size;
	
	public Image () {
		
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public int getSize() {
		return size;
	}
	
	public void setSize(int size) {
		this.size = size;
	}
}
