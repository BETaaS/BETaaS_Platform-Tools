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
package org.ogf.graap.wsag.server.api.impl;

import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlObject;
import org.ogf.graap.wsag.api.Agreement;
import org.ogf.graap.wsag.api.AgreementFactory;
import org.ogf.graap.wsag.api.AgreementOffer;
import org.ogf.graap.wsag.api.PendingAgreementListener;
import org.ogf.graap.wsag.api.types.AgreementDelegator;
import org.ogf.schemas.graap.wsAgreement.AgreementStateDefinition;
import org.ogf.schemas.graap.wsAgreement.TerminateInputType;

/**
 * PendingAgreementFacade
 * 
 * @author Oliver Waeldrich
 * 
 */
public class PendingAgreementFacade extends AgreementDelegator
    implements Runnable, Observer
{
    private static final Logger LOG = Logger.getLogger( PendingAgreementFacade.class );

    private AgreementFactory factory = null;

    private PendingAgreementListener listener = null;

    private final AgreementOffer offer;

    private final Map<String, Object> context;

    private final Observable observable = new Observable();

    /**
     * @return
     * @see java.util.Observable#hasChanged()
     */
    @Override
    public boolean hasChanged()
    {
        return observable.hasChanged() || getDelegator().hasChanged();
    }

    /**
     * @param o
     * @see java.util.Observable#addObserver(java.util.Observer)
     */
    @Override
    public void addObserver( Observer o )
    {
        observable.addObserver( o );
    }

    /**
     * @param o
     * @see java.util.Observable#deleteObserver(java.util.Observer)
     */
    public void deleteObserver( Observer o )
    {
        observable.deleteObserver( o );
    }

    /**
     * 
     * @see java.util.Observable#notifyObservers()
     */
    @Override
    public void notifyObservers()
    {
        //
        // When the facade is notified of an event, we call the delegation target's notification method. This
        // will in turn call the local update method and we publish the event to all locally registered
        // observers.
        //
        getDelegator().notifyObservers();
    }

    /**
     * @param arg
     * @see java.util.Observable#notifyObservers(java.lang.Object)
     */
    @Override
    public void notifyObservers( Object arg )
    {
        //
        // When the facade is notified of an event, we call the delegation target's notification method. This
        // will in turn call the local update method and we publish the event to all locally registered
        // observers.
        //
        getDelegator().notifyObservers( arg );
    }

    /**
     * 
     * @see java.util.Observable#deleteObservers()
     */
    public void deleteObservers()
    {
        observable.deleteObservers();
    }

    /**
     * @return
     * @see java.util.Observable#countObservers()
     */
    public int countObservers()
    {
        return observable.countObservers();
    }

    /**
     * Implementation of a pending agreement facade. The agreement creation is started in a separate process.
     * As long as the agreement creation is in progress, the facades represents the pending agreement. If the
     * agreement creation is successful, the pending agreement is replaced with the real one created by the
     * WSAG4J engine. This is an implementation of the state pattern (GOF).
     * 
     * @param offer
     *            the agreement offer
     * @param factory
     *            the agreement factory
     * @param listener
     *            the agreement acceptance client
     * @param context
     *            the factory invocation context
     */
    public PendingAgreementFacade( AgreementOffer offer, AgreementFactory factory,
                                   PendingAgreementListener listener, Map<String, Object> context )
    {
        super( new PendingAgreementImpl( offer ) );
        this.offer = offer;
        this.factory = factory;
        this.listener = listener;
        this.context = context;

        getState().setState( AgreementStateDefinition.PENDING );

        //
        // get notifed of all changes to the agreement instance
        //
        getDelegator().addObserver( this );
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run()
    {
        try
        {
            //
            // create the new agreement instance
            //
            Agreement newAgreement = factory.createAgreement( offer, context );

            //
            // set the agreement id to the id of the pending agreement, the pending agreement will therefore
            // be updated
            //
            newAgreement.setAgreementId( getAgreementId() );

            //
            // update new agreements execution properties, use the props from the pending agreement and
            // overwrite existing properties with the ones of the created agreement
            //
            Map<String, XmlObject> props = getExecutionContext();
            props.putAll( newAgreement.getExecutionContext() );

            newAgreement.getExecutionContext().putAll( props );

            //
            // update the observers new created agreement observer with the ones of the existing agreement
            // (i.e. database)
            //
            newAgreement.addObserver( this );

            //
            // notify the registered listeners of the decision
            //
            if ( getState().getState() == AgreementStateDefinition.PENDING_AND_TERMINATING )
            {
                try
                {
                    setDelegator( newAgreement );
                    terminate( TerminateInputType.Factory.newInstance() );
                }
                catch ( Exception e )
                {
                    // must not throw any exception
                    LOG.error( e.getMessage() );
                }
            }
            else
            {
                setDelegator( newAgreement );
                acceptAgreement();
            }

            //
            // notify the delegator that a change occured
            //
            notifyObservers();
        }
        catch ( Exception e )
        {
            if ( LOG.isInfoEnabled() )
            {
                LOG.info( "Error while creating pending agreement. Cause: " + e.getMessage() );
            }

            rejectAgreement();
        }
    }

    private void rejectAgreement()
    {
        getState().setState( AgreementStateDefinition.REJECTED );

        try
        {
            if ( listener != null )
            {
                listener.reject();
            }
        }
        catch ( Exception ex )
        {
            //
            // handler implementation failed, nothing we can do
            //
            LOG.error( ex.getMessage() );
        }
    }

    private void acceptAgreement()
    {
        try
        {
            if ( listener != null )
            {
                listener.accept();
            }
        }
        catch ( Exception ex )
        {
            //
            // handler implementation failed, nothing we can do
            //
            LOG.error( ex.getMessage() );
        }
    }

    @Override
    public void update( Observable o, Object arg )
    {
        observable.notifyObservers( arg );
    }

}
