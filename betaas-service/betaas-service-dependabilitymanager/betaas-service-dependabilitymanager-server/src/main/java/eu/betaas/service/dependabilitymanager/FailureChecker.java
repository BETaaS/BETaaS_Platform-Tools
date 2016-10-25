package eu.betaas.service.dependabilitymanager;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Hashtable;

import org.apache.log4j.Logger;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;

import eu.betaas.rabbitmq.publisher.interfaces.utils.Message;
import eu.betaas.rabbitmq.publisher.interfaces.utils.MessageBuilder;
import eu.betaas.taas.bigdatamanager.database.hibernate.data.DMNotifiedFailure;
import eu.betaas.taas.bigdatamanager.database.service.IBigDataDatabaseService;


/**
 * It reads periodically from betaasbus queue if there are failures messages and writes them in DB.
 * @author Intecs
 */
public class FailureChecker extends Thread {
		
	public void run() {
		setRunning(true);
		
		Message mMessage = new Message();
					    
		mLogger.info("Failure checker started");
		
		 if(connect()){
			 try {
				 channel.exchangeDeclare(EXCHANGE_NAME, "direct");
				  String queueName = channel.queueDeclare().getQueue();
			      channel.queueBind(queueName, EXCHANGE_NAME, KEY);
			        
			      mLogger.debug(" [*] Waiting for messages.");
				  consumer = new QueueingConsumer(channel);
				  channel.basicConsume(queueName, true, consumer);

				} catch (IOException e1) {

					setRunning(false);
					e1.printStackTrace();
				}
		 }		
		while (isRunning()) {
			 try{
			      
				  QueueingConsumer.Delivery delivery = null;
				  delivery = consumer.nextDelivery(WAITING_TIME);
				  if(delivery!= null){
			      String message = new String(delivery.getBody());
			      mLogger.debug(" [x] Received '" + message + "'");
			      
			      mMessage = parseFailure(message);
			     
			      store(mMessage);
				
		      }
		      else
		    	  mLogger.debug(" [.] Timeout receiving");
		 
			  }catch (InterruptedException e) {
				  setRunning(false);
				e.printStackTrace();
			  } catch (Exception e) {
				  setRunning(false);
					e.printStackTrace();
				}
			
		}
		
		disconnect();
	}
		
	private Message parseFailure(String message){
		Message newMessage = new Message();
		
		MessageBuilder messageBuilder = new MessageBuilder();
		
		newMessage = messageBuilder.returnMessageObject(message);
		
		return newMessage;
	}
	

	/**
	 * Start/stop the thread
	 * @param run
	 */
	public synchronized void setRunning(boolean run) {
		mIsRunning = run;
	}
	
	public synchronized boolean isRunning() {
		return mIsRunning;
	}
	

	
	
	private String format(GregorianCalendar cal) {
		return cal.get(Calendar.YEAR) + "-" +
				String.format("%02d",(cal.get(Calendar.MONTH)+1)) + "-" +
				String.format("%02d",cal.get(Calendar.DAY_OF_MONTH)) + " " +
				String.format("%02d",cal.get(Calendar.HOUR_OF_DAY)) + ":" +
				String.format("%02d",cal.get(Calendar.MINUTE));
	}
	
	
	public void store(Message localFail) throws Exception {
		int i, j;
		Message failRep = localFail;
		DMNotifiedFailure serv = new DMNotifiedFailure();
		PreparedStatement psApp = null;
		mCount++;
		
		Timestamp currentTimestamp = new Timestamp(failRep.getTimestamp());
		serv.setNotification_time(currentTimestamp.toString());
		serv.setLayer(failRep.getLayer().ordinal());
		String level = failRep.getLevel();
		if(level.equals(ERROR))
			serv.setLevel(0);
		else if(level.equals(WARNING))
			serv.setLevel(1);
		else if(level.equals(INFO))
			serv.setLevel(2);
		else
			serv.setLevel(3);
		serv.setCode(mCount);
		serv.setOriginator(failRep.getOrigin());
		serv.setDescription(failRep.getDescritpion());
		
		mLogger.info("Updating the application registry to DB");
		
		IBigDataDatabaseService dbService = DependabilityManager.getInstance().getDatabaseServiceIF();
		if (dbService == null) {
			throw new Exception("Cannot access DB to store the application registry");
		}
		
		Connection conn = dbService.getConnection();
		if (conn == null) {
			throw new Exception("Cannot get a valid DB connection to store the application registry");
		}
		
		try {
			psApp = conn.prepareStatement("INSERT INTO T_NOTIFIED_FAILURES (NOTIFICATION_TIME, LAYER, CODE, LEVEL, ORIGINATOR, DESCRIPTION) VALUES (?, ?, ?, ?, ?, ?)");
			if (psApp == null) {
				throw new Exception("Cannot prepare the statement to insert a new application");
			}

				psApp.setString(1,serv.getNotification_time());
				psApp.setInt(2, serv.getLayer());
				psApp.setInt(3, serv.getLevel());
				psApp.setInt(4, serv.getCode());
				psApp.setString(5, serv.getOriginator());
				psApp.setString(6, serv.getDescription());
				if (psApp.executeUpdate() != 1) {
					throw new Exception("Cannot insert the application in the DB registry");
				}
				

			// commit changes
			conn.commit();
			
			mLogger.info("Stored failure into DB");			
			
		} catch (Exception e) {
			
			// If any error occurs, undo the registry updates
			mLogger.info("Exception occurred: rolling back to undo DB updates");
			conn.rollback();
			throw e;
			
		} finally {
			if (psApp != null) {
				psApp.close();
			}
			if (conn != null) {
				conn.close();
			}
		}
	}
	

	
	
	/** Logger */
	private static Logger mLogger = Logger.getLogger(DependabilityManager.LOGGER_NAME);

	private boolean mIsRunning = false;
			
		  
	  public  boolean connect(){
		  boolean returnValue = false;
		  factory = new ConnectionFactory();
		  factory.setHost("localhost");
		  try {
				connection = factory.newConnection();
				channel = connection.createChannel();
				mLogger.debug("connected to RabbitMQ server");
				returnValue = true;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				setRunning(false);
				mLogger.debug("Cannot connect to RabbitMQ server");
			}
		  
		  return returnValue;
	  }
	  
	  
		 public  void disconnect(){
			 try {
				channel.close();
				 connection.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 }
		 
	 
			
		
		
		 private final static String KEY = DependabilityManager.DEPENDABILITY + ".*";
		 private static final String EXCHANGE_NAME = "betaas_bus";
		 private static final int WAITING_TIME = 10000; //millisec 
		 private static final String ERROR = "error";
		 private static final String WARNING = "warning";
		 private static final String INFO = "info";
		 
		 private static ConnectionFactory factory = null;
		 private static  com.rabbitmq.client.Connection connection = null;
		 private static Channel channel = null;
		 private QueueingConsumer consumer = null;
		 private int mCount = 0;

}
