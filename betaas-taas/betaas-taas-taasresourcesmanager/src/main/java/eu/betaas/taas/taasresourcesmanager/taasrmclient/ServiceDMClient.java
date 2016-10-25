package eu.betaas.taas.taasresourcesmanager.taasrmclient;

import org.apache.log4j.Logger;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

import eu.betaas.rabbitmq.publisher.interfaces.Publisher;

public class ServiceDMClient 
{
	
	private Logger logger= Logger.getLogger("betaas.service");
	
	public ServiceDMClient ()
	{
		
	}
	
	public void sendDetectedIssue (String issue)
	{
		String key = "dependability.service";	    
	    DependabilityMessage newMessage = new DependabilityMessage (issue);
	    
	    // Retrieve the BundleContext from the OSGi Framework
	    BundleContext context = FrameworkUtil.getBundle(ServiceDMClient.class).getBundleContext();
	    ServiceReference serviceReference = context.getServiceReference(Publisher.class.getName());
	      
	    if (serviceReference==null)return;
	    
	    logger.info("Sending error to queue");
	    Publisher service = (Publisher) context.getService(serviceReference); 
	    logger.info("Sending..");
	    service.publish(key,newMessage.getData());
	    logger.info("Sent!");

	}
}
