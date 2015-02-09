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

package eu.betaas.taas.qosmanager.heuristic;

import java.util.ArrayList;
import java.util.List;


import eu.betaas.taas.bigdatamanager.database.hibernate.data.QoSMAssignmentStar;

import eu.betaas.taas.bigdatamanager.database.hibernate.data.QoSMThingStar;

/**
 * The Class ReservationResults.
 */
public class ReservationResults {
	
	private boolean feasible;
	
	/** The battery level. */
	private List<QoSMThingStar> b;
	
	/** The y. */
	protected List<QoSMAssignmentStar> y;
	
	/** Which heuristic has been chosen. */
	protected int which;

	
	/**
	 * Instantiates a new reservation results.
	 *
	 * @param n the number of things
	 * @param k the number of request
	 */
	public ReservationResults() {
		setB(new ArrayList<QoSMThingStar>());
		y = new ArrayList<QoSMAssignmentStar>();
		which = -1;
		setFeasible(false);
	}
	

	/**
	 * Gets the request associated to a thingservice.
	 *
	 * @param id the thingServiceId
	 * @return the list of requests
	 */
	/*public List<String> getJobsofThing(String id){
		List<String> jobs=new ArrayList<String>();
		for(QoSMAssignmentStar a : y)
		{
			if(a.getEquivalentThingService().getThingService().getThingServiceId().equals(id))
				jobs.add(a.getEquivalentThingService().getRequest().getServiceId());
		}
		return jobs;
	}*/
	
	/**
	 * Gets the thing associated to a request.
	 *
	 * @param id the requestId
	 * @return the thingServiceId
	 */
	/*public String getThingofJob(String id){
		for(QoSMAssignment a : y)
		{
			if(a.getEquivalentThingService().getRequest().getServiceId().equals(id))
				return a.getEquivalentThingService().getThingService().getThingServiceId();
		}
		return "";
	}
	*/
	/**
	 * Gets the remaining battery of a thing.
	 *
	 * @param id the deviceId
	 * @return the remaining battery
	 */
	public double getBatteryofThing(String id){
		for(QoSMThingStar t : getB())
		{
			if(t.getDeviceId().equals(id))
				return t.getBatteryLevel();
		}
		return -1;
	}
	
	public double getCapacityofThing(String id){
		for(QoSMThingStar t : getB())
		{
			if(t.getDeviceId().equals(id))
				return t.getCapacityUsed();
		}
		return -1;
	}
	
	public int getNumAssofThing(String id){
		for(QoSMThingStar t : getB())
		{
			if(t.getDeviceId().equals(id))
				return t.getNumass();
		}
		return -1;
	}
	
	public boolean isFeasible() {
		return feasible;
	}

	public void setFeasible(boolean feasible) {
		this.feasible = feasible;
	}


	public List<QoSMThingStar> getB() {
		return b;
	}


	public void setB(ArrayList<QoSMThingStar> b2) {
		this.b = b2;
	}
	
	public List<QoSMAssignmentStar> getAss() {
		return y;
	}


	public void setAss(List<QoSMAssignmentStar> y) {
		this.y = y;
	}
	
	public String toString(){
		String msg = new String();
		msg = "Feasible: " + String.valueOf(feasible);
		msg += "\nWhich: " + String.valueOf(which);
		msg += "\nThings: ";
		for(QoSMThingStar t : b)
		{
			msg += t.toString();
		}
		msg += "\nAssignments: ";
		for(QoSMAssignmentStar a : y)
		{
			msg += a.toString();
		}
		return msg;
	}
}
