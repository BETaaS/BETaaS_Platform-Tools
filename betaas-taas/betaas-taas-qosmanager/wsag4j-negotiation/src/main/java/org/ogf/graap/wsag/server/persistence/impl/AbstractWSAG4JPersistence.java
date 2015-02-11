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
package org.ogf.graap.wsag.server.persistence.impl;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.ogf.graap.wsag.server.api.EngineComponent;
import org.ogf.graap.wsag.server.persistence.IAgreementFactoryHome;
import org.ogf.graap.wsag.server.persistence.PersistedResourceException;
import org.ogf.graap.wsag.server.persistence.PersistentAgreementFactory;

/**
 * <p>
 * Abstract implementation of an agreement factory home. This implementation provides global
 * configuration-related functions as well as it is responsible to find and load the agreement factory
 * prototype (based on the configuration).
 * </p>
 * 
 * <p>
 * All implementations of the {@link IAgreementFactoryHome} interface should extend this abstract
 * implementation.
 * </p>
 * 
 * @author Oliver Waeldrich
 * 
 */
public abstract class AbstractWSAG4JPersistence extends EngineComponent
    implements IAgreementFactoryHome
{

    // //////////////////////////////////////////////////////////////////////////////////
    // ////// variable definition section ////////
    // //////////////////////////////////////////////////////////////////////////////////

    private static final Logger LOG = Logger.getLogger( AbstractWSAG4JPersistence.class );

    /**
     * indicates that the persistence layer has changed and a reload is required.
     */
    private boolean changed = false;

    /**
     * stores the persistent factories of this layer
     */
    private final Vector<PersistentAgreementFactory> persistentFactories =
        new Vector<PersistentAgreementFactory>();

    /**
     * stores the known factories by their ids for fast factory lookup
     */
    private final Map<String, PersistentAgreementFactory> factoriesById =
        new HashMap<String, PersistentAgreementFactory>();

    // //////////////////////////////////////////////////////////////////////////////////
    // ////// abstract method declaration ////////
    // //////////////////////////////////////////////////////////////////////////////////

    /**
     * Loads the agreement factories for this persistence layer. The load method is invoked during the
     * initialization process of the persistence layer (see {@link #initialize()}) or during a
     * {@link #find(String)}, {@link #list()} or {@link #remove(String)} operation
     * 
     * 
     * @return the loaded factories
     * 
     * @throws PersistedResourceException
     *             indicates an error while loading the persistent factories
     */
    protected abstract PersistentAgreementFactory[] doLoad() throws PersistedResourceException;

    /**
     * Removes a persistent factory from the persistence layer.
     * 
     * @param factory
     *            the factory to remove
     * 
     * @return <code>true</code> if the factory was removed, otherwise <code>false</code>
     * 
     * @throws PersistedResourceException
     *             indicates an error while removing the factory
     */
    protected abstract boolean doRemove( PersistentAgreementFactory factory )
        throws PersistedResourceException;

    // //////////////////////////////////////////////////////////////////////////////////
    // ////// getter/setter methods ////////
    // //////////////////////////////////////////////////////////////////////////////////

    /**
     * Indicates that the persistence layer has changed and needs to be re-initialized (see
     * {@link #initialize()}).
     * 
     * @return <code>true</code> if the persistence layer needs to be reloaded, otherwise <code>false</code>.
     */
    public boolean hasChanged()
    {
        return changed;
    }

    /**
     * Sets the flag to indicate that the persistence layer has changed and needs to be re-initialized (see
     * {@link #initialize()}).
     * 
     * @param changed
     *            <code>true</code> if the persistence layer needs to be reloaded, otherwise
     *            <code>false</code>
     */
    public void setChanged( boolean changed )
    {
        this.changed = changed;
    }

    // //////////////////////////////////////////////////////////////////////////////////
    // ////// method implementations ////////
    // //////////////////////////////////////////////////////////////////////////////////

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doInitialize() throws Exception
    {
        synchronized ( this )
        {

            try
            {
                persistentFactories.clear();
                factoriesById.clear();
                
                PersistentAgreementFactory[] factories = doLoad();

                for ( int i = 0; i < factories.length; i++ )
                {
                    persistentFactories.add( factories[i] );
                    factoriesById.put( factories[i].getResourceId(), factories[i] );
                }

                setChanged( false );
            }
            catch ( Exception e )
            {
                String message =
                    MessageFormat.format(
                        "Could not load WSAG4J factory instance. Ignoring this instance. Error: {0}",
                        e.getMessage() );
                LOG.error( message, e );
            }

        }
    }

    /**
     * Lists the agreement for the factory with the given id.
     * 
     * @param agreementFactoryId
     *            the id of the factory for which the agreements are listed
     * 
     * @return the factory with the given id
     * 
     * @throws Exception
     *             indicates an error while looking up the factory at the persistence layer
     */
    @Override
    public PersistentAgreementFactory find( String agreementFactoryId ) throws Exception
    {
        if ( hasChanged() )
        {
            doInitialize();
        }

        PersistentAgreementFactory persistentAgreementFactory = null;

        // check if the factory is known/available
        if ( factoriesById.containsKey( agreementFactoryId ) )
        {
            persistentAgreementFactory = factoriesById.get( agreementFactoryId );
        }
        else
        {
            throw new Exception( MessageFormat.format( "No agreement factory with id ''{0}'' found.",
                agreementFactoryId ) );
        }

        return persistentAgreementFactory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PersistentAgreementFactory[] list() throws Exception
    {

        if ( hasChanged() )
        {
            doInitialize();
        }

        return persistentFactories.toArray( new PersistentAgreementFactory[persistentFactories.size()] );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void remove( String factoryId ) throws Exception
    {

        if ( hasChanged() )
        {
            doInitialize();
        }

        if ( factoriesById.containsKey( factoryId ) )
        {
            PersistentAgreementFactory factory = factoriesById.get( factoryId );

            if ( doRemove( factory ) )
            {
                persistentFactories.remove( factory );
                factoriesById.remove( factoryId );
            }
        }
    }
}
