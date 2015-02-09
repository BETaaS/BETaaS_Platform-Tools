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

public class SLACalculation
{
  private String thingServiceId;
  private int QoSparamsFulfill;
  private int QoSparamsNoFulfill;
  
  public String getThingServiceId()
  {
    return thingServiceId;
  }
  public void setThingServiceId(String thingServiceId)
  {
    this.thingServiceId = thingServiceId;
  }
  public int getQoSparamsFulfill()
  {
    return QoSparamsFulfill;
  }
  public void setQoSparamsFulfill(int qoSparamsFulfill)
  {
    QoSparamsFulfill = qoSparamsFulfill;
  }
  public int getQoSparamsNoFulfill()
  {
    return QoSparamsNoFulfill;
  }
  public void setQoSparamsNoFulfill(int qoSparamsNoFulfill)
  {
    QoSparamsNoFulfill = qoSparamsNoFulfill;
  }
}
