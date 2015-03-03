**BETaaS Adaptation Things Adaptor**
===================

## BETaaS Things Adaptor (TA) Overview


### Purpose

This component is responsible for the transition between the world of the actual devices to the main functionality of BETaaS platform. It belongs to the Adaptation architectural layer of the model and it maintains a smooth and steady communication pipeline with the devices that it discovers. BETaaS Things are created by the physical devices' characteristics and transmitted to the upper layers for producing services based on each Thing. The Things Adaptor is responsible for the discovery and continuous checking of new or removed devices according to the underlying communication protocol implemented in BETaaS Adaptation Plugin


### Status

<font style='color:red'>This component is ready for deployment</font>


## BETaaS Things Adaptor How to
Deploy the bundle in Karaf



#### Step 1

TA depends only on the <font style='color:red'>'betaas-adaptation-plugin'</font> which has to be previously installed (if not started) in Karaf.

#### Step 2

<font style='color:red'>Install the bundle with mvn like this (inside karaf console):</font>

	install mvn:eu.betaas/betaas-adaptation-thingsadaptor/0.0.1-SNAPSHOT
    
#### Alternatively

You can use the central betaas-features which includes the TA module together with the rest of the core and required modules and has the correct start-level timings that make sure depended modules start in the right order.

###** About BETaaS Project**

#### ** About the consortium**

BETaaS is a STREP Research project developed and co-funded by the European Commission Research and Innovation 7th Framework Program by the [BETaaS Consortium](http://www.betaas.eu/consortium.html#.VEeGuhZvAgk) under the ICT theme (Call 8) of DG CONNECT.

#### ** About BETaaS License**

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

[http://www.apache.org/licenses/LICENSE-2.0](http://www.apache.org/licenses/LICENSE-2.0)

Unless required by applicable law or agreed to in writing, software  distributed under the License is distributed on an "AS IS" BASIS,  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and  limitations under the License.


2014 All Rights Reserved by CONVERGE ICT