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


import java.util.Set;
import eu.betaas.taas.bigdatamanager.database.hibernate.data.QoSMAssignmentInternal;
import eu.betaas.taas.bigdatamanager.database.hibernate.data.QoSMEquivalentThingServiceInternal;
import eu.betaas.taas.bigdatamanager.database.hibernate.data.QoSMThingInternal;

public class QoSRankResults {
	private boolean feas;
	private Set<QoSMAssignmentInternal> assignments;
	private Set<QoSMThingInternal> things;
	private Set<QoSMEquivalentThingServiceInternal> equivalents;
	
	public QoSRankResults(boolean feas, 
			Set<QoSMAssignmentInternal> list, Set<QoSMThingInternal> list2, 
			Set<QoSMEquivalentThingServiceInternal> list3){
		this.feas = feas;
		this.setAssignments(list);
		this.setThings(list2);
		this.setEquivalents(list3);
	}
	public QoSRankResults(boolean feas){
		this.feas = feas;
		this.setAssignments(null);
		this.setThings(null);
		this.setEquivalents(null);
	}
	public QoSRankResults(){
		this.feas = false;
		this.setAssignments(null);
		this.setThings(null);
		this.setEquivalents(null);
	}

	public boolean isFeas() {
		return feas;
	}
	public void setFeas(boolean feas) {
		this.feas = feas;
	}
	public Set<QoSMAssignmentInternal> getAssignments() {
		return assignments;
	}
	public void setAssignments(Set<QoSMAssignmentInternal> assignments) {
		this.assignments = assignments;
	}
	public Set<QoSMThingInternal> getThings() {
		return things;
	}
	public void setThings(Set<QoSMThingInternal> things) {
		this.things = things;
	}
	public Set<QoSMEquivalentThingServiceInternal> getEquivalents() {
		return equivalents;
	}
	public void setEquivalents(Set<QoSMEquivalentThingServiceInternal> equivalents) {
		this.equivalents = equivalents;
	}
}
