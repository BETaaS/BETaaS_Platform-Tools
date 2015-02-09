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

//BETaaS - Building the Environment for the Things as a Service
//
//Component: TaaS QoS Manager Database
//Responsible: Carlo Vallati & Giacomo Tanganelli

package eu.betaas.taas.bigdatamanager.database.hibernate.data;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "QOSM_REQUEST_STAR")
public class QoSMRequestStar implements java.io.Serializable {
 
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Double maxResponseTime;

	private Double minInterRequestTime;
	
	private String gatewayId;
	
	QoSMServiceRequest id;
 
	public QoSMRequestStar() {
	}
	
	public QoSMRequestStar(String serviceId, Integer requestId, Double maxResponseTime, 
			Double minInterRequestTime, String gatewayId) {
		this.id = new QoSMServiceRequest(serviceId, requestId);
		this.maxResponseTime = maxResponseTime;
		this.minInterRequestTime = minInterRequestTime;
		this.gatewayId = gatewayId;
	}
	
	public QoSMRequestStar(QoSMRequestStar req) {
		this.id = new QoSMServiceRequest(req.id.getServiceId(), req.id.getRequestId());
		this.maxResponseTime = req.getMaxResponseTime();
		this.minInterRequestTime = req.getMinInterRequestTime();
		this.gatewayId = req.gatewayId;
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
	
	@Column(name = "gatewayId", unique = false, nullable = true)
	public String getGatewayId() {
		return gatewayId;
	}
	
	public void setGatewayId(String gatewayId) {
		this.gatewayId = gatewayId;
	}
	
	@Override
	public String toString(){
		String msg = new String();
		msg = "ServiceId: " + id.getServiceId() + "\nrequestId: " + id.getRequestId()+"\nGatewayId: " + gatewayId;
		msg += "\nMaxResponseTime: " + maxResponseTime + "\nMinInterrequestTime: " + minInterRequestTime + "\n";
		return msg;
	}
}


