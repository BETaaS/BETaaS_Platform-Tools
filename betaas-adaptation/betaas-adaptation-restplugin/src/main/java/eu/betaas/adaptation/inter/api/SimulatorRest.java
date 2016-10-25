package eu.betaas.adaptation.inter.api;

import java.util.List;

import eu.betaas.taas.bigdatamanager.database.hibernate.data.SimulatedThing;



public interface SimulatorRest {
	
	public void deleteThing(String thingId);
	
	public void createThing(String thing);
	
	public List<SimulatedThing> getThings();
	
}
