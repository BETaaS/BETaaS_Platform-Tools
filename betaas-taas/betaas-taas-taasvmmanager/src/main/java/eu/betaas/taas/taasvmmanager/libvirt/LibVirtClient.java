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

import java.util.UUID;

import org.apache.log4j.Logger;
import org.libvirt.Connect;
import org.libvirt.Domain;
import org.libvirt.LibvirtException;
import org.libvirt.NodeInfo;

public class LibVirtClient {
	
	private Logger log;
	
	private String TEMPLATE =
			"<domain type='kvm'>" +
				"<name>$vmName</name>" +
				"<uuid>$vmUuid</uuid>" +
				"<memory>$vmMemory</memory>" +
				"<vcpu>$vmCpu</vcpu>" +
				"<os>" +
					"<type arch='x86_64' machine='pc-1.0'>hvm</type>" +
					"<boot dev='hd'/>" +
				"</os>" +
				"<clock offset='utc'/>" +
				"<on_poweroff>destroy</on_poweroff>" +
				"<on_reboot>restart</on_reboot>" +
				"<on_crash>destroy</on_crash>" +
				"<devices>" +
					"<emulator>/usr/bin/kvm</emulator>" +
					"<disk type='file' device='disk'>" +
						"<source file='$vmImage'/>" +
						"<driver name='qemu' type='raw'/>" +
						"<target dev='hda' bus='ide'/>" +
						"<alias name='ide0-0-0'/>" +
						"<address type='drive' controller='0' bus='0' unit='0'/>" +
					"</disk>" +
					"<controller type='ide' index='0'>" +
						"<alias name='ide0'/>" +
						"<address type='pci' domain='0x0000' bus='0x00' slot='0x01' function='0x1'/>" +
					"</controller>" +
					"<interface type='network'>" +
						"<mac address='52:54:00:6a:84:e9'/>" +
						"<source network='default'/>" +
						"<target dev='vnet0'/>" +
						"<alias name='net0'/>" +
						"<address type='pci' domain='0x0000' bus='0x00' slot='0x03' function='0x0'/>" +
					"</interface>" +
					"<input type='mouse' bus='ps2'/>" +
					"<graphics type='vnc' port='5900' autoport='yes'/>" +
					"<video>" +
						"<model type='cirrus' vram='9216' heads='1'/>" +
						"<alias name='video0'/>" +
						"<address type='pci' domain='0x0000' bus='0x00' slot='0x02' function='0x0'/>" +
					"</video>" +
				"</devices>" +
			"</domain>";
	
	
	public LibVirtClient () {
		Connect conn;
		NodeInfo ni;
		
		log = Logger.getLogger("betaas.taas");
		
		try {
			conn = new Connect("qemu+tcp://127.0.0.1/system");
			ni = conn.nodeInfo();
			
			log.info("Connecting to local hypervisor");
			log.debug("model: " + ni.model + " mem(kb):" + ni.memory);
			
			int numOfVMs = conn.numOfDomains();
			
			/* Should be 0 */
			log.debug("Active VMs");
			for (int i = 1; i < numOfVMs + 1; i++) {
				Domain vm = conn.domainLookupByID(i);
				
				if (vm.getName().contains("betaas-")) {
					log.debug("vm name: " + vm.getName() + "  type: " + vm.getOSType()
						+ " max mem: " + vm.getMaxMemory() + " max cpu: " + vm.getMaxVcpus());
				}
			}
			
			String cap = conn.getCapabilities();
			log.debug("System capabilities");
			log.debug("cap: " + cap);
			
			conn.close();
		} catch (LibvirtException e) {

			log.error(e.getMessage());
			//e.printStackTrace();
		}
	}
	
	public boolean createVM(String vmName,
							UUID vmUuid,
			                double vmMemory,
	                        int vmCpu,
	                        String vmImage) {
		String template;
		
		Connect conn;
		
		try {
			log.info("Connecting to local hypervisor");
			conn = new Connect("qemu+tcp://127.0.0.1/system");
			
			log.debug("Creating template");
			vmUuid = UUID.randomUUID();
			template = TEMPLATE;
			template = template.replace("$vmName", vmName);
			template = template.replace("$vmMemory", String.valueOf(vmMemory));
			template = template.replace("$vmCpu", String.valueOf(vmCpu));
			template = template.replace("$vmImage", vmImage);
			template = template.replace("$vmUuid", vmUuid.toString());
			
			log.debug("Resulting template: \n" + template);
			log.info("Creating VM");
			Domain domain = conn.domainCreateXML(template, 0);
			
			conn.close();
		} catch (LibvirtException e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	public boolean deleteVM(UUID vmId) {
		Domain domain;
		Connect conn;
		
		try {
			log.info("Connecting to local hypervisor");
			conn = new Connect("qemu+tcp://127.0.0.1/system");
			
			domain = conn.domainLookupByUUID(vmId);
			domain.destroy();
			
			conn.close();
		} catch (LibvirtException e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
}
