package eu.betaas.adaptation.simulator.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.log4j.Logger;
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

public class FileUtils {
	
	private static Logger mLogger = Logger.getLogger("betaas.thingsadaptor");
	
	
	public static HashMap<String, String> readSensorFile(HashMap<String, String> sensorHash, final File sensorFile, Integer counter) {
		List<String> lines = new ArrayList<String>();
		String headers = "";
		String line = "";
		try {

			InputStream file =new FileInputStream(sensorFile);
			BufferedReader in = new BufferedReader(new InputStreamReader(file));
			mLogger.debug("Going to read data from the file...:"+sensorFile.getName());			
			if((line = in.readLine()) != null){
				headers = line;
			}
			while ((line = in.readLine()) != null) {
				lines.add(line);				
			}
			
			if (counter != null) {
				mLogger.debug("counter : " + counter);
				if(counter >= lines.size()){				    
					final int x = counter%lines.size();
					mLogger.debug("x : " + x);
					sensorHash = readLineDataInArray(headers,lines.get(x), sensorHash);
				} else {
					int x = counter;
					mLogger.debug("x : " + x);
					sensorHash = readLineDataInArray(headers,lines.get(x), sensorHash);
				}
			}
			
			in.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sensorHash;
	}
	
	public static void writeSensorFile(HashMap<String, String> sensorHash, final File sensorFile, final String value, Integer counter) {
		List<String> lines = new ArrayList<String>();
		String headers = "";
		String line = "";
		try {

			InputStream file =new FileInputStream(sensorFile);
			BufferedReader in = new BufferedReader(new InputStreamReader(file));
			mLogger.debug("Going to read data from the file...:"+sensorFile.getName());		
			if((line = in.readLine()) != null){
				headers = line;
			}
			while ((line = in.readLine()) != null) {
				lines.add(line);				
			}
			
			if (counter != null) {
				counter++;
				if(counter >= lines.size()){				    
					final int x = counter%lines.size();
					lines.set(x, writeLineDataInArray(headers, sensorHash, value));
				} else {
					int x = counter;
					lines.set(x, writeLineDataInArray(headers, sensorHash, value));
				}
			}
			
			FileWriter fw = new FileWriter(sensorFile);	
			//StringBuilder sb = new StringBuilder();
			lines.add(0, headers);
	        for (String li : lines)
	        {
	        	fw.append(li);
	        	fw.append("\n");
	        }
			fw.close();
			in.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static HashMap<String, String> readLineDataInArray(String headers, String line, HashMap<String, String> sensorHash){

		try {
			final String[] tempRead = line.split(",", -1);
			final String[] header = headers.split(",");
			if (tempRead.length > 1) {
				for (int i = 0; i < header.length; i++) {
					sensorHash.put(header[i], tempRead[i]);
				}
			}
		} catch (Exception e) {

			mLogger.error("Problem Reading through the Sensor file", e);
		}

		return sensorHash;
	}
	
	private static String writeLineDataInArray(String headers, HashMap<String, String> sensorHash, String value){

		String line = "";
		try {			
			final String[] header = headers.split(",", -1);
			mLogger.info(headers);
			for (String s : header) {
				mLogger.info(s + " : " + sensorHash.get(s));
				if (!s.equalsIgnoreCase("measurement")) {
					if(sensorHash.get(s) != null){
						line = line + "," + sensorHash.get(s);
					}
					else{
						line = "," + line;
					}
				}else{
					line = line + "," + value;
				}
			}
			line = line.replaceFirst(",", "");
			
			mLogger.error(line);
		} catch (Exception e) {

			mLogger.error("Problem writing to line of file",e);
		}
		
		return line;
	}

}
