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
import javax.persistence.Transient;

@Embeddable
public
class QoSMServiceRequest implements java.io.Serializable
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String serviceId;
	
	private int requestId;
	
	public QoSMServiceRequest(){}
	
	public QoSMServiceRequest(String serviceId, int requestId){
		this.setServiceId(serviceId);
		this.setRequestId(requestId);
	}

	@Column(name = "SERVICE_ID", unique = false, nullable = false)
	public String getServiceId() {
		return serviceId;
	}

	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}

	@Column(name = "REQUEST_ID", unique = false, nullable = false)
	public int getRequestId() {
		return requestId;
	}

	public void setRequestId(int requestId) {
		this.requestId = requestId;
	}
	
	public boolean equals(Object obj)
	{
		if (obj == this) return true;
		if (!(obj instanceof QoSMServiceRequest))
			return false;
		QoSMServiceRequest pk = (QoSMServiceRequest)obj;
		if (!serviceId.equals(pk.serviceId)) return false;
		if (requestId != pk.requestId) return false;
		return true;
	}
	public int hashCode( )
	{
		return serviceId.hashCode( ) + (int)requestId;
	}

	
	public String keyString() {
	 	return serviceId + ":" + (int)requestId;
		
	}

}
