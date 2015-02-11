/*
 *
Copyright 2014-2015 Department of Information Engineering, University of Pisa

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

// BETaaS - Building the Environment for the Things as a Service
//
// Component: TaaS QoS Manager
// Responsible: Carlo Vallati & Giacomo Tanganelli

package eu.betaas.taas.qosmanager.api.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

import eu.betaas.taas.bigdatamanager.database.hibernate.data.QoSMAssignmentInternal;
import eu.betaas.taas.bigdatamanager.database.hibernate.data.QoSMAssignmentStar;
import eu.betaas.taas.bigdatamanager.database.hibernate.data.QoSMAssuredRequestStar;
import eu.betaas.taas.bigdatamanager.database.hibernate.data.QoSMEquivalentThingServiceInternal;
import eu.betaas.taas.bigdatamanager.database.hibernate.data.QoSMEquivalentThingServiceStar;
import eu.betaas.taas.bigdatamanager.database.hibernate.data.QoSMRequestStar;
import eu.betaas.taas.bigdatamanager.database.hibernate.data.QoSMThingInternal;
import eu.betaas.taas.bigdatamanager.database.hibernate.data.QoSMThingServiceStar;
import eu.betaas.taas.bigdatamanager.database.hibernate.data.QoSMThingStar;
import eu.betaas.taas.bigdatamanager.database.service.IBigDataDatabaseService;
import eu.betaas.taas.qosmanager.api.QoSManagerExternalIF;
import eu.betaas.taas.qosmanager.api.QoSManagerInternalIF;
import eu.betaas.taas.qosmanager.api.QoSManagerNotificationIF;
import eu.betaas.taas.qosmanager.api.QoSRankList;
import eu.betaas.taas.qosmanager.api.QoSRankResults;
import eu.betaas.taas.qosmanager.api.QoSrequirements;
import eu.betaas.taas.qosmanager.api.QoSspec;
import eu.betaas.taas.qosmanager.api.WrongArgumentException;
import eu.betaas.taas.qosmanager.core.QoSManager;
import eu.betaas.taas.qosmanager.core.QoSManagerActivator;

public class ExternalAPIImpl implements QoSManagerExternalIF{

	private static Logger LOG = Logger.getLogger("betaas.taas");

	private static Logger LOGTest = Logger.getLogger("betaas.testplan");

	private QoSManager qosM;

	private String gatewayId;

	ExecutorService heuristicScheduler;

	ExecutorService WhitheBoardScheduler;

	private BundleContext context;

	Map<String, QoSManagerNotificationIF> MapGw;

	private QoSRankList allocation_schema;

	
	private Map<String, QoSMEquivalentThingServiceStar> m_equivalents;
	private Map<String, QoSMAssignmentStar> m_assignments;
	private Map<String, QoSMRequestStar> m_requests;
	private Map<String, QoSMAssuredRequestStar> m_assuredrequests;
	private Map<String, QoSMThingServiceStar> m_thingservices;
	private Map<String, QoSMThingStar> m_things;

	class Whiteboard extends Thread{
		private ServiceTracker tracker;
		public Whiteboard(BundleContext context){
			tracker = new ServiceTracker( context, QoSManagerNotificationIF.class.getName(), null); 
			tracker.open(); 
		}

		public synchronized void run() { 
			int n = 0; 
			while(true){
				Object [] providers = tracker.getServices(); 
				if ( providers != null && providers.length > 0 ) { 
					if ( n >= providers.length ) 
						n = 0; 
					QoSManagerNotificationIF gwQos = (QoSManagerNotificationIF) providers[n++]; 
					String gwId = gwQos.getGatewayId();
					MapGw.put(gwId, gwQos);
				} 
				try { wait( 5000 ); } catch( InterruptedException e ) {} 
			}

		}
	}

	public ExternalAPIImpl(QoSManagerActivator qoSManagerActivator,
			QoSManager qosM, String gatewayId, BundleContext context) {
		setQosM(qosM);
		this.gatewayId = gatewayId;
		this.context = context;
		heuristicScheduler = Executors.newSingleThreadExecutor();
		WhitheBoardScheduler = Executors.newSingleThreadExecutor();
		MapGw = new HashMap<String, QoSManagerNotificationIF>();
		WhitheBoardScheduler.execute(new Whiteboard(context));
		allocation_schema = null;
		
		m_equivalents = new HashMap<String, QoSMEquivalentThingServiceStar>();

		m_assignments = new HashMap<String, QoSMAssignmentStar>();

		m_requests = new HashMap<String, QoSMRequestStar>();

		m_thingservices = new HashMap<String, QoSMThingServiceStar>();

		m_things = new HashMap<String, QoSMThingStar>();
		
		m_assuredrequests = new HashMap<String, QoSMAssuredRequestStar>();
		
		read_db(m_equivalents, m_assignments, m_requests, m_assuredrequests, m_thingservices, m_things);
	}
	
	private synchronized void read_db(
			Map<String, QoSMEquivalentThingServiceStar> m_equivalents,
			Map<String, QoSMAssignmentStar> m_assignments,
			Map<String, QoSMRequestStar> m_requests,
			Map<String, QoSMAssuredRequestStar> m_assuredrequests,
			Map<String, QoSMThingServiceStar> m_thingservices,
			Map<String, QoSMThingStar> m_things) {

		IBigDataDatabaseService service = qosM.getService();
		List<QoSMEquivalentThingServiceStar> equivalents = service.getAllEquivalentQoSMThingServiceStar();

		List<QoSMAssignmentStar> assignments = service.getAllQoSMAssignmentStar();

		List<QoSMRequestStar> requests = service.getAllQoSMRequestStar();

		List<QoSMAssuredRequestStar> assuredrequests = service.getAllQoSMAssuredRequestStar();

		List<QoSMThingServiceStar> thingservices = service.getAllQoSMThingServiceStar();

		List<QoSMThingStar> things = service.getAllQoSMThingStar();


		for(QoSMEquivalentThingServiceStar eq : equivalents){
			String key = eq.getId().keyString();
			m_equivalents.put(key, eq);
		}

		for(QoSMAssignmentStar ass : assignments){
			String key = ass.getId().keyString();
			m_assignments.put(key, ass);
		}

		for(QoSMRequestStar req : requests){
			String key = req.getId().keyString();
			m_requests.put(key, req);
		}

		for(QoSMAssuredRequestStar req : assuredrequests){
			String key = req.getId().keyString();
			m_assuredrequests.put(key, req);
		}

		for(QoSMThingServiceStar ts : thingservices){
			String key = ts.getId();
			m_thingservices.put(key, ts);
		}
		for(QoSMThingStar t : things){
			String key = t.getDeviceId();
			m_things.put(key, t);
		}

	}

		private synchronized void update_db() {
		
		IBigDataDatabaseService service = qosM.getService();
		service.deleteAllQoSMAssignmentStar();
		service.deleteAllQoSMEquivalentThingServiceStar();
		service.deleteAllQoSMRequestStar();
//			service.deleteAllQoSMThingServiceStar();
//			service.deleteAllQoSMThingStar();
		service.deleteAllQoSMAssuredRequestStar();

		for(QoSMAssignmentStar a : m_assignments.values())
			service.saveQoSMAssignmentStar(a);
		for(QoSMEquivalentThingServiceStar e : m_equivalents.values())
			service.saveQoSMEquivalentThingServiceStar(e);
		for(QoSMRequestStar r : m_requests.values())
			service.saveQoSMRequestStar(r);
		for(QoSMAssuredRequestStar r : m_assuredrequests.values())
			service.saveQoSMAssuredRequestStar(r);
//			for(QoSMThingServiceStar ts : allocation_schema.getThingServicesMap().values())
//				service.saveQoSMThingServiceStar(ts);
		for(QoSMThingStar t : m_things.values())
			service.saveQoSMThingStar(t);
		
	}

	public synchronized QoSRankList createAgreement(String serviceId,
			ArrayList<ArrayList<String>> rank, QoSrequirements requirement,
			QoSManagerInternalIF caller, boolean all) throws WrongArgumentException {

		LOG.info("QoSM* - Create new Agreement");
		if(rank== null || rank.isEmpty())
		{
			LOG.warn("Empty rank!");
		}
		try {
			Future<QoSRankList> future = 
				heuristicScheduler.submit(new Heuristic(m_equivalents, m_assignments, 
						m_requests, m_assuredrequests, m_thingservices, m_things, this.MapGw, 
						serviceId, rank, requirement, caller.getGatewayId(), all, this.context));
		
			allocation_schema = future.get();
			
			if(allocation_schema == null){
				allocation_schema = new QoSRankList(false);
				return allocation_schema;
			}
			else{
				update_data(allocation_schema);
			}
			QoSRankList assured_allocation = null;
			if(!m_assuredrequests.isEmpty())
			{
				future = 
					heuristicScheduler.submit(new HeuristicAssured(m_equivalents, m_assignments, 
							m_requests, m_assuredrequests, m_thingservices, m_things, MapGw, null, null, null, null,
							all, this.context));
				assured_allocation = future.get();
				if(assured_allocation == null){

					return new QoSRankList(false);
				}
				
				allocation_schema.setAssignmentsMap(assured_allocation.getAssignmentsMap());
				allocation_schema.setEquivalentsMap(assured_allocation.getEquivalentsMap());
				allocation_schema.setRequestsMap(assured_allocation.getRequestsMap());
				allocation_schema.setThingServicesMap(assured_allocation.getThingServicesMap());
				allocation_schema.setAssuredRequestsMap(assured_allocation.getAssuredRequestsMap());
				allocation_schema.setThingsMap(assured_allocation.getThingsMap());
				update_data(allocation_schema);
				mergeSchemas(assured_allocation);

			}

			sendNotification();

			return allocation_schema;

		} catch (InterruptedException e) {

			e.printStackTrace();
		} catch (ExecutionException e) {

			e.printStackTrace();
		}
		
		return new QoSRankList();
	}

	private synchronized void update_data(QoSRankList allocation_schema)
	{
		m_equivalents = allocation_schema.getEquivalentsMap();
		m_assignments = allocation_schema.getAssignmentsMap();
		m_requests = allocation_schema.getRequestsMap();
		m_assuredrequests = allocation_schema.getAssuredRequestsMap();
		m_thingservices = allocation_schema.getThingServicesMap();
		m_things = allocation_schema.getThingsMap();
	}

	private void sendNotification() {
		try{
			for(String gatewayId : allocation_schema.getNotifyMap().keySet()){
				try{
					QoSManagerNotificationIF gwQos = MapGw.get(gatewayId);
					gwQos.putQoSRank(allocation_schema.getNotifyMap().get(gatewayId));
				}catch(NullPointerException e){
					LOG.error("QoSManagerInternalIF not found" + e.getMessage());
					LOG.debug("GatewayID: " + gatewayId);
					LOG.debug("MapGw: " + allocation_schema.getMapGw());
					LOG.debug("notifyMap: " + allocation_schema.getNotifyMap());
				}
			}
		} catch(NullPointerException e){
			LOG.error("Notification is unfeasible.");
		}
	}

	private void mergeSchemas(QoSRankList assured_allocation) {
		try{
			if(allocation_schema != null){
				//merge allocation schemas
				for(String gatewayId : allocation_schema.getNotifyMap().keySet()){
					QoSRankResults realtime = allocation_schema.getNotifyMap().get(gatewayId);
					if(assured_allocation.getNotifyMap().containsKey(gatewayId)){
						QoSRankResults assured = assured_allocation.getNotifyMap().get(gatewayId);

						Set<QoSMAssignmentInternal> ass_realtime = realtime.getAssignments();
						Set<QoSMAssignmentInternal> ass_assured = assured.getAssignments();
						for(QoSMAssignmentInternal ass : ass_assured){
							ass_realtime.add(ass);
						}

						Set<QoSMEquivalentThingServiceInternal> eq_realtime = realtime.getEquivalents();
						Set<QoSMEquivalentThingServiceInternal> eq_assured = assured.getEquivalents();
						for(QoSMEquivalentThingServiceInternal eq : eq_assured){
							eq_realtime.add(eq);
						}

						Set<QoSMThingInternal> t_realtime = realtime.getThings();
						Set<QoSMThingInternal> t_assured = assured.getThings();
						for(QoSMThingInternal t : t_assured)
							t_realtime.add(t);
					}
				}
			}
		}catch(NullPointerException e){
			LOG.warn("Merge is unfeasible. No real-time requests.");
		}
		try{
			if(assured_allocation != null){
				if(allocation_schema == null){
					allocation_schema = new QoSRankList(true);
				}
				for(String gatewayId : assured_allocation.getNotifyMap().keySet()){
					QoSRankResults assured = assured_allocation.getNotifyMap().get(gatewayId);
					if(allocation_schema.getNotifyMap().containsKey(gatewayId)){
						QoSRankResults realtime = allocation_schema.getNotifyMap().get(gatewayId);
						Set<QoSMAssignmentInternal> ass_realtime = realtime.getAssignments();
						Set<QoSMAssignmentInternal> ass_assured = assured.getAssignments();
						for(QoSMAssignmentInternal ass : ass_assured){
							ass_realtime.add(ass);
						}

						Set<QoSMEquivalentThingServiceInternal> eq_realtime = realtime.getEquivalents();
						Set<QoSMEquivalentThingServiceInternal> eq_assured = assured.getEquivalents();
						for(QoSMEquivalentThingServiceInternal eq : eq_assured){
							eq_realtime.add(eq);
						}

						Set<QoSMThingInternal> t_realtime = realtime.getThings();
						Set<QoSMThingInternal> t_assured = assured.getThings();
						for(QoSMThingInternal t : t_assured)
							t_realtime.add(t);
					}
					else{
						allocation_schema.getNotifyMap().put(gatewayId, assured);
					}
				}
			}
		}
		catch(NullPointerException e){
			LOG.warn("Merge is unfeasible. No assured requests.");
		}

	}

	

	public synchronized QoSRankList createAgreementAssured(String serviceId,
			ArrayList<ArrayList<String>> rank, QoSrequirements requirements,
			QoSManagerInternalIF caller, boolean all) throws WrongArgumentException {

		LOG.info("QoSM* - Create new Agreement");
		if(rank == null || rank.isEmpty())
		{
			LOG.error("Empty rank!");
			throw new WrongArgumentException("Empty Rank");
		}
		try {

			Future<QoSRankList> future = 
				heuristicScheduler.submit(new HeuristicAssured(m_equivalents, m_assignments, 
						m_requests, m_assuredrequests, m_thingservices, m_things, MapGw, serviceId, rank, requirements, 
						caller.getGatewayId(), all, this.context));
		
			QoSRankList assured_allocation = future.get();
			if(assured_allocation == null){

				return new QoSRankList(false);
			}
				
			if(allocation_schema == null)
				allocation_schema = new QoSRankList(true);
			allocation_schema.setAssignmentsMap(assured_allocation.getAssignmentsMap());
			allocation_schema.setEquivalentsMap(assured_allocation.getEquivalentsMap());
			allocation_schema.setRequestsMap(assured_allocation.getRequestsMap());
			allocation_schema.setThingServicesMap(assured_allocation.getThingServicesMap());
			allocation_schema.setAssuredRequestsMap(assured_allocation.getAssuredRequestsMap());
			allocation_schema.setThingsMap(assured_allocation.getThingsMap());
			mergeSchemas(assured_allocation);
			update_data(allocation_schema);

			sendNotification();
			return assured_allocation;
		} catch (InterruptedException e) {

			e.printStackTrace();
		} catch (ExecutionException e) {

			e.printStackTrace();
		}
		return new QoSRankList();
	}

	public synchronized boolean writeThingsServicesQoS(String deviceId, double battery,
			HashMap<String, QoSspec> thingServices, String gatewayId) {
		LOG.info("ExternalAPIImpl - WriteThingsServicesQoS");
		LOG.debug("DeviceID:" + deviceId);
		LOG.debug("GatewayID:" + gatewayId);
		LOG.debug("thingservices:" + thingServices.keySet());
		//IBigDataDatabaseService service = qosM.getService();
		QoSMThingStar t = new QoSMThingStar();
		t.setBatteryLevel(battery);
		t.setDeviceId(deviceId);
		t.setCapacityUsed(0.0);
		t.setNumass(0);
		t.setTUsed(0.0);
		t.setGatewayId(gatewayId);

		for(Entry<String, QoSspec> entry : thingServices.entrySet()) {
			String thingServiceId = entry.getKey();
			QoSspec value = entry.getValue();
			QoSMThingServiceStar ts = new QoSMThingServiceStar(t.getDeviceId(),thingServiceId, 
					value.getResponseTime(), value.getBatteryCost(), value.getComputationalCost());
			m_thingservices.put(ts.getThingServiceId(),ts);
			//service.saveQoSMThingServiceStar(ts);		
			
		}
		m_things.put(t.getDeviceId(), t);
		//service.saveQoSMThingStar(t);
		return true;

	}

	public synchronized boolean modifyThingsServicesQoS(ArrayList<String> thingServices, QoSManagerInternalIF caller) {
		//IBigDataDatabaseService service = qosM.getService();
		LOG.info("ExternalAPIImpl - modifyThingsServicesQoS");
		LOG.debug("thingservices: " + thingServices);
		for(String tsid : thingServices){
			try{
				
				QoSMThingServiceStar ts = m_thingservices.get(tsid);
				List<QoSMThingServiceStar> tslst = getTSs(ts.getDeviceId());
				if(tslst.size() == 1){
					//remove thing
					m_things.remove(ts.getDeviceId());
				}
				m_thingservices.remove(tsid);
				List<QoSMEquivalentThingServiceStar> eqlst = getEQTSs(tsid);
				for(QoSMEquivalentThingServiceStar eq : eqlst){
					m_equivalents.remove(eq.getId().keyString());
				}
				List<QoSMAssignmentStar> alst = getAss(tsid);
				boolean check = false;
				for(QoSMAssignmentStar a : alst){
					//new assignment schema required
					m_assignments.remove(a.getId().keyString());
				}
				if(check)
					this.assignment(false, caller);
			}catch(IndexOutOfBoundsException e){
				LOG.warn("Thing service "+ tsid + " already deleted.");
			}

		}
		return true;
	}

	private List<QoSMAssignmentStar> getAss(String tsid) {
		List<QoSMAssignmentStar> lst = new ArrayList<QoSMAssignmentStar>();
		for(QoSMAssignmentStar a : m_assignments.values())
		{
			if(a.getId().getThingServiceId().equals(tsid))
			{
				lst.add(a);
			}
		}
		return lst;
	}

	private List<QoSMEquivalentThingServiceStar> getEQTSs(String tsid) {
		List<QoSMEquivalentThingServiceStar> lst = new ArrayList<QoSMEquivalentThingServiceStar>();
		for(QoSMEquivalentThingServiceStar eq : m_equivalents.values())
		{
			if(eq.getId().getThingServiceId().equals(tsid))
			{
				lst.add(eq);
			}
		}
		return lst;
	}

	private List<QoSMThingServiceStar> getTSs(String deviceId) {
		List<QoSMThingServiceStar> lst = new ArrayList<QoSMThingServiceStar>();
		for(QoSMThingServiceStar ts : m_thingservices.values())
		{
			if(ts.getDeviceId().equals(deviceId))
			{
				lst.add(ts);
			}
		}
		return lst;
	}

	public void deleteAlreadyCommittedServices(ArrayList<String> serviceList) {
		// TODO Auto-generated method stub

	}

	public boolean removeReservations(String serviceID) {
		// TODO Auto-generated method stub
		return false;
	}

	public Map<String, Double> registerServiceQoS(String serviceId,
			ArrayList<String> thingServicesList,
			ArrayList<ArrayList<String>> equivalentThingServices) {
		return null;

	}

	public synchronized void updateQoSMEquivalents(
			String serviceId, ArrayList<ArrayList<String>> equivalentThingServices){
		IBigDataDatabaseService service = qosM.getService();

		int reqId = 0;
		for(ArrayList<String> eql : equivalentThingServices)
		{
			LOGTest.debug("DB Start - updateQoSMEquivalents:1 service_id: "+ serviceId);
			// TODO delete
			service.deleteQoSMEquivalentThingServiceStar(serviceId, reqId);
			LOGTest.debug("DB End - updateQoSMEquivalents:1 service_id: "+ serviceId);
			for(String eqid : eql){
				QoSMEquivalentThingServiceStar e = new QoSMEquivalentThingServiceStar(serviceId, reqId, eqid);
				LOGTest.debug("DB Start - updateQoSMEquivalents:2 service_id: "+ serviceId);
				m_equivalents.put(e.getId().keyString(), e);
				service.saveQoSMEquivalentThingServiceStar(e);
				LOGTest.debug("DB End - updateQoSMEquivalents:2 service_id: "+ serviceId);
			}
			reqId++;
		}


	}

	public void unregisterServiceQoS(String serviceId,
			ArrayList<String> selectedThingServicesList) {
		// TODO Auto-generated method stub

	}

	public void unallocate(String serviceId, ArrayList<String> ThingServicesList) {
		// TODO Auto-generated method stub

	}

	public boolean synchronizeThingsServicesQoS(
			HashMap<String, HashMap<String, QoSspec>> thingServices) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean removeThingsServicesQoS(ArrayList<String> deviceID) {
		// TODO Auto-generated method stub
		return false;
	}

	public String getModuleScore() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean promoteAsStar() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean registerNewStar(String gatewayID) {
		// TODO Auto-generated method stub
		return false;
	}

	public QoSRankList assignment(boolean requireResponse, QoSManagerInternalIF caller) {
		// TODO remove waiting
		try {
			this.createAgreement(null, null, null, caller, true);
		} catch (WrongArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(requireResponse)
			return allocation_schema;
		return null;
	}

	public String getGatewayId() {
		return gatewayId;
	}
	public void setGatewayId(String gatewayId) {
		this.gatewayId = gatewayId;
	}

	public QoSManager getQosM() {
		return qosM;
	}

	public void setQosM(QoSManager qosM) {
		this.qosM = qosM;
	}

}
