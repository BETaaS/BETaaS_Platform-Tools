package eu.betaas.rabbitmq.publisher.interfaces.utils;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MessageBuilder {

	public Message returnMessageObject(String jsonMessage){
		ObjectMapper mapper = new ObjectMapper(); // can reuse, share globally
		try {
			return mapper.readValue(jsonMessage, Message.class);
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return null;
	}
	
	public String getJsonEquivalent(Message message){
		ObjectMapper mapper = new ObjectMapper(); 
		
		try {
			return mapper.writeValueAsString(message);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}  
	
	
}
