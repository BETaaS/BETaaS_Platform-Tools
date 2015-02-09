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
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.ogf.graap.wsag.api.Agreement;
import org.ogf.graap.wsag.api.AgreementFactory;
import org.ogf.graap.wsag.api.logging.LogMessage;
import org.ogf.graap.wsag.server.persistence.PersistedResourceException;
import org.ogf.graap.wsag.server.persistence.PersistentAgreement;

/**
 * @author owaeld
 * 
 */
public class DatabasePersistentAgreementFactory extends AbstractPersistentAgreementFactory
{

    private static final Logger LOG = Logger.getLogger( DatabasePersistentAgreementFactory.class );

    /**
     * Creates a new {@link DatabasePersistentAgreementFactory} using the given agreement factory as
     * delegation target for calls to the {@link AgreementFactory} interface.
     * 
     * @param factory
     *            the agreement factory
     */
    public DatabasePersistentAgreementFactory( AgreementFactory factory )
    {
        super( factory );
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.ogf.graap.wsag.server.persistence.impl.SimplePersistentAgreementFactory#createAgreement(org.ogf
     * .graap.wsag .api.AgreementOffer)
     */
    @Override
    public PersistentAgreement persistAgreement( Agreement agreement ) throws PersistedResourceException
    {
        try
        {
            synchronized ( agreement )
            {
                LOG.debug( "About to create persist agreement " + getResourceId() );
                DatabasePersistentAgreement inserted =
                    DatabasePersistentAgreement.insertAgreement( agreement, getResourceId() );
                return inserted;
            }
        }
        catch ( Exception e )
        {
            throw new PersistedResourceException( e );
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ogf.graap.wsag.server.persistence.PersistentAgreementFactory#load()
     */
    @Override
    public PersistentAgreement[] doLoad() throws PersistedResourceException
    {
        if ( LOG.isDebugEnabled() )
        {
            LOG.debug( LogMessage.getMessage(
                "Generate list of agreements created by agreement factory ''{0}''.", getResourceId() ) );
        }

        try
        {
        	
            List<PersistentAgreement> loaded = new Vector<PersistentAgreement>();
            
            PersistentAgreementContainer[] persistentAgreementContainers =
            		PersistentAgreementContainer.listContainers( getResourceId() );
            
            // extract all agreements
            for ( PersistentAgreementContainer persistentAgreementContainer : persistentAgreementContainers )
            {
                PersistentAgreement persistentAgreement =
                    new DatabasePersistentAgreement( persistentAgreementContainer,
                        persistentAgreementContainer.getAgreementFactoryId() );
                persistentAgreement.load();
                loaded.add( persistentAgreement );
            }

            if ( LOG.isDebugEnabled() )
            {
                LOG.debug( MessageFormat.format( "Loaded  {0} agreements for agreement factory ''{1}''",
                    loaded.size(), getResourceId() ) );
            }

            return loaded.toArray( new PersistentAgreement[loaded.size()] );
        }
        catch ( Exception e )
        {
            String message =
                MessageFormat.format( "Failed to generate agreement list for factory {0}.", getResourceId() );
            throw new PersistedResourceException( message, e );
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ogf.graap.wsag.server.persistence.IAgreementHome#remove(java.lang.String)
     */
    @Override
    public void doRemove( PersistentAgreement toRemove ) throws PersistedResourceException
    {
        try
        {
            toRemove.delete();
        }
        catch ( Exception e )
        {
            throw new PersistedResourceException( "Removing agreement failed.", e );
        }
    }

}
