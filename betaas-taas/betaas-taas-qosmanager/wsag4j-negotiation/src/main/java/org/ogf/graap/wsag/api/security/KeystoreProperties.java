/* 
 * Copyright (c) 2007, Fraunhofer-Gesellschaft
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 * 
 * (1) Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the disclaimer at the end.
 *     Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in
 *     the documentation and/or other materials provided with the
 *     distribution.
 * 
 * (2) Neither the name of Fraunhofer nor the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 * 
 * DISCLAIMER
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *  
 */
package org.ogf.graap.wsag.api.security;

import org.ogf.graap.wsag4j.types.configuration.WSRFEngineConfigurationType;

/**
 * KeystoreProperties
 * 
 * @author Oliver Waeldrich
 * 
 */
public class KeystoreProperties
{

    private String keyStoreType = "JKS";

    private String keystoreFilename;

    private String keystorePassword;

    private String keyStoreAlias = "";

    private String privateKeyPassword;

    private String truststoreType = "JKS";

    private String truststoreFilename;

    private String truststorePassword;

    /**
     * default constructor
     */
    public KeystoreProperties()
    {
        super();
    }

    /**
     * Creates a new {@link KeystoreProperties} instance based on a given WSRF engine configuration.
     * 
     * @param configuration
     *            the WSRF engine configuration to use
     */
    public KeystoreProperties( WSRFEngineConfigurationType configuration )
    {
        super();

        keyStoreType = configuration.getKeystore().getKeystoreType();
        keystoreFilename = configuration.getKeystore().getKeystoreFile();
        keystorePassword = configuration.getKeystore().getKeystorePassword();
        keyStoreAlias = configuration.getKeystore().getAlias();
        privateKeyPassword = configuration.getKeystore().getAliasPassword();

        truststoreType = configuration.getTruststore().getTruststoreType();
        truststoreFilename = configuration.getTruststore().getTruststoreFile();
        truststorePassword = configuration.getTruststore().getTruststorePassword();
    }

    /**
     * @return the keyStoreType
     */
    public String getKeyStoreType()
    {
        return keyStoreType;
    }

    /**
     * @param keyStoreType
     *            the keyStoreType to set
     */
    public void setKeyStoreType( String keyStoreType )
    {
        this.keyStoreType = keyStoreType;
    }

    /**
     * @return the keystoreFilename
     */
    public String getKeystoreFilename()
    {
        return keystoreFilename;
    }

    /**
     * @param keystoreFilename
     *            the keystoreFilename to set
     */
    public void setKeystoreFilename( String keystoreFilename )
    {
        this.keystoreFilename = keystoreFilename;
    }

    /**
     * @return the keystorePassword
     */
    public String getKeystorePassword()
    {
        return keystorePassword;
    }

    /**
     * @param keystorePassword
     *            the keystorePassword to set
     */
    public void setKeystorePassword( String keystorePassword )
    {
        this.keystorePassword = keystorePassword;
    }

    /**
     * @return the keyStoreAlias
     */
    public String getKeyStoreAlias()
    {
        return keyStoreAlias;
    }

    /**
     * @param keyStoreAlias
     *            the keyStoreAlias to set
     */
    public void setKeyStoreAlias( String keyStoreAlias )
    {
        this.keyStoreAlias = keyStoreAlias;
    }

    /**
     * @return the privateKeyPassword
     */
    public String getPrivateKeyPassword()
    {
        return privateKeyPassword;
    }

    /**
     * @param privateKeyPassword
     *            the privateKeyPassword to set
     */
    public void setPrivateKeyPassword( String privateKeyPassword )
    {
        this.privateKeyPassword = privateKeyPassword;
    }

    /**
     * @return the truststoreType
     */
    public String getTruststoreType()
    {
        return truststoreType;
    }

    /**
     * @param truststoreType
     *            the truststoreType to set
     */
    public void setTruststoreType( String truststoreType )
    {
        this.truststoreType = truststoreType;
    }

    /**
     * @return the truststoreFilename
     */
    public String getTruststoreFilename()
    {
        return truststoreFilename;
    }

    /**
     * @param truststoreFilename
     *            the truststoreFilename to set
     */
    public void setTruststoreFilename( String truststoreFilename )
    {
        this.truststoreFilename = truststoreFilename;
    }

    /**
     * @return the truststorePassword
     */
    public String getTruststorePassword()
    {
        return truststorePassword;
    }

    /**
     * @param truststorePassword
     *            the truststorePassword to set
     */
    public void setTruststorePassword( String truststorePassword )
    {
        this.truststorePassword = truststorePassword;
    }

}
