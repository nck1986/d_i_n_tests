package com.github.nck1986;

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

import org.junit.Test;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.autoconfigure.web.client.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.test.annotation.Timed;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.HttpClientErrorException.*;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.TimeUnit;

@RestClientTest
@AutoConfigureWebClient(registerRestTemplate = true)
public class ContactDirectoryTest {
    @Autowired
    MockRestServiceServer mockRestServiceServer;
    @Autowired
    RestTemplate restTemplate;

    private final String BASE_URL = "http://some_domain.com";
    private final String PATH = "/company/%s/users?name=%s";

    /**
     * Company ID correct and User name correct
     * <p>
     * %companyId% - exist and correct
     * %someName% - exist and correct
     * HTTP status code - 200
     */
    @Test
    public void companyIdValidUserValid() {
        String companyId = "0", someName = "Ezekiel";
        String url = stringURL(companyId, someName);
        String body = "+71111111111";

        mockRestServiceServer.expect(requestTo(url))
                .andExpect(method(GET))
                .andRespond(withSuccess(body, TEXT_PLAIN));
        String request = restTemplate.getForObject(url, String.class);
        assertEquals(body, request);
        mockRestServiceServer.verify();
    }

    /**
     * Company ID does not exist
     * <p>
     * %companyId% - not exist
     * %someName% - correct
     * HTTP status code - 404 Not Found
     */
    @Test
    public void companyIdNotExistUserValid() {
        String companyId = "777", someName = "Izergil";
        String url = stringURL(companyId, someName);

        mockRestServiceServer.expect(requestTo(url))
                .andExpect(method(GET))
                .andRespond(withStatus(NOT_FOUND));
        assertThrows(NotFound.class, () -> restTemplate.getForObject(url, String.class));
        mockRestServiceServer.verify();
    }

    /**
     * Company ID contain not correct symbols or less 0 or empty
     *
     * %companyId% - invalid value or empty
     * %someName% - correct
     * HTTP status code - 400 Bad Request
     */
    @Test
    public void companyIdNotValidUserValid() {
        String companyId = "-777", someName = "Izergil";
        String url = stringURL(companyId, someName);

        mockRestServiceServer.expect(requestTo(url))
                .andExpect(method(GET))
                .andRespond(withStatus(BAD_REQUEST));
        assertThrows(BadRequest.class, () -> restTemplate.getForObject(url, String.class));
        mockRestServiceServer.verify();
    }

    /**
     * Company have no contact data
     * <p>
     * HTTP status code - 204 No Content
     */
    @Test
    public void companyIdHaveNoContacts() {

    }

    /**
     * User name contain not correct symbols
     */
    @Test
    public void companyIdValidUserNotValid() {

    }

    /**
     * User from other company
     */
    @Test
    public void companyIdValidUserNotFromThisCompany() {

    }

    /**
     * User not exist in this company and can not be find
     */
    @Test
    public void companyIdValidUserNotExist() {

    }

    /**
     * User valid and exist but have no permissions to read contacts data
     * <p>
     * HTTP status code - 403 Forbidden
     */
    @Test
    public void companyIdValidUserHaveNoPermissions() {
        String companyId = "777", someName = "Izergil";
        String url = stringURL(companyId, someName);

        mockRestServiceServer.expect(requestTo(url))
                .andExpect(method(GET))
                .andExpect(header(AUTHORIZATION,
                        "Basic " + encodeBasicAuth("user", "password", null)))
                .andRespond(withStatus(FORBIDDEN));
        restTemplate.getInterceptors()
                .add(new BasicAuthenticationInterceptor("user", "password"));
        assertThrows(Forbidden.class, () -> restTemplate.getForObject(url, String.class));
        mockRestServiceServer.verify();
    }

    /**
     * User Name is too long (more then 100 symbols)
     * <p>
     * HTTP status code - 414 URI Too Long
     */
    @Test
    public void companyIdValidUserNameTooLong() {
        String companyId = "777", someName = "Izergil";
        String url = stringURL(companyId, someName);

        mockRestServiceServer.expect(requestTo(url))
                .andExpect(method(GET))
                .andRespond(withStatus(URI_TOO_LONG));

        mockRestServiceServer.verify();
    }

    /**
     * Server response time is above 3000 ms
     * <p>
     * HTTP status code - 408 Request Timeout
     */
    @Timed(millis = 3000)
    @Test
    public void serverResponseTooLong() {
        int TIMEOUT = (int) TimeUnit.SECONDS.toMillis(3);
        String companyId = "0", someName = "Ezekiel";
        String url = stringURL(companyId, someName);
        String body = "+71111111111";

        mockRestServiceServer.expect(requestTo(url))
                .andExpect(method(GET))
                .andRespond(withSuccess(body, TEXT_PLAIN));
        String request = restTemplate.getForObject(url, String.class);

        assertEquals(body, request);
        mockRestServiceServer.verify();
    }

    public String stringURL(String companyId, String someName) {
        String url = BASE_URL + String.format(PATH, companyId, someName);
        return url;
    }
}
