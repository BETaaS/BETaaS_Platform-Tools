// BETaaS - Building the Environment for the Things as a Service
//
// Component: TaaSVMManager
// Responsible: Atos

package eu.betaas.taas.taasvmmanager.opennebula.datamodel;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.junit.Test;

import eu.betaas.taas.taasvmmanager.occi.datamodel.Compute;
import eu.betaas.taas.taasvmmanager.occi.datamodel.Link;
import eu.betaas.taas.taasvmmanager.occi.datamodel.ObjectFactory;
import eu.betaas.taas.taasvmmanager.occi.datamodel.UserCollection;

/**
* 
* @author Francisco Javier Nieto De-Santos (francisco.nieto@atos.net)
* @author Sergio Garcia Villalonga (sergio.garciavillalonga@atos.net)
*/
public class UserCollectionTest {

	private static final String collectionXML =
			"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
			"<user_collection>" +
				"<user href=\"http://path.to/user0\" name=\"user0\"/>" +
				"<user href=\"http://path.to/user1\" name=\"user1\"/>" +
				"<user href=\"http://path.to/user2\" name=\"user2\"/>" +
				"<user href=\"http://path.to/user3\" name=\"user3\"/>" +
				"<user href=\"http://path.to/user4\" name=\"user4\"/>" +
				"<user href=\"http://path.to/user5\" name=\"user5\"/>" +
				"<user href=\"http://path.to/user6\" name=\"user6\"/>" +
				"<user href=\"http://path.to/user7\" name=\"user7\"/>" +
				"<user href=\"http://path.to/user8\" name=\"user8\"/>" +
				"<user href=\"http://path.to/user9\" name=\"user9\"/>" +
			"</user_collection>";
	
	@Test
	public void toXML() {
		ObjectFactory factory = new ObjectFactory();
		ByteArrayOutputStream marshalled = new ByteArrayOutputStream();
		
		UserCollection collection = factory.createUserCollection();
		
		Link link;
		for (int i = 0; i < 10; i++) {
			link = new Link();
			
			link.setName("user" + i);
			link.setHref("http://path.to/user" + i);
			
			collection.getUser().add(link);
		}
		
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(Compute.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			jaxbMarshaller.marshal(collection, marshalled);
			
			System.out.println(marshalled.toString());
			assertEquals(collectionXML, marshalled.toString());
		} catch (JAXBException e) {
			e.printStackTrace();
			fail();
	    }
	}
	
	@Test
	public void fromXML() {
		UserCollection collection;
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(Compute.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			collection = (UserCollection)jaxbUnmarshaller.unmarshal(
					new StreamSource( new StringReader( collectionXML )));
			
			System.out.println(collection.toString());
			
			int counter = 0;
			for (Link link : collection.getUser()) {
				assertEquals("user" + counter, link.getName());
				assertEquals("http://path.to/user" + counter++, link.getHref());
			}
			assertEquals(10, counter);
		} catch (JAXBException e) {
			e.printStackTrace();
			fail();
	    }
	}

}
