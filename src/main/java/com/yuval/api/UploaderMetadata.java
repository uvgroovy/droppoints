package com.yuval.api;

import java.util.LinkedHashMap;

@SuppressWarnings("serial")
public class UploaderMetadata extends LinkedHashMap<String, String> {

	public UploaderMetadata(String uploaderName) {
		put("name", uploaderName);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (java.util.Map.Entry<String, String> entry : entrySet()) {
			sb.append(entry.getKey());
			sb.append(":");
			sb.append(entry.getValue());
			sb.append('\n');
		}
		
		return sb.toString();
	}
}
