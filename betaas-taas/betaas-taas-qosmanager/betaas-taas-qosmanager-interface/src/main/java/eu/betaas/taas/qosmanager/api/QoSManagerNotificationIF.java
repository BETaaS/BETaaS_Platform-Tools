package eu.betaas.taas.qosmanager.api;

import java.util.Map;

public interface QoSManagerNotificationIF {
	
	public String getGatewayId();
	
	public void setGatewayId(String gatewayId);
	
	public void putQoSRank(QoSRankResults qoSRankResults);

	public Map<String, Double> getBatteryLevels();
}
