**BETaaS VM Manager**
===================

The VM Manager is a component focused on providing those functionalities directly related to VM management in the local environment and in remote Clouds, whenever possible. This component retrieves information from the local computational resources, reporting to the TaaS Resources Manager which resources can be used. 

When requested, it deploys VMs locally by using libvirt and pre-defined flavors, each one customized to perform concrete functions in a BETaaS instance and with certain resources assigned for its optimal operation. In case the VM Manager is configured for working with an external Cloud, it will be able to connect with the remote infrastructure provider for deploying VMs. For now, only Open Nebula and OpenStack are supported.
       
## BETaaS Component Info

The VM Manager is located at 

	betaas-taas\betaas-taas-vmmanager
    
and it is not based on any Apache Maven projects. 
    
#### BETaaS services used

The VM Manager does not need to use services from other BETaaS components. The only thing it needs to do is to interact with other VM Managers in the same instance in order to request the deployment of VMs and to retrieve information about the resources available in other gateways.

#### BETaaS Services provided

The VM Manager provides services to:

- Provide the computation resources available in the instance.
- Perform local deployment of VMs
- Perform remote deployment of VMs

### VM Manager Software Components

The BETaaS VM Manager does not require any property from the file *betaas.gateway.cfg* :
  

###** About BETaaS Project**



#### ** About the consortium**

BETaaS is a STREP Research project developed and co-funded by the European Commission Research and Innovation 7th Framework Program by the [BETaaS Consortium](http://www.betaas.eu/consortium.html#.VEeGuhZvAgk) under the ICT theme (Call 8) of DG CONNECT.

#### ** About BETaaS License**

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

[http://www.apache.org/licenses/LICENSE-2.0](http://www.apache.org/licenses/LICENSE-2.0)

Unless required by applicable law or agreed to in writing, software  distributed under the License is distributed on an "AS IS" BASIS,  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and  limitations under the License.


2014 All Rights Reserved by ATOS Spain 