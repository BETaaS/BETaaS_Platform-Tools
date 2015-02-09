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

import java.io.IOException;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.ConfirmationCallback;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.TextOutputCallback;
import javax.security.auth.callback.UnsupportedCallbackException;


/**
 * KeystoreCallbackHandler
 * 
 * @author Oliver Waeldrich
 * 
 */
public class KeystoreCallbackHandler implements CallbackHandler
{

    private KeystoreProperties properties;

    /**
     * The callback handler uses the specified keystore properties to handle JAAS callbacks on the specified
     * keystore.
     * 
     * @param properties
     *            the keystore properties to use
     */
    public KeystoreCallbackHandler( KeystoreProperties properties )
    {
        this.properties = properties;
    }

    /**
     * {@inheritDoc}
     */
    public void handle( Callback[] callbacks ) throws IOException, UnsupportedCallbackException
    {
        int passwordInvocations = 0;

        for ( int i = 0; i < callbacks.length; i++ )
        {
            Callback callback = callbacks[i];

            if ( callback instanceof KeystoreCallback )
            {
                KeystoreCallback ksCallback = (KeystoreCallback) callback;

                ksCallback.setPrivateKeyPassword( properties.getPrivateKeyPassword() );
                ksCallback.setKeystorePassword( properties.getKeystorePassword() );
                ksCallback.setTruststorePassword( properties.getTruststorePassword() );

                continue;
            }
            else if ( callback instanceof TextOutputCallback )
            {
                continue;
            }
            else if ( callback instanceof NameCallback )
            {
                NameCallback nameCallback = (NameCallback) callback;
                nameCallback.setName( properties.getKeyStoreAlias() );
                continue;
            }
            else if ( callback instanceof PasswordCallback )
            {
                PasswordCallback passwordCallback = (PasswordCallback) callback;

                if ( passwordInvocations == 0 )
                {
                    passwordCallback.setPassword( properties.getKeystorePassword().toCharArray() );
                }
                else if ( passwordInvocations == 1 )
                {
                    passwordCallback.setPassword( properties.getPrivateKeyPassword().toCharArray() );
                }
                else
                {
                    throw new UnsupportedCallbackException( callback, "Only 2 invocations allowed." );
                }
                continue;
            }
            else if ( callback instanceof ConfirmationCallback )
            {
                ConfirmationCallback confirmationCallback = (ConfirmationCallback) callback;
                confirmationCallback.setSelectedIndex( ConfirmationCallback.OK );
                continue;
            }

            throw new UnsupportedCallbackException( callback );
        }
    }
}
