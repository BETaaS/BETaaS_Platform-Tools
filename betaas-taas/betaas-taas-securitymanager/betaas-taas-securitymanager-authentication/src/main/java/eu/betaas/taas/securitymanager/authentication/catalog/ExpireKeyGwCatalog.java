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
 * This class holds the expire time of the secret key associated with another 
 * GW within the instance.
 * 
 * @author Bayu Anggorojati [ba@es.aau.dk]
 * Center for TeleInFrastruktur, Aalborg University, Denmark
 *
 */
public class ExpireKeyGwCatalog {
	/** The logger */
	private Logger logger= Logger.getLogger("betaas.taas.securitymanager");
	
	/** The catalog of shared key with the corresponding GW ID */
	private Map<String, Long> gwExpireKeyCatalog;
	
	/** An instance of this catalog */
	private static ExpireKeyGwCatalog _instance = null;
	
	/** 
	 * Method to initiate the instance of KeyGwCatalog class
	 * @return
	 */
	public static ExpireKeyGwCatalog instance(){
		if(_instance == null){
			_instance = new ExpireKeyGwCatalog();
			Logger myLogger= Logger.getLogger("betaas.taas.securitymanager");
			myLogger.info("A new instance of the GW Expire Key Catalog was created!");
		}
		return _instance;
	}
	
	/**
	 * Constructor of this class
	 */
	public ExpireKeyGwCatalog(){
		gwExpireKeyCatalog = new HashMap<String, Long>();
	}
	
	/**
	 * Method to retrieve expire time of a key associated with GW ID: 'gwId'
	 * @param gwId
	 * @return
	 */
	public long getExpireKeyGw(String gwId){
		if(!gwExpireKeyCatalog.containsKey(gwId)){
			logger.error("There is no key expire time associated with GW: "+ gwId);
			return -1;
		}
		return gwExpireKeyCatalog.get(gwId);
	}
	
	/**
	 * Method to add new expire time of a key associated with a GW ID. It applies 
	 * for new and existing set of GW and expire time in the catalog.
	 * @param gwId
	 * @param time
	 * @return
	 */
	public boolean addExpireKeyGw(String gwId, long time){
		gwExpireKeyCatalog.put(gwId, time);
		logger.info("");
		return true;
	}
	
	public boolean removeExpireKeyGw(String gwId){
		if(!gwExpireKeyCatalog.containsKey(gwId)){
			logger.error("Expire time of a key associated with GW: " + gwId + 
					" is not found");
			return false;
		}
		gwExpireKeyCatalog.remove(gwId);
		return true;
	}
}
