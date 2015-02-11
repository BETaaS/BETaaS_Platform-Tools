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

package eu.betaas.taas.securitymanager.core.service.impl;

import java.security.Security;
import java.security.interfaces.ECPublicKey;

import org.apache.log4j.Logger;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import eu.betaas.taas.securitymanager.authentication.service.IGatewayEcmqvExtService;
import eu.betaas.taas.securitymanager.authentication.service.IGatewayEcmqvIntService;

import eu.betaas.taas.securitymanager.certificate.service.IGatewayCertificateService;
import eu.betaas.taas.securitymanager.common.certificate.utils.PKCS12Utils;
import eu.betaas.taas.securitymanager.common.model.BcCredential;
import eu.betaas.taas.securitymanager.common.mqv.EcmqvMessage;
//import eu.betaas.taas.securitymanager.core.activator.SecMTaasCoreActivator;
import eu.betaas.taas.securitymanager.core.service.ISecGWCommService;

/**
 * Class implementation of ISecGWCommService interface
 * 
 * @author Bayu Anggorojati [ba@es.aau.dk]
 * Center for TeleInFrastruktur, Aalborg University, Denmark
 *
 */
public class SecureGWCommService implements ISecGWCommService {
	/** The Logger */
	Logger log = Logger.getLogger("betaas.taas.securitymanager");
	
	/** Reference to Bundle Context from blueprint */
	private BundleContext context;
	
	/** Reference to Ecmqv Internal service from blueprint */
	private IGatewayEcmqvIntService gwEcmqvIntService;
	
	/** Reference to GWCertificateService from blueprint */
	private IGatewayCertificateService gwCertificateService;
	
	/** Tracker of GWEcmqvExtService */
	private ServiceTracker ecmqvExtTracker;
	
	public SecureGWCommService(){
//		this.coreActivator = coreActivator;
//		this.context = coreActivator.getContext();
	}
	
	public boolean deriveSharedKeys(String gwDestId) throws Exception {
		Security.addProvider(new BouncyCastleProvider());
		boolean sendLast = false;
		
		log.info("Start deriving shared keys");

		BcCredential myCertStore = null;

		// generate an ephemeral KeyPair
		AsymmetricCipherKeyPair myEphKp = gwEcmqvIntService.
				generateEphemeralKeyPair();
			
		// initiate the ECMQV procedure...
		// first load the credential and then the certificate of this GW
		myCertStore = gwCertificateService.loadMyCertificate(PKCS12Utils.GW_CERT);				
		X509CertificateHolder myCert = null;
		if(myCertStore != null){
			myCert = (X509CertificateHolder) myCertStore.getCertificateChain()[0];
		}
		// then, invoke the initEcmqv method to other GW, by retrieving the 
		// external ECMQV service first
		EcmqvMessage eMsg = null;
		IGatewayEcmqvExtService ecmqvExtServ = null;
			
		// initializing service tracker of GWEcmqvExtService
		ecmqvExtTracker = new ServiceTracker(
				context, IGatewayEcmqvExtService.class.getName(), null);
		ecmqvExtTracker.open();
		
		// give time to the tracker to find CertificateExtService
		Thread.sleep(2500);
		
		ServiceReference[] refs = ecmqvExtTracker.getServiceReferences();
		// iterating through the service references
		for(ServiceReference ref : refs){
			// check if gatewayId of remote GW equals to gwDestId of this GW
			if(ref.getProperty("gwId").equals(gwDestId)){
				log.debug("Found ExtEcmqv service of the GW destination ID");
				ecmqvExtServ = (IGatewayEcmqvExtService) context.getService(ref);
			}
		}
			
		if(ecmqvExtServ != null){
			// the actual invocation of initEcmqv 
			eMsg = ecmqvExtServ.initEcmqv(
					((ECPublicKey)myEphKp.getPublic()).getW().getAffineX().toByteArray(),	// the X-coordinate of EC public key param. 
					((ECPublicKey)myEphKp.getPublic()).getW().getAffineY().toByteArray(), // the Y-coordinate of EC public key param.
					myCert.getEncoded());
		}
			
		// upon receiving the eMsg, verify it and calculate the MAC 3
		byte[] mac3 = null;
		if(eMsg != null){
			mac3 = gwEcmqvIntService.responseEcmqv(eMsg);
		}
			
		// upon successful verification of eMsg which results in MAC 3, send MAC 3
		// to the other GW
			
		if(mac3!=null && ecmqvExtServ!=null){
			sendLast = ecmqvExtServ.lastEcmqv(mac3);
			log.info("the MAC 3 is correctly confirmed");
		}
		
		// closing the service tracker
		ecmqvExtTracker.close();
			
		return sendLast;
	}

	/**
	 * Blueprint set reference to BundleContext
	 * @param context
	 */
	public void setContext(BundleContext context) {
		this.context = context;
		log.debug("Got the BundleContext...");
	}

	/**
	 * Blueprint set reference to IGatewayEcmqvIntService
	 * @param gwEcmqvIntService
	 */
	public void setGwEcmqvIntService(IGatewayEcmqvIntService gwEcmqvIntService) {
		this.gwEcmqvIntService = gwEcmqvIntService;
		log.debug("Got the GWEcmqvIntService...");
	}

	/**
	 * Blueprint set reference to IGatewayCertificateService
	 * @param gwCertificateService
	 */
	public void setGwCertificateService(
			IGatewayCertificateService gwCertificateService) {
		this.gwCertificateService = gwCertificateService;
		log.debug("Got the GWCertificateService...");
	}
}
