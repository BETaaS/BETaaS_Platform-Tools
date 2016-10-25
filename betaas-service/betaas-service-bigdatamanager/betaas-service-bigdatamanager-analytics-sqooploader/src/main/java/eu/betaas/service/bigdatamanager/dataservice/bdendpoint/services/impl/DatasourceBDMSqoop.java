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
package eu.betaas.service.bigdatamanager.dataservice.bdendpoint.services.impl;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

import eu.betaas.service.bigdatamanager.dataservice.IDatasourceBDMInterface;
import eu.betaas.service.bigdatamanager.dataservice.bdendpoint.services.ImportDataManager;
import eu.betaas.service.bigdatamanager.sqoop.ISqoopConnector;


public class DatasourceBDMSqoop implements ImportDataManager{

	private String sqoop;
	private Timestamp lastImport;
	private String pwd;
	private Logger log;
	private ServiceTracker tracker;
	private BundleContext context;
	private ISqoopConnector sqoopconnection;
	private long freq=5000;
//	private long index=1;
	
	public void setSqoopClient(String sqoop) {
		this.sqoop = sqoop;
	}

	public void startService()  {
		
		log = Logger.getLogger("betaas.service");
		log.debug("sq  "+ sqoop);

		
		
		log.info("BETaaS Analytic Service Data Manager ");
		tracker = new ServiceTracker( context, IDatasourceBDMInterface.class.getName(),	null ); 
		tracker.open(); 
		log.info("TEST 6.1.X Sqoop Scheduler ");
		log.debug("Starting Analytics Data Service thread");

	
	}
	
	@Override
	public void loadData() {

		log.debug("### Tracking....");
		Object [] providers = tracker.getServices(); 
			
			log.debug("### Tracked...."+providers);
			if ( providers != null && providers.length > 0 ) {
					
				log.debug(".... found service ### " + providers.length);
									
				Date data = new Date();
				Timestamp timestamp = new Timestamp(data.getTime());
				log.debug(".... time "+timestamp);
				for (int i=0;i<providers.length;i++)processService((IDatasourceBDMInterface)providers[i]);
			
			} 
			

	}
	
	public void processService(IDatasourceBDMInterface service)  {
		
		log.info("sq connection ongoing with: "+ sqoop);
		//cleanUp();
		//index++;
		log.info("TEST 6.1.X Sqoop Component connected to data source ");
		sqoopconnection.connect(sqoop);
		
		long connection = sqoopconnection.createConnection(service.getInfo("").get("url")+service.getInfo("").get("db"), service.getInfo("").get("driver"), service.getInfo("").get("user"),service.getInfo("").get("pwd"));
		log.debug("sq connection is "+connection);
		Timestamp nowTime = new java.sql.Timestamp(Calendar.getInstance().getTime().getTime());
		long jobid=-1;
		if (!(service.getLastImportTime()==null)){
			lastImport = nowTime;
			log.info("TEST 6.1.X Sqoop Component creating a job incremental " + service.getLastImportTime());
			jobid = sqoopconnection.createJob(connection, nowTime, service.getInfo("").get("url")+service.getInfo("").get("db"), service.getInfo("").get("driver"), service.getInfo("").get("user"),service.getInfo("").get("pwd"));
			log.info("TEST 6.1.X data " + service.getLastImportTime()+"");
			service.reportLastImportTime(nowTime);
		}else{
			lastImport = nowTime;
			log.info("TEST 6.1.X Sqoop Component creating a job");
			jobid = sqoopconnection.createJob(connection, service.getInfo("").get("url")+service.getInfo("").get("db"), service.getInfo("").get("driver"), service.getInfo("").get("user"),service.getInfo("").get("pwd"));
			log.info("First time now: "+lastImport.toString());
			service.reportLastImportTime(nowTime);
		}
		log.debug("Now the time is: "+lastImport.toString());
		if (jobid==-1){
			log.error("Check for status and forms error ");
		}else{
			service.reportLastImportTime(nowTime);
			log.debug("Running job " +jobid);
			log.info("TEST 6.1.X Sqoop Component running a job for importing data");
			sqoopconnection.runJob(jobid);
			log.info("TEST 6.1.X Sqoop Component launched a job");
		}	
		
		

	}


	
	public void setContext(BundleContext context) {
		this.context=context;
		
	}
	
	public void setSqoopUrl(String sqoopUrl) {
		this.sqoop=sqoopUrl;
		
	}
	
	public void setFrequencyDelivery(long freq) {
		this.freq=freq;
	}


	public void closeService() {
		// TODO Auto-generated method stub
		tracker.close();
	}

	@Override
	public boolean isTerminated(long jobid) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean allTerminated() {
		// TODO Auto-generated method stub
		return false;
	}

	public ISqoopConnector getSqoopconnection() {
		return sqoopconnection;
	}

	public void setSqoopconnection(ISqoopConnector sqoopconnection) {
		this.sqoopconnection = sqoopconnection;
	}


}
