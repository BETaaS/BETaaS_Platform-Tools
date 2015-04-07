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
// Component: Context Manager, Adaptation Module
// Responsible: Tecnalia
package eu.betaas.adaptation.contextmanager.onto.classesExt.commonUtils;

import java.sql.Timestamp;

import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import eu.betaas.taas.bigdatamanager.core.services.ITaasBigDataManager;
import eu.betaas.taas.contextmanager.api.ThingsServiceManager;
import eu.betaas.taas.taasresourcesmanager.api.TaaSResourceManager;
import eu.betaas.taas.qosmanager.api.QoSManagerInternalIF;

import eu.betaas.rabbitmq.publisher.interfaces.Publisher;
import eu.betaas.rabbitmq.publisher.interfaces.utils.Message;
import eu.betaas.rabbitmq.publisher.interfaces.utils.Message.Layer;
import eu.betaas.rabbitmq.publisher.interfaces.utils.MessageBuilder;

public class ConfigBundleOSGiImpl implements ConfigBundleOSGi
{
  // PUBLIC SECTION
  public final static String LOGGER_NAME = "betaas.adaptation";

  // PROTECTED SECTION
  private static Logger mLogger = Logger.getLogger(LOGGER_NAME);
  private ITaasBigDataManager bdservice;
  private TaaSResourceManager rmservice;
  private ThingsServiceManager cmservice;
  private QoSManagerInternalIF qosservice;
  private static ConfigBundleOSGiImpl thing = null;
  private String mGWID;
  
  private boolean enabledbus=false;
  private List<String> messageBuffer = new Vector<String>();
//  private String key = "monitoring.adaptation.contextmanager";
  private String key = "monitoring.adaptation";
  private BundleContext context; 

  private ConfigBundleOSGiImpl()
  {
    super();
  }

  public static ConfigBundleOSGiImpl getInstance()
  {
    if (thing == null)
    {
      thing = new ConfigBundleOSGiImpl();
    }
    return thing;
  }

  public void readConfigFileOSGi() throws Exception
  {
    try
    {
      mLogger.info("Component Adaptation CM has started.");
      mLogger.debug("Bus is "+enabledbus);
    }
    catch (Exception e)
    {
      mLogger
          .error("Component CM perform operation readConfigFileOSGi. It has not been executed correctly. Exception: "
              + e.getMessage() + ".");
    }
  }

  public void closeConfigFileOSGi()
  {
    try
    {
      // this.thing = null;
      mLogger.info("Component Adaptation CM has stopped.");
    }
    catch (Exception e)
    {
      mLogger
          .error("Component CM perform operation closeConfigFileOSGi. It has not been executed correctly. Exception: "
              + e.getMessage() + ".");
    }
  }

  public void setBdservice(ITaasBigDataManager bdservice)
  {
    this.bdservice = bdservice;
  }

  public ITaasBigDataManager getBdservice()
  {
    return this.bdservice;
  }

  public void setRmservice(TaaSResourceManager rmservice)
  {
    this.rmservice = rmservice;
  }
  
  public void setQosservice(QoSManagerInternalIF qosservice)
  {
    this.qosservice = qosservice;
  }

  public TaaSResourceManager getRmservice()
  {
    return this.rmservice;
  }

  public void setCmservice(ThingsServiceManager cmservice)
  {
    this.cmservice = cmservice;
  }

  public ThingsServiceManager getCmservice()
  {
    return this.cmservice;
  }

  public QoSManagerInternalIF getQosservice()
  {
    return this.qosservice;
  }
  
  public void setGwId(String gwId)
  {
    gwId = String.format("%02d", Integer.parseInt(gwId));
    mGWID = gwId;
  }

  public String getGwId()
  {
    return mGWID;
  }
  
  public void busMessage(String message){
    mLogger.debug("Checking queue");
    if (!enabledbus)return;
    mLogger.debug("Sending to queue");
    ServiceReference serviceReference = context.getServiceReference(Publisher.class.getName());
    if (serviceReference==null){
      mLogger.warn("Requested to publish data to queue, but service betaas publisher not found");
      messageBuffer.add(message);
      return;
    }
    Publisher service = (Publisher) context.getService(serviceReference); 
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
  
    mLogger.debug("This is the message built '"+message+"'.");
    
    mLogger.debug("Sending to "); 
    service.publish(key,message);
    mLogger.debug("Sent to queue " + key);
  }
  
  public void sendData(String description, String level, String originator) {
    java.util.Date date= new java.util.Date();
    Timestamp timestamp = new Timestamp(date.getTime());
    Message msg = new Message();
    msg.setDescritpion(description);
    msg.setLayer(Layer.ADAPTATION);
    msg.setLevel(level);
    msg.setOrigin(originator);
    msg.setTimestamp(timestamp.getTime());
    MessageBuilder msgBuilder = new MessageBuilder();
    String json = msgBuilder.getJsonEquivalent(msg);
    busMessage(json);   
  }
  
  public BundleContext getContext() {
    return context;
  }

  public void setContext(BundleContext context) {
    this.context = context;
  }
  
  public boolean isEnabledbus() {
    return enabledbus;
  }

  public void setEnabledbus(boolean enabledbus) {
    this.enabledbus = enabledbus;
  }
}
