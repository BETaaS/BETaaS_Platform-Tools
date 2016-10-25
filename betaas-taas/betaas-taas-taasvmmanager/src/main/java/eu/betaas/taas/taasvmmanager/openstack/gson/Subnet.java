package eu.betaas.taas.taasvmmanager.openstack.gson;

public class Subnet {
	private String name;
	private boolean enable_dhcp;
	private String network_id;
	private String tenant_id;
	private String[] dns_nameservers;
	private AllocationPool[] allocation_pools;
	private String[] host_routes;
	private int ip_version;
	private String gateway_ip;
	private String cidr;
	private String id;
	
	public Subnet () {}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isEnable_dhcp() {
		return enable_dhcp;
	}

	public void setEnable_dhcp(boolean enable_dhcp) {
		this.enable_dhcp = enable_dhcp;
	}

	public String getNetwork_id() {
		return network_id;
	}

	public void setNetwork_id(String network_id) {
		this.network_id = network_id;
	}

	public String getTenant_id() {
		return tenant_id;
	}

	public void setTenant_id(String tenant_id) {
		this.tenant_id = tenant_id;
	}

	public String[] getDns_nameservers() {
		return dns_nameservers;
	}

	public void setDns_nameservers(String[] dns_nameservers) {
		this.dns_nameservers = dns_nameservers;
	}

	public AllocationPool[] getAllocation_pools() {
		return allocation_pools;
	}

	public void setAllocation_pools(AllocationPool[] allocation_pools) {
		this.allocation_pools = allocation_pools;
	}

	public String[] getHost_routes() {
		return host_routes;
	}

	public void setHost_routes(String[] host_routes) {
		this.host_routes = host_routes;
	}

	public int getIp_version() {
		return ip_version;
	}

	public void setIp_version(int ip_version) {
		this.ip_version = ip_version;
	}

	public String getGateway_ip() {
		return gateway_ip;
	}

	public void setGateway_ip(String gateway_ip) {
		this.gateway_ip = gateway_ip;
	}

	public String getCidr() {
		return cidr;
	}

	public void setCidr(String cidr) {
		this.cidr = cidr;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public class AllocationPool {
		private String start;
		private String end;
		
		public AllocationPool () {}

		public String getStart() {
			return start;
		}

		public void setStart(String start) {
			this.start = start;
		}

		public String getEnd() {
			return end;
		}

		public void setEnd(String end) {
			this.end = end;
		}
	}
}
