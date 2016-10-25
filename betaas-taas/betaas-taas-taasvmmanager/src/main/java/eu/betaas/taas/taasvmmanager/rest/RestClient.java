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

package eu.betaas.taas.taasvmmanager.rest;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.WebResource.Builder;

public class RestClient {
	
	private Logger log;
	public enum HeaderType {ACCEPT, CONTENTTYPE, XAUTHTOKEN};
	public enum StatusCode {OK, CREATED, ACCEPTED,
		NONAUTHORITATIVEINFORMATION, NOCONTENT, BADREQUEST,
		UNAUTHORIZED, FORBIDDEN, NOTFOUND, METHODNOTALLOWED,
		CONFLICT, REQUESTENTITYTOOLARGE, INTERNALSERVERERROR,
		SERVICEUNAVAILABLE};
	
	private static Map<HeaderType, String> HEADERS;
	static {
		Map<HeaderType, String> map =
				new HashMap<HeaderType, String>();
		map.put(HeaderType.ACCEPT, "Accept");
		map.put(HeaderType.CONTENTTYPE, "Content-Type");
		map.put(HeaderType.XAUTHTOKEN, "X-Auth-Token");
		HEADERS = Collections.unmodifiableMap(map);
	}
	
	private static Map<String, StatusCode> STATUSCODES;
	static {
		Map<String, StatusCode> map =
				new HashMap<String, StatusCode>();
		map.put("200", StatusCode.OK);
		map.put("201", StatusCode.CREATED);
		map.put("202", StatusCode.ACCEPTED);
		map.put("203", StatusCode.NONAUTHORITATIVEINFORMATION);
		map.put("204", StatusCode.NOCONTENT);
		map.put("400", StatusCode.BADREQUEST);
		map.put("401", StatusCode.UNAUTHORIZED);
		map.put("403", StatusCode.FORBIDDEN);
		map.put("404", StatusCode.NOTFOUND);
		map.put("405", StatusCode.METHODNOTALLOWED);
		map.put("409", StatusCode.CONFLICT);
		map.put("413", StatusCode.REQUESTENTITYTOOLARGE);
		map.put("500", StatusCode.INTERNALSERVERERROR);
		map.put("503", StatusCode.SERVICEUNAVAILABLE);
		STATUSCODES = Collections.unmodifiableMap(map);
	}
	
	private Client httpClient;
	private String endpointUrl;
	
	public RestClient (String endpointUrl) {
		log = Logger.getLogger("betaas.taas");
		
		log.info("[RestClient] Initializing RestClient...");
		this.httpClient  = Client.create();
		this.endpointUrl = endpointUrl;
		log.info("[RestClient] RestClient initialized.");
	}
	
	public String[] getResource(String path, Map<HeaderType, String> headers) {
		String result[];
		WebResource.Builder resource = buildResource(path, headers);
		
		ClientResponse response = resource.get(ClientResponse.class);
	
		result = getResponse(response);
		
		return result;
	}
	
	public String[] putResource(String path, Map<HeaderType, String> headers) {
		String result[];
		WebResource.Builder resource = buildResource(path, headers);
		
		ClientResponse response = resource.put(ClientResponse.class);
		
		result = getResponse(response);
		
		return result;
	}
	
	public String[] postResource(String path, Map<HeaderType, String> headers, String body) {
		String result[];
		WebResource.Builder resource = buildResource(path, headers);
		
		ClientResponse response = resource.post(ClientResponse.class, body);
		
		result = getResponse(response);
		
		return result;
	}
	
	public String[] deleteResource(String path, Map<HeaderType, String> headers) {
		String result[];
		WebResource.Builder resource = buildResource(path, headers);
		
		ClientResponse response = resource.delete(ClientResponse.class);
		
		result = getResponse(response);
		
		return result;
	}
	
	private String[] getResponse(ClientResponse httpResponse) {
		String[] ret = new String[2];
	 
		ret[0] = String.valueOf(httpResponse.getStatus());
		ret[1] = httpResponse.getEntity(String.class);
		
		return ret;
	}
	
	private WebResource.Builder buildResource (String path,
			                            Map<HeaderType, String> headers) {
		String completePath = endpointUrl + path;
		WebResource resource = httpClient.resource(completePath);
		Builder builder = resource.getRequestBuilder();
		
		for (HeaderType headerType : headers.keySet()) {
			if (headers.get(headerType) != null) {
				builder.header(HEADERS.get(headerType), headers.get(headerType));
			}
		}
		
		return builder;
	}
	
	public static StatusCode getStatusCode(String code) {
		return STATUSCODES.get(code);
	}
}
