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

import org.ogf.graap.wsag.server.accounting.IAccountingSystem;
import org.ogf.graap.wsag4j.types.engine.GuaranteeEvaluationResultType;
import org.ogf.schemas.graap.wsAgreement.GuaranteeTermType;

/**
 * The guarantee evaluator assesses the state of each guarantee defined in an agreement based on the agreement
 * properties. The evaluator is invoked as part of the agreement monitoring process that is implemented by the
 * {@link MonitorableAgreement} class, after the service term states of the agreement were updated.
 * 
 * As a result of the guarantee evaluation process, the appropriate accounting events are propagated to an
 * accounting system.
 * 
 * @see MonitorableAgreement
 * @see IServiceTermMonitoringHandler
 * @see IAccountingSystem
 * 
 * @author Oliver Waeldrich
 * 
 */
public interface IGuaranteeEvaluator
{

    /**
     * Evaluates a given guarantee. The variables used in the qualifying condition and service level objective
     * of the guarantee are passes a a map.
     * 
     * @param guarantee
     *            the guarantee to evaluate
     * 
     * @param variables
     *            a name/value map containing the variables
     * 
     * @return the evaluated guarantee term state
     * 
     * @throws Exception
     *             indicates an error while evaluating the guarantee term
     */
    GuaranteeEvaluationResultType evaluate( GuaranteeTermType guarantee, Map<String, Object> variables )
        throws Exception;

    /**
     * 
     * @param accountingSystem
     *            the accounting system to use
     *            
     * @deprecated
     */
    void setAccountingSystem( IAccountingSystem accountingSystem );

    /**
     * 
     * @return the accounting system to use
     * 
     * @deprecated
     */
    IAccountingSystem getAccountingSystem();

}
