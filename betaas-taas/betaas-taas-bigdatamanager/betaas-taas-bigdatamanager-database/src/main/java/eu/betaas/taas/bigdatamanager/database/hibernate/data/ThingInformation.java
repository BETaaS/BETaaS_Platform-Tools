/*
 Copyright 2014-2015 Hewlett-Packard Development Company, L.P.

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
package eu.betaas.taas.bigdatamanager.database.hibernate.data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name = "T_THINGS")
public class ThingInformation {

	
	@Id
    private String thingID;
		
    private String unit;
    
    private boolean is_input;
    
	private boolean is_digital;
    
    private String manufacturer;
    
    private String location;
    
    private String serial;
    
    private boolean is_output;
    
    private String maximum_response_time;
   
    private String computational_cost;
    
    private String protocol;
    
	public String getProtocol() {
		return protocol;
	}


	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}
	
	public String getComputational_cost() {
		return computational_cost;
	}


	public void setComputational_cost(String computational_cost) {
		this.computational_cost = computational_cost;
	}
    
    
	public boolean isIs_output() {
		return is_output;
	}


	public void setIs_output(boolean is_output) {
		this.is_output = is_output;
	}

	public String getMaximum_response_time() {
		return maximum_response_time;
	}


	public void setMaximum_response_time(String maximum_response_time) {
		this.maximum_response_time = maximum_response_time;
	}
    public String getSerial() {
		return serial;
	}

	public void setSerial(String serial) {
		this.serial = serial;
	}

	private String type;
	
    private byte[] tags;

	public String getThingID() {
		return thingID;
	}

	public void setThingID(String thingID) {
		this.thingID = thingID;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public byte[] getTags() {
		return tags;
	}

	public void setTags(byte[] tags) {
		this.tags = tags;
	}
    

    public boolean isIs_input() {
		return is_input;
	}

	public void setIs_input(boolean is_input) {
		this.is_input = is_input;
	}

	public boolean isIs_digital() {
		return is_digital;
	}

	public void setIs_digital(boolean is_digital) {
		this.is_digital = is_digital;
	}

	public String getManufacturer() {
		return manufacturer;
	}

	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}

	
	
}
