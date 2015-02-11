package eu.betaas.taas.taasvmmanager.cloudsclients;

import java.net.UnknownServiceException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.apache.log4j.Logger;

public class OCCIClient        
{
    private static Logger log = Logger.getLogger( OCCIClient.class );

    private static int maxvms = 10;
    private String username;
    private String password;
    private String url = ""; 
    
    public void setAuth(String auth_username, String password) {
        this.username = auth_username;
        this.password = password;
    }
    
    public void setUrl(String url) {
        this.url = url;
    }
    
    public void deployService(String service_id, VMRequest request) throws OCCIClientException {
        
    	log.debug("Using Remote Cloud URL: " + url);
        
    	OCCI_RestClient rc = new OCCI_RestClient(username, password, url);
        log.debug( "OCCI REST Client is instantiated for deploying" );
        
        if (isDeployed(service_id)) {
            //throw new ServiceInstantiationException("This service is already deployed! "
            //        + "Terminate it before deploying it again.", new java.lang.Throwable());
        	log.info("Service " + service_id + " is already deployed.");
        }
        // Get the number of VMs to deploy
        int total_vms = request.getInstances();
        int vmId = 0;       
        log.debug("Total VMs detected for " + service_id + "in external IP are "+ total_vms);
        
        // Get those VMs which are already running at remote cloud
        try
        {
        	List<Service> myList = getAllVMs();
        	Iterator<Service> myServIter = myList.iterator();
        	while (myServIter.hasNext())
        	{
        		Service currService = myServIter.next();
        		String listService = currService.getId();
        		if (service_id.equals(listService))
        		{
        			List<VMProperties> myVMs = currService.getVms();
        			Iterator<VMProperties> myVMsIter = myVMs.iterator();
                	while (myVMsIter.hasNext())
                	{
                		if (myVMsIter.next().getId().contains(service_id))
                		{
                			// We increase in one VM when the id contains the component name
                			total_vms++;
                		}
                	}
        		}
        	}
        	log.debug("Total VMs detected for the service " + service_id + " are "+ total_vms);
        }
        catch (Exception ex)
        {
        	log.error("It was not possible to retrieve VMs info for the service " + service_id);
    		log.error(ex.getMessage());
    		ex.printStackTrace();
        }
        
        // If sum < maxvms invoke createVM method as many times as needed
        if ( total_vms > OCCIClient.maxvms )
        {
        	// DO NOTHING NOW, FOR DEMO PURPOSES
            //throw new ServiceInstantiationException("Number of VMs to deploy exceeds the maximum", new java.lang.Throwable() );
        }
        
        if (total_vms > 0) vmId = total_vms;

        
         // Only deploy one instance of the VM
         String res = "10.0.0.0";
         try
         {
        	 log.info("Creating vm for service [" + service_id + "]");
             res = rc.createVM(service_id, request, vmId);
             log.info("CREATEVM-response: " + res);                
         } 
         catch(Exception e) 
         {
        	 log.error("It was not possible to deploy the VM!");
        	 log.error(e.getMessage());
        	 e.printStackTrace();        		
        	 return;       		
         }
            
         // Basic instance data
         String instanceId = "BETaaS" + service_id + vmId;
         String instanceIP = "10.0.0.1";
         boolean vmReady = false;
         int tryNumber = 0;
                        
         // Wait for a minute until the VM data is created at Arsys
         try
         {
        	 log.info("Wait for the VM creation at remote Cloud...");
        	 synchronized (this) {        				
        		 this.wait(180 * 1000);
        	 }
        	 while (!vmReady)
        	 {
        		 if (tryNumber>10)
        		 {
        			 log.error("CREATEVM - No VM info was generated after 7 minutes!!! --> Error at remote Cloud");
        			 throw new OCCIClientException("ERROR - No VM info was generated after 5 minutes!!!", null);            			
        		 }
        		 synchronized (this) {        				
        			 this.wait(75 * 1000);
        		 }	
        			
        		 // Retrieve info about the new instance 
        		 try
        		 {
        			 VMProperties myVM = getVM (service_id, instanceId);
        			 instanceIP = myVM.getHostname();
        			 log.info("CREATEVM-received IP address: " + instanceIP);
        			 vmReady = true;
        		 }
        		 catch (Exception exx)
        		 {
        			 log.error("The new VM data is not still ready. Waiting more...");
        			 tryNumber++;
        		 }
                   
        	 }    			
         }
         catch (Exception ex)
         {
        	 ex.printStackTrace();
         }               
    }
   
    public List<VMProperties> queryServiceProperties(String serviceId) throws UnknownServiceException {

        try {        	
            OCCI_RestClient rc = new OCCI_RestClient(username, password, url);
            log.debug("OCCI REST Client is instantiated for querying");

            List<VMProperties> res = rc.getServiceVMs(serviceId);
            return res;
        }
        catch (Exception e) {
            throw new UnknownServiceException("Service not found");
        }
    }
   
    public void terminate(String serviceId) throws UnknownServiceException {
    	
        OCCI_RestClient rc = new OCCI_RestClient(username, password, url);
        log.debug("OCCI REST Client is instantiated for terminating");

        rc.terminateService(serviceId);

        // TODO: Check if result is ok
        
        log.info("Servce [" + serviceId + "] terminated successfully.");
    }
    
    public boolean isDeployed(String serviceId) {
        List<VMProperties> vms = null;        
        try {
            vms = queryServiceProperties(serviceId);
            log.debug("VMs found: " + vms.size());
        } catch (UnknownServiceException e) {
        	e.printStackTrace();
            return false;
        }
        for (VMProperties vm : vms) {
            log.debug(vm.getId() + " - " + vm.getStatus());
            if (!(vm.getStatus().equals("terminated") ||
                    vm.getStatus().equals("terminating"))) {
                return true;
            }
        }
        return false;
    }
    
    public void deleteVM(String serviceId, int index) {
    	
    	OCCI_RestClient rc = new OCCI_RestClient(username, password, url);
    	log.debug("OCCI REST Client is instantiated for deleting a VM");
        rc.deleteVM(serviceId, index);
    }
    
    public void updateVM(String serviceId, String component_id, VMRequest request, int index) {
    	log.debug("OCCI REST Client is instantiated for updating a VM");
        OCCI_RestClient rc = new OCCI_RestClient(username, password, url);
        try {
            rc.updateVM(serviceId, request, index);
        } catch (OCCIClientException ex) {
            java.util.logging.Logger.getLogger(OCCIClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void executeAction(String serviceId, String action, int index, Map<String, String> attrs) {
    	
    	OCCI_RestClient rc = new OCCI_RestClient(username, password, url);
    	log.debug("OCCI REST Client is instantiated for executing an action");
        try {
            rc.executeAction(action, serviceId, index, attrs);
        } catch (OCCIClientException ex) {
            java.util.logging.Logger.getLogger(OCCIClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public VMProperties getVM(String serviceId, String instance) {
    	OCCI_RestClient rc = new OCCI_RestClient(username, password, url);
    	log.debug("OCCI REST Client is instantiated for getting a VM");
    	log.info("Looking for instance " + instance + " of service " + serviceId);
        VMProperties res = null;
        try {
            res = rc.getVM(serviceId, instance);
        } catch (OCCIClientException ex) {
            java.util.logging.Logger.getLogger(OCCIClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        return res;
    }
    
    public List<Service> getAllVMs() throws UnknownServiceException {
        try {        	
        	OCCI_RestClient rc = new OCCI_RestClient(username, password, url);
        	log.debug("OCCI REST Client is instantiated for getting all VMs");
            List<Service> res = rc.getAllVMs();
            return res;
        }
        catch (Exception e) {
            throw new UnknownServiceException("Service not found");
        }
    }
    
    public static void main(String[] args) 
    {
    	
    }
    
}
