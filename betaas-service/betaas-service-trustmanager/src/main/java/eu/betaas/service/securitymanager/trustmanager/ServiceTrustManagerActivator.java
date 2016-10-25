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


// BETaaS - Building the Environment for the Things as a Service
//
// Component: ServiceTrustManager
// Responsible: Atos
package eu.betaas.service.securitymanager.trustmanager;


import org.apache.log4j.Logger;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import eu.betaas.service.securitymanager.trustmanager.serviceproxy.TaaSBDMClient;
import eu.betaas.service.securitymanager.trustmanager.serviceproxy.TaaSCMClient;
import eu.betaas.service.securitymanager.trustmanager.servicethread.TrustServiceThread;

/**
* 
* @author Francisco Javier Nieto De-Santos (francisco.nieto@atos.net)
*/
public class ServiceTrustManagerActivator implements BundleActivator {

	private Logger log;
	private TrustServiceThread trustThread;
	
	public void start(BundleContext arg0) throws Exception {
		log = Logger.getLogger("betaas.taas");
		log.info("Starting Service Trust Manager...");		
		
		// Initialize external clients
		TaaSBDMClient.instance();
		TaaSCMClient.instance();
		
		// here we could execute some basic testing at the beginning, so we check everything is in place
		
		// Start the background thread which will be recalculating trust
		trustThread = TrustServiceThread.instance();
				
		log.info("Service Trust Manager started!");
	}

	public void stop(BundleContext arg0) throws Exception 
	{
		log.info("Stopping Service Trust Manager...");		
		trustThread.stopThread();
		trustThread = null;
		log.info("Service Trust Manager has been stopped.");
	}

}
