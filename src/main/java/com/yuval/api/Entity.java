package com.yuval.api;

import java.util.ArrayList;
import java.util.Collection;

import org.codehaus.jackson.annotate.JsonProperty;

abstract public class Entity {

    @JsonProperty("_links")
	private Collection<Link> links;

	public Collection<Link> getLinks() {
		return links;
	}

	public void addLinks(Collection<Link> links) {
		this.links.addAll(links);
	}

	public void addLink(String rel, String url) {
		if (links == null) {
			links = new ArrayList<Link>();
		}
		Link link = new Link();
		link.setRel(rel);
		link.setHref(url);
		links.add(link);
	}
	

	public Link getLink(String rel) {
		if (links != null) {
			for (Link link : links) {
				if (rel.equals(link.getRel())) {
					return link;
				}
			}
		}
		return null;
	}
	

	 protected Entity(String url) {
		addLink("self", url);
	}
}
