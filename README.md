**BETaaS Project**
===================

**Start building applications for the future**
**BETaaS** can help you overcome the limitations of current M2M applications platforms.
**BETaaS technology** provides a runtime platform that simplifies the deployment and execution of content-centric M2M applications, with an horizontal abstraction which relies on a local cloud of gateways, enabling the use of Things as a Service over heterogeneous things. 
**BETaaS platform** provides a uniform interface and services to map content (information) with things (resources) in a context-aware fashion. With **BETaaS**, the deployment of services for the execution of applications will be dynamic and will take into account the computational resources of the low-end physical devices used.
The platform also provides **BETaaS Things Simulator** to enable testing of simulated scenarios in conjunction with current deployed real scenarios.

## BETaaS Architecture

BETaaS Instances are organized with an especial gateways architecture: a centralized and a distributed architecture. Even if this sounds a bit confusing or even contradictory, it makes sense to do it this way, in order to gain efficiency and robustness while we still provide flexibility and control over certain operations.
There are some operations, which require the intervention of a central component which will orchestrate certain interactions and decision making processes (what we call the star component). This is the case of some QoS management operations, when negotiating QoS conditions. It is also the case of the instances management process, in which we prefer to have only one access point which will avoid new gateways to request joining an instance by contacting several gateways at the same time (creating race conditions an
d potential inconsistencies related to the decision of accepting or rejecting the new gateway). Not performing some operations in a centralized way may increase the risk of certain attacks to the platform or just keep the instance in an inconsistent status.

![Architecture of BETaaS](/betaas-docs/github/images/blayers.JPG)

On the other hand, there are other operations which do not require a concrete component to orchestrate any process, since they may be performed through direct interactions. Facilitating a distributed execution of these operations is good for the instance, since we may gain in efficiency, avoid bottlenecks, store the instance status in a distributed manner and avoid inconsistencies because of components keeping a different point of view about the instance. This is the case, for example, of the resources synchronization and registration in an instance, which is performed in a distributed way, being the corresponding component responsible of broadcasting any change in the list of known resources, so all the gateways will be aware of the new situation.

The three layers of BETaaS are:

* BETaaS adaptation layer
* BETaaS TaaS(Thing as a Service) layer
* BETaaS Service Layer

Details about architecture are available [here](http://www.betaas.eu/docs/deliverables/BETaaS%20-%20D3.1.2%20BETaaS%20Architecture%20v1.0.pdf)



#### BETaaS Adaptation Layer

Adaptation layer offer the following capabilities to BETaaS:

The Adaptation Layer aims at providing a common interface to the TaaS layer regardless of the underlying IoT/M2M system. Through this common set of APIs, the TaaS can access the functionalities offered by any IoT/M2M system in a uniform manner.
The Adaptation Layer relies on a set of basic functionalities which are assumed to be provided by the Physical Layer.
For each IoT/M2M system which is integrated in BETaaS, a specific implementation of the Adaptation Layer has to be provided through a plugin. Nevertheless, the Adaptation Layer exposes a uniform set of functionalities to the TaaS layer. A BETaaS gateway can run different instances of the Adaptation Layers concurrently to interconnect to different local IoT/M2M systems. 

[More details about Adaptation Layer](/betaas-docs/github/betaas-adaptation-layer.md)

#### BETaaS TaaS Layer

TaaS enables the service layer to access things as a service.
TaaS is implemented in a distributed manner: each gateway implements a TaaS local component which connects to the others to provide access to the things transparently regardless of their location in the network. A service requiring access to one thing, interacts with its TaaS local component, the unique interface towards the things. The local component is then responsible for accessing the thing through its own Adaptation Layer, if the thing is connected to the local network, or for requesting the service to the TaaS local component of the gateway where the thing is connected. The interconnection of all the TaaS local components forms an overlay which allows services to access the things regardless of their physical location through its local component which is a single point of access 

[More details about TaaS Layer](/betaas-docs/github/betaas-taas-layer.md)

#### BETaaS Service Layer

The Service Layer is built on top of TaaS: it provides services to applications leveraging on the things accessed through the TaaS. The Service layer implements the basic and extended capabilities of the BETaaS platform. The abstraction provided by TaaS allows services to access the things as they were connected the local gateway without have knowledge of their physical location. 

[More details about Service Layer](/betaas-docs/github/betaas-service-layer.md)

## Getting Started with BETaaS:

In order to star using BETaaS, first you will need to setup the environment:

* [Setup and deployment of BETaaS](/betaas-docs/github/installationofBETaaS.md)

In order to speed up development, Instant BETaaS, an Ubuntu virtual machine with BETaaS installed and configured is provided:
* [Instant BETaaS](/betaas-docs/github/how-to-instantbetaas.md)

### BETaaS Tutorials:

The following examples are provided:

* [How to build a things adapter for BETaaS](/betaas-docs/github/how_to_write_TA_Plugin.md)
* [Step by Step BETaaS application How To](/betaas-docs/github/how-to-AndroidApp.md)
* [How to build a Data Task](/betaas-docs/github/betaas-service-datatask.md)
* [How to run BETaaS on UDOO board](http://www.hackster.io/betaas-consortium/udoo-betaas1)

### BETaaS Demo Applications:

This application are provided in the current code release:

* [BETaaS Intrusion detection application](/betaas-docs/github/how-to-IntrusionDetection.md)
* [BETaaS LEZ application](/betaas-docs/github/how-to-LEZ.md)

### BETaaS Thing Simulator:

We do provide simulators for things. The following guides will help you to use them :

* [How to use a BETaaS Thing Simulator](/betaas-docs/github/how_to_use_TA_simulator.md)
* [How to use a BETaaS COAP Plugin](/betaas-docs/github/coap-plugin-how-to.md)

## About BETaaS Project

BETaaS (Building the Environment for the Things as a Service) is a STREP Research project developed and co-funded by the European Commission Research and Innovation 7th Framework Program by the [BETaaS Consortium](http://www.betaas.eu/consortium.html#.VEeGuhZvAgk) under the ICT theme (Call 8) of DG CONNECT.

### **About BETaaS Software License**

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

[http://www.apache.org/licenses/LICENSE-2.0](http://www.apache.org/licenses/LICENSE-2.0)

Unless required by applicable law or agreed to in writing, software  distributed under the License is distributed on an "AS IS" BASIS,  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and  limitations under the License.


2014 All Rights Reserved by [BETaaS Project](www.BETaaS.eu)
