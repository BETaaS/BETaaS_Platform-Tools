**BETaaS Adaptation Context Manager**
===================


The Adaptation Context Manager offers an interface to the ThingsAdaptor through the SemanticParserAdaptor.

This subcomponent is responsible for translating into a semantic format the data that is emitted or received by the Things connected to BETaaS. Once the information emitted by a Thing has been processed by the ThingsAdaptor, this component invokes the SemanticParserAdaptor which translates the data into a semantic format that is mapped into the BETaaS ontologies. 
The BETaaS Thing Ontology stores all the information related to the measurements that the Things connected to a gateway take.  
                                                                                                                                                                     
## BETaaS Component Info

The Adaptation Context Manager is located at 

	betaas-adaptation\betaas-adaptation-contextmanager

It is based on the following Apache Maven projects:

  	betaas-taas\betaas-taas-contextmanager

    
#### BETaaS services used

The Adaptation Context Manager uses services from:

- TaaS Context Manager, allowing it to use the semantic capabilities.
- TaaS Resource Manager, to inform when new Things have been connected and need to be published; and also to notify about new measurements.
- Big Data Manager at Service layer to store data into the DB.

#### BETaaS Services provided

Adaptation Context Manager receives information from the Things Adaptation Layer when the status of the Things or its' measurements need to be update. These means, translation of the information retrieved/sent from/to the Things into a semantic format. This information is mapped into an ontology.

The Adaptation Context Manager is in changer of managing the subscriptions/unsubscriptions made by the applications that use BETaaS to the elements in the ontology. The requests come from the TaaSResourceManager.

### Adaptation Context Manager Software Components

The BETaaS Adaptation Context Manger is configured by the following properties inside the file [*betaas.gateway.cfg*] (/betaas-configuration/configuration/betaas.gateway.cfg) :
  
    gwId
    
This property indicates the gateway id.

#### BETaaS Maven Project betaas-taas-contextmanager

The project betaas-taas-contextmanager provides an interface with the BETaaS ontology to insert or update information from it.

[More details about TaaS Context Manager](/betaas-docs/github/betaas-taas-cm-component.md)



###** About BETaaS Project**



#### ** About the consortium**

BETaaS is a STREP Research project developed and co-funded by the European Commission Research and Innovation 7th Framework Program by the [BETaaS Consortium](http://www.betaas.eu/consortium.html#.VEeGuhZvAgk) under the ICT theme (Call 8) of DG CONNECT.

#### ** About BETaaS License**

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

[http://www.apache.org/licenses/LICENSE-2.0](http://www.apache.org/licenses/LICENSE-2.0)

Unless required by applicable law or agreed to in writing, software  distributed under the License is distributed on an "AS IS" BASIS,  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and  limitations under the License.


2014 All Rights Reserved by Intecs Spa
