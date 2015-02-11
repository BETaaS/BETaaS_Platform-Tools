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

package eu.betaas.service.extendedservice.api.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Vector;

/**
 * It represents a user and his/her info on the applied traffic fees 
 */
public class User {

	public User(String id) {
		mId = id;
		mFeeHistory = new Vector<FeePeriod>();
		mTotal = 0.0f;
		mCurrentFee = 0.0f;
	}
	
	public String getId() {
		return mId;
	}
	
	// starts a period during which no fee is applied.
	// such period is not stored in the history
	public void setNoFee() {
		if (mFeeHistory.size() > 0) {
			Date now = new Date();
			mFeeHistory.get(mFeeHistory.size()-1).setEndDate(now);
		}
		mCurrentFee = 0.0f;
		mCurrentClass = 0;
	}
	
	
	public void setCurrentFee(int feeClass) {
		
		if (mFeeHistory.size() > 0) {
			// check if last fee period has no end date
			FeePeriod lastFeePeriod = mFeeHistory.get(mFeeHistory.size()-1);
			
//			if (lastFeePeriod.mFeeEndDate != null) {
//				// just add a new period
//				mTotal += lastFeePeriod.getDuration() * LEZProcessor.getFee(lastFeePeriod.mFeeClass);
//
//				Date now = new Date();
//				FeePeriod period = new FeePeriod(now, feeClass);
//				mFeeHistory.add(period);
//				
//			} else 
				if (feeClass != lastFeePeriod.mFeeClass) {
				// start a new fee period as the last one has a different amount
				// Set the end of the last period
				Date now = new Date();
				lastFeePeriod.setEndDate(now);

				mTotal += lastFeePeriod.getDuration() * LEZProcessor.getFee(lastFeePeriod.mFeeClass);
				
				// start a new one
				FeePeriod period = new FeePeriod(now, feeClass);
				mFeeHistory.add(period);
			} // else the last stored period is still valid
			
		} else {

			Date now = new Date();
			// start the first period
			FeePeriod period = new FeePeriod(now, feeClass);
			mFeeHistory.add(period);		
		}

		mCurrentFee = LEZProcessor.getFee(feeClass);
		mCurrentClass = feeClass;
	}
	
	
	/**
	 * Format the list of fee periods and return it
	 * @return a printable list of fee periods
	 */
	public String getHistory() {
		String result;
		GregorianCalendar gc = new GregorianCalendar();
		
		if (mFeeHistory.size() == 0) result = "No fee to be payed";
		else {
			gc.setTime(mFeeHistory.get(0).mFeeStartDate);
			result = "Fees history\n" +
					 "from " + format(gc) + "\n\n";
			for (int i=0; i < mFeeHistory.size(); i++) {
				gc.setTime(mFeeHistory.get(i).mFeeStartDate);				
				result += "From " + format(gc) + " to ";
				if (mFeeHistory.get(i).mFeeEndDate != null) {
				  gc.setTime(mFeeHistory.get(i).mFeeEndDate);
  				result += format(gc);
				}  else {
				  result += "-";
				}
				result += "\n";
				if (mFeeHistory.get(i).hasDefaultFee()) result += "(Unreliable traffic data)\n";
				result += "Fee = " + LEZProcessor.getFee(mFeeHistory.get(i).mFeeClass) + " euro/h";
				result += "--- --- ---\n";
			}
		}
		
		return result;
	}
	
	public int getHistoryLength() {
	  if (mFeeHistory == null) return 0;
	  return mFeeHistory.size();
	}
	
	
	public String getInfo() {
		String result = "";
		
		float currPeriodFee = 0.0f;
		if ((mFeeHistory.size() > 0) &&
			(!mFeeHistory.get(mFeeHistory.size()-1).hasFinished())) {
			
			currPeriodFee = mFeeHistory.get(mFeeHistory.size()-1).getDuration() * LEZProcessor.getFee(mCurrentClass);
		}
		
		float tot = mTotal + currPeriodFee;
		result += mCurrentFee + ";" + mCurrentClass + ";" + ((int)tot) + "." + (int)((tot*100) % 100);
		
		return result;
	}
	
	public String format(GregorianCalendar cal) {
		return cal.get(Calendar.YEAR) + "-" +
				String.format("%02d",(cal.get(Calendar.MONTH)+1)) + "-" +
				String.format("%02d",cal.get(Calendar.DAY_OF_MONTH)) + " " +
				String.format("%02d",cal.get(Calendar.HOUR_OF_DAY)) + ":" +
				String.format("%02d",cal.get(Calendar.MINUTE));
				//+ ":" + String.format("%02d",cal.get(Calendar.SECOND));
	}
	
	/**
	 * It represents a period with the same fee to be applied
	 */
	private class FeePeriod {

		public FeePeriod(Date start, int feeClass) {
			mFeeStartDate = start;
			mFeeEndDate = null;
			mFeeClass = feeClass;
		}
		
		public void setEndDate(Date end) {
			mFeeEndDate = end;
		}
		
		public boolean hasDefaultFee() {
			return (mFeeClass == LEZProcessor.DEFAULT_FEE_CLASS);
		}
		
		public boolean hasFinished() {
			return (mFeeEndDate != null);
		}
		
		// returns the length of this period in hours
		public float getDuration() {
			float length;
			
			if (mFeeStartDate == null) return 0.0f;
			
			if (mFeeEndDate != null) {
				length = ((float)mFeeEndDate.getTime() - (float)mFeeStartDate.getTime()) / 3600000.0f;
			} else {
				Date now = new Date();
				length = ((float)now.getTime() - (float)mFeeStartDate.getTime()) / 3600000.0f;
			}
			
			if (length < 0) return 0.0f;
			return length;
		}
		
		private Date mFeeStartDate;
		private Date mFeeEndDate;
		private int mFeeClass;		
	}
	
	private String mId;
	private Vector<FeePeriod> mFeeHistory;
	
	private float mCurrentFee;
	private int mCurrentClass;
	private float mTotal;
}
