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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.UUID;

import javax.security.auth.login.Configuration;

import org.apache.log4j.Logger;
import org.libvirt.Connect;
import org.libvirt.Domain;
import org.libvirt.LibvirtException;
import org.libvirt.NodeInfo;

import eu.betaas.taas.taasvmmanager.api.datamodel.Flavor;
import eu.betaas.taas.taasvmmanager.api.datamodel.Flavor.FlavorType;
import eu.betaas.taas.taasvmmanager.configuration.TaaSVMMAnagerConfiguration;
import eu.betaas.taas.taasvmmanager.configuration.TaaSVMMAnagerConfiguration.SystemArchitecture;
import eu.betaas.taas.taasvmmanager.messaging.MessageManager;
import eu.betaas.taas.taasvmmanager.util.Quota;
import eu.betaas.taas.taasvmmanager.util.Quota.QuotaLocalization;

public class LibVirtClient {
	
	private Logger log;
	
	private Quota  quota;
	private String cpuArch;
	private MessageManager mManager;
	
	public LibVirtClient () throws Exception {
		Connect conn;
		NodeInfo ni;
		
		log = Logger.getLogger("betaas.taas");
		mManager = MessageManager.instance();
		
		log.info("[LibVirtClient] Initializing LibVirt client");
		mManager.monitoringPublish("Initializing LibVirtClient");
		
		try {
			log.info("[LibVirtClient] Connecting to local hypervisor");
			
			conn = new Connect("qemu+tcp://127.0.0.1/system");
			ni = conn.nodeInfo();
			
			log.info("[LibVirtClient] model: " + ni.model + " mem(kb):" + ni.memory);
			mManager.monitoringPublish("Connecting to local hypervisor. " +
					"model: " + ni.model + " mem(kb):" + ni.memory);
			
			log.info("[LibVirtClient] Shutting downs active VMs");
			mManager.monitoringPublish("Shutting downs active VMs");
			String[]  domains = conn.listDefinedDomains();
			ArrayList<String> waitfor = new ArrayList<String>();
			for (String domainName : domains) {
				Domain vm = conn.domainLookupByName(domainName);
				
				if (vm.getName().contains("betaas-")) {
					log.info("[LibVirtClient] Shutting down " + vm.getName());
					mManager.monitoringPublish("Shutting down " + vm.getName());
					vm.shutdown();
					waitfor.add(domainName);
				}
			}
			
			log.info("[LibVirtClient] Removing active VMs");
			for (String domainName : waitfor) {
				Domain vm = conn.domainLookupByName(domainName);
				
				while (vm.isActive() == 1) {
					log.info("[LibVirtClient] Waiting for  " + vm.getName() + " to be halted");
					Thread.sleep(1000);
				}
				log.info("[LibVirtClient] Deleting " + vm.getName());
				mManager.monitoringPublish("Deleting " + vm.getName());
				vm.destroy();
			}
			
			log.info("[LibVirtClient] Getting free memory");
			long memory;
			if (TaaSVMMAnagerConfiguration.getSystemArchitecture() == SystemArchitecture.INTEL) {
				memory = conn.getFreeMemory();
			} else {
				memory = getFreeMemoryFromOS();
			}
			
			log.info("[LibVirtClient] Getting capacity");
			long capacity = conn.storagePoolLookupByName("default").getInfo().available;
			
			conn.close();
			
			quota   = new Quota(QuotaLocalization.LOCAL, ni.cpus, memory, capacity);
			cpuArch = ni.model;
			
			log.info("[LibVirtClient] Available CPUs   : " + ni.cpus);
			log.info("[LibVirtClient] Available memory : " + memory/1024/1024 + "MB");
			log.info("[LibVirtClient] Available disk   : " + capacity/1024/1024/1024 + "GB");
			
		} catch (LibvirtException e) {
			log.error(e.getMessage());
		}
		mManager.monitoringPublish("[LibVirtClient] LibVirt client initialized");
		log.info("[LibVirtClient] LibVirt client initialized");
	}
	
	private synchronized void updateQuota(double vcpu, double disk) {
		log.info("[LibVirtClient] Updating quota: ");
		log.info("[LibVirtClient]     cpu  : " + vcpu);
		log.info("[LibVirtClient]     disk : " + disk);
		quota.setvCpu(quota.getvCpu() + vcpu);
		quota.setDisk(quota.getDisk() + disk);
	}
	
	public boolean createVM(String vmName,
							  UUID   vmUuid,
			                  Flavor flavor,
	                          String vmImage) throws Exception {
		String  template;
		Connect conn;
		
		try {
			log.info("[LibVirtClient] Connecting to local hypervisor");
			conn = new Connect("qemu+tcp://127.0.0.1/system");
			
			log.info("Creating template");
			template = buildTemplate(
				vmName, vmUuid, flavor.getMemory(),flavor.getvCpu(), vmImage);
			
			log.info("[LibVirtClient] Resulting template: \n" + template);
			log.info("[LibVirtClient] Creating VM");
			Domain domain = conn.domainCreateXML(template, 0);
			
			conn.close();
			
			// TODO get image size
			updateQuota(-flavor.getvCpu(), -0);
		} catch (LibvirtException e) {
			mManager.monitoringPublish("There has been a problem creating the VM");
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	public boolean deleteVM(UUID vmId) {
		Domain domain;
		Connect conn;
		
		try {
			log.info("[LibVirtClient] Connecting to local hypervisor");
			conn = new Connect("qemu+tcp://127.0.0.1/system");
			
			domain = conn.domainLookupByUUID(vmId);
			domain.destroy();
			
			conn.close();
			
			Flavor flavor =
					TaaSVMMAnagerConfiguration.getFlavor(
							FlavorType.valueOf(domain.getName().split("-")[1]));
			updateQuota(-flavor.getvCpu(), -flavor.getDisk());
		} catch (LibvirtException e) {
			mManager.monitoringPublish("There has been a problem deleting the VM");
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	public Quota getQuota () {
		return quota.clone();
	}
	
	public boolean migrate (String vmId, String remoteIp) {
		Domain domain;
		Connect localConn;
		Connect remoteConn;
		UUID uuid = UUID.fromString(vmId);
		
		try {
			localConn = new Connect("qemu+tcp://127.0.0.1/system");
			remoteConn = new Connect("qemu+tcp://" + remoteIp +"/system"); 
			domain = localConn.domainLookupByUUID(uuid);
			
			domain.migrate(remoteConn, 2, null, null, 0);
		} catch (LibvirtException e) {
			mManager.monitoringPublish("There has been a problem migrating");
			return false;
		}
		
		return true;
	}
	
	public String buildTemplate (String name, UUID uuid, long memory,
			int vCpu, String vmImage) {
		String arch, machine, kernel, cmdline, dtb;
		SystemArchitecture architecture =
				TaaSVMMAnagerConfiguration.getSystemArchitecture();
		
		if (architecture == SystemArchitecture.INTEL) {
			arch    = "i686";
			machine = "pc";
			cmdline = "";
			kernel  = "";
			dtb     = "";
		} else {
			arch    = "arm";
			machine = "vexpress-a15";
			cmdline = "root=/dev/vda console=ttyAMA0 rootwait";
			kernel  = TaaSVMMAnagerConfiguration.getCustomKernelPath();
			dtb     = TaaSVMMAnagerConfiguration.getCustomDtbPath();
		}
		
		String template = LibVirtTemplates.getTemplate(
				name, uuid.toString(), String.valueOf(memory),
				String.valueOf(vCpu), arch, machine, kernel,
				cmdline, dtb, vmImage);
		
		return template;
		
		/*
		 * http://wiki.libvirt.org/page/QEMUSwitchToLibvirt
		 * 
		sudo qemu-system-arm 
			-enable-kvm
			-smp 1
			-m 256
			-M vexpress-a15 // machine in type
			-cpu host
			-kernel /home/pi/vexpress-zImage
			-dtb /home/pi/vexpress-v2p-ca15-tc1.dtb --> <dtb>/root/ppc.dtb</dtb> 
			-append "root=/dev/vda console=ttyAMA0 rootwait"
			-drive if=none,file=/home/pi/opensuse-factory.img,id=factory
			-device virtio-blk-device,drive=factory
			-net nic,macaddr=02:fd:01:de:ad:34 -net tap
			-monitor null -serial stdio -nographic
			*/
	}
	
	private long getFreeMemoryFromOS() throws Exception {
		long memory = -1;
		try { 
			Process p=Runtime.getRuntime().exec("free"); 
			p.waitFor(); 
			BufferedReader reader=
				new BufferedReader(new InputStreamReader(p.getInputStream())); 
			String line=reader.readLine(); 
			if (line != null) { 
				line=reader.readLine();		
				if (line != null) {
					memory =
						Long.valueOf(line.replaceAll(" +"," ").split(" ")[3]);
					memory /= 1024;
				} else {
					Exception e =
							new Exception("Error reading memory from OS");
					throw e;
				}
			} else {
				Exception e =
						new Exception("Error reading memory from OS");
				throw e;
			}
		} catch(IOException e1) {
			Exception e =
					new Exception(e1.getMessage());
			throw e;
		} catch(InterruptedException e2) {
			Exception e =
					new Exception(e2.getMessage());
			throw e;
		  } 
		
		return memory;
	}
}
