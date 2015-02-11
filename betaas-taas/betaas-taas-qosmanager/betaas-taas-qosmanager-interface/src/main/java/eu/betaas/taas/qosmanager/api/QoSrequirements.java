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

import java.io.Serializable;

public class QoSrequirements implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private double maxResponseTime;
	
	private double minInterRequestTime;
	
	private double maxBurstSize;
	
	private double averageRate;

	public QoSrequirements(){
		setMaxResponseTime(0.0);
		setMinInterRequestTime(0.0);
	}
	
	public QoSrequirements(QoSrequirements req) {
		setMaxResponseTime(req.getMaxResponseTime());
		setMinInterRequestTime(req.getMinInterRequestTime());
		setMaxBurstSize(req.getMaxBurstSize());
		setAverageRate(req.getAverageRate());
	}
	
	public double getMaxResponseTime() {
		return maxResponseTime;
	}

	public void setMaxResponseTime(double maxResponseTime) {
		this.maxResponseTime = maxResponseTime;
	}

	public double getMinInterRequestTime() {
		return minInterRequestTime;
	}
	
	public void setMinInterRequestTime(double minInterRequestTime) {
		this.minInterRequestTime = minInterRequestTime;
	}
	
	@Override
	public String toString(){
		String msg = new String();
		msg = "MaxResponseTime: " + String.valueOf(getMaxResponseTime()) + 
				"\nMinInterRequestTime: " + String.valueOf(getMinInterRequestTime()) + "\n";
		return msg;
	}

	public double getAverageRate() {
		return averageRate;
	}

	public void setAverageRate(double averageRate) {
		this.averageRate = averageRate;
	}

	public double getMaxBurstSize() {
		return maxBurstSize;
	}

	public void setMaxBurstSize(double maxBurstSize) {
		this.maxBurstSize = maxBurstSize;
	}
}
