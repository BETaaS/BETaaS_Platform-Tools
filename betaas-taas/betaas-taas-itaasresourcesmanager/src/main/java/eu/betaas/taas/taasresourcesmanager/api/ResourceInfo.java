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
Sergio García Villalonga. Atos Research and Innovation, Atos SPAIN SA
@email sergio.garciavillalonga@atos.net
**/

package eu.betaas.taas.taasresourcesmanager.api;

public class ResourceInfo 
{
	private int resourceType;
	private String resourceId;
	private String physicalResourceId;
	private int status;
	
	public ResourceInfo() {
	}
	
	public ResourceInfo (String id, String physicalId, int type, int theStatus)
	{
		resourceId = id;
		physicalResourceId = physicalId;
		resourceType = type;
		status = theStatus;			
	}
	
	public String getResourceId ()
	{
		return resourceId;
	}

	public void setResourceId(String rs) {
	  resourceId = rs;
	}

	
	public String getPhysicalResourceId ()
	{
		return physicalResourceId;
	}
	
	public void setPhysicalResourceId(String pri) {
	  physicalResourceId = pri;
	}
	
	public int getResourceType()
	{
		return resourceType;
	}
	
	public void setResourceType(int rt) {
	  resourceType = rt;
	}
	
	public int getStatus()
	{
		return status;
	}
	
	public void setStatus(int s) {
	  status = s;
	}
	
}
