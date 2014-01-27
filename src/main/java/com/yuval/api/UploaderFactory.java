package com.yuval.api;

import java.io.IOException;

public interface UploaderFactory {
	public void createUploader(String name, Object metadata) throws IOException;
	public Uploader getUploader(String name) throws IOException;
}
