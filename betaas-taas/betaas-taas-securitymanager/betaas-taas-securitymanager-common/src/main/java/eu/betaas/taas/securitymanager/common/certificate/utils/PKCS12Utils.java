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

package eu.betaas.taas.securitymanager.common.certificate.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.bouncycastle.asn1.ASN1Encoding;
import org.bouncycastle.asn1.DERBMPString;
import org.bouncycastle.asn1.pkcs.Attribute;
import org.bouncycastle.asn1.pkcs.ContentInfo;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.sec.ECPrivateKey;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.asn1.x509.SubjectKeyIdentifier;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.bc.BcX509ExtensionUtils;
import org.bouncycastle.crypto.engines.DESedeEngine;
import org.bouncycastle.crypto.engines.RC2Engine;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.ECKeyParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.util.PrivateKeyFactory;
import org.bouncycastle.operator.InputDecryptorProvider;
import org.bouncycastle.operator.OutputEncryptor;
import org.bouncycastle.operator.bc.BcDefaultDigestProvider;
import org.bouncycastle.pkcs.PKCS12PfxPdu;
import org.bouncycastle.pkcs.PKCS12PfxPduBuilder;
import org.bouncycastle.pkcs.PKCS12SafeBag;
import org.bouncycastle.pkcs.PKCS12SafeBagBuilder;
import org.bouncycastle.pkcs.PKCS12SafeBagFactory;
import org.bouncycastle.pkcs.PKCS8EncryptedPrivateKeyInfo;
import org.bouncycastle.pkcs.PKCSException;
import org.bouncycastle.pkcs.bc.BcPKCS12MacCalculatorBuilder;
import org.bouncycastle.pkcs.bc.BcPKCS12MacCalculatorBuilderProvider;
import org.bouncycastle.pkcs.bc.BcPKCS12PBEInputDecryptorProviderBuilder;
import org.bouncycastle.pkcs.bc.BcPKCS12PBEOutputEncryptorBuilder;
import org.bouncycastle.util.io.Streams;

import eu.betaas.taas.securitymanager.common.model.BcCredential;

/**
 * This class consists of methods that are used to create and read (including 
 * the validation) of PKCS12 file -- used to store the certificate
 * 
 * @author Bayu Anggorojati [ba@es.aau.dk]
 * Center for TeleInFrastruktur, Aalborg University, Denmark
 *
 */
public class PKCS12Utils {
	
	private static Logger log = Logger.getLogger("betaas.taas.securitymanager");
	
	public final static int GW_CERT = 1;
	public final static int APPS_CERT = 2;
  
  /**
   * A method to create PKCS12 file that stores the certificates.
   * @param pfxOut: the output of pkcs12 file (in OutputStream) 
   * @param key: private key that is associated with the credential
   * @param chain: chain of certificates (within the credential)
   * @param keyPasswd: key password
   * @throws Exception
   */
  public static void createPKCS12FileBc(OutputStream pfxOut, 
  		AsymmetricKeyParameter key, X509CertificateHolder[] chain, 
  		char[] keyPasswd) throws Exception{
  	
  	OutputEncryptor encOut = new BcPKCS12PBEOutputEncryptorBuilder(
  			PKCSObjectIdentifiers.pbeWithSHAAnd3_KeyTripleDES_CBC, 
  			new CBCBlockCipher(new DESedeEngine())).build(keyPasswd);
  	
  	PKCS12SafeBagBuilder taCertBagBuilder = null;
  	PKCS12SafeBagBuilder caCertBagBuilder = null;
  	PKCS12SafeBagBuilder eeCertBagBuilder = null;
  	SubjectKeyIdentifier pubKeyId = null;
  	
  	// identify the type of certificate from the given certificate chain
  	for(int i = 0;i<chain.length;i++){
  		Extensions exs = chain[i].getExtensions();
  		if(exs!=null){
  			KeyUsage ku = KeyUsage.fromExtensions(exs);
  			if(ku.toString().equals("KeyUsage: 0x"+Integer.toHexString(128 | 32))){
  				// end entity certificate
  				eeCertBagBuilder = new PKCS12SafeBagBuilder(chain[i]);
  				BcX509ExtensionUtils extUtils = new BcX509ExtensionUtils();
  				eeCertBagBuilder.addBagAttribute(PKCS12SafeBag.friendlyNameAttribute, 
  		    		new DERBMPString("Eric's Key"));
  		    pubKeyId = extUtils.createSubjectKeyIdentifier(
  		    		chain[i].getSubjectPublicKeyInfo());
  		    eeCertBagBuilder.addBagAttribute(PKCS12SafeBag.localKeyIdAttribute, 
  		    		pubKeyId);
    		}
  			else if(ku.toString().equals("KeyUsage: 0x"+
  		    		Integer.toHexString(128 | 4 | 2))){
    			// intermediate certificate
    			caCertBagBuilder = new PKCS12SafeBagBuilder(chain[i]);
    			caCertBagBuilder.addBagAttribute(PKCS12SafeBag.friendlyNameAttribute, 
    	    		new DERBMPString("BETaaS Intermediate Certificate"));
    		}
  		}
  		else{
  			// root certificate
  			taCertBagBuilder = new PKCS12SafeBagBuilder(chain[i]);
  			taCertBagBuilder.addBagAttribute(PKCS12SafeBag.friendlyNameAttribute, 
  	    		new DERBMPString("BETaaS Primary Certificate"));
  		}
  	}
  	
//    PKCS12SafeBagBuilder taCertBagBuilder = new PKCS12SafeBagBuilder(chain[2]);

//    PKCS12SafeBagBuilder caCertBagBuilder = new PKCS12SafeBagBuilder(chain[1]);
  	
//    PKCS12SafeBagBuilder eeCertBagBuilder = new PKCS12SafeBagBuilder(chain[0]);
    
    // the ECPrivateKey, consists of the key itself and the ECParams
    BigInteger dPriv = ((ECPrivateKeyParameters) key).getD();
    X9ECParameters ecParams = new X9ECParameters(
    		((ECKeyParameters) key).getParameters().getCurve(),
    		((ECKeyParameters) key).getParameters().getG(),
    		((ECKeyParameters) key).getParameters().getN(),
    		((ECKeyParameters) key).getParameters().getH(),
    		((ECKeyParameters) key).getParameters().getSeed()); 
    ECPrivateKey privParams = new ECPrivateKey(dPriv, ecParams);
    
    // include the ecParams
    AlgorithmIdentifier sigAlg = new AlgorithmIdentifier(
    		X9ObjectIdentifiers.id_ecPublicKey, ecParams);
    
//    PrivateKeyInfo keyInfo = PrivateKeyInfoFactory.createPrivateKeyInfo(key);
    
    PKCS12SafeBagBuilder keyBagBuilder = new PKCS12SafeBagBuilder(
    		new PrivateKeyInfo(sigAlg, privParams), encOut);

    keyBagBuilder.addBagAttribute(PKCS12SafeBag.friendlyNameAttribute, 
    		new DERBMPString("Eric's Key"));
    if(pubKeyId!=null)
    	keyBagBuilder.addBagAttribute(PKCS12SafeBag.localKeyIdAttribute, pubKeyId);

    PKCS12PfxPduBuilder builder = new PKCS12PfxPduBuilder();

    builder.addData(keyBagBuilder.build());
    
    // no need to insert SHA1Digest() because it is the default Digest algorithm
    // check each of the certbagbuilder
    if(caCertBagBuilder!=null && taCertBagBuilder!=null && 
    		eeCertBagBuilder!=null){
    	// include all types of certificate in the file --> root own's credential
    	builder.addEncryptedData(new BcPKCS12PBEOutputEncryptorBuilder(
    			PKCSObjectIdentifiers.pbeWithSHAAnd128BitRC2_CBC, 
    			new CBCBlockCipher(new RC2Engine())).build(keyPasswd), 
    			new PKCS12SafeBag[]{eeCertBagBuilder.build(), caCertBagBuilder.build(), 
    		taCertBagBuilder.build()});
    }
    else if(caCertBagBuilder!=null && taCertBagBuilder!=null && 
    		eeCertBagBuilder==null){
    	// only root and intermediate --> signer credential
    	builder.addEncryptedData(new BcPKCS12PBEOutputEncryptorBuilder(
      		PKCSObjectIdentifiers.pbeWithSHAAnd128BitRC2_CBC, 
      		new CBCBlockCipher(new RC2Engine())).build(keyPasswd), 
      		new PKCS12SafeBag[]{caCertBagBuilder.build(), taCertBagBuilder.build()});
    }
    else if(caCertBagBuilder==null && taCertBagBuilder==null){
    	// only end entity --> e.g. application, user, etc
    	builder.addEncryptedData(new BcPKCS12PBEOutputEncryptorBuilder(
      		PKCSObjectIdentifiers.pbeWithSHAAnd128BitRC2_CBC, 
      		new CBCBlockCipher(new RC2Engine())).build(keyPasswd), 
      		new PKCS12SafeBag[]{eeCertBagBuilder.build()});
    }
    else if(caCertBagBuilder!=null && taCertBagBuilder==null && 
    		eeCertBagBuilder!=null){
    	// only intermediate and end entity --> common GW certificate
    	builder.addEncryptedData(new BcPKCS12PBEOutputEncryptorBuilder(
      		PKCSObjectIdentifiers.pbeWithSHAAnd128BitRC2_CBC, 
      		new CBCBlockCipher(new RC2Engine())).build(keyPasswd), 
      		new PKCS12SafeBag[]{eeCertBagBuilder.build(), caCertBagBuilder.build()});
    }
    
//    PKCS12PfxPdu pfx = builder.build(new BcPKCS12MacCalculatorBuilder(
//    		new SHA256Digest(), 
//    		new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha256)), keyPasswd);
    PKCS12PfxPdu pfx = builder.build(new BcPKCS12MacCalculatorBuilder(),
    		keyPasswd);
    // make sure we don't include indefinite length encoding
    pfxOut.write(pfx.getEncoded(ASN1Encoding.DL));

    pfxOut.close();
  }
  
  /**
   * A method to read the PKCS12 file in InputStream and return it into 
   * PKCS12PfxPdu object.
   * @param pfxIn: PKCS12 input file as InputStream
   * @return: 
   * @throws Exception
   */
  public static PKCS12PfxPdu readPKCS12FileBc(InputStream pfxIn) 
  		throws Exception{
  	PKCS12PfxPdu pfxPdu = new PKCS12PfxPdu(Streams.readAll(pfxIn));
  	return pfxPdu;
  }
  
  /**
   * A method to load BcCredential (consists of certificate chain, end entity 
   * alias and private key of end entity credential) from the PKCS12 file
   * @param pkcs12FileName: the PKCS12 file name
   * @param keyPasswd: the password of the key credential
   * @return
   * @throws Exception
   */
  public static BcCredential loadPKCS12Credential(String pkcs12FileName, 
  		char[] keyPasswd, int certType){
  	
  	PKCS12PfxPdu pfxPdu = null;
//  	if(certType == APPS_CERT){
//  		log.info("Reading AppStoreCertInter.p12 file");
//  		InputStream is = PKCS12Utils.class.getResourceAsStream(pkcs12FileName);
//  		log.info("AppStoreCertInter.p12 file has been converted to InputStream");
//  		pfxPdu = new PKCS12PfxPdu(Streams.readAll(is));
//  		log.info("Read the PKCS12PfxPdu...");
//  	}
//  	else if(certType == GW_CERT){
  	// Try to put the AppStoreCertInter.p12 in the karaf, so no need to read
  	// from the resource, e.g. getResourceAsStream
  	log.debug("will start loading PKCS12 file...");
  		try {
				pfxPdu = new PKCS12PfxPdu(Streams.readAll(
						new FileInputStream(pkcs12FileName)));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				log.error("PKCS12 file: "+pkcs12FileName +" is not found!!");
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				log.error("IOException in initializing PKCS12PfxPdu...");
				e.printStackTrace();
			}
  		log.debug("Loading PKCS12 successfully...");
//  	}
  	try {
			if (!pfxPdu.isMacValid(new BcPKCS12MacCalculatorBuilderProvider(
					BcDefaultDigestProvider.INSTANCE), keyPasswd))
			{
				log.error("PKCS#12 MAC test failed!");
			  return null;
			}
		} catch (PKCSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
  	
  	ContentInfo[] infos = pfxPdu.getContentInfos();
  	InputDecryptorProvider inputDecryptorProvider = new 
    		BcPKCS12PBEInputDecryptorProviderBuilder().build(keyPasswd);
  	
  	String eeAlias = null;
    AsymmetricKeyParameter privCred = null;
    List<X509CertificateHolder> chainList = new ArrayList<X509CertificateHolder>();
//    log.info("Start iterating over the ContentInfo...");
    for (int i = 0; i != infos.length; i++){
    	if (infos[i].getContentType().equals(PKCSObjectIdentifiers.encryptedData)){
    		PKCS12SafeBagFactory dataFact = null;
				try {
					dataFact = new PKCS12SafeBagFactory(infos[i], 
							inputDecryptorProvider);
				} catch (PKCSException e) {
					// TODO Auto-generated catch block
					log.error("Error in initiating PKCS12SafeBagFactory...");
					e.printStackTrace();
				}
    		
    		PKCS12SafeBag[] bags = dataFact.getSafeBags();
    		for (int b = 0; b != bags.length; b++){
    			PKCS12SafeBag bag = bags[b];
    			X509CertificateHolder certHldr = (X509CertificateHolder)bag.getBagValue();
    			chainList.add(certHldr);
    			log.debug("Found a certificate and add it to certificate chain...");
        }
    	}
    	else{
    		PKCS12SafeBagFactory dataFact = new PKCS12SafeBagFactory(infos[i]);
    		PKCS12SafeBag[] bags = dataFact.getSafeBags();

        PKCS8EncryptedPrivateKeyInfo encInfo = (PKCS8EncryptedPrivateKeyInfo)
        		bags[0].getBagValue();
        PrivateKeyInfo info;
        AsymmetricKeyParameter privKey = null;
				try {
					info = encInfo.decryptPrivateKeyInfo(inputDecryptorProvider);
					privKey = PrivateKeyFactory.createKey(info);
				} catch (PKCSException e) {
					// TODO Auto-generated catch block
					log.error("Error in getting the decrypt private key info...");
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					log.error("Error in loading private key...");
					e.printStackTrace();
				}
        
        Attribute[] attributes = bags[0].getAttributes();
        for (int a = 0; a != attributes.length; a++){
        	Attribute attr = attributes[a];
        	if (attr.getAttrType().equals(PKCS12SafeBag.friendlyNameAttribute)){
        		eeAlias = ((DERBMPString)attr.getAttributeValues()[0]).getString();
            privCred = privKey;
            log.debug("Get end entity alias");
            log.debug("Priv. credential D: "+ ((ECPrivateKeyParameters)privCred)
            		.getD().toString());
          }
        }
      }
    }
    X509CertificateHolder[] chain = new X509CertificateHolder[chainList.size()];
    chain = (X509CertificateHolder[]) chainList.toArray(chain);
    
    BcCredential cred = new BcCredential(eeAlias, privCred, chain);
    log.debug("Credential has been loaded!!");
    
    return cred;
  }
  
  /**
   * A method to load BcCredential (consists of certificate chain, end entity 
   * alias and private key of end entity credential) from the PKCS12 file
   * @param pfx: the PKCS#12 file in byte
   * @param keyPasswd: the password of the key credential
   * @return
   * @throws Exception
   */
  public static BcCredential loadPKCS12Credential(byte[] pfx, char[] keyPasswd) 
  		throws Exception{
  	
  	PKCS12PfxPdu pfxPdu = new PKCS12PfxPdu(pfx);
  	
  	if (!pfxPdu.isMacValid(new BcPKCS12MacCalculatorBuilderProvider(
    		BcDefaultDigestProvider.INSTANCE), keyPasswd)){
  		log.error("PKCS#12 MAC test failed!");
      return null;
    }
  	
  	ContentInfo[] infos = pfxPdu.getContentInfos();
  	InputDecryptorProvider inputDecryptorProvider = new 
    		BcPKCS12PBEInputDecryptorProviderBuilder().build(keyPasswd);
  	
  	String eeAlias = null;
    AsymmetricKeyParameter privCred = null;
    List<X509CertificateHolder> chainList = new ArrayList<X509CertificateHolder>();
//    log.debug("Start iterating over the ContentInfo...");
    for (int i = 0; i != infos.length; i++){
    	if (infos[i].getContentType().equals(PKCSObjectIdentifiers.encryptedData)){
    		PKCS12SafeBagFactory dataFact = new PKCS12SafeBagFactory(infos[i], 
    				inputDecryptorProvider);
    		
    		PKCS12SafeBag[] bags = dataFact.getSafeBags();
    		for (int b = 0; b != bags.length; b++){
    			PKCS12SafeBag bag = bags[b];
    			X509CertificateHolder certHldr = (X509CertificateHolder)bag.getBagValue();
    			chainList.add(certHldr);
    			log.debug("Found a certificate and add it to certificate chain...");
        }
    	}
    	else{
    		PKCS12SafeBagFactory dataFact = new PKCS12SafeBagFactory(infos[i]);
    		PKCS12SafeBag[] bags = dataFact.getSafeBags();

        PKCS8EncryptedPrivateKeyInfo encInfo = (PKCS8EncryptedPrivateKeyInfo)bags[0].getBagValue();
        PrivateKeyInfo info = encInfo.decryptPrivateKeyInfo(inputDecryptorProvider);
        AsymmetricKeyParameter privKey = PrivateKeyFactory.createKey(info);
        
        Attribute[] attributes = bags[0].getAttributes();
        for (int a = 0; a != attributes.length; a++){
        	Attribute attr = attributes[a];
        	if (attr.getAttrType().equals(PKCS12SafeBag.friendlyNameAttribute)){
        		eeAlias = ((DERBMPString)attr.getAttributeValues()[0]).getString();
            privCred = privKey;
            log.debug("Get end entity alias");
            log.debug("Priv. credential D: "+ ((ECPrivateKeyParameters)privCred)
            		.getD().toString());
          }
        }
      }
    }
    X509CertificateHolder[] chain = new X509CertificateHolder[chainList.size()];
    chain = (X509CertificateHolder[]) chainList.toArray(chain);
    
    BcCredential cred = new BcCredential(eeAlias, privCred, chain);
    
    return cred;
  }
}
