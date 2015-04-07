//Copyright 2014-2015 Tecnalia.
//
//Licensed under the Apache License, Version 2.0 (the "License");
//you may not use this file except in compliance with the License.
//You may obtain a copy of the License at
//
//       http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.



// BETaaS - Building the Environment for the Things as a Service
//
// Component: Context Manager, Adaptation Module
// Responsible: Tecnalia
package eu.betaas.adaptation.contextmanager.api.impl;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;
import org.apache.log4j.Logger;

import com.google.gson.JsonObject;
import org.geonames.*;

import eu.betaas.adaptation.contextmanager.api.SemanticParserAdaptator;
import eu.betaas.taas.contextmanager.api.ThingsServiceManager;
import eu.betaas.taas.bigdatamanager.database.service.ThingsData;
import eu.betaas.taas.bigdatamanager.core.services.ITaasBigDataManager;
import eu.betaas.adaptation.contextmanager.onto.classesExt.commonUtils.ConfigBundleOSGi;
import eu.betaas.adaptation.contextmanager.onto.classesExt.commonUtils.ConfigBundleOSGiImpl;
import eu.betaas.adaptation.contextmanager.onto.classesExt.commonUtils.ReminderReachableRemove;
import eu.betaas.taas.taasresourcesmanager.api.TaaSResourceManager;
import eu.betaas.taas.qosmanager.api.QoSManagerInternalIF;

public class SemanticParserAdaptatorImpl implements SemanticParserAdaptator
{

  private ITaasBigDataManager bdservice;
  private TaaSResourceManager rmservice;
  static private ThingsServiceManager cmservice;
  static private QoSManagerInternalIF qosservice;
  private ConfigBundleOSGi oConfigOSGi;
  private String sGwIdLocal;
  private static Logger mLogger = Logger.getLogger(ConfigBundleOSGiImpl.LOGGER_NAME);

  private String PUBLIC = "public";
  private String PRIVATE = "private";
  private String INIT = "init";
  private String CHECK = "check";
  private String NORMAL = "normal";
  private String PREFIX_GEONAMES = "http://www.geonames.org/";
  private String PREFIX_BETAAS = "http://www.betaas.eu/2013/betaasOnt";

  String sPhysicalPlace = "";
//  String sNewThingServiceName = "";
  static String sQueryUpdate = "";
  static String sInstanceID = "";
  
  
  
  private double lat;
  private double lng;

  // ////////////////////////////////////////////////////////////////////////////////////
  // FUNCTION INTERFACES
  // ////////////////////////////////////////////////////////////////////////////////////
  // public void translateRealThingsInformation()
  // {
  // }

  public SemanticParserAdaptatorImpl(){
    try
    {
      oConfigOSGi = ConfigBundleOSGiImpl.getInstance(); //OJO
      cmservice = oConfigOSGi.getCmservice();
    }
    catch (SQLException e)
    {
      mLogger.error("Component CM perform operation CONSTRUCTOR. Exception: " + e.getMessage() + ".");
    }
  }
  
  public String publishThing_local(ArrayList<ThingsData> oThingsDataList,
      String sMode) throws Exception
  {
    boolean bResults = true;
    boolean bSubscribe = false;
    JsonObject jResultType = null;
    String sNewThingServiceName = "";
    
    bdservice = oConfigOSGi.getBdservice();
    rmservice = oConfigOSGi.getRmservice();
    qosservice = oConfigOSGi.getQosservice();
    sGwIdLocal = oConfigOSGi.getGwId();

    Date date = new Date();
    Timestamp ts = new Timestamp(date.getTime());
    String sEnvironmentOnto = null;

    try
    {

      ThingsData oThingsData;
      for (int i = 0; i < oThingsDataList.size(); i++)
      {
        oThingsData = oThingsDataList.get(i);
        boolean bOutput = oThingsData.isOutput();
        boolean bDigital = oThingsData.isDigital();
        String sMaximumResponseTime = oThingsData.getMaximumResponseTime();
        String sMemoryStatus = oThingsData.getMemoryStatus();
        String sComputationalCost = oThingsData.getComputationalCost();
        String sBatteryLevel = oThingsData.getBatteryLevel();
        String sBatteryCost = oThingsData.getBatteryCost();
        String sMeasurement = oThingsData.getMeasurement();
        String sProtocol = oThingsData.getProtocol();
        String sDeviceID = oThingsData.getDeviceID();
        String sThingID = oThingsData.getThingId();
        sThingID = oThingsData.getThingId();
        String sType = oThingsData.getType().toLowerCase();
        String sTypeClass = sType.substring(0, 1).toUpperCase()
            + sType.substring(1);
        String sUnit = oThingsData.getUnit().toLowerCase();
        boolean bEnvironment = oThingsData.getEnvironment(); // PUBLIC 1 = true 
                                                             // PRIVATE 0 = false
        if (bEnvironment)
          sEnvironmentOnto = PUBLIC;
        else
          sEnvironmentOnto = PRIVATE;
        String sLatitude = oThingsData.getLatitude().replace(" ", "");
        String sLongitude = oThingsData.getLongitude().replace(" ", "");
        String sAltitude = oThingsData.getAltitude().replace(" ", "");
        String sFloor = oThingsData.getFloor();
        String sLocationKeyword = oThingsData.getLocationKeyword().replace(" ",
            "");
        sLocationKeyword = sLocationKeyword.toLowerCase();
        sLocationKeyword = sLocationKeyword.substring(0, 1).toUpperCase()
            + sLocationKeyword.substring(1);
        String sLocationIdentifier = oThingsData.getLocationIdentifier()
            .replace(" ", "");
        sLocationIdentifier = sLocationIdentifier.toLowerCase();
        if (!sLocationIdentifier.isEmpty())
        {
          sLocationIdentifier = sLocationIdentifier.substring(0, 1)
              .toUpperCase() + sLocationIdentifier.substring(1);
        }
        mLogger
            .info("Component CM perform operation publishThing. Data comming from TA:"
                + "          Output: "
                + oThingsData.isOutput()
                + ".          Digital: "
                + oThingsData.isDigital()
                + ".          MaximumResponseTime: "
                + oThingsData.getMaximumResponseTime()
                + ".          MemoryStatus: "
                + oThingsData.getMemoryStatus()
                + ".          ComputationalCost: "
                + oThingsData.getComputationalCost()
                + ".          BatteryLevel: "
                + oThingsData.getBatteryLevel()
                + ".          BatteryCost: "
                + oThingsData.getBatteryCost()
                + ".          Measurement: "
                + oThingsData.getMeasurement()
                + ".          Protocol: "
                + oThingsData.getProtocol()
                + ".          DeviceID: "
                + oThingsData.getDeviceID()
                + ".          ThingID: "
                + oThingsData.getThingId()
                + ".          Type: "
                + oThingsData.getType()
                + ".          Unit: "
                + oThingsData.getUnit()
                + ".          Environment: "
                + oThingsData.getEnvironment()
                + ".          Latitude: "
                + oThingsData.getLatitude()
                + ".          Longitude: "
                + oThingsData.getLongitude()
                + ".          Altitude: "
                + oThingsData.getAltitude()
                + ".          Floor: "
                + oThingsData.getFloor()
                + ".          LocationKeyword: "
                + oThingsData.getLocationKeyword()
                + ".          LocationIdentifier: "
                + oThingsData.getLocationIdentifier() + ".");

        String sCurrentDateTime = getCurrentDateTime("DTL_GMT", "DTF_DEC");

        checkBlankThingsData(bOutput, bDigital, sMaximumResponseTime,
            sMemoryStatus, sComputationalCost, sBatteryLevel, sBatteryCost,
            sMeasurement, sProtocol, sDeviceID, sThingID, sType, sUnit,
            bEnvironment, sLatitude, sLongitude, sAltitude, sFloor,
            sLocationKeyword, sLocationIdentifier);
        
        mLogger.info("Checking Thing Type families on the ontology for the type: "+ sTypeClass);
        jResultType = cmservice.checkThingType(sType, oThingsData.isOutput()); //TODO
        
        mLogger.info("Checking Location families on the ontology for the locationKeyword: "+ sLocationKeyword);
        JsonObject jResultLocation = cmservice.checkThingLocation(sLocationKeyword); //TODO

        String sPrefixThingService;
        if (bOutput)
          sPrefixThingService = "get";
        else
          sPrefixThingService = "set";

        String sID = sThingID + "_" + sDeviceID;
        String sPointInstance = "point_" + sID;
        String sHWInstance = "hwProperties_" + sID;
        String sSensorInstance = "sensor_" + sID;
        String sObservationInstanceID = "observation_" + sID;
        sInstanceID = sObservationInstanceID;

        String sDeviceInstance = "device_" + sDeviceID;
        String sFloorInstance = "floor_" + sFloor;// "_1" vs "floor_1"
        String sRoomInstance = "_" + sLocationIdentifier.toLowerCase()
            + sLocationKeyword.toLowerCase();
        String sArea = "_" + sLocationIdentifier.toLowerCase()
            + sLocationKeyword.toLowerCase();

        String sThingServiceName = sPrefixThingService + sLocationIdentifier + sLocationKeyword + sTypeClass; // setMainKitchenPresence
        sNewThingServiceName = sThingServiceName + "_" + sDeviceID + "_" + sGwIdLocal;// setMainKitchenPresence_999_01

        if (sMode.equals(CHECK))
        {
          bSubscribe = cmservice.checkSubscribeService(sNewThingServiceName);
        }
        else
          bSubscribe = true;

        if (bSubscribe)
        {
          if (!sMode.equals(INIT))
          {
            String subject = "http://www.betaas.eu/2013/betaasOnt#"+sInstanceID;
             cmservice.sparqlRemoveStatement(subject);
          }

          if (bEnvironment)
          {
            if (!sLatitude.equals(""))
              lat = Double.parseDouble(sLatitude);

            if (!sLongitude.equals(""))
              lng = Double.parseDouble(sLongitude);

            if (!(sLatitude.equals("") || sLongitude.equals("") || sLatitude
                .equals("0.0") && sLongitude.equals("0.0")))
              sPhysicalPlace = getPhysicalPlaceOnGeonames(lat, lng);
          }

          cmservice.addResource(sTypeClass);
          
          sQueryUpdate = ""
              + "PREFIX BETaaS: <http://www.betaas.eu/2013/betaasOnt#> "
              + "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> "
              + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
              + "INSERT DATA { " + "BETaaS:" + sObservationInstanceID
              + " a BETaaS:Observation; " + "BETaaS:observation_result_time \""
              + sCurrentDateTime + "\"^^xsd:dateTime; "
              + "BETaaS:measurement '" + sMeasurement + "'; "
              + "BETaaS:observedProperty BETaaS:" + sType + "; "
              + "BETaaS:hasUnit '" + sUnit + "'; ";
          if (bEnvironment)
          {
            // sQueryUpdate = sQueryUpdate + "BETaaS:hasPoint " + sPointInstance
            // + "/point>; "
            sQueryUpdate = sQueryUpdate + "BETaaS:hasPoint BETaaS:"
                + sPointInstance + "; " + "BETaaS:hasLocation BETaaS:" + sArea
                + "; ";
            if (!sPhysicalPlace.isEmpty())
              sQueryUpdate = sQueryUpdate + "BETaaS:nearTo " + sPhysicalPlace
                  + "; ";
          }
          else
          {
            sQueryUpdate = sQueryUpdate
            // + "BETaaS:hasLocation BETaaS:" + sFloorInstance +"; "
                + "BETaaS:hasLocation BETaaS:" + sRoomInstance + "; ";
          }
          sQueryUpdate = sQueryUpdate + "BETaaS:observedBy BETaaS:"
              + sSensorInstance + ". "
              + "BETaaS:" + sSensorInstance + " a <" + PREFIX_BETAAS + "#" + sTypeClass + "Sensor>; " // OJO
              + "BETaaS:thingID '" + sThingID + "'; "
              + "BETaaS:maximum_response_time '" + sMaximumResponseTime + "'; "
              + "BETaaS:digital '" + bDigital + "'; " + "BETaaS:output '"
              + bOutput + "'; " + "BETaaS:hasHwProperties BETaaS:"
              + sHWInstance + "; " + "BETaaS:onPlatform BETaaS:"
              + sDeviceInstance + "; " + "BETaaS:hasService BETaaS:"
              + sNewThingServiceName + ". ";
          if (bEnvironment)
          {
            sQueryUpdate = sQueryUpdate + "BETaaS:" + sPointInstance
                + " a BETaaS:GeographicalPoint; " + "BETaaS:latitude \'"
                + sLatitude + "'; " + "BETaaS:longitude \'" + sLongitude
                + "'; " + "BETaaS:altitude \'" + sAltitude + "'. ";
            if (!sPhysicalPlace.isEmpty())
            {
              sQueryUpdate = sQueryUpdate + sPhysicalPlace
                  + "a BETaaS:GeographicalPlace; " + "BETaaS:label "
                  + sPhysicalPlace + ". ";
            }
            sQueryUpdate = sQueryUpdate + "BETaaS:" + sArea
                + " a BETaaS:Area; " + "BETaaS:location_keyword '"
                + sLocationKeyword + "'; " + "BETaaS:location_identifier '"
                + sLocationIdentifier + "'. ";
          }
          else
            sQueryUpdate = sQueryUpdate + "BETaaS:" + sFloorInstance
                + " a BETaaS:Floor; " + "BETaaS:level '" + sFloor + "'; "
                + "BETaaS:hasRoom BETaaS:" + sRoomInstance + ". " + "BETaaS:"
                + sRoomInstance + " a BETaaS:Room; "
                + "BETaaS:location_keyword '" + sLocationKeyword + "'; "
                + "BETaaS:location_identifier '" + sLocationIdentifier + "'; "
                + "BETaaS:isInFloor BETaaS:" + sFloorInstance + ". ";

          sQueryUpdate = sQueryUpdate + "BETaaS:" + sHWInstance
              + " a BETaaS:HwProperties; " + "BETaaS:connection_description '"
              + sProtocol + "'; " + "BETaaS:memory_status '" + sMemoryStatus
              + "'; " + "BETaaS:computational_cost '" + sComputationalCost
              + "'; " + "BETaaS:battery_level '" + sBatteryLevel + "'; "
              + "BETaaS:battery_cost '" + sBatteryCost + "'. " + "BETaaS:"
              + sDeviceInstance + " a BETaaS:Platform; " + "BETaaS:deviceID '"
              + sDeviceID + "'. ";
          if (bOutput)
            sQueryUpdate = sQueryUpdate + "BETaaS:" + sNewThingServiceName
                + " a BETaaS:SensorService. ";
          // + "BETaaS:SensorService rdfs:subClassOf* BETaaS:PhysicalService. ";
          else
            sQueryUpdate = sQueryUpdate + "BETaaS:" + sNewThingServiceName
                + " a BETaaS:ActuatorService. ";
          // +
          // "BETaaS:ActuatorService rdfs:subClassOf* BETaaS:PhysicalService. ";
          sQueryUpdate = sQueryUpdate + "BETaaS:" + sNewThingServiceName
              + " BETaaS:environment '" + sEnvironmentOnto + "'. ";
          if (sMode.equals(INIT))
            sQueryUpdate = sQueryUpdate + "BETaaS:" + sNewThingServiceName
                + " BETaaS:subscription 'false'. ";
          sQueryUpdate = sQueryUpdate + "BETaaS:" + sNewThingServiceName
              + " BETaaS:thingserviceID '" + sNewThingServiceName + "'. "
              + " }";

//          String sNameThread = "ontology update";
//          mLogger.debug("Component CM perform operation publishThing, starting "+sNameThread+" thread");
          cmservice.sparqlUpdate(sQueryUpdate);

          // Check for null values
          if (!((oThingsData.getDeviceID() == null) && (oThingsData
              .getThingId() == null)))
          {
            JsonObject thing = new JsonObject();
            thing.addProperty("timestamp", ts.toString());
            thing.addProperty("is_output", oThingsData.isOutput());
            thing.addProperty("is_digital", oThingsData.isDigital());
            thing.addProperty("maximum_response_time",
                oThingsData.getMaximumResponseTime());
            thing.addProperty("memory_status", oThingsData.getMemoryStatus());
            thing.addProperty("computational_cost",
                oThingsData.getComputationalCost());
            thing.addProperty("battery_level", oThingsData.getBatteryLevel());
            thing.addProperty("battery_cost", oThingsData.getBatteryCost());
            thing.addProperty("measurement", oThingsData.getMeasurement());
            thing.addProperty("protocol", oThingsData.getProtocol());
            thing.addProperty("deviceID", oThingsData.getDeviceID());
            thing.addProperty("thingID", oThingsData.getThingId());
            thing.addProperty("type", oThingsData.getType());
            thing.addProperty("unit", oThingsData.getUnit());
            thing.addProperty("environment", oThingsData.getEnvironment());
            thing.addProperty("latitude", oThingsData.getLatitude());
            thing.addProperty("longitude", oThingsData.getLongitude());
            thing.addProperty("altitude", oThingsData.getAltitude());
            thing.addProperty("floor", oThingsData.getFloor());
            thing.addProperty("location_keyword",
                oThingsData.getLocationKeyword());
            thing.addProperty("location_identifier",
                oThingsData.getLocationIdentifier());

            try
            {
              mLogger
                  .debug("Component CM call service ITaasBigDataManager.setThingsBDM. sDeviceID: "
                      + sDeviceID
                      + ", thing: "
                      + thing.toString()
                      + ".");
            bdservice.setThingsBDM(sDeviceID, thing);
            }
            catch (Exception e)
            {
              bResults = false;
              mLogger
                  .error("Component CM perform operation publishThingInit. Exception comming from ITaasBigDataManager.setThingsBDM: "
                      + e.getMessage() + ".");
            }
          }
          else
          {
            bResults = false;
            mLogger
                .error("Component CM perform operation publishThing. NOT call service ITaasBigDataManager.setThingsBDM FUNCTION because some parameters are null!.");
          }
          
          if (sMode.equals(INIT))
          {
            try
            {
              mLogger
                  .debug("Component CM call service TaaSRMservice.registerThingsServices. ThingID: "
                      + sThingID
                      + ", ThingService: "
                      + sNewThingServiceName
                      + ".");
              rmservice.registerThingsServices(sThingID, sNewThingServiceName);
            }
            catch (Exception e)
            {
              bResults = false;
              mLogger
                  .error("Component CM perform operation publishThingInit. Exception comming from TaaSRMservice.registerThingsServices: "
                      + e.getMessage() + ".");
            }
          }
          else
          {
            try
            {
              mLogger
                  .debug("Component CM call service TaaSRMservice.notifyNewMeasurement. ThingID: "
                      + sThingID
                      + ", ThingService: "
                      + sNewThingServiceName
                      + ".");
              rmservice.notifyNewMeasurement(sNewThingServiceName, oThingsData);
            }
            catch (Exception e)
            {
              bResults = false;
              mLogger
                  .error("Component CM perform operation publishThingInit. Exception comming from TaaSRMservice.registerThingsServices: "
                      + e.getMessage() + ".");
            }
          }
          
        }// subscribe
        else
        {
          mLogger
              .error("Component CM perform operation publishThingCheck. NO SUBSCRIPTION: " + sNewThingServiceName);
        }
      }// for
    }
    catch (InterruptedException ie) {
      
    }
    catch (Exception e)
    {
      bResults = false;
      mLogger
          .error("Component CM perform operation publishThingInit. It has not been executed correctly. Exception: "
              + e.getMessage() + ".");
    }
    mLogger.debug("Component CM perform operation publishThingInit. Results: " + bResults);
    if (bResults)
      oConfigOSGi.sendData("The Thing Service "+sNewThingServiceName+" has been published sucessfully!.", "info", "AdaptationCM");
    else
      oConfigOSGi.sendData("The Thing Service "+sNewThingServiceName+" has not been published sucessfully!.", "error", "AdaptationCM");
    return jResultType.toString();
  }

  public boolean subscribe(String sThingServiceName)
  {
    boolean bResult = true;
    try
    {
      sparqlUpdateProperty(sThingServiceName, "subscription", "true");
    }
    catch (Exception e)
    {
      mLogger.error("Component CM perform operation subscribe. It has not been executed correctly.");
      oConfigOSGi.sendData("The Thing Service has been published sucessfully!.", "info", "AdaptationCM");
      return false;
    }

    mLogger.debug("Component CM perform operation subscribe. Results: subscription " + bResult);
    oConfigOSGi.sendData("The Thing Service "+sThingServiceName+" has been subscribed sucessfully!.", "info", "AdaptationCM");
    return bResult;
  }

  public boolean unsubscribe(String sThingServiceName)
  {
    boolean bResult = true;
    try
    {
      sparqlUpdateProperty(sThingServiceName, "subscription", "false");
    }
    catch (Exception e)
    {
      mLogger.error("Component CM perform operation unsubscribe. It has not been executed correctly.");
      oConfigOSGi.sendData("The Thing Service "+sThingServiceName+" has been unsubscribed unsucessfully!.", "info", "AdaptationCM");
      return false;
    }

    mLogger.debug("Component CM perform operation unsubscribe. Results: subscription "+ bResult);
    oConfigOSGi.sendData("The Thing Service "+sThingServiceName+" has been unsubscribed sucessfully!.", "info", "AdaptationCM");
    return bResult;
  }

  public String publishThingInit(ArrayList<ThingsData> oThingsDataList)
      throws Exception
  {
    String json = this.publishThing_local(oThingsDataList, INIT);
    return json;
  }

  public String publishThingCheck(ArrayList<ThingsData> oThingsDataList)
      throws Exception
  {
    String json = this.publishThing_local(oThingsDataList, CHECK);
    return json;
  }

  public String publishThing(ArrayList<ThingsData> oThingsDataList)
      throws Exception
  {
    String json = this.publishThing_local(oThingsDataList, NORMAL);
    return json;
  }

  // Java Client for GeoNames Webservices
  private String getPhysicalPlaceOnGeonames(double lat, double lng)
  {
    String sPlaceName = "";
    String sGeoNameId = "";
    String hostname = "www.geonames.org";
    try
    {
      Process p1 = java.lang.Runtime.getRuntime().exec("ping -c 1 "+ hostname);
      int returnVal = p1.waitFor();
      boolean reachable = (returnVal==0);
      
      if (reachable){
      WebService.setUserName("betaas");

      PostalCodeSearchCriteria postalCodeSearchCriteria = new PostalCodeSearchCriteria();
      postalCodeSearchCriteria.setLatitude(lat);
      postalCodeSearchCriteria.setLongitude(lng);
      List<Toponym> placeNames = WebService.findNearbyPlaceName(lat, lng);
      for (int i = 0; i < placeNames.size(); i++)
      {
        sGeoNameId = String.valueOf(placeNames.get(i).getGeoNameId());
        sPlaceName = "<" + PREFIX_GEONAMES + sGeoNameId + ">";
      }
      }
//      else
//        mLogger.info("[CM] Adaptation Context Manager, Geonames function. Service " + hostname + " unavailable.");
    }
    catch (Exception e)
    {
//      mLogger
//          .error("[CM] Adaptation Context Manager, Geonames function. It has not been executed correctly. Probably there is not Internet connection.");
    }
    return sPlaceName;
  }

  private boolean checkBlankThingsData(boolean bOutput, boolean bDigital,
      String sMaximumResponseTime, String sMemoryStatus,
      String sComputationalCost, String sBatteryLevel, String sBatteryCost,
      String sMeasurement, String sProtocol, String sDeviceID, String sThingID,
      String sType, String sUnit, boolean bEnvironment, String sLatitude,
      String sLongitude, String sAltitude, String sFloor,
      String sLocationKeyword, String sLocationIdentifier)
  {
    boolean bCorrect = true;

    if (sMaximumResponseTime.equals(""))
    {
      bCorrect = false;
      mLogger
          .debug("Component CM perform operation checkBlankThingsData. Data coming from ThingsAdaptor is incompleted. No 'MaximumResponseTime' value.");
    }

    if (sMemoryStatus.equals(""))
    {
      bCorrect = false;
      mLogger
          .debug("Component CM perform operation checkBlankThingsData. Data coming from ThingsAdaptor is incompleted. No 'MemoryStatus' value.");
    }

    if (sComputationalCost.equals(""))
    {
      bCorrect = false;
      mLogger
          .debug("[Component CM perform operation checkBlankThingsData. Data coming from ThingsAdaptor is incompleted. No 'ComputationalCost' value.");
    }

    if (sBatteryLevel.equals(""))
    {
      bCorrect = false;
      mLogger
          .debug("Component CM perform operation checkBlankThingsData. Data coming from ThingsAdaptor is incompleted. No 'BatteryLevel' value.");
    }

    if (sBatteryCost.equals(""))
    {
      bCorrect = false;
      mLogger
          .debug("Component CM perform operation checkBlankThingsData. Data coming from ThingsAdaptor is incompleted. No 'BatteryCost' value.");
    }

    if (sMeasurement.equals(""))
    {
      bCorrect = false;
      mLogger
          .debug("Component CM perform operation checkBlankThingsData. Data coming from ThingsAdaptor is incompleted. No 'Measurement' value.");
    }

    if (sProtocol.equals(""))
    {
      bCorrect = false;
      mLogger
          .debug("Component CM perform operation checkBlankThingsData. Data coming from ThingsAdaptor is incompleted. No 'Protocol' value.");
    }

    if (sDeviceID.equals(""))
    {
      bCorrect = false;
      mLogger
          .debug("Component CM perform operation checkBlankThingsData. Data coming from ThingsAdaptor is incompleted. No 'DeviceID' value.");
    }

    if (sThingID.equals(""))
    {
      bCorrect = false;
      mLogger
          .debug("Component CM perform operation checkBlankThingsData. Data coming from ThingsAdaptor is incompleted. No 'ThingID' value.");
    }

    if (sType.equals(""))
    {
      bCorrect = false;
      mLogger
          .debug("Component CM perform operation checkBlankThingsData. Data coming from ThingsAdaptor is incompleted. No 'Type' value.");
    }

    if (sUnit.equals(""))
    {
      bCorrect = false;
      mLogger
          .debug("Component CM perform operation checkBlankThingsData. Data coming from ThingsAdaptor is incompleted. No 'Unit' value.");
    }

    if (sLocationKeyword.equals(null))
    {
      bCorrect = false;
      mLogger
          .debug("Component CM perform operation checkBlankThingsData. Data coming from ThingsAdaptor is incompleted. No 'LocationKeyword' value.");
    }

    if (sLocationIdentifier.equals(""))
      mLogger
          .debug("Component CM perform operation checkBlankThingsData. Data coming from ThingsAdaptor is incompleted. No 'LocationIdentifier' value. Not mandatory.");

    if (!bEnvironment)
    { // HOME environment
      if (sFloor.equals(""))
      {
        bCorrect = false;
        mLogger
            .debug("Component CM perform operation checkBlankThingsData. Data coming from ThingsAdaptor is incompleted. No 'Floor' value.");
      }
    }
    else
    // CITY environment
    {
      if (sLatitude.equals(""))
      {
        bCorrect = false;
        mLogger
            .debug("Component CM perform operation checkBlankThingsData. Data coming from ThingsAdaptor is incompleted. No 'Latitude' value.");
      }

      if (sLongitude.equals(""))
      {
        bCorrect = false;
        mLogger
            .debug("Component CM perform operation checkBlankThingsData. Data coming from ThingsAdaptor is incompleted. No 'Longitude' value.");
      }

      if (sAltitude.equals(""))
      {
        bCorrect = false;
        mLogger
            .debug("Component CM perform operation checkBlankThingsData. Data coming from ThingsAdaptor is incompleted. No 'Altitude' value.");
      }
    }

    return bCorrect;
  }

public boolean removeThingUnreachable (String sThingName)
{
  boolean bCorrect = true;
  try
  {
    mLogger.debug("Component CM perform operation removeThingUnreachable. Item: "+sThingName+".");
    String sThingServiceName = cmservice.sparqlRemoveDevice(sThingName);

    try{ qosservice.thingRemoved(sThingServiceName);}
    catch (Exception e) { mLogger.error("Component CM perform operation removeThing. It has not been executed correctly the call to qosservice.thingRemoved("+sInstanceID+"). Exception: " + e.getMessage() + ".");}

    List<String> sThingServiceList = new ArrayList<String>(); 
    sThingServiceList.add(sThingServiceName);
    
    try { rmservice.deleteThingServices(sThingServiceList);}
    catch (Exception e) { mLogger.error("Component CM perform operation removeThing. It has not been executed correctly the call to TaaSRM.deleteThingServices("+sThingServiceList.get(0)+"). Exception: " + e.getMessage() + ".");}
    
  }
  catch (Exception e)
  {
    mLogger.error("Component CM perform operation removeThing. It has not been executed correctly the call to QoSservice.thingRemoved or TaaSRM.deleteThingServices. Exception: " + e.getMessage() + ".");
    oConfigOSGi.sendData("The Thing Service "+sThingName+" has been removed unsucessfully!.", "error", "AdaptationCM");
  }
  oConfigOSGi.sendData("The Thing Service "+sThingName+" has been removed sucessfully!.", "info", "AdaptationCM");
  return bCorrect;
}
  
  public boolean removeThing (List<String> sThingList)
  {
    boolean bCorrect = true;
    String sNameThread;
    try
    {
    mLogger.debug("Component CM perform operation removeThing. Items: "+sThingList.size()+".");
    for (int i = 0; i < sThingList.size(); i++)
    {
      String sThingName = sThingList.get(i);
      String sInstanceID = "sensor_"+sThingName+"_"+sThingName;
      String sThingServiceName = cmservice.getThingServiceName(sInstanceID);
      try{ qosservice.unreachable(sThingServiceName);}
      catch (Exception e) { mLogger.error("Component CM perform operation removeThing. It has not been executed correctly the call to qosservice.unreachable("+sThingServiceName+"). The following exception has been returned: " + e.getMessage() + ".");}
      
      List<String> sThingServiceList = new ArrayList<String>(); 
      sThingServiceList.add(sThingServiceName);
//      TODO - Javi aún no lo ha hecho
//      try { rmservice.unreachable(sThingServiceList);}
//      catch (Exception e) { mLogger.error("Component CM perform operation removeThing. It has not been executed correctly the call to TaaSRM.deleteThingServices. Exception: " + e.getMessage() + ".");}
      
      java.util.Date date= new java.util.Date();
      sNameThread = "threadRemove_"+sThingName+"_"+new Timestamp(date.getTime());
      
      mLogger.debug("Component CM perform operation removeThing, starting "+sNameThread+" thread");
      new ReminderReachableRemove(2, sThingName, cmservice); //TODO!!!! 5m
      }
    }
    catch (Exception e)
    {
      mLogger.error("Component CM perform operation removeThing. It has not been executed correctly the call to QoSservice.thingRemoved or TaaSRM.deleteThingServices. Exception: " + e.getMessage() + ".");
    }
    return bCorrect;
  }
  

  public boolean sparqlUpdateProperty(String sThingServiceName,
      String sDataProperty, String sValue)
  {
    boolean bResult = true;
    String sNoValue = "";

    if (sValue.equals("true"))
      sNoValue = "false";
    else
      sNoValue = "true";
    
    try
    {
      cmservice = oConfigOSGi.getCmservice();
      
      String sQueryUpdate = ""
          + "PREFIX BETaaS: <http://www.betaas.eu/2013/betaasOnt#> "
          + "DELETE {BETaaS:" + sThingServiceName + " BETaaS:" + sDataProperty
          + " '" + sNoValue + "' } " + "INSERT  {BETaaS:" + sThingServiceName
          + " BETaaS:" + sDataProperty + " '" + sValue + "'} " + "WHERE { "
          + "} ";
       cmservice.sparqlUpdate(sQueryUpdate);
    }
    catch (Exception e)
    {
      mLogger
          .error("Component CM perform operation subscribe. It has not been executed correctly.");
      return false;
    }

    return bResult;
  }

  public void getRealTimeAdaptedInformation()
  {
    // TODO Auto-generated method stub
  }

  private static String getCurrentDateTime(String sDateTimeLocation,
      String sDateTimeFormatType) throws Exception
  {
    String sCurrentDateTime = "";

    try
    {
      Calendar cCalendar = null;
      if (sDateTimeLocation.equals("DTL_LOCAL"))
      {
        cCalendar = Calendar.getInstance();
      }
      else if (sDateTimeLocation.equals("DTL_GMT"))
      {
        cCalendar = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
      }

      String sDate = "" + cCalendar.get(Calendar.DATE);
      if (sDate.length() == 1)
      {
        sDate = "0" + sDate;
      }

      String sMonth = "" + (cCalendar.get(Calendar.MONTH) + 1);
      if (sMonth.length() == 1)
      {
        sMonth = "0" + sMonth;
      }

      String sYear = (new Integer(cCalendar.get(Calendar.YEAR))).toString();

      String sHourOfDay = "" + (cCalendar.get(Calendar.HOUR_OF_DAY));
      if (sHourOfDay.length() == 1)
      {
        sHourOfDay = "0" + sHourOfDay;
      }

      String sMinute = "" + (cCalendar.get(Calendar.MINUTE));
      if (sMinute.length() == 1)
      {
        sMinute = "0" + sMinute;
      }

      String sSecond = "" + (cCalendar.get(Calendar.SECOND));
      if (sSecond.length() == 1)
      {
        sSecond = "0" + sSecond;
      }

      String sMillisecond = "" + (cCalendar.get(Calendar.MILLISECOND));
      if (sMillisecond.length() == 2)
      {
        sMillisecond = "0" + sMillisecond;
      }
      else if (sMillisecond.length() == 1)
      {
        sMillisecond = "00" + sMillisecond;
      }

      sCurrentDateTime = getFormattedDateTime(sDate, sMonth, sYear, sHourOfDay,
          sMinute, sSecond, sMillisecond, sDateTimeFormatType);
    }
    catch (Exception e)
    {
      mLogger
          .error("[CM] Adaptation Context Manager, sparqlRemoveStatement function. Exception "
              + e.getMessage());
    }

    return sCurrentDateTime;
  }

  public static String getFormattedDateTime(String sDay, String sMonth,
      String sYear, String sHour, String sMin, String sSec, String sMillisec,
      String sDateTimeFormatType) throws Exception
  {
    String sFormattedDT = "";
    try
    {
      String sTmpFormattedDT = "";

      if (sDateTimeFormatType.equals("DTF_DEC"))
      {
        sTmpFormattedDT += sYear + "-";
        sTmpFormattedDT += sMonth + "-";
        sTmpFormattedDT += sDay + "T";
        sTmpFormattedDT += sHour + ":";
        sTmpFormattedDT += sMin + ":";
        sTmpFormattedDT += sSec + ".";
        sTmpFormattedDT += sMillisec;
      }

      sFormattedDT = sTmpFormattedDT;
    }
    catch (Exception e)
    {
      mLogger
          .error("Component CM perform operation getCurrentDateTime. It has not been executed correctly. Exception: "
              + e.getMessage() + ".");
    }

    return sFormattedDT;
  }

  public boolean addWordnetConceptTerm(String sTerm, String sSynsetID, String sDefinition)
  {
    boolean bResult = true;
    bResult = cmservice.addTerm(sTerm, sSynsetID, sDefinition);
    return bResult;
  }
}
