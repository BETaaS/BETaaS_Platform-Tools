//
//Copyright 2014-2015 Hewlett-Packard Development Company, L.P.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//       http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
package eu.betaas.service.bigdatamanager.stream;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.flume.Event;
import org.apache.flume.EventDeliveryException;
import org.apache.flume.api.RpcClient;
import org.apache.flume.api.RpcClientFactory;
import org.apache.flume.event.EventBuilder;
import org.apache.log4j.Logger;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.ShutdownSignalException;

public class StreamAgent  {
	
	private RpcClient client;
	private String agentHost = "10.15.5.51";
	private int agentPort = 41141;
	private Logger log;
	private String busendpoint = "localhost";
	private int busport=1234;
	private String ename = "betaas_queue";
	private String mode = "direct";
	private String qname = "";
	private String bussubkey = "betaas.datalayer";
	private static String SEPARATOR_FIELD = "&#&";
	private static String SEPARATOR_VALUE = "-#-";
	private QueueingConsumer consumer;
	private int queuewindow = 1;
	private int frequency = 1000;
	private Timer timer;
	Thread thread;
	
	public void startClient(){
		log = Logger.getLogger("betaas.service");
		setupQueue();
		setupAgent();
		timer = new Timer();
		timer.scheduleAtFixedRate(new AgentControl(), 1000, frequency);
		log.info("Starting BDM Streaming service");
	}
	
	public void sendData(String data) {
	    Event event = EventBuilder.withBody(data, Charset.forName("UTF-8"));
	    
	    Map<String, String> headers = new HashMap<String,String>();
	    
	    headers.put("TIMESTAMP", "");
		event.setHeaders(headers);
	    try {
	      client.append(event);
	    } catch (EventDeliveryException e) {
	      client.close();
	      log.error("Error while sending data");
	      
	    }
	}

	
	private void setupQueue(){
		try {
    		log.info("#Starting queue #"+busendpoint + " # "+busport );
	        ConnectionFactory factory = new ConnectionFactory();
	        factory.setHost(busendpoint);
	        factory.setPort(busport);
	        Connection connection;
	        log.info("#Starting queue  #" + ename);
			connection = factory.newConnection();
			log.info("#Starting connection for "+bussubkey);
	        Channel channel = connection.createChannel();
	        channel.exchangeDeclare(ename, mode);
	        //String queueName = channel.queueDeclare().getQueue();
	        channel.queueDeclare(qname, true, false, false, null);
	        channel.queueBind(qname, ename, bussubkey);
	        log.info("#Starting connection on queue #" + qname + " with key");
	        consumer = new QueueingConsumer(channel);
	        channel.basicConsume(qname, true, consumer);
	        log.info("#Running #");
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

	public void queueCheck(long freq){
		
		QueueingConsumer.Delivery delivery;
		try {
			log.debug("#Checking queue ... #");
			for (int i = 0 ; i < queuewindow;i++){
				delivery = consumer.nextDelivery(freq);
				if (delivery==null)return;
				String message = new String(delivery.getBody());
				log.info("#Got  #"+message);
				
				sendData(transformMessageForHive(message));
				log.info("#Data sent to agent ... #");
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.debug("#Data sent to agent ... #");
		}
      
	}
	
	
	public void queueCheck(){
		
		QueueingConsumer.Delivery delivery;
		try {
			log.info("#Checking queue ... #" );
			for (int i = 0 ; i < queuewindow;i++){
				delivery = consumer.nextDelivery();
				if (delivery==null){
					return;
				}
				String message = new String(delivery.getBody());
				log.info("#Got  #"+message);
				sendData(transformMessageForHive(message));
				log.info("#Data sent to agent ... #");
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("#Data sent to agent ... #");
		}
      
	}
	
	private String transformMessageForHive(String message){
		// nine fields GW-TthingID-Time-Location-Alt-Lon-lat-type-measure
		log.info("#Checking message  ... #"+message);
		String[] finalmessage = new String[9]; 
	
		String[] fieldlist = message.split(SEPARATOR_FIELD);
		for (String field : fieldlist){
			String[] values = field.split(SEPARATOR_VALUE);
			if (values!=null){
				if (values.length==2){
					if (values[0].equals("GATEWAY")){
						finalmessage[0] = values[1]+";";
					}
					if (values[0].equals("ID")){
						finalmessage[1] = values[1]+";";
					}
					if (values[0].equals("TIMESTAMP")){
						finalmessage[2] = values[1]+";";
					}
					if (values[0].equals("LOC")){
						finalmessage[3] = values[1]+";";
					}
					if (values[0].equals("ALT")){
						finalmessage[4] = values[1]+";";
					}	
					if (values[0].equals("LAT")){
						finalmessage[5] = values[1]+";";
					}	
					if (values[0].equals("LON")){
						finalmessage[6] = values[1]+";";
					}	
					if (values[0].equals("TYPE")){
						finalmessage[7] = values[1]+";";
					}	
					if (values[0].equals("MEASUREMENT")){
						finalmessage[8] = values[1];
					}	
				}
			}
		}
		String concatenatedmessage = "";
		for (String finalfield : finalmessage){
			concatenatedmessage=concatenatedmessage+finalfield;
		}
		
		log.debug("#Sending to agent ... #"+concatenatedmessage);
		return concatenatedmessage;
	}
	
	private void setupAgent(){
		log.info("BETaaS Flume Client Started" + agentHost + " # "+agentPort);
		client = RpcClientFactory.getDefaultInstance(agentHost, agentPort);
		
	}
	
	
	public void closeClient(){
		if (client!=null)client.close();
		if (timer!=null)timer.cancel();
		
	}


	public String getAgentHost() {
		return agentHost;
	}


	public void setAgentHost(String agentHost) {
		this.agentHost = agentHost;
	}


	public int getAgentPort() {
		return agentPort;
	}


	public void setAgentPort(int agentPort) {
		this.agentPort = agentPort;
	}


	public String getBusendpoint() {
		return busendpoint;
	}


	public void setBusendpoint(String busendpoint) {
		this.busendpoint = busendpoint;
	}


	public String getEname() {
		return ename;
	}


	public void setEname(String ename) {
		this.ename = ename;
	}


	public String getMode() {
		return mode;
	}


	public void setMode(String mode) {
		this.mode = mode;
	}


	public String getQname() {
		return qname;
	}


	public void setQname(String qname) {
		this.qname = qname;
	}


	public String getBussubkey() {
		return bussubkey;
	}


	public void setBussubkey(String bussubkey) {
		this.bussubkey = bussubkey;
	}

	public int getQueuewindow() {
		return queuewindow;
	}

	public void setQueuewindow(int queuewindow) {
		this.queuewindow = queuewindow;
	}

	public int getFrequency() {
		return frequency;
	}

	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}
	
	private class AgentControl extends TimerTask {
	
		@Override
		public void run() {
			
	            log.info("#Checking queue #");
	            	
	            queueCheck(frequency);
	            	
		
			
		}	
	}
	
	private class AgentControlBlock extends TimerTask {
		
		@Override
		public void run() {
			
	            log.info("#Checking queue #");
	            	
	            queueCheck();
	            	
		
			
		}	
	}

	public int getBusport() {
		return busport;
	}

	public void setBusport(int busport) {
		this.busport = busport;
	}

	
	
}
