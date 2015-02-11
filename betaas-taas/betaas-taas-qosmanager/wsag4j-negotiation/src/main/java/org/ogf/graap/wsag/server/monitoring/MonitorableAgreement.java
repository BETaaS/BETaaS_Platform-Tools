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
import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlInt;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlString;
import org.ogf.graap.wsag.api.Agreement;
import org.ogf.graap.wsag.api.logging.LogMessage;
import org.ogf.graap.wsag.server.accounting.IAccountingSystem;
import org.ogf.graap.wsag.server.accounting.SimpleAccountingSystemLogger;
import org.ogf.graap.wsag.server.api.IAgreementContext;
import org.ogf.graap.wsag.server.api.impl.AgreementContext;
import org.ogf.graap.wsag.server.persistence.impl.PersistentAgreementContainer;
import org.ogf.schemas.graap.wsAgreement.AgreementContextType;
import org.ogf.schemas.graap.wsAgreement.AgreementPropertiesType;
import org.ogf.schemas.graap.wsAgreement.AgreementStateType;
import org.ogf.schemas.graap.wsAgreement.GuaranteeTermStateType;
import org.ogf.schemas.graap.wsAgreement.ServiceTermStateType;
import org.ogf.schemas.graap.wsAgreement.TermTreeType;
import org.ogf.schemas.graap.wsAgreement.TerminateInputType;
import org.quartz.CronExpression;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;

/**
 * MonitorableAgreement
 * 
 * Supports monitoring of service terms, agreement state and automatic evaluation of guarantee terms.
 * 
 * @author Oliver Waeldrich
 * 
 */
public class MonitorableAgreement extends Observable
    implements Agreement, Observer
{

    private static final Logger LOG = Logger.getLogger( MonitorableAgreement.class );

    //
    // definition of constants set in the execution properties
    //

    /**
     * Key to resolve if monitoring was started for a particular agreement implementation from the agreement
     * execution context. It refers a {@link XmlBoolean}. If the boolean value is true monitoring was started
     * using the {@link MonitorableAgreement}, if false or not present monitoring was not used.
     * 
     * Used for loading persisted agreements.
     * 
     * @see #getAgreementInstance()
     * @see Agreement#getExecutionContext()
     */
    public static final String MONITORING_ACTIVE = "org.wsag4j.monitoring.isActive";

    /**
     * Key to resolve the monitoring interval for a particular agreement implementation from the agreement
     * execution context. It refers a {@link XmlString} value.
     * 
     * Used for loading persisted agreements.
     * 
     * @see #getAgreementInstance()
     * @see Agreement#getExecutionContext()
     */
    public static final String MONITORING_CRON = "org.wsag4j.monitoring.cron";

    /**
     * Key to resolve the class names of the monitoring handler for a particular agreement implementation from
     * the agreement execution context. It refers a {@link XmlString}. The monitoring handler class names are
     * stored in the agreement execution context with the following strategy:
     * 
     * <code>MONITORING_HANDLER + "." + handler[i].getClass().getName()</code>
     * 
     * Used for loading persisted agreements.
     * 
     * @see #getAgreementInstance()
     * @see Agreement#getExecutionContext()
     */
    public static final String MONITORING_HANDLER = "org.wsag4j.monitoring.handler";

    /**
     * Key to resolve the number of the monitoring handler for a particular agreement implementation from the
     * agreement execution context. It refers a {@link XmlInt}.
     * 
     * Used for loading persisted agreements.
     * 
     * @see #getAgreementInstance()
     * @see Agreement#getExecutionContext()
     */
    public static final String MONITORING_HANDLER_COUNT = "org.wsag4j.monitoring.handler.count";

    //
    // private variable definitions
    //
    private Agreement agreementInstance;

    private IAgreementContext executionContext = new AgreementContext( this );

    private final List<IServiceTermMonitoringHandler> monitoringHandler =
        new Vector<IServiceTermMonitoringHandler>();

    private Scheduler scheduler;

    private String jobName;

    private static final String JOB_GROUP = "WSAG4J";

    private IAccountingSystem accountingSystem = new SimpleAccountingSystemLogger();

    private boolean monitoring = false;

    /**
     * @return the monitoring
     */
    public boolean isMonitoring()
    {
        return monitoring;
    }

    //
    // default schedule for monitoring that fires each minute
    //
    private static final String DEFAULT_SCHEDULE = "0 0/1 * * * ?";

    private String cronExpression = DEFAULT_SCHEDULE;

    /**
     * Creates a new instance of a monitorable agreement. The agreement object, for which this
     * MonitorableAgreement is created, implements the methods to store the terms and the state of the
     * agreement, and to terminate the agreement.
     * 
     * @param agreement
     *            the agreement object, which should be monitored.
     */
    public MonitorableAgreement( Agreement agreement )
    {
        this.agreementInstance = agreement;

        //
        // register this instance as observer to state change events of the agreement instance
        //
        agreement.addObserver( this );

        //
        // update the execution context
        //
        executionContext.getExecutionProperties().putAll( agreement.getExecutionContext() );
        executionContext.getTransientExecutionProperties().putAll( agreement.getTransientExecutionContext() );

        initializeScheduler();
    }

    /**
     * Recreates an instance of a monitorable agreement. The agreement object, for which this
     * MonitorableAgreement is created, implements the methods to store the terms and the state of the
     * agreement, and to terminate the agreement.
     * 
     * @param persistentAgreementContainer
     *            the persisted agreement object, which should be monitored.
     */
    public MonitorableAgreement( PersistentAgreementContainer persistentAgreementContainer )
    {
        // agreement.addObserver(this);
        throw new UnsupportedOperationException( "Not supported yet." );
    }

    private void initializeScheduler()
    {
        //
        // Initialize the quartz scheduler
        //
        synchronized ( SchedulerFactory.class )
        {

            SchedulerFactory factory = new StdSchedulerFactory();

            try
            {
                scheduler = factory.getScheduler();

                if ( !scheduler.isStarted() )
                {
                    scheduler.start();
                }
            }
            catch ( SchedulerException e )
            {
                throw new IllegalStateException( "Failed to instantiate Quartz scheduler.", e );
            }

        }

    }

    private IMonitoringContext initializeMonitoringContext()
    {

        IMonitoringContext monitoringContext = new MonitoringContext();

        //
        // add the execution context properties to the monitoring context
        //
        monitoringContext.setProperties( executionContext.getExecutionProperties() );

        //
        // add the transient execution context properties to the monitoring context
        //
        monitoringContext.setTransientProperties( executionContext.getTransientExecutionProperties() );

        // deprecated
        // //
        // // add the agreement execution context
        // //
        // monitoringContext.getProperties().putAll(executionContext.getExecutionProperties());

        //
        // add monitoring handler to monitoring context
        //
        monitoringContext.setMonitoringHandler( new IServiceTermMonitoringHandler[0] );

        for ( int i = 0; i < monitoringHandler.size(); i++ )
        {
            monitoringContext.addMonitoringHandler( monitoringHandler.get( i ) );
        }

        monitoringContext.setAccountingSystem( accountingSystem );

        //
        // put the agreement context into the transient properties
        //
        monitoringContext.getTransientProperties().put(
            IMonitoringContext.WSAG4J_AGREEMENT_EXECUTION_CONTEXT, getExecutionContext() );

        return monitoringContext;
    }

    private synchronized void scheduleMonitoringJobs( IMonitoringContext monitoringContext ) throws Exception
    {

        //
        // TODO: set properties isMonitoring in the execution context
        // this allows us to restart monitoring if an agreement
        // was reloaded
        //

        jobName = initializeJobName();

        Trigger agreementMonitoringTrigger = createCronTrigger( jobName );

        //
        // create job details
        //
        JobDetail agreementMonitoringDetail = new JobDetail( jobName, JOB_GROUP, AgreementMonitorJob.class );

        agreementMonitoringDetail.getJobDataMap().put( AgreementMonitorJob.WSAG4J_AGREEMENT_INSTANCE,
            agreementInstance );
        agreementMonitoringDetail.getJobDataMap().put( AgreementMonitorJob.WSAG4J_MONITORING_CONTEXT,
            monitoringContext );

        scheduler.scheduleJob( agreementMonitoringDetail, agreementMonitoringTrigger );

        //
        // TODO: schedule job for terminating the monitoring process
        // if AgreementState is completed
        //
    }

    private String initializeJobName() throws SchedulerException
    {
        List<String> names = Arrays.asList( scheduler.getJobNames( JOB_GROUP ) );

        String name =
            MessageFormat.format( "WSAG4J_MONITORING_JOB_{0}", new Object[] { new Date().getTime() } );

        while ( names.contains( name ) )
        {
            name = MessageFormat.format( "WSAG4J_MONITORING_JOB_{0}", new Object[] { new Date().getTime() } );
            try
            {
                wait( 10 );
            }
            catch ( InterruptedException e )
            {
                // do nothing, just continue
            }
        }

        return name;
    }

    /**
     * Stores the number and class names of the monitoring handler in the execution properties.
     */
    private void saveHandlerToExecutionContext()
    {
        int handlerCount = 0;
        Iterator<IServiceTermMonitoringHandler> handler = monitoringHandler.iterator();

        while ( handler.hasNext() )
        {

            String handlerClass = handler.next().getClass().getName();

            String key = MONITORING_HANDLER + "." + handlerCount;
            XmlString value = XmlString.Factory.newValue( handlerClass );

            getMonitoringContext().getExecutionProperties().put( key, value );

            handlerCount++;
        }

        XmlInt value = XmlInt.Factory.newValue( Integer.valueOf( handlerCount ) );
        getMonitoringContext().getExecutionProperties().put( MONITORING_HANDLER_COUNT, value );
    }

    /**
     * Initializes the monitoring handler based on the handler count and names stored in the execution
     * context.
     */
    private void initializeHandlerFromExecutionContext()
    {
        int handlerCount = 0;
        monitoringHandler.clear();

        XmlInt count =
            (XmlInt) getMonitoringContext().getExecutionProperties().get( MONITORING_HANDLER_COUNT );
        if ( count != null )
        {
            handlerCount = count.getIntValue();
        }

        while ( handlerCount > 0 )
        {
            handlerCount--;

            String key = MONITORING_HANDLER + "." + handlerCount;
            XmlString value = (XmlString) getMonitoringContext().getExecutionProperties().get( key );

            LOG.debug( "initialize agreement monitoring handler" );

            //
            // load the agreement monitoring handler
            //
            try
            {
                String className = value.getStringValue();
                LOG.debug( LogMessage.getMessage( "instantiate monitoring handler ''{0}''", className ) );

                Class<IServiceTermMonitoringHandler> clazz;
                try
                {
                    //
                    // check if the class to instantiate implements our handler interface
                    //
                    @SuppressWarnings( "unchecked" )
                    Class<IServiceTermMonitoringHandler> convert =
                        (Class<IServiceTermMonitoringHandler>) this.getClass().getClassLoader()
                                                                   .loadClass( className );

                    clazz = convert;
                }
                catch ( ClassCastException e )
                {
                    final String msgNotRequiredInterface =
                        "monitoring handler must implement the 'IServiceTermMonitoringHandler' interface.";
                    throw new Exception( msgNotRequiredInterface, e );
                }

                //
                // instantiate and add the monitoring handler
                //
                addMonitoringHandler( clazz.newInstance() );

                LOG.debug( LogMessage.getMessage( "successfully instantiated monitoring handler ''{0}''",
                    className ) );
            }
            catch ( Exception e )
            {
                String msgText = "re-initializing monitorable agreement failed: {0}";
                LogMessage message = LogMessage.getMessage( msgText, e.getMessage() );
                LOG.error( message, e );
            }
        }

    }

    /**
     * @return the executionContext
     * @deprecated
     */
    @Deprecated
    public IAgreementContext getMonitoringContext()
    {
        return executionContext;
    }

    /**
     * @param executionContext
     *            the executionContext to set
     */
    public void setExecutionContext( IAgreementContext executionContext )
    {
        this.executionContext = executionContext;
    }

    /**
     * 
     * @param handler
     *            monitoring handler
     */
    public void addMonitoringHandler( IServiceTermMonitoringHandler handler )
    {
        monitoringHandler.add( handler );
    }

    /**
     * Returns the list of registered monitoring handler.
     * 
     * @return the monitoringHandler
     */
    public IServiceTermMonitoringHandler[] getMonitoringHandler()
    {
        return monitoringHandler.toArray( new IServiceTermMonitoringHandler[monitoringHandler.size()] );
    }

    /**
     * @return the cronExpression
     */
    public String getCronExpression()
    {
        return cronExpression;
    }

    /**
     * @param cronExpression
     *            the cronExpression to set
     */
    public void setCronExpression( String cronExpression )
    {
        this.cronExpression = cronExpression;
    }

    private Trigger createCronTrigger( String name ) throws Exception
    {
        //
        // create the cron trigger for job monitoring
        //
        CronTrigger trigger = new CronTrigger();

        try
        {
            if ( CronExpression.isValidExpression( cronExpression ) )
            {
                trigger.setCronExpression( cronExpression );
            }
            else
            {
                LOG.error( LogMessage.getMessage(
                    "Invalid cron expression ({0}). Using default monitoring schedule ({1}).",
                    cronExpression, DEFAULT_SCHEDULE ) );
                trigger.setCronExpression( DEFAULT_SCHEDULE );
            }
        }
        catch ( ParseException e )
        {
            String msgText = "Invalid default schedule <{0}>. Monitoring not scheduled.";
            String message = LogMessage.format( msgText, DEFAULT_SCHEDULE );
            throw new Exception( message, e );
        }

        trigger.setGroup( JOB_GROUP );
        trigger.setName( name );

        return trigger;
    }

    /**
     * Starts the agreement monitoring process.
     * 
     * @throws Exception
     *             failed to start monitoring
     */
    public void startMonitoring() throws Exception
    {
        //
        // set flag that the monitoring process has started
        //
        XmlBoolean isMonitoring = XmlBoolean.Factory.newValue( true );
        XmlString cron = XmlString.Factory.newValue( cronExpression );
        getMonitoringContext().getExecutionProperties().put( MONITORING_ACTIVE, isMonitoring );
        getMonitoringContext().getExecutionProperties().put( MONITORING_CRON, cron );

        //
        // save the monitoring handler in the execution context
        //
        saveHandlerToExecutionContext();

        //
        // initialize the monitoring context
        //
        IMonitoringContext monitoringContext = initializeMonitoringContext();

        //
        // schedule the monitoring jobs
        //
        try
        {
            scheduleMonitoringJobs( monitoringContext );
        }
        catch ( Exception e )
        {
            final String msgText = "Error scheduling monitoring jobs. Reason: {0}";
            String message = LogMessage.format( msgText, e.getMessage() );
            LOG.error( message, e );

            throw new Exception( message, e );
        }

        this.monitoring = true;
    }

    /**
     * Stops the agreement monitoring.
     * 
     * @throws Exception
     *             error while stopping the agreement monitor scheduler
     */
    public void stopMonitoring() throws Exception
    {
        try
        {
            XmlBoolean isMonitoring = XmlBoolean.Factory.newValue( false );
            getMonitoringContext().getExecutionProperties().put( MONITORING_ACTIVE, isMonitoring );
            getAgreementInstance().getExecutionContext().put( MONITORING_ACTIVE, isMonitoring );

            //
            // TODO: unset properties isMonitoring in the execution context
            // when an agreement is reloaded, the monitoring is started
            // if isMonitoring property is set in the execution properties
            //

            scheduler.unscheduleJob( jobName, JOB_GROUP );
        }
        catch ( SchedulerException e )
        {
            String message = "Error stoping the agreement monitoring. Reason: " + e.getMessage();
            LOG.error( message );

            throw new Exception( message, e );
        }

        this.monitoring = false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void terminate( TerminateInputType reason )
    {
        try
        {
            stopMonitoring();
        }
        catch ( Exception ex )
        {
            LOG.error( "The agreement monitoring scheduler was not stoped" );

            if ( LOG.isDebugEnabled() )
            {
                LOG.debug( ex );
            }
        }

        try
        {
            agreementInstance.terminate( reason );
        }
        catch ( Exception ex )
        {
            LOG.error( "The agreement could not be terminated." );

            if ( LOG.isDebugEnabled() )
            {
                LOG.debug( ex );
            }
        }

    }

    /**
     * This method notifies a concrete agreement instance that the monitored agreement instance was reloaded.
     * This essentially implies that the {@link Agreement#notifyReload(java.util.Map)} method is invoked.
     * Implementations of the {@link Agreement} can override the
     * {@link Agreement#notifyReinitialized(java.util.Map)} in order to implement domain specific
     * re-initialization logic.
     * 
     * @throws Exception
     *             indicates an error during the agreement reload process
     */
    public void notifyReload() throws Exception
    {
        agreementInstance.notifyReload( executionContext.getExecutionProperties() );

        //
        // re-initialize monitoring handler and restart agreement monitoring
        //
        initializeHandlerFromExecutionContext();

        Map<String, XmlObject> executionProperties = getMonitoringContext().getExecutionProperties();
        XmlString cron = (XmlString) executionProperties.get( MonitorableAgreement.MONITORING_CRON );

        if ( cron != null )
        {
            cronExpression = cron.getStringValue();
        }
        else
        {
            cronExpression = DEFAULT_SCHEDULE;
        }

        boolean isActive = false;
        if ( executionProperties.containsKey( MONITORING_ACTIVE ) )
        {
            isActive =
                ( (XmlBoolean) executionProperties.get( MonitorableAgreement.MONITORING_ACTIVE ) ).getBooleanValue();
        }

        if ( isActive )
        {
            startMonitoring();
        }
    }

    /**
     * @return the agreement id
     * @see org.ogf.graap.wsag.api.Agreement#getAgreementId()
     */
    @Override
    public String getAgreementId()
    {
        return agreementInstance.getAgreementId();
    }

    /**
     * @return the agreement context
     * @see org.ogf.graap.wsag.api.Agreement#getContext()
     */
    @Override
    public AgreementContextType getContext()
    {
        return agreementInstance.getContext();
    }

    /**
     * @return the guarantee term states
     * @see org.ogf.graap.wsag.api.Agreement#getGuaranteeTermStates()
     */
    @Override
    public GuaranteeTermStateType[] getGuaranteeTermStates()
    {
        return agreementInstance.getGuaranteeTermStates();
    }

    /**
     * @return the agreement name
     * @see org.ogf.graap.wsag.api.Agreement#getName()
     */
    @Override
    public String getName()
    {
        return agreementInstance.getName();
    }

    /**
     * @return the service term states
     * @see org.ogf.graap.wsag.api.Agreement#getServiceTermStates()
     */
    @Override
    public ServiceTermStateType[] getServiceTermStates()
    {
        return agreementInstance.getServiceTermStates();
    }

    /**
     * @return the agreement state
     * @see org.ogf.graap.wsag.api.Agreement#getState()
     */
    @Override
    public AgreementStateType getState()
    {
        return agreementInstance.getState();
    }

    /**
     * @return the terms of the agreement
     * @see org.ogf.graap.wsag.api.Agreement#getTerms()
     */
    @Override
    public TermTreeType getTerms()
    {
        return agreementInstance.getTerms();
    }

    /**
     * {@inheritDoc}
     */
    public Agreement getAgreementInstance()
    {
        return agreementInstance;
    }

    /**
     * @param accountingSystem
     *            the accountingSystem to set
     */
    public void setAccountingSystem( IAccountingSystem accountingSystem )
    {
        if ( accountingSystem != null )
        {
            this.accountingSystem = accountingSystem;
        }
    }

    /**
     * @return the accountingSystem
     */
    public IAccountingSystem getAccountingSystem()
    {
        return accountingSystem;
    }

    /**
     * If the monitored agreement receives a state change notification of the concrete agreement
     * implementation (@link {@link Agreement#notifyObservers()}) all observer registered to this monitorable
     * agreement will be notified of the state change ass well.
     * 
     * {@inheritDoc}
     * 
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    @Override
    public void update( Observable o, Object arg )
    {
        setChanged();
        notifyObservers();
        // if ( o instanceof Wsag4jObservable )
        // {
        // Wsag4jObservable observable = (Wsag4jObservable) o;
        // if ( observable.getType() == agreementInstance )
        // {
        // setChanged();
        // notifyObservers();
        // }
        // }
    }

    /**
     * @see org.ogf.graap.wsag.api.Agreement#validate()
     */
    @Override
    public boolean validate()
    {
        return agreementInstance.validate();
    }

    /**
     * @see org.ogf.graap.wsag.api.Agreement#notifyReload(java.util.Map)
     */
    @Override
    public void notifyReload( Map<String, XmlObject> executionCtx )
    {
        agreementInstance.notifyReload( executionCtx );
    }

    /**
     * @see org.ogf.graap.wsag.api.Agreement#setAgreementId(java.lang.String)
     */
    @Override
    public void setAgreementId( String agreementId )
    {
        agreementInstance.setAgreementId( agreementId );
    }

    /**
     * @see org.ogf.graap.wsag.api.Agreement#setContext(org.ogf.schemas.graap.wsAgreement.AgreementContextType)
     */
    @Override
    public void setContext( AgreementContextType context )
    {
        agreementInstance.setContext( context );
    }

    /**
     * @see org.ogf.graap.wsag.api.Agreement#setName(java.lang.String)
     */
    @Override
    public void setName( String name )
    {
        agreementInstance.setName( name );

    }

    /**
     * @see org.ogf.graap.wsag.api.Agreement#setTerms(org.ogf.schemas.graap.wsAgreement.TermTreeType)
     */
    @Override
    public void setTerms( TermTreeType terms )
    {
        agreementInstance.setTerms( terms );
    }

    /**
     * @see org.ogf.graap.wsag.api.Agreement#setState(org.ogf.schemas.graap.wsAgreement.AgreementStateType)
     */
    @Override
    public void setState( AgreementStateType agreementState )
    {
        agreementInstance.setState( agreementState );
    }

    /**
     * @see org.ogf.graap.wsag.api.Agreement#setGuaranteeTermStates(org.ogf.schemas.graap.wsAgreement.GuaranteeTermStateType[])
     */
    @Override
    public void setGuaranteeTermStates( GuaranteeTermStateType[] guaranteeTermStateList )
    {
        agreementInstance.setGuaranteeTermStates( guaranteeTermStateList );
    }

    /**
     * @see org.ogf.graap.wsag.api.Agreement#setServiceTermStates(org.ogf.schemas.graap.wsAgreement.ServiceTermStateType[])
     */
    @Override
    public void setServiceTermStates( ServiceTermStateType[] serviceTermStateList )
    {
        agreementInstance.setServiceTermStates( serviceTermStateList );
    }

    /**
     * @see org.ogf.graap.wsag.api.Agreement#getXMLObject()
     */
    @Override
    public AgreementPropertiesType getXMLObject()
    {
        return agreementInstance.getXMLObject();
    }

    /**
     * @see org.ogf.graap.wsag.api.Agreement#setXmlObject(org.ogf.schemas.graap.wsAgreement.AgreementPropertiesType)
     */
    @Override
    public void setXmlObject( AgreementPropertiesType properties )
    {
        agreementInstance.setXmlObject( properties );
    }

    /**
     * @see org.ogf.graap.wsag.api.Agreement#getTransientExecutionContext()
     */
    @Override
    public Map<String, Object> getTransientExecutionContext()
    {
        return agreementInstance.getTransientExecutionContext();
    }

    /**
     * @see org.ogf.graap.wsag.api.Agreement#getImplementationClass()
     */
    @Override
    public Class getImplementationClass()
    {
        return agreementInstance.getImplementationClass();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ogf.graap.wsag.api.Agreement#getExecutionContext()
     */
    @Override
    public Map<String, XmlObject> getExecutionContext()
    {
        return agreementInstance.getExecutionContext();
    }
}
