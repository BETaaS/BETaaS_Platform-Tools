import java.util.Calendar;

import org.junit.BeforeClass;
import org.junit.Test;

import eu.betaas.rabbitmq.publisher.interfaces.utils.Message;
import eu.betaas.rabbitmq.publisher.interfaces.utils.Message.Layer;
import eu.betaas.rabbitmq.publisher.interfaces.utils.MessageBuilder;


public class TestJsonMessage {
	static MessageBuilder mb;
	static Message testmsg;
	
	@BeforeClass
	public static void setup() {
		mb = new MessageBuilder();
		testmsg = new Message();
		
		testmsg.setDescritpion("My custom description");
		testmsg.setLayer(Layer.TAAS);
		testmsg.setLevel("TEST");
		testmsg.setOrigin("TaaS Cmp1");
		testmsg.setIdentity_number("alfa");
		testmsg.setTimestamp(Calendar.getInstance().get(Calendar.MILLISECOND));
	}
	
	@Test
	public void testMessage() {
		String test = mb.getJsonEquivalent(testmsg);
		System.out.println(test);
		mb.returnMessageObject(test);
	}
	
	
}
