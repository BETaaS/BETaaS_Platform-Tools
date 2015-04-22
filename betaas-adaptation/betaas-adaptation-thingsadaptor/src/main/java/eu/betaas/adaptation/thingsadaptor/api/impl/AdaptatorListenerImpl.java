/**
* Copyright 2014-2015 Converge ICT
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*
*/

package eu.betaas.adaptation.thingsadaptor.api.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;

import eu.betaas.adaptation.contextmanager.api.SemanticParserAdaptator;
import eu.betaas.adaptation.plugin.api.IAdaptorListener;
import eu.betaas.adaptation.plugin.api.IAdaptorPlugin;
import eu.betaas.adaptation.thingsadaptor.clients.AdaptorClient;
import eu.betaas.adaptation.thingsadaptor.port.ThingConstructor;

public class AdaptatorListenerImpl implements IAdaptorListener {

	Logger mLogger = Logger.getLogger("betaas.adaptation");
	private ServiceListener sl;
	private static BundleContext context;
	ServiceReference sr;
	private static AdaptatorListenerImpl listener = null;
	private IAdaptorPlugin adaptationPlugin;
	private SemanticParserAdaptator adaptationcm;

	private AdaptatorListenerImpl() {
		super();		
	}

	public static AdaptatorListenerImpl getInstance() {
		Logger mLogger = Logger.getLogger("betaas.adaptation");
		mLogger.debug("Called getInstance!"); // comment
		if (listener == null) {
			listener = new AdaptatorListenerImpl();
		}
		return listener;
	}

	public boolean notify(String type, String resourceID, HashMap<String, String> value) {
		mLogger.debug("Got notification from adaptation plugin for DeviceID:"+resourceID + " with value : " + value);
		ThingConstructor thingConstructor = new ThingConstructor(adaptationcm);
		thingConstructor.notifyMeasurment(resourceID, value);
		return false;
	}
	
	public boolean removeThing(String thingId) {
		mLogger.debug("Got notification from adaptation plugin that DeviceID:"+thingId + " was removed");
		List<String> ids = new ArrayList<String>();
		ids.add(thingId);
		adaptationcm.removeThing(ids);
		return true;
	}

	public void start() {
		AdaptorClient sClient = AdaptorClient.instance(context);
		adaptationPlugin = sClient.getApService();
		setMyServiceRegistered();
		register();
	}

	public void stop() {
		stopListener();
	}

	public void setMyServiceRegistered() {
		mLogger.info("####Registering the AdaptatorListenerImpl.");
		try {
			sl = new ServiceListener() {
				public void serviceChanged(ServiceEvent ev) {
					sr = ev.getServiceReference();
					IAdaptorListener adaptationLResource = (IAdaptorListener) context
							.getService(sr);
					switch (ev.getType()) {
					case ServiceEvent.REGISTERED: {
						mLogger.info("Register event");
						adaptationPlugin.setListener(adaptationLResource);
					}
						break;
					case ServiceEvent.UNREGISTERING: {
						mLogger.info("Unregister event");
						adaptationPlugin.setListener(null);
						context.ungetService(sr);
						sr = null;
					}
						break;
					default:
						mLogger.debug("default event");
						break;
					}
				}
			};
			context.addServiceListener(sl);
		} catch (Exception e) {
			mLogger.error("EXCEPTION: " + e.getMessage());
		}
	}

	public boolean stopListener() {
		try {
			context.removeServiceListener(sl);
			this.sl = null;
		} catch (Exception e) {
			mLogger.error("EXCEPTION: " + e.getMessage());
			return false;
		}
		return true;
	}

	public void register() {

		try {

			ServiceReference[] srl = context.getServiceReferences(IAdaptorListener.class.getName(), null);

			if (srl != null) {
				for (int i = 0; srl != null && i < srl.length; i++) {
					sl.serviceChanged(new ServiceEvent(ServiceEvent.REGISTERED,
							srl[i]));
				}
			}
		} catch (InvalidSyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void setContext(BundleContext context) {
		this.context = context;
	}
	
	public void setAdaptationcm(SemanticParserAdaptator adaptationcm) {
		this.adaptationcm = adaptationcm;
	}

}
