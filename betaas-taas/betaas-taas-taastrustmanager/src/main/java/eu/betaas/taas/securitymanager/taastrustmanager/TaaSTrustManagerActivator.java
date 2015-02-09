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
// Component: TaaSTrustManager
// Responsible: Atos
package eu.betaas.taas.securitymanager.taastrustmanager;


import org.apache.log4j.Logger;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import eu.betaas.taas.securitymanager.taastrustmanager.taasproxy.TaaSBDMClient;
import eu.betaas.taas.securitymanager.taastrustmanager.taasproxy.TaaSCMClient;
import eu.betaas.taas.securitymanager.taastrustmanager.taasthread.TrustTaaSThread;

/**
* 
* @author Francisco Javier Nieto De-Santos (francisco.nieto@atos.net)
* @author Sergio Garcia Villalonga (sergio.garciavillalonga@atos.net)
*/
public class TaaSTrustManagerActivator implements BundleActivator {

	private Logger log;
	private TrustTaaSThread trustThread;
	
	public void start(BundleContext arg0) throws Exception {
		log = Logger.getLogger("betaas.taas");
		log.info("Starting TaaS Trust Manager...");		
		
		// Initialize external clients
		TaaSBDMClient.instance();
		TaaSCMClient.instance();
		
		// here we could execute some basic testing at the beginning, so we check everything is in place
		
		// Start the background thread which will be recalculating trust
		trustThread = TrustTaaSThread.instance();
				
		log.info("TaaS Trust Manager started!");
	}

	public void stop(BundleContext arg0) throws Exception 
	{
		log.info("Stopping TaaS Trust Manager...");		
		trustThread.stopThread();
		trustThread = null;
		log.info("TaaS Trust Manager has been stopped.");
	}

}
