package eu.betaas.taas.taasvmmanager.openstack.gson;

public class Volume {
	private String id;
	private String display_name;
	private String display_description;
	private int size;
	private String volume_type;
	private Metadata metadata;
	private String availability_zone;
	private boolean bootable;
	private String snapshot_id;
	private String[] attachments;
	private String created_at;
	
	public Volume () {}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDisplay_name() {
		return display_name;
	}

	public void setDisplay_name(String display_name) {
		this.display_name = display_name;
	}

	public String getDisplay_description() {
		return display_description;
	}

	public void setDisplay_description(String display_description) {
		this.display_description = display_description;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public String getVolume_type() {
		return volume_type;
	}

	public void setVolume_type(String volume_type) {
		this.volume_type = volume_type;
	}

	public Metadata getMetadata() {
		return metadata;
	}

	public void setMetadata(Metadata metadata) {
		this.metadata = metadata;
	}

	public String getAvailability_zone() {
		return availability_zone;
	}

	public void setAvailability_zone(String availability_zone) {
		this.availability_zone = availability_zone;
	}

	public boolean isBootable() {
		return bootable;
	}

	public void setBootable(boolean bootable) {
		this.bootable = bootable;
	}

	public String getSnapshot_id() {
		return snapshot_id;
	}

	public void setSnapshot_id(String snapshot_id) {
		this.snapshot_id = snapshot_id;
	}

	public String[] getAttachments() {
		return attachments;
	}

	public void setAttachments(String[] attachments) {
		this.attachments = attachments;
	}

	public String getCreated_at() {
		return created_at;
	}

	public void setCreated_at(String created_at) {
		this.created_at = created_at;
	}

	public class Metadata {
		private String contents;
		
		public Metadata () {}

		public String getContents() {
			return contents;
		}

		public void setContents(String contents) {
			this.contents = contents;
		}
	}
}
