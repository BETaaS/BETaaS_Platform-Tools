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

package eu.betaas.taas.securitymanager.common.model;

import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;

public class BcCredential
{
    private X509CertificateHolder[] certChain;
    private AsymmetricKeyParameter privateKey;
    private String alias;

    public BcCredential(String alias, AsymmetricKeyParameter privateKey, 
    		X509CertificateHolder cert){
        this.certChain = new X509CertificateHolder[] { cert };
        this.privateKey = privateKey;
        this.alias = alias;
    }

    public BcCredential(String alias, AsymmetricKeyParameter privateKey, 
    		X509CertificateHolder[] certChain){
        this.certChain = certChain;
        this.privateKey = privateKey;
        this.alias = alias;
    }

    public AsymmetricKeyParameter getPrivateKey(){
        return privateKey;
    }

    public X509CertificateHolder[] getCertificateChain(){
        return certChain;
    }

    public String getAlias(){
        return alias;
    }
}
