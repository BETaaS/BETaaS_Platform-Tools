/**
Copyright 2014-2015 Center for TeleInFrastruktur (CTIF), Aalborg University

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

package eu.betaas.taas.securitymanager.authentication.catalog;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * This class holds the secret key associated with another GW in the instance.
 * 
 * @author Bayu Anggorojati [ba@es.aau.dk]
 * Center for TeleInFrastruktur, Aalborg University, Denmark
 *
 */
public class KeyGwCatalog {
	/** The logger */
	private Logger logger= Logger.getLogger("betaas.taas.securitymanager");
	
	/** The catalog of shared key with the corresponding GW ID */
	private Map<String, byte[]> gwKeyCatalog;
	
	/** An instance of this catalog */
	private static KeyGwCatalog _instance = null;
	
	/** 
	 * Method to initiate the instance of KeyGwCatalog class
	 * @return
	 */
	public static KeyGwCatalog instance(){
		if(_instance == null){
			_instance = new KeyGwCatalog();
			Logger myLogger= Logger.getLogger("betaas.taas.securitymanager");
			myLogger.info("A new instance of the GW Key Catalog was created!");
		}
		return _instance;
	}
	
	/**
	 * Constructor of this class
	 */
	public KeyGwCatalog(){
		gwKeyCatalog = new HashMap<String, byte[]>();
	}
	
	/**
	 * Method to retrieve shared key associated with a GW with ID = gwId 
	 * @param gwId
	 * @return
	 */
	public byte[] getKeyGw(String gwId){
		if(!gwKeyCatalog.containsKey(gwId)){
			logger.error("There is no key associated with GW ID: " + gwId);
			return null;
		}
		return gwKeyCatalog.get(gwId);
	}
	
	/**
	 * Method to add a new key associated with a GW ID in the catalog. It applies 
	 * for new and existing set of GW and key in the catalog.
	 * @param gwId
	 * @param key
	 * @return
	 */
	public boolean addKeyGw(String gwId, byte[] key){
		this.gwKeyCatalog.put(gwId, key);
		logger.info("Shared key has been added for GW: "+ gwId);
		return true;
	}
	
	/**
	 * Method to remove a key associated with a GW ID from the catalog.
	 * @param gwId
	 * @return
	 */
	public boolean removeKeyGw(String gwId){
		if(!gwKeyCatalog.containsKey(gwId)){
			logger.error("There is no key associated with GW ID "+gwId +
					" in the catalog to be removed");
			return false;
		}
		gwKeyCatalog.remove(gwId);
		return true;
	}
}
