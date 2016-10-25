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
Francisco Javier Nieto. Atos Research and Innovation, Atos SPAIN SA
@email francisco.nieto@atos.net 
Sergio García Villalonga. Atos Research and Innovation, Atos SPAIN SA
@email sergio.garciavillalonga@atos.net
**/

package eu.betaas.taas.taasvmmanager;

import java.util.List;

import org.apache.log4j.Logger;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import eu.betaas.taas.taasvmmanager.configuration.TaaSVMMAnagerConfiguration;
import eu.betaas.taas.taasvmmanager.vmsallocator.VMsAllocatorManager;



/**
* 
* @author Francisco Javier Nieto De-Santos (francisco.nieto@atos.net)
* @author Sergio Garcia Villalonga (sergio.garciavillalonga@atos.net)
*/
public class TaaSVMManagerActivator implements BundleActivator {
		
	private static Logger logger = Logger.getLogger("betaas.taas");
	
	public void start(BundleContext arg0) throws Exception {
		logger.info("Starting TaaS VM Manager...");
		logger.info("TaaS VM Manager started!");
	}

	public void stop(BundleContext arg0) throws Exception 
	{
		logger.info("Shutting down TaaS VM Manager...");
		logger.info("Deleting all pending VMs...");
	}
}
