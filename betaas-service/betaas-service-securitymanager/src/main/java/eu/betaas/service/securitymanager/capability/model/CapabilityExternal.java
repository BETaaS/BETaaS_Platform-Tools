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

package eu.betaas.service.securitymanager.capability.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.google.gson.JsonObject;

import eu.betaas.service.securitymanager.capability.elements.AccessRights;
import eu.betaas.service.securitymanager.capability.elements.IssuerInfo;
import eu.betaas.service.securitymanager.capability.elements.SubjectInfo;
import eu.betaas.service.securitymanager.capability.elements.ValidityCondition;

//@XmlRootElement(name = "CapabilityExternal")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CapabilityExternal")

/**
 * This class describes the External Capability data structure
 * 
 * @author Bayu Anggorojati [ba@es.aau.dk]
 * Center for TeleInFrastruktur, Aalborg University, Denmark
 *
 */
public class CapabilityExternal {
	@XmlElement(name = "IssuerInfo")
	private IssuerInfo issuerInfo;	// this should be in the Certificate type (certificate of the issuer)
	@XmlElement(name = "SubjectInfo")
	private SubjectInfo subjectInfo;	// extCAP holder information --> should be another class
	@XmlElement(name = "AccessRights")
	private AccessRights accessRights;	// a set of access rights
	@XmlElement(name = "ResourceID")
	private String resourceId;	// ID of resources to be accessed
	@XmlElement(name = "ValidityCondition")
	private ValidityCondition validityCondition;
	@XmlElement(name = "RevocationURL")
	private String revocationUrl;	// URL of the Capability revocation service
	@XmlElement(name = "DigitalSignature")
	private byte[] digitalSignature;
	
	public CapabilityExternal(){}
	
	/**
	 * get the certificate of the extCAP's issuer (to validate the issuer)
	 * @return
	 */
	public IssuerInfo getIssuerInfo() {
		return issuerInfo;
	}

	/**
	 * set the certificate of the extCAP's issuer
	 * @param issuer
	 */
	public void setIssuerInfo(IssuerInfo issuer) {
		this.issuerInfo = issuer;
	}

	/**
	 * get the Subject Info of this extCAP holder
	 * @return
	 */
	public SubjectInfo getSubjectInfo() {
		return subjectInfo;
	}

	/**
	 * set the Subject Info of this extCAP holder
	 * @param subjectInfo
	 */
	public void setSubjectInfo(SubjectInfo subjectInfo) {
		this.subjectInfo = subjectInfo;
	}

	/**
	 * get the access right associated with this extCAP
	 * @return
	 */
	public AccessRights getAccessRights() {
		return accessRights;
	}

	/**
	 * set the access right associated with this extCAP
	 * @param accessRight
	 */
	public void setAccessRights(AccessRights accessRights) {
		this.accessRights = accessRights;
	}

	/**
	 * get the ID of the resource (i.e. thingServiceId) to be accessed
	 * @return
	 */
	public String getResourceId() {
		return resourceId;
	}

	/**
	 * set the ID of the resource (i.e. thingServiceId) to be accessed
	 * @param resourceId
	 */
	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}
	
	public ValidityCondition getValidityCondition() {
		return validityCondition;
	}

	public void setValidityCondition(ValidityCondition validityCondition) {
		this.validityCondition = validityCondition;
	}

	/**
	 * get the URL of the capability revocation service
	 * @return
	 */
	public String getRevocationUrl() {
		return revocationUrl;
	}

	/**
	 * set the URL of the capability revocation service
	 * @param revocationUrl
	 */
	public void setRevocationUrl(String revocationUrl) {
		this.revocationUrl = revocationUrl;
	}

	/**
	 * get the digital signature of the capability. The digital signature is a 
	 * product of the hashed resourceId signed with the private key in the 
	 * issuer's (as the delegator) certificate.
	 * @return
	 */
	public byte[] getDigitalSignature() {
		return digitalSignature;
	}

	/**
	 * set the digital signature of the capability
	 * @param digitalSignature
	 */
	public void setDigitalSignature(byte[] digitalSignature) {
		this.digitalSignature = digitalSignature;
	}

	/**
	 * return the capability's content in (XML) String 
	 */
	@Override
	public String toString() {
		JsonObject jo = new JsonObject();
		
		jo.addProperty("IssuerInfo", issuerInfo.toString());
		jo.addProperty("SubjectInfo", subjectInfo.toString());
		jo.addProperty("AccessRights", accessRights.toString());
		jo.addProperty("ResourceID", resourceId);
		jo.addProperty("ValidityCondition", validityCondition.toString());
		jo.addProperty("RevocationURL", revocationUrl);
		String sig = "";
		for(byte b : digitalSignature){
			sig = sig + Integer.toHexString(0xFF & b);
		}
		jo.addProperty("DigitalSignature", sig);
		
		return jo.toString();
	}
	
	
}
