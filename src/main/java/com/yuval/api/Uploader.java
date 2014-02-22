package com.yuval.api;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

public interface Uploader extends Closeable {

	public void upload(String fileName, InputStream is) throws IOException;
}
