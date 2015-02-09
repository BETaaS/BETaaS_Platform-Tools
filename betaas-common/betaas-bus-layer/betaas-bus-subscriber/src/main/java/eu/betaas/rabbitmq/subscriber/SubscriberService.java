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
package eu.betaas.rabbitmq.subscriber;

import java.io.IOException;

import org.apache.log4j.Logger;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.ShutdownSignalException;

import eu.betaas.rabbitmq.subscriber.interfaces.Subscriber;

public class SubscriberService implements Subscriber, Runnable{

	private String host="localhost";
	private String ename="my_exc";
	private String bussubkey="bussubkey";
	private String qname="betaas_queue";
	private boolean enabled=false;
	private boolean stop=false;
	private String mode;
	private QueueingConsumer consumer;
    private Logger log = Logger.getLogger("betaas.bus");	
	
    public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}
	
	public String getBussubkey() {
		return bussubkey;
	}

	public void setBussubkey(String bussubkey) {
		this.bussubkey = bussubkey;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getEname() {
		return ename;
	}

	public void setEname(String ename) {
		this.ename = ename;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	
    
    public String getQname() {
		return qname;
	}

	public void setQname(String qname) {
		this.qname = qname;
	}

	public void startService() {
    	try {
    		log.info("#Starting queue #");
	        ConnectionFactory factory = new ConnectionFactory();
	        factory.setHost("localhost");
	        Connection connection;
	        log.info("#Starting #");
			connection = factory.newConnection();
			log.info("#Starting connection#");
	        Channel channel = connection.createChannel();
	
	        channel.exchangeDeclare(ename, mode);
	        String queueName = channel.queueDeclare().getQueue();
	        channel.queueBind(queueName, ename, bussubkey);
	        log.info("#Starting connection on queue #" );
	        consumer = new QueueingConsumer(channel);
	        channel.basicConsume(queueName, true, consumer);
	
	        log.info("#Running #");
	        run();
	        
    	} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ShutdownSignalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ConsumerCancelledException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
    }

	public void run() {
		log.info("#Thread starting #");
		new Thread (new Runnable()
	      {
	      public void run()
	        {
	        while (!stop)
	          {
	          try
	            {
	        	  Thread.sleep(5000);
	            	log.debug("#Checking queue #");
	            	queueCheck();
	            }
	          catch (Exception e){}
	          }
	        }
	      }).start();
	}
		
	public void stopService(){
		stop=true;
	}
	
	private void queueCheck(){
		
		QueueingConsumer.Delivery delivery;
		try {
			log.info("#Checking queue ... #");
			delivery = consumer.nextDelivery();
			  String message = new String(delivery.getBody());
			  log.info("#Got  #"+message);
		} catch (ShutdownSignalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ConsumerCancelledException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
      
	}
	
}
