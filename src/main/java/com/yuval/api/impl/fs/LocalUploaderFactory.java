package com.yuval.api.impl.fs;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.SocketException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.yuval.api.Uploader;
import com.yuval.api.UploaderFactory;
import com.yuval.api.UploaderMetadata;

public class LocalUploaderFactory implements UploaderFactory {

	Path baseDirectory = new File(System.getProperty("java.io.tmpdir")).toPath();

	public void setBaseDirectory(URI baseDirectory) {
		this.baseDirectory = Paths.get(baseDirectory);
	}

	public URI getBaseDirectory() {
		return baseDirectory.toUri();
	}
	
	@Override
	public URI initUploader(String name, UploaderMetadata metadata) throws IOException {
		Path subDir = baseDirectory.resolve(name);
		Files.createDirectories(subDir);
		writeMetadataIfNeeded(metadata, subDir);
		return subDir.toUri();
	}
	

	private void writeMetadataIfNeeded(UploaderMetadata metadata, Path subDir)
			throws SocketException, IOException, UnsupportedEncodingException {
		if (metadata != null) {
			// write metadata			
			try (ByteArrayInputStream in = new ByteArrayInputStream(
					metadata.toString().getBytes("UTF8"))) {
				Files.copy(in, subDir.resolve("metadata.txt"));
			}
		}
	}

	@Override
	public Uploader createUploader(URI uri) throws IOException {
		final Path dir = Paths.get(uri);
		return new Uploader() {
			
			@Override
			public void upload(String name, InputStream is) throws IOException {
				Path target = dir.resolve(name);
				Files.copy(is, target);
			}

			@Override
			public void close() throws IOException {				
			}
		};
	}

}
