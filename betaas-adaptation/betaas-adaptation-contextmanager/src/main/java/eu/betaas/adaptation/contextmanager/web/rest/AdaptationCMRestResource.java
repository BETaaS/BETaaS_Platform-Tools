package eu.betaas.adaptation.contextmanager.web.rest;

import java.util.ArrayList;

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

import eu.betaas.adaptation.contextmanager.api.impl.SemanticParserAdaptatorImpl;
import eu.betaas.taas.bigdatamanager.database.service.ThingsData;


@Produces(MediaType.APPLICATION_JSON)
@WebService
public class AdaptationCMRestResource {

  // PUBLIC SECTION
  public final static String LOGGER_NAME = "betaas.adaptation";

  // PRIVATE SECTION
  private static SemanticParserAdaptatorImpl thing = null;
  
  private static Logger mLogger = Logger.getLogger(LOGGER_NAME);
  
  
  @POST
  @Path("/install/{groupID}/{artID}/{verID}")
  @Consumes("application/xml;charset=UTF-8")
  public Response testPOS(@PathParam("groupID") String groupID, @PathParam("artID") String artID, @PathParam("verID") String verID) {
      mLogger.info("POST Bundle requested mvn:"+groupID+"/"+artID+"/"+verID);
      return Response.ok().entity( "Yes, it works. POST." ).build();
  }
  
  @GET
  @Path("/status/{groupID}/{artID}/{verID}")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response testGET(@PathParam("groupID") String groupID, @PathParam("artID") String artID, @PathParam("verID") String verID) {
      mLogger.info("GET Bundle requested mvn:");
      return Response.ok().entity( "Yes, it works. GET." ).build();
  }
  
  

//  @Path("/publishThingInit/{sConcept}/{sAltLabel}/{sDefinition}")
//  public Response publishThingInit(@PathParam("sConcept") String sConcept, @PathParam("sAltLabel") String sAltLabel, @PathParam("sDefinition") String sDefinition){
//      mLogger.info("publishThingInit Bundle requested mvn:");    
//
//      boolean bResult = thing.addTerm(sConcept, sAltLabel, sDefinition);
//      return Response.ok().entity( "Yes, it works." ).build();
//  }

  @GET
  @Path("/publishThingInit/{oThingsDataList}")
  @Consumes(MediaType.APPLICATION_JSON)
  public String publishThingInit(@PathParam("oThingsDataList") ArrayList<ThingsData> oThingsDataList) throws Exception {
    mLogger.info("publishThingInit Bundle requested.");
    String jsonResult = thing.publishThingInit(oThingsDataList);
    return jsonResult;
  }
  
  
  
  
  
  
  public void startService()
  {
    try
    {
      mLogger.info("Component Adapt CM REST has started.");
      thing = new SemanticParserAdaptatorImpl();
    }
    catch (Exception e)
    {
      mLogger.error("Component Adapt CM REST perform operation startService. It has not been executed correctly. Exception: " + e.getMessage() + ".");
    }
  }

  public void closeService()
  {
    try
    {
      mLogger.info("Component Adapt CM REST has stopped.");
//      thing = null;
    }
    catch (Exception e)
    {
      mLogger
          .error("Component CM perform operation closeService. It has not been executed correctly. Exception: "
              + e.getMessage() + ".");
    }
  }

  public void setContext(BundleContext context) {
    mLogger.info("Setting the taas context");
  }
  
  public void getContext() {
    mLogger.info("Getting the taas context");
  }
  
}
