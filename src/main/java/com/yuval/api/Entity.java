package com.yuval.api;

import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonProperty;

abstract public class Entity {

    @JsonProperty("_links")
	private Map<String, Link> links;

	public Map<String, Link> getLinks() {
		return links;
	}

	public void addLinks(Map<String, Link> links) {
		this.links.putAll(links);
	}

	public void addLink(String rel, String url) {
		if (links == null) {
			links = new HashMap<>();
		}
		Link link = new Link();
		link.setHref(url);
		links.put(rel, link);
	}
	

	public Link getLink(String rel) {
		if (links.containsKey(rel)) {
			return links.get(rel);
		}
		return null;
	}
	

	 protected Entity(String url) {
		addLink("self", url);
	}
}
