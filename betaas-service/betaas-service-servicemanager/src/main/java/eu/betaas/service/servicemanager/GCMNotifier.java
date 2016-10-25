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

package eu.betaas.service.servicemanager;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.HashMap;

//import org.apache.http.HttpEntity;
//import org.apache.http.client.methods.CloseableHttpResponse;
//import org.apache.http.client.methods.HttpPost;
//import org.apache.http.entity.StringEntity;
//import org.apache.http.impl.client.CloseableHttpClient;
//import org.apache.http.impl.client.HttpClients;
//import org.apache.http.message.BasicHeader;
//import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class GCMNotifier extends Thread {
	
	public final static long MINIMUM_GCM_FAILURE_TIME_MSEC = 30000L;

	public GCMNotifier(String notification, String GCMId) {
		mNotification = notification;
		mGCMId = GCMId;
	}
	
	
	public void run() {
		/*
		// Check whether a notification has been sent for the same GCMId with a failure in the recent past.
		// If so skip this notification.
		if (mGCMFailures.containsKey(mGCMId)) {
			long lastFailure = mGCMFailures.get(mGCMId).longValue();
			Date now = new Date();
			if (now.getTime() - lastFailure < MINIMUM_GCM_FAILURE_TIME_MSEC) {
				mLogger.debug("Skipping GCM notification to wait at least " + (MINIMUM_GCM_FAILURE_TIME_MSEC/1000.0f) + "s");
				return;
			}
		}	

		BasicHeader header;
		String line;
		CloseableHttpClient httpclient = HttpClients.createDefault();
		try {
			HttpPost httpPost = new HttpPost(
					"https://android.googleapis.com/gcm/send");
			header = new BasicHeader("Content-Type", "application/json");

			// Header
			httpPost.setHeader(header);
			header = new BasicHeader("Authorization", "key=" + ServiceManager.getInstance().getGcmKey());
			httpPost.addHeader(header);

			// Entity
			StringEntity entity = new StringEntity(
					"{\"data\": \"" + mNotification + "\", " + 
			         "\"registration_ids\": [\"" + mGCMId + "\"]}");
			httpPost.setEntity(entity);

			mLogger.debug("Sending the HTTP post to GCM");
			CloseableHttpResponse response = httpclient.execute(httpPost);

			try {
				mLogger.debug("Response: ");
				mLogger.debug(response.getStatusLine());
				HttpEntity entityResp = response.getEntity();

				String content = "";
				BufferedReader isr = new BufferedReader(new InputStreamReader(entityResp.getContent()));
				while ((line = isr.readLine()) != null) {
					mLogger.debug(line);
					content += line;
				}
				mLogger.debug("----- END OF RESPONSE -----");
				
				try {
					JsonElement jelement = new JsonParser().parse(content);
					JsonObject jobject = jelement.getAsJsonObject();
					jelement = jobject.get("success");
					if (jelement.getAsInt() != 1) {
						mLogger.debug("GCM replied with success");
						if (mGCMFailures.containsKey(mGCMId)) {
							mGCMFailures.remove(mGCMId);
						}
					} else {
						mLogger.debug("GCM replied with failure");
						Date now = new Date();
						mGCMFailures.put(mGCMId, now.getTime());
					}
				} catch (Exception e) {
					mLogger.debug("Error message from GCM");
					Date now = new Date();
					mGCMFailures.put(mGCMId, now.getTime());
				}

				EntityUtils.consume(entityResp);
				
			} finally {
				response.close();
			}
		} catch (Exception e) {
			mLogger.error("Exception: " + e.getMessage());
		} finally {
			try { httpclient.close(); } catch (Exception e) { mLogger.error("Cannot close HTTP client"); }
		}
		*/
	}
	
	/** Logger */
	private static Logger mLogger = Logger.getLogger(ServiceManager.LOGGER_NAME);

	private String mNotification;

	private String mGCMId;

	/** It keeps track of failures on GCM notifications to apps. For each GCM Id for which a
	 * notification failed, it stores the date in milliseconds of the last failure. It is used to
	 * avoid sending further GCM notifications until enough time has passed. */
	private HashMap<String, Long> mGCMFailures = new HashMap<String, Long>();

}
