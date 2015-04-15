//Copyright 2014-2015 Tecnalia.
//
//Licensed under the Apache License, Version 2.0 (the "License");
//you may not use this file except in compliance with the License.
//You may obtain a copy of the License at
//
//       http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
 
 
 
// BETaaS - Building the Environment for the Things as a Service
//
// Component: QoSManager Monitoring, TaaS Module
// Responsible: Tecnalia
package eu.betaas.taas.qosmanager.monitoring.api.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import eu.betaas.rabbitmq.publisher.interfaces.Publisher;
import eu.betaas.rabbitmq.publisher.interfaces.utils.Message;
import eu.betaas.rabbitmq.publisher.interfaces.utils.MessageBuilder;
import eu.betaas.rabbitmq.publisher.interfaces.utils.Message.Layer;
import eu.betaas.taas.contextmanager.api.ThingsServiceManager;
import eu.betaas.taas.qosmanager.monitoring.api.QoSManagerMonitoring;
import eu.betaas.taas.qosmanager.monitoring.api.impl.QoSMonitoringMeasure;

public class QoSManagerMonitoringImpl implements QoSManagerMonitoring
{
  // PUBLIC SECTION
  public final static String LOGGER_NAME = "betaas.taas";

  // PROTECTED SECTION
  private ThingsServiceManager cmservice;
  private static Logger mLogger = Logger.getLogger(LOGGER_NAME);
  private static Logger LOGTest = Logger.getLogger("betaas.testplan");
  private ArrayList<QoSMonitoringMeasure> oThingServiceQoSMeasure = new ArrayList<QoSMonitoringMeasure>();
  private static QoSManagerMonitoringImpl thing = null;
  
  private static BundleContext context;
  private boolean enabled=false;
  private List<String> messageBuffer = new Vector<String>();
  private String key = "monitoring.taas.qosmonitoring";
  
  private QoSManagerMonitoringImpl()
  {
    super();
  }


  
//  public boolean registerMeasurementSLAMonitoringPull(String sThingServiceName, int sOptimalRequestRate)
  public boolean getMeasurementSLAMonitoring(String sThingServiceName, int sMilisecondMinInterRequestRate)
  {
    Timestamp tMaximumTimeStamp;
    boolean bResults = true;
    
      QoSMonitoringMeasure oThingServiceMeasure = checkThingServiceExists(sThingServiceName);
      mLogger.info("Component QoS Monitoring perform operation QoSManagerMonitoring.registerMeasurementSLAMonitoring. "+sThingServiceName+" Thing Service.");
      sendData("Component QoS Monitoring perform operation QoSManagerMonitoring.registerMeasurementSLAMonitoring. "+sThingServiceName+" Thing Service.", "info", "TaaSQoSMonitoring");
      if (!(oThingServiceMeasure == null))
      {
        int iRequestRate = oThingServiceMeasure.getRequestRate();
        iRequestRate = iRequestRate + 1;
        oThingServiceMeasure.setRequestRate(iRequestRate);
      }
      else
      {
        QoSMonitoringMeasure monitoringMeasure = new QoSMonitoringMeasure();
        monitoringMeasure.setThingServiceName(sThingServiceName);

        monitoringMeasure.setOptimalRequestRate(sMilisecondMinInterRequestRate);

        int iRequestRate = monitoringMeasure.getRequestRate();
        iRequestRate = 1;
        monitoringMeasure.setRequestRate(iRequestRate);

        Date date = new Date();
        Timestamp ts = new Timestamp(date.getTime());
        monitoringMeasure.setInitialTimeStamp(ts);
        LOGTest.debug("Monitoring Start");

        String sMaximumTimeStamp = cmservice.getMaximumResponseTime(sThingServiceName);

        if (sMaximumTimeStamp.equals("")){
          tMaximumTimeStamp = new Timestamp (0);
        }else
          tMaximumTimeStamp = new Timestamp (Integer.parseInt(sMaximumTimeStamp));
        monitoringMeasure.setMaximumTimeStamp(tMaximumTimeStamp);
        
        boolean bExists = cmservice.checkAvailability(sThingServiceName);
        monitoringMeasure.setAvailability(bExists);

        oThingServiceQoSMeasure.add(monitoringMeasure);
        mLogger.debug("Component QoS Monitoring perform operation QoSManagerMonitoring.registerMeasurementSLAMonitoring function.");
        oThingServiceMeasure = null;
      }
    return bResults;
  }
  
  public boolean registerMeasurementSLAMonitoring(String sThingServiceName, int sMilisecondMinInterRequestRate, int iMilisecondPeriod)
  {
    Timestamp tMaximumTimeStamp;
    boolean bResults = true;
    
      QoSMonitoringMeasure oThingServiceMeasure = checkThingServiceExists(sThingServiceName);
      mLogger.info("Component QoS Monitoring perform operation QoSManagerMonitoring.registerMeasurementSLAMonitoring. "+sThingServiceName+" Thing Service.");
      sendData("Component QoS Monitoring perform operation QoSManagerMonitoring.registerMeasurementSLAMonitoring. "+sThingServiceName+" Thing Service.", "info", "TaaSQoSMonitoring");
      if (!(oThingServiceMeasure == null))
      {
        int iRequestRate = oThingServiceMeasure.getRequestRate();
        iRequestRate = iRequestRate + 1;
        oThingServiceMeasure.setRequestRate(iRequestRate);
      }
      else
      {
        QoSMonitoringMeasure monitoringMeasure = new QoSMonitoringMeasure();
        monitoringMeasure.setThingServiceName(sThingServiceName);
        
        monitoringMeasure.setOptimalRequestRate(sMilisecondMinInterRequestRate);

        monitoringMeasure.setPeriod(iMilisecondPeriod);

        int iRequestRate = monitoringMeasure.getRequestRate();
        iRequestRate = 1;
        monitoringMeasure.setRequestRate(iRequestRate);

        Date date = new Date();
        Timestamp ts = new Timestamp(date.getTime());
        monitoringMeasure.setInitialTimeStamp(ts);
        LOGTest.debug("Monitoring Start");

        String sMaximumTimeStamp = cmservice.getMaximumResponseTime(sThingServiceName);

        if (sMaximumTimeStamp.equals("")){
          tMaximumTimeStamp = new Timestamp (0);
        }else
          tMaximumTimeStamp = new Timestamp (Integer.parseInt(sMaximumTimeStamp));
        monitoringMeasure.setMaximumTimeStamp(tMaximumTimeStamp);
        
        boolean bExists = cmservice.checkAvailability(sThingServiceName);
        monitoringMeasure.setAvailability(bExists);

        oThingServiceQoSMeasure.add(monitoringMeasure);
        mLogger.debug("Component QoS Monitoring perform operation QoSManagerMonitoring.registerMeasurementSLAMonitoringPush function.");
        oThingServiceMeasure = null;
      }

    return bResults;

  }


  private QoSMonitoringMeasure checkThingServiceExists(
      String sNewThingServiceName)
  {
    QoSMonitoringMeasure oThingServiceMeasure = null;
    for (int i = 0; i < oThingServiceQoSMeasure.size(); i++)
    {
      oThingServiceMeasure = oThingServiceQoSMeasure.get(i);
      String sThingServiceName = oThingServiceMeasure.getThingServiceName();
      if (sThingServiceName.equals(sNewThingServiceName))
        return oThingServiceMeasure;
    }
    return null;
  }

  public boolean unregisterMeasurementSLAMonitoring(String sThingServiceName)
  {
    boolean bResults = false;
      mLogger.info("Component QoS Monitoring perform operation QoSManagerMonitoring.unregisterMeasurementSLAMonitoring. "+sThingServiceName+" Thing Service.");
      sendData("Component QoS Monitoring perform operation QoSManagerMonitoring.unregisterMeasurementSLAMonitoring. "+sThingServiceName+" Thing Service.", "info", "TaaSQoSMonitoring");
      QoSMonitoringMeasure oThingServiceMeasure = checkThingServiceExists(sThingServiceName);
      if (!(oThingServiceMeasure == null))
      {
//        mLogger.info("***************unregisterMeasurementSLAMonitoring Measurement exists: "+sThingServiceName);
//        int iRequestRate = oThingServiceMeasure.getRequestRate();
//        iRequestRate = iRequestRate - 1;
//        mLogger.info("***************unregisterMeasurementSLAMonitoring Measurement exists, iRequestRate: "+iRequestRate);
//        oThingServiceMeasure.setRequestRate(iRequestRate);
//        bResults = true;

        for (int j = 0; j < oThingServiceQoSMeasure.size(); j++)
        {
          QoSMonitoringMeasure monitoringMeasure = new QoSMonitoringMeasure();
          monitoringMeasure = oThingServiceQoSMeasure.get(j);
          monitoringMeasure.getThingServiceName().equals(sThingServiceName);
          oThingServiceQoSMeasure.remove(j);
//          mLogger.debug("Component QoS Monitoring perform operation QoSManagerMonitoring.unregisterMeasurementSLAMonitoring.");
        }

      }
      else
        mLogger.error("Component QoS Monitoring perform operation QoSManagerMonitoring.unregisterMeasurementSLAMonitoring. "+sThingServiceName+" Thing Service. No existing previously.");

    return bResults;
  }

  public SLACalculation calculateSLA(String sThingServiceName)
  {
    int iSuccess = 0;
    int iUnsuccess = 0;
    SLACalculation resultSLA = null;
    
    try{
    
      QoSMonitoringMeasure oThingServiceMeasure = checkThingServiceExists(sThingServiceName);
      mLogger.debug("Component QoS Monitoring perform operation QoSManagerMonitoring.calculateSLA. "+sThingServiceName+" Thing Service.");
      sendData("Component QoS Monitoring perform operation QoSManagerMonitoring.calculateSLA. "+sThingServiceName+" Thing Service.", "info", "TaaSQoSMonitoring");
      if (!(oThingServiceMeasure == null) && (oThingServiceMeasure.getAvailability()))
      {
        iSuccess++;
        
        int iRequestRate = oThingServiceMeasure.getRequestRate();
        int iOptiomalRequestRate = oThingServiceMeasure.getOptimalRequestRate();
        if (iRequestRate<iOptiomalRequestRate) iSuccess++;
        else iUnsuccess++;
        
        Timestamp tsMaximum = oThingServiceMeasure.getMaximumTimeStamp();
        Timestamp tsInitial = oThingServiceMeasure.getInitialTimeStamp();
        Date date = new Date();
        Timestamp tsActual = new Timestamp(date.getTime());
        Timestamp ts = diff(tsInitial, tsActual);
        LOGTest.debug("Monitoring End");
        
        if (tsMaximum.equals("")) iUnsuccess++;
        else
          //Constructs a Timestamp object using a milliseconds time value.
          if (ts.getTime() < tsMaximum.getTime()){ 
            iUnsuccess++;
          }
          else{
            iSuccess++;
          }
      }else{
        iUnsuccess=3;
      }
      
      resultSLA = new SLACalculation();
      resultSLA.setThingServiceId(sThingServiceName);
      resultSLA.setQoSparamsFulfill(iSuccess);
      resultSLA.setQoSparamsNoFulfill(iUnsuccess);
      
      mLogger.info("Component QoS Monitoring perform operation QoSManagerMonitoring.calculateSLA. "+sThingServiceName+" Thing Service. CalculateSLA, "+sThingServiceName +" ThingService: iSuccess_" + iSuccess+", iUnsuccess_" + iUnsuccess);
    }
    catch (Exception e)
    {
      mLogger.error("Component QoS Monitoring perform operation QoSManagerMonitoring.calculateSLA. It has not been executed correctly. Exception: " + e.getMessage()+".");
    }
    return resultSLA;
  }

  public static Timestamp diff (java.util.Date t1, java.util.Date t2)
  {
      // Make sure the result is always > 0
      if (t1.compareTo (t2) < 0)
      {
          java.util.Date tmp = t1;
          t1 = t2;
          t2 = tmp;
      }

      // Timestamps mix milli and nanoseconds in the API, so we have to separate the two
      long diffSeconds = (t1.getTime () / 1000) - (t2.getTime () / 1000);
      // For normals dates, we have millisecond precision
      int nano1 = ((int) t1.getTime () % 1000) * 1000000;
      // If the parameter is a Timestamp, we have additional precision in nanoseconds
      if (t1 instanceof Timestamp)
          nano1 = ((Timestamp)t1).getNanos ();
      int nano2 = ((int) t2.getTime () % 1000) * 1000000;
      if (t2 instanceof Timestamp)
          nano2 = ((Timestamp)t2).getNanos ();

      int diffNanos = nano1 - nano2;
      if (diffNanos < 0)
      {
          // Borrow one second
          diffSeconds --;
          diffNanos += 1000000000;
      }

      // mix nanos and millis again
      Timestamp result = new Timestamp ((diffSeconds * 1000) + (diffNanos / 1000000));
      // setNanos() with a value of in the millisecond range doesn't affect the value of the time field
      // while milliseconds in the time field will modify nanos! Damn, this API is a *mess*
      result.setNanos (diffNanos);
      return result;
  }

  
  // BLUEPRINT METHODS
  public void startService()
  {
    try
    {
      mLogger.info("Component QoS Monitoring has started.");
    }
    catch (Exception e)
    {
      mLogger.error("Component QoS Monitoring perform operation QoSManagerMonitoring.startService. It has not been executed correctly. Exception: " + e.getMessage()+".");
    }
  }

  public void closeService()
  {
    try
    {
      mLogger.info("Component QoS Monitoring has stopped.");
    }
    catch (Exception e)
    {
      mLogger.error("Component QoS Monitoring perform operation QoSManagerMonitoring.closeService. It has not been executed correctly. Exception: " + e.getMessage()+".");
    }
  }

  public static QoSManagerMonitoringImpl getInstance()
  {
    if (thing == null)
    {
      thing = new QoSManagerMonitoringImpl();
    }
    return thing;
  }
  
  public void setCmservice(ThingsServiceManager cmservice)
  {
    this.cmservice = cmservice;
  }

  public ThingsServiceManager getCmservice()
  {
    return this.cmservice;
  }

  public void setContext(BundleContext context)
  {
    this.context = context;
  }

  public BundleContext getContext()
  {
    return this.context;
  }
  

  public SLACalculation failureSLA(String sThingServiceName)
  {
    int iSuccess = 0;
    int iUnsuccess = 3;
    SLACalculation resultSLA = null;
    
    try{
      resultSLA = new SLACalculation();
      resultSLA.setThingServiceId(sThingServiceName);
      resultSLA.setQoSparamsFulfill(iSuccess);
      resultSLA.setQoSparamsNoFulfill(iUnsuccess);
      
      mLogger.info("Component QoS Monitoring perform operation QoSManagerMonitoring.failureSLA. "+sThingServiceName+" Thing Service. CalculateSLA, "+sThingServiceName +" ThingService: iSuccess_" + iSuccess+", iUnsuccess_" + iUnsuccess);
    }
    catch (Exception e)
    {
      mLogger.error("Component QoS Monitoring perform operation QoSManagerMonitoring.calculateSLA. It has not been executed correctly. Exception: " + e.getMessage()+".");
    }
    return resultSLA;
  }



  public SLACalculation calculateSLAPush(String sThingServiceName, int iMilisecondTaaSRequestRate)
  {
      int iSuccess = 0;
      int iUnsuccess = 0;
      SLACalculation resultSLA = null;
      
//      iTaaSRequestRate is in seconds, we must convert it to miliseconds
//      int imlsgTaaSRequestRate = iTaaSRequestRate * 1000;
      
//      String dateAsText = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(iRequestRate * 1000L));
      try{
      
        QoSMonitoringMeasure oThingServiceMeasure = checkThingServiceExists(sThingServiceName);
        mLogger.debug("Component QoS Monitoring perform operation QoSManagerMonitoring.calculateSLAPush. "+sThingServiceName+" Thing Service.");
        sendData("Component QoS Monitoring perform operation QoSManagerMonitoring.calculateSLAPush. "+sThingServiceName+" Thing Service.", "info", "TaaSQoSMonitoring");
        if (!(oThingServiceMeasure == null) && (oThingServiceMeasure.getAvailability()))
        {
          iSuccess++;
          
          int iRequestRate = oThingServiceMeasure.getRequestRate();
          int iOptiomalRequestRate = oThingServiceMeasure.getOptimalRequestRate();
          if (iRequestRate<iOptiomalRequestRate) iSuccess++;
          else iUnsuccess++;
          
//          Timestamp tsMaximum = oThingServiceMeasure.getMaximumTimeStamp();
//          int iMaximum = Integer.parseInt(tsMaximum.toString());
          int isgMaxRequestRate = oThingServiceMeasure.getPeriod();
          LOGTest.debug("Monitoring PUSH End");
          
          if (isgMaxRequestRate==0) iUnsuccess++;
          else
            //Constructs a Timestamp object using a seconds time value.
            if (iMilisecondTaaSRequestRate < isgMaxRequestRate){ 
              iUnsuccess++;
            }
            else{
              iSuccess++;
            }
        }else{
          iUnsuccess=3;
        }
        
        resultSLA = new SLACalculation();
        resultSLA.setThingServiceId(sThingServiceName);
        resultSLA.setQoSparamsFulfill(iSuccess);
        resultSLA.setQoSparamsNoFulfill(iUnsuccess);
        
        mLogger.info("Component QoS Monitoring perform operation QoSManagerMonitoring.calculateSLAPush. "+sThingServiceName+" Thing Service. CalculateSLA, "+sThingServiceName +" ThingService: iSuccess_" + iSuccess+", iUnsuccess_" + iUnsuccess);
      }
      catch (Exception e)
      {
        mLogger.error("Component QoS Monitoring perform operation QoSManagerMonitoring.calculateSLAPush. It has not been executed correctly. Exception: " + e.getMessage()+".");
      }
      return resultSLA;
  }

  public void busMessage(String message){
    mLogger.debug("Checking queue");
    if (!enabled)return;
    mLogger.debug("Sending to queue");
    ServiceReference serviceReference = this.getContext().getServiceReference(Publisher.class.getName());
    if (serviceReference==null){
      mLogger.warn("Requested to publish data to queue, but service betaas publisher not found");
      messageBuffer.add(message);
      return;
    }
    Publisher service = (Publisher) this.getContext().getService(serviceReference); 
    if (service==null){
      mLogger.warn("Requested to publish data to queue, but service betaas publisher not found");
      messageBuffer.add(message);
      return;
    }

    if (messageBuffer.size()>0){
      mLogger.warn("Buffered data available, publishing this data now with key ");
      for (int i =0 ; i<messageBuffer.size();i++){
        service.publish(key,messageBuffer.get(i));
        messageBuffer.remove(i);
      }
      
    }
  
    mLogger.debug("This is the message built "+message);
    
    mLogger.debug("Sending to "); 
    service.publish(key,message);
    mLogger.debug("Sent to queue " + key);
  }
  
  
  public void sendData(String description, String level, String originator) {
    java.util.Date date= new java.util.Date();
    Timestamp timestamp = new Timestamp(date.getTime());
    Message msg = new Message();
    msg.setDescritpion(description);
    msg.setLayer(Layer.TAAS);
    msg.setLevel(level);
    msg.setOrigin(originator);
    msg.setTimestamp(timestamp.getTime());
    MessageBuilder msgBuilder = new MessageBuilder();
    String json = msgBuilder.getJsonEquivalent(msg);
    busMessage(json);
  }
  
//  public boolean isEnabledbus() {
//    return enabledbus;
//  }
//
//  public void setEnabledbus(boolean enabledbus) {
//    this.enabledbus = enabledbus;
//  }
//  
//  public boolean getEnabledbus() {
//    return enabledbus;
//  }
  
  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }
}
