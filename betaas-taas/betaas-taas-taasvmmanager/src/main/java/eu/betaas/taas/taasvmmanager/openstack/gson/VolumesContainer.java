package eu.betaas.taas.taasvmmanager.openstack.gson;

public class VolumesContainer {
	private Volume[] volumes;
	
	public VolumesContainer() {}

	public Volume[] getVolumes() {
		return volumes;
	}

	public void setVolumes(Volume[] volumes) {
		this.volumes = volumes;
	}
}
