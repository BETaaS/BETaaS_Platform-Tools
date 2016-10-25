package eu.betaas.taas.taasresourcesmanager.messaging;

import java.sql.Timestamp;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import eu.betaas.rabbitmq.publisher.interfaces.Publisher;
import eu.betaas.rabbitmq.publisher.interfaces.utils.Message;
import eu.betaas.rabbitmq.publisher.interfaces.utils.Message.Layer;
import eu.betaas.rabbitmq.publisher.interfaces.utils.MessageBuilder;

public class MessageManager {
	private static final String DEPENDABILITYKEY = "dependability.taas";
	private static final String MONITORINGKEY = "monitoring.taas";
	
	private static MessageManager instance = null;
	
	private static Publisher publisher;
	private static BundleContext context;
	
	public void setupService() {
		ServiceReference serviceReference =
				context.getServiceReference(Publisher.class.getName());
		if (serviceReference==null)
			return;

		publisher = (Publisher) context.getService(serviceReference);
		
	/*	instance = new MessageManager();
		instance.init(publisher, context);*/
	}
	
	public synchronized static MessageManager instance() {
		if (instance == null) {
			instance = new MessageManager();
		}
		return instance;
	}
	
	public synchronized void monitoringPublish(String message) {
		if (publisher != null) 
		{
			// Generate the message
			java.util.Date date = new java.util.Date();
		    Timestamp timestamp = new Timestamp(date.getTime());
		    Message msg = new Message();
		    msg.setDescritpion(message);
		    msg.setLayer(Layer.TAAS);
		    msg.setLevel("INFO");
		    msg.setOrigin("TaaSRM");
		    msg.setTimestamp(timestamp.getTime());
		    MessageBuilder msgBuilder = new MessageBuilder();
		    String json = msgBuilder.getJsonEquivalent(msg);
		    		    
			publisher.publish(MONITORINGKEY, json);
		}
	}
	
	public synchronized void dependabilityPublish(String message) {
		if (publisher != null) 
		{
			// Generate the message
			java.util.Date date = new java.util.Date();
			Timestamp timestamp = new Timestamp(date.getTime());
			Message msg = new Message();
			msg.setDescritpion(message);
			msg.setLayer(Layer.TAAS);
			msg.setLevel("INFO");
			msg.setOrigin("TaaSRM");
			msg.setTimestamp(timestamp.getTime());
			MessageBuilder msgBuilder = new MessageBuilder();
			String json = msgBuilder.getJsonEquivalent(msg);
			publisher.publish(DEPENDABILITYKEY, json);
		}
	}

	public void setContext(BundleContext context) {
		this.context = context;
	}
}
