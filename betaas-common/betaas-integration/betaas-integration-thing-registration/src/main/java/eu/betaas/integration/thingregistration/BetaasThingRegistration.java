package eu.betaas.integration.thingregistration;

import org.apache.log4j.Logger;

import eu.betaas.adaptation.contextmanager.api.SemanticParserAdaptator;

public class BetaasThingRegistration {

	Logger log;
	SemanticParserAdaptator service;
	
	
	public void setService(SemanticParserAdaptator service) {
		this.service = service;
	}


	public void setupBundle(){
		log = Logger.getLogger("betaas.integration");
		log.info("Started bundle");
		
		service.getRealTimeAdaptedInformation();
		log.info("Invoked RealTimeAdapted Information bundle");
	}
	
	
}
