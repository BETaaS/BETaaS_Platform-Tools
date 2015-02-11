/*
 *
Copyright 2014-2015 Department of Information Engineering, University of Pisa

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
// Component: TaaS QoS Manager
// Responsible: Carlo Vallati & Giacomo Tanganelli

package eu.betaas.taas.qosmanager.api;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.betaas.taas.bigdatamanager.database.hibernate.data.QoSMAssignmentStar;
import eu.betaas.taas.bigdatamanager.database.hibernate.data.QoSMAssuredRequestStar;
import eu.betaas.taas.bigdatamanager.database.hibernate.data.QoSMEquivalentThingServiceStar;
import eu.betaas.taas.bigdatamanager.database.hibernate.data.QoSMRequestStar;
import eu.betaas.taas.bigdatamanager.database.hibernate.data.QoSMThingServiceStar;
import eu.betaas.taas.bigdatamanager.database.hibernate.data.QoSMThingStar;

public class QoSRankList {
	private boolean feas;
	private ArrayList<ArrayList<String>> assignments;
	private double hyperperiod;
	
	private Map<String, QoSMEquivalentThingServiceStar> equivalentsMap;
	private Map<String, QoSMAssignmentStar> assignmentsMap;
	private Map<String, QoSMRequestStar> requestsMap;
	private Map<String, QoSMAssuredRequestStar> assuredRequestsMap;
	private Map<String, QoSMThingServiceStar> thingServicesMap;
	private Map<String, QoSMThingStar> thingsMap;
	

	
	private Map<String, QoSRankResults> notifyMap;
	
	private Map<String, QoSManagerNotificationIF> mapGw;
	
	public QoSRankList(boolean feas, 
			ArrayList<ArrayList<String>> assignments){
		this.feas = feas;
		this.hyperperiod = 0.0;
		this.setAssignments(assignments);
		this.setAssignmentsMap(new HashMap<String, QoSMAssignmentStar>());
		this.setEquivalentsMap(new HashMap<String, QoSMEquivalentThingServiceStar>());
		this.setRequestsMap(new HashMap<String, QoSMRequestStar>());
		this.setAssuredRequestsMap(new HashMap<String, QoSMAssuredRequestStar>());
		this.setThingServicesMap(new HashMap<String, QoSMThingServiceStar>());
		this.setThingsMap(new HashMap<String, QoSMThingStar>());
		this.setNotifyMap(new HashMap<String, QoSRankResults>());
		this.setMapGw(new HashMap<String, QoSManagerNotificationIF>());
	}
	public QoSRankList(boolean feas){
		this.feas = feas;
		this.hyperperiod = 0.0;
		this.setAssignments(new ArrayList<ArrayList<String>>());
		this.setAssignmentsMap(new HashMap<String, QoSMAssignmentStar>());
		this.setEquivalentsMap(new HashMap<String, QoSMEquivalentThingServiceStar>());
		this.setRequestsMap(new HashMap<String, QoSMRequestStar>());
		this.setAssuredRequestsMap(new HashMap<String, QoSMAssuredRequestStar>());
		this.setThingServicesMap(new HashMap<String, QoSMThingServiceStar>());
		this.setThingsMap(new HashMap<String, QoSMThingStar>());
		this.setNotifyMap(new HashMap<String, QoSRankResults>());
		this.setMapGw(new HashMap<String, QoSManagerNotificationIF>());
	}
	public QoSRankList(){
		this.feas = false;
		this.hyperperiod = 0.0;
		this.setAssignments(new ArrayList<ArrayList<String>>());
		this.setAssignmentsMap(new HashMap<String, QoSMAssignmentStar>());
		this.setEquivalentsMap(new HashMap<String, QoSMEquivalentThingServiceStar>());
		this.setRequestsMap(new HashMap<String, QoSMRequestStar>());
		this.setAssuredRequestsMap(new HashMap<String, QoSMAssuredRequestStar>());
		this.setThingServicesMap(new HashMap<String, QoSMThingServiceStar>());
		this.setThingsMap(new HashMap<String, QoSMThingStar>());
		this.setNotifyMap(new HashMap<String, QoSRankResults>());
		this.setMapGw(new HashMap<String, QoSManagerNotificationIF>());
	}

	public boolean isFeas() {
		return feas;
	}
	public void setFeas(boolean feas) {
		this.feas = feas;
	}
	public ArrayList<ArrayList<String>> getAssignments() {
		return assignments;
	}
	public void setAssignments(ArrayList<ArrayList<String>> assignments) {
		this.assignments = assignments;
	}
	public double getHyperperiod() {
		return hyperperiod;
	}
	public void setHyperperiod(double hyperperiod) {
		this.hyperperiod = hyperperiod;
	}
	public void setNotifyMap(Map<String, QoSRankResults> notifyMap) {
		this.notifyMap = notifyMap;
	}
	public Map<String, QoSRankResults> getNotifyMap() {
		return notifyMap;
	}
	public void setMapGw(Map<String, QoSManagerNotificationIF> mapGw) {
		this.mapGw = mapGw;
	}
	public Map<String, QoSManagerNotificationIF> getMapGw() {
		return mapGw;
	}
	public Map<String, QoSMEquivalentThingServiceStar> getEquivalentsMap() {
		return equivalentsMap;
	}
	public void setEquivalentsMap(Map<String, QoSMEquivalentThingServiceStar> equivalentsMap) {
		this.equivalentsMap = equivalentsMap;
	}
	public Map<String, QoSMAssignmentStar> getAssignmentsMap() {
		return assignmentsMap;
	}
	public void setAssignmentsMap(Map<String, QoSMAssignmentStar> assignmentsMap) {
		this.assignmentsMap = assignmentsMap;
	}
	public Map<String, QoSMRequestStar> getRequestsMap() {
		return requestsMap;
	}
	public void setRequestsMap(Map<String, QoSMRequestStar> requestsMap) {
		this.requestsMap = requestsMap;
	}
	public Map<String, QoSMThingServiceStar> getThingServicesMap() {
		return thingServicesMap;
	}
	public void setThingServicesMap(Map<String, QoSMThingServiceStar> thingServicesMap) {
		this.thingServicesMap = thingServicesMap;
	}
	public Map<String, QoSMThingStar> getThingsMap() {
		return thingsMap;
	}
	public void setThingsMap(Map<String, QoSMThingStar> thingsMap) {
		this.thingsMap = thingsMap;
	}
	public Map<String, QoSMAssuredRequestStar> getAssuredRequestsMap() {
		return assuredRequestsMap;
	}
	public void setAssuredRequestsMap(Map<String, QoSMAssuredRequestStar> assuredRequestsMap) {
		this.assuredRequestsMap = assuredRequestsMap;
	}
}
