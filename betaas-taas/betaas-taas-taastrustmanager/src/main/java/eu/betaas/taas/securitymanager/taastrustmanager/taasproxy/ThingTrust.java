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

import java.util.ArrayList;

public class ThingTrust 
{

	private String units;
	private String thingId;
	private String thingServiceId;
	private ArrayList<ThingTrustData> dataList;
	private ArrayList<String> equivalents;	
	
	public ThingTrust (String tsIdentifier)
	{
		thingServiceId = tsIdentifier;
		dataList = new ArrayList<ThingTrustData>();
		equivalents = new ArrayList<String>();
	}
	
	public void setUnits (String thingUnits)
	{
		units = thingUnits;
	}
	
	public void setThingId (String theThingId)
	{
		thingId = theThingId;
	}
	
	public void setDataList (ArrayList<ThingTrustData> fullList)
	{
		dataList = fullList;
	}
			
	public void addData (ThingTrustData theData)
	{
		dataList.add(theData);
	}
	
	public void setEquivalents (ArrayList<String> fullList)
	{
		equivalents = fullList;
	}
	
	public void addEquivalent (String idThingService)
	{
		equivalents.add(idThingService);
	}
	
	public String getUnits()
	{
		return units;
	}
	
	public String getThingId()
	{
		return thingId;
	}
	
	public String thingServiceId()
	{
		return thingServiceId;
	}
	
	public ArrayList<ThingTrustData> getDataList()
	{
		return dataList;
	}
	
	public ArrayList<String> getEquivalents()
	{
		return equivalents;
	}
	
}
