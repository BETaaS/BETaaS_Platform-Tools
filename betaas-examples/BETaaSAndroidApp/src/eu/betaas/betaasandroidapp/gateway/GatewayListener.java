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
Sergio García Villalonga. Atos Research and Innovation, Atos SPAIN SA
@email sergio.garciavillalonga@atos.net
**/

package eu.betaas.betaasandroidapp.gateway;

import eu.betaas.betaasandroidapp.pojo.Gateway;
import eu.betaas.betaasandroidapp.pojo.Measurement;

public interface GatewayListener {
	public void onGatewayInstallSuccess(Gateway gateway);
	public void onGatewayInstallFailure(Gateway gateway, String cause);
	
	public void onGatewayUpdateSuccess(Gateway gateway);
	public void onGatewayUpdateFailure(Gateway gateway, String cause);
	
	public void onGatewayRemoveSuccess(Gateway gateway);
	public void onGatewayRemoveFailure(Gateway gateway, String cause);
	
	public void onApplicationInstallSuccess(Gateway gateway);
	public void onApplicationInstallFailure(String  cause);
	
	public void onApplicationUninstallSuccess();
	public void onApplicationUninstallFailure();
	
	public void onServiceSubscribeSuccess(String serviceId);
	public void onServiceSubscribeFailure(String serviceId, String cause);
	
	public void onServiceUnSubscribeSuccess();
	public void onServiceUnSubscribeFailure();
	
	public void onDataUpdate(Measurement measurement);
}
