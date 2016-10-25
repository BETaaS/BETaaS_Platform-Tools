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

package eu.betaas.service.securitymanager.capability.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedOutputStream;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Iterator;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.cms.Attribute;
import org.bouncycastle.asn1.cms.AttributeTable;
import org.bouncycastle.asn1.cms.CMSAttributes;
import org.bouncycastle.asn1.cms.Time;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.CMSSignedDataGenerator;
import org.bouncycastle.cms.CMSTypedData;
import org.bouncycastle.cms.DefaultCMSSignatureAlgorithmNameGenerator;
import org.bouncycastle.cms.DefaultSignedAttributeTableGenerator;
import org.bouncycastle.cms.SignerInfoGenerator;
import org.bouncycastle.cms.SignerInfoGeneratorBuilder;
import org.bouncycastle.cms.SignerInformation;
import org.bouncycastle.cms.SignerInformationStore;
import org.bouncycastle.cms.SignerInformationVerifier;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.operator.DefaultDigestAlgorithmIdentifierFinder;
import org.bouncycastle.operator.DefaultSignatureAlgorithmIdentifierFinder;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.OperatorException;
import org.bouncycastle.operator.bc.BcDigestCalculatorProvider;
import org.bouncycastle.util.CollectionStore;
import org.bouncycastle.util.Store;
import org.jboss.security.xacml.core.model.policy.ConditionType;

import de.odysseus.staxon.json.JsonXMLConfig;
import de.odysseus.staxon.json.JsonXMLConfigBuilder;
import de.odysseus.staxon.json.JsonXMLInputFactory;

import eu.betaas.service.securitymanager.capability.elements.ValidityCondition;
import eu.betaas.service.securitymanager.capability.elements.helper.ArrayOfString;
import eu.betaas.service.securitymanager.capability.model.CapabilityExternal;
import eu.betaas.service.securitymanager.capability.model.CapabilityInternal;
import eu.betaas.service.securitymanager.capability.model.Token;
import eu.betaas.taas.securitymanager.common.ec.operator.BcECDSAContentSignerBuilder;
import eu.betaas.taas.securitymanager.common.ec.operator.BcECDSASignerInfoVerifierBuilder;
import eu.betaas.taas.securitymanager.common.model.BcCredential;

/**
 * This class consists of some useful methods in relation to the creation and 
 * validation of the capability.
 * 
 * @author Bayu Anggorojati [ba@es.aau.dk]
 * Center for TeleInFrastruktur, Aalborg University, Denmark
 *
 */
public class CapabilityUtils {
	private static Logger log= Logger.getLogger("betaas.service.securitymanager");
	
	// detached signature mode
	/**
	 * Method to create exCap's signature with the issuer certificate detached 
	 * from the signed data 
	 * @param credentials: the credential that contains private key to sign the
	 * data
	 * @param content: the data or content to be signed
	 * @return: signed data in byte[]
	 * @throws OperatorCreationException
	 * @throws CMSException
	 * @throws IOException
	 */
	public static byte[] createCapSignature(BcCredential credentials, 
			String content) throws OperatorCreationException, CMSException, IOException{
		
		AsymmetricKeyParameter  key = credentials.getPrivateKey();
    X509CertificateHolder[] chain = credentials.getCertificateChain();

    X509CertificateHolder cert = chain[0];
//    Store certs = new CollectionStore(Arrays.asList(chain));
    
    // construct SignerInfoGenerator manually --> to deal with signingTime issue
    SignerInfoGeneratorBuilder sigBuilder = new SignerInfoGeneratorBuilder(
    		new BcDigestCalculatorProvider());
    
    Hashtable<ASN1ObjectIdentifier, Attribute> signedAttr = new Hashtable
    		<ASN1ObjectIdentifier, Attribute>();
    
    Attribute attr = new Attribute(CMSAttributes.signingTime, new DERSet(
    		new Time(new java.util.Date())));
    
    signedAttr.put(attr.getAttrType(), attr);
    AttributeTable signedAttributeTable = new AttributeTable(signedAttr);
    
    sigBuilder.setSignedAttributeGenerator(new 
    		DefaultSignedAttributeTableGenerator(signedAttributeTable));

    // set up the generator
    CMSSignedDataGenerator gen = new CMSSignedDataGenerator();

    AlgorithmIdentifier sigAlg = new DefaultSignatureAlgorithmIdentifierFinder().find("SHA1withECDSA");
    AlgorithmIdentifier digAlg = new DefaultDigestAlgorithmIdentifierFinder().find(sigAlg);
    
    SignerInfoGenerator signerInfoGen = sigBuilder.build(new 
    		BcECDSAContentSignerBuilder(sigAlg, digAlg).build(key), cert);
    
    gen.addSignerInfoGenerator(signerInfoGen);
    
//    gen.addSignerInfoGenerator(new SignerInfoGeneratorBuilder(new BcDigestCalculatorProvider()).build(new BcECDSAContentSignerBuilder(sigAlg, digAlg).build(key), cert));
    // do not store the certificate with signed data (i.e. detached signature)
//    gen.addCertificates(certs);
    
    // create the signed-data object
    CMSTypedData data = new CMSProcessableByteArray(content.getBytes());
    
    CMSSignedData signed = gen.generate(data);
    
    // recreate
//    signed = new CMSSignedData(data, signed.getEncoded());
    
		return signed.getEncoded();
	}
	
	/**
	 * Method to verify the signature of the exCap in a form of CMSSignedData
	 * @param signedData: the signed data
	 * @return: true if the signature is valid, false otherwise
	 * @throws CMSException
	 * @throws OperatorException
	 */
	public static boolean validateCapSignature(CMSSignedData signedData) 
			throws CMSException, OperatorException{
		Store certs = signedData.getCertificates();
    SignerInformationStore signers = signedData.getSignerInfos();
    Iterator it = signers.getSigners().iterator();
    
    if (it.hasNext())
    {
    	SignerInformation signer = (SignerInformation)it.next();
      X509CertificateHolder cert = (X509CertificateHolder)certs.
      		getMatches(signer.getSID()).iterator().next();

      SignerInformationVerifier verifier = new BcECDSASignerInfoVerifierBuilder(
      		new DefaultCMSSignatureAlgorithmNameGenerator(),
          new DefaultSignatureAlgorithmIdentifierFinder(),
          new DefaultDigestAlgorithmIdentifierFinder(),
          new BcDigestCalculatorProvider()).build(cert);
      
      return signer.verify(verifier);
    }
    
		return false;
	}
	
	// for signature with certificate stored
	/**
	 * Method to verify exCap's signature with the issuer certificate stored in
	 * the signed data 
	 * @param text: the original signed text
	 * @param signature: the signature in byte[]
	 * @return: true if signature is valid, false otherwise
	 * @throws CMSException
	 * @throws OperatorException
	 */
	public static boolean validateCapSignature(String text, byte[] signature) 
			throws CMSException, OperatorException{
		CMSSignedData signedData = new CMSSignedData(
				new CMSProcessableByteArray(text.getBytes()), signature);
		Store certs = signedData.getCertificates();
    SignerInformationStore signers = signedData.getSignerInfos();
    Iterator it = signers.getSigners().iterator();
    
    if (it.hasNext())
    {
    	SignerInformation signer = (SignerInformation)it.next();
      X509CertificateHolder cert = (X509CertificateHolder)certs.
      		getMatches(signer.getSID()).iterator().next();

      SignerInformationVerifier verifier = new BcECDSASignerInfoVerifierBuilder(
      		new DefaultCMSSignatureAlgorithmNameGenerator(),
          new DefaultSignatureAlgorithmIdentifierFinder(),
          new DefaultDigestAlgorithmIdentifierFinder(),
          new BcDigestCalculatorProvider()).build(cert);
      
      return signer.verify(verifier);
    }
    return false;
	}
	
	/**
	 * Method to verify exCap's signature for the detached signature or the issuer
	 * certificate is not stored in the signed data 
	 * @param text: the original signed text
	 * @param signature: the signature in byte[]
	 * @param cert: issuer certificate
	 * @return: true if signature is valid, false otherwise
	 * @throws CMSException
	 * @throws OperatorException
	 */
	public static boolean validateCapSignature(String text, byte[] signature, X509CertificateHolder cert) 
			throws CMSException, OperatorException{
		CMSSignedData signedData = new CMSSignedData(
				new CMSProcessableByteArray(text.getBytes()), signature);
//		Store certs = signedData.getCertificates();
    SignerInformationStore signers = signedData.getSignerInfos();
    Iterator it = signers.getSigners().iterator();
    
    if (it.hasNext())
    {
    	SignerInformation signer = (SignerInformation)it.next();
//      X509CertificateHolder cert = (X509CertificateHolder)certs.
//      		getMatches(signer.getSID()).iterator().next();

      SignerInformationVerifier verifier = new BcECDSASignerInfoVerifierBuilder(
      		new DefaultCMSSignatureAlgorithmNameGenerator(),
          new DefaultSignatureAlgorithmIdentifierFinder(),
          new DefaultDigestAlgorithmIdentifierFinder(),
          new BcDigestCalculatorProvider()).build(cert);
      
      return signer.verify(verifier);
    }
    
		return false;
	}
	
	/**
	 * Method to verify exCap's signature for the detached signature but the
	 * signature is verified by the public key instead of the certificate
	 * @param text: the original signed text
	 * @param signature: the signature in byte[]
	 * @param cert: issuer public key
	 * @return: true if signature is valid, false otherwise
	 * @throws CMSException
	 * @throws OperatorCreationException
	 */
	public static boolean validateCapSignature(String text, byte[] signature, 
			AsymmetricKeyParameter pubKey) throws CMSException, OperatorCreationException{
		boolean ver = false;
		
		CMSSignedData signedData = new CMSSignedData(
				new CMSProcessableByteArray(text.getBytes()), signature);
//		Store certs = signedData.getCertificates();
   SignerInformationStore signers = signedData.getSignerInfos();
   Iterator it = signers.getSigners().iterator();
   
   if (it.hasNext())
   {
   	SignerInformation signer = (SignerInformation)it.next();
//     X509CertificateHolder cert = (X509CertificateHolder)certs.
//     		getMatches(signer.getSID()).iterator().next();

     SignerInformationVerifier verifier = new BcECDSASignerInfoVerifierBuilder(
     		new DefaultCMSSignatureAlgorithmNameGenerator(),
         new DefaultSignatureAlgorithmIdentifierFinder(),
         new DefaultDigestAlgorithmIdentifierFinder(),
         new BcDigestCalculatorProvider()).build(pubKey);
     
     log.debug("will now verify the signature...");
     
     ver = signer.verify(verifier);
   }
   
   log.debug("Signature verification result: "+ver);
   
   return ver;
	}
	
	/**
	 * A method to check the validity time of the capability, e.g. valid after-
	 * before
	 * 
	 * @param vc: validity condition instance
	 * @return: true if the token is still within the validity period.
	 */
	public static boolean checkValidityCondition(ValidityCondition vc){
		Timestamp cts = new Timestamp(System.currentTimeMillis());
		Timestamp after = Timestamp.valueOf(vc.getValidAfter());
		Timestamp before = Timestamp.valueOf(vc.getValidBefore());
		
		if(cts.after(after)){
			if(cts.before(before)){
				log.debug("The token is within the validity period!!");
				return true;
			}
		}
		
		log.error("The token is outside the validity period!!");
		
		return false;
	}
	
	/**
	 * A method to convert internal capability object to XML file.
	 * 
	 * @param cap: the internal capability instance
	 * @param id: identifier of this capability
	 */
	public static void inCapToXml(CapabilityInternal cap, String id){
		JAXBContext context;
		try {
			context = JAXBContext.newInstance(CapabilityInternal.class);
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			File fileOutput = new File("inCap_"+id+".xml");
			FileOutputStream outputFile = new FileOutputStream(fileOutput);
      m.marshal(cap, outputFile);
      outputFile.flush();
		} catch (JAXBException e) {
			log.error("Error code: "+e.getErrorCode()+"\nError message: "+e.getMessage(), e.getCause());
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * A method to convert external capability object to XML file.
	 * 
	 * @param cap: the external capability instance
	 * @param id: identifier of this capability
	 */
	public static void exCapToXmlFile(CapabilityExternal cap, String id){
		JAXBContext context;
		try {
			Class[] classTobeBound = {CapabilityExternal.class, ArrayOfString.class};
			context = JAXBContext.newInstance(classTobeBound);
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			File fileOutput = new File("src\\exCap_"+id+".xml");
			FileOutputStream outputFile = new FileOutputStream(fileOutput);
      m.marshal(cap, outputFile);
      outputFile.flush();
		} catch (JAXBException e) {
			log.error("Error code: "+e.getErrorCode()+"\nError message: "+e.getMessage(), e.getCause());
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * A method to convert external capability object to XML output stream.
	 * 
	 * @param cap: the external capability instance
	 * @param id: identifier of this capability
	 * @return: OutputStream of the external capability in XML format.
	 */
	public static OutputStream exCapToXml(CapabilityExternal cap, String id){
		JAXBContext context;
		OutputStream os = new ByteArrayOutputStream();
		try {
			Class[] classTobeBound = {CapabilityExternal.class, ArrayOfString.class};
			context = JAXBContext.newInstance(classTobeBound);
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
      m.marshal(cap, os);
		} catch (JAXBException e) {
			log.error("Error code: "+e.getErrorCode()+"\nError message: "+e.getMessage(), e.getCause());
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return os;
	}
	
	/**
	 * A method to convert XML InputStream to internal capability object
	 * 
	 * @param inputStream: XML structure of internal capability
	 * @return: CapabilityInternal object
	 */
	public static CapabilityInternal xmlToInCap(InputStream inputStream){
		CapabilityInternal cap = new CapabilityInternal();	// MUST NOT BE NULL --> how to differentiate between InCap and ExCap??
		try {
			JAXBContext context = JAXBContext.newInstance(CapabilityInternal.class);
			Unmarshaller u = context.createUnmarshaller();
			
			cap = (CapabilityInternal) u.unmarshal(inputStream);
			log.debug("cap: "+cap);
			log.debug("ThingServiceID: "+cap.getResourceId());
			log.debug("Digital Signature: "+cap.getDigitalSignature());
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return cap;
	}
	
	/**
	 * A method to convert XML InputStream to external capability object
	 * 
	 * @param inputStream: XML structure of external capability
	 * @return: CapabilityExternal object
	 */
	public static CapabilityExternal xmlToExCap(InputStream inputStream){
		CapabilityExternal cap = new CapabilityExternal();	// MUST NOT BE NULL --> how to differentiate between InCap and ExCap??
		JAXBContext context;
		try {
			Class[] classTobeBound = {CapabilityExternal.class, ArrayOfString.class};
			context = JAXBContext.newInstance(classTobeBound);
			Unmarshaller u = context.createUnmarshaller();
			
			cap = (CapabilityExternal) u.unmarshal(inputStream);
			
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return cap;
	}
	
//	public static CapabilityExternal jsonToExCap(String json){
//		InputStream is = new ByteArrayInputStream(json.getBytes());
//		CapabilityExternal cap = new CapabilityExternal();
//		JAXBContext context;
//		try {
//			Class[] classTobeBound = {CapabilityExternal.class, ArrayOfString.class};
//			context = JAXBContext.newInstance(classTobeBound);
//			Unmarshaller u = context.createUnmarshaller();
////			u.setProperty(UnmarshallerProperties.MEDIA_TYPE, "application/json");
//			
//			cap = (CapabilityExternal) u.unmarshal(is);
//		} catch (JAXBException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		return cap;
//	}
//	
	
	public static Token jsonToToken(String json){
		JsonXMLConfig config = new JsonXMLConfigBuilder().build();
		InputStream is = new ByteArrayInputStream(json.getBytes());
		
		Token token = new Token();
		Class[] classes = {Token.class, CapabilityExternal.class, ArrayOfString.class};
		
		try {
			XMLStreamReader reader =  new JsonXMLInputFactory(config).createXMLStreamReader(is);
			JAXBContext context = JAXBContext.newInstance(classes);
			token = (Token) context.createUnmarshaller().unmarshal(reader);
			
			is.close();
		} catch (XMLStreamException e) {
			// TODO Auto-generated catch block
			log.error(e.getMessage());
			e.printStackTrace();
			return null;
		} catch (FactoryConfigurationError e) {
			// TODO Auto-generated catch block
			log.error(e.getMessage());
			e.printStackTrace();
			return null;
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			log.error(e.getMessage());
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			log.error(e.getMessage());
			e.printStackTrace();
			return null;
		}
		return token;				
	}
	/**
	 * A method to convert the XML string into token object
	 * 
	 * @param xml: The XML String of the token
	 * @return: Token object
	 */
	public static Token xmlToToken(String xml){
		InputStream is = new ByteArrayInputStream(xml.getBytes());
		Token token = new Token();
		JAXBContext context;
		Class[] classes = {Token.class, CapabilityExternal.class, ArrayOfString.class};
		try {
			context = JAXBContext.newInstance(classes);
			Unmarshaller u = context.createUnmarshaller();
//			u.setProperty(UnmarshallerProperties.MEDIA_TYPE, "application/json");
			token = (Token) u.unmarshal(is);
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
		
		return token;
	}
	
	/**
	 * A method to convert XML file into ConditionType object
	 * 
	 * @param file: the XML file
	 * @return: ConditionType object
	 */
	public static ConditionType xmlToConditionType(File file){
//		log.info("Get the condition type from XML file..");
		JAXBElement<ConditionType> jaxbCt = null;
		ConditionType cond = new ConditionType();
		try {
			JAXBContext context = JAXBContext.newInstance(ConditionType.class);
			Unmarshaller u = context.createUnmarshaller();
			// read xml from InputStream
//			 InputStream is = TestCondition.class.getClassLoader().getResourceAsStream("condition1.xml");
//			 jaxbCt = (JAXBElement<ConditionType>) u.unmarshal(is);
			jaxbCt = (JAXBElement<ConditionType>) u.unmarshal(new StreamSource(file), 
					ConditionType.class);
			
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			log.error("caught JAXB exception while encoding the condition type XML");
			e.printStackTrace();
		}
		cond = jaxbCt.getValue();
		return cond;
	}
}
