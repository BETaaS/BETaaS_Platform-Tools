/**

Copyright 2014 ATOS SPAIN S.A.

Licensed under the Apache License, Version 2.0 (the License);
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

Authors Contact:
Francisco Javier Nieto. Atos Research and Innovation, Atos SPAIN SA
@email francisco.nieto@atos.net
**/

package eu.betaas.taas.securitymanager.taastrustmanager.taasaggregator;

public class ThingServiceTrust 
{

	private String thingServiceId;	
	private double securityMechanisms;
	private double qoSFulfillment;
	private double dependability;
	private double scalability;
	private double batteryLoad;
	private double dataStability;
	private double thingServiceTrust;
	
	public ThingServiceTrust (String id, double secMech, double qos, double dependability, double scalability, double battery, double data, double trust)
	{
		thingServiceId = id;	
		securityMechanisms = secMech;
		qoSFulfillment = qos;
		this.dependability = dependability;
		this.scalability = scalability;
		batteryLoad = battery;
		dataStability = data;
		thingServiceTrust = trust;
	}
	
	public String getThingSerivceId ()
	{
		return thingServiceId;
	}
	
	public double getSecurityMechanisms ()
	{
		return securityMechanisms;
	}
	
	public double getQoSFulfillment ()
	{
		return qoSFulfillment;
	}
	
	public double getDependability ()
	{
		return dependability;
	}
	
	public double getScalability ()
	{
		return scalability;
	}
	
	public double getBatteryLoad ()
	{
		return batteryLoad;
	}
	
	public double getDataStability ()
	{
		return dataStability;
	}
	
	public double getThingServiceTrust ()
	{
		return thingServiceTrust;
	}
}
