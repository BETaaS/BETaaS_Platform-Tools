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
package org.ogf.graap.wsag.server.actions;

import java.util.Map;

import org.ogf.graap.wsag.server.engine.GenericNegotiation;

/**
 * <!-- begin-UML-doc --> AbstractCreateAgreementAction
 * 
 * Abstract base class for agreement creation actions.
 * 
 * @author Oliver Waeldrich
 * 
 *         <!-- end-UML-doc -->
 * 
 * @author Oliver Waeldrich
 * @generated "UML to Java (com.ibm.xtools.transform.uml2.java5.internal.UML2JavaTransform)"
 */
public abstract class AbstractNegotiationAction
    implements INegotiationAction
{

    /**
     * <!-- begin-UML-doc --> <!-- end-UML-doc -->
     * 
     * @generated "UML to Java (com.ibm.xtools.transform.uml2.java5.internal.UML2JavaTransform)"
     */
    private IActionHandlerContext handlerContext;

    /**
     * <!-- begin-UML-doc --> Returns the context of this action handler.
     * 
     * {@inheritDoc} <!-- end-UML-doc -->
     * 
     * @return Returns the context of this action handler.
     * 
     * @generated "UML to Java (com.ibm.xtools.transform.uml2.java5.internal.UML2JavaTransform)"
     */
    @Override
    public IActionHandlerContext getHandlerContext()
    {
        // begin-user-code
        return handlerContext;
        // end-user-code
    }

    /**
     * <!-- begin-UML-doc --> Sets the context for this action handler.
     * 
     * {@inheritDoc} <!-- end-UML-doc -->
     * 
     * @param context
     * @generated "UML to Java (com.ibm.xtools.transform.uml2.java5.internal.UML2JavaTransform)"
     */
    @Override
    public void setHandlerContext( IActionHandlerContext context )
    {
        // begin-user-code
        this.handlerContext = context;
        // end-user-code
    }

    /**
     * <!-- begin-UML-doc --> Default implementation of action initialization. Subclasses may overwrite this
     * method in order to provide a custom initialization of a handler.
     * 
     * {@inheritDoc} <!-- end-UML-doc -->
     * 
     * @throws ActionInitializationException
     * @generated "UML to Java (com.ibm.xtools.transform.uml2.java5.internal.UML2JavaTransform)"
     */
    @Override
    public void initialize() throws ActionInitializationException
    {
        // begin-user-code

        // nothing to do here

        // end-user-code
    }

    /**
     * Returns the negotiation context for the current invocation.
     * 
     * @param invocationContext
     *            the invocation context provided to the negotiation action
     * 
     * @return the context of the negotiation instance
     */
    @SuppressWarnings( "unchecked" )
    public Map<String, Object> getNegotiationContext( Map invocationContext )
    {
        return (Map<String, Object>) invocationContext.get( GenericNegotiation.NEGOTIATION_CONTEXT );
    }
}
