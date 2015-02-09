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

package eu.betaas.service.instancemanager;

import org.osgi.framework.BundleContext;

/**
 * This interfaces provides the OSGi context to whoever needs it. It is
 * used to avoid leaking the context in different classes
 * @author Intecs
 */
public interface ContextProvider {

	public BundleContext getBundleContext();
}
