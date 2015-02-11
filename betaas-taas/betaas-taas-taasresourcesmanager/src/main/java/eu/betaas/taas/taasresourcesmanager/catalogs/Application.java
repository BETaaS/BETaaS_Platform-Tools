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

package eu.betaas.taas.taasresourcesmanager.catalogs;

import java.util.ArrayList;
import java.util.Iterator;

public class Application 
{
	public static final int CREATED = 0;
	public static final int READY = 1;
	public static final int NOTREADY = 2;
	public static final int RUNNING = 3;
	public static final int FAILURE = 4;
		
	private ArrayList<FeatureService> featuresList;
	private String idApplication;
	private int status;
	private double trustLevel;
	private boolean qosFulfilled;
	private boolean trustFulfilled;
	
	public Application (String idApplication, double requiredTrust)
	{
		status = 0;
		this.idApplication = idApplication;
		trustLevel = requiredTrust;
		trustFulfilled = false;
		qosFulfilled = false;
		featuresList = new ArrayList<FeatureService>();
	}
		
	public String getApplicationId ()
	{
		return idApplication;
	}
	
	public int getStatus ()
	{
		return status;
	}
	
	public ArrayList<FeatureService> getFeatures ()
	{
		return featuresList;
	}
	
	public boolean isQoSFulfilled()
	{
		return qosFulfilled;
	}
	
	public boolean isTrustFulfilled()
	{
		return trustFulfilled;
	}
	
	public boolean hasCompleteMapping()
	{
		Iterator<FeatureService> myIterator = featuresList.iterator();
		
		while (myIterator.hasNext())
		{
			FeatureService actual = (FeatureService) myIterator.next();			
			if (!actual.isMappingCompleted())
			{
				// If any of the features has no thing service mapped, return false
				return false;
			}
		}
		
		//if (status==0) status = 1;
		return true;
	}
	
	public void addFeature (FeatureService newFeature)
	{
		featuresList.add(newFeature);
	}
	
	public boolean mapThingService (ArrayList<String> idThingServices, String feature, ArrayList<ArrayList<String>> equivalents)
	{
		Iterator<FeatureService> myIterator = featuresList.iterator();
		
		while (myIterator.hasNext())
		{
			FeatureService actual = (FeatureService) myIterator.next();
			if (actual.getFeature().equalsIgnoreCase(feature))
			{
				// Modify the Thing Services mapped to the feature
				actual.setThingServices(idThingServices);
				actual.setEquivalentThingServices(equivalents);
				return true;
			}
		}
		
		return false;
	}
	
	public void updateTrust(double newTrust)
	{
		if (newTrust<trustLevel)
		{			
			trustFulfilled = false;
			switch (status)
			{
			case READY:
				status = NOTREADY;
				break;
			case RUNNING:
				status = FAILURE;
			}
		}
		else 
		{
			trustFulfilled = true;
		
			if (qosFulfilled)		
			{						
				switch (status)
				{
				case NOTREADY:
					status = READY;
					break;
				case FAILURE:
					status = RUNNING;
				}
			}
		}
	}
	
	public void updateQoS (boolean newQoS)
	{
		qosFulfilled = newQoS;
		if (qosFulfilled==false)
		{
			switch (status)
			{
			case READY:
				status = NOTREADY;
				break;
			case RUNNING:
				status = FAILURE;
			}
			
		}
		else if(trustFulfilled)
		{
			switch (status)
			{
			case NOTREADY:
				status = READY;
				break;
			case FAILURE:
				status = RUNNING;
			}			
		}
	}
	
	public void changeStatus (int newStatus)
	{
		status = newStatus;
	}	
	
}
