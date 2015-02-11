/**
* Copyright 2014-2015 Converge ICT
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*
*/

package eu.betaas.adaptation.thingsadaptor.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.log4j.Logger;

public class ContextUtils {

	private static Logger mLogger = Logger.getLogger("betaas.adaptation");
	
	public static HashMap<String, String> readSensorFile(HashMap<String, String> sensorHash, final File sensorFile) {
		String line = "";
		try {

			InputStream file =new FileInputStream(sensorFile);
			BufferedReader in = new BufferedReader(new InputStreamReader(file));
			mLogger.info("Going to read data from the file...:"+sensorFile.getName());			
			
			while ((line = in.readLine()) != null) {
				String[] attributes = line.split(":");	
				String value = "";
				if(attributes.length>1){
					value = attributes[1];
				}
                                if (!sensorHash.containsKey(attributes[0])) {
				  sensorHash.put(attributes[0], value);
                                }
			}
			
			in.close();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return sensorHash;
	}
	
}
