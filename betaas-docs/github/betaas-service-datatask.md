**BETaaS Application Data Task**
===================


BETaaS Data Task at Service layer defines an interface that must be implemented in order to offer data analytis capabilities to applications deployed on BETaaS instances. Data Tasks should retrieve data from all available SQL database at service layers and then merge the results, or, if the Analytic platform is available, perform query over the Presto DB interface.


## BETaaS Component Info

The Projects implementing the Service Data Task are inside the folder: 

	betaas-service\betaas-service-bigdatamanager
    
In particulare the following Apache Maven projects within the parent folder,implements the Data Task service:

  	-    betaas-service-bigdatamanager-data\betaas-service-bigdatamanager\betaas-service-bigdatamanager-service-task\betaas-service-bigdatamanager-service-datatask
  	   

#### BETaaS services used

The Data Task BETaaS Data Task interface does not have mandatory service dependencies. However each new implementation of service must at least import the import such interface and implements its methods.
For more details about how to build a data task, refer to:

{ADD LINK}



#### BETaaS Services provided

The BETaaS DataTask Services exposes a generic interface that allows to add new customized data task to the BETaaS platform by simply implementing it. The interface is *eu.betaas.service.bigdatamanager.service.datatask* and provides the methods:

	public void runTask(HashMap<String,String> input);
	
	public TaskData getTaskData(String taskId);
	
	public boolean taskCompleted(String taskId);

	public void setupTask();
	
	public void removeTask();

### Service Big Data Manager Software Components

The BETaaS Data Tasks, when available, are configured inside the file *betaas.endpoints.cfg* to publish the service through DOSGi.


###** About BETaaS Project**

#### ** About the consortium**

BETaaS is a STREP Research project developed and co-funded by the European Commission Research and Innovation 7th Framework Program by the [BETaaS Consortium](http://www.betaas.eu/consortium.html#.VEeGuhZvAgk) under the ICT theme (Call 8) of DG CONNECT.

#### ** About BETaaS License**

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

[http://www.apache.org/licenses/LICENSE-2.0](http://www.apache.org/licenses/LICENSE-2.0)

Unless required by applicable law or agreed to in writing, software  distributed under the License is distributed on an "AS IS" BASIS,  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and  limitations under the License.


2014 All Rights Reserved by HP
