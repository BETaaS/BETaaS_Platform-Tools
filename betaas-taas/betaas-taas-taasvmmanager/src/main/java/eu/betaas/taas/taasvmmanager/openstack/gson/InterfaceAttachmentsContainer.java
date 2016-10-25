package eu.betaas.taas.taasvmmanager.openstack.gson;

public class InterfaceAttachmentsContainer {
	private InterfaceAttachment[] interfaceAttachments;
	
	public InterfaceAttachmentsContainer() {}
	
	public InterfaceAttachment[] getInterfaceAttachments() {
		return interfaceAttachments;
	}

	public void setInterfaceAttachments(InterfaceAttachment[] interfaceAttachments) {
		this.interfaceAttachments = interfaceAttachments;
	}

	public class InterfaceAttachment {
		private String port_state;
		private FixedIP[] fixed_ips;
		private String net_id;
		private String port_id;
		private String mac_addr;
		
		public InterfaceAttachment() {}
		
		public String getPort_state() {
			return port_state;
		}

		public void setPort_state(String port_state) {
			this.port_state = port_state;
		}

		public FixedIP[] getFixed_ips() {
			return fixed_ips;
		}

		public void setFixed_ips(FixedIP[] fixed_ips) {
			this.fixed_ips = fixed_ips;
		}

		public String getNet_id() {
			return net_id;
		}

		public void setNet_id(String net_id) {
			this.net_id = net_id;
		}

		public String getPort_id() {
			return port_id;
		}

		public void setPort_id(String port_id) {
			this.port_id = port_id;
		}

		public String getMac_addr() {
			return mac_addr;
		}

		public void setMac_addr(String mac_addr) {
			this.mac_addr = mac_addr;
		}

		public class FixedIP {
			private String subnet_id;
			private String ip_address;
			
			public FixedIP () {}

			public String getSubnet_id() {
				return subnet_id;
			}

			public void setSubnet_id(String subnet_id) {
				this.subnet_id = subnet_id;
			}

			public String getIp_address() {
				return ip_address;
			}

			public void setIp_address(String ip_address) {
				this.ip_address = ip_address;
			}
		}
	}
}
