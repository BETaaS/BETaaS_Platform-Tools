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

// BETaaS - Building the Environment for the Things as a Service
//
// Component: SM
// Responsible: Intecs

package eu.betaas.service.servicemanager.extended.discovery;

import java.util.Hashtable;

import org.apache.log4j.Logger;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

import org.osgi.framework.Constants;

import eu.betaas.service.servicemanager.ServiceManager;
import eu.betaas.service.servicemanager.api.ServiceManagerExternalIF;
import eu.betaas.service.servicemanager.extended.api.IExtendedService;

/**
 * This class manages the discovery of extended services and external SM within 
 * the same BETaaS instance of this GW.
 * External services references
 * @author Intecs
 */
public class Discovery {

	public Discovery(BundleContext context) {
		mContext = context;
		mRetrievedExtServices = new Hashtable<String, IExtendedService>();
		
		mSMTracker = new ServiceTracker(mContext, ServiceManagerExternalIF.class.getName(), null);
//		mSMTracker.open();
//		mLogger.info("Tracker for SM opened");
		
		//mExtServiceTracker = new ServiceTracker(mContext, IExtendedService.class.getName(), null);
    try {
      String filter = "(&(" + Constants.OBJECTCLASS + "=" + IExtendedService.class.getName() + ")(gwId="+ ServiceManager.getInstance().getGWId() +"))";
      mLogger.debug("Creating filter: " + filter);
      mExtServiceTracker = new ServiceTracker(mContext, 
                                              mContext.createFilter(filter),
                                              null);
    } catch (org.osgi.framework.InvalidSyntaxException e) {
      mLogger.error("Error creating the extended service tracker filter: " + e.getMessage());
      mExtServiceTracker = new ServiceTracker(mContext, IExtendedService.class.getName(), null);
    }
//		mExtServiceTracker.open();
//		mLogger.info("Tracker for extended services opened");
		
		Thread openThread = new Thread() {
			public void run() {
				mLogger.info("Opening the SM tracker");
				mSMTracker.open();
				mLogger.info("Tracker for SM opened");		
				
				mLogger.info("Opening the extended service tracker");
				mExtServiceTracker.open();
				mLogger.info("Tracker for extended services opened");
			}
		};
		openThread.start();

	}
	
	
	/**
	 * Retrieve from DOSGi the extended service with the specified unique name
	 * @param extServUniqueName
	 * @return the extended service or null if not found
	 */
	public IExtendedService retrieveExtendedService(String extServUniqueName) {
		IExtendedService extServRef = null;
		
		// Check if the requested service has been already retrieved in past requests
		if ((extServRef = mRetrievedExtServices.get(extServUniqueName)) != null) {
			return extServRef;
		}
		
		mLogger.debug("Searching for extended service: " + extServUniqueName);
		
		if (mContext == null) {
			mLogger.warn("Null bundle context");
			return null;
		}
		
		try {
			String id;
			ServiceReference ref[];
		
			IExtendedService serv;
			ref = mExtServiceTracker.getServiceReferences();
			if (ref != null) {
				mLogger.debug("Got " + ref.length + " elements from the tracker");
				for (int i=0; i < ref.length; i++) {
					if (ref[i] != null) {
						serv = (IExtendedService)(mContext.getService(ref[i]));

						if (serv != null) {
							id = serv.getUniqueID();
							if (id != null) {
								if (id.equals(extServUniqueName)) return serv;
							} else {
								mLogger.warn("ID n." + i + " is null");
							}
						} else {
							mLogger.warn("Service " + i + " is null");
						}
					} else {
						mLogger.warn("Service reference " + i + " is null");
					}
				}
			}

		} catch (java.lang.NoClassDefFoundError e) {
			mLogger.error("Error retrieving extended service: " + e.getMessage());
			return null;
		} catch (Exception e) {
			mLogger.error("Error retrieving extended service: " + e.getMessage());
			return null;
		}

		return null;		
		
		
//		// Search it through DOSGi
//		if (mContext == null) {
//			mLogger.warn("Cannot get extended service (null context)");
//			return null;
//		}
//
//		// NOTE: the extServiceID is unique in the GW but it is not in the instance. On the other
//		//       hand the service unique name is unique in the instance (e.g. it could contain the
//		//       package name, the vendor, etc.)
//		try {			
//			ServiceReference[] ref = 
//					mContext.getServiceReferences(IExtendedService.class.getName(),
//											      "(&(objectClass=" + IExtendedService.class.getName() + ")" +
//											      "(" + IExtendedService.EXTENDED_SERVICE_PROPERTY_NAME_UNIQUE_ID + "=" + extServUniqueName + "))");
//			if ((ref == null) || (ref.length == 0)) {
//				mLogger.warn("The requested extended services has not been found");
//				return null;
//			}
//			if (ref.length > 1) {
//				mLogger.warn("More than one extended service found with the unique service name");
//			}
//			extServRef = ((IExtendedService)mContext.getService(ref[0]));
//			
//			// store the retrieved service reference in the hash map for future calls
//			mRetrievedExtServices.put(extServUniqueName, extServRef);
//			
//		} catch (java.lang.NoClassDefFoundError e) {
//		    mLogger.error("No class definition found for IExtendedService Service");
//		    return null;
//		} catch (Exception e) {
//			mLogger.error("Cannot get the extended service: " + e.getMessage());
//			return null;
//		}		
//		
//		return extServRef;
	}


	
	/**
	 * @param gwId the id to be used for filtering available IMs
	 * @return the SM available service with the specified GW identifier, or null
	 */
	public ServiceManagerExternalIF retrieveSM(String gwId) {
		mLogger.debug("Retrieving available SMs for GW: " + gwId);
		
		if (mContext == null) {
			mLogger.warn("Null bundle context");
			return null;
		}
		
		try {
			String id;
			ServiceReference ref[];
		
			ServiceManagerExternalIF serv;
			ref = mSMTracker.getServiceReferences();
			if (ref != null) {
				mLogger.debug("Got " + ref.length + " elements from the tracker");
				for (int i=0; i < ref.length; i++) {
					if (ref[i] != null) {
						serv = (ServiceManagerExternalIF)(mContext.getService(ref[i]));

						if (serv != null) {
							id = serv.getGWId();
							if (id != null) {
								if (id.equals(gwId)) return serv;
							} else {
								mLogger.warn("ID n." + i + " is null");
							}
						} else {
							mLogger.warn("Service " + i + " is null");
						}
					} else {
						mLogger.warn("Service reference " + i + " is null");
					}
				}
			}

		} catch (java.lang.NoClassDefFoundError e) {
			mLogger.error("Error retrieving available SMs: " + e.getMessage());
			return null;
		} catch (Exception e) {
			mLogger.error("Error retrieving available SMs: " + e.getMessage());
			return null;
		}

		return null;		
	}
	

	
	
	/** Removes an extended services from the list of retrieved ones */
	public void remove(String extServUniqueName) {
		mRetrievedExtServices.remove(extServUniqueName);
	}
	
	/** Logger */
	private static Logger mLogger = Logger.getLogger(ServiceManager.LOGGER_NAME);
	
	/** The set of extended services that have been requested by applications and
	 * have been retrieved by SM. It is saved so that the execution is speeded up.
	 * The hash table stores extended service unique names and the corresponding implemented
	 * interface */
	private Hashtable<String, IExtendedService> mRetrievedExtServices;

	/** OSGi context */
	private BundleContext mContext;

	/** The tracker for other SMs of other GWs */
	private ServiceTracker mSMTracker;
	
	/** The tracker for the extended services installed within the instance */
	private ServiceTracker mExtServiceTracker;
}
