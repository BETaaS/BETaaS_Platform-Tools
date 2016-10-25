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

import org.apache.log4j.Logger;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.osgi.framework.BundleContext;
//import org.osgi.util.tracker.ServiceTracker;

import eu.betaas.taas.securitymanager.certificate.service.IGatewayStarCertificateIntService;
//import eu.betaas.taas.securitymanager.core.activator.SecMTaasCoreActivator;
import eu.betaas.taas.securitymanager.core.service.IInitGWStarService;
import eu.betaas.taas.securitymanager.core.utils.CoreBetaasBus;

/**
 * Class implementation of IInitGWStarService interface
 * 
 * @author Bayu Anggorojati [ba@es.aau.dk]
 * Center for TeleInFrastruktur, Aalborg University, Denmark
 *
 */
public class InitGWStarService implements IInitGWStarService {
	Logger log = Logger.getLogger("betaas.taas.securitymanager");	
	
	/** Reference to GWStarCertificateIntService */
	private IGatewayStarCertificateIntService gwStarCertIntService;
	
	/** Class that handles BETaaS BUS in authentication bundle */
	private CoreBetaasBus bus;
	
	/** Reference to Blueprint BundleContext */
	private BundleContext context;
	
	/**
	 * Initial setup method to initialize betaas bus service
	 */
	public void setup(){
		// set the GW ID
		bus = new CoreBetaasBus(context);
	}
	
	public InitGWStarService(){}
	
	public void initGwStar(String countryCode, String state, String location, 
			String orgName, String gwId) {

			// subject root
			X500NameBuilder subjRootBld = new X500NameBuilder(BCStyle.INSTANCE);
			subjRootBld.addRDN(BCStyle.C, countryCode);
			subjRootBld.addRDN(BCStyle.ST, state);
			subjRootBld.addRDN(BCStyle.L, location);
			subjRootBld.addRDN(BCStyle.O, orgName);
			subjRootBld.addRDN(BCStyle.CN, "BETaaS Instance Root Certificate");
			
			X500Name subjRoot = subjRootBld.build();
			
			X500NameBuilder subjInterBld = new X500NameBuilder(BCStyle.INSTANCE);
			subjInterBld.addRDN(BCStyle.C, countryCode);
			subjInterBld.addRDN(BCStyle.ST, state);
			subjInterBld.addRDN(BCStyle.L, location);
			subjInterBld.addRDN(BCStyle.O, orgName);
			subjInterBld.addRDN(BCStyle.CN, "BETaaS Instance CA Certificate");
			
			X500Name subjInter = subjInterBld.build();
			
			X500NameBuilder subjEndBld = new X500NameBuilder(BCStyle.INSTANCE);
			subjEndBld.addRDN(BCStyle.C, countryCode);
			subjEndBld.addRDN(BCStyle.ST, state);
			subjEndBld.addRDN(BCStyle.L, location);
			subjEndBld.addRDN(BCStyle.O, orgName);
			subjEndBld.addRDN(BCStyle.CN, "BETaaS Gateway Certificate");
			
			X500Name subjEnd = subjEndBld.build();
			
			log.info("Start initiating GW* certificate now!!");
			bus.sendData("Start initiating GW* certificate now!!", "info", "SecM");
			gwStarCertIntService.createGwStarCredentials(
					subjRoot, subjInter, subjEnd, gwId);
	}

	/**
	 * Blueprint set reference to IGatewayStarCertificateIntService
	 * @param gwStarCertIntService
	 */
	public void setGwStarCertIntService(
			IGatewayStarCertificateIntService gwStarCertIntService) {
		this.gwStarCertIntService = gwStarCertIntService;
		log.debug("Got the GWStarCertificateIntService...");
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
