/**

Copyright 2014 ATOS SPAIN S.A.

Licensed under the Apache License, Version 2.0 (the License);
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

Authors :
Sergio García Villalonga. Atos Research and Innovation, Atos SPAIN SA
@email sergio.garciavillalonga@atos.net
**/

package eu.betaas.betaasandroidapp.configuration;

import java.net.*;
import java.util.*;   
import org.apache.http.conn.util.InetAddressUtils;

public class Configuration {
	private static Configuration configuration = null;
	
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
	public final static String PROP_KEY_DATA_PULL_PERIOD_SEC = "PULL_SECONDS";
	
	private String deviceIP;
	
	public static Configuration getInstance() {
		if (configuration == null) {
			configuration = new Configuration();
		}
		
		return configuration;
	}
	
	private Configuration() {
		setDeviceIp();
	}
	
	private void setDeviceIp() {
		try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress().toUpperCase();
                        boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr); 
                        if (isIPv4) {
                        	deviceIP = sAddr;
                        }
                    }
                }
            }
        } catch (Exception ex) {
        	deviceIP = null;
        }
	}
	
	public String getIP() {
		return deviceIP;
	}
}
