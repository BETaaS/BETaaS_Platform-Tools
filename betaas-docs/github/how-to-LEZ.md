**BETaaS LEZ extended service Example**
===================


**Low Emission Zone** (LEZ) extended service aims at computing the fees to be applied to car drivers that access the LEZ, varying the fee based on the traffic intensity.

### BETaaS software pre requirements

The extended service expects measurements about:

- Car **drivers position** in the city <br/>
  It is assumed that users have an onboard GPS receiver sending data to the   BETaaS gateway
  
- **Traffic intensity** <br/>
  It is assumed that sensors are deployed on the city streets providing   measurements as cars/min
  
Then it is necessary that position and traffic sensors can be connected to BETaaS through a suitable adapter plug-in.

### BETaaS components used

The extended service is installed inside the platform and interacts with the Service Manager. Most of BETaaS components are then used to allow the resources allocation and the interaction with the sensors.

## BETaaS LEZ - How to

#### Step 1 - Setup

In order to make this service work some files have to be placed in correct folders and configured:

- **Sensors configuration**:

    - **in case you want to simulate them through the TA simulator**: copy the files contained in *src\main\resources\sensors* to the sensors folder referred by the *betaas.gateway.cfg* file (in the Karaf *etc* folder) through the **sensors** variable.
    
    - **in case you want to use real sensors attached to an adaptation plug-in**: **CSV** files contained in *src/main/resources/context* (both for GPS and traffic, a total of 9 files) must be copied to the context folder referred by the *betaas.gateway.cfg* file (in the Karaf *etc* folder). The context folder is configured via the **sensorsContext** variable in the *betaas.gateway.cfg* file. CSV files must be placed all together **in the same directory**. The CSV file names specify the sensors ID, so if you connect real sensors, make sure they have those IDs (or simply update the CSV files to be coherent with your sensors).

- *lezextendedservice.cfg* contained in *src/main/resources* must be copied to the Karaf *etc* folder

- The map file *mapMontacchiello_22052014.csv* contained in *src/main/resources* must be placed in a convenient folder and referred by *lezextendedservice.cfg* (see above)

#### Step 2 - The Manifest

The manifest contains specifications about the features that the LEZ service requires to run. Specifically, two features are requested: **GPSPOSITION** and **TRAFFIC**. All the available Thing Services created on top of devices that have those type (*type* field in the CSV context file, see above) will be used to fulfill the manifest. Of course all the other constraints must be met about power consumption, location, etc.

The manifest also contains the **credentials** to access the platform. See platform installation instructions to get valid credentials according to the certificates of the used BETaaS GWs.

#### Step 3 - Connecting the Things

Before starting the LEZ service, connect the GPS and Traffic Things having the same identifiers used to name the CSV context files (GARM_GPS1, 5067, 5264, etc). The corresponding Thing Services will be created. 

Things may also be simulated through the TA simulator.

#### Step 4 - Starting the Service

Once compiled (*mvn clean compile*), the LEZ service jar is produced in the target folder and can be used to install it in the BETaaS gateway. One way is to use the following command in Karaf:
install *file://path_to_jar*

The bundle will be listed among the others. In order to start it just execute:
start *LEZ_bundle_number*

When it is started, it tries to allocate the resources it needs by sending to the Service Manager its manifest file located at *src/main/resources*.

#### Step 5 - Service Operation

Once the resources allocation is completed (i.e. the manifest is accepted and requested features are covered by the existing Thing Services) the LEZ service registers to the GPS and traffic features to receive new data and update the users fee.

### Source Code Explanation

*LEZExtendedServiceImpl* is the main class that implements the interfaces defined by all BETaaS extended services.

Its setupService method is executed when the bundle starts (as specified in *src/main/resources/OSGI-INF/blueprint/lezextendedservice.xml*). It performs some initialization (e.g. it loads the city map) and requests Service Manager (SM) to allocate (install) resources using the manifest file.

Once the installation is completed, SM calls the *notifyInstallation* method providing the result. In case of success, it also provides the list of service identifiers (one for GPS and one for traffic) and security tokens to access those services.

It is in *notifyInstallation* that the LEZ service subscribe (register) to traffic and GPS data in order to receive data updates.

Once registered, LEZ will be notified by SM through the method *notifyData*.
Based on the service to which received data refers, *managePositionData* or *manageTrafficData* methods of *LEZProcessor* will be called.

When position data is received for a user, the traffic intensity is retrieved for that position and the user's applied fees history is updated (*processUserPosition*).

When traffic intensity is received that information is used to update the loaded map (*processTrafficIntensity*).

The extended service must also implement the interface *getResult(String additionalInfo)*. That is a general-purpose method to allow extenrnal applications retrieving the result of service computations. In the case of LEZ service, it expects in input an user id (in the form "id=*value*") and it returns that user info (see LEZProcessor *getUserInfo* method). It is meant to be used by the car drivers that want to know the applied fees.



## About BETaaS Project

#### ** About the consortium**

BETaaS is a STREP Research project developed and co-funded by the European Commission Research and Innovation 7th Framework Program by the [BETaaS Consortium](http://www.betaas.eu/consortium.html#.VEeGuhZvAgk) under the ICT theme (Call 8) of DG CONNECT.

#### ** About BETaaS License**

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

[http://www.apache.org/licenses/LICENSE-2.0](http://www.apache.org/licenses/LICENSE-2.0)

Unless required by applicable law or agreed to in writing, software  distributed under the License is distributed on an "AS IS" BASIS,  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and  limitations under the License.


2014 All Rights Reserved by Intecs Spa
