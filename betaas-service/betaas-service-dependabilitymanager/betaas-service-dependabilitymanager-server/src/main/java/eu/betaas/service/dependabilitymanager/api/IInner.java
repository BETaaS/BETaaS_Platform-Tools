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
// Component: DM
// Responsible: Intecs

package eu.betaas.service.dependabilitymanager.api;

/**
 * This interface defines the DM service exposed to the rest of bundles of the
 * same gateway. 
 *  
 * @author Intecs
 */
public interface IInner {
	
	/**
	 * Definition of layers to be notified along with failures details
	 */
	public enum FAILURE_LAYER {
		SERVICE,
		TAAS,
		ADAPTATION;
		
		public static int getID(FAILURE_LAYER layer) {
			switch (layer) {
				case SERVICE: return 0;
				case TAAS: return 1;
				case ADAPTATION: return 2;
				default: return -1;
			}				
		}
	}
	
	/**
	 * Definition of failure codes that can be notified to the Dependability Manager 
	 */
	public enum FAILURE_CODE {
		BUNDLE_INITIALIZATION,
		BUNDLE_FINALIZATION,
		OSGI_EXTERNAL_INTERFACE_CALL,
		OSGI_INTERNAL_INTERFACE_CALL,
		APP_INSTALLATION,
		RESOURCE_ALLOCATION;
		
		public static int getID(FAILURE_CODE code) {
			switch (code) {
				case BUNDLE_INITIALIZATION: return 0;
				case BUNDLE_FINALIZATION: return 1;
				case OSGI_EXTERNAL_INTERFACE_CALL: return 2;
				case OSGI_INTERNAL_INTERFACE_CALL: return 3;
				case APP_INSTALLATION: return 4;
				case RESOURCE_ALLOCATION: return 5;
				default: return -1;
			}
		}
	}
	
	public enum FAILURE_LEVEL {
		LOW,
		MEDIUM,
		HIGH;
		
		public static int getID(FAILURE_LEVEL level) {
			switch (level) {
				case LOW: return 0;
				case MEDIUM: return 1;
				case HIGH: return 2;
				default: return -1;
			}
		}
	}
	

	
}
