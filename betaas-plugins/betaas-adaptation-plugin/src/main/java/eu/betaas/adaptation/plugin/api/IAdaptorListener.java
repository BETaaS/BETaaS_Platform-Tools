/**
* Copyright 2014-2015 Converge ICT
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*
*/

package eu.betaas.adaptation.plugin.api;

import java.util.HashMap;

/**
 * This is the interface to be implemented by whoever wants to receive data 
 * notification from the ETSI plugin
 * @author Intecs
 */
public interface IAdaptorListener {

	/**
	 * Notify a measure from a sensor
	 * @param type a string giving some info about the device, e.g. the class "ETSI"
	 * @param resourceID id of the sensor
	 * @param value
	 * @return true on notification success
	 */
	public boolean notify(String type, String resourceID, HashMap<String, String> value);
	
	/**
	 * Notify cm a thing was removed
	 * @param thingId id of the sensor
	 * @return true on success
	 */
	public boolean removeThing(String thingId);
	
}
