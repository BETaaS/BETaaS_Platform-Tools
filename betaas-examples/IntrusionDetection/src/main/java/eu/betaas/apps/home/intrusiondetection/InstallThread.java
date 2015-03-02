/*
Copyright 2014-2015 Intecs Spa

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
*/

package eu.betaas.apps.home.intrusiondetection;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.ServletContext;
import javax.xml.namespace.QName;

import eu.betaas.apps.lib.soap.ServiceManagerExternalIF;
import eu.betaas.apps.lib.soap.ServiceManagerExternalIFPortType;

/**
 * This is the thread in charge of sending the install request to BETaaS
 * @author Intecs
 */
public class InstallThread extends Thread {

	private static final QName SERVICE_NAME = new QName("http://api.servicemanager.service.betaas.eu/", "ServiceManagerExternalIF");

	/**
	 * Constructor
	 * @param context the servlet context of the application
	 * @param WSDL that defines the BETaaS interface to applications
	 */
	public InstallThread(ServletContext context, String WSDL) {
		mWSDL = WSDL;
		mContext = context;
		
		mManifest = 
				"<?xml version='1.0'?>"+
						"<manifest>"+            
						"    <Application>"+
						"     <name>IntrusionDetection</name>"+
						"     <notificationAddress>http://127.0.0.1:8080/IntrusionDetection/</notificationAddress>"+
						"	 <credentials>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</credentials>"+
						"    </Application>"+
						"    <ServiceDescriptionTerm>"+
						"      <ServiceDefinition>"+
						"        <Feature>presence</Feature>"+
						"        <Areas>"+
						"          <Environment>Private</Environment>"+
						"          <LocationKeyword>home</LocationKeyword>"+
						"          <Floor>1</Floor>"+
						"        </Areas>"+
						"        <Delivery>betaas.delivery.RTPULL</Delivery>"+
						"        <Trust>5</Trust>"+
						"        <Period>2</Period>"+
						"        <QoS>                                                "+
						"          <MaxInterRequestTimeSec>5</MaxInterRequestTimeSec>"+
						"          <MaxResponseTimeSec>5</MaxResponseTimeSec>"+
						"          <MinAvailability>2</MinAvailability>"+      
						"          <MaxBurstSize>1</MaxBurstSize>"+ 
						"          <AverageRate>1</AverageRate>"+ 
						"        </QoS>"+
						"        <credentials></credentials>"+
						"      </ServiceDefinition>    "+
						"    </ServiceDescriptionTerm>"+
						"</manifest>";
		
	}
	
	public void run() {
		Logger log = (Logger)mContext.getAttribute(IntrusionDetectionContext.CONTEXT_ATTR_LOG);
		
		// Send the install SOAP request to the BETaaS Platform
		
		URL wsdlURL = ServiceManagerExternalIF.WSDL_LOCATION;
		
		if ((mWSDL != null) && (mWSDL.length() > 0)) {
			
			log.loginfo("Sending the install request to BETaaS using WSDL: " + mWSDL);
			
			File wsdlFile = new File(mWSDL);
	        try {
	            if (wsdlFile.exists()) {
	                wsdlURL = wsdlFile.toURI().toURL();
	            } else {
                    wsdlURL = new URL(mWSDL);
                }
	        } catch (MalformedURLException e) {
	        	log.logerr("Cannot build the URL for the configured WSDL file");
	        } 
		}

        try {  
          //log.loginfo("wsdlURL: " + wsdlURL);
        	ServiceManagerExternalIF ss = new ServiceManagerExternalIF(wsdlURL, SERVICE_NAME);
            ServiceManagerExternalIFPortType port = ss.getServiceManagerExternalIFPort();  

          log.loginfo("Calling installApplication");
	        java.lang.String _test_arg0 = mManifest;
	        //java.lang.String _test__return = port.test(_test_arg0);
	        String instRes  = port.installApplication(_test_arg0);
	        
	        log.loginfo("The installation request returned: " + instRes);
        } catch (Exception e) {
        	log.logerr("Error requesting the installation: " + e.getMessage());    
        }
        
	}
	
    /** The App servlet context */
    private ServletContext mContext;
	
	/** The WSDL file name describing the BETaaS web services exposed to the application */
	private String mWSDL;
	
	private String mManifest;
}
