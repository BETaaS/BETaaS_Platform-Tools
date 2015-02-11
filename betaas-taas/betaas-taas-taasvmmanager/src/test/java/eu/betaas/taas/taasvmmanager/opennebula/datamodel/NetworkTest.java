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
import eu.betaas.taas.taasvmmanager.occi.datamodel.Link;
import eu.betaas.taas.taasvmmanager.occi.datamodel.Network;
import eu.betaas.taas.taasvmmanager.occi.datamodel.ObjectFactory;

/**
* 
* @author Francisco Javier Nieto De-Santos (francisco.nieto@atos.net)
* @author Sergio Garcia Villalonga (sergio.garciavillalonga@atos.net)
*/
public class NetworkTest {

	private static final String networkXML =
			"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
			"<network href=\"http://www.path.to/network\">" +
				"<id>34</id>" +
				"<name>Name</name>" +
				"<user href=\"http://www.path.to/user\" name=\"user_name\"/>" +
				"<group>group</group>" +
				"<description>Description of the network</description>" +
				"<address>Address</address>" +
				"<size>12</size>" +
			"</network>";

	@Test
	public void toXML() {
		ObjectFactory factory = new ObjectFactory();
		ByteArrayOutputStream marshalled = new ByteArrayOutputStream();
		
		Link user = factory.createLink();
		user.setHref("http://www.path.to/user");
		user.setName("user_name");
		
		Network network = factory.createNetwork();
		network.setHref("http://www.path.to/network");
		network.setId(new BigInteger("34"));
		network.setName("Name");
		network.setUser(user);
		network.setGroup("group");
		network.setDescription("Description of the network");
		network.setAddress("Address");
		network.setSize("12");
		
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(Compute.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			jaxbMarshaller.marshal(network, marshalled);
			
			System.out.println(marshalled.toString());
			assertEquals(networkXML, marshalled.toString());
		} catch (JAXBException e) {
			e.printStackTrace();
			fail();
	    }
	}
	
	@Test
	public void fromXML() {
		Network network;
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(Compute.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			network = (Network)jaxbUnmarshaller.unmarshal(
					new StreamSource( new StringReader( networkXML )));
			
			System.out.println(network.toString());
			assertEquals("http://www.path.to/network", network.getHref());
			assertEquals("34", network.getId().toString());
			assertEquals("Name", network.getName());
			assertEquals("http://www.path.to/user", network.getUser().getHref());
			assertEquals("user_name", network.getUser().getName());
			assertEquals("group", network.getGroup());
			assertEquals("Description of the network", network.getDescription());
			assertEquals("Address", network.getAddress());
			assertEquals("12", network.getSize());
		} catch (JAXBException e) {
			e.printStackTrace();
			fail();
	    }
	}
}
