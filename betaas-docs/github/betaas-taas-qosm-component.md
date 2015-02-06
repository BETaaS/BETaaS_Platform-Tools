**BETaaS TaaS QoS Manager**
===================


The QoS Manager reserves the Thing Services in real-time invocations in order to meet applications requirements. It exploits the equivalence between Thing Services to choose the best Thing Service on each invocations. The overall procedure is exploited in two steps: reservation and dispatching. The former is accomplished during application installation and reserve Thing Services based on application's requirements and already committed flows. The reservation procedure is executed on the QoSM\* which is the main QoSM with a glogal view of the overall BETaaS instance.
The latter is performed at time of invocations and is in charge of selecting the previously reserved Thing Service for each application invocation. If such choice is unfeasible the QoSM Dispatcher selects another feasible Thing Service (if possible) and also notifies to the QoSM\* that a new reservation schema is required.

## BETaaS Component Info

The Service BETaaS Instance Manager is located at: 

	betaas-taas\betaas-taas-qosmanager
	
and it is not based on any Apache Maven projects.

#### BETaaS services used

The TaaS QoS Manager uses services from:

- TaaS Resource Manager, for obtaining equivalents Thing Services.
- Service Manager, to notify about QoS agreements.
- Things Adaptor, to retrieve thing contexual information.
- Big Data Manager at TaaS layer to store data into the DB.
- TaaS Context Manager, to retrieve information about Things and Thing Services.
- Other TaaS QoS Managers to interact with the QoSM*.

#### BETaaS services provided

The TaaS QoS Manager provides services to:

- Service Manager providing an interface to negotiate application's requirements.
- TaaS Resource Manager providing an interface to reserve and dispatch Thing Services.

### QoS Manager Software Configuration

The BETaaS QoS Manager does not require any property from the file *betaas.gateway.cfg* :

###** About BETaaS Project**

#### ** About the consortium**

BETaaS is a STREP Research project developed and co-funded by the European Commission Research and Innovation 7th Framework Program by the [BETaaS Consortium](http://www.betaas.eu/consortium.html#.VEeGuhZvAgk) under the ICT theme (Call 8) of DG CONNECT.

#### ** About BETaaS License**

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

[http://www.apache.org/licenses/LICENSE-2.0](http://www.apache.org/licenses/LICENSE-2.0)

Unless required by applicable law or agreed to in writing, software  distributed under the License is distributed on an "AS IS" BASIS,  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and  limitations under the License.


2014 All Rights Reserved by University of Pisa, Italy
