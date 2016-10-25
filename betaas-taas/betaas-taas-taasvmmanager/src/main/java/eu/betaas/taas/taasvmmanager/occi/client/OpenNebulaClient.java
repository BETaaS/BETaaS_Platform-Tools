/**

Copyright 2014 ATOS SPAIN S.A.

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
Francisco Javier Nieto. Atos Research and Innovation, Atos SPAIN SA
@email francisco.nieto@atos.net 
Sergio García Villalonga. Atos Research and Innovation, Atos SPAIN SA
@email sergio.garciavillalonga@atos.net
**/

package eu.betaas.taas.taasvmmanager.occi.client;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.List;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.xml.transform.stream.StreamSource;
import javax.xml.bind.*;

import org.apache.log4j.Logger;
import org.jdom.JDOMException;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.ClientFilter;
import com.sun.jersey.core.util.Base64;

import eu.betaas.taas.taasvmmanager.api.datamodel.InstanceType;
import eu.betaas.taas.taasvmmanager.occi.OCCIClient;
import eu.betaas.taas.taasvmmanager.occi.OCCIException;
import eu.betaas.taas.taasvmmanager.occi.datamodel.Compute;
import eu.betaas.taas.taasvmmanager.occi.datamodel.ComputeCollection;
import eu.betaas.taas.taasvmmanager.occi.datamodel.ComputeState;
import eu.betaas.taas.taasvmmanager.occi.datamodel.InstanceTypeCollection;
import eu.betaas.taas.taasvmmanager.occi.datamodel.Link;
import eu.betaas.taas.taasvmmanager.occi.datamodel.Network;
import eu.betaas.taas.taasvmmanager.occi.datamodel.NetworkCollection;
import eu.betaas.taas.taasvmmanager.occi.datamodel.Storage;
import eu.betaas.taas.taasvmmanager.occi.datamodel.StorageCollection;
import eu.betaas.taas.taasvmmanager.occi.datamodel.StorageType;
import eu.betaas.taas.taasvmmanager.occi.datamodel.User;
import eu.betaas.taas.taasvmmanager.occi.datamodel.UserCollection;
import eu.betaas.taas.taasvmmanager.occi.datamodel.Compute.Context;
import eu.betaas.taas.taasvmmanager.occi.datamodel.Compute.Disk;
import eu.betaas.taas.taasvmmanager.occi.datamodel.Compute.Nic;
import eu.betaas.taas.taasvmmanager.opennebula.util.OCCIProcesser;

/**
* 
* @author Francisco Javier Nieto De-Santos (francisco.nieto@atos.net)
* @author Sergio Garcia Villalonga (sergio.garciavillalonga@atos.net)
*/
public class OpenNebulaClient implements OCCIClient {

	private OCCIClientStatus status;
	private Client client    = null;
	private Link   userLink  = null;
	private String userGroup = null;
	private String endpointUrl;
	private static Logger log = Logger.getLogger("betaas.taas");
	
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
	
	public OpenNebulaClient (String endpointUrl, String user, String password) {
		status = new OCCIClientStatus();
		this.endpointUrl = endpointUrl;
		
		log.info("Creating OCCI Client...");
		client = Client.create();
        client.addFilter(new AuthClientFilter(user, password));
        
        try {
        	UserCollection users = getUsers();
        	for (Link link : users.getUser()) {
        		if (link.getName().equals(user)) {
        			userLink = link;
        		}
        	}
        	
        	User userObject = getUser(userLink.getHref());
        	userGroup = userObject.getGroup();
        	
        	//Check if the images have been uploaded
        	
        } catch (OCCIException e) {
        	status.setStatus(CloudStatus.FAILED);
            status.setErrorMessage(e.getMessage());        	
            log.error(e.getMessage());
        }
        
        status.setStatus(CloudStatus.OK);
	}
	
	private Object getResource(String href, Class classToBeBound)
											throws OCCIException {
		String processedMessage;
		OCCIProcesser processer = new OCCIProcesser();
		Object resource = null;
		WebResource r = client.resource(href);
		ClientResponse cRes = r.accept(MediaType.APPLICATION_XML)
		                       .get(ClientResponse.class);

		if (cRes.getStatus() != ClientResponse.Status.OK.getStatusCode()) {
			throw new OCCIException(cRes);
		}
		
		try {
			processedMessage = processer.tags2LowerCase(cRes.getEntity(String.class));
			
			log.debug(processedMessage);
			
			JAXBContext jaxbContext = JAXBContext.newInstance(classToBeBound);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			resource = jaxbUnmarshaller.unmarshal(
					new StreamSource( new StringReader( processedMessage )));
		} catch (JAXBException e) {
			log.error(e.getMessage());
		} catch (ClientHandlerException e) {
			log.error(e.getMessage());
		} catch (UniformInterfaceException e) {
			log.error(e.getMessage());
		} catch (JDOMException e) {
			log.error(e.getMessage());
		} catch (IOException e) {
			log.error(e.getMessage());
		}
		
		return resource;
	}
	
	private Object postResource (Object inputResource,
	                             String href,
	                             Class classToBeBound) throws OCCIException {
		ByteArrayOutputStream marshalledObject = new ByteArrayOutputStream();
		String returnMessage;
		OCCIProcesser processer = new OCCIProcesser();
		Object outputResource = null;
		WebResource r = client.resource(href);
		
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(classToBeBound);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			jaxbMarshaller.marshal(inputResource, marshalledObject);
			
			ClientResponse cRes = r.accept(MediaType.APPLICATION_XML)
                    .post(ClientResponse.class,
                 		   processer.tags2UpperCase(marshalledObject.toString()));

			if (cRes.getStatus() != ClientResponse.Status.CREATED.getStatusCode()) {
				throw new OCCIException(cRes);
			}
			
			returnMessage = processer.tags2LowerCase(cRes.getEntity(String.class));
			
			log.debug(returnMessage);
			
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			outputResource = jaxbUnmarshaller.unmarshal(
					new StreamSource( new StringReader( returnMessage )));
		} catch (JAXBException e) {
			log.error(e.getMessage());
		} catch (ClientHandlerException e) {
			log.error(e.getMessage());
		} catch (UniformInterfaceException e) {
			log.error(e.getMessage());
		} catch (JDOMException e) {
			log.error(e.getMessage());
		} catch (IOException e) {
			log.error(e.getMessage());
		}
		
		return outputResource;
	}
	
	private Object putResource (Object inputResource,
                                 String href,
                                 Class classToBeBound) throws OCCIException {
		ByteArrayOutputStream marshalledObject = new ByteArrayOutputStream();
		String returnMessage;
		OCCIProcesser processer = new OCCIProcesser();
		Object outputResource = null;
		WebResource r = client.resource(href);
		
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(classToBeBound);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			jaxbMarshaller.marshal(inputResource, marshalledObject);
			
			ClientResponse cRes = r.accept(MediaType.APPLICATION_XML)
                    .put(ClientResponse.class,
                 		   processer.tags2UpperCase(marshalledObject.toString()));

			if (cRes.getStatus() != ClientResponse.Status.OK.getStatusCode()) {
				throw new OCCIException(cRes);
			}
			
			returnMessage = processer.tags2LowerCase(cRes.getEntity(String.class));
			
			log.debug(returnMessage);
			
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			outputResource = jaxbUnmarshaller.unmarshal(
					new StreamSource( new StringReader( returnMessage )));
		} catch (JAXBException e) {
			log.error(e.getMessage());
		} catch (ClientHandlerException e) {
			log.error(e.getMessage());
		} catch (UniformInterfaceException e) {
			log.error(e.getMessage());
		} catch (JDOMException e) {
			log.error(e.getMessage());
		} catch (IOException e) {
			log.error(e.getMessage());
		}
		
		return outputResource;
	}
	
	private void deleteResource (String href) throws OCCIException {
		WebResource r = client.resource(href);
		
		ClientResponse cRes = r.accept(MediaType.APPLICATION_XML)
                .delete(ClientResponse.class);

		if (cRes.getStatus() != ClientResponse.Status.NO_CONTENT.getStatusCode()) {
			throw new OCCIException(cRes);
		}
	}
	
	public UserCollection getUsers() throws OCCIException {
		String href = endpointUrl + USERPATH;
		return (UserCollection) getResource(href, UserCollection.class);
	}

	public User getUser(String id) throws OCCIException {
		OCCIProcesser processer = new OCCIProcesser();
		String href = endpointUrl + USERPATH + "/" + processer.getIdFromHref(id);
		return (User) getResource(href, User.class);
	}

	public NetworkCollection getNetworks() throws OCCIException {
		String href = endpointUrl + NETWORKPATH;
		return (NetworkCollection) getResource(href, NetworkCollection.class);
	}

	public Network getNetwork(String id) throws OCCIException {
		OCCIProcesser processer = new OCCIProcesser();
		String href = endpointUrl + NETWORKPATH + "/" + processer.getIdFromHref(id);
		return (Network) getResource(href, Network.class);
	}

	public Network createNetwork(String name, String description,
			String address, String size) throws OCCIException {
		String href;
		Network network = new Network();
		network.setGroup(userGroup);
		network.setAddress(address);
		network.setDescription(description);
		network.setSize(size);
		
		href = endpointUrl + NETWORKPATH; 
		
		network = (Network) postResource(network, href, Network.class);
		
		return network;
	}

	public void deleteNetwork(String id) throws OCCIException {
		OCCIProcesser processer = new OCCIProcesser();
		deleteResource(endpointUrl
				     + NETWORKPATH + "/"
				     + processer.getIdFromHref(id));
	}

	public StorageCollection getStorages() throws OCCIException {
		String href = endpointUrl + STORAGEPATH;
		return (StorageCollection) getResource(href, StorageCollection.class);
	}

	public Storage getStorage(String id) throws OCCIException {
		OCCIProcesser processer = new OCCIProcesser();
		String href = endpointUrl + STORAGEPATH + "/" + processer.getIdFromHref(id);
		return (Storage) getResource(href, Storage.class);
	}

	public Storage createStorage(String name, String description,
			StorageType type, int size, String fstype) throws OCCIException {
		String href;
		Storage storage = new Storage();
		storage.setGroup(userGroup);
		storage.setDescription(description);
		storage.setSize(Integer.toString(size));
		storage.setType(type);
		storage.setFstype(fstype);
				
		href = endpointUrl + STORAGEPATH; 
		
		storage = (Storage) postResource(storage, href, Storage.class);
		
		return storage;
	}

	public void deleteStorage(String id) throws OCCIException {
		OCCIProcesser processer = new OCCIProcesser();
		deleteResource(endpointUrl
				     + STORAGEPATH + "/"
				     + processer.getIdFromHref(id));
	}

	public InstanceTypeCollection getInstanceTypes() throws OCCIException {
		String href = endpointUrl + INSTANCETYPEPATH;
		return (InstanceTypeCollection) getResource(href, InstanceTypeCollection.class);
	}

	public InstanceType getInstanceType(String id) throws OCCIException {
		OCCIProcesser processer = new OCCIProcesser();
		String href = endpointUrl + INSTANCETYPEPATH + "/" + processer.getIdFromHref(id);
		return (InstanceType) getResource(href, InstanceType.class);
	}

	public Disk createComputeDisk(Storage storage, String target)
			throws OCCIException {
		Link storageLink = new Link();
		storageLink.setHref(storage.getHref());
		storageLink.setName(storage.getName());
		
		Disk disk = new Disk();
		disk.setStorage(storageLink);
		disk.setTarget(target);
		disk.setType(storage.getType());
		
		return disk;
	}

	public Nic createComputeNic(Network network, String ip, String mac)
			throws OCCIException {
		Link networkLink = new Link();
		networkLink.setHref(network.getHref());
		networkLink.setName(network.getName());
		
		Nic nic = new Nic();
		nic.setIp(ip);
		nic.setMac(mac);
		nic.setNetwork(networkLink);
		
		return nic;
	}

	public ComputeCollection getComputes() throws OCCIException {
		String href = endpointUrl + COMPUTEPATH;
		return (ComputeCollection) getResource(href, ComputeCollection.class);
	}

	public Compute getCompute(String id) throws OCCIException {
		OCCIProcesser processer = new OCCIProcesser();
		String href = endpointUrl + COMPUTEPATH + "/" + processer.getIdFromHref(id);
		return (Compute) getResource(href, Compute.class);
	}

	public Compute createCompute(String name, int cpu, int memory,
			InstanceType instanceType, List<Disk> disks, List<Nic> nic)
			throws OCCIException {
		String href;
		Compute compute = new Compute();
		compute.setContext(new Context());
		compute.setUser(userLink);
		compute.setGroup(userGroup);
		compute.setInstanceType(instanceType.getName());
		compute.setName(name);
		compute.getDisk().addAll(disks);
		compute.getNic().addAll(nic);
		
		if (instanceType.getName().equals("custom")) {
			compute.setCpu(cpu);
			compute.setMemory(memory);
		}
		
		href = endpointUrl + COMPUTEPATH; 
		
		compute = (Compute) postResource(compute, href, Compute.class);
		
		return compute;
	}

	public void changeComputeState(String id, BETaaSComputeState newState)
			throws OCCIException {
		OCCIException exception;
		if (newState == BETaaSComputeState.FAILED) {
			exception = new OCCIException();
			exception.setMessage(newState.toString() +
					" is not a valid state.");
		}
		
		OCCIProcesser processer = new OCCIProcesser();
		String href = endpointUrl
			        + COMPUTEPATH + "/"
			        + processer.getIdFromHref(id);
		Compute compute = getCompute(id);
		compute.setState(ComputeState.fromValue(newState.toString()));
		compute = (Compute) putResource(compute, href, Compute.class);
	}

	public void saveComputeDisk(Compute compute, String storageId, String name)
			throws OCCIException {
		OCCIProcesser processer = new OCCIProcesser();
		String href = endpointUrl
			        + COMPUTEPATH + "/"
			        + processer.getIdFromHref(storageId);
		Compute saveAsCompute = new Compute();
		
		saveAsCompute.setHref(compute.getHref());
		Disk saveAsDisk;
		Link saveAsLink;
		for (Disk disk : compute.getDisk()) {
			if (disk.getStorage().getHref().equals(storageId)) {
				saveAsDisk = new Disk();
				saveAsLink = new Link();
				saveAsLink.setHref(disk.getStorage().getHref());
				saveAsLink.setName(name);
				saveAsDisk.setSaveAs(saveAsLink);
				saveAsCompute.getDisk().add(saveAsDisk);
			}
		}
		
		compute = (Compute) putResource(saveAsCompute, href, Compute.class);
	}

	public void deleteCompute(String id) throws OCCIException {
		OCCIProcesser processer = new OCCIProcesser();
		deleteResource(endpointUrl
				     + COMPUTEPATH + "/"
				     + processer.getIdFromHref(id));
	}

	public OCCIClientStatus getStatus() {
		return status;
	}
}
