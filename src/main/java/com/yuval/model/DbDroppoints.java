package com.yuval.model;

import java.io.Serializable;
import java.net.URI;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@SuppressWarnings("serial")
@Entity
@Table(name = "droppoints")
public class DbDroppoints implements Serializable {

	@Id
	@GeneratedValue
	@Column(name = "id")
	private Long id;

	@Column(unique = true)
	private String email;
	
	private URI backEndUrl;
	private String uploaderName;

	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
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
