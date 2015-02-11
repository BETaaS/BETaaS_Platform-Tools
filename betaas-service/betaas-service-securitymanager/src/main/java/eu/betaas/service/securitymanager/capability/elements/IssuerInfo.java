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

package eu.betaas.service.securitymanager.capability.elements;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlElement;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import eu.betaas.service.securitymanager.capability.elements.helper.IssuerType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Issuer" , propOrder = {
		"issuerType",
		"issuerCertificate",
		"issuerCapabilities"})

/**
 * This class describes the IssuerInfo data structure
 * 
 * @author Bayu Anggorojati [ba@es.aau.dk]
 * Center for TeleInFrastruktur, Aalborg University, Denmark
 *
 */
public class IssuerInfo {
	@XmlElement(name = "IssuerType")
	private String issuerType;
	@XmlElement(name = "IssuerCertificate")
	private byte[] issuerCertificate;
	@XmlElement(name = "IssuerCapabilities")
	private Capabilities issuerCapabilities;
	
	public IssuerInfo(){}

	public String getIssuerType() {
		return issuerType;
	}

	public void setIssuerType(String issuerType) {
		this.issuerType = issuerType;
	}

	public byte[] getIssuerCertificate() {
		return issuerCertificate;
	}

	public void setIssuerCertificate(byte[] issuerCertificate) {
		this.issuerCertificate = issuerCertificate;
	}

	public Capabilities getIssuerCapabilities() {
		return issuerCapabilities;
	}

	public void setIssuerCapabilities(Capabilities issuerCapabilities) {
		this.issuerCapabilities = issuerCapabilities;
	}
	
	public String toString(){
		Gson gson = new Gson();
		JsonObject jo = new JsonObject();
		jo.addProperty("IssuerType", issuerType.toString());
		
		String cert = "";
		for(byte b : issuerCertificate)
			cert = cert + Integer.toHexString(0xFF & b);
		
		jo.addProperty("IssuerCertificate", cert);
		jo.addProperty("IssuerCapabilities", gson.toJson(issuerCapabilities));
		
		return jo.toString();
	}
}
