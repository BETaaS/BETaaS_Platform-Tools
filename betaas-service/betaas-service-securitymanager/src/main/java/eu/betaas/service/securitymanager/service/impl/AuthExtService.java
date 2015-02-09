/**
Copyright 2014-2015 Center for TeleInFrastruktur (CTIF), Aalborg University

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

package eu.betaas.service.securitymanager.service.impl;

import eu.betaas.service.securitymanager.service.IAuthExtService;

/**
 * This class implements the IAuthExtService
 * 
 * @author Bayu Anggorojati [ba@es.aau.dk]
 * Center for TeleInFrastruktur, Aalborg University, Denmark
 *
 */
public class AuthExtService implements IAuthExtService {

	public String getToken(String gwId, String thingServiceId,
			String subjectType, byte[] subjectPublicKeyInfo) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean verifyToken(String gwId, String token) {
		// TODO Auto-generated method stub
		return false;
	}

}
