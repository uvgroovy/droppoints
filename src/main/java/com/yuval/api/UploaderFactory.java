package com.yuval.api;

import java.io.IOException;
import java.net.URI;

public interface UploaderFactory {
	
	public URI initUploader(String folderName, UploaderMetadata metadata) throws IOException;
	
	public Uploader createUploader(URI uri) throws IOException;
}
