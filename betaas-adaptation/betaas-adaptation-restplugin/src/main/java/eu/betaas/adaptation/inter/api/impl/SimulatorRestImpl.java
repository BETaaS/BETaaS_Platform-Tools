package eu.betaas.adaptation.inter.api.impl;

import java.util.List;

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

import eu.betaas.adaptation.inter.db.ServiceDB;
import eu.betaas.taas.bigdatamanager.database.hibernate.data.SimulatedThing;

/**
 * This class implements the interfaces of SM visible to external GW components
 * @author Intecs
 */
@WebService
public class SimulatorRestImpl{
	
	/** Logger */
	private static Logger mLogger = Logger.getLogger("betaas.adaptation");
	
	/** DB Service*/
	
	private ServiceDB dbService;
	
	
	
	public void setService(ServiceDB dbService) {
		this.dbService = dbService;
	}

	public void startService() {
		mLogger.info("Starting Things Simulator REST Service");		
	}
	
	public void closeService() {
		mLogger.info("Stopping Things Simulator REST Service");

	}
 
    @DELETE
    @Path("/delete/{id}")	 
    @Produces(MediaType.APPLICATION_JSON)
	public void deleteThing(@PathParam("id") String thingId) {

		mLogger.info("Called Delete Thing with id: " + thingId);
		dbService.deleteThing(thingId);
	}
	
	
	@POST	
    @Path("/thing")
	@Produces(MediaType.APPLICATION_JSON)
	public void createThing(String thing) {
		mLogger.info("Called Create Thing with data: " + thing);
		dbService.saveThing(thing);
	}
	
    @GET
    @Path("/things")
	public String getThings() {
		mLogger.info("Called getThings");
		return dbService.listAllThings();
	}
	
}