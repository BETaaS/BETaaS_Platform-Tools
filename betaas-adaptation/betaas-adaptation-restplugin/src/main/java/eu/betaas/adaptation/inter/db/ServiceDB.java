package eu.betaas.adaptation.inter.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;

import org.apache.log4j.Logger;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import eu.betaas.taas.bigdatamanager.database.hibernate.data.SimulatedThing;
import eu.betaas.taas.bigdatamanager.database.service.IBigDataDatabaseService;


public class ServiceDB  {
	
	public final static String LOGGER_NAME = "betaas.thingsadaptor";
	
	/** Logger */
	private static Logger mLogger = Logger.getLogger("betaas.adaptation");
	
	/** OSGi context */
	private BundleContext mContext;
	
	/** The connection to the DB */
	private Connection mConnection;
	
	/** The connection to the DB */
	private EntityManager em;
	
	public void setContext(BundleContext context) {
		mContext = context;	
	}
	
	public void start() {		
		mLogger.info("Start Things DB Simulator");
		mConnection = null;
		em = null;
	}
	
	public void stop() {
		mLogger.info("Stop Things DB Simulator");
		
		try {
			if ((mConnection != null) && (!mConnection.isClosed())) {
				mConnection.close();
			}
			if (em != null && em.isOpen()){
				em.close();
			}
		} catch (SQLException e) {
			mLogger.warn("Cannot close DB connection: " + e.getMessage());
		}
	}
	
	@SuppressWarnings("unchecked")
	public String listAllThings() {

		String allThingsList = null;

		try {
			
			List<SimulatedThing> allThings = this.getDatabaseServiceIF().listAllSimulatedThings();			
			mLogger.info("Things List retrieved Successfully!");
			if (allThings.size() > 0){
				Gson gson = new Gson();
				allThingsList = gson.toJson(allThings);
			}

		} catch (Exception e) {

			mLogger.error("Exception occurred while retrieving: " + e.getMessage());

		}
		return allThingsList;
	}
	
	@SuppressWarnings("unchecked")
	public List<String> listAllThingsObjects() {
		
		List<String> allThings = new ArrayList<String>();

		try {
			
			List<SimulatedThing> allSimulatedThings = this.getDatabaseServiceIF().listAllSimulatedThings();
			mLogger.info("Things List of "+allSimulatedThings.size()+" Objects retrieved Successfully!");
			allThings.clear();
			for (SimulatedThing s : allSimulatedThings){
				Gson gson = new Gson();
				allThings.add(gson.toJson(s));
			}
			mLogger.info("Things List of Objects retrieved Successfully!");			

		} catch (Exception e) {

			mLogger.error("Exception occurred while retrieving: " + e.getMessage());

		}
		return allThings;
	}
	
	public void saveThing(String thingData) {
		
		SimulatedThing simThing = new SimulatedThing();
		//If its OK, transform JsonString to Thing Simulated
		if (thingData != null && !thingData.equals("")){
			Gson gson = new Gson();
				try {
					simThing = gson.fromJson(thingData, SimulatedThing.class);
				} catch (JsonSyntaxException e) {
					e.printStackTrace();
					mLogger.error("Could not Parse SimulatedThing Object from JSON:"+thingData);
				}
		}
		
		try {
			
			this.getDatabaseServiceIF().saveSimulatedThing(simThing);

		} catch (PersistenceException e) {

			mLogger.error("PersistenceException occurred while persisting ThingJSON as SimulatedThing: "+ e.getMessage());

		} catch (Exception e) {
			
			mLogger.error("General Exception occurred while persisting ThingJSON as SimulatedThing: "+ e.getMessage());

		}

	}
	
	public void saveThing(SimulatedThing simThing) {
		
		try {
			
			this.getDatabaseServiceIF().saveSimulatedThing(simThing);

		} catch (PersistenceException e) {

			mLogger.error("PersistenceException occurred while persisting Thing as SimulatedThing: "+ e.getMessage());

		} catch (Exception e) {
			
			mLogger.error("General Exception occurred while persisting Thing as SimulatedThing: "+ e.getMessage());

		}

	}
	
	public SimulatedThing getThing(int thingId) {
		
		SimulatedThing simThing = new SimulatedThing();
		
		try {
			
			List<SimulatedThing> allThings = this.getDatabaseServiceIF().listAllSimulatedThings();
			for (SimulatedThing sim : allThings){
				if (thingId == sim.getId()){
					simThing = sim;
				}
			}

		} catch (PersistenceException e) {

			mLogger.error("PersistenceException occurred while persisting: "+ e.getMessage());

		} catch (Exception e) {
			
			mLogger.error("General Exception occurred while persisting: "+ e.getMessage());

		}
		
		return simThing;

	}

	public void deleteThing(String thingId) {	
		
		try {
			
			this.getDatabaseServiceIF().deleteSimulatedThing(Integer.valueOf(thingId));
	
			mLogger.info("Thing with id:"+thingId+" Deleted Successfully");

		} catch (PersistenceException e) {
			
			mLogger.error("Exception occurred while deleting (persisting): "+ e.getMessage());
			
		} catch (Exception ex){
			
			mLogger.error("Exception occurred while deleting Simulated Thing: "+ ex.getMessage());
			
		}

	}
	
	
	
	private IBigDataDatabaseService getDatabaseServiceIF() {
		if (mContext == null) {
			mLogger.error("Cannot get database service IF: null context");
			return null;
		}
		
		try {			
			ServiceReference ref = mContext.getServiceReference(IBigDataDatabaseService.class.getName());
				
			if (ref != null) {
				return ((IBigDataDatabaseService)mContext.getService(ref));
			}
		} catch (java.lang.NoClassDefFoundError e) {
		    mLogger.error("No class definition found for Database Service");
		    return null;
		} catch (Exception e) {
			mLogger.error("Database Service not available: " + e.getMessage());
			return null;
		}
			
		return null;
	}
	
}
