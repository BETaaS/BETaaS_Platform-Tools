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
package org.ogf.graap.wsag.api.configuration;

import org.apache.log4j.Logger;
import org.ogf.graap.wsag.api.logging.LogMessage;

/**
 * MSSEnvironment
 * 
 * @author Oliver Waeldrich
 */
public class WSAG4JEnvironment
    implements WSAG4JConfigurationEnvironment
{

    private static final Logger LOG = Logger.getLogger( WSAG4JEnvironment.class );

    /**
     * System property to lookup default WSAG4J configuration path.
     */
    public static final String DEFAULT_CONFIGURATION_PATH_KEY = "wsag4j.configuration.path";

    /**
     * Default WSAG4J configuration path.
     */
    public static final String DEFAULT_CONFIGURATION_PATH = "/etc/wsag4j";

    /**
     * Default WSAG4J configuration file.
     */
    public static final String DEFAULT_CONFIGURATION_FILE = "wsag4j.properties";

    private String configurationPath = null;

    /**
     * default constructor
     */
    public WSAG4JEnvironment()
    {
        String message =
            "WSAG4J environment properties file not found. Using default configuration path {0}.";
        LOG.debug( LogMessage.getMessage( message, DEFAULT_CONFIGURATION_PATH ) );

        configurationPath = DEFAULT_CONFIGURATION_PATH;
    }

    /**
     * Creates a configuration environment with the specified configuration path.
     * 
     * @param path
     *            the WSAG4J configuration path
     */
    public WSAG4JEnvironment( String path )
    {
        if ( path == null )
        {
            path = DEFAULT_CONFIGURATION_PATH;

            String message =
                "Configuration path not specified in WSAG4J environment properties file. "
                    + "Using default configuration path {0}.";
            LOG.debug( LogMessage.getMessage( message, DEFAULT_CONFIGURATION_PATH ) );
        }
        else
        {
            String message = "Configuration path specified in WSAG4J environment properties file. Path: {0}";
            LOG.debug( LogMessage.getMessage( message, path ) );
        }

        configurationPath = path;
    }

    /**
     * {@inheritDoc}
     * 
     * @see WSAG4JConfigurationEnvironment#getConfigurationPath()
     */
    public String getConfigurationPath()
    {
        String defaultPath = System.getProperty( DEFAULT_CONFIGURATION_PATH_KEY );
        if ( defaultPath != null )
        {
            String message =
                "Found system property {0} with value {1} (overwriting directory from configuration file {2}).";

            LOG.info( LogMessage.getMessage( message, DEFAULT_CONFIGURATION_PATH_KEY, defaultPath,
                configurationPath ) );

            return defaultPath;
        }

        return configurationPath;
    }

}
