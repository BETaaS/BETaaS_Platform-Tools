/**

Copyright 2013 ATOS SPAIN S.A. 

Licensed under the Apache License, Version 2.0 (the License);
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

Authors :
Oriol Collell, Francisco Javier Nieto. Atos Research and Innovation, Atos SPAIN SA
@email francisco.nieto@atosresearch.eu 

**/

package eu.betaas.taas.taasvmmanager.cloudsclients;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.ClientResponse.Status;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.ClientFilter;
import com.sun.jersey.api.client.filter.LoggingFilter;
import com.sun.jersey.core.util.Base64;

public class OCCI_RestClient 
{
    
    public static final String KIND_TAG = "kind";
    public static final String KIND = "http://optimis-project.eu/occi/schemas#optimis_compute";
    public static final String ATTRIBUTES_TAG = "attributes";
    public static final String ID_TAG = "id";
    public static final String ID_PREFIX = "vm";
    public static final String RESOURCES_TAG = "resources";
    public static final String SERVICES_TAG = "services";
    
    private static Logger log = Logger.getLogger("betaas");
    
    private String base_url = "http://optimis-ws.servidoresdns.net:8008/OptimisRestService.svc";
    
    private class AuthClientFilter extends ClientFilter {
        
        private String user = null;
        private String pass = null;
        
        public AuthClientFilter(String user, String pass) {
            this.user = user;
            this.pass = pass;
        }
        
        public ClientResponse handle(ClientRequest cr) {
            cr.getHeaders().add(HttpHeaders.AUTHORIZATION, "Basic " + new String(Base64.encode(user + ":" + pass), Charset.forName("ASCII")));
            ClientResponse resp = getNext().handle(cr);
            return resp;
        }
    }
    
    private Client client = null;
    
    public OCCI_RestClient(String user, String pass, String url) {        
        log.info("Creating OCCI Client...");
        ClientConfig cc = new DefaultClientConfig();
        //cc.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
        //cc.getFeatures().put(JSONJAXBContext.JSON_NOTATION, JSONJAXBContext.JSONNotation.MAPPED_JETTISON.name());
    	cc.getClasses().add(JSONObject.class);
        client = Client.create(cc);
        client.addFilter(new LoggingFilter(System.out));
        client.addFilter(new AuthClientFilter(user, pass));
        
        this.base_url = url;
    }
    
    public String createVM(String service_id, VMRequest request, int index) throws OCCIClientException 
    {
        WebResource r = client.resource(base_url + "/optimis_compute");
        try {
            //ClientResponse res = r.type(MediaType.APPLICATION_JSON_TYPE).post(ClientResponse.class, renderCompute(service_id, sc, index));        	
        	//ClientResponse res = r.type(MediaType.APPLICATION_JSON_TYPE).accept(MediaType.TEXT_HTML).post(ClientResponse.class, renderCompute(service_id, sc, index));        	
        	//ClientResponse res = r.type(MediaType.APPLICATION_JSON_TYPE).entity(input, MediaType.APPLICATION_JSON_TYPE).post(ClientResponse.class);
        	JSONObject input = renderCompute(service_id, request, index);
        	log.debug("Creating a VM through OCCI using:");
        	log.debug(input.toString());
        	ClientResponse res = r.type(MediaType.APPLICATION_JSON_TYPE).post(ClientResponse.class, input.toString());
            if (res.getStatus() != Status.CREATED.getStatusCode()) {
                throw new OCCIClientException("There was a problem while processing the request: " +
                        res.getStatus(), new Exception());
            }
            log.debug("Result obtained from remote Cloud:");
            log.debug(res.toString());
            //return res.getHeaders().get(HttpHeaders.LOCATION).get(0);
            return res.getLocation().toString();
        } catch (OCCIClientException e) {
            throw new OCCIClientException("There was a problem when transforming the service component to JSON format", e);
        }
    }
    
    public List<VMProperties> getServiceVMs(String serviceId) throws OCCIClientException {
        WebResource r = client.resource(base_url + "/vms/" + serviceId + "?monitoring=0");        
        try {
            //JSONObject res = r.accept(MediaType.APPLICATION_JSON_TYPE).get(JSONObject.class); 
            String occiRes = r.accept(MediaType.APPLICATION_JSON_TYPE).get(String.class);
            JSONObject res = new JSONObject (occiRes);
            JSONArray vms_json = res.getJSONArray(RESOURCES_TAG);
            List<VMProperties> vms = new LinkedList<VMProperties>();
            for (int i = 0;i < vms_json.length();i++) {
                vms.add(extractVMProperties(vms_json.getJSONObject(i)));
            }
            return vms;
        } catch (OCCIClientException e) {
            throw new OCCIClientException("There was a problem when extracting the information about the VMs", e);
        } catch (JSONException e) {
            throw new OCCIClientException("There was a problem when extracting the information about the VMs", e);
        }
    }
    
    public void terminateService(String serviceId) {
        WebResource r = client.resource(base_url + "/vms/" + serviceId);
        r.delete();
    }
        
    public void updateVM(String service_id, VMRequest request, int index) throws OCCIClientException {
        WebResource r = client.resource(base_url + "/vms/" + service_id + "/" + ID_PREFIX + index);
        try {
            ClientResponse res = r.type(MediaType.APPLICATION_JSON_TYPE).put(ClientResponse.class, renderCompute(service_id, request, index));
            if (res.getStatus() != Status.OK.getStatusCode()) {
                throw new OCCIClientException("There was a problem while processing the request: " +
                        res.getStatus(), new Exception());
            }
        } catch (OCCIClientException e) {
            throw new OCCIClientException("There was a problem when transforming the service component to JSON format", e);
        }
    }
    
    public void deleteVM(String serviceId, int index) {
        WebResource r = client.resource(base_url + "/vms/" + serviceId + "/" + ID_PREFIX + index);
        r.type(MediaType.APPLICATION_JSON).delete();
    }
    
    public void executeAction(String action, String serviceId, int index, Map<String, String> attrs) throws OCCIClientException {
        WebResource r = client.resource(base_url + "/vms/" + serviceId + "/" + ID_PREFIX + index + "?action=" + action);
        try {
            r.type(MediaType.APPLICATION_JSON).post(renderAction(attrs));
        } catch (OCCIClientException e) {
            throw new OCCIClientException("There was a problem when transforming the service component to JSON format", e);
        }
    }
    
    public VMProperties getVM(String serviceId, String instanceId) throws OCCIClientException {
        WebResource r = client.resource(base_url + "/vms/" + serviceId + "/" + instanceId);
        //JSONObject res = r.accept(MediaType.APPLICATION_JSON_TYPE).get(JSONObject.class);
        String occiRes = r.accept(MediaType.APPLICATION_JSON_TYPE).get(String.class);        
        try {
        	JSONObject res = new JSONObject (occiRes);
            VMProperties vms = extractVMProperties(res);
            return vms;
        } catch (OCCIClientException e) {
            throw new OCCIClientException("There was a problem when extracting the information about the VMs", e);
        }
        catch (JSONException ex) 
        {
        	throw new OCCIClientException("There was a problem parsing the VM JSON object", ex);
        }
    }
    
    public List<Service> getAllVMs() throws OCCIClientException {
        WebResource r = client.resource(base_url + "/vms");
        try {
            //JSONArray services_json = r.accept(MediaType.APPLICATION_JSON_TYPE).get(JSONArray.class);
            String occiResult = r.accept(MediaType.APPLICATION_JSON_TYPE).get(String.class);
            JSONArray services_json = new JSONArray (occiResult);
            //JSONArray services_json = r.getJSONArray(SERVICES_TAG);        	
            List<Service> services = new LinkedList<Service>();
            for (int i = 0;i < services_json.length();i++) {
                JSONObject service_json = services_json.getJSONObject(i);
                Service s = new Service(service_json.getString(ID_TAG));
                JSONArray vms_json = service_json.getJSONArray(RESOURCES_TAG);
                for (int j = 0;j < vms_json.length();j++) {
                    s.addVM(extractVMProperties(vms_json.getJSONObject(j)));
                }
                services.add(s);
            }
            return services;
        } catch (OCCIClientException e) {
            throw new OCCIClientException("There was a problem when extracting the information about the VMs", e);
        } catch (JSONException e) {
            throw new OCCIClientException("There was a problem when extracting the information about the VMs", e);
        }
    }
    
    private JSONObject renderCompute(String service_id, VMRequest request, Integer index) throws OCCIClientException{
        JSONObject json = new JSONObject();
        try {
            json.put(KIND_TAG, KIND);
            
            JSONObject attrs = new JSONObject();
            
            // Set x86 architecture by default, if a not-valid value is provided in the manifest
            String architecture = request.getArchitecture();
            if (!architecture.equalsIgnoreCase("x86") && !architecture.equalsIgnoreCase("x64"))
            {
            	setAttribute(VMRequest.OCCI_COMPUTE_ARCHITECTURE, attrs, "x86");
            }
            else
            {
            	setAttribute(VMRequest.OCCI_COMPUTE_ARCHITECTURE, attrs, architecture);
            }           
            
            setAttribute(VMRequest.OCCI_COMPUTE_CORES, attrs, request.getCores());
            setAttribute(VMRequest.OCCI_COMPUTE_MEMORY, attrs, (request.getMemory()/1024));
            setAttribute(VMRequest.OCCI_COMPUTE_SPEED, attrs, request.getSpeed());
            setAttribute(VMRequest.OPTIMIS_VM_IMAGE, attrs, request.getImage());
            setAttribute(VMRequest.OPTIMIS_SERVICE_ID, attrs, service_id);
            setAttribute(VMRequest.OCCI_COMPUTE_STATUS, attrs, "active");
            setAttribute(VMRequest.OCCI_COMPUTE_HOSTNAME, attrs, "10.10.10.10");
            json.put(ATTRIBUTES_TAG, attrs);           
            json.put(ID_TAG, "BETaaS" + service_id + index.toString());
        } catch (JSONException e) {
            throw new OCCIClientException("There was an error while trying to convert a "
                    + "ServiceComponent object into a JSON object", e);
        }
        log.debug ("Render Compute: " + json.toString());
        return json;
    }
    
    private JSONObject renderAction(Map<String, String> attrs) throws OCCIClientException {
        JSONObject json = new JSONObject();
        try {
            for (String key : attrs.keySet()) {
                json.put(key, attrs.get(key));
            }           
        } catch (JSONException e) {
            throw new OCCIClientException("There was an error while trying to convert a "
                    + "ServiceComponent object into a JSON object",e);
        }
        return json;
    }
        
    private void setAttribute(String attr_name, JSONObject attrs, Object value) throws JSONException {
        String parts[] = attr_name.split("\\.");
        setAttributeRec(0,parts,attrs, value);
    }
    
    private void setAttributeRec(int i, String[] parts, JSONObject attrs, Object value) throws JSONException {
        if (i == parts.length - 1) {
            attrs.put(parts[i], value);
            return;
        }
        if (attrs.has(parts[i])) {
            setAttributeRec(i+1, parts,attrs.getJSONObject(parts[i]), value);
        } else {
            attrs.put(parts[i], new JSONObject());
            setAttributeRec(i+1,parts,attrs.getJSONObject(parts[i]), value);
        }
    }
    
    private VMProperties extractVMProperties(JSONObject json) throws OCCIClientException {
        VMProperties vm = new VMProperties();
        try {
            JSONObject attrs = json.getJSONObject(ATTRIBUTES_TAG);
            vm.setHostname((String) getAttribute(VMProperties.OCCI_COMPUTE_HOSTNAME,attrs));
            vm.setStatus((String) getAttribute(VMProperties.OCCI_COMPUTE_STATUS,attrs));            
            vm.setId(json.getString(ID_TAG));
        } catch (JSONException e) {
            throw new OCCIClientException("There was an error while trying to convert a "
                    + "ServiceComponent object into a JSON object",e);
        }
        return vm;
    }
    
    private Object getAttribute(String attr_name, JSONObject attrs) throws JSONException {
        String parts[] = attr_name.split("\\.");
        return getAttributeRec(0,parts,attrs);
    }
    
    private Object getAttributeRec(int i, String[] parts, JSONObject attrs) throws JSONException {
        if (i == parts.length -1) {
            return attrs.get(parts[i]);
        }
        return getAttributeRec(i + 1, parts, attrs.getJSONObject(parts[i]));
    }
    
    public static void main(String[] args) {
        
        OCCI_RestClient cl = new OCCI_RestClient("servicemanager","opt1M1$12","http://optimis-ws.servidoresdns.net:8008/OptimisRestService.svc");
        
        List<VMRequest> scs = new ArrayList<VMRequest>();
        VMRequest c1 = new VMRequest();
        c1.setArchitecture("x86");
        c1.setCores(2);
        c1.setMemory(2.5);
        c1.setSpeed(0.6);
        c1.setImage("2a2917c2-1d7d-4269-9ad7-5cb2b0107b08.vmdk");
        c1.setInstances(2);
        scs.add(c1);
        
        try        
        {
        	//cl.terminateService("GeneDetectionBroker");
        	//cl.createVM("GeneDetectionBroker", "optimis-pm-PartD", c1, 3);
        	List<Service> myList = cl.getAllVMs();
        	Iterator<Service> myIter = myList.iterator();
        	while (myIter.hasNext())
        	{
        		Service myServ = myIter.next();
        		System.out.println ("Service id: " + myServ.getId());
        	}
        	VMProperties myProp = cl.getVM("GeneDetectionBroker", "optimis-pm-PartD4");
        	System.out.println ("IP address: " + myProp.getHostname());
        }
        catch (Exception ex)
        {
        	System.out.println ("Error");
        }
        
        /*
        try {
            JSONObject obj = cl.renderCompute("a", "c1", c1,1);
            System.out.println(obj.toString(1));
        } catch (Exception e) {
            e.printStackTrace();
        }
        */
        
    }
}
