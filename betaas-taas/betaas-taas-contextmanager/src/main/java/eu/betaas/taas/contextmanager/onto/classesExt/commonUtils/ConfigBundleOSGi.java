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

import java.sql.SQLException;
import eu.betaas.taas.bigdatamanager.database.service.IBigDataDatabaseService;


public interface ConfigBundleOSGi
{
  public void readConfigFileOSGi() throws Exception;
  public void closeConfigFileOSGi();

//  public void setService(IBigDataDatabaseService service) throws SQLException;
//
//  public IBigDataDatabaseService getService() throws SQLException;
  
}
