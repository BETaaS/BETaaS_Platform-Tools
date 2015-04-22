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

package eu.betaas.service.servicemanager.api.impl;

import org.apache.log4j.Logger;
import org.osgi.framework.BundleContext;

import eu.betaas.service.bigdatamanager.applicationdataservice.IDataManagerADService;
import eu.betaas.service.servicemanager.ServiceManager;
import eu.betaas.service.servicemanager.api.ServiceManagerExternalIF;
import eu.betaas.service.servicemanager.application.Installer;
import eu.betaas.service.servicemanager.application.RequestManager;
import eu.betaas.service.servicemanager.application.registry.AppRegistryRow;
import eu.betaas.service.servicemanager.application.registry.ApplicationRegistry;
import eu.betaas.service.servicemanager.extended.api.IExtendedService;
import eu.betaas.service.servicemanager.extended.discovery.Discovery;

import javax.jws.WebService;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.FormParam;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

/**
 * This class implements the interfaces of SM visible to external GW components
 * @author Intecs
 */
@Produces(MediaType.APPLICATION_XML)
@WebService
public class ExternalAPIImpl implements ServiceManagerExternalIF {
	
	////////////////////////// SETTERS FOR CONFIGURATION PARAMETERS //////////////////////
	  
	public void setGwId(String gwId) {
		ServiceManager.getInstance().setGwId(gwId);
	}
	
	/**
	 * Setter for the OSGi context
	 * @param context
	 */
	public void setContext(BundleContext context) {
		mLogger.info("Setting the context on external IF bundle");
		mContext = context;
		ServiceManager.setContext(context);
	}
	
	/** 
	 * Setter for BDM service
	 * @param service
	 */
	public void setBDMService(IDataManagerADService service) {
		mLogger.info("Setting BDM service on external IF bundle");
		mBDMService = service;
	}
	
	//////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Startup the service exposing internal IF to the gateway
	 */
	public void startService() {
		mLogger.info("Starting Service Manager external IF bundle");
		
		mDiscovery = new Discovery(mContext);
	}
	
	public void closeService() {
		mLogger.info("Stopping Service Manager external IF bundle");
		try {
			ServiceManager.getInstance().getAppRegistry().store();
		} catch (Exception e) {
			mLogger.error("Cannot store the application registry into the DB: " + e.getMessage());
		}
		try {
			ServiceManager.getInstance().getExtendedRegistry().store();
		} catch (Exception e) {
			mLogger.error("Cannot store the extended service registry into the DB: " + e.getMessage());
		}
	}

	
    @GET
    @Path("/gwid")
	public String getGWId() {
		mLogger.info("Called getGWId");
		
		return ServiceManager.getInstance().getGWId();
	}
  
    @POST
    @Path("/application")	 
    @Consumes("application/xml; charset=UTF-8")
  	public String installApplication(String manifestContent) {

		mLogger.info("Called install application: " + manifestContent);
		
		return mInstaller.install(manifestContent, mDiscovery);
	}


    @DELETE
    @Path("/application/{appID}")	 
    @Consumes("application/xml; charset=UTF-8")
	public boolean uninstallApplication(@PathParam("appID") String appID, String manifestContent) {

		mLogger.info("Called uninstall for application ID: " + appID);
		mLogger.info("Manifest: " + manifestContent);
				
		return mInstaller.uninstall(appID, manifestContent, mDiscovery);
	}
	
	
	@GET
    @Path("/application/{appID}")	
	public String getApplicationServices(@PathParam("appID") String appID) {
		
		mLogger.info("Called getApplicationServices: " + appID);
		
		return mInstaller.getApplicationServices(appID);
	}
	
    @GET
    @Path("/data/{appID}/{serviceID}")	 
    @Consumes("application/xml; charset=UTF-8")
	public String getThingServiceData(@PathParam("appID") String appID, 
                                      @PathParam("serviceID") String serviceID, 
                                      @HeaderParam("token") String token) {
		mLogger.info("Called getThingServiceData(" + appID + ", " + serviceID + ")");
    if ((token == null) || (token.length() == 0)) {
      mLogger.warn("Token not specified");
    }
		
		if ((appID == null) || (serviceID == null)) {
			mLogger.error("Wrong appID/serviceID");
			return "";
		}
		
		// Get the GW Id from the service ID
		int pos = serviceID.indexOf("::");
		if (pos < 0) {
			mLogger.error("Cannot extract GW identifier from serviceID");
			return "";
		}
		
		String gwId = serviceID.substring(0, pos);
		
		if (gwId.equals(ServiceManager.getInstance().getGWId())) {
		
			// It is a service allocated in this GW
			
			ApplicationRegistry applicationReg = ServiceManager.getInstance().getAppRegistry();
			if (applicationReg.getApp(appID, true) == null) {
				mLogger.error("Received getThingServiceData request from an unregistered application");
				return "";
			}
		
			RequestManager reqMng = new RequestManager();
		
			return reqMng.getThingServiceData(serviceID, true, token);

		} else {
			
			ServiceManagerExternalIF extSM = mDiscovery.retrieveSM(gwId);
			if (extSM != null) {
				
				mLogger.info("Calling remote getThingServiceData");
				return extSM.getThingServiceData(appID, serviceID, token);
				
			} else {
				mLogger.error("Cannot get remote SM with GWId=" + gwId);
				return "";
			}			
		}		
	}
	
	
    @PUT
    @Path("/data/{appID}/{serviceID}/{data}")	 
    @Consumes("application/xml; charset=UTF-8")
	public boolean setThingServiceData(@PathParam("appID") String appID, 
                                       @PathParam("serviceID") String serviceID, 
                                       @PathParam("data") String data, 
                                       @HeaderParam("token") String token) {
		mLogger.info("Called setThingServiceData(" + appID + ", " + serviceID + ", " + data + ")");
		
		if ((appID == null) || (serviceID == null)) {
			mLogger.error("Wrong appID/serviceID");
			return false;
		}
		
		if (data == null) {
			mLogger.error("Cannot set null data");
			return false;
		}
		
		// Get the GW Id from the service ID
		int pos = serviceID.indexOf("::");
		if (pos < 0) {
			mLogger.error("Cannot extract GW identifier from serviceID");
			return false;
		}
		
		String gwId = serviceID.substring(0, pos);
		
		if (gwId.equals(ServiceManager.getInstance().getGWId())) {
		
			// It is a service allocated in this GW
				
			ApplicationRegistry applicationReg = ServiceManager.getInstance().getAppRegistry();
			if (applicationReg.getApp(appID, true) == null) {
				mLogger.error("Received setThingServiceData request from an unregistered application");
				return false;
			}
			

			
			RequestManager reqMng = new RequestManager();
			
//			return reqMng.setThingServiceData(serviceID, jobject);
			return reqMng.setThingServiceData(serviceID, data, token);
			
		} else {
			
			ServiceManagerExternalIF extSM = mDiscovery.retrieveSM(gwId);
			if (extSM != null) {
				
				mLogger.info("Calling remote setThingServiceData for GWId=" + gwId);
				return extSM.setThingServiceData(appID, serviceID, data, token);
				
			} else {
				mLogger.error("Cannot get remote SM with GWId=" + gwId);
				return false;
			}		
		}
	}
	
	
	@GET
    @Path("/extended/{appID}/{extServUniqueName}")
    @Consumes("application/xml; charset=UTF-8")	
	public String getExtendedServiceData(@PathParam("appID") String appID, 
			                             @PathParam("extServUniqueName") String extServUniqueName, 
			                             @HeaderParam("additionalInfo") String additionalInfo) {
		IExtendedService extServRef = null;
		String res = null;
		
		mLogger.info("Called getExtendedServiceResult(" + appID + ", " + extServUniqueName + ")");
		
		ApplicationRegistry applicationReg = ServiceManager.getInstance().getAppRegistry();
		if (applicationReg.getApp(appID, true) == null) {
			mLogger.error("Received getExtendedServiceData request from an unregistered application");
			return null;
		}

		if ((extServRef = mDiscovery.retrieveExtendedService(extServUniqueName)) == null) {
			mLogger.warn("Cannot retrieve extended service");
			return null;
		}

		try {
			res = extServRef.getResult(additionalInfo);
		} catch (Exception e) {
			mLogger.error("Cannot get result from extended service: " + e.getMessage());
			// Maybe the service is no more valid. Remove it from the retrieved services list
			mDiscovery.remove(extServUniqueName);
			// Try to retrieve it again (through DOSGi)
			if ((extServRef = mDiscovery.retrieveExtendedService(extServUniqueName)) != null) {
				try {
					res = extServRef.getResult(additionalInfo);
				} catch (Exception e2) {
					res = null;
				}
			}
			
			res = null;
		}
		
		return res;
	}
		
    @POST
    @Path("/registration/{appID}/{serviceID}")	 
    @Consumes("application/xml; charset=UTF-8")
	public boolean register(@PathParam("appID") String appID, 
			                @PathParam("serviceID") String serviceID, 
			                @HeaderParam("token") String token) {
    	
		mLogger.info("Called register(" + appID + ", " + serviceID + ")");
		
		if ((appID == null) || (serviceID == null)) {
			mLogger.error("Wrong appID/serviceID");
			return false;
		}
		
		// Get the GW Id from the service ID
		int pos = serviceID.indexOf("::");
		if (pos < 0) {
			mLogger.error("Cannot extract GW identifier from serviceID");
			return false;
		}
		
		String gwId = serviceID.substring(0, pos);
		
		if (gwId.equals(ServiceManager.getInstance().getGWId())) {
		
			// It is a service allocated in this GW
			
			ApplicationRegistry applicationReg = ServiceManager.getInstance().getAppRegistry();
			AppRegistryRow regRow = applicationReg.getApp(appID, true);
			if (regRow == null) {
				mLogger.error("Received register request from an unregistered application");
				return false;
			}
			if (!regRow.hasService(serviceID)) {
				mLogger.error("Received register request for a service not allocated by the application");
				return false;
			}
			
			RequestManager reqMng = new RequestManager();
			return reqMng.register(appID, serviceID, token);
			
		} else {
			
			ServiceManagerExternalIF extSM = mDiscovery.retrieveSM(gwId);
			if (extSM != null) {
				
				mLogger.info("Calling remote register for GWId=" + gwId);
				return extSM.register(appID, serviceID, token);
				
			} else {
				mLogger.error("Cannot get remote SM with GWId=" + gwId);
				return false;
			}
		}
	}

    @DELETE
    @Path("/registration/{appID}/{serviceID}")	 
    @Consumes("application/xml; charset=UTF-8")
	public boolean unregister(@PathParam("appID") String appID, 
					          @PathParam("serviceID") String serviceID, 
					          @HeaderParam("token") String token) {
    	
		mLogger.info("Called unregister(" + appID + ", " + serviceID + ")");
		
		if ((appID == null) || (serviceID == null)) {
			mLogger.error("Wrong appID/serviceID");
			return false;
		}
		
		// Get the GW Id from the service ID
		int pos = serviceID.indexOf("::");
		if (pos < 0) {
			mLogger.error("Cannot extract GW identifier from serviceID");
			return false;
		}
		
		String gwId = serviceID.substring(0, pos);
		
		if (gwId.equals(ServiceManager.getInstance().getGWId())) {
		
			// It is a service allocated in this GW

			ApplicationRegistry applicationReg = ServiceManager.getInstance().getAppRegistry();
			AppRegistryRow regRow = applicationReg.getApp(appID, true);
			if (regRow == null) {
				mLogger.error("Received unregister request from an unregistered application");
				return false;
			}
			if (!regRow.hasService(serviceID)) {
				mLogger.error("Received unregister request for a service not allocated by the application");
				return false;
			}
			
			RequestManager reqMng = new RequestManager();
			return reqMng.unregister(appID, serviceID, token);
			
		} else {
			
			ServiceManagerExternalIF extSM = mDiscovery.retrieveSM(gwId);
			if (extSM != null) {
				
				mLogger.info("Calling remote unregister");
				return extSM.unregister(appID, serviceID, token);
				
			} else {
				mLogger.error("Cannot get remote SM with GWId=" + gwId);
				return false;
			}
		}
	}
	
	@GET
    @Path("/task/{appID}/{taskID}")
	public String getTaskData(@PathParam("appID") String appID, 
			                  @PathParam("taskID") String taskID) {
		
		mLogger.info("Called getTaskData(" + appID + ", " + taskID + ")");
		
		String result = "";
		ApplicationRegistry applicationReg = ServiceManager.getInstance().getAppRegistry();
		if (applicationReg.getApp(appID, true) == null) {
			mLogger.error("Received getThingServiceData request from an unregistered application");
			return "";
		}
		
		if (mBDMService == null) {
			mLogger.error("BDM service not available to request the task");
			return "";
		}

		try {
			result = mBDMService.taskData(taskID);
		} catch (Exception e) {
			mLogger.error("Error executing the task: " + e.getMessage());
			result = "";
		}
		
		return result;
	}
	

	/** Logger */
	private static Logger mLogger = Logger.getLogger(ServiceManager.LOGGER_NAME);

	/** OSGi context */
	private BundleContext mContext;
	
	/** The BDM service to use tasks */
	private IDataManagerADService mBDMService;
	
	private Installer mInstaller = new Installer();
	
	/** The discovery manager */
	Discovery mDiscovery;
}
