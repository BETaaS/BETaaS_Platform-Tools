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
package eu.betaas.adaptation.contextmanager.onto.classesExt.commonUtils;

import org.apache.log4j.Logger;

//import eu.betaas.adaptation.contextmanager.api.SemanticParserAdaptator;
import eu.betaas.taas.bigdatamanager.core.services.ITaasBigDataManager;
//import eu.betaas.taas.bigdatamanager.database.service.IBigDataDatabaseService;
//import eu.betaas.taas.bigdatamanager.database.service.ThingsData;
import eu.betaas.taas.contextmanager.api.ThingsServiceManager;
import eu.betaas.taas.taasresourcesmanager.api.TaaSResourceManager;
import eu.betaas.taas.qosmanager.api.QoSManagerInternalIF;

public class ConfigBundleOSGiImpl implements ConfigBundleOSGi
{
  // PUBLIC SECTION
  public final static String LOGGER_NAME = "betaas.adaptation"; //En SVN
//  public final static String LOGGER_NAME = "betaas.taas"; // En local

  // PROTECTED SECTION
  private static Logger mLogger = Logger.getLogger(LOGGER_NAME);
//  private IBigDataDatabaseService dbservice;
  private ITaasBigDataManager bdservice;
  private TaaSResourceManager rmservice;
  private ThingsServiceManager cmservice;
  private QoSManagerInternalIF qosservice;
  private static ConfigBundleOSGiImpl thing = null;
  private String mGWID;

  private ConfigBundleOSGiImpl()
  {
    super();
  }

  public static ConfigBundleOSGiImpl getInstance()
  {
    if (thing == null)
    {
      thing = new ConfigBundleOSGiImpl();
    }
    return thing;
  }

  public void readConfigFileOSGi() throws Exception
  {
    try
    {
      mLogger.info("Component Adaptation CM has started.");
    }
    catch (Exception e)
    {
      mLogger
          .error("Component CM perform operation readConfigFileOSGi. It has not been executed correctly. Exception: "
              + e.getMessage() + ".");
    }
  }

  public void closeConfigFileOSGi()
  {
    try
    {
      // this.thing = null;
      mLogger.info("Component Adaptation CM has stopped.");
    }
    catch (Exception e)
    {
      mLogger
          .error("Component CM perform operation closeConfigFileOSGi. It has not been executed correctly. Exception: "
              + e.getMessage() + ".");
    }
  }

//  public void setDbservice(IBigDataDatabaseService dbservice)
//      throws SQLException
//  {
//    this.dbservice = dbservice;
//  }
//
//  public IBigDataDatabaseService getDbservice() throws SQLException
//  {
//    return this.dbservice;
//  }

  public void setBdservice(ITaasBigDataManager bdservice)
  {
    this.bdservice = bdservice;
  }

  public ITaasBigDataManager getBdservice()
  {
    return this.bdservice;
  }

  public void setRmservice(TaaSResourceManager rmservice)
  {
    this.rmservice = rmservice;
  }
  
  public void setQosservice(QoSManagerInternalIF qosservice)
  {
    this.qosservice = qosservice;
  }

  public TaaSResourceManager getRmservice()
  {
    return this.rmservice;
  }

  public void setCmservice(ThingsServiceManager cmservice)
  {
    this.cmservice = cmservice;
  }

  public ThingsServiceManager getCmservice()
  {
    return this.cmservice;
  }

  public QoSManagerInternalIF getQosservice()
  {
    return this.qosservice;
  }
  
  public void setGwId(String gwId)
  {
    gwId = String.format("%02d", Integer.parseInt(gwId));
    mGWID = gwId;
  }

  public String getGwId()
  {
    return mGWID;
  }
}
