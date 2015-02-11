**BETaaS How-to create a Things Simulator**
===================


This file describes the basic steps for using the BETaaS Things simulator in order to assist the Adaptation Things Adaptor (TA) with discoverig, reading and writing values to a simulated environment and thus showcasing the basic functionality of BETaaS before the developer moves onto writing a plugin for a particular real world device communication protocol.


### BETaaS components used

Minimum requirements for testing are the following

1.	<font style='color:red'>betaas-adaptation-thingsadaptor</font>
2.	<font style='color:red'>betaas-adaptation-plugin</font>
3.	<font style='color:red'>betaas-adaptation-simulator</font>

## BETaaS How to produce a TA Things Simulator

In order to facilitate developers in testing the key concepts and functionality of BETaaS we have developed a simple Things Simulator (TS) based on a (csv) text file(s) that corresponds to the “physical” devices otherwise installed and discoverable by the system in a real situation. In this way, developers can easily and quickly test a number of devices ranging in number and characteristics respectively, with the number of files. For example, a number of 3 different csv files provide BETaaS platform with 3 different virtual devices.

#### CSV file Structure

The file structure of the CSV file indicates all the values required by the TS in order to smoothly simulate not only the values but also the contextual information needed by the platform. In particular the following fields are available as comma separated values headers in the csv file:

1.	deviceID. The device’s unique ID.
2.	Output. 1 for true 0 for false.
3.	Digital. 1 for true 0 for false.
4.	maximumResponseTime.
5.	memoryStatus. Between 0 and 100 the memory status of the device
6.	batteryLevel. Between 0 and 100 the battery level of the device
7.	measurement. The actual value/reading of the device.
8.	protocol.
9.	type. Type of sensor/actuator e.g. “temperature”, “presence”
10.	unit. The type of measurement’s unit e.g. “int”.
11.	environment. 
12.	latitude. The longitude of the device’s position
13.	longitude. The longitude of the device’s position
14.	altitude. The altitude of the device’s position
15.	floor. The floor of the device’s position
16.	locationKeyword.
17.	LocationIdentifier
18.	ComputationalCost
19.	BatteryCost

*(NOTE: Names should be provided exactly as in the above list)*


Example csv (headers and one line of data)

	deviceID,output,digital,maximumResponseTime,memoryStatus,batteryLevel,measurement,protocol,type,unit,environment,latitude,longitude,altitude,floor,locationKeyword,LocationIdentifier,ComputationalCost,BatteryCost
	31,1,1,1,50,50,0,ecosystem,presence,int,0,43.2112321,12.36542121,34.12,0,livingroom,1,0.1,1
    
More lines of data mean that the siimulator will go through more iterations inside the file thus producing more fluctuationns according of course to the needs of the developer.

#### Step 1. Location and number of files

The files must end in either .csv or .CSV and should be placed in the location provided at the cofiguration file 

	/path/to/karaf/etc/betaas.gateway.cfg

In the specific property:

	# Simulator settings
	sensors = C:/path/to/sensor/file(s)
    
*NOTE: Make sure the location of the files is nnot blocked or in anyway restricted to the agent or user running the BETaaS platform*

In one file oly ONE Thing can be identified (by the uique number in the first column (deviceID). For more Things you can add more .csv files in the same location.

	
#### Step 2. Bundling evertyrhing together.

Start BETaaS platform while including the <font style='color:red'>betaas-adaptation-simulator</font> bundle.


## About BETaaS Project

#### ** About the consortium**

BETaaS is a STREP Research project developed and co-funded by the European Commission Research and Innovation 7th Framework Program by the [BETaaS Consortium](http://www.betaas.eu/consortium.html#.VEeGuhZvAgk) under the ICT theme (Call 8) of DG CONNECT.

#### ** About BETaaS License**

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

[http://www.apache.org/licenses/LICENSE-2.0](http://www.apache.org/licenses/LICENSE-2.0)

Unless required by applicable law or agreed to in writing, software  distributed under the License is distributed on an "AS IS" BASIS,  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and  limitations under the License.


2014 All Rights Reserved by Converge ICT
