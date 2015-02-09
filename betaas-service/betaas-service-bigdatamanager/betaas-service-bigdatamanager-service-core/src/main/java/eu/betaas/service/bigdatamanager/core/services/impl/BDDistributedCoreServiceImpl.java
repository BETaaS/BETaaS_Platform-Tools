/*
 Copyright 2014-2015 Hewlett-Packard Development Company, L.P.

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
package eu.betaas.service.bigdatamanager.core.services.impl;

import java.sql.Timestamp;
import java.util.Date;

import org.apache.log4j.Logger;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import eu.betaas.service.bigdatamanager.core.services.IBigDataDistributedCoreService;
import eu.betaas.service.bigdatamanager.dataservice.IDatasourceBDMInterface;
import eu.betaas.taas.bigdatamanager.core.services.ITaasBigDataManager;



public class BDDistributedCoreServiceImpl implements IBigDataDistributedCoreService, Runnable {
	
	private Logger log;
	private Thread thread; 
	private BundleContext context;
	private ServiceTracker tracker; 
	private long freq=5000;
	private Timestamp timestamp;
	
	
	private ITaasBigDataManager taasService;


	public void startService() {
		// TODO Auto-generated method stub
		Date data = new Date();
		timestamp = new Timestamp(data.getTime());
		log = Logger.getLogger("betaas.service");
		log.info("Starting BDM Service data tracker ");
		
		tracker = new ServiceTracker( context, IDatasourceBDMInterface.class.getName(),	null ); 
		tracker.open(); 
		thread = new Thread( this, "BDM Service thread" ); 
		log.debug("Starting BDM Service thread");
		thread.start(); 
	
	}
	
	public void setService(ITaasBigDataManager service) {
		this.taasService = service;
		
	}

	public synchronized void run() {
		Thread current = Thread.currentThread(); 
		int n = 0; 
		while ( current == thread ) { 
		
			log.debug("### Tracking....");
			Object [] providers = tracker.getServices(); 
			
			log.debug("### Tracked...."+providers);
			if ( providers != null && providers.length > 0 ) {

				log.debug(".... found service ### " + providers.length);
				
				
				
				Date data = new Date();
				timestamp = new Timestamp(data.getTime());
				log.debug(".... time "+timestamp);
				JsonObject jsondata = taasService.getThingsData(timestamp);
				
				if (jsondata!=null){ 
					log.debug("Data i got is " + jsondata.toString());
					JsonArray arr = (JsonArray) jsondata.get("res");
					log.debug(".... Result returned ");
					if (arr!=null){ 
						log.debug("Result array retrieved");
						if (arr.size()>0) {
							log.debug("Result array not empty");
							IDatasourceBDMInterface cp = (IDatasourceBDMInterface) providers[n]; 
							log.info(".... sending data "+jsondata);
							if (jsondata!=null) {
								log.debug("Sending data");
								cp.sendData(jsondata.toString());
								log.debug("Data sent to Service Database Component");
							}
							log.info("BETaaS BDM has sent "+arr.size()+ " data");
						}	
					}
				}
				
				
			
				
			
			} 
			
				try { wait( freq ); } catch( InterruptedException e ) {} 
		} 

		
	}

	public void setContext(BundleContext context) {
		this.context=context;
		
	}

	public void setFrequencyDelivery(long freq) {
		this.freq=freq;
	}

	public void closeService(){
		tracker.close();
		thread=null;
		
	}

}
