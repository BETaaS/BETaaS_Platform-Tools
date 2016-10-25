package eu.betaas.taas.taasvmmanager.openstack.gson;

import com.google.gson.annotations.SerializedName;

public class Network {
	private String   status;
	private String[] subnets;
	private String   name;
	
	@SerializedName("provider:physical_network")
	private String physical_network;
	private boolean admin_state_up;
	private String tenant_id;
	
	@SerializedName("provider:network_type")
	private String network_type;
	
	@SerializedName("router:external")
	private boolean external;
	private boolean shared;
	private String id;
	
	@SerializedName("provider:segmentation_id")
	private String segmentation_id;

	private Segment[] segments;
	
	public Network () {}
	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String[] getSubnets() {
		return subnets;
	}

	public void setSubnets(String[] subnets) {
		this.subnets = subnets;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhysical_network() {
		return physical_network;
	}

	public void setPhysical_network(String physical_network) {
		this.physical_network = physical_network;
	}

	public boolean getAdmin_state_up() {
		return admin_state_up;
	}

	public void setAdmin_state_up(boolean admin_state_up) {
		this.admin_state_up = admin_state_up;
	}

	public String getTenant_id() {
		return tenant_id;
	}

	public void setTenant_id(String tenant_id) {
		this.tenant_id = tenant_id;
	}

	public String getNetwork_type() {
		return network_type;
	}

	public void setNetwork_type(String network_type) {
		this.network_type = network_type;
	}

	public boolean getExternal() {
		return external;
	}

	public void setExternal(boolean external) {
		this.external = external;
	}

	public boolean getShared() {
		return shared;
	}

	public void setShared(boolean shared) {
		this.shared = shared;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSegmentation_id() {
		return segmentation_id;
	}

	public void setSegmentation_id(String segmentation_id) {
		this.segmentation_id = segmentation_id;
	}
	
	public Segment[] getSegments() {
		return segments;
	}

	public void setSegments(Segment[] subnets) {
		this.segments = segments;
	}
	
	public class Segment {
		@SerializedName("provider:segmentation_id")
		private String segmentation_id;
		
		@SerializedName("provider:physical_network")
		private String physical_network;
		
		@SerializedName("provider:network_type")
		private String network_type;
		
		public Segment () {}

		public String getSegmentation_id() {
			return segmentation_id;
		}

		public void setSegmentation_id(String segmentation_id) {
			this.segmentation_id = segmentation_id;
		}

		public String getPhysical_network() {
			return physical_network;
		}

		public void setPhysical_network(String physical_network) {
			this.physical_network = physical_network;
		}

		public String getNetwork_type() {
			return network_type;
		}

		public void setNetwork_type(String network_type) {
			this.network_type = network_type;
		}
	}
}
