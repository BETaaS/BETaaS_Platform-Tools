package eu.betaas.taas.contextmanager.linkeddata.messaging;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import eu.betaas.rabbitmq.publisher.interfaces.Publisher;

public class MessageManager {
	private static final String DEPENDABILITYKEY = "dependability.taas.contextmanager.linkeddata";
	private static final String MONITORINGKEY = "monitoring.taas.contextmanager.linkeddata";
	
	private Publisher publisher;
	
	public MessageManager(BundleContext context) {
		ServiceReference serviceReference = context.getServiceReference(Publisher.class.getName());
		if (serviceReference==null)
			return;

		publisher = (Publisher) context.getService(serviceReference);
	}
	
	public void monitoringPublish(String message) {
		if (publisher != null) {
			publisher.publish(message, MONITORINGKEY);
		}
	}
	
	public void dependabilityPublish(String message) {
		if (publisher != null) {
			publisher.publish(message, DEPENDABILITYKEY);
		}
	}
}
