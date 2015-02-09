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

import java.io.ByteArrayOutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;

import eu.betaas.service.securitymanager.capability.elements.AccessRights;
import eu.betaas.service.securitymanager.capability.elements.IssuerInfo;
import eu.betaas.service.securitymanager.capability.elements.SubjectInfo;
import eu.betaas.service.securitymanager.capability.elements.ValidityCondition;
import eu.betaas.service.securitymanager.capability.model.CapabilityExternal;
import eu.betaas.service.securitymanager.capability.model.Token;

/**
 * This class contains methods that transform the token and any elements of the 
 * token into String of XML structure.
 * 
 * @author Bayu Anggorojati [ba@es.aau.dk]
 * Center for TeleInFrastruktur, Aalborg University, Denmark
 *
 */
public class CapToXmlUtils {
	
	/**
	 * A method to create an XML (in String) out of a Token object
	 * @param token: the Token object
	 * @return
	 * @throws JAXBException
	 */
	public static String createTokenXml(Token token) throws JAXBException{
		JAXBContext jc = JAXBContext.newInstance(Token.class);
		JAXBElement<Token> jaxbToken = new JAXBElement<Token>
			(new QName("Token"), Token.class, token);
		Marshaller m = jc.createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, false);
//		m.setProperty("eclipselink.media-type", "application/json");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		m.marshal(jaxbToken, baos);
		
		String tString = baos.toString();
//		ecString = ecString.substring(1, ecString.length()-1);
		
		return tString;
	}
	
	/**
	 * A method to create an XML (in String) out of an External Capability object
	 * @param exCap: the CapabilityExternal object
	 * @return
	 * @throws JAXBException
	 */
	public static String createExcapXml(CapabilityExternal exCap) throws JAXBException{
		JAXBContext jc = JAXBContext.newInstance(CapabilityExternal.class);
		JAXBElement<CapabilityExternal> jaxbEC = new JAXBElement<CapabilityExternal>
			(new QName("CapabilityExternal"), CapabilityExternal.class, exCap);
		Marshaller m = jc.createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, false);
//		m.setProperty("eclipselink.media-type", "application/json");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		m.marshal(jaxbEC, baos);
		
		String ecString = baos.toString();
//		ecString = ecString.substring(1, ecString.length()-1);
		
		return ecString;
	}
	
	/**
	 * A method to create an XML (in String) out of an AccessRights object
	 * @param ar: the AccessRights object
	 * @return
	 * @throws JAXBException
	 */
	public static String createAccessRightsXml(AccessRights ar) throws JAXBException{
		JAXBContext jc = JAXBContext.newInstance(AccessRights.class);
		JAXBElement<AccessRights> jaxbAR = new JAXBElement<AccessRights>
			(new QName("AccessRights"), AccessRights.class, ar);
		Marshaller m = jc.createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, false);
//		m.setProperty("eclipselink.media-type", "application/json");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		m.marshal(jaxbAR, baos);
		
		String arString = baos.toString();
//		arString = arString.substring(1, arString.length()-1);
		
		return arString;
	}
	
	/**
	 * A method to create an XML (in String) out of an IssuerInfo object
	 * @param ii: the IssuerInfo object
	 * @return
	 * @throws JAXBException
	 */
	public static String createIssuerInfoXml(IssuerInfo ii) throws JAXBException{
		JAXBContext jc = JAXBContext.newInstance(IssuerInfo.class, CapabilityExternal.class);
		JAXBElement<IssuerInfo> jaxbIi = new JAXBElement<IssuerInfo>
			(new QName("IssuerInfo"), IssuerInfo.class, ii);
		Marshaller m = jc.createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, false);
//		m.setProperty("eclipselink.media-type", "application/json");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		m.marshal(jaxbIi, baos);
		
		String iiString = baos.toString();
//		iiString = iiString.substring(1, iiString.length()-1);
		
		return iiString;
	}
	
	/**
	 * A method to create an XML (in String) out of a SubjectInfo object
	 * @param si: the SubjectInfo object
	 * @return
	 * @throws JAXBException
	 */
	public static String createSubjectInfoXml(SubjectInfo si) throws JAXBException{
		JAXBContext jc = JAXBContext.newInstance(SubjectInfo.class);
		JAXBElement<SubjectInfo> jaxbSi = 
				new JAXBElement<SubjectInfo>(new QName("SubjectInfo"), SubjectInfo.class, si);
		Marshaller m = jc.createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, false);
//		m.setProperty("eclipselink.media-type", "application/json");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		m.marshal(jaxbSi, baos);
		
		String siString = baos.toString();
//		siString = siString.substring(1, siString.length()-1);
		
		return siString;
	}
	
	/**
	 * A method to create an XML (in String) out of a ValidityCondition object
	 * @param vc
	 * @return
	 * @throws JAXBException
	 */
	public static String createValidityConditionXml(ValidityCondition vc) 
			throws JAXBException{
		JAXBContext jc = JAXBContext.newInstance(ValidityCondition.class);
		JAXBElement<ValidityCondition> jaxbVc = new JAXBElement<ValidityCondition>
			(new QName("ValidityCondition"), ValidityCondition.class, vc); 
		Marshaller m = jc.createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, false);
//		m.setProperty("eclipselink.media-type", "application/json");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		m.marshal(jaxbVc, baos);
		
		String vcString = baos.toString();
//		vcString = vcString.substring(1, vcString.length()-1);
		
		return vcString;
	}
	
	/**
	 * A method to create an XML (in String) out of a resourceId
	 * @param resourceId
	 * @return
	 */
	public static String createResourceIdXml(String resourceId){
		return "<ResourceId>"+resourceId+"</ResourceId>";
	}
	
	/**
	 * A method to create an XML (in String) out of a revocationUrl
	 * @param revocationUrl
	 * @return
	 */
	public static String createRevocationUrlXml(String revocationUrl){
		return "<RevocationUrl>"+revocationUrl+"</RevocationUrl>";
	}
}
