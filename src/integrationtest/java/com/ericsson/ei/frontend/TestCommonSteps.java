package com.ericsson.ei.frontend;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootContextLoader;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import com.ericsson.ei.config.Utils;
import com.ericsson.ei.utils.HttpRequest;
import com.ericsson.ei.utils.HttpRequest.HttpMethod;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

@Ignore
@RunWith(SpringRunner.class)
@SpringBootTest(classes = EIFrontendApplication.class, webEnvironment = WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = EIFrontendApplication.class, loader = SpringBootContextLoader.class)
public class TestCommonSteps {

    @LocalServerPort
    int frontendPort;
    private HttpRequest httpRequest;
    private ResponseEntity<String> response;
    private String hostName = "localhost";
    //private static String PROPERTIES_PATH = "/integration-test.properties";
    //private static String backendPort;

    private static final Logger LOGGER = LoggerFactory.getLogger(TestCommonSteps.class);

    /*
    @Before
    public void beforeTest() {
        String filePath = this.getClass().getResource(PROPERTIES_PATH).getFile();
        final Properties properties = Utils.getProperties(filePath);
        backendPort = properties.getProperty("ei.it.backend-port");
        LOGGER.debug("Back-end port: {}", backendPort);
    }
    */

    @Given("^frontend is up and running$")
    public void frontend_running() {
        LOGGER.info("Front-end port: {}", frontendPort);
    }

    @When("^a \'(\\w+)\' request is prepared for REST API \'(.*)\'$")
    public void request_to_rest_api(String method, String endpoint) throws Throwable {
        LOGGER.info("Method: {}, Endpoint: {}", method, endpoint);
        switch (method) {
        case "POST":
            httpRequest = new HttpRequest(HttpMethod.POST);
            break;
        case "GET":
            httpRequest = new HttpRequest(HttpMethod.GET);
            break;
        }
        httpRequest.setHost(hostName).setPort(frontendPort).setEndpoint(endpoint);
    }
    
    @When("^body is set to file \'(.*)\'$")
    public void set_body(String filename) throws Throwable {
        String filePath = this.getClass().getResource(filename).getFile();
        String fileContent = FileUtils.readFileToString(new File(filePath), "UTF-8");
        httpRequest.addHeader("Content-type", "application/json").setBody(fileContent);
    }

    @When("^request is sent$")
    public void request_sent() throws Throwable {
        response = httpRequest.performRequest();
    }

    @Then("^response code (\\d+) is received$")
    public void get_response_code(int statusCode) throws Throwable {
        LOGGER.info("Response code: {}", response.getStatusCode());
        assertEquals(HttpStatus.valueOf(statusCode), response.getStatusCode());
    }

    @Then("^response body \'(.*)\' is received$")
    public void get_response_body(String body) throws Throwable {
        LOGGER.info("Response body: {}", response.getBody());
        assertEquals(body, response.getBody());
    }
    
    @Then("^response body from file \'(.*)\' is received$")
    public void get_response_body_from_file(String filename) throws Throwable {
        String path = "/responses/";
        String filePath = this.getClass().getResource(path+filename).getFile();
        String fileContent = FileUtils.readFileToString(new File(filePath), "UTF-8");
        LOGGER.info("File path: {}", filePath);
        LOGGER.info("Response body: {}", response.getBody());
        assertEquals(fileContent, response.getBody());
    }
}
