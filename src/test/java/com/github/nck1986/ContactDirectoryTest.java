package com.github.nck1986;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.RequestMatcher;
import org.springframework.test.web.client.ResponseActions;
import org.springframework.web.client.HttpClientErrorException.BadRequest;
import org.springframework.web.client.HttpClientErrorException.Forbidden;
import org.springframework.web.client.HttpClientErrorException.NotFound;
import org.springframework.web.client.RestTemplate;

import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.encodeBasicAuth;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.TEXT_PLAIN;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RestClientTest
@AutoConfigureWebClient(registerRestTemplate = true)
public class ContactDirectoryTest {
	@Autowired
	MockRestServiceServer mockRestServiceServer;
	@Autowired
	RestTemplate restTemplate;

	final String URL = "/company/%s/users?name=%s";
	final String URL_PARAMETRIZED = format(URL,
	                                       "{companyId}",
	                                       "{userName}");

	@ParameterizedTest(name = "User `{1}` of company `{0}` should have contact `{2}`")
	@CsvFileSource(resources = "/contacts.csv")
	void ok(String companyId,
	        String userName,
	        String contact) {

		expect(companyId, userName)
				.andRespond(withSuccess(contact, TEXT_PLAIN));

		assertEquals(contact,
		             actual(companyId, userName),
		             "Wrong Contact");
		verify();
	}

	@ParameterizedTest(name = "User `{1}` of/or company `{0}` does not exist")
	@CsvFileSource(resources ="/nonExistentContacts.csv")
	void notFound(String companyId,
	              String userName) {

		expect(companyId, userName)
				.andRespond(withStatus(NOT_FOUND));

		assertThrows(NotFound.class,
		             () -> actual(companyId, userName),
		             "Contact not exist");
		verify();
	}

	@ParameterizedTest(name = "User `{1}` of/or company `{0}` has/have wrong Name/CompanyID")
	@CsvFileSource(resources ="/wrongContacts.csv")
	void badRequest(String companyId,
	                String userName) {

		expect(companyId, userName)
				.andRespond(withStatus(BAD_REQUEST));

		assertThrows(BadRequest.class,
		             () -> actual(companyId, userName),
		             "User Name or CompanyID is wrong");
		verify();
	}


	@ParameterizedTest(name = "User `{1}` have no access to company `{0}` contacts")
	@CsvFileSource(resources = "/forbiddenContacts.csv")
	void forbidden(String companyId,
	               String userName) {

		expect(companyId, userName)
				.andExpect(withBasicAuthorization())
				.andRespond(withStatus(FORBIDDEN));

		assertThrows(Forbidden.class,
		             () -> actual(companyId, userName),
		             "User have no access");
		verify();
	}

	String actual(String companyId,
	              String userName) {
		return restTemplate.getForObject(URL_PARAMETRIZED,
		                                 String.class,
		                                 companyId,
		                                 userName);
	}

	ResponseActions expect(String companyId,
	                       String userName) {
		return mockRestServiceServer.expect(requestTo(format(URL,
		                                                     companyId,
		                                                     userName)))
		                            .andExpect(method(GET));
	}

	private void verify() {
		mockRestServiceServer.verify();
	}

	private RequestMatcher withBasicAuthorization() {
		restTemplate.getInterceptors()
		            .add(new BasicAuthenticationInterceptor("user", "password"));

		return header(AUTHORIZATION,
		              "Basic " + encodeBasicAuth("user",
		                                         "password",
		                                         null));
	}
}
