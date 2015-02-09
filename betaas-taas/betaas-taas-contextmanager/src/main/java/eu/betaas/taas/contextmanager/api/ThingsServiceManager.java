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
package eu.betaas.taas.contextmanager.api;

import org.osgi.framework.BundleContext;
import com.google.gson.JsonObject;
import eu.betaas.taas.bigdatamanager.database.service.ThingsData;



/**
 * This interface defines the ContextManager service exposed through DOSGI and
 * visible also from the other gateways.
 * 
 * @author Tecnalia
 */

public interface ThingsServiceManager
{

  //BETAAS INTERFACE
  public String getContextThingServices_local(String sParameter, boolean bEnvironment, String sLocationIdentifier, String sLocationKeyword, String sLatitude, String sLongitude, String sAltitude, String sRadius);
  public String getContextThingServices(String sParameter, String sLocationIdentifier, String sLocationKeyword, String sFloor);
  public String getContextThingServices(String sParameter, String sLocationIdentifier, String sLocationKeyword, String sLatitude, String sLongitude, String sAltitude, String sRadius);
  public String getContextThingServices();
  public JsonObject getMeasurementCM(String sThingServiceName);
  public String getContextualMeasurement(String sThingServiceName);
  
  
  
  //ADAPTATION MODULE INTERFACE
  public void sparqlUpdate(String sSparqlUpdate);
  public String sparqlRemoveDevice(String sDevice);
  public boolean sparqlRemoveAllStatements();
  public boolean sparqlRemoveStatement(String sInstance);
  public boolean checkSubscribeService(String sThingServiceName);
  public JsonObject checkThingLocation(String term);
  public JsonObject checkThingType(String term, boolean type);
  
  
  //TAAS QoS MONITORING MODULE INTERFACE
  public String getMaximumResponseTime(String sThingServiceName);
  public boolean checkAvailability(String sThingServiceName);
  
  
  
  //BLUEPRINT METHODS
  public void startService();
  public void closeService();
  public void setContext(BundleContext context);
  public BundleContext getContext();
  public void setGwId(String gwId);
  public String getGwId();
}