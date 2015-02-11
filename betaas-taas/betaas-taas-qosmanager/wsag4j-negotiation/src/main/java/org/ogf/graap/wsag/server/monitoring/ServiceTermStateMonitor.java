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

import java.text.MessageFormat;

import org.apache.log4j.Logger;

/**
 * ServiceTermStateMonitor
 * 
 * @author Oliver Waeldrich
 * 
 */
public class ServiceTermStateMonitor
{

    private static final Logger LOG = Logger.getLogger( ServiceTermStateMonitor.class );

    /**
     * Calls the service term monitoring
     * 
     * @param monitoringContext
     *            call the {@link ServiceTermStateMonitor} with the given monitoring context
     * 
     * @return true if the monitoring was successful, false otherwise.
     */
    public boolean monitor( IMonitoringContext monitoringContext )
    {
        //
        // create a copy of the monitoring context
        //
        IMonitoringContext currentMonitoringContext;
        try
        {
            currentMonitoringContext = (IMonitoringContext) monitoringContext.clone();
        }
        catch ( CloneNotSupportedException e )
        {
            throw new IllegalStateException( "Could not clone monitoring context." );
        }

        //
        // let the monitoring handler update the states using the current context
        //
        for ( int i = 0; i < monitoringContext.getMonitoringHandler().length; i++ )
        {
            IServiceTermMonitoringHandler handler = null;

            try
            {
                handler = monitoringContext.getMonitoringHandler()[i];
                handler.monitor( currentMonitoringContext );
            }
            catch ( Exception e )
            {
                String handlerName = ( handler != null ) ? handler.getClass().getName() : null;

                String msgSDTUpdateError =
                    "Monitoring of service term states failed for handler [{0}]. "
                        + "Service term states are not updated. Reason: {1}";

                LOG.error( MessageFormat.format( msgSDTUpdateError,
                                                 new Object[] { handlerName, e.getMessage() } ) );

                if ( LOG.isDebugEnabled() )
                {
                    LOG.debug( e );
                }

                return false;
            }
        }

        //
        // after the monitoring process was executed successful,
        // we update the monitoring context object at once
        //
        monitoringContext.setServiceTemState( currentMonitoringContext.getServiceTermStates() );

        return true;
    }

}
