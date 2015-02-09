/**

Copyright 2014 ATOS SPAIN S.A.

Licensed under the Apache License, Version 2.0 (the License);
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

Authors :
Sergio García Villalonga. Atos Research and Innovation, Atos SPAIN SA
@email sergio.garciavillalonga@atos.net
**/

package eu.betaas.betaasandroidapp.pojo;

public class Measurement {
	private String gatewayId, serviceId;
	private Boolean measurement;
	private Long time;
	
	
	public Measurement (long time, Boolean measurement, String gatewayId, String serviceId) {
		this.time = time;
		this.measurement = measurement;
		this.gatewayId = gatewayId;
		this.serviceId = serviceId;
	}

	public Long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public Boolean getMeasurement() {
		return measurement;
	}

	public void setMeasurement(Boolean measurement) {
		this.measurement = measurement;
	}

	public String getGatewayId() {
		return gatewayId;
	}

	public void setGatewayId(String appId) {
		this.gatewayId = appId;
	}

	public String getServiceId() {
		return serviceId;
	}

	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}
}
