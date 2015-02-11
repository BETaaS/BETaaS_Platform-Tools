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
package eu.betaas.taas.bigdatamanager.logger.service.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import eu.betaas.taas.bigdatamanager.core.services.ITaasBigDataManager;
import eu.betaas.taas.bigdatamanager.logger.service.IBigDataLoggerService;

public class BDLocalLoggerService implements IBigDataLoggerService, Runnable {
	private Connection connection;
	private Logger logger;
	private Statement create_thing_table_stmt;
	private Statement create_data_table_stmt;
	private PreparedStatement create_new_thing_stmt;
	private PreparedStatement insert_thing_data_stmt;
	private PreparedStatement delete_thing_stmt;
	private PreparedStatement delete_thing_data_stmt;
	private PreparedStatement list_thing_stmt;
	private PreparedStatement get_thing_data_stmt;
	private PreparedStatement check_thing_exists_stmt;
	private PreparedStatement count_reads_stmt;
	private static final String CREATE_THING_TABLE_SQL="CREATE TABLE things (THING_ID VARCHAR(50) NOT NULL, NAME VARCHAR(50) NOT NULL, UNIT VARCHAR(50), TAGS VARCHAR(150), PRIMARY KEY(THING_ID) )";
	private static final String CREATE_DATA_TABLE_SQL="CREATE TABLE reads (THING_ID VARCHAR(50) NOT NULL, TIME_DATA VARCHAR(50) NOT NULL , VALUE VARCHAR(50) NOT NULL ,  FOREIGN KEY(THING_ID) REFERENCES things(THING_ID) )";
	private static final String CREATE_NEW_THING_SQL="INSERT INTO things VALUES(?,?,?,?)";
	private static final String INSERT_NEW_THING_DATA_SQL="INSERT INTO reads VALUES(?,?,?)";
	private static final String DELETE_THING_SQL="DELETE FROM thing WHERE THING_ID = ?";
	private static final String DELETE_THING_DATA_SQL="DELETE FROM reads WHERE THING_ID = ?";
	private static final String LIST_THING_SQL="SELECT THING_ID FROM things";
	private static final String GET_THING_DATA_SQL="SELECT TIME_DATA,VALUE FROM reads WHERE THING_ID = ?";
	private static final String CHECK_THING_EXISTS_SQL="SELECT count(*) FROM things WHERE THING_ID = ?";
	private static final String COUNT_READS_SQL="SELECT COUNT(*) FROM reads";
	private ResultSet rs;
	//private IBigDataDatabaseService service;
	private ITaasBigDataManager service;
	private Thread thread; 

	public void setLogger(String conf) {
		// TODO Auto-generated method stub
		logger = Logger.getLogger(conf);
		
		
	}
	
	public void  clearTables(){
		
		logger.info("Clearing table content from a previous run data");
		
	
	}
	
	
	public void setConnection(Connection connection) {
		// method used only for test purpose
		
		logger = Logger.getLogger("betaas.taas");
		this.connection =connection;
		// load drivers
		if (logger==null){
			
			logger.info("logger not set, will log on betaas appender");
		}
		logger.info("Connection has been provided");
		
		//this.clearTables();

	}
	
	public void setup() throws ClassNotFoundException, SQLException{
		this.setLogger("betaas.taas");
		logger.info("Setup has been started");
		logger.info("Generating data for test purpose");
		thread = new Thread( this, "BDM Service thread" ); 
		logger.info("Starting data injector Service thread");
		thread.start();
//		connection = null;
//	      
//        // TODO database tables should be empty at the beginning?	     	
//        
//        if (service==null){
//    	  logger.error("Service not available");
//    	  return;
//        } else {
//    	   logger.info("Using service");
//        }
//        JsonParser jp = new JsonParser();
//        JsonObject jo;
//        String  msg="{\"floor\":1,\"room\":kitchen,\"type\":presence,\"measurement\":true,\"environment\":kitchen,\"unit\":boolean}";
//      
//        jo = (JsonObject)jp.parse(msg);
//       
//        service.setThingsBDM("0000000001",  jo);
//        logger.info("Service "+jo.toString());
//        try {
//            Thread.sleep(5000);
//        } catch(InterruptedException ex) {
//            Thread.currentThread().interrupt();
//        }
//        msg="{\"timestamp\":\"2014-07-08 13:26:46.576\",\"is_output\":true,\"is_digital\":true,\"maximum_response_time\":\"30\",\"memory_status\":\"12\",\"computational_cost\":\"100\",\"battery_level\":\"100\",\"battery_cost\":\"200\",\"measurement\":\"false \",\"protocol\":\"etsi\",\"deviceID\":\"000001\",\"thingID\":\"GARM_GPS1\",\"type\":\"GPSPOSITION\",\"unit\":\"boolean\",\"environment\":true,\"latitude\":\"43.654987380589200\",\"longitude\":\"10.436325073242100\",\"altitude\":\"7.0\",\"floor\":\"0\",\"location_keyword\":\"car\",\"location_identifier\":\"na\"}";
//        //msg="{\"floor\":1,\"room\":null,\"type\":GPSPOSITION,\"measurement\":false,\"environment\":kitchen,\"unit\":boolean,\"location\":null}";
//        jo = (JsonObject)jp.parse(msg);
//        service.setThingsBDM("0000000001",  jo);
//        
//        
//        
//        logger.info("Service "+jo.toString());
//        
//        msg="{\"floor\":0,\"protocol\":\"etsi\",\"latitude\":\"43.654987380589200\",\"longitude\":\"10.436325073242100\",\"altitude\":\"7.0\",\"location_keyword\":\"car\",\"location_identifier\":\"na\",\"battery_cost\":\"200\",\"computational_cost\":\"100\",\"battery_level\":\"100\", \"is_output\":true,\"maximum_response_time\":\"30\",\"room\":bedroom,\"type\":presence,\"measurement\":false,\"environment\":bedroom,\"unit\":boolean}";
//        jo = (JsonObject)jp.parse(msg);
//        service.setThingsBDM("0000000002",  jo);
//        
//        
//        logger.info("Service "+jo.toString());
//        
//        
//        try {
//            Thread.sleep(5000);
//        } catch(InterruptedException ex) {
//            Thread.currentThread().interrupt();
//        }
//        
//        msg="{\"measurement\":false}";
//        jo = (JsonObject)jp.parse(msg);
//        service.setThingsBDM("0000000001",  jo);
//        
//        
//        logger.info("Service "+jo.toString());
//        
        
        
        /*
       EntityManager em = service.getEntityManager();
		logger.info("Database EM status "+em.isOpen());
		PersistentAgreementContainer data= new PersistentAgreementContainer();
		data.setAgreementClassName("test");
		data.setAgreementFactoryId("1");
		data.setState("false");
	
			em.getTransaction().begin();
			em.persist(data);
			em.getTransaction().commit();
			
			AgreementEprContainer agreementEprContainer = new AgreementEprContainer();
			agreementEprContainer.setEpr("test");
			agreementEprContainer.setAgreementId("test");
			agreementEprContainer.setEprAddress("test");
			agreementEprContainer.setAgreementFactoryId("tst");
		
			service.saveAgreementEprContainer(agreementEprContainer);
		
		
		
		Connection conn = service.getConnection();
		PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM AGREEMENT_EPR_CONTAINER");
		ResultSet rs = ps.executeQuery();
		while(rs.next()){
			logger.info("Database connection result for COUNT "+rs.getString(1));			
		}
		conn.close();
		*/
        
	}
	
	
	private void test(){
		
	}

	public void close(){
		logger.info("closing connection");
		
		thread=null;
		if (connection!=null){
			try {
				connection.close();
			} catch (SQLException e) {
				logger.error("closing connection failed");
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}


	public void createThing(String thingID, String name, String unit,  String tags) {
		// TODO Auto-generated method stub
		try {
			create_new_thing_stmt = connection.prepareStatement(CREATE_NEW_THING_SQL);
			create_new_thing_stmt.setString(1, thingID);
			create_new_thing_stmt.setString(2, name);
			create_new_thing_stmt.setString(3, unit);
			create_new_thing_stmt.setString(4, tags);
			create_new_thing_stmt.execute();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			logger.error("insert failed");
			e.printStackTrace();
			this.close();
		} finally {
	    	// avoid unclosed connections

	    	if (create_new_thing_stmt!=null)
				try {
					create_new_thing_stmt.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
			}
	    }
		
		
	}


	public boolean destroyThing(String thingID) {
		// TODO Auto-generated method stub
		try {
			delete_thing_data_stmt = connection.prepareStatement(DELETE_THING_DATA_SQL);
			delete_thing_data_stmt.setString(1, thingID);
			delete_thing_data_stmt.execute();
			delete_thing_stmt = connection.prepareStatement(DELETE_THING_SQL);
			delete_thing_stmt.setString(1, thingID);
			delete_thing_stmt.execute();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			logger.error("insert failed");
			e.printStackTrace();
			this.close();
		} finally {
	    	// avoid unclosed connections
	    	if (delete_thing_data_stmt!=null)
				try {
					delete_thing_data_stmt.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
			}
	    	if (delete_thing_stmt!=null)
				try {
					delete_thing_stmt.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
			}
	    }
		return false;
	}


	public void saveDataForThing(String thingID, String time, byte[] data) {
		// TODO Auto-generated method stub
		try {
			insert_thing_data_stmt = connection.prepareStatement(INSERT_NEW_THING_DATA_SQL);
			insert_thing_data_stmt.setString(1, thingID);
			insert_thing_data_stmt.setString(2, time);
			insert_thing_data_stmt.setBytes(3, data);
			insert_thing_data_stmt.execute();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("insert failed");
			e.printStackTrace();
			this.close();
		} finally {
	    	// avoid unclosed connections
	    	if (create_new_thing_stmt!=null)
				try {
					insert_thing_data_stmt.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
			}
	    }
	}


	public boolean thingContainerExists(String thingID) {
		// TODO Auto-generated method stub
		
		try {
			check_thing_exists_stmt = connection.prepareStatement(CHECK_THING_EXISTS_SQL);
			check_thing_exists_stmt.setString(1, thingID);
			rs = check_thing_exists_stmt.executeQuery();
			if (rs.next()){
				if (rs.getInt(1)==0){
					return false;
				} else {
					return true;
				}
			} else {
				return false;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			logger.error("insert failed");
			e.printStackTrace();
			this.close();
		} finally {
	    	// avoid unclosed connections
	    	if (check_thing_exists_stmt!=null)
				try {
					check_thing_exists_stmt.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
			}
	    }
		return false;
	}


	public byte[] getThingData(String param) {
		// TODO Auto-generated method stub
		try {
			get_thing_data_stmt = connection.prepareStatement(GET_THING_DATA_SQL);
			get_thing_data_stmt.setString(1, param);
			rs = get_thing_data_stmt.executeQuery();
			String result="";
			String data="";
			while(rs.next()){
				data = new String(rs.getBytes(2))+';';
				result= result + data;
			}
			 
			return result.getBytes();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			logger.error("insert failed");
			e.printStackTrace();
			this.close();
		} finally {
	    	// avoid unclosed connections
	    	if (get_thing_data_stmt!=null)
				try {
					get_thing_data_stmt.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
			}
	    	
	    }
		return null;
		
	}


	public Vector<String> getThingList() {
		// TODO Auto-generated method stub
		try {
			list_thing_stmt = connection.prepareStatement(LIST_THING_SQL);
			rs = list_thing_stmt.executeQuery();
			Vector<String> result = new Vector<String>();
			while(rs.next()){
				result.add(rs.getString(1));
			}
			return result;
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			logger.error("insert failed");
			e.printStackTrace();
			this.close();
		} finally {

	    	if (list_thing_stmt!=null)
				try {
					list_thing_stmt.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
			}
	    	
	    }
		return null;
	}


	public int countRowData() {
		// TODO Auto-generated method stub
		
		try {
			count_reads_stmt = connection.prepareStatement(COUNT_READS_SQL);
			rs = count_reads_stmt.executeQuery();
			if(rs.next()){
				
				return rs.getInt(1);
			}
			count_reads_stmt.close();

			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("insert failed");
			e.printStackTrace();
			this.close();
		} finally {
	    	// avoid unclosed connections

	    	if (count_reads_stmt!=null)
				try {
					count_reads_stmt.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
			}
	    	
	    }
		return 0;
	}

	public void setService(ITaasBigDataManager service) throws SQLException {
		// TODO Auto-generated method stub
		this.service = service;
		//this.setConnection(service.getConnection());
		
		
	}

	public synchronized void run() {
		Thread current = Thread.currentThread(); 
		int n = 0; 
		while ( current == thread ) { 
				
			if (service==null){
	    	  logger.error("Service not available");
	    	  return;
	        } 
	        JsonParser jp = new JsonParser();
	        JsonObject jo;
	        int x = (int)(Math.random() * 100);
	        int y = (int)(Math.random() * 4);
	        //int z = (int)Math.random() * 5;
	        String locazione="";
	        if (y==0){
	        	locazione="Castle Black";
	        } else if (y==1){
	        	locazione="Dragonstone";
	        }else if (y==2){
	        	locazione="Winterfell";
	        }else if (y==3){
	        	locazione="Tyrosh";
	        }else if (y==4){
	        	locazione="Winterfell";
	        }else {
	        	locazione="Sunspear";
	        }
	        
	        Calendar calendar = Calendar.getInstance();
	        java.util.Date now = calendar.getTime();
	        java.sql.Timestamp currentTimestamp = new java.sql.Timestamp(now.getTime());
	        
	        String  msg="{\"timestamp\":\""+currentTimestamp+"\",\"is_output\":true,\"is_digital\":true,\"maximum_response_time\":\"30\",\"memory_status\":\"12\",\"computational_cost\":\"100\",\"battery_level\":\"100\",\"battery_cost\":\"200\",\"measurement\":\""+x+"\",\"protocol\":\"etsi\",\"deviceID\":\"00000"+y+"\",\"thingID\":\"GARM_GPS"+y+"\",\"type\":\"TRAFFIC\",\"unit\":\"machineperminutes\",\"environment\":true,\"latitude\":\"43.654987380589200\",\"longitude\":\"10.436325073242100\",\"altitude\":\"7.0\",\"floor\":\"0\",\"location_keyword\":\"car\",\"location_identifier\":\""+locazione+"\"}";
	        logger.debug(msg);
	        String id = "GARM_GPS"+y;
	        jo = (JsonObject)jp.parse(msg);
	       
	        service.setThingsBDM(id,  jo);
			
			try { wait( 2000 ); } catch( InterruptedException e ) {} 
		} 
		
		
		
	}


	
}
