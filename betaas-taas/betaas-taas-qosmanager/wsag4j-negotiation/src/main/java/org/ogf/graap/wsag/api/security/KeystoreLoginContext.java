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

import java.net.URL;
import java.net.URLDecoder;

import javax.security.auth.Subject;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

import org.apache.log4j.Logger;
import org.ogf.graap.wsag.api.WsagConstants;
import org.ogf.graap.wsag.api.logging.LogMessage;
import org.ogf.graap.wsag4j.types.configuration.WSRFEngineConfigurationType;

/**
 * KeystoreLoginContext
 * 
 * @author Oliver Waeldrich
 * 
 */
public class KeystoreLoginContext extends LoginContext
{

    /**
     * JAAS default configuration
     */
    private static final String JAAS_DEFAULT_CONFIGURATION =
        "/META-INF/org.ogf.graap.wsag.api.security.KeystoreLoginContext.properties";

    private static final Logger LOG = Logger.getLogger( KeystoreLoginContext.class );

    static
    {
        try
        {
            if ( System.getProperties().contains( "java.security.auth.login.config" ) )
            {
                LOG.warn( "java.security.auth.login.config is already set - this may corrupt WSAG4J configuration" );
            }
            else
            {
                //
                // read application provided configuration
                //
                URL authconf = KeystoreLoginContext.class.getResource( WsagConstants.WSAG4J_JAAS_CONFIG_FILE );

                //
                // if null read client implementation default configuration
                //
                if ( authconf == null )
                {
                    authconf =
                        KeystoreLoginContext.class.getResource( WsagConstants.WSAG4J_JAAS_CONFIG_FILE_DEFAULT );
                }

                //
                // if still null use default API configuration
                //
                if ( authconf == null )
                {
                    authconf = KeystoreLoginContext.class.getResource( JAAS_DEFAULT_CONFIGURATION );
                }

                String p = URLDecoder.decode( authconf.toExternalForm(), "UTF-8" );
                LOG.info( LogMessage.getMessage( "WSAG4J JAAS configuration: {0}", p ) );

                System.setProperty( "java.security.auth.login.config", p );
            }
        }
        catch ( Exception e )
        {
            LOG.equals( "Could not read JAAS configuration." );
        }
    }

    /**
     * Creates a new login context using the specified keystore properties.
     * 
     * @param properties
     *            the keystore properties to use
     * 
     * @throws LoginException
     *             failed to login
     */
    public KeystoreLoginContext( KeystoreProperties properties )
        throws LoginException
    {
        this( new KeystoreCallbackHandler( properties ), new KeystoreConfiguration( properties ) );
    }

    /**
     * Creates a new login context using the specified WSRF engine configuration.
     * 
     * @param configuration
     *            the WSRF engine configuration to use
     * 
     * @throws LoginException
     *             failed to login
     */
    public KeystoreLoginContext( WSRFEngineConfigurationType configuration )
        throws LoginException
    {
        this( new KeystoreProperties( configuration ) );
    }

    private KeystoreLoginContext( KeystoreCallbackHandler cbHandler, KeystoreConfiguration configuration )
        throws LoginException
    {
        super( "KEYSTORE_CLIENT", new Subject(), cbHandler, configuration );
    }

}
