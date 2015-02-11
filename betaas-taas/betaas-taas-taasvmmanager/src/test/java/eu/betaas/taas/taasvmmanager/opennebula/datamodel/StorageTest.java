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
import eu.betaas.taas.taasvmmanager.occi.datamodel.ObjectFactory;
import eu.betaas.taas.taasvmmanager.occi.datamodel.Storage;
import eu.betaas.taas.taasvmmanager.occi.datamodel.StorageType;

/**
* 
* @author Francisco Javier Nieto De-Santos (francisco.nieto@atos.net)
* @author Sergio Garcia Villalonga (sergio.garciavillalonga@atos.net)
*/
public class StorageTest {

	private static final String storageXML =
			"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
			"<storage href=\"http://www.path.to/storage\">" +
				"<id>34</id>" +
				"<name>Name</name>" +
				"<user href=\"http://www.path.to/user\" name=\"user_name\"/>" +
				"<group>group</group>" +
				"<type>OS</type>" +
				"<description>Description of the storage</description>" +
				"<size>2048</size>" +
				"<fstype>ext3</fstype>" +
			"</storage>";

	@Test
	public void toXML() {
		ObjectFactory factory = new ObjectFactory();
		ByteArrayOutputStream marshalled = new ByteArrayOutputStream();
		
		Link user = factory.createLink();
		user.setHref("http://www.path.to/user");
		user.setName("user_name");
		
		Storage storage = factory.createStorage();
		storage.setHref("http://www.path.to/storage");
		storage.setId(new BigInteger("34"));
		storage.setName("Name");
		storage.setUser(user);
		storage.setGroup("group");
		storage.setType(StorageType.OS);
		storage.setDescription("Description of the storage");
		storage.setSize("2048");
		storage.setFstype("ext3");
		
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(Compute.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			jaxbMarshaller.marshal(storage, marshalled);
			
			System.out.println(marshalled.toString());
			assertEquals(storageXML, marshalled.toString());
		} catch (JAXBException e) {
			e.printStackTrace();
			fail();
	    }
	}
	
	@Test
	public void fromXML() {
		Storage storage;
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(Compute.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			storage = (Storage)jaxbUnmarshaller.unmarshal(
					new StreamSource( new StringReader( storageXML )));
			
			System.out.println(storage.toString());
			assertEquals("http://www.path.to/storage", storage.getHref());
			assertEquals("34", storage.getId().toString());
			assertEquals("Name", storage.getName());
			assertEquals("http://www.path.to/user", storage.getUser().getHref());
			assertEquals("user_name", storage.getUser().getName());
			assertEquals("group", storage.getGroup());
			assertEquals(StorageType.OS, storage.getType());
			assertEquals("Description of the storage", storage.getDescription());
			assertEquals("2048", storage.getSize());
			assertEquals("ext3", storage.getFstype());
		} catch (JAXBException e) {
			e.printStackTrace();
			fail();
	    }
	}
}
