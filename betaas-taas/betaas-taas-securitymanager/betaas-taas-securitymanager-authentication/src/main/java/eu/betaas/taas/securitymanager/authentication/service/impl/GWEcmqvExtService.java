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

package eu.betaas.taas.securitymanager.authentication.service.impl;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Date;

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
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.operator.DefaultDigestAlgorithmIdentifierFinder;

import eu.betaas.taas.securitymanager.authentication.catalog.ExpireKeyGwCatalog;
import eu.betaas.taas.securitymanager.authentication.catalog.KeyGwCatalog;
import eu.betaas.taas.securitymanager.authentication.service.IGatewayEcmqvExtService;
import eu.betaas.taas.securitymanager.certificate.service.IGatewayCertificateService;
import eu.betaas.taas.securitymanager.common.certificate.utils.PKCS12Utils;
import eu.betaas.taas.securitymanager.common.ec.ECKeyPairGen;
import eu.betaas.taas.securitymanager.common.ec.operator.BcECDSAContentVerifierProviderBuilder;
import eu.betaas.taas.securitymanager.common.model.BcCredential;
import eu.betaas.taas.securitymanager.common.mqv.*;

/**
 * Implementation class of the IGatewayEcmqvExtService interface
 * 
 * @author Bayu Anggorojati [ba@es.aau.dk]
 * Center for TeleInFrastruktur, Aalborg University, Denmark
 *
 */
public class GWEcmqvExtService implements IGatewayEcmqvExtService {
	/** Logger */
	private Logger log = Logger.getLogger("betaas.taas.securitymanager");
	
	private IGatewayCertificateService gwCertificateService;
	
	private static final long VALID_PERIOD = 10 * 60 * 1000; // 10 minutes 
	
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
	
	/** Empty Constructor */
	public GWEcmqvExtService(){}
	
	private boolean validateCert(X509CertificateHolder cert) throws Exception {
//		ServiceTracker certTracker = authActivator.getCertTracker();
		
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
//		statPub = (ECPublicKey) myCert.getPublicKey();
		statPriv = (ECPrivateKeyParameters) myCredential.getPrivateKey();
		
		if(cert.isSignatureValid(new BcECDSAContentVerifierProviderBuilder(new 
				DefaultDigestAlgorithmIdentifierFinder()).build(verKey)))
			return true;
		
		return false;
	}
	
	/**
	 * A method to verify MAC 3 which is received after the responseEcmqv
	 * @param mac3: the received MAC 3
	 * @param ufn: the user friendly name of the other GW
	 * @return true if the calculated MAC 3 matches with the received MAC 3
	 */
	public boolean verifyMac3(byte[] mac3){
		
		byte[] calcMac3 = ECMQVUtils.computeMAC("3", ufn, myUFN, 
				ephPub.getQ().getEncoded(), myEphPub.getQ().getEncoded(), k1);
		
		String calcMac3Str = "";
		for(byte b : calcMac3)
			calcMac3Str = calcMac3Str + Integer.toHexString(0xFF & b);
		
		log.debug("The calculated MAC (3):\n"+calcMac3Str);
		
		String mac3Str = "";
		for(byte b : mac3)
			mac3Str = mac3Str + Integer.toHexString(0xFF & b);
		
		if(!mac3Str.equals(calcMac3Str))
			return false;
		
		return true;
	}
	
	public EcmqvMessage initEcmqv(byte[] ephPubX, byte[] ephPubY, byte[] certByte){
		// decode the certificate
		X509CertificateHolder cert = null;
		try {
			cert = new X509CertificateHolder(certByte);
		} catch (IOException e1) {
			log.error("Error in decoding the submitted certificate!!");
			e1.printStackTrace();
		} 
				
		// validate the certificate
		boolean isCertValid = false;
		
		try {
			isCertValid = validateCert(cert);
		} catch (Exception e) {
			log.error("Error in verifying the submitted certificate: "+e.getMessage());
			e.printStackTrace();
		}
		
		if(!isCertValid){
			log.error("The submitted certificate is not valid!!");
			return null;
		}
		log.debug("Passed the certificate validation!!");
		
		// decode the ephemeral public key
		try {
			ephPub = ECKeyPairGen.generateECPublicKey192(new BigInteger(ephPubX), 
					new BigInteger(ephPubY));
		} catch (Exception e) {
			log.error("Error in decoding the submitted ephemeral public key: "
					+e.getMessage());
			e.printStackTrace();
		}
		
		// perform embedded public key validation
		boolean pubValid = ECMQVUtils.validateEmbedPubKey(ephPub);
		if(!pubValid){
			log.error("The submitted ephemeral public key is not valid!!");
			return null;
		}
		log.debug("Passed the embedded ephemeral public key validation!!");		
		
		// generates its own ephemeral key pairs, we assume that in this stage the 
		// ephemeral key pairs were not generated
		AsymmetricCipherKeyPair myEphKp = ECKeyPairGen.generateECKeyPair192();
		
		myEphPub = (ECPublicKeyParameters)myEphKp.getPublic();
		myEphPriv = (ECPrivateKeyParameters)myEphKp.getPrivate();
		
		// computes the implicit signature --> the static private key was obtained
		// when we validate the certificate (upon loading the KeyStore)
		BigInteger implSig = ECMQVUtils.computeImplicitSig(myEphPub, myEphPriv, 
					statPriv);
		
		// calculates the shared key K
		ECPoint K=null;
		try {
			K = ECMQVUtils.calculateSharedKey(ephPub, 
					(ECPublicKeyParameters)
					PublicKeyFactory.createKey(cert.getSubjectPublicKeyInfo()),
					ephPub.getParameters().getH(), 
					implSig);
		} catch (IOException e) {
			log.error("Error in calculating the shared key K: "+e.getMessage());
			e.printStackTrace();
		}
		 
		
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
		
		// compute the MAC to be sent to the other gateway
		byte[] myMac = ECMQVUtils.computeMAC("2", myUFN, ufn, 
					myEphPub.getQ().getEncoded(), ephPub.getQ().getEncoded(), k1);
		
		EcmqvMessage eMsg = new EcmqvMessage();
		eMsg.setMyMac(myMac);
		try {
			eMsg.setMyCertificate(myCert.getEncoded());
		} catch (IOException e) {
			log.error("Error in encoding the certificate: "+e.getMessage());
			e.printStackTrace();
		}
		
		eMsg.setEphemeralPublicX(myEphPub.getQ().normalize().getXCoord().
				toBigInteger().toByteArray());
		eMsg.setEphemeralPublicY(myEphPub.getQ().normalize().getXCoord().
				toBigInteger().toByteArray());
		
		return eMsg;
	}

	public long lastEcmqv(byte[] mac, String gwId) {
		// TODO Auto-generated method stub
		boolean isValid = false;
		
		// verify the received MAC 3
		isValid = verifyMac3(mac);
		
		if(isValid){
			Date expire = new Date();
			long keyExpire = expire.getTime() + VALID_PERIOD;
			
			// add the k2 in the catalog
			KeyGwCatalog keyGwCat = KeyGwCatalog.instance();
			keyGwCat.addKeyGw(gwId, k2);
			
			// add the expire time of the key in the catalog
			ExpireKeyGwCatalog expireGwCat = ExpireKeyGwCatalog.instance();
			expireGwCat.addExpireKeyGw(gwId, keyExpire);
			
			return keyExpire;
		}
		else
			return -1;
	}

	// blueprint set reference to the IGatewayCertificateService
	public void setGwCertificateService(
			IGatewayCertificateService gwCertificateService) {
		this.gwCertificateService = gwCertificateService;
		log.debug("Got IGatewayCertificateService from blueprint...");
	}
}
