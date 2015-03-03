# Instant BETaaS 2.1

Instant BETaaS is an ubuntu virtual machine with the BETaaS software pre-installed and configured. The image contains also the development tools, such as Eclipse, mvn, etc, needed to start developing application for the BETaaS platform, along with all the utils, such as thing simulator, needed to test the applications on a real BETaaS instance. In addition the image contains the linux containers framework, configured to emulate a BETaaS instance with more than one gateway.  

#### Requirements
- Oracle Virtual Box available from Oracle VM VirtualBox
- BETaaS Virtual Box Image available from [Link 1] 
- Recommended Configuration for Virtual Box (tested with below configuration for stable working)
   - 2 GB Memory
   - 20 GB HDD 
   - Two core processing

#### Deploying and Starting VM
- Install the oracle virtual box
- From the location where the VM is stored, double click the file InstantBETaaS2.1-release.vbox. A window as show below will open with the InstantBETaaS settings
- Click the Start icon to start the VM

#### Start BETaaS 
- Open a Terminal and start Zookeeper
```sh
$ zkServer.sh start
```
- Start karaf:
```sh
$ karaf
```
- Inside the karaf console enable the log output to check the status:
```
karaf@root> log:tail
```
- Wait for an output similar to the following:
```
2015-03-03 09:09:33,556 | INFO  | pool-17-thread-1 | thingsadaptor | tation.simulator.utils.FileUtils   46 | 235 - betaas-adaptation-simulator - 2.1.0.release | Going to read data from the file...:pir1.csv      
2015-03-03 09:09:33,556 | INFO  | pool-17-thread-1 | thingsadaptor | tation.simulator.utils.FileUtils   55 | 235 - betaas-adaptation-simulator - 2.1.0.release | counter : 5 
2015-03-03 09:09:33,556 | INFO  | pool-17-thread-1 | thingsadaptor | tation.simulator.utils.FileUtils   62 | 235 - betaas-adaptation-simulator - 2.1.0.release | x : 5
```
- Now the platform is up and running and the thing simulator is simulating a presence sensor installed in the kitchen  

#### Run the IntrusionDetection example application
The following steps are required to run the example application IntrusionDetection, a tomcat webapp that exploits the information coming from the presence sensor installed in the home/kitchen environment to detect intrusion and show the status on a web interface

- Open a new Terminal and edit the following file:
```sh
sudo vim /var/lib/tomcat7/webapps/IntrusionDetection/IntrusionDetection.cfg
```
- Change the value from `IS_INSTALLED=1` to `IS_INSTALLED=0` in order to force the application to re-install from scratch
- Restar tomcat:
```sh
sudo service tomcat7 restart
```
- Open the browser
- Go to the tomcat admin interface using the following URL to check the status:
```
http://localhost:8080/IntrusionDetection/
```

#### Passwords and default values
- The VM username/password are betaas/betaas
- The Linux Container web panel username/password are admin/admin
- The username/password of each linux container are ubuntu/ubuntu

[Link 1]:https://drive.google.com/file/d/0B6ruPOwpLiDCR1VYQzgzdHVoVHM/view?usp=sharing
