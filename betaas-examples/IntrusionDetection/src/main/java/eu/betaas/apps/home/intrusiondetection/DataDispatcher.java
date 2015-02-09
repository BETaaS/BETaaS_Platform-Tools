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
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Used to publish the service to clients requesting data
 * @author Intecs
 */
@Path("/presence")
public class DataDispatcher {
	
	@Context
	ServletContext mContext;
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getPresence() {
		PresenceInfo info = (PresenceInfo)mContext.getAttribute(IntrusionDetectionContext.CONTEXT_ATTR_PRESENCE_INFO);
		
		Presence presence = new Presence();
		
		presence.setmStartDate(info.getStartDate());
		presence.setmConnected(info.ismConnected());
		if (info.ismConnected()) {
			presence.setmLastDate(info.getLastDate());
			presence.setmLastPresence(info.getLastPresence());
		} else {
			presence.setmLastDate(null);
			presence.setmLastPresence(null);
		}
		presence.setmLastStatus(info.getLastStatus());
		
		CacheControl cc = new CacheControl(); 
	    cc.setNoCache(true);
	    cc.setMaxAge(0);
	    
		return Response.ok(presence).cacheControl(cc).build();
	}
}
