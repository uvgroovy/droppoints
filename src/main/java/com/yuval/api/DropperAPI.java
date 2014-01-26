package com.yuval.api;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

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
	
	@RequestMapping(value = URL_TRANSACTIONS, method = RequestMethod.POST)
	public ResponseEntity<Transaction> newTransaction() {
		Transaction t = new Transaction(URL_TRANSACTIONS + "/"+1);
		return new ResponseEntity<>(t, HttpStatus.CREATED);
	}

	
	@ResponseStatus(value = HttpStatus.NO_CONTENT)
	@RequestMapping(value = URL_TRANSACTION, method = RequestMethod.POST, consumes="multipart/form-data")
	public void addFile(HttpServletRequest request, @PathVariable long tid, @RequestParam("file") MultipartFile file) throws IOException {
		
		Uploader uploader = getUploaderForTransaction(tid);
		uploader.upload(file.getOriginalFilename(), file.getInputStream());
	}
	
	private Uploader getUploaderForTransaction(long id) throws IOException {
		return new HttpToFtpUploader(""+id);
	}

}
