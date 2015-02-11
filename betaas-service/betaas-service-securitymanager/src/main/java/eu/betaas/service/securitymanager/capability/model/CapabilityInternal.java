/**
Copyright 2014-2015 Center for TeleInFrastruktur (CTIF), Aalborg University

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

package eu.betaas.service.securitymanager.capability.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.google.gson.JsonObject;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CapabilityInternal")

/**
 * This class describes the Internal Capability data structure
 * 
 * @author Bayu Anggorojati [ba@es.aau.dk]
 * Center for TeleInFrastruktur, Aalborg University, Denmark
 *
 */
public class CapabilityInternal {
	@XmlElement(name = "ResourceID")
	private String resourceId;
	@XmlElement(name = "DigitalSignature")
	private byte[] digitalSignature;
	
	public CapabilityInternal(){}
	
	/**
	 * get the ID of the resource (i.e. thingServiceId) to be accessed
	 * @return
	 */	
	public String getResourceId() {
		return resourceId;
	}

	/**
	 * set the ID of the resource (i.e. thingServiceId) to be accessed
	 * @param resourceId
	 */
	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}

	/**
	 * get the digital signature of the capability. The digital signature is a 
	 * product of the hashed resourceId signed with the private key in the GW's 
	 * (as the resource owner) certificate.
	 * @return
	 */
	public byte[] getDigitalSignature() {
		return digitalSignature;
	}

	/**
	 * set the digital signature of the capability
	 * @param digitalSignature
	 */
	public void setDigitalSignature(byte[] digitalSignature) {
		this.digitalSignature = digitalSignature;
	}

	/**
	 * return the capability's content in (XML) String 
	 */
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		JsonObject obj = new JsonObject();
		obj.addProperty("ResoursceID", resourceId);
		
		return obj.toString();
	}
	
}
