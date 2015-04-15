//package eu.betaas.adaptation.contextmanager.api.impl;
//
//import static org.junit.Assert.*;
//
//import java.util.ArrayList;
//
//import org.junit.Before;
//import org.junit.BeforeClass;
//import org.junit.Test;
//
//import eu.betaas.adaptation.contextmanager.api.SemanticParserAdaptator;
//import eu.betaas.adaptation.contextmanager.onto.classesExt.commonUtils.ConfigBundleOSGi;
//import eu.betaas.adaptation.contextmanager.onto.classesExt.commonUtils.ConfigBundleOSGiImpl;
//import eu.betaas.taas.bigdatamanager.database.service.ThingsData;
//import eu.betaas.taas.contextmanager.api.ThingsServiceManager;
//
//public class SemanticParserAdaptatorImplTest
//{
//  private static SemanticParserAdaptator cmadaptation;
//  static private ThingsServiceManager cmservice;
//  
//  @BeforeClass
//  public static void setUpBeforeClass() throws Exception
//  {
//    
//        ThingsData td_thing1 = new ThingsData();
//        td_thing1.setOutput(true);
//        td_thing1.setMaximumResponseTime("1");
//        td_thing1.setMemoryStatus("50");
//        td_thing1.setComputationalCost("0.1");
//        td_thing1.setBatteryLevel("50");
//        td_thing1.setBatteryCost("10");
//        td_thing1.setMeasurement("false");
//        td_thing1.setProtocol("etsi");
//        td_thing1.setDeviceID("pir1");
//        td_thing1.setThingId("000012");
//        td_thing1.setType("presence");
//        td_thing1.setUnit("boolean");
//        td_thing1.setEnvironment(true);
//        td_thing1.setLatitude("43");
//        td_thing1.setLongitude("10.437342");
//        td_thing1.setAltitude("7.0");
//        td_thing1.setFloor("1");
//        td_thing1.setLocationKeyword("kitchen");
//        td_thing1.setLocationIdentifier("1");
//        
//        ArrayList<ThingsData> oThingsDataList = new ArrayList<ThingsData>();
//        oThingsDataList.add(td_thing1);
//        
//        cmadaptation = new SemanticParserAdaptatorImpl();
////        SemanticParserAdaptator spa = new SemanticParserAdaptator();
//        cmadaptation.publishThingInit(oThingsDataList);
//        
//        ConfigBundleOSGi oConfigOSGi = ConfigBundleOSGiImpl.getInstance();
//        cmservice = oConfigOSGi.getCmservice();
//        cmservice.getContextualMeasurement("get1KitchenPresence_pir1_01");
//            
//        cmservice.getContextThingServices("presence","home", null, "real time pull");
//  }
//
//  @Before
//  public void setUp() throws Exception
//  {
//  }
//
//  @Test
//  public void test()
//  {
//    fail("Not yet implemented");
//  }
//
//}
