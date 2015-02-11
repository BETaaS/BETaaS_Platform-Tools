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
package org.ogf.graap.wsag.server.engine;

import java.io.InputStream;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.apache.xml.resolver.tools.CatalogResolver;
import org.apache.xmlbeans.XmlObject;
import org.ogf.graap.wsag.api.AgreementFactory;
import org.ogf.graap.wsag.api.WsagConstants;
import org.ogf.graap.wsag.api.logging.LogMessage;
import org.ogf.graap.wsag.server.api.IAgreementFactory;
import org.ogf.graap.wsag.server.api.impl.FactoryContext;
import org.ogf.graap.wsag.server.api.impl.AgreementFactoryFacade;
import org.ogf.graap.wsag.server.persistence.EmfRegistry;
import org.ogf.graap.wsag.server.persistence.IAgreementFactoryHome;
import org.ogf.graap.wsag.server.persistence.PersistentAgreementFactory;
import org.ogf.graap.wsag.server.persistence.impl.AbstractWSAG4JPersistence;
import org.ogf.graap.wsag4j.types.configuration.FactoryConfigurationType;
import org.ogf.graap.wsag4j.types.configuration.ImplementationConfigurationType;
import org.ogf.graap.wsag4j.types.configuration.WSAG4JEngineConfigurationDocument;
import org.ogf.graap.wsag4j.types.configuration.WSAG4JEngineConfigurationType;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;

/**
 * The WSAGEngine provides access to all agreement factories configured for this engine. The available
 * factories can be retrieved via the agreement factory home interface. The agreement factory home is
 * retrieved by the {@link #getAgreementFactoryHome()} method. Before the engine can be used it must be
 * initialized. Engine initialization is triggered by the {@link #initializeEngine(String)} method.
 * 
 * @author Oliver Waeldrich
 * 
 */
public class WsagEngine
    implements IAgreementFactoryHome
{

    private static final Logger LOG = Logger.getLogger( WsagEngine.class );

    /**
     * Configuration of this engine instance.
     */
    private WSAG4JEngineConfigurationType engineConfiguration = null;

    /**
     * The persistence layer configured for this instance.
     */
    private AbstractWSAG4JPersistence persistenceLayer;

    /**
     * A map that contains the available persistent agreement factory which are identified by their factory
     * id.
     */
    protected Map<String, PersistentAgreementFactory> persistentFactories =
        new HashMap<String, PersistentAgreementFactory>();

    /**
     * An ordered list with the available agreement factories. The factories are stored in the order they were
     * retrieved from the persistence layer.
     */
    protected List<PersistentAgreementFactory> factoriesOL = new Vector<PersistentAgreementFactory>();

    /**
     * Returns a new WSAG engine instance.
     * 
     * @param configuration
     *            engine configuration as input stream
     * 
     * @return the new created engine instance
     * 
     * @throws EngineInstantiationException
     *             failed to instantiate the engine
     */
    public static WsagEngine getInstance( InputStream configuration ) throws EngineInstantiationException
    {
        WSAG4JEngineConfigurationDocument config = null;
        try
        {
            config = (WSAG4JEngineConfigurationDocument) XmlObject.Factory.parse( configuration );
        }
        catch ( Exception e )
        {
            String message =
                MessageFormat.format( "Failed to read WSAG4J engine configuration. Reason: {0}",
                    e.getMessage() );
            throw new EngineInstantiationException( message, e );
        }

        return getInstance( config.getWSAG4JEngineConfiguration() );
    }

    /**
     * Returns a new WSAG engine instance.
     * 
     * @param configFile
     *            engine configuration as file name (must be available in the class path)
     * 
     * @return the new created engine instance
     * 
     * @throws EngineInstantiationException
     *             failed to instantiate the engine
     */
    public static WsagEngine getInstance( String configFile ) throws EngineInstantiationException
    {
        InputStream in = WsagEngine.class.getResourceAsStream( configFile );
        return getInstance( in );
    }

    /**
     * Returns a new WSAG engine instance.
     * 
     * @param engineConfiguration
     *            engine configuration
     * 
     * @return the new created engine instance
     * 
     * @throws EngineInstantiationException
     *             failed to instantiate the engine
     */
    public static WsagEngine getInstance( WSAG4JEngineConfigurationType engineConfiguration )
        throws EngineInstantiationException
    {

        if ( engineConfiguration == null )
        {
            throw new EngineInstantiationException( "WSAG4J engine configuration must not be null." );
        }

        try
        {
            WsagEngine wsagEngine = new WsagEngine( engineConfiguration );
            wsagEngine.initialize();

            return wsagEngine;
        }
        catch ( Exception e )
        {
            String message =
                MessageFormat.format( "failed to instantiate WSAG4J engine. Reason: {0}", e.getMessage() );
            throw new EngineInstantiationException( message, e );
        }
    }

    /**
     * Creates a new WSAG4J engine instance.
     * 
     * @param engineConfiguration
     *            engine configuration file
     * 
     */
    private WsagEngine( WSAG4JEngineConfigurationType engineConfiguration )
    {
        this.engineConfiguration = engineConfiguration;
    }

    /**
     * looks up a factory with the given id.
     * 
     * @param factoryId
     *            the id of the factory to find
     * 
     * @return the factory
     * 
     * @throws Exception
     *             an error occurred while looking up the factory.
     * 
     * @see IAgreementFactoryHome#find(String)
     */
    public PersistentAgreementFactory find( String factoryId ) throws Exception
    {
        if ( persistentFactories.containsKey( factoryId ) )
        {
            return persistentFactories.get( factoryId );
        }

        return null;
    }

    /**
     * Instantiates a new agreement factory instance based on the engine configuration.
     * 
     * @param configuration
     *            Configuration of the wsag4j engine.
     * 
     * @return Agreement factory prototype based on the wsag4j configuration.
     * 
     * @throws Exception
     *             failed to initialize the configured factory
     */
    public IAgreementFactory getAgreementFactoryPrototype() throws Exception
    {
        String implementationClass = null;

        try
        {
            implementationClass =
                engineConfiguration.getFactory().getFactoryImplementation().getImplementationClass();

            if ( implementationClass == null )
            {
                throw new Exception();
            }
        }
        catch ( Exception ex )
        {
            String message =
                "Error in WSAG4J configuration: "
                    + "Could not load agreement factory implementation from configuration file.";
            throw new Exception( message, ex );
        }

        try
        {

            Object instance = Class.forName( implementationClass ).newInstance();

            if ( instance instanceof AgreementFactory )
            {
                AgreementFactory impl = (AgreementFactory) instance;

                IAgreementFactory factory = null;

                //
                // if the implementation is not an instance of
                // IWSAG4JAgreementFactory, return a facade for
                // the implementation.
                //
                if ( impl instanceof IAgreementFactory )
                {
                    factory = (IAgreementFactory) impl;
                }
                else
                {
                    factory = new AgreementFactoryFacade( impl );
                }

                //
                // explicitly initialize the factory context
                // and set the engine configuration
                //
                factory.setFactoryContext( new FactoryContext( factory ) );

                factory.initialize( this );

                //
                // also populate the factory context with the factory
                // configuration, so that action implementations may
                // look up the factory configuration
                //
                factory.getFactoryContext().put( WsagConstants.WSAG4J_FACTORY_CONFIGURATION,
                    engineConfiguration );

                return factory;

            }
            else
            {
                Object[] filler = new Object[] { implementationClass, IAgreementFactoryHome.class.getName() };
                String msgLoadPersistenceLayerError =
                    "Error loading WSAG4J persistence layer. Class [{0}] does not implement interface [{1}].";
                String message = MessageFormat.format( msgLoadPersistenceLayerError, filler );
                throw new Exception( message );
            }

        }
        catch ( ClassNotFoundException e )
        {
            String message =
                MessageFormat.format(
                    "Error loading WSAG4J persistence layer. Class [{0}] not found. Error: {1}",
                    implementationClass, e.getMessage() );
            throw new Exception( message );
        }
        catch ( InstantiationException e )
        {
            String message =
                MessageFormat.format( "Error loading WSAG4J persistence layer. "
                    + "Class [{0}] could not be instantiated. Error: {1}", implementationClass,
                    e.getMessage() );
            throw new Exception( message );
        }
        catch ( IllegalAccessException e )
        {
            String message =
                MessageFormat.format( "Error loading WSAG4J persistence layer. "
                    + "Class [{0}] could not be accessed. Error: {1}", implementationClass, e.getMessage() );
            throw new Exception( message );
        }
    }

    /**
     * Returns the configuration for this engine instance.
     * 
     * @return the wsag4jConfiguration
     */
    public WSAG4JEngineConfigurationType getConfiguration()
    {
        return engineConfiguration;
    }

    /**
     * Returns the resource id configured for this engine instance or the a generated UUID if not set.
     * 
     * @return the default resource id
     */
    public String getDefaultResourceId()
    {
        if ( getConfiguration().isSetResourceId() )
        {
            return getConfiguration().getResourceId();
        }
        else
        {
            LOG.error( "agreement factory id not set in configuration file" );
            LOG.error( "generate random agreement factory id " );
            LOG.error( "agreement persistence will be disabled" );
            return UUID.randomUUID().toString();
        }
    }

    /**
     * Returns the name of the persistence layer implementation class from the engine configuration.
     * 
     * @param configuration
     *            the engine configuration
     * 
     * @return the name of the persistence layer implementation class
     * 
     * @throws Exception
     *             failed to resolve the implementation class
     */
    private String getPersistenceImplementationClass( WSAG4JEngineConfigurationType configuration )
        throws Exception
    {
        String implementationClass;
        try
        {
            FactoryConfigurationType factoryConfig = configuration.getFactory();
            ImplementationConfigurationType persistenceConfig = factoryConfig.getPersistenceImplementation();
            implementationClass = persistenceConfig.getImplementationClass();

            LOG.debug( LogMessage.getMessage( "Create ''{0}'' instance as persistence layer.",
                implementationClass ) );
        }
        catch ( Exception e )
        {
            throw new Exception( "failed to read WSAG4J engine configuration", e );
        }
        return implementationClass;
    }

    /**
     * Returns the persistence layer for this engine.
     * 
     * @return the persistence layer for this engine
     */
    public IAgreementFactoryHome getPersistenceLayer()
    {
        return persistenceLayer;
    }

    /**
     * Initializes the WSAG4J Engine with the local configuration.
     * 
     * @throws Exception
     *             indicates an error during engine initialization
     */
    public void initialize() throws Exception  // Make it private CV
    {
        try
        {
            LOG.info( "start initialization process for new WSAG4J engine instance" );

            //
            // set the CatalogResolver as default entity resolver for XmlBeans
            //
            System.setProperty( "xmlbean.entityResolver", CatalogResolver.class.getName() );

            LOG.info( "load WSAG4J engine configuration" );

            LOG.info( "initialize WSAG4J persistence layer" );
            initializePersistenceLayer();

            LOG.info( "WSAG4J engine initialized successfully" );
        }
        catch ( Exception e )
        {
            throw new Exception( "failed to initialize WSAG4J engine", e );
        }
    }

    /**
     * Initializes the WSAG4J persistence layer.
     */
    private void initializePersistenceLayer() throws Exception
    {

        try
        {
            LOG.info( "WsagEngine -> initialize PersistenceLayer" );

            // build and initialise agreement factory home
            try
            {
                persistenceLayer = loadPersistenceLayer( engineConfiguration );
                persistenceLayer.initialize( this );

                PersistentAgreementFactory[] factories = getPersistenceLayer().list();
                for ( int j = 0; j < factories.length; j++ )
                {
                    if ( persistentFactories.containsKey( factories[j].getResourceId() ) )
                    {
                        String message1 =
                            "[duplicated resource id] "
                                + "the agreement factory resource id must be unique in a WSAG4J engine.";
                        LOG.error( message1 );

                        String message2 =
                            "[duplicated resource id] the factory with resource id ''{0}'' was not loaded.";
                        LOG.error( MessageFormat.format( message2,
                            new Object[] { factories[j].getResourceId() } ) );
                    }
                    else
                    {
                        persistentFactories.put( factories[j].getResourceId(), factories[j] );
                        factoriesOL.add( factories[j] );
                    }
                }
            }
            catch ( Exception e )
            {
                LOG.error( "error loading persistence layer", e );
            }

            LOG.info( "WsagEngine -> Persistence Layer initialized" );
        }
        catch ( Exception e )
        {
            LOG.error( "WsagEngine -> failed to initialize Persistence Layer", e );
            throw new Exception( "Failed to initialize persistence layer.", e );
        }
    }

    /**
     * @return all available persistent factories
     * 
     * @throws Exception
     *             indicates an error while generating the factory list
     * 
     * @see org.ogf.graap.wsag.server.persistence.IAgreementFactoryHome#list()
     */
    public PersistentAgreementFactory[] list() throws Exception
    {
        return factoriesOL.toArray( new PersistentAgreementFactory[persistentFactories.size()] );
    }

    private AbstractWSAG4JPersistence loadPersistenceLayer( WSAG4JEngineConfigurationType configuration )
        throws Exception
    {
        String implementationClass = getPersistenceImplementationClass( configuration );

        try
        {
            Object instance = Class.forName( implementationClass ).newInstance();

            if ( instance instanceof AbstractWSAG4JPersistence )
            {
                LOG.debug( "Persistence layer instance created and loaded." );
                return (AbstractWSAG4JPersistence) instance;
            }
            else
            {
                String text =
                    "Error loading WSAG4J persistence layer. Class {0} does not implement interface {1}.";
                String message =
                    MessageFormat.format( text, implementationClass, IAgreementFactoryHome.class.getName() );

                throw new Exception( message );
            }
        }
        catch ( ClassNotFoundException e )
        {
            String text = "Error loading WSAG4J persistence layer. Class [{0}] not found. Error: {1}";
            String message = MessageFormat.format( text, implementationClass, e.getMessage() );

            throw new Exception( message );
        }
        catch ( InstantiationException e )
        {
            throw new Exception( MessageFormat.format(
                "Error loading WSAG4J persistence layer. Class [{0}] could not be instantiated. Error: {1}",
                implementationClass, e.getMessage() ) );
        }
        catch ( IllegalAccessException e )
        {
            throw new Exception( MessageFormat.format(
                "Error loading WSAG4J persistence layer. Class [{0}] could not be accessed. Error: {1}",
                implementationClass, e.getMessage() ) );
        }
    }

    /**
     * @param factoryId
     *            removes the factory with the given id from the persistence layer
     * 
     * @throws Exception
     *             an error occurred while removing the factory
     * 
     * @see org.ogf.graap.wsag.server.persistence.IAgreementFactoryHome#remove(java.lang.String)
     */
    public void remove( String factoryId ) throws Exception
    {
        if ( persistentFactories.containsKey( factoryId ) )
        {
            factoriesOL.remove( persistentFactories.get( factoryId ) );
            persistentFactories.remove( factoryId );
        }
    }

    /**
     * Saves all factories.
     * 
     * @throws Exception
     *             indicates an error while saving the factories
     */
    public void save() throws Exception
    {
        PersistentAgreementFactory[] factories = list();
        for ( int i = 0; i < factories.length; i++ )
        {
            factories[i].save();
        }
    }

    /**
     * @param factories
     *            the factories to save
     * 
     * @throws Exception
     *             indicates an error while saving the factories
     * 
     * @see org.ogf.graap.wsag.server.persistence.IAgreementFactoryHome#saveAgreementFactories(org.ogf.graap.wsag.server.persistence.PersistentAgreementFactory[])
     */
    public void saveAgreementFactories( PersistentAgreementFactory[] factories ) throws Exception
    {
        for ( int i = 0; i < factories.length; i++ )
        {
            factories[i].save();
        }
    }

    /**
     * Shutdown of the WSAG4J engine instance.
     * 
     * @throws Exception
     *             indicates an error during engine shutdown
     */
    public void shutdown() throws Exception
    {
        LOG.info( "start shutdown process for WSAG4J engine instance" );

        try
        {
            LOG.info( "shutdown Quarz scheduler" );

            SchedulerFactory factory = new StdSchedulerFactory();
            Scheduler scheduler = factory.getScheduler();

            if ( scheduler.isStarted() )
            {
                scheduler.shutdown();
            }
        }
        catch ( SchedulerException e )
        {
            LOG.error( "Failed to shutdown quartz scheduler.", e );
        }

        LOG.info( "shutdown WSAG4J engine persistence layer" );
        shutdownPersistenceLayer();

        LOG.info( "shutdown of WSAG4J engine persistence layer completed" );

        LOG.info( "WSAG4J engine shutdown completed" );
    }

    /**
     * Shutdown the persistence layer and persist all agreements that are currently managed by the
     * implementation.
     * 
     * TODO: Save current agreement states.
     */
    private void shutdownPersistenceLayer() throws Exception
    {
    	   	
        // load all agreements
        PersistentAgreementFactory[] factories = getPersistenceLayer().list();
        
        for ( int i = 0; i < factories.length; i++ )
        {
            try
            {
                final String msgDoSave = "Save agreement factory ''{0}''.";
                LOG.debug( LogMessage.getMessage( msgDoSave, factories[i].getResourceId() ) );

                factories[i].save();

                final String msgSaved = "Agreement factory ''{0}'' saved.";
                LOG.debug( LogMessage.getMessage( msgSaved, factories[i].getResourceId() ) );
            }
            catch ( Exception e )
            {
                String message = "Failed to save agreement factory ''{0}''.";
                LOG.error( MessageFormat.format( message, new Object[] { factories[i].getResourceId() } ), e );
            }
        }

        // remove references to agreement (factory) home
        persistentFactories.clear();
        factoriesOL.clear();

        persistenceLayer = null;

        // close the entity manager factory
        EmfRegistry.finalizeEmfRegistry();
    }

}
