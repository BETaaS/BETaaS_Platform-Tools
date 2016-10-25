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
// Component: QoSManager Monitoring, TaaS Module
// Responsible: Tecnalia
package eu.betaas.taas.qosmanager.monitoring.api;

import java.util.ArrayList;

import org.osgi.framework.BundleContext;

import eu.betaas.taas.contextmanager.api.ThingsServiceManager;
import eu.betaas.taas.qosmanager.monitoring.api.impl.SLACalculation;

public interface QoSManagerMonitoring
{
  //BETAAS INTERFACE
  public boolean getMeasurementSLAMonitoring(String sThingServiceName, int sMilisecondMinInterRequestRate);
  public boolean getMeasurementSLAMonitoring(String sThingServiceName, int sMilisecondMinInterRequestRate, double dTolerateJitterParam);
  public boolean registerMeasurementSLAMonitoring(String sThingServiceName, int sMilisecondMinInterRequestRate, int iMilisecondPeriod);
  public boolean registerMeasurementSLAMonitoring(String sThingServiceName, int sMilisecondMinInterRequestRate, int iMilisecondPeriod, double dTolerateJitterParam);
  public boolean unregisterMeasurementSLAMonitoring(String sThingServiceName);
  public SLACalculation calculateSLA(String sThingServiceName);
  public SLACalculation calculateSLAPush(String sThingServiceName, int iMilisecondTaaSRequestRate);
  public SLACalculation failureSLA(String sThingServiceName);

  
  //BLUEPRINT METHODS
  public void startService() throws Exception;
  public void closeService();
  public void setContext(BundleContext context);
  public BundleContext getContext();
  public boolean isEnabled();
  public void setEnabled(boolean enabledbus);
//  public boolean getEnabled();

//  public void setCmservice(ThingsServiceManager cmservice);
//  public ThingsServiceManager getCmservice() throws Exception;
}
