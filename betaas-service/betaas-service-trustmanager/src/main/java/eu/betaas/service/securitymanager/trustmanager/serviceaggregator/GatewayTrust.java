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

package eu.betaas.service.securitymanager.trustmanager.serviceaggregator;

public class GatewayTrust 
{

	private String gatewayId;
	private double interactionHistory;
	private double dependability;
	private double path;
	private double energy;
	private double reputation;
	private double gatewayTrust;
	
	public GatewayTrust (String id, double history, double dependability, double pathTrust, double battery, double gwReputation, double trust)
	{
		gatewayId = id;	
		interactionHistory = history;		
		this.dependability = dependability;
		path = pathTrust;
		energy = battery;
		reputation = gwReputation;
		gatewayTrust = trust;
	}
	
	public String getGatewayId ()
	{
		return gatewayId;
	}
	
	public double getInteractionHistory ()
	{
		return interactionHistory;
	}
	
	public double getDependability ()
	{
		return dependability;
	}
	
	public double getPath ()
	{
		return path;
	}
	
	public double getEnergy ()
	{
		return energy;
	}
	
	public double getReputation ()
	{
		return reputation;
	}
	
	public double getGatewayTrust ()
	{
		return gatewayTrust;
	}
}
