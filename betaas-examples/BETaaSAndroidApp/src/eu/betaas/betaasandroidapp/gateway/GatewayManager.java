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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import eu.betaas.betaasandroidapp.communicator.GatewayCommunicator;
import eu.betaas.betaasandroidapp.communicator.GatewayCommunicatorException;
import eu.betaas.betaasandroidapp.communicator.GatewayCommunicatorFactory;
import eu.betaas.betaasandroidapp.db.DatabaseManager;
import eu.betaas.betaasandroidapp.pojo.Gateway;
import eu.betaas.betaasandroidapp.pojo.Measurement;

public class GatewayManager {
	public enum CommunicationState {UNINSTALLED, INSTALLING, INSTALLED, UNREGISTERED, REGISTERING, REGISTERED,UPDATING, ERROR};
	
	private static Context context = null;
	private static GatewayManager instance = null;
	private static ArrayList<GatewayListener> gatewayGlobalListeners;
	private static HashMap<String, ArrayList<GatewayListener>> gatewayListeners;
	private static HashMap<String, Gateway> gateways = null;
	private static HashMap<String, CommunicationState> applicationState;
	private static HashMap<String, CommunicationState> serviceState;
	private static HashMap<String, List<AsyncTask>> installationTasks = null;
	
	public static synchronized GatewayManager getInstance(Context context) {
		if (instance == null) {
			instance = new GatewayManager(context);
		}
		return instance;
	}
	
	private GatewayManager (Context context) {
		GatewayManager.context = context;
		GatewayManager.gateways =
				new HashMap<String, Gateway>();
		GatewayManager.gatewayGlobalListeners =
				new ArrayList<GatewayListener>();
		GatewayManager.gatewayListeners =
				new HashMap<String, ArrayList<GatewayListener>>();
		GatewayManager.applicationState =
				new HashMap<String, CommunicationState>();
		GatewayManager.serviceState =
				new HashMap<String, CommunicationState>();
		GatewayManager.installationTasks =
				new HashMap<String, List<AsyncTask>>();
		
		DatabaseManager dbm =
			DatabaseManager.getInstance(GatewayManager.context);
		
		for (Gateway gateway : dbm.getGateways()) {
			gateways.put(gateway.getId(), gateway);
			applicationState.put(gateway.getId(), CommunicationState.UNINSTALLED);
			installationTasks.put(gateway.getId(), new ArrayList<AsyncTask>());
		}
	}
	
	public List<Gateway> getGateways() {
		return new ArrayList(gateways.values());
	}
	
	public Gateway getGateway(String gatewayId) {
		return gateways.get(gatewayId);
	}
	
	public void registerForEvents(String gatewayId, GatewayListener listener) {
		ArrayList<GatewayListener> listeners;
		synchronized (gatewayListeners) {
			listeners = gatewayListeners.get(gatewayId);
			if (listeners == null) {
				listeners = new ArrayList<GatewayListener>();
				listeners.add(listener);
				gatewayListeners.put(gatewayId, listeners);
			} else {
				if (!listeners.contains(listener)) {
					listeners.add(listener);
				}
			}
		}
	}
	
	public void unRegisterForEvents(String gatewayId, GatewayListener listener) {
		ArrayList<GatewayListener> listeners;
		synchronized (gatewayListeners) {
			listeners = gatewayListeners.get(gatewayId);
			if (listeners != null) {
				if (listeners.contains(listener)) {
					listeners.remove(listener);
				}
			}
		}
	}
	
	public void registerForAllEvents(GatewayListener listener) {
		synchronized (gatewayGlobalListeners) {
			if (!gatewayGlobalListeners.contains(listener)) {
				gatewayGlobalListeners.add(listener);
			}
		}
	}
	
	public void unRegisterForAllEvents(GatewayListener listener) {
		synchronized (gatewayGlobalListeners) {
			if (gatewayGlobalListeners.contains(listener)) {
				gatewayGlobalListeners.remove(listener);
			}
		}
	}
	
	public void registerGateway (String name, String uri, int port) {
		boolean register = false;
		AsyncTask<Gateway, Void, Object> task;
		
		Gateway gateway = new Gateway(name, uri, port, "",
				new ArrayList<String>(), new ArrayList<String>());
		synchronized (gateways) {
			if (!gateways.keySet().contains(gateway.getId())) {
				gateways.put(gateway.getId(), gateway);
				register = true;
			}
		}
		
		if (register) {
			synchronized (applicationState) {
				applicationState.put(gateway.getId(), CommunicationState.UNINSTALLED);
			}
			
			synchronized (serviceState) {
				for (String service : gateway.getServices()) {
					serviceState.put(gateway.getId()+gateway.getAppId()+service,
							CommunicationState.UNREGISTERED);
				}
			}
			
			task = new AsyncTask<Gateway, Void, Object>() {
				
				@Override
				protected Object doInBackground(Gateway... params) {
					DatabaseManager dbm =
							DatabaseManager.getInstance(context);
						
					dbm.storeGateway(params[0]);
					
					installationTasks.put(params[0].getId(),
					                      new ArrayList<AsyncTask>());
					
					return params[0];
				}
					@Override
				protected void onPostExecute(Object result) {
					notifyGatewayInstalled((Gateway) result);
					super.onPostExecute(result);
				}
			};
			executeTask(task, gateway);
		}
	}
	
	public void updateGateway (Gateway gateway) {
		boolean update = false;
		AsyncTask<Gateway, Void, Object> task;
		synchronized (gateways) {
			if (gateways.keySet().contains(gateway.getId())) {
				gateways.put(gateway.getId(), gateway);
				update = true;
			}
		}
		
		if (update) {
			task = new AsyncTask<Gateway, Void, Object>() {
				
				@Override
				protected Object doInBackground(Gateway... params) {
					DatabaseManager dbm =
							DatabaseManager.getInstance(context);
						
					dbm.updateGateway(params[0]);
						
					return params[0];
				}

				@Override
				protected void onPostExecute(Object result) {
					notifyGatewayUpdated((Gateway)result);
					super.onPostExecute(result);
				}
			};
			executeTask(task, gateway);
		}
	}
	
	public synchronized void deleteGateway (final Gateway gateway) {
		boolean delete = false;
		AsyncTask<Gateway, Void, Object> task;
		synchronized (gateways) {
			if (gateways.keySet().contains(gateway.getId())) {
				gateways.remove(gateway.getId());
				delete = true;
			}
		}
		
		if (delete) {
			synchronized (applicationState) {
				applicationState.remove(gateway.getId());
			}
			
			synchronized (serviceState) {
				for (String service : gateway.getServices()) {
					serviceState.remove(gateway.getId()+gateway.getAppId()+service);
				}
			}
			
			task = new AsyncTask<Gateway, Void, Object>() {
			
				@Override
				protected Object doInBackground(Gateway... params) {
					final Gateway gw = params[0];
					
					GatewayCommunicator gc =
							GatewayCommunicatorFactory.getCommunicator(
									gw.getUri(), gw.getPort());
						
					for (String service : gw.getServices()) {
						try {
							gc.unsubscribe(gw, service);
						} catch (GatewayCommunicatorException e) {}
					}
					
					for (AsyncTask task : installationTasks.get(gw.getId())) {
						task.cancel(true);
					}
					installationTasks.remove(gw.getId());
					
					try {
						gc.uninstallApplication(gw);
					} catch (GatewayCommunicatorException e) {
						return e;
					}
					
					DatabaseManager dbm =
					DatabaseManager.getInstance(context);
					
					dbm.deleteGateway(gw);
					
					return gw;
				}
	
				@Override
				protected void onPostExecute(Object result) {
					if (result instanceof Gateway) {
						notifyGatewayRemoved(CommunicationState.UNINSTALLED,
								(Gateway) result, "");
					} else if (result instanceof GatewayCommunicatorException) {
						GatewayCommunicatorException e =
								(GatewayCommunicatorException) result;
						
						notifyGatewayRemoved(CommunicationState.ERROR,
								e.getGateway(),
								"Delete gateway: " + e.getCode() + " - " + e.getMessage());
					}
					
					super.onPostExecute(result);
				}
			};
			executeTask(task, gateway);
		}
	}
	
	public CommunicationState installApplication(String gatewayId) {
		AsyncTask<Gateway, Void, Object> task;
		Gateway gateway;
		CommunicationState state;
		
		synchronized (applicationState) {
			state = applicationState.get(gatewayId);
			
			if (state == CommunicationState.UNINSTALLED) {
				state = CommunicationState.INSTALLING;
				applicationState.put(gatewayId, state);
			} else {
				return state;
			}
		}
		
		synchronized (gateways) {
			gateway = gateways.get(gatewayId);
		}
		
		task = new AsyncTask<Gateway, Void, Object>() {
		
			@Override
			protected Object doInBackground(Gateway... params) {
				Gateway gw = params[0];
				GatewayCommunicator gc =
					GatewayCommunicatorFactory.getCommunicator(gw.getUri(), gw.getPort());
					
				Gateway ret;
				try {
					ret = gc.installApplication(gw);
					
					synchronized (applicationState) {
						if (applicationState.get(ret.getId()) ==
								CommunicationState.INSTALLING) {
							String ref;
							DatabaseManager dbm =
								DatabaseManager.getInstance(context);
						
							dbm.updateGatewayAppId(ret);
							dbm.updateGatewayServices(ret);
							dbm.updateGatewayTokens(ret);
						
							for (String service : ret.getServices()) {
								ref = ret.getId() + ret.getAppId() + service;
								serviceState.put(ref, CommunicationState.UNREGISTERED);
							}
							
							applicationState.put(ret.getId(),
									CommunicationState.INSTALLED);
						}
					}
					
					return ret;
				} catch (GatewayCommunicatorException e) {
					synchronized (applicationState) {
						if (applicationState.get(e.getGateway().getId()) ==
								CommunicationState.INSTALLING) {
							applicationState.put(e.getGateway().getId(),
									CommunicationState.UNINSTALLED);
						}
					}
					
					return e;
				}
			}
		
			@Override
			protected void onPostExecute(Object result) {
				if (result instanceof Gateway) {
					notifyApplicationInstalled(CommunicationState.INSTALLED,
							(Gateway) result, "");
				} else if (result instanceof GatewayCommunicatorException) {
					GatewayCommunicatorException e =
							(GatewayCommunicatorException) result;
					
					notifyApplicationInstalled(CommunicationState.ERROR,
							e.getGateway(),
							"Install application: " + e.getCode() + " - " + e.getMessage());
				}
				
				super.onPostExecute(result);
			}
		};
		installationTasks.get(gatewayId).add(task);
		executeTask(task, gateway);
		
		return state;
	}
	
	public CommunicationState subscribe(String gatewayId,
	                                     final String serviceId) {
		AsyncTask<Gateway, Void, Object> task;
		String ref;
		Gateway gateway;
		CommunicationState state;
		
		synchronized (gateways) {
			gateway = gateways.get(gatewayId);
			
			if (!gateway.getServices().contains(serviceId)) {
				return CommunicationState.ERROR;
			}
		}
		
		ref = gateway.getId()+gateway.getAppId()+serviceId;
		synchronized (serviceState) {
			state = serviceState.get(ref);
			
			if (state == CommunicationState.UNREGISTERED) {
				state = CommunicationState.REGISTERING;
				serviceState.put(ref, state);
			} else {
				//Could return REGISTERING, REGISTERED, UPDATING
				return state;
			}
		}
		
		task = new AsyncTask<Gateway, Void, Object>() {
			
			@Override
			protected Object doInBackground(Gateway... params) {
				Gateway gw = params[0];
				GatewayCommunicator gc =
					GatewayCommunicatorFactory.getCommunicator(gw.getUri(), gw.getPort());
				
				Gateway ret;
				try {
					ret = gc.subscribe(gw, serviceId);
					
					String ref = ret.getId() + ret.getAppId() + serviceId;
					synchronized (applicationState) {
						if (serviceState.get(ref) ==
								CommunicationState.REGISTERING) {
							serviceState.put(ref, CommunicationState.REGISTERED);
						}
					}
					return ret;
				} catch (GatewayCommunicatorException e) {
					ret = e.getGateway();
					String ref = ret.getId() + ret.getAppId() + serviceId;
					
					synchronized (serviceState) {
						if (serviceState.get(ref) ==
							CommunicationState.REGISTERING) {
						
							serviceState.put(ref, CommunicationState.UNREGISTERED);
						}
					}
					return e;
				}
			}
			
			@Override
			protected void onPostExecute(Object result) {
				if (result instanceof Gateway) {
					notifyServiceSubscribed(CommunicationState.REGISTERED,
							(Gateway) result, serviceId, "");
				} else if (result instanceof GatewayCommunicatorException) {
					GatewayCommunicatorException e =
							(GatewayCommunicatorException) result;
					
					notifyServiceSubscribed(CommunicationState.ERROR,
							e.getGateway(), serviceId,
							"Subscribe: " + e.getCode() + " - " + e.getMessage());
				}
				
				super.onPostExecute(result);
			}
		};
		executeTask(task, gateway);
		
		return state;
	}
	
	public CommunicationState getPresence (String gatewayId,
	                                        final String serviceId) {
		AsyncTask<Gateway, Void, Object> task;
		String ref;
		Gateway gateway;
		CommunicationState state;
		
		synchronized (gateways) {
			gateway = gateways.get(gatewayId);
			
			if (!gateway.getServices().contains(serviceId)) {
				return CommunicationState.ERROR;
			}
		}
		
		ref = gateway.getId()+gateway.getAppId()+serviceId;
		synchronized (serviceState) {
			state = serviceState.get(ref);
			
			if (state == CommunicationState.REGISTERED) {
				state = CommunicationState.UPDATING;
				serviceState.put(ref, state);
			} else {
				//Could return UNREGISTERED, REGINSTERING, UPDATING, ERROR
				return state;
			}
		}
		
		task = new AsyncTask<Gateway, Void, Object>() {

			@Override
			protected Object doInBackground(Gateway... params) {
				Gateway gw = params[0];
				
				GatewayCommunicator gc =
						GatewayCommunicatorFactory.getCommunicator(gw.getUri(), gw.getPort());
				
				Measurement measurement;
				try {
					measurement = gc.getPresence(gw, serviceId);
					
					DatabaseManager dbm =
							DatabaseManager.getInstance(context);
					dbm.storeMeasurement(measurement);
						
					String ref = gw.getId() + gw.getAppId() + serviceId;
					synchronized (serviceState) {
						serviceState.put(ref, CommunicationState.REGISTERED);
					}
				} catch (GatewayCommunicatorException e) {
					return e;
				}
				return measurement;
			}

			@Override
			protected void onPostExecute(Object result) {
				if (result instanceof Measurement) {
					notifyDataUpdate((Measurement) result);
				}
				super.onPostExecute(result);
			}
		};
		executeTask(task, gateway);
		
		return state;
	}
	
	public CommunicationState uninstallApplication () {
		return null;
	}
	
	public CommunicationState unsubscribe() {
		return null;
	}
	
	private ArrayList<GatewayListener> getListeners(String idGateway) {
		ArrayList<GatewayListener> ret = new ArrayList<GatewayListener>();
		ArrayList<GatewayListener> listeners;
		synchronized (gatewayListeners) {
			listeners = gatewayListeners.get(idGateway);
			
			if (listeners != null) {
				ret = (ArrayList<GatewayListener>) listeners.clone();
			}
		}
		return ret;
	}
	
	private void notifyGatewayInstalled(Gateway gateway) {
		ArrayList<GatewayListener> globalListeners;
		ArrayList<GatewayListener> listeners = getListeners(gateway.getId());
		
		synchronized (gatewayGlobalListeners) {
			globalListeners =
					(ArrayList<GatewayListener>) gatewayGlobalListeners.clone();
		}
		
		for (GatewayListener listener : listeners) {
			listener.onGatewayInstallSuccess(gateway);
		}
		
		for (GatewayListener listener : globalListeners) {
			listener.onGatewayInstallSuccess(gateway);
		}
	}
	
	private void notifyGatewayUpdated(Gateway gateway) {
		ArrayList<GatewayListener> globalListeners;
		ArrayList<GatewayListener> listeners = getListeners(gateway.getId());
		
		synchronized (gatewayGlobalListeners) {
			globalListeners =
					(ArrayList<GatewayListener>) gatewayGlobalListeners.clone();
		}
		
		for (GatewayListener listener : listeners) {
			listener.onGatewayUpdateSuccess(gateway);
		}
		
		for (GatewayListener listener : globalListeners) {
			listener.onGatewayUpdateSuccess(gateway);
		}
	}
	
	private void notifyGatewayRemoved(CommunicationState state,
	                                    Gateway gateway,
	                                    String  cause) {
		ArrayList<GatewayListener> globalListeners;
		ArrayList<GatewayListener> listeners = getListeners(gateway.getId());
		
		synchronized (gatewayGlobalListeners) {
			globalListeners =
					(ArrayList<GatewayListener>) gatewayGlobalListeners.clone();
		}
		
		if (state != CommunicationState.ERROR) {
			for (GatewayListener listener : listeners) {
				listener.onGatewayRemoveSuccess(gateway);
			}
			
			for (GatewayListener listener : globalListeners) {
				listener.onGatewayRemoveSuccess(gateway);
			}
		} else {
			for (GatewayListener listener : listeners) {
				listener.onGatewayRemoveFailure(gateway, cause);
			}
			
			for (GatewayListener listener : globalListeners) {
				listener.onGatewayRemoveFailure(gateway, cause);
			}
		}
	}
	
	private void notifyApplicationInstalled(CommunicationState state,
	                                          Gateway gateway,
	                                          String  cause) {
		ArrayList<GatewayListener> listeners = getListeners(gateway.getId());
		
		if (state != CommunicationState.ERROR) {
			for (GatewayListener listener : listeners) {
				listener.onApplicationInstallSuccess(gateway);
			}
		} else {
			for (GatewayListener listener : listeners) {
				listener.onApplicationInstallFailure(cause);
			}
		}
	}
	
	private void notifyApplicationUninstalled(String idGateway) {
		ArrayList<GatewayListener> listeners = getListeners(idGateway);
		
		for (GatewayListener listener : listeners) {
			listener.onApplicationUninstallSuccess();
		}
	}
	
	private void notifyServiceSubscribed(CommunicationState state,
								          Gateway gateway,
								          String  serviceId,
								          String  cause) {
		ArrayList<GatewayListener> listeners = getListeners(gateway.getId());
		
		if (state != CommunicationState.ERROR) {
			for (GatewayListener listener : listeners) {
				listener.onServiceSubscribeSuccess(serviceId);
			}
		} else {
			for (GatewayListener listener : listeners) {
				listener.onServiceSubscribeFailure(serviceId, cause);
			}
		}
	}
	
	private void notifyServiceUnSubscribed(String idGateway) {
		ArrayList<GatewayListener> listeners = getListeners(idGateway);
		
		for (GatewayListener listener : listeners) {
			listener.onServiceUnSubscribeSuccess();
		}
	}
	
	private void notifyDataUpdate(Measurement measurement) {
		ArrayList<GatewayListener> listeners = getListeners(measurement.getGatewayId());
		
		for (GatewayListener listener : listeners) {
			listener.onDataUpdate(measurement);
		}
	}
	
	private void executeTask(AsyncTask<Gateway, Void, Object> task, Gateway gateway) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
		    task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, gateway);
		} else {
		    task.execute(gateway);
		}
	}
}
