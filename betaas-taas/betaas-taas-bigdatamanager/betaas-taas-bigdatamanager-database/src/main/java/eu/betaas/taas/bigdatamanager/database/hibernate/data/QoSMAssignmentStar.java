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
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "QOSM_ASSIGNMENT_STAR")
public class QoSMAssignmentStar implements java.io.Serializable {
 
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	QoSMServiceRequestTS id;

	private Double totalBatteryCost;
	private Double totalComputationalCost;

	public QoSMAssignmentStar() {
	}
	
	public QoSMAssignmentStar(QoSMAssignmentInternal ass) {
		this.id = ass.id;
		this.totalBatteryCost = ass.getTotalBatteryCost();
		this.totalComputationalCost = ass.getTotalComputationalCost();
	}
 
	public QoSMAssignmentStar(String serviceId, int requestId, String thingServiceId,
			Double totalBatteryCost, Double totalComputationalCost) {
		this.id = new QoSMServiceRequestTS(serviceId, requestId, thingServiceId);
		this.totalBatteryCost = totalBatteryCost;
		this.totalComputationalCost = totalComputationalCost;
	}

	public QoSMAssignmentStar(QoSMAssignmentStar ass) {
		this.id = ass.id;
		this.totalBatteryCost = ass.getTotalBatteryCost();
		this.totalComputationalCost = ass.getTotalComputationalCost();
	}

	@EmbeddedId
	public QoSMServiceRequestTS getId( ) { return id; }
	public void setId(QoSMServiceRequestTS id) { this.id = id; }
 
	@Column(name = "totalBatteryCost", unique = false, nullable = true)
	public Double getTotalBatteryCost() {
		return totalBatteryCost;
	}
	
	public void setTotalBatteryCost(Double totalBatteryCost) {
		this.totalBatteryCost = totalBatteryCost;
	}
	
	@Column(name = "totalComputationalCost")
	public Double getTotalComputationalCost() {
		return totalComputationalCost;
	}
	
	public void setTotalComputationalCost(Double totalComputationalCost) {
		this.totalComputationalCost = totalComputationalCost;
	}
	
	@Override
	public String toString(){
		String msg = new String();
		msg = "\nRequest:\n" + this.id.getServiceId().toString() + ":" + String.valueOf(this.id.getRequestId());
		msg += "\nThingService:\n " + this.id.getThingServiceId().toString();
		msg += "\nTotalBatteryCost: " + String.valueOf(totalBatteryCost);
		msg += "\nTotalComputationalCost: " + String.valueOf(totalComputationalCost)+"\n";
		return msg;
	}
}