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

package eu.betaas.betaasandroidapp;

import java.text.DateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import eu.betaas.betaasandroidapp.gateway.GatewayListener;
import eu.betaas.betaasandroidapp.gateway.GatewayManager;
import eu.betaas.betaasandroidapp.gateway.GatewayManager.CommunicationState;
import eu.betaas.betaasandroidapp.pojo.Gateway;
import eu.betaas.betaasandroidapp.pojo.Measurement;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class GatewayFragment extends Fragment implements GatewayListener {

	private static final long UPDATEINTERVAL = 2000;

	private class GatewayUpdater extends TimerTask{
		public GatewayUpdater () {}
		
		@Override
		public void run() {
			gwManager.getPresence(gateway.getId(),
					gateway.getServices().get(0));
		}
	}
	
	private Gateway gateway;
	private String gatewayId;
	private GatewayManager gwManager;
	private GatewayUpdater gatewayUpdater;
	private TextView connectingTo, connectingToGatewayName, errorMessage,
	                  lastUpdateValue, measurementValue1, appIdValue,
	                  serviceIdValue;
	private LinearLayout connectingPanel, errorPanel, measurementsPanel,
	                      appIdPanel, serviceIdPanel;
	private View gatewayDataView;
	private Timer timer = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setRetainInstance(true);
		
		gatewayId = getArguments().getString("id");
		
		gwManager = GatewayManager.getInstance(this.getActivity().getApplicationContext());
		gwManager.registerForEvents(gatewayId, this);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		gateway = gwManager.getGateway(gatewayId);
		
		gatewayDataView = inflater.inflate(R.layout.gateway_data_layout, container, false);
		
		connectingToGatewayName = (TextView) gatewayDataView.findViewById(R.id.connectingToGatewayName);
		connectingToGatewayName.setText(gateway.getName());
		connectingTo = (TextView) gatewayDataView.findViewById(R.id.connectingTo);
		measurementValue1 = (TextView) gatewayDataView.findViewById(R.id.measurementValue1);
		appIdValue = (TextView) gatewayDataView.findViewById(R.id.appIdValue);
		serviceIdValue = (TextView) gatewayDataView.findViewById(R.id.serviceIdValue);
		lastUpdateValue = (TextView) gatewayDataView.findViewById(R.id.lastUpdateValue);
		errorMessage = (TextView) gatewayDataView.findViewById(R.id.errorMessage);
		
		gatewayUpdater = new GatewayUpdater();
		
		connectingPanel = (LinearLayout) gatewayDataView.findViewById(R.id.connectingPanel);
		errorPanel = (LinearLayout) gatewayDataView.findViewById(R.id.errorPanel);
		measurementsPanel = (LinearLayout) gatewayDataView.findViewById(R.id.measurementsPanel);
		appIdPanel = (LinearLayout) gatewayDataView.findViewById(R.id.appIdPanel);
		serviceIdPanel = (LinearLayout) gatewayDataView.findViewById(R.id.serviceIdPanel);
		
		
		startWorkflow();
		
		return gatewayDataView;
	}
	
	@Override
	public void onPause() {
		/*
		 * Since we do not want our battery to be drained, we
		 * stop the data pulling when the fragment is not visible.
		 * If we want to be constantly updating the state for 
		 * receive a notification when someone is at home, i should
		 * be done in a Service, instead within the fragment.
		 */
		errorPanel.setVisibility(View.INVISIBLE);
		if (timer != null) {
			timer.cancel();
			timer.purge();
			timer = null;
		}
		
		super.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
		startWorkflow();
	}

	@Override
	public void onDestroy() {
		gwManager.unRegisterForEvents(gatewayId, this);
		
		if (timer != null) {
			gatewayUpdater.cancel();
			timer.cancel();
			timer.purge();
			timer = null;
		}
		
		super.onDestroy();
	}
	
	public void startWorkflow() {
		CommunicationState state = gwManager.installApplication(gatewayId);
		
		if (state != CommunicationState.INSTALLING) {
			appIdPanel.setVisibility(View.VISIBLE);
			appIdValue.setText(gateway.getAppId());
			
			connectingTo.setText(getResources().getString(R.string.subscribing_service, gateway.getName()));
			state = gwManager.subscribe(gateway.getId(),
					gateway.getServices().get(0));
			
			if (state != CommunicationState.REGISTERING && timer == null) {
				connectingPanel.setVisibility(View.GONE);
				errorPanel.setVisibility(View.GONE);
				measurementsPanel.setVisibility(View.VISIBLE);
				serviceIdPanel.setVisibility(View.VISIBLE);
				
				serviceIdValue.setText(gateway.getServices().get(0));
				
				timer = new Timer();
				timer.schedule(gatewayUpdater, 0, UPDATEINTERVAL);
			}
		}
	}

	@Override
	public void onGatewayInstallSuccess(Gateway gateway) {}

	@Override
	public void onGatewayInstallFailure(Gateway gateway, String cause) {}

	@Override
	public void onGatewayUpdateSuccess(Gateway gateway) {}

	@Override
	public void onGatewayUpdateFailure(Gateway gateway, String cause) {}

	@Override
	public void onGatewayRemoveSuccess(Gateway gateway) {
		if (timer != null) {
			timer.cancel();
			timer.purge();
			timer = null;
		}
	}

	@Override
	public void onGatewayRemoveFailure(Gateway gateway, String cause) {}

	@Override
	public void onApplicationInstallSuccess(Gateway gateway) {
		String appId = gateway.getAppId();
		
		appIdPanel.setVisibility(View.VISIBLE);
		appIdValue.setText(appId);
		
		connectingTo.setText(getResources().getString(R.string.subscribing_service, gateway.getName()));
		
		/*
		 * Since it is only a simple demo, we will only subscribe
		 * to one service. More than one service can be retrieved
		 * dependin on what you requested in the manifest. Thus,
		 * you could display a list of services and let the user
		 * choose which one he want to subscribe to, you'd only
		 * need to add/modify some fragments, but the Gateway
		 * manager is able to handle that. 
		 */
		gwManager.subscribe(gateway.getId(), gateway.getServices().get(0));
	}

	@Override
	public void onApplicationInstallFailure(String cause) {
		connectingPanel.setVisibility(View.GONE);
		errorMessage.setText(cause);
		errorPanel.setVisibility(View.VISIBLE);
	}

	@Override
	public void onApplicationUninstallSuccess() {
		if (timer != null) {
			timer.cancel();
			timer.purge();
			timer = null;
		}
	}

	@Override
	public void onApplicationUninstallFailure() {}

	@Override
	public void onServiceSubscribeSuccess(String serviceId) {
		connectingPanel.setVisibility(View.GONE);
		errorPanel.setVisibility(View.GONE);
		measurementsPanel.setVisibility(View.VISIBLE);
		serviceIdPanel.setVisibility(View.VISIBLE);
		
		serviceIdValue.setText(serviceId);
		
		timer = new Timer();
		timer.schedule(gatewayUpdater, 0, UPDATEINTERVAL);
	}

	@Override
	public void onServiceSubscribeFailure(String serviceId, String cause) {
		connectingPanel.setVisibility(View.GONE);
		errorMessage.setText(cause);
		errorPanel.setVisibility(View.VISIBLE);
	}

	@Override
	public void onServiceUnSubscribeSuccess() {
		if (timer != null) {
			timer.cancel();
			timer.purge();
			timer = null;
		}
	}

	@Override
	public void onServiceUnSubscribeFailure() {}

	@Override
	public void onDataUpdate(Measurement measurement) {
		Date date = new Date(measurement.getTime()); 
		DateFormat dateFormat = android.text.format.DateFormat.getTimeFormat(this.getActivity().getApplicationContext());
		
		lastUpdateValue.setText(dateFormat.format(date));
		if (measurement.getMeasurement()) {
    		measurementValue1.setText(getResources().getString(R.string.caution));
    		measurementValue1.setTextColor(Color.RED);
    	} else {
    		measurementValue1.setText(getResources().getString(R.string.relax));
    		measurementValue1.setTextColor(Color.GREEN);
    	}
	}
}
