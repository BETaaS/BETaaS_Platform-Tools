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

import java.util.Vector;

/**
 * The registry of gateways that joined the instance
 * @author Intecs
 */
public class GWRegistry {
	
	/**
	 * Constructor
	 */
	public GWRegistry() {
		
		mGWRegistry = new Vector();
	}
	
	/**
	 * @param GWID a GW identifier
	 * @return true if the specified GW is present in the GW Registry
	 */
	public boolean hasJoined(String GWID) {
		GWRegistryRow row;
		
		for (int i=0; i < mGWRegistry.size(); i++) {
			row = (GWRegistryRow) mGWRegistry.elementAt(i);
			if (row.mGWID.equals(GWID)) return row.mJoined;
		}
		
		return false;
	}
	
	/**
	 * Mark a GW as joined or not
	 * @param GWID a GW identifier
	 * @param joined the value to set
	 * @return true iff the specified GW is present in the GW Registry
	 */
	public boolean setJoined(String GWID, boolean joined) {
		GWRegistryRow row;
		
		for (int i=0; i < mGWRegistry.size(); i++) {
			row = (GWRegistryRow) mGWRegistry.elementAt(i);
			if (row.mGWID.equals(GWID)) {
				row.mJoined = joined;
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Add an entry to the GW registry
	 * @param row the GW info to add
	 */
	public void addGW(GWRegistryRow row) {
		mGWRegistry.add(row);
		//TODO: write the GW row to the DB
	}
	
	/**
	 * @return the list of GW present in the registry and marked as joined GW
	 */
	public Vector<String> getGWList() {
		Vector<String> res = new Vector<String>();
		GWRegistryRow row;
		
		for (int i=0; i < mGWRegistry.size(); i++) {
			row = (GWRegistryRow) mGWRegistry.elementAt(i);
			if (row.mJoined) res.add(row.mGWID);
		}
		
		return res;
	}
	
	/** The actual registry */
	private Vector mGWRegistry;
}
