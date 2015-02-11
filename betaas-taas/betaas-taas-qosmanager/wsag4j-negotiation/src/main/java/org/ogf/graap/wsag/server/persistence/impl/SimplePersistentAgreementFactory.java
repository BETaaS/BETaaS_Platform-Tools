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

import java.util.UUID;

import org.apache.log4j.Logger;
import org.ogf.graap.wsag.api.Agreement;
import org.ogf.graap.wsag.api.AgreementFactory;
import org.ogf.graap.wsag.server.persistence.PersistedResourceException;
import org.ogf.graap.wsag.server.persistence.PersistentAgreement;
import org.ogf.graap.wsag.server.persistence.PersistentAgreementFactory;

/**
 * SimplePersistentAgreementFactory. Agreement factories are persisted by the WSAG4J engine configuration
 * files. Each engine instance is treated as an agreement factory configuration. Therefore, for each engine
 * instance one {@link SimplePersistentAgreementFactory} instance is created and initialized.
 * 
 * {@link SimplePersistentAgreementFactory} is an adapter between the {@link PersistentAgreementFactory} and
 * the {@link AgreementFactory} interfaces (between persistence layer and factory implementation).
 * 
 * @author Oliver Waeldrich
 * 
 */
public class SimplePersistentAgreementFactory extends AbstractPersistentAgreementFactory
    implements PersistentAgreementFactory
{
    private static final Logger LOG = Logger.getLogger( SimplePersistentAgreementFactory.class );

    /**
     * 
     * @param factory
     *            the delegation target
     */
    public SimplePersistentAgreementFactory( AgreementFactory factory )
    {
        super( factory );

        if ( LOG.isDebugEnabled() )
        {
            LOG.debug( "Create a new SimplePersistentAgreementFactory instance." );
        }

        //
        // replaced MUSE RandomUuidFactory with default Java implementation for uuid's
        //
        // this.resourceId = RandomUuidFactory.getInstance().createUUID();
        this.resourceId = UUID.randomUUID().toString();
    }

    /**
     * This agreement factory does not support persistence. Calls to this method will have no effect.
     * 
     * {@inheritDoc}
     */
    protected PersistentAgreement[] doLoad() throws PersistedResourceException
    {
        return new PersistentAgreement[0];
    }

    /**
     * This agreement factory does not support persistence. Calls to this method will return a
     * {@link SimplePersistentAgreement}.
     * 
     * @param agreement
     *            the agreement to persist
     * 
     * @throws {@inheritDoc}
     */
    protected PersistentAgreement persistAgreement( Agreement agreement ) throws PersistedResourceException
    {
        return new SimplePersistentAgreement( agreement, null );
    }

    /**
     * This agreement factory does not support persistence. Calls to this method will have no effect.
     * 
     * @param agreement
     *            the agreement to remove
     * 
     * @throws PersistedResourceException
     *             {@inheritDoc}
     * 
     * @see org.ogf.graap.wsag.server.persistence.IAgreementHome#remove(java.lang.String)
     */
    protected void doRemove( PersistentAgreement agreement ) throws PersistedResourceException
    {
    }

}
