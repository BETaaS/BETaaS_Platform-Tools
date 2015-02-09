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
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXBElement;

import eu.betaas.apps.lib.DataNotification;

@Path("/measurementNotification")
public class MeasurementNotificationReceiver {
	@Context
	ServletContext mContext;
	
	@Context
    UriInfo uriInfo;
	
	@PUT
	@Consumes(MediaType.APPLICATION_XML)
	/** Receives a new measurement notification for a requested service*/
	public Response putMeasurementNotificationXML(JAXBElement<DataNotification> notification) {
		PresenceInfo info = (PresenceInfo)mContext.getAttribute(IntrusionDetectionContext.CONTEXT_ATTR_PRESENCE_INFO);
		boolean presence;
		
		Response res = Response.created(uriInfo.getAbsolutePath()).build();
		
		DataNotification n = notification.getValue();
		System.out.println("--------------------------------");
		System.out.println("Service ID: " + n.getServiceID());
		System.out.println("Measurement value: " + n.getData());
		System.out.println("--------------------------------");
		
		try {
			if (n.getData() != null) {
	
				if (n.getData().equals("true")) {
					presence = true;
				} else if (n.getData().equals("false")) {
					presence = false;
				} else {
					throw new Exception("Unexpected measurement (not true neither false)");
				}
				
				info.setPresence(presence);
				
			} else {
				throw new Exception("Unexpected measurement (not true neither false)");
			}
			
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
		return res;
	}

}
