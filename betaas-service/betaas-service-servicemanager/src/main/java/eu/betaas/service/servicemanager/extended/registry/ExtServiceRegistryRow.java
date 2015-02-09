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

package eu.betaas.service.servicemanager.extended.registry;

import java.util.Iterator;
import java.util.Vector;


/**
 * It represents a row of the Extended services registry
 * @author Intecs
 */
public class ExtServiceRegistryRow {
	/** Possible status of application installation process */
	public enum ExtServiceInstallationStatus { 
		UNDEFINED,
		INSTALLING,
        INSTALLED,
        ERROR;
	
		public static int getCode(ExtServiceInstallationStatus status) {
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
		
		public static ExtServiceInstallationStatus getEnum(int code) {
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

	ExtServiceRegistryRow(String extServiceID, String extServiceName, ExtServiceInstallationStatus status, String credentials) {
		mExtServiceID = extServiceID;
		mExtServiceUniqueName = extServiceName;
		mStatus = status;
		mServiceList = new Vector<ExtServService>();
		mCredentials = credentials;
	}

	ExtServiceRegistryRow(String extServiceID, String extServiceUniqueName, String credentials) {
		mExtServiceID = extServiceID;
		mExtServiceUniqueName = extServiceUniqueName;
		mStatus = ExtServiceInstallationStatus.INSTALLING;
		mServiceList = new Vector<ExtServService>();
		mCredentials = credentials;
	}
	
	
	/**
	 * @param extServiceID
	 * @return true iff the extended service has the specified on-the-fly service
	 */
	public boolean hasService(String extServiceID) {
		if (mServiceList == null) return false;
		
		ExtServService serv;
		Iterator<ExtServService> iterator = mServiceList.iterator();
		while (iterator.hasNext()) {
			serv = iterator.next();
			if (serv != null) {
				if ((serv.mServiceID != null) && (serv.mServiceID.compareTo(extServiceID) == 0)) {
					return true;
				}
			}
		}
		return false;
	}

	/** Unique generated identifier of the service associated to application */
	public String mExtServiceID;
	
	/** The unique identifier of the extended service in the instance */
	public String mExtServiceUniqueName;
	
	/** Status of the installation */
	public ExtServiceInstallationStatus mStatus;
	
	/** Base64-encoded credentials */
	public String mCredentials;
	
	/** List of the services' ID associated to the application */
	public Vector<ExtServService> mServiceList;
	
}
