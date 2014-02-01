package com.yuval.api;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

import java.io.IOException;
import java.net.URI;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.google.common.collect.Sets;
import com.yuval.api.resources.DbTransaction;
import com.yuval.api.resources.ResultFile;
import com.yuval.api.resources.ResultFiles;
import com.yuval.api.resources.Transaction;
import com.yuval.api.resources.TransactionRepository;

@Controller
@RequestMapping(value = DropperAPI.REQUEST_PATH)
public class DropperAPI {

	static final String REQUEST_PATH = "/api/transactions";

	static final String URL_TRANSACTION = "/{tid}";

	@Inject
	UploaderFactory uploaderFactory;

	@Inject
	TransactionRepository transactionRepository;

	private static Set<String> REQUIRED_META_DATA = Sets.newHashSet("Name");

	@RequestMapping(method = RequestMethod.OPTIONS)
	public HttpEntity<Collection<String>> getMetaDataForTransaction()
			throws IOException {

		Collection<String> c = REQUIRED_META_DATA;
		ResponseEntity<Collection<String>> r = new ResponseEntity<>(c,
				HttpStatus.OK);
		return r;
	}

	@RequestMapping(method = RequestMethod.POST)
	public HttpEntity<Transaction> newTransaction(@RequestBody Map<String, Object> requestJson)
			throws IOException {

		validateMetaData(requestJson);

		String folderName = String.format("%s-%d", new Date().toString(),
				new Random().nextLong() % 1000);

		URI url = uploaderFactory.createUploader(folderName, null);

		DbTransaction dbT = new DbTransaction();
		dbT.setBackEndUrl(url);
		dbT.setUploaderName("name");
		dbT = transactionRepository.save(dbT);

		Transaction t = new Transaction();
		// set meta-data
		t.setBackEndUrl(dbT.getBackEndUrl());
		// serialize
		t.setUploaderName("fds");

		// serializeMetaDataToDb and get id
		long newId = dbT.getId();

		t.add(linkTo(DropperAPI.class).slash(newId).withSelfRel());

		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.set("Location", t.getLink("self").getHref());
		return new ResponseEntity<>(t, responseHeaders, HttpStatus.CREATED);
	}

	private void validateMetaData(Map<String, Object> requestJson) {

	}

	@RequestMapping(value = URL_TRANSACTION, method = RequestMethod.POST, consumes = "multipart/form-data")
	public HttpEntity<ResultFiles> addFile(@PathVariable String tid,
			@RequestParam("files[]") MultipartFile file) throws IOException {

		switch (tid) {
		case "test-ok":
			return new ResponseEntity<>(getResultFiles(file),
					HttpStatus.CREATED);
		case "test-fail":
			throw new IllegalArgumentException();
		}

		DbTransaction t = transactionRepository.get(Long.parseLong(tid));

		uploaderFactory.getUploader(t.getBackEndUrl()).upload(
				file.getOriginalFilename(), file.getInputStream());
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

	@ExceptionHandler(IllegalArgumentException.class)
	@ResponseBody
	public String exceptionHandler(IllegalArgumentException e,
			HttpServletResponse response) {
		response.setStatus(HttpStatus.BAD_REQUEST.value());
		return null;
	}
}
