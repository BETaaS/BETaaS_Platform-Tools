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
 * This interface exposes a method to initiate ECMQV key agreement protocol to 
 * create secure communication between 2 Gateways. This method is to be invoked
 * by other BETaaS component/manager within the same GW. 
 * 
 * @author Bayu Anggorojati [ba@es.aau.dk]
 * Center for TeleInFrastruktur, Aalborg University, Denmark
 *
 */
public interface ISecGWCommService {
	
	/**
	 * A method to derive shared keys based on ECMQV protocol in order to initiate
	 * secure communication between GWs.
	 * @param gwDestId: ID of the destination Gateway
	 * @return: true if the keys are derived successfully 
	 * @throws Exception
	 */
	public boolean deriveSharedKeys(String gwDestId) throws Exception;
	
	/**
	 * A method to encrypt the data to be sent to other GW
	 * @param gwDestId: The ID of destination GW
	 * @param data: data to be encrypted in String
	 * @return: encrypted data in Base64 encoding
	 */
	public String doEncryptData(String gwDestId, String data);
	
	/**
	 * A method to decrypt the data received from other GW
	 * @param gwOriId: The ID of GW that originates the message
	 * @param encrypted: The received encrypted data in Base64 encoding
	 * @return: the original data in String
	 */
	public String doDecryptData(String gwOriId, String encrypted);
}
