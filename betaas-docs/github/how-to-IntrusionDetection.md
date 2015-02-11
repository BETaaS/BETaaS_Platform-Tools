**Intrusion Detection Example**
===================


**Intrusion Detection** external application is an example showing how to use presence sensors to implement a simple alarm system.

### BETaaS software pre requirements

It is necessary that presence sensors can be connected to BETaaS through a suitable adapter plug-in.

### BETaaS components used

Most of BETaaS components are used to allow the resources allocation and the interaction with the sensors. The application itself interact with the Service Manager.

## BETaaS LEZ - How to

#### Step 1 - The Manifest

The manifest contains specifications about the features that the application requires to run. Specifically, one feature is requested: **PRESENCE**. All the available Thing Services created on top of devices that have that type (*type* field in the CSV context file, see above) will be used to fulfill the manifest. Of course all the other constraints must be met about power consumption, location, etc.

The manifest also contains the **credentials** to access the platform. See platform installation instructions to get valid credentials according to the certificates of the used BETaaS GWs.

Another important property specified by the manifest is the notification address. It specifies the REST address to which BETaaS will send notifications about installation, data availability, SLA violations, etc. In other cases a GCM identifier can be specified to receive notifications (it can be useful in case of mobile applications).

#### Step 3 - Connecting the Things

Before starting the application, connect the presence Things having the same identifiers used to name the CSV context files (see BETaaS gateway configuration - context files folder). The corresponding Thing Services will be created. 

Things may also be simulated through the TA simulator.

#### Step 4 - Starting the Service

First of all, open the IntrusionDetection.cfg located at IntrusionDetection\WebContent and update the **BETAAS_ADDRESS** and the **WSDL** parameter to contain the same address configured for the Service Manager in the endpoints configuration file of BETaaS. The string to substitute is http://localhost:18900.

Once compiled (*mvn clean compile*), the application war is produced in the target folder and can be used to deploy it in a Tomcat 6 instance (*webapps* folder). The first time it is started, Tomcat will extract the files from the war to the IntrusionDetection folder.

At startup, IntrusionDetection will try to allocate the resources it needs by sending to the Service Manager its manifest file located at *src/main/resources*.

The application writes its log in the IntrusionDetection folder. Check it to see if resources allocation is successfully completed.

#### Step 5 - Service Operation

Once the resources allocation is completed (i.e. the manifest is accepted and the requested presence feature is covered by the existing Thing Services) the IntrusionDetection web application starts pulling data from BETaaS. Then the result may be displayed through a Web page at http://localhost:8080/IntrusionDetection.

Once the resources are allocatd, the IntrusionDetection.cfg file is updated with info about allocated Thing Services. The parameter IS_INSTALLED is set to 1 so that at next restart the application will not request the allocation again and it will just start pulling data from BETaaS.

### Source Code Explanation

IntrusionDetection is implemented as a Web application. As soon it starts, it requests the resources allocation to BETaaS, then the Web page connects to it through Javascript REST requests to get updates on presence.

The Web page and scripts are stored inside *WebContent*. The server part is under *src*.

The main class is *eu.betaas.apps.home.intrusiondetection.IntrusionDetectionContext* whose method *contextInitialized* is called by Tomcat once the application is deployed and started. That is the method where the configuration is loaded and where the resources allocation process is started (*InstallThread* class).

*InstallThread* composes the manifest and sends it to the Service Manager SOAP interface (it uses the classes under *IntrusionDetection\src\main\java\eu\betaas\apps\lib\soap*). Since that request is asynchronous, once the installation completes, BETaaS sends the notification to the *notificationAddress* specified in the sent manifest. Such notification is received by *InstallNotificationReceiver* that parses it and in case of success starts the processing thread implemented by *IntrusionDetectionThread*.

*IntrusionDetectionThread* is in charge of periodically request data from the features it installed. That is done through the Service Manager SOAP interface *getThingServiceData*.


## About BETaaS Project

#### ** About the consortium**

BETaaS is a STREP Research project developed and co-funded by the European Commission Research and Innovation 7th Framework Program by the [BETaaS Consortium](http://www.betaas.eu/consortium.html#.VEeGuhZvAgk) under the ICT theme (Call 8) of DG CONNECT.

#### ** About BETaaS License**

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

[http://www.apache.org/licenses/LICENSE-2.0](http://www.apache.org/licenses/LICENSE-2.0)

Unless required by applicable law or agreed to in writing, software  distributed under the License is distributed on an "AS IS" BASIS,  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and  limitations under the License.


2014 All Rights Reserved by Intecs Spa
