/*
 *
Copyright 2014-2015 Department of Information Engineering, University of Pisa

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

// BETaaS - Building the Environment for the Things as a Service
//
// Component: TaaS QoS Manager
// Responsible: Carlo Vallati & Giacomo Tanganelli

package eu.betaas.taas.qosmanager.core;

import java.sql.Timestamp;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import eu.betaas.rabbitmq.publisher.interfaces.Publisher;
import eu.betaas.rabbitmq.publisher.interfaces.utils.Message;
import eu.betaas.rabbitmq.publisher.interfaces.utils.Message.Layer;
import eu.betaas.rabbitmq.publisher.interfaces.utils.MessageBuilder;
import eu.betaas.taas.bigdatamanager.database.service.IBigDataDatabaseService;
import eu.betaas.taas.qosmanager.api.QoSManagerExternalIF;


public class QoSManager {
	private static Logger logger = Logger.getLogger("betaas.taas");
	private BundleContext context; 
	private String key = "monitoring.taas";
	private QoSManagerExternalIF qosm_star;
	private IBigDataDatabaseService service;
	private int poolSize = 5;
	private ExecutorService pool;
	
	public QoSManager(BundleContext context){
		pool = Executors.newFixedThreadPool(poolSize);
		this.context = context;
	}
	
	public String getTemplate() {
		// TODO Auto-generated method stub
		return null;
	}

	public String createAgreement(String offer) {
		// TODO Auto-generated method stub
		
		return null;
	}

	public QoSManagerExternalIF getQosm_star() {
		return qosm_star;
	}

	public void setQosm_star(QoSManagerExternalIF qosm_star) {
		this.qosm_star = qosm_star;
	}

	public void setService(IBigDataDatabaseService service) {
		this.service = service;
		
	}

	public IBigDataDatabaseService getService() {
		return service;
		
	}

	public ExecutorService getPool() {
		return pool;
	}
	private void busMessage(String message){
		logger.debug("Checking queue");
		logger.debug("Sending to queue");
		ServiceReference serviceReference = context.getServiceReference(Publisher.class.getName());
		logger.debug("Sending to queue");
		if (serviceReference==null)return;
		Publisher service = (Publisher) context.getService(serviceReference); 
		logger.debug("Sending");
		service.publish(key,message);
		logger.debug("Sent");
		
		
	}
	public void sendData(String description, String level, String originator) {
		java.util.Date date= new java.util.Date();
		Timestamp timestamp = new Timestamp(date.getTime());
		Message msg = new Message();
		msg.setDescritpion(description);
		msg.setLayer(Layer.TAAS);
		msg.setLevel(level);
		msg.setOrigin(originator);
		msg.setTimestamp(timestamp.getTime());
		MessageBuilder msgBuilder = new MessageBuilder();
		String json = msgBuilder.getJsonEquivalent(msg);
		busMessage(json);
		
	}

}
