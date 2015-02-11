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
@Table(name = "QOSM_THING_INTERNAL")
public class QoSMThingInternal implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String deviceId;
	
	private Double batteryLevel;

	private Integer numass;

	private Double capacityUsed;
	
	private String gatewayId;
 
	public QoSMThingInternal() {
	}
	
	public QoSMThingInternal(QoSMThingInternal thing) {
		this.deviceId = thing.deviceId;
		this.batteryLevel = thing.batteryLevel;
		this.numass = thing.numass;
		this.capacityUsed = thing.capacityUsed;
		this.gatewayId = thing.gatewayId;
	}
 
	public QoSMThingInternal(String deviceId, Double batteryLevel, Integer numass, Double capacityUsed, String gatewayId) {
		this.deviceId = deviceId;
		this.batteryLevel = batteryLevel;
		this.numass = numass;
		this.capacityUsed = capacityUsed;
		this.gatewayId = gatewayId;
	}
	
	@Id
	@Column(name = "deviceId")
	public String getDeviceId() {
		return this.deviceId;
	}
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
 
	@Column(name = "batteryLevel", unique = false, nullable = false)
	public Double getBatteryLevel() {
		return this.batteryLevel;
	}
 	public void setBatteryLevel(Double batteryLevel) {
		this.batteryLevel = batteryLevel;
	}
 
	@Column(name = "capacityUsed", unique = false, nullable = false)
	public Double getCapacityUsed() {
		return capacityUsed;
	}
	
	public void setCapacityUsed(Double capacityUsed) {
		this.capacityUsed = capacityUsed;
	}

	@Column(name = "numass", unique = false, nullable = false)	
	public Integer getNumass() {
		return numass;
	}
	public void setNumass(Integer numass) {
		this.numass = numass;
	}
	
	@Column(name = "gatewayId", unique = false, nullable = false)
	public String getGatewayId() {
		return gatewayId;
	}

	public void setGatewayId(String gatewayId) {
		this.gatewayId = gatewayId;
	}
	
	public String toString(){
		String msg = new String();
		msg = "\n\tDeviceId: ";
		msg += this.deviceId;
		msg += "\n\tBatteryRamaining: ";
		msg += String.valueOf(this.batteryLevel);
		msg += "\n\tCapacityUsed: ";
		msg += String.valueOf(this.capacityUsed);
		msg += "\n\tNum assignment: ";
		msg += String.valueOf(this.numass);
		msg += "\n\tGatewayId: ";
		msg += this.gatewayId;
		msg += "\n";
		return msg;
		
	}
	
}
