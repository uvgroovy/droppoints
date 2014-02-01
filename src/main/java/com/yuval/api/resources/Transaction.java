package com.yuval.api.resources;

import java.net.URI;

import org.springframework.hateoas.ResourceSupport;

public class Transaction extends ResourceSupport {

	private URI backEndUrl;
	private String uploaderName;

	public void setBackEndUrl(URI url) {
		this.backEndUrl = url;
	}
	
	public URI getBackEndUrl() {
		return backEndUrl;
	}

	public String getUploaderName() {
		return uploaderName;
	}
	
	public void setUploaderName(String name) {
		this.uploaderName = name;
	}

}
