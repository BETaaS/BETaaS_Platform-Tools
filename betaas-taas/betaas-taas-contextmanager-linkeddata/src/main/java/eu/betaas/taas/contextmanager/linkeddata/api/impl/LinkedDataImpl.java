package eu.betaas.taas.contextmanager.linkeddata.api.impl;

import org.apache.log4j.Logger;

import eu.betaas.taas.contextmanager.linkeddata.api.LinkedData;
import eu.betaas.taas.contextmanager.linkeddata.semantics.ThingsManager;

public class LinkedDataImpl implements LinkedData {

	private String gwId;
	private Logger logger = Logger.getLogger("betaas.taas");
	
	public void setupService(){
		logger.info("[LinkedDataImpl] Service started");
	}
	
	public void notifyAddedThing(String idThingService) {
		ThingsManager manager = new ThingsManager();
		manager.updateNewDcatModel(idThingService);
	}

	public void notifyRemovedThing(String idThingService) {
		ThingsManager manager = new ThingsManager();
		manager.updateRemoveDcatModel(idThingService);
	}
	
	public void setGwId(String gwId) {
		this.gwId = gwId;
	}
}
