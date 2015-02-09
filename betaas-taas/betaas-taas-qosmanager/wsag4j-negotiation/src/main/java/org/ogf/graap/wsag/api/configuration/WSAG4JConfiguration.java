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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Properties;

import org.apache.commons.discovery.log.SimpleLog;
import org.apache.commons.discovery.resource.ClassLoaders;
import org.apache.commons.discovery.tools.DefaultClassHolder;
import org.apache.commons.discovery.tools.DiscoverClass;
import org.apache.commons.discovery.tools.DiscoverSingleton;
import org.apache.commons.discovery.tools.PropertiesHolder;
import org.apache.commons.discovery.tools.ResourceUtils;
import org.apache.commons.discovery.tools.SPInterface;
import org.apache.log4j.Logger;
import org.ogf.graap.wsag.api.logging.LogMessage;

/**
 * MSSConfiguration This class provides basic configuration mechanisms for WSAG4J components.
 * 
 * @author Oliver Waeldrich
 */
public class WSAG4JConfiguration
{

    private static final Logger LOG = Logger.getLogger( WSAG4JConfiguration.class );

    private static WSAG4JConfigurationEnvironment instance = null;

    /*
     * convenience methods
     */
    private static synchronized WSAG4JConfigurationEnvironment getEnvironment()
    {

        SimpleLog.setLevel( SimpleLog.LOG_LEVEL_INFO );

        if ( instance == null )
        {

            SPInterface mssConfigSP = getWSAG4JSPI();
            PropertiesHolder pHolder = new PropertiesHolder( WSAG4JEnvironment.DEFAULT_CONFIGURATION_FILE );
            DefaultClassHolder cHolder = new DefaultClassHolder( WSAG4JEnvironment.class.getName() );

            instance =
                (WSAG4JConfigurationEnvironment) DiscoverSingleton.find( null, mssConfigSP, pHolder, cHolder );
        }

        return instance;
    }

    private static SPInterface getWSAG4JSPI()
    {
        // get the library classloaders for discovery
        ClassLoaders loaders =
            ClassLoaders.getLibLoaders( WSAG4JConfigurationEnvironment.class, DiscoverClass.class, true );

        // load the properties file via the classloaders
        Properties properties =
            ResourceUtils.loadProperties( WSAG4JConfigurationEnvironment.class,
                WSAG4JEnvironment.DEFAULT_CONFIGURATION_FILE, loaders );

        if ( properties == null )
        {
            properties = new Properties();
        }

        // find the path of the application configuration
        String cPath =
            properties.getProperty( WSAG4JEnvironment.DEFAULT_CONFIGURATION_PATH_KEY,
                WSAG4JEnvironment.DEFAULT_CONFIGURATION_PATH );

        // instantiate new SPI with the configuration path
        // the configuration path is passed to the MSSConfigurationEnvironment
        // class on instantiation of the class by the discovery framework
        SPInterface mssConfigSP =
            new SPInterface( WSAG4JConfigurationEnvironment.class, new Class[] { String.class },
                new Object[] { cPath } );

        return mssConfigSP;
    }

    /**
     * @return the configuration path for this module
     */
    public static String getConfigurationPath()
    {
        return getEnvironment().getConfigurationPath();
    }

    /**
     * Finds an implementation for a given interface.
     * 
     * @param interfaceDef
     *            the interface definition to find an implementation for
     * @param configFile
     *            the configuration file that specifies the appropriate implementation
     * @param defaultImpl
     *            the default implementation to use
     * @return The instance of the implementation class
     */
    public static Object findImplementation( Class<?> interfaceDef, String configFile, String defaultImpl )
    {
        DiscoverClass discovery = new DiscoverClass();
        try
        {
            try
            {
                Properties properties = new Properties();

                InputStream resource = findResource( configFile );
                properties.load( resource );

                Class<?> theClass = discovery.find( interfaceDef, properties, defaultImpl );

                return theClass.newInstance();
            }
            catch ( IOException e )
            {
                String message = "Could not load resource {0}. Try default discovery.";
                LOG.debug( LogMessage.getMessage( message, configFile ) );

                Class<?> theClass = discovery.find( interfaceDef, configFile, defaultImpl );

                return theClass.newInstance();
            }
        }
        catch ( InstantiationException e )
        {
            final String message = "Could not instantiate class for interface {0}.";
            LOG.debug( LogMessage.getMessage( message, interfaceDef.getName() ), e );
        }
        catch ( IllegalAccessException e )
        {
            final String message = "Could not instantiate class for interface {0}.";
            LOG.debug( LogMessage.getMessage( message, interfaceDef.getName() ), e );
        }

        return null;
    }

    /**
     * Finds a singleton implementation for a given interface.
     * 
     * @param interfaceDef
     *            the interface definition to find an implementation for
     * @param configFile
     *            the configuration file that specifies the appropriate implementation
     * @param defaultImpl
     *            the default implementation to use
     * @return The instance of the implementation class
     */
    public static Object findSingeltonImplementation( Class<?> interfaceDef, String configFile,
                                                      String defaultImpl )
    {
        try
        {
            Properties properties = new Properties();

            InputStream resource = findResource( configFile );
            properties.load( resource );

            return DiscoverSingleton.find( interfaceDef, properties, defaultImpl );
        }
        catch ( IOException e )
        {
            final String message = "Could not load resource {0}. Try default discovery.";
            LOG.debug( LogMessage.getMessage( message, configFile ) );
            LOG.trace( e );

            return DiscoverSingleton.find( interfaceDef, configFile, defaultImpl );
        }
    }

    /**
     * @param resourceName
     *            The name of the requested resource.
     * @return The requested resource as <code>InputStream</code>.
     * @throws IOException
     *             there was an error reading the resource
     */
    public static InputStream findResource( String resourceName ) throws IOException
    {
        return findResource( getEnvironment().getConfigurationPath(), resourceName );
    }

    /**
     * @param path
     *            The path to the requested resource. If the resource is not found at the specified location
     *            discovery will be performed.
     * @param resourceName
     *            The name of the requested resource.
     * @return The requested resource as an InputStream.
     * @throws IOException
     *             there was an error reading the resource
     */
    public static InputStream findResource( String path, String resourceName ) throws IOException
    {
        return findResourceURL( path, resourceName ).openStream();
    }

    /**
     * @param resourceName
     *            The name of the requested resource.
     * @return The URL of the requested resource.
     * @throws IOException
     *             there was an error reading the resource
     */
    public static URL findResourceURL( String resourceName ) throws IOException
    {
        return findResourceURL( getEnvironment().getConfigurationPath(), resourceName );
    }

    /**
     * @param path
     *            The path to the requested resource. If the resource is not found at the specified location
     *            discovery will be performed.
     * @param resourceName
     *            The name of the requested resource.
     * @return The URL of the requested resource.
     * @throws IOException
     *             there was an error reading the resource
     */
    public static URL findResourceURL( String path, String resourceName ) throws IOException
    {
        if ( path == null )
        {
            path = "";
        }

        if ( resourceName == null )
        {
            throw new IOException( "Could not find resource. No resource name specified (null)." );
        }

        File file = null;

        // first try to find the resource in the specified directory
        // global configuration files should be kept outside the application
        // in order to easy update the application
        file = new File( path, resourceName );
        if ( file.exists() )
        {
            try
            {
                LogMessage message =
                    LogMessage.getMessage( "Found resource {0} in directory {1}.", resourceName, path );

                if ( LOG.isTraceEnabled() )
                {
                    LOG.trace( message );
                }

                return file.toURI().toURL();
            }
            catch ( IOException e )
            {
                LogMessage message = LogMessage.getMessage( "Could not read resource {0}.", resourceName );
                LOG.error( message, e );
            }
        }

        // then try to load the resource by its absolute name
        file = new File( resourceName );
        if ( file.exists() )
        {
            try
            {
                LOG.trace( LogMessage.getMessage( "Found resource [{0}].", new Object[] { resourceName } ) );

                return file.toURI().toURL();
            }
            catch ( IOException e )
            {
                LOG.error(
                    MessageFormat.format( "Could not read resource {0}.", new Object[] { resourceName } ), e );
            }
        }

        // then by its fully qualified resource name
        String qualifiedResourceName = path + System.getProperty( "file.separator" ) + resourceName;

        // special case for empty path on Windows systems
        if ( path.equals( "" ) )
        {
            qualifiedResourceName = "/" + resourceName;
        }

        URL resourceUrl = WSAG4JConfiguration.class.getResource( qualifiedResourceName );
        if ( resourceUrl != null )
        {
            LOG.trace( LogMessage.getMessage( "Found resource {0} by the classloader [external name: {1}]",
                qualifiedResourceName, resourceUrl.toExternalForm() ) );

            return resourceUrl;
        }

        // last try to find the resource by the classloader
        // this is the default behavior
        if ( resourceName.startsWith( "/" ) || resourceName.startsWith( "\\" ) )
        {
            resourceUrl = WSAG4JConfiguration.class.getResource( resourceName );
        }
        else
        {
            // we don't want to load the resource relative from MSSConfiguration
            resourceUrl = WSAG4JConfiguration.class.getResource( "/" + resourceName );
        }

        if ( resourceUrl != null )
        {
            LOG.trace( LogMessage.getMessage( "Found resource {0} by the classloader [external name: {1}]",
                resourceName, resourceUrl.toExternalForm() ) );

            return resourceUrl;
        }

        LOG.trace( LogMessage.getMessage( "The resource {0} was not found.", resourceName ) );
        LOG.trace( LogMessage.getMessage( "Tried the following directories: [{0}] [{1}]", path,
            System.getProperty( "java.class.path" ) ) );

        throw new FileNotFoundException( LogMessage.format(
            "The resource [{0}] was not found at the system.", resourceName ) );
    }

}
