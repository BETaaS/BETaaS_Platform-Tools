**BETaaS Service Layer**
===========================================

## BETaaS Service Layer Architecture

The Service Layer is the unique contact point between applications and a BETaaS Instance. The role of this layer is to abstract all the internal functionalities provided by BETaaS to the applications exploiting things available in the environment. 

This layer provides the mechanisms for applications to request their installation in the instance and, moreover, it exposes the API applications need to subscribe or access to the required data from things, not needing to be aware of the technical complexity of the protocols used or the allocation of things. It provides the way to request complex big data tasks as well.

Moreover, this layer also provides functionalities for enabling the management of BETaaS instances, by managing the list of gateways which are part of the instance.

## BETaaS Service Layer Components

The following components are available in this layer:

* Instance Manager 
* Service Manager  
* Service Big Data Manager 
* Service Security Manager 

### Instance Manager

The Instance Manager is in charge of the management of a BETaaS Instance. It is responsible of controlling the join and disjoin processes of gateways, carrying out all the tasks needed to facilitate synchronization of information among components, so the Instance status will be coherent.

[More details about Instance Manager](/betaas-docs/github/betaas-service-im-component.md)

### Service Manager

The Service Manager is the bridge between applications and the services and data provided by the BETaaS Instance. It manages the application installation process and those activities related to subscriptions and data retrieval, as well as Big Data functionalities.

[More details about Service Manager](/betaas-docs/github/betaas-service-sm-component.md)

### Service Big Data Manager

The Big Data at the Service layer focuses its functionalities on the provision of Big Data tasks by exploiting Hadoop capabilities. These capabilities are offered to applications so they can get added value information after processing raw data.

[More details about Big Data Manager](/betaas-docs/github/betaas-service-bdm.md)

### Service Security Manager

This component exposes the APIs for performing security related tasks, such as loading and validating certificates. It uses functionalities exposed by the TaaS Layer in order to provide these functionalities for applications (supporting the installation process, for instance).

[More details about Security Manager](/betaas-docs/github/betaas-service-secm-component.md)

## About BETaaS Project

#### ** About the consortium**

BETaaS is a STREP Research project developed and co-funded by the European Commission Research and Innovation 7th Framework Program by the [BETaaS Consortium](http://www.betaas.eu/consortium.html#.VEeGuhZvAgk) under the ICT theme (Call 8) of DG CONNECT.

#### ** About BETaaS License**

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

[http://www.apache.org/licenses/LICENSE-2.0](http://www.apache.org/licenses/LICENSE-2.0)

Unless required by applicable law or agreed to in writing, software  distributed under the License is distributed on an "AS IS" BASIS,  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and  limitations under the License.


2014 All Rights Reserved by [BETaaS Project](www.BETaaS.eu)
