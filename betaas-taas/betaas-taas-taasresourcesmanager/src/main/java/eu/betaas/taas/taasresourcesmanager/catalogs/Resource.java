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

Authors :
Francisco Javier Nieto. Atos Research and Innovation, Atos SPAIN SA
@email francisco.nieto@atos.net 

**/

package eu.betaas.taas.taasresourcesmanager.catalogs;

import java.util.ArrayList;

public class Resource 
{
	public static final int THINGSERVICE = 0;
	public static final int VIRTUALRESOURCE = 1;
	
	public static final int UNAVAILABLE = 0;
	public static final int ALLOCATED = 1;
	public static final int FREE = 2;
	
	private int resourceType;
	private String resourceId;
	private String physicalResourceId;
	private int status;
	private String gatewayId;
	private ArrayList<String> allocatedFeatures;
	
	public Resource (String id, String physicalId, int type, String gateway)
	{
		resourceId = id;
		physicalResourceId = physicalId;
		resourceType = type;
		status = 2;	//We'll consider that the initial status is Available and Free
		gatewayId = gateway;
		allocatedFeatures = new ArrayList<String>();
	}
	
	public String getResourceId ()
	{
		return resourceId;
	}
	
	public String getPhysicalResourceId ()
	{
		return physicalResourceId;
	}
	
	public int getResourceType()
	{
		return resourceType;
	}
	
	public int getStatus()
	{
		return status;
	}
	
	public String getGatewayId ()
	{
		return gatewayId;
	}
	
	public ArrayList<String> getAllocatedFeatures()
	{
		return allocatedFeatures;
	}
	
	public void setStatus (int newStatus)
	{
		status = newStatus;
	}
	
	public void addFeature (String idFeature)
	{
		allocatedFeatures.add(idFeature);
	}
	
	public void removeFeature (String idFeature)
	{
		allocatedFeatures.remove(idFeature);
	}
	
}
