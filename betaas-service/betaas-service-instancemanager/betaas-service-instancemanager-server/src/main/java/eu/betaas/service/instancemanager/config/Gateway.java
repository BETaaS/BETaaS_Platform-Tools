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
// Component: IM
// Responsible: Intecs

package eu.betaas.service.instancemanager.config;

/**
 * This class manages the configuration info about Gateways of the Betaas 
 * instance.
 * @author Intecs
 */
public class Gateway {

	/**
	 * Default constructor
	 */
	public Gateway() {
		mInstanceId = null;
		mIM = null;
		mID = null;
		mCredentials = null;
		mDescription = null;
		mAdminAddress = null;
	}
	
	/** The instance identifier if this IM belongs to an instance. The instance id is equal to the identifier
	 * of the GW to which the corresponding IM* belongs. So the full unique identifier can be considered as
	 * <mInstanceId, mID>
	 */
	public String mInstanceId;
	
	/** The Instance Manager of this Gateway */
	public InstanceManager mIM;
	
	/** GW unique identifier in the instance */
	public String mID;
	
	/** Credentials used by the GW to join the instance */
	public String mCredentials;
	
	/** Description of the gateway */
	public String mDescription;
	
	/** Address of the IM web console for this gateway */
	public String mAdminAddress;

	/** Parameters used to generate the GW credentials */
	public String mCountryCode;
	public String mState;
	public String mLocation;
	public String mOrgName;
	
	/**
	 * @return a String listing the GW attributes
	 */
	public String toString() {
		String result = "------------- IM Configuration --------------\n" + 
	                    "GW ID: " + mID + "\n" +
//	                    "IM Address: " + mIM.mAddress + "\n" +
				        "STAR: " + (mIM.mIsStar ? "Y" : "N") + "\n" +
				        "Credentials: " + mCredentials + "\n" +
				        "---------------------------------------------";
		
		return result;
	}
	
}
