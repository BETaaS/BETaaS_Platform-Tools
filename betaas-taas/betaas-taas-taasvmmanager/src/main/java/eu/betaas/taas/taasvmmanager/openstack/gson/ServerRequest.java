package eu.betaas.taas.taasvmmanager.openstack.gson;

public class ServerRequest {
	
	private Server server;
	
	public ServerRequest () {}
	
	public Server getServer() {
		return server;
	}

	public void setServer(Server server) {
		this.server = server;
	}

	public class Server {
		private String name;
		private String imageRef;
		private String flavorRef;
		private Network[] networks;
		private BlockDevice[] block_device_mapping_v2;
		
		public Server () {}
		
		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getImageRef() {
			return imageRef;
		}

		public void setImageRef(String imageRef) {
			this.imageRef = imageRef;
		}

		public String getFlavorRef() {
			return flavorRef;
		}

		public void setFlavorRef(String flavorRef) {
			this.flavorRef = flavorRef;
		}
		
		public Network[] getNetworks() {
			return networks;
		}
		
		public void setNetworks(Network[] networks) {
			this.networks = networks;
		}

		public BlockDevice[] getBlock_device_mapping_v2() {
			return block_device_mapping_v2;
		}

		public void setBlock_device_mapping_v2(
				BlockDevice[] block_device_mapping_v2) {
			this.block_device_mapping_v2 = block_device_mapping_v2;
		}

		public class Network {
			private String uuid;
			private String port;
			private String fixed_ip;

			public Network () {}

			public String getUuid() {
				return uuid;
			}

			public void setUuid(String uuid) {
				this.uuid = uuid;
			}

			public String getPort() {
				return port;
			}

			public void setPort(String port) {
				this.port = port;
			}

			public String getFixed_ip() {
				return fixed_ip;
			}

			public void setFixed_ip(String fixed_ip) {
				this.fixed_ip = fixed_ip;
			}
		}
		
		public class BlockDevice {
			private String device_name;
			private String source_type;
			private String destination_type;
			private String delete_on_termination;
			private String guest_format;
			private String boot_index;
			private String uuid;
			
			public BlockDevice () {}

			public String getDevice_name() {
				return device_name;
			}

			public void setDevice_name(String device_name) {
				this.device_name = device_name;
			}

			public String getSource_type() {
				return source_type;
			}

			public void setSource_type(String source_type) {
				this.source_type = source_type;
			}

			public String getDestination_type() {
				return destination_type;
			}

			public void setDestination_type(String destination_type) {
				this.destination_type = destination_type;
			}

			public String getDelete_on_termination() {
				return delete_on_termination;
			}

			public void setDelete_on_termination(String delete_on_termination) {
				this.delete_on_termination = delete_on_termination;
			}

			public String getGuest_format() {
				return guest_format;
			}

			public void setGuest_format(String guest_format) {
				this.guest_format = guest_format;
			}

			public String getBoot_index() {
				return boot_index;
			}

			public void setBoot_index(String boot_index) {
				this.boot_index = boot_index;
			}

			public String getUuid() {
				return uuid;
			}

			public void setUuid(String uuid) {
				this.uuid = uuid;
			}
		}
	}
}
