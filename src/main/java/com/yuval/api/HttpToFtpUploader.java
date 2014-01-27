package com.yuval.api;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;
import java.util.concurrent.TimeUnit;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.springframework.stereotype.Component;

import com.google.common.io.ByteStreams;


@Component
public class HttpToFtpUploader implements Uploader,  UploaderFactory {
	private static final int TIMEOUT = (int) TimeUnit.SECONDS.toMillis(10);
	private static final String baseDir = "yuval";
	private String folder;

	@Override
	public void createUploader(String name, Object metadata) throws IOException {

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
		} finally {
			ftpClient.disconnect();
		}

	}
	@Override
	public Uploader getUploader(String name) throws IOException {
		return new HttpToFtpUploader(name);
	}

	public HttpToFtpUploader() throws IOException {
		
	}

	public HttpToFtpUploader(String folder) throws IOException {
		this.folder = folder;

	}
	
	@Override
	public void upload(String name, InputStream is) throws IOException {
		FTPClient ftpClient = createFTPClient();
		try {
	        boolean cdSucess = ftpClient.changeWorkingDirectory(baseDir);
	        if (!cdSucess) {
	        	throw new IllegalArgumentException("No such dir!");
	        }
	        cdSucess = ftpClient.changeWorkingDirectory(folder);
	        if (!cdSucess) {
	        	throw new IllegalArgumentException("No such dir!");
	        }
	        
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
        String server = "intigua.hostedftp.com";
        String username = "oran.epelbaum@intigua.com";
        String password = "hza346NS!";

        
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
