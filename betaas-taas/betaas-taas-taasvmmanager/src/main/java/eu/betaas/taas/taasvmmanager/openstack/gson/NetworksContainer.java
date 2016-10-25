package eu.betaas.taas.taasvmmanager.openstack.gson;

public class NetworksContainer {
	private Network[] networks;
	
	public NetworksContainer () {}

	public Network[] getNetworks() {
		return networks;
	}

	public void setNetworks(Network[] networks) {
		this.networks = networks;
	}
}
