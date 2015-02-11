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

import org.ogf.graap.wsag.api.AgreementFactoryContext;
import org.ogf.graap.wsag.server.actions.IActionHandlerContext;
import org.ogf.graap.wsag.server.api.WsagSession;
import org.ogf.graap.wsag4j.types.configuration.ImplementationConfigurationType;

/**
 * <!-- begin-UML-doc --> <!-- end-UML-doc -->
 * 
 * @author Oliver Waeldrich
 * @generated "UML to Java (com.ibm.xtools.transform.uml2.java5.internal.UML2JavaTransform)"
 */
public class HandlerContext
    implements IActionHandlerContext
{
    /**
     * <!-- begin-UML-doc --> <!-- end-UML-doc -->
     * 
     * @generated "UML to Java (com.ibm.xtools.transform.uml2.java5.internal.UML2JavaTransform)"
     */
    private AgreementFactoryContext factoryContext;

    /**
     * {@inheritDoc}
     * 
     * @see IActionHandlerContext#getFactoryContext()
     * 
     * @generated "UML to Java (com.ibm.xtools.transform.uml2.java5.internal.UML2JavaTransform)"
     */
    public AgreementFactoryContext getFactoryContext()
    {
        // begin-user-code
        return factoryContext;
        // end-user-code
    }

    /**
     * <!-- begin-UML-doc --> <!-- end-UML-doc -->
     * 
     * @generated "UML to Java (com.ibm.xtools.transform.uml2.java5.internal.UML2JavaTransform)"
     */
    private ImplementationConfigurationType handlerConfiguration;

    private WsagSession session;

    /**
     * <!-- begin-UML-doc --> <!-- end-UML-doc -->
     * 
     * @param context
     *            sets the agreement factory context
     * 
     * @generated "UML to Java (com.ibm.xtools.transform.uml2.java5.internal.UML2JavaTransform)"
     */
    public void setFactoryContext( AgreementFactoryContext context )
    {
        // begin-user-code
        factoryContext = context;
        // end-user-code
    }

    /**
     * <!-- begin-UML-doc --> <!-- end-UML-doc -->
     * 
     * @param config
     *            sets the configuration for this particular handler
     * 
     * @generated "UML to Java (com.ibm.xtools.transform.uml2.java5.internal.UML2JavaTransform)"
     */
    public void setHandlerConfiguration( ImplementationConfigurationType config )
    {
        // begin-user-code
        handlerConfiguration = config;
        // end-user-code
    }

    /**
     * {@inheritDoc}
     * 
     * @see IActionHandlerContext#getHandlerConfiguration()
     * 
     * @generated "UML to Java (com.ibm.xtools.transform.uml2.java5.internal.UML2JavaTransform)"
     */
    public ImplementationConfigurationType getHandlerConfiguration()
    {
        // begin-user-code
        return handlerConfiguration;
        // end-user-code
    }

    /**
     * {@inheritDoc}
     */
    public WsagSession getSession()
    {
        return session;
    }

    /**
     * {@inheritDoc}
     */
    public void setSession( WsagSession session )
    {
        this.session = session;
    }
}