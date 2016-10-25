package eu.betaas.taas.securitymanager.authentication.utils;

import java.sql.Timestamp;

import org.apache.log4j.Logger;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import eu.betaas.rabbitmq.publisher.interfaces.Publisher;
import eu.betaas.rabbitmq.publisher.interfaces.utils.Message;
import eu.betaas.rabbitmq.publisher.interfaces.utils.MessageBuilder;
import eu.betaas.rabbitmq.publisher.interfaces.utils.Message.Layer;

public class AuthBetaasBus {
	private static Logger log = Logger.getLogger("betaas.taas.securitymanager");
	private String key = "monitoring.taas.secm";
	private BundleContext context;
	
	public AuthBetaasBus(BundleContext context){
		this.context = context;
	}
	
	private void busMessage(String message){
		log.debug("Checking queue");
		log.debug("Sending to queue");
		ServiceReference serviceReference = context.getServiceReference(
				Publisher.class.getName());
		log.debug("Sending to queue");
		if (serviceReference==null)return;
		Publisher service = (Publisher) context.getService(serviceReference); 
		log.debug("Sending");
		service.publish(key,message);
		log.debug("Sent");
	}
	
	public void sendData(String description, String level, String originator) {
		java.util.Date date= new java.util.Date();
		Timestamp timestamp = new Timestamp(date.getTime());
		Message msg = new Message();
		msg.setDescritpion(description);
		msg.setLayer(Layer.TAAS);
		msg.setLevel(level);
		msg.setOrigin(originator);
		msg.setTimestamp(timestamp.getTime());
		MessageBuilder msgBuilder = new MessageBuilder();
		String json = msgBuilder.getJsonEquivalent(msg);
		busMessage(json);
	}
}
