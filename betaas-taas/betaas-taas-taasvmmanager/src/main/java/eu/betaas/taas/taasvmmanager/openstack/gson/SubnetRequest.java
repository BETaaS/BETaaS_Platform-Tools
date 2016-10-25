package eu.betaas.taas.taasvmmanager.openstack.gson;

public class SubnetRequest {
	private Subnet subnet;
	
	public SubnetRequest () {}
	
	public Subnet getSubnet() {
		return subnet;
	}

	public void setSubnet(Subnet subnet) {
		this.subnet = subnet;
	}

	public class Subnet {
		private String name;
		private String network_id;
		private int    ip_version;
		private String cidr;
		
		public Subnet () {}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getNetwork_id() {
			return network_id;
		}

		public void setNetwork_id(String network_id) {
			this.network_id = network_id;
		}

		public int getIp_version() {
			return ip_version;
		}

		public void setIp_version(int ip_version) {
			this.ip_version = ip_version;
		}

		public String getCidr() {
			return cidr;
		}

		public void setCidr(String cidr) {
			this.cidr = cidr;
		}
	}
}
