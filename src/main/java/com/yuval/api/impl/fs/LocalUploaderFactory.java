package com.yuval.api.impl.fs;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.stereotype.Component;

import com.yuval.api.Uploader;
import com.yuval.api.UploaderFactory;

@Component
public class LocalUploaderFactory implements UploaderFactory {

	Path baseDirectory = new File(System.getProperty("java.io.tmpdir")).toPath();

	public void setBaseDirectory(URI baseDirectory) {
		this.baseDirectory = Paths.get(baseDirectory);
	}

	public URI getBaseDirectory() {
		return baseDirectory.toUri();
	}
	
	@Override
	public URI createUploader(String name, Object metadata) throws IOException {
		Path subDir = baseDirectory.resolve(name);
		Files.createDirectories(subDir);
		return subDir.toUri();
	}

	@Override
	public Uploader getUploader(URI uri) throws IOException {
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
