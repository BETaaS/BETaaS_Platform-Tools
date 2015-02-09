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
package org.ogf.graap.wsag.server.actions;

import org.ogf.graap.wsag.api.AgreementFactoryContext;
import org.ogf.graap.wsag.server.api.WsagSession;
import org.ogf.graap.wsag4j.types.configuration.ImplementationConfigurationType;

/**
 * <!-- begin-UML-doc -->
 * <p>
 * An action handler context contains information about the environment in which a specific action handler is
 * excecuted.
 * </p>
 * <!-- end-UML-doc -->
 * 
 * @author Oliver Waeldrich
 * @generated "UML to Java (com.ibm.xtools.transform.uml2.java5.internal.UML2JavaTransform)"
 */
public interface IActionHandlerContext
{
    /**
     * <!-- begin-UML-doc --> This is the shared context of the agreement factory. All action handler executed
     * in a specific factory have access to this context. The agreement factory may store shared objects at
     * initialization time&nbsp;in this context. <!-- end-UML-doc -->
     * 
     * @return the agreement factory context
     * 
     * @generated "UML to Java (com.ibm.xtools.transform.uml2.java5.internal.UML2JavaTransform)"
     */
    AgreementFactoryContext getFactoryContext();

    /**
     * <!-- begin-UML-doc --> This is the configuration of the handler instance. The handler configuration is
     * part of the engine configuration and may contain additional information that is required by the handler
     * to function properly, e.g. in that way additional configuration information can be provided to
     * initialize the handler properly. <!-- end-UML-doc -->
     * 
     * @return the action handler configuration
     * 
     * @generated "UML to Java (com.ibm.xtools.transform.uml2.java5.internal.UML2JavaTransform)"
     */
    ImplementationConfigurationType getHandlerConfiguration();

    /**
     * Returns the current WSAG4J session object when used, otherwise null.
     * 
     * @return the session object
     * 
     * @deprecated
     */
    @Deprecated
    WsagSession getSession();

    /**
     * Sets the WSAG4J session object when available, otherwise null.
     * 
     * @param session
     *            the session object
     * 
     * @deprecated
     */
    @Deprecated
    void setSession( WsagSession session );
}