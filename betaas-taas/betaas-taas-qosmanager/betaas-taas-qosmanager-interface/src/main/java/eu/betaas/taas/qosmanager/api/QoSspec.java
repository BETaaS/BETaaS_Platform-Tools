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

/**
 * The Class QoSspec which specify the QoS characteristics of each Thing Service.
 */
public class QoSspec implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** The Response time. */
	private double ResponseTime;
	
	/** The Battery cost. */
	private double BatteryCost;
	
	private double ComputationalCost;
	

	public QoSspec(QoSspec qosspec) {
		ResponseTime = qosspec.ResponseTime;
		BatteryCost = qosspec.BatteryCost;
		ComputationalCost = qosspec.ComputationalCost;
	}

	public QoSspec() {
		ResponseTime = 0.0;
		BatteryCost = 0.0;
		ComputationalCost = 0.0;
	}

	public double getResponseTime() {
		return ResponseTime;
	}

	public void setResponseTime(double responseTime) {
		ResponseTime = responseTime;
	}

	public double getBatteryCost() {
		return BatteryCost;
	}

	public void setBatteryCost(double batteryCost) {
		BatteryCost = batteryCost;
	}

	public double getComputationalCost() {
		return ComputationalCost;
	}

	public void setComputationalCost(double computationalCost) {
		ComputationalCost = computationalCost;
	}
	
	
}
