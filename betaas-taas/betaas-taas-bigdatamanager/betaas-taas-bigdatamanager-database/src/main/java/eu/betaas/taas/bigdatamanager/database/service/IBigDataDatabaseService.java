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
package eu.betaas.taas.bigdatamanager.database.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;

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
import eu.betaas.taas.bigdatamanager.database.hibernate.data.ExtServService;

public interface IBigDataDatabaseService {
	
	/**
	 * 
	 * Methods for setting service database connection
	 */
	
	public void setup() throws ClassNotFoundException, SQLException;
		
	public void setJdbcurl(String jdbcurl) ;

	public void setUser(String user);

	public void setPwd(String pwd);	


	/**
	 * 
	 * Methods for getting connection or object from database
	 * the entity manager should not be used by external bundles
	 */
	
	public EntityManager getEntityManager();
	
	public void setEntityManager(EntityManager entityManager);
	
	public Connection getConnection() throws SQLException;
	
	public void close();

	
	/**
	 * 
	 * Methods for manipulating data: update
	 */
	
	public void updateAgreementEprContainer(AgreementEprContainer agreementEprContainer);
	
	public void updateApplicationRegistry(ApplicationRegistry applicationRegistry);
	
	public void updateAppService(AppService appService);
	
	public void updatePersistentAgreementContainer(PersistentAgreementContainerDatabase persistentAgreementContainer);
	
	public void updateTrustManagerService(TrustManagerService trustManagerService);
	
	public void updateExtServiceRegistry(ExtServiceRegistry extServiceRegistry);
	
	public void updateExtServService(ExtServService extServService);
	
	
	/**
	 * 
	 * Methods for manipulating data: search
	 */	

	public AgreementEprContainer searchAgreementEprContainer(AgreementEprContainer agreementEprContainer);
	
	public ApplicationRegistry searchApplicationRegistry(ApplicationRegistry applicationRegistry);
	
	public AppService searchAppService(AppService appService);
	
	public PersistentAgreementContainerDatabase searchPersistentAgreementContainer(PersistentAgreementContainerDatabase persistentAgreementContainer);
	
	public TrustManagerService searchTrustManagerService(TrustManagerService trustManagerService);	
	
	public ThingData searchThingData(ThingData thingData);	
	
	public ThingInformation searchThingInformation(ThingInformation thingInformation);
	
	public ExtServiceRegistry searchExtServiceRegistry(ExtServiceRegistry extServiceRegistry);
	
	public ExtServService searchExtServService(ExtServService extServService);

	
	/**
	 * 
	 * Methods for manipulating data: delete
	 */	
	

	public void deleteAgreementEprContainer(AgreementEprContainer agreementEprContainer);
	
	public void deleteApplicationRegistry(ApplicationRegistry applicationRegistry);
	
	public void deleteAppService(AppService appService);
	
	public void deletePersistentAgreementContainer(PersistentAgreementContainerDatabase persistentAgreementContainer);
	
	public void deleteTrustManagerService(TrustManagerService trustManagerService);
	
	public void deleteThingData(ThingData thingData);
	
	public void deleteThingInformation(ThingInformation thingInformation);
	
	public void deleteExtServiceRegistry(ExtServiceRegistry extServiceRegistry);
	
	public void deleteExtServService(ExtServService extServService);
	

	/**
	 * 
	 * Methods for manipulating data: save
	 */	
	

	public void saveAgreementEprContainer(AgreementEprContainer agreementEprContainer);
	
	public void saveApplicationRegistry(ApplicationRegistry applicationRegistry);
	
	public void saveAppService(AppService appService);
	
	public void savePersistentAgreementContainer(PersistentAgreementContainerDatabase persistentAgreementContainer);
	
	public void saveTrustManagerService(TrustManagerService trustManagerService);
	
	public void saveThingInformation(ThingInformation thingInformation);
	
	public void saveThingData(ThingData thingData);
	
	public void saveExtServiceRegistry(ExtServiceRegistry extServiceRegistry);
	
	public void saveExtServService(ExtServService extServService);

	// QoSM functions
	//// 

	public List<QoSMThingServiceInternal> getAllQoSMThingServiceInternal();

	public List<QoSMRequestInternal> getAllQoSMRequestInternal();

	public List<QoSMThingStar> getAllQoSMThingStar();

	public List<QoSMAssignmentStar> getAllQoSMAssignmentStar();

	public List<QoSMRequestStar> getAllQoSMRequestStar();
	
	public List<QoSMThingServiceStar> getAllQoSMThingServiceStar();
	
	public List<QoSMEquivalentThingServiceStar> getAllEquivalentQoSMThingServiceStar();
	
	public List<QoSMAssuredRequestStar> getAllQoSMAssuredRequestStar();
	
	public List<QoSMAssuredRequestInternal> getAllQoSMAssuredRequestInternal();
	
	public List<QoSMEquivalentThingServiceInternal> getAllEquivalentQoSMThingServiceInternal();

	public List<QoSMAssignmentInternal> getAllQoSMAssignmentInternal();

	public List<QoSMThingInternal> getAllQoSMThingInternal();
	
	/////
	
	public void deleteAllQoSMAssignmentStar();

	public void deleteAllQoSMRequestStar();
	
	public void deleteAllQoSMEquivalentThingServiceStar();
	
	public void deleteAllQoSMThingServiceStar();

	public void deleteAllQoSMThingStar();
	
	public void deleteAllQoSMAssuredRequestStar();
	
	/////
	
	public void deleteQoSMThingInternal(QoSMThingInternal t);

	public void deleteQoSMThingServiceInternal(QoSMThingServiceInternal ts);

	public void deleteQoSMAssignmentInternal(QoSMAssignmentInternal a);
	
	public void deleteQoSMThingStar(QoSMThingStar t);

	public void deleteQoSMThingServiceStar(QoSMThingServiceStar ts);
	
	public void deleteQoSMEquivalentThingServiceStar(QoSMEquivalentThingServiceStar eq);
	
	public void deleteQoSMAssignmentStar(QoSMAssignmentStar a);
	
	public void deleteQoSMEquivalentThingServiceStar(String serviceId, int reqId);
	
	public void deleteQoSMEquivalentThingServiceInternal(
			QoSMEquivalentThingServiceInternal eqts);
	
	public void deleteQoSMRequestInternal(String serviceId);
	
	public void deleteQoSMAssuredRequestInternal(String serviceId);
	
	/////
	
	public void saveQoSMThingStar(QoSMThingStar t);
	
	public void saveQoSMThingServiceStar(QoSMThingServiceStar ts);

	public void saveQoSMEquivalentThingServiceStar(QoSMEquivalentThingServiceStar ets);

	public void saveQoSMAssignmentStar(QoSMAssignmentStar a);
	
	public void saveQoSMAssuredRequestInternal(QoSMAssuredRequestInternal request);
	
	public void saveQoSMRequestStar(QoSMRequestStar r);

	public void saveQoSMAssuredRequestStar(QoSMAssuredRequestStar r);
	
	public void saveQoSMAssignmentInternal(QoSMAssignmentInternal a);
	
	public void saveQoSMEquivalentThingServiceInternal(QoSMEquivalentThingServiceInternal e);
	
	public void saveQoSMRequestInternal(QoSMRequestInternal request);
	
	public void saveQoSMThingInternal(QoSMThingInternal t);
	
	public void saveQoSMThingServiceInternal(QoSMThingServiceInternal ts);
	
	/////

	public List<QoSMThingServiceStar> searchQoSMThingServiceStar(String thingServiceId);

	public List<QoSMThingStar> searchQoSMThingStar(String deviceId);

	public List<QoSMEquivalentThingServiceInternal> searchQoSMEquivalentThingServiceInternal(String tsid);

	public List<QoSMAssignmentInternal> searchQoSMAssignmentInternalTS(String tsid);

	public List<QoSMThingServiceInternal> searchQoSMThingServiceInternalT(String deviceId);

	public List<QoSMThingServiceStar> searchQoSMThingServiceStarT(String deviceId);

	public List<QoSMEquivalentThingServiceStar> searchQoSMEquivalentThingServiceStar(String tsid);

	public List<QoSMAssignmentStar> searchQoSMAssignmentStarTS(String tsid);
	
	public List<QoSMThingInternal> searchQoSMThingInternal(String deviceId);

	public List<QoSMThingServiceInternal> searchQoSMThingServiceInternal(
			String thingServiceId);

	/////
	
	/* Methods for use by the Rest Services of the Things Simulator*/
	public List<SimulatedThing> listAllSimulatedThings();
	
	public  void saveSimulatedThing(SimulatedThing thing);
	
	public  void deleteSimulatedThing(Integer thingDBId);
	
	/* END OF Methods for use by the Rest Services of the Things Simulator*/

}
