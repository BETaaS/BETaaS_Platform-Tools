//import static org.junit.Assert.*;
//
//import org.junit.Before;
//import org.junit.BeforeClass;
//import org.junit.Test;
//
//import eu.betaas.taas.contextmanager.api.ThingsServiceManager;
//import eu.betaas.taas.contextmanager.api.impl.ThingServiceList;
//import eu.betaas.taas.contextmanager.api.impl.ThingsServiceManagerImpl;
//
//
//public class ContextManagerTest
//{
//
//  private ThingsServiceManager oThingsServiceManager;
//  private ThingServiceList oThingServiceList;
//  
//  @BeforeClass
//  public void setUpBeforeClass() throws Exception
//  {
//    oThingsServiceManager = new ThingsServiceManagerImpl();
//    oThingServiceList = oThingsServiceManager.getContextThingServices("presence","home","real time pull");
//    
//  }
//
//  @Test
//  public void testGetContextThingServices()
//  {
//    String sOperator = oThingServiceList.getsOperator();
//    assertEquals("OR",sOperator);
//  }
//
//}
