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

package eu.betaas.betaasandroidapp.listeners;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import eu.betaas.betaasandroidapp.db.DatabaseManager;
import eu.betaas.betaasandroidapp.pojo.Gateway;

public abstract class DBGatewayListener extends Fragment {

	private DatabaseManager dbManager;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		dbManager = DatabaseManager.getInstance(getActivity());
		dbManager.registerGatewayListener(this);
	}
	
	@Override
	public void onDestroy() {
		dbManager.unRegisterGatewayListener(this);
		super.onDestroy();
	}
	
	public abstract void onGatewayAdded(Gateway gateway);
	public abstract void onGatewayUpdated(Gateway gateway);
	public abstract void onGatewayDeleted(Gateway gateway);
}
