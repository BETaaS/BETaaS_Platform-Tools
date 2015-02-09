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
package org.ogf.graap.wsag.samples;

import org.apache.log4j.Logger;
import org.ogf.graap.wsag.api.Agreement;
import org.ogf.graap.wsag.api.AgreementOffer;
import org.ogf.graap.wsag.api.exceptions.AgreementFactoryException;
import org.ogf.graap.wsag.server.actions.AbstractCreateAgreementAction;
import org.ogf.graap.wsag.server.actions.ActionInitializationException;
import org.ogf.schemas.graap.wsAgreement.ServiceDescriptionTermType;

/**
 * Sample1CreateAgreementAction
 * 
 * @author Oliver Waeldrich
 * 
 */
// START SNIPPET: Sample1NegotiateAgreementAction
public class Sample1CreateAgreementAction extends AbstractCreateAgreementAction
{

    private static Logger log = Logger.getLogger( Sample1CreateAgreementAction.class );

    /**
     * {@inheritDoc}
     * 
     * @see org.ogf.graap.wsag.server.actions.ICreateAgreementAction#createAgreement(org.ogf.graap.wsag.api.AgreementOffer)
     */
    public Agreement createAgreement( AgreementOffer offer ) throws AgreementFactoryException
    {
        // we have to check if terms are set
        if ( ( offer.getTerms() == null ) || ( offer.getTerms().getAll() == null ) )
        {
            String message = "Offer does not contain any terms.";
            log.error( message );

            throw new AgreementFactoryException( message );
        }

        // now we get the SDTs
        ServiceDescriptionTermType[] sdts = offer.getTerms().getAll().getServiceDescriptionTermArray();
        if ( ( sdts != null ) && ( sdts.length == 1 ) && ( sdts[0].getName().equals( "TEST_EXCEPTION" ) ) )
        {
            // if found a SDT named TEST_EXCEPTION, we fire the Exception
            throw new AgreementFactoryException( "No ServiceDescriptionTerms defined." );
        }
        //
        // Implement the SLA specific logic, e.g. reserve resources and start a service
        //
        // myResources.reserve();
        // myServer.start();
        //
        
        //
        // At last, create an agreement instance and return it
        //

        SampleAgreement agreement = new SampleAgreement( offer );
        return agreement;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ogf.graap.wsag.server.actions.IAction#initialize()
     */
    @Override
    public void initialize() throws ActionInitializationException
    {
        // nothing to do here
    }
}
// END SNIPPET: Sample1NegotiateAgreementAction
