/**

Copyright 2015 ATOS SPAIN S.A.

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

package eu.betaas.service.securitymanager.trustmanager.serviceproxy;

import java.sql.Timestamp;
import java.util.ArrayList;

public class GatewayInfo 
{
	private Timestamp meetingTime;
	private String gatewayId;
	private int totalInteractions;
	private int knownSuccess;
	private int knownFailures;
	private ArrayList<GatewayTrustData> dataList;
			
	public GatewayInfo (String gwIdentifier)
	{
		gatewayId = gwIdentifier;		
	}
		
	public String getGatewayId()
	{
		return gatewayId;
	}
	
	public Timestamp getMeetingTime()
	{
		return meetingTime;
	}
	
	public int getTotalInteractions()
	{
		return totalInteractions;
	}
		
	public int getKnownSuccess()
	{
		return knownSuccess;
	}
	
	public int getKnownFailures()
	{
		return knownFailures;
	}
	
	public ArrayList<GatewayTrustData> getDataList()
	{
		return dataList;
	}
}
