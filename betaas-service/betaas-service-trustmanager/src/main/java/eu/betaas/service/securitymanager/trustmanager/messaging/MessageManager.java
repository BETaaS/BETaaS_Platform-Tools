package eu.betaas.service.securitymanager.trustmanager.messaging;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import eu.betaas.rabbitmq.publisher.interfaces.Publisher;

public class MessageManager {
	private static final String DEPENDABILITYKEY = "dependability.taas.vmmanager";
	private static final String MONITORINGKEY = "monitoring.taas.vmmanager";
	
	private static MessageManager instance = null;
	
	private Publisher publisher;
	private BundleContext context;
	
	private MessageManager() {
		ServiceReference serviceReference =
				context.getServiceReference(Publisher.class.getName());
		if (serviceReference==null)
			return;

		publisher = (Publisher) context.getService(serviceReference);
	}
	
	public void setupService() {
		instance = new MessageManager();
	}
	
	public static MessageManager instance() {
		return instance;
	}
	
	public synchronized void monitoringPublish(String message) {
		if (publisher != null) {
			publisher.publish(message, MONITORINGKEY);
		}
	}
	
	public synchronized void dependabilityPublish(String message) {
		if (publisher != null) {
			publisher.publish(message, DEPENDABILITYKEY);
		}
	}
	
	public void setContext(BundleContext context) {
		this.context = context;
	}
}
