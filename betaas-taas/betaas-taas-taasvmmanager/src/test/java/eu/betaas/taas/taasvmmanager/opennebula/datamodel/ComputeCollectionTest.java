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
import eu.betaas.taas.taasvmmanager.occi.datamodel.ComputeCollection;
import eu.betaas.taas.taasvmmanager.occi.datamodel.Link;
import eu.betaas.taas.taasvmmanager.occi.datamodel.ObjectFactory;

/**
* 
* @author Francisco Javier Nieto De-Santos (francisco.nieto@atos.net)
* @author Sergio Garcia Villalonga (sergio.garciavillalonga@atos.net)
*/
public class ComputeCollectionTest {

	private static final String collectionXML =
			"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
			"<compute_collection>" +
				"<compute href=\"http://path.to/compute0\" name=\"compute0\"/>" +
				"<compute href=\"http://path.to/compute1\" name=\"compute1\"/>" +
				"<compute href=\"http://path.to/compute2\" name=\"compute2\"/>" +
				"<compute href=\"http://path.to/compute3\" name=\"compute3\"/>" +
				"<compute href=\"http://path.to/compute4\" name=\"compute4\"/>" +
				"<compute href=\"http://path.to/compute5\" name=\"compute5\"/>" +
				"<compute href=\"http://path.to/compute6\" name=\"compute6\"/>" +
				"<compute href=\"http://path.to/compute7\" name=\"compute7\"/>" +
				"<compute href=\"http://path.to/compute8\" name=\"compute8\"/>" +
				"<compute href=\"http://path.to/compute9\" name=\"compute9\"/>" +
			"</compute_collection>";
	
	@Test
	public void toXML() {
		ObjectFactory factory = new ObjectFactory();
		ByteArrayOutputStream marshalled = new ByteArrayOutputStream();
		
		ComputeCollection collection = factory.createComputeCollection();
		
		Link link;
		for (int i = 0; i < 10; i++) {
			link = factory.createLink();
			
			link.setName("compute" + i);
			link.setHref("http://path.to/compute" + i);
			
			collection.getCompute().add(link);
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
		ComputeCollection collection;
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(Compute.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			collection = (ComputeCollection)jaxbUnmarshaller.unmarshal(
					new StreamSource( new StringReader( collectionXML )));
			
			System.out.println(collection.toString());
			
			int counter = 0;
			for (Link link : collection.getCompute()) {
				assertEquals("compute" + counter, link.getName());
				assertEquals("http://path.to/compute" + counter++, link.getHref());
			}
			assertEquals(10, counter);
		} catch (JAXBException e) {
			e.printStackTrace();
			fail();
	    }
	}
}
