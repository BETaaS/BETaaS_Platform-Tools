/*
Copyright 2014-2015 Intecs Spa

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
*/

package eu.betaas.apps.home.intrusiondetection;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.namespace.QName;

import eu.betaas.apps.lib.soap.ServiceManagerExternalIF;
import eu.betaas.apps.lib.soap.ServiceManagerExternalIFPortType;

/**
 * Used to allow the clients to request a task to be run
 * @author Intecs
 */
@Path("/task")
public class TaskRunner {
	
	private static final QName SERVICE_NAME = new QName("http://api.servicemanager.service.betaas.eu/", "ServiceManagerExternalIF");

	@Context
	ServletContext mContext;
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response runTask() {
		Logger log = (Logger)mContext.getAttribute(IntrusionDetectionContext.CONTEXT_ATTR_LOG);
		Configuration config = (Configuration)mContext.getAttribute(IntrusionDetectionContext.CONTEXT_ATTR_CONFIG);
		
		// Send the getTaskData SOAP request to the BETaaS Platform		
		URL wsdlURL = ServiceManagerExternalIF.WSDL_LOCATION;
		
		String WSDL = config.getProperty(Configuration.PROP_KEY_BETAAS_WSDL);
		
		if ((WSDL != null) && (WSDL.length() > 0)) {
			
			log.loginfo("Sending the run task request to BETaaS using WSDL: " + WSDL);
			
			File wsdlFile = new File(WSDL);
	        try {
	            if (wsdlFile.exists()) {
	                wsdlURL = wsdlFile.toURI().toURL();
	            } else {
                    wsdlURL = new URL(WSDL);
                }
	        } catch (MalformedURLException e) {
	        	log.logerr("Cannot build the URL for the configured WSDL file");
	        } 
		}

		ServiceManagerExternalIF ss = new ServiceManagerExternalIF(wsdlURL, SERVICE_NAME);
        ServiceManagerExternalIFPortType port = ss.getServiceManagerExternalIFPort();  

        String taskResult = "";
        try {
            String appId = config.getProperty(Configuration.PROP_KEY_APP_ID); //"testAppID";
            String taskId = "taskid";
            log.loginfo("Requesting task for appID="+appId);
            taskResult = port.getTaskData(appId, taskId);	        
	          log.loginfo("The task request returned: " +  taskResult);
	        
	        //TODO: format the response by parsing the json object
//	        try {
//		        JsonElement jelement = new JsonParser().parse(taskResult);
//			    JsonObject  jobject = jelement.getAsJsonObject();
//			    System.out.println("jobject=" + jobject.toString());
//          } catch (Exception e) {
//	            taskResult = "";
//	        }
	        
        } catch (Exception e) {
        	log.logerr("Error requesting the task to run: " + e.getMessage());
        }

		CacheControl cc = new CacheControl(); 
	    cc.setNoCache(true);
	    cc.setMaxAge(0);
	    
		return Response.ok(taskResult).cacheControl(cc).build();
	}
}
