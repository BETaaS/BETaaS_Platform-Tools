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

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class Logger {

	public final static String DEFAULT_LOG_FILE_NAME = "IntrusionDetection.log";

	/**
	 * Constructor
	 * @param path where the log must be written
	 */
	public Logger(String path) {
		mStream = null;
		mFileName = path;
	}
	
	public void setFileName(String fileName) {
		mFileName = fileName;
	}
	
	/** Open the log file */
	public void open() throws Exception {
		try {
			FileOutputStream fos = new FileOutputStream(mFileName, true);
			mStream = new OutputStreamWriter(fos, "UTF-8");
		} catch (Exception e) {
			mStream = null;
			throw new Exception("IntrusionDetection cannot open log file: " + mFileName + ". " + e.getMessage());
		}
	}
	
	/** Write an error in the log file
	 * @param msg to be written
	 */
	public void logerr(String msg) {
		log(msg, "ERR");
	}
	
	/** Write info in the log file
	 * @param msg to be written
	 */
	public void loginfo(String msg) {
		log(msg, "INF");
	}
	
	private synchronized void log(String msg, String type) {
		if (mStream != null) {
			GregorianCalendar gc = new GregorianCalendar();
			gc.setTime(new Date());
			try {
				mStream.write(gc.get(Calendar.YEAR) + "-"
						+ String.format("%02d", gc.get(Calendar.MONTH) + 1) + "-"
						+ String.format("%02d", gc.get(Calendar.DAY_OF_MONTH)) + " "
						+ String.format("%02d", gc.get(Calendar.HOUR_OF_DAY)) + ":"
						+ String.format("%02d", gc.get(Calendar.MINUTE)) + ":"
						+ String.format("%02d", gc.get(Calendar.SECOND)) + 
						" " + type + ": " + msg + "\n");
				mStream.flush();
			} catch (IOException e) {
				System.out.println("IntrusionDetection cannot write to log file: " + mFileName);
				try {
					mStream.close();
				} catch (IOException e1) {}
				mStream = null;
			}
		}		
	}
	
	/** The output stream */
	private OutputStreamWriter mStream;
	
	/** The output file name, to be set before opening the log */
	private String mFileName;

}
