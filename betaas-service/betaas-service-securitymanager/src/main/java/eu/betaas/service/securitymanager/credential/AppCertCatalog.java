/**
Copyright 2014-2015 Center for TeleInFrastruktur (CTIF), Aalborg University

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

package eu.betaas.service.securitymanager.credential;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.bouncycastle.cert.X509CertificateHolder;

/**
 * This class holds the application certificate for each of the application 
 * installed in the instance
 * 
 * @author Bayu Anggorojati [ba@es.aau.dk]
 * Center for TeleInFrastruktur, Aalborg University, Denmark
 *
 */
public class AppCertCatalog {
private Logger logger= Logger.getLogger("betaas.service");
	
	private Map<String, X509CertificateHolder> appCertCatalog;
	static private AppCertCatalog _instance = null;
	
	public static AppCertCatalog instance(){
		if(_instance == null){
			_instance = new AppCertCatalog();
			Logger myLogger= Logger.getLogger("betaas.service");
			myLogger.info("A new instance of the Application Certificate Catalog was created!");
		}
		return _instance;
	}
	
	public AppCertCatalog(){
		appCertCatalog = new HashMap<String, X509CertificateHolder>();
	}
	
	public X509CertificateHolder getAppCertCatalog(String appId){
		if(!appCertCatalog.containsKey(appId)){
			logger.error("No certificate associated with application ID: "+appId);
			return null;
		}
		return appCertCatalog.get(appId);
	}
	
	public boolean addAppCert(String appId, X509CertificateHolder appCert){
		this.appCertCatalog.put(appId, appCert);
		logger.info("Application Certificate added: " + appId);
		return true;
	}
	
	public boolean removeAppCert(String appId){
		if(!appCertCatalog.containsKey(appId)){
			logger.error("The Application Certificate with ID: "+appId +" to be removed is not found!!");
			return false;
		}
		
		appCertCatalog.remove(appId);
		return true;
	}
}
