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
 * This interface defines the DM service exposed through OSGI and visible also
 * from the other gateways.
 *  
 * @author Intecs
 */
public interface IExtern {
	
	/**
	 * @return a report about the vitality of components and GWs (in case of DM*)
	 */
	public String getVitalityReport();
	
	/**
	 * @param maxRecords maximum length of result
	 * @return a report with the failures notified by components
	 */
	public String getFailureReport(int maxRecords);
	
	/**
	 * Used by DM* to check if DMs retrieved with DOSGi are actually healthy
	 * @return true if healthy
	 */
	public boolean checkVitality();
}
