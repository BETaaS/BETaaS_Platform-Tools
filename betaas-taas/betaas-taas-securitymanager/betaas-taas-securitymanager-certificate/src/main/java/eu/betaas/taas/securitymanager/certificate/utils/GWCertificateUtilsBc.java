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

package eu.betaas.taas.securitymanager.certificate.utils;

import java.math.BigInteger;
import java.util.Date;

import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.ExtensionsGenerator;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509ExtensionUtils;
import org.bouncycastle.cert.X509v1CertificateBuilder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.util.SubjectPublicKeyInfoFactory;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.DefaultDigestAlgorithmIdentifierFinder;
import org.bouncycastle.operator.DefaultSignatureAlgorithmIdentifierFinder;
import org.bouncycastle.operator.SignatureAlgorithmIdentifierFinder;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.pkcs.PKCS10CertificationRequestBuilder;
import org.bouncycastle.pkcs.bc.BcPKCS10CertificationRequestBuilder;

import eu.betaas.taas.securitymanager.common.ec.ECKeyPairGen;
import eu.betaas.taas.securitymanager.common.ec.operator.BcECDSAContentSignerBuilder;
import eu.betaas.taas.securitymanager.common.ec.operator.SHA1DigestCalculator;
import eu.betaas.taas.securitymanager.common.model.ArrayOfCertificate;
import eu.betaas.taas.securitymanager.common.model.BcCredential;

/**
 * This class consists of several methods useful for creating certificates,
 * credentials, and certification request (based on PKCS10). 
 * 
 * @author Bayu Anggorojati [ba@es.aau.dk]
 * Center for TeleInFrastruktur, Aalborg University, Denmark
 *
 */
public class GWCertificateUtilsBc {
	private static final long VALIDITY_PERIOD = 365 * 24 * 60 * 60 * 1000; // 1 year
	
	private static final String PKCS12_FILE_NAME_INTER = "GwStarInterCert.p12";
//	private static final String PKCS12_FILE_NAME_OWN = "GwStarOwnCert.p12";
	
	public static char[] KEY_PASSWD = "keyPassword".toCharArray();
	
	public static final String ALG_NAME = "SHA1withECDSA";
	
	private static SignatureAlgorithmIdentifierFinder algFinder = new DefaultSignatureAlgorithmIdentifierFinder();
	
	/**
	 * A method to create the BcCredential which contains root and intermediate 
	 * certificate that will be used to sign a certificate issued for other GW
	 * @param rootAlias: alias of root credential
	 * @param interAlias: alias of intermediate credential
	 * @param keyPasswd: password of the private key
	 * @return
	 * @throws Exception
	 */
	public static BcCredential createGwStarInterCredentials(X500Name subjRoot, 
			X500Name subjInter, String rootAlias, String interAlias) throws Exception
  {
		BcCredential rootCredential = createRootCredential(subjRoot, rootAlias);
    BcCredential interCredential = 
    		createIntermediateCredential(subjInter, interAlias, rootCredential.
    				getPrivateKey(), rootCredential.getCertificateChain()[0]);

    return new BcCredential(interCredential.getAlias(), 
    		interCredential.getPrivateKey(), new X509CertificateHolder[] 
    				{interCredential.getCertificateChain()[0], 
    	rootCredential.getCertificateChain()[0]});
  }
	
	/**
	 * A method to create a complete set of certificates of the GW*
	 * @param rootAlias: alias of root credential
	 * @param interAlias: alias of intermediate credential
	 * @param eeAlias: alias of end entity credential
	 * @param keyPasswd: password of the private key
	 * @return
	 * @throws Exception
	 */
	public static BcCredential createGwStarOwnCredentials(X500Name subject, 
			String rootAlias, String interAlias, String eeAlias, String ufn, String path) 
					throws Exception{
    
    BcCredential interStore = PKCS12Utils.loadPKCS12Credential(
    		path+PKCS12_FILE_NAME_INTER, KEY_PASSWD, PKCS12Utils.GW_CERT);
    
    AsymmetricKeyParameter priv = interStore.getPrivateKey();
    X509CertificateHolder[] chain = interStore.getCertificateChain();
    X509CertificateHolder interCert = chain[0];
    BcCredential endCredential = createEndEntityCredential(subject, eeAlias, 
    		priv, interCert, ufn);
    
    return new BcCredential(endCredential.getAlias(), 
    		endCredential.getPrivateKey(), new X509CertificateHolder[]{
    	endCredential.getCertificateChain()[0], chain[0], chain[1]});
	}
	
	/**
	 * 
	 * @param caInterAlias
	 * @param gwAlias
	 * @param gwPubKey
	 * @return
	 * @throws Exception
	 */
	public static ArrayOfCertificate createGwCredentials(X500Name subject, 
			String caInterAlias, String gwAlias, AsymmetricKeyParameter gwPubKey, 
			String ufn, String path) throws Exception{
		
		// load the "signing" BcCredential certificates of the GW*
		BcCredential interStore = PKCS12Utils.loadPKCS12Credential(
				path+PKCS12_FILE_NAME_INTER, KEY_PASSWD, PKCS12Utils.GW_CERT);
		// get the intermediate certificate and its private key
		AsymmetricKeyParameter priv = interStore.getPrivateKey();
		X509CertificateHolder interCert = interStore.getCertificateChain()[0];
		
		// create X509Certificate for the GW
		X509CertificateHolder endCert = buildEndEntityCert(subject, gwPubKey, priv, 
				interCert, ufn);
		
		ArrayOfCertificate certs = new ArrayOfCertificate();
		// swap the position of end entity and intermediate certificate (to fix 
		// the error when loading the credential)
		certs.getCertificate().add(endCert.getEncoded());
		certs.getCertificate().add(interCert.getEncoded());
		
		return certs;
	}
	
	/**
	 * 
	 * @param rootAlias
	 * @return
	 * @throws Exception
	 */
	public static BcCredential createRootCredential(X500Name subject, 
			String rootAlias) throws Exception
  {
		AsymmetricCipherKeyPair rootPair = ECKeyPairGen.generateECKeyPair192();
    X509CertificateHolder rootCert = buildRootCert(subject, rootPair);
      
    return new BcCredential(rootAlias, rootPair.getPrivate(), rootCert);
  }
	
	/**
	 * 
	 * @param interAlias
	 * @param caKey
	 * @param caCert
	 * @return
	 * @throws Exception
	 */
	public static BcCredential createIntermediateCredential(X500Name subject, 
			String interAlias, AsymmetricKeyParameter caKey, 
			X509CertificateHolder caCert) throws Exception
  {
      AsymmetricCipherKeyPair interPair = ECKeyPairGen.generateECKeyPair192();
      X509CertificateHolder interCert = buildIntermediateCert(subject, 
      		interPair.getPublic(), caKey, caCert);
      
      return new BcCredential(interAlias, interPair.getPrivate(), interCert);
  }
	
	/**
	 * 
	 * @param eeAlias
	 * @param caKey
	 * @param caCert
	 * @return
	 * @throws Exception
	 */
	public static BcCredential createEndEntityCredential(X500Name subject, 
			String eeAlias, AsymmetricKeyParameter caKey, X509CertificateHolder caCert, 
			String ufn) 
      		throws Exception
  {
      AsymmetricCipherKeyPair endPair = ECKeyPairGen.generateECKeyPair192();
      X509CertificateHolder endCert = buildEndEntityCert(subject, 
      		endPair.getPublic(), caKey, caCert, ufn);
      
      return new BcCredential(eeAlias, endPair.getPrivate(), endCert);
  }
	
	/**
	 * 
	 * @param keyPair
	 * @return
	 * @throws Exception
	 */
	public static X509CertificateHolder buildRootCert(X500Name subject, 
			AsymmetricCipherKeyPair keyPair) throws Exception
  {
		if(subject==null)
  		subject = new X500Name("CN = BETaaS Instance Root Certificate");
		
		X509v1CertificateBuilder certBldr = new X509v1CertificateBuilder(
				subject,
				BigInteger.valueOf(1),
				new Date(System.currentTimeMillis()),
        new Date(System.currentTimeMillis() + VALIDITY_PERIOD),
        subject,
        SubjectPublicKeyInfoFactory.createSubjectPublicKeyInfo(keyPair.getPublic()));
		
		AlgorithmIdentifier sigAlg = algFinder.find(ALG_NAME);
    AlgorithmIdentifier digAlg = new DefaultDigestAlgorithmIdentifierFinder().find(sigAlg);

      ContentSigner signer = new BcECDSAContentSignerBuilder(sigAlg, digAlg).build(keyPair.getPrivate());

    return certBldr.build(signer);
  }
	
	/**
	 * 
	 * @param intKey
	 * @param caKey
	 * @param caCert
	 * @return
	 * @throws Exception
	 */
	public static X509CertificateHolder buildIntermediateCert(X500Name subject,
			AsymmetricKeyParameter intKey, AsymmetricKeyParameter caKey, 
			X509CertificateHolder caCert) throws Exception
  {
		SubjectPublicKeyInfo intKeyInfo = SubjectPublicKeyInfoFactory.
				createSubjectPublicKeyInfo(intKey);
		
		if(subject==null)
  		subject = new X500Name("CN = BETaaS Instance CA Certificate");
		
		X509v3CertificateBuilder certBldr = new X509v3CertificateBuilder(
				caCert.getSubject(),
				BigInteger.valueOf(1),
 	      new Date(System.currentTimeMillis()),
        new Date(System.currentTimeMillis() + VALIDITY_PERIOD),
        subject,
        intKeyInfo);
		
		X509ExtensionUtils extUtils = new X509ExtensionUtils(
				new SHA1DigestCalculator());

    certBldr.addExtension(Extension.authorityKeyIdentifier, false, 
    		extUtils.createAuthorityKeyIdentifier(caCert))
    .addExtension(Extension.subjectKeyIdentifier, false, 
    		extUtils.createSubjectKeyIdentifier(intKeyInfo))
    .addExtension(Extension.basicConstraints, true, new BasicConstraints(0))
    .addExtension(Extension.keyUsage, true, new 
    		KeyUsage(KeyUsage.digitalSignature | KeyUsage.keyCertSign | 
    				KeyUsage.cRLSign));
    
    AlgorithmIdentifier sigAlg = algFinder.find(ALG_NAME);
    AlgorithmIdentifier digAlg = new DefaultDigestAlgorithmIdentifierFinder().
    		find(sigAlg);

    ContentSigner signer = new BcECDSAContentSignerBuilder(sigAlg, digAlg).
    		build(caKey);

    return certBldr.build(signer);
  }
	
	/**
	 * 
	 * @param entityKey - public key of the requesting GW
	 * @param caKey
	 * @param caCert
	 * @return
	 * @throws Exception
	 */
	public static X509CertificateHolder buildEndEntityCert(X500Name subject,
  		AsymmetricKeyParameter entityKey, AsymmetricKeyParameter caKey, 
  		X509CertificateHolder caCert, String ufn) throws Exception
	{
  	SubjectPublicKeyInfo entityKeyInfo = SubjectPublicKeyInfoFactory.
  			createSubjectPublicKeyInfo(entityKey);
  	
  	if(subject==null)
  		subject = new X500Name("CN = BETaaS Gateway Certificate");
  	
  	X509v3CertificateBuilder certBldr = new X509v3CertificateBuilder(
  			caCert.getSubject(),
        BigInteger.valueOf(1),
        new Date(System.currentTimeMillis()),
        new Date(System.currentTimeMillis() + VALIDITY_PERIOD),
        subject,
        entityKeyInfo);
  	
  	X509ExtensionUtils extUtils = new X509ExtensionUtils(
  			new SHA1DigestCalculator());
  	
    certBldr.addExtension(Extension.authorityKeyIdentifier, false, 
    		extUtils.createAuthorityKeyIdentifier(caCert))
    	.addExtension(Extension.subjectKeyIdentifier, false, 
    			extUtils.createSubjectKeyIdentifier(entityKeyInfo))
      .addExtension(Extension.basicConstraints, true, 
      		new BasicConstraints(false))
      .addExtension(Extension.keyUsage, true, new KeyUsage(
      		KeyUsage.digitalSignature | KeyUsage.keyEncipherment))
      .addExtension(Extension.subjectAlternativeName, false, new GeneralNames(
      		new GeneralName(GeneralName.rfc822Name, ufn)));

    AlgorithmIdentifier sigAlg = algFinder.find(ALG_NAME);
    AlgorithmIdentifier digAlg = new DefaultDigestAlgorithmIdentifierFinder().find(sigAlg);

    ContentSigner signer = new BcECDSAContentSignerBuilder(sigAlg, digAlg).build(caKey);
      
    return certBldr.build(signer);
	}
	
	/**
   * A method to build PKCS10 Certification request (BC style)
   * @param subject: the subject info/data in X500Name format
   * @param kp: the subject's key pair
   * @param subjectAltName: subject's UFN
   * @return
   * @throws Exception
   */
  public static PKCS10CertificationRequest buildCertificateRequest(
  		X500Name subject, AsymmetricCipherKeyPair kp, String subjectAltName) 
  				throws Exception{
  	String sigName = "SHA1withECDSA";
  	SignatureAlgorithmIdentifierFinder algFinder = new 
  			DefaultSignatureAlgorithmIdentifierFinder();
  	
  	PKCS10CertificationRequestBuilder requestBuilder = new 
  			BcPKCS10CertificationRequestBuilder(subject, kp.getPublic());
  	
  	ExtensionsGenerator extGen = new ExtensionsGenerator();
    extGen.addExtension(Extension.subjectAlternativeName, false, 
    		new GeneralNames(new GeneralName(GeneralName.rfc822Name, subjectAltName 
    				+"@betaas.eu")));
    requestBuilder.addAttribute(PKCSObjectIdentifiers.pkcs_9_at_extensionRequest, 
    		extGen.generate());
    
    AlgorithmIdentifier sigAlg = algFinder.find(sigName);
    AlgorithmIdentifier digAlg = new DefaultDigestAlgorithmIdentifierFinder().
    		find(sigAlg);
    
    ContentSigner signer = new BcECDSAContentSignerBuilder(sigAlg, digAlg).
    		build(kp.getPrivate());
    
    PKCS10CertificationRequest req1 = requestBuilder.build(signer);
    
    return req1;
  }
}
