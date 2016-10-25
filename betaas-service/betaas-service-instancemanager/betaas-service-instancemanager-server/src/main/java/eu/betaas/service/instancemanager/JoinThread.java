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

import eu.betaas.service.instancemanager.api.InstanceManagerExternalIF;
import eu.betaas.service.instancemanager.config.Configuration;
import eu.betaas.service.servicemanager.ServiceManager;

/**
 * The Thread in charge of requesting the join and disjoin procedures to IM*
 * @author Intecs
 */
public class JoinThread extends Thread {
	
	/**
	 * Constructor
	 * @param requestingIM The IM requesting to join the instance
	 * @param IMStarService The IM* star service to which the join must be requested
	 * @param GWID The gateway identifier of the IM requesting the join
	 * @param credentials To be used to join the instance
	 */
	public JoinThread(InstanceManager requestingIM, 
	                  InstanceManagerExternalIF IMStarService,
			          String GWID,
			          String credentials) {
		mRequestingIM = requestingIM;
		mIMStarService = IMStarService;
		mGWID = GWID;
		mCredentials = credentials;
		mJoin = true;
	}
	
	/**
	 * Constructor
	 * @param requestingIM The IM requesting to join the instance
	 * @param IMStarService The IM* star service to which the join must be requested
	 * @param GWID The gateway identifier of the IM requesting the join
	 * @param credentials To be used to join the instance
	 * @param join true to request join, false to request disjoin
	 */
	public JoinThread(InstanceManager requestingIM, 
			          InstanceManagerExternalIF IMStarService,
			          String GWID,
			          String credentials,
			          boolean join) {
		mRequestingIM = requestingIM;
		mIMStarService = IMStarService;
		mGWID = GWID;
		mCredentials = credentials;
		mJoin = join;
	}
	
    public void run() {
    	boolean res;
		try {
			if (mJoin) mLogger.info("Requesting to join the BETaaS instance");
			else mLogger.info("Requesting to disjoin the BETaaS instance");
			
			if ((mIMStarService.getInstanceID() == null) || (mIMStarService.getInstanceID().length() == 0)) {
				mLogger.error("The IM* does not have an instance identifier associated. Cancelling the operation");
				return;
			}
			
			IMInfo info = mRequestingIM.getInfo();
			mLogger.info("These are the info passed to the IM*:");
			mLogger.info("GW ID: " + info.mGWId);
			mLogger.info("Is Star: " + info.mIsStar);
			mLogger.info("Description: " + info.mDescription);
			mLogger.info("Admin Address" + info.mAdminAddress);
			
			if (mJoin) {
				res = mIMStarService.joinInstance(info.getGson(), mCredentials);
			} else {
				res = mIMStarService.disjoinInstance(info.getGson(), mCredentials);
			}
			
			if (!res) {
				if (mJoin) {
					mLogger.warn("Join refused by IM*");
					mRequestingIM.busMessage("Join refused by IM*", "error", InstanceManager.DEPENDABILITY);
				} else {
					mLogger.warn("Disjoin refused by IM*");
					mRequestingIM.busMessage("Disjoin refused by IM*", "error", InstanceManager.DEPENDABILITY);
				}
				return;
			} else {
				mLogger.info("Joined the BETaaS instance");
				mRequestingIM.busMessage("Joined the BETaaS instance", "info", InstanceManager.MONITORING);
			}
			
		} catch (Exception e) {
			if (mJoin) {
				System.out.println("Error joining the BETaaS instance: " + e.getMessage());
			} else {
				System.out.println("Error disjoining the BETaaS instance: " + e.getMessage());
			}
			return;
		}
		
		if (mJoin) {
			Configuration config = mRequestingIM.getConfiguration();

			if (mRequestingIM.getService().requestGwCertificate(config.mGW.mCountryCode, 
														  config.mGW.mState, 
														  config.mGW.mLocation, 
														  config.mGW.mOrgName, 
														  config.mGW.mID)) {
				mLogger.info("Certificate requested");
			} else {
				mLogger.error("Error requesting GW certificate to IM*");
			}
			
			mRequestingIM.setJoined(true, mIMStarService.getInstanceID());
			mRequestingIM.synchronize();
		} else {
			mRequestingIM.setJoined(false, null);
			mRequestingIM.synchronize();
		}
    }
    
	/** Logger */
	private static Logger mLogger = Logger.getLogger(InstanceManager.LOGGER_NAME);	

    /** The IM requesting to join the instance */
    private InstanceManager mRequestingIM;
    
    /** The IM* star service to which the join must be requested */
    private InstanceManagerExternalIF mIMStarService;

    /** GW identifier */
    private String mGWID;
    
    /** Credentials used to request the join */
    private String mCredentials;
    
    /** True iff the thread must request join, false for disjoin */
    private boolean mJoin;
}


