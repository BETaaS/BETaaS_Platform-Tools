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
import eu.betaas.taas.taasvmmanager.occi.datamodel.StorageCollection;

/**
* 
* @author Francisco Javier Nieto De-Santos (francisco.nieto@atos.net)
* @author Sergio Garcia Villalonga (sergio.garciavillalonga@atos.net)
*/
public class StorageCollectionTest {

	private static final String collectionXML =
			"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
			"<storage_collection>" +
				"<storage href=\"http://path.to/storage0\" name=\"storage0\"/>" +
				"<storage href=\"http://path.to/storage1\" name=\"storage1\"/>" +
				"<storage href=\"http://path.to/storage2\" name=\"storage2\"/>" +
				"<storage href=\"http://path.to/storage3\" name=\"storage3\"/>" +
				"<storage href=\"http://path.to/storage4\" name=\"storage4\"/>" +
				"<storage href=\"http://path.to/storage5\" name=\"storage5\"/>" +
				"<storage href=\"http://path.to/storage6\" name=\"storage6\"/>" +
				"<storage href=\"http://path.to/storage7\" name=\"storage7\"/>" +
				"<storage href=\"http://path.to/storage8\" name=\"storage8\"/>" +
				"<storage href=\"http://path.to/storage9\" name=\"storage9\"/>" +
			"</storage_collection>";
	
	@Test
	public void toXML() {
		ObjectFactory factory = new ObjectFactory();
		ByteArrayOutputStream marshalled = new ByteArrayOutputStream();
		
		StorageCollection collection = factory.createStorageCollection();
		
		Link link;
		for (int i = 0; i < 10; i++) {
			link = new Link();
			
			link.setName("storage" + i);
			link.setHref("http://path.to/storage" + i);
			
			collection.getStorage().add(link);
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
		StorageCollection collection;
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(Compute.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			collection = (StorageCollection)jaxbUnmarshaller.unmarshal(
					new StreamSource( new StringReader( collectionXML )));
			
			System.out.println(collection.toString());
			
			int counter = 0;
			for (Link link : collection.getStorage()) {
				assertEquals("storage" + counter, link.getName());
				assertEquals("http://path.to/storage" + counter++, link.getHref());
			}
			assertEquals(10, counter);
		} catch (JAXBException e) {
			e.printStackTrace();
			fail();
	    }
	}

}
