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

@author Bayu Anggorojati [ba@es.aau.dk]
Center for TeleInFrastruktur, Aalborg University, Denmark
 */

package eu.betaas.taas.securitymanager.common.ec.operator;

import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cms.CMSSignatureAlgorithmNameGenerator;
import org.bouncycastle.cms.SignerInformationVerifier;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.operator.DigestAlgorithmIdentifierFinder;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.SignatureAlgorithmIdentifierFinder;

public class BcECDSASignerInfoVerifierBuilder {
	private BcECDSAContentVerifierProviderBuilder contentVerifierProviderBuilder;
	private DigestCalculatorProvider digestCalculatorProvider;
	private CMSSignatureAlgorithmNameGenerator sigAlgNameGen;
	private SignatureAlgorithmIdentifierFinder sigAlgIdFinder;
	
	public BcECDSASignerInfoVerifierBuilder(
			CMSSignatureAlgorithmNameGenerator sigAlgNameGen, 
			SignatureAlgorithmIdentifierFinder sigAlgIdFinder, 
			DigestAlgorithmIdentifierFinder digestAlgorithmFinder, 
			DigestCalculatorProvider digestCalculatorProvider){
		
		this.sigAlgNameGen = sigAlgNameGen;
		this.sigAlgIdFinder = sigAlgIdFinder;
		this.contentVerifierProviderBuilder = 
				new BcECDSAContentVerifierProviderBuilder(digestAlgorithmFinder);
		this.digestCalculatorProvider = digestCalculatorProvider;
	}
	
	public SignerInformationVerifier build(X509CertificateHolder certHolder) 
			throws OperatorCreationException{
		return new SignerInformationVerifier(sigAlgNameGen, 
				sigAlgIdFinder, 
				contentVerifierProviderBuilder.build(certHolder), 
				digestCalculatorProvider);
	}
	
	public SignerInformationVerifier  build(AsymmetricKeyParameter pubKey) 
			throws OperatorCreationException{
		return new SignerInformationVerifier(sigAlgNameGen, 
				sigAlgIdFinder, 
				contentVerifierProviderBuilder.build(pubKey), 
				digestCalculatorProvider);
	}
}
