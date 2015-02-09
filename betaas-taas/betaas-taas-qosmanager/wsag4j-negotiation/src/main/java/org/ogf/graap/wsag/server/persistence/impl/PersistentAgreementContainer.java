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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Iterator;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.RollbackException;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.ogf.graap.wsag.api.Agreement;
import org.ogf.graap.wsag.api.exceptions.AgreementFactoryException;
import org.ogf.graap.wsag.api.logging.LogMessage;
import org.ogf.graap.wsag.server.persistence.EmfRegistry;
import org.ogf.graap.wsag4j.types.engine.PersistenceAgreementContextDocument;
import org.ogf.graap.wsag4j.types.engine.PersistenceAgreementContextType;
import org.ogf.schemas.graap.wsAgreement.AgreementPropertiesType;
import org.ogf.schemas.graap.wsAgreement.AgreementStateType;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

import eu.betaas.taas.bigdatamanager.database.service.IBigDataDatabaseService;
import eu.betaas.taas.qosmanager.negotiation.NegotiationActivator;

import eu.betaas.taas.bigdatamanager.database.hibernate.data.PersistentAgreementContainerDatabase;

/*
 * TODO: the container should implement the CRUD operations
 *       - the context element should be a transient XmlBean.
 *       - the context element should be de/serialized on the 
 *         CRUD operation invocation 
 */

/**
 * Entity class for the agreement persistence. It defines the entity fields (the tables' columns), the
 * named-queries used to access the stored data, and the corresponding getter/setter methods.
 * 
 * @author T.Weuffel
 */

public class PersistentAgreementContainer extends PersistentAgreementContainerDatabase
{
	
    // the logger should not be persisted
    private static final Logger LOG = Logger.getLogger( PersistentAgreementContainer.class );

    private PersistenceAgreementContextDocument persistenceContext;


    /**
     * The default constructor is required by the JPA2 environment. It is used to instantiate an instance of
     * this entity. All values are then passed to this instance by setter-injection.
     */
    public PersistentAgreementContainer()
    {
        persistenceContext = PersistenceAgreementContextDocument.Factory.newInstance();
        persistenceContext.addNewPersistenceAgreementContext();
    }

    public PersistentAgreementContainer(PersistentAgreementContainerDatabase a){
    	
        persistenceContext = PersistenceAgreementContextDocument.Factory.newInstance();
        persistenceContext.addNewPersistenceAgreementContext();
    	
        id=a.id;
        agreementId=a.agreementId;
        agreementFactoryId=a.agreementFactoryId;
        state=a.state;
        agreementClassName=a.agreementClassName;
        persistedAgreementContextType=a.persistedAgreementContextType;
    }

    /**
     * Creates a new {@link PersistentAgreementContainer} for the given agreement and agreement factory id.
     * 
     * @param agreement
     *            the agreement for which the container will be created
     * @param factoryId
     *            the id of the factory that created the agreement
     */
    public PersistentAgreementContainer( Agreement agreement, String factoryId )
    {
        agreementFactoryId = factoryId;

        // build wrapper
        persistenceContext = PersistenceAgreementContextDocument.Factory.newInstance();
        PersistenceAgreementContextType context = persistenceContext.addNewPersistenceAgreementContext();

        // store agreement properties
        context.addNewAgreementProperties();

        // store the whole agreement context itself
        PersistenceAgreementContextType.Entry entry = context.addNewEntry();
        entry.setValue( agreement.getContext() );
        entry.setName( DatabasePersistentAgreement.AGREEMENT_CONTEXT_ENTRY );

        // store the agreement properties
        entry = context.addNewEntry();

        entry.setValue( agreement.getXMLObject() );
        entry.setName( DatabasePersistentAgreement.AGREEMENT_PROPERTIES_ENTRY );
        context.getAgreementProperties().set( agreement.getXMLObject() );

        // set agreement class name
        setAgreementClassName( agreement.getImplementationClass().getName() );

        // set the current agreements' state and EPR
        setState( agreement.getState() );

        //
        // initial serialization of the xml object
        //
        this.persistedAgreementContextType = persistenceContext.xmlText();
    }

    /**
     * 
     * @return the agreement id
     */
    public String getAgreementId()
    {
        return agreementId;
    }

    /**
     * 
     * @return the agreement factory id
     */
    public String getAgreementFactoryId()
    {
        return agreementFactoryId;
    }

    /**
     * 
     * @param agreementFactoryId
     *            the factory id to set
     */
    public void setAgreementFactoryId( String agreementFactoryId )
    {
        this.agreementFactoryId = agreementFactoryId;
    }

    /**
     * 
     * @return the agreement state
     */
    public AgreementStateType getState()
    {
        AgreementPropertiesType props =
            persistenceContext.getPersistenceAgreementContext().getAgreementProperties();
        return props.getAgreementState();
    }

    /**
     * 
     * @param state
     *            the agreement state to set
     */
    public void setState( AgreementStateType state )
    {
        AgreementPropertiesType props =
            persistenceContext.getPersistenceAgreementContext().getAgreementProperties();
        props.setAgreementState( state );
        this.state = state.getState().toString();
    }

    /**
     * 
     * @return the agreement implementation class name
     */
    public String getAgreementClassName()
    {
        return agreementClassName;
    }

    /**
     * 
     * @param agreementClassName
     *            the agreement implementation class name to set
     */
    public void setAgreementClassName( String agreementClassName )
    {
        this.agreementClassName = agreementClassName;
    }

    /**
     * 
     * @return the context of the persistent agreement
     */
    public PersistenceAgreementContextType getPersistedAgreementContextType()
    {
        return persistenceContext.getPersistenceAgreementContext();
    }

    /**
     * 
     * @param persistenceAgreementContextType
     *            the context of the persistent agreement to set
     */
    public void
        setPersistedAgreementContextType( PersistenceAgreementContextType persistenceAgreementContextType )
    {
        this.persistenceContext.getPersistenceAgreementContext().set( persistenceAgreementContextType );
        this.persistedAgreementContextType = persistenceContext.xmlText();
    }

    /**
     * Creates a new persistent container that stores the agreement properties document and the agreement
     * execution context.
     * 
     * @param agreement
     *            the agreement
     * 
     * @param factoryId
     *            the id of the factory that created the agreement
     * 
     * @return the persistent container
     * 
     * @throws AgreementFactoryException
     *             indicates that the persistent container could not be created
     */
    public static PersistentAgreementContainer createContainer( Agreement agreement, String factoryId )
        throws AgreementFactoryException
    {
        LOG.debug( "Create new PersistentAgreementContainer " + factoryId );

        PersistentAgreementContainer container = new PersistentAgreementContainer( agreement, factoryId );

        //
        // serialize xml object
        //
        container.persistedAgreementContextType = container.persistenceContext.xmlText();

        // try to persist the wrapped agreement
        EntityManager em = EmfRegistry.getEntityManager();
        try
        {
            synchronized ( agreement )
            {
                em.getTransaction().begin();
                try
                {
                    LOG.info( "original agreement id: " + container.getAgreementId() + " " + container.getId() );

                    // persist and commit
                    em.persist( container );
                    em.getTransaction().commit();

                    String agreementId = container.getAgreementId();
                    LOG.info( "generated agreement id: " + agreementId + " " + container.getId() );

                }
                catch ( RollbackException ex )
                {
                    LOG.error( MessageFormat.format(
                        "Could not persist the wrapped agreement with id ''{0}''.",
                        agreement.getAgreementId() ), ex );

                    // roll back the persistment
                    em.getTransaction().rollback();

                    throw new AgreementFactoryException( MessageFormat.format(
                        "Could not persist the wrapped agreement with id ''{0}''.",
                        agreement.getAgreementId() ) );
                }

                // TODO: check if this can be done in one transaction
                //
                // update generated agreement ids
                //
                em.getTransaction().begin();
                try
                {
                    //
                    // update agreement id and serialize xml object
                    //
                    AgreementPropertiesType properties =
                        container.persistenceContext.getPersistenceAgreementContext()
                                                    .getAgreementProperties();
                    properties.setAgreementId( container.getAgreementId() );
                    agreement.setAgreementId( container.getAgreementId() );
                    container.persistedAgreementContextType = container.persistenceContext.xmlText();
                    em.merge( container );
                    em.getTransaction().commit();

                    LOG.trace( "updated agreement id: " + container.getAgreementId() );
                }
                catch ( RollbackException ex )
                {
                    LOG.error( LogMessage.getMessage(
                        "Could not persist the wrapped agreement with id ''{0}''.",
                        agreement.getAgreementId() ), ex );

                    // roll back the persistment
                    em.getTransaction().rollback();

                    String message = "Failed to update persistence context ''{0}'' with generated id.";
                    String error =
                        MessageFormat.format( message, new Object[] { agreement.getAgreementId() } );
                    throw new AgreementFactoryException( error );
                }
            }
        }
        finally
        {
            em.close();
        }

        return container;
    }

   
    /**
     * Loads a persistent container for the given agreement id and agreement factory id.
     * 
     * @param agreementId
     *            the id of the agreement to load
     * @param agreementFactoryId
     *            the id of the factory that created the agreement
     * @return the persistent container
     * 
     * @throws AgreementFactoryException
     *             indicates that the agreement could not be loaded
     */
    public static PersistentAgreementContainer loadContainer( String agreementId, String agreementFactoryId )
        throws AgreementFactoryException
    {
        //
        // load the persisted agreement
        //
        EntityManager em = EmfRegistry.getEntityManager();
        try
        {
            Query query = em.createNamedQuery( "PersistentAgreementContainer.findByAgreementId" );
            query.setParameter( "agreementId", agreementId );

            try
            {
                PersistentAgreementContainer container =
                    (PersistentAgreementContainer) query.getSingleResult();

                try
                {
                    String xml = container.persistedAgreementContextType;
                    container.persistenceContext =
                        (PersistenceAgreementContextDocument) XmlObject.Factory.parse( xml );
                }
                catch ( XmlException ex )
                {
                    LOG.error( "could not load persisted agreement context.", ex );
                    throw new AgreementFactoryException( "could not load persisted agreement context.", ex );
                }

                return container;

            }
            catch ( NoResultException ex )
            {
                //
                // not found, so we return null
                //
                return null;
            }
        }
        finally
        {
            em.close();
        }
    }

    /**
     * Loads a persistent container for the given agreement id and agreement factory id.
     * 
     * @param agreementId
     *            the id of the agreement to load
     * 
     * @return the persistent container
     * 
     * @throws AgreementFactoryException
     *             indicates that the agreement with the given id could not be loaded
     */
    public static PersistentAgreementContainer loadContainer( String agreementId )
        throws AgreementFactoryException
    {
        //
        // load the persisted agreement
        //
        EntityManager em = EmfRegistry.getEntityManager();
        try
        {
            Query query = em.createNamedQuery( "PersistentAgreementContainer.findByAgreementId" );
            query.setParameter( "agreementId", agreementId );

            try
            {
                PersistentAgreementContainer persistentAgreementContainer =
                    (PersistentAgreementContainer) query.getSingleResult();

                try
                {
                    String xml = persistentAgreementContainer.persistedAgreementContextType;
                    persistentAgreementContainer.persistenceContext =
                        (PersistenceAgreementContextDocument) XmlObject.Factory.parse( xml );
                }
                catch ( XmlException ex )
                {
                    LOG.error( "could not load persisted agreement context.", ex );
                    throw new AgreementFactoryException( "could not load persisted agreement context.", ex );
                }

                return persistentAgreementContainer;

            }
            catch ( NoResultException ex )
            {
                //
                // not found, so we return null
                //
                return null;
            }
        }
        finally
        {
            em.close();
        }
    }

    

    /**
     * Creates a list of all persistent agreement containers.
     * 
     * @return the persisted containers
     * 
     * @throws AgreementFactoryException
     *             indicates an error while listing the agreement containers
     */
    @SuppressWarnings( "unchecked" )
    public static PersistentAgreementContainer[] listContainers() throws AgreementFactoryException
    {
        //
        // load the persisted agreement
        //
        EntityManager em = EmfRegistry.getEntityManager();
        try
        {
            Query query = em.createNamedQuery( "PersistentAgreementContainer.findAll" );
            Collection<PersistentAgreementContainer> containers = null;
            try
            {
                containers = query.getResultList();

                for ( Iterator<PersistentAgreementContainer> iterator = containers.iterator(); iterator.hasNext(); )
                {
                    PersistentAgreementContainer persistentAgreementContainer = iterator.next();

                    try
                    {
                        String xml = persistentAgreementContainer.persistedAgreementContextType;
                        persistentAgreementContainer.persistenceContext =
                            (PersistenceAgreementContextDocument) XmlObject.Factory.parse( xml );
                    }
                    catch ( XmlException ex )
                    {
                        LOG.error( "could not load persisted agreement context.", ex );
                        throw new AgreementFactoryException( "could not load persisted agreement context.",
                            ex );
                    }
                }

                return containers.toArray( new PersistentAgreementContainer[containers.size()] );

            }
            catch ( NoResultException ex )
            {
                //
                // not found, so we return null
                //
                return null;
            }
        }
        finally
        {
            em.close();
        }
    }

    /**
     * Lists all persistent agreement containers for a given factory
     * 
     * @param agreementFactoryId
     *            the factory id
     * 
     * @return the container for the given factory id
     * 
     * @throws AgreementFactoryException
     *             indicates an error while listing the persistent containers for the given factory id
     */
    @SuppressWarnings( "unchecked" )
    public static PersistentAgreementContainer[] listContainers( String agreementFactoryId )
        throws AgreementFactoryException
    {
        //
        // load the persisted agreement
        //

    	
        EntityManager em = EmfRegistry.getEntityManager();
    	
        try
        {
        
            Query query = em.createNamedQuery( "PersistentAgreementContainer.findAll" );
        	
            Collection<PersistentAgreementContainer> containers = null;
            
            try
            {
            	containers = query.getResultList();
              	
                for ( Iterator<PersistentAgreementContainer> iterator = containers.iterator(); iterator.hasNext(); )
                {
             
                	PersistentAgreementContainer persistentAgreementContainer = iterator.next();
                	
                	try
                    {
                        String xml = persistentAgreementContainer.persistedAgreementContextType;
                        persistentAgreementContainer.persistenceContext =
                            (PersistenceAgreementContextDocument) XmlObject.Factory.parse( xml );
                    }
                    catch ( XmlException ex )
                    {
                        LOG.error( "could not load persisted agreement context.", ex );
                        throw new AgreementFactoryException( "could not load persisted agreement context.",
                            ex );
                    }
                }
                //LOG.error("lenght" + containersfinal.size());
                return containers.toArray( new PersistentAgreementContainer[containers.size()] );

            }
            catch ( NoResultException ex )
            {
                //
                // not found, so we return null
                //
                return null;
            }
        }
        finally
        {
            em.close();
        }
    }

    /**
     * Save the container instance.
     * 
     * @throws AgreementFactoryException
     *             indicates an error saving the container
     */
    public void saveContainer() throws AgreementFactoryException
    {
        // try to persist the wrapped agreement
        EntityManager em = EmfRegistry.getEntityManager();
        try
        {
            em.getTransaction().begin();
            try
            {
                Query query = em.createNamedQuery( "PersistentAgreementContainer.findByAgreementId" );
                query.setParameter( "agreementId", agreementId );

                PersistentAgreementContainer persistentAgreementContainer = null;
                try
                {
                    persistentAgreementContainer = (PersistentAgreementContainer) query.getSingleResult();
                }
                catch ( NoResultException ex )
                {
                    String error = "Persisted agreement not found in database. Update operation failed.";
                    LOG.error( error );
                    throw new AgreementFactoryException( error, ex );
                }

                //
                // make sure the agreement id was not changed
                //
                AgreementPropertiesType agreementProperties =
                    persistenceContext.getPersistenceAgreementContext().getAgreementProperties();

                agreementProperties.setAgreementId( agreementId );
                persistentAgreementContainer.agreementClassName = agreementClassName;
                persistentAgreementContainer.agreementFactoryId = agreementFactoryId;
                persistentAgreementContainer.agreementId = agreementId;
                persistentAgreementContainer.id = id;
                persistentAgreementContainer.persistedAgreementContextType = persistenceContext.xmlText();
                persistentAgreementContainer.persistenceContext = persistenceContext;
                persistentAgreementContainer.state =
                    agreementProperties.getAgreementState().getState().toString();

                em.merge( persistentAgreementContainer );

                em.getTransaction().commit();
            }
            catch ( Exception ex )
            {
                LOG.error( LogMessage.getMessage( "Could not update the wrapped agreement with id ''{0}''.",
                    getAgreementId() ), ex );

                // roll back the persistment
                em.getTransaction().rollback();

                throw new AgreementFactoryException( MessageFormat.format(
                    "Could not persist the wrapped agreement with id ''{0}''.", getAgreementId() ) );
            }

        }
        finally
        {
            em.close();
        }
    }

    /**
     * Deletes this container instance.
     * 
     * @throws AgreementFactoryException
     *             indicates an error while deleting the container
     */
    public void deleteContainer() throws AgreementFactoryException
    {
        // try to persist the wrapped agreement
        EntityManager em = EmfRegistry.getEntityManager();
        try
        {
            em.getTransaction().begin();
            try
            {
                Query query = em.createNamedQuery( "PersistentAgreementContainer.findByAgreementId" );
                query.setParameter( "agreementId", agreementId );

                try
                {
                    PersistentAgreementContainer persistentAgreementContainer =
                        (PersistentAgreementContainer) query.getSingleResult();
                    em.remove( persistentAgreementContainer );
                }
                catch ( NoResultException ex )
                {
                    LOG.warn( "Persisted agreement not found in database. Remove operation skipped." );
                }

                em.getTransaction().commit();
            }
            catch ( Exception ex )
            {
                LOG.error( LogMessage.getMessage( "Could not delete the wrapped agreement with id ''{0}''.",
                    getAgreementId() ), ex );

                // roll back the persistment
                em.getTransaction().rollback();

                throw new AgreementFactoryException( MessageFormat.format(
                    "Could not persist the wrapped agreement with id ''{0}''.", getAgreementId() ) );
            }

        }
        finally
        {
            em.close();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return persistenceContext.xmlText( new XmlOptions().setSavePrettyPrint() );
    }
    
    

}
