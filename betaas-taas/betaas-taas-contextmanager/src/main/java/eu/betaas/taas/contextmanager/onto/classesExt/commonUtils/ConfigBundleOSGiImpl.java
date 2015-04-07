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



//BETaaS - Building the Environment for the Things as a Service
//
//Component: Thing Service Manager
//Responsible: Tecnalia
package eu.betaas.taas.contextmanager.onto.classesExt.commonUtils;

import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import eu.betaas.rabbitmq.publisher.interfaces.Publisher;
import eu.betaas.taas.bigdatamanager.database.service.IBigDataDatabaseService;
import eu.betaas.taas.contextmanager.api.impl.ThingsServiceManagerImpl;


public class ConfigBundleOSGiImpl implements ConfigBundleOSGi
{
  //PUBLIC SECTION


  //PROTECTED SECTION
  private static Logger mLogger = Logger.getLogger(ThingsServiceManagerImpl.LOGGER_NAME);
  private IBigDataDatabaseService service;
  private static ConfigBundleOSGiImpl thing = null;
  
  private ConfigBundleOSGiImpl()
  {
    super();
  }
  
  public static ConfigBundleOSGiImpl getInstance()
  {
    if (thing==null) {
      thing = new ConfigBundleOSGiImpl();
      }
   return thing;
   }

  public void readConfigFileOSGi() throws ClassNotFoundException, Exception
  {
    try
    {
    }
    catch(Exception e)
    {
      mLogger
      .error("Component CM perform operation ConfigBundleOSGi.readConfigFileOSGi. It has not been executed correctly. Exception: "
          + e.getMessage() + ".");
    }
  }

  public void closeConfigFileOSGi()
  {
//    this.thing = null;
  }

//  public void setService(IBigDataDatabaseService service)
//  {
//    this.service = service;
//  }
//
//  public IBigDataDatabaseService getService()
//  {
//    return this.service;
//  }
  

}
