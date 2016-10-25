package eu.betaas.taas.taasvmmanager.openstack.gson;

public class AccessContainer {

	private Access access;
	
	public AccessContainer() {}
	
	public Access getAccess() {
		return access;
	}

	public void setAccess(Access access) {
		this.access = access;
	}

	public class Access {
		private Token token;
		private ServiceCatalog[] serviceCatalog;
		private User user;
		private Metadata metadata;
		
		public Access () {}
		
		public Token getToken() {
			return token;
		}

		public void setToken(Token token) {
			this.token = token;
		}

		public ServiceCatalog[] getServiceCatalog() {
			return serviceCatalog;
		}

		public void setServiceCatalog(ServiceCatalog[] serviceCatalog) {
			this.serviceCatalog = serviceCatalog;
		}

		public User getUser() {
			return user;
		}

		public void setUser(User user) {
			this.user = user;
		}

		public Metadata getMetadata() {
			return metadata;
		}

		public void setMetadata(Metadata metadata) {
			this.metadata = metadata;
		}

		public class Token {
			private String issued_at;
			private String expires;
			private String id;
			private Tenant tenant;
			
			public Token () {}
			
			public String getIssued_at() {
				return issued_at;
			}

			public void setIssued_at(String issued_at) {
				this.issued_at = issued_at;
			}

			public String getExpires() {
				return expires;
			}

			public void setExpires(String expires) {
				this.expires = expires;
			}

			public String getId() {
				return id;
			}

			public void setId(String id) {
				this.id = id;
			}

			public Tenant getTenant() {
				return tenant;
			}

			public void setTenant(Tenant tenant) {
				this.tenant = tenant;
			}

			public class Tenant {
				private String description;
				private String enabled;
				private String id;
				private String name;
				
				public Tenant () {}

				public String getDescription() {
					return description;
				}

				public void setDescription(String description) {
					this.description = description;
				}

				public String getEnabled() {
					return enabled;
				}

				public void setEnabled(String enabled) {
					this.enabled = enabled;
				}

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
			}
		}
		
		public class ServiceCatalog {
			private Endpoint[] endpoints;
			private String[]   endpoints_links;
			private String     type;
			private String     name;
			
			public ServiceCatalog () {}
			
			public Endpoint[] getEndpoints() {
				return endpoints;
			}

			public void setEndpoints(Endpoint[] endpoints) {
				this.endpoints = endpoints;
			}

			public String[] getEndpoints_links() {
				return endpoints_links;
			}

			public void setEndpoints_links(String[] endpoints_links) {
				this.endpoints_links = endpoints_links;
			}

			public String getType() {
				return type;
			}

			public void setType(String type) {
				this.type = type;
			}

			public String getName() {
				return name;
			}

			public void setName(String name) {
				this.name = name;
			}

			public class Endpoint {
				private String adminURL;
				private String region;
				private String internalURL;
				private String id;
				private String publicURL;
				
				public Endpoint () {}

				public String getAdminURL() {
					return adminURL;
				}

				public void setAdminURL(String adminURL) {
					this.adminURL = adminURL;
				}

				public String getRegion() {
					return region;
				}

				public void setRegion(String region) {
					this.region = region;
				}

				public String getInternalURL() {
					return internalURL;
				}

				public void setInternalURL(String internalURL) {
					this.internalURL = internalURL;
				}

				public String getId() {
					return id;
				}

				public void setId(String id) {
					this.id = id;
				}

				public String getPublicURL() {
					return publicURL;
				}

				public void setPublicURL(String publicURL) {
					this.publicURL = publicURL;
				}
			}
		}
		
		public class User {
			private String   username;
			private String[] roles_links;
			private String   id;
			private Role[]   roles;
			private String   name;
			
			public User () {}
			
			public String getUsername() {
				return username;
			}

			public void setUsername(String username) {
				this.username = username;
			}

			public String[] getRoles_links() {
				return roles_links;
			}

			public void setRoles_links(String[] roles_links) {
				this.roles_links = roles_links;
			}

			public String getId() {
				return id;
			}

			public void setId(String id) {
				this.id = id;
			}

			public Role[] getRoles() {
				return roles;
			}

			public void setRoles(Role[] roles) {
				this.roles = roles;
			}

			public String getName() {
				return name;
			}

			public void setName(String name) {
				this.name = name;
			}
			
			public class Role {
				private String name;
				
				public Role() {}

				public String getName() {
					return name;
				}

				public void setName(String name) {
					this.name = name;
				}
			}
		}
		
		public class Metadata {
			private int is_admin;
			private String[] roles;
			
			public Metadata() {}

			public int getIs_admin() {
				return is_admin;
			}

			public void setIs_admin(int is_admin) {
				this.is_admin = is_admin;
			}

			public String[] getRoles() {
				return roles;
			}

			public void setRoles(String[] roles) {
				this.roles = roles;
			}
		}
	}
}
