 
# BETaaS installation from scratch

## Requirements

### Java 7
```sh
sudo apt-get install software-properties-common
sudo add-apt-repository ppa:webupd8team/java
sudo apt-get update 
sudo apt-get install oracle-java7-installer
```
### Apache ZooKeeper
```sh
wget http://mirror.nohup.it/apache/zookeeper/stable/zookeeper-3.4.6.tar.gz
tar zxvf zookeeper-3.4.6.tar.gz
sudo mv zookeeper-3.4.6 /opt
mv /opt/zookeeper-3.4.6/conf/zoo_sample.cfg /opt/zookeeper-3.4.6/conf/zoo.cfg
```
### Apache Maven
```sh
wget http://mirror.nohup.it/apache/maven/maven-3/3.2.5/binaries/apache-maven-3.2.5-bin.tar.gz
tar zxvf apache-maven-3.2.5-bin.tar.gz
sudo mv apache-maven-3.2.5 /opt
```
Edit Maven Config file:
```sh
vim /opt/apache-maven-3.2.5/conf/settings.xml
```
by adding this snippet code inside the `<servers>` tag:
```xml
<server>
	<id>betaas-obr</id>
	<username>betaas-dev</username>
	<password>B3t4s$</password>
</server>
<server>
	<id>thirdparty</id>
	<username>betaas-dev</username>
	<password>B3t4s$</password>
</server>

```
### Apache Karaf
```sh
wget http://apache.panu.it/karaf/2.3.8/apache-karaf-2.3.8.tar.gz
tar zxvf apache-karaf-2.3.8.tar.gz
sudo mv apache-karaf-2.3.8 /opt
```

Edit Karaf config file:
```sh
vim /opt/apache-karaf-2.3.8/etc/config.properties 
```

change the `karaf.fromework`
```
#
# Framework selection properties
#
karaf.framework=equinox
```
### Configure the PATH
```sh
vim .bashrc
```
Add at the end of the file:
```sh
export PATH=$PATH:/opt/apache-karaf-2.3.8/bin:/opt/apache-maven-3.2.3/bin:/opt/zookeeper-3.4.6/bin
export JAVA_HOME=/usr/lib/jvm/java-7-oracle
```

## BETaaS

### Download Software
```sh
git clone https://github.com/BETaaS/BETaaS_Platform-Tools.git
```

### Configure BETaaS
##### Update the endpoint config file:
```sh
cp BETaaS_Platform-Tools/betaas-configuration/configuration/betaas.* /opt/apache-karaf-2.3.8/etc/
```
##### Update the gateway config file:

```sh
vim /opt/apache-karaf-2.3.8/etc/betaas.gateway.cfg
```
Then update the config file with `{PATH}` as the absolute path of your BETaaS installation and the unique `{GATEWAY_ID}`:
```
# Gatewaay configuration
gwId={GATEWAY_ID}

# TaaS Database settings

taasdb_jdbc = jdbc:h2:file:{PATH}/BETaaS_Platform-Tools/data/taasbdmdb;DB_CLOSE_DELAY=-1
taasdb_user = sa
taasdb_pwd = sa

# Service SQL Database settings 
bdm_jdbc_driver = org.h2.Driver
bdm_url = jdbc:h2:file:{PATH}/BETaaS_Platform-Tools/data/bdmdata/
bdm_db_name = servicedb
bdm_db_user = sa
bdm_db_pwd = sa
bdm_db_mode = test
bdm_db_DBSetup = keep

# Hadoop BDM Components

sqoopUrl = http://betaashadoop:12000/sqoop/
hiveUrl = jdbc:hive2://betaashadoop:10000/default
hiveUser = hive
hivePwd = hive
uploadToHDFSFrequency = 60000

# PrestoDB for analytics tasks

prestoJdbc = jdbc:presto://betaashadoop:18080/hive/default
prestoUser = test

# Simulator settings

sensors = {PATH}/BETaaS_Platform-Tools/data/sensors/

# Things Adaptor settings

sensorsContext = {PATH}/BETaaS_Platform-Tools/data/context/

# Instance Manager
credentials=na
instanceDescription=Low Emission Zone Gateway
adminAddress=http://localhost:8080/InstanceManager/
automaticJoin = 0       
trackerWaitTime=30000

# BETaaS Bus layer

busenabled = false
busmode = direct
busendpoint = localhost
busendexchange = betaas_bus
bussubkey = betaasbus

# Info for GW certificates
countryCode=DK
state=Nordjylland
location=Aalborg
orgName=BETaaS

# Info for Security Manager
certificatePath = {PATH}/BETaaS_Platform-Tools/data/securityConfig/certificate/
conditionPath = {PATH}/BETaaS_Platform-Tools/data/securityConfig/condition/

# CoAP settings

serversConfig = {PATH}/BETaaS_Platform-Tools/data/coap_plugin/server.xml
```
Then create the needed folders:

```sh
mkdir BETaaS_Platform-Tools/data
mkdir BETaaS_Platform-Tools/data/coap_plugin
mkdir BETaaS_Platform-Tools/data/sensors
mkdir BETaaS_Platform-Tools/data/context
mkdir -p BETaaS_Platform-Tools/data/securityConfig/certificate/
mkdir -p BETaaS_Platform-Tools/data/securityConfig/condition/
```

### Configure Security Certificate
```sh
cp BETaaS_Platform-Tools/betaas-configuration/configuration/AppStoreCertInter.p12 BETaaS_Platform-Tools/data/securityConfig/certificate/
cp BETaaS_Platform-Tools/betaas-configuration/configuration/condition1.xml BETaaS_Platform-Tools/data/securityConfig/condition/
```

##### Update the logging config file:
```sh
vim /opt/apache-karaf-2.3.8/etc/org.ops4j.pax.logging.cfg
```
Append to the end of the file:
```
log4j.logger.betaas=INFO, betaasfilelog
log4j.appender.betaasfilelog=org.apache.log4j.RollingFileAppender
log4j.appender.betaasfilelog.layout=org.apache.log4j.PatternLayout
log4j.appender.betaasfilelog.threshold=INFO
log4j.appender.betaasfilelog.layout.ConversionPattern=%d{ISO8601} | %d{yyyy-MM-dd HH:mm:ss} | %X{bundle.id} - %X{bundle.name} - %X{bundle.version} | %m%n
log4j.appender.betaasfilelog.file=${karaf.data}/log/karaf_betaas.log
log4j.appender.betaasfilelog.append=true
log4j.appender.betaasfilelog.maxFileSize=1MB
log4j.appender.betaasfilelog.maxBackupIndex=10

log4j.logger.betaas.taas=INFO, betaastaasfilelog
log4j.appender.betaastaasfilelog=org.apache.log4j.RollingFileAppender
log4j.appender.betaastaasfilelog.layout=org.apache.log4j.PatternLayout
log4j.appender.betaastaasfilelog.threshold=INFO
log4j.appender.betaastaasfilelog.layout.ConversionPattern=%d{ISO8601} | %d{yyyy-MM-dd HH:mm:ss} | %X{bundle.id} - %X{bundle.name} - %X{bundle.version} | %m%n
log4j.appender.betaastaasfilelog.file=${karaf.data}/log/karaf_betaas_taas.log
log4j.appender.betaastaasfilelog.append=true
log4j.appender.betaastaasfilelog.maxFileSize=1MB
log4j.appender.betaastaasfilelog.maxBackupIndex=10

log4j.logger.betaas.service=INFO, betaasservicefilelog
log4j.appender.betaasservicefilelog=org.apache.log4j.RollingFileAppender
log4j.appender.betaasservicefilelog.layout=org.apache.log4j.PatternLayout
log4j.appender.betaasservicefilelog.threshold=INFO
log4j.appender.betaasservicefilelog.layout.ConversionPattern=%d{ISO8601} | %d{yyyy-MM-dd HH:mm:ss} | %X{bundle.id} - %X{bundle.name} - %X{bundle.version} | %m%n
log4j.appender.betaasservicefilelog.file=${karaf.data}/log/karaf_betaas_service.log
log4j.appender.betaasservicefilelog.append=true
log4j.appender.betaasservicefilelog.maxFileSize=1MB
log4j.appender.betaasservicefilelog.maxBackupIndex=10

log4j.logger.betaas.adaptation=INFO, betaasadaptationfilelog
log4j.appender.betaasadaptationfilelog=org.apache.log4j.RollingFileAppender
log4j.appender.betaasadaptationfilelog.layout=org.apache.log4j.PatternLayout
log4j.appender.betaasadaptationfilelog.threshold=INFO
log4j.appender.betaasadaptationfilelog.layout.ConversionPattern=%d{ISO8601} | %d{yyyy-MM-dd HH:mm:ss} | %X{bundle.id} - %X{bundle.name} - %X{bundle.version} | %m%n
log4j.appender.betaasadaptationfilelog.file=${karaf.data}/log/karaf_betaas_adaptation.log
log4j.appender.betaasadaptationfilelog.append=true
log4j.appender.betaasadaptationfilelog.maxFileSize=1MB
log4j.appender.betaasadaptationfilelog.maxBackupIndex=10

log4j.logger.betaas.thingsadaptor=INFO, betaastafilelog
log4j.appender.betaastafilelog=org.apache.log4j.RollingFileAppender
log4j.appender.betaastafilelog.layout=org.apache.log4j.PatternLayout
log4j.appender.betaastafilelog.threshold=INFO
log4j.appender.betaastafilelog.layout.ConversionPattern=%d{ISO8601} | %d{yyyy-MM-dd HH:mm:ss} | %X{bundle.id} - %X{bundle.name} - %X{bundle.version} | %m%n
log4j.appender.betaastafilelog.file=${karaf.data}/log/karaf_betaas_ta.log
log4j.appender.betaastafilelog.append=true
log4j.appender.betaastafilelog.maxFileSize=1MB
log4j.appender.betaastafilelog.maxBackupIndex=10
```

### Compile BETaaS
```sh
cd BETaaS_Platform-Tools
mvn clean install
```

### Install BETaaS

Run Apache Karaf by issue `karaf`, then inside the Karaf Console issue the following command where `<IP_ADDRESS_ZOOKEEPER_SERVER>` is the ip adress of the host where you will run the Zookeeper server:
```sh
features:addurl mvn:eu.betaas/betaas-features/2.1-release/xml
features:install betaas-demo-gateway
features:chooseurl cxf-dosgi 1.5.0
config:propset -p org.apache.cxf.dosgi.discovery.zookeeper.server clientPort 2181
config:propset -p org.apache.cxf.dosgi.discovery.zookeeper zookeeper.host <IP_ADDRESS_ZOOKEEPER_SERVER>
config:propset -p org.apache.cxf.dosgi.discovery.zookeeper zookeeper.port 2181
features:install cxf-dosgi-discovery-distributed
```
Exit from Karaf by issue `Ctrl-D`

### Run BETaaS

Start the Zookeeper server on the first gateway:
```sh
zkServer.sh start 
```

Start Apache Karaf:
```sh
karaf
```
### Configure connected things

TODO

### Install Applications

TODO

### Run Applications

TODO

