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

package eu.betaas.taas.taasvmmanager.opennebula.util;

import static org.junit.Assert.*;

import java.io.IOException;

import org.jdom.JDOMException;
import org.junit.Test;

import eu.betaas.taas.taasvmmanager.occi.datamodel.StorageType;
import eu.betaas.taas.taasvmmanager.opennebula.util.OCCIProcesser;

public class OCCIProcesserTest {

	private OCCIProcesser processer = new OCCIProcesser();
	
	private static final String LOWERCASEXML =
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n" +
			"<compute href=\"http://www.path.to/compute\">" +
				"<id>34</id>" +
				"<name>Name</name>" +
				"<cpu>1</cpu>" +
				"<memory>1024</memory>" +
				"<user href=\"http://www.path.to/user\" name=\"user_name\" />" +
				"<group>group</group>" +
				"<instance_type>Small</instance_type>" +
				"<state>PENDING</state>" +
				"<disk>" +
					"<storage href=\"http://path.to/storage0\" " +
							"name=\"storage0\" />" +
					"<save_as href=\"http://path.to/save_as1\" />" +
					"<type>" + StorageType.OS + "</type>" +
					"<target>sda1</target>" +
				"</disk>" +
					"<disk>" +
					"<storage href=\"http://path.to/storage1\" " +
						"name=\"storage1\" />" +
					"<type>" + StorageType.CDROM + "</type>" +
					"<target>sda2</target>" +
				"</disk>" +
				"<disk>" +
					"<storage href=\"http://path.to/storage2\" " +
						"name=\"storage2\" />" +
					"<save_as href=\"http://path.to/save_as2\" />" +
					"<type>" + StorageType.DATABLOCK + "</type>" +
					"<target>sda3</target>" +
				"</disk>" +
				"<nic>" +
					"<network href=\"http://path.to/network3\" " +
						"name=\"network3\" />" +
					"<ip>192.168.1.3</ip>" +
					"<mac>10:1f:74:ff:03:13</mac>" +
				"</nic>" +
				"<nic>" +
					"<network href=\"http://path.to/network4\" " +
						"name=\"network4\" />" +
					"<ip>192.168.1.4</ip>" +
					"<mac>10:1f:74:ff:03:14</mac>" +
				"</nic>" +	
				"<context />" +
			"</compute>\r\n";
	
	private static final String UPPERCASEXML =
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n" +
			"<COMPUTE href=\"http://www.path.to/compute\">" +
				"<ID>34</ID>" +
				"<NAME>Name</NAME>" +
				"<CPU>1</CPU>" +
				"<MEMORY>1024</MEMORY>" +
				"<USER href=\"http://www.path.to/user\" name=\"user_name\" />" +
				"<GROUP>group</GROUP>" +
				"<INSTANCE_TYPE>Small</INSTANCE_TYPE>" +
				"<STATE>PENDING</STATE>" +
				"<DISK>" +
					"<STORAGE href=\"http://path.to/storage0\" " +
							"name=\"storage0\" />" +
					"<SAVE_AS href=\"http://path.to/save_as1\" />" +
					"<TYPE>" + StorageType.OS + "</TYPE>" +
					"<TARGET>sda1</TARGET>" +
				"</DISK>" +
				"<DISK>" +
					"<STORAGE href=\"http://path.to/storage1\" " +
						"name=\"storage1\" />" +
					"<TYPE>" + StorageType.CDROM + "</TYPE>" +
					"<TARGET>sda2</TARGET>" +
				"</DISK>" +
				"<DISK>" +
					"<STORAGE href=\"http://path.to/storage2\" " +
						"name=\"storage2\" />" +
					"<SAVE_AS href=\"http://path.to/save_as2\" />" +
					"<TYPE>" + StorageType.DATABLOCK + "</TYPE>" +
					"<TARGET>sda3</TARGET>" +
				"</DISK>" +
				"<NIC>" +
					"<NETWORK href=\"http://path.to/network3\" " +
						"name=\"network3\" />" +
					"<IP>192.168.1.3</IP>" +
					"<MAC>10:1f:74:ff:03:13</MAC>" +
				"</NIC>" +
				"<NIC>" +
					"<NETWORK href=\"http://path.to/network4\" " +
						"name=\"network4\" />" +
					"<IP>192.168.1.4</IP>" +
					"<MAC>10:1f:74:ff:03:14</MAC>" +
				"</NIC>" +	
				"<CONTEXT />" +
			"</COMPUTE>\r\n";
	
	private static final String SAVEASXML =
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n" +
			"<COMPUTE href=\"http://www.path.to/compute\">" +
				"<DISK>" +
					"<STORAGE href=\"http://path.to/storage0\" " +
							"name=\"storage0\" />" +
					"<SAVE_AS href=\"http://path.to/save_as1\" />" +
					"<TYPE>" + StorageType.OS + "</TYPE>" +
					"<TARGET>sda1</TARGET>" +
				"</DISK>" +
				"<DISK>" +
					"<STORAGE href=\"http://path.to/storage2\" " +
						"name=\"storage2\" />" +
					"<SAVE_AS href=\"http://path.to/save_as2\" />" +
					"<TYPE>" + StorageType.DATABLOCK + "</TYPE>" +
					"<TARGET>sda3</TARGET>" +
				"</DISK>" +
			"</COMPUTE>\r\n";
	
	private static final String TESTHREF =
			"http://path.to/resource/23";
	
	@Test
	public void testTags2UpperCase() {
		try {
			assertEquals(UPPERCASEXML, processer.tags2UpperCase(LOWERCASEXML));
		} catch (JDOMException e) {
			e.printStackTrace();
			fail(e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testTags2LowerCase() {
		try {
			assertEquals(LOWERCASEXML, processer.tags2LowerCase(UPPERCASEXML));
		} catch (JDOMException e) {
			e.printStackTrace();
			fail(e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testGetSaveAsPayload() {
		try {
			assertEquals(SAVEASXML, processer.getSaveAsPayload(UPPERCASEXML));
		} catch (JDOMException e) {
			e.printStackTrace();
			fail(e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	
	}

	@Test
	public void testGetIdFromHref() {
		assertEquals("23", processer.getIdFromHref(TESTHREF));
	}

}
