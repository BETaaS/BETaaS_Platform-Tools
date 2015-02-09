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

package eu.betaas.taas.taasresourcesmanager.resourcesoptimizer;

import org.apache.log4j.Logger;

import eu.betaas.taas.taasresourcesmanager.taasrmclient.TaaSVMMClient;
import eu.betaas.taas.taasvmmanager.cloudsclients.VMRequest;

public class VMResourcesAllocator 
{
	private TaaSVMMClient vmmClient;
	private Logger logger= Logger.getLogger("betaas.taas");
	
	public VMResourcesAllocator ()
	{
		vmmClient = TaaSVMMClient.instance();
	}
	
	public String createCompNode(int authLevel)
	{
		logger.info("Requesting a new VM of type " + authLevel);
		VMRequest flavorRequest = new VMRequest();
		flavorRequest.setCores(2);
		flavorRequest.setInstances(1);
		flavorRequest.setMemory(256);
		flavorRequest.setSpeed(1200);
		flavorRequest.setImage("BIGDATA");
		String vmId = vmmClient.createLocalVM(flavorRequest);
		return vmId;
	}
}
