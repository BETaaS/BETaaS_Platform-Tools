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
package org.ogf.graap.wsag.api;

import java.util.Map;
import java.util.Observer;

import org.apache.xmlbeans.XmlObject;
import org.ogf.schemas.graap.wsAgreement.AgreementContextType;
import org.ogf.schemas.graap.wsAgreement.AgreementPropertiesType;
import org.ogf.schemas.graap.wsAgreement.AgreementStateType;
import org.ogf.schemas.graap.wsAgreement.GuaranteeTermStateType;
import org.ogf.schemas.graap.wsAgreement.ServiceTermStateType;
import org.ogf.schemas.graap.wsAgreement.TermTreeType;
import org.ogf.schemas.graap.wsAgreement.TerminateInputType;

/**
 * Agreement interface used by the server module. It provides the required methods to access the properties of
 * the agreement and to terminate the agreement instance. Moreover, it provides access to the domains-specific
 * agreement implementation via the {@link #getAgreementInstance()} method.
 * 
 * @see AgreementFactory
 * @author Oliver Waeldrich
 */
public interface Agreement
{

    // definitions from the ArgreementPortType

    /**
     * Returns the agreement name.
     * 
     * @return the agreement name
     */
    String getName();

    /**
     * Returns the agreement id.
     * 
     * @return the agreement id
     */
    String getAgreementId();

    // /**
    // * Returns a domain-specific agreement implementation. The {@link AbstractAgreementType} implements the
    // * domain-specific termination method and stored the agreement properties. The
    // * {@link AbstractAgreementType#getXMLObject()} returns the actual properties of an agreement
    // * implementation. These properties must be valid at all time, i.e. if an agreement implementation
    // * overrides the {@link #getState()} method the implementation must make sure that the agreement
    // * properties are updated with the returned state.
    // *
    // * @return the agreement implementation
    // */
    // AbstractAgreementType getAgreementInstance();

    /**
     * @return the agreement context
     */
    AgreementContextType getContext();

    /**
     * @return the agreement terms
     */
    TermTreeType getTerms();

    /**
     * Terminates an agreement.
     * 
     * @param reason
     *            termination reason
     */
    void terminate( TerminateInputType reason );

    // definitions from the ArgreementStatePortType

    /**
     * @return the agreement state
     */
    AgreementStateType getState();

    /**
     * @return the agreement guarantee term states
     */
    GuaranteeTermStateType[] getGuaranteeTermStates();

    /**
     * @return the agreement service term states
     */
    ServiceTermStateType[] getServiceTermStates();

    /**
     * Validates the resource properties document of this instance.
     * 
     * @return true if the resource properties document is valid, otherwise false.
     */
    boolean validate();

    /**
     * This method allows an agreement instance to get notified after reload. An invocation of the
     * {@link #notifyReload(Map)} method sets the execution context (see {@link #getExecutionContext()}) of
     * the agreement and invokes the {@link #notifyReinitialized(Map)} method of this agreement instance.
     * 
     * @param executionCtx
     *            the current execution context
     */
    void notifyReload( Map<String, XmlObject> executionCtx );

    /**
     * Sets the ID of the agreement.
     * 
     * @param agreementId
     *            the agreement id to set
     */
    void setAgreementId( String agreementId );

    /**
     * Sets the context of the agreement.
     * 
     * @param context
     *            the agreement context to set
     */
    void setContext( AgreementContextType context );

    /**
     * Sets the name of the agreement.
     * 
     * @param name
     *            the agreement name to set
     */
    void setName( String name );

    /**
     * Sets the terms of the agreement.
     * 
     * @param terms
     *            the agreement terms to set
     */
    void setTerms( TermTreeType terms );

    /**
     * Sets the state of the agreement.
     * 
     * @param agreementState
     *            the agreement state to set
     */
    void setState( AgreementStateType agreementState );

    /**
     * Sets the guarantee term states of the agreement.
     * 
     * @param guaranteeTermStateList
     *            the guarantee term states to set
     */
    void setGuaranteeTermStates( GuaranteeTermStateType[] guaranteeTermStateList );

    /**
     * Sets the service term states of the agreement.
     * 
     * @param serviceTermStateList
     *            the service term states to set
     */
    void setServiceTermStates( ServiceTermStateType[] serviceTermStateList );

    /**
     * Returns the agreement properties as {@link XmlObject}.
     * 
     * @return an XML representation of the agreement properties document
     */
    AgreementPropertiesType getXMLObject();

    /**
     * Sets the properties for this agreement explicitly.
     * 
     * @param properties
     *            the agreement properties to set
     */
    void setXmlObject( AgreementPropertiesType properties );

    /**
     * Returns the persisted execution context for this agreement.
     * 
     * @return the agreement persisted execution context
     */
    Map<String, XmlObject> getExecutionContext();

    /**
     * Returns the transient execution context for this agreement.
     * 
     * @return the agreement transient execution context
     */
    Map<String, Object> getTransientExecutionContext();

    /**
     * Returns the class of the agreement implementation.
     * 
     * @return the implementation class
     */
    Class<?> getImplementationClass();

    /**
     * Adds a new observer to an agreement that gets notified of agreement state changes.
     * 
     * @param o
     *            the observer
     * 
     * @see java.util.Observable#addObserver(java.util.Observer)
     */
    void addObserver( Observer o );

    /**
     * 
     * @see java.util.Observable#notifyObservers()
     */
    void notifyObservers();

    /**
     * Notifies all registered observer of an agreement's state change.
     * 
     * @param arg
     *            notification message
     * 
     * @see java.util.Observable#notifyObservers(java.lang.Object)
     */
    void notifyObservers( Object arg );

    /**
     * Returns true if the content of the agreement has changed, otherwise false.
     * 
     * @return true if the agreement has changed, i.e. runtime state was updated
     * 
     * @see java.util.Observable#hasChanged()
     */
    boolean hasChanged();

}
