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
package eu.betaas.service.bigdatamanager.applicationdataservice;

import java.util.HashMap;
import java.util.List;

import eu.betaas.service.bigdatamanager.service.datatask.data.TaskInfo;

public interface IDataManagerADService {
	
	public String taskData(String idTask);
	
	public String taskData(String idTask,HashMap<String,String> input);
	
	//todo old interface
	public String getTaskData(String taskId);
	
	public List<TaskInfo> getTaskList(String passcode);
	
	public void setupService();
	
	public void close();
	
}
