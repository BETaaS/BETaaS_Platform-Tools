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

/**
 * SecurityConstants
 * 
 * @author Oliver Waeldrich
 * 
 */
public class SecurityConstants
{

    /**
     * WSAG4J default alias key. Used for lookup in {@link org.ogf.graap.wsag.security.ws.server.Merlin}
     * properties.
     */
    public static final String DEFAULT_ALIAS = "$$$wsag4j-default-alias$$$";

    /**
     * WSAG4J default alias password key. Used for lookup in
     * {@link org.ogf.graap.wsag.security.ws.server.Merlin} properties.
     */
    public static final String DEFAULT_ALIAS_PASSWORD = "$$$wsag4j-default-password$$$";

    /**
     * WSAG4J default signing crypto key. Used for lookup in
     * {@link org.ogf.graap.wsag.security.ws.server.Merlin} properties.
     */
    public static final String CRYPTO_SIGN = "http://de.fraunhofer.scai.wsag4j/security/sign";

    /**
     * WSAG4J default encoding crypto key. Used for lookup in
     * {@link org.ogf.graap.wsag.security.ws.server.Merlin} properties.
     */
    public static final String CRYPTO_ENC = "http://de.fraunhofer.scai.wsag4j/security/encode";

    /**
     * WSAG4J default client certificate key. Used for lookup in
     * <code>org.ogf.graap.wsag.server.api.WsagMessageContext</code>.
     */
    public static final String X509_CLIENT_CERTIFICATE =
        "http://de.fraunhofer.scai.wsag4j/security/x509-client-certificate";

    /**
     * Default key to lookup the JAAS subject of the authenticated user in the
     * <code>org.ogf.graap.wsag.server.api.WsagMessageContext</code>.
     */
    public static final String AUTHENTICATED_USER =
        "http://de.fraunhofer.scai.wsag4j/security/user-subject";
    
    /**
     * WSAG4J default client certificate chain key. Used for lookup
     * <code>org.ogf.graap.wsag.server.api.WsagMessageContext</code>.
     */
    public static final String X509_CLIENT_CERTIFICATE_CHAIN =
        "http://de.fraunhofer.scai.wsag4j/security/x509-client-certificate-chain";

    /**
     * WSAG4J default server identity key. Used for lookup by
     * <code>org.ogf.graap.wsag.client.remote.WsrfResourceClient</code>.
     */
    public static final String X500_SERVER_IDENTITY =
        "http://de.fraunhofer.scai.wsag4j/security/x500-server-identity";

    /**
     * WSAG4J default server certificate key. Used for lookup by
     * <code>org.ogf.graap.wsag.client.remote.Axis2SoapClient</code>.
     */
    public static final String X509_SERVER_CERTIFICATE =
        "http://de.fraunhofer.scai.wsag4j/security/x509-server-certificate";

    /**
     * WSAG4J default server certificate chain key. Used for lookup by
     * <code>org.ogf.graap.wsag.client.remote.Axis2SoapClient</code>.
     */
    public static final String X509_SERVER_CERTIFICATE_CHAIN =
        "http://de.fraunhofer.scai.wsag4j/security/x509-server-certificate-chain";

    /**
     * WSAG4J default SAML trust delegation key.
     */
    public static final String SAML_TRUST_DELEGATION =
        "http://de.fraunhofer.scai.wsag4j/security/saml-2.0-trust-delegation";

    // public static final String WSAG4J_SERVER_CRYPTO =
    // "http://de.fraunhofer.scai.wsag4j/security/wsag4j/server";
    // public static final String WSAG4J_CLIENT_CRYPTO =
    // "http://de.fraunhofer.scai.wsag4j/security/wsag4j/client";

    /**
     * WS-Security crypto provider key.
     */
    public static final String PROP_CRYPTO_PROVIDER = "org.apache.ws.security.crypto.provider";

    /**
     * WS-Security keystore type key.
     */
    public static final String PROP_KEYSTORE_TYPE = "org.apache.ws.security.crypto.merlin.keystore.type";

    /**
     * WS-Security keystore password key.
     */
    public static final String PROP_KEYSTORE_PASS = "org.apache.ws.security.crypto.merlin.keystore.password";

    /**
     * WS-Security keystore alias key.
     */
    public static final String PROP_KEYSTORE_ALIAS = "org.apache.ws.security.crypto.merlin.keystore.alias";

    /**
     * WS-Security keystore alias password key.
     */
    public static final String PROP_KEYSTORE_ALIAS_PASS =
        "org.apache.ws.security.crypto.merlin.alias.password";

    /**
     * WS-Security keystore filename key.
     */
    public static final String PROP_KEYSTORE_FILE = "org.apache.ws.security.crypto.merlin.file";

    /**
     * WS-Security trust store filename key.
     */
    public static final String PROP_TRUSTSTORE_FILE = "org.wsag4j.ws.security.crypto.merlin.truststore.file";

    /**
     * WS-Security trust store type key.
     */
    public static final String PROP_TRUSTSTORE_TYPE = "org.wsag4j.ws.security.crypto.merlin.truststore.type";

    /**
     * WS-Security trust store password key.
     */
    public static final String PROP_TRUSTSTORE_PASS =
        "org.wsag4j.ws.security.crypto.merlin.truststore.password";

}
