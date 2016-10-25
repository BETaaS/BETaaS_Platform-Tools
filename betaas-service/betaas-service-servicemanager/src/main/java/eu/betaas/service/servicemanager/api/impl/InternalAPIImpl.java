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

package eu.betaas.service.servicemanager.api.impl;

import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import com.google.gson.JsonObject;

import eu.betaas.service.bigdatamanager.applicationdataservice.IDataManagerADService;
import eu.betaas.service.servicemanager.ServiceManager;
import eu.betaas.service.servicemanager.api.ServiceManagerInternalIF;
import eu.betaas.service.servicemanager.application.RequestManager;
import eu.betaas.service.servicemanager.application.registry.AppRegistryRow;
import eu.betaas.service.servicemanager.application.registry.AppService;
import eu.betaas.service.servicemanager.application.registry.ApplicationRegistry;
import eu.betaas.service.servicemanager.extended.api.IExtendedService;
import eu.betaas.service.servicemanager.extended.discovery.Discovery;
import eu.betaas.service.servicemanager.extended.registry.ExtServService;
import eu.betaas.service.servicemanager.extended.registry.ExtServiceRegistryRow;
import eu.betaas.service.servicemanager.extended.registry.ExtendedRegistry;
import eu.betaas.service.servicemanager.extended.registry.ExtendedServiceManifest;
import eu.betaas.taas.qosmanager.api.QoSManagerInternalIF;
import eu.betaas.taas.taasresourcesmanager.api.Feature;
import eu.betaas.taas.taasresourcesmanager.api.TaaSResourceManager;
import eu.betaas.service.securitymanager.service.IAuthorizationService;

import org.apache.cxf.common.util.Base64Utility;

/**
 * This class implements the interfaces of SM visible to internal GW components
 * @author Intecs
 */
public class InternalAPIImpl implements ServiceManagerInternalIF {
	
	/**
	 * Startup the service exposing internal IF to the gateway
	 */
	public void startService() {
		mLogger.info("Starting Service Manager local IF bundle");
	}
	
	public void closeService() {
		mLogger.info("Stopping Service Manager local IF bundle");
	}
	
	/**
	 * Setter for the OSGi context
	 * @param context
	 */
	public void setContext(BundleContext context) {
		mLogger.info("Setting the context on internal IF bundle");
		mContext = context;	
		ServiceManager.setContext(context);
	}
	
	/** 
	 * Setter for BDM service
	 * @param service
	 */
	public void setBDMService(IDataManagerADService service) {
		mBDMService = service;
	}
	
	public boolean installExtendedService(String manifestContent) {
		int i;
		ExtServiceRegistryRow regRow = null;
		ExtServService service;
		String msg;
		
		mLogger.debug("Called install installExtendedService with manifest: " + manifestContent);

    if (mDiscovery == null)	{
      mDiscovery = new Discovery(mContext);
      try {
        Thread.sleep(3);
      } catch (InterruptedException e) {}
    }

		
		ExtendedRegistry extServReg = ServiceManager.getInstance().getExtendedRegistry();
		
		TaaSResourceManager rmIF = null; 
		
		// Parse the manifestFile
		ExtendedServiceManifest manifest = new ExtendedServiceManifest();
		try {
			manifest.load(manifestContent);
		} catch (Exception e) {
			msg = "Error loading the manifest: " + e.getMessage();
			mLogger.error(msg);
			return false;
		} 
		
		mLogger.info("Installing the extended service with unique ID: " + manifest.mUniqueExtendedServiceID);
		
		// Generate a new extended service row in the registry
		try {
			regRow = extServReg.addNewExtendedService(manifest.mUniqueExtendedServiceID, manifest.mCredentials);
		} catch (Exception e) {
			msg = "Error storing a new extended service in the registry";
			mLogger.error(msg);
			return false;
		}
		
		// Check credentials
		if (!checkCredentials(regRow)) return false;
		
		// Obtain the TaasRM service
		rmIF = getResourceManagerIF();
		if (rmIF == null) {
			regRow.mStatus = ExtServiceRegistryRow.ExtServiceInstallationStatus.ERROR;
			msg = "Taas RM Bundle is not active";
			mLogger.error(msg);
			return false;
		}
		
		// Put the allocating services into the registry
		for (i=0; i < manifest.mServices.length; i++) {
			service = new ExtServService();
			service.mName = ExtendedRegistry.EXT_SERVICE_NAME_PREFIX + i;
			service.mRequirements = manifest.mServices[i];
			service.mStatus = ExtServService.ServiceInstallationStatus.ALLOCATING;
			mLogger.info("Putting service " + i + " into the extended service registry (ALLOCATING)");
			regRow.mServiceList.add(service);
		}		
		
		// Loop on the requested service to allocate the corresponding resources
		// All services must be allocated in order to successfully complete the application installation
		for (i=0; i < manifest.mServices.length; i++) {
			
			service = regRow.mServiceList.get(i);
			
			mLogger.info("Allocating resources for service n. " + i + " (" + service.mName + " - " + service.mRequirements.mSemanticDescription + ")");
			
			Feature feature = new Feature(regRow.mExtServiceID, 
					                      service.mRequirements.mSemanticDescription,
					                      service.mRequirements.mLocation, 
					                      service.mRequirements.mType,
					                      service.mRequirements.mTrust,
					                      (int)service.mRequirements.mPeriod);
			service.mServiceID = rmIF.allocateResources(feature);
			if ((service.mServiceID == null) || (service.mServiceID.length() == 0)) {
				
				regRow.mStatus = ExtServiceRegistryRow.ExtServiceInstallationStatus.ERROR;
				service.mStatus = ExtServService.ServiceInstallationStatus.ERROR;
				
				msg = "TaaSRM failed to allocate resources";
				mLogger.error(msg);
				
				return false;
			}

			mLogger.info("Resources allocated, starting QoS negotiation (Service ID=" + service.mServiceID + ")");
			service.mStatus = ExtServService.ServiceInstallationStatus.QOS_NEGOTIATION;

			// start the QoS negotiation
			QoSManagerInternalIF qosm;
			qosm = getQoSMIF();
			if (qosm == null) {
				msg = "QoSM not available for QoS negotiation";
				mLogger.error(msg);
				return false;
			}
			
			mLogger.info("Requesting the negotiation template to QoSM");
			String template = qosm.getTemplate();
			
			if (template == null) {
				mLogger.error("Got null QoS template from QoSM");
				return false;
			}
			
			String offer = template;
			offer = offer.replace("$TRANSACTIONID", service.mServiceID).
			              replace("$MAXRESPONSETIME", service.mRequirements.mQoSMaxResponseTime).
			              replace("$MINAVAILABILITY", service.mRequirements.mQoSMinAvailability).
			              replace("$MAXINTERREQUESTTIME", service.mRequirements.mQoSMaxInterrequestTime);

			if (service.mRequirements.mAverageRate != null) {
				offer = offer.replace("$AVERAGERATE", service.mRequirements.mAverageRate);
			}
			if (service.mRequirements.mMaxBurstSize != null) {
				offer = offer.replace("$MAXBURSTSIZE", service.mRequirements.mMaxBurstSize);
			}
			
			mLogger.info("Sending the agreement offer to QoSM");
			qosm.createAgreement(offer);			
			
			ServiceManager.busMessage("Installed extended Service: " + service.mName, "info", ServiceManager.MONITORING);
			// Once the service is installed, TaaSRM will call notifyServiceInstallation(serviceID)
			
		} // loop on requested services
		
		
		return true;
	}

	
	public boolean notifyServiceInstallation(String serviceID) {
		return notifyServiceInstallation(serviceID, null);
	}
	
	
	public boolean notifyServiceInstallation(String serviceID, byte[] token) {
		int i, nServices;
		AppRegistryRow appRegRow = null;
		ExtServiceRegistryRow extRegRow = null;
		AppService appServ;
		ExtServService extSerService;
		ApplicationRegistry applicationReg = ServiceManager.getInstance().getAppRegistry();
		ExtendedRegistry extendedReg = ServiceManager.getInstance().getExtendedRegistry();
		boolean allServicesAllocated = true;
		boolean isApp = true;
		
		mLogger.info("Received service install notification. ServiceID=" + serviceID);

		if (serviceID == null) {
			mLogger.error("The application cannot be installed. Received a null service ID from TaaSRM");
			return false;
		}

		// Search the app or the extended service to which the notified service belongs, based on the service ID
		// The app/ext service must be in the INSTALLING status and the service in the QOS_NEGOTIATION status
		appRegRow = applicationReg.searchInstallingServiceID(serviceID);
		if (appRegRow == null) {
			isApp = false;
			extRegRow = extendedReg.searchInstallingServiceID(serviceID);
			if (extRegRow == null) {
				mLogger.error("Received from TaaSRM a service ID that is not present neither in the Application nor in the Extended Service registry");
				return false;
			}
		}
		
		// encode the token to store it in the registry and notify it
		String encodedToken = "";
		if (token != null) {
			encodedToken = Base64Utility.encode(token);
			mLogger.info("Token associated to the service: " + encodedToken);
		} else {
			mLogger.warn("The notified token associated to the service is null");
		}
		
		// Change the service status (its resources have been allocated)
		if (isApp) nServices = appRegRow.mServiceList.size();
		else nServices = extRegRow.mServiceList.size();
		
		if (isApp) mLogger.info("Notified service is for an app with " + nServices + " services");
		else mLogger.info("Notified service is for an extended service with " + nServices + " services");
			
		for (i=0; i < nServices; i++) {
			
			if (isApp) {
				///////////////// APPLICATION ////////////////
				appServ = appRegRow.mServiceList.get(i);
				
				if ((appServ.mStatus == AppService.ServiceInstallationStatus.QOS_NEGOTIATION) && 
					(appServ.mServiceID.equals(serviceID))) {
					
					appServ.mStatus = AppService.ServiceInstallationStatus.ALLOCATED;
					appServ.mRequirements.mCredentials = encodedToken;
					
					mLogger.info("Service n. " + i + " allocated (service ID=" + serviceID + ")");
					
				} else if (appServ.mStatus != AppService.ServiceInstallationStatus.ALLOCATED) {
					// found one service of the application for which resources have not been allocated yet
					allServicesAllocated = false;
				}
			} else {
				///////////// EXTENDED SERVICE //////////////
				extSerService = extRegRow.mServiceList.get(i);
				
				if ((extSerService.mStatus == ExtServService.ServiceInstallationStatus.QOS_NEGOTIATION) && 
					(extSerService.mServiceID.equals(serviceID))) {
						
					extSerService.mStatus = ExtServService.ServiceInstallationStatus.ALLOCATED;
					extSerService.mRequirements.mCredentials = encodedToken;
					mLogger.info("Service n. " + i + " allocated (service ID=" + serviceID + ")");
						
				} else if (extSerService.mStatus != ExtServService.ServiceInstallationStatus.ALLOCATED) {
					// found one service of the extended service for which resources have not been allocated yet
					allServicesAllocated = false;
				}
			}
		}
		
		// if all the application/ext service services are in the ALLOCATED status, then application/ext service has been installed
		if (isApp) {
			///////////////// APPLICATION ////////////////
			if ((nServices > 0) && allServicesAllocated) {
				// change the installation status in the registry
				appRegRow.mStatus = AppRegistryRow.ApplicationInstallationStatus.INSTALLED;
				
				mLogger.info("Application (" + appRegRow.mAppName + ") installed.");
				
				// notify to the application the assigned Application ID
				try {
					ServiceManager.getInstance().notifyAssignedAppID(appRegRow.mAppID, true, null, null);
					
				} catch (Exception e) {
					mLogger.error("Cannot notify the assigned ID to the installed application: " + e.getMessage());
				}
				ServiceManager.busMessage("Application (" + appRegRow.mAppName + ") installed.", "info", ServiceManager.MONITORING);
			}
		} else {
			///////////// EXTENDED SERVICE //////////////
			if ((nServices > 0) && allServicesAllocated) {
				// change the installation status in the registry
				extRegRow.mStatus = ExtServiceRegistryRow.ExtServiceInstallationStatus.INSTALLED;
				
				mLogger.info("Extended service (" + extRegRow.mExtServiceUniqueName + ") installed.");
				mLogger.info("Notifying the assigned ID to the extended service");
				
				ServiceManager.busMessage("Extended service (" + extRegRow.mExtServiceUniqueName + ") installed.", "info", ServiceManager.MONITORING);
	
				// notify to the extended service the assigned ID
				IExtendedService serv = mDiscovery.retrieveExtendedService(extRegRow.mExtServiceUniqueName); //getExtendedServiceIF();
				if (serv != null) {
					ArrayList<String> serviceList = new ArrayList<String>();
					ArrayList<String> tokenList = new ArrayList<String>();
					if (extRegRow.mServiceList != null) {
						for (i=0; i < extRegRow.mServiceList.size(); i++) {
							serviceList.add(extRegRow.mServiceList.get(i).mServiceID);
							tokenList.add(extRegRow.mServiceList.get(i).mRequirements.mCredentials);
						}
					}					
					serv.notifyInstallation(true, 
							                "Installation successfully executed", 
							                extRegRow.mExtServiceID, 
							                serviceList,
							                tokenList);
					ServiceManager.busMessage("Extended service (" + extRegRow.mExtServiceUniqueName + ") installed.", "info", ServiceManager.MONITORING);
				} else {
					mLogger.warn("Cannot get the extended service interface to notify it the installation result");
				}
			}
		}

		return true;
	}
	public boolean notifyServiceInstallationFailure(String serviceID, String errorMessage) {
		
		int i, nServices;
		AppRegistryRow appRegRow = null;
		ExtServiceRegistryRow extRegRow = null;
		AppService appServ;
		ExtServService extSerService;
		ApplicationRegistry applicationReg = ServiceManager.getInstance().getAppRegistry();
		ExtendedRegistry extendedReg = ServiceManager.getInstance().getExtendedRegistry();
		String serviceDescription = null;

		boolean isApp = true;
		
		mLogger.info("Received service install failure notification. ServiceID=" + serviceID + ". Error message = "+errorMessage);

		if (serviceID == null) {
			mLogger.error("The application cannot be installed. Received a null service ID from TaaSRM");
			return false;
		}

		// Search the app or the extended service to which the notified service belongs, based on the service ID
		// The app/ext service must be in the INSTALLING status and the service in the QOS_NEGOTIATION status
		appRegRow = applicationReg.searchInstallingServiceID(serviceID);
		if (appRegRow == null) {
			isApp = false;
			extRegRow = extendedReg.searchInstallingServiceID(serviceID);
			if (extRegRow == null) {
				mLogger.error("Received from TaaSRM a service ID that is not present neither in the Application nor in the Extended Service registry");
				return false;
			}
		}
		
		// Change the service status 
		if (isApp) nServices = appRegRow.mServiceList.size();
		else nServices = extRegRow.mServiceList.size();
		
		if (isApp) mLogger.info("Notified service failure is for an app with " + nServices + " services");
		else mLogger.info("Notified service failure is for an extended service with " + nServices + " services");

		for (i=0; i < nServices; i++) {
			
			if (isApp) {
				///////////////// APPLICATION ////////////////
				appServ = appRegRow.mServiceList.get(i);
				
				if (appServ.mServiceID.equals(serviceID)) {
					
					appServ.mStatus = AppService.ServiceInstallationStatus.ERROR;
					
					serviceDescription = appServ.mRequirements.mSemanticDescription;
					
					mLogger.info("Service n. " + i + " not installed (service ID=" + serviceID + " Service description = " + serviceDescription +")");
					
				}
			} else {
				///////////// EXTENDED SERVICE //////////////
				extSerService = extRegRow.mServiceList.get(i);
				
				if (extSerService.mServiceID.equals(serviceID)) {
						
					extSerService.mStatus = ExtServService.ServiceInstallationStatus.ERROR;
					
					serviceDescription = extSerService.mRequirements.mSemanticDescription;

					mLogger.info("Service n. " + i + " not installed  (service ID=" + serviceID + " Service description = " + serviceDescription + ")");
						
				} 
				
				
				
			}
			
		}
		if(isApp){
			 
			appRegRow.mStatus = AppRegistryRow.ApplicationInstallationStatus.ERROR;
			mLogger.info("Application (" + appRegRow.mAppName + ") is not installed.");
			
			// notify to the application the assigned Application ID
			try {
				ServiceManager.getInstance().notifyAssignedAppID(appRegRow.mAppID, false, errorMessage, serviceDescription);
				ServiceManager.busMessage("Service: " + serviceDescription + "cannot be installed", "error", ServiceManager.DEPENDABILITY);
			} catch (Exception e) {
				mLogger.error("Cannot notify the assigned ID to the installed application: " + e.getMessage());
			}
		}else{
			extRegRow.mStatus = ExtServiceRegistryRow.ExtServiceInstallationStatus.ERROR;
			
			mLogger.info("Extended service (" + extRegRow.mExtServiceUniqueName + ") is not installed.");
			mLogger.info("Notifying the assigned ID to the extended service");
			
			// notify to the extended service the assigned ID
			IExtendedService serv = mDiscovery.retrieveExtendedService(extRegRow.mExtServiceUniqueName); 
			if (serv != null) {
				ArrayList<String> serviceList = new ArrayList<String>();
				//ArrayList<String> tokenList = new ArrayList<String>();
				if (extRegRow.mServiceList != null) {
					for (i=0; i < extRegRow.mServiceList.size(); i++) {
						serviceList.add(extRegRow.mServiceList.get(i).mServiceID);
					//	tokenList.add(extRegRow.mServiceList.get(i).mRequirements.mCredentials);
					}
				}	
				String notifyMessage = "Installation failed. Service Description = " + serviceDescription + " Error message = " + errorMessage; 
				serv.notifyInstallation(false, 
										notifyMessage, 
						                extRegRow.mExtServiceID, 
						                serviceList,
						                null);
				ServiceManager.busMessage("Service: " + serviceDescription + "cannot be installed", "error", ServiceManager.DEPENDABILITY);
			} else {
				mLogger.warn("Cannot get the extended service interface to notify it the installation result");
			}
		}
		
		return true;
	}
	
	public String getThingServiceData(String extServID, String serviceID, String token) {
		RequestManager reqMng = new RequestManager();
		
		mLogger.info("Called getThingServiceData(" + extServID + ", " + serviceID + ", " + token + ") for extended service");
		
		ExtendedRegistry extReg = ServiceManager.getInstance().getExtendedRegistry();
		if (extReg.getExtService(extServID) == null) {
			mLogger.error("Received getThingServiceData request from an unregistered extended service");
			return "";
		}		
		
		return reqMng.getThingServiceData(serviceID, false, token);
	}
	
	
	public boolean setThingServiceData(String extServID, String serviceID, String data, String token) {
		RequestManager reqMng = new RequestManager();
		
		mLogger.info("Called setThingServiceData(" + extServID + ", " + serviceID + ", " + data.toString() + ", " + token + ") for extended service");
		
		ExtendedRegistry extReg = ServiceManager.getInstance().getExtendedRegistry();
		if (extReg.getExtService(extServID) == null) {
			mLogger.error("Received getThingServiceData request from an unregistered extended service");
			return false;
		}		
		
		return reqMng.setThingServiceData(serviceID, data, token);
	}
	

	public void notifyAgreementEPR(String serviceID, String agreementEPR) {
		mLogger.info("Received the agreement EPR from QoSM for service <"
				+ serviceID + ">: " + agreementEPR);
	}
	
	public void notifySLAViolation(String serviceID) {
		AppRegistryRow appRegRow = null;
		ExtServiceRegistryRow extRegRow = null;
//		AppService appServ;
//		ExtServService extSerService;
		ApplicationRegistry applicationReg = ServiceManager.getInstance().getAppRegistry();
		ExtendedRegistry extendedReg = ServiceManager.getInstance().getExtendedRegistry();
		boolean isApp = true;
		
		mLogger.warn("Received SLA violation notification. ServiceID=" + serviceID);

		if (serviceID == null) {
			mLogger.error("Received a null service ID from QoSM");
			return;
		}

		// Search the app or the extended service to which the notified service belongs, based on the service ID
		appRegRow = applicationReg.searchInstalledServiceID(serviceID);
		if (appRegRow == null) {
			isApp = false;
			extRegRow = extendedReg.searchInstalledServiceID(serviceID);
			if (extRegRow == null) {
				mLogger.error("Received from QoSM a service ID that is not present neither in the Application nor in the Extended Service registry");
				return;
			}
		}

		// Forward the notification
		if (isApp) {
			///////////////// APPLICATION ////////////////
			try {
				ServiceManager.getInstance().notifySLAViolation(appRegRow.mAppID, serviceID);
				mLogger.info("SLA violation notified to the application");
			} catch (Exception e) {
				mLogger.error("Cannot notify the SLA violation to the application: " + e.getMessage());
			}
		} else {
			///////////// EXTENDED SERVICE //////////////
			IExtendedService serv = mDiscovery.retrieveExtendedService(extRegRow.mExtServiceUniqueName); //getExtendedServiceIF();
			if (serv != null) {
				serv.notifySLAViolation(serviceID);
				mLogger.info("SLA violation notified to the extended service");
			} else {
				mLogger.warn("Cannot get the extended service interface to notify it the installation result");
			}
		}		
	}
	
	
	public boolean register(String extServiceID, String serviceID, String token) {
		mLogger.info("Called register for extended services (" + extServiceID + ", " + serviceID + ", " + token + ") )");
		
		ExtendedRegistry extReg = ServiceManager.getInstance().getExtendedRegistry();
		ExtServiceRegistryRow regRow = extReg.getExtService(extServiceID);
		if (regRow == null) {
			mLogger.error("Received register request from an unregistered extended service");
			return false;
		}
		if (!regRow.hasService(serviceID)) {
			mLogger.error("Received register request for a service not allocated by the extended service");
			return false;
		}
		
		RequestManager reqMng = new RequestManager();
		return reqMng.register(extServiceID, serviceID, token);
	}

	public boolean unregister(String extServiceID, String serviceID, String token) {
		mLogger.info("Called unregister for extended services (" + extServiceID + ", " + serviceID + ", " + token + ")");
		
		ExtendedRegistry extReg = ServiceManager.getInstance().getExtendedRegistry();
		ExtServiceRegistryRow regRow = extReg.getExtService(extServiceID);
		if (regRow == null) {
			mLogger.error("Received register request from an unregistered extended service");
			return false;
		}
		if (!regRow.hasService(serviceID)) {
			mLogger.error("Received register request for a service not allocated by the extended service");
			return false;
		}
		
		RequestManager reqMng = new RequestManager();
		return reqMng.unregister(extServiceID, serviceID, token);
	}	
	
	
	public boolean notifyNewMeasurement(String serviceID, JsonObject data) {
		
		mLogger.info("Called notifyNewMeasurement(" + serviceID + ", " + data + ")");
		
		// Search an application or an extended service that allocated the specified service
		
		ExtendedRegistry extReg = ServiceManager.getInstance().getExtendedRegistry();
		ExtServiceRegistryRow esRow = extReg.searchInstalledServiceID(serviceID);
		if (esRow != null) {
			// EXTENDED SERVICE
			IExtendedService serv = mDiscovery.retrieveExtendedService(esRow.mExtServiceUniqueName); //getExtendedServiceIF(esRow.mExtServiceUniqueName);
			if (serv != null) {
				try {
					serv.notifyData(serviceID, data);
					mLogger.info("Notified data to extended service (" + esRow.mExtServiceUniqueName + ")");
				} catch (Exception e) {
					mLogger.error("Cannot notify the measurement to extended service (" + esRow.mExtServiceUniqueName + ")");
				}
			} else {
				mLogger.error("Cannot get the extended service (" + esRow.mExtServiceUniqueName + ") to notify the measurement");
				return false;
			}
			
		} else {
			
			ApplicationRegistry applicationReg = ServiceManager.getInstance().getAppRegistry();
			AppRegistryRow appRow = applicationReg.searchInstalledServiceID(serviceID);
			if (appRow != null) {
				// APPLICATION
				
				// notify the data to the application
				try {
					ServiceManager.getInstance().notifyAppData(appRow.mAppID, serviceID, data);
					mLogger.info("Data notified to application");
				} catch (Exception e) {
					mLogger.error("Cannot notify data to the installed application: " + e.getMessage());
				}
			} else {
				mLogger.warn("No application/extended service is currently associated to the service related to the notification");
				return false;
			}
			
		}
		
		return true;
	}
	
	
	public String getTaskData(String extServID, String taskName) {
		mLogger.info("Called getTaskResult(" + extServID + ", " + taskName + ") for extended service");
		
		ExtendedRegistry extReg = ServiceManager.getInstance().getExtendedRegistry();
		if (extReg.getExtService(extServID) == null) {
			mLogger.error("Received getTaskResult request from an unregistered extended service");
			return "";
		}
		
		if (mBDMService == null) {
			mLogger.error("BDM service not available to request the task");
			return null;
		}

		String result = null;
		try {
			result = mBDMService.taskData(taskName, null);
			mLogger.info("Task executed");
			mLogger.debug("Task result: " + result);
		} catch (Exception e) {
			mLogger.error("Error executing the task: " + e.getMessage());
			result = null;
		}
		
		return result;
	}
	
	/** 
	 * @return the object that implements the TaaSRM interface 
	 */
	private TaaSResourceManager getResourceManagerIF() {
		if (mContext == null) return null;
		
		// NOTE: it is assumed that TaaSRM publishes this service only locally
		
		try {
			ServiceReference ref = mContext.getServiceReference(TaaSResourceManager.class.getName());
				
			if (ref != null) {
				return ((TaaSResourceManager)mContext.getService(ref));
			}
		} catch (java.lang.NoClassDefFoundError e) {
		    mLogger.error("No class definition found for TaaSRM");
		    return null;
		} catch (Exception e) {
			mLogger.error("TaaSRM not available: " + e.getMessage());
			return null;
		}
			
		return null;
	}
	
	
	/**
	 * @return the object that implements the QoSM interface 
	 */
	private QoSManagerInternalIF getQoSMIF() {
		if (mContext == null) return null;
		
		try {
			ServiceReference ref = mContext.getServiceReference(QoSManagerInternalIF.class.getName());
			
			if (ref != null) {
				return ((QoSManagerInternalIF)mContext.getService(ref));
			}
		} catch (java.lang.NoClassDefFoundError e) {
		    mLogger.error("No class definition found for QoSM");
		    return null;
		} catch (Exception e) {
			mLogger.error("QoSM not available: " + e.getMessage());
			return null;
		}
		
		return null;
	}
	
	/**
	 * Check if an application can be installed with the specified credentials
	 * @param regRow the application registry row
	 * @return true on success
	 */
	private boolean checkCredentials(ExtServiceRegistryRow regRow) {

//		System.out.println("*************************** ENABLE SECURITY ****************************");
//		return true;
		
		String msg;
		IAuthorizationService secmIF = null;
		byte[] decodedCredentials = null;
		
		if (regRow == null) return false;
		
		secmIF = ServiceManager.getInstance().getSECMIF();
		if (secmIF == null) {
			regRow.mStatus = ExtServiceRegistryRow.ExtServiceInstallationStatus.ERROR;
			msg = "SECM Bundle is not available";
			mLogger.error(msg);
			return false;
		}
		
		mLogger.info("Checking extdended service credentials (decoding from Base64)");
		try {
			decodedCredentials = Base64Utility.decode(regRow.mCredentials);
		} catch (Exception e) {
			mLogger.error("Error decoding Base64 input credentials: " + e.getMessage());
			return false;
		}
		
		try {
			if (!secmIF.checkAuthApplication(regRow.mExtServiceID, decodedCredentials)) {
				regRow.mStatus = ExtServiceRegistryRow.ExtServiceInstallationStatus.ERROR;

				msg = "Extended service not authorized";
				mLogger.error(msg);
				return false;
			}
		} catch (Exception e) {
			mLogger.error("Authorization failed: " + e.getMessage());
			return false;
		}
		mLogger.info("Extended service installation authorized");
		
		return true;
	}

	
	/** Logger */
	private static Logger mLogger = Logger.getLogger(ServiceManager.LOGGER_NAME);
	
	/** OSGi context */
	private BundleContext mContext;
	
	/** The BDM service to use tasks */
	private IDataManagerADService mBDMService;
	
	/** The discovery utility object */
	private Discovery mDiscovery = null;
	
	
}
