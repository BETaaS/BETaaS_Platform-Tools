**BETaaS How-to create a (Things Adapter) TA plugin Example**
===================


This file describes the basic steps for implementing a plugin module which will describe and handle a specific communication protocol for a set of devices.


### BETaaS software pre requirements

All BETaaS modules must be installed except betaas-adaptation-thingsadaptor which has as a prerequisite the TA plugin we are about to describe.
For minimum testing the only required module is the plugin Interface modules located in the <font style='color:red'>betaas-activation-plugin</font>

### BETaaS components used

1. betaas-adaptation-thingsadaptor is using the produced plugin module imported to it as <font style='color:red'>eu.betaas.adaptation.plugin.api</font>
2.	<font style='color:red'>betaas-activation-plugin</font> where the interfaces that will be extended reside

## BETaaS How to produce a custom TA adaptor plugin

Producing a specific plugin for the TA module is essentially the implementation of two (2) major interfaces residing at the betaas-adaptation-plugin module:

1.	IAdaptorPlugin
2.	IAdaptorListener

#### Step 1. Required methods from IAdaptorPlugin Interface

 All methods described in the interfaces mentioned above must be implemented. These are the following:
 
	void setListener(IAdaptorListener listener).

The method imposes the implementing class as the listener for registering and unregistering itself to and from the Things.

	Vector<HashMap<String, String>> discover().
    
This method instigates the connectivity with the devices of the underlying cloud of Things and should implement the particular protocols discovery mechanisms. After that call is completed the system shall at least have gathered the IDs of the devices (Thing IDs) that are essential to the further functioning of BETaaS. This method should be run on a regular basis depending on the needs of the developer if for example new devices are to be connected with the system after it’s startup.

	boolean register(String sensorID)
    
This method is used to register a specific device using its ID as described by the device protocol or developer’s needs. Once the Thing is registered there will be notifications published on behalf of it by the plugin (see method notify of IAdaptorListener)

	boolean unregister(String sensorID)
    
Once a device is no longer available or needed it can be removed and the implementation of this method should provide the details of such operation.

	String getData(String sensorID)
    
This method should implement how the TA will retrieve the value of a particular device based solely on its ID.


	public String setData(String sensorID, String value)
    
    
This method should provoke the change of value for, typically, an actuator identified by the sensorID and its new value.

#### Step 2. Required methods from IAdaptorListener Interface

	boolean notify(String type, String resourceID, HashMap<String, String> value)
    
This method should be called every time there is a change in the value of a sensor, registered by the system (i.e. the above register method is used for it).

	boolean removeThing(String thingId)
    
A thing is removed by the mechanism implemented by the method above in case of error or when the unregister method is called.

#### Step 3. Bundling evertyrhing together.

For bundling up the project that implements the methods described above it is required to import the following: *eu.betaas.adaptation.plugin.api* in order to succesfully build through maven.

Now the mvn project is ready to be built (assuming the name is betaas-adaptation-implemented) using the command through Karaf console:

	osgi:install mvn:eu.betaas/betaas-adaptation-implemented/0.0.1-SNAPSHOT

## About BETaaS Project

#### ** About the consortium**

BETaaS is a STREP Research project developed and co-funded by the European Commission Research and Innovation 7th Framework Program by the [BETaaS Consortium](http://www.betaas.eu/consortium.html#.VEeGuhZvAgk) under the ICT theme (Call 8) of DG CONNECT.

#### ** About BETaaS License**

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

[http://www.apache.org/licenses/LICENSE-2.0](http://www.apache.org/licenses/LICENSE-2.0)

Unless required by applicable law or agreed to in writing, software  distributed under the License is distributed on an "AS IS" BASIS,  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and  limitations under the License.


2014 All Rights Reserved by Converge ICT
