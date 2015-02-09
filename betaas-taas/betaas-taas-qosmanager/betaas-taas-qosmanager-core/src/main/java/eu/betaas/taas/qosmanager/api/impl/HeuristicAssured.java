package eu.betaas.taas.qosmanager.api.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.Callable;

import org.apache.log4j.Logger;
import org.osgi.framework.BundleContext;

import eu.betaas.taas.bigdatamanager.database.hibernate.data.QoSMAssignmentStar;
import eu.betaas.taas.bigdatamanager.database.hibernate.data.QoSMAssuredRequestStar;
import eu.betaas.taas.bigdatamanager.database.hibernate.data.QoSMEquivalentThingServiceStar;
import eu.betaas.taas.bigdatamanager.database.hibernate.data.QoSMRequestStar;
import eu.betaas.taas.bigdatamanager.database.hibernate.data.QoSMThingServiceStar;
import eu.betaas.taas.bigdatamanager.database.hibernate.data.QoSMThingStar;
import eu.betaas.taas.bigdatamanager.database.service.IBigDataDatabaseService;
import eu.betaas.taas.qosmanager.api.QoSManagerInternalIF;
import eu.betaas.taas.qosmanager.api.QoSManagerNotificationIF;
import eu.betaas.taas.qosmanager.api.QoSRankList;
import eu.betaas.taas.qosmanager.api.QoSrequirements;
import eu.betaas.taas.qosmanager.core.QoSManager;
import eu.betaas.taas.qosmanager.heuristic.Reservation;
import eu.betaas.taas.qosmanager.heuristic.ReservationResults;

class HeuristicAssured implements Callable<QoSRankList>{
	private static Logger LOG = Logger.getLogger("betaas.taas");
	private static Logger LOGTest = Logger.getLogger("betaas.testplan");

	private Map<String, QoSManagerNotificationIF> MapGw;
	private String serviceId;
	private ArrayList<ArrayList<String>> rank;
	private QoSrequirements req;
	private String gatewayId;
	boolean all;
	boolean pending;
	private QoSRankList allocation_schema;
	
	private Map<String, QoSMEquivalentThingServiceStar> equivalents;
	private Map<String, QoSMAssignmentStar> assignments;
	private Map<String, QoSMRequestStar> requests;
	private Map<String, QoSMAssuredRequestStar> assuredrequests;
	private Map<String, QoSMThingServiceStar> thingservices;
	private Map<String, QoSMThingStar> things;
	
	public HeuristicAssured(
			Map<String, QoSMEquivalentThingServiceStar> m_equivalents, 
			Map<String, QoSMAssignmentStar> m_assignments, Map<String, QoSMRequestStar> m_requests, 
			Map<String, QoSMAssuredRequestStar> m_assuredrequests, 
			Map<String, QoSMThingServiceStar> m_thingservices, Map<String, QoSMThingStar> m_things, 
			Map<String, QoSManagerNotificationIF> MapGw, String serviceId,
			ArrayList<ArrayList<String>> rank, QoSrequirements req,
			String gatewayId, boolean all, BundleContext context) {
		this.MapGw = MapGw;
		this.serviceId = serviceId;
		this.rank = rank;
		this.req = req;
		this.gatewayId = gatewayId;
		this.all = all;
		
		this.allocation_schema = new QoSRankList(true);
		setEquivalents(m_equivalents);
		setAssignments(m_assignments);
		setRequests(m_requests);
		setAssuredrequests(m_assuredrequests);
		setThingservices(m_thingservices);
		setThings(m_things);
	}

	private void setRequests(Map<String, QoSMRequestStar> requestsMap) {
		requests = requestsMap;
		
	}

	private void setThings(Map<String, QoSMThingStar> thingsMap) {
		things = thingsMap;
	}

	public QoSRankList call() throws Exception {
		LOG.info("Perform allocation");
		LOG.debug("serviceId:" + this.serviceId);
		LOG.debug("rank:" + this.rank);
		LOG.debug("req:" + this.req);
		LOG.debug("gatewayId:" + this.gatewayId);
		
		ReservationResults ris = null;
		QoSRankList response = new QoSRankList();
		
		List<QoSMThingStar> B = new ArrayList<QoSMThingStar>();
		List<QoSMAssuredRequestStar> K = new ArrayList<QoSMAssuredRequestStar>();
		ArrayList<QoSMAssignmentStar> P = new ArrayList<QoSMAssignmentStar>();
		
		Map<String, PS> PSs = new HashMap<String, PS>();
		Map<QoSMAssuredRequestStar, Set<QoSMEquivalentThingServiceStar>> equivalent = 
				new HashMap<QoSMAssuredRequestStar, Set<QoSMEquivalentThingServiceStar>>();
		
		Map<String, QoSMEquivalentThingServiceStar> new_equivalents = new HashMap<String, QoSMEquivalentThingServiceStar>();
		List<QoSMAssuredRequestStar> new_requests = new ArrayList<QoSMAssuredRequestStar>();
		
		initiliaze_structures(B, K);
		
		if(rank!=null && req != null && serviceId != null){
			LOGTest.debug("Heuristic Start - call add_new_request:1");
			add_new_request(rank, req, serviceId, gatewayId, K, new_requests, equivalent, new_equivalents);
			LOGTest.debug("Heuristic End - call add_new_request:1");
		}


		for(QoSMAssuredRequestStar request : K){
			LOGTest.debug("Heuristic Start - call add_previous_equivalens:1");
			add_previous_equivalens(equivalents, request, equivalent);
			LOGTest.debug("Heuristic End - call add_previous_equivalens:1");
			LOG.debug("Request:" + request);
			LOG.debug("Equivalent:" + equivalent);
		}
		boolean gen = generate_PSs(K, equivalent, PSs);
		if(!gen)
			return null;
				
		double hyperperiod = allocation_schema.getHyperperiod();
		for(QoSMAssuredRequestStar request : K){
			/*LOG.debug("TS:");
			
			for(Entry<String, QoSMThingServiceStar> ts : TS.entrySet()){
				LOG.debug(ts.getKey());
			}*/
			try{
				popolate_P(equivalent, request, P, hyperperiod);
			}catch(Exception e)
			{
				e.printStackTrace();
				return null;
			}
		}
		for(QoSMThingStar t : B){
			t.setBatteryLevel(t.getBatteryLevel()/100); //normalize Battery
			t.setTUsed(0.0);
		}
		for(QoSMThingServiceStar ts : thingservices.values()){
			ts.setBatteryCost(ts.getBatteryCost() / 100); //normalize BatteryCost
		}
		//GRetrieve PSs parameters
		Map<String, Double> Cis = new HashMap<String, Double>();
		Map<String, Double> Tis = new HashMap<String, Double>();
		Map<String, Double> MRT = new HashMap<String, Double>();
		for(String ts : thingservices.keySet()){
			try{
				Cis.put(ts, PSs.get(thingservices.get(ts).getDeviceId()).getCis());
				Tis.put(ts, PSs.get(thingservices.get(ts).getDeviceId()).getTis());
				MRT.put(ts, PSs.get(thingservices.get(ts).getDeviceId()).getMinMaxResonseTime());
			}catch (Exception e)
			{
				continue;
			}
		}
		
		LOG.debug("Allocate");
		Reservation res = new Reservation();
		LOGTest.debug("Reservation Start - call:1");
		ris = res.computeAssured(K, P, B, thingservices, 0.001, Cis, Tis, MRT);
		LOGTest.debug("Reservation End - call:1");
		//debug_input_parametrs(K, P, ris);

		if(!ris.isFeasible()){
			LOG.error("No feasible solution found!");
			return null;	
		}
		else{
			LOG.info("Feasibile solution found");
			LOGTest.debug("DB Start - starupdate_db:1");
			update_data(ris, new_requests, new_equivalents);
			LOGTest.debug("DB End - starupdate_db:1");
			LOG.info("Notify assignments");
			

			if(rank!=null && req != null && serviceId != null)
				response = Utils.notify_assignment(LOG, serviceId, MapGw, ris, hyperperiod, things, assignments, equivalents, 
						thingservices, null, assuredrequests, true);
			else
				response = Utils.notify_assignment(LOG, serviceId, MapGw, ris, hyperperiod, things, assignments, equivalents, 
						thingservices, null, assuredrequests, false);
		}
		response.setThingsMap(things);
		response.setAssignmentsMap(assignments);
		response.setEquivalentsMap(equivalents);
		response.setThingServicesMap(thingservices);
		response.setRequestsMap(requests);
		response.setAssuredRequestsMap(assuredrequests);
		return response;
	}
	
	private void update_data(ReservationResults ris, List<QoSMAssuredRequestStar> new_requests, 
			Map<String, QoSMEquivalentThingServiceStar> new_equivalents) {

		LOG.debug("Save new assignments");
		
		for(QoSMAssignmentStar a : ris.getAss()){
			a.setTotalBatteryCost(a.getTotalBatteryCost() * 100);
			assignments.put(a.getId().keyString(), a);
		}

		LOG.debug("Save new things' status");
		for(QoSMThingStar t : ris.getB()){
			t.setBatteryLevel(t.getBatteryLevel() * 100);
			things.put(t.getDeviceId(), t);
		}
		
		LOG.debug("Save new requests");
		for(QoSMAssuredRequestStar r : new_requests){
			assuredrequests.put(r.getId().keyString(), r);
		}
		
		LOG.debug("Save new equivalents");
		for(String key : new_equivalents.keySet()){
			equivalents.put(key, new_equivalents.get(key));
		}
	}

	private void debug_input_parametrs(List<QoSMAssuredRequestStar> k,
			ArrayList<QoSMAssignmentStar> P, ReservationResults ris) {
		LOG.debug("INPUT REQUEST");
		for(QoSMAssuredRequestStar r : k){
			LOG.debug(r);
			LOG.debug("__________________________");
		}
		LOG.debug("INPUT P");
		for(QoSMAssignmentStar p : P)
		{
			LOG.debug(p);
			LOG.debug("__________________________");
		}
		LOG.debug("RESULTS");
		LOG.debug(ris.toString());
		
	}

	private void popolate_P(
			Map<QoSMAssuredRequestStar, Set<QoSMEquivalentThingServiceStar>> equivalent,
			QoSMAssuredRequestStar request, ArrayList<QoSMAssignmentStar> p,
			double hyperperiod) {
		
		for(QoSMEquivalentThingServiceStar eq : equivalent.get(request)){
			try{
				LOGTest.debug("Heuristic Start - call popolate_P:1");
				QoSMThingServiceStar ts = thingservices.get(eq.getId().getThingServiceId());
				LOGTest.debug("Heuristic End - call popolate_P:1");
				LOGTest.debug("DB Start - popolate_P:1");
				QoSMThingStar t = things.get(ts.getDeviceId());
				LOGTest.debug("DB End - popolate_P:1");
				LOGTest.debug("Heuristic Start - call popolate_P:2");
				if(ts.getResponseTime() <= request.getMaxResponseTime()){
					double factor = hyperperiod / request.getMinInterRequestTime();
					// TODO correct
					double normalizedcost = ts.getBatteryCost() / t.getBatteryLevel();
					//double normalizedcost = ts.getBatteryCost() / 100.0;
					double computationalCost = (request.getMinInterRequestTime() / hyperperiod) 
							* ts.getComputationalCost();
					/*double computationalCost = (request.getMinInterRequestTime() / hyperperiod) 
							* ts.getResponseTime();*/
					p.add(new QoSMAssignmentStar(request.getId().getServiceId(), request.getId().getRequestId(),
							ts.getThingServiceId(), normalizedcost * factor, computationalCost));
				}
				else{
					p.add(new QoSMAssignmentStar(request.getId().getServiceId(), request.getId().getRequestId(),
							ts.getThingServiceId(), Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));

				}
				LOGTest.debug("Heuristic End - call popolate_P:2");
			}catch(NullPointerException e){
				LOG.error(e.getMessage());
			}
			
		}
	}

	private void add_previous_equivalens(
			Map<String, QoSMEquivalentThingServiceStar> equivalents, QoSMAssuredRequestStar request,
			Map<QoSMAssuredRequestStar, Set<QoSMEquivalentThingServiceStar>> equivalent) {
		for(QoSMEquivalentThingServiceStar e : equivalents.values()){
			if(e.getId().getServiceId().equals(request.getId().getServiceId()) 
					&& e.getId().getRequestId() == request.getId().getRequestId())
			{
				if(equivalent.containsKey(request))
				{
					Set<QoSMEquivalentThingServiceStar> tmp = equivalent.get(request);
					tmp.add(e);
					equivalent.put(request, tmp);
				}
				else{
					Set<QoSMEquivalentThingServiceStar> tmp = new HashSet<QoSMEquivalentThingServiceStar>();
					tmp.add(e);
					equivalent.put(request, tmp);
				}
			}
		}
		
	}
	
	private boolean generate_PSs(
		
			List<QoSMAssuredRequestStar> k,
			Map<QoSMAssuredRequestStar, Set<QoSMEquivalentThingServiceStar>> equivalent,
			Map<String, PS> pss) {
		
		boolean PSdone = false;
		while(!PSdone){
			PSdone = true;
			//List for each Thing
			for(QoSMAssuredRequestStar r : k)
			{
				if(!equivalent.containsKey(r)){
					return false;
				}
				LOGTest.debug("Heuristic Start - generate_PSs:1");
				Set<QoSMEquivalentThingServiceStar> eqList = equivalent.get(r);	
				LOGTest.debug("Heuristic End - generate_PSs:1");
				if(eqList.size() == 0)
					return false;
				for(QoSMEquivalentThingServiceStar eq : eqList){
					
					try{
						LOGTest.debug("DB Start - generate_PSs:1");
						if(!thingservices.containsKey(eq.getId().getThingServiceId())){
							LOG.error("No Thing service found.");
							return false;
						}
						QoSMThingServiceStar ts = thingservices.get(eq.getId().getThingServiceId());
						LOGTest.debug("DB End - generate_PSs:1");
						LOGTest.debug("Heuristic Start - generate_PSs:2");
						if(allocation_schema.getHyperperiod() == 0.0)
							allocation_schema.setHyperperiod(r.getMinInterRequestTime());
						double computationalCost = (r.getMinInterRequestTime() / allocation_schema.getHyperperiod()) 
								* ts.getResponseTime();
						LOGTest.debug("Heuristic End - generate_PSs:2");
						if(pss.containsKey(ts.getDeviceId())){
							LOGTest.debug("Heuristic Start - generate_PSs:3");
							PS ps = pss.get(ts.getDeviceId());
							Set<QoSMEquivalentThingServiceStar> lst = ps.getEq();
							lst.add(eq);
							ps.setEq(lst);
							/*QoSMThingStar thing = service.searchQoSMThingStar(ts.getDeviceId()).get(0);
							ps.setThing(thing);*/
							if(pss.get(ts.getDeviceId()).getMaxComputational() < computationalCost){
								ps.setMaxComputational(computationalCost);
								ps.setMaxThingService(eq);
							}
							if(pss.get(ts.getDeviceId()).getMinTime() > r.getMinInterRequestTime())
								ps.setMinTime(r.getMinInterRequestTime());
							if(pss.get(ts.getDeviceId()).getMinMaxResonseTime() > r.getMaxResponseTime())
								ps.setMinMaxResonseTime(r.getMaxResponseTime());
							pss.put(ts.getDeviceId(), ps);
							LOGTest.debug("Heuristic End - generate_PSs:3");
						}
						else
						{
							LOGTest.debug("DB Start - generate_PSs:2");
							if(!things.containsKey(ts.getDeviceId())){
								LOG.error("No Thing found.");
								return false;
							}
							QoSMThingStar thing = things.get(ts.getDeviceId());
							LOGTest.debug("DB End - generate_PSs:2");
							LOGTest.debug("Heuristic Start - generate_PSs:4");
							PS ps = new PS();
							Set<QoSMEquivalentThingServiceStar> lst = new HashSet<QoSMEquivalentThingServiceStar>();
							lst.add(eq);
							ps.setEq(lst);
							
							ps.setThing(thing);
							ps.setRequest(r);
							
							ps.setMaxComputational(computationalCost);
							ps.setMaxThingService(eq);
						
							ps.setMinTime(r.getMinInterRequestTime());
							ps.setMinMaxResonseTime(r.getMaxResponseTime());
							pss.put(ts.getDeviceId(), ps);
							LOGTest.debug("Heuristic End - generate_PSs:4");
						}
					} catch(IndexOutOfBoundsException e){
						LOG.error("Exception: No Thing service found.");
					}
				}
			}
			LOGTest.debug("Heuristic Start - generate_PSs:5");
			//PS
			for(String deviceId : pss.keySet()){
				PS ps = pss.get(deviceId);
				QoSMThingStar t = ps.getThing();
				double Uis = 1.0 - t.getCapacityUsed();
				double Tis = ps.getMaxComputational() / Uis;

				if(Tis < ps.getMinTime()){
					//OK
					ps.setCis(ps.getMaxComputational());
					ps.setTis(Tis);
				}
				else{
					//Remove
					equivalent.get(ps.getRequest()).remove(ps.getMaxThingService());
					PSdone = false;
					break;
				}
			}
			LOGTest.debug("Heuristic End - generate_PSs:5");
			
		}

		//check if all assured request can be satisfied
		for(QoSMAssuredRequestStar r : k)
		{
			if(!equivalent.containsKey(r)){
				return false;
			}
			else{
				if(equivalent.get(r).isEmpty())
					return false;
			}
		}
		return true;
	}

	private void add_new_request(ArrayList<ArrayList<String>> rank,
			QoSrequirements req, String serviceId, String gatewayId,
			List<QoSMAssuredRequestStar> k, List<QoSMAssuredRequestStar> new_requests,
			Map<QoSMAssuredRequestStar, Set<QoSMEquivalentThingServiceStar>> equivalent,
			Map<String, QoSMEquivalentThingServiceStar> new_equivalents) {
		
		int requestId = 0;
		for(ArrayList<String> r: rank){
			QoSMAssuredRequestStar request = new QoSMAssuredRequestStar(serviceId, requestId, 
					req.getMaxResponseTime(), req.getMinInterRequestTime(), gatewayId,
					req.getMaxBurstSize(), req.getAverageRate());
			k.add(request);
			new_requests.add(request);
			for(String eq : r){
				QoSMEquivalentThingServiceStar ets = new QoSMEquivalentThingServiceStar(serviceId, requestId, eq);
				if(equivalent.containsKey(request))
				{
					Set<QoSMEquivalentThingServiceStar> tmp = equivalent.get(request);
					tmp.add(ets);
					equivalent.put(request, tmp);
					new_equivalents.put(ets.getId().keyString(), ets);
				}
				else{
					Set<QoSMEquivalentThingServiceStar> tmp = new HashSet<QoSMEquivalentThingServiceStar>();
					tmp.add(ets);
					equivalent.put(request, tmp);
					new_equivalents.put(ets.getId().keyString(), ets);
				}
			}
			requestId++;
		}
		
	}

	private void initiliaze_structures(List<QoSMThingStar> B,
			List<QoSMAssuredRequestStar> K) {
		if(assuredrequests != null){
			for(QoSMAssuredRequestStar req : assuredrequests.values()){
				K.add(new QoSMAssuredRequestStar(req));
			}
		}
		
		LOGTest.debug("Heuristic End - call initiliaze_structures:1");
		Utils.update_batteryLevel(things, MapGw);
		LOGTest.debug("Heuristic Start - call initiliaze_structures:2");
		if(things != null){
			for(QoSMThingStar thing : things.values()){
				B.add(new QoSMThingStar(thing.getDeviceId(), thing.getBatteryLevel(), 
						0, 0.0, thing.getGatewayId()));
			}
		}
		LOGTest.debug("Heuristic End - call initiliaze_structures:2");
	}
	
	public Map<String, QoSMEquivalentThingServiceStar> getEquivalents() {
		return equivalents;
	}

	public void setEquivalents(Map<String, QoSMEquivalentThingServiceStar> equivalents) {
		this.equivalents = equivalents;
	}

	public Map<String, QoSMAssignmentStar> getAssignments() {
		return assignments;
	}

	public void setAssignments(Map<String, QoSMAssignmentStar> assignments) {
		this.assignments = assignments;
	}

	public Map<String, QoSMThingServiceStar> getThingservices() {
		return thingservices;
	}

	public void setThingservices(Map<String, QoSMThingServiceStar> thingservices) {
		this.thingservices = thingservices;
	}

	public Map<String, QoSMAssuredRequestStar> getAssuredrequests() {
		return assuredrequests;
	}

	public void setAssuredrequests(Map<String, QoSMAssuredRequestStar> assuredrequests) {
		this.assuredrequests = assuredrequests;
	}
	
}


