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

package eu.betaas.taas.taasvmmanager.api;

import java.util.HashMap;
import java.util.List;

import eu.betaas.taas.taasvmmanager.cloudsclients.VMRequest;
import eu.betaas.taas.taasvmmanager.occi.datamodel.InstanceType;

/**
 * 
 * @author Francisco Javier Nieto De-Santos (francisco.nieto@atos.net)
 * @author Sergio Garcia Villalonga (sergio.garciavillalonga@atos.net)
 */
public interface TaaSVMManager {

	/**
	 * Creates a virtual machine in the BETaaS instance
	 *  
	 * @param request the desired features of the virtual machine
	 * @return the URL of the created virtual machine
	 */
	public String createVM (VMRequest request); ///TODO Modify VMRequest
	
	/**
	 * Creates a virtual machine in an external provider
	 *  
	 * @param request the desired features of the virtual machine
	 * @return the URL of the created virtual machine
	 */
	public String createExtVM (VMRequest request); ///TODO Modify VMRequest

	
	/**
	 * Removes a virtual machine given its id
	 * 
	 * @param idVM the id that references to the virtual machine
	 * @return true if the removing is successful, false otherwise
	 */
	public boolean deleteVM (String idVM);
	
	/**
	 * Gets the available resources for every kind of VM
	 * 
	 * @return a map between VM type and the available resources.
	 */
	public HashMap<InstanceType, Integer> getAvailability();
	
	/**
	 * Migrates a VM to other device
	 * with a VMManager available on it
	 * 
	 * @param vmId ID of the VM to be migrated
	 * @param targetInfo ID of the target gateway
	 * @return true if the migration has been successful, false otherwise
	 */
	public boolean migrateVM(String vmId, String targetInfo);
	//TODO return information about the problem, if any?
	
	/**
	 * Migrates a set of VMs to other device
	 * with a VMManager available on it
	 * 
	 * @param vmIds list of IDs of the VMs to be migrated
	 * @return true if the migration has been successful, false otherwise
	 */
	public boolean sendVMs(List<String> vmIds);
	//TODO return information about the problem, if any?
}
