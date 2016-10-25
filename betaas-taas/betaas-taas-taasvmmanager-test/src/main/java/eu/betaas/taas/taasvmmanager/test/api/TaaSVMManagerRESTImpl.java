package eu.betaas.taas.taasvmmanager.test.api;

import java.util.HashMap;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

import com.google.gson.Gson;

import eu.betaas.taas.taasvmmanager.api.datamodel.Availability;
import eu.betaas.taas.taasvmmanager.api.datamodel.Flavor;
import eu.betaas.taas.taasvmmanager.api.datamodel.InstanceType;
import eu.betaas.taas.taasvmmanager.api.datamodel.VMRequest;
import eu.betaas.taas.taasvmmanager.test.client.TaaSVMManagerClient;

@Path("v1")
public class TaaSVMManagerRESTImpl {

	private Logger logger = Logger.getLogger("betaas.taas");
	
	public void startService(){
		logger.info("[TaaSVMManagerRESTImpl] External Starting REST api for VMManger");
		logger.info("[TaaSVMManagerRESTImpl] External REST api for VMManger started");
	}
	
	public void closeService() {
		logger.info("[TaaSVMManagerRESTImpl] External REST api for VMManger stopped");
	}
	
	@GET
    @Path("/flavors")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getFlavors() {
		TaaSVMManagerClient client = TaaSVMManagerClient.getInstance();
		List<Flavor> flavors = client.getFlavors();
		//List<String> flavors = client.getFlavors();
		return Response.ok(new Gson().toJson(flavors)).build();
	}
	
	@POST
    @Path("/machines")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createVM(String request) {
		TaaSVMManagerClient client = TaaSVMManagerClient.getInstance();
		Gson gson = new Gson();
		VMRequest req = gson.fromJson(request, VMRequest.class);
		
		try {
			String res = client.createVM(req);
			return Response.status(201).entity(res).build();
		} catch (Exception e) {
			return Response.serverError().entity(e.getMessage()).build();
		}
	}

	@POST
    @Path("/extmachines")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createExtVM(String request) {
		TaaSVMManagerClient client = TaaSVMManagerClient.getInstance();
		Gson gson = new Gson();
		VMRequest req = gson.fromJson(request, VMRequest.class);
		
		String res = client.createExtVM(req);
		
		return Response.ok(res).build();
	}

	@DELETE
    @Path("/machines/{idVM}")
	public Response deleteVM(@PathParam("idVM") String idVM) {
		TaaSVMManagerClient client = TaaSVMManagerClient.getInstance();
		boolean res = client.deleteVM(idVM);
		
		if (res) {
			return Response.status(204).build();
		} else {
			return Response.serverError().build();
		}
	}

	@GET
    @Path("/availability")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAvailability(@QueryParam("orderBy")String field) {
		TaaSVMManagerClient client = TaaSVMManagerClient.getInstance();
		List<Availability> res;
		if (field == null || field.equals("")) {
			res = client.getAvailability();
		} else if (field.equals("cpu")) {
			res = client.getAvailability(true);
		} else {
			//bad request
			return Response.status(400).build();
		}
		
		return Response.ok(new Gson().toJson(res)).build();
	}

	@PUT
    @Path("/migration")
	public Response sendVMs(String migrationInfo) {
		TaaSVMManagerClient client = TaaSVMManagerClient.getInstance();
		boolean ret;
		Gson gson = new Gson();
		MigrationInfo info;
		info = gson.fromJson(migrationInfo, MigrationInfo.class);
		
		if (info.getVmIds() == null) {
			//bad request
			return Response.status(400).build();
		}
		
		if (info.getTarget() == null || info.getTarget().equals("")) {
			//bad request
			return Response.status(400).build();
		}
		
		if (info.getVmIds().size() == 0) {
			//bad request
			return Response.status(400).build();
		}
		
		if (info.getVmIds().size() == 1) {
			ret = client.migrateVM(
					info.getVmIds().get(0), 
					info.getTarget());
		} else {
			ret = client.sendVMs(info.getVmIds(), info.getTarget());
		}
		
		if (ret) {
			return Response.status(204).build();
		} else {
			return Response.serverError().build();
		}
	}
	
	private class MigrationInfo {
		List<String> vmIds;
		String target;
		
		public MigrationInfo() {}

		public List<String> getVmIds() {
			return vmIds;
		}

		public void setVmIds(List<String> vmIds) {
			this.vmIds = vmIds;
		}

		public String getTarget() {
			return target;
		}

		public void setTarget(String target) {
			this.target = target;
		}
	}
}
