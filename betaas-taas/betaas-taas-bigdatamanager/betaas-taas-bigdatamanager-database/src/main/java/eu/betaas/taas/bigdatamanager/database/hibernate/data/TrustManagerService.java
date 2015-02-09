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

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

@Entity
@Table(name = "T_TRUST_MANAGER_SERVICE")
@IdClass(TrustManagerServiceId.class)
public class TrustManagerService {

	@Id
	private String thingServiceID;
	@Id
	private Timestamp timestamp;
	private Double SecurityMechanisms;
	private Double QoSFulfillment;
	private Double Dependability;
	private Double Scalability;
	private Double BatteryLoad;
	private Double DataStability;
	private Double ThingServiceTrust;
	
	
	public String getThingServiceId() {
		return thingServiceID;
	}
	public void setThingServiceId(String thingServiceId) {
		thingServiceID = thingServiceId;
	}
	public Timestamp getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}
	public Double getSecurityMechanisms() {
		return SecurityMechanisms;
	}
	public void setSecurityMechanisms(Double securityMechanisms) {
		SecurityMechanisms = securityMechanisms;
	}
	public Double getQoSFulfillment() {
		return QoSFulfillment;
	}
	public void setQoSFulfillment(Double qoSFulfillment) {
		QoSFulfillment = qoSFulfillment;
	}
	public Double getDependability() {
		return Dependability;
	}
	public void setDependability(Double dependability) {
		Dependability = dependability;
	}
	public Double getScalability() {
		return Scalability;
	}
	public void setScalability(Double scalability) {
		Scalability = scalability;
	}
	public Double getBatteryLoad() {
		return BatteryLoad;
	}
	public void setBatteryLoad(Double batteryLoad) {
		BatteryLoad = batteryLoad;
	}
	public Double getDataStability() {
		return DataStability;
	}
	public void setDataStability(Double dataStability) {
		DataStability = dataStability;
	}
	public Double getThingServiceTrust() {
		return ThingServiceTrust;
	}
	public void setThingServiceTrust(Double thingServiceTrust) {
		ThingServiceTrust = thingServiceTrust;
	}
	
	public TrustManagerServiceId gettrustManagerServiceId(){
		TrustManagerServiceId ti = new TrustManagerServiceId();
		ti.thingServiceID=this.getThingServiceId();
		ti.timestamp=this.getTimestamp();
		return ti;
	}
		
	
}

@Embeddable
class TrustManagerServiceId implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String thingServiceID;
	Timestamp timestamp;
}