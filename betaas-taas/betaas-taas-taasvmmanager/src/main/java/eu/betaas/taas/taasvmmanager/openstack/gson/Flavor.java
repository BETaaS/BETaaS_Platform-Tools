package eu.betaas.taas.taasvmmanager.openstack.gson;

import com.google.gson.annotations.SerializedName;

public class Flavor {
	private String name;
	private long   ram;
	private int    vcpus;
	private long   disk;
	private String id;
	private Link[] links;
	
	@SerializedName("os-flavor-access:is_public")
	private boolean pub;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getRam() {
		return ram;
	}

	public void setRam(long ram) {
		this.ram = ram;
	}

	public int getVcpus() {
		return vcpus;
	}

	public void setVcpus(int vcpus) {
		this.vcpus = vcpus;
	}

	public long getDisk() {
		return disk;
	}

	public void setDisk(long disk) {
		this.disk = disk;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public boolean isPub() {
		return pub;
	}

	public void setPub(boolean pub) {
		this.pub = pub;
	}
	
	public Link[] getLinks() {
		return links;
	}

	public void setLinks(Link[] links) {
		this.links = links;
	}

	public class Link {
		private String href;
		private String rel;
		
		public Link() {}

		public String getHref() {
			return href;
		}

		public void setHref(String href) {
			this.href = href;
		}

		public String getRel() {
			return rel;
		}

		public void setRel(String rel) {
			this.rel = rel;
		}
	}
}
