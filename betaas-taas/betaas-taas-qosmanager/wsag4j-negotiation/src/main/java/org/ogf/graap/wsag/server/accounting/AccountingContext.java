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
package org.ogf.graap.wsag.server.accounting;

import java.util.HashMap;
import java.util.Map;

import org.ogf.schemas.graap.wsAgreement.GuaranteeTermType;

/**
 * SimpleAccountingContext
 * 
 * @author Oliver Waeldrich
 */
public class AccountingContext extends HashMap<String, Object> implements IAccountingContext
{

    private static final long serialVersionUID = 1L;

    private boolean evaluationResult = false;

    private GuaranteeTermType guarantee;

    /**
     * {@inheritDoc}
     * 
     * @see org.ogf.graap.wsag.server.accounting.IAccountingContext#getEvaluationResult()
     */
    public boolean getEvaluationResult()
    {
        return evaluationResult;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ogf.graap.wsag.server.accounting.IAccountingContext#getGuarantee()
     */
    public GuaranteeTermType getGuarantee()
    {
        return guarantee;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ogf.graap.wsag.server.accounting.IAccountingContext#getProperties()
     */
    public Map<String, Object> getProperties()
    {
        return this;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ogf.graap.wsag.server.accounting.IAccountingContext#setEvaluationResult(boolean)
     */
    public void setEvaluationResult( boolean result )
    {
        evaluationResult = result;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ogf.graap.wsag.server.accounting.IAccountingContext#setGuarantee(org.ogf.schemas.graap.wsAgreement.GuaranteeTermType
     *      )
     */
    public void setGuarantee( GuaranteeTermType guarantee )
    {
        this.guarantee = guarantee;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ogf.graap.wsag.server.accounting.IAccountingContext#setProperties(java.util.Map)
     */
    public void setProperties( Map<String, Object> properties )
    {
        clear();
        putAll( properties );
    }

}
