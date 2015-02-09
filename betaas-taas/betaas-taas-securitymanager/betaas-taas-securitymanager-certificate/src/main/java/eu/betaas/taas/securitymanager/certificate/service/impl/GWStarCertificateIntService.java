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

package eu.betaas.taas.securitymanager.certificate.service.impl;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.security.Security;

import org.apache.log4j.Logger;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import eu.betaas.taas.securitymanager.certificate.service.IGatewayStarCertificateIntService;
import eu.betaas.taas.securitymanager.common.certificate.utils.Config;
import eu.betaas.taas.securitymanager.common.certificate.utils.GWCertificateUtilsBc;
import eu.betaas.taas.securitymanager.common.certificate.utils.PKCS12Utils;
import eu.betaas.taas.securitymanager.common.model.BcCredential;

/**
 * This class implements methods to create a set of certificates and credentials
 * for the GW*. 
 * 
 * @author Bayu Anggorojati [ba@es.aau.dk]
 * Center for TeleInFrastruktur, Aalborg University, Denmark
 *
 */
public class GWStarCertificateIntService implements 
		IGatewayStarCertificateIntService {

	Logger log = Logger.getLogger("betaas.taas.securitymanager");
	
	private static final String PKCS12_FILE_NAME_INTER = "GwStarInterCert.p12";
	private static final String PKCS12_FILE_NAME_OWN = "GwStarOwnCert.p12";
	
	private static final String ROOT_ALIAS = "root";
	private static final String INTER_ALIAS = "intermediate";
	private static final String END_ENTITY_ALIAS = "end";
	
	/** Path to the certificate files */
	private String certPath;
	
	public void createGwStarCredentials(X500Name subjRoot, X500Name subjInter, X500Name subjEnd, String ufn) {
		log.debug("In the beginning of createGwStarCredentials...");
		Security.addProvider(new BouncyCastleProvider());
		// First, create the GW* "signing" credentials including only the root and 
		// intermediate certificates which are encapsulated in a KeyStore
		BcCredential interCredentials = null;
		try {
			interCredentials = GWCertificateUtilsBc.
					createGwStarInterCredentials(subjRoot, subjInter, ROOT_ALIAS, INTER_ALIAS);
		} catch (Exception e) {
			log.error("Error creating GW* intermediate credentials: "+e.getMessage());
			e.printStackTrace();
		}
		
		log.info("Intermediate certificate of GW* has been created...");
		
		// create a PKCS12 file from the chain of certificate within the Intermediate credential
		AsymmetricKeyParameter priv = interCredentials.getPrivateKey();
		X509CertificateHolder[] chain = interCredentials.getCertificateChain();
		
		try {
			PKCS12Utils.createPKCS12FileBc(new FileOutputStream(certPath+PKCS12_FILE_NAME_INTER),
					priv, chain, GWCertificateUtilsBc.KEY_PASSWD);
		} catch (FileNotFoundException e) {
			log.error("Error creating PKCS12 file of intermediate credentials: " +e.
					getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			log.error("Error creating PKCS12 file of intermediate credentials: " +e.
					getMessage());
			e.printStackTrace();
		}
		
		// Then, create the GW* "own" credentials including the root, intermediate,
		// and the end entity certificates which are encapsulated in a KeyStore
		BcCredential ownCredentials = null;
		try {
			ownCredentials = GWCertificateUtilsBc.createGwStarOwnCredentials(subjEnd,
					ROOT_ALIAS, INTER_ALIAS, END_ENTITY_ALIAS, ufn, certPath);
		} catch (Exception e) {
			log.error("Error creating end entity credentials for GW*: "
					+e.getMessage());
			e.printStackTrace();
		}
		
		priv = ownCredentials.getPrivateKey();
		chain = ownCredentials.getCertificateChain();
		
		try {
			PKCS12Utils.createPKCS12FileBc(new FileOutputStream(certPath+PKCS12_FILE_NAME_OWN), 
					priv, chain, GWCertificateUtilsBc.KEY_PASSWD);
		} catch (FileNotFoundException e) {
			log.error("Error creating PKCS12 file of end entity credentials: "
					+e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			log.error("Error creating PKCS12 file of end entity credentials: "
					+e.getMessage());
			e.printStackTrace();
		}
		
		log.info("End entity certificate of GW* has been created...");
		
		// set this GW as GW*
		Config.isGwStar = true;
	}
	
	public void setCertificatePath(String certificatePath){
		this.certPath = certificatePath;
	}
}
