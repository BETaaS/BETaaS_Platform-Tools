package eu.betaas.taas.taasvmmanager.openstack.gson;

public class NetworkRequestContainer {

	private Network network;
	
	public NetworkRequestContainer () {}
	
	public Network getNetwork() {
		return network;
	}

	public void setNetwork(Network network) {
		this.network = network;
	}

	public class Network {
		private String   name;
		private boolean admin_state_up;
		
		public Network () {}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public boolean isAdmin_state_up() {
			return admin_state_up;
		}

		public void setAdmin_state_up(boolean admin_state_up) {
			this.admin_state_up = admin_state_up;
		}
	}
}
