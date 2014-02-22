package com.yuval.rest;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Collection;
import java.util.Date;
import java.util.Random;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletResponse;

import org.springframework.hateoas.ResourceSupport;
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

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Sets;
import com.yuval.api.Uploader;
import com.yuval.api.UploaderFactory;
import com.yuval.model.DbDroppoints;
import com.yuval.model.DroppointRepository;
import com.yuval.rest.resources.ResultFile;
import com.yuval.rest.resources.ResultFiles;
import com.yuval.rest.resources.Droppoint;

@Controller
@RequestMapping(value = DroppointsController.REQUEST_PATH)
public class DroppointsController {

	private static final String NAME = "name";

	static final String REQUEST_PATH = "/api/drops";

	static final String URL_TRANSACTION = "/{tid}";

	@Inject
	@Named("uploaderFactory")
	UploaderFactory uploaderFactory;

	@Inject
	DroppointRepository transactionRepository;

	private static Set<String> REQUIRED_META_DATA = Sets.newHashSet(NAME);

	@RequestMapping(method = RequestMethod.OPTIONS)
	public HttpEntity<Collection<String>> getMetaDataForTransaction()
			throws IOException {

		Collection<String> c = REQUIRED_META_DATA;
		ResponseEntity<Collection<String>> r = new ResponseEntity<>(c,
				HttpStatus.OK);
		return r;
	}

	@RequestMapping(method = RequestMethod.POST)
	public HttpEntity<Droppoint> newTransaction(
			@RequestBody Droppoint requestJson) throws IOException {

		DbDroppoints dbT = validateAndSetMetaData(requestJson);

		URI url = createRemoteUploadLocation();
		dbT.setBackEndUrl(url);

		dbT = transactionRepository.save(dbT);

		return returnNewObjResponse(convertToJsonModel(dbT));
	}

	private static <T extends ResourceSupport> ResponseEntity<T> returnNewObjResponse(
			T obj) {

		HttpHeaders responseHeaders = new HttpHeaders();
		URI location = URI.create(obj.getId().getHref());
		responseHeaders.setLocation(location);
		return new ResponseEntity<>(obj, responseHeaders, HttpStatus.CREATED);
	}

	private URI createRemoteUploadLocation() throws IOException {
		String folderName = String.format("%s-%d", new Date().toString(),
				new Random().nextLong() % 1000);
		URI url = uploaderFactory.createUploader(folderName, null);
		return url;
	}

	private Droppoint convertToJsonModel(DbDroppoints dbT) {
		Droppoint t = new Droppoint();
		// set meta-data
		// never send out the backend url as it is private
		// t.setBackEndUrl(dbT.getBackEndUrl());
		// serialize
		t.setUploaderName(dbT.getUploaderName());

		// serializeMetaDataToDb and get id
		long newId = dbT.getId();

		t.add(linkTo(DroppointsController.class).slash(newId).withSelfRel());
		return t;
	}

	private DbDroppoints validateAndSetMetaData(Droppoint requestJson) {
		Preconditions.checkArgument(
				!Strings.isNullOrEmpty(requestJson.getUploaderName()),
				"Missign metadata for transaction!");

		DbDroppoints t = new DbDroppoints();
		t.setUploaderName(requestJson.getUploaderName());
		return t;
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

		DbDroppoints t = transactionRepository.get(Long.parseLong(tid));

		try (InputStream fileInputStream = file.getInputStream();
				Uploader uploader = uploaderFactory.getUploader(t
						.getBackEndUrl())) {
			uploader.upload(file.getOriginalFilename(), fileInputStream);
		}
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
