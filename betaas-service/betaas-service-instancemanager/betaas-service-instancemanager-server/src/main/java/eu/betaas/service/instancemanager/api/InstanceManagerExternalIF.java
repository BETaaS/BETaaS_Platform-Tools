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

package eu.betaas.service.instancemanager.api;

import java.util.Vector;

import eu.betaas.service.instancemanager.IMInfo;

/** 
 * The OSGi interface exported by IM to other bundles (internal or external to this IM's GW) 
 * @author Intecs
 */
public interface InstanceManagerExternalIF {
	
	/**
	 * Called by other IMs to join the instance to which the IM* belongs
	 * @param info the joining IM's information as a JSON string corresponding to a IMInfo object
	 * @param credentials used to secure the join operation
	 * @return true in case of success, false if an error occurs
	 */
	public boolean joinInstance(String info, String credentials);

	/**
	 * Called by other IMs to disjoin from the instance to which the IM* belongs
	 * @param info the disjoining IM's information as a JSON string corresponding to a IMInfo object
	 * @param credentials used to secure the join operation
	 * @return true in case of success, false if an error occurs
	 */
	public boolean disjoinInstance(String info, String credentials);
	
	/**
	 * @return the instance identifier. It is not null when IM joined an instance
	 */
	public String getInstanceID();
	
	/**
	 * @return true iff the IM is the backup-IM*
	 */
	public boolean isBackupStar();
	
	/**
	 * @return the GW identifier
	 */
	public String getGWID();
	
	/**
	 * @return the administration GUI address
	 */
	public String getAdminAddress();
	
	/**
	 * @return the description of the GW to which the IM belongs
	 */
	public String getDescription();	
	
	/**
	 * @return true iff the GW is the star one
	 */
	public boolean isGWStar();
	
//	/**
//	 * Used to change the star configuration of the GW
//	 * @param star
//	 * @return true iff the change can be applied, false otherwise (e.g. the IM is star and the list of
//	 * joined GW is not empty)
//	 */
//	public boolean setGWStar(boolean star);
	
	/**
	 * @return the ID of the GW Star if it is found 
	 */
	public String getGWStarID();
	
	/**
	 * @return the list of joined GWs in case this GW is the star one
	 */
	public Vector<String> getJoinedGWs();
	
	/**
	 * Used to request this IM to start the join procedure with the IM with the specified ID, if it is found
	 * @param gwId of the GW to which request the join
	 * @return true iff the join procedure is started
	 */
	public boolean requestJoin(String gwId);
	
	/**
	 * Used to request this IM to start the disjoin procedure with the IM*, if it is found
	 * @return true iff the join procedure is started
	 */
	public boolean requestDisjoin();
	
	/**
	 * Used (e.g. by the Web admin app) to request info about this IM, its instance and
	 * other visible instances
	 * @return the representation of a Json object containing all the instance info
	 */
	public String getInstanceInfo();
	
	/**
	 * Called by a GW that has joined the instance to request SECM the GW certificate
	 * @param countryCode
	 * @param state
	 * @param location
	 * @param orgName
	 * @param gwId
	 * @return true on success
	 */
	public boolean requestGwCertificate(String countryCode, 
			                            String state, 
			                            String location, 
			                            String orgName, 
			                            String gwId);
}
