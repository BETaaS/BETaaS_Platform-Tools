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

import java.util.Map;

import org.apache.xmlbeans.XmlObject;
import org.ogf.graap.wsag.api.Agreement;
import org.ogf.graap.wsag.server.api.IAgreementContext;

/**
 * SimpleActionContext
 * 
 * @author Oliver Waeldrich
 * 
 */
public class AgreementContext
    implements IAgreementContext
{

    private final Agreement agreement;

    /**
     * Creates a new execution context for an agreement instance.
     * 
     * @param agreement
     *            existing agreement instance
     */
    public AgreementContext( Agreement agreement )
    {
        this.agreement = agreement;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Agreement getAgreement()
    {
        return agreement;
    }

    /**
     * Persisted Execution properties map (typed as String/XMLObject) to execution context of the agreement.
     * This method returns the persisted execution properties of the associated agreement instance.
     * 
     * @return the persisted execution properties of the associated agreement instance
     * 
     * @see Agreement#getAgreementInstance()
     * @see org.ogf.graap.wsag.api.types.AbstractAgreementType#getExecutionContext()
     */
    @Override
    public Map<String, XmlObject> getExecutionProperties()
    {
        return agreement.getExecutionContext();
    }

    /**
     * Persisted Execution properties map (typed as String/XMLObject) to execution context of the agreement.
     * This method alters the persisted execution properties of the associated agreement instance.
     * 
     * @param properties
     *            the persisted execution properties to set
     * 
     * @see Agreement#getAgreementInstance()
     * @see org.ogf.graap.wsag.api.types.AbstractAgreementType#getExecutionContext()
     */
    @Override
    public void setExecutionProperties( Map<String, XmlObject> properties )
    {
        synchronized ( agreement.getExecutionContext() )
        {
            agreement.getExecutionContext().clear();
            agreement.getExecutionContext().putAll( properties );
        }
    }

    /**
     * Transient execution properties map (typed as String/Object) to execution context of the agreement. This
     * method returns the transient execution properties of the associated agreement instance that are not
     * persisted.
     * 
     * @return the transient execution properties of the associated agreement instance
     * 
     * @see Agreement#getAgreementInstance()
     * @see org.ogf.graap.wsag.api.types.AbstractAgreementType#getExecutionContext()
     */
    @Override
    public Map<String, Object> getTransientExecutionProperties()
    {
        return agreement.getTransientExecutionContext();
    }

    /**
     * Transient Execution properties map (typed as String/Object) to execution context of the agreement. This
     * method alters the transient execution properties of the associated agreement instance.
     * 
     * @param properties
     *            the transient execution properties to set
     * 
     * @see Agreement#getAgreementInstance()
     * @see org.ogf.graap.wsag.api.types.AbstractAgreementType#getExecutionContext()
     */
    @Override
    public void setTransientExecutionProperties( Map<String, Object> properties )
    {
        synchronized ( agreement.getTransientExecutionContext() )
        {
            agreement.getTransientExecutionContext().clear();
            agreement.getTransientExecutionContext().putAll( properties );
        }
    }

}
