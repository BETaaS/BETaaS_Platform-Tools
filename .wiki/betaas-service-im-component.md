**BETaaS Service Instance Manager**
===================


BETaaS Instance Manager (IM) at Service layer manages is in charge of managing the operations related to the topology of the BETaaS instance, as a cloud of gateways.
BETaaS gateways can join together to share their Thing Services. Instance Managers of different GWs cooperate to decide which is the IM\*, i.e. the main Instance Manager in charge of accepting the join/disjoin requests.

## BETaaS Component Info

The Service BETaaS Instance Manager is located at: 

	betaas-service\betaas-service-instancemanager
    
It is based on the following Apache Maven sub-projects:

  	betaas-service\betaas-service-instancemanager\betaas-service-instancemanager-server
  	betaas-service\betaas-service-instancemanager\betaas-service-instancemanager-web

#### BETaaS services used

Instance Manager uses the Security Manager services to authenticate GWs requesting to join/disjoin. It also uses other components (currently TaaS Resource Manager) API to control their synchronization after changes in the BETaaS instance topology.

#### BETaaS Services provided

Instance Manager provides the API to request other GWs the join/disjoin. It also provides the interface to allow the administrator to perform instance management procedures on demand (through remote applications).

### Instance Manager Software Configuration

The Instance Manager is configured by the following properties inside the file *betaas.gateway.cfg* :
  
	credentials=na
	instanceDescription=Low Emission Zone Gateway
	adminAddress=http://192.168.31.44:8080/InstanceManager/
	automaticJoin = 0       
	trackerWaitTime=15000
  
Currently *instanceDescription* and *adminAddress* are used to be shown and linked by the Web administration page of IM. *automaticJoin*, if set to 1, makes the GW to try joining to any existing IM\* at startup. *trackerWaitTime* is the time that IM waits for at startup before searching for other visible GWs (it can be useful to allow all the bundles to start-up in time, it is in milliseconds).

#### BETaaS Maven Project betaas-service-instancemanager-server

The project betaas-service-instancemanager-server provides the generic API to perform operations on the BETaaS instance topology.

#### BETaaS Maven Project betaas-service-instancemanager-web

The project betaas-service-instancemanager-web implements a Web application used to administer the instance through a Web browser. It exploits the interfaces provided by the betaas-service-instancemanager-server.

###** About BETaaS Project**

#### ** About the consortium**

BETaaS is a STREP Research project developed and co-funded by the European Commission Research and Innovation 7th Framework Program by the [BETaaS Consortium](http://www.betaas.eu/consortium.html#.VEeGuhZvAgk) under the ICT theme (Call 8) of DG CONNECT.

#### ** About BETaaS License**

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

[http://www.apache.org/licenses/LICENSE-2.0](http://www.apache.org/licenses/LICENSE-2.0)

Unless required by applicable law or agreed to in writing, software  distributed under the License is distributed on an "AS IS" BASIS,  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and  limitations under the License.


2014 All Rights Reserved by Intecs Spa
