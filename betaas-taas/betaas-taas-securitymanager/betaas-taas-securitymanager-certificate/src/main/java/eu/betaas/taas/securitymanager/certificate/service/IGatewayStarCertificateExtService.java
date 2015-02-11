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

import java.io.IOException;
import java.security.GeneralSecurityException;

import org.bouncycastle.operator.OperatorCreationException;

import eu.betaas.taas.securitymanager.common.model.ArrayOfCertificate;

/**
 * This interface provides methods that expose some services within the GW* in 
 * relation to the certificate related operation for the other GW.
 * 
 * @author Bayu Anggorojati [ba@es.aau.dk]
 * Center for TeleInFrastruktur, Aalborg University, Denmark
 *
 */
public interface IGatewayStarCertificateExtService {
	/**
	 * A method used by other GW when requesting a certificate to GW* prior to 
	 * joining BETaaS instance (This is an external interface)
	 * @param gwCertReq: PKCS10CertificationRequest in byte (encoded)
	 * @return Public key of the GW* and certificate for the requesting GW 
	 * (encapsulated in PubKeyCert class)
	 * @throws IOException 
	 * @throws GeneralSecurityException 
	 * @throws OperatorCreationException 
	 * @throws Exception 
	 */
	public ArrayOfCertificate issueGwCertificate(byte[] gwCertReq) ;
	
	/**
	 * A method used by other GW when requesting a certificate to GW* prior to 
	 * joining BETaaS instance (This is invoked in the GW*)
	 * @param gwCertReq: PKCS10CertificationRequest in byte (encoded)
	 * @return a set of X509CertificateHolders in byte array (encoded)
	 */
	public byte[][] issueGwCertByte(byte[] gwCertReq);
	
	/**
	 * A method to identify whether this GW is actually a GW* (using external IF
	 * of instance manager)
	 * @return true if this is GW*, false otherwise
	 */
	public boolean isGWStar();
}
