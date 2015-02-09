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

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.apache.xmlbeans.XmlObject;
import org.ogf.graap.wsag.server.accounting.IAccountingSystem;
import org.ogf.graap.wsag.server.accounting.SimpleAccountingSystemLogger;
import org.ogf.schemas.graap.wsAgreement.ServiceTermStateDefinition;
import org.ogf.schemas.graap.wsAgreement.ServiceTermStateDocument;
import org.ogf.schemas.graap.wsAgreement.ServiceTermStateType;

/**
 * MonitoringContext
 * 
 * @author Oliver Waeldrich
 * 
 */
public class MonitoringContext implements IMonitoringContext
{

    private Vector<ServiceTermStateType> serviceTermStates = new Vector<ServiceTermStateType>();

    private Vector<IServiceTermMonitoringHandler> monitoringHandler =
        new Vector<IServiceTermMonitoringHandler>();

    private IAccountingSystem accountingSystem = new SimpleAccountingSystemLogger();

    /**
     * Maps to {@link org.ogf.graap.wsag.api.types.AbstractAgreementType#getExecutionContext()} .
     */
    private HashMap<String, XmlObject> executionProperties = new HashMap<String, XmlObject>();

    /**
     * Maps to {@link org.ogf.graap.wsag.api.types.AbstractAgreementType#getTransientExecutionContext()} .
     */
    private Map<String, Object> transientExecutionProperties = new HashMap<String, Object>();

    /**
     * Adds a new service term state with the given name to the state monitor. The state is initialized as
     * NOT_READY.
     * 
     * @param name
     *            the name of the state the state to add
     */
    public void addServiceTemState( String name )
    {
        ServiceTermStateType state = ServiceTermStateDocument.Factory.newInstance().addNewServiceTermState();
        state.setTermName( name );
        state.setState( ServiceTermStateDefinition.NOT_READY );
        addServiceTemState( state );
    }

    /**
     * Adds a new service term state to the state monitor.
     * 
     * @param state
     *            the state to add
     */
    public void addServiceTemState( ServiceTermStateType state )
    {
        serviceTermStates.add( state );
    }

    /**
     * Returns an array of the registered service term states
     * 
     * @return the registered service term states
     */
    public ServiceTermStateType[] getServiceTermStates()
    {
        return serviceTermStates.toArray( new ServiceTermStateType[serviceTermStates.size()] );
    }

    /**
     * Removes a service term state from the state monitor.
     * 
     * @param state
     *            the state to remove
     */
    public void removeServiceTemState( ServiceTermStateType state )
    {
        serviceTermStates.remove( state );
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ogf.graap.wsag.server.monitoring.IMonitoringContext#setServiceTemState(ServiceTermStateType[])
     */
    public void setServiceTemState( ServiceTermStateType[] states )
    {
        serviceTermStates.clear();
        for ( int i = 0; i < states.length; i++ )
        {
            serviceTermStates.add( states[i] );
        }
    }

    /**
     * Returns the service term state with the given name, or null if no term state with this name is
     * registered.
     * 
     * @param name
     *            the name of the service term state
     * @return the service term state with the given name
     */
    public ServiceTermStateType getServiceTermStateByName( String name )
    {
        for ( int i = 0; i < serviceTermStates.size(); i++ )
        {
            ServiceTermStateType termState = serviceTermStates.get( i );

            if ( name.equals( termState.getTermName() ) )
            {
                return termState;
            }
        }

        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ogf.graap.wsag.server.monitoring.IMonitoringContext#getProperties()
     */
    public Map<String, XmlObject> getProperties()
    {
        return executionProperties;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ogf.graap.wsag.server.monitoring.IMonitoringContext#setProperties(java.util.Map)
     */
    public void setProperties( Map<String, XmlObject> properties )
    {
        executionProperties.clear();
        executionProperties.putAll( properties );
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ogf.graap.wsag.server.monitoring.IMonitoringContext#getTransientProperties()
     */
    public Map<String, Object> getTransientProperties()
    {
        return transientExecutionProperties;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ogf.graap.wsag.server.monitoring.IMonitoringContext#setTransientProperties(java.util.Map)
     */
    public void setTransientProperties( Map<String, Object> properties )
    {
        transientExecutionProperties = properties;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ogf.graap.wsag.server.monitoring.IMonitoringContext#addMonitoringHandler(IServiceTermMonitoringHandler)
     */
    public void addMonitoringHandler( IServiceTermMonitoringHandler handler )
    {
        monitoringHandler.add( handler );
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ogf.graap.wsag.server.monitoring.IMonitoringContext#getMonitoringHandler()
     */
    public IServiceTermMonitoringHandler[] getMonitoringHandler()
    {
        return monitoringHandler.toArray( new IServiceTermMonitoringHandler[monitoringHandler.size()] );
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ogf.graap.wsag.server.monitoring.IMonitoringContext#removeMonitoringHandler(IServiceTermMonitoringHandler)
     */
    public void removeMonitoringHandler( IServiceTermMonitoringHandler handler )
    {
        monitoringHandler.remove( handler );
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ogf.graap.wsag.server.monitoring.IMonitoringContext#setMonitoringHandler(IServiceTermMonitoringHandler[])
     */
    public void setMonitoringHandler( IServiceTermMonitoringHandler[] handler )
    {
        monitoringHandler.clear();
        for ( int i = 0; i < handler.length; i++ )
        {
            monitoringHandler.add( handler[i] );
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.util.HashMap#clone()
     */
    public Object clone()
    {
        IMonitoringContext currentMonitoringContext = new MonitoringContext();

        currentMonitoringContext.setProperties( getProperties() );
        currentMonitoringContext.setTransientProperties( getTransientProperties() );

        for ( int i = 0; i < serviceTermStates.size(); i++ )
        {
            ServiceTermStateType state = (ServiceTermStateType) serviceTermStates.get( i );
            currentMonitoringContext.addServiceTemState( (ServiceTermStateType) state.copy() );
        }

        for ( int i = 0; i < monitoringHandler.size(); i++ )
        {
            IServiceTermMonitoringHandler handler = (IServiceTermMonitoringHandler) monitoringHandler.get( i );
            currentMonitoringContext.addMonitoringHandler( handler );
        }

        currentMonitoringContext.setAccountingSystem( getAccountingSystem() );

        return currentMonitoringContext;
    }

    /**
     * Sets the accounting system.
     * 
     * @param accountingSystem
     *            the accountingSystem to set
     */
    public void setAccountingSystem( IAccountingSystem accountingSystem )
    {
        this.accountingSystem = accountingSystem;
    }

    /**
     * @return the accountingSystem
     */
    public IAccountingSystem getAccountingSystem()
    {
        return accountingSystem;
    }

}
