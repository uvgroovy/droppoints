package com.yuval.api;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
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
		String tid = "1";
		Transaction t = new Transaction(URL_TRANSACTIONS + "/"+tid);
		uploaderFactory.createUploader(tid, null);
		return new ResponseEntity<>(t, HttpStatus.CREATED);
	}

	
	@RequestMapping(value = URL_TRANSACTION, method = RequestMethod.POST, consumes="multipart/form-data")
	public ResponseEntity<Map<String, Object>>  addFile(@PathVariable String tid, @RequestParam("files[]") MultipartFile file) throws IOException {
		Map<String, Object> h = new HashMap<>();
		Map<String, Object> m = new HashMap<>();
		m.put("name", file.getOriginalFilename());
		m.put("size", file.getSize());
		h.put("files", Arrays.asList(m));
		switch(tid) {
		case "test-ok":
			return new ResponseEntity<>(h, HttpStatus.CREATED);
		case "test-fail":
			throw new IllegalArgumentException();
		}
		
		UploaderFactory uploader = getUploaderForTransaction(tid);
		uploader.getUploader(""+tid).upload(file.getOriginalFilename(), file.getInputStream());
		return null;
	}
	
	private UploaderFactory getUploaderForTransaction(String id) throws IOException {
		return new HttpToFtpUploader(id);
	}

}
