package eu.betaas.taas.qosmanager.api.impl;

import java.util.Set;

import eu.betaas.taas.bigdatamanager.database.hibernate.data.QoSMAssuredRequestStar;
import eu.betaas.taas.bigdatamanager.database.hibernate.data.QoSMEquivalentThingServiceStar;
import eu.betaas.taas.bigdatamanager.database.hibernate.data.QoSMThingStar;

class PS{
	private Set<QoSMEquivalentThingServiceStar> eq;
	private Double maxComputational = Double.NEGATIVE_INFINITY;
	private Double minTime = Double.POSITIVE_INFINITY;
	private Double minMaxResonseTime = Double.POSITIVE_INFINITY;
	private Double tis;
	private Double cis;
	private QoSMThingStar thing;
	private QoSMEquivalentThingServiceStar MaxThingService;
	private QoSMAssuredRequestStar request;
	
	public Set<QoSMEquivalentThingServiceStar> getEq() {
		return eq;
	}

	public void setEq(Set<QoSMEquivalentThingServiceStar> eq) {
		this.eq = eq;
	}

	public Double getMaxComputational() {
		return maxComputational;
	}

	public void setMaxComputational(Double maxComputational) {
		this.maxComputational = maxComputational;
	}

	public Double getMinTime() {
		return minTime;
	}

	public void setMinTime(Double minTime) {
		this.minTime = minTime;
	}
	
	public QoSMThingStar getThing(){
		return thing;
	}
	
	public void setThing(QoSMThingStar thing){
		this.thing = thing;
	}

	public QoSMEquivalentThingServiceStar getMaxThingService() {
		return MaxThingService;
	}

	public void setMaxThingService(QoSMEquivalentThingServiceStar maxThingService) {
		MaxThingService = maxThingService;
	}

	public Double getTis() {
		return tis;
	}

	public void setTis(Double tis) {
		this.tis = tis;
	}

	public Double getCis() {
		return cis;
	}

	public void setCis(Double cis) {
		this.cis = cis;
	}

	public Double getMinMaxResonseTime() {
		return minMaxResonseTime;
	}

	public void setMinMaxResonseTime(Double minMaxResonseTime) {
		this.minMaxResonseTime = minMaxResonseTime;
	}

	public QoSMAssuredRequestStar getRequest() {
		return request;
	}

	public void setRequest(QoSMAssuredRequestStar request) {
		this.request = request;
	}
	
}
