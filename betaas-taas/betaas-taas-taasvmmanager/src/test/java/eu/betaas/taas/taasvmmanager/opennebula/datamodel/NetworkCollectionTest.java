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
import eu.betaas.taas.taasvmmanager.occi.datamodel.NetworkCollection;
import eu.betaas.taas.taasvmmanager.occi.datamodel.ObjectFactory;

/**
* 
* @author Francisco Javier Nieto De-Santos (francisco.nieto@atos.net)
* @author Sergio Garcia Villalonga (sergio.garciavillalonga@atos.net)
*/
public class NetworkCollectionTest {

	private static final String collectionXML =
			"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
			"<network_collection>" +
				"<network href=\"http://path.to/network0\" name=\"network0\"/>" +
				"<network href=\"http://path.to/network1\" name=\"network1\"/>" +
				"<network href=\"http://path.to/network2\" name=\"network2\"/>" +
				"<network href=\"http://path.to/network3\" name=\"network3\"/>" +
				"<network href=\"http://path.to/network4\" name=\"network4\"/>" +
				"<network href=\"http://path.to/network5\" name=\"network5\"/>" +
				"<network href=\"http://path.to/network6\" name=\"network6\"/>" +
				"<network href=\"http://path.to/network7\" name=\"network7\"/>" +
				"<network href=\"http://path.to/network8\" name=\"network8\"/>" +
				"<network href=\"http://path.to/network9\" name=\"network9\"/>" +
			"</network_collection>";
	
	@Test
	public void toXML() {
		ObjectFactory factory = new ObjectFactory();
		ByteArrayOutputStream marshalled = new ByteArrayOutputStream();
		
		NetworkCollection collection = factory.createNetworkCollection();
		
		Link link;
		for (int i = 0; i < 10; i++) {
			link = factory.createLink();
			
			link.setName("network" + i);
			link.setHref("http://path.to/network" + i);
			
			collection.getNetwork().add(link);
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
		NetworkCollection collection;
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(Compute.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			collection = (NetworkCollection)jaxbUnmarshaller.unmarshal(
					new StreamSource( new StringReader( collectionXML )));
			
			System.out.println(collection.toString());
			
			int counter = 0;
			for (Link link : collection.getNetwork()) {
				assertEquals("network" + counter, link.getName());
				assertEquals("http://path.to/network" + counter++, link.getHref());
			}
			assertEquals(10, counter);
		} catch (JAXBException e) {
			e.printStackTrace();
			fail();
	    }
	}

}
