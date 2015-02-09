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

/** 
 * It represent one of the "on-the-fly" services requested by the extended service 
 */
public class ExtServService {
	
	/** Possible status of an on-the-fly service (required by an extended service) installation process */
    public enum ServiceInstallationStatus {
    	UNDEFINED,
    	ALLOCATING,
    	QOS_NEGOTIATION,
    	ALLOCATED,
    	ERROR;
    	
    	public static int getCode(ServiceInstallationStatus status) {
			switch (status) {
				case ALLOCATING:
					return 1;
				case QOS_NEGOTIATION:
					return 2;
				case ALLOCATED:
					return 3;
				case ERROR:
					return 4;
				default:
					return 0;
			}
		}
		
		public static ServiceInstallationStatus getEnum(int code) {
			switch (code) {
				case 1:
					return ALLOCATING;
				case 2:
					return QOS_NEGOTIATION;
				case 3:
					return ALLOCATED;
				case 4:
					return ERROR;
				default:
					return UNDEFINED;
			}
		}
    };
    
	/** Name of the service */
	public String mName;
	
	/** Status of the service installation */
	public ServiceInstallationStatus mStatus;
	
	/** The requirements of the service declared by the extended service manifest */
	public ServiceRequirement mRequirements;
	
	/** The service ID returned by TaaSRM */
	public String mServiceID;
}

