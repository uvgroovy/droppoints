package com.yuval.api;

import java.io.IOException;
import java.net.URI;

public interface UploaderFactory {
	
	public URI createUploader(String folderName, Object metadata) throws IOException;
	
	public Uploader getUploader(URI uri) throws IOException;
}
