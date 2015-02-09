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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;

import org.apache.log4j.Logger;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.util.PublicKeyFactory;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

import eu.betaas.taas.securitymanager.certificate.service.IGatewayStarCertificateExtService;
import eu.betaas.taas.securitymanager.common.certificate.utils.Config;
import eu.betaas.taas.securitymanager.common.certificate.utils.GWCertificateUtilsBc;
import eu.betaas.taas.securitymanager.common.model.ArrayOfCertificate;

/**
 * This class implements methods to create a set of certificates and credential 
 * for GW that joining the BETaaS instance. So basically the invocation is done
 * by an external GW, and it is assumed that this service resides in the GW*
 * when it is being invoked. 
 * 
 * @author Bayu Anggorojati [ba@es.aau.dk]
 * Center for TeleInFrastruktur, Aalborg University, Denmark
 *
 */
public class GWStarCertificateExtService implements
		IGatewayStarCertificateExtService {

	private Logger log = Logger.getLogger("betaas.taas.securitymanager");
	
	/**  This GW ID */
	private String mGwId;
	
	/** The GWstarCertificateExtService tracker */
	private ServiceTracker extCertTracker;
	
	/** Bundle Context of this bundle */
	private BundleContext context;
	
	/** Reference to InstanceManagerExternalIF from Blueprint */
//	private InstanceManagerExternalIF instanceManagerIF;
	
	/** Certificate path -- from Blueprint */
	private String certPath;
	
	public GWStarCertificateExtService(){}	
	
	/**
	 * Initial setup method to determine whether this GW is a GW* or not
	 */
	public void setup(){
		// set the GW ID
		Config.gwId = this.mGwId;
		
//		log.debug("Tracking the External Certificate Manager service...");
//		extCertTracker = new ServiceTracker(context, 
//				IGatewayStarCertificateExtService.class.getName(), null);
//		extCertTracker.open();
//		
//		// wait for the tracker to gather info on the available extCertTracker services
//		try {
//			Thread.sleep(2500);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		ServiceReference[] refs = extCertTracker.getServiceReferences();
//		
//		// Check other external certificate manager, if one or more already exists,
//		// it means that this is not GW*, else this is a GW*
//		if(refs!=null && refs.length > 0){
//			log.debug("Other certificate manager(s) is found...");
//			// check whether it is this GW or not
//			for(ServiceReference ref : refs){
//				// compare the GW ID of the found GW with this own ID
//				if(!ref.getProperty("gwId").equals(mGwId)){
//					this.isGWStar = false;
//					break;
//				}
//				else
//					this.isGWStar = true;
//			}
//			//this.isGWStar = false;
//		}
//		else{
//			log.debug("Found no other certificate manager(s)...");
//			this.isGWStar = true;
//		}
//		log.debug("This GW is GW Star: "+ this.isGWStar);
//		Config.isGwStar = this.isGWStar;
//		Config.gwId = this.mGwId;
	}
	
	public byte[][] issueGwCertByte(byte[] gwCertReq){
		ArrayOfCertificate certsArray = issueGwCertificate(gwCertReq);
		
		byte[][] certs = 
				new byte[certsArray.getCertificate().size()][];
		
		for(int i=0;i<certsArray.getCertificate().size();i++){
			certs[i] = certsArray.getCertificate().get(i); 
		}
		
		return certs;
	}
	
	public ArrayOfCertificate issueGwCertificate(byte[] gwCertReq) {
		log.info("Start creating a certificate for new joining GW...");
		// decode the gwCertReq back to PKCS10CertificationRequest
		PKCS10CertificationRequest certReq = null;
		try {
			certReq = new PKCS10CertificationRequest(gwCertReq);
		} catch (IOException e) {
			log.error("Error decoding the PKCS10CertificationRequest: "+e.getMessage());
			e.printStackTrace();
		}
		// retrieve the public key of the requesting GW
		SubjectPublicKeyInfo subPubKeyInfo = certReq.getSubjectPublicKeyInfo();
		ECPublicKeyParameters ecKeyParams = null;
		try {
			ecKeyParams = (ECPublicKeyParameters)PublicKeyFactory.createKey(subPubKeyInfo);
		} catch (IOException e) {
			log.error("Error creating ECPublicKeyParameters from SubjectPublicKeyInfo: "
					+e.getMessage());
			e.printStackTrace();
		}
		String ufn = null;
		// parsing the UFN from the PKCS10CertificationRequest object
		ASN1Encodable[] ext = certReq.toASN1Structure()
				.getCertificationRequestInfo().getAttributes().toArray();
		for(int i=0;i<ext.length;i++){
			Enumeration en1 = ((DERSequence) ext[i]).getObjects();
			while(en1.hasMoreElements()){
				Object den1 = en1.nextElement();
				if(den1 instanceof DERSet){
					Enumeration en2 = ((DERSet) den1).getObjects();
					while(en2.hasMoreElements()){
						Object den2 = en2.nextElement();
						if(den2 instanceof DERSequence){
							Enumeration en3 = ((DERSequence) den2).getObjects();
							while(en3.hasMoreElements()){
								Object den3 = en3.nextElement();
								if(den3 instanceof DERSequence){
									Enumeration en4 = ((DERSequence) den3).getObjects();
									while(en4.hasMoreElements()){
										Object den4 = en4.nextElement();
										if(den4 instanceof DEROctetString){
											byte[] octets = ((DEROctetString) den4).getOctets();
											byte[] ocs = new byte[octets.length-4];
											for(int j =0;j<ocs.length;j++){
												ocs[j] = octets[j+4];
											}
											InputStream is = new ByteArrayInputStream(ocs);
											ufn = getStringFromInputStream(is);
										}
									}
								}
							}
						}
					}
				}
			}
		}
		
		ArrayOfCertificate certs = null;
		try {
			certs = GWCertificateUtilsBc.createGwCredentials(certReq.getSubject(),
					"intermediate", "end", ecKeyParams, ufn, certPath);
		} catch (Exception e) {
			log.error("Error generating Certificate for GW: "+e.getMessage());
			e.printStackTrace();
		}	
		
		log.info("Certificate for new joining GW has been created...");
		
		return certs;
	}
	
	private static String getStringFromInputStream(InputStream is) {
		BufferedReader br = null;
		StringBuilder sb = new StringBuilder();

		String line;
		try {

			br = new BufferedReader(new InputStreamReader(is));
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return sb.toString();
	}
	
	/**
	 * Method to close this bundle referred in Blueprint 
	 */
	public void close(){
		log.info("Closing certificate bundle...");
		// closing the tracker if they have been opened
		if(extCertTracker != null)
			extCertTracker.close();
	}
	
	/**
	 * Blueprint set reference to BundleContext
	 * @param context BundleContext
	 */
	public void setContext(BundleContext context) {
		this.context = context;
		log.debug("Got BundleContext from the blueprint...");
	}
	
	/**
	 * Blueprint set reference to GW ID
	 * @param gwId
	 */
	public void setGwId(String gwId){
		this.mGwId = gwId;
	}
	
	/**
	 * Blueprint set reference to certificatePath
	 * @param certificatePath
	 */
	public void setCertificatePath(String certificatePath){
		this.certPath = certificatePath;
	}
	
//	/**
//	 * Blueprint set reference to InstanceManagerExternalIF
//	 * @param imExIF
//	 */
//	public void setInstanceManagerIF(InstanceManagerExternalIF instanceManagerIF){
//		this.instanceManagerIF = instanceManagerIF;
//	}
	
	/**
	 * Getter method of this GW identifier
	 * @return this GW identifier
	 */
	public String getGwId(){
		return this.mGwId;
	}

	public boolean isGWStar() {
		// TODO Auto-generated method stub
		return Config.isGwStar;
	}

}
