# CoAP plugin
In order to install the CoAP Adaptation plugin some few actions are required.

#### Edit the betaas.gateway.cfg
Include the path to the plugin configuration file (server.xml)
```sh
vim /path/to/karaf/etc/betaas.gateway.cfg
```
By adding at the end the following statement, where `YOUR_PATH` should be chosen according to your preferences:
```
# CoAP settings
serversConfig = <YOUR_PATH>/server.xml
```

#### Create the server.xml 
Create the file server.xml structured as follow and put it in the path specified above:
```xml
<servers>
	<server>
	    <name>Unipi01</name>
	    <ip>127.0.0.1</ip>
	    <port>5683</port>
	</server>
	<server>
	    <name>Unipi02</name>
	    <ip>127.0.0.1</ip>
	    <port>5684</port>
	</server>
</servers>
```

Note: For each CoAP server that will be connected to the adapter a new server tag with its children must be added.

#### Deploy the CoAP Plugin

Open the Apache Karaf console and stop the BETaaS Adaptation Simulator, then deploy the CoAP plugin. The `BUNDLE_ID` correspond to the id of the BETaaS Adaptation Simulator that can be retrieved with the `list` command inside karaf.

```
stop <BUNDLE_ID>
osgi:install mvn:org.eclipse.californium/californium-osgi/1.0.0-SNAPSHOT
osgi:install mvn:eu.betaas/betaas-adaptation-coap/3.0.3-SNAPSHOT
```

#CoAP Server simulator

You can run many CoAP servers, each server is an independent process running locally. Each CoAP server exposes resources through the CoAP protocol. If more than one CoAP server will be deployed on the same machine, each server must listen on a diffent port.

The CoAP server simulator is hosted in:
```sh
cd betaas-utils/coap-server-betaas
```
#### Server configuration:
The server is a simple java program structured in two different packages:

- org.eclipse.californium.examples 
- org.eclipse.californium.examples.resources 

The first package contains the CoAP server, while the second contains all the classes that are defined to implement, each one, a different kind of resource. For example, few classes are already defined: the TrafficResource, the GPSResource, the PIRResource and the StreetLampResource. 

The CoAP server reads a config_server.xml file where there is the description of the resources exposed by the server itself. The structure of the config_server.xml is the following:
```xml
<Resources>
    <ClassNameResource deviceID="xxx" output="0|1" digital="0|1" maximumResponseTime="xxx" memoryStatus="xxx"
    batteryLevel="0-100" protocol="coap" type="xxx" unit="bool" environment="0" latitude="xxx" longitude="xxx" altitude="xxx" 
	floor="xxx" locationKeyword="xxx" LocationIdentifier="xxx"	
    ComputationalCost="0-1" BatteryCost="0-100" measurement="xxx">
    </ClassNameResource>
</Resources>
```
For example to add a PIRResource the config_server.xml is the following:

```xml
<Resources>
	<PIRResource deviceID="pir1" output="1" digital="1" maximumResponseTime="1" memoryStatus="50" batteryLevel="100"
	protocol="coap" 	type="presence" unit="bool" environment="0" latitude="40" longitude="10" altitude="7" 
	floor="1" locationKeyword="kitchen" LocationIdentifier="1"	
    ComputationalCost="0.1" BatteryCost="10" measurement="false">
    </PIRResource>
</Resources>
```
#### Add new kind of resources
1. add a new class inside the org.eclipse.californium.examples.resources package and define the behavior of GET,POST,PUT and DELETE
2. modify the config_server.xml
3. add an if clause to the main body of the CoAP server in org.eclipse.californium.examples in order o recognize the new resource in the XML file

#### Start a CoAP Server
```sh
mvn clean install
java -jar target/coap-server-betaas-1.0.0-SNAPSHOT.jar <port> <path/to/config_server.xml>
```
