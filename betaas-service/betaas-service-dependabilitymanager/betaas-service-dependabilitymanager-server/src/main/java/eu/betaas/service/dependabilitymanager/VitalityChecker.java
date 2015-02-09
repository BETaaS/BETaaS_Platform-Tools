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

package eu.betaas.service.dependabilitymanager;

import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.Hashtable;

import org.apache.log4j.Logger;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

/**
 * It performs periodical vitality checks and fills in the report.
 * It also try to perform recovery actions to start up missing components
 * @author Intecs
 */
public class VitalityChecker extends Thread {

	private final static int CHECK_PERIOD_SEC = 120;
	
	public VitalityChecker() {
		mBundleStatus = new Hashtable<String, Boolean>();
	}
	
	public void run() {
		String name;
		Boolean active;
		
		setRunning(true);
		mReport = new VitalityReport();

		BundleContext context = DependabilityManager.getInstance().getContext();
		if (context == null) {
			mLogger.error("Null context. Cannot start vitality checker");
			return;
		}
		
		mLogger.info("Vitality checker started");
		
		while (isRunning()) {
			//TODO: check vitality of each component and put the result in mReport
			mLogger.info("Performing vitality check");
			
			Bundle bundles[] = context.getBundles();
			if (bundles != null) {
				//System.out.println(bundles.length + " bundles found.");
				for (int i=0; i < bundles.length; i++) {
					if (bundles[i] != null) {
						name = bundles[i].getSymbolicName();
						if (name.contains("betaas")) {
							name = name.substring(name.indexOf("betaas"));
							if (bundles[i].getState() != Bundle.ACTIVE) {
								active = false;
							} else {
								active = true;
							}
							
							mBundleStatus.put(name, active);
						}
					}
				}
			}
			
			//System.out.println(mBundleStatus);
			
			//TODO: try to recovery missing components (enable this by config). Stop trying after n (=1?) times
			
			try {
				for (int t=0; t < CHECK_PERIOD_SEC; t++) {
					if (!isRunning()) break;
					sleep(1000);
				}
			} catch (InterruptedException e) {
				return;
			}
		}
	}
	
	/**
	 * Start/stop the thread
	 * @param run
	 */
	public synchronized void setRunning(boolean run) {
		mIsRunning = run;
	}
	
	public synchronized boolean isRunning() {
		return mIsRunning;
	}
	
	/**
	 * @param gwId
	 * @return the vitality report based on the currently available info
	 */
	public synchronized VitalityReport getVitalityReport(String gwId) {
		
		if (mReport == null) return null;
		
		GregorianCalendar gc = new GregorianCalendar();
		gc.setTime(new Date());

		mReport.generator = "DM from GW " + gwId;
		mReport.generationDate = format(gc);
		
		
		return mReport;
	}
	
	
	private String format(GregorianCalendar cal) {
		return cal.get(Calendar.YEAR) + "-" +
				String.format("%02d",(cal.get(Calendar.MONTH)+1)) + "-" +
				String.format("%02d",cal.get(Calendar.DAY_OF_MONTH)) + " " +
				String.format("%02d",cal.get(Calendar.HOUR_OF_DAY)) + ":" +
				String.format("%02d",cal.get(Calendar.MINUTE));
	}
	
	
	/**
	 * Add or update a row in the report
	 */
	private synchronized void setReportRow(String component, String status) {
		int n = mReport.dataList.size();
		for (int i=0; i < n; i++) {
			if (mReport.dataList.get(i).component.equals(component)) {
				// update the existing row
				mReport.dataList.get(i).status = status;
				return;
			}
		}
		// add a row
		VitalityReportRow row = new VitalityReportRow();
		row.component = component;
		row.status = status;
		mReport.dataList.add(row);
	}
	
	
	/** Logger */
	private static Logger mLogger = Logger.getLogger(DependabilityManager.LOGGER_NAME);

	private boolean mIsRunning = false;
	
	private Hashtable<String, Boolean> mBundleStatus;
	
	private VitalityReport mReport;
}
