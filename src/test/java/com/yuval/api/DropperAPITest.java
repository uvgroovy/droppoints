package com.yuval.api;

import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;

import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@RunWith(MockitoJUnitRunner.class)
public class DropperAPITest {

	@InjectMocks
	DropperAPI dropperApi;
	
	@Mock
	UploaderFactory uploadFactory;

    @Captor
    ArgumentCaptor<String> captor;

	@Test
	public void shouldNewTransactionWork() throws IOException {
		
		ResponseEntity<Transaction> t = dropperApi.newTransaction();
		
		verify(uploadFactory).createUploader(captor.capture(), any());
		assertThat(t.getBody().getLink("self").getHref(), containsString(captor.getValue()));
		
	}

}
