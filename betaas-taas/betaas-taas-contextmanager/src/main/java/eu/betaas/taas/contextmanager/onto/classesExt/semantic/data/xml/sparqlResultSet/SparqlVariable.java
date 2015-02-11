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

//@XmlRootElement(name="SparqlElem")
public class SparqlVariable
{
  //////////////////////////////////////////////////////////////////////////////////////
  // VARIABLES
  //////////////////////////////////////////////////////////////////////////////////////
  private String sparqlVariableName = "";
  private String sparqlVariableValue = "";
  private String sparqlVariableType = "";
  private String sparqlVariableLiteralLang = "";
  private String sparqlVariableLiteralDataTypeUri = "";
  
  //////////////////////////////////////////////////////////////////////////////////////
  // CONSTANTS
  //////////////////////////////////////////////////////////////////////////////////////
  public final static String SPARQL_VARIABLE_TYPE_LITERAL = "LITERAL";
  public final static String SPARQL_VARIABLE_TYPE_RESOURCE = "RESOURCE";
  public final static String SPARQL_VARIABLE_TYPE_ANON = "ANON";
  public final static String SPARQL_VARIABLE_TYPE_OTHER = "OTHER";
  
//  @XmlElement(name="name")
  public String getSparqlVariableName()
  {
    return sparqlVariableName;
  }
  public void setSparqlVariableName(String sparqlVariableName)
  {
    this.sparqlVariableName = sparqlVariableName;
  }
  
//  @XmlElement(name="value")
  public String getSparqlVariableValue()
  {
    return sparqlVariableValue;
  }
  public void setSparqlVariableValue(String sparqlVariableValue)
  {
    this.sparqlVariableValue = sparqlVariableValue;
  }
  
//  @XmlElement(name="type")
  public String getSparqlVariableType()
  {
    return sparqlVariableType;
  }
  public void setSparqlVariableType(String sparqlVariableType)
  {
    this.sparqlVariableType = sparqlVariableType;
  }
  
//  @XmlElement(name="literal_lang")
  public String getSparqlVariableLiteralLang()
  {
    return sparqlVariableLiteralLang;
  }
  public void setSparqlVariableLiteralLang(String sparqlVariableLiteralLang)
  {
    this.sparqlVariableLiteralLang = sparqlVariableLiteralLang;
  }
  
//  @XmlElement(name="literal_data_type_uri")
  public String getSparqlVariableLiteralDataTypeUri()
  {
    return sparqlVariableLiteralDataTypeUri;
  }
  public void setSparqlVariableLiteralDataTypeUri(String sparqlVariableLiteralDataTypeUri)
  {
    this.sparqlVariableLiteralDataTypeUri = sparqlVariableLiteralDataTypeUri;
  }
}