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

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

//import com.google.gson.Gson;
//import com.google.gson.JsonObject;

import eu.betaas.service.securitymanager.capability.model.CapabilityExternal;
import eu.betaas.service.securitymanager.capability.model.CapabilityIssuer;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "IssuerCapabilities" , propOrder = {"capability"})

/**
 * This class describes the Capabilities data structure
 * 
 * @author Bayu Anggorojati [ba@es.aau.dk]
 * Center for TeleInFrastruktur, Aalborg University, Denmark
 *
 */
public class Capabilities {
	
	public Capabilities(){}
	
	@XmlElement(name = "CapabilityIssuer")
	private List<CapabilityIssuer> capability;

	public List<CapabilityIssuer> getCapability() {
		if(capability==null){
			capability = new ArrayList<CapabilityIssuer>();
		}
		return capability;
	}
	
//	public String toString(){
//		Gson gson = new Gson();
//		JsonObject jo = new JsonObject();
//		
//		jo.addProperty("Capability", gson.toJson(capability));
//		
//		return jo.toString();
//	}
}
