package eu.betaas.taas.taasvmmanager.openstack.gson;

public class AuthRequest {
	private Auth auth;
	
	public AuthRequest () {}
	
	public Auth getAuth() {
		return auth;
	}

	public void setAuth(Auth auth) {
		this.auth = auth;
	}

	public class Auth {
		String tenantName;
		PasswordCredentials passwordCredentials;

		public Auth () {}
		
		public String getTenantName() {
			return tenantName;
		}

		public void setTenantName(String tenantName) {
			this.tenantName = tenantName;
		}

		public PasswordCredentials getPasswordCredentials() {
			return passwordCredentials;
		}

		public void setPasswordCredentials
					(PasswordCredentials passwordCredentials) {
			this.passwordCredentials = passwordCredentials;
		}	
		
		public class PasswordCredentials {
			String username;
			String password;
			
			public PasswordCredentials () {}
			
			public String getUsername() {
				return username;
			}
			
			public void setUsername(String username) {
				this.username = username;
			}
			
			public String getPassword() {
				return password;
			}
			
			public void setPassword(String password) {
				this.password = password;
			}
		}
	}
}
