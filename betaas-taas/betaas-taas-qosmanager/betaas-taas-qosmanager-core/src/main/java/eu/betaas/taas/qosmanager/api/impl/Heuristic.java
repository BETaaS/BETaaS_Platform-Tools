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
import eu.betaas.taas.qosmanager.api.QoSManagerInternalIF;
import eu.betaas.taas.qosmanager.api.QoSManagerNotificationIF;
import eu.betaas.taas.qosmanager.api.QoSRankList;
import eu.betaas.taas.qosmanager.api.QoSRankResults;
import eu.betaas.taas.qosmanager.api.QoSrequirements;
import eu.betaas.taas.qosmanager.core.QoSManager;
import eu.betaas.taas.qosmanager.heuristic.Reservation;
import eu.betaas.taas.qosmanager.heuristic.ReservationResults;

class Heuristic implements Callable<QoSRankList>{
	private static Logger LOG = Logger.getLogger("betaas.taas");
	private static Logger LOGTest = Logger.getLogger("betaas.testplan");
	private Map<String, QoSManagerNotificationIF> MapGw;
	private String serviceId;
	private ArrayList<ArrayList<String>> rank;
	private QoSrequirements req;
	private String gatewayId;
	boolean all;
	private Map<String, QoSMEquivalentThingServiceStar> equivalents;
	private Map<String, QoSMAssignmentStar> assignments;
	private Map<String, QoSMRequestStar> requests;
	private Map<String, QoSMAssuredRequestStar> assuredrequests;
	private Map<String, QoSMThingServiceStar> thingservices;
	private Map<String, QoSMThingStar> things;
	
	
	public Heuristic(
			Map<String, QoSMEquivalentThingServiceStar> m_equivalents, 
			Map<String, QoSMAssignmentStar> m_assignments, Map<String, QoSMRequestStar> m_requests, 
			Map<String, QoSMAssuredRequestStar> m_assuredrequests, 
			Map<String, QoSMThingServiceStar> m_thingservices, Map<String, QoSMThingStar> m_things, 
			Map<String, QoSManagerNotificationIF> MapGw, String serviceId, ArrayList<ArrayList<String>> rank,
			QoSrequirements req, String gatewayId, boolean all, BundleContext context) {
		this.MapGw = MapGw;
		this.serviceId = serviceId;
		this.rank = rank;
		this.req = req;
		this.gatewayId = gatewayId;
		this.all = all;
		setEquivalents(m_equivalents);
		setAssignments(m_assignments);
		setRequests(m_requests);
		setThingservices(m_thingservices);
		setThings(m_things);
		setAssuredrequests(m_assuredrequests);
	}
	public QoSRankList call() throws Exception {
		LOG.info("Perform allocation");
		LOG.debug("serviceId:" + this.serviceId);
		LOG.debug("rank:" + this.rank);
		LOG.debug("req:" + this.req);
		LOG.debug("gatewayId:" + this.gatewayId);
		
		List<QoSMThingStar> B = new ArrayList<QoSMThingStar>();
		List<QoSMRequestStar> K = new ArrayList<QoSMRequestStar>();
		ArrayList<QoSMAssignmentStar> P = new ArrayList<QoSMAssignmentStar>();
		ArrayList<Double> periods = new ArrayList<Double>();

		ReservationResults ris = null;
		QoSRankList response = null;
		Map<QoSMRequestStar, Set<QoSMEquivalentThingServiceStar>> equivalent = 
				new HashMap<QoSMRequestStar, Set<QoSMEquivalentThingServiceStar>>();
		
		Map<String, QoSMEquivalentThingServiceStar> new_equivalents = new HashMap<String, QoSMEquivalentThingServiceStar>();
		List<QoSMRequestStar> new_requests = new ArrayList<QoSMRequestStar>();
		
		initiliaze_structures(periods, B, K);
		
		if(rank!=null && req != null && serviceId != null){
			LOGTest.debug("Heuristic Start - call add_new_request:1");
			add_new_request(rank, req, serviceId, gatewayId, K, new_requests, equivalent, new_equivalents, periods);
			LOGTest.debug("Heuristic End - call add_new_request:1");
		}
		double hyperperiod = lcms(periods);
		for(QoSMRequestStar request : K){
			LOGTest.debug("Heuristic Start - call add_previous_equivalens:1");
			add_previous_equivalens(equivalents, request, equivalent);
			LOGTest.debug("Heuristic End - call add_previous_equivalens:1");
			LOG.debug("Request:" + request);
			LOG.debug("Equivalent:" + equivalent);
			LOG.debug("TS:");
			/*for(Entry<String, QoSMThingServiceStar> ts : TS.entrySet()){
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
		}
		for(QoSMThingServiceStar ts : thingservices.values()){
			ts.setBatteryCost(ts.getBatteryCost() / 100); //normalize BatteryCost
		}
		LOG.debug("Allocate");
		Reservation res = new Reservation();
		LOGTest.debug("Reservation Start - call:1");
		ris = res.compute(K, P, B, thingservices, 0.001);
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
				response = Utils.notify_assignment(LOG, serviceId, MapGw, ris, hyperperiod, things, assignments, equivalents, thingservices, requests, null, true);
			else
				response = Utils.notify_assignment(LOG, serviceId, MapGw, ris, hyperperiod, things, assignments, equivalents, thingservices, requests, null, false);
			response.setThingsMap(things);
			response.setAssignmentsMap(assignments);
			response.setEquivalentsMap(equivalents);
			response.setThingServicesMap(thingservices);
			response.setRequestsMap(requests);
			response.setAssuredRequestsMap(assuredrequests);
		}
		return response;
	}
	
	private void update_data(ReservationResults ris, List<QoSMRequestStar> new_requests, 
			Map<String, QoSMEquivalentThingServiceStar> new_equivalents) {

		LOG.debug("Save new assignments");
		assignments.clear();
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
		for(QoSMRequestStar r : new_requests){
			requests.put(r.getId().keyString(), r);
		}
		
		LOG.debug("Save new equivalents");
		for(String key : new_equivalents.keySet()){
			equivalents.put(key, new_equivalents.get(key));
		}
	}

	private void debug_input_parametrs(List<QoSMRequestStar> K,
			ArrayList<QoSMAssignmentStar> P, ReservationResults ris) {
		LOG.debug("INPUT REQUEST");
		for(QoSMRequestStar r : K){
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
			Map<QoSMRequestStar, Set<QoSMEquivalentThingServiceStar>> equivalent,
			QoSMRequestStar request, ArrayList<QoSMAssignmentStar> p, double hyperperiod) {
		
		for(QoSMEquivalentThingServiceStar eq : equivalent.get(request)){
			try{
				LOGTest.debug("Heuristic Start - call popolate_P:1");
				QoSMThingServiceStar ts = thingservices.get(eq.getId().getThingServiceId());
				LOGTest.debug("Heuristic End - call popolate_P:1");
				LOGTest.debug("DB Start - popolate_P:1");
				QoSMThingStar thing = things.get(ts.getDeviceId());
				LOGTest.debug("DB End - popolate_P:1");
				LOGTest.debug("Heuristic Start - call popolate_P:2");
				if(ts.getResponseTime() <= request.getMaxResponseTime()){
					double factor = hyperperiod / request.getMinInterRequestTime();
					// TODO correct
					double normalizedcost = ts.getBatteryCost() / thing.getBatteryLevel();
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
			Map<String, QoSMEquivalentThingServiceStar> equivalents, QoSMRequestStar request,
			Map<QoSMRequestStar, Set<QoSMEquivalentThingServiceStar>> equivalent) {
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

	private void add_new_request(
			ArrayList<ArrayList<String>> rank,
			QoSrequirements req,
			String serviceId,
			String gatewayId, List<QoSMRequestStar> K,
			List<QoSMRequestStar> new_requests,
			Map<QoSMRequestStar, Set<QoSMEquivalentThingServiceStar>> equivalent,
			Map<String, QoSMEquivalentThingServiceStar> new_equivalents,
			ArrayList<Double> periods) {

			int requestId=0;
			for(ArrayList<String> r: rank){
				QoSMRequestStar request = new QoSMRequestStar(serviceId, requestId, 
						req.getMaxResponseTime(), req.getMinInterRequestTime(), gatewayId);
				K.add(request);
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
			periods.add(req.getMinInterRequestTime());		
	}

	private void initiliaze_structures(
			ArrayList<Double> periods,
			List<QoSMThingStar> B, List<QoSMRequestStar> K) {
		

		LOGTest.debug("Heuristic Start - call initiliaze_structures:1");
		if(requests != null){
			for(QoSMRequestStar req : requests.values()){
				K.add(new QoSMRequestStar(req));
			}
		}
		for(QoSMRequestStar request : K)
			periods.add(request.getMinInterRequestTime());
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
	
	private static long lcms(ArrayList<Double> periods)
	{
	    long result = (long) Math.ceil(periods.get(0));
	    for(int i = 1; i < periods.size(); i++) result = lcm(result, (long) Math.ceil(periods.get(i)));
	    return result;
	}
	
	private static long gcd(long a, long b)
	{
	    while (b > 0)
	    {
	        long temp = b;
	        b = a % b; // % is remainder
	        a = temp;
	    }
	    return a;
	}

	private static long lcm(long a, long b)
	{
	    return a * (b / gcd(a, b));
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

	public Map<String, QoSMRequestStar> getRequests() {
		return requests;
	}

	public void setRequests(Map<String, QoSMRequestStar> requests) {
		this.requests = requests;
	}

	public Map<String, QoSMThingServiceStar> getThingservices() {
		return thingservices;
	}

	public void setThingservices(Map<String, QoSMThingServiceStar> thingservices) {
		this.thingservices = thingservices;
	}

	public Map<String, QoSMThingStar> getThings() {
		return things;
	}

	public void setThings(Map<String, QoSMThingStar> things) {
		this.things = things;
	}
	public void setAssuredrequests(Map<String, QoSMAssuredRequestStar> assuredrequests) {
		this.assuredrequests = assuredrequests;
	}
	public Map<String, QoSMAssuredRequestStar> getAssuredrequests() {
		return assuredrequests;
	}
	
}

