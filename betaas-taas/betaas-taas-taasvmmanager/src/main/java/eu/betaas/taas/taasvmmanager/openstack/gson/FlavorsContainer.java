package eu.betaas.taas.taasvmmanager.openstack.gson;

public class FlavorsContainer {
	private Flavor[] flavors;
	
	public FlavorsContainer () {}

	public Flavor[] getFlavors() {
		return flavors;
	}

	public void setFlavors(Flavor[] flavors) {
		this.flavors = flavors;
	}
}
