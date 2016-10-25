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
package eu.betaas.service.bigdatamanager.analyticservice.component;

import org.apache.log4j.Logger;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

import eu.betaas.service.bigdatamanager.dataservice.bdendpoint.services.ImportDataManager;
import eu.betaas.service.bigdatamanager.dataservice.bdendpoint.services.loader.HiveServiceInterface;

public class AnalyticDataManager implements Runnable{

	private Logger log;
	private Thread thread; 
	private ServiceTracker trackerHiveService;
	private ServiceTracker trackerSqoopService;
	private BundleContext context;
	private long freq=600000;

	
	public void startService()  {
		
		log = Logger.getLogger("betaas.service");
		
		log.info("BETaaS Analytic Service Data Manager ");
		trackerHiveService = new ServiceTracker( context, HiveServiceInterface.class.getName(),	null ); 
		trackerSqoopService = new ServiceTracker( context, ImportDataManager.class.getName(),	null ); 
		trackerHiveService.open(); 
		trackerSqoopService.open();
		log.debug("Starting Analytics Manager thread");
		thread = new Thread( this, "BDM Service thread" ); 
		log.info("TEST 6.1.X ADT Manager Component ");
		log.debug("Starting AM thread");
		thread.start();
	}
	
	public synchronized void run() {
		// TODO Auto-generated method stub

		Thread current = Thread.currentThread(); 

		while ( current == thread ) { 
		
			log.debug("### Tracking....");
			Object [] hiveproviders = trackerHiveService.getServices(); 
			Object [] sqoopproviders = trackerSqoopService.getServices(); 
			
			// let's load data into HDFS from the sources
			if ( sqoopproviders != null && sqoopproviders.length > 0 ) {
				log.debug("### Tracked.... "+sqoopproviders.length);
				ImportDataManager importDataManager = (ImportDataManager) sqoopproviders[0]; 
				log.debug("### Starting data loading from service Sqoop .... ");
				log.info("TEST 6.1.X Sqoop is loading data ");
				long ts1 = System.currentTimeMillis();
				importDataManager.loadData();
				long ts2 = System.currentTimeMillis();
				log.info("TEST 6.1.X Sqoop load data "+(ts2-ts1));
				log.debug("### Job submitted .... ");
				// check when is finished the import process
				log.debug(" Checking if import is terinated ");
				importDataManager.allTerminated();
				log.info("TEST 6.1.X Sqoop Job submitted ");
				log.debug(" Checking if import is terinated ");
				
			} 
			
			// import table data
			if ( hiveproviders != null && hiveproviders.length > 0 ) {
				log.info(" Loading data into Hive Table ");
				HiveServiceInterface hiveServiceInterface = (HiveServiceInterface) hiveproviders[0];
				log.info("TEST 6.1.X Hive Import  ");
				long tsu1 = System.currentTimeMillis();
				hiveServiceInterface.createTable(false);
				hiveServiceInterface.setConnection();
				hiveServiceInterface.loadData();
				long tsu2 = System.currentTimeMillis();
				log.info("TEST 6.1.X Hive Import has ended in "+(tsu2-tsu1));
				log.debug("Done ");
				
			}
			
			try { wait( freq ); } catch( InterruptedException e ) {} 
		} 
		
		
	}
	
	public void setContext(BundleContext context) {
		this.context=context;
		
	}
	
	public void setFrequency(String frequency){
		this.freq = Long.parseLong(frequency);
	}
	
	public void closeService() {
		// TODO Auto-generated method stub
		trackerHiveService.close();
		trackerSqoopService.close();
		thread.interrupt();
	}
}
