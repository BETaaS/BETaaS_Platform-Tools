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
import java.util.Date;
import java.util.GregorianCalendar;

import javax.servlet.ServletContext;
import javax.xml.namespace.QName;

import eu.betaas.apps.lib.soap.ServiceManagerExternalIF;
import eu.betaas.apps.lib.soap.ServiceManagerExternalIFPortType;
//import eu.betaas.apps.lib.soap.ServiceManagerExternalIFPortType;

/**
 * This is the thread running the application logic
 * @author Intecs
 */
public class IntrusionDetectionThread extends Thread {

	public IntrusionDetectionThread(ServletContext context) {
		mContext = context;
	}
	
	public void run() {
		Logger log = (Logger)mContext.getAttribute(IntrusionDetectionContext.CONTEXT_ATTR_LOG);
		Configuration config = (Configuration)mContext.getAttribute(IntrusionDetectionContext.CONTEXT_ATTR_CONFIG);
		InstallationInfo install = (InstallationInfo)mContext.getAttribute(IntrusionDetectionContext.CONTEXT_ATTR_INST_INFO);
		PresenceInfo info = (PresenceInfo)mContext.getAttribute(IntrusionDetectionContext.CONTEXT_ATTR_PRESENCE_INFO);
		
		GregorianCalendar now = new GregorianCalendar();
		now.setTime(new Date());
		info.setStartDate(now);
		
		info.setmConnected(true);
		
		String WSDL = config.getProperty(Configuration.PROP_KEY_BETAAS_WSDL);
		URL wsdlURL = ServiceManagerExternalIF.WSDL_LOCATION;
		
		int pullPeriod = DEFAULT_PULL_PERIOD_MILLIS;
		String strPull = config.getProperty(Configuration.PROP_KEY_DATA_PULL_PERIOD_SEC);
		if (strPull != null) {
			try {
				pullPeriod = new Integer(strPull).intValue() * 1000;
				if (pullPeriod < 1000) {
					pullPeriod = DEFAULT_PULL_PERIOD_MILLIS;
				}
			} catch (NumberFormatException e) {
				pullPeriod = DEFAULT_PULL_PERIOD_MILLIS;
			}
		} else {
			pullPeriod = DEFAULT_PULL_PERIOD_MILLIS;
		}
			
		
		if ((WSDL != null) && (WSDL.length() > 0)) {
			
			log.loginfo("Starting requesting data to BETaaS using WSDL: " + WSDL);
			
			File wsdlFile = new File(WSDL);
	        try {
	            if (wsdlFile.exists()) {
	                wsdlURL = wsdlFile.toURI().toURL();
	            } else {
                    wsdlURL = new URL(WSDL);
                }
	        } catch (MalformedURLException e) {
	        	log.logerr("Cannot build the URL for the configured WSDL file");
	        	return;
	        } 
		}
		
		ServiceManagerExternalIF ss = new ServiceManagerExternalIF(wsdlURL, SERVICE_NAME);
        ServiceManagerExternalIFPortType port = ss.getServiceManagerExternalIFPort();  
        
		while (true) {
			
			// Send the getThingServiceData SOAP request to the BETaaS Platform
			try {
				int serviceID = 0;
		        String appId = config.getProperty(Configuration.PROP_KEY_APP_ID);
		        String serviceId = config.getProperty(Configuration.PROP_KEY_SERVICE_ID_PREFIX + serviceID);
		        String token = config.getProperty(Configuration.PROP_KEY_SERVICE_TOKEN_PREFIX + serviceID);
		        
		        if ((serviceId != null) && (serviceId.length() > 0)) {
			        String data = port.getThingServiceData(appId, serviceId, token);
//					log.loginfo("Data retrieved: " + data);
					if ((data == null) || 
						((!data.equalsIgnoreCase("true")) &&
						 (!data.equalsIgnoreCase("false")))) {
						log.logerr("Unexpected presence data from BETaaS: " + data);
						info.setmConnected(false);
					} else {
						info.setPresence(data.equalsIgnoreCase("true"));
						info.setmConnected(true);
					}
		        } else {
		        	log.logerr("Cannot get service ID from configuration");
		        }
			} catch (Exception e) {
				log.logerr("Cannot retrieve data from BETaaS: " + e.getMessage());
				info.setmConnected(false);
			}
			
			try {
				sleep(pullPeriod);
			} catch (InterruptedException e) {}
		}
	}

	/** The App servlet context */
	private ServletContext mContext;
	
	private static final QName SERVICE_NAME = new QName("http://api.servicemanager.service.betaas.eu/", "ServiceManagerExternalIF");
	
	private final int DEFAULT_PULL_PERIOD_MILLIS = 2000;
}
