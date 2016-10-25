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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.UUID;

import org.junit.Test;
/*import org.libvirt.Connect;
import org.libvirt.ConnectAuth;
import org.libvirt.ConnectAuthDefault;
import org.libvirt.Domain;
import org.libvirt.LibvirtException;
import org.libvirt.NodeInfo;*/

import eu.betaas.taas.taasvmmanager.api.datamodel.Flavor.FlavorType;

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

	@Test
	public void getHypervisorInfo () {
		try {
			//client = new LibVirtClient();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void createVM () {
		long memory;
		try 
		{ 
		Process p=Runtime.getRuntime().exec("free"); 
		p.waitFor(); 
		BufferedReader reader=new BufferedReader(new InputStreamReader(p.getInputStream())); 
		String line=reader.readLine(); 
		if (line != null) 
		{ 
		line=reader.readLine();
		
		if (line != null) {
			memory = Long.valueOf(line.replaceAll(" *"," ").split(" ")[3]);
			System.out.println("================");
			System.out.println(memory);
		} else {
			
		}
		} else {
			
		}
		} catch (Exception e){
			
		}
	}
	
	public boolean removeVM(UUID uuid) {
		return client.deleteVM(uuid);
	}
}
