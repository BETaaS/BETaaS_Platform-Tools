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

// BETaaS - Building the Environment for the Things as a Service
//
// Component: <component>
// Responsible: Intecs

package eu.betaas.service.instancemanager.config;

import java.io.FileInputStream;
import java.net.InetAddress;
import java.util.Properties;

import org.apache.log4j.Logger;

import eu.betaas.service.instancemanager.config.InstanceManager;

/**
 * This class is used to manage the configuration of a NON-STAR Instance Manager
 * @author Intecs
 */
public class Configuration {
	
	/**
	 * Constructor
	 */
	public Configuration() {
		mGW = null;
	}
	
	/** Logger */
	private static Logger mLogger = Logger.getLogger(eu.betaas.service.instancemanager.InstanceManager.LOGGER_NAME);	

	/** The gateway where this IM is installed */
	public Gateway mGW;
	
	/**
	 * Test method
	 * @param args
	 */
	public static void main(String[] args) {
	}
	
	/** Configuration file key */
	private final static String KEY_GW_ID = "GW_ID";
	
	/** Configuration file key */
	private final static String KEY_IM_IS_STAR = "IM_IS_STAR";
	
	/** Configuration file Credential key (for the join procedure) */
	private final static String KEY_IM_CREDENTIALS = "IM_CREDENTIALS";
	
	/** Configuration file Description key */
	private final static String KEY_IM_DESCRIPTION = "IM_DESCRIPTION";
}
