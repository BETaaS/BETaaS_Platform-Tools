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
package eu.betaas.service.bigdatamanager.dataservice;

import java.sql.Timestamp;
import java.util.HashMap;



public interface IDatasourceBDMInterface {

	public void setupService() ;
	
	public HashMap<String,String> getInfo(String passcode);
	
	public void sendData(String data);
	
	public void reportLastImportTime(Timestamp timestamp) ;
	
	public Timestamp getLastImportTime() ;
	
	public String[][] getData(int row,String query) ;
	
	//public String taskData(String idTask);
	
	//public List<String> getTaskList(String passcode);

	public void closeService();
	
}
