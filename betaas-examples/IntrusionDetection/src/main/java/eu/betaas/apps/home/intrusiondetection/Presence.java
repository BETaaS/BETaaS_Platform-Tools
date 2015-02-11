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

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Presence {

	public String getmStartDate() {
		return mStartDate;
	}

	public void setmStartDate(String mStartDate) {
		this.mStartDate = mStartDate;
	}

	public boolean ismLastStatus() {
		return mLastStatus;
	}

	public void setmLastStatus(boolean mLastStatus) {
		this.mLastStatus = mLastStatus;
	}

	public String getmLastDate() {
		return mLastDate;
	}

	public void setmLastDate(String mLastDate) {
		this.mLastDate = mLastDate;
	}

	public String getmLastPresence() {
		return mLastPresence;
	}

	public void setmLastPresence(String mLastPresence) {
		this.mLastPresence = mLastPresence;
	}
	
	public boolean ismConnected() {
		return mConnected;
	}

	public void setmConnected(boolean mConnected) {
		this.mConnected = mConnected;
	}

	/** App start date */
	private String mStartDate;
	
	/** Connection status */
	private boolean mConnected;
	
	/** Last received presence data */
	private boolean mLastStatus;
	
	/** Date of last data reception */
	private String mLastDate;
	
	/** Date of last presence (true) notification received */
	private String mLastPresence;
}
