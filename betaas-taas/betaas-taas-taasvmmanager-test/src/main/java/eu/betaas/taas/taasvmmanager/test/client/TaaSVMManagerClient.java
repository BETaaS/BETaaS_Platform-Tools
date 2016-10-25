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

package eu.betaas.taas.taasvmmanager.test.client;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.util.tracker.ServiceTracker;

import eu.betaas.taas.taasvmmanager.api.TaaSVMManager;
import eu.betaas.taas.taasvmmanager.api.datamodel.Flavor;
import eu.betaas.taas.taasvmmanager.api.datamodel.InstanceType;
import eu.betaas.taas.taasvmmanager.api.datamodel.VMRequest;
import eu.betaas.taas.taasvmmanager.api.datamodel.Availability;


public class TaaSVMManagerClient {
	private static TaaSVMManagerClient instance;
	private Logger logger= Logger.getLogger("betaas.taas");
	private TaaSVMManager myClient;
	
	private TaaSVMManagerClient () {
		 logger.info("[TaaSVMManagerClient] Starting TaaSVMManagerClient");
		// Retrieve the BundleContext from the OSGi Framework
		BundleContext context = FrameworkUtil.getBundle(TaaSVMManagerClient.class).getBundleContext();
				
		// Open tracker in order to retrieve VM Manager services
		ServiceTracker myTracker = new ServiceTracker(context, TaaSVMManager.class.getName(), null); 
		myTracker.open();		
		Object [] providers = myTracker.getServices(); 
		
		// Select a provider
		int n = 0;
		if ( providers != null && providers.length > 0 ) { 		
			logger.info("[TaaSVMManagerClient] Number of providers found for TaaS VMM: " + providers.length);			
			myClient = (TaaSVMManager) providers[n];
			logger.info("[TaaSVMManagerClient] Taas VM Manager Service found!");
		} else {
			logger.info("[TaaSVMManagerClient] No providers were found for the TaaS VM Manager");			
		}
		
		// Close the tracker
		myTracker.close();
		logger.info("[TaaSVMManagerClient] TaaSVMManagerClient started"); 
	}

	public static TaaSVMManagerClient getInstance() {
		if (instance == null) {
			instance = new TaaSVMManagerClient();
		}
		return instance;
	}
	
	public List<Flavor> getFlavors() {
		return myClient.getFlavors();
	}

	public String createVM(VMRequest request) throws Exception {
		return myClient.createVM(request);
	}

	public String createExtVM(VMRequest request) {
		return myClient.createExtVM(request);
	}

	public boolean deleteVM(String idVM) {
		return myClient.deleteVM(idVM);
	}
	
	public List<Availability> getAvailability(boolean cpuPreference) {
		return myClient.getAvailability(cpuPreference);
	}

	public List<Availability> getAvailability() {
		return myClient.getAvailability();
	}

	public boolean migrateVM(String vmId, String targetInfo) {
		return myClient.migrateVM(vmId, targetInfo);
	}

	public boolean sendVMs(List<String> vmIds, String targetInfo) {
		return myClient.sendVMs(vmIds, targetInfo);
	}

	/*public List<String> getFlavors() {
		return Arrays.asList(new String[]{"uno", "dos"});
	}

	public String createVM(String request) {
		return request;
	}

	public String createExtVM(String request) {
		return request;
	}

	public boolean deleteVM(String idVM) {
		return true;
	}
	
	public HashMap<String, Integer> getAvailability(boolean cpuPreference) {
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		map.put("cuatro", 4);
		map.put("tres", 3);
		map.put("dos", 2);
		map.put("uno", 1);
 		return map;
	}

	public HashMap<String, Integer> getAvailability() {
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		map.put("uno", 1);
		map.put("dos", 2);
		map.put("tres", 3);
		map.put("cuatro", 4);
 		return map;
	}

	public boolean migrateVM(String vmId, String targetInfo) {
		return false;
	}

	public boolean sendVMs(List<String> vmIds, String targetInfo) {
		return true;
	}*/
}
