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

import static org.junit.Assert.*;

import java.awt.color.CMMException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import org.junit.Test;

import eu.betaas.taas.taasvmmanager.api.impl.TaaSVMManagerImpl;
import eu.betaas.taas.taasvmmanager.cloudsclients.VMProperties;
import eu.betaas.taas.taasvmmanager.cloudsclients.VMRequest;
import eu.betaas.taas.taasvmmanager.occi.datamodel.ComputeState;
import eu.betaas.taas.taasvmmanager.occi.datamodel.InstanceType;

/**
 * 
 * @author Francisco Javier Nieto De-Santos (francisco.nieto@atos.net)
 * @author Sergio Garcia Villalonga (sergio.garciavillalonga@atos.net)
 */
public class TaaSVMManagerTest {
	private TaaSVMManager vmManager;
	private String        vmHref = null;
	
	@Test
	public void test() {
		/*testGetProperties();
		vmManager = new TaaSVMManagerImpl("0");
		testCreateVM();
		testVMInfo();
		testGetVMs();
		testStopVM();
		testStartVM();
		testRemoveVM();
		testGetAvailability();*/
	}
	
	public void testGetProperties() {
		InputStream in =
			this.getClass().getResourceAsStream("/eu/betaas/taas/taasvmmanager/taasvmmanager.properties");
		assertNotNull(in);
		try {
			in.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void testCreateVM() {
		VMRequest request = new VMRequest();
		
		request.setCores(1);
		request.setMemory(128);
		
		vmHref = vmManager.createVM(request);
		assertNotNull(vmHref);
	}
	
	private void testRemoveVM() {
		VMProperties properties;
		ArrayList<String> vms;
		
		/*vmManager.deleteVM(vmHref);
		try {
			do {
				Thread.sleep(2000);
				properties = vmManager.getVMInfo(vmHref);
			} while (properties.getStatus().equals(ComputeState.CANCEL.toString())
					|| properties.getStatus().equals(ComputeState.SHUTDOWN.toString()));
			
			assertEquals(ComputeState.DONE.toString(), properties.getStatus());
			
			
			vms = vmManager.getVMs();
			assertEquals(0, vms.size());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}*/
	}
	
	private void testGetAvailability() {
		HashMap<InstanceType, Integer> cpuPref, memPref;
		cpuPref = vmManager.getAvailability();
		memPref = vmManager.getAvailability();
		
		assertNotNull(cpuPref);
		assertNotNull(memPref);
	}
}
