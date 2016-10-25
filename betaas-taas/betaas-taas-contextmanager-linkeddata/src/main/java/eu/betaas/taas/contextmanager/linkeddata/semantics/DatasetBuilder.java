package eu.betaas.taas.contextmanager.linkeddata.semantics;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import eu.betaas.taas.bigdatamanager.database.hibernate.data.ThingInformation;
import eu.betaas.taas.contextmanager.linkeddata.clients.TaaSBDMClient;
import eu.betaas.taas.contextmanager.linkeddata.clients.TaaSCMClient;
import eu.betaas.taas.contextmanager.linkeddata.clients.Thing;
import eu.betaas.taas.contextmanager.linkeddata.clients.ThingDatasetData;

public class DatasetBuilder 
{
	private Logger logger= Logger.getLogger("betaas.taas");
	private TaaSBDMClient myBDMClient;
	private TaaSCMClient mySCMClient;

	public DatasetBuilder ()
	{
		myBDMClient = TaaSBDMClient.instance();
		mySCMClient = TaaSCMClient.instance();
	}
	
	public String generateCSVDataset(String idThing) throws Exception
	{
		String ret = "";
		// Retrieve basic information about the thing service
		// Extract common data
		Thing thing = mySCMClient.getThing(idThing);
				
		if (thing==null)
		{
			Exception e;
			logger.error ("It was not possible to retrieve basic data for thing" + idThing);
			e = new Exception("404");
			throw e;
		}
		
		ThingInformation fullInfo = myBDMClient.getThingInformation(idThing);
		if (fullInfo==null)
		{
			Exception e;
			logger.error ("It was not possible to retrieve basic data for thing info " + idThing);
			e = new Exception("404");
			throw e;
		}
		
		// Retrieve last data generated
		ArrayList<ThingDatasetData> thingDataList = myBDMClient.getThingData(idThing);
		if (thingDataList == null)
		{
			Exception e;
			logger.error ("It was not possible to retrieve historical data for thing " + idThing);
			e = new Exception("404");
			throw e;
		}
		logger.debug ("Thing Data in the list received: " + thingDataList.size());
	
			// Include header
		ret +="LocationKeyword"
		    + ','
		    + "LocationIdentifier"
		    + ','
		    + "Floor"
		    + ','
		    + "Longitude"
		    + ','
		    + "Latitude"
		    + ','
		    + "Altitude"
		    + ','
		    + "Date"
		    + ','
		    + "Measurement"
		    + ','
		    + "Unit"
		    + ','
		    + "Type"
		    + '\n';
		
		for (ThingDatasetData data : thingDataList) {
			// Add data 
			ret += thing.getLocationKeyword()
			    + ','
			    + thing.getLocationIdentifier()
			    + ','
			    + thing.getFloor()
			    + ','
			    + thing.getLongitude()
			    + ','
			    + thing.getLatitude()
			    + ','
			    + thing.getAltitude()
			    + ','
			    + data.getTimestamp()
			    + ','
			    + data.getMeasurement()
			    + ','
			    + thing.getUnit()
			    + ','
			    + thing.getType()
			    + '\n';
		}
			
		return ret;
	}
}
