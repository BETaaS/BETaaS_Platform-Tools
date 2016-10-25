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

package eu.betaas.taas.taasvmmanager.api.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import eu.betaas.taas.taasvmmanager.api.TaaSVMManager;
import eu.betaas.taas.taasvmmanager.api.datamodel.Availability;
import eu.betaas.taas.taasvmmanager.api.datamodel.Flavor;
import eu.betaas.taas.taasvmmanager.api.datamodel.InstanceType;
import eu.betaas.taas.taasvmmanager.api.datamodel.VMRequest;
import eu.betaas.taas.taasvmmanager.api.datamodel.Flavor.FlavorType;
import eu.betaas.taas.taasvmmanager.configuration.TaaSVMMAnagerConfiguration;
import eu.betaas.taas.taasvmmanager.messaging.MessageManager;
import eu.betaas.taas.taasvmmanager.vmsallocator.VMsAllocatorManager;

import org.apache.log4j.Logger;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;

/**
 * 
 * @author Francisco Javier Nieto De-Santos (francisco.nieto@atos.net)
 * @author Sergio Garcia Villalonga (sergio.garciavillalonga@atos.net)
 */
public class TaaSVMManagerImpl implements TaaSVMManager {
	
	private Logger logger = Logger.getLogger("betaas.taas");
	
	private MessageManager  mManager;
	private ServiceListener sl;
	private BundleContext   context;
	
	public void setupService() {
		logger.info("[TaaSVMManagerImpl] Starting the service");
		VMsAllocatorManager.init();
		mManager = MessageManager.instance();
		mManager.monitoringPublish("[TaaSVMManagerImpl] Service started");
		logger.info("[TaaSVMManagerImpl] Service started");
	}
	
	public void stopService() {
		logger.info("[TaaSVMManagerImpl] Stopping the service");
		VMsAllocatorManager.deleteAllVMs();
		mManager.monitoringPublish("[TaaSVMManagerImpl] Service stopped");
		logger.info("[TaaSVMManagerImpl] Service stopped");
	}
	
	public TaaSVMManagerImpl () {}
	
	public String createVM (VMRequest request) throws Exception {
		logger.info("[TaaSVMManagerImpl] Create VM method inovked!");
		return VMsAllocatorManager.createVM(request);
	}
	
	public  List<Availability> getAvailability(boolean cpuPreference) {
		logger.info("[TaaSVMManagerImpl] Get availability (with preference) method inovked");
		return VMsAllocatorManager.getAvailability(cpuPreference);
	}

	public String createExtVM(VMRequest request) {
		logger.info("[TaaSVMManagerImpl] Create external VM method inovked!");
		return VMsAllocatorManager.createExtVM(request);
	}

	public boolean deleteVM(String idVM) {
		logger.info("[TaaSVMManagerImpl] Delete VM method inovked!");
		return VMsAllocatorManager.deleteVM(idVM);
	}

	public List<Availability> getAvailability() {
		logger.info("[TaaSVMManagerImpl] Get availability method inovked!");
		return VMsAllocatorManager.getAvailability();
	}

	public boolean migrateVM(String vmId, String targetInfo) {
		logger.info("[TaaSVMManagerImpl] Migrate VM method inovked!");
		return VMsAllocatorManager.migrateVM(vmId, targetInfo);
	}

	public boolean sendVMs(List<String> vmIds, String targetInfo) {
		logger.info("[TaaSVMManagerImpl] Send VMs method inovked!");
		return VMsAllocatorManager.sendVMs(vmIds, targetInfo);
	}

	public List<Flavor> getFlavors() {
		logger.info("[TaaSVMManagerImpl] Get flavors method inovked!");
		ArrayList<Flavor> flavors = new ArrayList<Flavor>();
		for (FlavorType flavor : FlavorType.values()) {
			flavors.add(TaaSVMMAnagerConfiguration.getFlavor(flavor));
		}
		return flavors;
	}
	
	public BundleContext getContext() {
		return context;
	}

	public void setContext(BundleContext context) {
		this.context = context;
	}
}
