package com.yuval.api;

import java.io.IOException;
import java.io.InputStream;

public interface Uploader {

	public void upload(String name, InputStream is) throws IOException;
}
