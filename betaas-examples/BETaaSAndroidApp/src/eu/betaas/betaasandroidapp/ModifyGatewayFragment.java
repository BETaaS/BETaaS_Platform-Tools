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

import eu.betaas.betaasandroidapp.db.DatabaseManager;
import eu.betaas.betaasandroidapp.gateway.GatewayManager;
import eu.betaas.betaasandroidapp.pojo.Gateway;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

public class ModifyGatewayFragment extends Fragment {

	private Button addButton, updateButton;
	private EditText name, host, port;
	private LinearLayout parentContainer;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setRetainInstance(true);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View addGatewayView = inflater.inflate(R.layout.add_gateway_layout, container, false);
		
		addFunctionality(addGatewayView);
		
		return addGatewayView;
	}
	
	private void addFunctionality(View view) {
		final Gateway gateway;
		String id = null;
		
		if (getArguments() != null) {
			id = getArguments().getString("id");
		}
		
		parentContainer = (LinearLayout) view.findViewById(R.id.modifyGatewayParent);
		
		name = (EditText) view.findViewById(R.id.addGatewayName);
		host = (EditText) view.findViewById(R.id.addGatewayHost);
		port = (EditText) view.findViewById(R.id.addGatewayPort);
	
		addButton = (Button) view.findViewById(R.id.addGatewayButton);
		updateButton = (Button) view.findViewById(R.id.updateGatewayButton);
		
		if (id != null) {
			gateway = DatabaseManager.getInstance(getActivity()).getGateway(id);
			parentContainer.removeView(addButton);

			name.setText(gateway.getName());
			host.setText(gateway.getUri());
			port.setText(String.valueOf(gateway.getPort()));
			
			updateButton.setEnabled(name.getText().toString().length() > 0 &&
					host.getText().toString().length() > 0 &&
					portIsInteger(port.getText().toString()));
			
			name.setOnKeyListener(new OnKeyListener() {
				
				@Override
				public boolean onKey(View v, int keyCode, KeyEvent event) {
					updateButton.setEnabled(name.getText().toString().length() > 0 &&
							host.getText().toString().length() > 0 &&
							portIsInteger(port.getText().toString()));
					
					return false;
				}
			});
			
			host.setOnKeyListener(new OnKeyListener() {
				
				@Override
				public boolean onKey(View v, int keyCode, KeyEvent event) {
					updateButton.setEnabled(name.getText().toString().length() > 0 &&
							host.getText().toString().length() > 0 &&
							portIsInteger(port.getText().toString()));
					
					return false;
				}
			});

			port.setOnKeyListener(new OnKeyListener() {
				
				@Override
				public boolean onKey(View v, int keyCode, KeyEvent event) {
					updateButton.setEnabled(name.getText().toString().length() > 0 &&
							host.getText().toString().length() > 0 &&
							portIsInteger(port.getText().toString()));
					
					return false;
				}
			});
			
			updateButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					gateway.setName(name.getText().toString());
					gateway.setUri(host.getText().toString());
					gateway.setPort(Integer.parseInt(port.getText().toString()));
					
					GatewayManager gwManager =
							GatewayManager.getInstance(getActivity().getApplicationContext());
					gwManager.updateGateway(gateway);
				}
			});
		} else {
			parentContainer.removeView(updateButton);

			addButton.setEnabled(name.getText().toString().length() > 0 &&
					host.getText().toString().length() > 0 &&
					portIsInteger(port.getText().toString()));
			
			name.setOnKeyListener(new OnKeyListener() {
				
				@Override
				public boolean onKey(View v, int keyCode, KeyEvent event) {
					addButton.setEnabled(name.getText().toString().length() > 0 &&
							host.getText().toString().length() > 0 &&
							portIsInteger(port.getText().toString()));
					
					return false;
				}
			});
			
			host.setOnKeyListener(new OnKeyListener() {
				
				@Override
				public boolean onKey(View v, int keyCode, KeyEvent event) {
					addButton.setEnabled(name.getText().toString().length() > 0 &&
							host.getText().toString().length() > 0 &&
							portIsInteger(port.getText().toString()));
					
					return false;
				}
			});

			port.setOnKeyListener(new OnKeyListener() {
				
				@Override
				public boolean onKey(View v, int keyCode, KeyEvent event) {
					addButton.setEnabled(name.getText().toString().length() > 0 &&
							host.getText().toString().length() > 0 &&
							portIsInteger(port.getText().toString()));
					
					return false;
				}
			});
			
			addButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					GatewayManager gwManager =
							GatewayManager.getInstance(getActivity().getApplicationContext());
					gwManager.registerGateway(name.getText().toString(),
							host.getText().toString(),
							Integer.parseInt(port.getText().toString()));
				}
			});
		}
	}
	
	private boolean portIsInteger(String port) {
		if (port == null || port.length() == 0) {
			return false;
		}
		
		try { 
	        Integer.parseInt(port); 
	    } catch(NumberFormatException e) { 
	        return false; 
	    }
	    return true;
	}
}
