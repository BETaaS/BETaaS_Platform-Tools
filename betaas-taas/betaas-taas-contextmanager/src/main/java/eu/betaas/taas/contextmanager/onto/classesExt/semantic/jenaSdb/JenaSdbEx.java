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
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.shared.Lock;
import com.hp.hpl.jena.tdb.TDB;
import com.hp.hpl.jena.tdb.TDBFactory;
import com.hp.hpl.jena.update.GraphStore;
import com.hp.hpl.jena.update.GraphStoreFactory;
import com.hp.hpl.jena.update.UpdateExecutionFactory;
import com.hp.hpl.jena.update.UpdateFactory;
import com.hp.hpl.jena.update.UpdateProcessor;
import com.hp.hpl.jena.update.UpdateRequest;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

import eu.betaas.taas.contextmanager.api.impl.ThingsServiceManagerImpl;
import eu.betaas.taas.contextmanager.onto.classesExt.semantic.data.xml.sparqlResultSet.SparqlResult;
import eu.betaas.taas.contextmanager.onto.classesExt.semantic.data.xml.sparqlResultSet.SparqlResultSet;
import eu.betaas.taas.contextmanager.onto.classesExt.semantic.data.xml.sparqlResultSet.SparqlVariable;
import eu.betaas.taas.contextmanager.onto.classesExt.semantic.data.skos.Skos;

public class JenaSdbEx
{
  // PUBLIC SECTION

  // PRIVATE SECTION
//  private static Model m_oModel = null;
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

  Dataset dataset = null;

  public boolean init() throws Exception
  {
    boolean bCorrect = true;
    

    try
    {
      dataset = null;
      Model m_oModel = null;
      
      String sTmpTdbFilePath = createFolder(PREFIX_TDB).getAbsolutePath();
      
      removeFolder(sTmpTdbFilePath);
      
//      TDBFactory.reset();
      dataset = TDBFactory.createDataset(sTmpTdbFilePath);
      dataset.begin(ReadWrite.WRITE);
      m_oModel = dataset.getDefaultModel();

      String sTmpOwlFilePath = sTmpTdbFilePath + "/" + PREFIX_ONTOLOGY;
      this.copyOwl(sTmpOwlFilePath);
//      m_oModel = RDFDataMgr.loadModel(sTmpOwlFilePath);
      
      FileManager.get().readModel(m_oModel, sTmpOwlFilePath);
      
      dataset.commit();
    }
    catch (Exception e)
    {
      dataset.abort();
//      close();
      bCorrect = false;
      mLogger
          .error("Component CM perform operation JenaSdbEx.init. Exception: "
              + e.getMessage() + ".");
    }finally{
      dataset.end();
    }
    return bCorrect;
  }
  
  private void removeFolder(String sTmpTdbFilePath)
  {
    File folder = new File(sTmpTdbFilePath);
    if (folder.exists()){
      File[] listOfFiles = folder.listFiles();

        for (int i = 0; i < listOfFiles.length; i++) {
          if (listOfFiles[i].isFile()) {
//            mLogger.info("File " + listOfFiles[i].getName());
            listOfFiles[i].delete();
//          } else if (listOfFiles[i].isDirectory()) {
//            mLogger.info("Directory " + listOfFiles[i].getName());
          }
        }
        
        folder.delete();
    }
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

//  public boolean close() throws Exception {
//    boolean bCorrect = true;
//    try {
//      if (m_oModel != null) {
//        m_oModel.close();
//        m_oModel = null;
//      }
//    } catch (Exception e) {
//      bCorrect = false;
//      mLogger.info("Component CM perform operation JenaSdbEx.close. Exception: "
//              + e.getMessage() + ".");
//    }
//    return bCorrect;
//  }
  
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
//       mLogger.debug("Component CM perform operation JenaSdbEx Module, sparqlQuery function. " +
//       "It is going to be executed the following SPARQL query:\n****************************************\n"
//       + sSparqlQuery + "\n****************************************\n"); //TODO 
      
      dataset.begin(ReadWrite.READ);
      
      SparqlResultSet oTmpSparqlResultSet = new SparqlResultSet();

      // Create a new query
      Query oQuery = QueryFactory.create(sSparqlQuery);

      // Execute the query and obtain results
//      QueryExecution oQueryExecution = QueryExecutionFactory.create(oQuery, m_oModel);//TODO comment
      QueryExecution oQueryExecution = QueryExecutionFactory.create(oQuery, dataset);
      com.hp.hpl.jena.query.ResultSet oJenaResultSet = oQueryExecution.execSelect();

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
      
      dataset.commit();

      oSparqlResultSet = oTmpSparqlResultSet;
    }
    catch (Exception e)
    {
      dataset.abort();
    }finally{
      dataset.end();
    }
    return oSparqlResultSet;
  }

  public boolean sparqlUpdate(String sSparqlUpdate) throws Exception
  {
    boolean bCorrect = true;
    try
    {
//       mLogger.debug("Component CM perform operation JenaSdbEx Module, sparqlUpdate function. It is going to be executed the following SPARQL update:\n****************************************\n"
//       + sSparqlUpdate + "\n****************************************\n");
      dataset.begin(ReadWrite.WRITE);
      Model m_oModel = dataset.getDefaultModel();
      
      GraphStore oGraphStore = GraphStoreFactory.create(dataset);
      UpdateRequest oUpdateRequest = UpdateFactory.create(sSparqlUpdate);
      UpdateProcessor oUpdateProcessor = UpdateExecutionFactory.create(
          oUpdateRequest, oGraphStore);
      oUpdateProcessor.execute();
      
      dataset.commit();
      TDB.sync(m_oModel);
    }
    catch (Exception e)
    {
      dataset.abort();
      mLogger
          .error("Component CM perform operation JenaSdbEx.sparqlUpdate. Exception: "
              + e.getMessage() + ".");
    }finally{
      dataset.end();
    }
    return bCorrect;
  }

  public boolean export(String sExportFilePath) throws Exception
  {
    boolean bCorrect = true;
//       mLogger.debug("[CM] JenaSdbEx Module, export function. It is going to be exported the ontology to the OWL file '"
//       + sExportFilePath + "'.");
      
      dataset.begin(ReadWrite.WRITE);
      try {
        Model m_oModel = dataset.getDefaultModel();
        
      OutputStream cOutputStream = new FileOutputStream(sExportFilePath);
      m_oModel.write(cOutputStream);
      cOutputStream.close();
      
      dataset.commit(); 
      TDB.sync(m_oModel);
      
      dataset.end();
      
//      ClearModel(m_oModel);
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
  
//  public void SaveAndCloseModel(Model m_oModel){
//    if(m_oModel!=null && dataset!=null){
//      m_oModel.commit();
//      m_oModel.close();
//      dataset.close();
//    }
//}
//
//public void ClearModel(Model m_oModel){
//    if(m_oModel!=null && dataset!=null){
//      m_oModel.removeAll();
//      SaveAndCloseModel(m_oModel);
//    }
//    }
  
  public boolean createSKOSConcept(String sTopConcept, String sConcept, String sAltLabel, String sDefinition) {
    boolean bCorrect = true;
      sAltLabel = sAltLabel.replace("\"", "");
      sDefinition = sDefinition.replace("\"", "");
      sDefinition = sDefinition.replace("\\", "");  

      dataset.begin(ReadWrite.WRITE);
      try {
        Model m_oModel = dataset.getDefaultModel();
        
      Resource skosConcept  = m_oModel.createResource(NS + sConcept);
      skosConcept .addProperty(RDF.type, Skos.Concept);
      skosConcept .addProperty(RDF.type, ResourceFactory.createResource("http://www.w3.org/2002/07/owl#NamedIndividual"));
      skosConcept .addProperty(Skos.prefLabel, sConcept);
      skosConcept .addProperty(Skos.definition, sDefinition);
      skosConcept .addProperty(Skos.altLabel, sAltLabel);
      
      
      if (!(sTopConcept == null)){
        skosConcept .addProperty(Skos.narrower,NS + sTopConcept);
      }
      else{
        //Call comming from TA
        mLogger.info("Component CM perform operation JenaSdbEx.createSKOSConcept, TopConcept:" + sTopConcept +" sConcept: "+ sConcept +" sAltLabel: "+ sAltLabel +" sDefinition: "+ sDefinition);
        skosConcept .addProperty(Skos.narrower,NS + "Sensor");
      }
      dataset.commit();
    } catch (Exception e) {
      mLogger
      .error("Component CM perform operation JenaSdbEx.createSKOSConcept. Exception: " + e.getMessage() + ".");
      bCorrect = false;
    } finally {
      dataset.end();
    }
    return bCorrect;
  }

  public boolean createConcept(String sConcept)
  {
    boolean bCorrect = false;
    dataset.begin(ReadWrite.WRITE);
    
    try {
      Model m_oModel = dataset.getDefaultModel();
    
      Resource resource =  m_oModel.getResource(NS+"Sensor");
      Resource instance = m_oModel.createResource(NS+sConcept+"Sensor");
      m_oModel.add(instance, RDF.type, RDFS.Class);
      m_oModel.add(instance, RDFS.subClassOf, resource);
      dataset.commit();
      bCorrect = true;
    } catch (Exception e) {
      mLogger
      .error("Component CM perform operation JenaSdbEx.createConcept. Exception: " + e.getMessage() + ".");
      bCorrect = false;
    } finally {
      dataset.end();
    }
  return bCorrect;
  }

}