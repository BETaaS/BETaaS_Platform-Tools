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
import java.util.Date;

import org.apache.log4j.Logger;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

import eu.betaas.taas.securitymanager.authentication.service.IEncryptDecryptService;
import eu.betaas.taas.securitymanager.authentication.service.IGatewayEcmqvExtService;
import eu.betaas.taas.securitymanager.authentication.service.IGatewayEcmqvIntService;

import eu.betaas.taas.securitymanager.certificate.service.IGatewayCertificateService;
import eu.betaas.taas.securitymanager.common.certificate.utils.PKCS12Utils;
import eu.betaas.taas.securitymanager.common.model.BcCredential;
import eu.betaas.taas.securitymanager.common.mqv.EcmqvMessage;
//import eu.betaas.taas.securitymanager.core.activator.SecMTaasCoreActivator;
import eu.betaas.taas.securitymanager.core.service.ISecGWCommService;
import eu.betaas.taas.securitymanager.core.utils.CoreBetaasBus;

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
	
	/** Reference to EncryptDecryptService from blueprint */
	private IEncryptDecryptService encryptService;
	
	/** Tracker of GWEcmqvExtService */
	private ServiceTracker ecmqvExtTracker;
	
	/**  This GW ID */
	private String mGwId;
	
	/** Class that handles BETaaS BUS in authentication bundle */
	private CoreBetaasBus bus;
		
	/**
	 * Initial setup method to initialize betaas bus service
	 */
	public void setup(){
		// set the GW ID
		bus = new CoreBetaasBus(context);
	}
	
	public SecureGWCommService(){
//		this.coreActivator = coreActivator;
//		this.context = coreActivator.getContext();
	}
	
	public boolean deriveSharedKeys(String gwDestId) throws Exception {
		Security.addProvider(new BouncyCastleProvider());
		boolean result = false;
		
		log.info("Start deriving shared keys");
		bus.sendData("Start deriving shared keys", "info", "SecM");

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
				bus.sendData("Found ExtEcmqv service of the GW destination ID", "debug", 
						"SecM");
				ecmqvExtServ = (IGatewayEcmqvExtService) context.getService(ref);
			}
		}
			
		if(ecmqvExtServ != null){
			// the actual invocation of initEcmqv 
			eMsg = ecmqvExtServ.initEcmqv(
					((ECPublicKeyParameters)myEphKp.getPublic()).getQ().normalize().getXCoord().toBigInteger().toByteArray(),	// the X-coordinate of EC public key param. 
					((ECPublicKeyParameters)myEphKp.getPublic()).getQ().normalize().getYCoord().toBigInteger().toByteArray(), // the Y-coordinate of EC public key param.
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
			long sendLast = ecmqvExtServ.lastEcmqv(mac3, mGwId);
			// set the expire time and k2 at the catalog of the internal interface 
			gwEcmqvIntService.setKeyAndExpireTime(gwDestId, sendLast);
			// closing the service tracker
			ecmqvExtTracker.close();
			if(sendLast >= 0){
				log.info("the MAC 3 is correctly confirmed");
				bus.sendData("the MAC 3 is correctly confirmed", "info", "SecM");
				result = true;
			}
			else{
				log.info("the MAC 3 isn't valid!!");
				bus.sendData("the MAC 3 isn't valid", "warning", "SecM");
			}
		}
		return result;
	}
	
	public String doEncryptData(String gwDestId, String data){
		//check if the key associated with gwDestId exists and is not expired
		boolean isK2 = false;
		byte[] k2 = gwEcmqvIntService.getK2(gwDestId);
		
		if(k2==null){
			log.debug("k2 is not found, need to initiate key agreement protocol.");
			// initiate ECMQV protocol
			try {
				isK2 = deriveSharedKeys(gwDestId);
				if(isK2)
					k2 = gwEcmqvIntService.getK2(gwDestId);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				log.error("Exception in ECMQV key agreement protocol: "+e.getMessage());
				bus.sendData("Exception in ECMQV key agreement protocol", "error", 
						"SecM");
				return null;
			}
		}
		else{
			// if the key has been expired
			if((new Date()).getTime() >= gwEcmqvIntService.getExpireTime(gwDestId)){
				log.debug("k2 is expired, need to initiate key agreement protocol.");
				// again initiate ECMQV protocol
				try {
					isK2 = deriveSharedKeys(gwDestId);
					if(isK2)
						k2 = gwEcmqvIntService.getK2(gwDestId);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else
				isK2 = true;
		}
		// start the encryption
		log.debug("start encrypting data...");
		log.debug("isK2: "+isK2);
		if(isK2)
			return encryptService.doEncryption(k2, data);
		else{
			log.warn("There is problem with secret key (k2) associated with GW ID: "
					+gwDestId);
			bus.sendData("There is problem with secret key (k2) associated with GW "+
					"ID: " +gwDestId, "warning", "SecM");
			return null;
		}
	}
	
	public String doDecryptData(String gwOriId, String encrypted){
		//check if the key associated with gwDestId exists and is not expired
		boolean isK2 = false;
		byte[] k2 = gwEcmqvIntService.getK2(gwOriId);
			
		// we can assume that k2 exists since the received encrypted message uses k2
		// now just check if k2 is not expired
		if((new Date()).getTime() >= gwEcmqvIntService.getExpireTime(gwOriId)){
			// initiate ECMQV protocol
			try {
				isK2 = deriveSharedKeys(gwOriId);
				if(isK2)
					k2 = gwEcmqvIntService.getK2(gwOriId);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else{
			isK2 = true;
		}
		// start the decryption
		if(isK2)
			return encryptService.doDecryption(k2, encrypted);
		else{
			log.warn("There is problem with secret key (k2) associated with GW ID: "
					+ gwOriId);
			bus.sendData("There is problem with secret key (k2) associated with GW "+
					"ID: " +gwOriId, "warning", "SecM");
			return null;
		}
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
	
	public void setEncryptDecryptService(
			IEncryptDecryptService encryptDecryptService){
		this.encryptService = encryptDecryptService;
		log.debug("Got the EncryptDecryptService...");
	}

	/**
	 * Blueprint set reference to gwId of this GW
	 * @param gwId
	 */
	public void setGwId(String gwId) {
		this.mGwId = gwId;
	}
}
