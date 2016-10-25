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
package eu.betaas.service.bigdatamanager.sqoop;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.sqoop.client.SqoopClient;
import org.apache.sqoop.model.MConnection;
import org.apache.sqoop.model.MConnectionForms;
import org.apache.sqoop.model.MForm;
import org.apache.sqoop.model.MInput;
import org.apache.sqoop.model.MJob;
import org.apache.sqoop.model.MJobForms;
import org.apache.sqoop.model.MSubmission;
import org.apache.sqoop.submission.SubmissionStatus;
import org.apache.sqoop.submission.counter.Counter;
import org.apache.sqoop.submission.counter.CounterGroup;
import org.apache.sqoop.submission.counter.Counters;
import org.apache.sqoop.validation.Status;


public class SqoopConnector implements ISqoopConnector {
	
	private SqoopClient client;
	private String sqoop = "http://betaashadoop:12000/sqoop/";
	private Logger log;
	private MConnection newCon;
	
	public void setupService(){
		log = Logger.getLogger("betaas.service");
		log.info("### Sqoop loading ");
		
		
		
		
	}
	
	public void close(){
		
	}

	public boolean connect(String sqoop) {
		this.sqoop=sqoop;
		log.info("TEST 6.1.X Sqoop connect");
		client = new SqoopClient(sqoop);
		log.debug("### Client created ");
		cleanUp();
		
		log.debug("### Connected ");
		
		return true;
		
	}
	
	private void printMessage(List<MForm> formList) {
		
		for(MForm form : formList) {
		    List<MInput<?>> inputlist = form.getInputs();
		    if (form.getValidationMessage() != null) {
		    	log.debug("Form message: " + form.getValidationMessage());
		    }
		    for (MInput minput : inputlist) {
		      if (minput.getValidationStatus() == Status.ACCEPTABLE) {
		    	  log.debug("Warning:" + minput.getValidationMessage());
		      } else if (minput.getValidationStatus() == Status.UNACCEPTABLE) {
		    	  log.error("Error:" + minput.getValidationMessage());
		      }
		    }
		  }
		  
		}

	public long createConnection(String connection,String driver, String user, String pwd) {
		newCon = client.newConnection(1);
		MConnectionForms conForms = newCon.getConnectorPart();
		MConnectionForms frameworkForms = newCon.getFrameworkPart();
		newCon.setName("bdmconnection");

		//Set connection forms the service interface
		conForms.getStringInput("connection.connectionString").setValue(connection);
		conForms.getStringInput("connection.connectionString").setValue("jdbc:mysql://10.15.5.51/servicedb");
		conForms.getStringInput("connection.jdbcDriver").setValue("com.mysql.jdbc.Driver");
		//conForms.getStringInput("connection.jdbcDriver").setValue(driver);
		conForms.getStringInput("connection.username").setValue(user);
		conForms.getStringInput("connection.password").setValue(pwd);

		frameworkForms.getIntegerInput("security.maxConnections").setValue(0);
		
		Status status  = client.createConnection(newCon);
		
		if(status.canProceed()) {
			log.debug(newCon.getConnectorPart().getForms());
			log.debug(newCon.getFrameworkPart().getForms());
			System.out.println("ID for connection! "+newCon.getPersistenceId());
			return newCon.getPersistenceId();
		 
		} else {
		 log.warn("Check for status and forms error ");
		 printMessage(newCon.getConnectorPart().getForms());
		 printMessage(newCon.getFrameworkPart().getForms());
		 return -1;
		}
	}
	public long createJob(long connection, String connectionstr,String driver, String user, String pwd) {
		
		//Creating dummy job object
		log.info("TEST 6.1.X Sqoop creating job");
		MJob newjob = client.newJob(connection, org.apache.sqoop.model.MJob.Type.IMPORT);
		
		MJobForms connectorForm = newjob.getConnectorPart();
		MJobForms frameworkForm = newjob.getFrameworkPart();

		newjob.setName("BDMImportJob");
		
		//Database configuration
		//connectorForm.getStringInput("table.schemaName").setValue("");
		//Input either table name or sql
		//connectorForm.getStringInput("table.tableName").setValue("T_THING_DATA");
		connectorForm.getStringInput("table.sql").setValue("select gatewayID,thingID,CAST(timestamp as CHAR(50)),location,unit,type,measurement from T_THING_DATA where ${CONDITIONS}");
		connectorForm.getStringInput("table.partitionColumn").setValue("thingID");

		//Output configurations
		frameworkForm.getEnumInput("output.storageType").setValue("HDFS");
		frameworkForm.getEnumInput("output.outputFormat").setValue("TEXT_FILE");//Other option: SEQUENCE_FILE
		
		// TODO might provide a better algo for folder creation
		frameworkForm.getStringInput("output.outputDirectory").setValue("/output/test"+connection);

		//Job resources
		frameworkForm.getIntegerInput("throttling.extractors").setValue(1);
		frameworkForm.getIntegerInput("throttling.loaders").setValue(1);
		Status status = client.createJob(newjob);
		log.info("TEST 6.1.X Sqoop job created");
		if(status.canProceed()) {
			log.debug("New Job ID: "+ newjob.getPersistenceId());
			return newjob.getPersistenceId();
		} else {
			printMessage(newjob.getConnectorPart().getForms());
			printMessage(newjob.getFrameworkPart().getForms());
			return -1;
		}
		
		
	}
	
	public long createJob(long connection, Timestamp from, String connectionstr,String driver, String user, String pwd) {
		
		//Creating dummy job object
		
		MJob newjob = client.newJob(connection, org.apache.sqoop.model.MJob.Type.IMPORT);
		MJobForms connectorForm = newjob.getConnectorPart();
		MJobForms frameworkForm = newjob.getFrameworkPart();

		newjob.setName("BDMImportJob");
		//Database configuration
		//connectorForm.getStringInput("table.schemaName").setValue(db);
		log.debug("FROM "+from);
		//Input either table name or sql
		//connectorForm.getStringInput("table.tableName").setValue("T_THING_DATA");
		connectorForm.getStringInput("table.sql").setValue("select gatewayID,thingID,CAST(timestamp as CHAR(50)),location,unit,type,measurement from T_THING_DATA where timestamp >= '"+from.toString()+"' and ${CONDITIONS}");
		log.info("Now the time is: "+from.toString());
		//connectorForm.getStringInput("table.columns").setValue("gatewayID,thingID,timestamp,location,unit,type,measurement");
		connectorForm.getStringInput("table.partitionColumn").setValue("thingID");
		//Set boundary value only if required
		//connectorForm.getStringInput("table.boundaryQuery").setValue("");

		//Output configurations
		frameworkForm.getEnumInput("output.storageType").setValue("HDFS");
		frameworkForm.getEnumInput("output.outputFormat").setValue("TEXT_FILE");//Other option: SEQUENCE_FILE
		
		// TODO might provide a better algo for folder creation
		frameworkForm.getStringInput("output.outputDirectory").setValue("/output/test"+connection);

		//Job resources
		frameworkForm.getIntegerInput("throttling.extractors").setValue(1);
		frameworkForm.getIntegerInput("throttling.loaders").setValue(1);

		Status status = client.createJob(newjob);
		log.info("TEST 6.1.X Sqoop job created");
		if(status.canProceed()) {
			log.debug("New Job ID: "+ newjob.getPersistenceId());
			return newjob.getPersistenceId();
		} else {
			log.warn("Check for status and forms error ");
			 printMessage(newjob.getConnectorPart().getForms());
			 printMessage(newjob.getFrameworkPart().getForms());
			return -1;
		}

	}

	public synchronized void runJob(long jobName) {
		//log.info("Submitting job.. "+client.getConnector(1).getConnectionForms().getEnumInput("connection.connectionString").getValue());
		//Job submission start
		log.info("Submitting "+new Date().toString());
		long ts1 = System.currentTimeMillis();
		MSubmission submission = client.startSubmission(jobName);
		log.debug("Status : " + submission.getStatus());
		if(submission.getStatus().isRunning() && submission.getProgress() != -1) {
			log.info("Progress : " + String.format("%.2f %%", submission.getProgress() * 100));
		}
		log.debug("Hadoop job id :" + submission.getExternalId());
		log.debug("Job link : " + submission.getExternalLink());
		Counters counters = submission.getCounters();
		if(counters != null) {
			log.debug("Counters:");
		  for(CounterGroup group : counters) {
			  log.debug("\t");
			  log.debug(group.getName());
		    for(Counter counter : group) {
		      log.debug("\t\t");
		      log.debug(counter.getName());
		      log.debug(": ");
		      log.debug(counter.getValue());
		    }
		  }
		}
		if(submission.getExceptionInfo() != null) {
			log.warn("Exception info : " +submission.getExceptionInfo());
			
		}
	

		//Check job status
		MSubmission submissionstatus = client.getSubmissionStatus(jobName);
		log.info("Hadoop status :" + submissionstatus.getStatus());
		log.info("Job  : " + submissionstatus.getProgress());
		
		while(submissionstatus.getStatus() == SubmissionStatus.BOOTING) {
		  log.debug("booting");
		  
		  log.debug("Waiting one minute");
		  try { wait( 60000 ); } catch( InterruptedException e ) {} 
		   submissionstatus = client.getSubmissionStatus(jobName);
			log.debug("Hadoop status :" + submissionstatus.getStatus());
			log.debug("Job  : " + submissionstatus.getProgress());
		}
		log.info("Hadoop status :" + submissionstatus.getStatus());
		log.debug("Job  : " + submissionstatus.getProgress());
		
		while(submissionstatus.getStatus()==SubmissionStatus.RUNNING ) {
		  log.debug("Progress : " + String.format("%.2f %%", submission.getProgress() * 100));
		  
		  log.debug("Waiting one minutes");
		  try { wait( 60000 ); } catch( InterruptedException e ) {} 
		   submissionstatus = client.getSubmissionStatus(jobName);
			log.debug("Hadoop status :" + submissionstatus.getStatus());
			log.debug("Job  : " + submissionstatus.getProgress());
		}
		
		
		long ts2 = System.currentTimeMillis();
		log.info("TEST 6.1.X Sqoop processed job in "+(ts2-ts1));
		log.debug("Completed "+new Date().toString());
		
		
		
	}

	public void cleanUp(){
		List<MJob> jobs = client.getJobs();
		for (int p=0;p<jobs.size();p++){
			client.deleteJob(jobs.get(p).getPersistenceId());
		}
		
		List<MConnection> conns = client.getConnections();
		for (int j=0;j<conns.size();j++){
			client.deleteConnection(conns.get(j).getPersistenceId());
		}
		
		
	}

}
