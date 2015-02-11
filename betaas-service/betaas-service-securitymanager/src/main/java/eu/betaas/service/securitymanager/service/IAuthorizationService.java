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

package eu.betaas.service.securitymanager.service;

import java.io.IOException;

import javax.xml.bind.JAXBException;

import org.bouncycastle.cert.CertException;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.operator.OperatorException;

/**
 * This class provides methods to authenticate the application certificate, as
 * well as any operation related to the token (creation, validation, and revocation)
 * 
 * @author Bayu Anggorojati [ba@es.aau.dk]
 * Center for TeleInFrastruktur, Aalborg University, Denmark
 *
 */
public interface IAuthorizationService {
	
	/**
	 * Method to check the application's credential/certificate in installation
	 * @param credential: the APPS certificate in byte[]
	 * @return true if certificate is valid, false otherwise
	 * @throws Exception
	 */
	public boolean checkAuthApplication(String appId, byte[] credential) throws Exception;
	
	/**
	 * This method is invoked by other Module (TaaSRM) upon apps. installation (or
	 * GW joining, etc), which returns the overall token (External Capabilities)
	 * for the requested thingServiceIDs
	 * @param thingServiceIds: list thingServiceIDs requested by applications, or
	 * GW or etc...
	 * @param subjectType: type of Subject that sends the token request (GW, APPS,
	 * USER)
	 * @param subjectPublicKeyInfo: information about public key info of the 
	 * subject
	 * @return the overall token for accessing the services
	 */
	public String getToken(String[] thingServiceId, String subjectType, byte[] subjectPublicKeyInfo) throws Exception;
	
	/**
	 * This method is invoked by TaaSRM especially upon apps. installation, which
	 * returns the overall token (External Capabilities) for the requested thing-
	 * ServiceIDs
	 * @param thingServiceIds: list thingServiceIDs requested by applications, or
	 * GW or etc...
	 * @param subjectType: type of Subject that sends the token request (GW, APPS,
	 * USER)
	 * @param subjectPublicKeyInfo: information about public key info of the 
	 * subject
	 * @return the overall token for accessing the services
	 * @throws Exception
	 */
	public String getTokenApp(String[] thingServiceId, String subjectType, String appId) throws Exception;
	
	/**
	 * Method to validate the certificate of the token/capability issuer
	 * @param cert: Issuer certificate extracted from capability
	 * @param it: Issuer Type
	 * @return
	 * @throws IOException
	 * @throws OperatorException
	 * @throws CertException
	 */
	public boolean verifyToken(String token) throws JAXBException, IOException, OperatorException, CertException, CMSException;
	
	
	public String updateToken(String token);
	public boolean revokeToken(String token);

}
