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

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

public class Configuration {

	public final static String CONFIG_FILE_NAME = "IntrusionDetection.cfg";

	/** Configuration property key */
	public final static String PROP_KEY_IS_INSTALLED = "IS_INSTALLED";
	
	/** Configuration property key */
	public final static String PROP_KEY_LOG_FILE_NAME = "LOG_FILE_NAME";
	
	/** Configuration property key */
	public final static String PROP_KEY_BETAAS_WSDL = "WSDL";
	
	/** Configuration property key */
	public final static String PROP_KEY_APP_ID = "APP_ID";
	
	/** Configuration property key */
	public final static String PROP_KEY_N_SERVICES = "N_SERVICES";
	
	/** Configuration property key */
	public final static String PROP_KEY_SERVICE_ID_PREFIX = "SERVICE_ID_";
	
	/** Configuration property key */
	public final static String PROP_KEY_SERVICE_TOKEN_PREFIX = "SERVICE_TOKEN_";
	
	/** Configuration property key */
	public final static String PROP_KEY_DATA_PULL_PERIOD_SEC = "PULL_SECONDS";
	
	/** Application properties */
	public Properties mAppProps;
	
	public Configuration() {
		mAppProps = null;
	}
	
	public void saveConfig() throws Exception {
		FileOutputStream outputStream = new FileOutputStream(mFileName);
		mAppProps.store(outputStream, null);
	}
		
	public String getProperty(String key) {
		if (mAppProps != null) return mAppProps.getProperty(key);
		
		return null;
	}
	
	/**
	 * Loads the web app configuration
	 * @param path from where configuration must be loaded 
	 * @throws Exception
	 */
	public void loadProperties(String path) throws Exception {
		mAppProps = new Properties();
		FileInputStream in;
		String filename = path;
		try {
			in = new FileInputStream(filename);
			mAppProps.load(in);
			in.close();
			mFileName = filename;
		} catch (Exception e) {
			mFileName = null;
			mAppProps = null;
			throw new Exception("Cannot load configuration from " + filename + ". " + e.getMessage());
		}
	}
	
	/** Name with path of the property file */
	private String mFileName;
	
}
