/* 
 * Copyright (c) 2011, Fraunhofer-Gesellschaft
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
package org.ogf.graap.wsag.samples;

import org.apache.log4j.Logger;
import org.ogf.graap.wsag.api.AgreementOffer;
import org.ogf.graap.wsag.api.types.AbstractAgreementType;
import org.ogf.schemas.graap.wsAgreement.AgreementPropertiesType;
import org.ogf.schemas.graap.wsAgreement.AgreementStateDefinition;
import org.ogf.schemas.graap.wsAgreement.AgreementStateDocument;
import org.ogf.schemas.graap.wsAgreement.AgreementStateType;
import org.ogf.schemas.graap.wsAgreement.GuaranteeTermStateDefinition;
import org.ogf.schemas.graap.wsAgreement.GuaranteeTermStateType;
import org.ogf.schemas.graap.wsAgreement.ServiceTermStateDefinition;
import org.ogf.schemas.graap.wsAgreement.ServiceTermStateType;
import org.ogf.schemas.graap.wsAgreement.TerminateInputType;
import org.w3c.dom.Node;

/**
 * Sample agreement implementation.
 * 
 * @author Oliver Waeldrich
 * 
 */
public class SampleAgreement extends AbstractAgreementType
{

    /**
     * constructs new agreement based on offer.
     * 
     * @param offer
     *            offer object
     */
    public SampleAgreement( AgreementOffer offer )
    {
        super( offer );
        setName( "SampleAgreement" );
    }

    private static Logger log = Logger.getLogger( SampleAgreement.class );

    /**
     * Creates an agreement with the given agreement properties document.
     * 
     * @param properties
     *            the agreement properties document
     */
    public SampleAgreement( AgreementPropertiesType properties )
    {
        super();
        this.agreementProperties = properties;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ogf.graap.wsag.api.Agreement#terminate(org.ogf.schemas.graap.wsAgreement.TerminateInputType)
     */
    public void terminate( TerminateInputType reason )
    {
        Node terminateReson = reason.getDomNode().getFirstChild();
        if ( terminateReson != null )
        {
            // find out what the reason is and take the appropriate action
            if ( log.isInfoEnabled() )
            {
                log.info( "The agreement was terminated for the following reason: " + reason.xmlText() );
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ServiceTermStateType[] getServiceTermStates()
    {
        // in order to add specific behavior when a property is read,
        // simply overwrite the method in question

        // first create a new ServiceTermStateDocument result array
        ServiceTermStateType[] state = new ServiceTermStateType[1];

        // now you can add some specific functionality e.g. querying a sub system
        // state[0] = mysystem.getState();

        // here we simply initialize the our result array
        state[0] = ServiceTermStateType.Factory.newInstance();

        // and do some update action on the state object
        for ( int i = 0; i < state.length; i++ )
        {
            state[i].setState( ServiceTermStateDefinition.READY );
        }

        // now we can return our state object;
        return state;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AgreementStateType getState()
    {
        // it is also possible to create a new state object, but in order to have
        // automatic validity checks enabled, the local state holder should be updated
        AgreementStateDocument stateDoc = AgreementStateDocument.Factory.newInstance();
        AgreementStateType state = stateDoc.addNewAgreementState();

        state.setState( AgreementStateDefinition.OBSERVED );

        // now we can return our state object;
        return stateDoc.getAgreementState();
    }

    /**
     * {@inheritDoc}
     */
    // START SNIPPET: GetServiceTermStates
    @Override
    public GuaranteeTermStateType[] getGuaranteeTermStates()
    {
        GuaranteeTermStateType[] state = new GuaranteeTermStateType[2];

        state[0] = GuaranteeTermStateType.Factory.newInstance();
        state[1] = GuaranteeTermStateType.Factory.newInstance();

        state[0].setState( GuaranteeTermStateDefinition.NOT_DETERMINED );
        state[0].setTermName( "term_0" );

        state[1].setState( GuaranteeTermStateDefinition.FULFILLED );
        state[1].setTermName( "term_1" );

        return state;
    }
    // END SNIPPET: GetServiceTermStates

}
