package eu.betaas.taas.contextmanager.linkeddata.api;

public interface LinkedData {
	public void notifyAddedThing(String idThingService);
	public void notifyRemovedThing(String idThingService);
}
