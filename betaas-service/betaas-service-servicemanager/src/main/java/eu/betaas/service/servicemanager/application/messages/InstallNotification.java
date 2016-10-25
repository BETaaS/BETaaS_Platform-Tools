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

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlRootElement(name = "InstallNotification")
@XmlAccessorType (XmlAccessType.FIELD)
public class InstallNotification {

	public InstallNotification() {
	}
	
	public int getInstallSuccess() {
		return installSuccess;
	}

	public void setInstallSuccess(int installSuccess) {
		this.installSuccess = installSuccess;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getAppID() {
		return appID;
	}

	public void setAppID(String appID) {
		this.appID = appID;
	}

	public List<ServiceInstallation> getServiceList() {
		return serviceList;
	}

	public void setServiceList(List<ServiceInstallation> serviceList) {
		this.serviceList = serviceList;
	}
	
	public String toString() {
		String content = "<InstallNotification>" + "<message>"
				+ getMessage() + "</message>" + "<installSuccess>"
				+ getInstallSuccess() + "</installSuccess>" + "<appID>"
				+ getAppID() + "</appID>";
		
		if (getServiceList() != null) {
			for (int i = 0; i < getServiceList().size(); i++) {
				content += "<ServiceInstallation>";
				content += "<serviceID>" + getServiceList().get(i).getServiceID() + "</serviceID>";
				content += "<token>" + getServiceList().get(i).getToken() + "</token>";
				content += "</ServiceInstallation>";
			}
		}

		content += "</InstallNotification>";

		return content;
	}

	/** Text message describing the result (e.g. the error cause) */
	private String message;

	/** 1 on success, 0 on error */
	private int installSuccess;
	
	/** The ID assigned to the application in case of success */
	private String appID;

	/** The list of the identifiers of the allocated services */
	@XmlElement(name = "ServiceInstallation")
	private List<ServiceInstallation> serviceList;
}
