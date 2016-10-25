package eu.betaas.taas.contextmanager.web.rest;

import javax.jws.WebService;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import org.apache.log4j.Logger;
import org.osgi.framework.BundleContext;

import com.google.gson.JsonObject;

import eu.betaas.taas.contextmanager.api.impl.ThingsServiceManagerImpl;

@Produces(MediaType.APPLICATION_JSON)
@WebService
public class TaasCMRestResource {

  // PUBLIC SECTION
  public final static String LOGGER_NAME = "betaas.taas";

  // PRIVATE SECTION
  private static ThingsServiceManagerImpl thing = null;
  
  private static Logger mLogger = Logger.getLogger(LOGGER_NAME);
  
  
  @POST
  @Path("/install/{groupID}/{artID}/{verID}")
  @Consumes("application/xml;charset=UTF-8")
  public Response testPOS(@PathParam("groupID") String groupID, @PathParam("artID") String artID, @PathParam("verID") String verID) {
      mLogger.info("POST Bundle requested mvn:"+groupID+"/"+artID+"/"+verID);
      return Response.ok().entity( "Yes, it works." ).build();
  }
  
  @GET
  @Path("/status/{groupID}/{artID}/{verID}")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response testGET(@PathParam("groupID") String groupID, @PathParam("artID") String artID, @PathParam("verID") String verID) {
      mLogger.info("GET Bundle requested mvn:");
      return Response.ok().entity( "Yes, it works." ).build();
  }
  
  @GET
  @Path("/addTerm/{sConcept}/{sAltLabel}/{sDefinition}")
  @Consumes(MediaType.APPLICATION_JSON)
  public boolean addTerm(@PathParam("sConcept") String sConcept, @PathParam("sAltLabel") String sAltLabel, @PathParam("sDefinition") String sDefinition){
    mLogger.info("addTerm Bundle requested.");
    boolean bResult = thing.addTerm(sConcept, sAltLabel, sDefinition);
    return bResult;    
  }
  
  
  
  
  @GET
  @Path("/checkThingType/{sTerm}/{sNoun}")
  @Consumes(MediaType.APPLICATION_JSON)
  public String checkThingType(@PathParam("sTerm") String sTerm, @PathParam("sNoun") String sNoun){
    boolean bNoun;
    sNoun = sNoun.toLowerCase();
    if (sNoun.equals("false"))
      bNoun = false;
    else
      bNoun = true;

    JsonObject jResultType = thing.checkThingType(sTerm, bNoun);
    return jResultType.toString();    
  }
  
  
  
  @GET
  @Path("/checkThingLocation/{sTerm}")
  @Consumes(MediaType.APPLICATION_JSON)
  public String checkThingLocation(@PathParam("sTerm") String sTerm){
    mLogger.info("checkThingLocation Bundle requested.");
    JsonObject jResultLocation = thing.checkThingLocation(sTerm);
    return jResultLocation.toString();    
  }
  
  
  public void startService()
  {
    try
    {
      mLogger.info("Component CM REST has started.");
//      thing = new ThingsServiceManagerImpl();
      thing = ThingsServiceManagerImpl.getInstance();
      
      thing.startWordnet();
      
    }
    catch (Exception e)
    {
      mLogger.error("Component CM REST perform operation startService. It has not been executed correctly. Exception: " + e.getMessage() + ".");
    }
  }

  public void closeService()
  {
    try
    {
      mLogger.info("Component CM REST has stopped.");
//      thing = null;
    }
    catch (Exception e)
    {
      mLogger.error("Component TaaS CM perform operation stopped. It has not been executed correctly. Exception: " + e.getMessage() + ".");
    }
  }

//  public void setContext(BundleContext context) {
//    mLogger.info("Setting the taas context");
//  }
//  
//  public void getContext() {
//    mLogger.info("Getting the taas context");
//  }
  
}

