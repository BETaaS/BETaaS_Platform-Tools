package eu.betaas.taas.contextmanager.onto.classesExt.wordnet;

import java.util.HashMap;
import java.util.Map;

import edu.mit.jwi.item.POS;

public enum Pos {

  NOUN, VERB, ADJECTIVE, ADVERB, OTHER;

  private static Map<String,Pos> bmap = null;
  private static Map<Pos,POS> wmap = null;
  
//  public static Pos fromBrownTag(String btag) throws Exception {
//    // .. omitted for brevity, see previous post for body
//  }

//  public static Pos fromWordnetPOS(POS pos) {
//    if (wmap == null) {
//      wmap = buildPosBidiMap();
//    }
//    return wmap.get(pos);
//  }
  
  public static POS toWordnetPos(Pos pos) {
    if (wmap == null) {
      wmap = buildPosBidiMap();
    }
    return wmap.get(pos);
  }

  private static Map<Pos,POS> buildPosBidiMap() {
    wmap = new HashMap<Pos,POS>();
    wmap.put(Pos.NOUN, POS.NOUN);
    wmap.put(Pos.VERB, POS.VERB);
    wmap.put(Pos.ADJECTIVE, POS.ADJECTIVE);
    wmap.put(Pos.ADVERB, POS.ADVERB);
    wmap.put(Pos.OTHER, null);
    return wmap;
  }
}
