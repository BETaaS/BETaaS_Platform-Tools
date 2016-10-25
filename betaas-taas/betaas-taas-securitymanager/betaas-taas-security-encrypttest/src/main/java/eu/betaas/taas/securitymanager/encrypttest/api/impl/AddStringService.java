package eu.betaas.taas.securitymanager.encrypttest.api.impl;

import org.apache.log4j.Logger;

import eu.betaas.taas.securitymanager.core.service.ISecGWCommService;
import eu.betaas.taas.securitymanager.encrypttest.api.IAddStringService;

public class AddStringService implements IAddStringService {
	Logger log = Logger.getLogger("betaas.taas.securitymanager");	
	
	private ISecGWCommService secCommService;
	
	public String helloName(String name, String senderGwId) {
		log.debug("Sender GW ID: "+senderGwId);
		// first decrypt the received String "name"
		String nameDecrypt = secCommService.doDecryptData(senderGwId, name);
		log.debug("The received name: "+nameDecrypt);
		// send back "Hello " + name +"!!"
		String helloName = "Hello "+nameDecrypt+"!!"; 
		// encrypt the String to be sent
		String helloNameEncr = secCommService.doEncryptData(senderGwId, helloName);
		
		return helloNameEncr;
	}

	public String concatenateString(String one, String two, String senderGwId) {
		log.debug("Sender GW ID: "+senderGwId);
		// first decrypt the received String "one" and then "two"
		String oneDecrypt = secCommService.doDecryptData(senderGwId, one);
		String twoDecrypt = secCommService.doDecryptData(senderGwId, two);
		log.debug("The received strings: "+oneDecrypt+" and "+twoDecrypt);
		// send back "This is "+one+two
		String combine = "This is "+oneDecrypt+twoDecrypt;
		// encrypt the final String to be sent
		String concatenateEncr = secCommService.doEncryptData(senderGwId, combine);
		
		return concatenateEncr;
	}

	public String suffleString(String one, String two) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	
	/** Get ISecGWCommService from blueprint */
	public void setSecCommCoreService(ISecGWCommService secCommCoreService){
		log.debug("Set the ISecGWCommService from blueprint...");
		this.secCommService = secCommCoreService;
	}
	
}
