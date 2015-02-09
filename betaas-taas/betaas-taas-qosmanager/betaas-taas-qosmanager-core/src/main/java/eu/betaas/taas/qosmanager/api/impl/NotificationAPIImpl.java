package eu.betaas.taas.qosmanager.api.impl;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import eu.betaas.taas.bigdatamanager.database.hibernate.data.QoSMAssignmentInternal;
import eu.betaas.taas.bigdatamanager.database.hibernate.data.QoSMEquivalentThingServiceInternal;
import eu.betaas.taas.bigdatamanager.database.hibernate.data.QoSMThingInternal;
import eu.betaas.taas.bigdatamanager.database.hibernate.data.QoSMThingServiceInternal;
import eu.betaas.taas.bigdatamanager.database.service.IBigDataDatabaseService;
import eu.betaas.taas.bigdatamanager.database.service.ThingsData;
import eu.betaas.taas.contextmanager.api.ThingsServiceManager;
import eu.betaas.taas.qosmanager.api.QoSManagerInternalIF;
import eu.betaas.taas.qosmanager.api.QoSManagerNotificationIF;
import eu.betaas.taas.qosmanager.api.QoSRankResults;
import eu.betaas.taas.qosmanager.core.QoSManager;

public class NotificationAPIImpl implements QoSManagerNotificationIF, Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private static Logger LOG = Logger.getLogger("betaas.taas");
	
	private static Logger LOGTest = Logger.getLogger("betaas.testplan");
	
	private String gatewayId;
	private QoSManager qosM;
	private ThingsServiceManager cm;
	private QoSManagerInternalIF qosManagerInetrnal;
	
	public NotificationAPIImpl(QoSManager qosm, String gwId, ThingsServiceManager cm, 
			QoSManagerInternalIF qosManagerInetrnal){
		this.qosM = qosm;
		this.gatewayId = gwId;
		this.cm = cm;
		this.qosManagerInetrnal = qosManagerInetrnal;
	}
	
	public void putQoSRank(QoSRankResults qoSRankResults) {
		LOG.info("InternalAPIImpl - putQoSRank");
		if(qoSRankResults.isFeas())
		{
			//Update Local DB
			qosManagerInetrnal.update_db(qoSRankResults);
		}
	}
	
	
	public Map<String, Double> getBatteryLevels() {
		
		return qosManagerInetrnal.getBatteryLevels();
	}

	public String getGatewayId() {
		return gatewayId;
	}
	public void setGatewayId(String gatewayId) {
		this.gatewayId = gatewayId;
	}

}
