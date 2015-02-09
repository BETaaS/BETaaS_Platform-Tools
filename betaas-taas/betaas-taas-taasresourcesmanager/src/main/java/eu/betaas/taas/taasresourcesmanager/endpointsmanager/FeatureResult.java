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

package eu.betaas.taas.taasresourcesmanager.endpointsmanager;

import java.util.ArrayList;
import java.util.Iterator;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import eu.betaas.taas.taasresourcesmanager.api.ThingServiceResult;

public class FeatureResult 
{		
		private ArrayList<ThingServiceResult> dataList;		
		private String operator;		
		private String featureId;			
		
		public FeatureResult (String theFeature, String theOperator)
		{
			featureId = theFeature;
			operator = theOperator;
			dataList = new ArrayList<ThingServiceResult>();
		}
		
		public FeatureResult (String theFeature)
		{
			featureId = theFeature;	
			dataList = new ArrayList<ThingServiceResult>();
		}
		
		public void setOperator (String theOperator)
		{
			operator = theOperator;
		}
		
		public void addTSResult (ThingServiceResult newResult)
		{
			dataList.add(newResult);
		}
		
		public ArrayList<String> getArrayData()
		{
			ArrayList<String> results = new ArrayList<String> ();
			
			// Iterate all the results
			Iterator<ThingServiceResult> myIterator = dataList.iterator();			
			while (myIterator.hasNext())
			{
				results.add(myIterator.next().getMeasurement());
			}
			
			// Add the operator
			results.add(operator);
			
			return results;
		}
		
		public JsonObject getData() 
		{			
			//Conversion to JSON
			Gson gson = new Gson();		
		    JsonObject jsonResult = new JsonObject();
		    jsonResult.add("ServiceResult", gson.toJsonTree(this));
		
		    return jsonResult;
		}
		
		public String getFeatureId()
		{
			return featureId;
		}
		
		/*
		public static void main(String args[]) {

			FeatureResult test = new FeatureResult("001", "AVERAGE");
		    
			//Populate example
			ThingServiceResult data;			

			// First TS
			data = new ThingServiceResult();
			data.setMeasure("52");			
			data.setUnit("cars/hour");
			data.setEnvironment(true);
			data.setLat(42.1f);
			data.setLon(12.01f);
			data.setAlt(23.45f);
			data.setFloor(0);
			data.setLocationKeyword("street");
			data.setLocationIdentifier("Argo Avenue");				
			test.addTSResult(data);		
			
			// Second TS			
			data = new ThingServiceResult();
			data.setMeasure("58");			
			data.setUnit("cars/hour");
			data.setEnvironment(true);
			data.setLat(42.14f);
			data.setLon(12.023f);
			data.setAlt(23.41f);
			data.setFloor(0);
			data.setLocationKeyword("street");
			data.setLocationIdentifier("Roaming Street");				
			test.addTSResult(data);		
			
		    // GET TEST DATA AND PRINT IT
			JsonObject result = test.getData();
		    String str = result.toString();
		    System.out.println(str);	
			
		}
		*/


}
