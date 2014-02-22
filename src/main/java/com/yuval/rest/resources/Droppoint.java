package com.yuval.rest.resources;

import org.springframework.hateoas.ResourceSupport;

public class Droppoint extends ResourceSupport {

	private String uploaderName;

	public String getUploaderName() {
		return uploaderName;
	}
	
	public void setUploaderName(String name) {
		this.uploaderName = name;
	}

}
