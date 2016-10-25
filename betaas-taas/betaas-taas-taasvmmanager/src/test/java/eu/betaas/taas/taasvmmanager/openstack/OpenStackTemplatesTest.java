package eu.betaas.taas.taasvmmanager.openstack;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.Test;

import eu.betaas.taas.taasvmmanager.occi.datamodel.Compute.Disk;
import eu.betaas.taas.taasvmmanager.occi.datamodel.Link;
import eu.betaas.taas.taasvmmanager.occi.datamodel.StorageType;

public class OpenStackTemplatesTest {
	
	@Test
	public void testGetRequestTokenTemplate() {
		String username = "username";
		String password = "password";
		String tenant = "tenant";
		String requestTokenBody =
				"{"
			 +    "\"auth\":{"
			 +      "\"tenantName\":\"tenant\","
			 +      "\"passwordCredentials\":{"
			 +        "\"username\":\"username\","
			 +        "\"password\":\"password\""
			 +      "}"
			 +    "}"
			 +  "}";
		
		assertEquals(
				requestTokenBody, 
				OpenStackTemplates.getRequestTokenTemplate(
						username, password, tenant));
	}

	@Test
	public void testGetCreateFlavorTemplate() {
		String name  = "flavor";
		int    ram   = 256;
		int    vcpus = 2;
		int    disk  = 1;
		UUID   uuid  = UUID.randomUUID();		
		String createFlavorBody =
			"{"
		  +   "\"flavor\":{"
		  +		"\"name\":\"flavor\","
		  +     "\"ram\":256,"
		  +     "\"vcpus\":2,"
		  +     "\"disk\":1,"
		  +     "\"id\":\""+ uuid.toString() +"\","
		  +     "\"os-flavor-access:is_public\":true"
		  +   "}"
		  + "}";
		
		assertEquals(
				createFlavorBody, 
				OpenStackTemplates.getCreateFlavorTemplate(
						name, ram, vcpus, disk, uuid));
	}

	@Test
	public void testGetCreateNetworkTemplate() {
		String name  = "network";
		String createNetworkBody =
			"{"
		  +   "\"network\":{"
		  +     "\"name\":\"network\","
		  +     "\"admin_state_up\":true"
		  +   "}"
		  + "}";
		
		assertEquals(
				createNetworkBody,
				OpenStackTemplates.getCreateNetworkTemplate(name));
	}

	@Test
	public void testGetCreateSubnetTemplate() {
		String name = "flavor";
		String cidr = "192.168.1.0/8";
		String uuid = UUID.randomUUID().toString();
		String createSubnetBody =
				"{"
			  +   "\"subnet\":{"
			  +     "\"name\":\"flavor\","
			  +     "\"network_id\":\"" + uuid + "\","
			  +     "\"ip_version\":4,"
			  +     "\"cidr\":\"192.168.1.0/8\""
			  +   "}"
			  + "}";
		
		assertEquals(
				createSubnetBody,
				OpenStackTemplates.getCreateSubnetTemplate(
						name, uuid, cidr));
	}

	@Test
	public void testGetCreateStorageTemplate() {
		String name = "storage";
		String desc = "test storage";
		int    size = 2;
		String createStorageBody =
			"{"
		  +   "\"volume\":{"
		  +     "\"description\":\"test storage\","
		  +     "\"size\":2,"
		  +     "\"name\":\"storage\""
		  +   "}"
		  + "}";
		
		assertEquals(
				createStorageBody,
				OpenStackTemplates.getCreateStorageTemplate(name, desc, size));
	}

	@Test
	public void testGetCreateComputeTemplate() {
		List<Disk> disks = new ArrayList<Disk>();
		Disk   disk;
		Link   link;
		String diskUUID  = UUID.randomUUID().toString();
		String imageRef  = UUID.randomUUID().toString();
		String flavorRef = UUID.randomUUID().toString();
		String name      = "compute";
		String networkId = UUID.randomUUID().toString();
		String createComputeBody =
				"{"
			  +   "\"server\":{"
			  +     "\"name\":\"compute\","
			  +     "\"imageRef\":\"" + imageRef + "\","
			  +     "\"flavorRef\":\"" + flavorRef + "\","
			  +     "\"networks\":["
			  +       "{"
			  +         "\"uuid\":\"" + networkId + "\""
			  +       "}"
			  +     "],"
			  +     "\"block_device_mapping_v2\":["
			  +       "{"
			  +         "\"device_name\":\"/dev/sda1\","
			  +         "\"source_type\":\"image\","
			  +         "\"destination_type\":\"local\","
			  +         "\"delete_on_termination\":\"True\","
			  +         "\"guest_format\":\"ephemeral\","
			  +         "\"boot_index\":\"-1\","
			  +         "\"uuid\":\"" + imageRef + "\""
			  +       "},"
			  +       "{"
			  +         "\"device_name\":\"/dev/sda2\","
			  +         "\"source_type\":\"volume\","
			  +         "\"destination_type\":\"volume\","
			  +         "\"delete_on_termination\":\"True\","
			  +         "\"guest_format\":\"ephemeral\","
			  +         "\"boot_index\":\"0\","
			  +         "\"uuid\":\"" + diskUUID + "\""
			  +       "}"
			  +     "]"
			  +   "}"
			  + "}";
		
		disk = new Disk();
		link = new Link();
		link.setHref(imageRef);
		disk.setStorage(link);
		disk.setTarget("/dev/sda1");
		disk.setType(StorageType.OS);
		disks.add(disk);
		
		disk = new Disk();
		link = new Link();
		link.setHref(diskUUID);
		disk.setStorage(link);
		disk.setTarget("/dev/sda2");
		disk.setType(StorageType.DATABLOCK);
		disks.add(disk);
		
		assertEquals(
				createComputeBody,
				OpenStackTemplates.getCreateComputeTemplate(
						imageRef, flavorRef, name, networkId, disks));
	}
}
