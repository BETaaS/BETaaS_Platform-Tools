**BETaaS TaaS Security Manager**
===============
BETaaS Security Manager at TaaS layer, manages the security functionality at the TaaS layer, including the root certificate creation for the BETaaS instance, creating as well as validating certificate for the Gateways (GWs) that are member of the BETaaS instance, and to perform key agreement between two GWs in order to establish secure communication.

This component also includes the Trust Manager, a subcomponent in charge of calculating trust for things connected to the system, based on a trust model designed for analyzing security features of things, fulfillment of QoS parameters, scalability capacity, things reliability, energy availability and quality of provided data.

##BETaaS Component Info
All software components are under : 

	betaas-taas\betaas-taas-securitymanager
    
It is based on the following Apache Maven projects:

  	betaas-taas\betaas-taas-securitymanager\betaas-taas-securitymanager-common
  	betaas-taas\betaas-taas-securitymanager\betaas-taas-securitymanager-certificate
  	betaas-taas\betaas-taas-securitymanager\betaas-taas-securitymanager-authentication
  	betaas-taas\betaas-taas-securitymanager\betaas-taas-securitymanager-core
      betaas-taas\betaas-taas-trustmanager
    
    
#### BETaaS services used

#### BETaaS Services provided
The BETaaS TaaS Security Manager provides services to BETaaS Instance Manager in creating an instance certificate upon initiating an instance and also in creating a certificate for the GW that wants to join the already existing instance. 

It also provides APIs for accessing to trust evaluations of the things connected to the instance, according to the calculations performed.

### BETaaS TaaS Security Manager Software Components
The BETaaS TaaS Security Manager is configured by the following properties inside the file *betaas.gateway.cfg* [here](/betaas-configuration/configuration/betaas.gateway.cfg) :
  
    gwId
This property indicates the gateway ID.
  
    countryCode=
    state=
    location=
    orgName=
   These properties indicate the information needed for creating certificate for the gateways.

#### BETaaS Maven Project betaas-taas-securitymanager-common

The project betaas-taas-securitymanager-common provides common methods used in the other components of BETaaS TaaS Security Manager, especially those which are related to key generation, encryption, signature, and key agreement. Note that ECC (Elliptic Curve Cryptography) is used in all of the operation mentioned.
{ADD LINK}

#### BETaaS Maven Project betaas-taas-securitymanager-certificate
The project betaas-taas-securitymanager-certificate provides generic APIs to other BETaaS security manager components to create certificate, both for the instance and gateways, and also to store and load certificate in a gateway. Please note that all the certificates are stored in a directory whose location can be configured in *betaas.gateway.cfg* file with the following property: *certificatePath*. Example of certificate location configuration is: 
	certificatePath = {PATH}/betaas/data/securityConfig/certificate/

#### BETaaS Maven Project betaas-taas-securitymanager-authentication
The project betaas-taas-securitymanager-authentication provides generic APIs to other BETaaS security manager component (i.e. betaas-taas-securitymanager-core) to establish a key agreement protocol based on ECMQV (Elliptic Curve Menezes-Qu-Vanstone) between two gateways. 
It also provides API to encrypt and decrypt the communicated messages between two gateways using the key derived from the key agreement protocol mentioned earlier.

#### BETaaS Maven Project betaas-taas-securitymanager-core
The project betaas-taas-securitymanager-core  provides generic APIs to other BETaaS components outside the security manager to create certificate for both the instance and gateways, as well as to establish key agreement between two gateways for secure communication. So, basically this component encapsulate the complex security mechanisms into a set of simple APIs to the other components in BETaaS.

#### BETaaS Maven Project betaas-taas-trustmanager
The project betaas-taas-trustmanager provides an API which allows activating frequent trust assessments for a concrete thing. Moreover, it is possible to access to the trust evaluations through the exposed API, in order to know how trustoworthy a thing is with respect to the model proposed by BETaaS.


### *About BETaaS Project*
#### *About the consortium*

BETaaS is a STREP Research project developed and co-funded by the European Commission Research and Innovation 7th Framework Program by the [BETaaS Consortium](http://www.betaas.eu/consortium.html#.VEeGuhZvAgk) under the ICT theme (Call 8) of DG CONNECT.

#### *About BETaaS License*

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

[http://www.apache.org/licenses/LICENSE-2.0](http://www.apache.org/licenses/LICENSE-2.0)

Unless required by applicable law or agreed to in writing, software  distributed under the License is distributed on an "AS IS" BASIS,  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and  limitations under the License.


2014 All Rights Reserved by CTIF, Aalborg University, and ATOS
