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

package eu.betaas.taas.taasvmmanager.occi;

import java.util.List;

import eu.betaas.taas.taasvmmanager.api.datamodel.InstanceType;
import eu.betaas.taas.taasvmmanager.occi.datamodel.Compute;
import eu.betaas.taas.taasvmmanager.occi.datamodel.ComputeCollection;
import eu.betaas.taas.taasvmmanager.occi.datamodel.InstanceTypeCollection;
import eu.betaas.taas.taasvmmanager.occi.datamodel.Network;
import eu.betaas.taas.taasvmmanager.occi.datamodel.NetworkCollection;
import eu.betaas.taas.taasvmmanager.occi.datamodel.Storage;
import eu.betaas.taas.taasvmmanager.occi.datamodel.StorageCollection;
import eu.betaas.taas.taasvmmanager.occi.datamodel.StorageType;
import eu.betaas.taas.taasvmmanager.occi.datamodel.User;
import eu.betaas.taas.taasvmmanager.occi.datamodel.UserCollection;
import eu.betaas.taas.taasvmmanager.occi.datamodel.Compute.Disk;
import eu.betaas.taas.taasvmmanager.occi.datamodel.Compute.Nic;

/**
* 
* @author Francisco Javier Nieto De-Santos (francisco.nieto@atos.net)
* @author Sergio Garcia Villalonga (sergio.garciavillalonga@atos.net)
*/
public interface OCCIClient {
	public class OCCIClientStatus {
		private CloudStatus status;
		private String errorMessage;
		
		public OCCIClientStatus () {
			status = CloudStatus.INITIALIZING;
			errorMessage = "";
		}
		
		public OCCIClientStatus (CloudStatus status, String errorMessage) {
			this.status = status;
			this.errorMessage = errorMessage;
		}
		
		public CloudStatus getStatus() {
			return status;
		}
		public void setStatus(CloudStatus status) {
			this.status = status;
		}
		public String getErrorMessage() {
			return errorMessage;
		}
		public void setErrorMessage(String errorMessage) {
			this.errorMessage = errorMessage;
		}
	}
	
	public static enum CloudStatus {INITIALIZING, OK, FAILED};
	
	/**** Constants to Flavor definition. Move to properties file ****/
	public static enum BETAAS_IMAGES {BETAAS_COMPUTE_X86,
	                                     BETAAS_STORAGE_X86,
	                                     BETAAS_APP_X86,
	                                     BETAAS_COMPUTE_ARM,
	                                     BETAAS_STORAGE_ARM,
	                                     BETAAS_APP_ARM}
	
	public static String COMPUTE_COMPUTE_NAME = "Compute BETaaS instance type";
	public static String STORAGE_COMPUTE_NAME = "Storage BETaaS instance type";
	public static String APP_COMPUTE_NAME = "App BETaaS instance type";
	public static String APP_HIGH_COMPUTE_NAME = "High-end app BETaaS instance type";
	
	public static int COMPUTE_COMPUTE_VCPUS = 4;
	public static int STORAGE_COMPUTE_VCPUS = 1;
	public static int APP_COMPUTE_VCPUS = 1;
	public static int APP_HIGH_COMPUTE_VCPUS = 2;
	
	public static int COMPUTE_COMPUTE_RAM = 512;
	public static int STORAGE_COMPUTE_RAM = 256;
	public static int APP_COMPUTE_RAM = 256;
	public static int APP_HIGH_COMPUTE_RAM = 512;
	
	public static int COMPUTE_COMPUTE_DISK = 1;
	public static int STORAGE_COMPUTE_DISK = 8;
	public static int APP_COMPUTE_DISK = 1;
	public static int APP_HIGH_COMPUTE_DISK = 1;
	/*****************************************************************/
	
	public static enum BETaaSComputeState {INIT, ACTIVE, STOPPED, SUSPENDED, DONE, FAILED}; 

	public static final String NETWORKPATH = "/network";
	public static final String STORAGEPATH = "/storage";
	public static final String COMPUTEPATH = "/compute";
	public static final String INSTANCETYPEPATH = "/instance_type";
	public static final String USERPATH = "/user";
	
	/**
	 * Get the list of registered users
	 * in an OpenNebula instance.
	 * 
	 * @return a collection of users
	 * @throws OCCIException
	 */
	public UserCollection getUsers() throws OCCIException;
	
	/**
	 * Get the details of a given user
	 * 
	 * @param id the user ID
	 * @return an User instance with all the related information
	 * @throws OCCIException if the given user does not exist
	 */
	public User getUser(String id) throws OCCIException;
	
	/**
	 * Get all the networks in an OpenNebula
	 * instance accessible for the user.
	 * 
	 * @return a collection of networks
	 * @throws OCCIException
	 */
	public NetworkCollection getNetworks () throws OCCIException;
	
	/**
	 * Get the details of a given network
	 * 
	 * @param id the network ID
	 * @return a Network instance with all the related information
	 * @throws OCCIException if the given network does not exist
	 */
	public Network getNetwork (String id) throws OCCIException;
	
	/**
	 * Create a network with the given properties
	 * 
	 * @param name the network name
	 * @param description the network desccription
	 * @param address the network address
	 * @param size the network size //TODO what does it mean
	 * @return a Network instance with updated network information, like its ID
	 * @throws OCCIException
	 */
	public Network createNetwork (String name,
								   String description,
								   String address,
								   String size) throws OCCIException;
	
	/**
	 * Delete a network given its ID
	 * 
	 * @param id the network ID
	 * @throws OCCIException
	 */
	public void deleteNetwork (String id) throws OCCIException;
	
	/**
	 * Get all the storages in an OpenNebula
	 * instance accessible for the user.
	 * 
	 * @return a collection of storages
	 * @throws OCCIException
	 */
	public StorageCollection getStorages () throws OCCIException;
	
	/**
	 * Get the details of a given storage
	 * 
	 * @param id the storage ID
	 * @return a Storage instance with all the related information
	 * @throws OCCIException if the given storage does not exist
	 */
	public Storage getStorage (String id) throws OCCIException;
	
	/**
	 * Create a storage with the given properties
	 * 
	 * @param name the storage name
	 * @param description the storage description
	 * @param type the storage type. It can take the values OS, CDROM and DATABLOCK
	 * @param size the storage size in MBs
	 * @param fstype the file system type of the storage in case it is a DATABLOcK one
	 * @return a Storage instance with updated storage information, like its ID
	 * @throws OCCIException
	 */
	public Storage createStorage (String name,
								   String description,
								   StorageType type,
								   int size, 
								   String fstype) throws OCCIException;
	/**
	 * Delete a storage given its id
	 * 
	 * @param id the storage ID
	 * @throws OCCIException
	 */
	public void deleteStorage (String id) throws OCCIException;
	
	/**
	 * Return the instance types that the system
	 * provides to use them when creating a compute
	 * 
	 * @return a collection of instance types
	 * @throws OCCIException
	 */
	public InstanceTypeCollection getInstanceTypes() throws OCCIException;
	
	/**
	 * Get the details of a given instance type
	 * 
	 * @param id the instance type ID
	 * @return a InstanceType instance with all the related information
	 * @throws OCCIException if the given instance type does not exist
	 */
	public InstanceType getInstanceType(String id) throws OCCIException;
	
	/**
	 * Create a Disk instance to its later use in a compute
	 * 
	 * @param storage the storage the disk will be instantiated from
	 * @param target the future mount point in the compute
	 * @return a Disk instance with the related information to use in a compute
	 * @throws OCCIException
	 */
	public Disk createComputeDisk(Storage storage, String  target) throws OCCIException;
	
	/**
	 * Create a Nic instance to its later use in a compute
	 * 
	 * @param network the network the compute will be connected to
	 * @param ip the ip address the compute will use in the interface exposed to the network
	 * @param mac the mac the compute will use in the interface exposed to the network
	 * @return a Nic instance with the related information to use in a compute
	 * @throws OCCIException
	 */
	public Nic createComputeNic(Network network, String ip, String mac) throws OCCIException;
	
	/**
	 * Get all the computes in an OpenNebula
	 * instance accessible for the user.
	 * 
	 * @return a collection of computes
	 * @throws OCCIException
	 */
	public ComputeCollection getComputes () throws OCCIException;
	
	/**
	 * Get the details of a given compute
	 * 
	 * @param id the compute ID
	 * @return a Compute instance with all the related information
	 * @throws OCCIException if the given compute does not exist
	 */
	public Compute getCompute (String id) throws OCCIException;
	
	/**
	 * Create a compute with the given properties
	 * 
	 * @param name the compute name
	 * @param cpu the number of CPUs the compute will have if the instance type allows custom instances
	 * @param memory the memory in MBs the compute will have if the instance type allows custom instances
	 * @param instanceType the instance type of the compute
	 * @param disks a list of Disks that will be attached to the compute
	 * @param nic a list of netowrk interface controllers that will be attached to the compute
	 * @return a Compute instance with updated compute information, like its ID
	 * @throws OCCIException if the given compute does not exist
	 */
	public Compute createCompute (String name,
								   int cpu,
								   int memory,
								   InstanceType instanceType,
								   List<Disk> disks,
								   List<Nic> nic) throws OCCIException;
	
	/**
	 * Change the state of a compute. The available states in the supported
	 *  OpenNebula release are INIT, PENDING, HOLD, SUSPENDED, RESUME, REBOOT,
	 *  RESET, ACTIVE, STOPPED, CANCEL, SHUTDOWN and DONE. If no error is raised
	 *  the update request is being process, but polling is required to confirm.
	 * 
	 * @param id the compute id
	 * @param newState the new state of the compute
	 * @throws OCCIException
	 */
	public void changeComputeState(String id, BETaaSComputeState newState)
												throws OCCIException;
	
	/**
	 * Mark a storage resource assigned to a compute to be saved in the current
	 *  state. The operation will take place when the compute is in DONE state.
	 *  If no error is raised the creation request will be process, but polling 
	 *  will be required to confirm.
	 * 
	 * @param compute the compute where the storage to be saved belongs to
	 * @param storageId the storage id
	 * @param name the name that will be assigned to the new storage
	 * @throws OCCIException
	 */
	public void saveComputeDisk(Compute compute,
	                              String storageId,
	                              String name) throws OCCIException;
	
	/**
	 * Delete a compute given its id
	 * 
	 * @param id the compute id
	 * @throws OCCIException
	 */
	public void deleteCompute (String id) throws OCCIException;
	
	/**
	 * Returns the status of the cloud
	 * 
	 * @return status of the cloud
	 */
	public OCCIClientStatus getStatus () throws OCCIException;
}
