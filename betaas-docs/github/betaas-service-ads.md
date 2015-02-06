**BETaaS Application Data Service Manager**
===================


BETaaS Application Data Service (ADS) at Service layer, manages access to Data Task. A data task is a service implemeting an interface that allows application to rereive result returned after a processing of data stored in BDM service SQL database.


## BETaaS Component Info

The Projects implementing the Service BDM Manager are inside the folder: 

	betaas-service\betaas-service-bigdatamanager
    
In particulare the following Apache Maven projects within the parent folder,implements the basic BDM services:

  	-    betaas-service-bigdatamanager-data\betaas-service-bigdatamanager\betaas-service-bigdatamanager-service\betaas-service-bigdatamanager-service-application-data-service
  	   

#### BETaaS services used

The BETaaS Service ADS does use the DataTask Services dynamically. When the list of task available is requested, the components contacts each service implemnting the DataTask interface to retrieve their description and their ids. 

#### BETaaS Services provided

The BETaaS ADS service does not directly provides services to an application, but is coordinated by the BETaaS Service Manager. The Service Manager on behalf of an application, contacts the ADS to retrieve the list of available data task, their description and their ID. Then whenever an application has selected one of these tasks and request its execution to the the Service Manager, the ADS is contacted in order to retrieve and run the corresponding data task.

### Service Big Data Manager Software Components

The BETaaS ADS Manager is configured only inside the file *betaas.endpoints.cfg* where its DOSGi service port is set :
  
    serviceADSAddress = http://gatewayIP:18002/ads
    
At least one BETaaS ADS Service for instance is required for exectuing deployed data task.

#### BETaaS Maven Project betaas-service-bigdatamanager-service-application-data-service

The project defines the ADS Service interfaces. It depends on the Data Task interface. Details about its implementation are [here](/betaas-docs/github/betaas-service-datatask.md) 

###** About BETaaS Project**

#### ** About the consortium**

BETaaS is a STREP Research project developed and co-funded by the European Commission Research and Innovation 7th Framework Program by the [BETaaS Consortium](http://www.betaas.eu/consortium.html#.VEeGuhZvAgk) under the ICT theme (Call 8) of DG CONNECT.

#### ** About BETaaS License**

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

[http://www.apache.org/licenses/LICENSE-2.0](http://www.apache.org/licenses/LICENSE-2.0)

Unless required by applicable law or agreed to in writing, software  distributed under the License is distributed on an "AS IS" BASIS,  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and  limitations under the License.


2014 All Rights Reserved by HP
