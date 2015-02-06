**BETaaS Things as a Service (TaaS) Layer**
===========================================

## BETaaS TaaS Layer Architecture

The Thing as a Service (TaaS) layer is in charge of those functionalities which have to do with thing services, as a representation of things. Basically, this layer is focused on resources management, not looking at them as concrete things (i.e. a humidity sensor connected through Zigbee), but just as thing services and other computational resources which can be used by applications.

As such, this layer takes care of resources allocation as upper layers request them (i.e. when installing an application for a BETaaS instance) based on their semantic representation. Moreover, this layer facilitates the access to the resources by locating the concrete resources to be invoked.

Moreover, other functionalities are managed at the TaaS layer, such as QoS for thing services, some security features and trust management. All these are related to the resources allocation process, but this layer also manages functionalities related to Big Data management and VMs management (deployment and usage of computational resources).

## BETaaS TaaS Layer Components

The following components are available in this layer:

* TaaS Resources Manager 
* TaaS Context Manager 
* QoS Manager 
* VM Manager 
* TaaS Big Data Manager 
* TaaS Security Manager 

### TaaS Resources Manager

This component is the responsible of carrying out the process of resources allocation when requested by components in the upper layer. It manages resources in the instance, by keeping track of all the resources available. It also supports access to resources by means of pull and push mechanisms.

Moreover, this component is also in charge of some management tasks related to VMs management, by selecting the way to proceed when new computational resources are requested.

### TaaS Context Manager

The TaaS Context Manager is the component hosting the BETaaS ontology and it provides the means to identify which thing services should be allocated to a requested feature. It is the one representing things as thing services and it enables a distributed management of resources.

### QoS Manager

The QoS Manager is the component in charge of negotiating QoS conditions for applications, according to the required conditions in the application manifest. During the allocation process, it is also in charge of selecting the best thing services for a concrete feature request, depending on the current context of an instance. Finally, it must determine whether SLAs are fulfilled or not by thing services.

### VM Manager

This component manages the creation, modification and deletion of VMs running in a BETaaS instance. It can retrieve information about available resources in the local gateway and it is able to use virtualization technologies for deploying VMs locally. In the case a remote Cloud is available, it may deploy VMs in certain cloud infrastructures (Open Nebula and Open Stack).

### TaaS Big Data Manager

The TaaS Big Data Manager represents the storage solution for the rest of components in the BETaaS gateway. It provides special APIs for certain operations (i.e. storing and retrieving things data) and generic APIs for executing SQL sentences.

### TaaS Security Manager

At the TaaS layer, the Security Manager is in charge of calculating trust for thing services according to the corresponding trust model. Moreover, it is in charge of the security tokens management, so it is possible to validate that operations requested by components at the upper layer (the Service Layer) are authorized.

## About BETaaS Project

#### ** About the consortium**

BETaaS is a STREP Research project developed and co-funded by the European Commission Research and Innovation 7th Framework Program by the [BETaaS Consortium](http://www.betaas.eu/consortium.html#.VEeGuhZvAgk) under the ICT theme (Call 8) of DG CONNECT.

#### ** About BETaaS License**

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

[http://www.apache.org/licenses/LICENSE-2.0](http://www.apache.org/licenses/LICENSE-2.0)

Unless required by applicable law or agreed to in writing, software  distributed under the License is distributed on an "AS IS" BASIS,  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and  limitations under the License.


2014 All Rights Reserved by [BETaaS Project](www.BETaaS.eu)