package com.yuval.rest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.ServletContext;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.springframework.http.HttpEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import com.yuval.api.Uploader;
import com.yuval.api.UploaderFactory;
import com.yuval.api.UploaderMetadata;
import com.yuval.model.DbDroppoint;
import com.yuval.model.DroppointRepository;
import com.yuval.rest.DroppointsController;
import com.yuval.rest.resources.ResultFiles;
import com.yuval.rest.resources.Droppoint;

@RunWith(MockitoJUnitRunner.class)
public class DroppointsControllerTest {

	@InjectMocks
	DroppointsController dropperApi;
	
	@Mock
	UploaderFactory uploadFactory;
	
	@Mock
	ServletContext servletContext;
	
	@Mock
	DroppointRepository transactionRepository;
	
    @Captor
    ArgumentCaptor<UploaderMetadata> metadataCaptor;

    @Before
    public void init() {

		MockHttpServletRequest request = new MockHttpServletRequest();
		ServletRequestAttributes requestAttributes = new ServletRequestAttributes(request);
		RequestContextHolder.setRequestAttributes(requestAttributes);
		
    	when(servletContext.getContextPath()).thenReturn("");
    }
    
	@Test
	public void shouldNewTransactionWork() throws IOException, URISyntaxException {
		
		//new HashMap<String, String>()
		Droppoint request = new Droppoint();
		String uploaderName = "d";
		request.setUploaderName(uploaderName);
		

		URI uri = new URI("f:123");
		when(uploadFactory.initUploader(anyString(), any(UploaderMetadata.class))).thenReturn(uri);
		
		final Long dbId = 1L;
		DbDroppoint anyT = Matchers.any(DbDroppoint.class);
		when(transactionRepository.save(anyT)).thenAnswer(new Answer<DbDroppoint>() {

			@Override
			public DbDroppoint answer(InvocationOnMock invocation) throws Throwable {
				DbDroppoint t = (DbDroppoint)invocation.getArguments()[0];
				t.setId(dbId);
				return t;
			}
		});
		
		// test
		HttpEntity<Droppoint> t = dropperApi.newDroppoint(request);
		
		// verify		
		verify(uploadFactory).initUploader(anyString(), metadataCaptor.capture());

		assertThat(uploaderName, isIn(metadataCaptor.getValue().values()));
		
		assertThat(t.getBody().getId().getHref(), containsString(dbId.toString()));
		
		assertThat(t.getBody().getId().getHref(), containsString(dbId.toString()));
		
	}
	

	@Test(expected = IllegalArgumentException.class)
	public void traversalShouldntWork() throws IOException {

		MultipartFile f = mock(MultipartFile.class);
		when(f.getOriginalFilename()).thenReturn("..");
		
		dropperApi.addFile("test-ok", f);
		
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testFailShouldThrow() throws IOException {

		MultipartFile f = mock(MultipartFile.class);
		when(f.getOriginalFilename()).thenReturn("b");
		
		dropperApi.addFile("test-fail", f);
		
	}


	@Test()
	public void testOkShouldDoNothing() throws IOException {

		MultipartFile f = mock(MultipartFile.class);
		when(f.getOriginalFilename()).thenReturn("b");
		
		dropperApi.addFile("test-ok", f);
		
	}
	
	@Test
	public void shouldAddFileWork() throws IOException, URISyntaxException {

		String tid = "123";
		URI uri = new URI("f:123");
		String filename = "f.name";
		long size = 123;
		MultipartFile f = mock(MultipartFile.class);
		InputStream is = mock(InputStream.class);
		when(f.getInputStream()).thenReturn(is);
		when(f.getOriginalFilename()).thenReturn(filename);
		when(f.getSize()).thenReturn(size);
		DbDroppoint tr = new DbDroppoint();
		tr.setId(Long.parseLong(tid));
		tr.setBackEndUrl(uri);
		when(transactionRepository.get(Long.parseLong(tid))).thenReturn(tr);

		Uploader uploader = mock(Uploader.class);
		when(uploadFactory.createUploader(uri)).thenReturn(uploader);
		
		HttpEntity<ResultFiles> t = dropperApi.addFile(tid, f);
		
		verify(uploadFactory).createUploader(uri);
		verify(uploader).upload(filename, is);
		
		assertEquals(size, t.getBody().getFiles().get(0).getSize());
		assertEquals(filename, t.getBody().getFiles().get(0).getName());
		
	}

}
