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
package org.ogf.graap.wsag.server.persistence.impl;

import java.util.Map;
import java.util.Observer;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlObject;
import org.ogf.graap.wsag.api.Agreement;
import org.ogf.graap.wsag.server.engine.WsagEngine;
import org.ogf.graap.wsag.server.persistence.PersistentAgreement;
import org.ogf.schemas.graap.wsAgreement.AgreementContextType;
import org.ogf.schemas.graap.wsAgreement.AgreementPropertiesType;
import org.ogf.schemas.graap.wsAgreement.AgreementStateType;
import org.ogf.schemas.graap.wsAgreement.GuaranteeTermStateType;
import org.ogf.schemas.graap.wsAgreement.ServiceTermStateType;
import org.ogf.schemas.graap.wsAgreement.TermTreeType;
import org.ogf.schemas.graap.wsAgreement.TerminateInputType;
import org.w3.x2005.x08.addressing.EndpointReferenceType;

/**
 * Default implementation of a persistent agreement. Essentially this agreement is not persisted at all. It
 * implements the {@link PersistentAgreement} interface and is superseded by the
 * {@link DatabasePersistentAgreement}.
 * 
 * @author Oliver Waeldrich
 * 
 */
public class SimplePersistentAgreement
    implements PersistentAgreement
{
	
	private static final Logger LOG = Logger.getLogger( WsagEngine.class );

    private final Agreement agreement;

    private final EndpointReferenceType epr;

    /**
     * 
     * @param agreement
     *            the agreement instance
     * 
     * @param epr
     *            the agreement EPR
     */
    public SimplePersistentAgreement( Agreement agreement, EndpointReferenceType epr )
    {
        this.agreement = agreement;
        this.epr = epr;
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
     * {@inheritDoc}
     */
    @Override
    public EndpointReferenceType getAgreementEPR()
    {
        return epr;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void load() throws Exception
    {
        //
        // nothing to do in simple implementation
        //
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void save() throws Exception
    {
    	
    	LOG.info( "Nothing to do here" );
    	
        //
        // nothing to do in simple implementation
        //
    }

    @Override
    public void delete() throws Exception
    {
    }

    public String getName()
    {
        return agreement.getName();
    }

    public String getAgreementId()
    {
        return agreement.getAgreementId();
    }

    public AgreementContextType getContext()
    {
        return agreement.getContext();
    }

    public TermTreeType getTerms()
    {
        return agreement.getTerms();
    }

    public void terminate( TerminateInputType reason )
    {
        agreement.terminate( reason );
    }

    public AgreementStateType getState()
    {
        return agreement.getState();
    }

    public GuaranteeTermStateType[] getGuaranteeTermStates()
    {
        return agreement.getGuaranteeTermStates();
    }

    public ServiceTermStateType[] getServiceTermStates()
    {
        return agreement.getServiceTermStates();
    }

    public boolean validate()
    {
        return agreement.validate();
    }

    public void notifyReload( Map<String, XmlObject> executionCtx )
    {
        agreement.notifyReload( executionCtx );
    }

    public void setAgreementId( String agreementId )
    {
        agreement.setAgreementId( agreementId );
    }

    public void setContext( AgreementContextType context )
    {
        agreement.setContext( context );
    }

    public void setName( String name )
    {
        agreement.setName( name );
    }

    public void setTerms( TermTreeType terms )
    {
        agreement.setTerms( terms );
    }

    public void setState( AgreementStateType agreementState )
    {
        agreement.setState( agreementState );
    }

    public void setGuaranteeTermStates( GuaranteeTermStateType[] guaranteeTermStateList )
    {
        agreement.setGuaranteeTermStates( guaranteeTermStateList );
    }

    public void setServiceTermStates( ServiceTermStateType[] serviceTermStateList )
    {
        agreement.setServiceTermStates( serviceTermStateList );
    }

    public AgreementPropertiesType getXMLObject()
    {
        return agreement.getXMLObject();
    }

    public void setXmlObject( AgreementPropertiesType properties )
    {
        agreement.setXmlObject( properties );
    }

    public Map<String, XmlObject> getExecutionContext()
    {
        return agreement.getExecutionContext();
    }

    public Map<String, Object> getTransientExecutionContext()
    {
        return agreement.getTransientExecutionContext();
    }

    public Class<?> getImplementationClass()
    {
        return agreement.getImplementationClass();
    }

    public void addObserver( Observer o )
    {
        agreement.addObserver( o );
    }

    public void notifyObservers()
    {
        agreement.notifyObservers();
    }

    public void notifyObservers( Object arg )
    {
        agreement.notifyObservers( arg );
    }

    public boolean hasChanged()
    {
        return agreement.hasChanged();
    }

}
