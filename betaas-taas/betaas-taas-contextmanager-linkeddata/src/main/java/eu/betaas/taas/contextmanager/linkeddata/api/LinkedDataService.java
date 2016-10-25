package eu.betaas.taas.contextmanager.linkeddata.api;

import javax.ws.rs.core.Response;

public interface LinkedDataService {
	public Response getCatalog();
	public Response getDataset(String id);
	public Response getThings();
}
