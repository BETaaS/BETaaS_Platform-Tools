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

import java.util.HashMap;
import java.util.List;

import eu.betaas.taas.taasvmmanager.api.TaaSVMManager;
import eu.betaas.taas.taasvmmanager.cloudsclients.VMRequest;
import eu.betaas.taas.taasvmmanager.occi.datamodel.InstanceType;
import eu.betaas.taas.taasvmmanager.vmsallocator.VMsAllocatorManager;

import org.apache.log4j.Logger;

/**
 * 
 * @author Francisco Javier Nieto De-Santos (francisco.nieto@atos.net)
 * @author Sergio Garcia Villalonga (sergio.garciavillalonga@atos.net)
 */
public class TaaSVMManagerImpl implements TaaSVMManager {
	
	private String user;
	private static Logger logger = Logger.getLogger("betaas.taas");
	
	public TaaSVMManagerImpl () {}
	
	public TaaSVMManagerImpl (String idApplication) {
		user = idApplication;
	}
	
	public String createVM (VMRequest request) {
		logger.info("Create VM method inovked!");
		VMsAllocatorManager myAllocator = VMsAllocatorManager.instance(user);
		if (myAllocator == null) {
			logger.error("Error instantiating VM Allocator manager");
			return null;
		}
		
		return myAllocator.createVM(request);
	}
	
	public HashMap<InstanceType, Integer> getAvailability(boolean cpuPreference) {
		logger.info("Get availability (with preference) method inovked");
		VMsAllocatorManager myAllocator = VMsAllocatorManager.instance(user);
		if (myAllocator == null) {
			logger.error("Error instantiating VM Allocator manager");
			return null;
		}
		
		return myAllocator.getAvailability(cpuPreference);
	}

	public String createExtVM(VMRequest request) {
		logger.info("Create external VM method inovked!");
		VMsAllocatorManager myAllocator = VMsAllocatorManager.instance(user);
		if (myAllocator == null) {
			logger.error("Error instantiating VM Allocator manager");
			return null;
		}
		
		return myAllocator.createExtVM(request);
	}

	public boolean deleteVM(String idVM) {
		logger.info("Delete VM method inovked!");
		VMsAllocatorManager myAllocator = VMsAllocatorManager.instance(user);
		if (myAllocator == null) {
			logger.error("Error instantiating VM Allocator manager");
			return false;
		}
		
		return myAllocator.deleteVM(idVM);
	}

	public HashMap<InstanceType, Integer> getAvailability() {
		logger.info("Get availability method inovked!");
		VMsAllocatorManager myAllocator = VMsAllocatorManager.instance(user);
		if (myAllocator == null) {
			logger.error("Error instantiating VM Allocator manager");
			return null;
		}
		
		return myAllocator.getAvailability();
	}

	public boolean migrateVM(String vmId, String targetInfo) {
		logger.info("Migrate VM method inovked!");
		VMsAllocatorManager myAllocator = VMsAllocatorManager.instance(user);
		if (myAllocator == null) {
			logger.error("Error instantiating VM Allocator manager");
			return false;
		}
		
		return myAllocator.migrateVM(vmId, targetInfo);
	}

	public boolean sendVMs(List<String> vmIds) {
		logger.info("Send VMs method inovked!");
		VMsAllocatorManager myAllocator = VMsAllocatorManager.instance(user);
		if (myAllocator == null) {
			logger.error("Error instantiating VM Allocator manager");
			return false;
		}
		
		return myAllocator.sendVMs(vmIds);
	}
}
