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

package eu.betaas.service.servicemanager.application.registry;

import java.util.Iterator;
import java.util.Vector;

import eu.betaas.service.servicemanager.extended.registry.ExtServService;


/**
 * It represents a row of the Application registry
 * @author Intecs
 */
public class AppRegistryRow {
	/** Possible status of application installation process */
	public enum ApplicationInstallationStatus { 
		UNDEFINED,
		INSTALLING,
        INSTALLED,
        ERROR;
	
		public static int getCode(ApplicationInstallationStatus status) {
			switch (status) {
				case INSTALLING:
					return 1;
				case INSTALLED:
					return 2;
				case ERROR:
					return 3;
				default:
					return 0;
			}
		}
		
		public static ApplicationInstallationStatus getEnum(int code) {
			switch (code) {
				case 1:
					return INSTALLING;
				case 2:
					return INSTALLED;
				case 3:
					return ERROR;
				default:
					return UNDEFINED;
			}
		}
	};

	AppRegistryRow(String appID, String appName, String credentials, String notificationAddress, ApplicationInstallationStatus status) {
		mAppID = appID;
		mAppName = appName;
		mCredentials = credentials;
		mNotificationAddress = notificationAddress;
		mStatus = status;
		mServiceList = new Vector<AppService>();
		mExtServiceList = new Vector<ExtServService>();
	}

	AppRegistryRow(String appID, String appName, String credentials, String notificationAddress) {
		mAppID = appID;
		mAppName = appName;
		mCredentials = credentials;
		mNotificationAddress = notificationAddress;
		mStatus = ApplicationInstallationStatus.INSTALLING;
		mServiceList = new Vector<AppService>();
		mExtServiceList = new Vector<ExtServService>();
	}
	
	/**
	 * @param serviceID
	 * @return true iff the application has the specified on-the-fly service
	 */
	public boolean hasService(String serviceID) {
		if (mServiceList == null) return false;
		
		AppService serv;
		Iterator<AppService> iterator = mServiceList.iterator();
		while (iterator.hasNext()) {
			serv = iterator.next();
			if (serv != null) {
				if ((serv.mServiceID != null) && (serv.mServiceID.compareTo(serviceID) == 0)) {
					return true;
				}
			}
		}
		return false;
	}

	/** Unique application identifier */
	public String mAppID;
	
	/** The application name */
	public String mAppName;
	
	/** Status of the installation */
	public ApplicationInstallationStatus mStatus;
	
	/** List of the on-the-fly services' ID associated to the application */
	public Vector<AppService> mServiceList;
	
	/** List of the extended services' ID associated to the application */
	public Vector<ExtServService> mExtServiceList;
	
	/** Credentials to install the application */
	public String mCredentials;
	
	/** It is the address at which the application receives data from
	 * the SM (e.g. measurements, install notifications, etc) */
	public String mNotificationAddress;
}
