**BETaaS Big Data Analytics Manager**
===================


BETaaS Analytics Manager (AM) at Service layer, manages access to analytics platfom and data loading operation. It uses two components, one responsible of loading data from database sources in to HDFS files by using Sqoop2 jobs and another component for loading data in to Hive and defining a meta structure. 

## BETaaS Component Info

The Projects implementing the Service BDM Manager are inside the folder: 

	betaas-service\betaas-service-bigdatamanager\betaas-service-bigdatamanager-analytic
    
In particular the following Apache Maven projects within the parent folder,implements the services:

- betaas-service-bigdatamanager-analytics-manager
- betaas-service-bigdatamanager-analytics-hivejdbc
- betaas-service-bigdatamanager-analytics-sqooploader
- betaas-service-bigdatamanager-sqoop
  	   

#### BETaaS services used

The BETaaS Analytic Manager, requires the BDM database service to create the Sqoop2 jobs. In fact for each available component that write data to SQL database, it loads data in to HDFS.
It is mandatory that the following components are installed within the analytic platform:

	- HDFS
    - Sqooop 2
    - Hive
    - Presto Database
    
Morevoer, HDFS should be reachable by the BETaaS gateway where the BD Analytic Manager is installed. Morevoer Presto Database should be configured to retrieve data by using the table defined inside the Hive Metastore.

#### BETaaS Services provided

The BETaaS AD Manager does not directly provide a database service to the data task to run analytic tasks, this is accomplished indirectly by task conencting the Presto Database isntance where data is loadded. 

### Service Big Data Manager Software Components
    
The BD Analytic manager requires a Big Data platform, the following properties are defined inside the file *betaas.gateway.cfg* ti provide the required component configuration:

	sqoopUrl = http://betaashadoop:12000/sqoop/
    hiveUrl = jdbc:hive2://betaashadoop:10000/default
	hiveUser = hive
	hivePwd = hive
	uploadToHDFSFrequency = 60000

The properties starting with hive prefix, defines the Hive Thrift interfaces and the username and password to be used to connect the Hive metastore.
The sqoopUrl provide the Url where Sqoop2 is running.
Finally the frequency of the Job loading data into HDFS must be provided in milliseconds by the property uploadToHDFSFrequency.

#### BETaaS Maven Project betaas-service-bigdatamanager-analytics-manager

The project defines the main Analytic Manager service. This runs a thread that at regular intervals checkscall for the hivejdbc service and the sqooploader service.

{ADD LINK}

#### BETaaS Maven Project betaas-service-bigdatamanager-analytics-sqooploader

The project defines the Sqoop loader service, this service at regular interval defined by the parameter uploadToHDFSFrequency, create a Sqoop2 job for each available BDM database source that loads data in to HDFS. The driver that allows to connect to the sqoop instance is provided by betaas-service-bigdatamanager-sqoop.

#### BETaaS Maven Project betaas-service-bigdatamanager-sqoop

This project defines the connector OSGi ready, for the sqoop 2 instance. It wraps the Sqoop2 driver inside an OSGi bundle. It selects data from a SQL source by running the following query:

	select gatewayID,thingID,CAST(timestamp as CHAR(50)),location,unit,type,measurement from T_THING_DATA where ${CONDITIONS}
    
The criteria is a parameter that specify a condition to import only data that are more recent that the last time the same sqoop2 job has been run on the same data sources.

It also defines the following HDFS directory as the location where files improted by sqoop are stored:

	/output/


{ADD LINK}

#### BETaaS Maven Project betaas-service-bigdatamanager-analytics-hivejdbc

The project defines the Hive Jdbc component, which, after data is loaded from database SQL into HDFS, imports the data stored in HDFS files by the sqooploader into Hive. It also defines a meta table on top of this imported data, so that Presto DB can use it to query data in a SQL like manner.

Hive table is defined in this way:

	CREATE EXTERNAL TABLE IF NOT EXISTS betaasbd (gateway string,thing string,dte string,loc string,test string,type string,value string)  ROW FORMAT DELIMITED FIELDS TERMINATED BY ','


{ADD LINK}

###** About BETaaS Project**

#### ** About the consortium**

BETaaS is a STREP Research project developed and co-funded by the European Commission Research and Innovation 7th Framework Program by the [BETaaS Consortium](http://www.betaas.eu/consortium.html#.VEeGuhZvAgk) under the ICT theme (Call 8) of DG CONNECT.

#### ** About BETaaS License**

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

[http://www.apache.org/licenses/LICENSE-2.0](http://www.apache.org/licenses/LICENSE-2.0)

Unless required by applicable law or agreed to in writing, software  distributed under the License is distributed on an "AS IS" BASIS,  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and  limitations under the License.


2014 All Rights Reserved by HP
