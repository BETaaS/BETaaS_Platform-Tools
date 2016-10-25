/*
 *
Copyright 2014-2015 Department of Information Engineering, University of Pisa

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

// BETaaS - Building the Environment for the Things as a Service
//
// Component: TaaS QoS Manager Database
// Responsible: Carlo Vallati & Giacomo Tanganelli

package eu.betaas.taas.bigdatamanager.database.hibernate.data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name = "QOSM_THING_SERVICE_INTERNAL")
public class QoSMThingServiceInternal implements java.io.Serializable {
 
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String deviceId;	

	private String thingServiceId;
	
	private Double responseTime;

	private Double batteryCost;

	private Double computationalCost;
	
	private Boolean reachable;
 
	public QoSMThingServiceInternal() {
	}
	
	public QoSMThingServiceInternal(String deviceId, String thingServiceId, Double responseTime, Double batteryCost, Double computationalCost) {
		this.deviceId = deviceId;
		this.thingServiceId = thingServiceId;
		this.responseTime = responseTime;
		this.batteryCost = batteryCost;
		this.computationalCost = computationalCost;
		this.reachable = true;
	}
	
public QoSMThingServiceInternal(QoSMThingServiceInternal ts) {
		this.deviceId = ts.getDeviceId();
		this.thingServiceId = ts.getThingServiceId();
		this.responseTime = ts.getResponseTime();
		this.batteryCost = ts.getBatteryCost();
		this.computationalCost = ts.getComputationalCost();
		this.reachable = ts.getReachable();
	}

	//	@EmbeddedId
//	public QoSMThingServiceInternal_PK getId( ) { return id; }
//	public void setId(QoSMThingServiceInternal_PK id) { this.id = id; }
	@Id
	@Column(name = "TS_ID", unique = true, nullable = false)
	public String getId( ) { return thingServiceId; }
	public void setId(String id) { this.thingServiceId = id; }
	
	@Column(name="deviceId", unique = false, nullable = true)
	public String getDeviceId() {
		return deviceId;
	}
	
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	
	@Column(name = "thingServiceId", unique = false, nullable = true)
	public String getThingServiceId() {
		return thingServiceId;
	}
	public void setThingServiceId(String thingServiceId) {
		this.thingServiceId = thingServiceId;
	}
	
	@Column(name = "responseTime")
	public Double getResponseTime() {
		return responseTime;
	}
	public void setResponseTime(Double responseTime) {
		this.responseTime = responseTime;
	}
	
	@Column(name = "batteryCost")
	public Double getBatteryCost() {
		return batteryCost;
	}
	public void setBatteryCost(Double batteryCost) {
		this.batteryCost = batteryCost;
	}
	
	@Column(name = "computationalCost")
	public Double getComputationalCost() {
		return computationalCost;
	}
	
	public void setComputationalCost(Double computationalCost) {
		this.computationalCost = computationalCost;
	}
	
	public String toString(){
		String msg = new String();
		msg = "\t\tThingServiceId: ";
		msg += this.thingServiceId;
		msg += "\n\t\tResponseTime: ";
		msg += String.valueOf(this.responseTime);
		msg += "\n\t\tBatteryCost: ";
		msg += String.valueOf(this.batteryCost);
		msg += "\n\t\tComputationalCost: ";
		msg += String.valueOf(this.computationalCost);
		return msg;
	}
	
	@Column(name="reachable", unique = false, nullable = true)
	public void setReachable(Boolean reachable) {
		this.reachable = reachable;
	}

	public Boolean getReachable() {
		return reachable;
	}
}

