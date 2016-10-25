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

import org.apache.log4j.Logger;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

import eu.betaas.taas.securitymanager.certificate.service.IGatewayCertificateService;
import eu.betaas.taas.securitymanager.certificate.service.IGatewayStarCertificateExtService;
import eu.betaas.taas.securitymanager.common.ec.ECKeyPairGen;
import eu.betaas.taas.securitymanager.common.model.ArrayOfCertificate;
//import eu.betaas.taas.securitymanager.core.activator.SecMTaasCoreActivator;
import eu.betaas.taas.securitymanager.core.service.IJoinInstanceService;
import eu.betaas.taas.securitymanager.core.utils.CoreBetaasBus;

/**
 * Class implementation of IJoinInstanceService interface
 * 
 * @author Bayu Anggorojati [ba@es.aau.dk]
 * Center for TeleInFrastruktur, Aalborg University, Denmark
 *
 */
public class JoinInstanceService implements IJoinInstanceService {
	
	Logger log = Logger.getLogger("betaas.taas.securitymanager");
	
	/** Bundle Context used in the Activator class */
	private BundleContext context;
	
	/** Reference to GWCertificateService */
	private IGatewayCertificateService gwCertificateService;
	
	/** ServiceTracker of GWStarCertificateExtService... */
	private ServiceTracker extCertTracker;
	
	/** Class that handles BETaaS BUS in authentication bundle */
	private CoreBetaasBus bus;
		
	/**
	 * Initial setup method to initialize betaas bus service
	 */
	public void setup(){
		// set the GW ID
		bus = new CoreBetaasBus(context);
	}
	
	public JoinInstanceService(){}
	
	public boolean requestGwCertificate(String countryCode, String state, 
			String location, String orgName, String gwId) throws Exception {
		Security.addProvider(new BouncyCastleProvider());
		
		boolean ok = false;
		log.info("Start the request certificate instance...");
		bus.sendData("Start the request certificate instance", "info", "SecM");
		
		ArrayOfCertificate certsArray = null;
			
		// initiate a CertificationRequest message
		X500NameBuilder x500NameBld = new X500NameBuilder(BCStyle.INSTANCE);
		x500NameBld.addRDN(BCStyle.C, countryCode);
		x500NameBld.addRDN(BCStyle.ST, state);
		x500NameBld.addRDN(BCStyle.L, location);
		x500NameBld.addRDN(BCStyle.O, orgName);
		x500NameBld.addRDN(BCStyle.CN, "BETaaS Gateway Certificate");
			
		X500Name subject = x500NameBld.build();
			
		AsymmetricCipherKeyPair kp = ECKeyPairGen.generateECKeyPair192();
//		log.info("intServ: "+intServ.toString());
		// get the certification request message
		PKCS10CertificationRequest gwCertReq = gwCertificateService.
				buildCertificationRequest(subject, kp, gwId);
		log.info("Successfully generate PKCS10CertificationRequest!!");
		bus.sendData("Successfully generate PKCS10CertificationRequest", "info", 
				"SecM");
		
		// get the GW* external cert. service via ServiceTracker
		IGatewayStarCertificateExtService extServ = null;
		
		extCertTracker = new ServiceTracker(
				context, IGatewayStarCertificateExtService.class.getName(), null);
		extCertTracker.open();
		
		// give time to the tracker to find CertificateExtService
		Thread.sleep(2500);
		
		ServiceReference[] refs = extCertTracker.getServiceReferences();
		
		// iterating through the service references
		for(ServiceReference ref : refs){
			log.debug("GW ID: "+ref.getProperty("gwId"));
			log.debug("Is it GW*: "+((IGatewayStarCertificateExtService) context.getService(ref)).isGWStar());
			// check if the gatewayId of remote GW equals gwStar
			if(((IGatewayStarCertificateExtService) context.getService(ref)).isGWStar()){
					log.debug("Found the ExtCert service of GW*");
					bus.sendData("Found the ExtCert service of GW*", "debug", "SecM");
					extServ = (IGatewayStarCertificateExtService) context.getService(ref);
					break;
			}
		}
			
		// send a request to issue a certificate for me (this GW) to GW* 
		if(gwCertReq!=null && extServ!= null){
			certsArray = extServ.issueGwCertificate(gwCertReq.getEncoded());
			
			X509CertificateHolder[] certs = 
					new X509CertificateHolder[certsArray.getCertificate().size()];
			
			// decode the received array of certificates (consists of intermediate 
			// and my own certificates) from array byte[] to X509Certificate
			for(int i = 0; i<certsArray.getCertificate().size();i++){
				byte[] cert = certsArray.getCertificate().get(i);
				certs[i] = new X509CertificateHolder(cert);
			}
			
			log.debug("Start storing the newly created certificate from GW*...");
			bus.sendData("Start storing the newly created certificate from GW*", 
					"debug", "SecM");
			// now store the certificates in a .p12 file
			gwCertificateService.storeMyCertificate(kp.getPrivate(), certs);
			ok = true;
			
			log.info("Successfully requesting certificate from GW* and store it");
			bus.sendData("Successfully requesting certificate from GW* and store it", 
					"info", "SecM");
			
			// closing the service tracker
			extCertTracker.close();
		}
		
		return ok;
	}
	
	/**
	 * Blueprint set reference to BundleContext
	 * @param context BundleContext
	 */
	public void setContext(BundleContext context) {
		this.context = context;
		log.info("Got BundleContext from the blueprint...");
	}

	/**
	 * Blueprint set reference to IGatewayCertificateService 
	 * @param gwCertificateService
	 */
	public void setGwCertificateService(
			IGatewayCertificateService gwCertificateService) {
		this.gwCertificateService = gwCertificateService;
	}
}
