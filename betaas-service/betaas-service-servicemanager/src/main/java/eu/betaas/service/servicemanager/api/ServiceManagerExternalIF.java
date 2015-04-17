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

package eu.betaas.service.servicemanager.api;

//import javax.ws.rs.Consumes;
//import javax.ws.rs.GET;
//import javax.ws.rs.HeaderParam;
//import javax.ws.rs.POST;
//import javax.ws.rs.PUT;
//import javax.ws.rs.Path;
//import javax.ws.rs.PathParam;

/**
 * This interface defines the SM service exposed through OSGI and visible also
 * from the other gateways.
 *  
 * @author Intecs
 */
//@WebService
public interface ServiceManagerExternalIF {
	
	/**
	 * Getter for the GW identifier for this SM
	 * @return This GW identifier
	 */
	public String getGWId();
	
	/**
	 * It is called by the application (e.g. IntrusionDetection) in order 
	 * to request the internal GW modules to prepare for the service execution
	 * and receive an appID that uniquely identifies the installation request.
	 * @param manifestContent This is the Manifest containing 
	 * the information to be forwarded to TaasRM: semantic, QoS requirements
	 * credential and the Betaas ApplicationID.
	 * @return The ID assigned to the app or an empty string on error
	 */
//    @POST
//    @Path("/installation")	 
//    @Consumes("application/xml; charset=UTF-8")
  	public String installApplication(String manifestContent); 
	
	
	/**
	 * Get the list of services identifiers
	 * @return a json structure (as a String) containing the list of service ID installed for the app
	 *         in the same order the service were specified in the manifest
	 */
//	@GET
//  @Path("/installation/{appID}")	
	public String getApplicationServices(/*@PathParam("appID")*/ String appID);
	

	
	/** It is used to uninstall a previously installed application
	 * @param appID the ID returned by SM during the installation process
	 * @param manifestContent the manifest from which credentials will be taken
	 * @return true in case of success
	 */
//    @DELETE
//    @Path("/installation/{appID}")	 
//    @Consumes("application/xml; charset=UTF-8")
	public boolean uninstallApplication(/*@PathParam("appID")*/ String appID, 
			                            String manifestContent);
	
	/**
	 * Used by applications to get Thing Services data (pull)
	 * @param appID the application that requested the service at installation time
	 * @param serviceID the ID returned by TaaSRM (through SM)
	 * @param token the Base64 encoded token to access the service 
	 * @return the requested data or null in case of error
	 */
//    @GET
//    @Path("/data/{appID}/{serviceID}")	 
//    @Consumes("application/xml; charset=UTF-8")
	public String getThingServiceData(/*@PathParam("appID")*/ String appID, 
                                      /*@PathParam("serviceID")*/ String serviceID, 
                                      /*@HeaderParam("token")*/ String token);
	
	/**
	 * Used to control actuators via the allocated thing services
	 * @param appID the application that requested the thing services at installation time
	 * @param serviceID the service installed to control thing services 
	 * @param data the data to be set
	 * @param token the Base64 encoded token to access the service 
	 * @return true if the command is successfully sent to TaaS layer
	 */
//    @PUT
//    @Path("/data/{appID}/{serviceID}/{data}")	 
//    @Consumes("application/xml; charset=UTF-8")
	public boolean setThingServiceData(/*@PathParam("appID")*/ String appID, 
                                       /*@PathParam("serviceID")*/ String serviceID, 
                                       /*@PathParam("data")*/ String data, 
                                       /*@HeaderParam("token")*/ String token);
	
	//TODO: add credentials to this getExtededServiceData params
	/**
	 * It is used by applications to get results from ExtendedServices 
	 * @param appID the application that requested the extended service at installation time
	 * @param extServUniqueName the extended service unique name requested at installation time
	 * @param additionalInfo optional information that could be used to pass parameters (e.g.
	 *        as XML) to request a specific result.
	 * @return the result of extended service processing (it depends on the extended service logic,
	 *         and in general it has nothing to do with the data returned by the thing services
	 *         used by the extended service)
	 */
//	@GET
//  @Path("/extended/{appID}/{extServUniqueName}")
//  @Consumes("application/xml; charset=UTF-8")	
	public String getExtendedServiceData(/*@PathParam("appID")*/ String appID, 
			                             /*@PathParam("extServUniqueName")*/ String extServUniqueName, 
			                             /*@HeaderParam("additionalInfo")*/ String additionalInfo);
	
	/**
	 * Used by apps to register to a specified service in order to receive
	 * asynchronous notifications (push)
	 * @param appID
	 * @param serviceID
	 * @param token the Base64 encoded token to access the service 
	 * @return true in case of success
	 */
//  @POST
//  @Path("/registration/{appID}/{serviceID}")	 
//  @Consumes("application/xml; charset=UTF-8")
	public boolean register(/*@PathParam("appID")*/ String appID, 
			                /*@PathParam("serviceID")*/ String serviceID, 
			                /*@HeaderParam("token")*/ String token);
	
	/**
	 * Used by apps to unregister to a specified service in order to stop receiving
	 * asynchronous notifications (push)
	 * @param appID
	 * @param serviceID
	 * @param token the Base64 encoded token to access the service 
	 * @return true in case of success
	 */
//  @DELETE
//  @Path("/registration/{appID}/{serviceID}")	 
//  @Consumes("application/xml; charset=UTF-8")
	public boolean unregister(/*@PathParam("appID")*/ String appID, 
					          /*@PathParam("serviceID")*/ String serviceID, 
					          /*@HeaderParam("token")*/ String token);

	/**
	 * Used by apps to request a BDM task execution data analysis
	 * @param appID the application identifier
	 * @param taskID the task identifier
	 * @return the task result
	 */
//	@GET
//  @Path("/task/{appID}/{taskID}")
	public String getTaskData(/*@PathParam("appID")*/ String appID, 
			                  /*@PathParam("taskID")*/ String taskID);
	
}
