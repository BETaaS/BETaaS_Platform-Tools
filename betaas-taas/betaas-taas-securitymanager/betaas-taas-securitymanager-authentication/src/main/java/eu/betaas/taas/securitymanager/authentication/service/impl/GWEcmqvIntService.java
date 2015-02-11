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

@author Bayu Anggorojati [ba@es.aau.dk]
Center for TeleInFrastruktur, Aalborg University, Denmark
 */

package eu.betaas.taas.securitymanager.authentication.service.impl;

import java.math.BigInteger;

import org.apache.log4j.Logger;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.util.PublicKeyFactory;
import org.bouncycastle.operator.DefaultDigestAlgorithmIdentifierFinder;
//import org.osgi.util.tracker.ServiceTracker;

//import eu.betaas.taas.securitymanager.authentication.activator.ExtAuthenticationActivator;
import eu.betaas.taas.securitymanager.authentication.service.IGatewayEcmqvIntService;
import eu.betaas.taas.securitymanager.certificate.service.IGatewayCertificateService;
import eu.betaas.taas.securitymanager.common.certificate.utils.PKCS12Utils;
import eu.betaas.taas.securitymanager.common.ec.ECKeyPairGen;
import eu.betaas.taas.securitymanager.common.ec.operator.BcECDSAContentVerifierProviderBuilder;
import eu.betaas.taas.securitymanager.common.model.BcCredential;
import eu.betaas.taas.securitymanager.common.mqv.ECMQVUtils;
import eu.betaas.taas.securitymanager.common.mqv.EcmqvMessage;

/**
 * Implementation class of the IGatewayEcmqvIntService interface
 * 
 * @author Bayu Anggorojati [ba@es.aau.dk]
 * Center for TeleInFrastruktur, Aalborg University, Denmark
 *
 */
public class GWEcmqvIntService implements IGatewayEcmqvIntService {
	/** Logger */
	private Logger log = Logger.getLogger("betaas.taas.securitymanager");
	
	/** Reference to GWCertificateService from blueprint */
	private IGatewayCertificateService gwCertificateService;
	
	/**	My Certificate */
	private X509CertificateHolder myCert;	
//	private ECPublicKey statPub;		// my static/long term public key
	/** My static or long term private key */
	private ECPrivateKeyParameters statPriv;	
	/** My generated ephemeral public key */
	private ECPublicKeyParameters myEphPub;
	/** my generated ephemeral private key */
	private ECPrivateKeyParameters myEphPriv;
	/** ephemeral public key of the other GW */
	private ECPublicKeyParameters ephPub;
	/** shared key 1 (it is used to encrypt the MAC 2) */
	private byte[] k1;
	/** shared key 2 (the session key) */
	private byte[] k2;		
	/** My User Friendly Name, taken from my certificate */
	private String myUFN;
	/** other GW's UFN, derived from the submitted certificate */
	private String ufn;
	
	public GWEcmqvIntService(){
//		this.authActivator = activator;
	}
	
	private boolean validateCert(X509CertificateHolder cert) throws 
			Exception{		
		// first load the KeyStore that contains certificates from GW* as well as 
		// my own certificate issued by GW*
		// refer to GWCertificateService directly from blueprint
		BcCredential myCredential = gwCertificateService.loadMyCertificate(
				PKCS12Utils.GW_CERT);
		// get the public key of intermediate certificate from GW*
		AsymmetricKeyParameter verKey = ECKeyPairGen.generateECPublicKey(
				myCredential.getCertificateChain()[1].getSubjectPublicKeyInfo());
			
		// get my own certificate
		myCert = (X509CertificateHolder)myCredential.getCertificateChain()[0];
			
		// get the static/long term public and private key
//			statPub = (ECPublicKey) myCert.getPublicKey();
		statPriv = (ECPrivateKeyParameters) myCredential.getPrivateKey();
			
		if(cert.isSignatureValid(new BcECDSAContentVerifierProviderBuilder(new 
				DefaultDigestAlgorithmIdentifierFinder()).build(verKey)))
			return true;
		
		return false;
	}
	
	/**
	 * A method to verify MAC 2 which is received after invoking the initEcmqv 
	 * @param mac2: the received MAC 2
	 * @param ufnA: the user friendly name of the other GW
	 * @param ufnB: the user friendly name of this GW
	 * @param ephPubA: the ephemeral public key of the other GW
	 * @param ephPubB: the ephemeral public key of this GW
	 * @param k1: one of the calculated shared key
	 * @return true if the calculated MAC 2 matches with the received MAC 2
	 */
	private boolean verifyMac2(byte[] mac2, String ufnA, String ufnB, 
			ECPublicKeyParameters ephPubA, ECPublicKeyParameters ephPubB, byte[] k1){
		
		byte[] calcMac2 = ECMQVUtils.computeMAC("2", ufnA, ufnB, 
				ephPubA.getQ().getEncoded(), ephPubB.getQ().getEncoded(), k1);
		
		String calcMac2Str = "";
		for(byte b : calcMac2)
			calcMac2Str = calcMac2Str + Integer.toHexString(0xFF & b);
		
		log.debug("The calculated MAC (2):\n"+calcMac2Str);
		
		String mac2Str = "";
		for(byte b : mac2)
			mac2Str = mac2Str + Integer.toHexString(0xFF & b);
		
		if(!mac2Str.equals(calcMac2Str))
			return false;
		
		return true;
	}
	
	public AsymmetricCipherKeyPair generateEphemeralKeyPair() throws Exception {
		AsymmetricCipherKeyPair myEphKp = ECKeyPairGen.generateECKeyPair192();
		myEphPub = (ECPublicKeyParameters)myEphKp.getPublic();
		myEphPriv = (ECPrivateKeyParameters)myEphKp.getPrivate();
		
		return myEphKp;
	}

	public byte[] responseEcmqv(EcmqvMessage eMsg) throws Exception{
		// decode the certificate
		X509CertificateHolder cert = new X509CertificateHolder(eMsg.getMyCertificate()); 
		
		// decode the ECPublicKey
		ECPublicKeyParameters ephPub = ECKeyPairGen.generateECPublicKey192(
				new BigInteger(eMsg.getEphemeralPublicX()), 
				new BigInteger(eMsg.getEphemeralPublicY()));
		// get the MAC 2
		byte[] mac2 = eMsg.getMyMac();
		
		// validate the certificate
		boolean isCertValid = false;
		isCertValid = validateCert(cert);
			
		if(!isCertValid){
			log.error("The submitted certificate is not valid!!");
			return null;
		}
		log.debug("Passed the certificate validation!!");
			
		// perform embedded public key validation
		boolean pubValid = ECMQVUtils.validateEmbedPubKey(ephPub);
		if(!pubValid){
			log.error("The submitted ephemeral public key is not valid!!");
			return null;
		}
		log.debug("Passed the embedded ephemeral public key validation!!");
		// set the ephPub with this received ephPub
		this.ephPub = ephPub;
		
		// now, no need to generate my own ephemeral key here, because it is done
		// compute the implicit signature
		BigInteger implSig = ECMQVUtils.computeImplicitSig(myEphPub, myEphPriv, 
				statPriv);
		
		// calculates the shared key K
		ECPublicKeyParameters statPub = (ECPublicKeyParameters) 
				PublicKeyFactory.createKey(cert.getSubjectPublicKeyInfo());
		org.bouncycastle.math.ec.ECPoint K = ECMQVUtils.calculateSharedKey(
				this.ephPub, statPub, this.ephPub.getParameters().getH(), implSig);
		
		// derive 2 symmetric keys from the shared key K
		byte[] Kx = K.normalize().getXCoord().toBigInteger().toByteArray();
		int Lx = K.normalize().getXCoord().toBigInteger().bitLength();
		double x = Math.log(Lx)/Math.log(2.0);
		double L = Math.pow(2, 1+ Math.ceil(x));
		
		byte[] deriveK = ECMQVUtils.deriveKeyHKDF(Kx, (int)L/8);
		
		// k1 and k2 split from newKey --> k1: to be MACed, k2: the session key
		k1 = new byte[deriveK.length/2];
		k2 = new byte[deriveK.length/2];
		int c = 0;
		for(byte b : deriveK){
			 if(c<deriveK.length/2){
				 k1[c] = b;
			 }
			 else{
				 k2[c-deriveK.length/2]=b;
			 }
			 c++;
		}
		
		// retrieving my user friendly name from the SubjectAlternativeNames in my 
		// certificate
		Extensions myExs = myCert.getExtensions();
		if(myExs!=null){
			GeneralNames gns = GeneralNames.fromExtensions(myExs, 
					Extension.subjectAlternativeName);
			for(int i=0;i<gns.getNames().length;i++){
				myUFN = gns.getNames()[i].getName().toString();
			}
		}
		
		// retrieving other GW user friendly name from the SubjectAlternativeNames 
		// in the submitted certificate
		Extensions oExs = cert.getExtensions();
		if(oExs!=null){
			GeneralNames gns = GeneralNames.fromExtensions(oExs, 
					Extension.subjectAlternativeName);
			for(int i=0;i<gns.getNames().length;i++){
				ufn = gns.getNames()[i].getName().toString();
			}
		}
		
		// validate MAC 2, which is received from other GW
		boolean isMac2Valid = verifyMac2(mac2,ufn,myUFN,this.ephPub,myEphPub,k1); 
		
		// compute the MAC to be sent to the other gateway
		if(!isMac2Valid){
			log.error("Fails to verify the received MAC (2)!!");
			return null;
		}
		log.debug("Successfully verifies the received MAC (2)!!");
		
		byte[] mac3 = ECMQVUtils.computeMAC("3", myUFN, ufn, 
				myEphPub.getQ().getEncoded(), ephPub.getQ().getEncoded(), k1);
		
		return mac3;
	}

	/**
	 * blueprint set reference to GWCertificateService
	 * @param gwCertificateService
	 */
	public void setGwCertificateService(
			IGatewayCertificateService gwCertificateService) {
		this.gwCertificateService = gwCertificateService;
		log.debug("Got GWCertificateService from blueprint...");
	}

}
