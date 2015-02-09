// BETaaS - Building the Environment for the Things as a Service
//
// Component: TaaSVMManager
// Responsible: Atos

package eu.betaas.taas.taasvmmanager.opennebula.datamodel;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.math.BigInteger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.junit.Test;

import eu.betaas.taas.taasvmmanager.occi.datamodel.Compute;
import eu.betaas.taas.taasvmmanager.occi.datamodel.ComputeState;
import eu.betaas.taas.taasvmmanager.occi.datamodel.Link;
import eu.betaas.taas.taasvmmanager.occi.datamodel.ObjectFactory;
import eu.betaas.taas.taasvmmanager.occi.datamodel.StorageType;

/**
* 
* @author Francisco Javier Nieto De-Santos (francisco.nieto@atos.net)
* @author Sergio Garcia Villalonga (sergio.garciavillalonga@atos.net)
*/
public class ComputeTest {
		
	private static final String computeXML =
			"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
			"<compute href=\"http://www.path.to/compute\">" +
				"<id>34</id>" +
				"<name>Name</name>" +
				"<cpu>1</cpu>" +
				"<memory>1024</memory>" +
				"<user href=\"http://www.path.to/user\" name=\"user_name\"/>" +
				"<group>group</group>" +
				"<instance_type>Small</instance_type>" +
				"<state>PENDING</state>" +
				"<disk>" +
					"<storage href=\"http://path.to/storage0\" " +
							"name=\"storage0\"/>" +
					"<type>" + StorageType.OS + "</type>" +
					"<target>sda1</target>" +
				"</disk>" +
					"<disk>" +
					"<storage href=\"http://path.to/storage1\" " +
						"name=\"storage1\"/>" +
					"<type>" + StorageType.CDROM + "</type>" +
					"<target>sda2</target>" +
				"</disk>" +
				"<disk>" +
					"<storage href=\"http://path.to/storage2\" " +
						"name=\"storage2\"/>" +
					"<save_as href=\"http://path.to/save_as\"/>" +
					"<type>" + StorageType.DATABLOCK + "</type>" +
					"<target>sda3</target>" +
				"</disk>" +
				"<nic>" +
					"<network href=\"http://path.to/network3\" " +
						"name=\"network3\"/>" +
					"<ip>192.168.1.3</ip>" +
					"<mac>10:1f:74:ff:03:13</mac>" +
				"</nic>" +
				"<nic>" +
					"<network href=\"http://path.to/network4\" " +
						"name=\"network4\"/>" +
					"<ip>192.168.1.4</ip>" +
					"<mac>10:1f:74:ff:03:14</mac>" +
				"</nic>" +	
				"<context/>" +
			"</compute>";
	
	private static final String SAVEASXML =
			"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
			"<compute href=\"http://www.path.to/compute\">" +
				"<disk>" +
					"<storage href=\"http://path.to/storage0\" " +
							"name=\"storage0\"/>" +
					"<save_as href=\"http://path.to/save_as0\"/>" +
					"<type>" + StorageType.OS + "</type>" +
					"<target>sda1</target>" +
				"</disk>" +
				"<disk>" +
					"<storage href=\"http://path.to/storage2\" " +
						"name=\"storage2\"/>" +
					"<save_as href=\"http://path.to/save_as2\"/>" +
					"<type>" + StorageType.DATABLOCK + "</type>" +
					"<target>sda3</target>" +
				"</disk>"+
			"</compute>";

	@Test
	public void toXML() {
		Link link;
		int index;
		ObjectFactory factory = new ObjectFactory();
		ByteArrayOutputStream marshalled = new ByteArrayOutputStream();
		
		Link user = factory.createLink();
		user.setHref("http://www.path.to/user");
		user.setName("user_name");
		
		Compute compute = factory.createCompute();
		compute.setHref("http://www.path.to/compute");
		compute.setId(new BigInteger("34"));
		compute.setName("Name");
		compute.setCpu(1);
		compute.setMemory(1024);
		compute.setUser(user);
		compute.setGroup("group");
		compute.setInstanceType("Small");
		compute.setState(ComputeState.PENDING);
		
		Compute.Disk disk;
		for (index = 0; index < 3 ; index++) {
			link = factory.createLink();
			
			link.setName("storage" + index);
			link.setHref("http://path.to/storage" + index);
			
			disk = factory.createComputeDisk();
			
			disk.setStorage(link);
			disk.setTarget("sda" + (index+1));
			disk.setType(StorageType.values()[index%3]);
			
			if (StorageType.values()[index%3] == StorageType.DATABLOCK) {
				link = factory.createLink();
				link.setHref("http://path.to/save_as");
				disk.setSaveAs(link);
			}
			
			compute.getDisk().add(disk);
		}
		
		Compute.Nic nic;
		for (; index < 5 ; index++) {
			link = factory.createLink();
			
			link.setName("network" + index);
			link.setHref("http://path.to/network" + index);
			
			nic = factory.createComputeNic();
			
			nic.setNetwork(link);
			nic.setIp("192.168.1." + index);
			nic.setMac("10:1f:74:ff:03:1" + index);
			
			compute.getNic().add(nic);
		}
		
		compute.setContext(factory.createComputeContext());
		
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(Compute.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			jaxbMarshaller.marshal(compute, marshalled);
			
			System.out.println(marshalled.toString());
			assertEquals(computeXML, marshalled.toString());
			
			/* Test the save as parsing */
			compute = new Compute();
			compute.setHref("http://www.path.to/compute");
			for (index = 0; index < 2 ; index++) {
				link = factory.createLink();
				
				link.setName("storage" + index*2);
				link.setHref("http://path.to/storage" + index*2);
				
				disk = factory.createComputeDisk();
				
				disk.setStorage(link);
				disk.setTarget("sda" + ((index*2)+1));
				disk.setType(StorageType.values()[index*2]);
				
				
				link = factory.createLink();
				link.setHref("http://path.to/save_as" + index*2);
				disk.setSaveAs(link);
				
				compute.getDisk().add(disk);
			}
			marshalled = new ByteArrayOutputStream();
			jaxbMarshaller.marshal(compute, marshalled);
			
			System.out.println(marshalled.toString());
			assertEquals(SAVEASXML, marshalled.toString());
		} catch (JAXBException e) {
			e.printStackTrace();
			fail();
	    }
	}
	
	@Test
	public void fromXML() {
		Compute compute;
		int index = 0;
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(Compute.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			compute = (Compute)jaxbUnmarshaller.unmarshal(
					new StreamSource( new StringReader( computeXML )));
			
			System.out.println(compute.toString());
			assertEquals("http://www.path.to/compute", compute.getHref());
			assertEquals("34", compute.getId().toString());
			assertEquals("Name", compute.getName());
			assertEquals(new Integer(1), compute.getCpu());
			assertEquals(new Integer(1024), compute.getMemory());
			assertEquals("http://www.path.to/user", compute.getUser().getHref());
			assertEquals("user_name", compute.getUser().getName());
			assertEquals("group", compute.getGroup());
			assertEquals("Small", compute.getInstanceType());
			assertEquals(ComputeState.PENDING, compute.getState());
			
			assertEquals(3, compute.getDisk().size());
			for (Compute.Disk disk : compute.getDisk()) {
				assertEquals("storage" + index, disk.getStorage().getName());
				assertEquals("http://path.to/storage" + index,
						disk.getStorage().getHref());
				assertEquals("sda" + (index+1), disk.getTarget());
				assertEquals(StorageType.values()[index++%3],
						disk.getType());
				
				if (disk.getType() == StorageType.DATABLOCK) {
					assertEquals("http://path.to/save_as",
							disk.getSaveAs().getHref());
				} else {
					assertNull(disk.getSaveAs());
				}
			}
			
			assertEquals(2, compute.getNic().size());
			for (Compute.Nic nic : compute.getNic()) {
				assertEquals("network" + index, nic.getNetwork().getName());
				assertEquals("http://path.to/network" + index,
						nic.getNetwork().getHref());
				assertEquals("192.168.1." + index, nic.getIp());
				assertEquals("10:1f:74:ff:03:1" + index++,
						nic.getMac());
			}
			
			/* Test the save as parsing */
			jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			compute = (Compute)jaxbUnmarshaller.unmarshal(
					new StreamSource( new StringReader( SAVEASXML )));
			assertEquals(2, compute.getDisk().size());
			assertEquals("http://path.to/save_as0",
					compute.getDisk().get(0).getSaveAs().getHref());
			assertEquals("http://path.to/save_as2",
					compute.getDisk().get(1).getSaveAs().getHref());
		} catch (JAXBException e) {
			e.printStackTrace();
			fail();
	    }
	}
}
