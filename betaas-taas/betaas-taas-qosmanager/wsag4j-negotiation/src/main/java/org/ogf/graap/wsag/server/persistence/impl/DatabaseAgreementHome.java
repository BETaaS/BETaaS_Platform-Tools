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

import java.util.List;
import java.util.Vector;

import org.ogf.graap.wsag.server.persistence.IAgreementFactoryHome;
import org.ogf.graap.wsag.server.persistence.IAgreementHome;
import org.ogf.graap.wsag.server.persistence.PersistedResourceException;
import org.ogf.graap.wsag.server.persistence.PersistentAgreement;
import org.ogf.graap.wsag.server.persistence.PersistentAgreementFactory;

/**
 * Facade for all agreement factories known to the WSAG4J engine.
 * <p>
 * Database-based implementation of an agreement home. This implementation encapsulates the retrieval of
 * persisted agreements, the re-building of persisted agreements and the deletion of persisted agreements in
 * case of an agreement removal.
 * </p>
 * 
 * <p>
 * Persisted agreements are encapsulated and stored in {@link PersistentAgreementContainer} objects. These
 * objects store all agreement-related information and are used to re-build the original agreement. To access
 * the information stored in such a container, {@link DatabasePersistentAgreement} instances are used. This
 * class is able to provide a transparent access and processing of the required information.
 * </p>
 * 
 * @author T.Weuffel
 */

//
// TODO there shouldn't be a single facade for all agreement factories in the system,
// instead the home should be implemented by each factory on its own. Re-desing in v2.0.
//
public class DatabaseAgreementHome implements IAgreementHome
{

    private IAgreementFactoryHome factoryHome;

    /**
     * @param factoryHome
     *            the the factory home for resolving and removing the agreement instances.
     */
    public DatabaseAgreementHome( IAgreementFactoryHome factoryHome )
    {
        this.factoryHome = factoryHome;
    }

    /*
     * TODO the API would allow to resolve multiple agreements with the same id from different factories. This
     * issue should be solved in version 2.0 redesign.
     */
    /**
     * {@inheritDoc}
     */
    public PersistentAgreement find( String agreementId ) throws PersistedResourceException
    {
        try
        {
            PersistentAgreementFactory[] factories = factoryHome.list();
            for ( int i = 0; i < factories.length; i++ )
            {
                PersistentAgreement agreement = factories[i].find( agreementId );
                if ( agreement != null )
                {
                    return agreement;
                }
            }
        }
        catch ( Exception e )
        {
            throw new PersistedResourceException( e );
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public PersistentAgreement[] list() throws Exception
    {
        List<PersistentAgreement> result = new Vector<PersistentAgreement>();

        PersistentAgreementFactory[] factories = factoryHome.list();
        for ( int i = 0; i < factories.length; i++ )
        {
            PersistentAgreement[] agreements = factories[i].list();
            for ( int j = 0; j < agreements.length; j++ )
            {
                result.add( agreements[j] );
            }
        }
        return result.toArray( new PersistentAgreement[result.size()] );
    }

    /**
     * {@inheritDoc}
     */
    public PersistentAgreement[] list( String agreementFactoryId ) throws Exception
    {
        PersistentAgreementFactory[] factories = factoryHome.list();
        for ( int i = 0; i < factories.length; i++ )
        {
            if ( factories[i].getResourceId().equals( agreementFactoryId ) )
            {
                return factories[i].list();
            }
        }

        //
        // factory with resource id not found
        //
        return new PersistentAgreement[0];
    }

    /**
     * 
     * This method should not be used as this class is a facade.
     * 
     * {@inheritDoc}
     * 
     * @see IAgreementHome#remove(String)
     * 
     * @deprecated
     */
    public void remove( String agreementId ) throws PersistedResourceException
    {
        PersistentAgreementFactory[] factories = new PersistentAgreementFactory[0];

        try
        {
            factories = factoryHome.list();
        }
        catch ( Exception e )
        {
            throw new PersistedResourceException( e );
        }

        for ( int i = 0; i < factories.length; i++ )
        {
            factories[i].remove( agreementId );
        }
    }
}
