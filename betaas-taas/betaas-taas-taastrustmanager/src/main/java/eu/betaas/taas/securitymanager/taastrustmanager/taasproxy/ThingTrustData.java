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

package eu.betaas.taas.securitymanager.taastrustmanager.taasproxy;

import java.sql.Timestamp;

public class ThingTrustData 
{
	private Timestamp timestamp;
	private String measurement;
	private String batteryLevel;
	private String memoryLevel;
	
	public ThingTrustData (Timestamp dataTime, String value, String battery, String memory)
	{
		timestamp = dataTime;
		measurement = value;
		batteryLevel = battery;
		memoryLevel = memory;		
	}
	
	public Timestamp getTimestamp()
	{
		return timestamp;
	}
	
	public String getMeasurement()
	{
		return measurement;
	}
	
	public String getBatteryLevel()
	{
		return batteryLevel;
	}
	
	public String getMemoryLevel()
	{
		return memoryLevel;
	}
	
}
