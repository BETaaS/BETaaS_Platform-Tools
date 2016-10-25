package eu.betaas.taas.securitymanager.requirements.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.osgi.util.tracker.ServiceTracker;

import eu.betaas.adaptation.thingsadaptor.api.ThingsAdaptor;
import eu.betaas.taas.securitymanager.requirements.activator.SecurityRequirementsActivator;
import eu.betaas.taas.securitymanager.requirements.helper.Ids;
import eu.betaas.taas.securitymanager.requirements.service.IThingsRequirementsService;
import eu.betaas.taas.securitymanager.taastrustmanager.api.TaaSTrustManager;

public class ThingsRequirementsService implements IThingsRequirementsService {

	SecurityRequirementsActivator sRecActivator;
	
	public ThingsRequirementsService(SecurityRequirementsActivator activator){
		this.sRecActivator = activator;
	}
	
	public List<List<String>> getSecurityRank(
			List<List<String>> eqThingServices, Map<String, String> securitySpecs) {
		// getting the ThingsAdaptor service
		ServiceTracker thingTracker = sRecActivator.getThingTracker();
		ThingsAdaptor thAdaptor = (ThingsAdaptor) thingTracker.getService();
		// need to comply with the application manifest
		String encAlgThing = "EC";			// dummy data
		String keyBitsThing = "192";	// dummy data
	
		// get the TaaSTrustManager service
		ServiceTracker trustTracker = sRecActivator.getTrustTracker();
		TaaSTrustManager trustServ = (TaaSTrustManager) trustTracker.getService();
		
		List<List<String>> results = new ArrayList<List<String>>();
		
		// Get the Required Trust Score
		double reqTrust = Double.parseDouble(securitySpecs.get("RequiredTrust"));
		// get the required encryption algorithm used by things
		String reqEncryptAlg = securitySpecs.get("EncryptionAlgorithm");
		// get the required key length in bits used by things
		int reqBitsLength = Integer.parseInt(securitySpecs.get("KeyBits"));
		
		// iterate the equivalent Thing Services one by one
		for(int i=0;i<eqThingServices.size();i++){
			if(eqThingServices.get(i).size()!=0){
				List<Ids> ids = new ArrayList<Ids>();
				for(int j=0;j<eqThingServices.get(i).size();j++){
					// get the information from ThingsAdaptor
					// encAlgThing = thAdaptor.getMeasurement(thingId, "Encryption")
					// keyBitsThing = thAdaptor.getMeasurement(thingId, "KeyBits")
					double thingTrust = trustServ.getTrust(eqThingServices.get(i).get(j));
					// TODO: need to update this according to available API from ThingsAdaptor
					// for now, we use dummy data
					// check if the encryption algorithm is match
					if(encAlgThing.equals(reqEncryptAlg)){
						// check if the minimum bits length for the key is fulfilled
						if(Integer.parseInt(keyBitsThing)>= reqBitsLength){
							if(thingTrust >= reqTrust){
								ids.add(new Ids(thingTrust, j));
								if(ids.size()!=0){
									Ids[] idsArray = new Ids[ids.size()];
									for(int z = 0;z<ids.size();z++){
										idsArray[z] = ids.get(z);
									}
									// sorting the ThingsService
									Arrays.sort(idsArray);
									List<String> sortedGroup = new ArrayList<String>();
									for(int k = 0;k<idsArray.length;k++){
										sortedGroup.add(eqThingServices.get(i).
												get(idsArray[k].getId()));
									}
									results.add(sortedGroup);
								}
								else{
									results.add(new ArrayList<String>());
								}
							}
						}
					}
				}
			}
			else{
				results.add(new ArrayList<String>());
			}
		}
		
		return results;
	}

}
