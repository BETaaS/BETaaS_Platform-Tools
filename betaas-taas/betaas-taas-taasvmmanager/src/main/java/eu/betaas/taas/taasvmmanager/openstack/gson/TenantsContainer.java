package eu.betaas.taas.taasvmmanager.openstack.gson;

public class TenantsContainer {
	private Tenant[] tenants;
	private Link[]   tenants_links;
	
	public TenantsContainer() {}
	
	public Tenant[] getTenants() {
		return tenants;
	}

	public void setTenants(Tenant[] tenants) {
		this.tenants = tenants;
	}

	public Link[] getTenants_links() {
		return tenants_links;
	}

	public void setTenants_links(Link[] tenants_links) {
		this.tenants_links = tenants_links;
	}

	public class Tenant {
		private String id;
		private String name;
		private String description;
		private boolean enabled;
		
		public Tenant() {}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public boolean isEnabled() {
			return enabled;
		}

		public void setEnabled(boolean enabled) {
			this.enabled = enabled;
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
