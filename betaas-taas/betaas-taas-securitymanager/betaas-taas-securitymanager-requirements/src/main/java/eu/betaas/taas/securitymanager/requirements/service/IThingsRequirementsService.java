package eu.betaas.taas.securitymanager.requirements.service;

import java.util.List;
import java.util.Map;

public interface IThingsRequirementsService {
	public List<List<String>> getSecurityRank(
			List<List<String>> eqThingServices, Map<String, String> securitySpecs);
}
