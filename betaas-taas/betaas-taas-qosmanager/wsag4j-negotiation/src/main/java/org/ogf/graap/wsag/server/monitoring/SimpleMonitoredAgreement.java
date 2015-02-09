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

import org.ogf.graap.wsag.api.AgreementOffer;
import org.ogf.graap.wsag.api.types.AbstractAgreementType;
import org.ogf.graap.wsag.server.api.AgreementTerminationHandler;
import org.ogf.schemas.graap.wsAgreement.TerminateInputType;

/**
 * TODO: use MonitorableAgreement as default agreement implementation
 * 
 * This class will be used in future as default agreement implementation. There will be a re-factoring of the
 * WSAG4J API in version 2.
 * 
 * This class implements an agreement that is used in conjunction with a {@link MonitorableAgreement}. When a
 * {@link SimpleMonitoredAgreement} is instantiated the {@link MonitorableAgreement} uses is used as default
 * agreement implementation.
 * 
 * @author owaeld
 * 
 */
public class SimpleMonitoredAgreement extends AbstractAgreementType
{

    private final AgreementTerminationHandler terminationHandler;

    private MonitorableAgreement monitor;

    /**
     * Creates a simple monitored agreement implementation that invokes the
     * {@link AgreementTerminationHandler} when the agreement's terminate method is called. the termination
     * handler implements the domain specific logic to terminate this particular agreement instance.
     * 
     * @param offer
     *            the offer used to create the agreement
     * 
     * @param terminationHandler
     *            the agreement termination handler
     */
    public SimpleMonitoredAgreement( AgreementOffer offer, AgreementTerminationHandler terminationHandler )
    {
        super( offer );
        this.terminationHandler = terminationHandler;
    }

    // @formatter:off
    /**
     * The terminate method invokes the {@link AgreementTerminationHandler#terminate(TerminateInputType, 
     * org.ogf.graap.wsag.server.api.IAgreementContext)} method. The {@link AgreementTerminationHandler} 
     * implements the domain-specific logic to terminate an agreement instance.
     * 
     * @param reason
     *            the agreement termination reason
     * 
     * @see org.ogf.graap.wsag.api.Agreement#terminate(org.ogf.schemas.graap.wsAgreement.TerminateInputType)
     */
    // @formatter:on
    @Override
    public void terminate( TerminateInputType reason )
    {
        terminationHandler.terminate( reason, monitor.getMonitoringContext() );
    }

    // @formatter:off
    /**
     * Sets the associated monitorable agreement instance that uses this implementation. This property is a
     * backward reference to a {@link MonitorableAgreement}. This reference is used to resolve the agreement
     * execution context when the terminate method is called. The terminate method essentially invokes the
     * {@link AgreementTerminationHandler#terminate(TerminateInputType, 
     * org.ogf.graap.wsag.server.api.IAgreementContext)} method.
     * 
     * @param monitorable
     *            the monitorable agreement to set
     * 
     * @see AgreementTerminationHandler#terminate(TerminateInputType,
     *      org.ogf.graap.wsag.server.api.IAgreementContext)
     * @see MonitorableAgreement#getExecutionContext()
     */
    // @formatter:on
    public void setMonitorableAgreement( MonitorableAgreement monitorable )
    {
        this.monitor = monitorable;
    }
}
