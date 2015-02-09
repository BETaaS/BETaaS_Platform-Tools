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
package org.ogf.graap.wsag.server.actions.impl;

import org.apache.log4j.Logger;
import org.ogf.graap.wsag.api.AgreementFactoryContext;
import org.ogf.graap.wsag.api.logging.LogMessage;
import org.ogf.graap.wsag.server.actions.IActionBuilder;
import org.ogf.graap.wsag.server.actions.IActionHandler;
import org.ogf.graap.wsag.server.actions.ICreateAgreementAction;
import org.ogf.graap.wsag.server.actions.IGetTemplateAction;
import org.ogf.graap.wsag.server.actions.INegotiationAction;
import org.ogf.graap.wsag4j.types.configuration.ActionType;
import org.ogf.graap.wsag4j.types.configuration.ImplementationConfigurationType;

/**
 * WSAG4JFactoryActionBuilder
 * 
 * @author Oliver Waeldrich
 * 
 */
public class ActionBuilder
    implements IActionBuilder
{

    private static final Logger LOG = Logger.getLogger( ActionBuilder.class );

    /**
     * <!-- begin-UML-doc --> Creates a new instance of an {@link AgreementFactoryAction} based on the given
     * configuration. <!-- end-UML-doc -->
     * 
     * @param configuration
     *            The configuration of the factory action.
     * 
     * @param factoryContext
     *            The context of the agreement factory that this action is created for.
     * 
     * @return The instantiated {@link AgreementFactoryAction} based on the given configuration.
     * 
     * @throws Exception
     *             An error occurred during the instantiation process.
     * 
     * @generated "UML to Java (com.ibm.xtools.transform.uml2.java5.internal.UML2JavaTransform)"
     */
    public AgreementFactoryAction
        createAgreementFactoryAction( ActionType configuration,
                                      AgreementFactoryContext factoryContext ) throws Exception
    {
        // begin-user-code
        LOG.debug( LogMessage.getMessage( "Instantiate factory action {0}", configuration.getName() ) );

        //
        // load the IGetTemplateAction.
        //
        ImplementationConfigurationType templateActionCfg =
            configuration.getGetTemplateConfiguration();
        IGetTemplateAction templateAction =
            (IGetTemplateAction) loadAction( templateActionCfg.getImplementationClass(),
                IGetTemplateAction.class );

        HandlerContext context = new HandlerContext();
        context.setFactoryContext( factoryContext );
        context.setHandlerConfiguration( templateActionCfg );
        templateAction.setHandlerContext( context );

        LOG.debug( "Get template action instantiated." );

        //
        // load the ICreateAgreementAction.
        //
        ImplementationConfigurationType createActionCfg =
            configuration.getCreateAgreementConfiguration();
        ICreateAgreementAction agreementAction =
            (ICreateAgreementAction) loadAction( createActionCfg.getImplementationClass(),
                ICreateAgreementAction.class );

        context = new HandlerContext();
        context.setFactoryContext( factoryContext );
        context.setHandlerConfiguration( createActionCfg );

        agreementAction.setHandlerContext( context );

        LOG.debug( "Create agreement action instantiated." );

        //
        // The configuration of a negotiation action is optional.
        // It is not required that an agreement factory supports
        // negotiation for a specific template.
        //
        ImplementationConfigurationType negotiateActionCfg =
            configuration.getNegotiationConfiguration();
        INegotiationAction negotiateAction = null;

        context = new HandlerContext();
        context.setFactoryContext( factoryContext );
        context.setHandlerConfiguration( negotiateActionCfg );

        if ( negotiateActionCfg == null )
        {
            negotiateAction = new NegotiationUnsupportedAction();

            LOG.debug( "No negotiation action configured." );
        }
        else
        {
            negotiateAction =
                (INegotiationAction) loadAction( negotiateActionCfg.getImplementationClass(),
                    INegotiationAction.class );

            LOG.debug( "Negotiation action instantiated." );
        }
        negotiateAction.setHandlerContext( context );

        //
        // create the generic agreement factory action.
        //
        AgreementFactoryAction action =
            new AgreementFactoryAction( templateAction, agreementAction, negotiateAction );

        //
        // set the name for this action
        //
        action.setName( configuration.getName() );

        //
        // specify, whether or not sessions are supported by this
        // action block
        //
        action.setUseSession( configuration.getUseSession() );

        LOG.debug( LogMessage.getMessage( "Factory action supports wsag4j session: {0}",
            configuration.getUseSession() ) );

        LOG.debug( LogMessage.getMessage( "Factory action {0} successfully instantiated.",
            configuration.getName() ) );

        return action;
        // end-user-code
    }

    private IActionHandler loadAction( String className, Class<?> interfaceClass ) throws Exception
    {
        try
        {
            Object instance = Class.forName( className ).newInstance();

            if ( !interfaceClass.isInstance( instance ) )
            {
                String msgText = "Class {0} is not an instance of {1}.";
                String message =
                    LogMessage.format( msgText, className, interfaceClass.getName() );

                throw new Exception( message );
            }

            if ( instance instanceof IActionHandler )
            {
                return (IActionHandler) instance;
            }

            String msgText = "Action {0} is not an instance of {1}.";
            String message =
                LogMessage.format( msgText, className, IActionHandler.class.getName() );

            throw new Exception( message );
        }
        catch ( ClassNotFoundException ex )
        {
            String msgText =
                "Action {0} could not be loaded. Reason: Class {1} could not be found.";
            String message = LogMessage.format( msgText, className, ex.getMessage() );

            throw new Exception( message );
        }
        catch ( Exception ex )
        {
            String msgText = "Action {0} could not be loaded. Reason: {1}";
            String message = LogMessage.format( msgText, className, ex.getMessage() );

            throw new Exception( message );
        }
    }

}
