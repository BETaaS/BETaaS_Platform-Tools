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

import org.ogf.graap.wsag.server.api.IAgreementFactory;
import org.ogf.graap.wsag.server.persistence.PersistedResourceException;
import org.ogf.graap.wsag.server.persistence.PersistentAgreementFactory;

/**
 * SimpleWSAG4JPersistence loads the factory configurations defined in the WSAG4J engine configuration files
 * and and instantiates a factory for each engine instance.
 * 
 * @author Oliver Waeldrich
 * 
 */
public class SimpleWSAG4JPersistence extends AbstractWSAG4JPersistence
{

    /*
     * (non-Javadoc)
     * 
     * @see org.ogf.graap.wsag.server.persistence.impl.AbstractWSAG4JPersistence#doLoad()
     */
    @Override
    protected PersistentAgreementFactory[] doLoad() throws PersistedResourceException
    {
        try
        {
            //
            // load the implementation specified in the wsag4j.properties file here
            // initialize it, and if feasible, set the configuration data
            //
            IAgreementFactory factory = getEngine().getAgreementFactoryPrototype();
            factory.initialize( getEngine() );

            // build the factory
            SimplePersistentAgreementFactory persistentFactory =
                new SimplePersistentAgreementFactory( factory );

            persistentFactory.setResourceId( getEngine().getDefaultResourceId() );

            return new PersistentAgreementFactory[] { persistentFactory };
        }
        catch ( Exception e )
        {
            throw new PersistedResourceException( e );
        }
    }

    /**
     * {@inheritDoc}
     */
    public void saveAgreementFactories( PersistentAgreementFactory[] factories )
    {
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ogf.graap.wsag.server.persistence.impl.AbstractWSAG4JPersistence#doRemove(PersistentAgreementFactory)
     */
    @Override
    protected boolean doRemove( PersistentAgreementFactory factory ) throws PersistedResourceException
    {
        throw new PersistedResourceException( "Operation not supported for simple WSAG4J persistence." );
    }
}
