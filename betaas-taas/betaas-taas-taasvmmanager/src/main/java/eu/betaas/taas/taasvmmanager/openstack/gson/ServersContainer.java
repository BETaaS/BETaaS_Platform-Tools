package eu.betaas.taas.taasvmmanager.openstack.gson;

public class ServersContainer {
	private Server[] servers;
	
	public ServersContainer() {}
	
	public Server[] getServers() {
		return servers;
	}

	public void setServers(Server[] servers) {
		this.servers = servers;
	}

	public class Server {
		private String id;
		private Link[] links;
		private String name;
		
		public Server() {}
		
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

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
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
