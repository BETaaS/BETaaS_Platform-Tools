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

package eu.betaas.service.instancemanager;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Used to pass IM information to IM* when joining an instance
 * @author Intecs
 */
public class IMInfo {
	
	public IMInfo() {
		mGWId = null;
		mAdminAddress = null;
		mDescription = null;
		mIsStar = false;
		mCredentials = null;
	}
	
	/** Gateway identifier */
	public String mGWId;
	
	/** Address of the IM administration console */
	public String mAdminAddress;
	
	/** Description of the GW */
	public String mDescription;
	
	/** true iff the IM is star */
	public boolean mIsStar;
	
	/** Credentials used to join instances */
	public String mCredentials;
	
	/**
	 * @return the String representation of a GSON object corresponding to this IMInfo
	 */
	public String getGson() {
		Gson gson = new Gson();
		JsonObject info = new JsonObject();
	    info.add("IMinfo", gson.toJsonTree(this));
	    return info.toString();
	}
	
	/**
	 * Build an IMInfo object starting from a GSON representation
	 * @param info the GSON representation to set this object
	 * @throws Exception
	 */
	public void build(String info) throws Exception {
    	JsonElement jelement = new JsonParser().parse(info);
    	JsonObject jobject = jelement.getAsJsonObject();
    	JsonObject IMinfo = jobject.getAsJsonObject("IMinfo");
    	mGWId = IMinfo.get("mGWId").getAsString();
    	mAdminAddress = IMinfo.get("mAdminAddress").getAsString();
    	mDescription = IMinfo.get("mDescription").getAsString();
    	mIsStar = IMinfo.get("mIsStar").getAsBoolean();
    	mCredentials = IMinfo.get("mCredentials").getAsString();
	}
}
