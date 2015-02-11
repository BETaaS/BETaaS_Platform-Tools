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

package eu.betaas.taas.taasvmmanager.opennebula.client;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import eu.betaas.taas.taasvmmanager.occi.client.OpenNebulaClient;
import eu.betaas.taas.taasvmmanager.occi.datamodel.Compute;
import eu.betaas.taas.taasvmmanager.occi.datamodel.ComputeCollection;
import eu.betaas.taas.taasvmmanager.occi.datamodel.InstanceType;
import eu.betaas.taas.taasvmmanager.occi.datamodel.InstanceTypeCollection;
import eu.betaas.taas.taasvmmanager.occi.datamodel.Link;
import eu.betaas.taas.taasvmmanager.occi.datamodel.Network;
import eu.betaas.taas.taasvmmanager.occi.datamodel.NetworkCollection;
import eu.betaas.taas.taasvmmanager.occi.datamodel.Storage;
import eu.betaas.taas.taasvmmanager.occi.datamodel.StorageCollection;
import eu.betaas.taas.taasvmmanager.occi.datamodel.StorageType;
import eu.betaas.taas.taasvmmanager.occi.datamodel.User;
import eu.betaas.taas.taasvmmanager.occi.datamodel.UserCollection;
import eu.betaas.taas.taasvmmanager.occi.OCCIClient;
import eu.betaas.taas.taasvmmanager.occi.OCCIException;

/**
* 
* @author Francisco Javier Nieto De-Santos (francisco.nieto@atos.net)
* @author Sergio Garcia Villalonga (sergio.garciavillalonga@atos.net)
*/
public class OpenNebulaClientTest {
	
	@Test
	public void testOpenNebulaInfrastructure () {
		/*OpenNebulaClient client = new OpenNebulaClientImpl(ENDPOINTURL,
															TESTUSER,
															PASSWORD);*/
		
		Network network;
		Storage storage;
		Compute compute;
		
		/*testGetUsers(client);
		testGetUser(client);
		testGetNetworks(client);
		testGetNetwork(client);
		testGetStorages(client);
		testGetStorage(client);
		testGetComputes(client);
		testGetCompute(client);
		network = testCreateNetwork(client);
		network = testModifyNetwork(client, network);
		storage = testCreateStorage(client);
		storage = testModifyStorage(client, storage);
		compute = testCreateCompute(client, network, storage);
		compute = testModifyCompute(client, compute);
		testDeleteCompute(client, compute);
		testDeleteStorage(client, storage);
		testDeleteNetwork(client, network);*/
	}
	
	public void testGetUsers(OCCIClient oneClient) {
		UserCollection collection;
		try {
			collection = oneClient.getUsers();
			assertTrue(collection.getUser().size() > 0);
		} catch (OCCIException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	public void testGetUser(OCCIClient oneClient) {
		UserCollection collection;
		User user;
		Link link;
		try {
			collection = oneClient.getUsers();
			assertTrue(collection.getUser().size() > 0);
			
			link = collection.getUser().get(0);
			user = oneClient.getUser(link.getHref());
			assertNotNull(user);
			assertEquals(link.getHref(), user.getHref());
			assertEquals(link.getName(), user.getName());
		} catch (OCCIException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	public void testGetNetworks(OCCIClient oneClient) {
		NetworkCollection collection;
		try {
			collection = oneClient.getNetworks();
			assertTrue(collection.getNetwork().size() > 0);
		} catch (OCCIException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	public void testGetNetwork(OCCIClient oneClient) {
		NetworkCollection collection;
		Network network;
		Link link;
		try {
			collection = oneClient.getNetworks();
			assertTrue(collection.getNetwork().size() > 0);
			
			link = collection.getNetwork().get(0);
			network = oneClient.getNetwork(link.getHref());
			assertNotNull(network);
			assertEquals(link.getHref(), network.getHref());
			assertEquals(link.getName(), network.getName());
		} catch (OCCIException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	public void testGetStorages(OCCIClient oneClient) {
		StorageCollection collection;
		try {
			collection = oneClient.getStorages();
			assertTrue(collection.getStorage().size() > 0);
		} catch (OCCIException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	public void testGetStorage(OCCIClient oneClient) {
		StorageCollection collection;
		Storage storage;
		Link link;
		try {
			collection = oneClient.getStorages();
			assertTrue(collection.getStorage().size() > 0);
			
			link = collection.getStorage().get(0);
			storage = oneClient.getStorage(link.getHref());
			assertNotNull(storage);
			assertEquals(link.getHref(), storage.getHref());
			assertEquals(link.getName(), storage.getName());
		} catch (OCCIException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	public void testGetInstanceTypes(OCCIClient oneClient) {
		InstanceTypeCollection collection;
		try {
			collection = oneClient.getInstanceTypes();
			assertTrue(collection.getInstanceType().size() > 0);
		} catch (OCCIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	public void testGetInstanceType(OCCIClient oneClient) {
		InstanceTypeCollection collection;
		InstanceType instanceType;
		Link link;
		try {
			collection = oneClient.getInstanceTypes();
			assertTrue(collection.getInstanceType().size() > 0);
			
			link = collection.getInstanceType().get(0);
			instanceType = oneClient.getInstanceType(link.getHref());
			assertNotNull(instanceType);
			assertEquals(link.getHref(), instanceType.getHref());
			assertEquals(link.getName(), instanceType.getName());
		} catch (OCCIException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	public void testGetComputes(OCCIClient oneClient) {
		ComputeCollection collection;
		try {
			collection = oneClient.getComputes();
			assertTrue(collection.getCompute().size() > 0);
		} catch (OCCIException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	public void testGetCompute(OCCIClient oneClient) {
		ComputeCollection collection;
		Compute compute;
		Link link;
		try {
			collection = oneClient.getComputes();
			assertTrue(collection.getCompute().size() > 0);
			
			link = collection.getCompute().get(0);
			compute = oneClient.getCompute(link.getHref());
			assertNotNull(compute);
			assertEquals(link.getHref(), compute.getHref());
			assertEquals(link.getName(), compute.getName());
		} catch (OCCIException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	public Network testCreateNetwork(OCCIClient oneClient) {
		Network  network = null;
		boolean found   = false;
		String networkName        = "networkName";
		String networkDescription = "Network Description";
		String networkAddress     = "192.172.2.0/24";
		String networkSize        = "256";
		String href;
		
		try {
			network = oneClient.createNetwork(networkName,
			                                  networkDescription,
			                                  networkAddress,
			                                  networkSize);
			
			assertNotNull(network);
			href = network.getHref();
			
			for (Link link : oneClient.getNetworks().getNetwork()) {
				found = found || href.equals(link.getHref());
			}
			
			network = oneClient.getNetwork(href);
			
			assertNotNull(network);
			assertEquals(href, network.getHref());
			assertEquals(networkName, network.getName());
			assertEquals(networkDescription, network.getDescription());
			assertEquals(networkAddress, network.getAddress());
			assertEquals(networkSize, network.getSize());
		} catch (OCCIException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		
		return network;
	}
	
	public Network testModifyNetwork(OCCIClient oneClient,
	                                  Network          network) {
		return network;
	}
	
	public Storage testCreateStorage(OCCIClient oneClient) {
		Storage  storage = null;
		boolean found   = false;
		String      storageName        = "networkName";
		String      storageDescription = "Network Description";
		StorageType storageType        = StorageType.DATABLOCK;
		String      fsType             = "ext4";
		int        storageSize        = 128;
		String href;
		
		try {
			storage = oneClient.createStorage(storageName,
			                                  storageDescription,
			                                  storageType,
			                                  storageSize,
			                                  fsType);
			
			assertNotNull(storage);
			href = storage.getHref();
			
			for (Link link : oneClient.getStorages().getStorage()) {
				found = found || href.equals(link.getHref());
			}
			
			storage = oneClient.getStorage(href);
			
			assertNotNull(storage);
			assertEquals(href, storage.getHref());
			assertEquals(storageName, storage.getName());
			assertEquals(storageDescription, storage.getDescription());
			assertEquals(storageType, storage.getType());
			assertEquals(storageSize, storage.getSize());
		} catch (OCCIException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		
		return storage;
	}
	
	public Storage testModifyStorage(OCCIClient oneClient,
	                                  Storage          storage) {
		return storage;
	}
	
	public Compute testCreateCompute(OCCIClient oneClient,
									  Network          network,
									  Storage          storage) {
		Compute compute = null;
		InstanceType computeInstanceType;
		String href;
		boolean found = false;
		List <Compute.Nic> computeNics   = new ArrayList<Compute.Nic>();
		List <Compute.Disk> computeDisks = new ArrayList<Compute.Disk>();
		String computeName = "computeName";
		int computeCPUs = 1;
		int computeMemory = 256;
		
		String  soDiskHref = storage.getHref(); //TODO search for a suitable disk;
		String  ip         = "10.11.12.3";
		String  mac        = "10:1f:74:ff:03:16";
		String  target     = "/dev/sdb";
		
		try {
			Compute.Nic nic = oneClient.createComputeNic(network,ip, mac);
			computeNics.add(nic);
			
			Compute.Disk osDisk = oneClient.createComputeDisk(
					oneClient.getStorage(soDiskHref), target);
			Compute.Disk datablockDisk = oneClient.createComputeDisk(storage,
			                                                         target);
			computeDisks.add(osDisk);
			//computeDisks.add(datablockDisk);
			
			computeInstanceType = oneClient.getInstanceType(
					oneClient.getInstanceTypes().getInstanceType().get(0).getHref());
			
			compute = oneClient.createCompute(computeName,
			                                  computeCPUs,
			                                  computeMemory,
			                                  computeInstanceType,
			                                  computeDisks,
			                                  computeNics);
			
			assertNotNull(compute);
			href = compute.getHref();
			
			for (Link link : oneClient.getComputes().getCompute()) {
				found = found || href.equals(link.getHref());
			}
			
			compute = oneClient.getCompute(href);
			
			assertNotNull(compute);
			assertEquals(href, compute.getHref());
			assertEquals(computeName, compute.getName());
			assertEquals(computeInstanceType.getName(), compute.getInstanceType());
			assertEquals(1, compute.getNic().size());
			assertEquals(nic.getIp(), compute.getNic().get(0).getIp());
			//assertEquals(nic.getMac(), compute.getNic().get(0).getMac());
			assertEquals(nic.getNetwork().getHref(),
					compute.getNic().get(0).getNetwork().getHref());
			assertEquals(nic.getNetwork().getName(),
					compute.getNic().get(0).getNetwork().getName());
			
			if (compute.getInstanceType().equals("custom")) {
				assertEquals(new Integer(computeCPUs), compute.getCpu());
				assertEquals(new Integer(computeMemory), compute.getMemory());
			}
			
			for (Compute.Disk instantiatedDisk : compute.getDisk()) {
				for (Compute.Disk initialDisk : computeDisks) {
					found = found || (instantiatedDisk.getStorage().getHref() ==
					                      initialDisk.getStorage().getHref() &&
					                  instantiatedDisk.getSaveAs().getName() ==
					                      initialDisk.getStorage().getName() &&
					                  instantiatedDisk.getTarget() ==
					                      initialDisk.getTarget()  &&
					                  instantiatedDisk.getType()   ==
					                       initialDisk.getType());
				}
				assertTrue(found);
				found = false;
			}
		} catch (OCCIException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		
		return compute;
	}
	
	public Compute testModifyCompute(OCCIClient oneClient,
                                      Compute          compute) {
		return compute;
	}
	
	public void testDeleteCompute(OCCIClient oneClient,
	                                Compute          compute) {
		String href = compute.getHref();
		
		try {
			oneClient.deleteCompute(href);
			
			for (Link link : oneClient.getComputes().getCompute()) {
				assertTrue(!href.equals(link.getHref()));
			}
		} catch (OCCIException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	public void testDeleteStorage(OCCIClient oneClient,
	                                Storage          storage) {
		String href = storage.getHref();
		
		try{
			oneClient.deleteStorage(href);
			
			for (Link link : oneClient.getStorages().getStorage()) {
				assertTrue(!href.equals(link.getHref()));
			}
		} catch (OCCIException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	public void testDeleteNetwork(OCCIClient oneClient,
	                                Network          network) {
		String href = network.getHref();
		
		try {
			oneClient.deleteNetwork(href);
			
			for (Link link : oneClient.getNetworks().getNetwork()) {
				assertTrue(!href.equals(link.getHref()));
			}
		} catch (OCCIException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
}
