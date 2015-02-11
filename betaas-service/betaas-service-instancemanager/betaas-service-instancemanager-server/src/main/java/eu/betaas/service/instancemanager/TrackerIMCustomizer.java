/*
Copyright 2014-2015 Intecs Spa

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
*/

package eu.betaas.service.instancemanager;

import org.apache.log4j.Logger;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import eu.betaas.service.instancemanager.api.InstanceManagerExternalIF;

/**
 * This class defines the actions to be taken on IM service changes and
 * it is used to discover IM*
 * @author Intecs
 *
 */
public class TrackerIMCustomizer implements ServiceTrackerCustomizer {

	/**
	 * Constructor
	 * @param context to retrieve bundle services
	 */
	public TrackerIMCustomizer(BundleContext context, IMStarHandler handler) {
		mContext = context;
		mIMStarHandler = handler;
	}

	public Object addingService(ServiceReference reference) {
		InstanceManagerExternalIF imStarService = null;
		mLogger.info("Tracking the IM* activation");
		
		Object ref = mContext.getService(reference);

		if (ref instanceof InstanceManagerExternalIF) {
			imStarService = (InstanceManagerExternalIF)mContext.getService(reference);
			if ((imStarService != null) && (mIMStarHandler != null)) {
				mIMStarHandler.handleNewServiceNotification(imStarService);
			}
		}
		
		return ref;
	}

	public void modifiedService(ServiceReference reference, Object service) {
	}

	public void removedService(ServiceReference reference, Object service) {
		
		//TODO: disjoin
		
		mContext.ungetService(reference);
	}

	/** Logger */
	private static Logger mLogger = Logger.getLogger(InstanceManager.LOGGER_NAME);	

	/** OSGi context */
	private final BundleContext mContext;

	/** Handler of events catched by this tracker */
	private final IMStarHandler mIMStarHandler;

}