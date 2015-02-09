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
package org.ogf.graap.wsag.api.types;

import org.ogf.graap.wsag.api.AgreementOffer;
import org.ogf.schemas.graap.wsAgreement.AgreementContextType;
import org.ogf.schemas.graap.wsAgreement.AgreementOfferDocument;
import org.ogf.schemas.graap.wsAgreement.AgreementTemplateType;
import org.ogf.schemas.graap.wsAgreement.AgreementType;
import org.ogf.schemas.graap.wsAgreement.CreateAgreementInputDocument;
import org.ogf.schemas.graap.wsAgreement.CreateAgreementInputType;
import org.ogf.schemas.graap.wsAgreement.NoncriticalExtensionType;
import org.ogf.schemas.graap.wsAgreement.TermTreeType;
import org.ogf.schemas.graap.wsAgreement.negotiation.NegotiationOfferType;
import org.w3.x2005.x08.addressing.EndpointReferenceType;

/**
 * AgreementOfferType
 * 
 * @author Oliver Waeldrich
 */
public class AgreementOfferType extends WSAGXmlType
    implements AgreementOffer
{

    private final CreateAgreementInputDocument agreementOfferDocument =
        CreateAgreementInputDocument.Factory.newInstance();

    private AgreementType offer;

    /**
     * Initializes an agreement offer from a template.
     * 
     * @param template
     *            the template that is used to create the offer
     */
    public AgreementOfferType( AgreementTemplateType template )
    {
        //
        // process the agreement template
        //
        AgreementType processed = processTemplate( template );

        //
        // initialize the quote
        //
        initialize( processed );
    }

    /**
     * Constructs a quote for the given negotiation offer.
     * 
     * @param negotiationOffer
     *            the negotiation offer
     */
    public AgreementOfferType( org.ogf.graap.wsag.api.types.NegotiationOfferTypeImpl negotiationOffer )
    {
        this( negotiationOffer.getXMLObject() );
    }

    /**
     * Initializes an agreement offer from a negotiation offer.
     * 
     * @param negotiationOffer
     *            the negotiation offer that is used to create the agreement offer
     */
    public AgreementOfferType( NegotiationOfferType negotiationOffer )
    {
        AgreementOfferDocument offerDoc = AgreementOfferDocument.Factory.newInstance();
        offerDoc.addNewAgreementOffer();

        if ( negotiationOffer.isSetAgreementId() )
        {
            offerDoc.getAgreementOffer().setAgreementId( negotiationOffer.getAgreementId() );
        }

        if ( negotiationOffer.isSetName() )
        {
            offerDoc.getAgreementOffer().setName( negotiationOffer.getName() );
        }

        if ( negotiationOffer.getContext() != null )
        {
            offerDoc.getAgreementOffer().addNewContext().set( negotiationOffer.getContext().copy() );
        }

        if ( negotiationOffer.getTerms() != null )
        {
            offerDoc.getAgreementOffer().addNewTerms().set( negotiationOffer.getTerms().copy() );
        }

        initialize( offerDoc.getAgreementOffer() );
    }

    /**
     * Initializes an agreement offer from a agreement type.
     * 
     * @param offer
     *            the offer that is used to create this object
     */
    public AgreementOfferType( AgreementType offer )
    {
        initialize( (AgreementType) offer.copy() );
    }

    private void initialize( AgreementType offerTemplate )
    {
        if ( offerTemplate == null )
        {
            throw new IllegalStateException( "Parameter Template must not be null." );
        }

        if ( offerTemplate.getContext() == null )
        {
            throw new IllegalStateException( "Parameter Template#Context must not be null." );
        }

        if ( offerTemplate.getTerms() == null )
        {
            throw new IllegalStateException( "Parameter Template#Terms must not be null." );
        }

        //
        // initialize the offer object
        //
        CreateAgreementInputType input = agreementOfferDocument.addNewCreateAgreementInput();
        offer = (AgreementType) input.addNewAgreementOffer().set( offerTemplate );

        String agreementId =
            ( offerTemplate.getAgreementId() != null ) ? offerTemplate.getAgreementId() : "1";
        String agreementName =
            ( offerTemplate.getName() != null ) ? offerTemplate.getName() : "AGREEMENT_OFFER";

        setAgreementId( agreementId );
        setName( agreementName );
    }

    /**
     * {@inheritDoc}
     */
    public EndpointReferenceType getInitiatorEPR()
    {
        return getAgreementInput().getInitiatorAgreementEPR();
    }

    /**
     * {@inheritDoc}
     */
    public NoncriticalExtensionType[] getNoncriticalExtensions()
    {
        return getAgreementInput().getNoncriticalExtensionArray();
    }

    /**
     * {@inheritDoc}
     */
    public void setInitiatorEPR( EndpointReferenceType initiatorAgreementEPR )
    {
        getAgreementInput().setInitiatorAgreementEPR( initiatorAgreementEPR );
    }

    /**
     * {@inheritDoc}
     */
    public void setNoncriticalExtensions( NoncriticalExtensionType[] noncriticalExtensionArray )
    {
        getAgreementInput().setNoncriticalExtensionArray( noncriticalExtensionArray );
    }

    /**
     * {@inheritDoc}
     */
    public String getAgreementId()
    {
        return getAgreementOffer().getAgreementId();
    }

    /**
     * {@inheritDoc}
     */
    public AgreementContextType getContext()
    {
        return getAgreementOffer().getContext();
    }

    /**
     * {@inheritDoc}
     */
    public String getName()
    {
        return getAgreementOffer().getName();
    }

    /**
     * {@inheritDoc}
     */
    public TermTreeType getTerms()
    {
        return getAgreementOffer().getTerms();
    }

    /**
     * {@inheritDoc}
     */
    public void setAgreementId( String agreementId )
    {
        getAgreementOffer().setAgreementId( agreementId );
    }

    /**
     * {@inheritDoc}
     */
    public void setContext( AgreementContextType context )
    {
        getAgreementOffer().setContext( context );
    }

    /**
     * {@inheritDoc}
     */
    public void setName( String name )
    {
        getAgreementOffer().setName( name );
    }

    /**
     * {@inheritDoc}
     */
    public void setTerms( TermTreeType terms )
    {
        getAgreementOffer().setTerms( terms );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean validate()
    {
        return validate( agreementOfferDocument );
    }

    private CreateAgreementInputType getAgreementInput()
    {
        return agreementOfferDocument.getCreateAgreementInput();
    }

    private AgreementType getAgreementOffer()
    {
        return offer;
    }

    /**
     * {@inheritDoc}
     */
    public AgreementType getXMLObject()
    {
        return offer;
    }

}
