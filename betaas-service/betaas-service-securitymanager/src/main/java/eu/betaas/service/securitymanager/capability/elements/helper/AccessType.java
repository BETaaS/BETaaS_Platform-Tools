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
 */

package eu.betaas.service.securitymanager.capability.elements.helper;

/**
 * 
 * This class lists the different access modes
 * 
 * @author Bayu Anggorojati [ba@es.aau.dk]
 * Center for TeleInFrastruktur, Aalborg University, Denmark
 *
 */
public class AccessType {
	// general GET mode
	public static final String GET = "GET";
	public static final String REALTIME_PULL = "PULL_RT";
	public static final String NONREALTIME_PULL = "PULL_NRT";
	public static final String REALTIME_PUSH = "PUSH_RT";
	public static final String NONREALTIME_PUSH = "PUSH_NRT";
	public static final String SET = "PUT";
	public static final String UPDATE = "POST";
	public static final String DELETE = "DELETE";
//	GET, POST, PUT, DELETE
}
