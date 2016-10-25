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

Authors Contact:
Francisco Javier Nieto. Atos Research and Innovation, Atos SPAIN SA
@email francisco.nieto@atos.net
**/

package eu.betaas.service.securitymanager.trustmanager.api.impl;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import eu.betaas.service.securitymanager.trustmanager.api.ServiceTrustManager;
import eu.betaas.service.securitymanager.trustmanager.messaging.MessageManager;
import eu.betaas.service.securitymanager.trustmanager.serviceaggregator.GatewayTrust;
import eu.betaas.service.securitymanager.trustmanager.serviceproxy.TaaSBDMClient;
import eu.betaas.service.securitymanager.trustmanager.servicethread.TrustServiceThread;

public class ServiceTrustManagerImpl implements ServiceTrustManager
{
	private Logger logger= Logger.getLogger("betaas.service");
	private TrustServiceThread trustThread;
	private MessageManager mManager;
	
	public void start()  
	{		
		logger.info("Starting Service Trust Manager...");		
				
		// here we could execute some basic testing at the beginning, so we check everything is in place
		mManager = MessageManager.instance();
		// Start the background thread which will be recalculating trust
		trustThread = TrustServiceThread.instance();		
		
		logger.info("Service Trust Manager started!");
		mManager.monitoringPublish("Service Trust Manager started!");
	}
	
	public void stop() 
	{
		logger.info("Stopping Service Trust Manager...");		
		trustThread.stopThread();
		trustThread = null;
		logger.info("Service Trust Manager has been stopped.");
	}
	
	public double getTrust (String idGateway)
	{
		logger.info("Service Trust Manager Service invoked for getting trust!");
		TaaSBDMClient myClient = TaaSBDMClient.instance();
		GatewayTrust trustValue = myClient.getTrustData(idGateway);
		if (trustValue == null)
		{
			logger.error("No trust calculated for " + idGateway + " was found -> Default value provided");
			return 2.0;
		}
		return trustValue.getGatewayTrust();
	}
	
	public ArrayList<Double> getTrust (ArrayList<String> gatewaysList)
	{
		logger.info("Service Trust Manager Service invoked for getting trust with a list!");
		TaaSBDMClient myClient = TaaSBDMClient.instance();
		ArrayList<Double> results = new ArrayList<Double> ();
		
		// Iterate through all the Thing Services for getting trust
		for (int i=0; i<gatewaysList.size(); i++)
		{
			GatewayTrust trustValue = myClient.getTrustData(gatewaysList.get(i));
			results.add(new Double (trustValue.getGatewayTrust()));
		}	
		
		return results;
	}
	
	public boolean registerGateway (String idGateway)
	{
		logger.info("Service Trust Manager Service registering Gateway " + idGateway);
		return true;
	}
	
	public boolean removeGateway (String idGateway)
	{
		logger.info("Service Trust Manager Service removing Gateway " + idGateway);
		return true;
	}
}
