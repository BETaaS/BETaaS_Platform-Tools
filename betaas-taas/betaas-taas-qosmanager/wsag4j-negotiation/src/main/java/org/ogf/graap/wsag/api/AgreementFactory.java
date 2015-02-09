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
package org.ogf.graap.wsag.api;

import java.util.Map;

import org.apache.xmlbeans.XmlObject;
import org.ogf.graap.wsag.api.exceptions.AgreementFactoryException;
import org.ogf.graap.wsag.api.exceptions.NegotiationFactoryException;
import org.ogf.schemas.graap.wsAgreement.AgreementTemplateType;
import org.ogf.schemas.graap.wsAgreement.negotiation.NegotiationContextType;

/**
 * This interface defines the operations of an AgreementFactory. Components can query the supported templates
 * of a factory, initiate negotiation processes and create new agreements using this interface.
 * 
 * @see Agreement
 * @author Oliver Waeldrich
 */
public interface AgreementFactory
{

    /**
     * @return the agreement templates exposed by this factory
     */
    AgreementTemplateType[] getTemplates();

    /**
     * Creates a new agreement based on an offer.
     * 
     * @param offer
     *            agreement offer
     * 
     * @param context
     *            invocation context
     * 
     * @return the created agreement instance
     * 
     * @throws AgreementFactoryException
     *             Indicates that the agreement offer was rejected or another fault occurred.
     */
    Agreement createAgreement( AgreementOffer offer, Map<String, Object> context )
        throws AgreementFactoryException;

    /**
     * Creates a new pending agreement based on an offer.
     * 
     * @param offer
     *            agreement offer
     * 
     * @param context
     *            invocation context
     * 
     * @param listener
     *            the listener, when provided, receives the agreement acceptance events once the creation
     *            process is finished
     * 
     * @return the created agreement instance
     * @throws AgreementFactoryException
     *             Indicates that the agreement offer was rejected or another fault occurred.
     */
    Agreement createPendingAgreement( AgreementOffer offer, PendingAgreementListener listener,
                                      Map<String, Object> context ) throws AgreementFactoryException;

    /**
     * Initializes a new negotiation instance. This method creates a new negotiation instance based on the
     * provided negotiation context, taking into account the critical and non-critical extensions.
     * 
     * @param context
     *            The negotiation context defines the roles and obligations of the negotiating parties.
     *            Furthermore, it defines the nature of the negotiation process (e.g. negotiation or
     *            re-negotiation).
     * @param criticalExtensions
     *            Critical extensions must be supported during the negotiation process. If a critical
     *            extension is not understood or supported by the negotiation factory, it must throw an @see
     *            NegotiationFactoryException.
     * @param nonCriticalExtensions
     *            Non-critical extensions should be supported during the negotiation process. If a
     *            non-critical extension is not understood or supported by the negotiation factory, it can be
     *            ignored. Alternatively, an @see NegotiationFactoryException could be thrown.
     * @param environment
     *            Provides access to additional variables provided by the calling instance. These environment
     *            variables are domain specific.
     * @return the new initiated negotiation instance
     * @throws NegotiationFactoryException
     *             indicates that an error occurred while instantiating the negotiation process
     */
    Negotiation initiateNegotiation( NegotiationContextType context, XmlObject[] criticalExtensions,
                                     XmlObject[] nonCriticalExtensions, Map<String, Object> environment )

    throws NegotiationFactoryException;

    /**
     * Returns the factory context.
     * 
     * @return the context for this agreement factory
     */
    AgreementFactoryContext getFactoryContext();

    /**
     * Sets the factory context.
     * 
     * @param context
     *            sets the context for this agreement factory
     */
    void setFactoryContext( AgreementFactoryContext context );

}
