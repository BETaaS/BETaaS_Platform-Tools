package eu.betaas.adaptation.contextmanager.onto.classesExt.commonUtils;

import java.sql.SQLException;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Instant;
import org.joda.time.Interval;

import eu.betaas.adaptation.contextmanager.api.SemanticParserAdaptator;
import eu.betaas.adaptation.contextmanager.api.impl.SemanticParserAdaptatorImpl;
import eu.betaas.adaptation.contextmanager.onto.classesExt.commonUtils.ConfigBundleOSGiImpl;
import eu.betaas.taas.contextmanager.api.ThingsServiceManager;


public class ReminderReachableRemove {
  private Timer timer;
  private String sThing;
  private static SemanticParserAdaptator oSemanticParserAdaptator = new SemanticParserAdaptatorImpl();

  
  public ReminderReachableRemove(int minutes, String sThingName, ThingsServiceManager cmservice) throws SQLException {
    sThing = sThingName;    
    timer = new Timer();
    timer.schedule(new RemindTask(), minutes * 60 * 1000);
  }

  class RemindTask extends TimerTask {
    private ThingsServiceManager cmservice;
    private Logger mLogger = Logger.getLogger(ConfigBundleOSGiImpl.LOGGER_NAME);
    ConfigBundleOSGi oConfigOSGi = ConfigBundleOSGiImpl.getInstance();
    
    public void run() {
      String sInstanceID = "sensor_"+sThing+"_"+sThing;
      mLogger.info("Component CM perform operation ReminderReachableRemove. Preparing to remove sensor "+sInstanceID+"...");
      String sThingServiceName = null;
      try
      {
        this.cmservice = oConfigOSGi.getCmservice();
      }
      catch (Exception e)
      {
        mLogger.error("ReminderReachableRemove Exception : "+e.getMessage());
      }
      String timeLastObservation = cmservice.getLastObservation(sInstanceID); //Check 2015-03-17T16:51:19.860

      DateTime dt = new DateTime(timeLastObservation);
      DateTime dt_now = new DateTime();

      Interval interval = new Interval(new Instant (dt), new Instant (dt_now));
      int intervalMinutes = interval.toPeriod().getMinutes();
      
      if (intervalMinutes>4 && oSemanticParserAdaptator.searchThingServiceDeleteList(sThingServiceName)){ //TODO 
        sThingServiceName = cmservice.sparqlRemoveDevice(sInstanceID);
        oSemanticParserAdaptator.removeThingUnreachable(sThingServiceName);
        mLogger.info("Component CM perform operation ReminderReachableRemove. The thing "+sThingServiceName+" has been removed");
        oSemanticParserAdaptator.delThingServiceDeleteList(sThingServiceName);
      }
      else
        mLogger.info("Component CM perform operation ReminderReachableRemove. The thing "+sInstanceID+" can't be removed");
        timer.cancel();
    }
  }
}