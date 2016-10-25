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

/**
 * This is a wrapper for the MIT WordNet inteface that simplifies basic operations
 * such as retrieving synonyms and hypernyms for a word.
 * 
 * @author Izaskun Mendia
 *
 */
package eu.betaas.taas.contextmanager.onto.classesExt.wordnet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import edu.mit.jwi.Dictionary;
import edu.mit.jwi.IDictionary;
import edu.mit.jwi.item.IIndexWord;
import edu.mit.jwi.item.IIndexWordID;
import edu.mit.jwi.item.ISynset;
import edu.mit.jwi.item.ISynsetID;
import edu.mit.jwi.item.IWord;
import edu.mit.jwi.item.IWordID;
import edu.mit.jwi.item.POS;
import edu.mit.jwi.item.Pointer;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import eu.betaas.taas.contextmanager.api.ThingsServiceManager;
import eu.betaas.taas.contextmanager.api.impl.ThingsServiceManagerImpl;

public class WordNetUtils
{
  // PUBLIC SECTION

  // PRIVATE SECTION
//  private ThingsServiceManager oThingsServiceManager = new ThingsServiceManager();
//  private static ThingsServiceManager oThingsServiceManager = null;
  
  private static Logger mLogger = Logger.getLogger(ThingsServiceManagerImpl.LOGGER_NAME);
  private static String WORDNET = "";
  private static String PREFIX_WORDNET = "";
  private final static String WORDNET_LINUX = "WordNetDictLinux";
  private final static String WORDNET_WIN = "WordNetDictWin";
  
  private String sWordnetDirectory = null;
  private URL urlWordnetDirectory = null;
  private static IDictionary iDictionary = null;
  private Map<String,POS> suffixPosMap;
  private static Map<Pos,POS> wmap = null;
  
  private static String OS = System.getProperty("os.name").toLowerCase();
  private static String tmpdir1 = System.getProperty("user.home");
  
  private final static String SYNSETID = "synsetID";
  private final static String TERM = "term";
  private final static String DEFINITION = "definition";
  private final static String SYNONYM = "synonym";
  private final static String HOLONYM = "holonym";
  private final static String HOLOHOLONYM = "holoholonym";
  private final static String HYPERHOLONYM = "hyperholonym";
  private final static String HYPERNYM = "hypernym";
  private final static String HYPERHYPERNYM = "hyperhypernym";

//  ThingsServiceManager oThingsServiceManager = new ThingsServiceManagerImpl();
  ThingsServiceManager oThingsServiceManager = ThingsServiceManagerImpl.getInstance();
  
  public boolean init()
  {
    String[] includedWordnetFiles = null;
    boolean bCorrect = true;
    try
    {
//      oThingsServiceManager = new ThingsServiceManagerImpl();
	oThingsServiceManager = ThingsServiceManagerImpl.getInstance();

      
      mLogger.debug("Component CM perform operation WordNetUtils.checkWordnet. This is a wrapper for the MIT WordNet inteface that simplifies basic operations such as retrieving synonyms for a word.");
      
      oThingsServiceManager.sendData("Starting Wordnet... a wrapper for the MIT WordNet inteface that simplifies basic operations such as retrieving synonyms for a word.","info", "Wordnet");
      File fileDestiny = null;
      String pathWordnetFileName = null;

      if(isLinux()){
        WORDNET = WORDNET_LINUX;
      }
      else if (isWindows()){ 
        WORDNET = WORDNET_WIN;
      }
      else
        mLogger.error("ERROR, unknown SO");
      PREFIX_WORDNET = "/META-INF/" + WORDNET + "/";
      File pathWordnetDirectory = new File(tmpdir1, WORDNET);

      if (!pathWordnetDirectory.exists() && !pathWordnetDirectory.mkdir())
        throw new IOException("Failed to create temporary directory " + pathWordnetDirectory);

      if(isLinux()) {
        includedWordnetFiles = new String[]
      {   PREFIX_WORDNET + "adj.exc", PREFIX_WORDNET + "adv.exc",
          PREFIX_WORDNET + "cntlist", PREFIX_WORDNET + "cntlist.rev",
          PREFIX_WORDNET + "data.adj", PREFIX_WORDNET + "data.adv",
          PREFIX_WORDNET + "data.noun", PREFIX_WORDNET + "data.verb",
          PREFIX_WORDNET + "frames.vrb", PREFIX_WORDNET + "index.verb",
          PREFIX_WORDNET + "index.adj", PREFIX_WORDNET + "index.adv",
          PREFIX_WORDNET + "index.noun", PREFIX_WORDNET + "index.sense",
          PREFIX_WORDNET + "lexnames", PREFIX_WORDNET + "log.grind.3.0",
          PREFIX_WORDNET + "Makefile", PREFIX_WORDNET + "Makefile.am",
          PREFIX_WORDNET + "Makefile.in", PREFIX_WORDNET + "noun.exc",
          PREFIX_WORDNET + "sentidx.vrb", PREFIX_WORDNET + "sents.vrb",
          PREFIX_WORDNET + "verb.exc", PREFIX_WORDNET + "verb.Framestext" };
      }else if (isWindows()) {
        includedWordnetFiles = new String[]
            { PREFIX_WORDNET + "adj.exc", PREFIX_WORDNET + "adv.exc",
                PREFIX_WORDNET + "cntlist", PREFIX_WORDNET + "cntlist.rev",
                PREFIX_WORDNET + "data.adj", PREFIX_WORDNET + "data.adv",
                PREFIX_WORDNET + "data.noun", PREFIX_WORDNET + "data.verb",
                PREFIX_WORDNET + "frames.vrb", PREFIX_WORDNET + "index.verb",
                PREFIX_WORDNET + "index.adj", PREFIX_WORDNET + "index.adv",
                PREFIX_WORDNET + "index.noun", PREFIX_WORDNET + "index.sense",
                PREFIX_WORDNET + "log.grind.2.1", PREFIX_WORDNET + "noun.exc",
                PREFIX_WORDNET + "sentidx.vrb", PREFIX_WORDNET + "sents.vrb",
                PREFIX_WORDNET + "verb.exc", PREFIX_WORDNET + "verb.Framestext" };
      } else mLogger.error("Your OS is not support!!");
      
      ClassLoader wordnetClassLoader = WordNetUtils.class.getClassLoader();
      for (String includedWordnetFile : includedWordnetFiles)
      {
        URL urlSource = wordnetClassLoader.getResource(includedWordnetFile);
        InputStream isSource = this.getClass().getResourceAsStream(includedWordnetFile);

        if (urlSource != null)
        {
          try
          {
            String pathWordnetFile = urlSource.toString();
            pathWordnetFileName = pathWordnetFile.substring(pathWordnetFile.lastIndexOf("/") + 1);
            fileDestiny = new File(tmpdir1, "/" + WORDNET + "/" + pathWordnetFileName);
            fileDestiny.deleteOnExit();
            FileUtils.copyInputStreamToFile(isSource, fileDestiny);
            isSource.close();
          }
          catch (Exception e)
          {
            bCorrect=false;
            mLogger.error("Component CM perform operation WordNetUtils.checkWordnet. It has not been executed correctly. Problems with copy InputStream to File. Exception: "
                    + e.getMessage() + ".");
          } finally {
            isSource.close();
            IOUtils.closeQuietly(isSource);
            isSource = null;
            System.gc();
        }
        }
      }
      
    }
    catch (Exception e)
    {
          mLogger.error("Component CM perform operation WordNetUtils.checkWordnet. It has not been executed correctly. Exception: "
                  + e.getMessage() + ".");
          }
      
    return bCorrect;
    }
      
    public ArrayList<String> checkWordnet(String sLemmaToCompare, Pos pos) 
    {
      ArrayList<String> oListSynonyms = null;
      oListSynonyms = new ArrayList<String>();
      IIndexWord idxWord = null;
      
      try
      {
        if (pos.toString().equals("VERB")){
          idxWord = iDictionary.getIndexWord(sLemmaToCompare, POS.VERB);
        }
        else{
          idxWord = iDictionary.getIndexWord(sLemmaToCompare, POS.NOUN);
        }
        
        for (IWordID wordID : idxWord.getWordIDs())
        {
        IWord word = iDictionary.getWord(wordID);
        
        String message = "SYNONYMS. Searching synonyms for term " + sLemmaToCompare.toUpperCase() + ", with the following WordnetID: " + wordID + ".";
       
        message = "SYNONYMS. The " + sLemmaToCompare.toUpperCase() + " description is: " + word.getSynset().getGloss() + ".";
        mLogger.debug(message);
        oThingsServiceManager.sendData(message,"info", "Wordnet");
        
        ISynset synset = word.getSynset();
        message = "SYNOMYMS. Synonyms found for the term " + sLemmaToCompare.toUpperCase() + ". Synonyms: " + synset.toString()+ ".";
        mLogger.debug(message);//TODO
        oThingsServiceManager.sendData(message,"info", "Wordnet");
        // iterate over words associated with the synset
        for (IWord w : synset.getWords())
        {
          oListSynonyms.add(w.getLemma());
        }
        }
      }
      catch (Exception e)
      {
        oListSynonyms.add(sLemmaToCompare);
        mLogger.info("SYNOMYMS. Synonyms not found for the term "+sLemmaToCompare.toUpperCase()+ ".");
      }
    
    return oListSynonyms;
  }


    private List<Pos> getPosFromWordnet(String word) {
      List<Pos> poslist = new ArrayList<Pos>();
      for (Pos pos : Pos.values()) {
        try {
          IIndexWord indexWord = iDictionary.getIndexWord(word, Pos.toWordnetPos(pos));
          if (indexWord != null) {
            poslist.add(pos);
          }
        } catch (NullPointerException e) {
          // JWI throws this if it cannot find the word in its dictionary
          // so we just dont add anything to the poslist.
          continue;
        }
      }
      return poslist;
    }
  
    public Pos getPartOfSpeech(String word) {
      List<Pos> partsOfSpeech = getPosFromWordnet(word);
      int numPos = partsOfSpeech.size();
      if (numPos == 0) {
        // unknown Pos, apply word rules to figure out Pos
          return Pos.NOUN;
      } else if (numPos == 1) {
        // unique Pos, return
        return partsOfSpeech.get(0);
      } else {
        // ambiguous Pos, apply disambiguation rules
        mLogger.debug("SYNOMYMS. Ambiguous term "+word.toUpperCase()+". User should clarify if it is referring to a verb or to a noun. By default the CM considers a NOUN.");//TODO
        return partsOfSpeech.get(0);
      }
    }
      
  public static boolean isWindows() {
    return (OS.indexOf("win") >= 0);
  }
 
 
  public static boolean isLinux() {
    return (OS.indexOf("linux") >= 0);
  }
  
  public JsonArray getSynsets(String sLemma, boolean type)
  {
    // type = true    sensor
    // type = false   actuator
    JsonArray jaTempResultValue = null;
    JsonArray jaTempHolonymValue = null;
    JsonArray jaTempHypernymValue = null;
    JsonObject joTempResultValue = null;
    IIndexWord idxWord = null;
    try {
      jaTempResultValue = new JsonArray();
      if (type)
        idxWord = iDictionary.getIndexWord(sLemma, POS.NOUN);
      else
        idxWord = iDictionary.getIndexWord(sLemma, POS.VERB);

        // iterate over senses      
      for (IWordID w : idxWord.getWordIDs()) {
        joTempResultValue = new JsonObject();
        ISynsetID sSynset = w.getSynsetID();
        joTempResultValue.addProperty(SYNSETID, sSynset.toString());
        IWord word = iDictionary.getWord(w);
        ISynset iSynset = word.getSynset();

        String definition = word.getSynset().getGloss();
        joTempResultValue.addProperty(DEFINITION, definition);
        
        String synonyms = "";
        for (IWord iw : word.getSynset().getWords()) {
          synonyms = synonyms + iw.getLemma().toUpperCase() + " ";
        }
        joTempResultValue.addProperty(SYNONYM, synonyms);

        if (type){
          jaTempHolonymValue = getHolonyms(iDictionary, iSynset);
          if (jaTempHolonymValue.size()>0)
            joTempResultValue.add(HOLONYM, jaTempHolonymValue);

          jaTempHolonymValue = getHoloHolonyms(iDictionary, iSynset);
          if (jaTempHolonymValue.size()>0)
            joTempResultValue.add(HOLOHOLONYM, jaTempHolonymValue);

          jaTempHolonymValue = getHyperHolonyms(iDictionary, iSynset);
          if (jaTempHolonymValue.size()>0)
            joTempResultValue.add(HYPERHOLONYM, jaTempHolonymValue);

          jaTempHypernymValue = getHypernyms(iDictionary, iSynset);
          if (jaTempHypernymValue.size()>0){
              joTempResultValue.add(HYPERNYM, jaTempHypernymValue);
          }

          jaTempHypernymValue = getHyperHypernyms(iDictionary, iSynset);
          if (jaTempHypernymValue.size()>0){
              joTempResultValue.add(HYPERHYPERNYM, jaTempHypernymValue);
          }

        }     
      jaTempResultValue.add(joTempResultValue);
      }
    } catch (Exception e) {
      mLogger.error("- No synonyms on WordNet.");
//       mLogger.error("Component CM perform operation WordNetUtils.getSynsets. Exception: " + e.getMessage() + ".");
    }
    return jaTempResultValue;
  }
  
    /**
     * Retrieve a set of holonyms for a word.
     */
    public static JsonArray getHolonyms(IDictionary iDictionary, ISynset iSynset) {
    JsonArray aHolonyms = new JsonArray(); 
              
        // multiple holonym chains are possible for a synset
        for(ISynsetID iSynsetId : iSynset.getRelatedSynsets(Pointer.HOLONYM_PART)) {
          List<IWord> iWords = iDictionary.getSynset(iSynsetId).getWords();
          for(IWord iWord2: iWords) {
            String sLemma = iWord2.getLemma();
            JsonObject oHolonyms = new JsonObject();
            oHolonyms.addProperty(TERM, sLemma);          
            aHolonyms.add(oHolonyms);
          }
        }
      return aHolonyms;
    }
    
    public static JsonArray getHoloHolonyms(IDictionary dict, ISynset iSynset) {
      JsonArray aHolonyms = new JsonArray(); 
      for(ISynsetID iSynsetId1 : iSynset.getRelatedSynsets(Pointer.HOLONYM_PART)) {
            for(ISynsetID iSynsetId2 : dict.getSynset(iSynsetId1).getRelatedSynsets(Pointer.HOLONYM_PART)) {
              List<IWord> iWords = dict.getSynset(iSynsetId2).getWords();
              for(IWord iWord2: iWords) {
                String sLemma = iWord2.getLemma();
                
                JsonObject oHolonyms = new JsonObject();
                oHolonyms.addProperty(TERM, sLemma);
                aHolonyms.add(oHolonyms);
              }
            }
          }
        return aHolonyms;
      } 
    
    public static JsonArray getHyperHolonyms(IDictionary dict, ISynset iSynset) {
      JsonArray aHolonyms = new JsonArray(); 
      for(ISynsetID iSynsetId1 : iSynset.getRelatedSynsets(Pointer.HOLONYM_PART)) {
            for(ISynsetID iSynsetId2 : dict.getSynset(iSynsetId1).getRelatedSynsets(Pointer.HYPERNYM)) {
              List<IWord> iWords = dict.getSynset(iSynsetId2).getWords();
              for(IWord iWord2: iWords) {
                String sLemma = iWord2.getLemma();
                
                JsonObject oHolonyms = new JsonObject();
                oHolonyms.addProperty(TERM, sLemma);
                aHolonyms.add(oHolonyms);
              }
            }
          }
        return aHolonyms;
      } 
    
    /**
     * Retrieve a set of hypernyms for a word.
     */
    public static JsonArray getHypernyms(IDictionary iDictionary, ISynset iSynset) {
    JsonArray aHypernyms = new JsonArray(); 
              
        // multiple hypernym chains are possible for a synset
        for(ISynsetID iSynsetId : iSynset.getRelatedSynsets(Pointer.HYPERNYM)) {
          List<IWord> iWords = iDictionary.getSynset(iSynsetId).getWords();
          for(IWord iWord2: iWords) {
            String sLemma = iWord2.getLemma();
            
              JsonObject oHypernyms = new JsonObject();
            oHypernyms.addProperty(TERM, sLemma);
            aHypernyms.add(oHypernyms);
          }
        }
      return aHypernyms;
    }
    
    public static JsonArray getHyperHypernyms(IDictionary dict, ISynset iSynset) {
      JsonArray aHypernyms = new JsonArray(); 
      for(ISynsetID iSynsetId1 : iSynset.getRelatedSynsets(Pointer.HYPERNYM)) {
            for(ISynsetID iSynsetId2 : dict.getSynset(iSynsetId1).getRelatedSynsets(Pointer.HYPERNYM)) {
              List<IWord> iWords = dict.getSynset(iSynsetId2).getWords();
              for(IWord iWord2: iWords) {
                String sLemma = iWord2.getLemma();
                
                JsonObject oHypernyms = new JsonObject();
                oHypernyms.addProperty(TERM, sLemma);
                aHypernyms.add(oHypernyms);
              }
            }
          }
        return aHypernyms;
      }   

    public void loadDictionary() {
      try {
        sWordnetDirectory = tmpdir1 + "/" + WORDNET;
        
        urlWordnetDirectory = new URL("file", null, sWordnetDirectory);
        mLogger.info("Component CM perform operation WordNetUtils.checkWordnet. Wordnet dictionary path directory: " + PREFIX_WORDNET + ".");
        oThingsServiceManager.sendData("Wordnet dictionary path directory: " + PREFIX_WORDNET + ".","info", "Wordnet");

        iDictionary = new Dictionary(urlWordnetDirectory);    
        iDictionary.open();

      } catch (Exception e) {
        mLogger.error("Component CM perform operation WordNetUtils.loadDictionary. Exception: " + e.getMessage() + ".");
      }
    }
    
}
