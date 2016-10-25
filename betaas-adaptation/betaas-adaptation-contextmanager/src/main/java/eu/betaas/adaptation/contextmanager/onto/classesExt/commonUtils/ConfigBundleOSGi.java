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

import java.sql.SQLException;

import org.osgi.framework.BundleContext;

import eu.betaas.taas.bigdatamanager.core.services.ITaasBigDataManager;
import eu.betaas.taas.contextmanager.api.ThingsServiceManager;
import eu.betaas.taas.taasresourcesmanager.api.TaaSResourceManager;
import eu.betaas.taas.qosmanager.api.QoSManagerInternalIF;

public interface ConfigBundleOSGi
{
  public void readConfigFileOSGi() throws Exception;
  public void busMessage(String message);
  public void sendData(String description, String level, String originator);
  public void closeConfigFileOSGi();

  public void setBdservice(ITaasBigDataManager bdservice) throws SQLException;
  public ITaasBigDataManager getBdservice() throws SQLException;
  
  public void setRmservice(TaaSResourceManager service) throws SQLException;
  public TaaSResourceManager getRmservice() throws SQLException;
  
  public void setCmservice(ThingsServiceManager cmservice) throws SQLException;
  public ThingsServiceManager getCmservice() throws SQLException;
  
  public void setQosservice(QoSManagerInternalIF qosservice) throws SQLException;
  public QoSManagerInternalIF getQosservice() throws SQLException;
  
  public void setGwId(String gwId);
  public String getGwId();
  
  public String getDelimiter();
  public void setDelimiter(String delimiter);
  
  public BundleContext getContext();
  public void setContext(BundleContext context);
  
  public boolean isEnabledbus();
  public void setEnabledbus(boolean enabledbus);
}
