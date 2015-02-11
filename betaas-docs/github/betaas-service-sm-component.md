**BETaaS Service Manager**
===================


Service Manager at Service layer is the component in charge of providing the BETaaS API to external application and extended services so that they can access BETaaS.
                                                                                                                                                                     
## BETaaS Component Info

The Service Manager is located at 

	betaas-service\betaas-service-servicemanager
    
#### BETaaS used services

The Service Manager uses services from:

- Quality of Service Manager for the Service Layer Agreement
- TaaS Resource Manager to allocate resources requested by applications and extended services
- Security Manager to authenticate application when they access BETaaS
- Big Data Manager at Service layer to store data into the DB

#### BETaaS provided Services

Service Manager provides API to the extern to access BETaaS and to TaaS Resource Manager to get notifications related to resources allocations and Thing Service data availability.

##### REST and SOAP API

The API provided by Service Manager to external applications comes both with **REST** and **SOAP**. The service description files are available at:
- **http://<IP>:8181/cxf/sm?_wadl** for **REST**, where *IP* refers to the Karaf host. This service description describes all the access points, type and parameters. For example:
    - to install/uninstall an application: POST/DELETE request to <base_URL>/application with the manifest in the body
    - to get the list of services installed for an application: **GET** request to *<base_URL>/application/{appID}*
    - to get data from a Thing Service **GET** request to **base_URL>//data/{appID}/{serviceID}*
    - to set some data using a Thing Service: **PUT** request to *<base_URL>/data/{appID}/{serviceID}/{data}*
    - to get data from an extended service: **GET** request to *<base_URL>/extended/{appID}/{extServUniqueName}*
    - to subscribe/unsubscribe to a Thing Service: **POST**/**DELETE** request to *<base_URL>/registration/{appID}/{serviceID}*
    - to get a BDM task result: **GET** request to *<base_URL>/task/{appID}/{taskID}*
- **http://<IP>:9304/sm-service?wsdl** for  **SOAP**, where *<IP>:9304* is the value configured in *betaas.endpoints.cfg* (Karaf *etc* folder) for the Service Manager


###** About BETaaS Project**

#### ** About the consortium**

BETaaS is a STREP Research project developed and co-funded by the European Commission Research and Innovation 7th Framework Program by the [BETaaS Consortium](http://www.betaas.eu/consortium.html#.VEeGuhZvAgk) under the ICT theme (Call 8) of DG CONNECT.

#### ** About BETaaS License**

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

[http://www.apache.org/licenses/LICENSE-2.0](http://www.apache.org/licenses/LICENSE-2.0)

Unless required by applicable law or agreed to in writing, software  distributed under the License is distributed on an "AS IS" BASIS,  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and  limitations under the License.


2014 All Rights Reserved by Intecs Spa
