package eu.betaas.taas.taasvmmanager.dependability;

import java.util.Calendar;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class DependabilityMessage 
{

	private String date;
	private String layer;
	private String level;
	private String originator;
	private String description;
	
	public DependabilityMessage (String theMessage)
	{
		date = Calendar.getInstance().getTime().toString();
		layer = "service";
		level = "error";
		originator = "TaaSRM";
		description = theMessage;
	}
	
	public String getDate()
	{
		return date;
	}
	
	public String getLayer()
	{
		return layer;
	}
	
	public String getLevel()
	{
		return level;
	}
	
	public String getOriginator()
	{
		return originator;
	}
	
	public String getDescription()
	{
		return description;
	}
	
	public String getData()
	{
		//Conversion to JSON
		Gson gson = new Gson();		
	    JsonObject jsonResult = new JsonObject();
	    return jsonResult.getAsString();
	}
}
