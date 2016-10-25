package eu.betaas.taas.securitymanager.encrypttest.api;

public interface IAddStringService {
	public String helloName(String name, String senderGwId);
	public String concatenateString(String one, String two, String senderGwId);
	public String suffleString(String one, String two);
}
