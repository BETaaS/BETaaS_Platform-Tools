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

package eu.betaas.adaptation.thingsadaptor.clients;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import eu.betaas.adaptation.plugin.api.IAdaptorPlugin;


public class AdaptorClient {
	
	private List<IAdaptorPlugin> apService =  new ArrayList<IAdaptorPlugin>();
	private Logger logger= Logger.getLogger("betaas.adaptation");
	static private AdaptorClient _instance = null;
	
	private AdaptorClient(BundleContext context)
	{
	
		try {
			ServiceReference[] ref = context.getServiceReferences(IAdaptorPlugin.class.getName(), null);
			if ((ref != null) && (ref.length > 0)) {
				for (int i = 0; i < ref.length; i++) {
					IAdaptorPlugin service = ((IAdaptorPlugin) context.getService(ref[i]));
					apService.add(service);
				}
			}
			
		} catch (java.lang.NoClassDefFoundError e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static AdaptorClient instance(BundleContext context) 
	{
		if (null == _instance) 
		{			
			_instance = new AdaptorClient(context);				
			if (_instance.apService == null)
			{
				_instance = null;
				return null;
			}
		}
		return _instance;
	}

	public List<IAdaptorPlugin> getApService() {
		return apService;
	}

	public void setApService(List<IAdaptorPlugin> apService) {
		this.apService = apService;
	}

	
	
}
