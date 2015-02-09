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

import java.text.DecimalFormat;
import java.text.MessageFormat;

import org.apache.log4j.Logger;
import org.ogf.graap.wsag4j.types.engine.CompensationType;
import org.ogf.graap.wsag4j.types.engine.GuaranteeEvaluationResultType;
import org.ogf.graap.wsag4j.types.engine.SLAMonitoringNotificationEventType;
import org.ogf.graap.wsag4j.types.engine.SLOEvaluationResultType;

/**
 * SimpleLogAccountingSystem
 * 
 * @author Oliver Waeldrich
 * 
 */
public class SimpleAccountingSystemLogger implements IAccountingSystem
{

    private static final Logger LOG = Logger.getLogger( SimpleAccountingSystemLogger.class );

    /**
     * {@inheritDoc}
     * 
     * @see org.ogf.graap.wsag.server.accounting.IAccountingSystem#issueCompensation(CompensationType,
     *      IAccountingContext)
     */
    public void issueCompensation( SLAMonitoringNotificationEventType notificationEvent )
    {

        for ( GuaranteeEvaluationResultType evaluationResult : notificationEvent.getGuaranteeEvaluationResultArray() )
        {
            LOG.info( MessageFormat.format( "evaluation result for guarantee ''{0}'': {1}",
                                            new Object[] { evaluationResult.getName(), evaluationResult.getType() } ) );
            
            if ( evaluationResult.isSetCompensation() )
            {
                LOG.info( MessageFormat.format( "issue compensation for guarantee ''{0}''",
                                                new Object[] { evaluationResult.getName() } ) );
                logCompensation( evaluationResult.getCompensation(), evaluationResult.getType() );
            }
            else
            {
                LOG.info( MessageFormat.format( "no compensation issued for guarantee ''{0}''",
                                                new Object[] { evaluationResult.getName() } ) );
            }
        }

    }

    private void logCompensation( CompensationType compensation, SLOEvaluationResultType.Enum type )
    {

        String compensationType = "";

        switch ( type.intValue() )
        {
            case SLOEvaluationResultType.INT_SLO_FULFILLED:
                compensationType = "reward";
                break;
            case SLOEvaluationResultType.INT_SLO_VIOLATED:
                compensationType = "penalty";
                break;
            default:
                compensationType = "unsupported result";
        }

        long value = compensation.getValue().longValue();
        String unit = ( compensation.isSetUnit() ) ? compensation.getUnit() : "";
        
        DecimalFormat twoDForm = new DecimalFormat( "#.##" );
        String formatedValue = twoDForm.format( value );

        LOG.info( MessageFormat.format( "{0}: {1} {2}", new Object[] { compensationType, formatedValue, unit } ) );
    }

}
