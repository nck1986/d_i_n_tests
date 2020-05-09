package com.github.nck1986;

import static org.junit.Assert.assertEquals;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.MediaType.TEXT_PLAIN;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

@RestClientTest
@AutoConfigureWebClient(registerRestTemplate = true)
public class ContactDirectoryTest {
    @Autowired
    MockRestServiceServer mockRestServiceServer;
    @Autowired
    RestTemplate restTemplate;

    private final String BASE_URL = "http://some_domain.com";
    private final String PATH = "/company/%d/users?name=%s";

    @Test
    public void companyIdValidUserValid() {
        mockRestServiceServer.expect(requestTo("/company/777/users?name=Izergil"))
                .andExpect(method(GET))
                .andRespond(withSuccess("+71111111111", TEXT_PLAIN));
        String request = restTemplate.getForObject("/company/777/users?name=Izergil", String.class);
        assertEquals("+71111111111", request);
        mockRestServiceServer.verify();
    }

    @Test public void companyIdNotExistUserValid(){

    }

    @Test public void companyIdNotValidUserValid(){

    }

    @Test public void companyIdHaveNoContacts(){

    }

    @Test public void companyIdValidUserNotValid(){

    }

    @Test public void companyIdValidUserNotFromThisCompany(){

    }

    @Test public void companyIdValidUserNotExist(){

    }

    /**
     * User valid and exist but have no permissions to read contacts data
     */
    @Test public void companyIdValidUserHaveNoPermissions(){

    }

    /**
     * User Name is too long (more then 100 symbols)
     */
    @Test public void companyIdValidUserNameTooLong(){

    }

    /**
     * Server response time is above 3000 ms
     */
    @Test public void serverResponseTooLong(){

    }
}
