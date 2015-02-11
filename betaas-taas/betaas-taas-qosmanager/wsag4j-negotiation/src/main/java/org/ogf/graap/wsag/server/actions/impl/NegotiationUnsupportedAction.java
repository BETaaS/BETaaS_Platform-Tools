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

import java.text.MessageFormat;
import java.util.Map;

import org.ogf.graap.wsag.api.exceptions.NegotiationException;
import org.ogf.graap.wsag.server.actions.AbstractNegotiationAction;
import org.ogf.graap.wsag.server.actions.ActionInitializationException;
import org.ogf.schemas.graap.wsAgreement.negotiation.NegotiationOfferType;

/**
 * NegotiationUnsupportedAction
 * 
 * @author Oliver Waeldrich
 * 
 */
public class NegotiationUnsupportedAction extends AbstractNegotiationAction
{

    /**
     * <!-- begin-UML-doc --> <!-- end-UML-doc -->
     * 
     * @throws ActionInitializationException
     *             indicates an error during initialization
     * 
     * @generated "UML to Java (com.ibm.xtools.transform.uml2.java5.internal.UML2JavaTransform)"
     */
    @Override
    public void initialize() throws ActionInitializationException
    {
        // begin-user-code

        // end-user-code
    }

    /**
     * <!-- begin-UML-doc --> <!-- end-UML-doc -->
     * 
     * {@inheritDoc}
     * 
     * @param quote
     *            the negotiation offer to process
     * @param context
     *            the shared invocation context
     * @return the generated counter offers
     * 
     * @generated "UML to Java (com.ibm.xtools.transform.uml2.java5.internal.UML2JavaTransform)"
     */
    public NegotiationOfferType[] negotiate( NegotiationOfferType quote, Map<String, Object> context )
        throws NegotiationException
    {
        // begin-user-code
        String name = null;
        String version = null;

        try
        {
            name = quote.getContext().getTemplateName();
            version = quote.getContext().getTemplateId();
        }
        catch ( Exception e )
        {
            name = ( name == null ) ? "unspecified" : name;
            version = ( version == null ) ? "unspecified" : version;
        }

        String message = "Negotiation is not supported for template [name: {0} id: {1}]";
        throw new UnsupportedOperationException( MessageFormat.format( message,
            new Object[] { name, version } ) );
        // end-user-code
    }

}
