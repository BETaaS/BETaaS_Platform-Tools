**BETaaS Android app Example**
===================


**BETaaS Android app** external application is an example showing how to use the Android SDK to interact with the BETaaS REST iterface and get sensor information to implement a simple alarm system.

### BETaaS software pre requirements

It is necessary that presence sensors (or the proper simulator) can be connected to BETaaS through a suitable adapter plug-in and, if you want to modify the code or use it to build you own app, you have the [Android SDK](https://developer.android.com/sdk/index.html) [installed](https://developer.android.com/sdk/installing/index.html). If you only want to test it, the SDK is not required, since an app binary is provided.

### BETaaS components used

Most of BETaaS components are used to allow the resources allocation and the interaction with the sensors. The application itself interact with the Service Manager, which provides the REST API.

## BETaaS - How to

#### Step 1 - The Manifest

The manifest contains specifications about the features that the application requires to run. Specifically, one feature is requested: **PRESENCE**. All the available Thing Services created on top of devices that have that type (*type* field in the CSV context file, see above) will be used to fulfill the manifest. Of course all the other constraints must be met about power consumption, location, etc.

The manifest also contains the **credentials** to access the platform. See platform installation instructions to get valid credentials according to the certificates of the used BETaaS GWs.

Since the application is suited for PULL data mode, not push, the notification address specified in the manifest is required for comatibility, but it has no real use.

#### Step 2 - Connecting the Things

Before starting the application, connect the presence Things having the same identifiers used to name the CSV context files (see BETaaS gateway configuration - context files folder). The corresponding Thing Services will be created. 

Things may also be simulated through the TA simulator.

#### Step 3 - Installing the App

The app binary file can be found under the /bin project folder, it's named BETaaS.apk. To allow the installation of applications from other sources than the Google Play market, you should enable this feature in your phone. It is located at Settings→Security→Unknown Sources and activate it. After that, there are several ways to install it:

 * Using the Android Platform Tools. To use them, you should have installed the Android SDK previously. When done, go to the <Android SDK>/platform-tools folder using a command line interface and perform the following command:

```
adb install <path to BETaaS.apk file>\BETaaS.apk   #in Windows
./adb install <path to BETaaS.apk file>\BETaaS.apk #in Unix-like systems
```

 * Using PC to Smartphone communication programs like AirDroid. These kind of applications allow the transfer of several kind of data (like images or sounds) easily, including apps.

 * Uploading the BETaaS.apk file to any kind of server and downloading it using your smartphone. This, when detecting you download an apk file, should ask you if you want to install it.

#### Step 4 - Connecting to the service

At the beggining, when you have no Gateway connection configured, the "Add Gateway" activity will show up. There, you have the option of adding a new one, setting its name, the IP where the desired Gateway to use is running and the port of the REST interface (8181 by default). Then, tap on the "Add" button and the application will install and register to the service of the Gateway. It will show the message "Relax, no one is at home" or, eventually, "Caution! There is someone at home".

The menu of the app shows the connected gateways, as well as the option of adding a new gateway. In this way, we can get the information of several gateways. To remove the connection to a gateway, a long press on the corresponding item in the menu, will show an option to delete it.

To save battery, the app does only retrieve data from the gateway when its activity is showed. It does not run as a backgrounded service so, when changing the shown gateway or exiting the application, the previous gateway information gathering will be paused.

### Source Code Explanation

As explained before, in the beggining, the application requires the connection data for a gateway. Then, the main Gateway view is shown. The main interfaces are implemented as activities or fragments. For example, the GatewayFragment.java implements the interface control to show the information gathered from a Gateway. It manages the lifecycle of the installation, subscription and unsubscription of the app and services, and sets a timer to gather the sensors data every 5 seconds.

The interface is decoupled from the backend using a proxy class called GatewayManager.java. It implements all the needed methods to manage the lifecycle like install application, subscribing to a service, getting the data. Thus, the way to get the data (in this case the REST communication) is hidden to the interface. In this way, we could support other connectors, like a SOAP connector transparently from the interface classes point of view. In fact, there is a legacy SOAP connector, currently disabled, but that may be further developed to serve as an example. All the time consuming methods defined in the GatewayManager class, like those involved with the HTTP communication or the database operations, are performed in an asynchronous way and using callbacks, following the best practices in Android, that states that the interface thread should not perform time consuming operations. Also, the GatewayManager handles different state of the Application to manage the creation and destruction of the interface elements, thus time consuming operations are tolerant to screen rotation.

Finally, we can distinguish a backend layer, where some classes implement the communication with the gateway. We highlint GatewayCommunicatorRestImpl.java and RESTClient.java. The first one sets the needed data for to interact with the REST API for every operation defined in the GatewayManager.java. For example, it implements a InstallAplication method, where the headers and needed request body is set to finally, call a method on the RESTClient class. The RESTClient class implements the most basic communication with the REST API, and is used by every method declared in the GatewayCommunicatorRestImpl class. It provides basic GET, POST, PUT and DELETE methods, that get the needed data and information and returns the server's response and data back to the GatewayCommunicatorRestImpl methods. Then, this is transformed to usable objects.

Below we show some of the REST API methods, they are generic calls to the BETaaS API that should be performed in different threads:

```
public String[] getResource(String path, Map<HeaderType, String> headers) {
    HttpHost target = new HttpHost(endpointUrl, port);
        
    String result[];
        
    try {
        // specify the get request
        HttpGet getRequest = new HttpGet(path);
        for (HeaderType header : headers.keySet()) {
            getRequest.setHeader(HEADERS.get(header), headers.get(header));
        }
    
        HttpResponse httpResponse = httpclient.execute(target, getRequest);
    
        result = getResponse(httpResponse);
    
    } catch (Exception e) {
        result = new String[2];
        result[0] = "clientException";
        result[1] = e.getMessage();
    } 
        
    return result;
}

public String[] putResource(String path, Map<HeaderType, String> headers) {
    HttpHost target = new HttpHost(endpointUrl, port);
        
    String result[];
       
    try {
        // specify the get request
        HttpPut putRequest = new HttpPut(path);
        for (HeaderType header : headers.keySet()) {
            putRequest.setHeader(HEADERS.get(header), headers.get(header));
        }
        
        HttpResponse httpResponse = httpclient.execute(target, putRequest);
      
        result = getResponse(httpResponse);
        
    } catch (Exception e) {
        result = new String[2];
        result[0] = "clientException";
        result[1] = e.getMessage();
    } 
        
    return result;
}
    
public String[] postResource(String path, Map<HeaderType, String> headers, String body) {
    HttpHost target = new HttpHost(endpointUrl, port);
       
    String result[];
        
    try {
        // specify the get request
        HttpPost postRequest = new HttpPost(path);
        for (HeaderType header : headers.keySet()) {
            postRequest.setHeader(HEADERS.get(header), headers.get(header));
        }
            
        if (body != null) {
            HttpEntity b = new ByteArrayEntity(body.getBytes("UTF-8"));
            postRequest.setEntity(b);
        }

        HttpResponse httpResponse = httpclient.execute(target, postRequest);

        result = getResponse(httpResponse);

    } catch (Exception e) {
        result = new String[2];
        result[0] = "clientException";
        result[1] = e.getMessage();
    }
    
    return result;
}

public String[] deleteResource(String path, Map<HeaderType, String> headers) {
    HttpHost target = new HttpHost(endpointUrl, port);
    
    String[] result;
    
    try {
        // specify the post request
        HttpDelete delRequest = new HttpDelete(path);
        for (HeaderType header : headers.keySet()) {
            delRequest.setHeader(HEADERS.get(header), headers.get(header));
        }
    
        HttpResponse httpResponse = httpclient.execute(target, delRequest);
        
        result = getResponse(httpResponse);
    
    } catch (Exception e) {
        result = new String[2];
        result[0] = "clientException";
        result[1] = e.getMessage();
    } 
    
    return result;
}
```

The methods shown above share most of their code, since we planned to do a very simple generic client. The only appreciable difference is the admittance of a *body* paramenter, applicable only to the cases we need. The *getResponse* method simply formats the raw response received from the BETaaS REST API to a better suited structure. If you wanted to use one of this methods to operate with BETaaS, the following could be done:

```
public Measurement getPresence(Gateway gateway,
                               String  serviceId)
                                        throws GatewayCommunicatorException{
    String appId = gateway.getAppId();
    
    String path = BASE_PATH
            + RESOURCES.get(ResourceType.DATA)
            + appId + "/" + serviceId;
    Map<HeaderType, String> headers = new HashMap<HeaderType, String>();
    headers.put(HeaderType.TOKEN, gateway.getTokens().get(0));
    
    String[] result = restClient.getResource(path, headers);
    
    if (!result[0].contains("200 OK")) {
        throw new GatewayCommunicatorException(
                gateway,
                result[0],
                "Getting data: " + result[1]);
    }
    return new Measurement(Calendar.getInstance().getTimeInMillis(),
            Boolean.parseBoolean(result[1]),
            gateway.getId(),
            serviceId);
}
```

This method gets the information of presence from the BETaaS platform. To do that, it receives information about the gateway to communicate with and the service assigned to that task. The needed token is set in the headers, the corresponding URL of the resource is set and, with that information the communication is delegated to the *getResource* method. After that, the result is properly handled.

## About BETaaS Project

#### ** About the consortium**

BETaaS is a STREP Research project developed and co-funded by the European Commission Research and Innovation 7th Framework Program by the [BETaaS Consortium](http://www.betaas.eu/consortium.html#.VEeGuhZvAgk) under the ICT theme (Call 8) of DG CONNECT.

#### ** About BETaaS License**

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

[http://www.apache.org/licenses/LICENSE-2.0](http://www.apache.org/licenses/LICENSE-2.0)

Unless required by applicable law or agreed to in writing, software  distributed under the License is distributed on an "AS IS" BASIS,  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and  limitations under the License.


2014 All Rights Reserved by Intecs Spa
