**BETaaS TaaS Resources Manager**
===================

The TaaS Resources Manager is in charge of managing all the resources available in a gateway and in an instance, being these computational resources (i.e. CPU, memory, storage, etc...) as well as sensors and actuators. The component keeps a list of resources available and allocates them to the applications which request their installation in a BETaaS instance.

Basically, when installing applications, the component goes through all the requested features and it assigns a list of thing services which will fulfill the required feature:

- The list of initial thing services is obtained from the Context Manager, according to the given information (feature name, location...);
- The initial list is pre-filtered by using the trust model of the Security Manager, removing those thing services which have low trust;
- The list is filtered again depending on the QoS parameters requested, in such a way the QoS Manager will allocate those thing services most appropiate for the application depending on the current context of the instance.

Although the thing services are allocated, they will be managed internally by the TaaS Resources Manager always, since the application only receives an identifier to be invoked. This means that the TaaS Resources Manager also is in charge of carrying out the invocations when an application subscribes or gets data from the set of things represented by the identifier.

                                                                                                                                                                     
## BETaaS Component Info

The TaaS Resources Manager is located at 

	betaas-taas\betaas-taas-taasresourcesmanager
    betaas-taas\betaas-taas-itaasresourcesmanager
    
and it is not based on any Apache Maven projects. While the first project represents the implementation of the component, the second one represents its interfaces and the Java classes used for interchanging data with other BETaaS components.

    
#### BETaaS services used

The TaaS Resources Manager uses services from:

- Security Manager, for obtaining trust evaluations, for obtaining security tokens and for validating security tokens.
- TaaS Context Manager, to retrieve the initial list of candidate thing services, when allocating things to an application.
- QoS Manager, to filter thing services and optimize their allocation when installing an application.
- VM Manager, to request the deployment of new VMs, as well as their deletion for releasing resources.
- Service Manager, to notify about installations as well as data notifications coming from things.
- Things Adaptor, to invoke directly things if required (pull mechanisms for accessing data).
- Other TaaS Resources Managers, to broadcast information about available resources and to interact when allocating and accessing to resources, so resources from other gateways can be reserved and used.


#### BETaaS Services provided

The TaaS Resources Manager provides services to:

- Allocate resources to applications requesting concrete features from a BETaaS instance.
- Invoke those thing services allocated for executing a concrete feature, previously allocated.
- Manage resources in the instance, supporting a distributed interaction among several gateways, and subscriptions, when these are requested by applications.
- Manage the allocation of VMs when required to do so, determining when to deploy locally or remotely.

### TaaS Resources Manager Software Components

The BETaaS TaaS Resources Manager does not require any property from the file *betaas.gateway.cfg* :
  

###** About BETaaS Project**



#### ** About the consortium**

BETaaS is a STREP Research project developed and co-funded by the European Commission Research and Innovation 7th Framework Program by the [BETaaS Consortium](http://www.betaas.eu/consortium.html#.VEeGuhZvAgk) under the ICT theme (Call 8) of DG CONNECT.

#### ** About BETaaS License**

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

[http://www.apache.org/licenses/LICENSE-2.0](http://www.apache.org/licenses/LICENSE-2.0)

Unless required by applicable law or agreed to in writing, software  distributed under the License is distributed on an "AS IS" BASIS,  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and  limitations under the License.


2014 All Rights Reserved by ATOS Spain 