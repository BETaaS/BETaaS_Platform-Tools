package eu.betaas.taas.taasvmmanager.messaging;

import org.apache.log4j.Logger;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import eu.betaas.rabbitmq.publisher.interfaces.Publisher;

public class MessageManager {
	private static Logger logger = Logger.getLogger("betaas.taas");
	
	private static final String DEPENDABILITYKEY = "dependability.taas.vmmanager";
	private static final String MONITORINGKEY = "monitoring.taas.vmmanager";
	
	private static MessageManager instance = null;
	
	private static Publisher publisher;
	private static BundleContext context;
	
	public void setupService() {
		logger.info("[TaaSVMMMessageManager] Starting message manager...");
		ServiceReference serviceReference =
				context.getServiceReference(Publisher.class.getName());
		if (serviceReference==null) {
			logger.info("[TaaSVMMMessageManager] Message manager started (serviceReference is null)...");
			return;
		}

		publisher = (Publisher) context.getService(serviceReference);
		logger.info("[TaaSVMMMessageManager] Message manager started...");
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
