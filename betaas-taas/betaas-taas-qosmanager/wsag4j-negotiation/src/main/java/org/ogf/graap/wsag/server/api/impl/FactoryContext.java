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
package org.ogf.graap.wsag.server.api.impl;

import java.util.HashMap;

import org.ogf.graap.wsag.api.AgreementFactoryContext;
import org.ogf.graap.wsag.server.api.IAgreementFactory;

/**
 * <!-- begin-UML-doc --> The AgreementFactoryContext stores information that is accessible in the context of
 * agreement factory operations. <!-- end-UML-doc -->
 * 
 * @author Oliver Waeldrich
 * @generated "UML to Java (com.ibm.xtools.transform.uml2.java5.internal.UML2JavaTransform)"
 */
public class FactoryContext extends HashMap<Object, Object>
    implements AgreementFactoryContext
{

    private static final long serialVersionUID = 4940500027668164288L;

    /**
     * <!-- begin-UML-doc --> The agreement factory that this context belongs to. <!-- end-UML-doc -->
     * 
     * @generated "UML to Java (com.ibm.xtools.transform.uml2.java5.internal.UML2JavaTransform)"
     */
    private IAgreementFactory agreementFactory;

    /**
     * <!-- begin-UML-doc --> Each agreement factory has an associated context. This method provides a
     * backward reference to the agreement factory object. <!-- end-UML-doc -->
     * 
     * @return The agreement factory to which this context belongs to.
     * 
     * @generated "UML to Java (com.ibm.xtools.transform.uml2.java5.internal.UML2JavaTransform)"
     */
    @Override
    public IAgreementFactory getAgreementFactory()
    {
        // begin-user-code
        return agreementFactory;
        // end-user-code
    }

    /**
     * <!-- begin-UML-doc --> <!-- end-UML-doc -->
     * 
     * @param agreementFactory
     *            the agreementFactory to set
     * @generated "UML to Java (com.ibm.xtools.transform.uml2.java5.internal.UML2JavaTransform)"
     */
    public void setAgreementFactory( IAgreementFactory agreementFactory )
    {
        // begin-user-code
        this.agreementFactory = agreementFactory;
        // end-user-code
    }

    /**
     * <!-- begin-UML-doc --> Creates a new agreement factory context for a given factory. <!-- end-UML-doc
     * -->
     * 
     * @param agreementFactory
     *            Creates a new context for a given agreement factory.
     * 
     * @generated "UML to Java (com.ibm.xtools.transform.uml2.java5.internal.UML2JavaTransform)"
     */
    public FactoryContext( IAgreementFactory agreementFactory )
    {
        // begin-user-code
        this.agreementFactory = agreementFactory;
        // end-user-code
    }

}
