package eu.betaas.taas.taasvmmanager.openstack.parser;

import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import eu.betaas.taas.taasvmmanager.api.datamodel.InstanceType;
import eu.betaas.taas.taasvmmanager.occi.OCCIException;
import eu.betaas.taas.taasvmmanager.occi.datamodel.Compute;
import eu.betaas.taas.taasvmmanager.occi.datamodel.ComputeCollection;
import eu.betaas.taas.taasvmmanager.occi.datamodel.ComputeState;
import eu.betaas.taas.taasvmmanager.occi.datamodel.InstanceTypeCollection;
import eu.betaas.taas.taasvmmanager.occi.datamodel.Network;
import eu.betaas.taas.taasvmmanager.occi.datamodel.NetworkCollection;
import eu.betaas.taas.taasvmmanager.occi.datamodel.Storage;
import eu.betaas.taas.taasvmmanager.occi.datamodel.StorageCollection;
import eu.betaas.taas.taasvmmanager.occi.datamodel.Compute.Disk;
import eu.betaas.taas.taasvmmanager.occi.datamodel.Compute.Nic;
import eu.betaas.taas.taasvmmanager.occi.datamodel.User.Quota;

public abstract class OpenStackParser {
	private static Logger log = Logger.getLogger("betaas.taas");
	
	protected static final String OSSTATE_ACTIVE         = "ACTIVE";
    protected static final String OSSTATE_BUILDING       = "BUILDING";
    protected static final String OSSTATE_DELETED        = "DELETED";
    protected static final String OSSTATE_ERROR          = "ERROR";
    protected static final String OSSTATE_HARD_REBOOT   = "HARD_REBOOT";
    protected static final String OSSTATE_PASSWORD       = "PASSWORD";
    protected static final String OSSTATE_PAUSED         = "PAUSED";
    protected static final String OSSTATE_REBOOT         = "REBOOT";
    protected static final String OSSTATE_REBUILD        = "REBUILD";
    protected static final String OSSTATE_RESCUED        = "RESCUED";
    protected static final String OSSTATE_RESIZED        = "RESIZED";
    protected static final String OSSTATE_REVERT_RESIZE = "REVERT_RESIZE";
    protected static final String OSSTATE_SHUTOFF        = "SHUTOFF";
    protected static final String OSSTATE_SOFT_DELETED  = "SOFT_DELETED";
    protected static final String OSSTATE_STOPPED        = "STOPPED";
    protected static final String OSSTATE_SUSPENDED      = "SUSPENDED";
    protected static final String OSSTATE_UNKNOWN        = "UNKNOWN";
    protected static final String OSSTATE_VERIFY_RESIZE = "VERIFY_RESIZE";
    
    protected static final String MALFORMEDRESPONSE =
            "Malformed response";
    protected static final String BADRESOURCENAME =
            "The %s %s does not exist";
    protected static final String BADREFERENCE =
            "Bad reference, %s %s does not exist.";
    protected static final String BADSTATE =
            "Bad state, %s does not exist.";
    protected static final String BADADDRESS =
            "The address %s is malformed.";
	
	public abstract String[] parseAuthorizationResponse (String response)
			throws OCCIException;
    
    public abstract String[][] parseNetworkCollectionResponse (String response)
    		                                             throws OCCIException;
    
    public abstract String[] parseCreatedNetworkResponse(String response)
                                                         throws OCCIException;
    
    public abstract NetworkCollection parseSubnetCollectionResponse (String response)
                                                         throws OCCIException;
    
    public abstract Network parseSubnetResponse (String response)
                                                       throws OCCIException;
    
    public abstract StorageCollection parseStorageCollectionResponse (String response)
                                                        throws OCCIException;
    
    public abstract Storage parseStorageResponse (String response)
                                              throws OCCIException;

    public abstract StorageCollection parseImageCollectionResponse (String response)
                                                         throws OCCIException;

    public abstract Storage parseImageResponse (String response) throws OCCIException;
    
    public abstract ComputeCollection parseComputeCollectionResponse (String response)
    		                                             throws OCCIException;

    public abstract Compute parseComputeResponse (String response)
                                                     throws OCCIException;
    
    public abstract String parseComputeInstanceTypeId (
    		String response,
    		HashMap<String, InstanceType> instanceTypes) throws OCCIException;
    
    public abstract List<Disk> parseAttachedStoragesResponse (String response) 
                                                        throws OCCIException;
    
    public abstract List<Nic> parseVirtualInterfacesResponse (String response) 
                                                        throws OCCIException;
    
    public abstract InstanceTypeCollection
                    parseInstanceTypeCollectionResponse (String response) 
                                                        throws OCCIException;
    
    public abstract InstanceType parseInstanceTypeResponse (String response) 
                                                        throws OCCIException;
    
    public abstract Quota parseComputeQuotaResponse(String response) throws OCCIException;
    
    public abstract  int parseStorageQuotaResponse(String response) throws OCCIException;
    
    public ComputeState fromOpenStackComputeState(String state)
            throws OCCIException {
		if (state.equals(OSSTATE_ACTIVE)) {
			return ComputeState.ACTIVE;
		} else if (state.equals(OSSTATE_BUILDING)) {
			return ComputeState.PENDING;
		} else if (state.equals(OSSTATE_DELETED)) {
			return ComputeState.DONE;
		} else if (state.equals(OSSTATE_ERROR)) {
			return ComputeState.FAILED;
		} else if (state.equals(OSSTATE_HARD_REBOOT)) {
			return ComputeState.RESET;
		} else if (state.equals(OSSTATE_PASSWORD)) {
			return ComputeState.RESET;
		} else if (state.equals(OSSTATE_PAUSED)) {
			return ComputeState.SUSPENDED;
		} else if (state.equals(OSSTATE_REBOOT)) {
			return ComputeState.REBOOT;
		} else if (state.equals(OSSTATE_REBUILD)) {
			return ComputeState.PENDING;
		} else if (state.equals(OSSTATE_RESCUED)) {
			return ComputeState.RESET;
		} else if (state.equals(OSSTATE_RESIZED)) {
			return ComputeState.RESET;
		} else if (state.equals(OSSTATE_REVERT_RESIZE)) {
			return ComputeState.RESET;
		} else if (state.equals(OSSTATE_SHUTOFF)) {
			return ComputeState.SHUTDOWN;
		} else if (state.equals(OSSTATE_SOFT_DELETED)) {
			return ComputeState.DONE;
		} else if (state.equals(OSSTATE_STOPPED)) {
			return ComputeState.STOPPED;
		} else if (state.equals(OSSTATE_SUSPENDED)) {
			return ComputeState.SUSPENDED;
		} else if (state.equals(OSSTATE_UNKNOWN)) {
			return ComputeState.FAILED;
		} else if (state.equals(OSSTATE_VERIFY_RESIZE)) {
			return ComputeState.RESET;
		} else {
			OCCIException exception = new OCCIException();
			exception.setMessage(String.format(BADSTATE, state));
			log.error(String.format(BADSTATE, state));
			throw exception;
		}
	}
		
	public String toOpenStackComputeState(ComputeState state)
	            throws OCCIException {
		switch(state) {
		case ACTIVE:
			return OSSTATE_ACTIVE;
		case CANCEL:
			return OSSTATE_DELETED;
		case DONE:
			return OSSTATE_DELETED;
		case FAILED:
			return OSSTATE_ERROR;
		case HOLD:
			return OSSTATE_PAUSED;
		case INIT:
			return OSSTATE_BUILDING;
		case PENDING:
			return OSSTATE_BUILDING;
		case REBOOT:
			return OSSTATE_REBOOT;
		case RESET:
			return OSSTATE_REBUILD;
		case RESUME:
			return OSSTATE_ACTIVE;
		case SHUTDOWN:
			return OSSTATE_SHUTOFF;
		case STOPPED:
			return OSSTATE_STOPPED;
		case SUSPENDED:
			return OSSTATE_SUSPENDED;
		default:
			OCCIException exception = new OCCIException();
			exception.setMessage(String.format(BADSTATE, state));
			log.error(String.format(BADSTATE, state));
			throw exception;
		}
	}
}
