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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;

//import eu.betaas.taas.contextmanager.onto.classesExt.commonUtils.ConfigBundleOSGiImpl;


public class ThingServiceList implements Serializable
{
  //PUBLIC SECTION
  
  //PROTECTED SECTION
  private ArrayList<String> CMThingServicesList;
  private ArrayList<ArrayList<String>> CMThingServicesListEq;
  private String sOperator;
  
  
  public ArrayList<String> getCMThingServicesList()
  {
    return CMThingServicesList;
  }
  public void setCMThingServicesList(ArrayList<String> cMThingServicesList)
  {
    CMThingServicesList = cMThingServicesList;
  }
  public ArrayList<ArrayList<String>> getCMThingServicesListEq()
  {
    return CMThingServicesListEq;
  }
  public void setCMThingServicesListEq(ArrayList<ArrayList<String>> cMThingServicesListEq2)
  {
    CMThingServicesListEq = cMThingServicesListEq2;
  }
  public String getsOperator()
  {
    return sOperator;
  }
  public void setsOperator(String sOperator)
  {
    this.sOperator = sOperator;
  }
  
  
}
