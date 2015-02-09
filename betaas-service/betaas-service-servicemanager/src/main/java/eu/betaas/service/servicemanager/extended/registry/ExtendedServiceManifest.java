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

// BETaaS - Building the Environment for the Things as a Service
//
// Component: SM
// Responsible: Intecs

package eu.betaas.service.servicemanager.extended.registry;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import eu.betaas.service.servicemanager.ServiceManager;
import eu.betaas.service.servicemanager.Util;
import eu.betaas.service.servicemanager.application.registry.AppManifest;
import eu.betaas.service.servicemanager.extended.registry.ServiceRequirement;
import eu.betaas.taas.taasresourcesmanager.api.Feature;
import eu.betaas.taas.taasresourcesmanager.api.Location;

/**
 * This class manages the input manifest file containing the info from the extended service 
 * to be used by inner modules.
 * @author intecs
 */
public class ExtendedServiceManifest {
	
	//TODO: implement ExtendedServiceManifest based on the agreed structure
	
	/**
	 * Constructor
	 */
	public ExtendedServiceManifest() {
		mUniqueExtendedServiceID = null;
		mServices = null;
	}
	
	/**
	 * It fills internal structure by parsing the manifest
	 * @param manifestContent
	 */
	public void load(String manifestContent) throws Exception {
		String val;
		
		mContent = manifestContent;
		
		//TODO: finalize the manifest structure
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		 
        try {
           DocumentBuilder builder = dbf.newDocumentBuilder();
           InputStream is = new ByteArrayInputStream(mContent.getBytes("UTF-16"));
           Document document = builder.parse(is);
           NodeList nl = document.getElementsByTagName("manifest");
           
           // UNIQUE ID
           mUniqueExtendedServiceID = Util.getContent(nl, "manifest/ExtendedService/UniqueExtendedServiceID");
           if ((mUniqueExtendedServiceID == null) || (mUniqueExtendedServiceID.length() == 0)) 
        	   throw new Exception("Error with manifest/ExtendedService/UniqueExtendedServiceID");
           
           mCredentials = Util.getContent(nl, "manifest/ExtendedService/credentials");
           if ((mCredentials == null) || (mCredentials.length() == 0)) mLogger.warn("Null credentials");
           else mLogger.info("Encoded Base64 credentials length: " + mCredentials.length()); 
           
            ArrayList<Node> servicesList = Util.getNodeList(nl, "manifest/ServiceDescriptionTerm/ServiceDefinition");
           if (servicesList.size() == 0) mLogger.warn("No service specified in the manifest");
           else mLogger.info("Processing " + servicesList.size() + " service requests");
           
           if (servicesList.size() == 0) throw new Exception("No service specified in the manifest");
           
           mServices = new ServiceRequirement[servicesList.size()];
           
           /////////////////////////////////// ON THE FLY SERVICES ////////////////////////////
           for (int i=0; i < servicesList.size(); i++) {

				mServices[i] = new ServiceRequirement();
				
				// FEATURE
				mServices[i].mSemanticDescription = Util.getContent(servicesList.get(i), "Feature");
				if ((mServices[i].mSemanticDescription == null) || (mServices[i].mSemanticDescription.length() == 0))
					throw new Exception("Wrong Feature specified for service n. " + i);
				mLogger.info("Service " + i + " Feature: " + mServices[i].mSemanticDescription);
				
				// LOCATION
				String env, floor, altitude, latitude, longitude, locationKeyword, locationIdentifier;
				float radius;
				
				env = Util.getContent(servicesList.get(i), "Areas/Environment");
				if ((env == null) || ((!env.equalsIgnoreCase("private")) && (!env.equalsIgnoreCase("public"))))
					throw new Exception("Environment must be either public or private for service n. " + i);
				mLogger.debug("Service " + i + " Environment: " + env);
				
				locationKeyword = Util.getContent(servicesList.get(i), "Areas/LocationKeyword");
				if (locationKeyword == null) locationKeyword = "";
				mLogger.debug("Service " + i + " LocationKeyword: " + locationKeyword);
				
				locationIdentifier = Util.getContent(servicesList.get(i), "Areas/LocationIdentifier");
				if (locationIdentifier == null) locationIdentifier = "";
				mLogger.debug("Service " + i + " LocationIdentifier: " + locationIdentifier);
				
				if (env.equalsIgnoreCase("private")) {
					floor = Util.getContent(servicesList.get(i), "Areas/Floor");
					mLogger.debug("Service " + i + " Floor: " + floor);
					if (floor == null) floor = "";
					
					mServices[i].mLocation = new Location(floor, locationKeyword, locationIdentifier);
				} else {
					altitude = Util.getContent(servicesList.get(i), "Areas/Altitude");
					mLogger.debug("Service " + i + " Altitude: " + altitude);
					latitude = Util.getContent(servicesList.get(i), "Areas/Latitude");
					mLogger.debug("Service " + i + " Latitude: " + latitude);
					longitude = Util.getContent(servicesList.get(i), "Areas/Longitude");
					mLogger.debug("Service " + i + " Longitude: " + longitude);
					val = Util.getContent(servicesList.get(i), "Areas/Radius");
					if (val == null) radius = 0.0f;
					else {
						try {
							radius = Float.parseFloat(val);
							mLogger.debug("Service " + i + " : Radius" + radius);
						} catch (NumberFormatException e) {
							throw new Exception("Invalid radius for service n. " + i + ": " + val);
						}
					}
					
					mServices[i].mLocation = new Location(latitude, longitude, altitude, radius, locationKeyword, locationIdentifier);
				}
				
				// TRUST
				val = Util.getContent(servicesList.get(i), "Trust");
				if ((val != null) && (val.length() > 0)) {
					try {
						mServices[i].mTrust = Double.valueOf(val);
					} catch (NumberFormatException e) {
						throw new Exception("Wrong value for Trust on service n. " + i);
					}
				}
				else mServices[i].mTrust = 0.0;
				mLogger.debug("Service " + i + " Trust: " + mServices[i].mTrust);
					
				// SERVICE TYPE
				val = Util.getContent(servicesList.get(i), "Delivery");
				if (val.equals(AppManifest.RTPULL)) mServices[i].mType = Feature.RTPULL;
				else if (val.equals(AppManifest.RTPUSH)) mServices[i].mType = Feature.RTPUSH;
				else if (val.equals(AppManifest.NRTPULL)) mServices[i].mType = Feature.NRTPULL;
				else if (val.equals(AppManifest.NRTPUSH)) mServices[i].mType = Feature.NRTPUSH;
				else if (val.equals(AppManifest.SET)) mServices[i].mType = Feature.PUT;
				else throw new Exception("Service " + i + ": Unexpected Service Type - " + val);
				mLogger.debug("Service " + i + " Type: " + mServices[i].mType);
				
				// CREDENTIALS
				mServices[i].mCredentials = Util.getContent(servicesList.get(i), "credentials");
				mLogger.debug("Service " + i + " credentials: " + mServices[i].mCredentials);
				
				// QoS
				mServices[i].mQoSMaxInterrequestTime = Util.getContent(servicesList.get(i), "QoS/MaxInterRequestTimeSec");
				mLogger.debug("Service " + i + " QoS MaxInterRequestTimeSec: " + mServices[i].mQoSMaxInterrequestTime); 
				try {
					Float.parseFloat(mServices[i].mQoSMaxInterrequestTime);
				} catch (Exception e) {
					throw new Exception("Wrong value for MaxInterRequestTimeSec");
				}

				mServices[i].mQoSMaxResponseTime = Util.getContent(servicesList.get(i), "QoS/MaxResponseTimeSec");
				mLogger.debug("Service " + i + " QoS MaxResponseTimeSec: " + mServices[i].mQoSMaxResponseTime);
				try {
					Float.parseFloat(mServices[i].mQoSMaxResponseTime);
				} catch (Exception e) {
					throw new Exception("Wrong value for MaxResponseTimeSec");
				}

				mServices[i].mQoSMinAvailability = Util.getContent(servicesList.get(i), "QoS/MinAvailability");
				mLogger.debug("Service " + i + " QoS MinAvailability: " + mServices[i].mQoSMinAvailability);
				try {
					Float.parseFloat(mServices[i].mQoSMinAvailability);
				} catch (Exception e) {
					throw new Exception("Wrong value for MinAvailability");
				}
				
				mServices[i].mAverageRate = Util.getContent(servicesList.get(i), "QoS/AverageRate");
				if ((mServices[i].mAverageRate != null) && (mServices[i].mAverageRate.length() > 0)) {
					mLogger.debug("Service " + i + " QoS AverageRate: " + mServices[i].mAverageRate);
					try {
						Float.parseFloat(mServices[i].mAverageRate);
					} catch (Exception e) {
						throw new Exception("Wrong value for AverageRate");
					}
				} else {
					mServices[i].mAverageRate = null;
				}
				
				mServices[i].mMaxBurstSize = Util.getContent(servicesList.get(i), "QoS/MaxBurstSize");
				if ((mServices[i].mMaxBurstSize != null) && (mServices[i].mMaxBurstSize.length() >  0)) {
					mLogger.debug("Service " + i + " QoS MaxBurstSize: " + mServices[i].mMaxBurstSize);
					try {
						Float.parseFloat(mServices[i].mMaxBurstSize);
					} catch (Exception e) {
						throw new Exception("Wrong value for MaxBurstSize");
					}
				} else {
					mServices[i].mMaxBurstSize = null;
				}
				
				// ADDITIONAL CHECKS
				if ((mServices[i].mType == Feature.RTPUSH) ||
					(mServices[i].mType == Feature.NRTPUSH)) {
					val = Util.getContent(servicesList.get(i), "Period");
					try {
						mServices[i].mPeriod = Float.valueOf(val);
					} catch (NumberFormatException e) {
						throw new Exception("Wrong value for period on service n. " + i);
					}
					mLogger.debug("Service " + i + " period: " + mServices[i].mPeriod);
					if (mServices[i].mPeriod <= 0) throw new Exception("Wrong value for period on service n. " + i);
				}
           }
           
           
        } catch (SAXException sxe) {
        	throw new Exception("SAX Exception: " + sxe.getMessage());
        } catch (ParserConfigurationException pce) {
            throw new Exception("Parse configuration exception: " + pce.getMessage());
        } catch (IOException ioe) {
        	throw new Exception("IO Exception: " + ioe.getMessage());
        }		
		
		
	}
	

	/** Logger */
	private static Logger mLogger = Logger.getLogger(ServiceManager.LOGGER_NAME);
	
	/** Unique identifier for extended services within the whole instance */
	public String mUniqueExtendedServiceID;
	
	/** Credentials to install the extended service */
	public String mCredentials;
	
	/** Services requested by the application */
	public ServiceRequirement[] mServices;
	
	/** The manifest file content */
	public String mContent;
	
}
