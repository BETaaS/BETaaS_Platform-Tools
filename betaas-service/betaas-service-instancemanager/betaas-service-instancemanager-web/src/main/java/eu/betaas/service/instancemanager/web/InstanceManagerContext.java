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

package eu.betaas.service.instancemanager.web;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.xml.namespace.QName;

/**
 * Used to initialize and destroy the Web application
 * @author Intecs
 */
public class InstanceManagerContext implements ServletContextListener {

	public static final QName SERVICE_NAME = new QName("http://api.instancemanager.service.betaas.eu/", "InstanceManagerExternalIF");
	
	/** Context attribute for log file */
	public final static String CONTEXT_ATTR_LOG = "LOG";
	
	/** Context attribute for app properties*/
	public final static String CONTEXT_ATTR_CONFIG = "CONFIG";
	
	/** Application configuration */
	public Configuration mConfig;
	
	public void contextInitialized(ServletContextEvent event) {
		mContext = event.getServletContext();
		
		mLog = new Logger(mContext.getRealPath(Logger.DEFAULT_LOG_FILE_NAME));
		mConfig = new Configuration();
		
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
		
		mLog.loginfo("InstanceManager (Web) started");
	}

	public void contextDestroyed(ServletContextEvent event) {
		mContext = null;
	}
	
	/** The Web App context */
	private ServletContext mContext;
	
	/** The logger */
	private Logger mLog;
}
