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

import java.util.List;


public class TaskInfo {
	// tells if task run over database data or HDFS database
	// the first will use db service available at the invokation time, the second requires an HDFS Hive database to be available
	public enum TaskSource {
		SQLDATABASE,NOSQLDATABASE
	}
	
	// if task is a real time that immediately reply with data or it need to be polled untill it reach completition
	public enum TaskType {
		SYNCRONOUS,ASYNCRONOUS
	}
	// result is represented in a single row or as an array of rows
	public enum rowStyle {
		SINGLE,MULTIPLE
	}
	// tells which data is considered by the task, for example a task that analyze only presence data will use PRESENCE
	public enum TaskDataType {
		PRESENCE,TEMPERATURE,TRAFFIC,ALL
	}
	
	private String taskname;
	private TaskDataType taskDataType;
	private TaskSource source;
	private TaskType type;
	private List<Descriptor> inputList;
	private List<Descriptor> outputList;
	private String description;
	
	
	public TaskInfo(String taskname, String description,TaskSource source, TaskType type,TaskDataType taskDataType,
			List<Descriptor> inputList, List<Descriptor> outputList) {
		super();
		this.taskname = taskname;
		this.source = source;
		this.type = type;
		this.inputList = inputList;
		this.outputList = outputList;
		this.taskDataType =taskDataType;
		this.description=description;
	}
	public TaskDataType getTaskDataType() {
		return taskDataType;
	}
	public String getTaskname() {
		return taskname;
	}
	public String getDescription() {
		return description;
	}
	public TaskSource getSource() {
		return source;
	}
	public TaskType getType() {
		return type;
	}
	public List<Descriptor> getInputList() {
		return inputList;
	}
	public List<Descriptor> getOutputList() {
		return outputList;
	}
	
	
	
	
	
	
}
