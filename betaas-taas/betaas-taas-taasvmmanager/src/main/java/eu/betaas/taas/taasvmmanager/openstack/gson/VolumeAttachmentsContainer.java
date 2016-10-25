package eu.betaas.taas.taasvmmanager.openstack.gson;

public class VolumeAttachmentsContainer {
	private VolumeAttachment[] volumeAttachments;
	
	public VolumeAttachmentsContainer () {}
	
	public VolumeAttachment[] getVolumeAttachments() {
		return volumeAttachments;
	}

	public void setVolumeAttachments(VolumeAttachment[] volumeAttachments) {
		this.volumeAttachments = volumeAttachments;
	}

	public class VolumeAttachment {
		private String device;
		private String id;
		private String serverId;
		private String volumeId;
		
		public VolumeAttachment () {}

		public String getDevice() {
			return device;
		}

		public void setDevice(String device) {
			this.device = device;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getServerId() {
			return serverId;
		}

		public void setServerId(String serverId) {
			this.serverId = serverId;
		}

		public String getVolumeId() {
			return volumeId;
		}

		public void setVolumeId(String volumeId) {
			this.volumeId = volumeId;
		}
	}
}
