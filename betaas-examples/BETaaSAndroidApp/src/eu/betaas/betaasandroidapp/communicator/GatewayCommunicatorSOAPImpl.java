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

import eu.betaas.betaasandroidapp.configuration.Configuration;
import eu.betaas.betaasandroidapp.pojo.Gateway;
import eu.betaas.betaasandroidapp.pojo.Measurement;
import eu.betaas.betaasandroidapp.soap.ServiceManagerExternalIF;

public class GatewayCommunicatorSOAPImpl implements GatewayCommunicator{
	private ServiceManagerExternalIF serviceManagerIF;
	private String mManifest = 
			"<?xml version='1.0'?>" +
					"<manifest>" +
					    "<Application>" +
					     "<name>TestTemperature</name>" +
					     "<notificationAddress>http://"+ 
					     	Configuration.getInstance().getIP() +
					     ":8080/TestTemperature/" +
					     "</notificationAddress>" +
					    "</Application>" +    
					    "<ServiceDescriptionTerm>" +    
					      "<ServiceDefinition>" +
					        "<Feature>presence</Feature>" +
					        "<Areas>" +
					          "<Area>home</Area>" +
					        "</Areas>" +
					        "<Delivery>betaas.delivery.RTPULL</Delivery>" +
					        "<Trust>0.9</Trust>" +
					        "<QoS>" +
					          "<maxDelaySec>5</maxDelaySec>" +
					        "</QoS>" +
					        "<credentials>fonwon2vlxkfwoi2309s</credentials>" +
					      "</ServiceDefinition>" +
					    "</ServiceDescriptionTerm>" +
					"</manifest>";
	
	public GatewayCommunicatorSOAPImpl(String host, int port) {
		String url;
		serviceManagerIF = new ServiceManagerExternalIF();
		url = (host.contains("http://"))? host + ":" + port + "/sm-service" :
			"http://"+ host + ":" + port + "/sm-service";
		serviceManagerIF.setUrl(url);
	}

	@Override
	public Gateway installApplication(Gateway gateway)
			throws GatewayCommunicatorException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Measurement getPresence(Gateway gateway, String serviceId)
			throws GatewayCommunicatorException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void uninstallApplication(Gateway gateway)
			throws GatewayCommunicatorException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Gateway subscribe(Gateway gateway, String serviceId)
			throws GatewayCommunicatorException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void unsubscribe(Gateway gateway, String serviceId)
			throws GatewayCommunicatorException {
		// TODO Auto-generated method stub
		
	}
}