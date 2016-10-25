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

package eu.betaas.adaptation.thingsadaptor.api;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import eu.betaas.adaptation.thingsadaptor.config.Thing;
import eu.betaas.adaptation.thingsadaptor.config.ThingWithContext;
import eu.betaas.taas.bigdatamanager.database.service.ThingsData;

/**
 * @author nzonidis
 *This is the main Service of TA (Things Adaptor) which publishes the available methods of this service
 * 
 */
public interface ThingsAdaptor {	

	
	/**
	 * @return a Vector of HashMaps the number of which is the Devices found.
	 * The HashMap MUST contain at least the K,V of ID. Other characteristics might include
	 * the battery level, the location etc. 			
	 */
	//public Vector<HashMap<String, String>> discover();
	
	/**
	 * @param ThingId as the SensorID created.
	 * @return the current measurement of the sensor/thing
	 */
	//public String getData(String ThingId);
	
	
	/**
	 * @param ThingID for which the interested module wants to receive updates. 
	 * @return true if the registration is correct.
	 */
	public boolean register(String ThingID);
	
	/**
	 * @param ThingId as the SensorID created.
	 * @param Value to be set for the thingId
	 * @return the current measurement of the sensor/thing
	 */
	public String setThingValue(String thingId, String value);
	
	/**
	 * Method which essentially initializes the communication and provides 
	 * the list of Things found to the upper layers
	 * @return List of Thing objects.
	 */
	public Vector<HashMap<String, String>> getThingsLocal();
	/**
	 * Method which essentially initializes the communication and provides 
	 * the list of Things found to the upper layers
	 * It might provide a more technically detailed version of the same list as the getThingsLocal method  
	 * @return List of Thing objects.
	 */
	public List<Thing> getPhysicalThingsData();
	
	/**
	 * Method for providing a more detailed version of a particular Thing to the upper layers
	 * @return A ThingWithContext object.
	 */
	public ThingWithContext translateThingsInformation(final Thing theThing);
	
	/**
	 * 
	 */
	public List<ThingsData> getMeasurement(List<ThingsData> selectedThingsList);
	
	/**
	 * 
	 */
	public void setMeasurement(List<ThingsData> selectedThingsList);
	
	/**
	 * Method for getting the current list of Things available with their live data
	 * @return A List of ThingsData 
	 */
	public List<ThingsData>  getRealTimeInformation();
	
	/**
	 * previous version of getMeasurement method used by taasresourcemanager
	 * @return A ThingWithContext object.
	 */
	public ThingsData getMeasurement(String thingId);
	
	/**
	 * Method for getting the current list of Things connected to the GW
	 * @return A List of ThingsData 
	 */
	public List<ThingsData> getMeasurementThingsMonitoring(List<ThingsData> thingsList);
	
	/**
	 *subscribe updates from a given Thing with a certain frequency
	 * * @return true if all the subscriptions for all the things have been successful
	 */
	public boolean subscribe(String thingId, int seconds);
	
	/**
	 *subscribe updates from a given Thing when available
	 * * @return true if all the subscriptions for all the things have been successful
	 */
	/*public boolean subscribe(String thingId);*/
	
	/**
	 * unsubscribe updates from things
	 * * @return true if all the unsubscriptions for all the things have been successful
	 */
	public boolean unsubscribe(List<String> thingIdsList);
}
