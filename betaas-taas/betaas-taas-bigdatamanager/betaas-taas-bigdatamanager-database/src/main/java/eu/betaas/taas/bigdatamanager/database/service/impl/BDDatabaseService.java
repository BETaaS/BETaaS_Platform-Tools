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
package eu.betaas.taas.bigdatamanager.database.service.impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.h2.jdbcx.JdbcConnectionPool;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import eu.betaas.rabbitmq.publisher.interfaces.Publisher;
import eu.betaas.rabbitmq.publisher.interfaces.utils.Message;
import eu.betaas.rabbitmq.publisher.interfaces.utils.MessageBuilder;
import eu.betaas.rabbitmq.publisher.interfaces.utils.Message.Layer;
import eu.betaas.taas.bigdatamanager.database.hibernate.HibernateSupport;
import eu.betaas.taas.bigdatamanager.database.hibernate.data.AgreementEprContainer;
import eu.betaas.taas.bigdatamanager.database.hibernate.data.AppService;
import eu.betaas.taas.bigdatamanager.database.hibernate.data.ApplicationRegistry;
import eu.betaas.taas.bigdatamanager.database.hibernate.data.PersistentAgreementContainerDatabase;
import eu.betaas.taas.bigdatamanager.database.hibernate.data.QoSMAssignmentInternal;
import eu.betaas.taas.bigdatamanager.database.hibernate.data.QoSMAssignmentStar;
import eu.betaas.taas.bigdatamanager.database.hibernate.data.QoSMAssuredRequestInternal;
import eu.betaas.taas.bigdatamanager.database.hibernate.data.QoSMAssuredRequestStar;
import eu.betaas.taas.bigdatamanager.database.hibernate.data.QoSMEquivalentThingServiceInternal;
import eu.betaas.taas.bigdatamanager.database.hibernate.data.QoSMEquivalentThingServiceStar;
import eu.betaas.taas.bigdatamanager.database.hibernate.data.QoSMRequestInternal;
import eu.betaas.taas.bigdatamanager.database.hibernate.data.QoSMRequestStar;
import eu.betaas.taas.bigdatamanager.database.hibernate.data.QoSMThingInternal;
import eu.betaas.taas.bigdatamanager.database.hibernate.data.QoSMThingServiceInternal;
import eu.betaas.taas.bigdatamanager.database.hibernate.data.QoSMThingServiceStar;
import eu.betaas.taas.bigdatamanager.database.hibernate.data.QoSMThingStar;
import eu.betaas.taas.bigdatamanager.database.hibernate.data.SimulatedThing;
import eu.betaas.taas.bigdatamanager.database.hibernate.data.ThingData;
import eu.betaas.taas.bigdatamanager.database.hibernate.data.ThingInformation;
import eu.betaas.taas.bigdatamanager.database.hibernate.data.TrustManagerService;
import eu.betaas.taas.bigdatamanager.database.hibernate.data.ExtServiceRegistry;
import eu.betaas.taas.bigdatamanager.database.service.IBigDataDatabaseService;
import eu.betaas.taas.bigdatamanager.database.hibernate.data.ExtServService;

public class BDDatabaseService implements IBigDataDatabaseService {

	private static String JDBCDRIVER="org.h2.Driver";
	private boolean enabledbus=false;
	private String jdbcurl;
	private String user;
	private String pwd;
	private String gwid;
	private String DROP_TABLE_ONTO="DROP TABLE IF EXISTS ";
	private Statement dropstat;
	private JdbcConnectionPool connectionPool;
	private Logger logger;
	private EntityManager entityManager;
	private Map<String,Object> config;
	private BundleContext context;
	MessageBuilder mb;

	public void setup() throws ClassNotFoundException, SQLException  {

		logger = Logger.getLogger("betaas.taas");

		logger.debug("BETaaS TaaS DB started");
		logger.debug("Drivers used: "+JDBCDRIVER);
		Class.forName(JDBCDRIVER);
		logger.debug("Creating database connection instance");
		connectionPool = null;		
		config = new HashMap<String,Object>();
		config.put("hibernate.connection.url", jdbcurl);
		config.put("hibernate.connection.username",user); 
		config.put("hibernate.connection.password",pwd);
	
		entityManager = HibernateSupport.getEntityManager(config);

        Map<String, Object> mp = entityManager.getProperties();
        System.out.println("get this "+mp.toString());
        logger.debug("Going to create a cpool for " + jdbcurl + " un "+user + " pwd " + pwd);
        logger.debug("Hibernate session created succesfully");
		logger.debug("Creating connection pool");
		connectionPool = JdbcConnectionPool.create(jdbcurl, user, pwd);
		Connection delConn= connectionPool.getConnection();
		String table_drop = DROP_TABLE_ONTO + "nodes"; 
		logger.debug("Going to drop table ");
		
		dropstat=delConn.createStatement();
		dropstat.executeUpdate(table_drop);
		dropstat.close();
		logger.debug("Table nodes dropped");
		
		table_drop = DROP_TABLE_ONTO + "prefixes"; 
		logger.debug("Going to drop table ");
		
		dropstat=delConn.createStatement();
		dropstat.executeUpdate(table_drop);
		dropstat.close();
		logger.info("Table prefixes dropped");

		table_drop = DROP_TABLE_ONTO + "quads"; 
		logger.debug("Going to drop table ");
		
		dropstat=delConn.createStatement();
		dropstat.executeUpdate(table_drop);
		dropstat.close();
		logger.debug("Table quads dropped");
		
		
		table_drop = DROP_TABLE_ONTO + "triples"; 
		logger.debug("Going to drop table ");
		
		dropstat=delConn.createStatement();
		dropstat.executeUpdate(table_drop);
		dropstat.close();
		logger.debug("Table quads dropped");
		logger.debug("Closing drop connection");
		delConn.close();
		mb = new MessageBuilder();
		busMessage("Created empty TaaS Database "+ jdbcurl +" on Gateway "+gwid);
		logger.debug("Created connection pool");
		
		
	}

	public Connection getConnection() throws SQLException {
		
		logger.debug("Returning a connection from the pool");
		if (connectionPool == null) {
			logger.error("Connection not configured");
			return null;
		}
		logger.debug("Going to create a cpool for " + jdbcurl + " un "+user + " pwd " + pwd);
		//busMessage("Requested a new connection from the connection pool: Active Connections:"+connectionPool.getActiveConnections());
		logger.debug("Active connections " + connectionPool.getActiveConnections());
		logger.debug("Max conn " + connectionPool.getMaxConnections());
		logger.debug("Timeout " + connectionPool.getLoginTimeout());
		
		
		return connectionPool.getConnection();
		
	}

	public void close() {

		logger.debug("Destroying the connection pool and the session");
		entityManager.close();
		if (connectionPool!=null){
			connectionPool.dispose();
		}
	}
	
	public EntityManager getEntityManager() {
		return entityManager;
	}

	public void setEntityManager(EntityManager entityManager) {
		logger.debug("Setting the entity manager");
		this.entityManager = entityManager;
	}
	
	public void setJdbcurl(String jdbcurl) {
		this.jdbcurl = jdbcurl;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public  void updateAgreementEprContainer(AgreementEprContainer agreementEprContainer) {
		entityManager.getTransaction().begin();
		AgreementEprContainer epr = entityManager.find(AgreementEprContainer.class, agreementEprContainer.getId());
		epr.setAgreementFactoryId(agreementEprContainer.getAgreementFactoryId());
		epr.setAgreementId(agreementEprContainer.getAgreementId());
		epr.setEpr(agreementEprContainer.getEpr());
		epr.setEprAddress(agreementEprContainer.getEprAddress());
		entityManager.getTransaction().commit();
		
	}

	public  void updateApplicationRegistry(ApplicationRegistry applicationRegistry) {
		entityManager.getTransaction().begin();
		ApplicationRegistry epr = entityManager.find(ApplicationRegistry.class, applicationRegistry.getId());
		epr.setAddress(applicationRegistry.getAddress());
		epr.setName(applicationRegistry.getName());
		entityManager.getTransaction().commit();		
	}
	
	public  void updateExtServiceRegistry(ExtServiceRegistry extServiceRegistry) {
		entityManager.getTransaction().begin();
		ExtServiceRegistry epr = entityManager.find(ExtServiceRegistry.class, extServiceRegistry.getId());
		epr.setName(extServiceRegistry.getName());
		epr.setStatus(extServiceRegistry.getStatus());
		entityManager.getTransaction().commit();
	}
	
	public  void updateExtServService(ExtServService extServService) {
		entityManager.getTransaction().begin();
		ExtServService epr = entityManager.find(ExtServService.class, extServService.getId());
		epr.setName(extServService.getName());
		epr.setCredentials(extServService.getCredentials());
		epr.setQos_specs(extServService.getQos_specs());
		epr.setSemantic_specs(extServService.getSemantic_specs());
		epr.setStatus(extServService.getStatus());
		
		entityManager.getTransaction().commit();
	}

	public  void updateAppService(AppService appService) {
		entityManager.getTransaction().begin();
		AppService epr = entityManager.find(AppService.class, appService.getId());
//		epr.setApp_id(appService.getApp_id());
		epr.setCredentials(appService.getCredentials());
		epr.setName(appService.getName());
		epr.setQos_specs(appService.getQos_specs());
		epr.setSemantic_specs(appService.getSemantic_specs());
		entityManager.getTransaction().commit();
		
	}

	public  void updatePersistentAgreementContainer(PersistentAgreementContainerDatabase persistentAgreementContainer) {
		entityManager.getTransaction().begin();
		PersistentAgreementContainerDatabase epr = entityManager.find(PersistentAgreementContainerDatabase.class, persistentAgreementContainer.getId());
		epr.setAgreementClassName(persistentAgreementContainer.getAgreementClassName());
		epr.setAgreementFactoryId(persistentAgreementContainer.getAgreementFactoryId());
		epr.setAgreementId(persistentAgreementContainer.getAgreementId());
		epr.setPersistedAgreementContextTypeString(persistentAgreementContainer.getPersistedAgreementContextTypeString());
		epr.setStateString(persistentAgreementContainer.getStateString());
		entityManager.getTransaction().commit();
		
	}

	public  void updateTrustManagerService(TrustManagerService trustManagerService) {
		entityManager.getTransaction().begin();
		TrustManagerService epr = entityManager.find(TrustManagerService.class, trustManagerService.getThingServiceId());
		epr.setBatteryLoad(epr.getBatteryLoad());
		epr.setDataStability(epr.getDataStability());
		epr.setDependability(epr.getDependability());
		epr.setQoSFulfillment(epr.getQoSFulfillment());
		epr.setScalability(epr.getScalability());
		epr.setSecurityMechanisms(epr.getSecurityMechanisms());
		epr.setThingServiceTrust(epr.getThingServiceTrust());
		epr.setTimestamp(epr.getTimestamp());
		entityManager.getTransaction().commit();
		
	}

	public AgreementEprContainer searchAgreementEprContainer(AgreementEprContainer agreementEprContainer) {
		return entityManager.find(AgreementEprContainer.class,agreementEprContainer.getId());
	}

	public ApplicationRegistry searchApplicationRegistry(ApplicationRegistry applicationRegistry) {
		return entityManager.find(ApplicationRegistry.class,applicationRegistry.getId());
	}
	
	public ExtServService searchExtServService(ExtServService extServService) {
		return entityManager.find(ExtServService.class,extServService.getId());
	}
	
	public ExtServiceRegistry searchExtServiceRegistry(ExtServiceRegistry extServiceRegistry) {
		return entityManager.find(ExtServiceRegistry.class,extServiceRegistry.getId());
	}

	public AppService searchAppService(AppService appService) {
		return entityManager.find(AppService.class,appService.getId());
	}

	public PersistentAgreementContainerDatabase searchPersistentAgreementContainer(PersistentAgreementContainerDatabase persistentAgreementContainer) {
		return entityManager.find(PersistentAgreementContainerDatabase.class,persistentAgreementContainer.getId());
	}

	public TrustManagerService searchTrustManagerService(TrustManagerService trustManagerService) {
		return entityManager.find(TrustManagerService.class,trustManagerService.gettrustManagerServiceId());
	}

	public  void deleteAgreementEprContainer(AgreementEprContainer agreementEprContainer) {
		entityManager.getTransaction().begin();
		AgreementEprContainer epr = entityManager.find(AgreementEprContainer.class, agreementEprContainer.getId());
		entityManager.remove(epr);
		entityManager.getTransaction().commit();
		
	}

	public  void deleteApplicationRegistry(ApplicationRegistry applicationRegistry) {
		entityManager.getTransaction().begin();
		ApplicationRegistry epr = entityManager.find(ApplicationRegistry.class, applicationRegistry.getId());
		entityManager.remove(epr);
		entityManager.getTransaction().commit();
	}
	
	public  void deleteExtServService(ExtServService extServService) {
		entityManager.getTransaction().begin();
		ExtServService epr = entityManager.find(ExtServService.class, extServService.getId());
		entityManager.remove(epr);
		entityManager.getTransaction().commit();
	}
	
	public  void deleteExtServiceRegistry(ExtServiceRegistry extServiceRegistry) {
		entityManager.getTransaction().begin();
		ExtServiceRegistry epr = entityManager.find(ExtServiceRegistry.class, extServiceRegistry.getId());
		entityManager.remove(epr);
		entityManager.getTransaction().commit();
	}

	public  void deleteAppService(AppService appService) {
		entityManager.getTransaction().begin();
		AppService epr = entityManager.find(AppService.class, appService.getId());
		entityManager.remove(epr);
		entityManager.getTransaction().commit();
		
	}

	public  void deletePersistentAgreementContainer(PersistentAgreementContainerDatabase persistentAgreementContainer) {
		entityManager.getTransaction().begin();
		PersistentAgreementContainerDatabase epr = entityManager.find(PersistentAgreementContainerDatabase.class, persistentAgreementContainer.getId());
		entityManager.remove(epr);
		entityManager.getTransaction().commit();
		
	}

	public  void deleteTrustManagerService(TrustManagerService trustManagerService) {
		entityManager.getTransaction().begin();
		TrustManagerService epr = entityManager.find(TrustManagerService.class, trustManagerService.getThingServiceId());
		entityManager.remove(epr);
		entityManager.getTransaction().commit();
		
	}

	public  void saveAgreementEprContainer(AgreementEprContainer agreementEprContainer) {
		entityManager.getTransaction().begin();
		entityManager.persist(agreementEprContainer);
		entityManager.getTransaction().commit();
		
	}

	public  void saveApplicationRegistry(ApplicationRegistry applicationRegistry) {
		entityManager.getTransaction().begin();
		entityManager.persist(applicationRegistry);
		entityManager.getTransaction().commit();
		
	}
	
	public  void saveExtServService(ExtServService extServService) {
		entityManager.getTransaction().begin();
		entityManager.persist(extServService);
		entityManager.getTransaction().commit();
	}
	
	public  void saveExtServiceRegistry(ExtServiceRegistry extServiceRegistry) {
		entityManager.getTransaction().begin();
		entityManager.persist(extServiceRegistry);
		entityManager.getTransaction().commit();
		
	}

	public  void saveAppService(AppService appService) {
		entityManager.getTransaction().begin();
		entityManager.persist(appService);
		entityManager.getTransaction().commit();
		
	}

	public  void savePersistentAgreementContainer(PersistentAgreementContainerDatabase persistentAgreementContainer) {
		entityManager.getTransaction().begin();
		entityManager.persist(persistentAgreementContainer);
		entityManager.getTransaction().commit();	
	}

	public  void saveTrustManagerService(TrustManagerService trustManagerService) {
		entityManager.getTransaction().begin();
		entityManager.persist(trustManagerService);
		entityManager.getTransaction().commit();
		
	}

	public  void updateThingData(ThingData thingData) {
	
		entityManager.getTransaction().begin();
		ThingData epr = entityManager.find(ThingData.class, thingData.getThingID());
		epr.setMeasurement(thingData.getMeasurement());
		epr.setTimestamp(thingData.getTimestamp());
		entityManager.getTransaction().commit();
	}


	public ThingData searchThingData(ThingData thingData) {
		return entityManager.find(ThingData.class,thingData.getThingID());
	}
	
	public ThingInformation searchThingInformation(ThingInformation thingInformation) {
		return entityManager.find(ThingInformation.class,thingInformation.getThingID());
	}

	public  void deleteThingData(ThingData thingData) {
		entityManager.getTransaction().begin();
		ThingData epr = entityManager.find(ThingData.class, thingData.getThingID());
		entityManager.remove(epr);
		entityManager.getTransaction().commit();
		
	}

	public  void saveThingData(ThingData thingData) {
	
		entityManager.getTransaction().begin();
		entityManager.persist(thingData);
		entityManager.getTransaction().commit();
		logger.info("Saved data");
		
	}

	public  void deleteThingInformation(ThingInformation thingInformation) {
		entityManager.getTransaction().begin();
		ThingInformation epr = entityManager.find(ThingInformation.class, thingInformation.getThingID());
		entityManager.remove(epr);
		entityManager.getTransaction().commit();
		
	}

	public  void saveThingInformation(ThingInformation thingInformation) {
		entityManager.getTransaction().begin();
		entityManager.persist(thingInformation);
		entityManager.getTransaction().commit();
		
	}

	

	public  void saveQoSMAssignmentInternal(QoSMAssignmentInternal assignment) {
		QoSMAssignmentInternal epr = entityManager.find(QoSMAssignmentInternal.class, 
				assignment.getId());
		if( epr != null){
			entityManager.getTransaction().begin();
			entityManager.merge(assignment);
			entityManager.getTransaction().commit();
		}
		else{
			entityManager.getTransaction().begin();
			entityManager.persist(assignment);
			entityManager.getTransaction().commit();
		}

	}

	public  void saveQoSMEquivalentThingServiceInternal(
			QoSMEquivalentThingServiceInternal equivalentThingService) {
		QoSMEquivalentThingServiceInternal epr = entityManager.find(QoSMEquivalentThingServiceInternal.class, 
				equivalentThingService.getId());
		if( epr != null){
			entityManager.getTransaction().begin();
			entityManager.merge(equivalentThingService);
			entityManager.getTransaction().commit();
		}
		else{
			entityManager.getTransaction().begin();
			entityManager.persist(equivalentThingService);
			entityManager.getTransaction().commit();
		}
		
	}

	public  void saveQoSMRequestInternal(QoSMRequestInternal request) {

		/*try{
			if(entityManager.find(QoSMRequestInternal.class, request.getId())!= null)
			{
				entityManager.getTransaction().begin();
				entityManager.merge(request);
				entityManager.getTransaction().commit();
			}
		}catch(java.lang.IllegalStateException e){
			logger.error("Exception Merge: " + e.getMessage());
			logger.error(request.toString());
		}
		try{
			if(entityManager.find(QoSMRequestInternal.class, request.getId())== null){
					entityManager.getTransaction().begin();
					entityManager.persist(request);
					entityManager.getTransaction().commit();
			}
		}catch(java.lang.IllegalStateException e){
			logger.error("Exception Persist: " + e.getMessage());
			logger.error(request.toString());
		}*/
		QoSMRequestInternal epr = entityManager.find(QoSMRequestInternal.class, request.getId());
		if( epr != null){
			entityManager.getTransaction().begin();
			entityManager.merge(request);
			entityManager.getTransaction().commit();
		}
		else{
			entityManager.getTransaction().begin();
			entityManager.persist(request);
			entityManager.getTransaction().commit();
		}
		
	}

	public  void saveQoSMThingInternal(QoSMThingInternal thing) {
		QoSMThingInternal epr = entityManager.find(QoSMThingInternal.class, thing.getDeviceId());
		if( epr != null){
			entityManager.getTransaction().begin();
			entityManager.merge(thing);
			entityManager.getTransaction().commit();
		}
		else{
			entityManager.getTransaction().begin();
			entityManager.persist(thing);
			entityManager.getTransaction().commit();
		}

	}

	public  void saveQoSMThingServiceInternal(QoSMThingServiceInternal thingService) {
		QoSMThingServiceInternal epr = entityManager.find(QoSMThingServiceInternal.class, thingService.getId());
		if( epr != null){
			entityManager.getTransaction().begin();
			entityManager.merge(thingService);
			entityManager.getTransaction().commit();
		}
		else{
			entityManager.getTransaction().begin();
			entityManager.persist(thingService);
			entityManager.getTransaction().commit();
		}
	}

	

	public List<QoSMThingInternal> searchQoSMThingInternal(String deviceId) {
		Query query = entityManager.createQuery("SELECT DISTINCT t FROM QoSMThingInternal t WHERE t.deviceId = :cust");
		query.setParameter("cust", deviceId);
		return (List<QoSMThingInternal>) query.getResultList();
	}

	public List<QoSMThingServiceInternal> searchQoSMThingServiceInternal(
			String thingServiceId) {
		Query query = entityManager.createQuery("SELECT DISTINCT ts FROM QoSMThingServiceInternal ts "
				+ "WHERE ts.id.thingServiceId =:custtsId");
		query.setParameter("custtsId", thingServiceId);
		return (List<QoSMThingServiceInternal>) query.getResultList();
	}

	public  void deleteQoSMEquivalentThingServiceInternal(
			QoSMEquivalentThingServiceInternal eqts) {
		QoSMEquivalentThingServiceInternal epr = entityManager.find(QoSMEquivalentThingServiceInternal.class, 
				eqts.getId());
		if(epr != null){
			entityManager.getTransaction().begin();
			entityManager.remove(epr);
			entityManager.getTransaction().commit();
		}
		
	}

	public List<QoSMThingServiceInternal> getAllQoSMThingServiceInternal() {
		Query query = entityManager.createQuery("FROM QoSMThingServiceInternal");
		return (List<QoSMThingServiceInternal>) query.getResultList();
	}

	public List<QoSMRequestInternal> getAllQoSMRequestInternal() {
		Query query = entityManager.createQuery("FROM QoSMRequestInternal");
		return (List<QoSMRequestInternal>) query.getResultList();

	}
	
	public List<QoSMThingStar> getAllQoSMThingStar(){
		Query query = entityManager.createQuery("FROM QoSMThingStar");
		return (List<QoSMThingStar>) query.getResultList();
	}
	
	public List<QoSMAssignmentStar> getAllQoSMAssignmentStar(){
		Query query = entityManager.createQuery("FROM QoSMAssignmentStar");
		return (List<QoSMAssignmentStar>) query.getResultList();
	}
	
	public List<QoSMRequestStar> getAllQoSMRequestStar(){
		Query query = entityManager.createQuery("FROM QoSMRequestStar");
		return (List<QoSMRequestStar>) query.getResultList();
	}
	
	public List<QoSMThingServiceStar> searchQoSMThingServiceStar(String thingServiceId){
		Query query = entityManager.createQuery("SELECT DISTINCT ts FROM QoSMThingServiceStar ts "
				+ "WHERE ts.thingServiceId = :custThingServiceId");
		query.setParameter("custThingServiceId", thingServiceId);
		return (List<QoSMThingServiceStar>) query.getResultList();
	}
	
	public List<QoSMThingServiceStar> getAllQoSMThingServiceStar(){
		Query query = entityManager.createQuery("FROM QoSMThingServiceStar");
		return (List<QoSMThingServiceStar>) query.getResultList();
	}
	
	public void deleteAllQoSMAssignmentStar(){
		entityManager.getTransaction().begin();
		entityManager.createQuery("DELETE FROM QoSMAssignmentStar").executeUpdate();
		entityManager.getTransaction().commit();
	}
	
	public void deleteAllQoSMRequestStar(){
		entityManager.getTransaction().begin();
		entityManager.createQuery("DELETE FROM QoSMRequestStar").executeUpdate();
		entityManager.getTransaction().commit();
	}
	
	public List<QoSMEquivalentThingServiceStar> getAllEquivalentQoSMThingServiceStar(){
		Query query = entityManager.createQuery("FROM QoSMEquivalentThingServiceStar");
		return (List<QoSMEquivalentThingServiceStar>) query.getResultList();
	}
	
	public void deleteAllQoSMEquivalentThingServiceStar(){
		entityManager.getTransaction().begin();
		entityManager.createQuery("DELETE FROM QoSMEquivalentThingServiceStar").executeUpdate();
		entityManager.getTransaction().commit();
	}
	
	public void saveQoSMThingStar(QoSMThingStar t){
		QoSMThingStar epr = entityManager.find(QoSMThingStar.class, t.getDeviceId());
		if( epr != null){
			entityManager.getTransaction().begin();
			entityManager.merge(t);
			entityManager.getTransaction().commit();
		}
		else{
			entityManager.getTransaction().begin();
			entityManager.persist(t);
			entityManager.getTransaction().commit();
		}
	}
	
	public void saveQoSMThingServiceStar(QoSMThingServiceStar ts){
		QoSMThingServiceStar epr = entityManager.find(QoSMThingServiceStar.class, ts.getDeviceId());
		if( epr != null){
			entityManager.getTransaction().begin();
			entityManager.merge(ts);
			entityManager.getTransaction().commit();
		}
		else{
			entityManager.getTransaction().begin();
			entityManager.persist(ts);
			entityManager.getTransaction().commit();
		}
	}

	public void saveQoSMEquivalentThingServiceStar(QoSMEquivalentThingServiceStar ets){
		QoSMEquivalentThingServiceStar epr = entityManager.find(QoSMEquivalentThingServiceStar.class, ets.getId());
		if( epr != null){
			entityManager.getTransaction().begin();
			entityManager.merge(ets);
			entityManager.getTransaction().commit();
		}
		else{
			entityManager.getTransaction().begin();
			entityManager.persist(ets);
			entityManager.getTransaction().commit();
		}
	}
	
	public void saveQoSMAssignmentStar(QoSMAssignmentStar a){
		QoSMAssignmentStar epr = entityManager.find(QoSMAssignmentStar.class, a.getId());
		if( epr != null){
			entityManager.getTransaction().begin();
			entityManager.merge(a);
			entityManager.getTransaction().commit();
		}
		else{
			entityManager.getTransaction().begin();
			entityManager.persist(a);
			entityManager.getTransaction().commit();
		}
	}
	
	public void saveQoSMAssuredRequestInternal(QoSMAssuredRequestInternal request){
		QoSMAssuredRequestInternal epr = entityManager.find(QoSMAssuredRequestInternal.class, request.getId());
		if( epr != null){
			entityManager.getTransaction().begin();
			entityManager.merge(request);
			entityManager.getTransaction().commit();
		}
		else{
			entityManager.getTransaction().begin();
			entityManager.persist(request);
			entityManager.getTransaction().commit();
		}
	}
	
	public List<QoSMAssuredRequestStar> getAllQoSMAssuredRequestStar(){
		Query query = entityManager.createQuery("FROM QoSMAssuredRequestStar");
		return (List<QoSMAssuredRequestStar>) query.getResultList();
	}
	
	public List<QoSMAssuredRequestInternal> getAllQoSMAssuredRequestInternal(){
		Query query = entityManager.createQuery("FROM QoSMAssuredRequestInternal");
		return (List<QoSMAssuredRequestInternal>) query.getResultList();
	}
	
	public List<QoSMThingStar> searchQoSMThingStar(String deviceId){
		Query query = entityManager.createQuery("SELECT DISTINCT t FROM QoSMThingStar t WHERE t.deviceId = :cust");
		query.setParameter("cust", deviceId);
		return (List<QoSMThingStar>) query.getResultList();
	}

	public List<QoSMEquivalentThingServiceInternal> searchQoSMEquivalentThingServiceInternal(String tsid){
		Query query = entityManager.createQuery("SELECT DISTINCT ets FROM QoSMEquivalentThingServiceInternal ets "
				+ "WHERE ets.id.thingServiceId = :cust");
		query.setParameter("cust", tsid);
		return (List<QoSMEquivalentThingServiceInternal>) query.getResultList();
	}
	
	public List<QoSMAssignmentInternal> searchQoSMAssignmentInternalTS(String tsid){
		Query query = entityManager.createQuery("SELECT DISTINCT a FROM QoSMAssignmentInternal a "
				+ "WHERE a.id.thingServiceId =:custThingServiceId");
		query.setParameter("custThingServiceId", tsid);
		return (List<QoSMAssignmentInternal>) query.getResultList();
	}
	
	public List<QoSMThingServiceInternal> searchQoSMThingServiceInternalT(String deviceId){
		Query query = entityManager.createQuery("SELECT DISTINCT ts FROM QoSMThingServiceInternal ts "
				+ "WHERE ts.deviceId = :cust");
		query.setParameter("cust", deviceId);
		return (List<QoSMThingServiceInternal>) query.getResultList();
	}
	
	public void deleteQoSMThingInternal(QoSMThingInternal t){
		entityManager.getTransaction().begin();
		QoSMThingInternal epr = entityManager.find(QoSMThingInternal.class, t.getDeviceId());
		entityManager.remove(epr);
		entityManager.getTransaction().commit();
		entityManager.flush();
		entityManager.clear();
		
	}
	
	public void deleteQoSMThingServiceInternal(QoSMThingServiceInternal ts){
		entityManager.getTransaction().begin();
		QoSMThingServiceInternal epr = entityManager.find(QoSMThingServiceInternal.class, ts.getId());
		entityManager.remove(epr);
		entityManager.getTransaction().commit();
		entityManager.flush();
		entityManager.clear();
	}
	
	public void deleteQoSMAssignmentInternal(QoSMAssignmentInternal a){
		entityManager.getTransaction().begin();
		QoSMAssignmentInternal epr = entityManager.find(QoSMAssignmentInternal.class, a.getId());
		entityManager.remove(epr);
		entityManager.getTransaction().commit();
		entityManager.flush();
		entityManager.clear();
	}
	
	public List<QoSMThingServiceStar> searchQoSMThingServiceStarT(String deviceId){
		Query query = entityManager.createQuery("SELECT DISTINCT ts FROM QoSMThingServiceStar ts "
				+ "WHERE ts.deviceId = :cust");
		query.setParameter("cust", deviceId);
		return (List<QoSMThingServiceStar>) query.getResultList();
	}
	
	public void deleteQoSMThingStar(QoSMThingStar t){
		entityManager.getTransaction().begin();
		QoSMThingStar epr = entityManager.find(QoSMThingStar.class, t.getDeviceId());
		entityManager.remove(epr);
		entityManager.getTransaction().commit();
		entityManager.flush();
		entityManager.clear();
	}
	
	public void deleteQoSMThingServiceStar(QoSMThingServiceStar ts){
		entityManager.getTransaction().begin();
		QoSMThingServiceStar epr = entityManager.find(QoSMThingServiceStar.class, ts.getId());
		entityManager.remove(epr);
		entityManager.getTransaction().commit();
		entityManager.flush();
		entityManager.clear();
		
	}
	
	public List<QoSMEquivalentThingServiceStar> searchQoSMEquivalentThingServiceStar(String tsid){
		Query query = entityManager.createQuery("SELECT DISTINCT ets FROM QoSMEquivalentThingServiceStar ets "
				+ "WHERE ets.id.thingServiceId = :cust");
		query.setParameter("cust", tsid);
		return (List<QoSMEquivalentThingServiceStar>) query.getResultList();
	}
	
	public void deleteQoSMEquivalentThingServiceStar(QoSMEquivalentThingServiceStar eq){
		entityManager.getTransaction().begin();
		QoSMEquivalentThingServiceStar epr = entityManager.find(QoSMEquivalentThingServiceStar.class, eq.getId());
		entityManager.remove(epr);
		entityManager.getTransaction().commit();
		entityManager.flush();
		entityManager.clear();
	}
	
	public List<QoSMAssignmentStar> searchQoSMAssignmentStarTS(String tsid){
		Query query = entityManager.createQuery("SELECT DISTINCT a FROM QoSMAssignmentStar a "
				+ "WHERE a.id.thingServiceId =:custThingServiceId");
		query.setParameter("custThingServiceId", tsid);
		return (List<QoSMAssignmentStar>) query.getResultList();
	}
	
	public void deleteQoSMAssignmentStar(QoSMAssignmentStar a){
		entityManager.getTransaction().begin();
		QoSMAssignmentStar epr = entityManager.find(QoSMAssignmentStar.class, a.getId());
		entityManager.remove(epr);
		entityManager.getTransaction().commit();
		entityManager.flush();
		entityManager.clear();
	}

	public void deleteQoSMEquivalentThingServiceStar(String serviceId, int reqId) {
		entityManager.getTransaction().begin();
		Query query =  entityManager.createQuery("DELETE FROM QoSMEquivalentThingServiceStar ets " +
				"WHERE ets.id.serviceId =:custServiceId AND ets.id.requestId =:custRequestId");
		query.setParameter("custServiceId", serviceId);
		query.setParameter("custRequestId", reqId);
		query.executeUpdate();
		entityManager.getTransaction().commit();
		
	}
	
	public void deleteQoSMRequestInternal(String serviceId){
		entityManager.getTransaction().begin();
		Query query =  entityManager.createQuery("DELETE FROM QoSMRequestInternal r " +
				"WHERE r.id.serviceId =:custServiceId");
		query.setParameter("custServiceId", serviceId);
		query.executeUpdate();
		entityManager.getTransaction().commit();
	}
	
	public void deleteQoSMAssuredRequestInternal(String serviceId){
		entityManager.getTransaction().begin();
		Query query =  entityManager.createQuery("DELETE FROM QoSMAssuredRequestInternal r " +
				"WHERE r.id.serviceId =:custServiceId");
		query.setParameter("custServiceId", serviceId);
		query.executeUpdate();
		entityManager.getTransaction().commit();
	}
	
	public void deleteAllQoSMThingServiceStar(){
		entityManager.getTransaction().begin();
		Query query =  entityManager.createQuery("DELETE FROM QoSMThingServiceStar");
		query.executeUpdate();
		entityManager.getTransaction().commit();
		entityManager.flush();
		entityManager.clear();
	}

	public void deleteAllQoSMThingStar(){
		entityManager.getTransaction().begin();
		Query query =  entityManager.createQuery("DELETE FROM QoSMThingStar");
		query.executeUpdate();
		entityManager.getTransaction().commit();
	}

	public void saveQoSMRequestStar(QoSMRequestStar r){
		QoSMRequestStar epr = entityManager.find(QoSMRequestStar.class, r.getId());
		if( epr != null){
			entityManager.getTransaction().begin();
			entityManager.merge(r);
			entityManager.getTransaction().commit();
		}
		else{
			entityManager.getTransaction().begin();
			entityManager.persist(r);
			entityManager.getTransaction().commit();
		}
	}
	
	public void deleteAllQoSMAssuredRequestStar(){
		entityManager.getTransaction().begin();
		Query query =  entityManager.createQuery("DELETE FROM QoSMAssuredRequestStar");
		query.executeUpdate();
		entityManager.getTransaction().commit();
	}

	public void saveQoSMAssuredRequestStar(QoSMAssuredRequestStar r){
		QoSMAssuredRequestStar epr = entityManager.find(QoSMAssuredRequestStar.class, r.getId());
		if( epr != null){
			entityManager.getTransaction().begin();
			entityManager.merge(r);
			entityManager.getTransaction().commit();
		}
		else
		{
			entityManager.getTransaction().begin();
			entityManager.persist(r);
			entityManager.getTransaction().commit();
		}
	}
	
	public List<QoSMEquivalentThingServiceInternal> getAllEquivalentQoSMThingServiceInternal(){
		Query query = entityManager.createQuery("FROM QoSMEquivalentThingServiceInternal");
		return (List<QoSMEquivalentThingServiceInternal>) query.getResultList();
	}

	public List<QoSMAssignmentInternal> getAllQoSMAssignmentInternal(){
		Query query = entityManager.createQuery("FROM QoSMAssignmentInternal");
		return (List<QoSMAssignmentInternal>) query.getResultList();
	}

	public List<QoSMThingInternal> getAllQoSMThingInternal(){
		Query query = entityManager.createQuery("FROM QoSMThingInternal");
		return (List<QoSMThingInternal>) query.getResultList();
	}
	/* Methods for use by the Rest Services of the Things Simulator*/
	public List<SimulatedThing> listAllSimulatedThings() {
		List<SimulatedThing> result = entityManager.createQuery("from SimulatedThing").getResultList();
		return result;
	}
	
	public  void saveSimulatedThing(SimulatedThing thing) {
		if(thing != null){
			SimulatedThing existing = entityManager.find(SimulatedThing.class, thing.getId());
			if (existing != null){
				logger.info("Simulated Thing found with id:"+existing.getId());
				entityManager.getTransaction().begin();
				entityManager.merge(thing);
				entityManager.getTransaction().commit();
			} else {
				logger.info("Simulated Thing Not found with Device id:"+thing.getDeviceID());
				entityManager.getTransaction().begin();
				entityManager.persist(thing);
				entityManager.getTransaction().commit();	
			}			
		}		
	}
	
	public  void deleteSimulatedThing(Integer thingDBId) {
		SimulatedThing thingToDelete = entityManager.find(SimulatedThing.class, thingDBId);
		if(thingToDelete != null){
			entityManager.getTransaction().begin();
			entityManager.remove(thingToDelete);
			entityManager.getTransaction().commit();
		}		
	}

	public boolean isEnabledbus() {
		return enabledbus;
	}

	public void setEnabledbus(boolean enabledbus) {
		this.enabledbus = enabledbus;
	}
	
	public BundleContext getContext() {
		return context;
	}

	public void setContext(BundleContext context) {
		this.context = context;
	}

	
	
	public String getGwid() {
		return gwid;
	}

	public void setGwid(String gwid) {
		this.gwid = gwid;
	}

	private void busMessage(String message){
		logger.debug("Checking queue");
		if (!enabledbus)return;
		logger.debug("Sending to queue");
		ServiceReference serviceReference = context.getServiceReference(Publisher.class.getName());
		logger.debug("Sending to queue");
		if (serviceReference==null)return;
		
		Publisher service = (Publisher) context.getService(serviceReference); 
		logger.debug("Sending");
		Message messageFormat = new Message();
		messageFormat.setLayer(Layer.TAAS);
		messageFormat.setLevel("INFO");
		messageFormat.setOrigin("BD Manager");
		messageFormat.setDescritpion(message);


		
		service.publish("taas.database",mb.getJsonEquivalent(messageFormat));
		logger.debug("Sent");
		
		
	}
	/* END OF Methods for use by the Rest Services of the Things Simulator*/
}
