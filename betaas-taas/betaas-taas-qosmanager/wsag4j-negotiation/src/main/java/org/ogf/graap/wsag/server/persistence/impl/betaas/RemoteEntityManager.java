// BETaaS - Building the Environment for the Things as a Service
//
// Component: WSAG4J negotiator
// Responsible: Carlo Vallati 

/**
 * RemoteEntityManager modified EntityManager to manage transparently the fact that the manager is remote
 * @author C. Vallati 
 */

package org.ogf.graap.wsag.server.persistence.impl.betaas;

import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.metamodel.Metamodel;

import org.apache.log4j.Logger;
import org.ogf.graap.wsag.server.persistence.impl.PersistentAgreementContainer;

import eu.betaas.taas.bigdatamanager.database.hibernate.data.PersistentAgreementContainerDatabase;;

public class RemoteEntityManager implements EntityManager {

	private static final Logger LOG = Logger.getLogger( RemoteEntityManager.class );
	
	private EntityManager em;
	
	public RemoteEntityManager(EntityManager e){
		em = e;
	}
	
	public void persist(Object entity) {
		
		PersistentAgreementContainer container = (PersistentAgreementContainer) entity; 
		
		PersistentAgreementContainerDatabase abs = new PersistentAgreementContainerDatabase(container); 
		
		em.persist(abs);
		
		// The only thing modified can be the id and the agreementId
		container.id = abs.id;
		container.agreementId = abs.agreementId;
	}

	public <T> T merge(T entity) {
		
		PersistentAgreementContainer container = (PersistentAgreementContainer) entity; 
		
		PersistentAgreementContainerDatabase abs = new PersistentAgreementContainerDatabase(container); 

		T ret = (T) em.merge(abs);
		
		// We suppose that 
		container.id = abs.id;
		container.agreementId = abs.agreementId;
		
		return ret;
	}

	public void remove(Object entity) {
		em.remove(entity);
		
	}

	public <T> T find(Class<T> entityClass, Object primaryKey) {
		return em.find(entityClass, primaryKey);
	}

	public <T> T find(Class<T> entityClass, Object primaryKey,
			Map<String, Object> properties) {
		return em.find(entityClass, primaryKey, properties);
	}

	public <T> T find(Class<T> entityClass, Object primaryKey,
			LockModeType lockMode) {
		return em.find(entityClass, primaryKey, lockMode);
	}

	public <T> T find(Class<T> entityClass, Object primaryKey,
			LockModeType lockMode, Map<String, Object> properties) {
		return em.find(entityClass, primaryKey, lockMode, properties);
	}

	public <T> T getReference(Class<T> entityClass, Object primaryKey) {
		return em.getReference(entityClass, primaryKey);
	}

	public void flush() {
		em.flush();
	}

	public void setFlushMode(FlushModeType flushMode) {
		em.setFlushMode(flushMode);
		
	}

	public FlushModeType getFlushMode() {
		return em.getFlushMode();
	}

	public void lock(Object entity, LockModeType lockMode) {
		em.lock(entity, lockMode);
		
	}

	public void lock(Object entity, LockModeType lockMode,
			Map<String, Object> properties) {
		em.lock(entity, lockMode, properties);
	}

	public void refresh(Object entity) {
		em.refresh(entity);
		
	}

	public void refresh(Object entity, Map<String, Object> properties) {
		em.refresh(entity, properties);
		
	}

	public void refresh(Object entity, LockModeType lockMode) {
		em.refresh(entity, lockMode);
		
	}

	public void refresh(Object entity, LockModeType lockMode,
			Map<String, Object> properties) {
		em.refresh(entity, lockMode, properties);
		
	}

	public void clear() {
		em.clear();
		
	}

	public void detach(Object entity) {
		em.detach(entity);
		
	}

	public boolean contains(Object entity) {
		return em.contains(entity);
	}

	public LockModeType getLockMode(Object entity) {
		return em.getLockMode(entity);
	}

	public void setProperty(String propertyName, Object value) {
		em.setProperty(propertyName, value);
	}

	public Map<String, Object> getProperties() {
		return em.getProperties();
	}

	public Query createQuery(String qlString) {
		return em.createQuery(qlString);
	}

	public <T> TypedQuery<T> createQuery(CriteriaQuery<T> criteriaQuery) {
		return em.createQuery(criteriaQuery);
	}

	public <T> TypedQuery<T> createQuery(String qlString, Class<T> resultClass) {
		return em.createQuery(qlString, resultClass);
	}

	public Query createNamedQuery(String name) {
		// I need to hide the fact that the query is remote...
		String newName = name.replaceAll("PersistentAgreementContainer", "PersistentAgreementContainerDatabase"); // Database persist the type defined in the database bundle
		Query q = em.createNamedQuery(newName); 
		RemoteQuery rq = new RemoteQuery(q);
		return rq;
	}

	public <T> TypedQuery<T> createNamedQuery(String name, Class<T> resultClass) {
		return em.createNamedQuery(name, resultClass);
	}

	public Query createNativeQuery(String sqlString) {
		return em.createNativeQuery(sqlString);
	}

	public Query createNativeQuery(String sqlString, Class resultClass) {
		return em.createNativeQuery(sqlString, resultClass);
	}

	public Query createNativeQuery(String sqlString, String resultSetMapping) {
		return em.createNativeQuery(sqlString, resultSetMapping);
	}

	public void joinTransaction() {
		em.joinTransaction();
	}

	public <T> T unwrap(Class<T> cls) {
		return em.unwrap(cls);
	}

	public Object getDelegate() {
		return em.getDelegate();
	}

	public void close() {
		//LOG.error("Hey here I'm closing...");
		// The remote EntityManager is shared, be generous don't close it, someone might need it...
		//em.close();
	}

	public boolean isOpen() {
		return em.isOpen();
	}

	public EntityTransaction getTransaction() {
		return em.getTransaction();
	}

	public EntityManagerFactory getEntityManagerFactory() {
		return em.getEntityManagerFactory();
	}

	public CriteriaBuilder getCriteriaBuilder() {
		return em.getCriteriaBuilder();
	}

	public Metamodel getMetamodel() {
		return em.getMetamodel();
	}

}
