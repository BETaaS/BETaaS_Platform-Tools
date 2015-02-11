/**

Copyright 2014 ATOS SPAIN S.A.

Licensed under the Apache License, Version 2.0 (the License);
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

Authors :
Francisco Javier Nieto. Atos Research and Innovation, Atos SPAIN SA
@email francisco.nieto@atos.net 

**/

// BETaaS - Building the Environment for the Things as a Service
//
// Component: TaaSResourceManager
// Responsible: Atos

package eu.betaas.taas.taasresourcesmanager.catalogs;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.log4j.Logger;

/**
* 
* @author Francisco Javier Nieto De-Santos (francisco.nieto@atos.net)
* @author Sergio Garcia Villalonga (sergio.garciavillalonga@atos.net)
*/
public class ApplicationsCatalog 
{
	private HashMap<String, Application> myCatalog;
	private HashMap<String, String> featureServicesCatalog;
	private Logger logger= Logger.getLogger("betaas.taas");
	static private ApplicationsCatalog _instance = null;
	
	private ApplicationsCatalog ()
	{
		myCatalog = new HashMap<String, Application> ();
		featureServicesCatalog = new HashMap<String, String> ();
		initCatalog();
	}
	
	private void initCatalog ()
	{
		// Check the Big Data Manager for existing services which were running before correctly
		
		
	}
	
	public static synchronized ApplicationsCatalog instance() 
	{
		if (null == _instance) 
		{
			_instance = new ApplicationsCatalog();			
			Logger myLogger= Logger.getLogger("betaas.taas");
			myLogger.info("A new instance of the Applications Catalog was created!");
		}
		return _instance;
	}
	
	public Application getApplication (String appId)
	{
		return myCatalog.get(appId);
	}
	
	public Application addApplication (String appId)
	{		
		// Add the application to the catalog
		Application newApp = new Application (appId, 2.5);
		myCatalog.put(appId, newApp);
		
		return newApp;
	}
	
	public boolean removeApplication (String appId)
	{
		// Check whether the service exists
		if (!myCatalog.containsKey(appId))
		{
			logger.error("The Application Id to be removed was not found! " + appId);
			return false;
		}
		
		// Remove the application from the catalog		
		myCatalog.remove(appId);
		
		// Remove the application and all its features associated
		
		return true;
	}
	
	public String getServiceFromFeatureId (String featureServiceId)
	{
		return featureServicesCatalog.get(featureServiceId);
	}
	
	public void storeCatalog()
	{
		Collection<Application> myCollection = myCatalog.values();
		Iterator<Application> myIter = myCollection.iterator();
		while (myIter.hasNext())
		{
			Application current = (Application) myIter.next();
			if (current.getStatus()==Application.RUNNING)
			{
				// Store the data about the service
			}
		}
		
	}
	
	public void addFeatureService (String appId, String featureServId)
	{
		featureServicesCatalog.put(featureServId, appId);
	}
}
