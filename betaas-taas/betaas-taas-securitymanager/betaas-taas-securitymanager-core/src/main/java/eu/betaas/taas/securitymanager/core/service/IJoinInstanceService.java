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

package eu.betaas.taas.securitymanager.core.service;

/**
 * This interface exposes a service that initiates certificate request for a GW
 * that wants to join an instance which is to be invoked by other BETaaS 
 * component/manager within the same GW.
 * 
 * @author Bayu Anggorojati [ba@es.aau.dk]
 * Center for TeleInFrastruktur, Aalborg University, Denmark
 *
 */
public interface IJoinInstanceService {
	
	/**
	 * A method to initiate a GW to join an instance that will result a certificate
	 * that will be created by GW*.
	 * @param countryCode: e.g. DK, DE, GR, IT, US, ...
	 * @param state: state of the country where the instance is located
	 * @param location: location of the instance
	 * @param orgName: organization name
	 * @param gwId: gateway ID
	 * @return: true if joining is accepted and certificate is successfully 
	 * retrieved by and from GW*, and then stored in my own GW. 
	 * @throws Exception 
	 */
	public boolean requestGwCertificate(String countryCode, String state, 
			String location, String orgName, String gwId) throws Exception;
}
