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
package org.ogf.graap.wsag.api.types;

import org.ogf.graap.wsag.api.NegotiationOffer;
import org.ogf.schemas.graap.wsAgreement.AgreementContextType;
import org.ogf.schemas.graap.wsAgreement.AgreementTemplateType;
import org.ogf.schemas.graap.wsAgreement.AgreementType;
import org.ogf.schemas.graap.wsAgreement.TermTreeType;
import org.ogf.schemas.graap.wsAgreement.negotiation.NegotiationConstraintSectionType;
import org.ogf.schemas.graap.wsAgreement.negotiation.NegotiationOfferContextType;
import org.ogf.schemas.graap.wsAgreement.negotiation.NegotiationOfferDocument;
import org.ogf.schemas.graap.wsAgreement.negotiation.NegotiationOfferType;

/**
 * NegotiationOfferType
 * 
 * @author hrasheed
 */
public class NegotiationOfferTypeImpl extends WSAGXmlType implements NegotiationOffer
{

    private org.ogf.schemas.graap.wsAgreement.negotiation.NegotiationOfferType negotiationOffer =
        org.ogf.schemas.graap.wsAgreement.negotiation.NegotiationOfferType.Factory.newInstance();

    /**
     * Initializes an negotiation offer from a template.
     * 
     * @param template
     *            the template used to create the negotiation offer
     */
    public NegotiationOfferTypeImpl( AgreementTemplateType template )
    {
        //
        // process the agreement template
        //
        AgreementType processed = processTemplate( template );

        //
        // initialize the negotiation offer
        //
        initialize( (NegotiationOfferType) processed.changeType( NegotiationOfferType.type ) );
    }

    /**
     * Initializes a negotiation offer from a negotiation offer.
     * 
     * @param negotiationOffer
     *            the negotiation offer used to create this type
     */
    public NegotiationOfferTypeImpl( NegotiationOfferType negotiationOffer )
    {
        initialize( (NegotiationOfferType) negotiationOffer.copy() );
    }

    private void initialize( org.ogf.schemas.graap.wsAgreement.negotiation.NegotiationOfferType negOffer )
    {
        //
        // make sure that negotiation offer is not a document fragment
        //
        NegotiationOfferDocument negotiationOfferDoc = NegotiationOfferDocument.Factory.newInstance();
        negotiationOfferDoc.addNewNegotiationOffer().set( negOffer.copy() );
        negOffer = negotiationOfferDoc.getNegotiationOffer();

        if ( negOffer == null )
        {
            throw new IllegalStateException( "Parameter negotiationOffer must not be null." );
        }

        //
        // check required parameter
        //
        if ( negOffer.getContext() == null )
        {
            throw new IllegalStateException( "Parameter negotiationOffer#Context must not be null." );
        }

        if ( negOffer.getTerms() == null )
        {
            throw new IllegalStateException( "Parameter negotiationOffer#Terms must not be null." );
        }

        this.negotiationOffer = negOffer;

        String offerId = ( negOffer.getOfferId() != null ) ? negOffer.getOfferId() : "1";
        String offerName = ( negOffer.getName() != null ) ? negOffer.getName() : "NEGOTIATIONOFFER";

        setOfferId( offerId );
        setName( offerName );
    }

    /**
     * @return the offer id
     */
    public String getOfferId()
    {
        return getNegotiationOffer().getOfferId();
    }

    /**
     * @param id
     *            offer id
     */
    public void setOfferId( String id )
    {
        getNegotiationOffer().setOfferId( id );
    }

    /**
     * @return the offer name
     */
    public String getName()
    {
        return getNegotiationOffer().getName();
    }

    /**
     * @param name
     *            the offer name to set
     */
    public void setName( String name )
    {
        getNegotiationOffer().setName( name );
    }

    /**
     * @return the agreement id
     */
    public String getAgreementId()
    {
        return getNegotiationOffer().getAgreementId();
    }

    /**
     * @param id
     *            agreement id
     */
    public void setAgreementId( String id )
    {
        getNegotiationOffer().setAgreementId( id );
    }

    /**
     * The context of the agreement to negotiate.
     * 
     * @return the agreement context
     */
    public AgreementContextType getContext()
    {
        return getNegotiationOffer().getContext();
    }

    /**
     * @param context
     *            the agreement context to set
     */
    public void setContext( AgreementContextType context )
    {
        getNegotiationOffer().setContext( context );
    }

    /**
     * @return the negotiation terms
     */
    public TermTreeType getTerms()
    {
        return getNegotiationOffer().getTerms();
    }

    /**
     * @param terms
     *            the negotiation terms to set
     */
    public void setTerms( TermTreeType terms )
    {
        getNegotiationOffer().setTerms( terms );
    }

    /**
     * @return the negotiation offer context
     */
    public NegotiationOfferContextType getNegotiationOfferContext()
    {
        return getNegotiationOffer().getNegotiationOfferContext();
    }

    /**
     * @param negOffercontext
     *            the negotiation offer context to set
     */
    public void setNegotiationOfferContext( NegotiationOfferContextType negOffercontext )
    {
        getNegotiationOffer().setNegotiationOfferContext( negOffercontext );
    }

    /**
     * @return the negotiation constraints
     */
    public NegotiationConstraintSectionType getNegotiationConstraints()
    {
        return getNegotiationOffer().getNegotiationConstraints();
    }

    /**
     * @param constraints
     *            the negotiation constraints to set
     */
    public void setNegotiationConstraints( NegotiationConstraintSectionType constraints )
    {
        getNegotiationOffer().setNegotiationConstraints( constraints );
    }

    private org.ogf.schemas.graap.wsAgreement.negotiation.NegotiationOfferType getNegotiationOffer()
    {
        return negotiationOffer;
    }

    /**
     * @return the XML representation of the negotiation offer
     */
    public org.ogf.schemas.graap.wsAgreement.negotiation.NegotiationOfferType getXMLObject()
    {
        return this.negotiationOffer;
    }

    /**
     * Validates the internal XML representation of this object.
     * 
     * {@inheritDoc}
     */
    public boolean validate()
    {
        return validate( negotiationOffer );
    }

}
