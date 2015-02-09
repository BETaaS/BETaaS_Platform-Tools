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
package eu.betaas.service.bigdatamanager.service.datatask.data;

import java.sql.Timestamp;
import java.util.HashMap;

public class TaskData {
	// current status of this task run
	public enum TaskStatus {
	    RUNNING,ENDED,FAILED,INITIALIZED 
	}
	
	private int taskRunId;
	private Timestamp tasktarttime;
	private HashMap<String,String> actualinput;
	private TaskInfo taskdescription;
	private TaskStatus status;
	// now task is represented as a plain string but it shoudl be made as jackson json object
	private String taskOutput;
	
	
	
	public TaskData(int taskRunId, Timestamp tasktarttime,
			HashMap<String, String> actualinput, TaskInfo taskdescription,
			TaskStatus status, String taskOutput) {
		super();
		this.taskRunId = taskRunId;
		this.tasktarttime = tasktarttime;
		this.actualinput = actualinput;
		this.taskdescription = taskdescription;
		this.status = status;
		this.taskOutput = taskOutput;
	}
	public HashMap<String, String> getActualinput() {
		return actualinput;
	}
	public void setActualinput(HashMap<String, String> actualinput) {
		this.actualinput = actualinput;
	}
	public TaskStatus getStatus() {
		return status;
	}
	public void setStatus(TaskStatus status) {
		this.status = status;
	}
	public String getTaskOutput() {
		return taskOutput;
	}
	public void setTaskOutput(String taskOutput) {
		this.taskOutput = taskOutput;
	}
	public int getTaskRunId() {
		return taskRunId;
	}
	public Timestamp getTasktarttime() {
		return tasktarttime;
	}
	public void setTasktarttime(Timestamp time) {
		tasktarttime=time;
	}
	public TaskInfo getTaskdescription() {
		return taskdescription;
	}
	
	
	
}
