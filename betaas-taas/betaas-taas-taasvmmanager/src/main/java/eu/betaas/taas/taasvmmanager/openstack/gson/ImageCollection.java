package eu.betaas.taas.taasvmmanager.openstack.gson;

import java.util.List;

public class ImageCollection {
	List <Image> images;

	public ImageCollection () {	}
	
	public List<Image> getImages() {
		return images;
	}

	public void setImages(List<Image> images) {
		this.images = images;
	}
}
