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
package eu.betaas.taas.contextmanager.onto.classesExt.semantic.data.xml.sparqlResultSet;

import java.util.ArrayList;

//@XmlRootElement(name="sparql_result_set")
public class SparqlResultSet
{
  private ArrayList<SparqlResult> sparqlResultsList = new ArrayList<SparqlResult>();

//  @XmlElement(name="sparql_result")
  public ArrayList<SparqlResult> getSparqlResultsList()
  {
    return sparqlResultsList;
  }
  
  public void addSparqlResult(SparqlResult sparqlResult)
  {
    sparqlResultsList.add(sparqlResult);
  }

}

