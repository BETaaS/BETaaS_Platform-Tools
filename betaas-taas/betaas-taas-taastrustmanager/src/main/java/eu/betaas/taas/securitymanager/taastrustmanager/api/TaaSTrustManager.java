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

package eu.betaas.taas.securitymanager.taastrustmanager.api;

import java.util.ArrayList;

public interface TaaSTrustManager 
{
	public double getTrust (String idThingsService);
	
	public ArrayList<Double> getTrust (ArrayList<String> thingServicesList);
	
	public boolean registerThingsService (String idThingsService);
	
	public boolean removeThingsService (String idThingsService);
	
	public boolean subscribeThreshold (String idThingsService, double threshold);
	
	public boolean removeThreshold (String idThingsService);
}
