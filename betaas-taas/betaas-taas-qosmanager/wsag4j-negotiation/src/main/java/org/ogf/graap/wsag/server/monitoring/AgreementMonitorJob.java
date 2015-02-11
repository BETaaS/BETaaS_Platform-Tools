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

import org.apache.log4j.Logger;
import org.ogf.graap.wsag.api.types.AbstractAgreementType;
import org.ogf.schemas.graap.wsAgreement.AgreementStateDefinition;
import org.ogf.schemas.graap.wsAgreement.AgreementStateType;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * AgreementMonitorJob
 * 
 * @author Oliver Waeldrich
 * 
 */
public class AgreementMonitorJob implements Job
{
    private static final Logger LOG = Logger.getLogger( AgreementMonitorJob.class );

    /**
     * Identifies the WSAG4J agreement instance in a {@link JobExecutionContext}
     */
    public static final String WSAG4J_AGREEMENT_INSTANCE = "wsag4j.agreement.instance";

    /**
     * Identifies the WSAG4J monitoring context in a {@link JobExecutionContext}
     */
    public static final String WSAG4J_MONITORING_CONTEXT = "wsag4j.agreement.monitoring.context";

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings( "unchecked" )
    public void execute( JobExecutionContext context ) throws JobExecutionException
    {

        Map<Object, Object> jobData = context.getJobDetail().getJobDataMap();
        AbstractAgreementType agreementInstance =
            (AbstractAgreementType) jobData.get( WSAG4J_AGREEMENT_INSTANCE );
        IMonitoringContext monitoringContext = (IMonitoringContext) jobData.get( WSAG4J_MONITORING_CONTEXT );

        AgreementMonitor monitor = new AgreementMonitor();
        monitor.setAgreementInstance( agreementInstance );
        monitor.setMonitoringContext( monitoringContext );

        try
        {
            monitor.updateStates();
        }
        catch ( Exception e )
        {
            LOG.debug( e.getMessage(), e );
            throw new JobExecutionException( "Error updating agreement states.", e );
        }

        try
        {
            AgreementStateType state = monitor.getAgreementInstance().getState();
            if ( ( state.getState() == AgreementStateDefinition.COMPLETE )
                || ( state.getState() == AgreementStateDefinition.TERMINATED ) )
            {

                //
                // if the agreement is in complete or terminated state,
                // unschedule the monitoring job
                //
                context.getScheduler().unscheduleJob( context.getJobDetail().getName(),
                                                      context.getJobDetail().getGroup() );
            }
        }
        catch ( Exception e )
        {
            throw new JobExecutionException( "Error unscheduling monitoring job.", e );
        }
    }

}
