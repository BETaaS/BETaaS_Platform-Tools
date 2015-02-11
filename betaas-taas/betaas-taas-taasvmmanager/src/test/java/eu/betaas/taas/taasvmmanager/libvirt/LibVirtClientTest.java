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

package eu.betaas.taas.taasvmmanager.libvirt;

import static org.junit.Assert.*;

import java.util.UUID;

import org.junit.Test;
import org.libvirt.Connect;
import org.libvirt.ConnectAuth;
import org.libvirt.ConnectAuthDefault;
import org.libvirt.Domain;
import org.libvirt.LibvirtException;
import org.libvirt.NodeInfo;

public class LibVirtClientTest {

	private String pathToImage = "/var/lib/libvirt/images/TinyCoreBase.img";
	private LibVirtClient client;
	
	@Test
	public void test() {
		/*getHypervisorInfo();
		UUID uuid = createVM();
		assertNotNull(uuid);
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		removeVM(uuid);
		getHypervisorInfo();
		assertTrue(true);*/
	}

	public void getHypervisorInfo () {
		client = new LibVirtClient();
	}
	
	public boolean createVM () {
		return client.createVM("TestVM", UUID.randomUUID(), 128*1024, 1, pathToImage);
	}
	
	public boolean removeVM(UUID uuid) {
		return client.deleteVM(uuid);
	}
}
