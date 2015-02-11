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
import java.util.List;
import java.util.Vector;

/**
* This interface defines the service exposed through OSGI by the ETSI Plugin for TA
*  
* @author Intecs
*/
public interface IAdaptorPlugin {

	/**
	 * @param listener When register() is called, this is the listener that will
	 *                 be notified when the corresponding data will be available. 
	 *                 Once the unregister() is called, the setListener should be
	 *                 called with a null argument.
	 */
	public void setListener(IAdaptorListener listener);
	
	/**
	 * @return the set of attributes of the available ETSI devices
	 */
	public Vector<HashMap<String, String>> discover();

	/**
	 * Register for a specific sensor device. The TA will be notified about
	 * new incoming data from that sensor 
	 * @param sensorID unique identifier of the sensor
	 * @return true iff the registration is completed successfully
	 */
	public boolean register(String sensorID);
	
	/**
	 * Unregister for a specific sensor device. TA will stop receiving notifications.
	 * @param sensorID unique identifier of the sensor
	 * @return true if the unregistration is completed successfully
	 */
	public boolean unregister(String sensorID);
	
	
	/**
	 * @param ThingId as the SensorID created.
	 * @return the current measurement of the sensor/thing
	 */
	public String getData(String sensorID);
	
	/**
	 * @param ThingId as the SensorID created.
	 * @return the current measurement of the sensor/thing
	 */
	public String setData(String sensorID, String value);
}
