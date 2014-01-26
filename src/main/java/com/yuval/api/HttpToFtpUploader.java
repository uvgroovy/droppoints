package com.yuval.api;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;
import java.util.concurrent.TimeUnit;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import com.google.common.io.ByteStreams;

public class HttpToFtpUploader implements Uploader {
	private static final int TIMEOUT = (int) TimeUnit.SECONDS.toMillis(10);
	private String folder;
	private String server;
	private String username;
	private String password;
	private String baseDir;

	public HttpToFtpUploader(String folder) throws IOException {
		this.folder = folder;
		this.server = "intigua.hostedftp.com";
		this.username = "oran.epelbaum@intigua.com";
		this.password = "hza346NS!";
		this.baseDir = "yuval";
		this.folder = folder;

		FTPClient ftpClient = createFTPClient();
		try {
	        ftpClient.makeDirectory(baseDir);
	        ftpClient.changeWorkingDirectory(baseDir);
	        ftpClient.makeDirectory(folder);
	        ftpClient.changeWorkingDirectory(folder);
		} finally {
			ftpClient.disconnect();
		}
	}
	
	@Override
	public void upload(String name, InputStream is) throws IOException {
		FTPClient ftpClient = createFTPClient();
		try {
	        ftpClient.changeWorkingDirectory(baseDir);
	        ftpClient.changeWorkingDirectory(folder);
	        
	        ftpClient.setSoTimeout(TIMEOUT);
	        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
	
	        OutputStream os = ftpClient.storeFileStream(name);
	        ByteStreams.copy(is, os);
	        os.close();
	        is.close();
	
	        if (!ftpClient.completePendingCommand()) {
	                throw new IOException();
	        }
		} finally {
			ftpClient.disconnect();
		}
	}

	private FTPClient createFTPClient() throws SocketException, IOException {
		FTPClient ftpClient = new FTPClient();

        ftpClient.setDataTimeout(TIMEOUT);
        ftpClient.setConnectTimeout(TIMEOUT);
        ftpClient.setDefaultTimeout(TIMEOUT);

        ftpClient.connect(server);
        ftpClient.enterLocalPassiveMode();
        ftpClient.login(username, password);

        int reply = ftpClient.getReplyCode();

        if (!FTPReply.isPositiveCompletion(reply)){
            ftpClient.disconnect();
        	throw new IOException();
        }
		return ftpClient;
	}

}
