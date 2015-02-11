**BETaaS Service Dependability Manager**
===================


Dependability is one of the extended capabilities of BETaaS. It is managed at the service layer by **Dependability Manager** since it provides a sort of service to the platform itself. 

The main functionalities are:

- Anomalies and failure notification. A SW component of each system level has the possibility to notify some unexpected events; moreover a centralized vitality check mechanism is in charge of discovering unreachable modules
- Perform some recovery processes, in particular related to GW or SW module restart
- Inform the BETaaS administrator about system failures by means of a specific Web application. A set of predefined statistics is available to allow retrieving synthetic data from the repository of occurred failures.


## BETaaS Component Info

The Dependability Manager is designed to receive notifications from any other component of BETaaS. It is located at

	betaas-service\betaas-service-dependabilitymanager
    
It is based on the following Apache Maven sub-projects:

  	betaas-service\betaas-service-dependabilitymanager\betaas-service-dependabilitymanager-server
  	betaas-service\betaas-service-dependabilitymanager\betaas-service-dependabilitymanager-web

#### BETaaS services used

The BETaaS Dependability Manager uses the Service BETaaS database service, to store information about received failure notifications.

#### BETaaS Services provided

The Dependability Manager provides a service to BETaaS components to receive failure notifications.

### Depedability Manager Software Components

Currently the Dependability Manager does not require any configuration to be set.

#### BETaaS Maven Project betaas-service-dependabilitymanager-server

The project betaas-service-dependabilitymanager-server provides the API to notify failures so that it can store them into the GW database.

#### BETaaS Maven Project betaas-service-dependabilitymanager-web

The project betaas-service-dependabilitymanager-web was originally intented to provide a GUI to check the incoming failure notification. Currently it is under evaluation the possibility to use an existing tool that allow to manage a queue of notifications through a Web application. That queue would 

###** About BETaaS Project**

#### ** About the consortium**

BETaaS is a STREP Research project developed and co-funded by the European Commission Research and Innovation 7th Framework Program by the [BETaaS Consortium](http://www.betaas.eu/consortium.html#.VEeGuhZvAgk) under the ICT theme (Call 8) of DG CONNECT.

#### ** About BETaaS License**

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

[http://www.apache.org/licenses/LICENSE-2.0](http://www.apache.org/licenses/LICENSE-2.0)

Unless required by applicable law or agreed to in writing, software  distributed under the License is distributed on an "AS IS" BASIS,  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and  limitations under the License.


2014 All Rights Reserved by Intecs Spa
