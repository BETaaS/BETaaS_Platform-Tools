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

import java.io.FileOutputStream;

import org.apache.log4j.Logger;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.osgi.framework.BundleContext;

import eu.betaas.taas.securitymanager.certificate.service.IGatewayCertificateService;
import eu.betaas.taas.securitymanager.certificate.service.IGatewayStarCertificateExtService;
import eu.betaas.taas.securitymanager.certificate.utils.CertificateBetaasBus;
import eu.betaas.taas.securitymanager.common.certificate.utils.Config;
import eu.betaas.taas.securitymanager.common.certificate.utils.GWCertificateUtilsBc;
import eu.betaas.taas.securitymanager.common.certificate.utils.PKCS12Utils;
import eu.betaas.taas.securitymanager.common.model.BcCredential;

/**
 * This is class implementation of IGatewayCertificateService interface
 * 
 * @author Bayu Anggorojati [ba@es.aau.dk]
 * Center for TeleInFrastruktur, Aalborg University, Denmark
 *
 */
public class GWCertificateService implements IGatewayCertificateService {

	private static Logger log = Logger.getLogger("betaas.taas.securitymanager");
	
	private static final String COMMON_GW_CERT = "GwOwnCert.p12";
	private static final String GW_STAR_CERT = "GwStarOwnCert.p12";
	private static final char[] KEY_PASSWD = "keyPassword".toCharArray();
	
	/** Class that handles BETaaS BUS in this certificate bundle */
	private CertificateBetaasBus bus;
	
	/** Bundle context reference from blueprint */
	private BundleContext context;
	
	/** Path to the certificate files */
	private String certPath;
	
	/**
	 * Initial setup method to initialize the betaas bus service
	 */
	public void setup(){
		bus = new CertificateBetaasBus(context);
	}

	public PKCS10CertificationRequest buildCertificationRequest(X500Name subject,
			AsymmetricCipherKeyPair kp, String subjectAltName) throws Exception {
		log.info("Building a certification request...");
		bus.sendData("Building a certification request", "info", "SecM");
		return GWCertificateUtilsBc.buildCertificateRequest(subject, kp, subjectAltName);
	}

	public BcCredential loadMyCertificate(int certType){
		BcCredential myCred;
		if(Config.isGwStar){
			log.info("Loading certificate of GW*...");
			bus.sendData("Loading certificate of GW*", "info", "SecM");
			myCred = PKCS12Utils.loadPKCS12Credential(certPath+GW_STAR_CERT, KEY_PASSWD, 
					certType);
		}
		else{
			log.info("Loading certificate of common GW...");
			bus.sendData("Loading certificate of GW*", "info", "SecM");
			myCred = PKCS12Utils.loadPKCS12Credential(certPath+COMMON_GW_CERT, KEY_PASSWD, 
					certType);
		}
		
		return myCred;
	}

	public void storeMyCertificate(AsymmetricKeyParameter priv, 
			X509CertificateHolder[] chain) throws Exception {
		if(Config.isGwStar){
			log.info("Storing certificate of GW*...");
			bus.sendData("Storing certificate of GW*", "info", "SecM");
			PKCS12Utils.createPKCS12FileBc(new FileOutputStream(certPath+GW_STAR_CERT), 
					priv, chain, KEY_PASSWD);
		}
		else{
			log.info("Storing certificate common GW...");
			bus.sendData("Storing certificate common GW", "info", "SecM");
			PKCS12Utils.createPKCS12FileBc(new FileOutputStream(certPath+COMMON_GW_CERT), 
					priv, chain, KEY_PASSWD);
		}
	}

	public BcCredential loadAppStoreCertificate(String fileName) throws Exception {
		log.info("Loading AppStore's certificate...");
		bus.sendData("Loading AppStore's certificate", "info", "SecM");
		return PKCS12Utils.loadPKCS12Credential(fileName, KEY_PASSWD, 
				PKCS12Utils.APPS_CERT);
	}

	public BcCredential readAppsCertificate(byte[] pfx) throws Exception {
		// TODO Auto-generated method stub
		log.info("Reading Application's certificate...");
		bus.sendData("Reading Application's certificate", "info", "SecM");
		return PKCS12Utils.loadPKCS12Credential(pfx, KEY_PASSWD);
	}
	
	public void setCertificatePath(String certificatePath){
		this.certPath = certificatePath;
	}
	
	/**
	 * Blueprint set reference to BundleContext
	 * @param context BundleContext
	 */
	public void setContext(BundleContext context) {
		this.context = context;
		log.debug("Got BundleContext from the blueprint...");
	}
}
