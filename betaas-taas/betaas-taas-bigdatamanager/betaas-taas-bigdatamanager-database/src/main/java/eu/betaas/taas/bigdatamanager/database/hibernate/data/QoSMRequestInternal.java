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
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "QOSM_REQUEST_INTERNAL")
public class QoSMRequestInternal implements java.io.Serializable {
 
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Double maxResponseTime;

	private Double minInterRequestTime;
	
	QoSMServiceRequest id;
 
	public QoSMRequestInternal() {
	}
	
	public QoSMRequestInternal(QoSMRequestInternal req) {
		this.id = new QoSMServiceRequest(req.id.getServiceId(), req.id.getRequestId());
		this.maxResponseTime = req.maxResponseTime;
		this.minInterRequestTime = req.minInterRequestTime;
	}
	
	public QoSMRequestInternal(String serviceId, Integer requestId, Double maxResponseTime, Double minInterRequestTime) {
		this.id = new QoSMServiceRequest(serviceId, requestId);
		this.maxResponseTime = maxResponseTime;
		this.minInterRequestTime = minInterRequestTime;
	}
	
	@EmbeddedId
	public QoSMServiceRequest  getId( ) { return id; }
	public void setId(QoSMServiceRequest  id) { this.id = id; }

	@Column(name = "maxResponseTime", unique = false, nullable = true)
	public Double getMaxResponseTime() {
		return maxResponseTime;
	}
	
	public void setMaxResponseTime(Double maxResponseTime) {
		this.maxResponseTime = maxResponseTime;
	}
	
	@Column(name = "minInterRequestTime", unique = false, nullable = true)
	public Double getMinInterRequestTime() {
		return minInterRequestTime;
	}
	
	public void setMinInterRequestTime(Double minInterRequestTime) {
		this.minInterRequestTime = minInterRequestTime;
	}
	
	public String toString(){
		String msg = new String();
		msg = "\t\tServiceId: " + id.getServiceId() + "\n\t\trequestId: " + id.getRequestId();
		msg += "\n\t\tMaxResponseTime: " + maxResponseTime + "\n\t\tMinInterrequestTime: " + minInterRequestTime;
		return msg;
	}
}

