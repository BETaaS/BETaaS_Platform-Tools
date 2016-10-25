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
//import eu.betaas.taas.qosmanager.api.QoSManagerInternalIF;
import eu.betaas.taas.qosmanager.monitoring.api.QoSManagerMonitoring;
//import eu.betaas.taas.qosmanager.monitoring.api.impl.QoSMonitoringMeasure;

import org.joda.time.DateTime;
import org.joda.time.Instant;
import org.joda.time.Interval;

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
//  static private QoSManagerInternalIF qosservice;
  
  private static BundleContext context;
  private boolean enabled=false;
  private List<String> messageBuffer = new Vector<String>();
  private String key = "monitoring.taas.qosmonitoring";
  
  private QoSManagerMonitoringImpl()
  {
    super();
  }


  
  @Deprecated
  public boolean getMeasurementSLAMonitoring(String sThingServiceName, int sMilisecondMinInterRequestRate)
  {
    mLogger.info("Component QoS Monitoring perform operation QoSManagerMonitoring.getMeasurementSLAMonitoring. "+sThingServiceName+" Thing Service.");
    sendData("Component QoS Monitoring perform operation QoSManagerMonitoring.getMeasurementSLAMonitoring. "+sThingServiceName+" Thing Service.", "info", "TaaSQoSMonitoring");
    
    boolean bResults = true;
    try{
    Timestamp tMaximumTimeStamp;
    
      QoSMonitoringMeasure oThingServiceMeasure = checkThingServiceExists(sThingServiceName);
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
        LOGTest.debug("Monitoring Start "+sThingServiceName);

        String sMaximumTimeStamp = cmservice.getMaximumResponseTime(sThingServiceName);

        if (sMaximumTimeStamp.equals("")){
          tMaximumTimeStamp = new Timestamp (0);
        }else{
            tMaximumTimeStamp = new Timestamp (Integer.parseInt(sMaximumTimeStamp));
        }
        monitoringMeasure.setMaximumTimeStamp(tMaximumTimeStamp);
        
        boolean bExists = cmservice.checkAvailability(sThingServiceName);
        monitoringMeasure.setAvailability(bExists);

        oThingServiceQoSMeasure.add(monitoringMeasure);
        mLogger.debug("Component QoS Monitoring perform operation QoSManagerMonitoring.getMeasurementSLAMonitoring function.");
        oThingServiceMeasure = null;
      }
    }
    catch (Exception e)
    {
      mLogger.error("Component QoS Monitoring perform operation QoSManagerMonitoring.getMeasurementSLAMonitoring. It has not been executed correctly. Exception: " + e.getMessage()+".");
    }
    return bResults;
  }
  
  @Deprecated
  public boolean registerMeasurementSLAMonitoring(String sThingServiceName, int sMilisecondMinInterRequestRate, int iMilisecondPeriod)
  {
    mLogger.info("Component QoS Monitoring perform operation QoSManagerMonitoring.registerMeasurementSLAMonitoring. "+sThingServiceName+" Thing Service.");
    sendData("Component QoS Monitoring perform operation QoSManagerMonitoring.registerMeasurementSLAMonitoring. "+sThingServiceName+" Thing Service.", "info", "TaaSQoSMonitoring");
    boolean bResults = true;
    try{
    Timestamp tMaximumTimeStamp;
    
      QoSMonitoringMeasure oThingServiceMeasure = checkThingServiceExists(sThingServiceName);
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
        LOGTest.debug("Monitoring Start "+sThingServiceName);

        String sMaximumTimeStamp = cmservice.getMaximumResponseTime(sThingServiceName);
        
        if (sMaximumTimeStamp.equals("")){
          tMaximumTimeStamp = new Timestamp (0);
        }else{
          tMaximumTimeStamp = new Timestamp (Integer.parseInt(sMaximumTimeStamp));
        }
        monitoringMeasure.setMaximumTimeStamp(tMaximumTimeStamp);
        
        boolean bExists = cmservice.checkAvailability(sThingServiceName);
        monitoringMeasure.setAvailability(bExists);

        oThingServiceQoSMeasure.add(monitoringMeasure);
        mLogger.debug("Component QoS Monitoring perform operation QoSManagerMonitoring.registerMeasurementSLAMonitoringPush function.");
        oThingServiceMeasure = null;
      }
    }
      catch (Exception e)
      {
        mLogger.error("Component QoS Monitoring perform operation QoSManagerMonitoring.registerMeasurementSLAMonitoring. It has not been executed correctly. Exception: " + e.getMessage()+".");
      }
    return bResults;

  }

  
  public boolean getMeasurementSLAMonitoring(String sThingServiceName, int sMilisecondMinInterRequestRate, double dTolerateJitterParam)
  {
    boolean bResults = true;
    try{
      QoSMonitoringMeasure oThingServiceMeasure = checkThingServiceExists(sThingServiceName);
      mLogger.info("Component QoS Monitoring perform operation QoSManagerMonitoring.registerMeasurementSLAMonitoringPull. "+sThingServiceName+" Thing Service. sMilisecondMinInterRequestRate "+sMilisecondMinInterRequestRate+" dTolerateJitterParam"+dTolerateJitterParam+".");
      sendData("Component QoS Monitoring perform operation QoSManagerMonitoring.registerMeasurementSLAMonitoringPull. "+sThingServiceName+" Thing Service.", "info", "TaaSQoSMonitoring");
      if (!(oThingServiceMeasure == null))
      {
        QoSMonitoringMeasure monitoringMeasurePull = new QoSMonitoringMeasure();
        Timestamp tsInitial = monitoringMeasurePull.getInitialTimeStamp();
        
        DateTime dt_now = new DateTime();

        Interval interval = new Interval(new Instant (tsInitial), new Instant (dt_now));
        int intervalMillis = interval.toPeriod().getMillis();
        
        if (intervalMillis<sMilisecondMinInterRequestRate){
          mLogger.debug("Component QoS Monitoring perform operation QoSManagerMonitoring.calculateSLAPull. MinInterRequestRate violation.");
          monitoringMeasurePull.setiUnsucess(1);
        }else{
          mLogger.debug("Component QoS Monitoring perform operation QoSManagerMonitoring.calculateSLAPull. MinInterRequestRate ok.");
          monitoringMeasurePull.setiUnsucess(0);
        }
          
      }
      else
      {
        QoSMonitoringMeasure monitoringMeasure = new QoSMonitoringMeasure();
        monitoringMeasure.setThingServiceName(sThingServiceName);
        
        monitoringMeasure.setOptimalRequestRate(sMilisecondMinInterRequestRate);
        
        monitoringMeasure.setTolerateJitter(dTolerateJitterParam);

        int iRequestRate = 1;
        monitoringMeasure.setRequestRate(iRequestRate);

        Date date = new Date();
        Timestamp ts = new Timestamp(date.getTime());
        monitoringMeasure.setInitialTimeStamp(ts);
        LOGTest.debug("Monitoring Start "+sThingServiceName);

        String sMaximumTimeStamp = cmservice.getMaximumResponseTime(sThingServiceName);
        
        int iMaximumTimeStamp =  Integer.parseInt(sMaximumTimeStamp);
        monitoringMeasure.setMaximumTimeStamp(new Timestamp (iMaximumTimeStamp));
        
        boolean bExists = cmservice.checkAvailability(sThingServiceName);
        monitoringMeasure.setAvailability(bExists);

        oThingServiceQoSMeasure.add(monitoringMeasure);
        mLogger.debug("Component QoS Monitoring perform operation QoSManagerMonitoring.registerMeasurementSLAMonitoringPull function.");
        oThingServiceMeasure = null;
      }
    }
      catch (Exception e)
      {
        mLogger.error("Component QoS Monitoring perform operation QoSManagerMonitoring.registerMeasurementSLAMonitoringPull. It has not been executed correctly. Exception: " + e.getMessage()+".");
      }
    return bResults;

  }
  
  public boolean registerMeasurementSLAMonitoring(String sThingServiceName, int sMilisecondMinInterRequestRate, int iMilisecondPeriod, double dTolerateJitterParam)
  {
    boolean bResults = true;
    try{
      QoSMonitoringMeasure oThingServiceMeasure = checkThingServiceExists(sThingServiceName);
      mLogger.info("Component QoS Monitoring perform operation QoSManagerMonitoring.registerMeasurementSLAMonitoringPush. "+sThingServiceName+" Thing Service. sMilisecondMinInterRequestRate "+sMilisecondMinInterRequestRate+" iMilisecondPeriod, "+iMilisecondPeriod+"dTolerateJitterParam"+dTolerateJitterParam+".");
      sendData("Component QoS Monitoring perform operation QoSManagerMonitoring.registerMeasurementSLAMonitoringPush. "+sThingServiceName+" Thing Service.", "info", "TaaSQoSMonitoring");
      if (!(oThingServiceMeasure == null))
      {
        QoSMonitoringMeasure monitoringMeasure = new QoSMonitoringMeasure();
        Timestamp tsInitial = monitoringMeasure.getInitialTimeStamp();
        
        DateTime dt_now = new DateTime();

        Interval interval = new Interval(new Instant (tsInitial), new Instant (dt_now));
        int intervalMillis = interval.toPeriod().getMillis();
        
        if (intervalMillis<sMilisecondMinInterRequestRate){
          mLogger.debug("Component QoS Monitoring perform operation QoSManagerMonitoring.calculateSLAPush. MinInterRequestRate violation.");
          monitoringMeasure.setiUnsucess(1);
        }else{
          mLogger.debug("Component QoS Monitoring perform operation QoSManagerMonitoring.calculateSLAPush. MinInterRequestRate ok.");
          monitoringMeasure.setiUnsucess(0);
        }
          
      }
      else
      {
        QoSMonitoringMeasure monitoringMeasure = new QoSMonitoringMeasure();
        monitoringMeasure.setThingServiceName(sThingServiceName);
        
        monitoringMeasure.setOptimalRequestRate(sMilisecondMinInterRequestRate);

        monitoringMeasure.setPeriod(iMilisecondPeriod);
        
        monitoringMeasure.setTolerateJitter(dTolerateJitterParam);

        int iRequestRate = 1;
        monitoringMeasure.setRequestRate(iRequestRate);

        Date date = new Date();
        Timestamp ts = new Timestamp(date.getTime());
        monitoringMeasure.setInitialTimeStamp(ts);
        LOGTest.debug("Monitoring Start "+sThingServiceName);

        String sMaximumTimeStamp = cmservice.getMaximumResponseTime(sThingServiceName);
        
        int iMaximumTimeStamp =  Integer.parseInt(sMaximumTimeStamp);
        monitoringMeasure.setMaximumTimeStamp(new Timestamp (iMaximumTimeStamp));
        
        boolean bExists = cmservice.checkAvailability(sThingServiceName);
        monitoringMeasure.setAvailability(bExists);

        oThingServiceQoSMeasure.add(monitoringMeasure);
        mLogger.debug("Component QoS Monitoring perform operation QoSManagerMonitoring.registerMeasurementSLAMonitoringPush function.");
        oThingServiceMeasure = null;
      }
    }
      catch (Exception e)
      {
        mLogger.error("Component QoS Monitoring perform operation QoSManagerMonitoring.registerMeasurementSLAMonitoringPush. It has not been executed correctly. Exception: " + e.getMessage()+".");
      }
    return bResults;

  }


  private QoSMonitoringMeasure checkThingServiceExists(String sNewThingServiceName)
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
    try{
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
    }
    catch (Exception e)
      {
        mLogger.error("Component QoS Monitoring perform operation QoSManagerMonitoring.unregisterMeasurementSLAMonitoring. It has not been executed correctly. Exception: " + e.getMessage()+".");
      }
    return bResults;
  }

  public SLACalculation calculateSLA(String sThingServiceName)
  {
    int iSuccess = 0;
    int iUnsuccess = 0;
    SLACalculation resultSLA = null;
    String sMaximumTimeStamp = null;
    
    try{
    
      QoSMonitoringMeasure oThingServiceMeasure = checkThingServiceExists(sThingServiceName);
      mLogger.debug("Component QoS Monitoring perform operation QoSManagerMonitoring.calculateSLAPull. "+sThingServiceName+" Thing Service.");
      sendData("Component QoS Monitoring perform operation QoSManagerMonitoring.calculateSLAPull. "+sThingServiceName+" Thing Service.", "info", "TaaSQoSMonitoring");
      mLogger.debug("Component QoS Monitoring perform operation QoSManagerMonitoring.calculateSLAPull. Param AVALILABILITY: "+oThingServiceMeasure.getAvailability()+".");

      boolean bExists = cmservice.checkAvailability(sThingServiceName);
      
      if (!(oThingServiceMeasure == null) && (bExists))
      {
        mLogger.debug("Component QoS Monitoring perform operation QoSManagerMonitoring.calculateSLAPull. Availability ok.");
        iSuccess++;

        int initialUnsucess = oThingServiceMeasure.getiUnsucess();
        if (initialUnsucess==1)
          iUnsuccess++;
        else{
          iSuccess++;
        }

        LOGTest.debug("Monitoring PULL End");
          //Constructs a Timestamp object using a seconds time value.
          sMaximumTimeStamp = cmservice.getMaximumResponseTime(sThingServiceName);
        
          
          Timestamp tsMaximum = oThingServiceMeasure.getMaximumTimeStamp();
          DateTime dt_now = new DateTime();

          Interval interval = new Interval(new Instant (tsMaximum), new Instant (dt_now));
          int intervalMillis = interval.toPeriod().getMillis();

          
          if ((intervalMillis-oThingServiceMeasure.getTolerateJitter()) > Integer.parseInt(sMaximumTimeStamp)){ 
            mLogger.debug("Component QoS Monitoring perform operation QoSManagerMonitoring.calculateSLAPull. Max Response Time violation.");
            iUnsuccess++;
          }
          else{
            mLogger.debug("Component QoS Monitoring perform operation QoSManagerMonitoring.calculateSLAPull. Max Response Time ok.");
            iSuccess++;
          }
        
      }else{
        mLogger.debug("Component QoS Monitoring perform operation QoSManagerMonitoring.calculateSLAPull. Availability violation.");
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
      mLogger.error("Component QoS Monitoring perform operation QoSManagerMonitoring.failureSLA. It has not been executed correctly. Exception: " + e.getMessage()+".");
    }
    return resultSLA;
  }



  public SLACalculation calculateSLAPush(String sThingServiceName, int iMilisecondTaaSRequestRate)
  {
      int iSuccess = 0;
      int iUnsuccess = 0;
      SLACalculation resultSLA = null;
      String sMaximumTimeStamp = null;
      
      try{
      
        QoSMonitoringMeasure oThingServiceMeasure = checkThingServiceExists(sThingServiceName);
        mLogger.debug("Component QoS Monitoring perform operation QoSManagerMonitoring.calculateSLAPush. "+sThingServiceName+" Thing Service.");
        sendData("Component QoS Monitoring perform operation QoSManagerMonitoring.calculateSLAPush. "+sThingServiceName+" Thing Service.", "info", "TaaSQoSMonitoring");
        mLogger.debug("Component QoS Monitoring perform operation QoSManagerMonitoring.calculateSLAPush. Param AVALILABILITY: "+oThingServiceMeasure.getAvailability()+".");

        boolean bExists = cmservice.checkAvailability(sThingServiceName);
        
        if (!(oThingServiceMeasure == null) && (bExists))
        {
          mLogger.debug("Component QoS Monitoring perform operation QoSManagerMonitoring.calculateSLAPush. Availability ok.");
          iSuccess++;

          int initialUnsucess = oThingServiceMeasure.getiUnsucess();
          if (initialUnsucess==1)
            iUnsuccess++;
          else{
            iSuccess++;
          }

          int iMilisecondPeriod = oThingServiceMeasure.getPeriod(); //isgMaxRequestRate
          LOGTest.debug("Monitoring PUSH End");
          mLogger.debug("Component QoS Monitoring perform operation QoSManagerMonitoring.calculateSLAPush. Param TIMESTAMP: iMilisecondTaaSRequestRate :"+iMilisecondTaaSRequestRate +", isgMaxRequestRate:"+iMilisecondPeriod+".");
          if (iMilisecondPeriod==0){ 
            mLogger.debug("Component QoS Monitoring perform operation QoSManagerMonitoring.calculateSLAPush. Max Response Time violation.");
            iUnsuccess++;
          }
          else{
          
            if ((iMilisecondTaaSRequestRate-oThingServiceMeasure.getTolerateJitter()) > iMilisecondPeriod){ 
              mLogger.debug("Component QoS Monitoring perform operation QoSManagerMonitoring.calculateSLAPush. Max Response Time violation.");
              iUnsuccess++;
            }
            else{
              mLogger.debug("Component QoS Monitoring perform operation QoSManagerMonitoring.calculateSLAPush. Max Response Time ok.");
              iSuccess++;
            }
          }
        }else{
          mLogger.debug("Component QoS Monitoring perform operation QoSManagerMonitoring.calculateSLAPush. Availability violation.");
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
  
  
  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }
  

}