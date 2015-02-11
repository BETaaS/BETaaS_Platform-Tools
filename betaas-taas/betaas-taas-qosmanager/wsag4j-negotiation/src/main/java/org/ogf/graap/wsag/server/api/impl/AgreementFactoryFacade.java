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
package org.ogf.graap.wsag.server.api.impl;

import java.util.Map;

import org.apache.xmlbeans.XmlObject;
import org.ogf.graap.wsag.api.Agreement;
import org.ogf.graap.wsag.api.AgreementFactory;
import org.ogf.graap.wsag.api.AgreementOffer;
import org.ogf.graap.wsag.api.Negotiation;
import org.ogf.graap.wsag.api.PendingAgreementListener;
import org.ogf.graap.wsag.api.exceptions.AgreementFactoryException;
import org.ogf.graap.wsag.api.exceptions.NegotiationFactoryException;
import org.ogf.schemas.graap.wsAgreement.AgreementTemplateType;
import org.ogf.schemas.graap.wsAgreement.negotiation.NegotiationContextType;

/**
 * The agreement factory facade is an implementation of a
 * {@link org.ogf.graap.wsag.server.api.IAgreementFactory}. Calls are delegated to an {@link AgreementFactory}
 * implementation. This class is used for backward compatibility.
 * 
 * @author Oliver Waeldrich
 */
public class AgreementFactoryFacade extends AbstractAgreementFactory
{

    private final AgreementFactory factory;

    /**
     * 
     * @param factory
     *            the factory implementation
     */
    public AgreementFactoryFacade( AgreementFactory factory )
    {
        super();
        this.factory = factory;
    }

    /**
     * 
     * @return The agreement factory object of this facade.
     */
    public AgreementFactory getAgreementFactory()
    {
        return factory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Agreement createAgreement( AgreementOffer offer, Map<String, Object> context )
        throws AgreementFactoryException
    {
        return factory.createAgreement( offer, context );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Agreement createPendingAgreement( AgreementOffer offer, PendingAgreementListener listener,
                                             Map<String, Object> context ) throws AgreementFactoryException
    {
        return factory.createPendingAgreement( offer, listener, context );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AgreementTemplateType[] getTemplates()
    {
        return factory.getTemplates();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Negotiation
        initiateNegotiation( NegotiationContextType context, XmlObject[] criticalExtensions,
                             XmlObject[] nonCriticalExtensions, Map<String, Object> environment )
            throws NegotiationFactoryException
    {
        return factory.initiateNegotiation( context, criticalExtensions, nonCriticalExtensions, environment );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ogf.graap.wsag.server.api.EngineComponent#doInitialize()
     */
    @Override
    protected void doInitialize() throws Exception
    {
        // nothing to do here
    }
}
