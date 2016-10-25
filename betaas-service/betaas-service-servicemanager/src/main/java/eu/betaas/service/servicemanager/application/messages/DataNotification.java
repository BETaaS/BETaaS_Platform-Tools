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

package eu.betaas.service.servicemanager.application.messages;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class DataNotification {

	public DataNotification() {
	}
	
	public String getServiceID() {
		return serviceID;
	}

	public void setServiceID(String serviceID) {
		this.serviceID = serviceID;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}
	
	public String toString() {
		String result =
		 "<DataNotification>"+
			 "<serviceID>" + getServiceID() + "</serviceID>"+
			 "<data>" + getData() + "</data>" +
		 "</DataNotification>";
		
		return result;
	}

	/** The ID of the service whose data is being notified */
	private String serviceID;

	/** Data being notified */
	private String data;
}
