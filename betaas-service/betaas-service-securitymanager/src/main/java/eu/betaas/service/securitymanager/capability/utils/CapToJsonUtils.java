package eu.betaas.service.securitymanager.capability.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import de.odysseus.staxon.json.JsonXMLConfig;
import de.odysseus.staxon.json.JsonXMLConfigBuilder;
import de.odysseus.staxon.json.JsonXMLOutputFactory;
import de.odysseus.staxon.json.stream.JsonStreamFactory;
import de.odysseus.staxon.json.stream.impl.JsonStreamFactoryImpl;
import de.odysseus.staxon.json.util.XMLMultipleStreamWriter;
import eu.betaas.service.securitymanager.capability.elements.AccessRights;
import eu.betaas.service.securitymanager.capability.elements.IssuerInfo;
import eu.betaas.service.securitymanager.capability.elements.SubjectInfo;
import eu.betaas.service.securitymanager.capability.elements.ValidityCondition;
import eu.betaas.service.securitymanager.capability.model.CapabilityExternal;
import eu.betaas.service.securitymanager.capability.model.Token;

public class CapToJsonUtils {	
	private static JsonXMLConfig config = new JsonXMLConfigBuilder().build();
	
	private static JsonStreamFactory stream = new JsonStreamFactoryImpl();
	
	public static String createTokenJson(Token token){
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			JAXBContext jc = JAXBContext.newInstance(Token.class);
			JAXBElement<Token> jaxbToken = new JAXBElement<Token>
			(new QName("Token"), Token.class, token);
			// obtain a StAX writer from StAXON
			XMLStreamWriter writer = new JsonXMLOutputFactory(config).createXMLStreamWriter(baos);
			writer = new XMLMultipleStreamWriter(writer, true, "CapabilityExternal", 
					"CapabilityIssuer","AccessRight");
			jc.createMarshaller().marshal(jaxbToken, writer);
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (XMLStreamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (FactoryConfigurationError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		String tokenString = baos.toString();
		return tokenString;
	}
	
	public static String createExcapJson(CapabilityExternal exCap) {		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			JAXBContext jc = JAXBContext.newInstance(CapabilityExternal.class);
			JAXBElement<CapabilityExternal> jaxbEC = new JAXBElement<CapabilityExternal>
			(new QName("CapabilityExternal"), CapabilityExternal.class, exCap);
			// obtain a StAX writer from StAXON
			XMLStreamWriter writer = new JsonXMLOutputFactory(config).createXMLStreamWriter(baos);
			writer = new XMLMultipleStreamWriter(writer, true, "CapabilityIssuer","AccessRight");
			
			jc.createMarshaller().marshal(jaxbEC, writer);
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (XMLStreamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (FactoryConfigurationError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}		
		
		String ecString = baos.toString();
//		System.out.println(ecString);
//		ecString = ecString.substring(1, ecString.length()-1);
		
		return ecString;
	}
	
	public static String createAccessRightsJson(AccessRights ar) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			JAXBContext jc = JAXBContext.newInstance(AccessRights.class);
			JAXBElement<AccessRights> jaxbAR = new JAXBElement<AccessRights>
			(new QName("AccessRights"), AccessRights.class, ar);
			// obtain a StAX writer from StAXON
			XMLStreamWriter writer = new JsonXMLOutputFactory(config).createXMLStreamWriter(baos);
			writer = new XMLMultipleStreamWriter(writer, true, "AccessRight");
			
			jc.createMarshaller().marshal(jaxbAR, writer);
			
			String arString = baos.toString();
			baos.close();
//			System.out.println("Original AccessRights JSON:\n"+arString);			
			arString = arString.substring(1, arString.length()-1);
			
			return arString;
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}	
		catch (XMLStreamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (FactoryConfigurationError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	public static String createIssuerInfoJson(IssuerInfo ii) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			JAXBContext jc = JAXBContext.newInstance(IssuerInfo.class, CapabilityExternal.class);
			JAXBElement<IssuerInfo> jaxbIi = new JAXBElement<IssuerInfo>
			(new QName("IssuerInfo"), IssuerInfo.class, ii);
			// obtain a StAX writer from StAXON
			XMLStreamWriter writer = new JsonXMLOutputFactory(config).createXMLStreamWriter(baos);		
			writer = new XMLMultipleStreamWriter(writer, true, "CapabilityIssuer");
			
			jc.createMarshaller().marshal(jaxbIi, writer);			
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (XMLStreamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (FactoryConfigurationError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
		String iiString = baos.toString();
//		System.out.println("IssuerInfo original: "+iiString);
		iiString = iiString.substring(1, iiString.length()-1);
//		System.out.println("IssuerInfo edit: "+iiString);
		
		return iiString;
	}
	
	public static String createSubjectInfoJson(SubjectInfo si){
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			JAXBContext jc = JAXBContext.newInstance(SubjectInfo.class);
			JAXBElement<SubjectInfo> jaxbSi = new JAXBElement<SubjectInfo>
			(new QName("SubjectInfo"), SubjectInfo.class, si);
			// obtain a StAX writer from StAXON
			XMLStreamWriter writer = new JsonXMLOutputFactory(config).createXMLStreamWriter(baos);		
					
			jc.createMarshaller().marshal(jaxbSi, writer);			
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (XMLStreamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (FactoryConfigurationError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
		String siString = baos.toString();
		siString = siString.substring(1, siString.length()-1);
		
		return siString;
	}
	
	public static String createValidityConditionJson(ValidityCondition vc) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			JAXBContext jc = JAXBContext.newInstance(ValidityCondition.class);
			JAXBElement<ValidityCondition> jaxbVc = new JAXBElement<ValidityCondition>
			(new QName("ValidityCondition"), ValidityCondition.class, vc); 
			// obtain a StAX writer from StAXON
			XMLStreamWriter writer = new JsonXMLOutputFactory(config).createXMLStreamWriter(baos);		
					
			jc.createMarshaller().marshal(jaxbVc, writer);			
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (XMLStreamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (FactoryConfigurationError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
				
		String vcString = baos.toString();
//		System.out.println("Original ValidityCondition:\n"+vcString);
		
		vcString = vcString.substring(1, vcString.length()-1);
		
		return vcString;
	}
	
	public static String createResourceIdJson(String resourceId){
		return "\"ResourceId\":\""+resourceId+"\"";
	}
	
	public static String createRevocationUrlJson(String revocationUrl){
		return "\"RevocationUrl\":\""+revocationUrl+"\"";
	}
}
