package com.yuval.api.impl.ftp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import com.google.common.io.ByteStreams;
import com.yuval.api.Uploader;

public class FtpUploader implements Uploader {

	private FTPClient ftpClient;

	public FtpUploader(FTPClient ftpClient) throws SocketException, IOException {
		this.ftpClient = ftpClient;

		setTransferParams();
	}

	@Override
	public void upload(String fileName, InputStream is) throws IOException {
		copyData(is, fileName);
		testCopySuccess();
	}

	@Override
	public void close() throws IOException {
		if (ftpClient != null) {
			ftpClient.disconnect();
			ftpClient = null;
		}
	}

	private void testCopySuccess() throws IOException {
		if (!ftpClient.completePendingCommand()) {
			throw new IOException();
		}
	}

	private void copyData(InputStream is, String fileName) throws IOException {
		try (OutputStream os = ftpClient.storeFileStream(fileName)) {
			ByteStreams.copy(is, os);
		}
	}

	private void setTransferParams() throws SocketException, IOException {
		ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
	}

}
