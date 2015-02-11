/* 
 * Copyright (c) 2005-2011, Fraunhofer-Gesellschaft
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
package org.ogf.graap.wsag.server.persistence.impl;

import java.lang.reflect.Constructor;
import java.text.MessageFormat;
import java.util.Iterator;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;
import javax.persistence.RollbackException;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlObject;
import org.ogf.graap.wsag.api.Agreement;
import org.ogf.graap.wsag.api.AgreementOffer;
import org.ogf.graap.wsag.api.exceptions.AgreementFactoryException;
import org.ogf.graap.wsag.api.logging.LogMessage;
import org.ogf.graap.wsag.api.types.AbstractAgreementType;
import org.ogf.graap.wsag.api.types.AgreementDelegator;
import org.ogf.graap.wsag.api.types.AgreementOfferType;
import org.ogf.graap.wsag.server.monitoring.MonitorableAgreement;
import org.ogf.graap.wsag.server.persistence.EmfRegistry;
import org.ogf.graap.wsag.server.persistence.PersistedResourceException;
import org.ogf.graap.wsag.server.persistence.PersistentAgreement;
import org.ogf.graap.wsag4j.types.engine.PersistenceAgreementContextType;
import org.ogf.graap.wsag4j.types.engine.PersistenceAgreementContextType.Entry;
import org.ogf.schemas.graap.wsAgreement.AgreementContextType;
import org.ogf.schemas.graap.wsAgreement.AgreementPropertiesType;
import org.ogf.schemas.graap.wsAgreement.AgreementStateType;
import org.ogf.schemas.graap.wsAgreement.AgreementTemplateType;
import org.ogf.schemas.graap.wsAgreement.GuaranteeTermStateType;
import org.ogf.schemas.graap.wsAgreement.ServiceTermStateType;
import org.ogf.schemas.graap.wsAgreement.TermTreeType;
import org.ogf.schemas.graap.wsAgreement.TerminateInputType;
import org.w3.x2005.x08.addressing.EndpointReferenceType;

import eu.betaas.taas.bigdatamanager.database.hibernate.data.PersistentAgreementContainerDatabase;

/**
 * <p>
 * Database-related implementation of the {@link PersistentAgreement} interface. This implementation uses
 * {@link PersistentAgreementContainer} instances to rebuild the original agreement and provides a transparent
 * access to the stored informations.
 * </p>
 * 
 * <p>
 * During object instantiation an existing {@link PersistentAgreementContainer} instance is used to rebuild
 * the agreement or an agreement itself is passed to the constructor. In the second case a
 * {@link PersistentAgreementContainer} instance is created and stored, to initially persist the agreement.
 * </p>
 * 
 * @author T.Weuffel
 */
public class DatabasePersistentAgreement extends AgreementDelegator
    implements PersistentAgreement, Agreement, Observer
{

    /**
     * Data associated with an agreement instance is stored in the database under a unique identifier. This
     * key identifies the context of a particular agreement in the database.
     */
    public static final String AGREEMENT_CONTEXT_ENTRY = "agreement_context";

    /**
     * Data associated with an agreement instance is stored in the database under a unique identifier. This
     * key identifies the context properties of a particular agreement in the database.
     */
    public static final String AGREEMENT_PROPERTIES_ENTRY = "agreement_properties";

    private static final Logger LOG = Logger.getLogger( DatabasePersistentAgreement.class );

    /**
     * The {@link MonitorableAgreement} wraps the domain specific agreement implementation that inherits from
     * {@link AbstractAgreementType}. After reload all agreement instances are wrapped with a
     * {@link MonitorableAgreement}. in case monitoring was active it will be restarted, otherwise the
     * {@link MonitorableAgreement} acts as an isolation layer to the concrete implementation, i.e. all calls
     * are simply delegated to the concrete implementation.
     */
    protected MonitorableAgreement agreement;

    /**
     * This container stores the agreement resource properties document and the agreement execution properties
     * in the database.
     * 
     * {@link AbstractAgreementType#getXMLObject()} {@link AbstractAgreementType#getExecutionContext()}
     */
    protected PersistentAgreementContainer persistentAgreementContainer;

    /**
     * The factory resource id. This id identifies the factory uniquely in the system.
     */
    protected String agreementFactoryId;

    private DatabasePersistentAgreement( MonitorableAgreement agreement, String agreementFactoryId )
    {
        super( agreement );
        this.agreement = agreement;
        this.agreementFactoryId = agreementFactoryId;
    }

    /**
     * Uses an existing {@link PersistentAgreementContainer} instance to instantiate the persisted agreement.
     * The initialization itself is delegated to the {@link #load()} method. Use this constructor to load an
     * existing agreement instance.
     * 
     * @param persistentAgreementContainer
     *            Instance storing all the agreement-related information.
     * @param agreementFactoryId
     *            ID of the agreement factory, used to build and deploy this agreement.
     */
    public DatabasePersistentAgreement( PersistentAgreementContainer persistentAgreementContainer,
                                        String agreementFactoryId )
    {
        super( null );

        LOG.trace( "Create a new DatabasePersistentAgreement instance." );

        this.persistentAgreementContainer = persistentAgreementContainer;
        this.agreementFactoryId = agreementFactoryId;
    }

    /**
     * Inserts an agreement in the database.
     * 
     * @param agreement
     *            the agreement to persist
     * @param agreementFactoryId
     *            the id of the factory that created the agreement
     * 
     * @return an instance of the persisted agreement
     * 
     * @throws AgreementFactoryException
     *             failed to insert agreement in the database
     */
    public static DatabasePersistentAgreement
        insertAgreement( Agreement agreement, String agreementFactoryId ) throws AgreementFactoryException
    {

        LOG.debug( "Create new PersistentAgreementContainer " + agreementFactoryId );

        PersistentAgreementContainer container =
            PersistentAgreementContainer.createContainer( agreement, agreementFactoryId );

        if ( LOG.isDebugEnabled() )
        {
            LOG.debug( LogMessage.getMessage(
                "PersistentAgreementContainer for agreement ''{0}'' persisted.", agreement.getAgreementId() ) );
        }

        if ( LOG.isDebugEnabled() )
        {
            LOG.debug( "Insert new DatabasePersistentAgreement instance into database." );
        }

        MonitorableAgreement monitorable =
            initializeMonitorableAgreement( agreement, container.getPersistedAgreementContextType() );

        DatabasePersistentAgreement result =
            new DatabasePersistentAgreement( monitorable, agreementFactoryId );
        result.persistentAgreementContainer = container;
        result.agreementFactoryId = agreementFactoryId;

        monitorable.addObserver( result );
        monitorable.notifyObservers();

        return result;
    }

    // ////////////////////////////////////////
    // //
    // Agreement Delegation Methods //
    // //
    // ////////////////////////////////////////
    /**
     * {@inheritDoc}
     * 
     * @see org.ogf.graap.wsag.server.monitoring.MonitorableAgreement#getGuaranteeTermStates()
     */
    @Override
    public GuaranteeTermStateType[] getGuaranteeTermStates()
    {
        return agreement.getGuaranteeTermStates();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ogf.graap.wsag.server.monitoring.MonitorableAgreement#getAgreementId()
     */
    @Override
    public String getAgreementId()
    {
        return agreement.getAgreementId();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ogf.graap.wsag.server.monitoring.MonitorableAgreement#getContext()
     */
    @Override
    public AgreementContextType getContext()
    {
        return agreement.getContext();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ogf.graap.wsag.server.monitoring.MonitorableAgreement#getName()
     */
    @Override
    public String getName()
    {
        return agreement.getName();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ogf.graap.wsag.server.monitoring.MonitorableAgreement#getServiceTermStates()
     */
    @Override
    public ServiceTermStateType[] getServiceTermStates()
    {
        return agreement.getServiceTermStates();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ogf.graap.wsag.server.monitoring.MonitorableAgreement#getState()
     */
    @Override
    public AgreementStateType getState()
    {
        return agreement.getState();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ogf.graap.wsag.server.monitoring.MonitorableAgreement#getTerms()
     */
    @Override
    public TermTreeType getTerms()
    {
        return agreement.getTerms();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ogf.graap.wsag.server.monitoring.MonitorableAgreement#getAgreementInstance()
     */
    public Agreement getAgreementInstance()
    {
        return agreement;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ogf.graap.wsag.server.monitoring.MonitorableAgreement#terminate(org.ogf.schemas.graap.wsAgreement.TerminateInputType)
     */
    @Override
    public void terminate( TerminateInputType reason )
    {
        agreement.terminate( reason );
    }

    // ////////////////////////////////////////////////
    // //
    // Agreement Persistence Methods //
    // //
    // ////////////////////////////////////////////////
    /**
     * <p>
     * Load an agreement based on the persisted information, the {@link PersistentAgreementContainer}
     * instance. All information packages in the {@link PersistenceAgreementContextType} instance is loaded
     * and used to re-build the original XML documents.
     * </p>
     * 
     * <p>
     * The result of this load-operation is a {@link MonitorableAgreement} instance stored in the
     * {@link #agreement} variable.
     * </p>
     * 
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings( "unchecked" )
    public synchronized void load() throws Exception
    {

        //
        // if we reload the agreement make sure that for the actual agreement instance
        // monitoring is stopped in order to prevent memory leaks.
        //
        if ( agreement != null )
        {
            agreement.stopMonitoring();
        }

        if ( LOG.isDebugEnabled() )
        {
            LOG.debug( "Load DatabasePersistentAgreement." );
        }

        // re-build agreement context
        PersistenceAgreementContextType persistenceAgreementContext =
            persistentAgreementContainer.getPersistedAgreementContextType();

        // re-build agreement properties
        AgreementPropertiesType agreementPropertiesType =
            persistenceAgreementContext.getAgreementProperties();

        // load the agreement itself (dependent on the original class)
        String className = persistentAgreementContainer.getAgreementClassName();

        LOG.debug( LogMessage.getMessage( "Re-load the original agreement using class ''{0}''.", className ) );

        Class<AbstractAgreementType> clazz;
        try
        {
            clazz = (Class<AbstractAgreementType>) this.getClass().getClassLoader().loadClass( className );
        }
        catch ( ClassCastException e )
        {
            throw new Exception(
                "Load agreement failed. Agreement must inherit from AbstractAgreement class.", e );
        }

        AbstractAgreementType abstractAgreementType = null;

        //
        // First try to instantiate class using the default constructor
        //
        try
        {
            Constructor<AbstractAgreementType> constructor = clazz.getConstructor();
            abstractAgreementType = constructor.newInstance();
            abstractAgreementType.setXmlObject( agreementPropertiesType );
        }
        catch ( NoSuchMethodException e )
        {
            LOG.trace( LogMessage.getMessage( "Default constructor for class {0} not implemented.",
                clazz.getName() ) );
        }

        //
        // if default constructor is not implemented use constructor(AgreementPropertiesType)
        //
        if ( abstractAgreementType == null )
        {
            try
            {
                Constructor<AbstractAgreementType> constructor =
                    clazz.getConstructor( AgreementPropertiesType.class );
                abstractAgreementType = constructor.newInstance( agreementPropertiesType );
            }
            catch ( NoSuchMethodException e )
            {
                LOG.trace( LogMessage.getMessage(
                    "Constructor AgreementTypeImpl(AgreementPropertiesType) for class {0} not implemented.",
                    clazz.getName() ) );
            }
        }

        //
        // if still no success use the AgreementOffer constructor
        //
        if ( abstractAgreementType == null )
        {
            try
            {
                Constructor<AbstractAgreementType> constructor = clazz.getConstructor( AgreementOffer.class );

                //
                // First create an offer based on the stored agreement
                //
                AgreementTemplateType template = AgreementTemplateType.Factory.newInstance();
                template.addNewCreationConstraints();
                template.addNewContext().set( agreementPropertiesType.getContext() );
                template.addNewTerms().set( agreementPropertiesType.getTerms() );
                template.setName( agreementPropertiesType.getContext().getTemplateName() );
                template.setTemplateId( agreementPropertiesType.getContext().getTemplateId() );

                //
                // instantiate the agreement object and set the properties document
                //
                abstractAgreementType = constructor.newInstance( new AgreementOfferType( template ) );
                abstractAgreementType.setXmlObject( agreementPropertiesType );
            }
            catch ( NoSuchMethodException e )
            {
                LOG.trace( MessageFormat.format(
                    "Constructor AgreementTypeImpl(AgreementOffer) for class {0} not implemented.",
                    clazz.getName() ) );
            }
        }
        //
        // if agreement class was still not instantiated throw an exception
        //
        if ( abstractAgreementType == null )
        {
            String message =
                MessageFormat.format( "Could not instantiate agreement class ''{0}''.\n"
                    + "Agreement class does neither implement default constructor "
                    + "nor constructor Ageement(AgreementPropertiesType).", clazz.getName() );
            LOG.error( message );
            throw new Exception( message );
        }

        Entry[] entries = persistenceAgreementContext.getEntryArray();
        for ( int i = 0; i < entries.length; i++ )
        {
            abstractAgreementType.getExecutionContext().put( entries[i].getName(), entries[i].getValue() );
        }

        agreement = initializeMonitorableAgreement( abstractAgreementType, persistenceAgreementContext );
        setDelegator( agreement );

        agreement.addObserver( this );

        //
        // notify the agreement instance of reload
        //
        agreement.notifyReload();

        LOG.debug( "Instantiated Monitorable Agreement instance for persisted agreement." );
    }

    private static MonitorableAgreement
        initializeMonitorableAgreement( Agreement agreement, PersistenceAgreementContextType pContext )
    {
        //
        // TODO: this is kind of hackish, should be re-factored in version 2.0
        //

        //
        // if the agreement is already an instance of the MonitorableAgreement class simply return it
        //
        if ( agreement instanceof MonitorableAgreement )
        {
            return (MonitorableAgreement) agreement;
        }

        //
        // otherwise we wrap the implementation with the MonitorableAgreement
        //
        Agreement instance = null;

        if ( agreement instanceof AbstractAgreementType )
        {
            instance = agreement;
        }
        else
        {
            instance = agreement;
        }

        //
        // Finally, create the monitorable agreement
        //
        MonitorableAgreement monitorable = new MonitorableAgreement( instance );

        return monitorable;
    }

    /**
     * <p>
     * Handles the save-operation of an agreement. All required information (the current version/state of the
     * information) are capsuled in the agreement-related {@link PersistentAgreementContainer} instance, which
     * then is stored in the database.
     * </p>
     * 
     * <p>
     * If the agreement was already persisted, the existing persisted agreement is replaced with the new
     * version. If not, the agreement is initially persisted. The decision is based on the existence of a
     * database-record with the same agreement id.
     * </p>
     * 
     * {@inheritDoc}
     */
    @Override
    public synchronized void save() throws Exception
    {
        if ( LOG.isDebugEnabled() )
        {
            LOG.debug( LogMessage.getMessage( "Save DatabasePersistentAgreement ''{0}''.",
                agreement.getAgreementId() ) );
        }

        //
        // check if the agreement was already persisted
        //

        EntityManager em = EmfRegistry.getEntityManager();
        Query query = em.createNamedQuery( "PersistentAgreementContainer.findByAgreementId" );

        String agreementId = agreement.getAgreementId();
        query.setParameter( "agreementId", agreementId );

        PersistentAgreementContainer container = null;
        boolean persistentAgreementContainerExists = true;
        try
        {
            container = (PersistentAgreementContainer) query.getSingleResult();
            
        }
        catch ( NonUniqueResultException ex )
        {
            LOG.error( LogMessage.getMessage(
                "Found more than one PersistentAgreementContainer for the agreement id ''{0}''.",
                agreement.getAgreementId() ), ex );

            throw new Exception( MessageFormat.format(
                "Found more than one PersistentAgreementContainer for the agreement id ''{0}''.",
                agreement.getAgreementId() ) );
        }
        catch ( NoResultException ex )
        {
            persistentAgreementContainerExists = false;
        }

        // was already persisted, so just update it
        if ( persistentAgreementContainerExists )
        {
            LOG.debug( LogMessage.getMessage( "Update PersistentAgreementContainer ''{0}''.",
                agreement.getAgreementId() ) );

            // build wrapper
            PersistenceAgreementContextType persistenceAgreementContext =
                PersistenceAgreementContextType.Factory.newInstance();

            // update agreement properties
            persistenceAgreementContext.addNewAgreementProperties();

            // update the agreement properties
            persistenceAgreementContext.getAgreementProperties().set( agreement.getXMLObject() );

            //
            // update the agreement execution context
            //
            Map<String, XmlObject> executionProperties = agreement.getExecutionContext();
            persistenceAgreementContext.setEntryArray( new PersistenceAgreementContextType.Entry[0] );
            Iterator<String> keys = executionProperties.keySet().iterator();
            while ( keys.hasNext() )
            {
                String key = keys.next();
                XmlObject value = executionProperties.get( key );
                PersistenceAgreementContextType.Entry entry = persistenceAgreementContext.addNewEntry();
                entry.setName( key );
                entry.addNewValue().set( value );
                entry.getValue().changeType( value.schemaType() );
            }

            // PersistenceAgreementContextType.Entry entry = persistenceAgreementContext.addNewEntry();
            // entry.set(agreement.getContext());
            // entry.setName(AGREEMENT_CONTEXT_ENTRY);

            // set the new PersistenceAgreementContextType
            container.setPersistedAgreementContextType( persistenceAgreementContext );

            // set the agreements' status and EPR
            container.setState( agreement.getState() );

            // set agreement class name
            container.setAgreementClassName( agreement.getImplementationClass().getName() );

            // try to persist the wrapped agreement
            em.getTransaction().begin();

            try
            {
                // persist and commit
            	em.merge( container );
                em.getTransaction().commit();
            }
            catch ( RollbackException ex )
            {
                LOG.error( LogMessage.getMessage( "Could not update the wrapped agreement with id ''{0}''.",
                    agreement.getAgreementId() ), ex );

                // roll back the persistent
                em.getTransaction().rollback();

                throw new Exception( MessageFormat.format(
                    "Could not update the wrapped agreement with id ''{0}''.", agreement.getAgreementId() ) );
            }
            finally
            {
                em.close();
            }
        }
        // was not persisted, create a new PersistentAgreementContainer and persist it
        else
        {
            String msgNoAgreementFound =
                "No agreement instance found in database. "
                    + "Save operation can only be executed on existing agreement instances.";
            throw new AgreementFactoryException( msgNoAgreementFound );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Agreement getAgreement()
    {
        return agreement;
    }

    /**
     * {@inheritDoc}
     * 
     * @deprecated
     */
    @Override
    @Deprecated
    public EndpointReferenceType getAgreementEPR()
    {
        throw new UnsupportedOperationException( "Not implemented yet." );
    }

    /**
     * 
     * @return the persistent agreement container of this instance
     */
    public PersistentAgreementContainer getPersistentAgreementContainer()
    {
        return persistentAgreementContainer;
    }

    /**
     * Retrieves state change notifications of the concrete agreement implementation. If a state notification
     * was received the agreement is saved.
     * 
     * {@inheritDoc}
     * 
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    @Override
    public void update( Observable o, Object arg )
    {
        //
        // state changes to an agreement object are propagated to the MonitorableAgreement instance
        // which in turn notifies the database agreement instance.
        //
        if ( o == agreement )
        {
            try
            {
                save();
            }
            catch ( Exception e )
            {
                LOG.error( "Failed to save agreement instance.", e );
            }
        }
        else
        {
            LOG.error( "Received a state change notification for an greement instance different than the registered." );
        }
    }

    @Override
    public void delete() throws Exception
    {
        String agreementId = getAgreementId();
        LOG.debug( LogMessage.getMessage( "Remove agreement ''{0}'' from database.", agreementId ) );

        //
        // TODO: instead of removing the container we should set the agreement to not active,
        // i.e. it will not be loaded for the factory
        //

        //
        // if there is an agreement with the specified id we remove the agreement container
        //
        PersistentAgreementContainer container = null;
        String removeError =
            MessageFormat.format(
                "Agreement ''{0}'' could not be removed. Agreement does not exist in database.", agreementId );
        try
        {
            container = PersistentAgreementContainer.loadContainer( getAgreementId(), agreementFactoryId );
        }
        catch ( Exception e )
        {
            LOG.error( removeError, e );
            throw new PersistedResourceException( removeError, e );
        }

        if ( container == null )
        {
            throw new PersistedResourceException( removeError );
        }

        //
        // stop monitoring processes when active
        //
        try
        {
            agreement.stopMonitoring();
        }
        catch ( Exception e )
        {
            LOG.error( "Exception while stoping agreement monitoring.", e );
        }

        //
        // now perform delete operation
        //
        try
        {
            container.deleteContainer();
        }
        catch ( Exception e )
        {
            LOG.error( LogMessage.getMessage( "Agreement ''{0}'' could not be removed.", agreementId ), e );
        }

        if ( LOG.isDebugEnabled() )
        {
            LOG.debug( LogMessage.getMessage( "Agreement ''{0}'' removed.", agreementId ) );
        }
    }

}
