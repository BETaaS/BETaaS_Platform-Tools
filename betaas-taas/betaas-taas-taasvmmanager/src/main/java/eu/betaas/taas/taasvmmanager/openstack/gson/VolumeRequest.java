package eu.betaas.taas.taasvmmanager.openstack.gson;

import java.util.UUID;

public class VolumeRequest {

	private Volume volume;
	
	public VolumeRequest () {}
	
	public Volume getVolume() {
		return volume;
	}

	public void setVolume(Volume volume) {
		this.volume = volume;
	}

	public class Volume {
		private String availability_zone;
		private UUID   source_volid;
		private String description;
		private UUID   snapshot_id;
		private int    size;
		private String name;
		private UUID   imageRef;
		private String volume_type;
		
		public Volume () {}

		public String getAvailability_zone() {
			return availability_zone;
		}

		public void setAvailability_zone(String availability_zone) {
			this.availability_zone = availability_zone;
		}

		public UUID getSource_volid() {
			return source_volid;
		}

		public void setSource_volid(UUID source_volid) {
			this.source_volid = source_volid;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public UUID getSnapshot_id() {
			return snapshot_id;
		}

		public void setSnapshot_id(UUID snapshot_id) {
			this.snapshot_id = snapshot_id;
		}

		public int getSize() {
			return size;
		}

		public void setSize(int size) {
			this.size = size;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public UUID getImageRef() {
			return imageRef;
		}

		public void setImageRef(UUID imageRef) {
			this.imageRef = imageRef;
		}

		public String getVolume_type() {
			return volume_type;
		}

		public void setVolume_type(String volume_type) {
			this.volume_type = volume_type;
		}
	}
}
