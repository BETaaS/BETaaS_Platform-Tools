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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlObject;
import org.ogf.graap.wsag.api.logging.LogMessage;
import org.ogf.graap.wsag.api.types.AbstractAgreementType;
import org.ogf.graap.wsag4j.types.engine.GuaranteeEvaluationResultType;
import org.ogf.graap.wsag4j.types.engine.SLAMonitoringNotificationEventType;
import org.ogf.schemas.graap.wsAgreement.AgreementPropertiesDocument;
import org.ogf.schemas.graap.wsAgreement.AgreementPropertiesType;
import org.ogf.schemas.graap.wsAgreement.AgreementStateDefinition;
import org.ogf.schemas.graap.wsAgreement.AgreementStateType;
import org.ogf.schemas.graap.wsAgreement.GuaranteeTermStateDefinition;
import org.ogf.schemas.graap.wsAgreement.GuaranteeTermStateType;
import org.ogf.schemas.graap.wsAgreement.GuaranteeTermType;
import org.ogf.schemas.graap.wsAgreement.ServicePropertiesType;
import org.ogf.schemas.graap.wsAgreement.ServiceSelectorType;
import org.ogf.schemas.graap.wsAgreement.ServiceTermStateDefinition;
import org.ogf.schemas.graap.wsAgreement.ServiceTermStateType;

/**
 * WSAG4JAgreementMonitor
 * 
 * Monitors the service term states of an agreement instance and evaluates the guarantee term states for
 * guarantees defined in the agreement instance.
 * 
 * @author Oliver Waeldrich
 * 
 */
public class AgreementMonitor
{

    private static final String XPATH_NS_DECL_WSAG =
        "declare namespace wsag='http://schemas.ggf.org/graap/2007/03/ws-agreement';";

    private static final String XPATH_QUERY_GUARANTEE_TERM =
        "$this/wsag:Terms/wsag:All//wsag:GuaranteeTerm[ @wsag:Name = ''{0}'']";

    private static final String XPATH_QUERY_SERVICE_PROPERTIES =
        "$this/wsag:Terms/wsag:All//wsag:ServiceProperties[ @wsag:ServiceName = ''{0}'']";

    //
    // static variable definitions
    //
    private static final Logger LOG = Logger.getLogger( AgreementMonitor.class );

    //
    // private variable definitions
    //
    private AbstractAgreementType agreementInstance;

    private IMonitoringContext monitoringContext;

    private ServiceTermStateMonitor stateMonitor = new ServiceTermStateMonitor();

    //
    // message code definitions
    //
    static final String ERR_UPDATE_GUARANTEE_STATE = "AgreementMonitor.ERR_UPDATE_GUARANTEE_STATE"; //$NON-NLS-1$ 

    static final String ERR_UPDATE_AGREEMENT_STATE = "AgreementMonitor.ERR_UPDATE_AGREEMENT_STATE"; //$NON-NLS-1$

    static final String ERR_GET_STD_STATES = "AgreementMonitor.ERR_GET_STD_STATES"; //$NON-NLS-1$

    static final String ERR_INIT_SDT_STATES = "AgreementMonitor.ERR_INIT_SDT_STATES"; //$NON-NLS-1$

    static final String ERR_UPD_STD_STATES = "AgreementMonitor.ERR_UPD_STD_STATES"; //$NON-NLS-1$

    private static final String ERR_GET_GUARANTEE_STATES = "AgreementMonitor.ERR_GET_GUARANTEE_STATES"; //$NON-NLS-1$

    static final String WARN_MULTIPLE_GUARANTEES = "AgreementMonitor.WARN_MULTIPLE_GUARANTEE_OCCOURENCES"; //$NON-NLS-1$

    static final String WARN_SP_DEF_NOT_FOUND = "AgreementMonitor.WARN_SP_DEF_NOT_FOUND"; //$NON-NLS-1$

    static final String WARN_MULTIPLE_SP_DEFS = "AgreementMonitor.WARN_SP_DEF_MULTIPLE_OCCURENCES"; //$NON-NLS-1$

    static final String WARN_GURANTEE_NOT_FOUND = "AgreementMonitor.WARN_GURANTEE_TERM_NOT_FOUND"; //$NON-NLS-1$

    static final String DBG_EVAL_GUARANTEE_STATE = "AgreementMonitor.DBG_EVAL_GUARANTEE_STATE"; //$NON-NLS-1$

    static final String DBG_SERVICE_PROPERTIES = "AgreementMonitor.DBG_SERVICE_PROPERTIES"; //$NON-NLS-1$

    static final String DBG_DUPLICATE_VAR_DEF = "AgreementMonitor.DEBUG_DUPLICATE_VAR_DEF"; //$NON-NLS-1$

    //
    // method implementations
    //

    /**
     * @return the agreementInstance
     */
    public AbstractAgreementType getAgreementInstance()
    {
        return agreementInstance;
    }

    /**
     * @param agreementInstance
     *            the agreementInstance to set
     */
    public void setAgreementInstance( AbstractAgreementType agreementInstance )
    {
        this.agreementInstance = agreementInstance;
    }

    /**
     * @return the monitoringContext
     */
    public IMonitoringContext getMonitoringContext()
    {
        return monitoringContext;
    }

    /**
     * @param monitoringContext
     *            the monitoringContext to set
     */
    public void setMonitoringContext( IMonitoringContext monitoringContext )
    {
        this.monitoringContext = monitoringContext;
    }

    /**
     * Updates the states of the associated agreement instance. First, the service term states are updated by
     * using the registered monitoring handler. In a second step, all guarantee terms are evaluated and
     * updated. When the monitoring cycle is completed, the resource property document of the agreement
     * instance is updated with the results.
     * 
     * The monitoring handler are invoked in the same order as they are registered.
     * 
     * @throws Exception
     *             indicates an error while updating the states
     */
    public void updateStates() throws Exception
    {
        //
        // synchronize the monitoring process
        //
        IMonitoringContext mutex = monitoringContext;

        synchronized ( mutex )
        {
            //
            // Monitoring of an agreement instance is only performed
            // in observed or in observed and terminating state
            //

            //
            // For the agreement processing we create a copy of the agreement resource properties document.
            // First we must make sure that the agreement properties object has a DOM element representation
            // (important for XPath queries), not a DOM document fragment representation.
            //
            AgreementPropertiesDocument apDoc = AgreementPropertiesDocument.Factory.newInstance();
            AgreementPropertiesType apCopy =
                (AgreementPropertiesType) getAgreementInstance().getXMLObject().copy();
            apDoc.setAgreementProperties( apCopy );

            //
            // now this agreement properties object is an element, no document fragment anymore
            //
            AgreementPropertiesType agreementProperties = apDoc.getAgreementProperties();

            AgreementStateType agreementState = agreementProperties.getAgreementState();
            if ( ( agreementState.getState() == AgreementStateDefinition.OBSERVED )
                || ( agreementState.getState() == AgreementStateDefinition.OBSERVED_AND_TERMINATING ) )
            {

                doUpdateStates( agreementProperties );

                //
                // set the updated state at once and update the agreement execution properties
                //
                synchronized ( getAgreementInstance().getXMLObject() )
                {
                    getAgreementInstance().setState( agreementProperties.getAgreementState() );
                    getAgreementInstance().setServiceTermStates(
                        agreementProperties.getServiceTermStateArray() );
                    getAgreementInstance().setGuaranteeTermStates(
                        agreementProperties.getGuaranteeTermStateArray() );

                    getAgreementInstance().getExecutionContext().putAll( monitoringContext.getProperties() );

                }
            }

            getAgreementInstance().notifyObservers();
        }
    }

    private void doUpdateStates( AgreementPropertiesType agreementProperties ) throws Exception
    {
        try
        {
            //
            // first we update the state of the service terms
            //
            boolean monitoringResult = updateServiceTermStates( agreementProperties );

            //
            // Now we evaluate the guarantees defined in this agreement
            //
            GuaranteeTermStateType[] gStates = new GuaranteeTermStateType[0];

            try
            {
                //
                // Get the auto generated service term state documents.
                //
                gStates = agreementProperties.getGuaranteeTermStateArray();
            }
            catch ( Exception e )
            {
                String error = Messages.formatString( ERR_GET_GUARANTEE_STATES, e.getMessage() );
                throw new Exception( error, e );
            }

            //
            // If the service term states were updated, the guarantees are evaluated.
            // Otherwise, all guarantees are in the state not determined. This is an
            // error of the system and human intervention is required.
            //
            if ( monitoringResult )
            {
                Vector<GuaranteeEvaluationResultType> evalResults =
                    new Vector<GuaranteeEvaluationResultType>();
                //
                // Now evaluate the guarantee term state
                //
                for ( int i = 0; i < gStates.length; i++ )
                {
                    try
                    {
                        GuaranteeEvaluationResultType result =
                            updateGuaranteeTermState( gStates[i].getTermName(), agreementProperties );
                        evalResults.add( result );
                    }
                    catch ( Exception e )
                    {
                        String error =
                            Messages.formatString( ERR_UPDATE_GUARANTEE_STATE, gStates[i].getTermName(),
                                e.getMessage() );
                        throw new Exception( error, e );
                    }
                }

                //
                // create the notification event, update the states and notify the accounting system
                //
                SLAMonitoringNotificationEventType monitoringEvent =
                    SLAMonitoringNotificationEventType.Factory.newInstance();

                monitoringEvent.setAgreementId( getAgreementInstance().getAgreementId() );

                for ( int i = 0; i < gStates.length; i++ )
                {
                    GuaranteeEvaluationResultType result = evalResults.get( i );
                    gStates[i] = result.getDetails().getGuaranteeState();
                    monitoringEvent.addNewGuaranteeEvaluationResult().set( result );
                }

                getMonitoringContext().getAccountingSystem().issueCompensation( monitoringEvent );
            }
            else
            {
                for ( int i = 0; i < gStates.length; i++ )
                {
                    gStates[i].setState( GuaranteeTermStateDefinition.NOT_DETERMINED );
                }
            }
            //
            // update the agreement property document at once and update the overall agreement state
            //
            agreementProperties.setGuaranteeTermStateArray( gStates );
            updateAgreementState( agreementProperties );

        }
        catch ( Exception e )
        {
            LOG.error( e.getMessage() );
            throw e;
        }
    }

    private void updateAgreementState( AgreementPropertiesType agreementProperties )
    {
        try
        {
            AgreementStateType state = agreementProperties.getAgreementState();

            ServiceTermStateType[] sdtstates = agreementProperties.getServiceTermStateArray();
            for ( int i = 0; i < sdtstates.length; i++ )
            {
                if ( ( sdtstates[i].getState() == ServiceTermStateDefinition.NOT_READY )
                    || ( sdtstates[i].getState() == ServiceTermStateDefinition.READY ) )
                {
                    if ( state.getState() == AgreementStateDefinition.OBSERVED_AND_TERMINATING )
                    {
                        //
                        // if the agreement is currently terminating we keep this state
                        //
                        return;
                    }
                    else
                    {
                        //
                        // in all other cases (pending, ...) we change to observed state
                        //
                        state.setState( AgreementStateDefinition.OBSERVED );
                    }
                    return;
                }
            }

            //
            // only if all service term states are completed, the overall agreement is completed
            //
            state.setState( AgreementStateDefinition.COMPLETE );
        }
        catch ( Exception e )
        {
            LOG.error( LogMessage.getMessage( ERR_UPDATE_AGREEMENT_STATE, e.getMessage() ) );
            LOG.debug( e );
        }
    }

    private boolean updateServiceTermStates( AgreementPropertiesType agreementProperties ) throws Exception
    {
        try
        {
            //
            // Get the auto generated service term state documents.
            //
            ServiceTermStateType[] states = new ServiceTermStateType[0];
            try
            {
                states = agreementProperties.getServiceTermStateArray();
            }
            catch ( Exception e )
            {
                String error = Messages.formatString( ERR_GET_STD_STATES, e.getMessage() );
                throw new Exception( error, e );
            }

            //
            // Initialize the current monitoring context
            //
            IMonitoringContext currentMonitoringContext = (IMonitoringContext) monitoringContext.clone();

            try
            {
                currentMonitoringContext.setServiceTemState( new ServiceTermStateType[0] );

                //
                // we copy of the service term states and set the for the current monitoring context
                //
                for ( int i = 0; i < states.length; i++ )
                {
                    states[i] = (ServiceTermStateType) states[i].copy();
                }

                currentMonitoringContext.setServiceTemState( states );
            }
            catch ( Exception e )
            {
                throw new IllegalStateException( Messages.getString( ERR_INIT_SDT_STATES ), e );
            }

            //
            // do the state monitoring
            //
            boolean monitoringResult = stateMonitor.monitor( currentMonitoringContext );

            //
            // finally update the agreement monitoring context
            // and the service term states in the agreement
            //
            agreementProperties.setServiceTermStateArray( currentMonitoringContext.getServiceTermStates() );
            monitoringContext = currentMonitoringContext;

            return monitoringResult;
        }
        catch ( Exception e )
        {
            String message = LogMessage.format( ERR_UPD_STD_STATES, e.getMessage() );
            throw new Exception( message, e );
        }
    }

    private GuaranteeTermType loadGuaranteeTerm( String name, AgreementPropertiesType agreementProperties )
    {
        String xpath = XPATH_NS_DECL_WSAG + MessageFormat.format( XPATH_QUERY_GUARANTEE_TERM, name );
        XmlObject[] result = agreementProperties.selectPath( xpath );

        if ( result.length == 0 )
        {
            LOG.warn( Messages.formatString( WARN_GURANTEE_NOT_FOUND, name ) );
            return null;
        }

        if ( result.length > 1 )
        {
            LOG.warn( Messages.formatString( WARN_MULTIPLE_GUARANTEES, name ) );
        }

        return (GuaranteeTermType) result[0];
    }

    private ServicePropertiesType loadServiceProperties( String serviceName,
                                                         AgreementPropertiesType agreementProperties )
    {
        String xpath =
            XPATH_NS_DECL_WSAG + MessageFormat.format( XPATH_QUERY_SERVICE_PROPERTIES, serviceName );

        XmlObject[] result = agreementProperties.selectPath( xpath );

        if ( result.length == 0 )
        {
            LOG.warn( Messages.formatString( WARN_SP_DEF_NOT_FOUND, serviceName ) );
            ServicePropertiesType empty = ServicePropertiesType.Factory.newInstance();
            empty.setName( "empty" ); //$NON-NLS-1$
            empty.setServiceName( serviceName );
            empty.addNewVariableSet();
            return empty;
        }

        if ( result.length > 1 )
        {
            String warn = Messages.formatString( WARN_MULTIPLE_SP_DEFS, serviceName );
            LOG.warn( warn );
        }

        return (ServicePropertiesType) result[0];
    }

    private GuaranteeEvaluationResultType
        updateGuaranteeTermState( String termName, AgreementPropertiesType agreementProperties )
            throws Exception
    {
        GuaranteeTermType guarantee = loadGuaranteeTerm( termName, agreementProperties );

        LOG.debug( LogMessage.getMessage( DBG_EVAL_GUARANTEE_STATE, termName ) );

        Map<String, Object> variableMap = new HashMap<String, Object>();

        for ( int i = 0; i < guarantee.getServiceScopeArray().length; i++ )
        {
            //
            // for each service we should only have one instance
            // of service properties defined
            //
            ServiceSelectorType scope = guarantee.getServiceScopeArray( i );
            ServicePropertiesType properties =
                loadServiceProperties( scope.getServiceName(), agreementProperties );

            LOG.debug( LogMessage.getMessage( DBG_SERVICE_PROPERTIES, properties.getServiceName(),
                properties.getName() ) );

            ServicePropertyResolver resolver = new ServicePropertyResolver( properties, agreementProperties );
            Map<String, Object> var = resolver.resolveServiceProperties();

            Iterator<String> keys = var.keySet().iterator();
            while ( keys.hasNext() )
            {
                String variableName = (String) keys.next();
                if ( variableMap.containsKey( variableName ) )
                {
                    LOG.debug( LogMessage.getMessage( DBG_DUPLICATE_VAR_DEF, variableName ) );
                }
                else
                {
                    variableMap.put( variableName, var.get( variableName ) );
                }
            }
        }

        IGuaranteeEvaluator evaluator = new SimpleGuaranteeEvaluator();
        return evaluator.evaluate( guarantee, variableMap );
    }

}
