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

import org.bouncycastle.crypto.AsymmetricCipherKeyPair;

import eu.betaas.taas.securitymanager.common.mqv.EcmqvMessage;

/**
 * This interface provides methods that expose services concerning the ECMQV key
 * agreement protocol within the GW itself.
 * 
 * @author Bayu Anggorojati [ba@es.aau.dk]
 * Center for TeleInFrastruktur, Aalborg University, Denmark
 *
 */
public interface IGatewayEcmqvIntService {
	/**
	 * A method to generate ephemeral key pair before initiating ECMQV. This is to
	 * be invoked locally (in the same GW)
	 * @return
	 * @throws Exception
	 */
	public AsymmetricCipherKeyPair generateEphemeralKeyPair() throws Exception;
	
	/**
	 * A method to response the initEcmqv and after receiving EcmqvMessage from 
	 * other GW. This is to be invoked locally (in the same GW)
	 * @param eMsg: result of initEcmqv or the received EcmqvMessage 
	 * @return the calculated MAC (in byte array) that will be sent back again to
	 * the other GW from which we want to derive shared keys
	 * @throws Exception 
	 */
	public byte[] responseEcmqv(EcmqvMessage eMsg) throws Exception;
	
	/**
	 * A method to set the expire time as well as the k2 of the shared key as a 
	 * result of lastEcmqv method from the external interface of a remote GW, in 
	 * the catalogs.
	 * @param remoteGwId
	 * @param time
	 */
	public void setKeyAndExpireTime(String remoteGwId, long time);
	
	/**
	 * A method to retrieve the expire time of the shared key associated with a 
	 * remote GW. 
	 * @return
	 */
	public long getExpireTime(String remoteGwId);
	
	/**
	 * A method to retrieve k2 to be used for encryption associated with a remote 
	 * GW. 
	 * @return
	 */
	public byte[] getK2(String remoteGwId);
}