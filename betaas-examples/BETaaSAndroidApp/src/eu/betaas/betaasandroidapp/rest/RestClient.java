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

package eu.betaas.betaasandroidapp.rest;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

public class RestClient {
	
	public enum HeaderType {CONTENTTYPE, TOKEN};
	
	private static Map<HeaderType, String> HEADERS;
	static {
		Map<HeaderType, String> map = new HashMap<RestClient.HeaderType, String>();
		map.put(HeaderType.CONTENTTYPE, "Content-Type");
		map.put(HeaderType.TOKEN, "token");
		HEADERS = Collections.unmodifiableMap(map);
	}
	
	private HttpClient httpclient;
	private String endpointUrl;
	private int port;
	
	public RestClient (String endpointUrl, int port) {
		httpclient = new DefaultHttpClient();
		this.endpointUrl = endpointUrl;
		this.port        = port;
	}
	
	public String[] getResource(String path, Map<HeaderType, String> headers) {
		HttpHost target = new HttpHost(endpointUrl, port);
		
		String result[];
		
		try {
			// specify the get request
			HttpGet getRequest = new HttpGet(path);
			for (HeaderType header : headers.keySet()) {
				getRequest.setHeader(HEADERS.get(header), headers.get(header));
			}
	
			HttpResponse httpResponse = httpclient.execute(target, getRequest);
	
			result = getResponse(httpResponse);
	
		} catch (Exception e) {
			result = new String[2];
			result[0] = "clientException";
			result[1] = e.getMessage();
		} 
		
		return result;
	}
	
	public String[] putResource(String path, Map<HeaderType, String> headers) {
		HttpHost target = new HttpHost(endpointUrl, port);
		
		String result[];
		
		try {
			// specify the get request
			HttpPut putRequest = new HttpPut(path);
			for (HeaderType header : headers.keySet()) {
				putRequest.setHeader(HEADERS.get(header), headers.get(header));
			}
		
			HttpResponse httpResponse = httpclient.execute(target, putRequest);
		
			result = getResponse(httpResponse);
		
		} catch (Exception e) {
			result = new String[2];
			result[0] = "clientException";
			result[1] = e.getMessage();
		} 
		
		return result;
	}
	
	public String[] postResource(String path, Map<HeaderType, String> headers, String body) {
		HttpHost target = new HttpHost(endpointUrl, port);
		
		String result[];
		
		try {
			// specify the get request
			HttpPost postRequest = new HttpPost(path);
			for (HeaderType header : headers.keySet()) {
				postRequest.setHeader(HEADERS.get(header), headers.get(header));
			}
			
			if (body != null) {
				HttpEntity b = new ByteArrayEntity(body.getBytes("UTF-8"));
				postRequest.setEntity(b);
			}

			HttpResponse httpResponse = httpclient.execute(target, postRequest);

			result = getResponse(httpResponse);

		} catch (Exception e) {
			result = new String[2];
			result[0] = "clientException";
			result[1] = e.getMessage();
		}

		return result;
	}
	
	public String[] deleteResource(String path, Map<HeaderType, String> headers) {
		HttpHost target = new HttpHost(endpointUrl, port);
		
		String[] result;
		
		try {
			// specify the post request
			HttpDelete delRequest = new HttpDelete(path);
			for (HeaderType header : headers.keySet()) {
				delRequest.setHeader(HEADERS.get(header), headers.get(header));
			}
		
			HttpResponse httpResponse = httpclient.execute(target, delRequest);
			
			result = getResponse(httpResponse);
		
		} catch (Exception e) {
			result = new String[2];
			result[0] = "clientException";
			result[1] = e.getMessage();
		} 
		
		return result;
	}
	
	private String[] getResponse(HttpResponse httpResponse) {
		String[] ret = new String[2];
		HttpEntity entity = httpResponse.getEntity();
	 
		ret[0] = httpResponse.getStatusLine().toString();
		
		if (entity != null) {
			try {
				ret[1] = EntityUtils.toString(entity);
			} catch (ParseException e) {
				ret[1] = e.getMessage();
			} catch (IOException e) {
				ret[1] = e.getMessage();
			}
		}
		
		return ret;
	}
}
