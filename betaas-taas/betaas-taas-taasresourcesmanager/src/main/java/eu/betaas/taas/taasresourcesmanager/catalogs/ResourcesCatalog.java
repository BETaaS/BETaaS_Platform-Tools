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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.log4j.Logger;

import eu.betaas.taas.taasresourcesmanager.api.ResourceInfo;


/**
* 
* @author Francisco Javier Nieto De-Santos (francisco.nieto@atos.net)
* @author Sergio Garcia Villalonga (sergio.garciavillalonga@atos.net)
*/
public class ResourcesCatalog 
{

	private HashMap<String, Resource> myCatalog;
	private Logger logger= Logger.getLogger("betaas.taas");
	static private ResourcesCatalog _instance = null;	
	
	private ResourcesCatalog ()
	{
		myCatalog = new HashMap<String, Resource> ();
		initCatalog();
	}
	
	private void initCatalog ()
	{		
		// Access the VM Manager for adding virtual resources (VMs)
	}
	
	public static synchronized ResourcesCatalog instance() 
	{
		if (null == _instance) 
		{
			_instance = new ResourcesCatalog();			
			Logger myLogger= Logger.getLogger("betaas.taas");
			myLogger.info("A new instance of the Resources Catalog was created!");
		}
		return _instance;
	}
	
	public Resource getResource (String resourceId)
	{
		return myCatalog.get(resourceId);
	}
		
	public boolean addResource (Resource theResource)
	{		
		// Add the resource to the catalog		
		myCatalog.put(theResource.getResourceId(), theResource);
		logger.debug("Resource added: " + theResource.getResourceId());
		return true;
	}
	
	public boolean removeResource (String resourceId)
	{
		// Check whether the resource exists
		if (!myCatalog.containsKey(resourceId))
		{
			logger.error("The Resource Id to be removed was not found! " + resourceId);
			return false;
		}
		
		// Remove the resource from the catalog		
		myCatalog.remove(resourceId);
		
		return true;
	}
	
	public boolean removeResources ()
	{
		myCatalog.clear();
		return true;
	}
	
	public boolean removeResources (String location)
	{
		myCatalog.clear();
		return true;
	}
	
	public ArrayList<ResourceInfo> getResourcesForSynchronizing()
	{
		logger.debug("Preparing list of resources for synchronization...");
		// Check if the catalog is empty
		if (myCatalog.isEmpty())
		{
			logger.warn("Our catalog is empty, we will send a null list!");
			return null;
		}
		
		// Catalog not empty, prepare list
		ArrayList<ResourceInfo> resList = new ArrayList<ResourceInfo> ();
		Collection<Resource> myCollection = myCatalog.values();
		Iterator<Resource> myIter = myCollection.iterator();
		while (myIter.hasNext())
		{
			Resource current = (Resource) myIter.next();
			// Only send those Resources belonging to the current Gateway (in order to avoid races issues if broadcast is ongoing)
			if (current.getGatewayId().equalsIgnoreCase("localhost"))
			{
				resList.add(new ResourceInfo(current.getResourceId(), current.getPhysicalResourceId(), current.getResourceType(), current.getStatus()));
			}			
		}
		
		return resList;
	}
	
	public ArrayList<String> getResourcesList()
	{
		return new ArrayList<String>(myCatalog.keySet());
	}
}
