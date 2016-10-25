package eu.betaas.taas.taasvmmanager.openstack.gson;

public class SubnetsContainer {
	private Subnet[] subnets;
	
	public SubnetsContainer() {}

	public Subnet[] getSubnets() {
		return subnets;
	}

	public void setSubnets(Subnet[] subnets) {
		this.subnets = subnets;
	}
}
