**BETaaS Adaptation Layer**
===========================================

## BETaaS Adaptation Architecture

The Adaptation Layer is the bridge between the physical things in the environment and the BETaaS platform. This layer enables the mechanisms so BETaaS can abstract the protocols used by each thing to the upper layer (the Tings as a Service Layer). This layer hosts several adaptors implemented as plug-ins, so it is possible to interact with things through CoAP, ETSI, etc...

Moreover, there is a set of semantic functionalities which annotate the things registered, providing some additional information which will be used in the TaaS Layer. This additional information supports BETaaS in its understanding of the purpose and way to work of the things connected.

Finally, the current implementation provides a things simulator as well, with the intention to facilitate testing applications easily and at a very low cost, since no physical things are needed in order to install and use applications.

## BETaaS TaaS Layer Components

The following components are available in this layer:

* Things Adaptor 
* Adaptation Context Manager 
* Things Simulator 

### Things Adaptor

This is the component in charge of interacting with things directly. It registers the different plug-ins available and it uses them for discovering and accessing things, sending the data gathered to upper layers. It can be used for PULL and PUSH modes.

Currently, there are plug-ins for interacting with ETSI platforms, with the CONVERGE Ecosystem platform and with CoAP sensors. BETaaS can be extended with more plug-ins, in case developers want to extend the BETaaS support for IoT protocols. 

[More details about Things Adaptor](/betaas-docs/github/betaas-adaptation-things-adaptor.md)

### Adaptation Context Manager

The Context Manager belonging to the Adaptation Layer is in charge of semantic things annotations. It uses the ontologies network designed in BETaaS in order to understand the purpose and characteristics of the things connected to the instance.

[More details about Context Manager](/betaas-docs/github/betaas-adaptation-cm-component.md)

### Things Simulator

Since we aim at facilitating the testing of the BETaaS Platform, the Adaptation Layer includes a things simulator which will register several things in an instance and will generate data as if it was produced by those things. It can be configured for simulating different things and different data easily.

[How to use the things simulator](/betaas-docs/github/how_to_use_TA_simulator.md)

## About BETaaS Project

#### ** About the consortium**

BETaaS is a STREP Research project developed and co-funded by the European Commission Research and Innovation 7th Framework Program by the [BETaaS Consortium](http://www.betaas.eu/consortium.html#.VEeGuhZvAgk) under the ICT theme (Call 8) of DG CONNECT.

#### ** About BETaaS License**

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

[http://www.apache.org/licenses/LICENSE-2.0](http://www.apache.org/licenses/LICENSE-2.0)

Unless required by applicable law or agreed to in writing, software  distributed under the License is distributed on an "AS IS" BASIS,  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and  limitations under the License.


2014 All Rights Reserved by [BETaaS Project](www.BETaaS.eu)
