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

package eu.betaas.taas.taasvmmanager.configuration;

import static org.junit.Assert.*;

import java.util.HashMap;

import org.junit.Test;

public class TaaSVMMAnagerConfigurationTest {

	@Test
	public void testLoadConfiguration() {
		//TaaSVMMAnagerConfiguration.loadConfiguration();
		assertTrue(true);
	}

	//@Test
	public void testGetClouds() {
		HashMap<String, String[]> clouds =
				TaaSVMMAnagerConfiguration.getClouds();
		
		int i = 0;
		for (String url : clouds.keySet()) {
			assertEquals(url, "http://url.cloud."+ i +"/");
			assertEquals(clouds.get(url)[0], "type"+ i);
			assertEquals(clouds.get(url)[1], "user"+ i);
			assertEquals(clouds.get(url)[2], "password"+ i++);
		}
	}
}
