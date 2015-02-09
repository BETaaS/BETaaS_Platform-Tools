NOTES ON IntrusionDetection 
ver. 0.0.1-SNAPSHOT

IntrusionDetection is a Web Application running on Tomcat 6.

Once deployed, it loads its configuration file IntrusionDetection.cfg
from webapps/IntrusionDetection

IntrusionDetection.cfg is also used to track if the application 
has been installed on the BETaaS platform. If it is not, it performs
the install request at startup.

IntrusionDetection exposes a REST interface to be used by
BETaaS for notifications (e.g. during installation or for measurements).

IntrusionDetection communicates with BETaaS through the
configured HTTP address by means of SOAP. The corresponding WSDL
is contained in the project and published by Tomcat.

