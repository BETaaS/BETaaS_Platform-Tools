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

package eu.betaas.service.instancemanager.web;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import eu.betaas.service.instancemanager.web.soap.InstanceManagerExternalIF;
import eu.betaas.service.instancemanager.web.soap.InstanceManagerExternalIFPortType;

/**
 * Used to allow the clients to request to join to an instance
 * @author Intecs
 */
@Path("/join")
public class Joiner {
	
	@Context
	ServletContext mContext;
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response join(@QueryParam("gwId") String gwId) {
		String response = "Cannot perform the join (see server log)";
		Logger log = (Logger)mContext.getAttribute(InstanceManagerContext.CONTEXT_ATTR_LOG);
		Configuration config = (Configuration)mContext.getAttribute(InstanceManagerContext.CONTEXT_ATTR_CONFIG);
		
		// Send the join SOAP request to the BETaaS Platform		
		URL wsdlURL = InstanceManagerExternalIF.WSDL_LOCATION;
		
		String WSDL = config.getProperty(Configuration.PROP_KEY_BETAAS_WSDL);
		
		if ((WSDL != null) && (WSDL.length() > 0)) {
			
			log.loginfo("Sending the join request to BETaaS using WSDL: " + WSDL);
			
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

		InstanceManagerExternalIF ss = new InstanceManagerExternalIF(wsdlURL, InstanceManagerContext.SERVICE_NAME);
		InstanceManagerExternalIFPortType port = ss.getInstanceManagerExternalIFPort();  

        boolean joinResult = false;
        try {
        	joinResult = port.requestJoin(gwId);	        
	        log.loginfo("The IM returned: " +  joinResult);
	        if (joinResult) response = "Join successfully started";
	        else response = "Error starting the join (see server log)";
        } catch (Exception e) {
        	log.logerr("Error requesting the instance info: " + e.getMessage());
        }

		CacheControl cc = new CacheControl(); 
	    cc.setNoCache(true);
	    cc.setMaxAge(0);
	    
		return Response.ok(response).cacheControl(cc).build();
	}
}
