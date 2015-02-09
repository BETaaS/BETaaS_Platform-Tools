/**

Copyright 2014 ATOS SPAIN S.A.

Licensed under the Apache License, Version 2.0 (the License);
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

Authors :
Sergio García Villalonga. Atos Research and Innovation, Atos SPAIN SA
@email sergio.garciavillalonga@atos.net
**/

package eu.betaas.betaasandroidapp.pojo;

import java.util.List;
import java.util.UUID;

public class Gateway {
	private String id;
	private String name;
	private String uri;
	private int    port;
	private String appId;
	private List<String> services;
	private List<String> tokens;
	
	public Gateway(String   name,
			        String   uri,
			        int     port,
			        String   appId,
			        List<String> services,
			        List<String> tokens) {
		this.id       = UUID.randomUUID().toString();
		this.name     = name;
		this.uri      = uri;
		this.port     = port;
		this.appId    = appId;
		this.services = services;
		this.tokens   = tokens;
	}
	
	public Gateway(String   id,
			        String   name,
			        String   uri,
			        int     port,
			        String   appId,
			        List<String> services,
			        List<String> tokens) {
		this.id       = id;
		this.name     = name;
		this.uri      = uri;
		this.port     = port;
		this.appId    = appId;
		this.services = services;
		this.tokens   = tokens;
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

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public List<String> getServices() {
		return services;
	}

	public void setServices(List<String> services) {
		this.services = services;
	}

	public List<String> getTokens() {
		return tokens;
	}

	public void setTokens(List<String> tokens) {
		this.tokens = tokens;
	}
}
