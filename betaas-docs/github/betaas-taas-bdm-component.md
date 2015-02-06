**BETaaS TaaS Big Data Manager**
===================


BETaaS Big Data Manager at TaaS layer, manages the collection, storage and maintenance of data for the BETaaS TaaS components. Moreover, it is also responsible for defining the storage configuration so that data is available for components that need to access it. The TaaS BD allows other TaaS components to access data by retrieving connections from its internal database connection pool or by defining an interface that maps an underlying table. In this way is avoided the proliferation of different database system and data management is centralized by only one component reducing the management efforts. The BD at TaaS level offers also a set of functionalities to process Things’s data and sent it to the Service level BD component. In this way each TaaS BD inside a BETaaS instance, contribute to the service layer BD component data by providing its relevant information collected.


## BETaaS Component Info

The TaaS BETaaS Big Data Manager write data to a local [H2](http://www.h2database.com/html/main.html) database. It is used by otehr TaaS components also to store their operative data. All software components are under : 

	betaas-taas\betaas-taas-bigdatamanager
    
It is based on the following Apache Maven projects:

  	betaas-taas\betaas-taas-bigdatamanager\betaas-taas-bigdatamanager-core
  	betaas-taas\betaas-taas-bigdatamanager\betaas-taas-bigdatamanager-database

#### BETaaS services used

The BETaaS TaaS BDM does use the TaaS BETaaS database service, available under {LINK TO BetaaS TaaS database} to store thing information and data.

#### BETaaS Services provided

The BETaaS TaaS BDM provides service to BETaaS Service BDM, to retrieve and get data to be stored in Service SQL databases. It also provides service to BETaaS TaaS Context Manager to store thing data when is available.

### TaaS Big Data Manager Software Components

The BETaaS TaaS Big Data Manager is configured by the following properties inside the file *betaas.gateway.cfg* :
  
	taasdb_jdbc = jdbc:h2:file:*PATH* taasbdmdb;DB_CLOSE_DELAY=-1
	taasdb_user = sa
	taasdb_pwd = sa
  
These properties allows to specify the location where the database file created by H2 should be stored.

#### BETaaS Maven Project betaas-taas-bigdatamanager-database

The project betaas-taas-bigdatamanager-database provides the generic API to access BETaaS database in order to create tables and manipulate data. It is used by other TaaS components to store their data in per components table.

{ADD LINK}

#### BETaaS Maven Project betaas-taas-bigdatamanager-core

The project betaas-taas-bigdatamanager-database provides a service that allows to store and retrieve things information and data.

{ADD LINK}

###** About BETaaS Project**

#### ** About the consortium**

BETaaS is a STREP Research project developed and co-funded by the European Commission Research and Innovation 7th Framework Program by the [BETaaS Consortium](http://www.betaas.eu/consortium.html#.VEeGuhZvAgk) under the ICT theme (Call 8) of DG CONNECT.

#### ** About BETaaS License**

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

[http://www.apache.org/licenses/LICENSE-2.0](http://www.apache.org/licenses/LICENSE-2.0)

Unless required by applicable law or agreed to in writing, software  distributed under the License is distributed on an "AS IS" BASIS,  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and  limitations under the License.


2014 All Rights Reserved by HP
