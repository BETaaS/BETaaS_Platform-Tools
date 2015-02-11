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
package org.ogf.graap.wsag.server.engine;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlObject;
import org.ogf.graap.wsag.api.Agreement;
import org.ogf.graap.wsag.api.AgreementOffer;
import org.ogf.graap.wsag.api.Negotiation;
import org.ogf.graap.wsag.api.PendingAgreementListener;
import org.ogf.graap.wsag.api.WsagConstants;
import org.ogf.graap.wsag.api.exceptions.AgreementFactoryException;
import org.ogf.graap.wsag.api.exceptions.NegotiationFactoryException;
import org.ogf.graap.wsag.api.logging.LogMessage;
import org.ogf.graap.wsag.server.actions.ActionBuilder;
import org.ogf.graap.wsag.server.actions.impl.AgreementFactoryAction;
import org.ogf.graap.wsag.server.api.impl.AbstractAgreementFactory;
import org.ogf.graap.wsag.server.api.impl.PendingAgreementFacade;
import org.ogf.graap.wsag4j.types.configuration.ActionType;
import org.ogf.schemas.graap.wsAgreement.AgreementTemplateType;
import org.ogf.schemas.graap.wsAgreement.negotiation.NegotiationContextType;

/**
 * The {@link GenericAgreementFactory} is the default implementation of an agreement factory. It support
 * configuration of {@link AgreementFactoryAction}'s and automatic offer validation out of the box.
 * 
 * @author Oliver Waeldrich
 * 
 * @see AgreementFactoryAction
 */

public class GenericAgreementFactory extends AbstractAgreementFactory
{

    private static final Logger LOG = Logger.getLogger( GenericAgreementFactory.class );

    private boolean initialized = false;

    private final TemplateRegistry registry = new TemplateRegistry();

    private TemplateValidator validator;

    /**
     * Load the agreement factory actions based on the WSAG4J factory configuration. The factory configuration
     * is stored in the factory context under the key {@link WsagConstants#WSAG4J_FACTORY_CONFIGURATION}.
     * 
     * @return the actions that are configured with the factory
     * 
     * @throws Exception
     *             indicates that an error occurred while loading the actions
     */
    protected AgreementFactoryAction[] loadActions() throws Exception
    {
        //
        // Populate the factory context with the factory
        // configuration, so that action implementations may
        // look up the factory configuration
        //
        getFactoryContext().put( WsagConstants.WSAG4J_FACTORY_CONFIGURATION, getEngine().getConfiguration() );

        //
        // initialize the actions
        //
        Vector<AgreementFactoryAction> actions = new Vector<AgreementFactoryAction>();

        //
        // for each action configuration section
        //
        ActionType[] actionConfig = getEngine().getConfiguration().getActionArray();
        for ( int i = 0; i < actionConfig.length; i++ )
        {
            actionConfig[i].setName( actionConfig[i].getName() == null ? "<not specified"
                            : actionConfig[i].getName() );

            Object[] filler =
                new Object[] { new Integer( i + 1 ), new Integer( actionConfig.length ),
                    actionConfig[i].getName() };
            String message = MessageFormat.format( "Load action {0} of {1}: {2}", filler );
            LOG.info( message );

            try
            {
                AgreementFactoryAction action =
                    ActionBuilder.getInstance().createAgreementFactoryAction( actionConfig[i],
                        getFactoryContext() );
                actions.add( action );
            }
            catch ( Exception ex )
            {
                filler = new Object[] { actionConfig[i].getName(), ex.getMessage() };
                message = MessageFormat.format( "Action [{0}] was not loaded. Reason {1}", filler );
                LOG.error( message );
            }
        }

        return actions.toArray( new AgreementFactoryAction[actions.size()] );
    }

    /**
     * Initializes a {@link GenericAgreementFactory} instance based on a provided EngineConfiguration. The
     * initialize method loads the engine configuration, initializes the configured actions, and populates the
     * action registry.
     * 
     * @param engine
     *            the WsagEngine for this factory
     */
    @Override
    public synchronized void doInitialize()
    {
        if ( initialized )
        {
            return;
        }

        try
        {
            if ( getEngine().getConfiguration() == null )
            {
                String message = "Engine configuration must not be null.";
                throw new Exception( message );
            }

            initializeEngine();
        }
        catch ( Exception e )
        {
            Object[] filler = new Object[] { e.getMessage() };
            String message = MessageFormat.format( "Failed to initialize WSAG4J engine. Error: {0}", filler );
            LOG.error( message );
        }
        finally
        {
            initialized = true;
        }
    }

    /**
     * Initializes the WSAG4J engine and instantiates the configrued factory actions.
     */
    private void initializeEngine()
    {
        AgreementFactoryAction[] actions = new AgreementFactoryAction[0];

        try
        {
            actions = loadActions();
        }
        catch ( Exception ex )
        {
            actions = new AgreementFactoryAction[0];
            Object[] filler = new Object[] { ex.getMessage() };
            String msgErrorLoadActions =
                "Error while loading agreement factory actions. No actions were loaded. Reason: {0}";
            String message = MessageFormat.format( msgErrorLoadActions, filler );
            LOG.error( message );
        }

        for ( int i = 0; i < actions.length; i++ )
        {
            try
            {
                LOG.debug( LogMessage.getMessage( "Initialize factory action: {0}", actions[i].getName() ) );

                actions[i].setEngine( getEngine() );
                actions[i].initialize();

                LOG.debug( LogMessage.getMessage( "Deploy factory action: {0}", actions[i].getName() ) );

                registry.add( actions[i] );
            }
            catch ( Exception e )
            {
                String message = "Error while initializing action {0}. Action was not loaded. Reason: {1}";
                LOG.error( LogMessage.getMessage( message, actions[i].getName(), e.getMessage() ) );
            }
        }

        validator = new TemplateValidator();
        validator.setConfiguration( getEngine().getConfiguration().getValidator() );
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ogf.graap.wsag.api.AgreementFactory#createAgreement(org.ogf.graap.wsag.api.AgreementOffer)
     */
    @Override
    public Agreement createAgreement( AgreementOffer offer, Map<String, Object> context )
        throws AgreementFactoryException
    {
        AgreementFactoryAction action = getActionForOffer( offer );

        if ( action == null )
        {
            Object[] filler =
                new Object[] { offer.getContext().getTemplateName(), offer.getContext().getTemplateId() };
            String msgNoActionFound =
                "No ICreateAgreementAction found for offer (template name [{0}] : template id [{1}])";
            String message = MessageFormat.format( msgNoActionFound, filler );
            throw new AgreementFactoryException( message );
        }

        AgreementTemplateType template = getTemplateForOffer( offer );

        StringBuffer error = new StringBuffer();
        if ( getValidator().validate( offer, template, error ) )
        {
            LOG.info( "Agreement offer successfully validated." );

            Agreement agreement = action.createAgreement( offer, context );
            return agreement;
        }
        else
        {
            LOG.info( "Agreement offer validation failed." );
            AgreementFactoryException ex = new AgreementFactoryException( error.toString() );
            String msgOfferValidationFailed =
                "Agreement offer validation failed. The offer is not valid with respect to the template constraints.";
            throw new AgreementFactoryException( msgOfferValidationFailed, ex );

        }

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

        Map<String, Object> properties = new HashMap<String, Object>();
        properties.putAll( environment );
        properties.put( GenericNegotiation.CRITICAL_EXTENSIONS, criticalExtensions );
        properties.put( GenericNegotiation.NON_CRITICAL_EXTENSIONS, nonCriticalExtensions );

        return new GenericNegotiation( this, registry, context, properties );
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ogf.graap.wsag.api.AgreementFactory#getTemplates()
     */
    @Override
    public AgreementTemplateType[] getTemplates()
    {
        return registry.getAllTemplates();
    }

    private AgreementTemplateType getTemplateForOffer( AgreementOffer offer )
    {
        String templateName = offer.getContext().getTemplateName();
        String templateId = offer.getContext().getTemplateId();
        return registry.findTemplate( templateName, templateId );
    }

    private AgreementFactoryAction getActionForOffer( AgreementOffer offer )
    {
        String templateName = offer.getContext().getTemplateName();
        String templateId = offer.getContext().getTemplateId();
        return registry.findAction( templateName, templateId );
    }

    private synchronized TemplateValidator getValidator()
    {
        if ( validator == null )
        {
            validator = new TemplateValidator();
            validator.setConfiguration( getEngine().getConfiguration().getValidator() );
        }
        return validator;
    }

    /**
     * The template registry stores the templates of this factory instance along with the associated
     * {@link AgreementFactoryAction}.
     * 
     * @return the template registry of this factory instance
     */
    public TemplateRegistry getTemplateRegistry()
    {
        return registry;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.ogf.graap.wsag.api.AgreementFactory#createPendingAgreement(org.ogf.graap.wsag.api.AgreementOffer)
     */
    @Override
    public Agreement createPendingAgreement( AgreementOffer offer, PendingAgreementListener listener,
                                             Map<String, Object> context ) throws AgreementFactoryException
    {
        PendingAgreementFacade agreement = new PendingAgreementFacade( offer, this, listener, context );
        Thread runner = new Thread( agreement );
        runner.start();

        return agreement;
    }

}
