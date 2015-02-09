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

import org.ogf.schemas.graap.wsAgreement.AgreementContextType;
import org.ogf.schemas.graap.wsAgreement.AgreementType;
import org.ogf.schemas.graap.wsAgreement.TermTreeType;
import org.ogf.schemas.graap.wsAgreement.negotiation.NegotiationConstraintSectionType;
import org.ogf.schemas.graap.wsAgreement.negotiation.NegotiationOfferContextType;

/**
 * The NegotiationOffer interface implements the required methods to access the properties of a negotiation
 * offer. Negotiation offers are used in a {@link Negotiation} process in order to negotiate valid
 * {@link AgreementOffer} instances that can be used to create new {@link Agreement}s with an
 * {@link AgreementFactory}.
 * 
 * @see Negotiation
 * @see AgreementFactory
 * @see Agreement
 * @author hrasheed
 */
public interface NegotiationOffer
{

    /**
     * @return the agreement id
     */
    String getAgreementId();

    /**
     * @param id
     *            agreement id
     */
    void setAgreementId( String id );

    /**
     * @return the agreement name
     */
    String getName();

    /**
     * @param name
     *            the agreement name to set
     */
    void setName( String name );

    /**
     * @return the agreement context
     */
    AgreementContextType getContext();

    /**
     * @param context
     *            the agreement context to set
     */
    void setContext( AgreementContextType context );

    /**
     * @return the agreement terms
     */
    TermTreeType getTerms();

    /**
     * @param terms
     *            the agreement terms to set
     */
    void setTerms( TermTreeType terms );

    /**
     * @return the negotiation offer context
     */
    NegotiationOfferContextType getNegotiationOfferContext();

    /**
     * @param context
     *            the agreement context to set
     */
    void setNegotiationOfferContext( NegotiationOfferContextType context );

    /**
     * @return the offer id
     */
    String getOfferId();

    /**
     * @param id
     *            offer id
     */
    void setOfferId( String id );

    /**
     * @return the negotiation constraints
     */
    NegotiationConstraintSectionType getNegotiationConstraints();

    /**
     * @param constraints
     *            the negotiation constraints to set
     */
    void setNegotiationConstraints( NegotiationConstraintSectionType constraints );

    /**
     * @return the XML representation of the negotiation offer
     */
    AgreementType getXMLObject();

}
