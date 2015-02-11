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

import org.jboss.security.xacml.core.model.policy.ConditionType;

import com.google.gson.JsonObject;

import eu.betaas.service.securitymanager.capability.elements.helper.AccessType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AccessRight" , propOrder = {
		"accessType",
		"condition"
})
/**
 * This class describes the Access Right data structure
 * @author Bayu Anggorojati [ba@es.aau.dk]
 * Center for TeleInFrastruktur, Aalborg University, Denmark
 *
 */
public class AccessRight {
	@XmlElement(name = "AccessType")
	private String accessType;
	@XmlElement(name = "Condition")
	private ConditionType condition;
	
	public String getAccessType() {
		return accessType;
	}
	public void setAccessType(String accessType) {
		this.accessType = accessType;
	}
	public ConditionType getCondition() {
		return condition;
	}
	public void setCondition(ConditionType condition) {
		this.condition = condition;
	}
	
	public String toString(){
		JsonObject jo = new JsonObject();
		
		jo.addProperty("AccessType", accessType.toString());
		jo.addProperty("Condition", condition.toString());
		
		return jo.toString();
	}
	
}
