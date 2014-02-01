package com.yuval.api;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
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
import javax.servlet.http.HttpServletRequest;

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
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import com.yuval.api.resources.DbTransaction;
import com.yuval.api.resources.ResultFiles;
import com.yuval.api.resources.Transaction;
import com.yuval.api.resources.TransactionRepository;

@RunWith(MockitoJUnitRunner.class)
public class DropperAPITest {

	@InjectMocks
	DropperAPI dropperApi;
	
	@Mock
	UploaderFactory uploadFactory;
	
	@Mock
	ServletContext servletContext;
	
	@Mock
	TransactionRepository transactionRepository;
	
    @Captor
    ArgumentCaptor<String> captor;

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
		HttpServletRequest request = null;
		

		URI uri = new URI("f:123");
		when(uploadFactory.createUploader(anyString(), any())).thenReturn(uri);
		
		final Long dbId = 1L;
		DbTransaction anyT = Matchers.any(DbTransaction.class);
		when(transactionRepository.save(anyT)).thenAnswer(new Answer<DbTransaction>() {

			@Override
			public DbTransaction answer(InvocationOnMock invocation) throws Throwable {
				DbTransaction t = (DbTransaction)invocation.getArguments()[0];
				t.setId(dbId);
				return t;
			}
		});
		
		// test
		HttpEntity<Transaction> t = dropperApi.newTransaction(request);
		
		// verify		
		verify(uploadFactory).createUploader(anyString(), any());
		assertThat(t.getBody().getId().getHref(), containsString(dbId.toString()));
		assertThat(t.getBody().getBackEndUrl(), equalTo(uri));
		
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
		DbTransaction tr = new DbTransaction();
		tr.setId(Long.parseLong(tid));
		tr.setBackEndUrl(uri);
		when(transactionRepository.get(Long.parseLong(tid))).thenReturn(tr);

		Uploader uploader = mock(Uploader.class);
		when(uploadFactory.getUploader(uri)).thenReturn(uploader);
		
		HttpEntity<ResultFiles> t = dropperApi.addFile(tid, f);
		
		verify(uploadFactory).getUploader(uri);
		verify(uploader).upload(filename, is);

		assertEquals(size, t.getBody().getFiles().get(0).getSize());
		assertEquals(filename, t.getBody().getFiles().get(0).getName());
		
	}

}
