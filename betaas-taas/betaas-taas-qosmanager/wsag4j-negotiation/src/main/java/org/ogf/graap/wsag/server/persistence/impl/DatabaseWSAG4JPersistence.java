/* 
 * Copyright (c) 2005-2011, Fraunhofer-Gesellschaft
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

import org.apache.log4j.Logger;
import org.ogf.graap.wsag.api.logging.LogMessage;
import org.ogf.graap.wsag.server.api.IAgreementFactory;
import org.ogf.graap.wsag.server.persistence.PersistedResourceException;
import org.ogf.graap.wsag.server.persistence.PersistentAgreement;
import org.ogf.graap.wsag.server.persistence.PersistentAgreementFactory;

/**
 * @author T.Weuffel
 */
public class DatabaseWSAG4JPersistence extends AbstractWSAG4JPersistence
{

    private static final Logger LOG = Logger.getLogger( DatabaseWSAG4JPersistence.class );

    /*
     * (non-Javadoc)
     * 
     * @see org.ogf.graap.wsag.server.persistence.impl.AbstractWSAG4JPersistence#doLoad()
     */
    @Override
    protected PersistentAgreementFactory[] doLoad() throws PersistedResourceException
    {

        LOG.debug( "Loading DatabaseWSAG4JPersistence instance" );

        AbstractPersistentAgreementFactory persistentFactory = null;

        //
        // load the implementation specified in the wsag4j.properties file here
        // initialize it, and if feasible, set the configuration data
        //

        //
        // get the default engine resource id
        //
        String resourceId = getEngine().getDefaultResourceId();
        try
        {
            //
            // initialize the factory prototype based on the configuration
            //
            IAgreementFactory factory = getEngine().getAgreementFactoryPrototype();
            
            //
            // create the persistent factory
            //
            persistentFactory = new DatabasePersistentAgreementFactory( factory );
            persistentFactory.setResourceId( resourceId );
            
            //
            // load the active agreements for this factory
            //
            persistentFactory.load();
            LOG.debug( LogMessage.getMessage( "wsag4jConfiguration.getResourceId(): {0}", resourceId ) );
        }
        catch ( Exception ex )
        {
            String message =
                MessageFormat.format(
                    "Could not load WSAG4J factory instance ''{0}''. Ignoring this instance.", resourceId );
            throw new PersistedResourceException( message, ex );
        }

        if ( LOG.isDebugEnabled() )
        {
            LOG.debug( "DatabaseWSAG4JPersistence initialized." );
        }

        return new PersistentAgreementFactory[] { persistentFactory };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean doRemove( PersistentAgreementFactory factory ) throws PersistedResourceException
    {
        throw new PersistedResourceException( "Operation not supported for WSAG4J database persistence." );
    }

    /**
     * {@inheritDoc}
     * 
     * @deprecated
     */
    @Deprecated
    public void saveAgreementFactories( PersistentAgreementFactory[] factories ) throws Exception
    {
        if ( LOG.isDebugEnabled() )
        {
            LOG.debug( LogMessage.getMessage(
                "Try to save all agreements for the {0} specified agreement factories.", factories.length ) );
        }

        //
        // iterate over all agreement factories and try to persist the separate agreements (per factory)
        //
        // TODO: use agreement home to list existing agreements for a factory
        //
        for ( PersistentAgreementFactory persistentAgreementFactory : factories )
        {
            for ( PersistentAgreement persistentAgreement : persistentAgreementFactory.list() )
            {
                try
                {
                    // delegate the save operation to the agreement itself
                    persistentAgreement.save();
                }
                catch ( Exception ex )
                {
                    LOG.error( LogMessage.getMessage(
                        "Could not save agreement ''{0}'' for agreement factory ''{1}''.",
                        persistentAgreement, persistentAgreementFactory.getResourceId() ), ex );
                }
            }
        }

        if ( LOG.isDebugEnabled() )
        {
            LOG.debug( "Saved all agreements." );
        }
    }

}
