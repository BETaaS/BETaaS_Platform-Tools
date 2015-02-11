/**
Copyright 2014-2015 Center for TeleInFrastruktur (CTIF), Aalborg University

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

@author Bayu Anggorojati [ba@es.aau.dk]
Center for TeleInFrastruktur, Aalborg University, Denmark
 */

package eu.betaas.taas.securitymanager.common.mqv;

public class EcmqvMessage {

	private byte[] ephemeralPublicX;
	private byte[] ephemeralPublicY;
	private byte[] myCertificate;
	private byte[] myMac;
	
	public EcmqvMessage(){}
	
	public byte[] getEphemeralPublicX() {
		return ephemeralPublicX;
	}
	
	public void setEphemeralPublicX(byte[] ephemeralPublic) {
		this.ephemeralPublicX = ephemeralPublic;
	}
	
	public byte[] getEphemeralPublicY() {
		return ephemeralPublicY;
	}

	public void setEphemeralPublicY(byte[] ephemeralPublicY) {
		this.ephemeralPublicY = ephemeralPublicY;
	}

	public byte[] getMyCertificate() {
		return myCertificate;
	}
	
	public void setMyCertificate(byte[] myCertificate) {
		this.myCertificate = myCertificate;
	}
	
	public byte[] getMyMac() {
		return myMac;
	}
	
	public void setMyMac(byte[] myMac) {
		this.myMac = myMac;
	}
}
