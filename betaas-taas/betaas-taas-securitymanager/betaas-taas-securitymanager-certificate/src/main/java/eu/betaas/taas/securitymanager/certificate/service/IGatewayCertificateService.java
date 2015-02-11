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
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;

import eu.betaas.taas.securitymanager.common.model.BcCredential;

/**
 * This interface provides methods that expose some services in relation to the
 * general certificate related operation in the internal GW.
 * 
 * @author Bayu Anggorojati [ba@es.aau.dk]
 * Center for TeleInFrastruktur, Aalborg University, Denmark
 *
 */
public interface IGatewayCertificateService {
	/**
	 * A method to load my own certificate and GW* intermediate certificate stored 
	 * in .p12 file
	 * @return BcCredential containing my certificate and the signing certificate
	 * of the GW* (intermediate certificate) 
	 * @throws Exception 
	 * 
	 */
	public BcCredential loadMyCertificate(int certType) ;
	
	/**
	 * A method to load certificate that will be used to validate the applications 
	 * certificate
	 * @param fileName: PKCS12 file name that stores "AppsStore" intermediate 
	 * certificate
	 * @return BcCredential containing the "AppsStore" intermediate certificate
	 * @throws Exception
	 */
	public BcCredential loadAppStoreCertificate(String fileName) throws Exception;
	
	/**
	 * A method to decode the credential of the application from the submitted
	 * bytes array
	 * @param pfx: PKCS12 file that stores application's end entity certificate 
	 * in byte
	 * @return BcCredential containing the application's end entity certificate
	 * @throws Exception
	 */
	public BcCredential readAppsCertificate(byte[] pfx) throws Exception;
	
	/**
	 * A method to store my (chain of) certificate(s) into a .p12 file 
	 * @param key: private key that corresponds to public key of my certificate
	 * @param chain: the certificate chains (GW* intermediate and my certificates)
	 * @throws Exception
	 */
	public void storeMyCertificate(AsymmetricKeyParameter priv, 
			X509CertificateHolder[] chain) throws Exception;
	
	/**
	 * A method to build a PKCS10 Certification Request for a GW, before sending
	 * a request to join BETaaS instance to GW*
	 * @param subject: the X500Name consists of subject info in the certificate
	 * @param kp: the generated long term key pair for certificate 
	 * @param subjectAltName: subject alternative name (e.g. User Friendly Name)
	 * @return
	 * @throws Exception
	 */
	public PKCS10CertificationRequest buildCertificationRequest(X500Name subject, 
			AsymmetricCipherKeyPair kp, String subjectAltName) throws Exception;
}
