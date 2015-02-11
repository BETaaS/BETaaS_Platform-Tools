package eu.betaas.taas.qosmanager.api.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;

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
import eu.betaas.taas.qosmanager.heuristic.ReservationResults;

public class Utils {
	private static Logger LOGTest = Logger.getLogger("betaas.testplan");
	
	protected static Map<String, QoSRankResults> createNotifications(
			Map<String, QoSMAssignmentStar> assignments, Map<String, QoSMEquivalentThingServiceStar> equivalents, 
			Map<String, QoSMThingServiceStar> thingservices, Map<String, QoSMThingStar> things) {
		
		Map<String, QoSRankResults> ris = new HashMap<String, QoSRankResults>();
		
		Set<String> gateways = new HashSet<String>();
		
		Map<String, Set<QoSMAssignmentInternal>> risAss = 
				new HashMap<String, Set<QoSMAssignmentInternal>>();
		
		for(QoSMAssignmentStar a : assignments.values()){
			String thingserviceId = a.getId().getThingServiceId();
			QoSMThingServiceStar ts = thingservices.get(thingserviceId);
			QoSMThingStar t = things.get(ts.getDeviceId());
			String gatewayId = t.getGatewayId();
			gateways.add(gatewayId);
			if(risAss.containsKey(gatewayId)){
				if(risAss.get(gatewayId).isEmpty()){
					//first assignment for the gateway
					Set<QoSMAssignmentInternal> lst = new HashSet<QoSMAssignmentInternal>();
					lst.add(new QoSMAssignmentInternal(a.getId().getServiceId(),
								a.getId().getRequestId(), ts.getThingServiceId(), a.getTotalBatteryCost(), 
								a.getTotalComputationalCost()));
					risAss.put(gatewayId, lst);
				}
				else{
					Set<QoSMAssignmentInternal> lst = risAss.get(gatewayId);
					lst.add(new QoSMAssignmentInternal(a.getId().getServiceId(),
							a.getId().getRequestId(), ts.getThingServiceId(), a.getTotalBatteryCost(), 
							a.getTotalComputationalCost()));
					risAss.put(gatewayId, lst);
				}
			}
			else{
				Set<QoSMAssignmentInternal> lst = new HashSet<QoSMAssignmentInternal>();
				lst.add(new QoSMAssignmentInternal(a.getId().getServiceId(),
							a.getId().getRequestId(), ts.getThingServiceId(), a.getTotalBatteryCost(), 
							a.getTotalComputationalCost()));
				risAss.put(gatewayId, lst);
			}
		}
		
		Map<String, Set<QoSMThingInternal>> risThing = 
				new HashMap<String, Set<QoSMThingInternal>>();
		for(String deviceID : things.keySet()){
			QoSMThingStar t = things.get(deviceID);
			String gatewayId = t.getGatewayId();
			gateways.add(gatewayId);
			if(risThing.containsKey(gatewayId)){
				if(risThing.get(gatewayId).isEmpty()){
					//first assignment for the gateway
					Set<QoSMThingInternal> lst = new HashSet<QoSMThingInternal>();
					lst.add(new QoSMThingInternal(deviceID,t.getBatteryLevel(), t.getNumass(), 
							t.getCapacityUsed(), gatewayId));
					risThing.put(gatewayId, lst);
				}
				else{
					Set<QoSMThingInternal> lst = risThing.get(gatewayId);
					lst.add(new QoSMThingInternal(deviceID,t.getBatteryLevel(), t.getNumass(), 
							t.getCapacityUsed(), gatewayId));
					risThing.put(gatewayId, lst);
				}
			}
			else{
				Set<QoSMThingInternal> lst = new HashSet<QoSMThingInternal>();
				lst.add(new QoSMThingInternal(deviceID,t.getBatteryLevel(), t.getNumass(), 
						t.getCapacityUsed(), gatewayId));
				risThing.put(gatewayId, lst);
			}

		}
		
		Map<String, Set<QoSMEquivalentThingServiceInternal>> risEq = 
				new HashMap<String, Set<QoSMEquivalentThingServiceInternal>>();
		
		for(QoSMEquivalentThingServiceStar eq : equivalents.values()){
			String serviceId = eq.getId().getServiceId();
			Integer requestId = eq.getId().getRequestId();
			String thingserviceId = eq.getId().getThingServiceId();
			QoSMThingServiceStar ts = thingservices.get(thingserviceId);
			QoSMThingStar t = things.get(ts.getDeviceId());
			String gatewayId = t.getGatewayId();
			gateways.add(gatewayId);
			if(risEq.containsKey(gatewayId)){
				if(risEq.get(gatewayId).isEmpty()){
					//first assignment for the gateway
					Set<QoSMEquivalentThingServiceInternal> lst = 
							new HashSet<QoSMEquivalentThingServiceInternal>();
					lst.add(new QoSMEquivalentThingServiceInternal(serviceId,requestId,thingserviceId));
					risEq.put(gatewayId, lst);
				}
				else{
					Set<QoSMEquivalentThingServiceInternal> lst = risEq.get(gatewayId);
					lst.add(new QoSMEquivalentThingServiceInternal(serviceId,requestId,thingserviceId));
					risEq.put(gatewayId, lst);
				}
			}
			else{
				Set<QoSMEquivalentThingServiceInternal> lst = 
						new HashSet<QoSMEquivalentThingServiceInternal>();
				lst.add(new QoSMEquivalentThingServiceInternal(serviceId,requestId,thingserviceId));
				risEq.put(gatewayId, lst);
			}
			
		}
		for(String gw : gateways)
		{
			QoSRankResults res = new QoSRankResults(true, risAss.get(gw), risThing.get(gw), risEq.get(gw));
			ris.put(gw, res);
		}
		return ris;
	}


	protected static void create_response_for_requester(String serviceId, Map<String, QoSMAssignmentStar> assignments,
			QoSRankList response) {
		ArrayList<ArrayList<String>> listResponse = new ArrayList<ArrayList<String>>();
		Map<Integer, ArrayList<String>> m_listResponse = new HashMap<Integer, ArrayList<String>>();
		//Create response for the requester gateway

		for(QoSMAssignmentStar a : assignments.values()){
			if(a.getId().getServiceId().equals(serviceId)){
				int reqId = a.getId().getRequestId();
				if(m_listResponse.containsKey(reqId))
				{
					ArrayList<String> tmp = m_listResponse.get(reqId);
					tmp.add(a.getId().getThingServiceId());
					m_listResponse.put(reqId, tmp);
				}
				else{
					ArrayList<String> tmp = new ArrayList<String>();
					tmp.add(a.getId().getThingServiceId());
					m_listResponse.put(reqId, tmp);
				}
			}
				
		}
		for(ArrayList<String> lst : m_listResponse.values()){
			listResponse.add(lst);
		}
		response.setAssignments(listResponse);
	}

	protected static void create_total_assignment_for_involved_gateways(
			Map<String, QoSMAssignmentStar> assignments,
			Map<String, QoSRankResults> notifyMap,
			Map<String, QoSMThingStar> things,
			Map<String, QoSMRequestStar> requests,
			Map<String, QoSMAssuredRequestStar> assuredrequests) {
		//create total assignment for involved gateways
		for(QoSMAssignmentStar a : assignments.values()){
			String serviceId = a.getId().getServiceId();
			Integer requestId = a.getId().getRequestId();
			String key = serviceId + ":" + (int)requestId;
			String ownerGW = "";
			if(requests != null){
				QoSMRequestStar request = requests.get(key);
				ownerGW = request.getGatewayId();
			}
			else{
				try{
				QoSMAssuredRequestStar request = assuredrequests.get(key);
				ownerGW = request.getGatewayId();
				} catch(NullPointerException e){
					continue;
				}
			}
			
			if(notifyMap.containsKey(ownerGW)){
				if(notifyMap.get(ownerGW).getAssignments() == null)
					notifyMap.get(ownerGW).setAssignments(new HashSet<QoSMAssignmentInternal>());
				notifyMap.get(ownerGW).getAssignments().add(new QoSMAssignmentInternal(serviceId, requestId, 
						a.getId().getThingServiceId(), a.getTotalBatteryCost(), a.getTotalComputationalCost()));
			}
			else{
				notifyMap.put(ownerGW, new QoSRankResults(true, new HashSet<QoSMAssignmentInternal>(), 
						new HashSet<QoSMThingInternal>(), 
						new HashSet<QoSMEquivalentThingServiceInternal>()));
				notifyMap.get(ownerGW).getAssignments().add(new QoSMAssignmentInternal(serviceId, requestId, 
						a.getId().getThingServiceId(), a.getTotalBatteryCost(), a.getTotalComputationalCost()));
			}
		}
		
	}

	protected static QoSRankList notify_assignment(Logger LOG, String serviceId, Map<String, QoSManagerNotificationIF> mapGw, 
			ReservationResults ris, double hyperperiod,
			Map<String, QoSMThingStar> things, Map<String, QoSMAssignmentStar> assignments, 
			Map<String, QoSMEquivalentThingServiceStar> equivalents, Map<String, QoSMThingServiceStar> thingservices, Map<String, QoSMRequestStar> requests, 
			Map<String, QoSMAssuredRequestStar> assuredrequests, boolean request_response) {
		
		QoSRankList response = new QoSRankList();
		// gw, result
		Map<String, QoSRankResults> notifyMap = Utils.createNotifications(assignments, equivalents, thingservices, things);
		response.setFeas(true);
		response.setHyperperiod(hyperperiod);
		if(request_response){
			Utils.create_response_for_requester(serviceId, assignments, response);
		}
		Utils.create_total_assignment_for_involved_gateways(assignments, notifyMap, things, requests, assuredrequests);
		
		response.setNotifyMap(notifyMap);
		response.setMapGw(mapGw);
		return response;
	}



	public static void update_batteryLevel(Map<String, QoSMThingStar> mapThing,
			Map<String, QoSManagerNotificationIF> mapGw) {
		
		for(QoSManagerNotificationIF gwQoS : mapGw.values()){
			//Get battery of all the thing connected to GW gwQoS
			Map<String, Double> batteries = gwQoS.getBatteryLevels();
			for(String item : batteries.keySet()){
				mapThing.get(item).setBatteryLevel(batteries.get(item));
			}
		}	
	}

	
}
