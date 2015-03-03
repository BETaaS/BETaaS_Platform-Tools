**BETaaS Service Big Data Manager**
===================


BETaaS Big Data Manager (BDM) at Service layer, manages the storage coming from one of more gateway. it is made by a service running in each gateway resposnible of delviering data from the TaaS layer, to an available Servcice Database. Such service can be searched and used in remove gateway, in fact it exposes its interface through DOSGi.


## BETaaS Component Info

The Projects implementing the Service BDM Manager are inside the folder: 

	betaas-service\betaas-service-bigdatamanager
    
In particulare the following Apache Maven projects within the parent folder,implements the basic BDM services:

  	- betaas-service-bigdatamanager-data\betaas-service-bigdatamanager\betaas-service-bigdatamanager-service\betaas-service-bigdatamanager-service-core
  	- betaas-service-bigdatamanager-data\betaas-service-bigdatamanager-data-connector\betaas-service-bigdatamanager-data-connector-interface
      -	betaas-service-bigdatamanager-data\betaas-service-bigdatamanager-data-connector\betaas-service-bigdatamanager-data-connector-database
   
    

#### BETaaS services used

The BETaaS Service BDM does use the BDM TaaS BETaaS, available [here](/betaas-docs/github/betaas-taas-bdm-component.md) to get TaaS data to be stored in Service database.

#### BETaaS Services provided

The BETaaS Service BDM provides service to store data from TaaS layer and  a service to store data into a SQL database. It also provides an interface for the Application Data Service, the component responsible of providing data to application.

### Service Big Data Manager Database Structure

#### SDatabase Structure

The structure of the database is the following:

	TABLE `t_thing_data` (
		`gatewayID` VARCHAR(255) NOT NULL,
		`thingID` VARCHAR(255) NOT NULL,
		`timestamp` DATETIME NOT NULL,
		`location` VARCHAR(255) NULL DEFAULT NULL,
		`unit` VARCHAR(255) NULL DEFAULT NULL,
		`type` VARCHAR(255) NULL DEFAULT NULL,
		`measurement` VARCHAR(255) NULL DEFAULT NULL,
		`floor` VARCHAR(255) NULL DEFAULT NULL,
		`room` VARCHAR(255) NULL DEFAULT NULL,
		`environment` VARCHAR(255) NULL DEFAULT NULL,
		`city_name` VARCHAR(255) NULL DEFAULT NULL,
		`latitude` VARCHAR(255) NULL DEFAULT NULL,
		`longitude` VARCHAR(255) NULL DEFAULT NULL,
		`protocol` VARCHAR(255) NULL DEFAULT NULL,
		`altitude` VARCHAR(255) NULL DEFAULT NULL,
		`location_keyword` VARCHAR(255) NULL DEFAULT NULL,
		`location_identifier` VARCHAR(255) NULL DEFAULT NULL,
		PRIMARY KEY (`gatewayID`, `thingID`, `timestamp`)
	)

### Service Big Data Manager Software Configuration

The BETaaS Service Big Data Manager is configured by the following properties inside the file *betaas.gateway.cfg* :
  
	# Service Database settings
    bdm_jdbc_driver = org.h2.Driver
    bdm_url = jdbc:h2:file:/usr/betaas/data/
    bdm_db_name = servicedb
    bdm_db_user = sa
    bdm_db_pwd = sa
    bdm_db_DBSetup = keep
  
These properties set by default allows to create a BDM service that stores H2 information should be stored.

It is possible to change the default H2 storage, to a MariaDB database instance. In this case the provided configuration should be:

	bdm_jdbc_driver = org.mariadb.jdbc.Driver
        bdm_url =  jdbc:mariadb://mariadbhost:3306
	bdm_db_user = username
	bdm_db_pwd = passowrd
	bdm_db_DBSetup = keep
    
Change localhost, username and passoword accordingly with your maria DB intallation. Also the parameter bdm_db_DBSetup, can be set as: delete, to deleate database each time the gateway starts or keep to mantain data even when the gatewat is restarted.

## Service Big Data Manager Analytic Platform

The BETaaS Big Data Manager provides the possibility of loading and querying big data. For this purpose it provides integration with an anlytic platform. All details are provided [here](/betaas-docs/github/betaas-service-hadoop.md) 

## Service Big Data Application Data Task

The BETaaS Big Data Manager provides a component called Application Data Task which provides application an access to the data platform in order to process information. All details are provided [here](/betaas-docs/github/betaas-service-ads.md) 

#### BETaaS Maven Project betaas-service-bigdatamanager-service-core

The project defines the basice BDM Core Service, which implements a component that checks for available OSGi service implenting the betaas-service-bigdatamanager-data-connector-interface interface. If an instance of this service is found, it loads data from the TaaS BDM database in to this found service by using a JSON format.


#### BETaaS Maven Project betaas-service-bigdatamanager-data-connector-interface

The project defines an interface for each service that implenents access to a SQL database. The project does not contain any concrete implementation of such interface.


#### BETaaS Maven Project betaas-service-bigdatamanager-data-connector-database

The project implementes a service providing the interface defined in betaas-service-bigdatamanager-data-connector-interface. This service load the configuration from the *betaas.gateway.cfg* file in order to load JDBC drivers and creating a connection to a database which can be either maria DB or H2. 


###** About BETaaS Project**

#### ** About the consortium**

BETaaS is a STREP Research project developed and co-funded by the European Commission Research and Innovation 7th Framework Program by the [BETaaS Consortium](http://www.betaas.eu/consortium.html#.VEeGuhZvAgk) under the ICT theme (Call 8) of DG CONNECT.

#### ** About BETaaS License**

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

[http://www.apache.org/licenses/LICENSE-2.0](http://www.apache.org/licenses/LICENSE-2.0)

Unless required by applicable law or agreed to in writing, software  distributed under the License is distributed on an "AS IS" BASIS,  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and  limitations under the License.


2014 All Rights Reserved by HP
