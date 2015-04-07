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
// Component: Context Manager, TaaS Module
// Responsible: Tecnalia
package eu.betaas.taas.contextmanager.api.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

import eu.betaas.taas.bigdatamanager.database.service.IBigDataDatabaseService;
import eu.betaas.taas.bigdatamanager.database.service.ThingsData;
import eu.betaas.taas.contextmanager.api.ThingsServiceManager;
import eu.betaas.taas.contextmanager.onto.classesExt.semantic.data.xml.sparqlResultSet.SparqlResult;
import eu.betaas.taas.contextmanager.onto.classesExt.semantic.data.xml.sparqlResultSet.SparqlResultSet;
import eu.betaas.taas.contextmanager.onto.classesExt.semantic.data.xml.sparqlResultSet.SparqlVariable;
import eu.betaas.taas.contextmanager.onto.classesExt.wordnet.WordNetUtils;
import eu.betaas.taas.contextmanager.onto.core.OntoBetaas;

public class ThingsServiceManagerImpl implements ThingsServiceManager
{
  // PUBLIC SECTION
  public final static String LOGGER_NAME = "betaas.taas";

  // PROTECTED SECTION
  private final static String SYNSETID = "synsetID";
  private final static String DEFINITION = "definition";
  private final static String HOLONYM = "holonym";
  private final static String HOLOHOLONYM = "holoholonym";
  private final static String HYPERHOLONYM = "hyperholonym";
  private final static String HYPERNYM = "hypernym";
  private final static String HYPERHYPERNYM = "hyperhypernym";
  private final static String SYNONYMS = "synonyms";
  private final static String DISAMBIGUATION = "disambiguation";
  private final static String SYNONYM = "synonym";
  private final static String TERM = "term";

  private String lSynonyms = "";
  private String sBroaderTerm = "";
  
  private static Logger mLogger = Logger.getLogger(LOGGER_NAME);

  private IBigDataDatabaseService service;
  private BundleContext mBundleContext;
  private ServiceListener sl;
  private static ThingsServiceManagerImpl thing = null;
  private static OntoBetaas oOntoBetaas = null;
  private WordNetUtils oWordNetUtils = null;

  private String mGWID;

  private String PRESENCE = "presence";
  private String PREFIX_BETAAS = "http://www.betaas.eu/2013/betaasOnt";

  private String LIST = "list";
  private String LIST_EQ = "eq_list";
  private String OPERATOR = "operator";

  private ThingsServiceManagerImpl()
  {
    super();
  }

  public static ThingsServiceManagerImpl getInstance()
  {
    if (thing == null)
    {
      thing = new ThingsServiceManagerImpl();
    }
    return thing;
  }

  public JsonObject getMeasurementCM(String sThingServiceName)
  {
    mLogger.debug("Component CM perform operation getMeasurementCM.");

    SparqlResultSet oSparqlResultSet = null;
    boolean bPropertyLocation = false;
    String sThingValue = "";
    String sAltitude = "";
    String sLatitude = "";
    String sLongtitude = "";
    int i;
    JsonObject joResultValue = new JsonObject();

    try
    {
      String sQuery = ""
          + "PREFIX BETaaS: <http://www.betaas.eu/2013/betaasOnt#> "
          + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
          + "SELECT DISTINCT ?value ?property ?altitude ?latitude ?longitude WHERE { "
          + "?observation a BETaaS:Observation; "
          + "BETaaS:observedProperty ?property; "
          + "BETaaS:measurement ?value; " + "BETaaS:observedBy ?sensor. "
          + "OPTIONAL {?observation BETaaS:hasPoint ?point.} "
          + "?sensor a ?Device; " + "BETaaS:hasService BETaaS:"
          + sThingServiceName + ". " + "?point a ?Point; "
          + "BETaaS:altitude ?altitude; " + "BETaaS:latitude ?latitude; "
          + "BETaaS:longitude ?longitude; " + "}";

      oSparqlResultSet = oOntoBetaas.sparqlQuery(sQuery);

      ArrayList<SparqlResult> sparqlResultsList = new ArrayList<SparqlResult>();
      sparqlResultsList = oSparqlResultSet.getSparqlResultsList();

      if (sparqlResultsList.size() < 1){
        mLogger.warn("Component CM perform operation getMeasurementCM. The ThingService does not exist.");
        return null;
      }

      for (i = 0; i < sparqlResultsList.size(); i++)
      {
        SparqlResult results = (SparqlResult) sparqlResultsList.get(i);

        ArrayList<SparqlVariable> sparqlVariablesList = new ArrayList<SparqlVariable>();
        sparqlVariablesList = results.getSparqlVariablesList();
        for (int y = 0; y < sparqlVariablesList.size(); y++)
        {
          String sVarName = sparqlVariablesList.get(y).getSparqlVariableName();

          if (sVarName.equals("value"))
          {
            String sTmpThingValue = sparqlVariablesList.get(y)
                .getSparqlVariableValue();
            sThingValue = sTmpThingValue
                .substring(sTmpThingValue.indexOf("#") + 1);
          }

          if (sVarName.equals("property"))
          {
            String sTmpThingProperty = sparqlVariablesList.get(y)
                .getSparqlVariableValue();
            String sThingProperty = sTmpThingProperty
                .substring(sTmpThingProperty.indexOf("#") + 1);
            if (sThingProperty.equals("location"))
              bPropertyLocation = true;
          }

          if (sVarName.equals("altitude") && bPropertyLocation)
            sAltitude = sparqlVariablesList.get(y).getSparqlVariableValue();

          if (sVarName.equals("latitude") && bPropertyLocation)
            sLatitude = sparqlVariablesList.get(y).getSparqlVariableValue();

          if (sVarName.equals("longitude") && bPropertyLocation)
            sLongtitude = sparqlVariablesList.get(y).getSparqlVariableValue();
        }
      }
      if (bPropertyLocation)
      {
        joResultValue.addProperty("altitude", sAltitude);
        joResultValue.addProperty("latitude", sLatitude);
        joResultValue.addProperty("longitude", sLongtitude);
      }
      else
        joResultValue.addProperty("value", sThingValue);
    }
    catch (Exception e)
    {
      mLogger
          .error("Component CM perform operation getMeasurementCM. It has not been executed correctly. Exception: "
              + e.getMessage() + ".");
    }
    mLogger.info("Component CM perform operation getMeasurementCM. Results: "
        + sThingServiceName + " Value: " + joResultValue.toString());
    return joResultValue;
  }

  public String getContextualMeasurement(String sThingServiceName)
  {
    mLogger.debug("Component CM perform operation getContextualMeasurement. "
        + "ThingServiceName: " + sThingServiceName + ".");

    ThingsData oThingsData = null;
    SparqlResultSet oSparqlResultSet = null;
    int i;

    try
    {
      String sQuery = ""
          + "PREFIX BETaaS: <http://www.betaas.eu/2013/betaasOnt#> "
          + "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
          + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
          + "SELECT  * WHERE { "
          + "?service rdf:type ?type. "
          + "?type rdfs:subClassOf* BETaaS:PhysicalService. "
          + "?service BETaaS:environment ?environment. "
          + "?service BETaaS:thingserviceID ?thingserviceID. "
          + "FILTER( CONTAINS(?thingserviceID, '" + sThingServiceName + "') ). "

          + "?sensor BETaaS:hasService ?service. "
          + "OPTIONAL{?sensor BETaaS:thingID ?thingID. ?sensor BETaaS:maximum_response_time ?max. ?sensor BETaaS:digital ?digital. ?sensor BETaaS:output ?output.} "
          + "OPTIONAL{?sensor BETaaS:hasHwProperties ?hw. ?hw BETaaS:connection_description ?connection_description. ?hw BETaaS:battery_level ?battery_level. ?hw BETaaS:battery_cost ?battery_cost. ?hw BETaaS:memory_status ?memory_status. ?hw BETaaS:computational_cost ?computational_cost.} "
          + "OPTIONAL{?sensor BETaaS:hasService ?service. ?service BETaaS:environment ?environment.} "

          + "?sensor BETaaS:onPlatform ?platform. "
          + "?platform BETaaS:deviceID ?deviceID. "

          + "?observation BETaaS:observedBy ?sensor. "
          + "OPTIONAL{?observation BETaaS:hasUnit ?unit.} ?observation BETaaS:hasLocation ?place. ?place BETaaS:location_keyword ?location_keyword. ?place BETaaS:location_identifier ?location_identifier. "
          + "OPTIONAL{?place BETaaS:isInFloor ?floor. ?floor BETaaS:level ?level.} OPTIONAL{?observation BETaaS:hasPoint ?point. ?point BETaaS:altitude ?altitude;  BETaaS:latitude ?latitude;      BETaaS:longitude ?longitude.} "
          + "?observation BETaaS:observedProperty ?property. OPTIONAL{?observation BETaaS:measurement ?measurement.} "
          + "}";

      oSparqlResultSet = oOntoBetaas.sparqlQuery(sQuery);

      ArrayList<SparqlResult> sparqlResultsList = new ArrayList<SparqlResult>();
      sparqlResultsList = oSparqlResultSet.getSparqlResultsList();

      if (sparqlResultsList.size() < 1){
        mLogger.error("Component CM perform operation getContextualMeasurement. The ThingService does not exist.");
        return null;
      }
      else{
      for (i = 0; i < sparqlResultsList.size(); i++)
      {
        SparqlResult results = (SparqlResult) sparqlResultsList.get(i);

        ArrayList<SparqlVariable> sparqlVariablesList = new ArrayList<SparqlVariable>();
        sparqlVariablesList = results.getSparqlVariablesList();
        oThingsData = new ThingsData();
        for (int y = 0; y < sparqlVariablesList.size(); y++)
        {
          String sVarName = sparqlVariablesList.get(y).getSparqlVariableName();

          if (sVarName.equals("output"))
          {
            String sThingValue = sparqlVariablesList.get(y)
                .getSparqlVariableValue();
            if (sThingValue.equals("true"))
              oThingsData.setOutput(true);
            else if (sThingValue.equals("false"))
              oThingsData.setOutput(false);
          }
          if (sVarName.equals("digital"))
          {
            String sThingValue = sparqlVariablesList.get(y)
                .getSparqlVariableValue();
            if (sThingValue.equals("true"))
              oThingsData.setDigital(true);
            else if (sThingValue.equals("false"))
              oThingsData.setDigital(false);
          }
          if (sVarName.equals("max"))
          {
            String sThingValue = sparqlVariablesList.get(y)
                .getSparqlVariableValue();
            oThingsData.setMaximumResponseTime(sThingValue);
          }
          if (sVarName.equals("memory_status"))
          {
            String sThingValue = sparqlVariablesList.get(y)
                .getSparqlVariableValue();
            oThingsData.setMemoryStatus(sThingValue);
          }
          if (sVarName.equals("computational_cost"))
          {
            String sThingValue = sparqlVariablesList.get(y)
                .getSparqlVariableValue();
            oThingsData.setComputationalCost(sThingValue);
          }
          if (sVarName.equals("battery_level"))
          {
            String sThingValue = sparqlVariablesList.get(y)
                .getSparqlVariableValue();
            oThingsData.setBatteryLevel(sThingValue);
          }
          if (sVarName.equals("battery_cost"))
          {
            String sThingValue = sparqlVariablesList.get(y)
                .getSparqlVariableValue();
            oThingsData.setBatteryCost(sThingValue);
          }
          if (sVarName.equals("measurement"))
          {
            String sThingValue = sparqlVariablesList.get(y)
                .getSparqlVariableValue();
            oThingsData.setMeasurement(sThingValue);
          }
          if (sVarName.equals("connection_description"))
          {
            String sThingValue = sparqlVariablesList.get(y)
                .getSparqlVariableValue();
            oThingsData.setProtocol(sThingValue);
          }
          if (sVarName.equals("deviceID"))
          {
            String sThingValue = sparqlVariablesList.get(y)
                .getSparqlVariableValue();
            oThingsData.setDeviceID(sThingValue);
          }
          if (sVarName.equals("thingID"))
          {
            String sThingValue = sparqlVariablesList.get(y)
                .getSparqlVariableValue();
            oThingsData.setThingId(sThingValue);
          }
          if (sVarName.equals("property"))
          {
            String sThingValue = sparqlVariablesList.get(y)
                .getSparqlVariableValue();
            sThingValue = sThingValue.substring(sThingValue.indexOf("#") + 1);
            oThingsData.setType(sThingValue);
          }
          if (sVarName.equals("unit"))
          {
            String sThingValue = sparqlVariablesList.get(y)
                .getSparqlVariableValue();
            oThingsData.setUnit(sThingValue);
          }
          if (sVarName.equals("environment"))
          {
            String sThingValue = sparqlVariablesList.get(y)
                .getSparqlVariableValue();
            if (sThingValue.equals("public"))
              oThingsData.setEnvironment(true);
            else if (sThingValue.equals("private"))
              oThingsData.setEnvironment(false);
          }
          if (sVarName.equals("latitude"))
          {
            String sThingValue = sparqlVariablesList.get(y)
                .getSparqlVariableValue();
            oThingsData.setLatitude(sThingValue);
          }
          if (sVarName.equals("longitude"))
          {
            String sThingValue = sparqlVariablesList.get(y)
                .getSparqlVariableValue();
            oThingsData.setLongitude(sThingValue);
          }
          if (sVarName.equals("altitude"))
          {
            String sThingValue = sparqlVariablesList.get(y)
                .getSparqlVariableValue();
            oThingsData.setAltitude(sThingValue);
          }
          if (sVarName.equals("level"))
          {
            String sThingValue = sparqlVariablesList.get(y)
                .getSparqlVariableValue();
            oThingsData.setFloor(sThingValue);
          }
          if (sVarName.equals("location_keyword"))
          {
            String sThingValue = sparqlVariablesList.get(y)
                .getSparqlVariableValue();
            oThingsData.setLocationKeyword(sThingValue);
          }
          if (sVarName.equals("location_identifier"))
          {
            String sThingValue = sparqlVariablesList.get(y)
                .getSparqlVariableValue();
            oThingsData.setLocationIdentifier(sThingValue);
          }
        }
      }
      }
    }
    catch (Exception e)
    {
      mLogger
          .error("Component CM perform operation getContextualMeasurement. It has not been executed correctly. Possible deviceID null value. Exception: "
              + e.getMessage() + ".");
    }
    mLogger
        .info("Component CM perform operation getContextualMeasurement. Results: "
            + sThingServiceName);
    return oThingsData.getJsonRepresentation();

  }

  public String getContextThingServices(String sParameter,
      String sLocationIdentifier, String sLocationKeyword, String sFloor)
  {
    String sResults = "";
    try
    {
      mLogger.debug("Component CM perform operation getContextThingServices. "
          + "Parameter: " + sParameter + ", sLocationIdentifier:"
          + sLocationIdentifier + ", sLocationKeyword: " + sLocationKeyword
          + ", sFloor: " + sFloor + ".");

      if ((sParameter == null) || (sLocationIdentifier == null)
          || (sLocationKeyword == null) || (sFloor == null))
        mLogger
            .error("Component CM perform operation getContextThingServices. No NULL values are expected.");
      else
        sResults = getContextThingServices_searchingGWs(false, sParameter,
            sLocationIdentifier.trim(), sLocationKeyword.trim(), sFloor.trim(),
            "", "", "");
    }
    catch (Exception e)
    {
      mLogger
          .error("Component CM perform operation getContextThingServices. It has not been executed correctly. Exception: "
              + e.getMessage() + ".");
    }
    mLogger
        .info("Component CM perform operation getContextThingServices. Result: "
            + sResults);

    return sResults;
  }

  public String getContextThingServices(String sParameter,
      String sLocationIdentifier, String sLocationKeyword, String sLatitude,
      String sLongitude, String sAltitude, String sRadius)
  {
    String sResults = "";
    try
    {
      mLogger.debug("Component CM perform operation getContextThingServices. "
          + "Parameter: " + sParameter + ", sLocationIdentifier:"
          + sLocationIdentifier + ", sLocationKeyword: " + sLocationKeyword
          + ", sLatitude: " + sLatitude + ", sLongitude: " + sLongitude
          + ", sAltitude: " + sAltitude + ", sRadius: " + sRadius + ".");

      if ((sParameter == null) || (sLocationIdentifier == null)
          || (sLocationKeyword == null) || (sLatitude == null)
          || (sLongitude == null) || (sAltitude == null) || (sRadius == null))
        mLogger
            .error("Component CM perform operation getContextThingServices. No NULL values are expected.");
      else
        sResults = getContextThingServices_searchingGWs(true, sParameter,
            sLocationIdentifier.trim(), sLocationKeyword.trim(), sLatitude,
            sLongitude, sAltitude, sRadius);
    }
    catch (Exception e)
    {
      mLogger
          .error("Component CM perform operation getContextThingServices. It has not been executed correctly. Exception: "
              + e.getMessage() + ".");
    }
    mLogger
        .info("Component CM perform operation getContextThingServices. Result: "
            + sResults);
    return sResults;
  }

  public String getContextThingServices_searchingGWs(boolean bEnvironment,
      String sParameter, String sLocationIdentifier, String sLocationKeyword,
      String sLatitude, String sLongitude, String sAltitude, String sRadius)
  {
    boolean bConcat = false;
    String sjoThingServiceNameList = null;
    String sjoThingServiceNameList_local = null;
    String sjoThingServiceNameList_remote = null;
    JsonObject joThingServiceNameList = new JsonObject();

    try
    {
      String filter = "(&(service.imported=*)(objectClass="
          + ThingsServiceManager.class.getName() + "))";
      ServiceReference[] srl = mBundleContext.getServiceReferences(
          (String) null, filter);

      if (srl != null)
      {
        for (int i = 0; srl != null && i < srl.length; i++)
        {
          mLogger
              .debug("Component CM perform operation getContextThingServices_searchingGWs. How many CM resources?: "
                  + (srl.length + 1) + ".");
          sl.serviceChanged(new ServiceEvent(ServiceEvent.REGISTERED, srl[i]));

          sjoThingServiceNameList_remote = getContextThingServices_remote(
              srl[i], bEnvironment, sParameter, sLocationIdentifier,
              sLocationKeyword, sLatitude, sLongitude, sAltitude, sRadius);

          if (bConcat)
            joThingServiceNameList = setContextThingServices(
                sjoThingServiceNameList_remote, joThingServiceNameList);
          else
          {
            JsonParser jp = new JsonParser();
            JsonObject joThingServiceNameList_remote = (JsonObject) jp
                .parse(sjoThingServiceNameList_remote);
            joThingServiceNameList = joThingServiceNameList_remote;
          }

          mLogger
              .debug("Component CM perform operation getContextThingServices_searchingGWs. The REMOTE THINGSERVICESLIST is: "
                  + sjoThingServiceNameList_remote);
          bConcat = true;
        }
      }
      else
      {
        mLogger
            .debug("Component CM perform operation getContextThingServices_searchingGWs. How many CM resources?: Just one. ME!");
        bConcat = false;
      }
      sjoThingServiceNameList_local = getContextThingServices_local(sParameter,
          bEnvironment, sLocationIdentifier, sLocationKeyword, sLatitude,
          sLongitude, sAltitude, sRadius);

      mLogger
          .debug("Component CM perform operation getContextThingServices_searchingGWs. The LOCAL THINGSERVICESLIST is: "
              + sjoThingServiceNameList_local);
      if (bConcat)
      {
        joThingServiceNameList = setContextThingServices(
            sjoThingServiceNameList_local, joThingServiceNameList);
        mLogger
            .debug("Component CM perform operation getContextThingServices_searchingGWs. The COMPLETE THINGSERVICESLIST is: "
                + joThingServiceNameList.toString());
        sjoThingServiceNameList = joThingServiceNameList.toString();
      }
      else
        sjoThingServiceNameList = sjoThingServiceNameList_local;
    }
    catch (InvalidSyntaxException ise)
    {
      mLogger
          .error("Component CM perform operation getContextThingServices_searchingGWs. It has not been executed correctly. InvalidSyntaxException: "
              + ise.getMessage() + ".");
    }
    catch (Exception e)
    {
      mLogger
          .error("Component CM perform operation getContextThingServices_searchingGWs. It has not been executed correctly. Possible deviceID null value. Exception: "
              + e.getMessage() + ".");
    }

    return sjoThingServiceNameList;
  }

  private JsonObject setContextThingServices(
      String sjoThingServiceNameList_new, JsonObject joThingServiceNameList)
  {
    JsonParser jp = new JsonParser();
    JsonObject joThingServiceNameList_new = (JsonObject) jp
        .parse(sjoThingServiceNameList_new);

    JsonArray jaThingServiceNameList_old = new JsonArray();

    JsonArray jaThingServiceNameList_new_list = (JsonArray) joThingServiceNameList_new
        .get(LIST);
    jaThingServiceNameList_old = joThingServiceNameList.getAsJsonArray(LIST);
    jaThingServiceNameList_old.addAll(jaThingServiceNameList_new_list);

    joThingServiceNameList.add(LIST, jaThingServiceNameList_old);

    JsonArray jaThingServiceNameList_new_listEq = (JsonArray) joThingServiceNameList_new
        .get(LIST_EQ);
    joThingServiceNameList.getAsJsonArray(LIST_EQ).addAll(
        jaThingServiceNameList_new_listEq);

    return joThingServiceNameList;
  }

  private String getContextThingServices_remote(ServiceReference sr,
      boolean bEnvironment, String sParameter, String sLocationIdentifier,
      String sLocationKeyword, String sLatitude, String sLongitude,
      String sAltitude, String sRadius)
  {
    String sjoThingServiceNameList_remote = null;
    try
    {
      ThingsServiceManager taasCMResource = (ThingsServiceManager) mBundleContext
          .getService(sr);
      if (taasCMResource == null)
      {
        mLogger
            .error("Component CM perform operation getContextThingServices_remote. Taas Context Manager resource is null");
        return null;
      }

      String sGwIdRemote = taasCMResource.getGwId();

      String sGwIdLocal = getGwId();
      mLogger
          .debug("Component CM perform operation getContextThingServices_remote. sGwIdRemote: "
              + sGwIdRemote);
      mLogger
          .debug("-Component CM perform operation getContextThingServices_remote. sGwIdLocal: "
              + sGwIdLocal);

      if (sGwIdLocal != sGwIdRemote)
      {
        sjoThingServiceNameList_remote = taasCMResource
            .getContextThingServices_local(sParameter, bEnvironment,
                sLocationIdentifier, sLocationKeyword, sLatitude, sLongitude,
                sAltitude, sRadius);
      }
    }
    catch (Exception e)
    {
      mLogger
          .error("Component CM perform operation getContextThingServices_remote. It has not been executed correctly. Exception: "
              + e.getMessage() + ".");
    }

    return sjoThingServiceNameList_remote;
  }

  public String getContextThingServices_local(String sParameter,
      boolean bEnvironment, String sLocationIdentifier,
      String sLocationKeyword, String sLatitude, String sLongitude,
      String sAltitude, String sRadius)
  {
    String sQuery = "";
    String sResults = "";
    sParameter = sParameter.toLowerCase();
    String sTypeThing = sParameter.substring(0, 1).toUpperCase()
        + sParameter.substring(1);
    sTypeThing = "<" + PREFIX_BETAAS + "#" + sTypeThing + "Sensor>"; //TODO

    sLocationIdentifier = sLocationIdentifier.toLowerCase();
    sLocationIdentifier = sLocationIdentifier.replace(" ", "");
    sLocationKeyword = sLocationKeyword.toLowerCase();
    sLocationKeyword = sLocationKeyword.replace(" ", "");
    String sLocation = sLocationIdentifier + sLocationKeyword;
    
    if (bEnvironment)
    {
      sQuery = "" + "PREFIX BETaaS: <http://www.betaas.eu/2013/betaasOnt#> "
          + "SELECT DISTINCT ?service ?altitude ?longitude ?latitude  WHERE { "
          + "?observation BETaaS:hasPoint ?point. "
          + "?point BETaaS:altitude ?altitude. "
          + "?point BETaaS:longitude ?longitude. "
          + "?point BETaaS:latitude ?latitude. "
          + "?observation BETaaS:observedBy ?sensor. " + "?sensor a "
          + sTypeThing + "." + "?sensor BETaaS:hasService ?service. ";
      // + "?service a BETaaS:ActuatorService. "; 
      // + "?service a BETaaS:SensorService. "; 
      if (sLocationKeyword.isEmpty())
        sQuery = sQuery + "} ";
      else
      {
        sQuery = sQuery + "?service BETaaS:thingserviceID ?thingserviceID. "
            + "FILTER( ";
        if (!((sLocationIdentifier.isEmpty()) || (sLocationIdentifier.equals("null"))))
        {
          sLocationIdentifier = sLocationIdentifier.substring(0, 1)
              .toUpperCase() + sLocationIdentifier.substring(1);
          sQuery = sQuery + "CONTAINS(?thingserviceID, '" + sLocationIdentifier + "' ) && ";
        }

//        WordNetUtils wn = new WordNetUtils();
//        ArrayList<String> oListSynonyms = wn.checkWordnet(sLocationKeyword.toLowerCase());
        ArrayList<String> oListSynonyms = oWordNetUtils.checkWordnet(sLocationKeyword.toLowerCase());
        for (int y = 0; y < oListSynonyms.size(); y++)
        {
          String sLocationKeywordName = oListSynonyms.get(y);
          sLocationKeywordName = sLocationKeywordName.substring(0, 1).toUpperCase() + sLocationKeywordName.substring(1);
          sQuery = sQuery + "CONTAINS(?thingserviceID, '" + sLocationKeywordName + "' )";
          if (!(y == (oListSynonyms.size() - 1)))
            sQuery = sQuery + " || ";
        }
        sQuery = sQuery + " ). }";
      }
    }
    else
    {
      if (sLatitude.equals(""))
      { // getContextThingServices("Temperature","Main","Bathroom","")
        sQuery = "" + "PREFIX BETaaS: <http://www.betaas.eu/2013/betaasOnt#> "
            + "SELECT ?service WHERE { "
            + "  ?observation a BETaaS:Observation;"
            + "   BETaaS:hasLocation BETaaS:_" + sLocation + ";"// _mainbathroom;"
            + "   BETaaS:observedBy ?sensor." + "?sensor a " + sTypeThing + ";"// <http://www.betaas.eu/2013/betaasOnt#TemperatureSensor>;"
            + "   BETaaS:hasService ?service." + "  }";
      }
      else
      // getContextThingServices("Temperature","","","1")
      {
        String sFloor = "floor_" + sLatitude;
        sQuery = "" + " PREFIX BETaaS: <http://www.betaas.eu/2013/betaasOnt#> "
            + " SELECT ?service " + " WHERE { "
            + " ?observation BETaaS:hasLocation ?loc. "
            + " ?loc BETaaS:isInFloor BETaaS:" + sFloor + ". "
            + " ?observation BETaaS:observedBy ?sensor. " + " ?sensor a "
            + sTypeThing + "."// <http://www.betaas.eu/2013/betaasOnt#TemperatureSensor>;"
            + " ?sensor BETaaS:hasService ?service. " + " }";
      }
    }
    sResults = getContextThingServices_query(sParameter, sQuery, sLatitude,
        sLongitude, sAltitude, sRadius);
    return sResults;
  }

  public String getContextThingServices_query(String sParameter, String sQuery,
      String sLatitude, String sLongitude, String sAltitude, String sRadius)
  {
    int i;
    String sThingServiceName = null;
    String sTmpThingServiceName = null;
    String sThingServiceNameEq = null;
    String sTmpThingServiceNameEq = null;
    String sThingType = null;
    String sOperator = null;
    String sAltitudeSensor = null;
    String sLongitudeSensor = null;
    String sLatitudeSensor = null;

    ArrayList<String> CMThingServicesList = new ArrayList<String>();
    HashMap<String, String> hmTmpCMThingServicesList = new HashMap<String, String>();

    JsonObject joThingServiceNameList = new JsonObject();
    JsonArray jaThingServiceNameList = new JsonArray();

    SparqlResultSet oSparqlResultSet = null;
    try
    {
      oSparqlResultSet = oOntoBetaas.sparqlQuery(sQuery);

      ArrayList<SparqlResult> sparqlResultsList = new ArrayList<SparqlResult>();
      sparqlResultsList = oSparqlResultSet.getSparqlResultsList();

      for (i = 0; i < sparqlResultsList.size(); i++)
      {
        SparqlResult results = (SparqlResult) sparqlResultsList.get(i);

        ArrayList<SparqlVariable> sparqlVariablesList = new ArrayList<SparqlVariable>();
        sparqlVariablesList = results.getSparqlVariablesList();

        boolean bServ = false;
        boolean bAlt = false;
        boolean bLong = false;
        boolean bLat = false;
        boolean bRadius = false;

        for (int y = 0; y < sparqlVariablesList.size(); y++)
        {
          String sVarName = sparqlVariablesList.get(y).getSparqlVariableName();
          if (sVarName.equals("service"))
          {
            String sTmpThingType = sparqlVariablesList.get(y)
                .getSparqlVariableValue();
            sThingType = "_"
                + sTmpThingType.substring(sTmpThingType.indexOf("#") + 1);
            bServ = true;
          }
          if (sVarName.equals("altitude"))
          {
            sAltitudeSensor = sparqlVariablesList.get(y)
                .getSparqlVariableValue();
            bAlt = true;
          }
          if (sVarName.equals("longitude"))
          {
            sLongitudeSensor = sparqlVariablesList.get(y)
                .getSparqlVariableValue();
            bLong = true;
          }
          if (sVarName.equals("latitude"))
          {
            sLatitudeSensor = sparqlVariablesList.get(y)
                .getSparqlVariableValue();
            bLat = true;
          }
          if (bAlt && bLong && bLat && !sRadius.isEmpty())
          {
            bRadius = checkDistancePoints(sLatitudeSensor, sLongitudeSensor,
                sLatitude, sLongitude, sRadius);
            if (bRadius)
              bServ = true;
            else
              bServ = false;
          }
        }
        if (bServ)
        {
          String sTmpThingType = sLongitudeSensor + ":" + sLatitudeSensor
              + sThingType;
          CMThingServicesList.add(sTmpThingType);
          sThingType = sThingType.substring(sThingType.indexOf("_") + 1);
          jaThingServiceNameList.add(new JsonPrimitive(sThingType));
        }
      }

      joThingServiceNameList.add(LIST, jaThingServiceNameList);

      // GET eqTHINGSERVICESLIST
      for (i = 0; i < CMThingServicesList.size(); i++)
      {
        hmTmpCMThingServicesList.put(CMThingServicesList.get(i), "NO_EQ");
      }

      JsonArray jaThingServiceNameListEq = new JsonArray();
      for (Entry<String, String> entry : hmTmpCMThingServicesList.entrySet())
      {
        if (entry.getValue().equals("NO_EQ"))
        {
          sTmpThingServiceName = entry.getKey();
          sThingServiceName = sTmpThingServiceName.substring(0,
              sTmpThingServiceName.lastIndexOf("_"));
          JsonArray jaThingServiceNameEq = new JsonArray();

          for (i = 0; i < CMThingServicesList.size(); i++)
          {
            sTmpThingServiceNameEq = CMThingServicesList.get(i);
            sThingServiceNameEq = sTmpThingServiceNameEq.substring(0,
                sTmpThingServiceNameEq.lastIndexOf("_"));
            if (sThingServiceName.equals(sThingServiceNameEq))
            {
              hmTmpCMThingServicesList.put(sTmpThingServiceNameEq, "EQ");
              sTmpThingServiceNameEq = sTmpThingServiceNameEq
                  .substring(sTmpThingServiceNameEq.indexOf("_") + 1);
              jaThingServiceNameEq
                  .add(new JsonPrimitive(sTmpThingServiceNameEq));
            }
          }
          jaThingServiceNameListEq.add(jaThingServiceNameEq);
        }
      }
      joThingServiceNameList.add(LIST_EQ, jaThingServiceNameListEq);
      mLogger
          .debug("Component CM perform operation getContextThingServices. The EQUIVALENT THINGSERVICESLIST is: "
              + jaThingServiceNameListEq.toString());

      // GET OPERATOR
      if (sParameter.equals(PRESENCE))
      {
        sOperator = "OR";
      }
      else
        sOperator = "AVERAGE";

      joThingServiceNameList.addProperty(OPERATOR, sOperator);
    }
    catch (Exception e)
    {
      mLogger
          .error("Component CM perform operation getContextThingServices_query. It has not been executed correctly. Exception: "
              + e.getMessage() + ".");
    }

    mLogger
        .debug("Component CM perform operation getContextThingServices. Result: "
            + joThingServiceNameList.toString());
    return joThingServiceNameList.toString();
  }

  private boolean checkDistancePoints(String sLatitudeSensor,
      String sLongitudeSensor, String sLatitude, String sLongitude,
      String sRadius)
  {
    // Haversine function
    // http://www.xnoccio.com/es/394-implementacion-de-la-formula-haversine-en-java/
    boolean bResults = true;

    int distance = calculateDistanceByHaversineFormula(sLongitudeSensor,
        sLatitudeSensor, sLongitude, sLatitude);

    float fDiameter = Float.parseFloat(sRadius) * 2;
    int iDiameter = Math.round(fDiameter);

    int distancePoints = iDiameter - distance;

    if (distancePoints > 0)
      bResults = true;
    else
      bResults = false;

    return bResults;
  }

  private int calculateDistanceByHaversineFormula(String sLongitudeSensor,
      String sLatitudeSensor, String sLongitude, String sLatitude)
  {
    Double lat1 = Double.parseDouble(sLatitudeSensor);
    Double lon1 = Double.parseDouble(sLongitudeSensor);
    Double lat2 = Double.parseDouble(sLatitude);
    Double lon2 = Double.parseDouble(sLongitude);

    double earthRadius = 6371; // km

    lat1 = Math.toRadians(lat1);
    lon1 = Math.toRadians(lon1);
    lat2 = Math.toRadians(lat2);
    lon2 = Math.toRadians(lon2);

    double dlon = (lon2 - lon1);
    double dlat = (lat2 - lat1);

    double sinlat = Math.sin(dlat / 2);
    double sinlon = Math.sin(dlon / 2);

    double a = (sinlat * sinlat) + Math.cos(lat1) * Math.cos(lat2)
        * (sinlon * sinlon);
    double c = 2 * Math.asin(Math.min(1.0, Math.sqrt(a)));

    double distanceInMeters = earthRadius * c * 1000;

    return (int) distanceInMeters;
  }

  public String getContextThingServices()
  {
    JsonObject joThingServiceNameList = new JsonObject();
    JsonArray jaThingServiceName = new JsonArray();

    SparqlResultSet oSparqlResultSet = null;

    int i;
    mLogger.debug("Component CM perform operation getContextThingServices.");
    try
    {
      String sQuery = ""
          + "PREFIX BETaaS: <http://www.betaas.eu/2013/betaasOnt#> "
          + "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
          + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
          + "SELECT ?service WHERE { " + "?service rdf:type ?type. "
          + "?type rdfs:subClassOf* BETaaS:PhysicalService. " + "}";
      oSparqlResultSet = oOntoBetaas.sparqlQuery(sQuery);

      ArrayList<SparqlResult> sparqlResultsList = new ArrayList<SparqlResult>();
      sparqlResultsList = oSparqlResultSet.getSparqlResultsList();

      for (i = 0; i < sparqlResultsList.size(); i++)
      {
        SparqlResult results = (SparqlResult) sparqlResultsList.get(i);

        ArrayList<SparqlVariable> sparqlVariablesList = new ArrayList<SparqlVariable>();
        sparqlVariablesList = results.getSparqlVariablesList();
        for (int y = 0; y < sparqlVariablesList.size(); y++)
        {
          String sVarName = sparqlVariablesList.get(y).getSparqlVariableName();

          if (sVarName.equals("service"))
          {
            String sTmpThingType = sparqlVariablesList.get(y)
                .getSparqlVariableValue();
            String sThingType = sTmpThingType.substring(sTmpThingType
                .indexOf("#") + 1);
            jaThingServiceName.add(new JsonPrimitive(sThingType));
          }
        }
      }

      joThingServiceNameList.add("list", jaThingServiceName);
    }
    catch (Exception e)
    {
      mLogger
          .error("Component CM perform operation getContextThingServices. It has not been executed correctly. Exception: "
              + e.getMessage() + ".");
    }

    mLogger
        .info("Component CM perform operation getContextThingServices. Results: "
            + joThingServiceNameList.toString());
    return joThingServiceNameList.toString();
  }

  // ADAPTATION MODULE INTERFACE
  public void sparqlUpdate(String sSparqlUpdate)
  {
    try
    {
      oOntoBetaas.sparqlUpdate(sSparqlUpdate);
    }
    catch (Exception e)
    {
      mLogger
          .error("Component CM perform operation sparqlUpdate. It has not been executed correctly. Exception: "
              + e.getMessage() + ".");
    }
  }

  public String sparqlRemoveDevice(String sInstance)
  {
    String sThingService = null;
    try
    {
  sThingService = sparqRemoveStatementThingServices(sInstance);
  int i = sThingService.indexOf("#");
  if (i>0)
    sThingService = sThingService.substring(i+1);
  
      String subject = "http://www.betaas.eu/2013/betaasOnt#"+sInstance;
      sparqlRemoveStatement(subject);
    }
    catch (Exception e)
    {
      mLogger
          .error("Component CM perform operation sparqlUpdate. It has not been executed correctly. Exception: "
              + e.getMessage() + ".");
    }
    return sThingService ;
  }

  private String sparqRemoveStatementThingServices(String sThing)
  {
    SparqlResultSet oSparqlResultSet = null;
    String sThingService = null;
    int i;

    try
    {
      String sQuery = ""
        + "PREFIX BETaaS: <http://www.betaas.eu/2013/betaasOnt#> "
        + "SELECT ?service WHERE { BETaaS:"
        + sThing+" BETaaS:hasService ?service. "
        +"}";
      oSparqlResultSet = oOntoBetaas.sparqlQuery(sQuery);

      ArrayList<SparqlResult> sparqlResultsList = new ArrayList<SparqlResult>();
      sparqlResultsList = oSparqlResultSet.getSparqlResultsList();

      for (i = 0; i < sparqlResultsList.size(); i++)
      {
        SparqlResult results = (SparqlResult) sparqlResultsList.get(i);

        ArrayList<SparqlVariable> sparqlVariablesList = new ArrayList<SparqlVariable>();
        sparqlVariablesList = results.getSparqlVariablesList();

        for (int y = 0; y < sparqlVariablesList.size(); y++)
        {
          String sVarName = sparqlVariablesList.get(y).getSparqlVariableName();

          if (sVarName.equals("service"))
          {
            sThingService = sparqlVariablesList.get(y).getSparqlVariableValue();
            this.sparqlRemoveStatement(sThingService );
          }

      }
    }
    }
    catch (Exception e)
    {
      mLogger
          .error("Component CM perform operation sparqlRemoveStatementCascade. It has not been executed correctly. Exception: "
              + e.getMessage() + ".");
    }
    return sThingService;
  }
  public boolean sparqlRemoveStatement(String sInstance)
  {
    try
    {
      String sQueryUpdate = "DELETE WHERE { <" + sInstance + "> ?p ?o .}";
      sparqlUpdate(sQueryUpdate);
      
      sQueryUpdate = "DELETE WHERE { ?p ?o <" + sInstance + "> .}";
      sparqlUpdate(sQueryUpdate);
    }
    catch (Exception e)
    {
      mLogger.error("Component CM perform operation sparqlRemoveStatement. It has not been executed correctly. Exception: "
              + e.getMessage() + ".");
    }
    return true;
  }

  public boolean sparqlRemoveAllStatements()
  {
    try
    {
      String sQueryUpdate = ""
          + "PREFIX owl: <http://www.w3.org/2002/07/owl#> "
          + "DELETE {?member ?p ?o} " + "WHERE { " + "?class a owl:Class . "
          + "?member a ?class . " + "?member ?p ?o . " + "}";
      sparqlUpdate(sQueryUpdate);
    }
    catch (Exception e)
    {
      mLogger
          .error("Component CM perform operation sparqlRemoveAllStatements."
              + " It has not been executed correctly the call to method sparqlRemoveStatement. Exception: "
              + e.getMessage() + ".");
    }
    return true;
  }

  public boolean checkSubscribeService(String sNewThingServiceName)
  {
    SparqlResultSet oSparqlResultSet = null;
    String sSuscription = "false";
    boolean bResult = false;
    int i;

    try
    {
      String sQuery = ""
          + "PREFIX BETaaS: <http://www.betaas.eu/2013/betaasOnt#> "
          + "SELECT ?subscription WHERE " + "{ "
          + "?service a BETaaS:SensorService; "
          + "BETaaS:subscription ?subscription; "
          + "BETaaS:thingserviceID ?servicename. "
          + "FILTER(CONTAINS(?servicename, '" + sNewThingServiceName + "')). "
          + "}";
      oSparqlResultSet = oOntoBetaas.sparqlQuery(sQuery);

      ArrayList<SparqlResult> sparqlResultsList = new ArrayList<SparqlResult>();
      sparqlResultsList = oSparqlResultSet.getSparqlResultsList();

      for (i = 0; i < sparqlResultsList.size(); i++)
      {
        SparqlResult results = (SparqlResult) sparqlResultsList.get(i);

        ArrayList<SparqlVariable> sparqlVariablesList = new ArrayList<SparqlVariable>();
        sparqlVariablesList = results.getSparqlVariablesList();
        for (int y = 0; y < sparqlVariablesList.size(); y++)
        {
          String sVarName = sparqlVariablesList.get(y).getSparqlVariableName();
          if (sVarName.equals("subscription"))
          {
            sSuscription = sparqlVariablesList.get(y).getSparqlVariableValue();
          }
        }
      }
    }
    catch (Exception e)
    {
      mLogger
          .error("Component CM perform operation checkSubscribeService. It has not been executed correctly. Exception: "
              + e.getMessage() + ".");
    }

    if (sSuscription.equals("true"))
      bResult = true;
    else
      bResult = false;

    return bResult;
  }

  // TAAS QoS MONITORING MODULE INTERFACE
  public String getMaximumResponseTime(String sThingServiceName)
  {
    SparqlResultSet oSparqlResultSet = null;
    String sMaximumResponseTime = "";
    int i;

    try
    {
      String sQuery = ""
          + "PREFIX BETaaS: <http://www.betaas.eu/2013/betaasOnt#> "
          + "SELECT ?max " + "WHERE { " 
          + "?sensor BETaaS:hasService BETaaS:" + sThingServiceName + ". "
          + "?sensor BETaaS:maximum_response_time ?max. "
          + "}";
      oSparqlResultSet = oOntoBetaas.sparqlQuery(sQuery);

      ArrayList<SparqlResult> sparqlResultsList = new ArrayList<SparqlResult>();
      sparqlResultsList = oSparqlResultSet.getSparqlResultsList();

      for (i = 0; i < sparqlResultsList.size(); i++)
      {
        SparqlResult results = (SparqlResult) sparqlResultsList.get(i);

        ArrayList<SparqlVariable> sparqlVariablesList = new ArrayList<SparqlVariable>();
        sparqlVariablesList = results.getSparqlVariablesList();
        for (int y = 0; y < sparqlVariablesList.size(); y++)
        {
          String sVarName = sparqlVariablesList.get(y).getSparqlVariableName();
          if (sVarName.equals("max"))
            sMaximumResponseTime = sparqlVariablesList.get(y)
                .getSparqlVariableValue();
        }
      }
    }
    catch (Exception e)
    {
      mLogger
          .error("Component CM perform operation getMaximumResponseTime. It has not been executed correctly. Exception: "
              + e.getMessage() + ".");
    }
    return sMaximumResponseTime;
  }

  public boolean checkAvailability(String sThingServiceName)
  {
    SparqlResultSet oSparqlResultSet = null;
    String sThingService = "";
    boolean bResults = false;
    int i;

    try
    {
      String sQuery = ""
          + "PREFIX BETaaS: <http://www.betaas.eu/2013/betaasOnt#> "
          + "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
          + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
          + "SELECT ?service "
          + "WHERE { " 
          + "BETaaS:" + sThingServiceName + " rdf:type ?type. "
          + "?type rdfs:subClassOf* BETaaS:PhysicalService. " 
          + "BETaaS:" + sThingServiceName + " BETaaS:thingserviceID ?service. "
          + "}";
      oSparqlResultSet = oOntoBetaas.sparqlQuery(sQuery);

      ArrayList<SparqlResult> sparqlResultsList = new ArrayList<SparqlResult>();
      sparqlResultsList = oSparqlResultSet.getSparqlResultsList();

      for (i = 0; i < sparqlResultsList.size(); i++)
      {
        SparqlResult results = (SparqlResult) sparqlResultsList.get(i);

        ArrayList<SparqlVariable> sparqlVariablesList = new ArrayList<SparqlVariable>();
        sparqlVariablesList = results.getSparqlVariablesList();
        for (int y = 0; y < sparqlVariablesList.size(); y++)
        {
          String sVarName = sparqlVariablesList.get(y).getSparqlVariableName();
          if (sVarName.equals("service"))
          {
            sThingService = sparqlVariablesList.get(y).getSparqlVariableValue();
            if (sThingService.equals(sThingServiceName))
              bResults = true;
          }
        }
      }
    }
    catch (Exception e)
    {
      mLogger
          .error("Component CM perform operation checkAvailability. It has not been executed correctly. Exception: "
              + e.getMessage() + ".");
    }
    return bResults;
  }

  // ZOOKEEPER REGISTRATION
  public void setMyServiceRegistered()
  {
    try
    {
      sl = new ServiceListener()
      {
        public void serviceChanged(ServiceEvent ev)
        {
          ServiceReference sr = ev.getServiceReference();
          switch (ev.getType())
          {
            case ServiceEvent.REGISTERED:
            {
              // mLogger.info("ServiceEvent.REGISTERED, GwId : " +
              // taasCMResource.getGwId()); // comment
            }
              break;
            case ServiceEvent.UNREGISTERING:
            {
              // mLogger.info("ServiceEvent.UNREGISTERING, GwId : " +
              // taasCMResource.getGwId()); // comment
              mBundleContext.ungetService(sr);
              sr = null;
            }
              break;
            default:
              // mLogger.info("ServiceEvent.DEFAULT, GwId : " +
              // taasCMResource.getGwId()); // comment
              break;
          }
        }
      };

      String filter = "(&(service.imported=*)(objectClass="
          + ThingsServiceManager.class.getName() + "))";
      mBundleContext.addServiceListener(sl, filter);
    }
    catch (InvalidSyntaxException ise)
    {
      mLogger
          .error("Component CM perform operation setMyServiceRegistered function. It has not been executed correctly. InvalidSyntaxException: "
              + ise.getMessage());
    }
    catch (Exception e)
    {
      mLogger
          .error("Component CM perform operation setMyServiceRegistered. It has not been executed correctly. Exception: "
              + e.getMessage() + ".");
    }
  }

  
  // /////////////////////////////////////////////////////////////////////////////
  // INFER FAMILIES OF TYPES OF THINGS
  // -
  // SKOS THING TYPES
  // /////////////////////////////////////////////////////////////////////////////
  public JsonObject checkThingType(String term, boolean type) {
    // true sensor
    // false actuator
    boolean bCorrect = true;
    JsonObject joTempResultValue = new JsonObject();
    JsonObject joSynonymO = null;
    JsonObject joSynonymW = null;
    JsonArray jaSynonymsO = new JsonArray();
    JsonArray jaSynonymsW = new JsonArray();

    term = term.toLowerCase();
    mLogger.debug("Term: " + term.toUpperCase() + ".");

    try {
      // 1.- Verify if the term already exists on the ontology
      joTempResultValue = verifyTermOnOntology(term);
      if (!(joTempResultValue == null))
        mLogger.debug("- The term " + term.toUpperCase() + " is on the ontology. ");
      else{
        // The term is not in the ontology
        mLogger.debug("- The term " + term.toUpperCase() + " is NOT on the ontology. ");

        // Get all the synsets related with that term
        JsonArray aSynset = oWordNetUtils.getSynsets(term, type); 

        mLogger.debug("- Different senses for the term "
            + term.toUpperCase() + ": ");
        if (aSynset.size() < 1)
          mLogger.debug("  No Synsets on Wordnet.");

        jaSynonymsO = new JsonArray();
        jaSynonymsW = new JsonArray();
        for (JsonElement sSynset : aSynset) {
          JsonObject joLemma = sSynset.getAsJsonObject();

          // SYNSETID
          String sTotalSynset = joLemma.get(SYNSETID).toString();
          String search4DigitSynset = sTotalSynset.substring(5, 9);
          String search8DigitSynset = sTotalSynset.substring(5, 13);
          bCorrect = verifySynset(search4DigitSynset);

          // SYNONYMS
          String sSynonyms = joLemma.get(SYNONYM).toString()
              .replace("\\\"", "");

          // DEFINITION
          String sDefinition = joLemma.get(DEFINITION).toString()
              .replace("\\\"", "");

          mLogger.debug("  Synset: " + sTotalSynset);
          mLogger.debug("  Synonyms: " + sSynonyms);
          mLogger.debug("  Definition: " + sDefinition);

          // Already exists some synonyms on the ontology
          if (bCorrect) {
            joSynonymO = new JsonObject();
            joSynonymO.addProperty(SYNSETID, search8DigitSynset);
            joSynonymO.addProperty(SYNONYM, sSynonyms);
            joSynonymO.addProperty(DEFINITION, sDefinition);
            mLogger.debug("    YES, on the ontology you can find synomyns to the term "
                    + term.toUpperCase()
                    + " with synset "
                    + sTotalSynset + ".");
            mLogger.debug("    The synonym common pattern is: "
                + search4DigitSynset);
            mLogger.debug("    Synonyms on the ontology: "
                + lSynonyms.toUpperCase());
            jaSynonymsO.add(joSynonymO);
          } else {
            joSynonymW = new JsonObject();
            joSynonymW.addProperty(SYNSETID, search8DigitSynset);
            joSynonymW.addProperty(SYNONYM, sSynonyms);
            joSynonymW.addProperty(DEFINITION, sDefinition);
            mLogger.debug("    NO, on the ontology you can't find synomyns to the term "
                    + term.toUpperCase()
                    + " with synset "
                    + sTotalSynset
                    + ". The synonym common pattern is: "
                    + search4DigitSynset);
            jaSynonymsW.add(joSynonymW);
          }
          mLogger.debug("");
        }

        joTempResultValue = new JsonObject();
        joTempResultValue.add(TERM, new JsonPrimitive(term));

        // NO, on the ontology you can't find synomyns to the term
        if (jaSynonymsO.size() < 1) {
          joTempResultValue.add(DISAMBIGUATION, new JsonPrimitive(
              true));
          joTempResultValue.add(SYNONYMS, jaSynonymsW);
        } else {
          // YES, on the ontology you can find synomyns to the term
          if (jaSynonymsO.size() > 1) {
            joTempResultValue.add(DISAMBIGUATION,
                new JsonPrimitive(true));
            joTempResultValue.add(SYNONYMS, jaSynonymsO);
          } else if (jaSynonymsO.size() == 1) {
            joTempResultValue.add(DISAMBIGUATION,
                new JsonPrimitive(false));
            joTempResultValue.add(SYNONYMS, jaSynonymsO);

            JsonObject joTempResult = jaSynonymsO.get(0)
                .getAsJsonObject();
            addThingType(sBroaderTerm, term,
                joTempResult.get(SYNSETID).toString(),
                joTempResult.get(DEFINITION).toString());
          }
        }
      }

      mLogger.info("- Sending JSON to disambiguate to the TA.");
      mLogger.info("  JSON: " + joTempResultValue.toString());
      
    } catch (Exception e) {
      mLogger.error("CheckThingType " + e.getMessage() + " "+ e.getLocalizedMessage() + " " + e.getCause());
    }
    return joTempResultValue;
  }

  // /////////////////////////////////////////////////////////////////////////////
  // INFER RELATIONS AMONG LOCATIONS OF THINGS
  // -
  // SKOS LOCATION
  // /////////////////////////////////////////////////////////////////////////////

  public JsonObject checkThingLocation(String term) {
    // true:sensor
    // false:actuator
    boolean bCorrect = true;
    boolean firstSenseOnly = false;
    
    JsonObject oTerm = null;
    JsonObject joTempResultValue = null;
    JsonObject joSynonymO = null;
    JsonObject joSynonymW = null;
    JsonArray jaSynonymsO = new JsonArray();
    JsonArray jaSynonymsW = new JsonArray();

    term = term.toLowerCase();
    mLogger.debug("Term: " + term.toUpperCase() + ".");

    try {
      // 1.- Verify if the term already exists on the ontology
      joTempResultValue = verifyTermOnOntology(term);
      if (!(joTempResultValue == null))
        mLogger.debug("- The term " + term.toUpperCase() + " is on the ontology. ");
      else{
        // The term is not in the ontology
        mLogger.debug("- The term " + term.toUpperCase() + " is NOT on the ontology. ");
        
        // 2.- Get all the synsets related with that term
        JsonArray aSynset = oWordNetUtils.getSynsets(term, true);

        mLogger.debug("- Different senses for the term " + term.toUpperCase() + ": ");
        if (aSynset.size() < 1)
          mLogger.debug("  No Synsets on Wordnet.");

        jaSynonymsO = new JsonArray();
        jaSynonymsW = new JsonArray();
        for (JsonElement sSynset : aSynset) {
          JsonObject joLemma = sSynset.getAsJsonObject();

          // SYNSETID
          String sTotalSynset = joLemma.get(SYNSETID).toString();
          String search4DigitSynset = sTotalSynset.substring(5, 9);
          String search8DigitSynset = sTotalSynset.substring(5, 13);
          // 3.- Check synonyms on the ontology
          bCorrect = verifySynset(search4DigitSynset);
          mLogger.debug("  Synset: " + sTotalSynset);

          // SYNONYMS
          String sSynonyms = joLemma.get(SYNONYM).toString()
              .replace("\\\"", "");
          mLogger.debug("  Synonyms: " + sSynonyms);

          // DEFINITION
          String sDefinition = joLemma.get(DEFINITION).toString()
              .replace("\\\"", "");
          mLogger.debug("  Definition: " + sDefinition);

          // Already exists some synonyms on the ontology
          if (bCorrect) {
            joSynonymO = new JsonObject();
            joSynonymO.addProperty(SYNSETID, search8DigitSynset);
            joSynonymO.addProperty(SYNONYM, sSynonyms);
            joSynonymO.addProperty(DEFINITION, sDefinition);
            mLogger.debug("    YES, on the ontology you can find synomyns to the term "
                    + term.toUpperCase()
                    + " with synset "
                    + sTotalSynset + ".");
            mLogger.debug("    The synonym common pattern is: " + search4DigitSynset);
            mLogger.debug("    Synonyms on the ontology: " + lSynonyms.toUpperCase());
            jaSynonymsO.add(joSynonymO);
          } else {
            joSynonymW = new JsonObject();
            joSynonymW.addProperty(SYNSETID, search8DigitSynset);
            joSynonymW.addProperty(SYNONYM, sSynonyms);
            joSynonymW.addProperty(DEFINITION, sDefinition);
            mLogger.debug("    NO, on the ontology you can't find synomyns to the term "
                    + term.toUpperCase()
                    + " with synset "
                    + sTotalSynset
                    + ". The synonym common pattern is: "
                    + search4DigitSynset);
            jaSynonymsW.add(joSynonymW);

            // HOLONYMS
            JsonArray aHolonym = joLemma.getAsJsonArray(HOLONYM);
            if (aHolonym == null) {//
              mLogger.debug("    NO, there is not holonyms for the term "+ term.toUpperCase()+ " on WORDNET.");

              // HYPERNYMS
              JsonArray aHypernym = joLemma.getAsJsonArray(HYPERNYM);
              if (aHypernym == null)
                mLogger.debug("    NO, there is not hypernyms for the term "+ term.toUpperCase()+ " on WORDNET.");
              else {
                mLogger.debug("    YES, there are hypernyms for the term "+ term.toUpperCase()+ " on WORDNET. Hypernyms: "+ aHypernym.toString().toUpperCase());
                for (JsonElement eHypernym : aHypernym) {
                  JsonObject jobject = eHypernym.getAsJsonObject();
                  String sHypernym = jobject.get(TERM).toString().replace("\"", "");

                  oTerm = verifyTermOnOntology(sHypernym);
                  if (!(oTerm == null)) {
                    mLogger.debug("      YES, the term "+ sHypernym.toString()+ " is on the ONTOLOGY. Hypernyms: "+ aHypernym.toString().toUpperCase());
                    mLogger.debug("      Add term "+ term.toUpperCase()+ " on the ONTOLOGY. Term related with the hypernym "+ sHypernym.toUpperCase());
                    this.addThingType(sHypernym, term, search8DigitSynset, sDefinition); 
                    firstSenseOnly = true;
                    joTempResultValue = oTerm;
                    break;
                  } else{
                    mLogger.debug("      NO, the term "+ sHypernym.toUpperCase()+ " is not on the ONTOLOGY.");
                    
                    // HYPERHYPERNYMS
                    JsonArray aHyperHypernym = joLemma.getAsJsonArray(HYPERHYPERNYM);
                    if (aHyperHypernym == null)
                      mLogger.debug("      NO, there is not hyperhypernyms for the term "+ sHypernym.toUpperCase()+ " on WORDNET.");
                    else {
                      mLogger.debug("      YES, there are hyperhypernyms for the term "+ sHypernym.toUpperCase()+ " on WORDNET. Hyperhypernyms: "+ aHyperHypernym.toString().toUpperCase());
                      for (JsonElement eHyperHypernym : aHyperHypernym) {
                        JsonObject jobject2 = eHyperHypernym.getAsJsonObject();
                        String sHyperHypernym = jobject2.get(TERM).toString().replace("\"", "");

                        oTerm = verifyTermOnOntology(sHyperHypernym);
                        if (!(oTerm == null)) {
                          mLogger.debug("          YES, the term "+ sHyperHypernym.toString()+ " is on the ONTOLOGY. HyperHypernyms: "+ aHyperHypernym.toString().toUpperCase());
                          mLogger.debug("          Add term "+ term.toUpperCase()+ " on the ONTOLOGY. Term related with the hyperhypernym "+ sHyperHypernym.toUpperCase());
                          this.addThingType(sHyperHypernym, term, search8DigitSynset, sDefinition);
                          firstSenseOnly = true;
                          joTempResultValue = oTerm;
                          break;
                        } else
                          mLogger.debug("          NO, the term "+ sHyperHypernym.toUpperCase()+ " is not on the ONTOLOGY.");
                      }
                    }

                  }
                }
              }

              

            } else {
              mLogger.debug("    YES, there are holonyms for the term "+ term.toUpperCase()+ " on WORDNET. Holonyms: "+ aHolonym.toString().toUpperCase());
              for (JsonElement eHolonym : aHolonym) {
                JsonObject jobject = eHolonym.getAsJsonObject();
                String sHolonym = jobject.get(TERM).toString().replace("\"", "");

                oTerm = verifyTermOnOntology(sHolonym);
                if (!(oTerm == null)) {
                  mLogger.debug("      YES, the term "+ sHolonym.toUpperCase()+ " is on the ONTOLOGY. Holonyms: "+ aHolonym.toString().toUpperCase());
                  mLogger.debug("      Add term "+ term.toUpperCase()+ " on the ONTOLOGY. Term related with the holonym "+ sHolonym.toUpperCase());
                  this.addThingType(sHolonym, term, search8DigitSynset, sDefinition);
                  firstSenseOnly = true;
                  joTempResultValue = oTerm;
                  break;
                } else{
                  mLogger.debug("      NO, the term "+ sHolonym.toUpperCase()+ " is not on the ONTOLOGY.");
                
                  
                  // HYPERHOLONYMS
                  JsonArray aHyperHolonym = joLemma.getAsJsonArray(HYPERHOLONYM);
                  if (aHyperHolonym == null) {//
                    mLogger.debug("    NO, there is not hyperholonyms for the term "+ term.toUpperCase()+ " on WORDNET.");
                  } else {
                    mLogger.debug("    YES, there are hyperholonyms for the term "+ term.toUpperCase()+ " on WORDNET. Hyperholonyms: "+ aHyperHolonym.toString().toUpperCase());
                    for (JsonElement eHyperHolonym : aHyperHolonym) {
                      JsonObject jobject1 = eHyperHolonym.getAsJsonObject();
                      String sHyperHolonym = jobject1.get(TERM).toString().replace("\"", "");

                      oTerm = verifyTermOnOntology(sHyperHolonym);
                      if (!(oTerm == null)) {
                        mLogger.debug("      YES, the term "+ sHyperHolonym.toUpperCase()+ " is on the ONTOLOGY. HyperHolonyms: "+ aHyperHolonym.toString().toUpperCase());
                        mLogger.debug("      Add term "+ term.toUpperCase()+ " on the ONTOLOGY. Term related with the hyperholonym "+ sHyperHolonym.toUpperCase());
                        this.addThingType(sHyperHolonym, term, search8DigitSynset, sDefinition);
                        firstSenseOnly = true;
                        joTempResultValue = oTerm;
                        break;
                      } else
                        mLogger.debug("      NO, the term "+ sHyperHolonym.toUpperCase()+ " is not on the ONTOLOGY.");
                    }
                  }
                  
                  
                }
              }
            }

//
//            // HOLOHOLONYMS
//            JsonArray aHoloHolonym = joLemma.getAsJsonArray(HOLOHOLONYM);
//            if (aHoloHolonym == null) {//
//              mLogger.info("    NO, there is not holoholonyms for the term "+ term.toUpperCase()+ " on WORDNET.");
//              // //// HYPERNYMS 
//
//            } else {
//              mLogger.info("    YES, there are holoholonyms for the term "+ term.toUpperCase()+ " on WORDNET. Holonyms: "+ aHoloHolonym.toString().toUpperCase());
//              for (JsonElement eHoloHolonym : aHoloHolonym) {
//                JsonObject jobject = eHoloHolonym.getAsJsonObject();
//                String sHoloHolonym = jobject.get(TERM).toString().replace("\"", "");
//
//                JsonObject oTerm = verifyTermOnOntology(sHoloHolonym);
//                if (!(oTerm == null)) {
//                  mLogger.info("    YES, the term "+ sHoloHolonym.toUpperCase()+ " is on the ONTOLOGY. Holonyms: "+ aHoloHolonym.toString().toUpperCase());
//                  mLogger.info("    Add term "+ term.toUpperCase()+ " on the ONTOLOGY. Term related with the holonym "+ sHoloHolonym.toUpperCase());
//                } else
//                  mLogger.info("      NO, the term "+ sHoloHolonym.toUpperCase()+ " is not on the ONTOLOGY.");
//              }
//            }
            
          }
          mLogger.debug("");
        }
        

        if (!firstSenseOnly){
          joTempResultValue = new JsonObject();
          joTempResultValue.add(TERM, new JsonPrimitive(term));

          // NO, on the ontology you can't find synomyns to the term
          if (jaSynonymsO.size() < 1) {
            if (jaSynonymsW.size() == 1){
              joTempResultValue.add(DISAMBIGUATION, new JsonPrimitive(false));
              mLogger.debug("      Add term "+ term.toUpperCase()+ " on the ONTOLOGY. ");
//              this.addThingType(null, term, search8DigitSynset, sDefinition);
            }
            else 
              joTempResultValue.add(DISAMBIGUATION, new JsonPrimitive(true));
            joTempResultValue.add(SYNONYMS, jaSynonymsW);
          } else {
            // YES, on the ontology you can find synomyns to the term
            if (jaSynonymsO.size() > 1) {
              joTempResultValue.add(DISAMBIGUATION,new JsonPrimitive(true));
              joTempResultValue.add(SYNONYMS, jaSynonymsO);
            } else if (jaSynonymsO.size() == 1) {
              joTempResultValue.add(DISAMBIGUATION,new JsonPrimitive(false));
              joTempResultValue.add(SYNONYMS, jaSynonymsO);
              
              JsonObject joTempResult = jaSynonymsO.get(0).getAsJsonObject();
              addThingType(sBroaderTerm, term,joTempResult.get(SYNSETID).toString(),joTempResult.get(DEFINITION).toString());
            }
          }
        }
      }
      
      mLogger.info("- Sending JSON to disambiguate to the TA.");    
      mLogger.info("  JSON: " + joTempResultValue.toString());
    } catch (Exception e) {
      mLogger.error("CheckThingLocation " + e.getMessage() + " "  + e.getLocalizedMessage() + " " + e.getCause());
    }
    return joTempResultValue;
  }

  public void addResource(String sConcept){
    oOntoBetaas.addResource(sConcept);
  }
  
  private void addThingType(String sBroaderConcept, String sConcept, String sAltLabel, String sDefinition) {
    try {
      int i = sBroaderConcept.indexOf("#");
      if (i>0)
        sBroaderConcept = sBroaderConcept.substring(i+1);
      
      mLogger.info("- Add Resource. BroaderConcept: " + sBroaderConcept.substring( sBroaderConcept.indexOf("#") + 1).toUpperCase()
          + ", Concept: " + sConcept.toUpperCase() + ", ID: "
          + sAltLabel + ", Definition: " + sDefinition + ".");
      oOntoBetaas.addResource(sBroaderConcept, sConcept, sAltLabel, sDefinition);
    } catch (Exception e) {
      mLogger.error("AddThingType CM " + e.getMessage() + " " + e.getLocalizedMessage() + " " + e.getCause());
    }
  }

  public void addTerm(String sConcept, String sAltLabel, String sDefinition) {
    try {
      mLogger.info("- Add Resource.  Concept: " + sConcept.toUpperCase() + ", ID: " + sAltLabel + ", Definition: " + sDefinition + ".");
      oOntoBetaas.addResource(null, sConcept, sAltLabel, sDefinition);
    } catch (Exception e) {
      mLogger.error("AddThingType " + e.getMessage() + " " + e.getLocalizedMessage() + " " + e.getCause());
    }
  }

  
  private boolean verifySynset(String altLabel) {
    boolean bCorrect = true;
    SparqlResultSet oSparqlResultSet = null;
    try {
      String sQuery = ""
          + "PREFIX owl: <http://www.w3.org/2002/07/owl#> "
          + "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
          + "PREFIX skos: <http://www.w3.org/2004/02/skos/core#> "
          + "SELECT  DISTINCT * WHERE { "
          + "?broaderTerm rdf:type owl:NamedIndividual. "
          + " OPTIONAL{ ?broaderTerm skos:definition ?definition.} "
          + " OPTIONAL{ ?broaderTerm skos:altLabel ?altLabel.} "
          + " OPTIONAL{?broaderTerm skos:narrower ?term.} "
          + " FILTER strstarts(?altLabel,'" + altLabel + "') "
          + "}";

      oSparqlResultSet = oOntoBetaas.sparqlQuery(sQuery);

      ArrayList<SparqlResult> sparqlResultsList = new ArrayList<SparqlResult>();
      sparqlResultsList = oSparqlResultSet.getSparqlResultsList();

      if (sparqlResultsList.size() < 1) {
        bCorrect = false;
      } else {
        bCorrect = true;
        lSynonyms = "";
        for (int z = 0; z < sparqlResultsList.size(); z++) {
          SparqlResult results = (SparqlResult) sparqlResultsList.get(z);

          ArrayList<SparqlVariable> sparqlVariablesList = new ArrayList<SparqlVariable>();
          sparqlVariablesList = results.getSparqlVariablesList();

          for (int y = 0; y < sparqlVariablesList.size(); y++) {
            String sVarName = sparqlVariablesList.get(y)
                .getSparqlVariableName();
            String sVarValue = sparqlVariablesList.get(y)
                .getSparqlVariableValue();
            
//            if (sVarName.equals("term")) {
//              String sTerm = sVarValue.substring(sVarValue.lastIndexOf("#") + 1);
//              lSynonyms = lSynonyms + sTerm + " ";
//            }

            if (sVarName.equals("altLabel")) {
              lSynonyms = lSynonyms + "" + sVarValue + " ";
            }
            
            if (sVarName.equals("broaderTerm")) {
              int i = sVarValue.lastIndexOf("#");
              if (i>0)
                sVarValue = sVarValue.substring( i + 1);
//TODO        Synonyms on the ontology: 05018103 LUMINOSITY - 05018785 LUMINESCENCE - 05018103 BRIGHTNESS - 05018103 LIGHT - 05018103 LIGHT - 05018103 LIGHT - 05018103 LIGHT - 
              lSynonyms = lSynonyms + "" + sVarValue + " - ";
              sBroaderTerm = sVarValue;
            }
          }
        }
      }
    } catch (Exception e) {
      mLogger.error("Component CM perform operation verifySynset. It has not been executed correctly. Exception: " +e.getMessage());
    }
    return bCorrect;
  }


  private JsonObject verifyTermOnOntology(String term) 
  {
    boolean bCorrect = true;
    JsonObject joTempResultValue = new JsonObject();
    JsonArray jaSynonyms = null;
    JsonObject joSynonym = null;
    SparqlResultSet oSparqlResultSet = null;
    String synsetID = null;
    String description = null;

    try {
      String sQuery = ""
          + "PREFIX owl: <http://www.w3.org/2002/07/owl#> "
          + "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
          + "PREFIX skos: <http://www.w3.org/2004/02/skos/core#> "
          + "SELECT  DISTINCT * " + "WHERE " + "{ "
          + "?term rdf:type owl:NamedIndividual; "
          + "   skos:prefLabel ?label; "
          + "   skos:definition ?definition; "
          + "   skos:altLabel ?altLabel. "
          + " FILTER strends(?label,'" + term + "') " + "}";
      oSparqlResultSet = oOntoBetaas.sparqlQuery(sQuery);

      ArrayList<SparqlResult> sparqlResultsList = new ArrayList<SparqlResult>();
      sparqlResultsList = oSparqlResultSet.getSparqlResultsList();

      if (sparqlResultsList.size() < 1) {
        bCorrect = false;
        joTempResultValue = null;
      } else {
        bCorrect = true;
        SparqlResult results = (SparqlResult) sparqlResultsList.get(0);// OJO

        ArrayList<SparqlVariable> sparqlVariablesList = new ArrayList<SparqlVariable>();
        sparqlVariablesList = results.getSparqlVariablesList();

        jaSynonyms = new JsonArray();
        joSynonym = new JsonObject();
        for (int y = 0; y < sparqlVariablesList.size(); y++) {
          String sVarName = sparqlVariablesList.get(y)
              .getSparqlVariableName();

          if (sVarName.equals("altLabel")) {
            synsetID = sparqlVariablesList.get(y)
                .getSparqlVariableValue();
            joSynonym.addProperty(SYNSETID, synsetID);
          }

          if (sVarName.equals(DEFINITION)) {
            description = sparqlVariablesList.get(y)
                .getSparqlVariableValue();
            joSynonym.addProperty(DEFINITION, description);
          }
        }
        jaSynonyms.add(joSynonym);

        joTempResultValue.add(TERM, new JsonPrimitive(term));
        joTempResultValue.add(DISAMBIGUATION, new JsonPrimitive(false));
        joTempResultValue.add(SYNONYMS, jaSynonyms);
      }
    } catch (Exception e) {
      mLogger.error("Component CM perform operation verifyTermOnOntology. It has not been executed correctly. Exception: " +e.getMessage());
    }
    return joTempResultValue;
  }


  // BLUEPRINT METHODS
  public void startService()
  {
    try
    {
      mLogger.info("Component CM has started.");

      oOntoBetaas = new OntoBetaas();
      boolean bCorrect = oOntoBetaas.connectToRepository();
      if (bCorrect == false)
      {
        mLogger
            .error("Component CM perform operation startService. It has not been executed correctly the call to method OntoBetaas.init(...).");
      }
      
      oWordNetUtils = new WordNetUtils();
      bCorrect = oWordNetUtils.init();
      if (bCorrect == false)
      {
        mLogger
            .error("Component CM perform operation startService. It has not been executed correctly the call to method WordNetUtils.init(...).");
      }
      oWordNetUtils.loadDictionary();
      
      setMyServiceRegistered();
    }
    catch (Exception e)
    {
      mLogger
          .error("Component CM perform operation startService. It has not been executed correctly. Exception: "
              + e.getMessage() + ".");
    }
  }

  public void closeService()
  {
    try
    {
      oOntoBetaas.exportOwl();
      oOntoBetaas.close();
      oOntoBetaas = null;
      mBundleContext.removeServiceListener(sl);
      this.sl = null;
      mLogger.info("Component CM has stopped.");
    }
    catch (Exception e)
    {
      mLogger
          .error("[CM] TaaS Context Manager, closeService function. It has not been executed correctly. Exception: "
              + e.getMessage() + ".");
    }
  }

  public void setService(IBigDataDatabaseService service)
  {
    this.service = service;
  }

  public IBigDataDatabaseService getService()
  {
    return this.service;
  }

  public void setContext(BundleContext context)
  {
    mBundleContext = context;
  }

  public BundleContext getContext()
  {
    return mBundleContext;
  }

  public void setGwId(String gwId)
  {
    // If the gwID need to have a concrete number of digits, use:
    // gwId = String.format("%02d", Integer.parseInt(gwId));
    mGWID = gwId;
  }

  public String getGwId()
  {
    return mGWID;
  }
  
}

