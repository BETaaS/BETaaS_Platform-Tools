**BETaaS TaaS Context Manager**
===================


The Context Manager generates the thing services, which is the way in which the TaaS Layer exposes basic services which can be mapped directly to one or more things. When a thing is connected to BETaaS, the ThingServicesManager generates both the thing service and its associated thing-service-id, which like the rest of the information related to things, is stored into the BETaaS ontology. 
The Context Manager also generates the services to be used by applications. These services are associated to the thing services, though this relationship is not one to one. 
                                                                                                                                                                     
## BETaaS Component Info

The TaaS Context Manager is located at 

	betaas-taas\betaas-taas-contextmanager
    
and it is not based on any Apache Maven projects.

    
#### BETaaS services used

The TaaS Context Manager uses services from:

- Big Data Manager at Service layer to store data into the DB, sending the Big Data Manager all the information about the Things registered in BETaaS, together with the data that those Things are providing.
- TaaS Resource Manager, to inform when new Things have been connected and need to be published; and also to notify about new measurements.


#### BETaaS Services provided

The TaaS Context Manager provides services to:

- Adaptation Context Manager providing an interface with the BETaaS ontology to insert or update information from it.
- TaaS Resource Manager providing an interface with the BETaaS ontology to retrieve information from the Things and the Thing Services.
- QoS Monitoring providing an interface with the BETaaS ontology to retrieve information from the Things and the Thing Services.

### TaaS Context Manager Software Components

The BETaaS Context Manager does not require any property from the file *betaas.gateway.cfg* :
  

###** About BETaaS Project**



#### ** About the consortium**

BETaaS is a STREP Research project developed and co-funded by the European Commission Research and Innovation 7th Framework Program by the [BETaaS Consortium](http://www.betaas.eu/consortium.html#.VEeGuhZvAgk) under the ICT theme (Call 8) of DG CONNECT.

#### ** About BETaaS License**

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

[http://www.apache.org/licenses/LICENSE-2.0](http://www.apache.org/licenses/LICENSE-2.0)

Unless required by applicable law or agreed to in writing, software  distributed under the License is distributed on an "AS IS" BASIS,  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and  limitations under the License.


2014 All Rights Reserved by Intecs Spa