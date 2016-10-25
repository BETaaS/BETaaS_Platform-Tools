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
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import eu.betaas.rabbitmq.publisher.interfaces.Publisher;
import eu.betaas.rabbitmq.publisher.interfaces.utils.Message;
import eu.betaas.rabbitmq.publisher.interfaces.utils.Message.Layer;
import eu.betaas.rabbitmq.publisher.interfaces.utils.MessageBuilder;
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
	private boolean busenabled;
	private String gateway;
	private MessageBuilder mb;
	
	private ITaasBigDataManager taasService;


	public void startService() {
		Date data = new Date();
		timestamp = new Timestamp(data.getTime());
		mb = new MessageBuilder();
		log = Logger.getLogger("betaas.service");
		log.info("Starting BDM Service data tracker ");
		
		tracker = new ServiceTracker( context, IDatasourceBDMInterface.class.getName(),	null ); 
		tracker.open(); 
		thread = new Thread( this, "BDM Service thread" ); 
		log.debug("Starting BDM Service thread");
		busInfoMessage("Started");
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
				//log.info("TEST 6.1.X request to get ThingData "+"*"+System.currentTimeMillis());
				JsonObject jsondata = taasService.getThingsData(timestamp);
				if (jsondata!=null){ 
					
					log.debug("Data i got is " + jsondata.toString());
					JsonArray arr = (JsonArray) jsondata.get("res");
					log.debug(".... Result returned ");
					if (arr!=null){ 
						//log.info("TEST 6.1.X request includes "+arr.size()+"*"+System.currentTimeMillis());
						if (arr.size()>0) {
							
							log.debug("TEST 6.1.25 Result array not empty");
							IDatasourceBDMInterface cp = (IDatasourceBDMInterface) providers[n]; 
							log.debug(".... sending data "+jsondata);
							if (jsondata!=null) {
								busInfoMessage("Sending data "+arr.size()+" from gateway "+gateway);
								cp.sendData(jsondata.toString());
								//log.info("TEST 6.1.25.	LAB-TP-120-040 Data sent to Service Database Component "+arr.size()+" in "+(ts2-ts1));
							}
							
						}	
					}
				}
			
			} 
			
				try { wait( freq ); } catch( InterruptedException e ) {} 
		} 

		
	}
	
	private void busInfoMessage(String message){
		log.debug("Checking queue");
		if (!busenabled)return;
		Message messageFormat = new Message();
		messageFormat.setLayer(Layer.SERVICE);
		messageFormat.setLevel("INFO");
		messageFormat.setOrigin("BD Manager");
		messageFormat.setDescritpion(message);
		
		log.debug("Sending to queue");
		ServiceReference serviceReference = context.getServiceReference(Publisher.class.getName());
		log.debug("Sending to queue");
		if (serviceReference==null)return;
		
		Publisher service = (Publisher) context.getService(serviceReference); 
		log.debug("Sending");
		
		
		service.publish("taas.database",mb.getJsonEquivalent(messageFormat) );
		log.debug("Sent");
		
		
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

	public boolean isBusenabled() {
		return busenabled;
	}

	public void setBusenabled(boolean busenabled) {
		this.busenabled = busenabled;
	}

	public String getGateway() {
		return gateway;
	}

	public void setGateway(String gateway) {
		this.gateway = gateway;
	}

	
	
}
