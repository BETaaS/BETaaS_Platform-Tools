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
package org.ogf.graap.wsag.server.monitoring;

import java.util.Map;

import org.apache.xmlbeans.XmlObject;
import org.ogf.graap.wsag.server.accounting.IAccountingSystem;
import org.ogf.schemas.graap.wsAgreement.ServiceTermStateType;

/**
 * IMonitoringContext
 * 
 * The monitoring context holds the current service term states of the monitoring process. It is used by the @see
 * ServiceTermStateMonitor to update the service term states, and by the @see AgreementMonitor to retrieve the
 * actual service term states.
 * 
 * @author Oliver Waeldrich
 * 
 */
public interface IMonitoringContext
{
    /**
     * Key to look up the {@link IAgreementContext} from the transient monitoring properties.
     * 
     * @see #getTransientProperties()
     */
    String WSAG4J_AGREEMENT_EXECUTION_CONTEXT = "wsag4j.context.agreement.execution";

    /**
     * 
     * @return the properties defined for this monitoring context
     */
    Map<String, XmlObject> getProperties();

    /**
     * @param properties
     *            the monitoring properties to set
     */
    void setProperties( Map<String, XmlObject> properties );

    /**
     * 
     * @return the transient properties defined for this monitoring context
     */
    Map<String, Object> getTransientProperties();

    /**
     * @param properties
     *            the monitoring transient properties to set
     */
    void setTransientProperties( Map<String, Object> properties );

    /**
     * Adds the given service term state for monitoring to the context.
     * 
     * @param state
     *            the state to add
     */
    void addServiceTemState( ServiceTermStateType state );

    /**
     * Adds a new service term state with the given name for monitoring to the context.
     * 
     * @param name
     *            the name of the service term state
     */
    void addServiceTemState( String name );

    /**
     * Retrieves all ServiceTermStates monitored in this context.
     * 
     * @return an array of ServiceTermStates monitored in this context
     */
    ServiceTermStateType[] getServiceTermStates();

    /**
     * Retrieves a ServiceTermState identified by the given name.
     * 
     * @param name
     *            the name of the ServiceTerm
     * 
     * @return the state of the ServiceTerm
     */
    ServiceTermStateType getServiceTermStateByName( String name );

    /**
     * Adds the given service term state for monitoring to the context.
     * 
     * @param states
     *            the state array to set
     */
    void setServiceTemState( ServiceTermStateType[] states );

    /**
     * Adds a service term monitoring handler to this context.
     * 
     * @param handler
     *            the handler to add
     */
    void addMonitoringHandler( IServiceTermMonitoringHandler handler );

    /**
     * Sets the service term monitoring handler for this context.
     * 
     * @param handler
     *            the handlers to set
     */
    void setMonitoringHandler( IServiceTermMonitoringHandler[] handler );

    /**
     * Removes a service term monitoring handler from this context.
     * 
     * @param handler
     *            the handler to remove
     */
    void removeMonitoringHandler( IServiceTermMonitoringHandler handler );

    /**
     * Gets all service term monitoring handler for this context.
     * 
     * @return the service term monitoring handler for this context
     */
    IServiceTermMonitoringHandler[] getMonitoringHandler();

    /**
     * Creates and returns a copy of this object.
     * 
     * @return a coned copy of this instance
     * 
     * @throws CloneNotSupportedException
     *             indicates that the implementation does not support cloning
     */
    Object clone() throws CloneNotSupportedException;

    /**
     * Sets the accounting system that is used with this monitoring context.
     * 
     * @param system
     *            the accounting system to set
     */
    void setAccountingSystem( IAccountingSystem system );

    /**
     * Retrieves the accounting system that is used with this monitoring context.
     * 
     * @return the registered accounting systems
     */
    IAccountingSystem getAccountingSystem();
}
