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

package eu.betaas.taas.securitymanager.certificate.service;

import org.bouncycastle.asn1.x500.X500Name;

/**
 * This interface provides methods that expose some services within the GW* in 
 * relation to the certificate related operation to be used internally.
 * 
 * @author Bayu Anggorojati [ba@es.aau.dk]
 * Center for TeleInFrastruktur, Aalborg University, Denmark
 *
 */
public interface IGatewayStarCertificateIntService {
	
	/**
	 * A method to initiate the GW* credentials (including intermediate and my own
	 * KeyStore) creation, when it is decided to be the GW*. The intermediate Key-
	 * Store is used to sign certificates for other GW (with its private key). 
	 * @param rootAlias: alias of the root certificate
	 * @param interAlias: alias of the intermediate certificate
	 * @param eeAlias: alias of the end entity certificate
	 * @param keyPasswd: password of private key in the KeyStore
	 * @throws Exception 
	 */
	public void createGwStarCredentials(X500Name subjRoot, X500Name subjInter, 
			X500Name subjEnd, String ufn);
}
