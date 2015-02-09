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

package eu.betaas.service.securitymanager.capability.model;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * This class provides methods to store, get, update, and remove the internal
 * capability that is associated with a resource or thing service in this GW
 * 
 * @author Bayu Anggorojati [ba@es.aau.dk]
 * Center for TeleInFrastruktur, Aalborg University, Denmark
 *
 */
public class CapabilityCatalog {
	private Logger logger= Logger.getLogger("betaas.service");
	
	private Map<String, CapabilityInternal> inCapCatalog;
	static private CapabilityCatalog _instance = null;
	
	public static CapabilityCatalog instance(){
		if(_instance==null){
			_instance = new CapabilityCatalog();
			Logger myLogger= Logger.getLogger("betaas.service");
			myLogger.info("A new instance of the Capability Catalog was created!");
		}
		return _instance;
	}
	
	public CapabilityCatalog(){
		inCapCatalog = new HashMap<String, CapabilityInternal>();
	}
	
	public CapabilityInternal getInCap(String thingServiceId){
		if(!inCapCatalog.containsKey(thingServiceId)){
			logger.error("No Capability associated with Thing Service ID: "+thingServiceId);
			return null;
		}
		return inCapCatalog.get(thingServiceId);
	}
	
	public boolean addInCap(CapabilityInternal inCap ){
//		System.out.println("inCapCatalog: "+inCapCatalog);
		this.inCapCatalog.put(inCap.getResourceId(), inCap);
		logger.info("Internal Capability added: " + inCap.getResourceId());
		return true;
	}
	
	public boolean removeInCap(String thingServiceId){
		if(!inCapCatalog.containsKey(thingServiceId)){
			logger.error("The Internal Capability with ID: "+thingServiceId +" to be removed is not found!!");
			return false;
		}
				
		inCapCatalog.remove(thingServiceId);
		return true;
	}
}
