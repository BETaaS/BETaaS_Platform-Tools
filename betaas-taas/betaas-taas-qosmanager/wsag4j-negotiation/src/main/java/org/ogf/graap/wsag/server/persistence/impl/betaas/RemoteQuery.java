

package org.ogf.graap.wsag.server.persistence.impl.betaas;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;
import javax.persistence.Parameter;
import javax.persistence.Query;
import javax.persistence.TemporalType;

import org.ogf.graap.wsag.server.persistence.impl.PersistentAgreementContainer;

import eu.betaas.taas.bigdatamanager.database.hibernate.data.PersistentAgreementContainerDatabase;

public class RemoteQuery implements Query {

	private Query qr;
	
	public RemoteQuery(Query q){
		qr = q;
	}
	
	public List getResultList() {
		List<Object> l = qr.getResultList();
		List<Object> ret = new ArrayList();
		
		for(int i=0; i < l.size(); i++){
			PersistentAgreementContainerDatabase o = (PersistentAgreementContainerDatabase) l.get(i);
			
			PersistentAgreementContainer abs = new PersistentAgreementContainer(o);
			
			ret.add(abs);
		}
		
		return ret;
	}

	public Object getSingleResult() {
		PersistentAgreementContainerDatabase o = (PersistentAgreementContainerDatabase) qr.getSingleResult();
		
		PersistentAgreementContainer abs = new PersistentAgreementContainer(o);
		
		return abs;
	}

	public int executeUpdate() {
		return qr.executeUpdate();
	}

	public Query setMaxResults(int maxResult) {
		return qr.setMaxResults(maxResult);
	}

	public int getMaxResults() {
		return qr.getMaxResults();
	}

	public Query setFirstResult(int startPosition) {
		return qr.setFirstResult(startPosition);
	}

	public int getFirstResult() {
		return qr.getFirstResult();
	}

	public Query setHint(String hintName, Object value) {
		return qr.setHint(hintName, value);
	}

	public Map<String, Object> getHints() {
		return qr.getHints();
	}

	public <T> Query setParameter(Parameter<T> param, T value) {
		return qr.setParameter(param, value);
	}

	public Query setParameter(Parameter<Calendar> param, Calendar value,
			TemporalType temporalType) {
		return qr.setParameter(param, value, temporalType);
	}

	public Query setParameter(Parameter<Date> param, Date value,
			TemporalType temporalType) {
		return qr.setParameter(param, value, temporalType);
	}

	public Query setParameter(String name, Object value) {
		return qr.setParameter(name, value);
	}

	public Query setParameter(String name, Calendar value,
			TemporalType temporalType) {
		return qr.setParameter(name, value, temporalType);
	}

	public Query setParameter(String name, Date value, TemporalType temporalType) {
		return qr.setParameter(name, value, temporalType);
	}

	public Query setParameter(int position, Object value) {
		return qr.setParameter(position, value);
	}

	public Query setParameter(int position, Calendar value,
			TemporalType temporalType) {
		return qr.setParameter(position, value, temporalType);
	}

	public Query setParameter(int position, Date value,
			TemporalType temporalType) {
		return qr.setParameter(position, value, temporalType);
	}

	public Set<Parameter<?>> getParameters() {
		return qr.getParameters();
	}

	public Parameter<?> getParameter(String name) {
		return qr.getParameter(name);
	}

	public <T> Parameter<T> getParameter(String name, Class<T> type) {
		return qr.getParameter(name, type);
	}

	public Parameter<?> getParameter(int position) {
		return qr.getParameter(position);
	}

	public <T> Parameter<T> getParameter(int position, Class<T> type) {
		return qr.getParameter(position, type);
	}

	public boolean isBound(Parameter<?> param) {
		return qr.isBound(param);
	}

	public <T> T getParameterValue(Parameter<T> param) {
		return qr.getParameterValue(param);
	}

	public Object getParameterValue(String name) {
		return qr.getParameterValue(name);
	}

	public Object getParameterValue(int position) {
		return qr.getParameterValue(position);
	}

	public Query setFlushMode(FlushModeType flushMode) {
		return qr.setFlushMode(flushMode);
	}

	public FlushModeType getFlushMode() {
		return qr.getFlushMode();
	}

	public Query setLockMode(LockModeType lockMode) {
		return qr.setLockMode(lockMode);
	}

	public LockModeType getLockMode() {
		return qr.getLockMode();
	}

	public <T> T unwrap(Class<T> cls) {
		return qr.unwrap(cls);
	}

}
