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

package eu.betaas.betaasandroidapp.communicator;

import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;

import eu.betaas.betaasandroidapp.pojo.Gateway;
import eu.betaas.betaasandroidapp.pojo.Installation;
import eu.betaas.betaasandroidapp.pojo.Measurement;
import eu.betaas.betaasandroidapp.rest.RestClient;
import eu.betaas.betaasandroidapp.rest.RestClient.HeaderType;


public class GatewayCommunicatorRESTImpl implements GatewayCommunicator {
	private static final long COMMUNICATION_TIMEOUT = 20000;
	private static final long RETRY_TIME = 5000;
	
	private static final String BASE_PATH = "/cxf/sm/";
	
	private enum ResourceType {APPLICATION, DATA, REGISTRATION};
	
	private static Map<ResourceType, String> RESOURCES;
	static {
		Map<ResourceType, String> map = new HashMap<ResourceType, String>();
		map.put(ResourceType.APPLICATION, "application/");
		map.put(ResourceType.DATA, "data/");
		map.put(ResourceType.REGISTRATION, "registration/");
		RESOURCES = Collections.unmodifiableMap(map);
	}
	
	private RestClient restClient;
	
	private static final String MANIFEST = 
					  "<?xml version='1.0'?>"
					+ "<manifest>"   
					+     "<Application>"
					+         "<name>IntrusionDetection</name>"
					+         "<notificationAddress>http://localhost:8080/betaasapptester/</notificationAddress>"
					+         "<credentials>MIIGogIBAzCCBlwGCSqGSIb3DQEHAaCCBk0EggZJMIIGRTCCAnoGCSqGSIb3DQEHAaCCAmsEggJnMIICYzCCAl8GCyqGSIb3DQEMCgECoIICAjCCAf4wKAYKKoZIhvcNAQwBAzAaBBSiM9ygH9LOjPPea3tqszExY8m2HwICBAAEggHQRE00I6fDH1XUgBTCUO8MOvUZudJzjEGD0D9I1ziVZS3UvJnn22CA6wdjSd0c2H2+gQ8vCeq9YFcNWCtmwhUePNpuTqCv1UkUGceuu9wEViZfDzx6chB2GpWH07ecLgkfYRWnlItCog3DorkB3S2Aq2WzYj1qiyTkWfl0kKTRi1UeGG3DRQu+wKhPzJNOCJDyU4w3qXp8oWOuv07xjH62JHZjmRfL1r63ouh3RE/KCxhGOG45EgcEvdSu+sVXEPLaEfM3IClCrlyw7tuzBM2HEfxO03P4nNukGRCthuUIljtclnY4wmGVjYBpTQ1sdiPqdA9z7jsSLBOcqKhSL6/2MjxNI2cMyYVrcY8GQEl/wobBhEZHqbPG5oPgoHB7kFkEKUzzviX5t+lR+Jsfg6Qkfe+JxQaONW1f7dGq+slHn1u8Us7tpdJa3PQMAqLhTDTOAfqIVqS3TkpW/RmCJn7s/6EF/uq+eN0STPjMkx3tFhdl1K5Rsk/nYrIIf6/toMpUZw0MBQcqyDq/vlyBrOpNiEl0kCXcKRGXVn62da5A/aZCFmPCvoaxX43ubYth87qL4ybs1A8Ot3fEUSU4Hda9FX+zeZRQI2N5XfI/93NNM8kxSjAjBgkqhkiG9w0BCRQxFh4UAEUAcgBpAGMAJwBzACAASwBlAHkwIwYJKoZIhvcNAQkVMRYEFP0PmAanSQppmRiPMFWX1C29VTl2MIIDwwYJKoZIhvcNAQcGoIIDtDCCA7ACAQAwggOpBgkqhkiG9w0BBwEwKAYKKoZIhvcNAQwBBT AaBBRjn +sgg57T7m7ORMn7hpy5gQuwfAICBACAggNwK83S32W1HBhrmN+L52Bu4zQn9PcPzo2M0yZR8UAvZQt1REl6atrFgK46d4JMhWilYehLdbU74myB/Cd9+u5mKC3aLv6DkRzlQxpMjYOwMygLQLOoo4YpVN8j2i+3E4zUtjUhTGfSe3/lJHoNx7jCEmLyuJKMIXM8E+yq0RbrPjidQ8H44R+sbUFnghuI+4vOrvlMaqKNA0fztb3UPfQeylVa4qKl3iBBCPCg4ynpwyWRog+XWgzWqgESr5AtMCheXz1q1eg2VGeBqt430SIvVaE0pL96hZVtW+T2PQB0TSBJuytUpmjdmX2zWjD8yOeJPfT2eUDqIxAh5l02mQvNtGXSJlmhg2YSMWOX8YaZEIjkGsVVlib7Uy4CsKLD13cH9S3ZuAngNXdP8h/XAFm4zv2i3/QLrk8+4asVxBBqVcGsfMk5sR6JHzfPfcT3cmxl2nKw6c07CvmGGt9QJ5aYXH9Xx3pm5IP0KmvNEPf03z+N9C69jDB4Og6Hhq4ftuZJcdmBCTq1qRuPZxn2DH8yqBlSAVwGZWvY0k9TqLSsJeYtvrYvKthW6JTjctpdTf0juNkRI/BrPN9ayriS7u952q7RSnyiLL0rZvvwCT/O6MJkZodLw16JRBe21bQeZo6tYLddNvgOCGGU/K6hf+9WuZgMaU7h1QIvM9xci9YiSELsFriovs+74SNT2b87g9IO5pj+YKs2V0lIAUG4trTR26np3+P9rdx9XtAD6pvZMg/vnDz4EfH3mf/J72PGpouJrXNaN4IV1YsIHURL2J5+eYWT8i3N/gPDubogdY4SxbQlu+jkdj0cuon27cFw1ijD1ulfjHNeu4B892ykUMyhHaF9q/tLDuL1AbAgNKR8VGpNrMrS5ohclvMramg9NCXi5Y2vU+va6SERWoXTUSiW0Ylbpe1V/OVzQWHz+53bx5Yiw1cqxF4v3lLkx0g l1YBow5d jb2Dl22fJHfSs3V0jYDuo8NWCG6en20BmWPomFMTS/uW+FsCoM4h7faA8sr6FtGmVvwASYzHf3Jzw6kHHw7Vuy+aUq2ojJDIyWrCGCnJ396KmmajloGuWotTVlv31WzETt2i0Pazwf3CeaBPNJa8F3VSp509DnoCDuu3DNxlyF/nCCQqV0QR6VXHJLxyi3tiER4mjmOHoNfUpOodnyjA9MCEwCQYFKw4DAhoFAAQU662jUEl853ow6BE8wQP3UoSPQ9cEFFmzItL/tXT3OBMYeIpJN7FtGgDEAgIEAA==</credentials>"
					+     "</Application>"
					+     "<ServiceDescriptionTerm>"
					+         "<ServiceDefinition>"
					+             "<Feature>presence</Feature>"
					+             "<Areas>"
					+                 "<Environment>Private</Environment>"
					+                 "<LocationKeyword>home</LocationKeyword>"
					+                 "<Floor>1</Floor>"
					+             "</Areas>"
					+             "<Delivery>betaas.delivery.RTPULL</Delivery>"
					+             "<Trust>5</Trust>"
					+             "<Period>2</Period>"
					+             "<QoS>"                                              
					+                 "<MaxInterRequestTimeSec>5</MaxInterRequestTimeSec>"
					+                 "<MaxResponseTimeSec>5</MaxResponseTimeSec>"
					+                 "<MinAvailability>2</MinAvailability>"     
					+             "</QoS>"
					+             "<credentials></credentials>"
					+         "</ServiceDefinition>"  
					+     "</ServiceDescriptionTerm>"
					+ "</manifest>";
	
	public GatewayCommunicatorRESTImpl(final String host, final int port) {
		restClient = new RestClient(host, port);
	}
	
	@Override
	public Gateway installApplication(Gateway gateway)
						throws GatewayCommunicatorException{
		String appId;
		long timeout;
		long currentTime;
		String path = BASE_PATH + RESOURCES.get(ResourceType.APPLICATION);
		Map<HeaderType, String> headers = new HashMap<HeaderType, String>();
		headers.put(HeaderType.CONTENTTYPE, "application/xml");
		
		String[] result =
		restClient.postResource(path, headers, MANIFEST);
		
		//Change to enum
		if (!result[0].contains("200 OK")) {
			throw new GatewayCommunicatorException(
					gateway,
					result[0],
					"Installation: " + result[1]);
		}
		
		appId = result[1];
		
		path = BASE_PATH
			+ RESOURCES.get(ResourceType.APPLICATION)
			+ appId;
		result =
			restClient.getResource(path,
					new HashMap<RestClient.HeaderType,
					String>());
		
		if (!result[0].contains("200 OK")) {
			throw new GatewayCommunicatorException(
					gateway,result[0],
					"Getting services: " + result[1]);
		}
		
		Gson gson = new Gson();
		Installation installation =
				gson.fromJson(result[1], Installation.class);
		
		currentTime = Calendar.getInstance().getTimeInMillis();
		timeout =
			Calendar.getInstance().getTimeInMillis() + COMMUNICATION_TIMEOUT;
		while ((installation.getInstallationInfo() == null ||
				installation.getInstallationInfo().getTokenList() == null ||
				installation.getInstallationInfo().getTokenList().size() == 0) &&
				currentTime < timeout) {
			try {
				Thread.sleep(RETRY_TIME);
				//currentTime = Calendar.getInstance().getTimeInMillis();
				
				result = restClient.getResource(path,
							new HashMap<RestClient.HeaderType,
							String>());
				
				if (!result[0].contains("200 OK")) {
					throw new GatewayCommunicatorException(
							gateway,
							result[0],
							"Getting services: " + result[1]);
				}
				
				installation =
					gson.fromJson(result[1], Installation.class);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		if (timeout >= COMMUNICATION_TIMEOUT &&
				(installation.getInstallationInfo() == null ||
				installation.getInstallationInfo().getTokenList() == null ||
				installation.getInstallationInfo().getTokenList().size() == 0)) {
			throw new GatewayCommunicatorException(
					gateway,
					"Client timeout",
					"Timeout getting services from " + gateway.getName());
		} else if (installation.getInstallationInfo() == null ||
				installation.getInstallationInfo().getTokenList() == null ||
				installation.getInstallationInfo().getTokenList().size() == 0) {
			throw new GatewayCommunicatorException(
					gateway,
					"No services",
					"no services available in " + gateway.getName());
		}
		
		gateway.setAppId(appId);
		gateway.setServices(installation.getInstallationInfo().getServiceList());
		gateway.setTokens(installation.getInstallationInfo().getTokenList());
		
		return gateway;
	}

	@Override
	public Measurement getPresence(Gateway gateway, String serviceId)
					 				throws GatewayCommunicatorException{
		String appId = gateway.getAppId();
		
		String path = BASE_PATH
				+ RESOURCES.get(ResourceType.DATA)
				+ appId + "/" + serviceId;
		Map<HeaderType, String> headers = new HashMap<HeaderType, String>();
		headers.put(HeaderType.TOKEN, gateway.getTokens().get(0));
		
		String[] result = restClient.getResource(path, headers);
		
		if (!result[0].contains("200 OK")) {
			throw new GatewayCommunicatorException(
					gateway,
					result[0],
					"Getting data: " + result[1]);
		}
		return new Measurement(Calendar.getInstance().getTimeInMillis(),
				Boolean.parseBoolean(result[1]),
				gateway.getId(),
				serviceId);
	}
	
	@Override
	public Gateway subscribe(Gateway gateway, String serviceId)
					 			throws GatewayCommunicatorException{
		String appId = gateway.getAppId();
		
		String token = ""; //Per gateway or per application?
		
		String path = BASE_PATH
			+ RESOURCES.get(ResourceType.REGISTRATION)
			+ appId + "/" + serviceId;
		Map<HeaderType, String> headers = new HashMap<HeaderType, String>();
		headers.put(HeaderType.TOKEN, token);
		
		String[] result = restClient.postResource(path, headers, null);
		
		if (!result[0].contains("200 OK")) {
			throw new GatewayCommunicatorException(
					gateway,
					result[0],
					"Subscribing: " + result[1]);
		}
		return gateway;
	}
	
	@Override
	public void unsubscribe(Gateway gateway, String serviceId)
					 			throws GatewayCommunicatorException{
		String appId = gateway.getAppId();
		
		String token = ""; //Per gateway or per application?
		
		String path = BASE_PATH
				+ RESOURCES.get(ResourceType.REGISTRATION)
				+ appId + "/" + serviceId;
		Map<HeaderType, String> headers = new HashMap<HeaderType, String>();
		headers.put(HeaderType.TOKEN, token);
		
		String[] result = restClient.deleteResource(path, headers);
		
		if (!result[0].contains("200 OK")) {
			throw new GatewayCommunicatorException(
					gateway,
					result[0],
					"Unsubscribing: " + result[1]);
		}
	}
	
	@Override
	public void uninstallApplication(Gateway gateway)
					 throws GatewayCommunicatorException {}

}
