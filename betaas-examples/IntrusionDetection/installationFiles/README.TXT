REQUIREMENTS
############

- Karaf 2.3.3 with BETaaS installed
- Apache Tomcat 6 (tested on 6.0.37)


INSTALLATION
############

In order to install IntrusionDetection from scratch:


1. Copy the following files contained in this directory in the securityConfig folder inside Karaf home:
   GwStarInterCert.p12
   GwStarOwnCert.p12
   AppStoreCertInter.p12
   condition1.xml

2. Copy the pir1.csv file contained in this directory into the TA simulator folder (it is the one referred by the property "sensors" in the betaas.gateway.cfg (<Karaf_home>/etc>))

3. Start Karaf

4. Compile IntrusionDetection by running the following command in the IntrusionDetection folder:
   mvn clean install

5. Copy the war file generated in the IntrusionDetection/target folder to <Tomcat_home>/webapps folder

6. Start Tomcat. The war file content is extracted into <Tomcat_home>/webapps/IntrusionDetection

7. Open betaas.endpoints.cfg (<Karaf_home>/etc>) and copy the serviceManagerAddress value. Use it to update the following properties in <Tomcat_home>/webapps/IntrusionDetection/IntrusionDetection.cfg:
   - BETAAS_ADDRESS
   - WSDL

   For example:
   BETAAS_ADDRESS=http://192.168.31.44:9304/sm-service
   WSDL=http://192.168.31.44:9304/sm-service?wsdl

8. Shutdown and restart Tomcat


Then IntrusionDetection automatically requests to BETaaS to be installed. You can check the log in <Tomcat_home>/webapps/IntrusionDetection/IntrusionDetection.log.

IntrusionDetection.cfg is automatically updated with installation result info.


RESTART OF IntrusionDetection
#############################

Once the above steps have been performed, IntrusionDetection can be reinstalled just by:

1. Shutdown Tomcat

2. Update IntrusionDetection.cfg setting:

   IS_INSTALLED=0

3. Restart Tomcat

Then IntrusionDetection will start again and request to BETaaS to be installed.


WEB PAGE
########

Once Tomcat is started, the result of IntrusionDetection is visible at:

http://localhost:8080/IntrusionDetection

