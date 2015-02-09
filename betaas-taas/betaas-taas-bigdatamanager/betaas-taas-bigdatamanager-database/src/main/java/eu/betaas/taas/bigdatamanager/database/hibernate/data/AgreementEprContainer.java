/*
 Copyright 2014-2015 Hewlett-Packard Development Company, L.P.

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
package eu.betaas.taas.bigdatamanager.database.hibernate.data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "AGREEMENT_EPR_CONTAINER")
public class AgreementEprContainer   {


	@Id
	@GeneratedValue
    private Integer id;

	@Column( name = "agreement_id" )
    private String agreementId;
	@Column( name = "agreement_factory_id" )
    private String agreementFactoryId;
	@Column( name = "epr" )
    private String epr;
	@Column( name = "epr_address" )
    private String eprAddress;


	public Integer getId() {
		return id;
	}

	public String getAgreementId() {
		return agreementId;
	}

	public void setAgreementId(String agreementId) {
		this.agreementId = agreementId;
	}

	public String getAgreementFactoryId() {
		return agreementFactoryId;
	}

	public void setAgreementFactoryId(String agreementFactoryId) {
		this.agreementFactoryId = agreementFactoryId;
	}

	public String getEpr() {
		return epr;
	}

	public void setEpr(String epr) {
		this.epr = epr;
	}

	public String getEprAddress() {
		return eprAddress;
	}

	public void setEprAddress(String eprAddress) {
		this.eprAddress = eprAddress;
	}


    
    
	
	
}
