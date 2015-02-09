/*
 Copyright 2014-2015 Hewlett-Packard Development Company, L.P.

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
package eu.betaas.taas.bigdatamanager.logger.service;

import java.sql.SQLException;
import java.util.Vector;

import eu.betaas.taas.bigdatamanager.core.services.ITaasBigDataManager;

public interface IBigDataLoggerService {
	
	// used to inject jdbcurl depends on os 
	public void setService(ITaasBigDataManager service) throws SQLException;

	// create a table for data of a specific thing
	public void createThing(String thingID, String name, String unit, String tags);
	
	// delete a table for data of a specific thing if exists
	public boolean destroyThing(String thingID);
	
	// save data from a specific thing
	public void saveDataForThing(String thingID, String time, byte[] data);
	
	// check if a thing is already registered
	public boolean thingContainerExists(String thingID);
	
	// get data stored by a thing 
	public byte[] getThingData(String param);
	
	// get list of thing registered on database
	public Vector<String> getThingList();

	// get count of data stored
	public int countRowData();
	
	// clear old data
	public void clearTables();
	
	// close all connections
	public void close();


}
