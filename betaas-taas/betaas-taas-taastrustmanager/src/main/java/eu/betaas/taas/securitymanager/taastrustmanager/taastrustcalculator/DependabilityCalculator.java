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

package eu.betaas.taas.securitymanager.taastrustmanager.taastrustcalculator;

import org.apache.log4j.Logger;

public class DependabilityCalculator 
{

	private Logger logger= Logger.getLogger("betaas.taas");
	
	public float calculateTrustAspect (String thingServiceId)
	{
		// 1 - Retrieve required data
		logger.debug("Calculating dependability...");
		// 2 - Calculate MTBF
		
		// 3 - Perform the Z-test
		
		
		return 2.5f;
	}
}
