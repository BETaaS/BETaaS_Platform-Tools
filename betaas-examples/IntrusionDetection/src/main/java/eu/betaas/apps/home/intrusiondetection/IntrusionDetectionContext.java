/*
Copyright 2014-2015 Intecs Spa

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

package eu.betaas.apps.home.intrusiondetection;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.xml.namespace.QName;

/**
 * Used to initialize and destroy the Web application
 * @author Intecs
 */
public class IntrusionDetectionContext implements ServletContextListener {

	public final static String MANIFEST_FILE_NAME = "http://localhost:8080/IntrusionDetection/manifest.xml";

    public static final QName SERVICE_NAME = new QName("http://api.servicemanager.service.betaas.eu/", "ServiceManagerExternalIF");
	
	/** Context attribute for log file */
	public final static String CONTEXT_ATTR_LOG = "LOG";
	
	/** Context attribute for app properties*/
	public final static String CONTEXT_ATTR_CONFIG = "CONFIG";
	
	/** Context attribute for installation info */
	public final static String CONTEXT_ATTR_INST_INFO = "CONTEXT_ATTR_INST_INFO";
	
	/** Context attribute for presence info */
	public final static String CONTEXT_ATTR_PRESENCE_INFO = "PRESENCE_INFO";
	
	/** Application configuration */
	public Configuration mConfig;
	
	public void contextInitialized(ServletContextEvent event) {
		mContext = event.getServletContext();
		
		mLog = new Logger(mContext.getRealPath(Logger.DEFAULT_LOG_FILE_NAME));
		mConfig = new Configuration();
		mInstallationInfo = new InstallationInfo();
		mPresenceInfo = new PresenceInfo();
		
		try {
			mConfig.loadProperties(mContext.getRealPath(Configuration.CONFIG_FILE_NAME));
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		try {
			String name = mConfig.getProperty(Configuration.PROP_KEY_LOG_FILE_NAME);
			if (name != null) mLog.setFileName(mContext.getRealPath(name));
			mLog.open();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			mLog = null;
		}
		
		mContext.setAttribute(CONTEXT_ATTR_LOG, mLog);
		mContext.setAttribute(CONTEXT_ATTR_CONFIG, mConfig);
		mContext.setAttribute(CONTEXT_ATTR_INST_INFO, mInstallationInfo);
		mContext.setAttribute(CONTEXT_ATTR_PRESENCE_INFO, mPresenceInfo);
		
		mLog.loginfo("IntrusionDetection started");
		
		if ((mConfig.getProperty(Configuration.PROP_KEY_IS_INSTALLED) != null) && 
		    (mConfig.getProperty(Configuration.PROP_KEY_IS_INSTALLED).equals("1"))) {
			mLog.loginfo("IntrusionDetection is already installed");
			mLog.loginfo("Starting the processing");
			IntrusionDetectionThread th = new IntrusionDetectionThread(mContext);
			th.start();			
		} else {
			mLog.loginfo("Starting installation of application");
			mLog.loginfo("WSDL: " + mConfig.getProperty(Configuration.PROP_KEY_BETAAS_WSDL));

			InstallThread inst = new InstallThread(mContext,
					                               mConfig.getProperty(Configuration.PROP_KEY_BETAAS_WSDL));
			inst.start();
		}
	}

	public void contextDestroyed(ServletContextEvent event) {
		mContext = null;
	}
	
	/** The Web App context */
	private ServletContext mContext;
	
	/** The logger */
	private Logger mLog;
	
	/** Info on received presence data */
	private PresenceInfo mPresenceInfo;
	
	/** The installation info received from BETaaS */
	private InstallationInfo mInstallationInfo;
}
