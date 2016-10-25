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

import eu.betaas.taas.taasvmmanager.api.datamodel.InstanceType;
import eu.betaas.taas.taasvmmanager.occi.datamodel.Compute;
import eu.betaas.taas.taasvmmanager.occi.datamodel.ObjectFactory;

/**
* 
* @author Francisco Javier Nieto De-Santos (francisco.nieto@atos.net)
* @author Sergio Garcia Villalonga (sergio.garciavillalonga@atos.net)
*/
public class InstanceTypeTest {

	private static final String storageXML =
			"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
			"<instance_type href=\"http://www.path.to/instance_type\">" +
				"<id>34</id>" +
				"<name>Name</name>" +
				"<cpu>1</cpu>" +
				"<memory>1024</memory>" +
			"</instance_type>";

	@Test
	public void toXML() {
		ObjectFactory factory = new ObjectFactory();
		ByteArrayOutputStream marshalled = new ByteArrayOutputStream();
		
		InstanceType instance_type = factory.createInstanceType();
		instance_type.setHref("http://www.path.to/instance_type");
		instance_type.setId(new BigInteger("34"));
		instance_type.setName("Name");
		instance_type.setCpu(1);
		instance_type.setMemory(1024);
		
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(Compute.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			jaxbMarshaller.marshal(instance_type, marshalled);
			
			System.out.println(marshalled.toString());
			assertEquals(storageXML, marshalled.toString());
		} catch (JAXBException e) {
			e.printStackTrace();
			fail();
	    }
	}
	
	@Test
	public void fromXML() {
		InstanceType instanceType;
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(Compute.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			instanceType = (InstanceType)jaxbUnmarshaller.unmarshal(
					new StreamSource( new StringReader( storageXML )));
			
			System.out.println(instanceType.toString());
			assertEquals("http://www.path.to/instance_type", instanceType.getHref());
			assertEquals("34", instanceType.getId().toString());
			assertEquals("Name", instanceType.getName());
			assertEquals(1, instanceType.getCpu());
			assertEquals(1024, instanceType.getMemory());
		} catch (JAXBException e) {
			e.printStackTrace();
			fail();
	    }
	}

}
