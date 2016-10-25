package eu.betaas.service.bigdatamanager.dataservice.database.services.impl;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import eu.betaas.service.bigdatamanager.dataservice.IDatasourceBDMInterface;

public class DBMDatabaseServiceManager {

	private ServiceRegistration registration;
	private BundleContext context; 
	
	private boolean enabledbus=false;
	private String jdbc_driver;
	private String jdbc_url;
	private String user;
	private String pwd;
	private String db_name;
	private String setup;
	private String id_instance;
	private int queue_max=50;
	private int queue_cur=0;
	private boolean enabled=false;
	private String address;
	private Logger logger;
	private String gateway;
	
	public void activateService(){
		logger = Logger.getLogger("betaas.service");
		if (!enabled){
			logger.info("BDM Database service not enabled in this Gateway service layer");
			return;
		}
		logger.info("BDM Database service is enabled in this Gateway service layer");
		Dictionary<String,String> props = new Hashtable<String,String>();
		 
        props.put("service.exported.interfaces", "*");
        props.put("service.exported.configs", "org.apache.cxf.ws");
        props.put("org.apache.cxf.ws.address", address);
         
        DatabaseBDMService bdmservice = new DatabaseBDMService();
        bdmservice.setContext(context);
        bdmservice.setDb(db_name);
        bdmservice.setDBSetup(setup);
        bdmservice.setEnabledbus(enabledbus);
        bdmservice.setJdbc(jdbc_url);
        bdmservice.setDrivers(jdbc_driver);
        bdmservice.setUser(user);
        bdmservice.setPwd(pwd);
        bdmservice.setGateway(gateway);
        bdmservice.setEnabled(enabled);
        bdmservice.setupService();
        
        registration = context.registerService(IDatasourceBDMInterface.class.getName(), bdmservice, props);
		
	}
	
	public void unregisterService(){
		if (registration!=null)registration.unregister();
	}
	public void setJdbc(String jdbc) {
		this.jdbc_url=jdbc;
	}

	public void setDrivers(String drivers) {
		this.jdbc_driver=drivers;
	}

	public void setUser(String user) {
		this.user=user;		
	}

	public void setPwd(String pwd) {
		this.pwd=pwd;		
	}
		
	public String getId_instance() {
		return id_instance;
	}

	public void setId_instance(String id_instance) {
		this.id_instance = id_instance;
	}

	public int getQueue_max() {
		return queue_max;
	}

	public void setQueue_max(int queue_max) {
		this.queue_max = queue_max;
	}

	public int getQueue_cur() {
		return queue_cur;
	}

	public void setQueue_cur(int queue_cur) {
		this.queue_cur = queue_cur;
	}

	public boolean isEnabledbus() {
		return enabledbus;
	}

	public void setEnabledbus(boolean enabledbus) {
		this.enabledbus = enabledbus;
	}

	public BundleContext getContext() {
		return context;
	}

	public void setContext(BundleContext context) {
		this.context = context;
	}
	public void setMode(String mode) {
		if (mode=="off")this.enabled=false;
	}

	public void setDb(String db) {
		this.db_name=db;
	}

	public void setDBSetup(String setup) {
		this.setup=setup;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getGateway() {
		return gateway;
	}

	public void setGateway(String gateway) {
		this.gateway = gateway;
	}
	
}
