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
package eu.betaas.rabbitmq.publisher;

import java.io.IOException;

import org.apache.log4j.Logger;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import eu.betaas.rabbitmq.publisher.interfaces.Publisher;



public class PublisherService implements Publisher{

	private String host="localhost";
	private int port=15672;
	private String ename="my_queue";
	private String mode="direct";
	private boolean enabled=false;

	// empty routing key means that all message are received and not filter is specified
	private String routingKey="";
	private ConnectionFactory factory ;
	private Connection connection;
	private Channel channel;
	private Logger log = Logger.getLogger("betaas.bus");
	
	public void startService(){
		
		if (!enabled){
			log.info("#BUS not enabled #");
			return;
		}
		log.info("#BUS config #"+host+" # "+port);
		factory = new ConnectionFactory();
        factory.setHost(host);
        factory.setPort(port);
        log.info("#Connected #");
        try {
			connection = factory.newConnection();
	        channel = connection.createChannel();
			channel.exchangeDeclare(ename, mode);
			log.info("#Exchange "+ ename +" declared as "+mode+" #");
        } catch (IOException e) {
        	enabled=false;
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void publish(String key,String message){
		if (!enabled) return;
		 try {
			
			channel.basicPublish(ename, key, null, message.getBytes());
			//log.info("################################################");
			log.info("Sent to "+ename+ " key "+key+" message "+message);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getEname() {
		return ename;
	}

	public void setEname(String ename) {
		this.ename = ename;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}
	
	public void stopService(){
		try {
			channel.close();
			connection.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
       
	}

	public void setMode(String mode) {
		this.mode=mode;
		
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}
	
	
	
	
	
	
	
}
