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

import eu.betaas.taas.taasresourcesmanager.api.Location;

public class FeatureService 
{
	public static final int RTPULL = 0;
	public static final int NRTPULL = 1;
	public static final int RTPUSH = 2;
	public static final int NRTPUSH = 3;
	public static final int PUT = 4;
	
	private String featureCovered;
	private Location location;
	private int accessType;
	private ArrayList<String> idThingServices;
	private String operation;
	private ArrayList<ArrayList<String>> equivalentThingServices;
	private boolean completeMapping;
	private String featureServiceId;
	private int period;
	private float requiredTrust;
	
	public FeatureService (String feature, Location theLocation, int type, String operator, String featureId, int period)
	{
		featureCovered = feature;
		location = theLocation;
		accessType = type;
		operation = operator;
		completeMapping = false;
		featureServiceId = featureId;
		this.period = period;
	}
	
	public void setThingServices (ArrayList<String> idThingServicesSelected)
	{		
		if (idThingServicesSelected == null || idThingServicesSelected.size()<=0)
		{
			completeMapping = false;
		}
		idThingServices = idThingServicesSelected;
	}
	
	public void setEquivalentThingServices (ArrayList<ArrayList<String>> equivalents)
	{
		equivalentThingServices = equivalents;
		Iterator<ArrayList<String>> myIter = equivalents.iterator();
		while (myIter.hasNext())
		{
			ArrayList<String> current = myIter.next();
			if (current == null || current.size()<=0)
			{
				completeMapping = false;
				return;
			}
		}
		
		completeMapping = true;
	}
	
	public String getFeature ()
	{
		return featureCovered;
	}
	
	public Location getLocation()
	{
		return location;
	}
	
	public int getType()
	{
		return accessType;
	}
	
	public ArrayList<String> getThingServices ()
	{
		return idThingServices;
	}
	
	public ArrayList<ArrayList<String>> getEquivalents ()
	{
		return equivalentThingServices;
	}
	
	public String getOperator()
	{
		return operation;
	}
	
	public void setOperator (String newOperator)
	{
		operation = newOperator;
	}
	
	public boolean isMappingCompleted ()
	{
		return completeMapping;
	}
	
	public String getFeatureServiceId ()
	{
		return featureServiceId;
	}
	
	public void setPeriod (int thePeriod)
	{
		period = thePeriod;
	}
	
	public int getPeriod ()
	{
		return period;
	}
	
	public float getRequiredTrust()
	{
		return requiredTrust;
	}
}
