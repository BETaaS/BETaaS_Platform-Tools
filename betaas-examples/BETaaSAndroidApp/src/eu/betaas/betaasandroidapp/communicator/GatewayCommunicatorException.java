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

package eu.betaas.betaasandroidapp.communicator;

import eu.betaas.betaasandroidapp.pojo.Gateway;

public class GatewayCommunicatorException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8434393183311727267L;
	
	String code;
	Gateway gateway;
	
	public GatewayCommunicatorException (Gateway gateway,
	                                      String    code,
	                                      String  message) {
		super(message);
		this.gateway = gateway;
		this.code = code;
	}

	public Gateway getGateway() {
		return gateway;
	}

	public void setGateway(Gateway gateway) {
		this.gateway = gateway;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
}
