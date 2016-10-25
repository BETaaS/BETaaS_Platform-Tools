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

package eu.betaas.service.servicemanager.application;

import java.util.Vector;

import org.apache.cxf.common.util.Base64Exception;
import org.apache.cxf.common.util.Base64Utility;
import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import eu.betaas.service.servicemanager.ServiceManager;
import eu.betaas.service.servicemanager.application.registry.AppManifest;
import eu.betaas.service.servicemanager.application.registry.AppRegistryRow;
import eu.betaas.service.servicemanager.application.registry.AppService;
import eu.betaas.service.servicemanager.application.registry.ApplicationRegistry;
import eu.betaas.service.servicemanager.application.registry.ApplicationRegistry.NotificationAddressType;
import eu.betaas.service.servicemanager.extended.discovery.Discovery;
import eu.betaas.taas.qosmanager.api.QoSManagerInternalIF;
import eu.betaas.taas.taasresourcesmanager.api.Feature;
import eu.betaas.taas.taasresourcesmanager.api.TaaSResourceManager;
import eu.betaas.service.securitymanager.service.IAuthorizationService;

/**
 * This class is used to manage application installation request
 * @author Intecs
 */
public class Installer {
	
	// Used to notify services list to installed application
	private class ServiceList {
		public Vector<String> serviceList;
		public Vector<String> tokenList;
	}
	
	
	public Installer() {
	}
	
	/**
	 * Install an application into the platform
	 * @param content the manifest
	 * @param discovery the discovery manager
	 * @return the assigned application identifier, an empty string on error
	 */
	public synchronized String install(String content, Discovery discovery) {
		int i;
//		final AppRegistryRow regRow = null;
		AppService service;
		String msg;
		String appId = null;
		
		ApplicationRegistry applicationReg = ServiceManager.getInstance().getAppRegistry();
		
		TaaSResourceManager rmIF = null;
		
		// Parse the manifestFile
		final AppManifest manifest = new AppManifest();
		try {
			manifest.load(content);
		} catch (Exception e) {
			msg = "Error loading the Manifest: " + e.getMessage();
			mLogger.error(msg);
			return "";
		} 
		
		mLogger.info("Installing the application: " + manifest.mApplicationName);
		
		try {
			String addr = manifest.mNotificationAddress;
			NotificationAddressType type = NotificationAddressType.REST_NOTIFICATION_ADDRESS;
			if (manifest.mGCMId != null) {
				type = NotificationAddressType.GCM_NOTIFICATION_ADDRESS;
				addr = manifest.mGCMId;
			}
			// Generate a new app row in the registry
			final AppRegistryRow regRow = applicationReg.addNewApplication(manifest.mApplicationName, 
													  manifest.mCredentials,
					                                  addr,
					                                  type);
			mLogger.info("Generated app id: " + regRow.mAppID);
			appId = regRow.mAppID;
			
			// Check credentials
			if (!checkCredentials(regRow)) {
				try {
					msg = "Authorization failed during installation";
					ServiceManager.getInstance().notifyInstallError(regRow.mAppID, msg);
				} catch (Exception e) {
					mLogger.error("Cannot notify the installation error to the application: " + e.getMessage());
				}
				return "";
			}
			
			// Check if all the required extended services are found
			for (i=0; i < manifest.mServices.length; i++) {
				if (manifest.mServices[i].mIsExtended) {
					if (discovery.retrieveExtendedService(manifest.mServices[i].mUniqueExtendedServiceID) == null) {
						regRow.mStatus = AppRegistryRow.ApplicationInstallationStatus.ERROR;
						
						// Notify the application about the missing service
						msg = "Missing required extended service: " + manifest.mServices[i].mUniqueExtendedServiceID;
						mLogger.error(msg);
						try {
							ServiceManager.getInstance().notifyInstallError(regRow.mAppID, msg);
						} catch (Exception e) {
							mLogger.error("Cannot notify the installation error to the application: " + e.getMessage());
						}
						return "";
					}
				}
			}
			ServiceManager.busMessage("App: " + appId + " installed", "info", ServiceManager.MONITORING);
			
			Thread allocationThread = new Thread() {
				public void run() {
					allocateServices(manifest, regRow);				
				}
			};
			allocationThread.start();
		} catch (Exception e) {
			msg = "Error installing the app: " + e.getMessage();
			mLogger.error(msg);
			if (appId != null) {
				try {
					ServiceManager.getInstance().notifyInstallError(appId, msg);
				} catch (Exception e2) {
					mLogger.error("Cannot notify the installation error to the application: " + e.getMessage());
				}
			}
			return "";
		}
		
		
		return appId;
	}
	
	
	/**
	 * Uninstall the specified application
	 * @param appID the ID returned by the installation process
	 * @param manifestContent the manifest from which info (e.g. security ones) are taken
	 * @param discovery the discovery manager
	 * @return true on success, false otherwise
	 */
	public synchronized boolean uninstall(String appID,  Discovery discovery) {
		TaaSResourceManager rmIF = null;
		AppService serv = null;
		String servID = null;
		int i;
		
		
		
		ApplicationRegistry applicationReg = ServiceManager.getInstance().getAppRegistry();
		AppRegistryRow regRow = applicationReg.getApp(appID, true);
		
		if (regRow == null) {
			mLogger.error("The specified application ID is not in the registry. Cannot uninstall");
			return false;
		}
		if ((regRow.mServiceList == null) || (regRow.mServiceList.size() == 0)) {
			mLogger.warn("The application ID being uninstalled has no associated services");
			try {
				mLogger.info("Removing the application from registry");
				applicationReg.remove(appID);
			} catch (Exception e) {
				mLogger.error("Cannot remove the application from registry: " + e.getMessage());
				return false;
			}
			mLogger.info("Application uninstalled");
			return true;
		}
		
		// Obtain the services of the TaasRM
		rmIF = ServiceManager.getInstance().getResourceManagerIF();
		if (rmIF == null) {
			mLogger.error("Taas RM Bundle is not available to execute uninstall");
			return false;
		}
		
		for (i=0; i < regRow.mServiceList.size(); i++) {
			serv = regRow.mServiceList.elementAt(i);
			servID = serv.mServiceID;
			try {
				rmIF.freeLocalResources(servID);
			} catch (Exception e) {
				mLogger.error("Cannot uninstall service: " + servID);
				mLogger.error("Failed to uninstall application");
				regRow.mStatus = AppRegistryRow.ApplicationInstallationStatus.ERROR;
				return false;
			}
		}
		try {
			applicationReg.remove(appID);
		} catch (Exception e) {
			mLogger.error("Cannot remove the application from registry: " + e.getMessage());
			return false;
		}

		mLogger.info("Application uninstalled");
		ServiceManager.busMessage("Application: " + appID + "uninstalled", "info", ServiceManager.MONITORING);
		
		return true;		
	}
	
	/**
	 * stop the specified application
	 * @param appID the ID returned by the installation process
	 * @return true on success, false otherwise
	 */
	public synchronized boolean stop(String appID) {
		TaaSResourceManager rmIF = null;
		AppService serv = null;
		String servID = null;
		int i;

		
		ApplicationRegistry applicationReg = ServiceManager.getInstance().getAppRegistry();
		AppRegistryRow regRow = applicationReg.getApp(appID, true);

		
		if (regRow == null) {
			mLogger.error("The specified application ID is not in the registry. Cannot stop");
			return false;
		}

		if ((regRow.mServiceList == null) || (regRow.mServiceList.size() == 0)) {
			mLogger.warn("The application ID being stopped has no associated services");
		}

		// Obtain the services of the TaasRM
		rmIF = ServiceManager.getInstance().getResourceManagerIF();

		if (rmIF == null) {
			mLogger.error("Taas RM Bundle is not available to execute stop");
			return false;
		}

		for (i=0; i < regRow.mServiceList.size(); i++) {
			serv = regRow.mServiceList.elementAt(i);
			servID = serv.mServiceID;
			byte[] decodedToken = null;
			if (serv.mRequirements.mCredentials != null){

			try {
				decodedToken = Base64Utility.decode(serv.mRequirements.mCredentials);
				rmIF.unRegisterService(servID, decodedToken);
			} catch (Exception e) {
				mLogger.error("Cannot stop service: " + servID);
				mLogger.error("Failed to stop application");
				regRow.mStatus = AppRegistryRow.ApplicationInstallationStatus.ERROR;
				return false;
			}
			}
		}
		
		mLogger.info("Application stopped");
		ServiceManager.busMessage("Application: " + appID + " stopped", "info", ServiceManager.MONITORING);
		
		
		return true;
	}
	
	/**
	 * start the specified application
	 * @param appID the ID returned by the installation process
	 * @return true on success, false otherwise
	 */
	public synchronized boolean start(String appID) {
		TaaSResourceManager rmIF = null;
		AppService serv = null;
		String servID = null;
		int i;
		
			
		ApplicationRegistry applicationReg = ServiceManager.getInstance().getAppRegistry();
		AppRegistryRow regRow = applicationReg.getApp(appID, true);
		
		if (regRow == null) {
			mLogger.error("The specified application ID is not in the registry. Cannot start");
			return false;
		}
		if ((regRow.mServiceList == null) || (regRow.mServiceList.size() == 0)) {
			mLogger.warn("The application ID being stopped has no associated services");
		}
		
		// Obtain the services of the TaasRM
		rmIF = ServiceManager.getInstance().getResourceManagerIF();
		if (rmIF == null) {
			mLogger.error("Taas RM Bundle is not available to execute start");
			return false;
		}
		
		for (i=0; i < regRow.mServiceList.size(); i++) {
			serv = regRow.mServiceList.elementAt(i);
			servID = serv.mServiceID;
			byte[] decodedToken = null;
			if (serv.mRequirements.mCredentials != null){

				try {
					decodedToken = Base64Utility.decode(serv.mRequirements.mCredentials);
					rmIF.startFullApplication(servID, decodedToken);
				} catch (Exception e) {
					mLogger.error("Cannot start service: " + servID);
					mLogger.error("Failed to start application");
					regRow.mStatus = AppRegistryRow.ApplicationInstallationStatus.ERROR;
					return false;
				}
			}
		}
		
		mLogger.info("Application stopped");
		ServiceManager.busMessage("Application: " + appID + " stopped", "info", ServiceManager.MONITORING);
		return true;
	}
	
	
	public synchronized String getApplicationServices(String appID) {
		ApplicationRegistry applicationReg = ServiceManager.getInstance().getAppRegistry();
		AppRegistryRow regRow = applicationReg.getApp(appID, true);
		
		Gson gson = new Gson();		
	    JsonObject jsonResult = new JsonObject();
	    ServiceList list = new ServiceList();
	    list.serviceList = new Vector<String>();
	    list.tokenList = new Vector<String>();
		
	    if ((regRow == null) || (regRow.mServiceList == null)) {
			mLogger.error("The specified application ID is not in the registry. Returning a empty list of services.");
		} else {
			for (AppService serv : regRow.mServiceList) {
				list.serviceList.add(serv.mServiceID);
				list.tokenList.add(serv.mRequirements.mCredentials);
			}
		}
	    jsonResult.add("InstallationInfo", gson.toJsonTree(list));
		
		return jsonResult.toString();
	}
	
	private byte[] decodeCredentials(AppManifest manifest) {
		byte[] decodedCredentials = null;
		
		mLogger.info("Checking application credentials (decoding from Base64)");
		try {
			decodedCredentials = Base64Utility.decode(manifest.mCredentials);
		} catch (Base64Exception e) {
			mLogger.error("Error decoding Base64 input credentials: " + e.getMessage());
			return null;
		}
		
		return decodedCredentials;
	}

	/**
	 * Check if an application can be installed with the specified credentials
	 * @param regRow the application registry row
	 * @return true on success
	 */
	private boolean checkCredentials(AppRegistryRow regRow) {
		
		
//		System.out.println("*************************** ENABLE SECURITY ****************************");
//		return true;
		
		String msg;
		IAuthorizationService secmIF = null;
		byte[] decodedCredentials = null;
		
		if (regRow == null) return false;
		
		secmIF = ServiceManager.getInstance().getSECMIF();
		if (secmIF == null) {
			regRow.mStatus = AppRegistryRow.ApplicationInstallationStatus.ERROR;

			msg = "SECM Bundle is not available";
			mLogger.error(msg);
			try {
				ServiceManager.getInstance().notifyInstallError(regRow.mAppID, msg);
			} catch (Exception e) {
				mLogger.error("Cannot notify the installation error to the application: " + e.getMessage());
			}			
			
			return false;
		}
		
		mLogger.info("Checking application credentials (decoding from Base64)");
		try {
			decodedCredentials = Base64Utility.decode(regRow.mCredentials);
		} catch (Base64Exception e) {
			mLogger.error("Error decoding Base64 input credentials: " + e.getMessage());
			return false;
		}
		try {
			if (!secmIF.checkAuthApplication(regRow.mAppID, decodedCredentials)) {
				regRow.mStatus = AppRegistryRow.ApplicationInstallationStatus.ERROR;

				msg = "Application not authorized";
				mLogger.error(msg);
				try {
					ServiceManager.getInstance().notifyInstallError(regRow.mAppID, msg);
				} catch (Exception e) {
					mLogger.error("Cannot notify the installation error to the application: " + e.getMessage());
				}			
				
				return false;
			}
		} catch (Exception e) {
			mLogger.error("Authorization failed: " + e.getMessage());
			return false;
		}
		mLogger.info("Application installation authorized");
		
		return true;
	}
	

	private void allocateServices(AppManifest manifest, AppRegistryRow regRow) {
		AppService service;
		String msg;
		int i;
		TaaSResourceManager rmIF = null;

		// Obtain the the TaasRM service
		rmIF = ServiceManager.getInstance().getResourceManagerIF();
		if (rmIF == null) {
			regRow.mStatus = AppRegistryRow.ApplicationInstallationStatus.ERROR;

			msg = "Taas RM Bundle is not available";
			mLogger.error(msg);
			try {
				ServiceManager.getInstance().notifyInstallError(regRow.mAppID, msg);
			} catch (Exception e) {
				mLogger.error("Cannot notify the installation error to the application: " + e.getMessage());
			}
			return;
		}
		
		// Put the allocating services into the registry
		int nOTF = 0;
		for (i=0; i < manifest.mServices.length; i++) {
			// skip extended services as they already allocated resources for themselves
			if (manifest.mServices[i].mIsExtended) continue;
			nOTF++;
			
			service = new AppService();
			service.mName = ApplicationRegistry.APP_SERVICE_NAME_PREFIX + i;
			service.mRequirements = manifest.mServices[i];
			service.mStatus = AppService.ServiceInstallationStatus.ALLOCATING;
			regRow.mServiceList.add(service);
		}		
		// in case no on-the-fly service has to be installed, set this app in the installed state 
		if (nOTF == 0) {
			regRow.mStatus = AppRegistryRow.ApplicationInstallationStatus.INSTALLED;
		}
		
		
		// Loop on the requested on-the-fly services to allocate the corresponding resources
		// All services must be allocated in order to successfully complete the application installation
		for (i=0; i < manifest.mServices.length; i++) {
			
			// skip extended services as they already allocated resources for themselves
			if (manifest.mServices[i].mIsExtended) continue;
			
			service = regRow.mServiceList.get(i);
			
			mLogger.info("Allocating resources for service n. " + i + " (" + service.mName + ")");
			
			Feature feature = new Feature(regRow.mAppID, 
					                      service.mRequirements.mSemanticDescription,
					                      service.mRequirements.mLocation, 
					                      service.mRequirements.mType,
					                      service.mRequirements.mTrust,
					                      (int)service.mRequirements.mPeriod);
			service.mServiceID = rmIF.allocateResources(feature);
			if ((service.mServiceID == null) || (service.mServiceID.length() == 0)) {
				
				regRow.mStatus = AppRegistryRow.ApplicationInstallationStatus.ERROR;
				service.mStatus = AppService.ServiceInstallationStatus.ERROR;
				
				msg = "TaaSRM failed to allocate resources";
				mLogger.error(msg);
				
				try {
					ServiceManager.getInstance().notifyInstallError(regRow.mAppID, msg);
				} catch (Exception e) {
					mLogger.error("Cannot notify the installation error to the application: " + e.getMessage());
				}
				return;
			}

			mLogger.info("Resources allocated, starting QoS negotiation (Service ID=" + service.mServiceID + ")");
			service.mStatus = AppService.ServiceInstallationStatus.QOS_NEGOTIATION;

			// start the QoS negotiation
			QoSManagerInternalIF qosm;
			qosm = ServiceManager.getInstance().getQoSMIF();
			if (qosm == null) {
				msg = "QoSM not available for QoS negotiation";
				mLogger.error(msg);
				try {
					ServiceManager.getInstance().notifyInstallError(regRow.mAppID, msg);
				} catch (Exception e) {
					mLogger.error("Cannot notify the installation error to the application: " + e.getMessage());
					e.printStackTrace();
				}
				return;
			}
			
			mLogger.info("Requesting the negotiation template to QoSM");
			String template = qosm.getTemplate();
			
			if (template == null) {
				mLogger.error("Got null QoS template from QoSM");
				return;
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
			
			// Once the service is installed, TaaSRM will call notifyServiceInstallation(serviceID)
			
		} // loop on requested services
		
	}
	
	/** Logger */
	private static Logger mLogger = Logger.getLogger(ServiceManager.LOGGER_NAME);

}
