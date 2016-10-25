package eu.betaas.taas.contextmanager.linkeddata.api.impl;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.osgi.framework.BundleContext;

import eu.betaas.taas.contextmanager.linkeddata.api.LinkedDataService;
import eu.betaas.taas.contextmanager.linkeddata.messaging.MessageManager;
import eu.betaas.taas.contextmanager.linkeddata.semantics.DatasetBuilder;
import eu.betaas.taas.contextmanager.linkeddata.semantics.ThingsManager;


@Path("v1")
public class LinkedDataServiceImpl implements LinkedDataService {
	private Logger logger = Logger.getLogger("betaas.taas");
	
	private BundleContext context;
	private MessageManager mManager;
	
	public void setupService(){
		mManager = new MessageManager(context);
		mManager.monitoringPublish("External linked data service started");
		logger.info("[LinkedDataServiceImpl] External linked data service started");
	}
	
	@GET
    @Path("/things")
	@Produces(MediaType.APPLICATION_XML)
	public Response getCatalog() {
		try {
			mManager.monitoringPublish("Get request for the things catalog");
			ThingsManager manager = new ThingsManager();
			String body = manager.retrieveThingListAsDCAT();
			return Response.ok(body).build();
		} catch (Exception e) {
			mManager.monitoringPublish("Server error getting request for the things catalog");
			return Response.serverError().build();
		}
	}
	
	@GET
    @Path("/things")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getThings() {
		try {
			mManager.monitoringPublish("Get request for the list of things");
			ThingsManager manager = new ThingsManager();
			String body = manager.retrieveThingListAsJSON();
			return Response.ok(body).build();
		} catch (Exception e) {
			mManager.monitoringPublish("Server error getting request for the list of things");
			return Response.serverError().build();
		}
	}
	
	@GET
    @Path("/datasets/{id}")
	public Response getDataset(@PathParam("id") String id) {
		DatasetBuilder dsBuilder = new DatasetBuilder();
		try {
			mManager.monitoringPublish("Get request for the dataset " + id);
			String body = dsBuilder.generateCSVDataset(id);
			return Response.ok(body)
					        .header(
					        	"Content-Disposition",
					        	"attachment; filename=dataset" + id + ".csv")
					        .build();
		} catch (Exception e) {
			mManager.monitoringPublish("Error " + e.getMessage() + " for dataset " + id);
			return Response.status(Integer.valueOf(e.getMessage())).build();
		}
	}
	
	public void setContext(BundleContext context) {
		this.context = context;
	}
}
