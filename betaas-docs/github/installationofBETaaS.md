In order to start developing BETaaS components, the following paragraph introduce new developers with instructions on how to prepare the development environment and deploy BETaaS gateways.

##### Configuring the developer Environment

Deployment of BETaaS instance has been done on both Linux and Windows based environments, the baseline software requirements are:

* Java 1.7
* Apache Maven 3.0.5 or higher
* Apache Karaf 2.3.X or higher

For installation of two or more instances:

* Apache Zookeeper Server 3.4.6

##### Configuring Karaf

In order to deploy a gateway, Karaf must be configured to use Equinox OSGi environment. 
Supposing, Karaf is currently installed in {KARAF_HOME}, change the settings inside the file:

*{KARAF_HOME}/etc/config.properties* 

to:

	karaf.framework=equinox

Once the environment has been changed, also Log4J logging system, can be set to be split over three files, one for each BETaaS layer (adaptation,TaaS and service).
Just replace the file 

*{KARAF_HOME}/etc/org.ops4j.pax.logging.cfg* 

with the one provided in the gitHub repository [here](/betaas-configuration/configuration/org.ops4j.pax.logging.cfg)

Moreover, Maven should be set to use the BETaaS third-party Repository, in order to enable this repository in you local maven installation, add in the following file:

*{M2_HOME}/settings.xml* 

The following repo:

    <server>
      <id>betaas-thirdparty</id>
      <username>betaas-user-external</username>
      <password>pass-TOBEPROVIDED</password>
    </server>

Once this settings has been applied, the BETaaS Code can be built locally running, from the gitHub root folder.

	mvn clean install
    
Once the build is terminated, BETaaS is deployable in your current environment.

##### Configuring the BETaaS Gateway

Before installing a BETaaS gateways, it is mandatory to copy the following configuration settings files:

*betaas.gateway.cfg* [here](/betaas-configuration/configuration/betaas.gateway.cfg)
*betaas.endpoints.cfg* [here](/betaas-configuration/configuration/betaas.endpoints.cfg)

inside the Karaf folder:

*{KARAF_HOME}/etc/org.ops4j.pax.logging.cfg* 

The file *betaas.gateway.cfg*  includes the settings for the BETaaS components, such as the database file location, the gateway id. For basic settings of gateway, the correct folder location for database and Things simulation, if enabled, must be set accordingly with the current environment.

The file *betaas.endpoints.cfg* is only used when DOSGi is configured (see the next paragraph for details) because it contains the list of IP addresses and the port used by the BETaaS components that are exposed remotely to other gateways.


##### Installing BETaaS gateways

BETaaS gateways can be installed within the same machine, for testing purposes, or in a distributed environment. This affects how Zookeeper and DOSGi are configured. 
The mechanism that BETaaS provides to install gateways, is by the mean of Karaf Features, in order to make available BETaaS features within Apache Karaf, run the following command inside the Karaf console:

	features:addurl mvn:eu.betaas/betaas-features/3.0.3-SNAPSHOT/xml 


###### Installing a single BETaaS gateway

When the first BETaaS gateway is installed, it is mandatory to installed the main services mandatory in a BETaaS instance. In order to deploy such components, a specific features has been provided:

	features:install betaas-demo-gateway 

###### Connecting BETaaS gateways

Prior to installing additional gateways, Apache Zookeeper and DOSGi must be configured.

To install the required Apache Zookeeper DOSGi client, run the following in Karaf console:

	features:chooseurl cxf-dosgi 1.5.0
	features:install cxf-dosgi-discovery-distributed 
    
DOSGi will be available in Karaf, but one more step need to be performed, the configuration of the zookeeper feature to connect an available instance of Zookeeper:

	config:propset -p org.apache.cxf.dosgi.discovery.zookeeper zookeeper.port {ZK_PORT}
	config:propset -p org.apache.cxf.dosgi.discovery.zookeeper zookeeper.host {ZK_IP}

Change {ZK_PORT} and {ZK_IP} accordingly to your environment setup.

After the previous steps are performed, additional gateways to the first one, can be installed can be deployed with the following feature:

	features:install betaas- second-gateway 
    

###** About BETaaS Project**

#### ** About BETaaS Software License**

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

[http://www.apache.org/licenses/LICENSE-2.0](http://www.apache.org/licenses/LICENSE-2.0)

Unless required by applicable law or agreed to in writing, software  distributed under the License is distributed on an "AS IS" BASIS,  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and  limitations under the License.


2014 All Rights Reserved by [BETaaS Project](www.BETaaS.eu)

