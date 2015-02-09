package eu.betaas.taas.securitymanager.requirements.service;

import java.util.List;
import java.util.Map;


public interface IThingsRequirementsService {
	/**
	 * Rank the list of thing services based on the provided security specs
	 * @param eqThingServices
	 * @param securitySpecs
	 * @return
	 */
	public List<List<String>> getSecurityRank(
			List<List<String>> eqThingServices, Map<String, String> securitySpecs);
}
