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

import javax.security.auth.callback.Callback;

/**
 * KeystoreHandler
 * 
 * @author Oliver Waeldrich
 * 
 */
public class KeystoreCallback implements Callback
{

    private String keystorePassword;

    private String truststorePassword;

    private String privateKeyPassword;

    /**
     * @param keystorePassword
     *            The keystore password to set.
     */
    public void setKeystorePassword( String keystorePassword )
    {
        this.keystorePassword = keystorePassword;
    }

    /**
     * @return Returns the privateKeyPassword.
     */
    public String getPrivateKeyPassword()
    {
        return privateKeyPassword;
    }

    /**
     * @param privateKeyPassword
     *            The privateKeyPassword to set.
     */
    public void setPrivateKeyPassword( String privateKeyPassword )
    {
        this.privateKeyPassword = privateKeyPassword;
    }

    /**
     * @return Returns the keystorePassword.
     */
    public String getKeystorePassword()
    {
        return keystorePassword;
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
