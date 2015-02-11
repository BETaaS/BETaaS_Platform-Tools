// BETaaS - Building the Environment for the Things as a Service
//
// Component: WSAG4J negotiator
// Responsible: Carlo Vallati 

package org.ogf.graap.wsag.server.persistence;

import java.text.MessageFormat;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.apache.log4j.Logger;
import org.ogf.graap.wsag.api.logging.LogMessage;
import org.ogf.graap.wsag.server.persistence.impl.betaas.RemoteEntityManager;

/**
 * Emfregistry modified class for BETaaS integration, it refers to bigdatamanager-database as persistence storage
 * @author C. Vallati 
 */
public class EmfRegistry
{

    private static final Logger LOG = Logger.getLogger( EmfRegistry.class );

    // The bigdatamanager-database allows to export only one EntityManager, it has to be kept open! -> modify accordingly the class using it
    private static RemoteEntityManager em;


    /**
     * Sets the persistence mode.
     * 
     * @param persistenceMode
     *            the persistence mode to set
     */
    public static void setPersistenceMode( String persistenceMode )
    {
        // Doing nothing, into betass there is only one mode...

        LOG.debug( LogMessage.getMessage( "Set wsag4j persistence mode to: {0}", persistenceMode ) );
    }

    /**
     * returns the entity manager.
     * 
     * @return the new entity manager
     */
    public static EntityManager getEntityManager()
    {
        return em;
    }

    /**
     * Set the entity manager.
     */
    public static void setEntityManager(EntityManager e){
    	em = new RemoteEntityManager(e);
    }

    /**
     * Returns the info message.
     * 
     * @return info message
     */
    public static String printInfo()
    {
        return new String("EmfRegistry [BETaaS implementation");
    }

    /**
     * Closes the entity manager factory and all entity managers.
     */
    public static synchronized void finalizeEmfRegistry()
    {
        // Nothing to do here....
    }
}
