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
package eu.betaas.taas.contextmanager.onto.core;

import java.io.File;
import org.apache.log4j.Logger;

import eu.betaas.taas.contextmanager.api.impl.ThingsServiceManagerImpl;
//import eu.betaas.taas.contextmanager.onto.db.DbMethods;
import eu.betaas.taas.contextmanager.onto.classesExt.semantic.data.xml.sparqlResultSet.SparqlResultSet;
import eu.betaas.taas.contextmanager.onto.classesExt.semantic.jenaSdb.JenaSdbEx;

public class OntoBetaas
{
  // PUBLIC SECTION

  // PRIVATE SECTION
  private static Logger mLogger = Logger.getLogger(ThingsServiceManagerImpl.LOGGER_NAME);
  private static final String PREFIX_ONTOLOGY_TO_EXPORT = "betaasOnt_export.owl";
  private JenaSdbEx m_oJenaEx = null; ///TODO ¿?

  public boolean connectToRepository() throws Exception
  {
    boolean bCorrect = true;
    try
    {
      if (m_oJenaEx == null)
      {
        m_oJenaEx = new JenaSdbEx();
        bCorrect = m_oJenaEx.init();

        if (bCorrect == false)
        {
          return bCorrect;
        }
      }
    }
    catch (Exception e)
    {
      mLogger
          .error("Component CM perform operation OntoBetaas.connectToRepository function. Exception: "
              + e.getMessage() + ".");
    }
    return bCorrect;
  }

  public String exportOwl() throws Exception
  {
    String sOwlFilePath = "";
    try
    {
      File sTmpOwlFilePath = new File(System.getProperty("java.io.tmpdir", null), PREFIX_ONTOLOGY_TO_EXPORT);
      boolean bCorrect = m_oJenaEx.export(sTmpOwlFilePath.toString());
      if (bCorrect == false)
      {
        return sOwlFilePath;
      }

      sOwlFilePath = sTmpOwlFilePath.toString();
    }
    catch (Exception e)
    {
      mLogger
          .error("Component CM perform operation OntoBetaas.exportOwl function. Exception: "
              + e.getMessage() + ".");
    }
    return sOwlFilePath;
  }

  public boolean close() throws Exception
  {
    boolean bCorrect = true;
    try
    {
      boolean bCorrectJena = true;
      boolean bCorrectDbMethods = true;
      
      if (m_oJenaEx != null)
      {
        bCorrectJena = m_oJenaEx.close();
        m_oJenaEx = null;
      }
      
      if((bCorrectJena == false) || (bCorrectDbMethods == false))
      {
        bCorrect = false;
      }
    }
    catch (Exception e)
    {
      mLogger
          .error("Component CM perform operation OntoBetaas.close function. Exception: "
              + e.getMessage() + ".");
    }
    return bCorrect;
  }

  public boolean sparqlUpdate(String sSparqlUpdate) throws Exception
  {
    boolean bCorrect = true;
    try
    {
      bCorrect = m_oJenaEx.sparqlUpdate(sSparqlUpdate);
//      bCorrect = true;
    }
    catch (Exception e)
    {
      mLogger
          .error("Component CM perform operation OntoBetaas.sparqlUpdate function. Exception: "
              + e.getMessage() + ".");
    }
    return bCorrect;
  }

  public SparqlResultSet sparqlQuery(String sSparqlQuery) throws Exception
  {
    SparqlResultSet oSparqlResultSet = null;
    try
    {
      oSparqlResultSet = m_oJenaEx.sparqlQuery(sSparqlQuery);
    }
    catch (Exception e)
    {
      mLogger
          .error("Component CM perform operation OntoBetaas.sparqlQuery function. Exception: "
              + e.getMessage() + ".");
    }
    return oSparqlResultSet;
  }

  public boolean addResource(String sBroaderConcept, String sConcept, String sAltLabel, String sDefinition)
  {
    boolean bCorrect = true;
    try{
      bCorrect = m_oJenaEx.createSKOSConcept(sBroaderConcept, sConcept, sAltLabel, sDefinition);
    }
    catch(Exception e)
    {
      System.out.println("Exception OntoBetaas.addResource. "+ e.getMessage());
    }
  return bCorrect;
  }
}
