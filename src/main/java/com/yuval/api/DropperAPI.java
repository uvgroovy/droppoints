package com.yuval.api;

import java.io.IOException;
import java.util.Date;
import java.util.Random;

import javax.inject.Inject;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping(value = "/api")
public class DropperAPI {

	static final String URL_TRANSACTIONS = "/transactions";
	static final String URL_TRANSACTION = "/transactions/{tid}";

	@Inject
	UploaderFactory uploaderFactory;

	@RequestMapping(value = URL_TRANSACTIONS, method = RequestMethod.POST)
	public ResponseEntity<Transaction> newTransaction() throws IOException {
		String tid = String.format("%s-%d", new Date().toString(), new Random().nextLong()%1000);
		Transaction t = new Transaction("/droppoint/api" + URL_TRANSACTIONS + "/" + tid);
		uploaderFactory.createUploader(tid, null);

		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.set("Location", t.getLink("self").getHref());
		return new ResponseEntity<>(t,
				responseHeaders, HttpStatus.CREATED);
	}

	@RequestMapping(value = URL_TRANSACTION, method = RequestMethod.POST, consumes = "multipart/form-data")
	public ResponseEntity<ResultFiles> addFile(@PathVariable String tid,
			@RequestParam("files[]") MultipartFile file) throws IOException {

		switch (tid) {
		case "test-ok":
			return new ResponseEntity<>(getResultFiles(file),
					HttpStatus.CREATED);
		case "test-fail":
			throw new IllegalArgumentException();
		}

		UploaderFactory uploader = getUploaderForTransaction(tid);
		uploader.getUploader("" + tid).upload(file.getOriginalFilename(),
				file.getInputStream());
		return new ResponseEntity<>(getResultFiles(file), HttpStatus.CREATED);
	}

	private ResultFiles getResultFiles(MultipartFile file) {
		ResultFiles files = new ResultFiles();
		ResultFile rf = new ResultFile();
		rf.setName(file.getOriginalFilename());
		rf.setSize(file.getSize());
		files.getFiles().add(rf);
		return files;
	}

	private UploaderFactory getUploaderForTransaction(String id)
			throws IOException {
		return new HttpToFtpUploader(id);
	}

}
