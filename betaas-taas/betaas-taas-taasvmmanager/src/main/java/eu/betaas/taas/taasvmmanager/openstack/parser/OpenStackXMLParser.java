package eu.betaas.taas.taasvmmanager.openstack.parser;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;

import com.google.gson.Gson;

import eu.betaas.taas.taasvmmanager.api.datamodel.InstanceType;
import eu.betaas.taas.taasvmmanager.occi.OCCIException;
import eu.betaas.taas.taasvmmanager.occi.datamodel.Compute;
import eu.betaas.taas.taasvmmanager.occi.datamodel.Compute.Disk;
import eu.betaas.taas.taasvmmanager.occi.datamodel.Compute.Nic;
import eu.betaas.taas.taasvmmanager.occi.datamodel.ComputeCollection;
import eu.betaas.taas.taasvmmanager.occi.datamodel.InstanceTypeCollection;
import eu.betaas.taas.taasvmmanager.occi.datamodel.Link;
import eu.betaas.taas.taasvmmanager.occi.datamodel.Network;
import eu.betaas.taas.taasvmmanager.occi.datamodel.NetworkCollection;
import eu.betaas.taas.taasvmmanager.occi.datamodel.Storage;
import eu.betaas.taas.taasvmmanager.occi.datamodel.StorageCollection;
import eu.betaas.taas.taasvmmanager.occi.datamodel.StorageType;
import eu.betaas.taas.taasvmmanager.occi.datamodel.User.Quota;
import eu.betaas.taas.taasvmmanager.openstack.gson.Image;
import eu.betaas.taas.taasvmmanager.openstack.gson.ImageCollection;

public class OpenStackXMLParser extends OpenStackParser {
    private static Logger log = Logger.getLogger("betaas.taas");
    
    public static final String KEYSTONENAMESPACE =
            "http://docs.openstack.org/identity/api/v2.0";
    public static final String QUANTUMNAMESPACE =
            "http://openstack.org/quantum/api/v2.0";
    public static final String PROVIDERNAMESPACE =
            "http://docs.openstack.org/ext/provider/api/v1.0";
    public static final String ROUTERNAMESPACE =
            "http://docs.openstack.org/ext/neutron/router/api/v1.0";
    public static final String XSINAMESPACE =
            "http://www.w3.org/2001/XMLSchema-instance";
    public static final String VOLUMENAMESPACE =
            "http://docs.openstack.org/volume/api/v1";
    public static final String ATOMNAMESPACE =
            "http://www.w3.org/2005/Atom";
    public static final String COMPUTENAMESPACE =
            "http://docs.openstack.org/compute/api/v1.1";
    public static final String VIFNAMESPACE =
    		"http://docs.openstack.org/compute/ext/extended-virtual-interfaces-net/api/v1.1";
    public static final String COMMONNAMESPACE =
            "http://docs.openstack.org/common/api/v1.";
    public static final String BLOCKNAMESPACE =
    		"http://docs.openstack.org/api/openstack-block-storage/2.0/content";
    public static final String OSVOLHOSTATTRNAMESPACE =
    		"http://docs.openstack.org/openstack-block-storage/2.0/content/Volume_Host_Attribute.html";
    public static final String OSVOLIMAGEMETANAMESPACE =
    		"http://docs.openstack.org/openstack-block-storage/2.0/content/Volume_Image_Metadata.html";
    public static final String OSVOLTENANTATTRNAMESPACE =
    		"http://docs.openstack.org/openstack-block-storage/2.0/content/Volume_Tenant_Attribute.html";
    
    public String[] parseAuthorizationResponse (String response) throws OCCIException {
        List result;
        HashMap<String, String> urls = new HashMap<String, String>();
        String[] openStackServices = {"nova", "cinder", "neutron"};
        String[] ret = new String[6];
        SAXBuilder builder = new SAXBuilder();
        builder.setValidation(false);
        builder.setIgnoringElementContentWhitespace(true);
        Document xmldoc;
        String url;
        OCCIException exception;
        
        log.info("[OpenStackParser] Parsing authorization response...");
        try {
            /**** Get the token ****/
            xmldoc = builder.build(new StringReader(response));
            XPath xpath = XPath.newInstance("//ns:token/@id");
            xpath.addNamespace("ns", KEYSTONENAMESPACE);
            ret[0] = ((Attribute)xpath.selectSingleNode(xmldoc)).getValue();
            
            /**** Get Open Stack Services' URLs ****/
            for (String service : openStackServices) { 
                xpath = XPath.newInstance("//ns:service[@name=\"" + service + 
                        "\"]/ns:endpoint/@publicURL");
                xpath.addNamespace("ns", KEYSTONENAMESPACE);
                result = xpath.selectNodes(xmldoc);
                
                for (Object obj : result) {
                	Attribute attribute = (Attribute)obj;
                    url = attribute.getValue();
                    
                    if (url != null &&
                            !url.contains("/v3") &&
                            !url.contains("/v1")) {
                        urls.put(service, url);
                    }
                }
            }
            
            xmldoc = builder.build(new StringReader(response));
            xpath  = XPath.newInstance("//ns:user/@username");
            xpath.addNamespace("ns", KEYSTONENAMESPACE);
            ret[1] = ((Attribute)xpath.selectSingleNode(xmldoc)).getValue();
            
            xmldoc = builder.build(new StringReader(response));
            xpath  = XPath.newInstance("//ns:user/@id");
            xpath.addNamespace("ns", KEYSTONENAMESPACE);
            ret[2] = ((Attribute)xpath.selectSingleNode(xmldoc)).getValue();
            
            ret[3] = urls.get("nova");
            ret[4] = urls.get("cinder");
            ret[5] = urls.get("neutron");
            
            if (ret[3] == null) {
				exception = new OCCIException();
				exception.setMessage(
						"There is no compute service available publicly");
				throw exception;
			} else if (ret[4] == null) {
				exception = new OCCIException();
				exception.setMessage(
						"There is no storage service available publicly");
				throw exception;
			} else if (ret[5] == null) {
				exception = new OCCIException();
				exception.setMessage(
						"There is no network service available publicly");
				throw exception;
			}
        } catch (JDOMException e) {
            log.error(e.getMessage());
        } catch (IOException e) {
        	log.error(e.getMessage());
        }
        log.debug("[OpenStackParser] Authorization response parsed...");
        
        return ret;
    }
    
    public String[][] parseNetworkCollectionResponse (String response)
    		                                             throws OCCIException {
        String[][] ret = null;
        List idList, nameList, subnetList;
        XPath xpath;
        SAXBuilder builder = new SAXBuilder();
        builder.setValidation(false);
        builder.setIgnoringElementContentWhitespace(true);
        Document xmldoc;
        
        log.info("[OpenStackParser] Parsing network collection response...");
        try {
            xmldoc = builder.build(new StringReader(response));
            xpath = XPath.newInstance("//ns:network/ns:id");
            xpath.addNamespace("ns", QUANTUMNAMESPACE);
            xpath.addNamespace("provider", PROVIDERNAMESPACE);
            xpath.addNamespace("quantum", QUANTUMNAMESPACE);
            xpath.addNamespace("router", ROUTERNAMESPACE);
            xpath.addNamespace("xsi", XSINAMESPACE);
            idList = xpath.selectNodes(xmldoc);
            
            xpath = XPath.newInstance("//ns:network/ns:name");
            xpath.addNamespace("ns", QUANTUMNAMESPACE);
            xpath.addNamespace("provider", PROVIDERNAMESPACE);
            xpath.addNamespace("quantum", QUANTUMNAMESPACE);
            xpath.addNamespace("router", ROUTERNAMESPACE);
            xpath.addNamespace("xsi", XSINAMESPACE);
            nameList = xpath.selectNodes(xmldoc);
            
            ret = new String[idList.size()][];
            if (idList.size() == nameList.size()) {
	            for (int i = 0; i < idList.size(); i++) {
	                String id = ((Element)idList.get(i)).getValue();
	                
	                xpath =
	                    XPath.newInstance("//ns:network[ns:id=\"" +
	                                      id + "\"]/*/ns:subnet");
	                xpath.addNamespace("ns", QUANTUMNAMESPACE);
	                xpath.addNamespace("provider", PROVIDERNAMESPACE);
	                xpath.addNamespace("quantum", QUANTUMNAMESPACE);
	                xpath.addNamespace("router", ROUTERNAMESPACE);
	                xpath.addNamespace("xsi", XSINAMESPACE);
	                subnetList = xpath.selectNodes(xmldoc);
	                
	                ret[i] = new String[subnetList.size() + 2];
	                
	                ret[i][0] = id;
	                ret[i][1] = ((Element)nameList.get(i)).getValue();
	                
	                for (int j = 0; j < subnetList.size(); j++) {
	                    ret[i][j+2] = ((Element)subnetList.get(j)).getValue();
	                }
	            }
            } else {
            	OCCIException exception = new OCCIException();
                exception.setMessage(MALFORMEDRESPONSE);
                log.error(MALFORMEDRESPONSE);
                throw exception;
            }
        } catch (JDOMException e) {
            log.error(e.getMessage());
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        log.debug("[OpenStackParser] Network collection response parsed...");
        
        return ret;
    }
    
    public String[] parseCreatedNetworkResponse(String response)
                                                         throws OCCIException {
        String[] ret = new String[2];
        XPath xpath;
        SAXBuilder builder = new SAXBuilder();
        builder.setValidation(false);
        builder.setIgnoringElementContentWhitespace(true);
        Document xmldoc;
        Element id, status;
        
        log.info("[OpenStackParser] Parsing created network response...");
        try {
            xmldoc = builder.build(new StringReader(response));
            xpath = XPath.newInstance("//ns:id");
            xpath.addNamespace("ns", QUANTUMNAMESPACE);
            xpath.addNamespace("provider", PROVIDERNAMESPACE);
            xpath.addNamespace("quantum", QUANTUMNAMESPACE);
            xpath.addNamespace("router", ROUTERNAMESPACE);
            xpath.addNamespace("xsi", XSINAMESPACE);
            id = (Element)xpath.selectSingleNode(xmldoc);
            
            xpath = XPath.newInstance("//ns:status");
            xpath.addNamespace("ns", QUANTUMNAMESPACE);
            xpath.addNamespace("provider", PROVIDERNAMESPACE);
            xpath.addNamespace("quantum", QUANTUMNAMESPACE);
            xpath.addNamespace("router", ROUTERNAMESPACE);
            xpath.addNamespace("xsi", XSINAMESPACE);
            status = (Element)xpath.selectSingleNode(xmldoc);
            
            if (id != null && status != null) {
            	ret[0] = id.getValue();
            	ret[1] = status.getValue();
            } else {
            	OCCIException exception = new OCCIException();
                exception.setMessage(MALFORMEDRESPONSE);
                log.error(MALFORMEDRESPONSE);
                throw exception;
            }
        } catch (JDOMException e) {
            log.error(e.getMessage());
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        log.debug("[OpenStackParser] Created network response parsed...");
        
        return ret;
    }
    
    public NetworkCollection parseSubnetCollectionResponse (String response)
                                                         throws OCCIException {
        Link link;
        ArrayList<Link> links;
        NetworkCollection ret = null;
        SAXBuilder builder = new SAXBuilder();
        builder.setValidation(false);
        builder.setIgnoringElementContentWhitespace(true);
        Document xmldoc;
        
        log.info("[OpenStackParser] Parsing subnet collection response...");
        try {
            xmldoc = builder.build(new StringReader(response));
            XPath xpath = XPath.newInstance("//ns:subnet/ns:id");
            xpath.addNamespace("ns", QUANTUMNAMESPACE);
            xpath.addNamespace("quantum", QUANTUMNAMESPACE);
            xpath.addNamespace("xsi", XSINAMESPACE);
            List idList = xpath.selectNodes(xmldoc);
            
            xpath = XPath.newInstance("//ns:subnet/ns:name");
            xpath.addNamespace("ns", QUANTUMNAMESPACE);
            xpath.addNamespace("quantum", QUANTUMNAMESPACE);
            xpath.addNamespace("xsi", XSINAMESPACE);
            List nameList = xpath.selectNodes(xmldoc);
            
            if (idList.size() == nameList.size()) {
                links = new ArrayList<Link>();
                ret = new NetworkCollection();
                Iterator idIt = idList.iterator();
                Iterator nameIt = nameList.iterator();
                
                while (idIt.hasNext()) {
                    link = new Link();
                    link.setHref(((Element)idIt.next()).getValue());
                    link.setName(((Element)nameIt.next()).getValue());
                    
                    links.add(link);
                }
                ret = new NetworkCollection();
                ret.getNetwork().addAll(links);
            } else {
            	OCCIException exception = new OCCIException();
                exception.setMessage(MALFORMEDRESPONSE);
                log.error(MALFORMEDRESPONSE);
                throw exception;
            }
            
        } catch (JDOMException e) {
            log.error(e.getMessage());
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        log.debug("[OpenStackParser] Subnet collection response parsed...");
        
        return ret;
    }
    
    public Network parseSubnetResponse (String response)
                                                       throws OCCIException {
        Network ret = null;
        Element href, address, name;
        String size;
        SAXBuilder builder = new SAXBuilder();
        builder.setValidation(false);
        builder.setIgnoringElementContentWhitespace(true);
        Document xmldoc;
        int netClass;
        
        log.info("[OpenStackParser] Parsing subnet response...");
        try {
            ret = new Network();
            
            xmldoc = builder.build(new StringReader(response));
            XPath xpath = XPath.newInstance("//ns:id");
            xpath.addNamespace("ns", QUANTUMNAMESPACE);
            xpath.addNamespace("quantum", QUANTUMNAMESPACE);
            xpath.addNamespace("xsi", XSINAMESPACE);
            href = (Element)xpath.selectSingleNode(xmldoc);
            
            xpath = XPath.newInstance("//ns:cidr");
            xpath.addNamespace("ns", QUANTUMNAMESPACE);
            xpath.addNamespace("quantum", QUANTUMNAMESPACE);
            xpath.addNamespace("xsi", XSINAMESPACE);
            address = (Element)xpath.selectSingleNode(xmldoc);
            
            xpath = XPath.newInstance("//ns:name");
            xpath.addNamespace("ns", QUANTUMNAMESPACE);
            xpath.addNamespace("quantum", QUANTUMNAMESPACE);
            xpath.addNamespace("xsi", XSINAMESPACE);
            name = (Element)xpath.selectSingleNode(xmldoc);
            
            if (href != null && address != null && name != null) {
            	ret.setAddress(address.getValue());
            	ret.setHref(href.getValue());
            	ret.setName(name.getValue());
            	
            	netClass = Integer.parseInt(ret.getAddress().split("\\.")[0]);
                size     = null;
                if (netClass < 128) {
                	size = "A";
                } else if (netClass < 192) {
                	size = "B";
                } else if (netClass < 224) {
                	size = "C";
                } else if (netClass < 240) {
                	size = "D";
                } else if (netClass < 256) {
                	size = "E";
                } else {
                	OCCIException exception = new OCCIException();
                    exception.setMessage(BADADDRESS);
                    log.error(BADADDRESS);
                    throw exception;
                }
            	
            	ret.setSize(size);
            } else {
            	OCCIException exception = new OCCIException();
                exception.setMessage(MALFORMEDRESPONSE);
                log.error(MALFORMEDRESPONSE);
                throw exception;
            }
        } catch (JDOMException e) {
            log.error(e.getMessage());
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        log.debug("[OpenStackParser] Subnet response parsed...");
        
        return ret;
    }
    
    public StorageCollection parseStorageCollectionResponse (String response)
                                                        throws OCCIException {
        StorageCollection ret = null;
        List<Link> linkList;
        Link link;
        SAXBuilder builder = new SAXBuilder();
        builder.setValidation(false);
        builder.setIgnoringElementContentWhitespace(true);
        Document xmldoc;
        
        log.info("[OpenStackParser] Parsing storage collection response...");
        try {
            xmldoc = builder.build(new StringReader(response));
            XPath xpath = XPath.newInstance("//ns:volume/@id");
            xpath.addNamespace("ns", BLOCKNAMESPACE);
            xpath.addNamespace("atom", ATOMNAMESPACE);
            List idList = xpath.selectNodes(xmldoc);
            
            xpath = XPath.newInstance("//ns:volume/@display_name");
            xpath.addNamespace("ns", BLOCKNAMESPACE);
            xpath.addNamespace("atom", ATOMNAMESPACE);
            List nameList = xpath.selectNodes(xmldoc);
            
            if (idList.size() == nameList.size()) {
                ret      = new StorageCollection();
                linkList = new ArrayList<Link>();
                
                for (int i = 0 ; i < idList.size() ; i++) {
                    link = new Link();
                    link.setHref(((Attribute)idList.get(i)).getValue());
                    link.setName(((Attribute)nameList.get(i)).getValue());
                    linkList.add(link);
                }
            } else {
                OCCIException exception = new OCCIException();
                exception.setMessage(MALFORMEDRESPONSE);
                log.error(MALFORMEDRESPONSE);
                throw exception;
            }
        } catch (JDOMException e) {
            log.error(e.getMessage());
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        log.debug("[OpenStackParser] Storage collection response parsed...");
        
        return ret;
    }
    
    public Storage parseStorageResponse (String response)
                                              throws OCCIException {
        String id, name, size;
    	Storage ret = null;
        SAXBuilder builder = new SAXBuilder();
        builder.setValidation(false);
        builder.setIgnoringElementContentWhitespace(true);
        Document xmldoc;
        
        log.info("[OpenStackParser] Parsing storage response...");
        try {
            xmldoc = builder.build(new StringReader(response));
            XPath xpath = XPath.newInstance("//ns:volume");
            xpath.addNamespace("ns", BLOCKNAMESPACE);
            xpath.addNamespace("atom", ATOMNAMESPACE);
            xpath.addNamespace("os-vol-host-attr", OSVOLHOSTATTRNAMESPACE);
            xpath.addNamespace("os-vol-image-meta", OSVOLIMAGEMETANAMESPACE);
            xpath.addNamespace("os-vol-tenant-attr", OSVOLTENANTATTRNAMESPACE);
            
            Element result = (Element)xpath.selectSingleNode(xmldoc);
            
            id   = result.getAttributeValue("id");
            name = result.getAttributeValue("name");
            size = result.getAttributeValue("size");
            
            
            if (id != null && name != null && size != null) {
	            ret = new Storage();
	            ret.setDescription(
	                    result.getAttributeValue("description"));
	            ret.setHref(id);
	            ret.setName(name);
	            ret.setSize(size);
	            ret.setType(StorageType.DATABLOCK);
            } else {
            	OCCIException exception = new OCCIException();
                exception.setMessage(MALFORMEDRESPONSE);
                log.error(MALFORMEDRESPONSE);
                throw exception;
            }
        } catch (JDOMException e) {
            log.error(e.getMessage());
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        log.debug("[OpenStackParser] Storage response parsed...");
        
        return ret;
    }

    public StorageCollection parseImageCollectionResponse (String response)
                                                         throws OCCIException {
        StorageCollection ret = new StorageCollection();
        Gson gson = new Gson();
        Link link;
        
        log.info("[OpenStackParser] Parsing image collection response...");
        ImageCollection col = gson.fromJson(response, ImageCollection.class);
        
        for (Image img : col.getImages()) {
        	link = new Link();
        	link.setHref(img.getId().toString());
        	link.setName(img.getName());
        	
        	if (img.getId() != null && img.getName() != null) {
        		ret.getStorage().add(link);
        	} else {
        		OCCIException exception = new OCCIException();
                exception.setMessage(MALFORMEDRESPONSE);
                log.error(MALFORMEDRESPONSE);
                throw exception;
        	}
        }
        log.debug("[OpenStackParser] Image collection response parsed...");
        
        return ret;
    }

    public Storage parseImageResponse (String response)
            throws OCCIException {
    	String id, name;
    	int size;
		Storage ret = new Storage();
		Gson gson = new Gson();
		
		log.info("[OpenStackParser] Parsing image response...");
		Image img = gson.fromJson(response, Image.class);
		
		id   = img.getId();
		name = img.getName();
		size = img.getSize();
		
		if (id != null && name != null && size > 0) {
			ret.setName(name);
			ret.setSize(String.valueOf(size));
			ret.setHref(id);
			ret.setType(StorageType.OS);
		} else {
    		OCCIException exception = new OCCIException();
            exception.setMessage(MALFORMEDRESPONSE);
            log.error(MALFORMEDRESPONSE);
            throw exception;
    	}
		log.debug("[OpenStackParser] Image response parsed...");
		
		return ret;
	}
    
    public ComputeCollection parseComputeCollectionResponse (String response)
    		                                             throws OCCIException {
        ComputeCollection ret = null;
        List<Link> linkList;
        Link link;
        SAXBuilder builder = new SAXBuilder();
        builder.setValidation(false);
        builder.setIgnoringElementContentWhitespace(true);
        Document xmldoc;
        
        log.info("[OpenStackParser] Parsing compute collection response...");
        try {
            /**** Get the token ****/
            xmldoc = builder.build(new StringReader(response));
            XPath xpath = XPath.newInstance("//ns:server/@id");
            xpath.addNamespace("ns", COMPUTENAMESPACE);
            xpath.addNamespace("atom", ATOMNAMESPACE);
            List idList = xpath.selectNodes(xmldoc);
            
            xpath = XPath.newInstance("//ns:server/@name");
            xpath.addNamespace("ns", VOLUMENAMESPACE);
            List nameList = xpath.selectNodes(xmldoc);
            
            if (idList.size() == nameList.size()) {
                ret      = new ComputeCollection();
                linkList = new ArrayList<Link>();
                
                for (int i = 0 ; i < idList.size() ; i++) {
                    link = new Link();
                    link.setHref(((Attribute)idList.get(i)).getValue());
                    link.setName(((Attribute)nameList.get(i)).getValue());
                    linkList.add(link);
                }
            } else {
                OCCIException exception = new OCCIException();
                exception.setMessage(MALFORMEDRESPONSE);
                log.error(MALFORMEDRESPONSE);
                throw exception;
            }
        } catch (JDOMException e) {
            log.error(e.getMessage());
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        log.debug("[OpenStackParser] Compute collection response parsed...");
        
        return ret;
    }

    public Compute parseComputeResponse (String response)
                                                     throws OCCIException {
    	String id, name, state;
    	Attribute flavorId;
        Compute ret = null;
        SAXBuilder builder = new SAXBuilder();
        builder.setValidation(false);
        builder.setIgnoringElementContentWhitespace(true);
        Document xmldoc;
        
        log.info("[OpenStackParser] Parsing compute response...");
        try {
            xmldoc = builder.build(new StringReader(response));
            XPath xpath = XPath.newInstance("//ns:server");
            xpath.addNamespace("ns", COMPUTENAMESPACE);
            xpath.addNamespace("atom", ATOMNAMESPACE);
            
            Element server = (Element)xpath.selectSingleNode(xmldoc);
            
            id    = server.getAttributeValue("id");
            name  = server.getAttributeValue("name");
            state = server.getAttributeValue("status");
            
            xpath = XPath.newInstance("//ns:flavor/@id");
            xpath.addNamespace("ns", COMPUTENAMESPACE);
            xpath.addNamespace("atom", ATOMNAMESPACE);
            
            flavorId = (Attribute) xpath.selectSingleNode(xmldoc);
            
            if (id   != null &&
                name != null &&
                state != null &&
                flavorId != null) {
            	ret = new Compute();
            	
                ret.setHref(id);
                ret.setName(name);
                ret.setState(fromOpenStackComputeState(state));
                ret.setInstanceType(flavorId.getValue());
            } else {
            	OCCIException exception = new OCCIException();
                exception.setMessage(MALFORMEDRESPONSE);
                log.error(MALFORMEDRESPONSE);
                throw exception;
            }
        } catch (JDOMException e) {
            log.error(e.getMessage());
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        log.debug("[OpenStackParser] Compute response parsed...");
        
        return ret;
    }
    
    public String parseComputeInstanceTypeId (
    		String response,
    		HashMap<String, InstanceType> instanceTypes) throws OCCIException {
    	Attribute id = null;
        SAXBuilder builder = new SAXBuilder();
        builder.setValidation(false);
        builder.setIgnoringElementContentWhitespace(true);
        Document xmldoc;
        
        log.info("[OpenStackParser] Parsing compute's instance type id...");
        try {
            xmldoc = builder.build(new StringReader(response));
            XPath xpath = XPath.newInstance("//ns:flavor/@id");
            xpath.addNamespace("ns", COMPUTENAMESPACE);
            xpath.addNamespace("atom", ATOMNAMESPACE);
            
            id = (Attribute)xpath.selectSingleNode(xmldoc);
            
            if (id == null) {
            	OCCIException exception = new OCCIException();
                exception.setMessage(MALFORMEDRESPONSE);
                log.error(MALFORMEDRESPONSE);
                throw exception;
            }
        } catch (JDOMException e) {
            log.error(e.getMessage());
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        log.debug("[OpenStackParser] Compute's instance type id parsed...");
        
        return id.getValue();
    }
    
    public List<Disk> parseAttachedStoragesResponse (String response) 
                                                        throws OCCIException {
    	List<Disk> ret = null;
    	Disk disk;
        Link link;
        SAXBuilder builder = new SAXBuilder();
        builder.setValidation(false);
        builder.setIgnoringElementContentWhitespace(true);
        Document xmldoc;
        
        log.info("[OpenStackParser] Parsing attached storages response...");
        try {
            xmldoc = builder.build(new StringReader(response));
            XPath xpath = XPath.newInstance("//volumeAttachment/@volumeId");
            List volumeIdList = xpath.selectNodes(xmldoc);
            
            xpath = XPath.newInstance("//volumeAttachment/@device");
            List deviceList = xpath.selectNodes(xmldoc);
            
            if (volumeIdList.size() == deviceList.size()) {
                ret = new ArrayList<Disk>();
                
                for (int i = 0 ; i < volumeIdList.size() ; i++) {
                	disk = new Disk();
                	link = new Link();
                    link.setHref(((Attribute)volumeIdList.get(i)).getValue());

                    disk.setStorage(link);
                    disk.setTarget(((Attribute)deviceList.get(i)).getValue());
                    
                    ret.add(disk);
                }
            } else {
                OCCIException exception = new OCCIException();
                exception.setMessage(MALFORMEDRESPONSE);
                log.error(MALFORMEDRESPONSE);
                throw exception;
            }
        } catch (JDOMException e) {
            log.error(e.getMessage());
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        log.debug("[OpenStackParser] Attached storages response parsed...");
        
        return ret;
    }
    
    public List<Nic> parseVirtualInterfacesResponse (String response) 
                                                        throws OCCIException {
    	List<Nic> ret = null;
    	Nic nic;
        Link link;
        SAXBuilder builder = new SAXBuilder();
        builder.setValidation(false);
        builder.setIgnoringElementContentWhitespace(true);
        Document xmldoc;
        
        log.info("[OpenStackParser] Parsing virtual interfaces response...");
        try {
            xmldoc = builder.build(new StringReader(response));
            XPath xpath = XPath.newInstance("//ns:virtual_interfaces/@mac_address");
            xpath.addNamespace("ns", COMPUTENAMESPACE);
            xpath.addNamespace("OS-EXT-VIF-NET", VIFNAMESPACE);
            List macList = xpath.selectNodes(xmldoc);
            
            xpath = XPath.newInstance("//ns:virtual_interfaces/@OS-EXT-VIF-NET:net_id");
            xpath.addNamespace("ns", COMPUTENAMESPACE);
            xpath.addNamespace("OS-EXT-VIF-NET", VIFNAMESPACE);
            List networkList = xpath.selectNodes(xmldoc);
            
            if (macList.size() > 0 && networkList.size() > 0) {
                ret = new ArrayList<Nic>();
                
                for (int i = 0 ; i < macList.size() ; i++) {
                	nic = new Nic();
                	link = new Link();
                    link.setHref(((Attribute)networkList.get(i)).getValue());

                    nic.setMac(((Attribute)macList.get(i)).getValue());
                    nic.setNetwork(link);
                }
            } else {
                OCCIException exception = new OCCIException();
                exception.setMessage(MALFORMEDRESPONSE);
                log.error(MALFORMEDRESPONSE);
                throw exception;
            }
        } catch (JDOMException e) {
            log.error(e.getMessage());
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        log.debug("[OpenStackParser] Virtual interfaces response parsed...");
        
        return ret;
    }
    
    public InstanceTypeCollection
                    parseInstanceTypeCollectionResponse (String response) 
                                                        throws OCCIException {
        InstanceTypeCollection ret = null;
        List<Link> linkList;
        Link link;
        SAXBuilder builder = new SAXBuilder();
        builder.setValidation(false);
        builder.setIgnoringElementContentWhitespace(true);
        Document xmldoc;
        
        log.info("[OpenStackParser] "
        		+ "Parsing instance type collection response...");
        try {
            xmldoc = builder.build(new StringReader(response));
            XPath xpath = XPath.newInstance("//ns:flavor/@id");
            xpath.addNamespace("ns", COMPUTENAMESPACE);
            xpath.addNamespace("atom", ATOMNAMESPACE);
            List idList = xpath.selectNodes(xmldoc);
            
            xpath = XPath.newInstance("//ns:flavor/@name");
            xpath.addNamespace("ns", COMPUTENAMESPACE);
            xpath.addNamespace("atom", ATOMNAMESPACE);
            List nameList = xpath.selectNodes(xmldoc);
            
            if (idList.size() == nameList.size()) {
                ret      = new InstanceTypeCollection();
                linkList = new ArrayList<Link>();
                
                for (int i = 0 ; i < idList.size() ; i++) {
                    link = new Link();
                    link.setHref(((Attribute)idList.get(i)).getValue());
                    link.setName(((Attribute)nameList.get(i)).getValue());
                    linkList.add(link);
                }

                ret.getInstanceType().addAll(linkList);
            } else {
                OCCIException exception = new OCCIException();
                exception.setMessage(MALFORMEDRESPONSE);
                log.error(MALFORMEDRESPONSE);
                throw exception;
            }
        } catch (JDOMException e) {
            log.error(e.getMessage());
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        log.debug("[OpenStackParser] "
        		+ "Instance type collection response parsed...");
        
        return ret;
    }
    
    public InstanceType parseInstanceTypeResponse (String response) 
                                                        throws OCCIException {
    	Attribute vcpu, ram, name, id;
        InstanceType ret = new InstanceType();
        SAXBuilder builder = new SAXBuilder();
        builder.setValidation(false);
        builder.setIgnoringElementContentWhitespace(true);
        Document xmldoc;
        
        log.info("[OpenStackParser] Parsing instance type response...");
        try {
            xmldoc = builder.build(new StringReader(response));
            XPath xpath = XPath.newInstance("//ns:flavor/@vcpus");
            xpath.addNamespace("ns", KEYSTONENAMESPACE);
            vcpu = (Attribute) xpath.selectSingleNode(xmldoc);
            
            xpath = XPath.newInstance("//ns:flavor/@ram");
            xpath.addNamespace("ns", KEYSTONENAMESPACE);
            ram = (Attribute) xpath.selectSingleNode(xmldoc);
            
            xpath = XPath.newInstance("//ns:flavor/@name");
            xpath.addNamespace("ns", KEYSTONENAMESPACE);
            name = (Attribute) xpath.selectSingleNode(xmldoc);
            
            xpath = XPath.newInstance("//ns:flavor/@id");
            xpath.addNamespace("ns", KEYSTONENAMESPACE);
            id = (Attribute) xpath.selectSingleNode(xmldoc);
            
            if (vcpu != null && ram != null && name!= null && id != null) {
            	ret.setCpu(Integer.parseInt(vcpu.getValue()));
                ret.setHref(id.getValue());
                ret.setMemory(Integer.parseInt(ram.getValue()));
                ret.setName(name.getValue());
            } else {
            	OCCIException exception = new OCCIException();
                exception.setMessage(MALFORMEDRESPONSE);
                log.error(MALFORMEDRESPONSE);
                throw exception;
            }
        } catch (JDOMException e) {
            log.error(e.getMessage());
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        log.debug("[OpenStackParser] Instance type response parsed...");
        
        return ret;
    }
    
    public Quota parseComputeQuotaResponse(String response) throws OCCIException {
    	Element vcpu, ram, servers;
        Quota ret = new Quota();
        SAXBuilder builder = new SAXBuilder();
        builder.setValidation(false);
        builder.setIgnoringElementContentWhitespace(true);
        Document xmldoc;
        
        log.info("[OpenStackParser] Parsing compute quota response...");
        try {
            xmldoc = builder.build(new StringReader(response));
            XPath xpath = XPath.newInstance("//cores");
            vcpu = (Element) xpath.selectSingleNode(xmldoc);
            
            xpath = XPath.newInstance("//ram");
            ram = (Element) xpath.selectSingleNode(xmldoc);
            
            xpath = XPath.newInstance("//instances");
            servers = (Element) xpath.selectSingleNode(xmldoc);
            
            if (vcpu != null && ram != null && servers!= null) {
            	ret.setCpu(Integer.parseInt(vcpu.getValue()));
            	ret.setMemory(Integer.parseInt(ram.getValue()));
            	ret.setNumVms(Integer.parseInt(servers.getValue()));
            } else {
            	OCCIException exception = new OCCIException();
                exception.setMessage(MALFORMEDRESPONSE);
                log.error(MALFORMEDRESPONSE);
                throw exception;
            }
        } catch (JDOMException e) {
            log.error(e.getMessage());
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        log.debug("[OpenStackParser] Compute quota response parsed...");
        
        return ret;
    }
    
    public int parseStorageQuotaResponse(String response) throws OCCIException {
    	Attribute totalPerStorage, maxStorages;
        SAXBuilder builder = new SAXBuilder();
        builder.setValidation(false);
        builder.setIgnoringElementContentWhitespace(true);
        Document xmldoc;
        
        log.info("[OpenStackParser] Parsing storage quota response...");
        try {
            xmldoc = builder.build(new StringReader(response));
            XPath xpath = XPath.newInstance("//maxTotalVolumeGigabytes");
            xpath.addNamespace("ns", COMMONNAMESPACE);
            xpath.addNamespace("atom", ATOMNAMESPACE);
            totalPerStorage =
            		(Attribute) xpath.selectSingleNode(xmldoc);
            
            xpath = XPath.newInstance("//maxTotalVolumeGigabytes");
            xpath.addNamespace("ns", COMMONNAMESPACE);
            xpath.addNamespace("atom", ATOMNAMESPACE);
            maxStorages =
            		(Attribute) xpath.selectSingleNode(xmldoc);
            
            if (totalPerStorage != null && maxStorages != null) {
            	//TODO Should we allow it? It can be a source of problems,
            	//   better return only the total per storage?
            	return Integer.parseInt(totalPerStorage.getValue()) *
            			Integer.parseInt(maxStorages.getValue());
            } else {
            	OCCIException exception = new OCCIException();
                exception.setMessage(MALFORMEDRESPONSE);
                log.error(MALFORMEDRESPONSE);
                throw exception;
            }
        } catch (JDOMException e) {
            log.error(e.getMessage());
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        log.debug("[OpenStackParser] Storage quota response parsed...");
        return 0;
    }
}
