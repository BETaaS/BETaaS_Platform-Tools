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

import eu.betaas.service.securitymanager.capability.elements.AccessRights;
import eu.betaas.service.securitymanager.capability.elements.IssuerInfo;
import eu.betaas.service.securitymanager.capability.elements.SubjectInfo;
import eu.betaas.service.securitymanager.capability.elements.ValidityCondition;

@XmlAccessorType(XmlAccessType.FIELD)

/**
 * This class describes the Capability Issuer data structure
 * 
 * @author Bayu Anggorojati [ba@es.aau.dk]
 * Center for TeleInFrastruktur, Aalborg University, Denmark
 *
 */
public class CapabilityIssuer {
	@XmlElement(name = "IssuerInfo")
	private IssuerInfo iIssuerInfo;	// this should be in the Certificate type (certificate of the issuer)
	@XmlElement(name = "SubjectInfo")
	private SubjectInfo iSubjectInfo;	// extCAP holder information --> should be another class
	@XmlElement(name = "AccessRights")
	private AccessRights iAccessRights;	// a set of access rights
	@XmlElement(name = "ResourceID")
	private String iResourceId;	// ID of resources to be accessed
	@XmlElement(name = "ValidityCondition")
	private ValidityCondition iValidityCondition;
	@XmlElement(name = "RevocationURL")
	private String iRevocationUrl;	// URL of the Capability revocation service
	@XmlElement(name = "DigitalSignature")
	private byte[] iDigitalSignature;
	
	public CapabilityIssuer(){}
	
	/**
	 * get the certificate of the extCAP's issuer (to validate the issuer)
	 * @return
	 */
	public IssuerInfo getIIssuerInfo() {
		return iIssuerInfo;
	}

	/**
	 * set the certificate of the extCAP's issuer
	 * @param issuer
	 */
	public void setIIssuerInfo(IssuerInfo iIssuerInfo) {
		this.iIssuerInfo = iIssuerInfo;
	}

	/**
	 * get the Subject Info of this extCAP holder
	 * @return
	 */
	public SubjectInfo getISubjectInfo() {
		return iSubjectInfo;
	}

	/**
	 * set the Subject Info of this extCAP holder
	 * @param subjectInfo
	 */
	public void setISubjectInfo(SubjectInfo iSubjectInfo) {
		this.iSubjectInfo = iSubjectInfo;
	}

	/**
	 * get the access right associated with this extCAP
	 * @return
	 */
	public AccessRights getIAccessRights() {
		return iAccessRights;
	}

	/**
	 * set the access right associated with this extCAP
	 * @param accessRight
	 */
	public void setIAccessRights(AccessRights iAccessRights) {
		this.iAccessRights = iAccessRights;
	}

	/**
	 * get the ID of the resource (i.e. thingServiceId) to be accessed
	 * @return
	 */
	public String getIResourceId() {
		return iResourceId;
	}

	/**
	 * set the ID of the resource (i.e. thingServiceId) to be accessed
	 * @param iResourceId
	 */
	public void setIResourceId(String iResourceId) {
		this.iResourceId = iResourceId;
	}
	
	public ValidityCondition getIValidityCondition() {
		return iValidityCondition;
	}

	public void setIValidityCondition(ValidityCondition iValidityCondition) {
		this.iValidityCondition = iValidityCondition;
	}

	/**
	 * get the URL of the capability revocation service
	 * @return
	 */
	public String getIRevocationUrl() {
		return iRevocationUrl;
	}

	/**
	 * set the URL of the capability revocation service
	 * @param iRevocationUrl
	 */
	public void setIRevocationUrl(String iRevocationUrl) {
		this.iRevocationUrl = iRevocationUrl;
	}

	/**
	 * get the digital signature of the capability. The digital signature is a 
	 * product of the hashed resourceId signed with the private key in the 
	 * issuer's (as the delegator) certificate.
	 * @return
	 */
	public byte[] getIDigitalSignature() {
		return iDigitalSignature;
	}

	/**
	 * set the digital signature of the capability
	 * @param iDigitalSignature
	 */
	public void setIDigitalSignature(byte[] iDigitalSignature) {
		this.iDigitalSignature = iDigitalSignature;
	}
}
