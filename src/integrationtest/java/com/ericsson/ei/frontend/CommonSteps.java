package com.ericsson.ei.frontend;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import org.apache.commons.io.FileUtils;
import org.apache.tomcat.util.codec.binary.Base64;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootContextLoader;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.AbstractTestExecutionListener;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import com.ericsson.ei.utils.AMQPCommunication;
import com.ericsson.eiffelcommons.utils.HttpRequest;
import com.ericsson.eiffelcommons.utils.HttpRequest.HttpMethod;
import com.ericsson.eiffelcommons.utils.ResponseEntity;

import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

@Ignore
@RunWith(SpringRunner.class)
@SpringBootTest(classes = EIFrontendApplication.class, webEnvironment = WebEnvironment.RANDOM_PORT, properties = "spring.main.allow-bean-definition-overriding=true")
@ContextConfiguration(classes = EIFrontendApplication.class, loader = SpringBootContextLoader.class)
@TestExecutionListeners(listeners = { DependencyInjectionTestExecutionListener.class, CommonSteps.class })
public class CommonSteps extends AbstractTestExecutionListener {

    @LocalServerPort
    private int frontEndPort;
    private String frontEndHost = "localhost";
    private String protocol = "http";
    private String baseURL;
    private String rabbitmqHost;
    private int rabbitmqPort;
    private String rabbitmqUsername;
    private String rabbitmqPassword;
    private String rabbitmqExchange;
    private String rabbitmqKey;

    private List<HttpRequest> httpRequestList = new ArrayList<>();
    private HttpRequest httpRequest;
    private ResponseEntity response;

    private static final String RESOURCE_PATH = "src/integrationtest/resources/";
    private static final String BODIES_PATH = "bodies/";
    private static final String RESPONSES_PATH = "responses/";
    private static final String EIFFEL_EVENT_FILE = "eiffel_event.json";

    private static final Logger LOGGER = LoggerFactory.getLogger(CommonSteps.class);

    @Before
    public void beforeAllScenarios() {
        baseURL = String.format("%s://%s:%d", protocol, frontEndHost, frontEndPort);
    }

    @Before("@QueryByIdScenario or @QueryFreestyleScenario")
    public void beforeQueryScenario() {
        rabbitmqHost = System.getProperty("rabbitmq.host");
        rabbitmqPort = Integer.getInteger("rabbitmq.port");
        rabbitmqUsername = System.getProperty("rabbitmq.username");
        rabbitmqPassword = System.getProperty("rabbitmq.password");
        rabbitmqExchange = System.getProperty("rabbitmq.exchange");
        rabbitmqKey = System.getProperty("rabbitmq.key");
    }

    @Given("^frontend is up and running$")
    public void frontend_running() {
        LOGGER.debug("Front-end port: {}", frontEndPort);
        assertEquals(true, frontEndPort != 0);
    }

    @Given("^an aggregated object is created$")
    public void aggregated_object_created() throws IOException, TimeoutException {
        LOGGER.debug("Sending Eiffel events for aggregation.");
        String eventFilePath = Paths.get(RESOURCE_PATH, EIFFEL_EVENT_FILE).toString();
        String eventFileContent = FileUtils.readFileToString(new File(eventFilePath), "UTF-8");

        AMQPCommunication amqp = new AMQPCommunication(rabbitmqHost, rabbitmqPort, rabbitmqUsername, rabbitmqPassword);
        assertEquals(true, amqp.produceMessage(eventFileContent, rabbitmqExchange, rabbitmqKey));
        amqp.closeConnection();
        LOGGER.debug("Eiffel events sent.");
    }

    @When("^a \'(\\w+)\' request is prepared for REST API \'(.*)\'$")
    public void request_to_rest_api(String method, String endpoint) throws Throwable {
        LOGGER.debug("Method: {}, Endpoint: {}", method, endpoint);
        httpRequest = new HttpRequest(HttpMethod.valueOf(method));
        httpRequest.setBaseUrl(baseURL).setEndpoint(endpoint);
    }

    @When("^\'(.*)\' endpoint is set in request list at index (\\d+)$")
    public void endpoint_set_to(String endpoint, int index) throws Throwable {
        httpRequestList.get(index).setEndpoint(endpoint);
    }

    @When("^\'(.*)\' is appended to endpoint$")
    public void append_to_endpoint(String append) throws Throwable {
        String endpoint = httpRequest.getEndpoint() + append;
        httpRequest.setEndpoint(endpoint);
    }

    @When("^param key \'(.*)\' with value \'(.*)\' is added$")
    public void add_param(String key, String value) throws Throwable {
        httpRequest.addParam(key, value);
    }

    @When("^body is set to file \'(.*)\'$")
    public void set_body(String filename) throws Throwable {
        String filePath = Paths.get(RESOURCE_PATH, BODIES_PATH, filename).toString();
        String fileContent = FileUtils.readFileToString(new File(filePath), "UTF-8");
        httpRequest.addHeader("Content-type", "application/json").setBody(fileContent);
    }

    @When("^aggregation is prepared with rules file \'(.*)\' and events file \'(.*)\'$")
    public void aggregation_is_prepared(String rulesFileName, String eventsFileName) throws Throwable {
        String rulesPath = Paths.get(RESOURCE_PATH, BODIES_PATH, rulesFileName).toString();
        String eventsPath = Paths.get(RESOURCE_PATH, BODIES_PATH, eventsFileName).toString();
        String rules = FileUtils.readFileToString(new File(rulesPath), "UTF-8");
        String events = FileUtils.readFileToString(new File(eventsPath), "UTF-8");
        String body = new JSONObject().put("listRulesJson", new JSONArray(rules))
                .put("listEventsJson", new JSONArray(events)).toString();
        httpRequest.setBody(body);
    }

    @When("^username \"([^\"]*)\" and password \"([^\"]*)\" is used as credentials$")
    public void with_credentials(String username, String password) throws Throwable {
        String auth = username + ":" + password;
        String encodedAuth = new String(Base64.encodeBase64(auth.getBytes()), "UTF-8");
        httpRequest.addHeader("Authorization", "Basic " + encodedAuth);
    }

    @When("^request is sent$")
    public void request_sent() throws Throwable {
        response = httpRequest.performRequest();
    }

    @When("^request is performed from request list at index (\\d+)$")
    public void request_sent_using_list(int index) throws Throwable {
        response = httpRequestList.get(index).performRequest();
    }

    @When("^request is sent for (\\d+) seconds until response code no longer matches (\\d+)$")
    public void request_sent_body_not_received(int seconds, int statusCode) throws Throwable {
        long stopTime = System.currentTimeMillis() + (seconds * 1000);
        do {
            response = httpRequest.performRequest();
        } while (response.getStatusCode() == statusCode && stopTime > System.currentTimeMillis());
        assertEquals("Response code for URL: " + getUrl(), HttpStatus.OK.value(), response.getStatusCode());
    }

    @Then("^request is saved to request list at index (\\d+)$")
    public void request_is_saved_to_list(int index) throws Throwable {
        httpRequestList.add(index, httpRequest);
        httpRequest.resetHttpRequestObject();
    }

    @Then("^response code (\\d+) is received$")
    public void get_response_code(int statusCode) throws Throwable {
        LOGGER.debug("Response code: {}", response.getStatusCode());
        assertEquals("Response code for URL: " + getUrl(), statusCode, response.getStatusCode());
    }

    @Then("^response body \'(.*)\' is received$")
    public void get_response_body(String body) throws Throwable {
        LOGGER.debug("Response body: {}", response.getBody());
        assertEquals("Response body for URL: " + getUrl(), body, response.getBody());
    }

    @Then("^response body from file \'(.*)\' is received$")
    public void get_response_body_from_file(String filename) throws Throwable {
        String filePath = Paths.get(RESOURCE_PATH, RESPONSES_PATH, filename).toString();
        String fileContent = FileUtils.readFileToString(new File(filePath), "UTF-8");
        LOGGER.debug("File path: {}", filePath);
        LOGGER.debug("Response body: {}", response.getBody());
        assertEquals("File content", fileContent.replaceAll("\\s+", ""), response.getBody().replaceAll("\\s+", ""));
    }

    @Then("^response body contains \'(.*)\'$")
    public void response_body_contains(String value) throws Throwable {
        LOGGER.debug("Response body: {}", response.getBody());
        LOGGER.debug("Contains: {}", value);
        assertEquals("Response body to contain '" + value + "'", true, response.getBody().contains(value));
    }

    @Then("^response body does not contain \'(.*)\'$")
    public void response_body_does_not_contain(String value) throws Throwable {
        LOGGER.info("Response body: {}", response.getBody());
        LOGGER.info("Does not contain: {}", value);
        assertEquals("Response body not to contain '" + value + "'", true, !response.getBody().contains(value));
    }

    @Then("^remove \'(.*)\' from request headers at list index (\\d+)$")
    public void remove_key_from_request_headers_at_list_index(String headerKey, int index) {
        httpRequestList.get(index).removeHeader(headerKey);
    }

    private String getUrl() {
        final String url = String.format("%s%s", httpRequest.getBaseUrl(), httpRequest.getEndpoint());
        return url;
    }
}
