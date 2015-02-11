/*
 Copyright 2014-2015 Hewlett-Packard Development Company, L.P.

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */
package eu.betaas.taas.bigdatamanager.database.hibernate;

import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.spi.PersistenceProvider;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

public class HibernateSupport {

	private static final String CONFIG_PROP = "taas-jpa";
	
	private static EntityManagerFactory emf;

	public static EntityManager getEntityManager() {
		return getEntityManagerFactory().createEntityManager();
	}

	public static EntityManager getEntityManager(Map<String,Object> props) {
		return getEntityManagerFactory(props).createEntityManager();
	}	
	
	private static EntityManagerFactory getEntityManagerFactory() {
		if ( emf == null ) {
			Bundle thisBundle = FrameworkUtil.getBundle( HibernateSupport.class );
			// Could get this by wiring up OsgiTestBundleActivator as well.
			BundleContext context = thisBundle.getBundleContext();

			ServiceReference serviceReference = context.getServiceReference( PersistenceProvider.class.getName() );
			PersistenceProvider persistenceProvider = (PersistenceProvider) context.getService( serviceReference );

			emf = persistenceProvider.createEntityManagerFactory( CONFIG_PROP, null );
		}
		return emf;
	}	
	
	private static EntityManagerFactory getEntityManagerFactory(Map<String,Object> props) {
		if ( emf == null ) {
			Bundle thisBundle = FrameworkUtil.getBundle( HibernateSupport.class );
			// Could get this by wiring up OsgiTestBundleActivator as well.
			BundleContext context = thisBundle.getBundleContext();

			ServiceReference serviceReference = context.getServiceReference( PersistenceProvider.class.getName() );
			PersistenceProvider persistenceProvider = (PersistenceProvider) context.getService( serviceReference );

		
				
			emf = persistenceProvider.createEntityManagerFactory( CONFIG_PROP, props );
		}
		return emf;
	}

	
}


