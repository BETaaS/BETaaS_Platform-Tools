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
package eu.betaas.taas.qosmanager.monitoring.api.impl;

import java.sql.Timestamp;

public class QoSMonitoringMeasure
{
  private String sThingServiceName;
  
  private int sOptimalRequestRate;
  private Timestamp sMaximumTimeStamp;
  
  private int sRequestRate;
  private Timestamp sInitialTimeStamp;
  private boolean sAvailability;
  
  private int iPeriod;
  
  
  public int getOptimalRequestRate()
  {
    return sOptimalRequestRate;
  }
  public void setOptimalRequestRate(int sOptimalRequestRate)
  {
    this.sOptimalRequestRate = sOptimalRequestRate;
  }
  public Timestamp getMaximumTimeStamp()
  {
    return sMaximumTimeStamp;
  }
  public void setMaximumTimeStamp(Timestamp sOptimalTimeStamp)
  {
    this.sMaximumTimeStamp = sOptimalTimeStamp;
  }
  public int getRequestRate()
  {
    return sRequestRate;
  }
  public void setRequestRate(int sRequestRate)
  {
    this.sRequestRate = sRequestRate;
  }
  public Timestamp getInitialTimeStamp()
  {
    return sInitialTimeStamp;
  }
  public void setInitialTimeStamp(Timestamp sTimeStamp)
  {
    this.sInitialTimeStamp = sTimeStamp;
  }
  public boolean getAvailability()
  {
    return sAvailability;
  }
  public void setAvailability(boolean sAvailability)
  {
    this.sAvailability = sAvailability;
  }
  public String getThingServiceName()
  {
    return sThingServiceName;
  }
  public void setThingServiceName(String sThingServiceName)
  {
    this.sThingServiceName = sThingServiceName;
  }
  public int getPeriod()
  {
    return iPeriod;
  }
  public void setPeriod(int iPeriod)
  {
    this.iPeriod = iPeriod;
  }
}
