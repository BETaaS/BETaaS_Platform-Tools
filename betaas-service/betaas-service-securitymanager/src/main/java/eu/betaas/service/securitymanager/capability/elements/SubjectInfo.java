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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.google.gson.JsonObject;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SubjectInfo" , propOrder = {
		"subjectType",
		"publicKeyInfo"
})

/**
 * This class describes the SubjectInfo data structure
 * 
 * @author Bayu Anggorojati [ba@es.aau.dk]
 * Center for TeleInFrastruktur, Aalborg University, Denmark
 *
 */
public class SubjectInfo {
	@XmlElement(name = "SubjectType")
	private String subjectType;
	@XmlElement(name = "PublicKeyInfo")
	private byte[] publicKeyInfo;
	
	public SubjectInfo(){}

	public String getSubjectType() {
		return subjectType;
	}

	public void setSubjectType(String subjectType) {
		this.subjectType = subjectType;
	}

	public byte[] getPublicKeyInfo() {
		return publicKeyInfo;
	}

	public void setPublicKeyInfo(byte[] publicKeyInfo) {
		this.publicKeyInfo = publicKeyInfo;
	}
	
	public String toString(){
		JsonObject jo = new JsonObject();
		jo.addProperty("SubjectType", subjectType);
		String pubKeyInfo = "";
		
		for(byte b : publicKeyInfo){
			pubKeyInfo = pubKeyInfo + Integer.toHexString(0xFF & b);
		}
		
		jo.addProperty("PublicKeyInfo", pubKeyInfo);
		return jo.toString();
	}
}
