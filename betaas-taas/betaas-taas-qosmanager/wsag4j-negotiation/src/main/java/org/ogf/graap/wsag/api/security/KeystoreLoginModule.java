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
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.MessageFormat;
import java.util.Map;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;
import javax.security.auth.x500.X500Principal;
import javax.security.auth.x500.X500PrivateCredential;

import org.ogf.graap.wsag.api.configuration.WSAG4JConfiguration;

/**
 * KeystoreLoginModule
 * 
 * @author Oliver Waeldrich
 * 
 */
public class KeystoreLoginModule
    implements LoginModule
{
    private Subject klmSubject;

    private CallbackHandler cbHandler;

    // private Map sharedState;

    @SuppressWarnings( "rawtypes" )
    private Map klmOptions;

    // variables related to access the keystore
    private KeyStore keystore;

    private String keystoreType;

    private String keystoreFile;

    private String keystorePassword;

    private String alias;

    private String privateKeyPassword;

    private String truststoreType;

    private String truststoreFile;

    private String truststorePassword;

    // variables related to user authentication
    private X500Principal userPrincipal;

    // variables related to login module steering
    private boolean login = false;

    private boolean commit = false;

    /**
     * {@inheritDoc}
     * 
     * @see javax.security.auth.spi.LoginModule#initialize(javax.security.auth.Subject,
     *      javax.security.auth.callback.CallbackHandler, java.util.Map, java.util.Map)
     */
    @SuppressWarnings( "rawtypes" )
    public void initialize( Subject subject, CallbackHandler callbackHandler, Map sharedState, Map options )
    {
        this.klmSubject = subject;
        this.cbHandler = callbackHandler;
        // this.sharedState = sharedState;
        this.klmOptions = options;

        initializeOptions();
    }

    private void initializeOptions()
    {

        keystoreFile = (String) klmOptions.get( "keyStoreURL" );
        keystoreType = (String) klmOptions.get( "keyStoreType" );
        alias = (String) klmOptions.get( "keyStoreAlias" );

        truststoreFile = (String) klmOptions.get( "trustStoreURL" );
        truststoreType = (String) klmOptions.get( "trustStoreType" );

        //
        // processing of the options
        //
        // keystoreFile = resolveKeystoreURL(keystoreFile);
        // truststoreFile = resolveKeystoreURL(truststoreFile);

        keystoreType = ( keystoreType == null ) ? "JKS" : keystoreType;
        truststoreType = ( truststoreType == null ) ? "JKS" : truststoreType;
    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.security.auth.spi.LoginModule#login()
     */
    public boolean login() throws LoginException
    {
        KeystoreCallback ksCallback = new KeystoreCallback();
        Callback[] callbacks = new Callback[] { ksCallback };

        // handle login callbacks
        try
        {
            cbHandler.handle( callbacks );
        }
        catch ( IOException e )
        {
            String message = "IO error during login";
            LoginException le = new LoginException( message );
            le.initCause( e );
            throw le;
        }
        catch ( UnsupportedCallbackException e )
        {
            String message = "Invalid callback handler. Callback not supported.";
            LoginException le = new LoginException( message );
            le.initCause( e );
            throw le;
        }

        keystorePassword = ksCallback.getKeystorePassword();

        truststorePassword = ksCallback.getTruststorePassword();

        privateKeyPassword = ksCallback.getPrivateKeyPassword();

        //
        // if an empty alias is supplied, we set the alias to null
        // this is for treating PKCS12 files, where certificates
        // do not have a alias
        //
        // if ("".equals(alias)) alias = null;

        if ( ( keystoreFile == null ) || ( keystorePassword == null ) || ( privateKeyPassword == null ) )
        {

            String message =
                "Missing required parameter. "
                    + "The KeystoreLoginModule requires the following parameters: "
                    + "[keystoreFilename, keystorePassword, alias, privateKeyPassword]";

            throw new LoginException( message );
        }

        loadKeyStore();

        login = true;

        return true;
    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.security.auth.spi.LoginModule#commit()
     */
    public boolean commit() throws LoginException
    {
        if ( !login )
        {
            return false;
        }

        PrivateKey userKey;
        X500PrivateCredential userCredential;
        X509Certificate[] userCertificateChain;

        try
        {
            userCertificateChain = getCertificates( alias );
            userKey = (PrivateKey) keystore.getKey( alias, privateKeyPassword.toCharArray() );
        }
        catch ( KeyStoreException e )
        {
            // thrown by keystoreManager.getCertificateByAlias(defaultAlias)[0]
            String message = "Could not get default certificate from KeyStoreManager";
            LoginException le = new LoginException( message );
            le.initCause( e );
            throw le;
        }
        catch ( Exception e )
        {
            // thrown by keystoreManager.getKeyEntry(defaultAlias)
            String message = "Could not get private key from KeyStoreManager";
            LoginException le = new LoginException( message );
            le.initCause( e );
            throw le;
        }

        if ( userCertificateChain == null )
        {
            Object[] filler = new Object[] { alias };
            String message = MessageFormat.format( "No certificates found for user {0}", filler );
            throw new LoginException( message );
        }

        userCredential = new X500PrivateCredential( userCertificateChain[0], userKey );

        userPrincipal =
            new X500Principal( userCredential.getCertificate().getSubjectX500Principal().getName() );

        klmSubject.getPrivateCredentials().add( userCredential );
        klmSubject.getPrivateCredentials().add( keystore );
        klmSubject.getPrincipals().add( userPrincipal );

        commit = true;

        return true;
    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.security.auth.spi.LoginModule#abort()
     */
    public boolean abort() throws LoginException
    {
        if ( !login )
        {
            return false;
        }
        if ( ( login ) && ( !commit ) )
        {
            // login succeeded, but overall authentication failed
            login = false;

            klmSubject.getPrincipals().remove( userPrincipal );
            // klmSubject.getPrivateCredentials().remove( keystore );

            userPrincipal = null;

            keystore = null;
            keystoreFile = null;
            keystorePassword = null;
            keystoreType = null;

            alias = null;
            privateKeyPassword = null;
        }
        else
        {
            // overall authentication succeeded and commit succeeded,
            // but someone else's commit failed
            logout();
        }

        return true;
    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.security.auth.spi.LoginModule#logout()
     */
    public boolean logout() throws LoginException
    {
        klmSubject.getPrincipals().remove( userPrincipal );
        klmSubject.getPrivateCredentials().remove( keystore );

        login = false;
        commit = false;

        userPrincipal = null;

        keystore = null;
        keystoreFile = null;
        keystorePassword = null;
        keystoreType = null;

        alias = null;
        privateKeyPassword = null;

        return true;
    }

    private synchronized KeyStore getKeystore() throws LoginException
    {
        if ( keystore == null )
        {
            loadKeyStore();
        }
        return keystore;
    }

    private void loadKeyStore() throws LoginException
    {
        try
        {
            String actualKSType = ( keystoreType == null ) ? KeyStore.getDefaultType() : keystoreType;

            keystore = KeyStore.getInstance( actualKSType );

            if ( keystoreFile == null )
            {
                throw new IOException( "No keystore specified by user." );
            }

            InputStream ksInput = WSAG4JConfiguration.findResource( keystoreFile );
            keystore.load( ksInput, keystorePassword.toCharArray() );

        }
        catch ( KeyStoreException e )
        {
            throw new LoginException( e.getMessage() );
        }
        catch ( IOException e )
        {
            throw new LoginException( e.getMessage() );
        }
        catch ( CertificateException e )
        {
            throw new LoginException( e.getMessage() );
        }
        catch ( NoSuchAlgorithmException e )
        {
            throw new LoginException( e.getMessage() );
        }

    }

    /**
     * Gets the list of certificates for a given alias.
     * <p/>
     * 
     * @param ksAlias
     *            Lookup certificate chain for this alias
     * 
     * @return Array of X509 certificates for this alias name, or null if this alias does not exist in the
     *         keystore
     * 
     * @throws KeyStoreException
     *             error accessing the keystore
     * 
     * @throws LoginException
     *             error logging into the keystore
     */
    private X509Certificate[] getCertificates( String ksAlias ) throws KeyStoreException, LoginException
    {
        Certificate[] certs = null;
        Certificate cert = null;

        KeyStore store = getKeystore();

        if ( store != null )
        {
            // There's a chance that there can only be a set of trust stores
            certs = store.getCertificateChain( ksAlias );
            if ( certs == null || certs.length == 0 )
            {
                // no cert chain, so lets check if getCertificate gives us a
                // result.
                cert = store.getCertificate( ksAlias );
            }
        }

        if ( cert != null )
        {
            certs = new Certificate[] { cert };
        }
        else if ( certs == null )
        {
            // At this pont we don't have certs or a cert
            return null;
        }

        X509Certificate[] x509certs = new X509Certificate[certs.length];
        for ( int i = 0; i < certs.length; i++ )
        {
            x509certs[i] = (X509Certificate) certs[i];
        }
        return x509certs;
    }

}
