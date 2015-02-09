package eu.betaas.adaptation.securitymanager.service;

public interface ITokenService {
	
	public byte[] createToken(String resourceId);

}
