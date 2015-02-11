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
import eu.betaas.taas.taasvmmanager.occi.datamodel.InstanceTypeCollection;
import eu.betaas.taas.taasvmmanager.occi.datamodel.Link;
import eu.betaas.taas.taasvmmanager.occi.datamodel.ObjectFactory;

/**
* 
* @author Francisco Javier Nieto De-Santos (francisco.nieto@atos.net)
* @author Sergio Garcia Villalonga (sergio.garciavillalonga@atos.net)
*/
public class InstanceTypeCollectionTest {

	private static final String collectionXML =
			"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
			"<instance_type_collection>" +
				"<instance_type href=\"http://path.to/instance_type0\" name=\"instance_type0\"/>" +
				"<instance_type href=\"http://path.to/instance_type1\" name=\"instance_type1\"/>" +
				"<instance_type href=\"http://path.to/instance_type2\" name=\"instance_type2\"/>" +
				"<instance_type href=\"http://path.to/instance_type3\" name=\"instance_type3\"/>" +
				"<instance_type href=\"http://path.to/instance_type4\" name=\"instance_type4\"/>" +
				"<instance_type href=\"http://path.to/instance_type5\" name=\"instance_type5\"/>" +
				"<instance_type href=\"http://path.to/instance_type6\" name=\"instance_type6\"/>" +
				"<instance_type href=\"http://path.to/instance_type7\" name=\"instance_type7\"/>" +
				"<instance_type href=\"http://path.to/instance_type8\" name=\"instance_type8\"/>" +
				"<instance_type href=\"http://path.to/instance_type9\" name=\"instance_type9\"/>" +
			"</instance_type_collection>";
	
	@Test
	public void toXML() {
		ObjectFactory factory = new ObjectFactory();
		ByteArrayOutputStream marshalled = new ByteArrayOutputStream();
		
		InstanceTypeCollection collection = factory.createInstanceTypeCollection();
		
		Link link;
		for (int i = 0; i < 10; i++) {
			link = factory.createLink();
			
			link.setName("instance_type" + i);
			link.setHref("http://path.to/instance_type" + i);
			
			collection.getInstanceType().add(link);
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
		InstanceTypeCollection collection;
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(Compute.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			collection = (InstanceTypeCollection)jaxbUnmarshaller.unmarshal(
					new StreamSource( new StringReader( collectionXML )));
			
			System.out.println(collection.toString());
			
			int counter = 0;
			for (Link link : collection.getInstanceType()) {
				assertEquals("instance_type" + counter, link.getName());
				assertEquals("http://path.to/instance_type" + counter++, link.getHref());
			}
			assertEquals(10, counter);
		} catch (JAXBException e) {
			e.printStackTrace();
			fail();
	    }
	}

}
