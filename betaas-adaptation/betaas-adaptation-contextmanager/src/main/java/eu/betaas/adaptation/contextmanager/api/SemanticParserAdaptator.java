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
package eu.betaas.adaptation.contextmanager.api;

import java.util.ArrayList;
import java.util.List;
import eu.betaas.taas.bigdatamanager.database.service.ThingsData;

public interface SemanticParserAdaptator{
  
  public String publishThingInit(ArrayList<ThingsData> oThingsDataList) throws Exception;
  public String publishThingCheck(ArrayList<ThingsData> oThingsDataList) throws Exception;
  public String publishThing(ArrayList<ThingsData> oThingsDataList) throws Exception;
  public boolean addWordnetConceptTerm(String sTerm, String sSynsetID, String sDefinition);
  public boolean removeThing(List<String> sThingServiceList);
  public boolean removeThingUnreachable (String sThingName);
  
  public boolean subscribe(String sThingServiceName);
  public boolean unsubscribe(String sThingServiceName);

//boolean publishThing_local(ArrayList<ThingsData> oThingsDataList, String sMode) throws Exception;
public void getRealTimeAdaptedInformation();
//public void setActuatorsAdaptedValues();

}
