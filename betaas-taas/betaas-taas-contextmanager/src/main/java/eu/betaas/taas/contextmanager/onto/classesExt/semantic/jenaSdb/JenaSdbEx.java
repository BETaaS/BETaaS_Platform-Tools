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
package eu.betaas.taas.contextmanager.onto.classesExt.semantic.jenaSdb;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;

import org.apache.jena.riot.RDFDataMgr;
import org.apache.log4j.Logger;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ReadWrite;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.tdb.TDBFactory;
import com.hp.hpl.jena.update.GraphStore;
import com.hp.hpl.jena.update.GraphStoreFactory;
import com.hp.hpl.jena.update.UpdateExecutionFactory;
import com.hp.hpl.jena.update.UpdateFactory;
import com.hp.hpl.jena.update.UpdateProcessor;
import com.hp.hpl.jena.update.UpdateRequest;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;

import eu.betaas.taas.contextmanager.api.impl.ThingsServiceManagerImpl;
import eu.betaas.taas.contextmanager.onto.classesExt.semantic.data.xml.sparqlResultSet.SparqlResult;
import eu.betaas.taas.contextmanager.onto.classesExt.semantic.data.xml.sparqlResultSet.SparqlResultSet;
import eu.betaas.taas.contextmanager.onto.classesExt.semantic.data.xml.sparqlResultSet.SparqlVariable;
import eu.betaas.taas.contextmanager.onto.classesExt.semantic.data.skos.Skos;

public class JenaSdbEx
{
  // PUBLIC SECTION

  // PRIVATE SECTION
  private static Model m_oModel = null;
//  private Resource scheme = null;
  private final static String PREFIX_ONTOLOGY = "betaasOnt.owl";
  private final static String PREFIX_SKOS_ONTOLOGY = "betaasThingsSkosOnt.owl";
  private final static String PREFIX_METAINF_OWL = "/META-INF/" + PREFIX_ONTOLOGY;
  private final static String PREFIX_METAINF_SKOS = "/META-INF/" + PREFIX_SKOS_ONTOLOGY;
  private final static String PREFIX_TDB = "/TDB/";
  private final static String PREFIX_TDB_OWL = "/tdbowl/";
  private final static String PREFIX_TDB_SKOS = "/tdbskos/";
  private final static String NS = "http://www.betaas.eu/2013/betaasOnt#";
  
  private final static Logger mLogger = Logger.getLogger(ThingsServiceManagerImpl.LOGGER_NAME);
  private final static String PREFIX_METAINF = "/META-INF/" + PREFIX_ONTOLOGY;


//  public boolean init() throws Exception
//  {
//    boolean bCorrect = true;
//    Dataset dataset = null;
//    Dataset dataset_owl = null;
//    Dataset dataset_skos = null;
//    Model m_oModel_owl = null;
//    Model m_oModel_skos = null;
//    try
//    {
//      String sTmpTdbFilePath = createFolder(PREFIX_TDB).getAbsolutePath();
//
//      File sTmpTdbFilePath1 = new File(sTmpTdbFilePath);
//      sTmpTdbFilePath1.deleteOnExit();
//      if (sTmpTdbFilePath1.listFiles().length<=0) {     
//        String sTmpTdbFilePath_owl = createFolder(PREFIX_TDB_OWL).getAbsolutePath();
//        String sTmpTdbFilePath_skos = createFolder(PREFIX_TDB_SKOS).getAbsolutePath();
//
//        dataset = TDBFactory.createDataset(sTmpTdbFilePath);
//        dataset_owl = TDBFactory.createDataset(sTmpTdbFilePath_owl);
//        dataset_skos = TDBFactory.createDataset(sTmpTdbFilePath_skos);
//
//        m_oModel = dataset.getDefaultModel();
//        m_oModel_owl = dataset_owl.getDefaultModel();
//        m_oModel_skos = dataset_skos.getDefaultModel();
//
////        m_oModel_owl = RDFDataMgr.loadModel(PREFIX_METAINF_OWL);
//        String sTmpOwlFilePath = sTmpTdbFilePath_owl + "/" + PREFIX_ONTOLOGY;
//        this.copyOwl(sTmpOwlFilePath, PREFIX_METAINF_OWL);
//        m_oModel_owl = RDFDataMgr.loadModel(sTmpOwlFilePath);
//
////        m_oModel_skos = RDFDataMgr.loadModel(PREFIX_METAINF_SKOS);
//        sTmpOwlFilePath = sTmpTdbFilePath_owl + "/" + PREFIX_SKOS_ONTOLOGY;
//        this.copyOwl(sTmpOwlFilePath, PREFIX_METAINF_SKOS);
//        m_oModel_skos = RDFDataMgr.loadModel(sTmpOwlFilePath);
//
//        m_oModel.add(m_oModel_owl);
//        m_oModel.add(m_oModel_skos);
//
//        if (m_oModel_owl != null) {
//          m_oModel_owl.close();
//          m_oModel_owl = null;
//        }
//        if (m_oModel_skos != null) {
//          m_oModel_skos.close();
//          m_oModel_skos = null;
//        }
//
//        Resource resource1 = m_oModel.getResource(NS + "Property");
//        Resource resource2 = m_oModel.getResource(Skos.Concept.toString());
//        m_oModel.add(resource1, OWL.equivalentClass, resource2);
//        }else{
//          dataset = TDBFactory.createDataset(sTmpTdbFilePath);
//          m_oModel = dataset.getDefaultModel(); //No sé pq, pero no deja crear m_oModel cuando se hace restart
//        }
//      } catch (Exception e) {
//        // close();
//        bCorrect = false;
//        mLogger.error("Component CM perform operation JenaSdbEx.init. Exception: " + e.getMessage() + ", " + e.getLocalizedMessage() + ", " + e.toString() +".");
//      }
//
//      return bCorrect;
//    }

  public boolean init() throws Exception
  {
    boolean bCorrect = true;
    Dataset dataset = null;

    try
    {
      String sTmpTdbFilePath = createFolder(PREFIX_TDB).getAbsolutePath();

      dataset = TDBFactory.createDataset(sTmpTdbFilePath);
      dataset.begin(ReadWrite.READ);
      m_oModel = dataset.getDefaultModel();

      dataset.end();

      String sTmpOwlFilePath = sTmpTdbFilePath + "/" + PREFIX_ONTOLOGY;
      this.copyOwl(sTmpOwlFilePath);
      m_oModel = RDFDataMgr.loadModel(sTmpOwlFilePath);
    }
    catch (Exception e)
    {
      close();
      bCorrect = false;
      mLogger
          .error("Component CM perform operation JenaSdbEx.init. Exception: "
              + e.getMessage() + ".");
    }
    return bCorrect;
  }
  
  public boolean copyOwl(String sExportFilePath) throws Exception
  {
    boolean bCorrect = true;
    InputStream oInputStream = null;
    OutputStream oOutputStream = null;
    try
    {
      oInputStream = this.getClass().getResourceAsStream(PREFIX_METAINF);

      oOutputStream = new FileOutputStream(sExportFilePath);

      int read = 0;
      byte[] bytes = new byte[1024];

      while ((read = oInputStream.read(bytes)) != -1)
      {
        oOutputStream.write(bytes, 0, read);
      }

      oOutputStream.close();
      oInputStream.close();
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
    finally
    {
      if (oInputStream != null)
      {
        try
        {
          oInputStream.close();
        }
        catch (IOException e)
        {
          e.printStackTrace();
        }
      }
      else
        bCorrect = false;
      if (oOutputStream != null)
      {
        try
        {
          // outputStream.flush();
          oOutputStream.close();
        }
        catch (IOException e)
        {
          e.printStackTrace();
        }

      }
      else
        bCorrect = false;
    }
    return bCorrect;
  }
  
  public boolean copyOwl(String sExportFilePath, String stream) throws Exception
  {
    boolean bCorrect = true;
    InputStream oInputStream = null;
    OutputStream oOutputStream = null;
    try
    {
      oInputStream = this.getClass().getResourceAsStream(stream);

      oOutputStream = new FileOutputStream(sExportFilePath);

      int read = 0;
      byte[] bytes = new byte[1024];

      while ((read = oInputStream.read(bytes)) != -1)
      {
        oOutputStream.write(bytes, 0, read);
      }

      oOutputStream.close();
      oInputStream.close();
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
    finally
    {
      if (oInputStream != null)
      {
        try
        {
          oInputStream.close();
        }
        catch (IOException e)
        {
          e.printStackTrace();
        }
      }
      else
        bCorrect = false;
      if (oOutputStream != null)
      {
        try
        {
          // outputStream.flush();
          oOutputStream.close();
        }
        catch (IOException e)
        {
          e.printStackTrace();
        }

      }
      else
        bCorrect = false;
    }
    return bCorrect;
  }

  public boolean close() throws Exception {
    boolean bCorrect = true;
    try {
      if (m_oModel != null) {
        m_oModel.close();
        m_oModel = null;
      }
    } catch (Exception e) {
      bCorrect = false;
      mLogger.info("Component CM perform operation JenaSdbEx.close. Exception: "
              + e.getMessage() + ".");
    }
    return bCorrect;
  }
  
  private File createFolder(String sSource)
  {
    File pathDirectory = null;

    try
    {
      pathDirectory = new File(System.getProperty("java.io.tmpdir", null),
          sSource);
      if (!pathDirectory.exists() && !pathDirectory.mkdir())
        throw new IOException(
            "Component CM. Failed to create temporary directory "
                + pathDirectory);
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }

    return pathDirectory;
  }

//  public boolean copyOwl(String sExportFilePath) throws Exception
//  {
//    boolean bCorrect = true;
//    InputStream oInputStream = null;
//    OutputStream oOutputStream = null;
//    try
//    {
//      oInputStream = this.getClass().getResourceAsStream(PREFIX_METAINF);
//
//      oOutputStream = new FileOutputStream(sExportFilePath);
//
//      int read = 0;
//      byte[] bytes = new byte[1024];
//
//      while ((read = oInputStream.read(bytes)) != -1)
//      {
//        oOutputStream.write(bytes, 0, read);
//      }
//
//      oOutputStream.close();
//      oInputStream.close();
//    }
//    catch (IOException e)
//    {
//      e.printStackTrace();
//    }
//    finally
//    {
//      if (oInputStream != null)
//      {
//        try
//        {
//          oInputStream.close();
//        }
//        catch (IOException e)
//        {
//          e.printStackTrace();
//        }
//      }
//      else
//        bCorrect = false;
//      if (oOutputStream != null)
//      {
//        try
//        {
//          // outputStream.flush();
//          oOutputStream.close();
//        }
//        catch (IOException e)
//        {
//          e.printStackTrace();
//        }
//
//      }
//      else
//        bCorrect = false;
//    }
//    return bCorrect;
//  }

//  public boolean close() throws Exception
//  {
//    boolean bCorrect = true;
//    try
//    {
//      if (m_oModel != null)
//      {
//        m_oModel.close();
//        m_oModel = null;
//      }
//    }
//    catch (Exception e)
//    {
//      bCorrect = false;
//      mLogger
//          .error("Component CM perform operation JenaSdbEx.close. Exception: "
//              + e.getMessage() + ".");
//    }
//    return bCorrect;
//  }

  private SparqlVariable getSparqlVariableAttribs(QuerySolution oQuerySolution,
      String sVariableName, String sSparqlQuery) throws Exception
  {
    SparqlVariable oSparqlVariable = new SparqlVariable();

    try
    {
      RDFNode oRDFNode = oQuerySolution.get(sVariableName);
      if (oRDFNode == null)
      {
        mLogger.error("Component CM perform operation JenaSdbEx.getSparqlVariableAttribs. "
                + "Unable to get RDFNode of variable '"
                + sVariableName
                + "' of SPARQL '" + sSparqlQuery + "'.");
        return oSparqlVariable;
      }

      // If you need to test the thing returned
      if (oRDFNode.isLiteral())
      {
        Literal oLiteral = (Literal) oRDFNode;
        String sValue = oLiteral.getLexicalForm();
        String sLiteralLang = oLiteral.getLanguage();
        String sLiteralDatatypeUri = oLiteral.getDatatypeURI();

        oSparqlVariable.setSparqlVariableName(sVariableName);
        oSparqlVariable.setSparqlVariableValue(sValue);
        oSparqlVariable.setSparqlVariableLiteralLang(sLiteralLang);
        oSparqlVariable
            .setSparqlVariableType(SparqlVariable.SPARQL_VARIABLE_TYPE_LITERAL);
        oSparqlVariable
            .setSparqlVariableLiteralDataTypeUri(sLiteralDatatypeUri);
      }
      else if (oRDFNode.isResource())
      {
        Resource oResource = (Resource) oRDFNode;
        if (oResource.isAnon())
        {
          String sValue = oRDFNode.toString();

          oSparqlVariable.setSparqlVariableName(sVariableName);
          oSparqlVariable.setSparqlVariableValue(sValue);
          oSparqlVariable
              .setSparqlVariableType(SparqlVariable.SPARQL_VARIABLE_TYPE_ANON);
        }
        else
        {
          String sValue = oResource.getURI();

          oSparqlVariable.setSparqlVariableName(sVariableName);
          oSparqlVariable.setSparqlVariableValue(sValue);
          oSparqlVariable
              .setSparqlVariableType(SparqlVariable.SPARQL_VARIABLE_TYPE_RESOURCE);
        }
      }
      else
      {
        String sValue = oRDFNode.toString();

        oSparqlVariable.setSparqlVariableName(sVariableName);
        oSparqlVariable.setSparqlVariableValue(sValue);
        oSparqlVariable
            .setSparqlVariableType(SparqlVariable.SPARQL_VARIABLE_TYPE_OTHER);
      }
    }
    catch (Exception e)
    {
      mLogger.error("Component CM perform operation JenaSdbEx.getSparqlVariableAttribs. Exception: "
              + e.getMessage() + ".");
    }

    return oSparqlVariable;
  }

  public SparqlResultSet sparqlQuery(String sSparqlQuery) throws Exception
  {
    SparqlResultSet oSparqlResultSet = new SparqlResultSet();
    try
    {
       mLogger.debug("Component CM perform operation JenaSdbEx Module, sparqlQuery function. " +
       "It is going to be executed the following SPARQL query:\n****************************************\n"
       + sSparqlQuery + "\n****************************************\n"); 
      SparqlResultSet oTmpSparqlResultSet = new SparqlResultSet();

      // Create a new query
      Query oQuery = QueryFactory.create(sSparqlQuery);

      // Execute the query and obtain results
      QueryExecution oQueryExecution = QueryExecutionFactory.create(oQuery, m_oModel);
      com.hp.hpl.jena.query.ResultSet oJenaResultSet = oQueryExecution
          .execSelect();

//       Output query results TODO
//       ResultSetFormatter.out(System.out, oJenaResultSet, oQuery);//comment

      while (oJenaResultSet.hasNext())
      {
        QuerySolution cQuerySolution = oJenaResultSet.nextSolution();

        SparqlResult oSparqlResult = new SparqlResult();

        Iterator<String> asVarialesNamesList = cQuerySolution.varNames();
        while (asVarialesNamesList.hasNext())
        {
          String sVariableName = asVarialesNamesList.next();

          SparqlVariable oSparqlVariable = getSparqlVariableAttribs(
              cQuerySolution, sVariableName, sSparqlQuery);
          oSparqlResult.addSparqlVariable(oSparqlVariable);
        }

        oTmpSparqlResultSet.addSparqlResult(oSparqlResult);
      }

      // Close Query
      oQueryExecution.close();

      oSparqlResultSet = oTmpSparqlResultSet;
    }
    catch (Exception e)
    {
      mLogger.error("***************Component CM perform operation JenaSdbEx.sparqlQuery. Exception: " + e.getMessage() + ".");
    }
    return oSparqlResultSet;
  }

  public boolean sparqlUpdate(String sSparqlUpdate) throws Exception
  {
    boolean bCorrect = true;
    try
    {
       mLogger.debug("Component CM perform operation JenaSdbEx Module, sparqlUpdate function. It is going to be executed the following SPARQL update:\n****************************************\n"
       + sSparqlUpdate + "\n****************************************\n");

      GraphStore oGraphStore = GraphStoreFactory.create(m_oModel);
      UpdateRequest oUpdateRequest = UpdateFactory.create(sSparqlUpdate);
      UpdateProcessor oUpdateProcessor = UpdateExecutionFactory.create(
          oUpdateRequest, oGraphStore);
      oUpdateProcessor.execute();

      // mLogger.debug("[CM] JenaSdbEx Module, sparqlUpdate function. The previous SPARQL update has been executed.");
    }
    catch (Exception e)
    {
      mLogger
          .error("Component CM perform operation JenaSdbEx.sparqlUpdate. Exception: "
              + e.getMessage() + ".");
    }
    return bCorrect;
  }

  public boolean export(String sExportFilePath) throws Exception
  {
    boolean bCorrect = true;
    try
    {
//       mLogger.debug("[CM] JenaSdbEx Module, export function. It is going to be exported the ontology to the OWL file '"
//       + sExportFilePath + "'.");
      OutputStream cOutputStream = new FileOutputStream(sExportFilePath);
      m_oModel.write(cOutputStream);
      cOutputStream.close();
//       mLogger.debug("[CM] JenaSdbEx Module, export function. It has been exported the ontology to the OWL file '"
//       + sExportFilePath + "'.");
    }
    catch (Exception e)
    {
      mLogger
          .error("Component CM perform operation JenaSdbEx.export. Exception: "
              + e.getMessage() + ".");
    }
    return bCorrect;
  }
  
  public boolean createSKOSConcept(String sTopConcept, String sConcept, String sAltLabel, String sDefinition) {
    boolean bCorrect = true;
    try {
      sAltLabel = sAltLabel.replace("\"", "");
      sDefinition = sDefinition.replace("\"", "");
      sDefinition = sDefinition.replace("\\", "");  
              
      // Manually add the top concept because it's not in the tables
      Resource top = m_oModel.createResource(NS + sConcept);
      top.addProperty(RDF.type, Skos.Concept);
      top.addProperty(RDF.type, ResourceFactory.createResource("http://www.w3.org/2002/07/owl#NamedIndividual"));
      top.addProperty(Skos.prefLabel, sConcept);
      top.addProperty(Skos.definition, sDefinition);
      top.addProperty(Skos.altLabel, sAltLabel);
      
      if (!(sTopConcept == null))
        top.addProperty(Skos.narrower,NS + sTopConcept);
      
    } catch (Exception e) {
      e.printStackTrace();
      bCorrect = false;
    }
    return bCorrect;
  }

}