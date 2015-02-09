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
import eu.betaas.taas.taasvmmanager.occi.datamodel.ObjectFactory;
import eu.betaas.taas.taasvmmanager.occi.datamodel.User;
import eu.betaas.taas.taasvmmanager.occi.datamodel.User.Quota;
import eu.betaas.taas.taasvmmanager.occi.datamodel.User.Usage;

/**
* 
* @author Francisco Javier Nieto De-Santos (francisco.nieto@atos.net)
* @author Sergio Garcia Villalonga (sergio.garciavillalonga@atos.net)
*/
public class UserTest {

	private static final String storageXML =
			"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
			"<user href=\"http://www.path.to/user\">" +
				"<id>34</id>" +
				"<name>Name</name>" +
				"<group>group</group>" +
				"<quota>" +
					"<cpu>8</cpu>" +
					"<memory>4096</memory>" +
					"<num_vms>10</num_vms>" +
					"<storage>2</storage>" +
				"</quota>" +
				"<usage>" +
					"<cpu>2</cpu>" +
					"<memory>512</memory>" +
					"<num_vms>2</num_vms>" +
					"<storage>0</storage>" +
				"</usage>" +
			"</user>";

	@Test
	public void toXML() {
		ObjectFactory factory = new ObjectFactory();
		ByteArrayOutputStream marshalled = new ByteArrayOutputStream();
		
		Quota quota = factory.createUserQuota();
		quota.setCpu(8);
		quota.setMemory(4096);
		quota.setNumVms(10);
		quota.setStorage(2);
		
		Usage usage = factory.createUserUsage();
		usage.setCpu(2);
		usage.setMemory(512);
		usage.setNumVms(2);
		usage.setStorage(0);
		
		User user = factory.createUser();
		user.setHref("http://www.path.to/user");
		user.setId(new BigInteger("34"));
		user.setName("Name");
		user.setGroup("group");
		user.setQuota(quota);
		user.setUsage(usage);
		
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(Compute.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			jaxbMarshaller.marshal(user, marshalled);
			
			System.out.println(marshalled.toString());
			assertEquals(storageXML, marshalled.toString());
		} catch (JAXBException e) {
			e.printStackTrace();
			fail();
	    }
	}
	
	@Test
	public void fromXML() {
		User user;
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(Compute.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			user = (User)jaxbUnmarshaller.unmarshal(
					new StreamSource( new StringReader( storageXML )));
			
			System.out.println(user.toString());
			assertEquals("http://www.path.to/user", user.getHref());
			assertEquals("34", user.getId().toString());
			assertEquals("Name", user.getName());
			assertEquals(8, user.getQuota().getCpu());
			assertEquals(4096, user.getQuota().getMemory());
			assertEquals(10, user.getQuota().getNumVms());
			assertEquals(2, user.getQuota().getStorage());
			assertEquals(2, user.getUsage().getCpu());
			assertEquals(512, user.getUsage().getMemory());
			assertEquals(2, user.getUsage().getNumVms());
			assertEquals(0, user.getUsage().getStorage());
		} catch (JAXBException e) {
			e.printStackTrace();
			fail();
	    }
	}

}
