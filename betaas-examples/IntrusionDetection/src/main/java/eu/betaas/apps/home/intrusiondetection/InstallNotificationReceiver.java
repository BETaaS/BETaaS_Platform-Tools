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

import javax.servlet.ServletContext;
import javax.ws.rs.Path;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXBElement;

import eu.betaas.apps.lib.InstallNotification;
import eu.betaas.apps.lib.ServiceInstallation;
import eu.betaas.apps.lib.api.InstallNotificationReceiverIF;

@Path("/installNotification")
public class InstallNotificationReceiver implements InstallNotificationReceiverIF {
	@Context
    UriInfo mUriInfo;
	
	@Context
	ServletContext mContext;

	public InstallNotificationReceiver() {
	}

	@PUT
	@Consumes(MediaType.APPLICATION_XML)
	/** Receives an installation notification specifying the operation result */
	public Response putInstallNotificationXML(JAXBElement<InstallNotification> notification) {
		
		Logger log = (Logger)mContext.getAttribute(IntrusionDetectionContext.CONTEXT_ATTR_LOG);
		Configuration config = (Configuration)mContext.getAttribute(IntrusionDetectionContext.CONTEXT_ATTR_CONFIG);
		InstallationInfo install = (InstallationInfo)mContext.getAttribute(IntrusionDetectionContext.CONTEXT_ATTR_INST_INFO);
		
		Response res = Response.created(mUriInfo.getAbsolutePath()).build();
		
		log.loginfo("Received installation notification");

		InstallNotification c = notification.getValue();
		if (c.getInstallSuccess() == 1) {

			install.mInstallationNotification = c;

			log.loginfo("Application successfully installed");

			// Update the app configuration
			config.mAppProps.setProperty(Configuration.PROP_KEY_IS_INSTALLED, "1");
			config.mAppProps.setProperty(Configuration.PROP_KEY_APP_ID, c.getAppID());

			if (c.getServiceList() != null) {
				config.mAppProps.setProperty(Configuration.PROP_KEY_N_SERVICES, 
 					                         new Integer(c.getServiceList().size()).toString());
				for (int nService=0; nService < c.getServiceList().size(); nService++) {
					config.mAppProps.setProperty(Configuration.PROP_KEY_SERVICE_ID_PREFIX + nService, 
							                     c.getServiceList().get(nService).getServiceID());
					config.mAppProps.setProperty(Configuration.PROP_KEY_SERVICE_TOKEN_PREFIX + nService, 
		                     c.getServiceList().get(nService).getToken());	
				}
			} else {
				config.mAppProps.setProperty(Configuration.PROP_KEY_N_SERVICES, "0");
			}
			
			try {
				config.saveConfig();
				log.loginfo("Configuration saved");
			} catch (Exception e) {
				log.logerr("Cannot save configuration: " + e.getMessage());
			}
			
			log.loginfo("App ID: " + c.getAppID());
			if (c.getServiceList() == null) {
				log.loginfo("No service associated to application");
			} else {
				for (ServiceInstallation si : c.getServiceList()) {
					log.loginfo("  Service  ID: " + si.getServiceID());
				}
				log.loginfo("--- end of list ---");
			}
			
			log.loginfo("Starting the processing");
			IntrusionDetectionThread th = new IntrusionDetectionThread(mContext);
			th.start();
			
		} else {
			String installErrorMsg = c.getMessage();
			log.logerr("Application not installed");
			if (installErrorMsg != null) {
				log.logerr(installErrorMsg);
			}
		}
		
		return res;
	}
	
}
