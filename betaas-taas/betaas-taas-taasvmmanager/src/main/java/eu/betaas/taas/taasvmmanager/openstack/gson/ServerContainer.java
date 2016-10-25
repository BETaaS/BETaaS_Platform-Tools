package eu.betaas.taas.taasvmmanager.openstack.gson;

import com.google.gson.annotations.SerializedName;

public class ServerContainer {
	private Server server;
	
	public ServerContainer() {}
	
	public Server getServer() {
		return server;
	}

	public void setServer(Server server) {
		this.server = server;
	}

	public class Server {
		private String accessIPv4;
		private String accessIPv6;
		private AddressContainer addresses;
		private String created;
		private Flavor flavor;
		private String hostId;
		private String id;
		private Image image;
		private Link[] links;
		private String name;
		private int progress;
		private String status;
		private String tenant_id;
		private String updated;
		private String user_id;
		
		public Server () {}
		
		public String getAccessIPv4() {
			return accessIPv4;
		}

		public void setAccessIPv4(String accessIPv4) {
			this.accessIPv4 = accessIPv4;
		}

		public String getAccessIPv6() {
			return accessIPv6;
		}

		public void setAccessIPv6(String accessIPv6) {
			this.accessIPv6 = accessIPv6;
		}

		public AddressContainer getAddresses() {
			return addresses;
		}

		public void setAddresses(AddressContainer addresses) {
			this.addresses = addresses;
		}

		public String getCreated() {
			return created;
		}

		public void setCreated(String created) {
			this.created = created;
		}

		public Flavor getFlavor() {
			return flavor;
		}

		public void setFlavor(Flavor flavor) {
			this.flavor = flavor;
		}

		public String getHostId() {
			return hostId;
		}

		public void setHostId(String hostId) {
			this.hostId = hostId;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public Image getImage() {
			return image;
		}

		public void setImage(Image image) {
			this.image = image;
		}

		public Link[] getLinks() {
			return links;
		}

		public void setLinks(Link[] links) {
			this.links = links;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public int getProgress() {
			return progress;
		}

		public void setProgress(int progress) {
			this.progress = progress;
		}

		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}

		public String getTenant_id() {
			return tenant_id;
		}

		public void setTenant_id(String tenant_id) {
			this.tenant_id = tenant_id;
		}

		public String getUpdated() {
			return updated;
		}

		public void setUpdated(String updated) {
			this.updated = updated;
		}

		public String getUser_id() {
			return user_id;
		}

		public void setUser_id(String user_id) {
			this.user_id = user_id;
		}

		public class AddressContainer {
			@SerializedName("private")
			private Address[] private_addr;
			@SerializedName("public")
			private Address[] public_addr;
			
			public AddressContainer () {}
		
			public Address[] getPrivate_addr() {
				return private_addr;
			}

			public void setPrivate_addr(Address[] private_addr) {
				this.private_addr = private_addr;
			}

			public Address[] getPublic_addr() {
				return public_addr;
			}

			public void setPublic_addr(Address[] public_addr) {
				this.public_addr = public_addr;
			}

			public class Address {
				private String addr;
				private int version;
				
				public Address() {}

				public String getAddr() {
					return addr;
				}

				public void setAddr(String addr) {
					this.addr = addr;
				}

				public int getVersion() {
					return version;
				}

				public void setVersion(int version) {
					this.version = version;
				}
			}
		}
		
		public class Flavor {
			private String id;
			private Link[] links;
			
			public Flavor() {}

			public String getId() {
				return id;
			}

			public void setId(String id) {
				this.id = id;
			}

			public Link[] getLinks() {
				return links;
			}

			public void setLinks(Link[] links) {
				this.links = links;
			}
		}
		
		public class Image {
			private String id;
			private Link[] links;
			
			public Image() {}

			public String getId() {
				return id;
			}

			public void setId(String id) {
				this.id = id;
			}

			public Link[] getLinks() {
				return links;
			}

			public void setLinks(Link[] links) {
				this.links = links;
			}
		}
		
		public class Link {
			private String href;
			private String rel;
			
			public Link() {}

			public String getHref() {
				return href;
			}

			public void setHref(String href) {
				this.href = href;
			}

			public String getRel() {
				return rel;
			}

			public void setRel(String rel) {
				this.rel = rel;
			}
		}
	}
}
