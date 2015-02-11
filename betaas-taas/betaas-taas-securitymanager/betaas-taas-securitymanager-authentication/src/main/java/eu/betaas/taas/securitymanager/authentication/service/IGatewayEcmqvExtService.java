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

package eu.betaas.taas.securitymanager.authentication.service;

import eu.betaas.taas.securitymanager.common.mqv.EcmqvMessage;

/**
 * This interface provides methods that expose services concerning the ECMQV key
 * agreement protocol to the the external GW.
 * 
 * @author Bayu Anggorojati [ba@es.aau.dk]
 * Center for TeleInFrastruktur, Aalborg University, Denmark
 *
 */
public interface IGatewayEcmqvExtService {
	
	
	/**
	 * A method to initiate the ECMQV to derive shared keys with other GW. This is
	 * to be invoked by other GW.
	 * @param ephPub: ephemeral public key of the other GW (who invokes this method)
	 * @param cert: certificate of the other GW (who invokes this method)
	 * @return EcmqvMessage object, which contains my ephemeral public key, my
	 * certificate, and the calculated MAC 2
	 * @throws Exception
	 */
	public EcmqvMessage initEcmqv(byte[] ephPubX, byte[] ephPubY, byte[] certByte);
	
	
	/**
	 * A method to send the last ECMQV message which contains the calculated MAC 3
	 * that is to be invoked by other GW
	 * @param mac the calculated MAC 3
	 * @return true if the calculated MAC 3 is valid, otherwise false
	 */
	public boolean lastEcmqv(byte[] mac);
	
}
