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

// BETaaS - Building the Environment for the Things as a Service
//
// Component: SM
// Responsible: Intecs

package eu.betaas.es;

import javax.jws.WebService;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;

@Produces(MediaType.APPLICATION_JSON)
@WebService
public class Manager  {
	
	private static Logger mLogger = Logger.getLogger("betaas.service");
	
	BundleContext bc;
	  
	public void startService() {
		
		mLogger.info("Bundle Extended Service Manager started");
		
	}
	


	public void closeService() {
		mLogger.info("Bundle Extended Service Manager stopped");
	}

	
    @GET
    @Path("/status/{groupID}/{artID}/{verID}")
    @Consumes(MediaType.APPLICATION_JSON)
	public String getEsList(@PathParam("groupID") String groupID,@PathParam("artID") String artID,@PathParam("verID") String verID) {
    	
    	mLogger.info("Bundle requested mvn:" + groupID+"/"+artID+"/"+verID);
    	String name = "mvn:" + groupID+"/"+artID+"/"+verID;
    	Bundle[] blist = bc.getBundles();
    	String status = "Not Installed";
    
		for (Bundle b : blist){
			
			if (b.getLocation().equals(name)){
				mLogger.info("Bundle found ");
				if (b.getState()==Bundle.ACTIVE) mLogger.info("Bundle is active ");
				mLogger.info("Bundle Id "+b.getBundleId());
				mLogger.info("Bundle State "+b.getState());
				mLogger.info("Bundle name "+b.getLocation());
				if (b.getState()==Bundle.ACTIVE)status = "Active";
				if (b.getState()==Bundle.INSTALLED)status = "Installed";
				if (b.getState()==Bundle.RESOLVED)status = "Stopped";
				
			}
			
		}
    	
		return "{\"status\":\""+status+"\"}";
	}
  
    @POST
    @Path("/install/{groupID}/{artID}/{verID}")	 
    @Consumes("application/xml; charset=UTF-8")
  	public String installEs(@PathParam("groupID") String groupID,@PathParam("artID") String artID,@PathParam("verID") String verID) {
    	mLogger.info("Bundle install mvn:" + groupID+"/"+artID+"/"+verID);
    	String status = "Not Installed";
    	String name = "mvn:" + groupID+"/"+artID+"/"+verID;
    	try {
			Bundle b = bc.installBundle(name);
			mLogger.info("Bundle installed  as" + b.getBundleId());
			status = "Resolved";
			//b.start();
			//status = "Started";
		} catch (BundleException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
		return "{\"status\":\""+status+"\"}";
	}

    @DELETE
    @Path("/uninstall/{groupID}/{artID}/{verID}")	 
    @Consumes("application/xml; charset=UTF-8")
  	public String deleteEs(@PathParam("groupID") String groupID,@PathParam("artID") String artID,@PathParam("verID") String verID) {
    	mLogger.info("Bundle uninstall mvn:" + groupID+"/"+artID+"/"+verID);
    	mLogger.info("Bundle requested mvn:" + groupID+"/"+artID+"/"+verID);
    	String name = "mvn:" + groupID+"/"+artID+"/"+verID;
    	Bundle[] blist = bc.getBundles();
    	String status = "Not Installed";
    
		for (Bundle b : blist){
			
			if (b.getLocation().equals(name)){
				mLogger.info("Bundle found as "+ b.getBundleId());
				if (b.getState()==Bundle.ACTIVE) mLogger.info("Bundle is active ");
				 status = "Resolved";
				try {
					b.uninstall();
					status = "Not Installed";
				} catch (BundleException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					status = "Error";
				}
				
			}
			
		}
    	
		return "{\"status\":\""+status+"\"}";
	}

    @GET
    @Path("/start/{groupID}/{artID}/{verID}")
    @Consumes(MediaType.APPLICATION_JSON)
	public String startEs(@PathParam("groupID") String groupID,@PathParam("artID") String artID,@PathParam("verID") String verID) {
    	String name = "mvn:" + groupID+"/"+artID+"/"+verID;
    	Bundle[] blist = bc.getBundles();
    	String status = "Not Installed";
    
		for (Bundle b : blist){
			
			if (b.getLocation().equals(name)){
				mLogger.info("Bundle to be started found as "+ b.getBundleId());
				if (b.getState()==Bundle.ACTIVE)status = "Active";
				if (b.getState()==Bundle.INSTALLED)status = "Installed";
				if (b.getState()==Bundle.RESOLVED)status = "Stopped";
					try {
						b.start();
						status = "Active";
					} catch (BundleException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						status = "Error";
					}
								
			}
			
		}
    	
		return "{\"status\":\""+status+"\"}";
    }
    
    @GET
    @Path("/stop/{groupID}/{artID}/{verID}")
    @Consumes(MediaType.APPLICATION_JSON)
	public String stopEs(@PathParam("groupID") String groupID,@PathParam("artID") String artID,@PathParam("verID") String verID) {
    	String name = "mvn:" + groupID+"/"+artID+"/"+verID;
    	Bundle[] blist = bc.getBundles();
    	String status = "Not Installed";
    
		for (Bundle b : blist){
			
			if (b.getLocation().equals(name)){
				mLogger.info("Bundle to be stopped found as "+ b.getBundleId());
				if (b.getState()==Bundle.ACTIVE)status = "Active";
				if (b.getState()==Bundle.INSTALLED)status = "Installed";
				if (b.getState()==Bundle.RESOLVED)status = "Stopped";
					try {
						b.stop();
						status = "Stopped";
					} catch (BundleException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						status = "Error";
					}
				
				
				
			}
			
		}
    	
		return "{\"status\":\""+status+"\"}";
    }
    
	public BundleContext getBc() {
		return bc;
	}

	public void setBc(BundleContext bc) {
		this.bc = bc;
	}    
    
}
