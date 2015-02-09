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

package eu.betaas.taas.securitymanager.taastrustmanager.api.impl;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import eu.betaas.taas.securitymanager.taastrustmanager.api.TaaSTrustManager;
import eu.betaas.taas.securitymanager.taastrustmanager.taasaggregator.ThingServiceTrust;
import eu.betaas.taas.securitymanager.taastrustmanager.taasproxy.TaaSBDMClient;
import eu.betaas.taas.securitymanager.taastrustmanager.taasthread.TrustTaaSThread;

public class TaaSTrustManagerImpl implements TaaSTrustManager
{
	private Logger logger= Logger.getLogger("betaas.taas");
	private TrustTaaSThread trustThread;
	
	public void start()  
	{		
		logger.info("Starting TaaS Trust Manager...");		
				
		// here we could execute some basic testing at the beginning, so we check everything is in place
		
		// Start the background thread which will be recalculating trust
		trustThread = TrustTaaSThread.instance();		
		
		logger.info("TaaS Trust Manager started!");
	}
	
	public void stop() 
	{
		logger.info("Stopping TaaS Trust Manager...");		
		trustThread.stopThread();
		trustThread = null;
		logger.info("TaaS Trust Manager has been stopped.");
	}
	
	public double getTrust (String idThingsService)
	{
		logger.info("Taas Trust Manager Service invoked for getting trust!");
		TaaSBDMClient myClient = TaaSBDMClient.instance();
		ThingServiceTrust trustValue = myClient.getTrustData(idThingsService);
		if (trustValue == null)
		{
			logger.error("No trust calculated for " + idThingsService + " was found -> Default value provided");
			return 2.0;
		}
		return trustValue.getThingServiceTrust();
	}
	
	public ArrayList<Double> getTrust (ArrayList<String> thingServicesList)
	{
		logger.info("Taas Trust Manager Service invoked for getting trust with a list!");
		TaaSBDMClient myClient = TaaSBDMClient.instance();
		ArrayList<Double> results = new ArrayList<Double> ();
		
		// Iterate through all the Thing Services for getting trust
		for (int i=0; i<thingServicesList.size(); i++)
		{
			ThingServiceTrust trustValue = myClient.getTrustData(thingServicesList.get(i));
			results.add(new Double (trustValue.getThingServiceTrust()));
		}	
		
		return results;
	}
	
	public boolean registerThingsService (String idThingsService)
	{
		logger.info("Taas Trust Manager Service registering Things Service " + idThingsService);
		return true;
	}
	
	public boolean removeThingsService (String idThingsService)
	{
		logger.info("Taas Trust Manager Service removing Things Service " + idThingsService);
		return true;
	}
	
	public boolean subscribeThreshold (String idThingsService, double threshold)
	{
		logger.info("Taas Trust Manager Service monitoring threshold for " + idThingsService);
		TrustTaaSThread activeThread = TrustTaaSThread.instance();
		return activeThread.subscribeThreshold(threshold, idThingsService);		
	}
	
	public boolean removeThreshold (String idThingsService)
	{
		logger.info("Taas Trust Manager Service removing threshold for " + idThingsService);
		TrustTaaSThread activeThread = TrustTaaSThread.instance();
		return activeThread.removeThreshold(idThingsService);
	}
}
