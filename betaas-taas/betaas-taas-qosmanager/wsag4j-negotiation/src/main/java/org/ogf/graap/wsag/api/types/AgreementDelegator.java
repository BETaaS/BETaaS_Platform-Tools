/* 
 * Copyright (c) 2012, Fraunhofer-Gesellschaft
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
package org.ogf.graap.wsag.api.types;

import java.util.Map;
import java.util.Observer;

import org.apache.xmlbeans.XmlObject;
import org.ogf.graap.wsag.api.Agreement;
import org.ogf.schemas.graap.wsAgreement.AgreementContextType;
import org.ogf.schemas.graap.wsAgreement.AgreementPropertiesType;
import org.ogf.schemas.graap.wsAgreement.AgreementStateType;
import org.ogf.schemas.graap.wsAgreement.GuaranteeTermStateType;
import org.ogf.schemas.graap.wsAgreement.ServiceTermStateType;
import org.ogf.schemas.graap.wsAgreement.TermTreeType;
import org.ogf.schemas.graap.wsAgreement.TerminateInputType;

/**
 * Default implementation of an agreement delegator. Subclasses may overwrite methods as needed.
 * 
 * @author owaeld
 * 
 */
public class AgreementDelegator
    implements Agreement
{

    private Agreement agreement;

    /**
     * Creates a new instance of the delegator. All calls to this instance will be delegated to the delegation
     * target.
     * 
     * @param agreement
     *            delegation target
     */
    public AgreementDelegator( Agreement agreement )
    {
        this.agreement = agreement;
    }

    /**
     * Returns the delegation target of this instance.
     * 
     * @return the delegation target
     */
    protected Agreement getDelegator()
    {
        return agreement;
    }

    /**
     * Sets the delegation target for this instance.
     * 
     * @param delegator
     *            the delegation target to set
     */
    protected void setDelegator( Agreement delegator )
    {
        this.agreement = delegator;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ogf.graap.wsag.api.Agreement#getName()
     */
    @Override
    public String getName()
    {
        return agreement.getName();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ogf.graap.wsag.api.Agreement#getAgreementId()
     */
    @Override
    public String getAgreementId()
    {
        return agreement.getAgreementId();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ogf.graap.wsag.api.Agreement#getContext()
     */
    @Override
    public AgreementContextType getContext()
    {
        return agreement.getContext();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ogf.graap.wsag.api.Agreement#getTerms()
     */
    @Override
    public TermTreeType getTerms()
    {
        return agreement.getTerms();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ogf.graap.wsag.api.Agreement#terminate(org.ogf.schemas.graap.wsAgreement.TerminateInputType)
     */
    @Override
    public void terminate( TerminateInputType reason )
    {
        agreement.terminate( reason );
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ogf.graap.wsag.api.Agreement#getState()
     */
    @Override
    public AgreementStateType getState()
    {
        return agreement.getState();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ogf.graap.wsag.api.Agreement#getGuaranteeTermStates()
     */
    @Override
    public GuaranteeTermStateType[] getGuaranteeTermStates()
    {
        return agreement.getGuaranteeTermStates();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ogf.graap.wsag.api.Agreement#getServiceTermStates()
     */
    @Override
    public ServiceTermStateType[] getServiceTermStates()
    {
        return agreement.getServiceTermStates();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ogf.graap.wsag.api.Agreement#validate()
     */
    @Override
    public boolean validate()
    {
        return agreement.validate();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ogf.graap.wsag.api.Agreement#notifyReload(java.util.Map)
     */
    @Override
    public void notifyReload( Map<String, XmlObject> executionCtx )
    {
        agreement.notifyReload( executionCtx );
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ogf.graap.wsag.api.Agreement#setAgreementId(java.lang.String)
     */
    @Override
    public void setAgreementId( String agreementId )
    {
        agreement.setAgreementId( agreementId );
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ogf.graap.wsag.api.Agreement#setContext(org.ogf.schemas.graap.wsAgreement.AgreementContextType)
     */
    @Override
    public void setContext( AgreementContextType context )
    {
        agreement.setContext( context );
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ogf.graap.wsag.api.Agreement#setName(java.lang.String)
     */
    @Override
    public void setName( String name )
    {
        agreement.setName( name );
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ogf.graap.wsag.api.Agreement#setTerms(org.ogf.schemas.graap.wsAgreement.TermTreeType)
     */
    @Override
    public void setTerms( TermTreeType terms )
    {
        agreement.setTerms( terms );
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ogf.graap.wsag.api.Agreement#setState(org.ogf.schemas.graap.wsAgreement.AgreementStateType)
     */
    @Override
    public void setState( AgreementStateType agreementState )
    {
        agreement.setState( agreementState );
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ogf.graap.wsag.api.Agreement#setGuaranteeTermStates(org.ogf.schemas.graap.wsAgreement.GuaranteeTermStateType[])
     */
    @Override
    public void setGuaranteeTermStates( GuaranteeTermStateType[] guaranteeTermStateList )
    {
        agreement.setGuaranteeTermStates( guaranteeTermStateList );
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ogf.graap.wsag.api.Agreement#setServiceTermStates(org.ogf.schemas.graap.wsAgreement.ServiceTermStateType[])
     */
    @Override
    public void setServiceTermStates( ServiceTermStateType[] serviceTermStateList )
    {
        agreement.setServiceTermStates( serviceTermStateList );
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ogf.graap.wsag.api.Agreement#getXMLObject()
     */
    @Override
    public AgreementPropertiesType getXMLObject()
    {
        return agreement.getXMLObject();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ogf.graap.wsag.api.Agreement#setXmlObject(org.ogf.schemas.graap.wsAgreement.AgreementPropertiesType)
     */
    @Override
    public void setXmlObject( AgreementPropertiesType properties )
    {
        agreement.setXmlObject( properties );
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ogf.graap.wsag.api.Agreement#getExecutionContext()
     */
    @Override
    public Map<String, XmlObject> getExecutionContext()
    {
        return agreement.getExecutionContext();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ogf.graap.wsag.api.Agreement#getTransientExecutionContext()
     */
    @Override
    public Map<String, Object> getTransientExecutionContext()
    {
        return agreement.getTransientExecutionContext();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ogf.graap.wsag.api.Agreement#getImplementationClass()
     */
    @Override
    public Class<?> getImplementationClass()
    {
        return agreement.getImplementationClass();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ogf.graap.wsag.api.Agreement#addObserver(java.util.Observer)
     */
    @Override
    public void addObserver( Observer o )
    {
        agreement.addObserver( o );
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ogf.graap.wsag.api.Agreement#notifyObservers()
     */
    @Override
    public void notifyObservers()
    {
        agreement.notifyObservers();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ogf.graap.wsag.api.Agreement#notifyObservers(java.lang.Object)
     */
    @Override
    public void notifyObservers( Object arg )
    {
        agreement.notifyObservers( arg );
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ogf.graap.wsag.api.Agreement#hasChanged()
     */
    @Override
    public boolean hasChanged()
    {
        return agreement.hasChanged();
    }
}
