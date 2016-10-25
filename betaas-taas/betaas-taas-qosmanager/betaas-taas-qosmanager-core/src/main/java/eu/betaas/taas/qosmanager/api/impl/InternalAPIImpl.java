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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Logger;

import eu.betaas.service.servicemanager.api.ServiceManagerInternalIF;
import eu.betaas.taas.bigdatamanager.database.hibernate.data.QoSMAssignmentInternal;
import eu.betaas.taas.bigdatamanager.database.hibernate.data.QoSMAssuredRequestInternal;
import eu.betaas.taas.bigdatamanager.database.hibernate.data.QoSMEquivalentThingServiceInternal;
import eu.betaas.taas.bigdatamanager.database.hibernate.data.QoSMRequestInternal;
import eu.betaas.taas.bigdatamanager.database.hibernate.data.QoSMThingInternal;
import eu.betaas.taas.bigdatamanager.database.hibernate.data.QoSMThingServiceInternal;
import eu.betaas.taas.bigdatamanager.database.service.IBigDataDatabaseService;
import eu.betaas.taas.bigdatamanager.database.service.ThingsData;
import eu.betaas.taas.contextmanager.api.ThingsServiceManager;
import eu.betaas.taas.qosmanager.api.QoSManagerInternalIF;
import eu.betaas.taas.qosmanager.api.QoSRankList;
import eu.betaas.taas.qosmanager.api.QoSRankResults;
import eu.betaas.taas.qosmanager.api.QoSrequirements;
import eu.betaas.taas.qosmanager.api.QoSspec;
import eu.betaas.taas.qosmanager.api.WrongArgumentException;
import eu.betaas.taas.qosmanager.core.QoSManager;
import eu.betaas.taas.qosmanager.core.QoSManagerActivator;
import eu.betaas.taas.qosmanager.monitoring.api.QoSManagerMonitoring;
import eu.betaas.taas.qosmanager.monitoring.api.impl.SLACalculation;
import eu.betaas.taas.qosmanager.negotiation.NegotiationInterface;
import eu.betaas.taas.taasresourcesmanager.api.TaaSResourceManager;

public class InternalAPIImpl implements QoSManagerInternalIF, Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static Logger LOG = Logger.getLogger("betaas.taas");
	
	private static Logger LOGTest = Logger.getLogger("betaas.testplan");
	
	private QoSManager qosM;
	private NegotiationInterface ni;
	private TaaSResourceManager trm;
	private ServiceManagerInternalIF sm;
	private QoSManagerMonitoring qosMonitoring;
	private ThingsServiceManager cm;
	private String gatewayId;
	private Double jitter;
	private Map<String, QoSMEquivalentThingServiceInternal> m_equivalents;
	private Map<String, QoSMAssignmentInternal> m_assignments;
	private Map<String, QoSMRequestInternal> m_requests;
	private Map<String, QoSMAssuredRequestInternal> m_assuredrequests;
	private Map<String, QoSMThingServiceInternal> m_thingservices;
	private Map<String, QoSMThingInternal> m_things;

	private String delimiter;
	
	public InternalAPIImpl(QoSManagerActivator qoSManagerActivator,
			QoSManager qosm, NegotiationInterface n,  
			TaaSResourceManager t, 
			ServiceManagerInternalIF s, QoSManagerMonitoring qM, ThingsServiceManager cmservice, String gwId, 
			Double qosMonitoringJitter, String delimeter){
		qosM = qosm;
		ni = n;
		trm = t;
		sm = s;
		qosMonitoring = qM;
		cm = cmservice;
		gatewayId = gwId;
		jitter = qosMonitoringJitter;
		this.delimiter = delimeter;
		m_equivalents = new HashMap<String, QoSMEquivalentThingServiceInternal>();

		m_assignments = new HashMap<String, QoSMAssignmentInternal>();

		m_requests = new HashMap<String, QoSMRequestInternal>();

		m_thingservices = new HashMap<String, QoSMThingServiceInternal>();

		m_things = new HashMap<String, QoSMThingInternal>();
		
		m_assuredrequests = new HashMap<String, QoSMAssuredRequestInternal>();
		
		read_db(m_equivalents, m_assignments, m_requests, m_assuredrequests, m_thingservices, m_things);
	}
	public String getTemplate() {
		// Forward the request to the negotiation manager
		return ni.getTemplate("BETaaS-Template");
	}

	public void createAgreement(String offer) {
		LOGTest.debug("Start createAgreement");
		LOG.info("Receive CreateAgreement");
		this.qosM.sendData("Create new Agreement", "info", "QoSM");
		// Parse offer
		InputStream stream = new ByteArrayInputStream(offer.getBytes());
		XMLInputFactory factory = XMLInputFactory.newInstance();
		String serviceid = null;
		String maxresponsetime = null;
		String mininterrequesttime = null;
		String maxburstsize = null;
		String avgrate = null;
		try {
			XMLStreamReader reader = factory.createXMLStreamReader(stream);
			String tagContent = null;
			
			while(reader.hasNext()){
				int event = reader.next();
			    switch(event){
			    	case XMLStreamConstants.CHARACTERS:
		    	          tagContent = reader.getText().trim();
		    	          break;
			    	case XMLStreamConstants.END_ELEMENT:
			    		if(reader.getLocalName().equals("transactionID")){
			    			serviceid = tagContent;
			    		}
			    		if(reader.getLocalName().equals("MaxResponseTime")){
			    			maxresponsetime = tagContent;
			    		}
			    		if(reader.getLocalName().equals("MaxInterRequestTime")){
			    			mininterrequesttime = tagContent;
			    		}
			    		if(reader.getLocalName().equals("MaxBurstSize")){
			    			maxburstsize = tagContent;
			    		}
			    		if(reader.getLocalName().equals("AverageRate")){
			    			avgrate = tagContent;
			    		}
			    		break;
			    }
	    
			}
		} catch (XMLStreamException e) {
			e.printStackTrace();
		}
		if(maxburstsize.startsWith("$"))
			maxburstsize = null;
		if(avgrate.startsWith("$"))
			avgrate = null;
		LOGTest.debug("ServiceId" + serviceid);
		LOG.debug("ServiceId" + serviceid + " MaxResponseTime" + maxresponsetime 
				+ " MinInterRequestTime" + mininterrequesttime + "MaxBurstSize" + maxburstsize + "AverageRate" +avgrate);
		LOGTest.debug("TaaSRM Start - createAgreement:1");
		// Get the equivalent things rank
		ArrayList<ArrayList<String>> rank = trm.getSecurityRank (serviceid);
		LOG.info("TaaSRM Security Rank");
		this.qosM.sendData("Retrieve Security rank", "info", "QoSM");
		LOG.info(rank);
		LOGTest.debug("TaaSRM End - createAgreement:1");
		LOG.info("Retrieved taasRM security Rank");
		
		if(maxburstsize == null && avgrate == null){
			//Real-time PUSH

			QoSrequirements requirements = new QoSrequirements();
			requirements.setMaxResponseTime(Double.parseDouble(maxresponsetime));
			requirements.setMinInterRequestTime(Double.parseDouble(mininterrequesttime));
			
			LOG.info("invoke QoSM* createAgreement");
			QoSRankList reply;
			try {
				reply = qosM.getQosm_star().createAgreement(serviceid, rank, requirements, this, true);
				if(reply.isFeas()){
					LOG.info("Feasible allocation found");
					this.qosM.sendData("Feasible allocation found", "info", "QoSM");
					LOG.debug("Popolate requests");
					LOG.info("Save requests to DB");
					LOGTest.debug("DB Start - createAgreement:1");
					
					update_db(rank, serviceid, requirements);
					 
					LOGTest.debug("DB End  - createAgreement:1");
					LOGTest.debug("Negotiation Start - createAgreement:1");
					// Forward the offer to the negotiation manager
					String agreement = ni.sendOffer(offer);
					LOGTest.debug("Negotiation End - createAgreement:1");
					if(agreement != null && agreement != ""){
						LOGTest.debug("SM Start - createAgreement:1");
						// Notify the created agreement EPR
						LOG.info("Send Success Notification to SM");
						this.qosM.sendData("Send Success Notification to SM", "info", "QoSM");
						sm.notifyAgreementEPR(serviceid,agreement);
						LOGTest.debug("SM End - createAgreement:1");
						ArrayList<ArrayList<String>> equivalentTSListQoSRank = reply.getAssignments();
						LOGTest.debug("TaaSRM Start - createAgreement:2");
						// Push back the rank 
						LOG.info("Send putQoSRank to TaaSRM serviceId: " + serviceid);
						this.qosM.sendData("Send results to TaaSRM", "info", "QoSM");
						LOG.info(equivalentTSListQoSRank);
						trm.putQoSRank(serviceid, equivalentTSListQoSRank);
						LOGTest.debug("TaaSRM End - createAgreement:2");
					}
					else{
						LOG.error("Negotiation Error. Send RevokeService to TaaSRM");
						this.qosM.sendData("Negotiation Error. Send RevokeService to TaaSRM", "error", "QoSM");
						trm.revokeService(serviceid);
					}
				}
				else{
					LOG.error("No feasible allocation found. Send RevokeService to TaaSRM");
					this.qosM.sendData("No feasible allocation found. Send RevokeService to TaaSRM", "error", "QoSM");
					trm.revokeService(serviceid);
				}
			} catch (WrongArgumentException e) {
				e.printStackTrace();
			}
		}
		else{
			// Real-Time PULL
			QoSrequirements requirements = new QoSrequirements();
			requirements.setMaxResponseTime(Double.parseDouble(maxresponsetime));
			requirements.setMinInterRequestTime(Double.parseDouble(mininterrequesttime));
			requirements.setMaxBurstSize(Double.parseDouble(maxburstsize));
			requirements.setAverageRate(Double.parseDouble(avgrate));
			
			LOG.info("invoke QoSM* createAgreement");
			QoSRankList reply;
			try {
				System.err.println(serviceid);
				System.err.println(rank);
				System.err.println(requirements);
				reply = qosM.getQosm_star().createAgreementAssured(serviceid, rank, requirements, this, true);
				if(reply != null && reply.isFeas()){
					LOG.info("Feasible allocation found");
					this.qosM.sendData("Feasible allocation found", "info", "QoSM");
					LOG.debug("Popolate requests");
					LOG.info("Save requests to DB");
					LOGTest.debug("DB Start - createAgreement:2");
					assured_update_db(rank, serviceid, requirements);
					
					LOGTest.debug("DB End - createAgreement:2");
					// Forward the offer to the negotiation manager
					LOGTest.debug("Negotiation Start - createAgreement:2");
					String agreement = ni.sendOffer(offer);
					LOGTest.debug("Negotiation End - createAgreement:2");
					if(agreement != null && agreement != ""){
						LOG.info("Send Success Notification to SM");
						this.qosM.sendData("Send Success Notification to SM", "info", "QoSM");
						// Notify the created agreement EPR
						sm.notifyAgreementEPR(serviceid,agreement);
						
						ArrayList<ArrayList<String>> equivalentTSListQoSRank = reply.getAssignments();
						LOG.info("Send putQoSRank to TaaSRM");
						this.qosM.sendData("Send results to TaaS RM", "info", "QoSM");
						// Push back the rank 
						trm.putQoSRank(serviceid, equivalentTSListQoSRank);
					}
					else{
						LOG.error("Send RevokeService to TaaSRM");
						this.qosM.sendData("Send RevokeService to TaaSRM", "error", "QoSM");
						trm.revokeService(serviceid);
					}
				}
				else{
					LOG.error("Send RevokeService to TaaSRM");
					this.qosM.sendData("Send RevokeService to TaaSRM", "error", "QoSM");
					trm.revokeService(serviceid);
				}
			} catch (WrongArgumentException e) {
				e.printStackTrace();
			}
		}
		LOGTest.debug("createAgreement Finished");
	}

	private synchronized void assured_update_db(ArrayList<ArrayList<String>> rank,
			String serviceid, QoSrequirements requirements) {
		int reqId = 0;
		for(ArrayList<String> r: rank)
		{
			QoSMAssuredRequestInternal request = new QoSMAssuredRequestInternal(serviceid, reqId, 
					requirements.getMaxResponseTime(), requirements.getMinInterRequestTime(), 
					requirements.getMaxBurstSize(), requirements.getAverageRate());
			IBigDataDatabaseService service = qosM.getService();
			LOG.debug("insert_request Request:" + request);
			
			service.saveQoSMAssuredRequestInternal(request);
			reqId++;
		}
		
	}
	
	private synchronized void update_db(ArrayList<ArrayList<String>> rank, String serviceid,
			QoSrequirements requirements) {
		int reqId = 0;
		for(ArrayList<String> r: rank)
		{
			int period = (int) requirements.getMinInterRequestTime();
			QoSMRequestInternal request = new QoSMRequestInternal(serviceid, reqId, 
					requirements.getMaxResponseTime(), requirements.getMinInterRequestTime());
			request.setPeriod(period);
			IBigDataDatabaseService service = qosM.getService();
			LOG.debug("insert_request Request:" + request);
			
			service.saveQoSMRequestInternal(request);
			
			reqId++;
		}
		
	}
	
	public Map<String, Double> registerServiceQoSPUSH(String serviceId, int periodInMillisec, 
			ArrayList<ArrayList<String>> equivalentThingServices){
		return this.registerServiceQoS(serviceId, periodInMillisec, equivalentThingServices, false);
	}
	
	@Deprecated
	public Map<String, Double> registerServiceQoSPUSH(String serviceId, int periodInMillisec, 
			ArrayList<String> thingServicesList, ArrayList<ArrayList<String>> equivalentThingServices){
		LOG.error("Deprecated registerServiceQoSPUSH");
		return null;
	}
	@Deprecated
	public Map<String, Double> registerServiceQoSPUSH(String serviceId,
			ArrayList<String> thingServicesList,
			ArrayList<ArrayList<String>> equivalentThingServices) {
		LOG.error("Deprecated registerServiceQoSPUSH");
		return null;
	}

	public List<String> registerServiceQoSPULL(String serviceId,
			ArrayList<ArrayList<String>> equivalentThingServices) {
		Map<String, Double> ret = this.registerServiceQoS(serviceId, 0, equivalentThingServices, true);
		List<String> ris = new ArrayList<String>();
		for(String ts : ret.keySet())
			ris.add(ts);
		return ris;
	}
	@Deprecated
	public List<String> registerServiceQoSPULL(String serviceId,
			ArrayList<String> thingServicesList,
			ArrayList<ArrayList<String>> equivalentThingServices) {
		LOG.error("Deprecated registerServiceQoSPULL");
		return null;
	}
	
	private synchronized Map<String, Double> registerServiceQoS(String serviceId,
			int periodInMillisec,
			ArrayList<ArrayList<String>> equivalentThingServices, boolean assured) {
		LOGTest.debug("Dispatching Start service_id: "+ serviceId);
		this.qosM.sendData("Register service", "info", "QoSM");
		LOG.info("TaaSRM equivalentlist");
		LOG.info(equivalentThingServices);
		Map<String, QoSMEquivalentThingServiceInternal> eqnew_valid = 
			new HashMap<String, QoSMEquivalentThingServiceInternal>();
		read_db(m_equivalents, m_assignments, m_requests, m_assuredrequests, m_thingservices, m_things);

		//Update equivalent thingServices
		int reqId = 0;
		for(ArrayList<String> eqlst : equivalentThingServices){
			Map<String, QoSMEquivalentThingServiceInternal> eqold = actual_equivalent(m_equivalents, serviceId, reqId);
			Map<String, QoSMEquivalentThingServiceInternal> eqnew = new_equivalent(serviceId, reqId, eqold, eqlst);
			for(String key : eqnew.keySet())
			{
				eqnew_valid.put(key, eqnew.get(key));
			}
			Map<String, QoSMEquivalentThingServiceInternal> toadd = to_add(eqold, eqnew, m_thingservices);
			//Map<String, QoSMEquivalentThingServiceInternal> todelete = to_delete(eqold, eqnew);
			Map<String, QoSMEquivalentThingServiceInternal> todelete = new HashMap<String, QoSMEquivalentThingServiceInternal>();
			update_equivalents_db(toadd, todelete, m_equivalents, serviceId, reqId);
			reqId++;
		}
		//Notify QoSM*
		qosM.getQosm_star().updateQoSMEquivalents(serviceId, equivalentThingServices);
		
		Map<String, Pair<Double, Integer>> retMonitor = new HashMap<String, Pair<Double, Integer>>();
		Map<String, Double> ret = new LinkedHashMap<String, Double>();
		//start_assignment=0 -> allocation still feasible, start_assignment=1 -> periodic allocation, 
		//start_assignment=2 -> no feasible allocation
		int start_assignment = 0;
		boolean restart = false;
		if(assured){
			do{
				
				Map<String, QoSMAssignmentInternal> list_ass = list_ass(m_assignments, serviceId);
				
				LOG.debug("ServiceId" + serviceId);
				LOG.debug("List assignment size:" + list_ass.size());
				for(QoSMAssignmentInternal ass : list_ass.values()){
					
					LOG.debug("assignment serviceId:" + ass.getId().getServiceId());
					LOG.debug("assignment requestId:" + ass.getId().getRequestId());
					LOG.debug("assignment thingServiceId:" + ass.getId().getThingServiceId());
					String tsId = ass.getId().getThingServiceId();
					int requestId = ass.getId().getRequestId();
					//LOG.debug(dump_request_internal(service.getAllQoSMRequestInternal()));
					
					try{
						QoSMAssuredRequestInternal request = getAssuredRequest(m_assuredrequests, serviceId, requestId);
						if(request == null)
							throw new IndexOutOfBoundsException();
						
						LOG.debug("Request serviceId:" + request.getId().getServiceId());
						LOG.debug("Request requestId:" + request.getId().getRequestId());
						
						
						if(ts_valid(m_equivalents, eqnew_valid, serviceId, requestId, tsId))
						{
							//TS is still valid
							Double minInter = request.getMinInterRequestTime();
							ret.put(tsId, minInter);
							Map<String, QoSMThingServiceInternal> attachedts = getThingServices(m_equivalents, m_thingservices, serviceId, requestId);
							Map<String, Double> new_ts = new HashMap<String, Double>();
							for(String tsid : attachedts.keySet()){
								QoSMThingServiceInternal ts = attachedts.get(tsid);
								
								if(ts.getResponseTime() <= request.getMaxResponseTime())
								{
									if(m_things.containsKey(ts.getDeviceId())){
										QoSMThingInternal t = m_things.get(ts.getDeviceId()); 
										//double hyperperiod = getActualHyperperiod(service);
										double factor = 1;
										double normalizedcost = factor * (ts.getBatteryCost() / t.getBatteryLevel());
										double tot = t.getBatteryLevel() - normalizedcost;
										new_ts.put(ts.getThingServiceId(), tot);
									}
								}
							
								
							}
							new_ts.remove(tsId);
							ret.putAll(sortByValue(new_ts));
							
						}
						else{
							// No feasible dispatching policy
							start_assignment = 2;
							break;
						}
					} catch (IndexOutOfBoundsException e) {
						LOG.error("Couldn't find Assured Request Internal");
						this.qosM.sendData("Couldn't find Assured Request Internal", "error", "QoSM");
						LOG.error("Request: " + serviceId + ":" + requestId);
					}
					
				}
				if(start_assignment == 2){
					// No feasible dispatching policy
					//start a new assignment task on the QOSM* and wait for new assignment schema
					QoSRankList response = qosM.getQosm_star().assignment(true, this);
					if(response != null){
						restart = true;
						ret.clear();
					}
					else{
						//No feasible allocation found
						return null;
					}
				}
			}while(restart);
			
			LOGTest.debug("Dispatching End service_id: "+ serviceId);
			
			String msg = "Dispatching Rank: [";
			for(String item : ret.keySet()){
				msg += item + ", ";
			}
			msg.substring(0, msg.length()-2);
			msg += "]";
			this.qosM.sendData(msg, "info", "QoSM");
			LOG.info(msg);
			return ret;
		}
		else{
			// If not assured
			do{
				start_assignment = 0;
				restart = false;
				
				Map<String, QoSMAssignmentInternal> list_ass = list_ass(m_assignments, serviceId);

				LOG.debug("ServiceId" + serviceId);
				LOG.debug("List assignment size:" + list_ass.size());
				for(QoSMAssignmentInternal ass : list_ass.values()){

					LOG.debug("assignment serviceId:" + ass.getId().getServiceId());
					LOG.debug("assignment requestId:" + ass.getId().getRequestId());
					LOG.debug("assignment thingServiceId:" + ass.getId().getThingServiceId());
					String tsId = ass.getId().getThingServiceId();
					
					int requestId = ass.getId().getRequestId();
					//LOG.debug(dump_request_internal(service.getAllQoSMRequestInternal()));
					
					QoSMRequestInternal request = getRequest(m_requests, serviceId, requestId);
					if(request == null)
						throw new IndexOutOfBoundsException();
					if(request.getPeriod() == 0)
						request.setPeriod(periodInMillisec);
					
					LOG.debug("Request serviceId:" + request.getId().getServiceId());
					LOG.debug("Request requestId:" + request.getId().getRequestId());

					if(ts_valid(m_equivalents, eqnew_valid, serviceId, requestId, tsId))
					{
						//TS is still valid
						Double minInter = request.getMinInterRequestTime();
						int period = request.getPeriod();
						Pair<Double, Integer> tmp = new ImmutablePair<Double, Integer>(minInter, period);
						retMonitor.put(tsId, tmp);
						ret.put(tsId, minInter);
						Map<String, QoSMThingServiceInternal> attachedts = getThingServices(m_equivalents, m_thingservices, serviceId, requestId);
						Map<String, Double> new_ts = new HashMap<String, Double>();
						for(String tsid : attachedts.keySet()){
							QoSMThingServiceInternal ts = attachedts.get(tsid);
							
							if(ts.getResponseTime() <= request.getMaxResponseTime())
							{
								if(m_things.containsKey(ts.getDeviceId())){
									QoSMThingInternal t = m_things.get(ts.getDeviceId()); 
									//double hyperperiod = getActualHyperperiod(service);
									double factor = 1;
									double normalizedcost = factor * (ts.getBatteryCost() / t.getBatteryLevel());
									double tot = t.getBatteryLevel() - normalizedcost;
									new_ts.put(ts.getThingServiceId(), tot);
								}
							}
						
							
						}
						new_ts.remove(tsId);
						ret.putAll(sortByValue(new_ts));
	
					}
					else
					{
						// Handle the case in which the previous selected thing service is no more available 
						// Re-run the heuristic from the main thread pool
		
						start_assignment = 1;	
						
						//get directly connected tss
						Map<String, QoSMThingServiceInternal> attachedts = getThingServices(m_equivalents, m_thingservices, serviceId, requestId);
						if(attachedts.isEmpty())
						{
							// No feasible dispatching policy
							start_assignment = 2;
							break;
						}
						Double min = Double.POSITIVE_INFINITY;
						String new_ts = null;
						Double period = request.getMinInterRequestTime();
						for(QoSMThingServiceInternal ts : attachedts.values()){
							if(ts.getResponseTime() <= request.getMaxResponseTime())
							{
								if(m_things.containsKey(ts.getDeviceId())){
									QoSMThingInternal t = m_things.get(ts.getDeviceId()); 
									//double hyperperiod = getActualHyperperiod(service);
									double factor = 1;
									double normalizedcost = factor * (ts.getBatteryCost() / t.getBatteryLevel());
									double tot = t.getBatteryLevel() - normalizedcost;
									if(tot < min){
										min = tot;
										new_ts = ts.getThingServiceId();
									}
								}
							}
						}
						if(new_ts == null)
						{
							// No feasible dispatching policy
							start_assignment = 2;
							break;
						}
						ret.put(new_ts, period);
					}
				}
				if(start_assignment == 2){
					// No feasible dispatching policy
					//start a new assignment task on the QOSM* and wait for new assignment schema
					QoSRankList response = qosM.getQosm_star().assignment(true, this);
					if(response != null){
						restart = true;
						ret.clear();
					}
					else{
						//No feasible allocation found
						return null;
					}
				}
			}while(restart);
			
			if(start_assignment == 1){
				//start a new assignment task on the QOSM* without waiting for response
				qosM.getQosm_star().assignment(false, this);
			}
			//QoSMonitor
			for(Entry<String, Pair<Double, Integer>> selts : retMonitor.entrySet()){
				String tsId = selts.getKey().toString();
				LOG.warn("tsID:" + tsId);
				Integer MinInterRequestRate = selts.getValue().getLeft().intValue();
				LOG.warn("minInterRequestTime:" + MinInterRequestRate.intValue());
				Integer period = selts.getValue().getRight();
				LOG.warn("period:" + period.intValue());
				Double tollerateJitterParam = period * this.getJitter().doubleValue();
				LOG.warn("jitter:" + tollerateJitterParam.doubleValue());
				//boolean ret_monitor = qosMonitoring.registerMeasurementSLAMonitoring(tsId, MinInterRequestRate, period);
				boolean ret_monitor = qosMonitoring.registerMeasurementSLAMonitoring(tsId, MinInterRequestRate, period, tollerateJitterParam);
				if (!ret_monitor)
				{
					//TODO handle error monitoring
					return null;
				}
			}
			LOGTest.debug("Dispatching End service_id: "+ serviceId);
			String msg = "Dispatching Rank: [";
			for(String item : ret.keySet()){
				msg += item + ", ";
			}
			msg.substring(0, msg.length()-2);
			msg += "]";
			this.qosM.sendData(msg, "info", "QoSM");
			LOG.info(msg);
			return ret;
		}//END NOT Assured
	}
	
	public synchronized void unregisterServiceQoS(String serviceId) {
		LOG.info("QOSM unregisterServiceQoS - " + serviceId);
		IBigDataDatabaseService service = qosM.getService();
		service.deleteQoSMRequestInternal(serviceId);
		service.deleteQoSMAssuredRequestInternal(serviceId);
		LOG.info("QoSM* unregisterServiceQoS - " + serviceId);
		qosM.getQosm_star().unregisterServiceQoS(serviceId);
		qosM.getQosm_star().assignment(false, this);
		LOG.info("Finish unregisterServiceQoS - " + serviceId);
	}

	private QoSMRequestInternal getRequest(
			Map<String, QoSMRequestInternal> m_requests, String serviceId,
			int requestId) {
		String key = serviceId + ":" + (int)requestId;
		if(m_requests.containsKey(key))
			return m_requests.get(key);
	return null;
	}
	private Double getRequest(
			Map<String, QoSMAssuredRequestInternal> m_requests, String serviceId) {
		for(String key : m_requests.keySet())
		{
			int pos = key.lastIndexOf(":");
			String s = key.substring(0, pos);
			if(s.equals(serviceId))
				return m_requests.get(key).getMinInterRequestTime();
		}
		return null;
	}
	private boolean ts_valid(
			Map<String, QoSMEquivalentThingServiceInternal> m_equivalents,
			Map<String, QoSMEquivalentThingServiceInternal> eqnew, String serviceId, int requestId, String tsId) {
		String key = serviceId + ":" + (int)requestId + ":" + tsId;
		return m_equivalents.containsKey(key) || eqnew.containsKey(key);
	}
	private Map<String, QoSMThingServiceInternal> getThingServices(
			Map<String, QoSMEquivalentThingServiceInternal> m_equivalents,
			Map<String, QoSMThingServiceInternal> m_thingservices,
			String serviceId, int requestId) {
		Map<String, QoSMThingServiceInternal> ris = new HashMap<String, QoSMThingServiceInternal>();
		for(String key : m_equivalents.keySet())
		{
			int pos = key.indexOf(this.delimiter);
			String tmp = key.substring(pos);
			String appId = key.substring(0, pos);
			String[] parts = tmp.split(":");
			String s = appId + parts[0];
			int r = Integer.parseInt(parts[1]);
			String ts = parts[2];
			if(s.equals(serviceId) && r == requestId)
				if(m_thingservices.containsKey(ts))
					ris.put(ts, m_thingservices.get(ts));
		}
		return ris;
	}
	private QoSMAssuredRequestInternal getAssuredRequest(
			Map<String, QoSMAssuredRequestInternal> m_assuredrequests,
			String serviceId, int requestId) {
			String key = serviceId + ":" + (int)requestId;
			if(m_assuredrequests.containsKey(key))
				return m_assuredrequests.get(key);
		return null;
	}
	private Map<String, QoSMAssignmentInternal> list_ass(
			Map<String, QoSMAssignmentInternal> m_assignments, String serviceId) {
		Map<String, QoSMAssignmentInternal> ris = new HashMap<String, QoSMAssignmentInternal>();
		for(String key : m_assignments.keySet()){
			int pos = key.indexOf(this.delimiter);
			String tmp = key.substring(pos);
			String appId = key.substring(0, pos);
			String[] parts = tmp.split(":");
			String s = appId + parts[0];
			if(s.equals(serviceId))
				ris.put(key, m_assignments.get(key));
		}
		return ris;
	}
	private synchronized void update_equivalents_db(
			Map<String, QoSMEquivalentThingServiceInternal> toadd,
			Map<String, QoSMEquivalentThingServiceInternal> todelete,
			Map<String, QoSMEquivalentThingServiceInternal> m_equivalents,
			String serviceId, int reqId) {
		IBigDataDatabaseService service = qosM.getService();
		
		for(String key : todelete.keySet()){
			service.deleteQoSMEquivalentThingServiceInternal(m_equivalents.get(key));
			m_equivalents.remove(key);
		}
		for(String key : toadd.keySet()){
			m_equivalents.put(key, toadd.get(key));
			service.saveQoSMEquivalentThingServiceInternal(m_equivalents.get(key));
		}
	}
	
	private Map<String, QoSMEquivalentThingServiceInternal> to_delete(
			Map<String, QoSMEquivalentThingServiceInternal> eqold,
			Map<String, QoSMEquivalentThingServiceInternal> eqnew) {
		Map<String, QoSMEquivalentThingServiceInternal> ris = new HashMap<String, QoSMEquivalentThingServiceInternal>();
		for(String key : eqold.keySet()){
			if(!eqnew.containsKey(key)){
				ris.put(key, eqold.get(key));
			}
		}
		return ris;
	}
	
	private Map<String, QoSMEquivalentThingServiceInternal> to_add(
			Map<String, QoSMEquivalentThingServiceInternal> eqold,
			Map<String, QoSMEquivalentThingServiceInternal> eqnew, Map<String, QoSMThingServiceInternal> m_thingservices) {
		Map<String, QoSMEquivalentThingServiceInternal> ris = new HashMap<String, QoSMEquivalentThingServiceInternal>();
		for(String key : eqnew.keySet()){
			if(!eqold.containsKey(key)){
				// check if ts is mine
				int pos = key.indexOf(this.delimiter);
				String tmp = key.substring(pos);
				String[] parts = tmp.split(":");
				String tsId = parts[2];
				if(m_thingservices.containsKey(tsId))
					ris.put(key, eqnew.get(key));
			}
		}
		return ris;
	}
	private Map<String, QoSMEquivalentThingServiceInternal> new_equivalent(String serviceId, int requestId,
			Map<String, QoSMEquivalentThingServiceInternal> eqold,
			ArrayList<String> eqlst) {
		Map<String, QoSMEquivalentThingServiceInternal> ris = new HashMap<String, QoSMEquivalentThingServiceInternal>();
		for(String tsId : eqlst)
		{
			String key = serviceId + ":" + (int)requestId + ":" + tsId;
			ris.put(key, new QoSMEquivalentThingServiceInternal(serviceId, requestId, tsId));
		}
		return ris;
	}
	
	private Map<String, QoSMEquivalentThingServiceInternal> actual_equivalent(
			Map<String, QoSMEquivalentThingServiceInternal> m_equivalents,
			String serviceId, int reqId) {
		Map<String, QoSMEquivalentThingServiceInternal> ris = new HashMap<String, QoSMEquivalentThingServiceInternal>();
		for(String key : m_equivalents.keySet())
		{
			int pos = key.indexOf(this.delimiter);
			String tmp = key.substring(pos);
			String appId = key.substring(0, pos);
			String[] parts = tmp.split(":");
			String s = appId + parts[0];
			int r = Integer.parseInt(parts[1]);
			if(s.equals(serviceId) && r == reqId)
				ris.put(key, m_equivalents.get(key));
		}
		return ris;
	}
	private synchronized void read_db(
			Map<String, QoSMEquivalentThingServiceInternal> m_equivalents,
			Map<String, QoSMAssignmentInternal> m_assignments,
			Map<String, QoSMRequestInternal> m_requests,
			Map<String, QoSMAssuredRequestInternal> m_assuredrequests,
			Map<String, QoSMThingServiceInternal> m_thingservices,
			Map<String, QoSMThingInternal> m_things) {
		IBigDataDatabaseService service = qosM.getService();
		List<QoSMEquivalentThingServiceInternal> equivalents = service.getAllEquivalentQoSMThingServiceInternal();
		
		List<QoSMAssignmentInternal> assignments = service.getAllQoSMAssignmentInternal();
		
		List<QoSMRequestInternal> requests = service.getAllQoSMRequestInternal();
		
		List<QoSMAssuredRequestInternal> assuredrequests = service.getAllQoSMAssuredRequestInternal();
		
		List<QoSMThingServiceInternal> thingservices = service.getAllQoSMThingServiceInternal();
		
		List<QoSMThingInternal> things = service.getAllQoSMThingInternal();
		
		
		for(QoSMEquivalentThingServiceInternal eq : equivalents){
			String key = eq.getId().keyString();
			m_equivalents.put(key, eq);
		}
		
		for(QoSMAssignmentInternal ass : assignments){
			String key = ass.getId().keyString();
			m_assignments.put(key, ass);
		}
		
		for(QoSMRequestInternal req : requests){
			String key = req.getId().keyString();
			m_requests.put(key, req);
		}
		
		for(QoSMAssuredRequestInternal req : assuredrequests){
			String key = req.getId().keyString();
			m_assuredrequests.put(key, req);
		}
		
		for(QoSMThingServiceInternal ts : thingservices){
			String key = ts.getId();
			m_thingservices.put(key, ts);
		}
		
		for(QoSMThingInternal t : things){
			String key = t.getDeviceId();
			m_things.put(key, t);
		}
		
	}
	/*private String dump_request_internal(List<QoSMRequestInternal> list) {
		String ret = "";
		int count =1;
		for(QoSMRequestInternal r : list){
			ret += count + "REQUEST: ";
			ret += r.getId().getServiceId() + " - " + r.getId().getRequestId() ;
			ret += "\n\n";
			count++;
		}
		ret += "";
		return ret;
	}*/


	public boolean writeThingsServicesQoS(ArrayList<String> thingServiceList) {
		LOG.info("InternalAPIImpl - WriteThingsServicesQoS");
		this.qosM.sendData(" Add new Thing Services", "info", "QoSM");
		LOG.debug("thingservices:" + thingServiceList);
		HashMap<String, QoSspec> thingServices = new HashMap<String, QoSspec>();
		for(String thingServiceId : thingServiceList){
			Double battery = 0.0;
			LOG.debug("InternalAPIImpl - cm.getContextualMeasurement");
			String data = cm.getContextualMeasurement(thingServiceId);
			if(data == null)
			{
				LOG.error("Context data is not available.");
				this.qosM.sendData("Context data is not available.", "error", "QoSM");
				return false;
			}
			LOG.debug("InternalAPIImpl - ThingSDataObtained");
			ThingsData thing = new ThingsData(data);
			String deviceId = thing.getDeviceID();
			if(deviceId == null || deviceId.equals("null"))
			{
				LOG.error("deviceId is not available.");
				this.qosM.sendData("Device is not available.", "error", "QoSM");
				return false;
			}
			QoSspec spec = new QoSspec();
			if(thing.getBatteryCost() == null || thing.getBatteryCost().equals("null"))
			{
				LOG.error("BatteryCost is not available.");
				this.qosM.sendData("Battery Cost is not available.", "error", "QoSM");
				return false;
			}
			spec.setBatteryCost(Double.parseDouble(thing.getBatteryCost()));
			if(thing.getComputationalCost() == null || thing.getComputationalCost().equals("null"))
			{
				LOG.error("ComputationalCost is not available.");
				this.qosM.sendData("Computational Cost is not available.", "error", "QoSM");
				return false;
			}
			spec.setComputationalCost(Double.parseDouble(thing.getComputationalCost()));
			if(thing.getMaximumResponseTime() == null || thing.getMaximumResponseTime().equals("null"))
			{
				LOG.error("MaximumResponseTime is not available.");
				this.qosM.sendData("Max Response Time is not available.", "error", "QoSM");
				return false;
			}
			// To port in milliseconds
			spec.setResponseTime(Double.parseDouble(thing.getMaximumResponseTime())/1000);
			if(thing.getBatteryLevel() == null || thing.getBatteryLevel().equals("null"))
			{
				LOG.error("BatteryLevel is not available.");
				this.qosM.sendData("Battery Level is not available.", "error", "QoSM");
				return false;
			}
			battery = Double.parseDouble(thing.getBatteryLevel());
			thingServices.put(thingServiceId, spec);
			LOG.debug("InternalAPIImpl - call QoSM* WriteThingsServicesQoS");
			boolean ret = qosM.getQosm_star().writeThingsServicesQoS(deviceId, battery, thingServices, this.getGatewayId());
			LOG.debug("GWId:" + this.getGatewayId());
			LOG.debug("Star GWId:" + qosM.getQosm_star().getGatewayId());
			boolean ret2 = this.writeThingsServicesQoSDB(deviceId, battery, thingServices, this.getGatewayId());
			if(!ret || !ret2)
					return false;			
		}
		return true;
	}
	
	private synchronized boolean writeThingsServicesQoSDB(String deviceId, double battery,
			HashMap<String, QoSspec> thingServices, String gatewayId) {
		LOG.info("InternalAPIImpl - WriteThingsServicesQoSDB");
		LOG.debug("DeviceID:" + deviceId + " GatewayID:" + gatewayId);
		IBigDataDatabaseService service = qosM.getService();
		QoSMThingInternal t = new QoSMThingInternal();
	    t.setBatteryLevel(battery);
	    t.setDeviceId(deviceId);
	    t.setCapacityUsed(0.0);
	    t.setNumass(0);
	    t.setGatewayId(gatewayId);
	    service.saveQoSMThingInternal(t);
	    for(Entry<String, QoSspec> entry : thingServices.entrySet()) {
		    String thingServiceId = entry.getKey();
		    QoSspec value = entry.getValue();
		    QoSMThingServiceInternal ts = new QoSMThingServiceInternal(t.getDeviceId(), thingServiceId,
		    		value.getResponseTime(),
		    		value.getBatteryCost(), value.getComputationalCost());
		    LOG.info(thingServiceId);
		    service.saveQoSMThingServiceInternal(ts);		    
		}

		return true;
	}
	
	
	public synchronized boolean modifyThingsServicesQoS(ArrayList<String> thingServices) {
		LOG.info("InternalAPIImpl - modifyThingsServicesQoS");
		this.qosM.sendData("Delete Thing Services", "info", "QoSM");
		LOG.debug("thingservices: " + thingServices);
		IBigDataDatabaseService service = qosM.getService();
		boolean ret = qosM.getQosm_star().modifyThingsServicesQoS(thingServices, this);
		for(String tsid : thingServices){
			try{
				QoSMThingServiceInternal ts = service.searchQoSMThingServiceInternal(tsid).get(0);
				LOG.debug("thingservice: " + ts.getThingServiceId());
				List<QoSMThingServiceInternal> tslst = service.searchQoSMThingServiceInternalT(ts.getDeviceId());
				if(tslst.size() == 1){
					//remove thing
					QoSMThingInternal t = service.searchQoSMThingInternal(ts.getDeviceId()).get(0);
					LOG.debug("thing: " + t.getDeviceId());
					service.deleteQoSMThingInternal(t);
				}
				service.deleteQoSMThingServiceInternal(ts);
				List<QoSMEquivalentThingServiceInternal> eqlst = service.searchQoSMEquivalentThingServiceInternal(tsid);
				for(QoSMEquivalentThingServiceInternal eq : eqlst){
					LOG.debug("equivalentthingservice: " + eq.getId().getThingServiceId());
					service.deleteQoSMEquivalentThingServiceInternal(eq);
				}
				List<QoSMAssignmentInternal> alst = service.searchQoSMAssignmentInternalTS(tsid);
				for(QoSMAssignmentInternal a : alst){
					LOG.debug("assignment: " + a.getId().getThingServiceId());
					//new assignment schema required
					service.deleteQoSMAssignmentInternal(a);
				}
			} catch(IndexOutOfBoundsException e)
			{
				LOG.warn("Thing service "+ tsid + " already deleted.");
			}
			
		}
		if(!ret)
			return false;
		return true;
	}
	
	public boolean thingRemoved(String thingServiceId){
		ArrayList<String> tmp = new ArrayList<String>();
		tmp.add(thingServiceId);
		return this.modifyThingsServicesQoS(tmp);
	}

	public SLACalculation calculateSLA(
			String selectedThingService) {
		return qosMonitoring.calculateSLA(selectedThingService);
	}
	
	public SLACalculation calculateSLAPush(String sThingServiceName, int iMilisecondTaaSRequestRate){
		return qosMonitoring.calculateSLAPush(sThingServiceName, iMilisecondTaaSRequestRate);
	}
	public SLACalculation failureSLA(String sThingServiceName){
		return qosMonitoring.failureSLA(sThingServiceName);
	}
	
	public boolean getMeasurementSLAMonitoring(String sThingServiceName, String serviceId) {
		try{
			Integer minInter = getRequest(this.m_assuredrequests, serviceId).intValue();
			Double tollerateJitterParam = minInter * this.getJitter().doubleValue();
			return qosMonitoring.getMeasurementSLAMonitoring(sThingServiceName, minInter, tollerateJitterParam);
		} catch(NullPointerException e)
		{
			LOG.error(e.getStackTrace());
			return true;
		}
		//return qosMonitoring.getMeasurementSLAMonitoring(sThingServiceName, minInter);
	}

	public String getGatewayId() {
		return gatewayId;
	}
	public void setGatewayId(String gatewayId) {
		this.gatewayId = gatewayId;
	}

	public synchronized void update_db(QoSRankResults ret) {
		LOGTest.debug("DB Start - update_db:1");
		IBigDataDatabaseService service = qosM.getService();
		if(ret.getThings() != null){
			LOG.debug("Things size:" + ret.getThings().size());
			for(QoSMThingInternal t : ret.getThings())
			{
				service.saveQoSMThingInternal(t);
			}
		}
		if(ret.getEquivalents() != null){
			LOG.debug("EquivalentThings size:" + ret.getEquivalents().size());
			for(QoSMEquivalentThingServiceInternal e : ret.getEquivalents()){
				service.saveQoSMEquivalentThingServiceInternal(e);
			}
		}
		if(ret.getAssignments() != null){
			LOG.debug("Assignment size:" + ret.getAssignments().size());
			for(QoSMAssignmentInternal a : ret.getAssignments()){
				service.saveQoSMAssignmentInternal(a);
			}
		}
		LOGTest.debug("DB End - update_db:1");
	}
	
	public synchronized Map<String, Double> getBatteryLevels() {
		Map<String, Double> ret = new HashMap<String, Double>();
		LOGTest.debug("DB Start - getBatteryLevels:1");
		IBigDataDatabaseService service = qosM.getService();
		List<QoSMThingServiceInternal> tss = service.getAllQoSMThingServiceInternal();
		LOGTest.debug("DB End - getBatteryLevels:1");
		for(QoSMThingServiceInternal ts : tss)
		{
			LOGTest.debug("CM Start - getBatteryLevels:1");
			LOG.info("Battery Level GW: " + this.gatewayId + "TS: " + ts.getThingServiceId());
			String data = cm.getContextualMeasurement(ts.getThingServiceId());
			LOGTest.debug("CM End - getBatteryLevels:1");
			ThingsData thing = new ThingsData(data);
			String deviceId = thing.getDeviceID();
			if(deviceId == null || thing.getBatteryLevel() == null || 
					deviceId.equals("null") || thing.getBatteryLevel().equals("null") )
			{
				LOG.error("CM is not available.");
				this.qosM.sendData("CM is not available.", "error", "QoSM");
				LOG.info("Use previous data.");
				try{
					QoSMThingInternal t = service.searchQoSMThingInternal(ts.getDeviceId()).get(0);
					ret.put(ts.getDeviceId(), t.getBatteryLevel());
				} catch(IndexOutOfBoundsException e)
				{
					LOG.error("Thing is not available.");
					this.qosM.sendData("Thing is not available.", "error", "QoSM");
				}
			}
			else{
				double batteryLevel = Double.parseDouble(thing.getBatteryLevel());
				ret.put(deviceId, batteryLevel);
			}
		}
		
		return ret;
	}
	@Deprecated
	public ArrayList<SLACalculation> calculateSLA(
			ArrayList<String> selectedThingService) {
		return null;
	}
	
	@Deprecated
	public boolean getMeasurementSLAMonitoring(
			ArrayList<String> sThingServiceName) {
		return false;
	}
	@Deprecated
	public boolean getMeasurementSLAMonitoring(String sThingServiceName,
			int sMilisecondMinInterRequestRate) {
		return false;
	}
	
	public synchronized void reachable(String tsid) {
		IBigDataDatabaseService service = qosM.getService();
		QoSMThingServiceInternal ts = service.searchQoSMThingServiceInternal(tsid).get(0);
		if(ts.getReachable() != true){
			ts.setReachable(true);
			service.saveQoSMThingServiceInternal(ts);
			qosM.getQosm_star().reachable(tsid);
			qosM.getQosm_star().assignment(false, this);
		}
		LOG.info("QoSM - End reachable");
	}
	public void unreachable(String tsid) {
		LOG.info("QoSM - Start unreachable, tsid" + tsid);
		IBigDataDatabaseService service = qosM.getService();
		QoSMThingServiceInternal ts = service.searchQoSMThingServiceInternal(tsid).get(0);
		if(ts.getReachable() != false){
			ts.setReachable(false);
			service.saveQoSMThingServiceInternal(ts);
			qosM.getQosm_star().unreachable(tsid);
			qosM.getQosm_star().assignment(false, this);
		}
		LOG.info("QoSM - End unreachable");
	}

	public Double getJitter() {
		return jitter;
	}
	public void setJitter(Double qosMonitoringJitter) {
		this.jitter = qosMonitoringJitter;
	}
	
	private static Map<String, Double> sortByValue(Map<String, Double> unsortMap) {	 
		List<Entry<String, Double>> list = new LinkedList<Entry<String, Double>>();
		while(unsortMap.size() > 0){
			String max_key = null;
			Double max_value = 0.0;
			Entry<String, Double> to_add = null;
			for( Entry<String, Double> k : unsortMap.entrySet())
			{
				if(k.getValue()>max_value){
					max_value = k.getValue();
					max_key = k.getKey();
					to_add = k;
				}
			}
			list.add(to_add);
			unsortMap.remove(max_key);
		}
			
		
		/*Collections.sort(list, new Comparator<Entry<String, Double>>() {
			

			public int compare(Entry<String, Double> o1,
					Entry<String, Double> o2) {
				return ((Entry<String, Double>) (o1)).getValue()
						.compareTo(((Entry<String, Double>) (o2)).getValue());
			}
		});
	 */
		Map<String, Double> sortedMap = new LinkedHashMap<String, Double>();
		for (Iterator<Entry<String, Double>> it = list.iterator(); it.hasNext();) {
			Entry<String, Double> item = it.next();
			sortedMap.put(item.getKey(), item.getValue());
		}
		return sortedMap;
	}

}
