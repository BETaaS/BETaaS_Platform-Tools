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

package eu.betaas.service.securitymanager.service.impl;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMResult;

//import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.CertException;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.util.PublicKeyFactory;
import org.bouncycastle.operator.DefaultDigestAlgorithmIdentifierFinder;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.OperatorException;
import org.jboss.security.xacml.core.model.policy.ConditionType;
import org.jboss.security.xacml.core.model.policy.ObjectFactory;
import org.jboss.security.xacml.interfaces.XACMLConstants;
import org.jboss.security.xacml.sunxacml.BasicEvaluationCtx;
import org.jboss.security.xacml.sunxacml.EvaluationCtx;
import org.jboss.security.xacml.sunxacml.ParsingException;
import org.jboss.security.xacml.sunxacml.PolicyMetaData;
import org.jboss.security.xacml.sunxacml.attr.BooleanAttribute;
import org.jboss.security.xacml.sunxacml.attr.StringAttribute;
import org.jboss.security.xacml.sunxacml.attr.TimeAttribute;
import org.jboss.security.xacml.sunxacml.cond.Condition;
import org.jboss.security.xacml.sunxacml.cond.EvaluationResult;
import org.jboss.security.xacml.sunxacml.cond.VariableManager;
import org.jboss.security.xacml.sunxacml.ctx.Attribute;
import org.jboss.security.xacml.sunxacml.ctx.RequestCtx;
import org.jboss.security.xacml.sunxacml.ctx.Subject;
//import org.osgi.framework.BundleContext;
//import org.osgi.util.tracker.ServiceTracker;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import eu.betaas.service.securitymanager.capability.elements.AccessRight;
import eu.betaas.service.securitymanager.capability.elements.AccessRights;
import eu.betaas.service.securitymanager.capability.elements.IssuerInfo;
import eu.betaas.service.securitymanager.capability.elements.SubjectInfo;
import eu.betaas.service.securitymanager.capability.elements.ValidityCondition;
import eu.betaas.service.securitymanager.capability.elements.helper.AccessType;
import eu.betaas.service.securitymanager.capability.elements.helper.IssuerType;
import eu.betaas.service.securitymanager.capability.model.CapabilityExternal;
import eu.betaas.service.securitymanager.capability.model.Token;
import eu.betaas.service.securitymanager.capability.utils.CapToXmlUtils;
import eu.betaas.service.securitymanager.capability.utils.CapabilityUtils;
import eu.betaas.service.securitymanager.credential.AppCertCatalog;
import eu.betaas.service.securitymanager.service.IAuthorizationService;
import eu.betaas.taas.securitymanager.certificate.service.IGatewayCertificateService;
import eu.betaas.taas.securitymanager.common.ec.ECKeyPairGen;
import eu.betaas.taas.securitymanager.common.ec.operator.BcECDSAContentVerifierProviderBuilder;
import eu.betaas.taas.securitymanager.common.model.BcCredential;

/**
 * This class implements IAuthorizationService 
 * 
 * @author Bayu Anggorojati [ba@es.aau.dk]
 * Center for TeleInFrastruktur, Aalborg University, Denmark
 *
 */
public class AuthorizationService implements IAuthorizationService {
	private static final String APPS_CERT_NAME_INTER = "AppStoreCertInter.p12"; 
	private static final String APPS_CERT_NAME_OWN = "AppStoreCertOwn.p12";
	
	/** Path to the AppStore certificate file */
	private String certPath;
	
	/** Path to the condition file to be used in the token */
	private String conditionPath;
	
	private static final String REVOCATION_URL = "http://revocation.betaas.eu";
	
	private static final int VALIDITY_PERIOD = 100 * 24 * 60 * 60 * 1000; // 100 days

	/** The Logger */
	private Logger log = Logger.getLogger("betaas.service.securitymanager");
	
	/** Activator class of this Authorization bundle */
//	private SecurityManagerActivator authActivator;
	
	/** Reference to Certificate service in blueprint */
	private IGatewayCertificateService certServ;
	
	/** Bundle Context used in the Activator class */
//	private BundleContext bc;
	
	/** Credential of this GW that includes its certificate */
	private BcCredential myCredential;
	
	/** Credential of the CA that issues certificate for Applications */
	private BcCredential appsCaCredential;
	
	/** Catalog of Application certificate */
	private AppCertCatalog appCertCatalog;
	
	/** GW ID of this GW */
	private String myGwID;
	
	private ObjectFactory of = new ObjectFactory();
	
	public AuthorizationService(){
		this.appCertCatalog = AppCertCatalog.instance();
		// get myGwID --> to be implemented
	}
	
	public boolean checkAuthApplication(String appId, byte[] credential) 
			throws Exception {
		// the credential is actually a .p12 file, so we need to decode it first
//		X509CertificateHolder cert = null;
//		try {
//			cert = new X509CertificateHolder(credential);
//		} catch (IOException e1) {
//			log.error("Error in decoding the submitted certificate!!");
//			e1.printStackTrace();
//		}
		
//		ServiceTracker certTracker = authActivator.getCertTracker();
		
		// first load the BcCredential that contains certificates that signs Apps
		// certificate
		boolean isValid = false;
		
//		String credStr = "";
//		
//		for(byte b: credential)
//			credStr = credStr + b;
		
//		log.info("Submitted credential from SM: "+ credStr);
		// the received credential from Application and Instance manager is in
		// Base64 encoding --> we need to decode it first to normal byte []
//		byte[] decoded = Base64.decodeBase64(credential);
		
//		int n = 0;
//		Object[] certProviders = certTracker.getServices();
//		if(certProviders != null && certProviders.length > 0){
//			log.info("Found certificate service provider");
//			if(n>=certProviders.length )
//				n=0;
//			certServ = (IGatewayCertificateService) 
//					certProviders[n++];
			
			// the credential is actually a .p12 file, so we need to decode it first
			BcCredential appsCred = certServ.readAppsCertificate(credential);
			log.debug("Read the submitted credential by application");
			// application's certificate is the only in the cred
			X509CertificateHolder appsCert = appsCred.getCertificateChain()[0];
//			log.info("Get the application's certificate: "+appsCert.toString());
			// load the credential of the CA's certificate (e.g. for signing)
			appsCaCredential = certServ.loadAppStoreCertificate(certPath + APPS_CERT_NAME_INTER);
			log.debug("Load the CA's credential from taas certificate service");
			SubjectPublicKeyInfo info = appsCaCredential.getCertificateChain()[0].
					getSubjectPublicKeyInfo();
//			X509CertificateHolder[] certList = appsCaCredential.getCertificateChain(); 
			
			AsymmetricKeyParameter verKey = PublicKeyFactory.createKey(info);
			
			isValid = appsCert.isSignatureValid(new BcECDSAContentVerifierProviderBuilder(new 
					DefaultDigestAlgorithmIdentifierFinder()).build(verKey));
//			isValid = true;
			log.info("Is credential valid? "+ isValid);
			// add the submitted certificate by apps into the catalog
			appCertCatalog.addAppCert(appId, appsCert);
//		}
		
		return isValid;
	}

	/**
	 * Method to form an ExternalCapability for local GW (this GW)
	 * @param thingServiceId
	 * @param subjectType
	 * @param subjectPublicKeyInfo
	 * @param myCertByte
	 * @return
	 * @throws JAXBException
	 * @throws OperatorCreationException
	 * @throws CMSException
	 * @throws IOException
	 */
	private CapabilityExternal getTokenLocal(String thingServiceId, 
			String subjectType, byte[] subjectPublicKeyInfo, byte[] myCertByte) throws 
			JAXBException, OperatorCreationException, CMSException, IOException{
		CapabilityExternal exCap = new CapabilityExternal();
		
		exCap.setResourceId(thingServiceId);
		exCap.setRevocationUrl(REVOCATION_URL);
		
		// Issuer Info
		IssuerInfo ii = new IssuerInfo();
		ii.setIssuerCertificate(myCertByte);
		ii.setIssuerType(IssuerType.APPLICATION_TYPE);
		exCap.setIssuerInfo(ii);
		
		// create SubjectInfo class and then add it to the exCap
		SubjectInfo si = new SubjectInfo();
		si.setSubjectType(subjectType);
//		si.setPublicKeyInfo(publicKey);
		exCap.setSubjectInfo(si);
		
		// set validity condition (validity time) of the exCap
		ValidityCondition vc = new ValidityCondition();
		Timestamp ts1 = new Timestamp(System.currentTimeMillis());
		Timestamp ts2 = new Timestamp(System.currentTimeMillis() + VALIDITY_PERIOD);
		vc.setValidAfter(ts1.toString());
		vc.setValidBefore(ts2.toString());
		exCap.setValidityCondition(vc);
		
		AccessRights accessRights = new AccessRights();
		AccessRight ar1 = new AccessRight();
//		ar1.setAccessType(AccessType.REALTIME_PULL);
		// create condition --> read from XML file
		// right now we assume only 1 condition exists
		final File conditionFolder = new File(conditionPath);
		for(File condFile : conditionFolder.listFiles()){
			if(!condFile.isDirectory() && condFile.getName() != null && 
					(condFile.getName().endsWith(".xml") || condFile.getName().endsWith(".xml"))){
				ConditionType condition = CapabilityUtils.xmlToConditionType(condFile);
				ar1.setCondition(condition);
			}
		}
//		accessRights.getAccessRight().add(ar1);
//		exCap.setAccessRights(accessRights);
		
		// creating digital signature
		String iiJson = CapToXmlUtils.createIssuerInfoXml(ii);	
		String siJson = CapToXmlUtils.createSubjectInfoXml(si);
		String arJson = CapToXmlUtils.createAccessRightsXml(accessRights);
		String riJson = CapToXmlUtils.createResourceIdXml(thingServiceId);
		String vcJson = CapToXmlUtils.createValidityConditionXml(vc);
		String revUrlJson = CapToXmlUtils.createRevocationUrlXml(REVOCATION_URL);
		String capContents = iiJson+","+siJson+","+arJson+riJson+","+vcJson+","
				+revUrlJson;
		
		// set the digital signature
		byte[] sign = CapabilityUtils.createCapSignature(myCredential, capContents);
		exCap.setDigitalSignature(sign);
		
		return exCap;
	}
	
//	private String getTokenRemote(){
//		
//	}
	
	public String getToken(String[] thingServiceIds, String subjectType,
			byte[] subjectPublicKeyInfo) throws Exception {
		Token token = new Token();
		CapabilityExternal exCap = new CapabilityExternal();
		
//		BcCredential myCredential = null;
		// find my credential using the Certificate bundle
//		ServiceTracker certTracker = authActivator.getCertTracker();
//		log.info("Found Certificate service from tracker");
//		Object certServ = certTracker.getService();
		
		myCredential = certServ.loadMyCertificate(1);
		
		// get my certificate from credential
		byte[] myCertByte = null;
		if(myCredential!=null)
			myCertByte = myCredential.getCertificateChain()[0].getEncoded();
		
		for(String thingServiceId : thingServiceIds){
			/////////////////////////////////////////////////////
			// TODO: invoke the getToken in each respective GW ID
			/////////////////////////////////////////////////////
			
			// extract the GW ID first --> from the last chars in thingServiceId
			String gwId = thingServiceId.substring(thingServiceId.indexOf("_", 
					thingServiceId.indexOf("_") + 1) + 1, thingServiceId.length());
			
			// procedures to invoke getToken from gwId 
			// for now any GW can issue a token on behalf of other GW
//			if(gwId == myGwID){
				// thingServiceId is in my/this GW
				exCap = getTokenLocal(thingServiceId, subjectType, subjectPublicKeyInfo,
						myCertByte);
//			}
//			else{
				// invoke the getToken from GW(gwId)
				
//			}
			// add the exCap to the exCapList
			token.getCapability().add(exCap);
		}
		
		// TODO: need to include all the exCap in the JSON format (JSON array)		
		return CapToXmlUtils.createTokenXml(token);
	}
	
	/**
	 * Method to form an ExternalCapability especially in Apps. installation
	 * @param thingServiceId
	 * @param subjectType
	 * @param appId
	 * @param myCertByte
	 * @return
	 * @throws JAXBException
	 * @throws OperatorCreationException
	 * @throws CMSException
	 * @throws IOException
	 */
	private CapabilityExternal getTokenAppsLocal(String thingServiceId, 
			String subjectType, String appId, byte[] myCertByte) throws Exception{
		
		log.debug("Start creating capability external...");
		CapabilityExternal exCap = new CapabilityExternal();
		
		exCap.setResourceId(thingServiceId);
		exCap.setRevocationUrl(REVOCATION_URL);
		
		// Issuer Info
		IssuerInfo ii = new IssuerInfo();
		ii.setIssuerCertificate(myCertByte);
		ii.setIssuerType(IssuerType.GATEWAY_TYPE);
		exCap.setIssuerInfo(ii);
		
		// create SubjectInfo class and then add it to the exCap
		SubjectInfo si = new SubjectInfo();
		si.setSubjectType(subjectType);
		// get SubjectPublicKeyInfo from apps certificate --> from appId
		X509CertificateHolder appsCert = appCertCatalog.getAppCertCatalog(appId);
		byte[] pubKeyInfo = appsCert.getSubjectPublicKeyInfo().getEncoded();
		si.setPublicKeyInfo(pubKeyInfo);
//		si.setPublicKeyInfo(publicKey);
		exCap.setSubjectInfo(si);
		
		// set validity condition (validity time) of the exCap
		ValidityCondition vc = new ValidityCondition();
		Timestamp ts1 = new Timestamp(System.currentTimeMillis());
		Timestamp ts2 = new Timestamp(System.currentTimeMillis() + VALIDITY_PERIOD);
		vc.setValidAfter(ts1.toString());
		vc.setValidBefore(ts2.toString());
		exCap.setValidityCondition(vc);
		
		AccessRights accessRights = new AccessRights();
		AccessRight ar1 = new AccessRight();
		ar1.setAccessType(AccessType.REALTIME_PULL);
		// create condition --> read from XML file
		log.info("Will read condition for access right from a file...");
		// right now we assume only 1 condition exists
		final File conditionFolder = new File(conditionPath);
		for(File condFile : conditionFolder.listFiles()){
			if(!condFile.isDirectory() && condFile.getName() != null && 
					(condFile.getName().endsWith(".xml") || condFile.getName().endsWith(".xml"))){
				ConditionType condition = CapabilityUtils.xmlToConditionType(condFile);
				ar1.setCondition(condition);
			}
		}
		
		accessRights.getAccessRight().add(ar1);
		exCap.setAccessRights(accessRights);
		log.info("The access rights have been set!!");
		
		// creating digital signature
		String iiJson = CapToXmlUtils.createIssuerInfoXml(ii);
		String siJson = CapToXmlUtils.createSubjectInfoXml(si);
		String arJson = CapToXmlUtils.createAccessRightsXml(accessRights);
		String riJson = CapToXmlUtils.createResourceIdXml(thingServiceId);
		String vcJson = CapToXmlUtils.createValidityConditionXml(vc);
		String revUrlJson = CapToXmlUtils.createRevocationUrlXml(REVOCATION_URL);
		String capContents = iiJson+","+siJson+","+arJson+riJson+","+vcJson+","
				+revUrlJson;
		
		// set the digital signature
		byte[] sign = CapabilityUtils.createCapSignature(myCredential, capContents);
		exCap.setDigitalSignature(sign);
		log.info("The digital signature has been generated!!");
		
		return exCap;
	}
	
	public String getTokenApp(String[] thingServiceIds, String subjectType, 
			String appId) throws JAXBException {
		// TODO: modify the way token is created according to information structure
		// provided by TaaSRM: AppId --> FeatureId --> thingServiceId
		// for now we just use one token per application
		
		Token token = new Token();
		CapabilityExternal exCap = new CapabilityExternal();
		
		log.info("Start creating token for application...");
		// find my credential using the Certificate bundle
//		ServiceTracker certTracker = authActivator.getCertTracker();
//		log.info("Found Certificate service from tracker");
//		Object certServ = certTracker.getService();
		
		myCredential = certServ.loadMyCertificate(1);
		log.debug("Successfully load my certificate");
		
		// get my certificate from credential
		byte[] myCertByte = null;
		if(myCredential!=null){
			try {
				myCertByte = myCredential.getCertificateChain()[0].getEncoded();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				log.error("Error in encoding the certificate...");
				e.printStackTrace();
			}
			log.debug("certificate encoded...");
//		for(String thingServiceId : thingServiceIds){
			// extract the GW ID first --> from the last chars in thingServiceId
//			String gwId = thingServiceId.substring(thingServiceId.indexOf("_", 
//					thingServiceId.indexOf("_") + 1) + 1, thingServiceId.length());
			
		
			// for now, the thingServiceId is replaced by appId --> one Capability
			// External for each application
			try {
				exCap = getTokenAppsLocal(appId, subjectType, appId, myCertByte);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				log.error("Error in creating capability external");
				e.printStackTrace();
				return null;
			}
			log.info("A capability external has been created...");
			
			token.getCapability().add(exCap);
		}
		
		return CapToXmlUtils.createTokenXml(token);
	}
	
	/**
	 * to validate capability's issuer certificate
	 * @param cert
	 * @param it
	 * @return
	 * @throws IOException
	 * @throws OperatorException
	 * @throws CertException
	 */
	private boolean validateIssuerCert(X509CertificateHolder cert, String it) 
			throws IOException, OperatorException, CertException{
		// validate it using the certificate from GW* 
		if(it.equals(IssuerType.GATEWAY_TYPE)){
			AsymmetricKeyParameter verKey = PublicKeyFactory.createKey(
					myCredential.getCertificateChain()[1].getSubjectPublicKeyInfo());
			if(cert.isSignatureValid(new BcECDSAContentVerifierProviderBuilder(new 
					DefaultDigestAlgorithmIdentifierFinder()).build(verKey))){
				log.info("The issuer certificate is valid!!");
				return true;
			}
		}
		// validate it using the certificate from the BETaaS Apps Store
		else if(it.equals(IssuerType.APPLICATION_TYPE)){
			X509CertificateHolder[] certList = appsCaCredential.getCertificateChain();
			AsymmetricKeyParameter verKey = PublicKeyFactory.createKey(
					certList[0].getSubjectPublicKeyInfo());
			if(cert.isSignatureValid(new BcECDSAContentVerifierProviderBuilder(new 
					DefaultDigestAlgorithmIdentifierFinder()).build(verKey))){
				log.info("The issuer certificate is valid!!");
				return true;
			}
		}
		// think about it later
		else if(it.equals(IssuerType.USER_TYPE)){
			
		}
		log.error("The issuer certificate is NOT valid!!");
		return false;
	}
	
	/**
	 * forming ReqCtx for Condition checking 
	 * @param resourceId
	 * @return
	 * @throws Exception
	 */
	private RequestCtx createReqCtx(String resourceId) throws Exception{
		// Forming List<Subject> --> for RequestCtx
		List<Subject> subjects2 = new ArrayList<Subject>();
		List<Attribute> attributes2 = new ArrayList<Attribute>();
		Attribute subjAtr2 = new Attribute(
				new URI(XACMLConstants.ATTRIBUTEID_ACCESS_SUBJECT), null, null, 
				new StringAttribute("Subject"));
		attributes2.add(subjAtr2);
		Subject subj2 = new Subject(attributes2);
		subjects2.add(subj2);
		// Forming List<Attribute> --> Environments (TimeAttribute)
		List<Attribute> environments = new ArrayList<Attribute>();
		// current time
		Attribute env = new Attribute(
				new URI(XACMLConstants.ATTRIBUTEID_CURRENT_TIME), null, null , 
				new TimeAttribute());
		environments.add(env);
		
		// Forming List<Attribute> --> Resources
		List<Attribute> resources = new ArrayList<Attribute>();
		Attribute res = new Attribute(
				new URI(XACMLConstants.ATTRIBUTEID_RESOURCE_ID), null, null, 
				new StringAttribute(resourceId));
		resources.add(res);
		
		// create RequestCtx
		return new RequestCtx(subjects2, resources, new ArrayList(), environments);
	}
	
	/**
	 * Check the condition specified in the token with the condition saved in the 
	 * GW (concerning the specific thingService)
	 * @param ct: extracted Condition from the token
	 * @param req: RequestCtx (required by XACML API)
	 * @return boolean true or false (true if the condition matches)
	 */
	private boolean checkCondition(ConditionType ct, RequestCtx req){
		boolean access = false;
		
		JAXBElement<ConditionType> jaxbCT = of.createCondition(ct);
		JAXBContext jc = null;
		try {
			jc = JAXBContext.newInstance(ConditionType.class);
			Marshaller mar = jc.createMarshaller();
			
			// create DOM Document
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	    DocumentBuilder db = dbf.newDocumentBuilder();
	    Document doc = db.newDocument();
	    
	    mar.marshal(jaxbCT, doc);
	    
	    // convert ConditionType to XML Node (org.w3c.dom.Node)
	    DOMResult res = new DOMResult();
			res.setNode(doc.getDocumentElement());
			Node conditionNode = res.getNode();
			
			// prepare for the Condition
			PolicyMetaData metadata = new PolicyMetaData(2,0); 
			VariableManager vm = new VariableManager(new HashMap(), metadata);
			Condition condition = Condition.getInstance(conditionNode, metadata, vm);
			
			// evaluate condition -- first convert RequestCtx into EvaluationCtx
			EvaluationCtx context = new BasicEvaluationCtx(req);
			EvaluationResult result = condition.evaluate(context);
			BooleanAttribute bool = (BooleanAttribute)(result.getAttributeValue());
			// get the condition evaluation result in boolean
			access = bool.getValue();
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParsingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		log.info("Checking Condition in the Access Right return: "+access);
		
		return access;
	}
	
	/**
	 * Method to check the AccessRights specified in the token with the actual
	 * access type corresponds to the service and condition
	 * @param ars: access rights specified in the token
	 * @param resourceId: thing service ID
	 * @param req: RequestCtx required by the XACML API
	 * @return
	 */
	private boolean checkAccessRight(AccessRights ars, String resourceId, 
			RequestCtx req){
		boolean access = false;
		for(AccessRight ar: ars.getAccessRight()){
			// check access type
			if(resourceId.startsWith("get")){
				// AccessType.GET
				if(ar.getAccessType().equals(AccessType.GET))
					access = true;
			}else if(resourceId.startsWith("set")){
				// AccessType.PUT or AccessType.POST
				if(ar.getAccessType().equals(AccessType.UPDATE) 
						|| ar.getAccessType().equals(AccessType.SET))
					access = true;
			}
			
			// get the Condition from ConditionType
			ConditionType ct = ar.getCondition();
			// check if the Condition is not null
			if(ct!=null){
				checkCondition(ct, req);
			}	
			else{
				// there is no condition --> always returns true
				log.info("There is no condition in the Access Right");
				access = true;
			}			
			
		}
		
		log.info("The overall access right check result is: "+access);
		
		return access;
	}
	
	/**
	 * Method to verify the token upon access request.
	 * @param token: the required token to be verified (in JSON format)
	 */
	public boolean verifyToken(String token) throws JAXBException, IOException, 
		OperatorException, CertException, CMSException {
//		boolean isValid = false;
		log.info("Start the token validation process...");
		
		Token tokenRead = CapabilityUtils.xmlToToken(token);
		
		if(tokenRead!=null){
			for(CapabilityExternal cap : tokenRead.getCapability()){
	//			CapabilityExternal cap = CapabilityUtils.jsonToExCap(token);
				log.debug("Reading capability "+cap.getResourceId());
				// issuer info
				IssuerInfo ii = cap.getIssuerInfo();
				// subject info
				SubjectInfo si = cap.getSubjectInfo();
				// access rights
				AccessRights ars = cap.getAccessRights();
				// resource ID
				String resourceId = cap.getResourceId();
				// validity condition
				ValidityCondition vc = cap.getValidityCondition();
				// revocation URL
				String revocationUrl = cap.getRevocationUrl();
				
				String iiJson = CapToXmlUtils.createIssuerInfoXml(ii);
				String siJson = CapToXmlUtils.createSubjectInfoXml(si);
				String arJson = CapToXmlUtils.createAccessRightsXml(ars);
				String riJson = CapToXmlUtils.createResourceIdXml(resourceId);
				String vcJson = CapToXmlUtils.createValidityConditionXml(vc);
				String revUrlJson = CapToXmlUtils.createRevocationUrlXml(revocationUrl);
				String capContents = iiJson+","+siJson+","+arJson+riJson+","+vcJson+","
						+revUrlJson;
				
				// decoding the issuer certificate from byte[] 
				byte[] issuerCert = ii.getIssuerCertificate();
				X509CertificateHolder cert = new X509CertificateHolder(issuerCert);
				
				// validate issuer certificate of the external capability
				if(!validateIssuerCert(cert, ii.getIssuerType())){
	//				log.error("The issuer certificate is NOT valid!!!");
					return false;
				}
				
				// check validity condition
				if(!CapabilityUtils.checkValidityCondition(vc))
					return false;
				
				// check the access rights
	//			try {
	//				if(!checkAccessRight(ars, resourceId, createReqCtx(resourceId)))
	//					return false;
	//			} catch (Exception e) {
	//				// TODO Auto-generated catch block
	//				e.printStackTrace();
	//			}
				
				// validating the Digital signature of External Capability
				AsymmetricKeyParameter pubKey = PublicKeyFactory.createKey(
						cert.getSubjectPublicKeyInfo());
				if(!CapabilityUtils.validateCapSignature(
						capContents, cap.getDigitalSignature(), pubKey)){
					return false;
				}
				log.info("Passed all the validation test..");
			}
		}
		else{
			log.error("Error in parsing the token...");
			return false;
		}
		
		log.debug("will return true...");
		
		return true;
	}
	
	public String updateToken(String token) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean revokeToken(String token) {
		// TODO Auto-generated method stub
		return false;
	}

	/** blueprint set reference to IGatewayCertificateService */
	public void setCertServ(IGatewayCertificateService certServ) {
		this.certServ = certServ;
		log.debug("Got the IGatewayCertificateService...");
	}
	
	public void setCertificatePath(String certificatePath){
		this.certPath = certificatePath;
	}
	
	public void setConditionPath(String conditionPath){
		this.conditionPath = conditionPath;
	}
}
