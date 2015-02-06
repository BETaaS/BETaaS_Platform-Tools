**BETaaS Service Security Manager**
===============
BETaaS Security Manager at Service layer manages the security functionality at the Service layer, including the application certificate validation, and creating, validating, updating as well as revoking the token for the applications.

##BETaaS Component Info
All software components are under : 

	betaas-service\betaas-service-securitymanager

#### BETaaS services used
The BETaaS Service Security Manager uses services from BETaaS TaaS Security manager for the services related to loading and validating certificates.

#### BETaaS Services provided
The BETaaS Service Security Manager provides services to BETaaS Service Manager for validating the application's certificate in the application installation procedure. It also provides services to BETaaS TaaS Resource Manager for creating and validating access token for the application.

### BETaaS TaaS Security Manager Software Components
The BETaaS Service Security Manager requires a BETaaS Apps Store certificate in order to validate the application's certificate and optionally an access right condition in an XML file for creating an access token. Those files need to be stored under a directory in the Apache Karaf Home which is configured in *betaas.gateway.cfg*. Examples of the configuration is:
	certificatePath = {PATH}/betaas/data/securityConfig/certificate/
	conditionPath = {PATH}/betaas/data/securityConfig/condition/
	
Then, the following files that can be found in the *betaas/betaas-configuration/configuration*, should be placed to the respective above mentioned directories:
    AppStoreCertInter.p12 
    condition1.xml
	
In order to test your application, a certificate file named: *testAppsCert.p12* is provided also in *betaas/betaas-configuration/configuration*. It is necessary to attach this certificate to your application to successfully installing it in the platform.

### *About BETaaS Project*
#### *About the consortium*

BETaaS is a STREP Research project developed and co-funded by the European Commission Research and Innovation 7th Framework Program by the [BETaaS Consortium](http://www.betaas.eu/consortium.html#.VEeGuhZvAgk) under the ICT theme (Call 8) of DG CONNECT.

#### *About BETaaS License*

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

[http://www.apache.org/licenses/LICENSE-2.0](http://www.apache.org/licenses/LICENSE-2.0)

Unless required by applicable law or agreed to in writing, software  distributed under the License is distributed on an "AS IS" BASIS,  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and  limitations under the License.


2014 All Rights Reserved by CTIF, Aalborg University
