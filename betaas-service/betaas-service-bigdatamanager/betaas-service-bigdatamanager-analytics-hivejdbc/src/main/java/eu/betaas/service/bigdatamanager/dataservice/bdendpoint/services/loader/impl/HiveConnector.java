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
package eu.betaas.service.bigdatamanager.dataservice.bdendpoint.services.loader.impl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;

import eu.betaas.service.bigdatamanager.dataservice.bdendpoint.services.loader.HiveServiceInterface;



public class HiveConnector implements HiveServiceInterface{
	
	
	private static String driverName = "org.apache.hive.jdbc.HiveDriver";
	
	private static String TABLE_SQL="CREATE EXTERNAL TABLE IF NOT EXISTS betaasbd (gateway string,thing string,dte string,loc string,test string,type string,value string)  ROW FORMAT DELIMITED FIELDS TERMINATED BY ','";
	
	private static String LOAD_DATA = "LOAD DATA INPATH \"/output/*/*\" INTO TABLE betaasbd";
	
	private Logger log;
	
	private Connection con;
	
	private String dbconnection="jdbc:hive2://betaashadoop:10000/default";
	
	private String user="hive";
	
	private String pwd="hive";
		
	public String getDbconnection() {
		return dbconnection;
	}

	public void setDbconnection(String dbconnection) {
		this.dbconnection = dbconnection;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}


	private static String tableName = "betaasbd";
	
	public void start()  {
		log = Logger.getLogger("betaas.service");
		log.info("BETaaS Hive Jdbc Service Starting");
	    try {
	    	log.debug("Hive Jdbc Driver Loading");
	    	
	        Class.forName(driverName);
	        log.debug("Driver loaded");
	        setConnection();
		     }catch (Exception e){
		    	 log.error("problem occurred: ");
		    	 e.printStackTrace();
		     }
	}

	public void setConnection() {

		try {
			log.debug("Connecting..." + dbconnection);
			con = DriverManager.getConnection(dbconnection, user, pwd);
			log.debug("Connecting...Success");
		} catch (Exception e) {
			log.error("Connecting... fail");
			e.printStackTrace();
		}
	}

	public void createTable(boolean overwrite) {
		// TODO Auto-generated method stub
		log.debug("Creating table...");
		Statement stmt;
		try {
			stmt = con.createStatement();
			log.debug("Checking drop requirements..."+overwrite);
			if (overwrite)stmt.execute("drop table if exists "+tableName);
			log.debug("Creating table if required...");
	        stmt.execute(TABLE_SQL);
	        log.debug("Table setup complete");
	        String sql = "show tables";
	        log.info("Showing table: ");
	        ResultSet res = stmt.executeQuery(sql);
	        if (res.next()) {
	        	log.info(res.getString(1));
	        }
	        sql = "describe " + tableName;
	        log.debug("Describe created table: ");
	        res = stmt.executeQuery(sql);
	        while (res.next()) {
	        	log.info(res.getString(1) + "\t" + res.getString(2));
	        }
	        log.debug("Terminating...");
	        res.close();
	        stmt.close();
	        log.info("Table initialization complete");
	     
		} catch (SQLException e) {
			log.error("Failure...");
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public void loadData() {
		// TODO Auto-generated method stub
		Statement stmt;
		try {
			
//			Configuration conf = new Configuration();
//			conf.set("fs.default.name", "hdfs://betaashadoop:9000/tmp/output/");
//			
//			conf.set("fs.default.name", "hdfs://betaashadoop:9000/tmp/output/");
//			
//			
//			FileSystem hdfs = FileSystem.get(conf);
//			
//			hdfs.getContentSummary("hdfs://betaashadoop:9000/tmp/output/");
//			

			stmt = con.createStatement();
			log.debug("Running load operation: ");
	        stmt.execute(LOAD_DATA);
	        log.debug("Loaded!");
	        String sql = "select count(*) from " + tableName;
	        log.debug("Running count operation: ");
	        stmt.close();
		} catch (SQLException e) {
			log.error("SQL Failure...");
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	public void stop()  {
		// TODO Auto-generated method stub
		try {
			if (con!=null) if (!(con.isClosed()))con.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		log.info("Closing Hive Connector Service");
	}
	
}
