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

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.AppConfigurationEntry.LoginModuleControlFlag;
import javax.security.auth.login.Configuration;

/**
 * Implementation of a {@link Configuration} using a Java keystore.
 * 
 * @author Oliver Waeldrich
 * 
 */
public class KeystoreConfiguration extends Configuration
{

    /**
     * 
     */
    private static final String KEYSTORE_CLIENT = "KEYSTORE_CLIENT";

    private final Configuration configuration = Configuration.getConfiguration();

    private final KeystoreProperties properties;

    private AppConfigurationEntry[] wsag4jEntries = new AppConfigurationEntry[0];

    /**
     * @param properties
     *            the keystore properties
     */
    public KeystoreConfiguration( KeystoreProperties properties )
    {
        this.properties = properties;
        wsag4jEntries = readConfigurationEntries();
    }

    @Override
    public AppConfigurationEntry[] getAppConfigurationEntry( String name )
    {

        if ( KEYSTORE_CLIENT.equals( name ) )
        {
            return wsag4jEntries;
        }
        else
        {
            //
            // Otherwise return default configuration
            //
            return configuration.getAppConfigurationEntry( name );
        }
    }

    /**
     * @param name
     * @return
     */
    private AppConfigurationEntry[] readConfigurationEntries()
    {
        //
        // if WSAG4J configuration is requested, return the appropriate configuration
        //
        Vector<AppConfigurationEntry> result = new Vector<AppConfigurationEntry>();

        AppConfigurationEntry[] entries = configuration.getAppConfigurationEntry( KEYSTORE_CLIENT );
        for ( int i = 0; i < entries.length; i++ )
        {
            Map<String, Object> map = new HashMap<String, Object>();
            map.putAll( entries[i].getOptions() );
            //
            // set the keystore settings
            //
            map.put( "keyStoreURL", properties.getKeystoreFilename() );
            map.put( "keyStoreType", properties.getKeyStoreType() );
            map.put( "keyStoreAlias", properties.getKeyStoreAlias() );

            map.put( "trustStoreURL", properties.getTruststoreFilename() );
            map.put( "trustStoreType", properties.getTruststoreType() );

            String moduleName = entries[i].getLoginModuleName();
            LoginModuleControlFlag controlFlag = entries[i].getControlFlag();
            AppConfigurationEntry entry = new AppConfigurationEntry( moduleName, controlFlag, map );

            result.add( entry );
        }

        if ( entries.length == 0 )
        {
            String moduleName = KeystoreLoginModule.class.getName();
            LoginModuleControlFlag flag = LoginModuleControlFlag.REQUIRED;

            Map<String, String> map = new HashMap<String, String>();

            //
            // set the keystore settings
            //
            map.put( "keyStoreURL", properties.getKeystoreFilename() );
            map.put( "keyStoreType", properties.getKeyStoreType() );
            map.put( "keyStoreAlias", properties.getKeyStoreAlias() );

            map.put( "trustStoreURL", properties.getTruststoreFilename() );
            map.put( "trustStoreType", properties.getTruststoreType() );

            AppConfigurationEntry entry = new AppConfigurationEntry( moduleName, flag, map );
            result.add( entry );
        }
        return result.toArray( new AppConfigurationEntry[result.size()] );
    }

    @Override
    public void refresh()
    {
        configuration.refresh();
    }
}
