package com.yuval.api.impl.ftp;

import java.io.IOException;
import java.net.SocketException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.TimeUnit;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import com.google.common.base.Preconditions;
import com.yuval.api.Uploader;
import com.yuval.api.UploaderFactory;

public class FtpUploaderFactory implements UploaderFactory {
	private static final int TIMEOUT = (int) TimeUnit.SECONDS.toMillis(10);

	private String baseDir = "droppoint";
	private String server;
	private String username;
	private String password;

	public FtpUploaderFactory(String server, String username, String password) {
		this.server = server;
		this.username = username;
		this.password = password;
	}

	public FtpUploaderFactory(String server, String username, String password,
			String baseDir) {
		this(server, username, password);
		this.baseDir = baseDir;
	}

	@Override
	public URI createUploader(String name, Object metadata) throws IOException {
		URI uri;
		try {
			uri = new URI("ftpprovider", name, null);
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException(e);
		}

		FTPClient ftpClient = createFTPClient();
		try {
			ftpClient.makeDirectory(baseDir);
			boolean cdSucess = ftpClient.changeWorkingDirectory(baseDir);
			if (!cdSucess) {
				throw new IllegalArgumentException("No such dir!");
			}

			ftpClient.makeDirectory(name);
			cdSucess = ftpClient.changeWorkingDirectory(name);
			if (!cdSucess) {
				throw new IllegalArgumentException("No such dir!");
			}
			return uri;
		} finally {
			ftpClient.disconnect();
		}

	}

	@Override
	public Uploader getUploader(URI uri) throws IOException {
		final String folder = uri.getPath();
		FTPClient ftpClient = null;
		try {
			ftpClient = createFTPClient();
			cdToUploadFolder(ftpClient, folder);
			FtpUploader uploader = new FtpUploader(ftpClient);
			ftpClient = null;
			return uploader;
		} finally {
			if (ftpClient != null) {
				ftpClient.disconnect();
			}
		}
	}

	private void cdToUploadFolder(FTPClient ftpClient, final String folder)
			throws IOException {
		boolean cdSucess = ftpClient.changeWorkingDirectory(baseDir);
		Preconditions.checkArgument(cdSucess, "No such dir! " + folder);

		cdSucess = ftpClient.changeWorkingDirectory(folder);
		Preconditions.checkArgument(cdSucess, "No such dir! " + folder);
	}

	private FTPClient createFTPClient() throws SocketException, IOException {

		FTPClient ftpClient = new FTPClient();

		ftpClient.setDataTimeout(TIMEOUT);
		ftpClient.setConnectTimeout(TIMEOUT);
		ftpClient.setDefaultTimeout(TIMEOUT);
		ftpClient.setSoTimeout(TIMEOUT);

		ftpClient.connect(server);
		ftpClient.enterLocalPassiveMode();
		ftpClient.login(username, password);

		int reply = ftpClient.getReplyCode();

		if (!FTPReply.isPositiveCompletion(reply)) {
			ftpClient.disconnect();
			throw new IOException();
		}
		return ftpClient;
	}

}
