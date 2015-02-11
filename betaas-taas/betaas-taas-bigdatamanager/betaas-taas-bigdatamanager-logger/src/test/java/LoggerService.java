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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Vector;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import eu.betaas.taas.bigdatamanager.logger.service.impl.BDLocalLoggerService;


public class LoggerService {

	private static BDLocalLoggerService serviceToTest;
	private static String JDBCDRIVER="org.h2.Driver";
	private static final String JDBCURL = "jdbc:h2:mem:temp";
	private static Connection connection;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
		serviceToTest = new BDLocalLoggerService();
		Class.forName(JDBCDRIVER);
		connection = DriverManager.getConnection(JDBCURL, null,null);	
		serviceToTest.setConnection(connection);
	}

	@Test
	public void testDatabaseInMemory() {

		try {
			
			assertEquals(serviceToTest.countRowData(),0);
			serviceToTest.createThing("test1", "test", "C", "temp,on board");
			assertTrue(serviceToTest.thingContainerExists("test1"));
			String timeStamp1 = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
			String datatosend = "25";
			serviceToTest.saveDataForThing("test1",  timeStamp1 , datatosend.getBytes());
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String timeStamp2= new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
			datatosend = "30";
			serviceToTest.saveDataForThing("test1", timeStamp2 , datatosend.getBytes() );
			assertEquals(serviceToTest.countRowData(),2);
			Vector<String> res = serviceToTest.getThingList();
			assertEquals(res.size(),1);
			assertEquals(res.elementAt(0).toString(),"test1");
			String result = new String(serviceToTest.getThingData("test1"),"UTF-8");
			String[] arrRestult = result.split(";");
			assertEquals(arrRestult.length,2);
			assertEquals(arrRestult[0],"25");
			assertEquals(arrRestult[1],"30");
			serviceToTest.close();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("could not create database");
		}
	}
	
	@AfterClass
	public static void cleanUp() throws SQLException{
		if (connection!=null)connection.close();
	}


	
}
