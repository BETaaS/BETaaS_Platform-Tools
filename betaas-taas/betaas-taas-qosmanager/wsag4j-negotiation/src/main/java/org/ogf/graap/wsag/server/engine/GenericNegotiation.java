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
package org.ogf.graap.wsag.server.engine;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlObject;
import org.ogf.graap.wsag.api.Negotiation;
import org.ogf.graap.wsag.api.exceptions.NegotiationException;
import org.ogf.graap.wsag.api.logging.LogMessage;
import org.ogf.graap.wsag.server.actions.impl.AgreementFactoryAction;
import org.ogf.graap.wsag.server.api.IAgreementFactory;
import org.ogf.schemas.graap.wsAgreement.AgreementTemplateType;
import org.ogf.schemas.graap.wsAgreement.negotiation.NegotiationContextType;
import org.ogf.schemas.graap.wsAgreement.negotiation.NegotiationOfferContextType;
import org.ogf.schemas.graap.wsAgreement.negotiation.NegotiationOfferDocument;
import org.ogf.schemas.graap.wsAgreement.negotiation.NegotiationOfferStateType;
import org.ogf.schemas.graap.wsAgreement.negotiation.NegotiationOfferType;
import org.w3c.dom.Node;

/**
 * GenericNegotiation implements the default negotiation behavior in the WSAG4J framework. It validates
 * incoming offers with respect to adherence to creation constraints defined in the originating templates,
 * with respect to negotiation constraints defined in the parent offers, and invokes the appropriate
 * negotiation strategies for incoming offers.
 * 
 * @author owaeld
 */
public class GenericNegotiation
    implements Negotiation
{

    /**
     * Default logger.
     */
    private static final Logger LOG = Logger.getLogger( GenericNegotiation.class );

    /**
     * Critical extensions key
     */
    public static final String CRITICAL_EXTENSIONS = "org.wsag4j.negotiation.properties.critical";

    /**
     * Non-critical extensions key
     */
    public static final String NON_CRITICAL_EXTENSIONS = "org.wsag4j.negotiation.properties.noncritical";

    /**
     * Negotiation context key
     */
    public static final String NEGOTIATION_CONTEXT = "org.wsag4j.negotiation.context";

    /**
     * The agreement factory that is used to create the negotiated agreements.
     */
    @SuppressWarnings( "unused" )
    private final IAgreementFactory factory;

    /**
     * The template registry where the supported negotiation actions are registered.
     */
    private final TemplateRegistry registry;

    /**
     * The context used to create the negotiation instance.
     */
    private final NegotiationContextType context;

    /**
     * The negotiation properties contain a set of parameters passed from the negotiation factory.
     */
    private final Map<String, Object> negotiationProperties;

    private final Map<String, NegotiationOfferType> offerHistory =
        new HashMap<String, NegotiationOfferType>();

    private final TemplateValidator templateValidator;

    /**
     * @param factory
     *            The factory that agreement offers are negotiated for.
     * 
     * @param registry
     *            The registry to lookup the factory actions.
     * 
     * @param context
     *            The context in which the negotiation is created in.
     */
    public GenericNegotiation( IAgreementFactory factory, TemplateRegistry registry,
                               NegotiationContextType context )
    {

        this( factory, registry, context, new HashMap<String, Object>() );
    }

    private void checkOfferIntegrity( NegotiationOfferType offer ) throws NegotiationException
    {
        NegotiationOfferContextType negotiationOfferContext = offer.getNegotiationOfferContext();
        if ( negotiationOfferContext == null )
        {
            String message = "missing negotiation offer context";
            throw new NegotiationException( message );
        }

        String parentOfferId = negotiationOfferContext.getCounterOfferTo();
        if ( parentOfferId == null )
        {
            String message = "missing counter-offer-to-id in negotiation offer context";
            throw new NegotiationException( message );
        }

        String offerTemplateId = offer.getContext().getTemplateId();
        if ( offerTemplateId == null )
        {
            String message = "missing template-id in context";
            throw new NegotiationException( message );
        }

        String offerTemplateName = offer.getContext().getTemplateName();
        if ( offerTemplateName == null )
        {
            String message = "missing template name in context";
            throw new NegotiationException( message );
        }
    }

    /**
     * 
     * @param factory
     *            The factory that agreement offers are negotiated for.
     * 
     * @param registry
     *            The registry to lookup the factory actions.
     * 
     * @param context
     *            The context in which the negotiation is created in.
     * 
     * @param negotiationProperties
     *            Additional parameters that were passed from the negotiation factory.
     */
    public GenericNegotiation( IAgreementFactory factory, TemplateRegistry registry,
                               NegotiationContextType context, Map<String, Object> negotiationProperties )
    {

        this.factory = factory;
        this.registry = registry;
        this.context = context;
        this.negotiationProperties = negotiationProperties;

        this.templateValidator = new TemplateValidator();
        this.templateValidator.setConfiguration( factory.getEngine().getConfiguration().getValidator() );
    }

    /**
     * {@inheritDoc}
     * 
     * @see Negotiation#advertise(NegotiationOfferType[], XmlObject[])
     */
    public void advertise( NegotiationOfferType[] quotes, XmlObject[] noncriticalExtensions )
        throws NegotiationException
    {
        //
        // TODO: implement advertise method using callback handlers
        //
        throw new UnsupportedOperationException( "not implemented" );
    }

    /**
     * {@inheritDoc}
     * 
     * @see Negotiation#negotiate(NegotiationOfferType[], XmlObject[])
     */
    public NegotiationOfferType[] negotiate( NegotiationOfferType[] counterOffers,
                                             XmlObject[] noncriticalExtensions ) throws NegotiationException
    {
        // list of all negotiated (counter) offers
        List<NegotiationOfferType> result = new Vector<NegotiationOfferType>();

        // build global invocation context
        Map<String, Object> invocationContext = new HashMap<String, Object>();
        invocationContext.put( NON_CRITICAL_EXTENSIONS, noncriticalExtensions );
        invocationContext.put( NEGOTIATION_CONTEXT, negotiationProperties );

        // start negotiation process for each counter offer
        for ( int i = 0; i < counterOffers.length; i++ )
        {
            NegotiationOfferType counterOffer = counterOffers[i];

            try
            {
                //
                // load the original (parent) offers' id
                //
                checkOfferIntegrity( counterOffer );
                NegotiationOfferContextType negotiationOfferContext =
                    counterOffer.getNegotiationOfferContext();
                String parentOfferId = negotiationOfferContext.getCounterOfferTo();
                String offerTemplateId = counterOffer.getContext().getTemplateId();
                String offerTemplateName = counterOffer.getContext().getTemplateName();

                //
                // check integrity of the referenced parent, throw an exception in case of error
                //
                checkParentOffer( counterOffer, parentOfferId );

                //
                // validate counter offer against the template
                //
                AgreementTemplateType templateType =
                    registry.findTemplate( offerTemplateName, offerTemplateId );

                if ( templateType == null )
                {
                    throw new NegotiationException( "Could not load any template for passed offer." );
                }

                boolean isValid = templateValidator.validate( counterOffer, templateType );
                if ( isValid )
                {
                    LOG.debug( "Counter offer validated against template." );

                    //
                    // check if we start at the root node or some leaf
                    //
                    String templateId = MessageFormat.format( "{0}-{1}", offerTemplateId, offerTemplateName );
                    if ( offerHistory.containsKey( parentOfferId ) )
                    {
                        String message = "Found parent offer with ID ''{0}''.";
                        LOG.debug( LogMessage.getMessage( message, parentOfferId ) );

                        NegotiationOfferType parentOffer = offerHistory.get( parentOfferId );

                        //
                        // validate counter offer against the parent offer
                        //
                        isValid = templateValidator.validate( counterOffer, parentOffer );
                        if ( isValid )
                        {
                            LOG.debug( "Counter offer validated against parent offer." );

                            Vector<NegotiationOfferType> results =
                                performNegotiation( counterOffer, invocationContext, offerTemplateId,
                                    offerTemplateName );

                            //
                            // store negotiated offer in offer history
                            //
                            for ( NegotiationOfferType negotiatedOffer : results )
                            {
                                offerHistory.put( negotiatedOffer.getOfferId(),
                                    (NegotiationOfferType) negotiatedOffer.copy() );
                                result.add( negotiatedOffer );
                            }
                        }
                        else
                        {
                            String msgValidationFailed = "offer / counter offer validation failed";
                            throw new NegotiationException( msgValidationFailed );
                        }
                    }
                    else if ( parentOfferId.equals( templateId ) )
                    {
                        final String message =
                            "Found root node. Start negotiation process for template ''{0}''.";
                        LOG.debug( LogMessage.getMessage( message, templateId ) );

                        Vector<NegotiationOfferType> results =
                            performNegotiation( counterOffer, invocationContext, offerTemplateId,
                                offerTemplateName );

                        //
                        // change counterOfferTo to the template ID
                        // store negotiated offer in offer history
                        //
                        for ( NegotiationOfferType negotiatedOffer : results )
                        {
                            negotiatedOffer.getNegotiationOfferContext().setCounterOfferTo( templateId );

                            offerHistory.put( negotiatedOffer.getOfferId(),
                                (NegotiationOfferType) negotiatedOffer.copy() );
                            result.add( negotiatedOffer );
                        }
                    }
                    else
                    {
                        String msgUnknownOffer =
                            "Negotiation process did not start from root or any other node.";
                        throw new NegotiationException( msgUnknownOffer );
                    }
                }
                else
                {
                    //
                    // add rejected offer to result and continue
                    //

                    throw new NegotiationException(
                        "Validation of the negotiation offer against template failed." );
                }
            }
            catch ( NegotiationException ex )
            {
                String errorMessage = "Negotiation process aborted. Building REJECT-counter-offer.";
                LOG.error( errorMessage );

                NegotiationOfferType rejected = buildRejectOffer( counterOffer, ex );
                result.add( rejected );
            }
        }

        NegotiationOfferType[] offerArray = result.toArray( new NegotiationOfferType[result.size()] );

        return offerArray;
    }

    /**
     * @param i
     * @param counterOffer
     * @param parentOfferId
     * @throws NegotiationException
     */
    private void checkParentOffer( NegotiationOfferType counterOffer, String parentOfferId )
        throws NegotiationException
    {
        LOG.debug( LogMessage.getMessage( "Processing offer ''{0}''.", counterOffer.getOfferId() ) );

        //
        // check, if there is a parent counter offer and if the status of this offer is != rejected
        //

        if ( offerHistory.containsKey( parentOfferId ) )
        {
            if ( offerHistory.get( parentOfferId ).getNegotiationOfferContext().getState().isSetRejected() )
            {
                String errorMessage = "Negotiation based on a rejected counter offer is not possible.";
                LOG.error( errorMessage );

                throw new NegotiationException( errorMessage );
            }
        }
    }

    private Vector<NegotiationOfferType>
        performNegotiation( NegotiationOfferType counterOffer, Map<String, Object> invocationContext,
                            String offerTemplateId, String offerTemplateName ) throws NegotiationException
    {
        Vector<NegotiationOfferType> results = new Vector<NegotiationOfferType>();

        //
        // Lookup negotiation action for quote. In case of an error,
        // the negotiation offer is rejected and an appropriate error
        // reason is added.
        //

        AgreementFactoryAction action = loadAction( counterOffer );

        if ( action == null )
        {
            String msgText = "No action for template id ''{0}'' and template name ''{1}'' found.";
            String message = MessageFormat.format( msgText, offerTemplateId, offerTemplateName );
            throw new NegotiationException( message );
        }
        else
        {
            //
            // Invoke the proper negotiation logic for the quote. In case of an
            // error, the negotiation offer is rejected and an appropriate error
            // reason is added.
            //

            try
            {
                final String message =
                    "Negotiate (counter) offer [offer id=''{0}'', counterOfferTo=''{1}''].";
                LOG.debug( LogMessage.getMessage( message, counterOffer.getOfferId(),
                    counterOffer.getNegotiationOfferContext().getCounterOfferTo() ) );

                NegotiationOfferType[] negotiatedOffers = action.negotiate( counterOffer, invocationContext );

                for ( NegotiationOfferType negotiatedOffer : negotiatedOffers )
                {
                    //
                    // check if the used template for the offer and the new counter offer equals
                    //

                    String negotiatedOfferTemplateId = negotiatedOffer.getContext().getTemplateId();
                    String negotiatedOfferTemplateName = negotiatedOffer.getContext().getTemplateName();

                    if ( !offerTemplateId.equals( negotiatedOfferTemplateId ) )
                    {
                        String exMessageText =
                            "Template IDs of offer ''{0}'' and counter offer ''{1}'' not equal.";
                        String exMessage =
                            MessageFormat.format( exMessageText, offerTemplateId, negotiatedOfferTemplateId );

                        throw new NegotiationException( exMessage );
                    }
                    else if ( !offerTemplateName.equals( negotiatedOfferTemplateName ) )
                    {
                        String exMessageText =
                            "Template names of offer ''{0}'' and counter offer ''{1}'' not equal.";
                        String exMessage =
                            MessageFormat.format( exMessageText, offerTemplateName,
                                negotiatedOfferTemplateName );

                        throw new NegotiationException( exMessage );
                    }

                    //
                    // generate a unique id for each counter offer
                    //

                    String offerUuid = UUID.randomUUID().toString();

                    while ( offerHistory.containsKey( offerUuid ) )
                    {
                        offerUuid = UUID.randomUUID().toString();
                    }

                    negotiatedOffer.setOfferId( offerUuid );
                    negotiatedOffer.getNegotiationOfferContext()
                                   .setCounterOfferTo( counterOffer.getOfferId() );
                    results.add( negotiatedOffer );
                }
            }
            catch ( NegotiationException ex )
            {
                String errorMessage = "Negotiation of offer failed.";
                LOG.error( errorMessage );

                NegotiationOfferType rejected = buildRejectOffer( counterOffer, ex );
                results.add( rejected );
            }
        }

        return results;
    }

    private NegotiationOfferType
        buildRejectOffer( NegotiationOfferType counterOffer, NegotiationException ex )
    {
        //
        // TODO catch the proper exception and add the rejection reason.
        //
        NegotiationOfferDocument negotiationOfferDocument = NegotiationOfferDocument.Factory.newInstance();
        negotiationOfferDocument.addNewNegotiationOffer().set( counterOffer.copy() );

        NegotiationOfferType rejected = negotiationOfferDocument.getNegotiationOffer();
        rejected.getNegotiationOfferContext().setState( NegotiationOfferStateType.Factory.newInstance() );
        rejected.getNegotiationOfferContext().getState().addNewRejected();

        String offerUuid = UUID.randomUUID().toString();
        while ( offerHistory.containsKey( offerUuid ) )
        {
            offerUuid = UUID.randomUUID().toString();
        }
        rejected.setOfferId( offerUuid );
        offerHistory.put( offerUuid, (NegotiationOfferType) rejected.copy() );

        Node imported =
            rejected.getDomNode().getOwnerDocument().importNode( ex.getBaseFault().getDomNode(), true );
        rejected.getDomNode().appendChild( imported );

        return rejected;
    }

    /**
     * {@inheritDoc}
     * 
     * @see Negotiation#getNegotiationContext()
     */
    public NegotiationContextType getNegotiationContext()
    {
        return context;
    }

    /**
     * {@inheritDoc}
     * 
     * @see Negotiation#getNegotiationOffers()
     */
    public NegotiationOfferType[] getNegotiationOffers()
    {
        Collection<NegotiationOfferType> offers = offerHistory.values();
        NegotiationOfferType[] offerArray = offers.toArray( new NegotiationOfferType[offers.size()] );

        return offerArray;
    }

    /**
     * @see Negotiation#terminate()
     */
    public void terminate()
    {
        //
        // TODO: add handler with negotiation specific termination strategy
        //
    }

    /**
     * Loads the appropriate negotiation strategy for an incoming quote. The negotiation strategy is
     * identified by the template the incoming offer is based on.
     * 
     * @param quote
     *            negotiation offer
     * @return the {@link AgreementFactoryAction} for this quote
     */
    public AgreementFactoryAction loadAction( NegotiationOfferType quote )
    {
        String templateName = quote.getContext().getTemplateName();
        String templateId = quote.getContext().getTemplateId();
        AgreementFactoryAction action = registry.findAction( templateName, templateId );

        return action;
    }

    /**
     * Default implementation for retrieving negotiable templates from the associated agreement factory.
     * 
     * {@inheritDoc}
     * 
     * @see org.ogf.graap.wsag.api.Negotiation#getNegotiableTemplates()
     * @see TemplateRegistry#getNegotiableTemplates()
     */
    public AgreementTemplateType[] getNegotiableTemplates()
    {
        return registry.getNegotiableTemplates();
    }
}
