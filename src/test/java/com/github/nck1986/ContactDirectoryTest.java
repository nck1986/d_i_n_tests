package com.github.nck1986;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.MediaType.TEXT_PLAIN;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import org.junit.Test;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.autoconfigure.web.client.*;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.Timed;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.HttpClientErrorException.*;
import org.springframework.web.client.RestTemplate;

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
     * Company ID contain not correct symbols or less 0
     */
    @Test
    public void companyIdNotValidUserValid() {

    }

    /**
     * Company have no contact data
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
     */
    @Test
    public void companyIdValidUserHaveNoPermissions() {

    }

    /**
     * User Name is too long (more then 100 symbols)
     */
    @Test
    public void companyIdValidUserNameTooLong() {

    }

    /**
     * Server response time is above 3000 ms
     */
    @Timed(millis = 3000)
    @Test
    public void serverResponseTooLong() {

    }

    public String stringURL(String companyId, String someName) {
        String url = BASE_URL + String.format(PATH, companyId, someName);
        return url;
    }
}
