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

import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.servlet.ServletContext;

/** 
 * Used to store received presence data and other info to be provided to the app user
 * @author Intecs
 */
public class PresenceInfo {

	/**
	 * Constructor
	 */
	public PresenceInfo() {
		mConnected = false;
		mStartDate = null;
		mLastStatus = false;
		mLastDate = null;
		mLastPresence = null;
	}
	
	/**
	 * Sets the app start date
	 * @param when
	 */
	public synchronized void setStartDate(GregorianCalendar when) {
		mStartDate = when;
	}
		
	public synchronized boolean ismConnected() {
		return mConnected;
	}

	public synchronized void setmConnected(boolean mConnected) {
		this.mConnected = mConnected;
	}

	/** 
	 * Manage received presence data
	 * @param presence
	 */
	public synchronized void setPresence(boolean presence) {
		mLastStatus = presence;
		if (mLastDate == null) {
			mLastDate = new GregorianCalendar();
		}
		mLastDate.setTime(Calendar.getInstance().getTime());
		
		if (presence) {
			if (mLastPresence == null) {
				mLastPresence = new GregorianCalendar();
			}
			mLastPresence = new GregorianCalendar();
			mLastPresence.setTime(Calendar.getInstance().getTime());
		}
	}

	public synchronized String getStartDate() {
		if (mStartDate == null) return "";
		
		String res = format(mStartDate);
		
		return res;
	}
	
	public synchronized boolean getLastStatus() {
		return mLastStatus;
	}
	
	public synchronized String getLastDate() {
		if (mLastDate == null) return "";
		
		String res = format(mLastDate);
		
		return res;
	}
	
	public synchronized String getLastPresence() {
		if (mLastPresence == null) return "";
		
		String res = format(mLastPresence);
		
		return res;
	}
	
	private String format(GregorianCalendar gc) {
		String res = String.format(gc.get(Calendar.YEAR) + "-"
				+ String.format("%02d", gc.get(Calendar.MONTH) + 1) + "-"
				+ String.format("%02d", gc.get(Calendar.DAY_OF_MONTH)) + " "
				+ String.format("%02d", gc.get(Calendar.HOUR_OF_DAY)) + ":"
				+ String.format("%02d", gc.get(Calendar.MINUTE)) + ":"
				+ String.format("%02d", gc.get(Calendar.SECOND)));
		
		return res;
	}

	/** App start date */
	private GregorianCalendar mStartDate;
	
	/** Last received presence data */
	private boolean mLastStatus;
	
	/** Date of last data reception */
	private GregorianCalendar mLastDate;
	
	/** Date of last presence (true) notification received */
	private GregorianCalendar mLastPresence;
	
	/** True when the application is receiving data from BETaaS without problems */
	private boolean mConnected;
}
