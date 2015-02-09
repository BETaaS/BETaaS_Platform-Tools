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
package org.ogf.graap.wsag.server.persistence.impl;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlObject;
import org.ogf.graap.wsag.api.Agreement;
import org.ogf.graap.wsag.api.AgreementFactory;
import org.ogf.graap.wsag.api.AgreementFactoryContext;
import org.ogf.graap.wsag.api.AgreementOffer;
import org.ogf.graap.wsag.api.Negotiation;
import org.ogf.graap.wsag.api.PendingAgreementListener;
import org.ogf.graap.wsag.api.exceptions.AgreementFactoryException;
import org.ogf.graap.wsag.api.exceptions.NegotiationFactoryException;
import org.ogf.graap.wsag.api.logging.LogMessage;
import org.ogf.graap.wsag.server.persistence.PersistedResourceException;
import org.ogf.graap.wsag.server.persistence.PersistentAgreement;
import org.ogf.graap.wsag.server.persistence.PersistentAgreementFactory;
import org.ogf.schemas.graap.wsAgreement.AgreementTemplateType;
import org.ogf.schemas.graap.wsAgreement.negotiation.NegotiationContextType;
import org.w3.x2005.x08.addressing.EndpointReferenceType;

/**
 * A persistent factory can list all agreements that it has created, even after system restart. Agreement
 * creation itself is handles by a delegate, i.e. the factory class that is configured in the wsag4j config
 * file.
 * 
 * @author owaeld
 */
public abstract class AbstractPersistentAgreementFactory
    implements PersistentAgreementFactory
{

    private static final Logger LOG = Logger.getLogger( SimplePersistentAgreementFactory.class );

    /**
     * The factory implementation that handles agreement creation.
     */
    protected AgreementFactory factory;

    /**
     * The resource id of the factory. This id uniquely identifies the factory.
     */
    protected String resourceId;

    private final List<PersistentAgreement> activeAgreements = new Vector<PersistentAgreement>();

    /**
     * @param factory
     *            the delegation target for the {@link AgreementFactory} calls. This is typically an instance
     *            of {@link org.ogf.graap.wsag.server.engine.GenericAgreementFactory}.
     */
    public AbstractPersistentAgreementFactory( AgreementFactory factory )
    {
        this.factory = factory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AgreementFactory getAgreementFactory()
    {
        return factory;
    }

    /**
     * Persists the given agreement instance.
     * 
     * @param agreement
     *            the agreement to persist
     * @return the persisted agreement
     * 
     * @throws PersistedResourceException
     *             indicates that an error occurred while persisting the agreement
     */
    protected abstract PersistentAgreement persistAgreement( Agreement agreement )
        throws PersistedResourceException;

    /**
     * Loads all agreements that were created by this factory.
     * 
     * @return the loaded agreements
     * 
     * @throws PersistedResourceException
     *             indicates an error while loading the agreements from the persistence layer
     */
    protected abstract PersistentAgreement[] doLoad() throws PersistedResourceException;

    /**
     * Removes an agreement from the persistence layer
     * 
     * @param toRemove
     *            the agreement to remove
     * 
     * @throws PersistedResourceException
     *             indicates that the agreement could not be removed from the persistence layer
     */
    protected abstract void doRemove( PersistentAgreement toRemove ) throws PersistedResourceException;

    /**
     * @see org.ogf.graap.wsag.api.AgreementFactory#createPendingAgreement(org.ogf.graap.wsag.api.AgreementOffer)
     */
    @Override
    public Agreement createPendingAgreement( AgreementOffer offer, PendingAgreementListener listener,
                                             Map<String, Object> context ) throws AgreementFactoryException
    {
        Agreement agreementInstance = factory.createPendingAgreement( offer, listener, context );

        // create the agreement (with the persisted PersistentAgreementContainer
        PersistentAgreement persistentAgreement;

        try
        {
            persistentAgreement = persistAgreement( agreementInstance );
            activeAgreements.add( persistentAgreement );
        }
        catch ( PersistedResourceException e )
        {
            throw new AgreementFactoryException( "Failed to persist agreement.", e );
        }

        // try to save the agreement
        String agreementId = agreementInstance.getAgreementId();

        try
        {
            persistentAgreement.save();
        }
        catch ( Exception ex )
        {
            LOG.error( LogMessage.getMessage( "Could not save the new agreement ''{0}''.", agreementId ), ex );
        }

        return persistentAgreement;
    }

    /**
     * Creates a new agreement instance.
     * 
     * @param offer
     *            the agreement offer
     * 
     * @return the new agreement
     * 
     * @throws AgreementFactoryException
     *             indicates that the agreement offer was rejected or any other error while creating the
     *             agreement.
     * 
     * @see org.ogf.graap.wsag.api.AgreementFactory#createAgreement(org.ogf.graap.wsag.api.AgreementOffer)
     */
    @Override
    public Agreement createAgreement( AgreementOffer offer, Map<String, Object> context )
        throws AgreementFactoryException
    {
        Agreement agreement = factory.createAgreement( offer, context );

        // create the agreement (with the persisted PersistentAgreementContainer
        PersistentAgreement persistentAgreement;

        try
        {
            persistentAgreement = persistAgreement( agreement );
            activeAgreements.add( persistentAgreement );
        }
        catch ( PersistedResourceException e )
        {
            throw new AgreementFactoryException( "Failed to persist agreement.", e );
        }

        // try to save the agreement
        String agreementId = agreement.getAgreementId();

        try
        {
            persistentAgreement.save();
        }
        catch ( Exception ex )
        {
            LOG.error( LogMessage.getMessage( "Could not save the new agreement ''{0}''.", agreementId ), ex );
        }

        return persistentAgreement;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ogf.graap.wsag.server.persistence.impl.SimplePersistentAgreementFactory#addAgreement(org.ogf.graap.wsag.api.Agreement,
     *      org.w3.x2005.x08.addressing.EndpointReferenceType)
     */
    @Override
    public void addAgreement( Agreement agreement, EndpointReferenceType agreementEpr )
    {
        // create the agreement (with the persisted PersistentAgreementContainer

        try
        {
            PersistentAgreement persisted = persistAgreement( agreement );
            activeAgreements.add( persisted );
        }
        catch ( Exception ex )
        {
            LOG.error(
                LogMessage.getMessage( "Could not persist agreement ''{0}''.", agreement.getAgreementId() ),
                ex );
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ogf.graap.wsag.server.persistence.PersistentAgreementFactory#load()
     */
    @Override
    public void load() throws Exception
    {
        activeAgreements.clear();

        PersistentAgreement[] agreements = doLoad();
        // extract all agreements
        for ( int i = 0; i < agreements.length; i++ )
        {
            activeAgreements.add( agreements[i] );
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see PersistentAgreementFactory#save()
     */
    @Override
    public void save() throws Exception
    {
        Iterator<PersistentAgreement> it = activeAgreements.iterator();
        LOG.info("active agreements "+ activeAgreements.size());
        while ( it.hasNext() )
        {
            PersistentAgreement persistentAgreement = it.next();
            persistentAgreement.save();
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ogf.graap.wsag.server.persistence.PersistentAgreementFactory#list()
     */
    @Override
    public PersistentAgreement[] list()
    {
        // convert to array of persistent agreements
        return activeAgreements.toArray( new PersistentAgreement[activeAgreements.size()] );
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ogf.graap.wsag.server.persistence.IAgreementHome#list(java.lang.String)
     */
    @Override
    public PersistentAgreement[] list( String agreementFactoryId ) throws Exception
    {
        if ( resourceId.equals( agreementFactoryId ) )
        {
            return list();
        }
        else
        {
            return new PersistentAgreement[0];
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ogf.graap.wsag.server.persistence.IAgreementHome#find(java.lang.String)
     */
    @Override
    public PersistentAgreement find( String agreementId ) throws PersistedResourceException
    {
        LOG.debug( LogMessage.getMessage( "Try to find a agreement with id ''{0}''.", agreementId ) );

        synchronized ( activeAgreements )
        {
            for ( Iterator<PersistentAgreement> iterator = activeAgreements.iterator(); iterator.hasNext(); )
            {
                PersistentAgreement actual = iterator.next();

                if ( actual.getAgreement().getAgreementId().equals( agreementId ) )
                {
                    return actual;
                }
            }
        }

        String msgText = "agreement with id ''{0}'' was not found at factory ''{1}''.";
        String error = LogMessage.format( msgText, agreementId, getResourceId() );
        throw new PersistedResourceException( error );
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ogf.graap.wsag.server.persistence.IAgreementHome#remove(java.lang.String)
     */
    @Override
    public void remove( String agreementId ) throws PersistedResourceException
    {
        LOG.debug( LogMessage.getMessage( "Remove agreement with id''{0}''.", agreementId ) );

        //
        // find the agreement instance in the active list
        //
        PersistentAgreement toRemove = null;
        Iterator<PersistentAgreement> it = activeAgreements.iterator();
        while ( it.hasNext() )
        {
            PersistentAgreement persistentAgreement = it.next();
            if ( persistentAgreement.getAgreement().getAgreementId().equals( agreementId ) )
            {
                toRemove = persistentAgreement;
                break;
            }
        }

        if ( toRemove == null )
        {
            //
            // no agreement with the given id in this factory
            //
            String msgText = "The agreement with id ''{0}'' does not exist at factory ''{1}''";
            LOG.debug( LogMessage.getMessage( msgText, agreementId, getResourceId() ) );

            String removeTxt = "Agreement ''{0}'' was not removed. Agreement does not exist in factory.";
            throw new PersistedResourceException( LogMessage.format( removeTxt, agreementId ) );
        }

        try
        {
            synchronized ( activeAgreements )
            {
                doRemove( toRemove );
                activeAgreements.remove( toRemove );
            }
        }
        catch ( Exception e )
        {
            LOG.error( LogMessage.getMessage( "Agreement ''{0}'' could not be removed.", agreementId ), e );
        }

        LOG.debug( LogMessage.getMessage( "Agreement ''{0}'' removed.", agreementId ) );
    }

    /**
     * @return the resourceId
     */
    @Override
    public String getResourceId()
    {
        return resourceId;
    }

    /**
     * @param resourceId
     *            the resourceId to set
     */
    public void setResourceId( String resourceId )
    {
        this.resourceId = resourceId;

        String message = "Replaced generated unique resource id with set resource id ''{0}''.";
        LOG.debug( LogMessage.getMessage( message, resourceId ) );
    }

    /**
     * @return the supported templates
     * 
     * @see org.ogf.graap.wsag.api.AgreementFactory#getTemplates()
     */
    @Override
    public AgreementTemplateType[] getTemplates()
    {
        return factory.getTemplates();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Negotiation
        initiateNegotiation( NegotiationContextType context, XmlObject[] criticalExtensions,
                             XmlObject[] nonCriticalExtensions, Map<String, Object> environment )
            throws NegotiationFactoryException
    {

        Negotiation negotiation =
            factory.initiateNegotiation( context, criticalExtensions, nonCriticalExtensions, environment );

        return negotiation;
    }

    /**
     * @return
     * @see org.ogf.graap.wsag.api.AgreementFactory#getFactoryContext()
     */
    @Override
    public AgreementFactoryContext getFactoryContext()
    {
        return factory.getFactoryContext();
    }

    /**
     * @param context
     * @see org.ogf.graap.wsag.api.AgreementFactory#setFactoryContext(org.ogf.graap.wsag.api.AgreementFactoryContext)
     */
    @Override
    public void setFactoryContext( AgreementFactoryContext context )
    {
        factory.setFactoryContext( context );
    }
}
