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

/**
 * IServiceTermMonitor
 * 
 * Implements a monitoring handler that updates the state of one or a set of service terms. The monitoring
 * handler is invoked by the {@link MonitorableAgreement} for each monitoring interval. It implements the
 * domain specific logic to retrieve the state of a particular service term.
 * 
 * @see MonitorableAgreement
 * 
 * @author Oliver Waeldrich
 * 
 */
public interface IServiceTermMonitoringHandler
{

    /**
     * The monitoring handler implements the domain specific logic to retrieve the state of a particular
     * service term (or a set of service terms). The service term states can be retrieved via the monitoring
     * context. the monitoring context can also be used to store and access variables from different
     * monitoring handlers and store the over multiple invocations.
     * 
     * @param monitoringContext
     *            the monitoring context
     * 
     * @throws Exception
     *             Indicates an error in the monitoring handler. The agreement states will not be updated
     *             during this monitoring cycle.
     */
    void monitor( IMonitoringContext monitoringContext ) throws Exception;

    /*
     * TODO the monitoring handler should have a method
     * IServiceTermMonitoringHandler#initialize(IMonitoringContext)
     */
}
