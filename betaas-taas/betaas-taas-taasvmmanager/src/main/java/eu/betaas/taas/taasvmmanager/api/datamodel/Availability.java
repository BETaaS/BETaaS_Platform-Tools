package eu.betaas.taas.taasvmmanager.api.datamodel;

import java.util.HashMap;

public class Availability {
	public static enum Situation {INTERNAL_CLOUD, EXTERNAL_CLOUD};
	private Situation situation;
	private HashMap<InstanceType, Long> availableInstances;
	private HashMap<Flavor, Long> availableFlavors;
	
	public Availability() {}

	public Situation getSituation() {
		return situation;
	}

	public void setSituation(Situation situation) {
		this.situation = situation;
	}

	public HashMap<InstanceType, Long> getAvailableInstances() {
		return availableInstances;
	}

	public void setAvailableInstances(
			HashMap<InstanceType, Long> availableInstances) {
		this.availableInstances = availableInstances;
	}
	
	public HashMap<Flavor, Long> getAvailableFlavors() {
		return availableFlavors;
	}

	public void setAvailableFlavors(HashMap<Flavor, Long> availableFlavors) {
		this.availableFlavors = availableFlavors;
	}

	public void setAvailableInstance(InstanceType type, Long available) {
		if (availableInstances == null)
			availableInstances = new HashMap<InstanceType, Long>();
		
		availableInstances.put(type, available);
	}
	
	public void setAvailableFlavor(Flavor type, Long available) {
		if (availableFlavors == null)
			availableFlavors = new HashMap<Flavor, Long>();
		
		availableFlavors.put(type, available);
	}
}
