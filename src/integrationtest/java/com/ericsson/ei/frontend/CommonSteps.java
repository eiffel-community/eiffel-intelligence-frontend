package com.ericsson.ei.frontend;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.AbstractTestExecutionListener;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import com.ericsson.ei.utils.AMQPCommunication;
import com.ericsson.ei.utils.HttpRequest;
import com.ericsson.ei.utils.HttpRequest.HttpMethod;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

@Ignore
@RunWith(SpringRunner.class)
@SpringBootTest(classes = EIFrontendApplication.class, webEnvironment = WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = EIFrontendApplication.class, loader = SpringBootContextLoader.class)
@TestExecutionListeners(listeners = { DependencyInjectionTestExecutionListener.class, CommonSteps.class })
public class CommonSteps extends AbstractTestExecutionListener {

    @LocalServerPort
    private int frontendPort;
    private HttpRequest httpRequest;
    private ResponseEntity<String> response;

    private static final String HOST = "localhost";
    private static final String RABBIT_USERNAME = "myuser";
    private static final String RABBIT_PASSWORD = "myuser";
    private static final String RABBIT_EXCHANGE = "ei-exchange";
    private static final String RABBIT_KEY = "#";

    private static final String BODIES_PATH = "/bodies/";
    private static final String RESPONSES_PATH = "/responses/";
    private static final String EIFFEL_EVENTS_JSON_PATH = "/eiffel_events_for_test.json";

    private static final Logger LOGGER = LoggerFactory.getLogger(CommonSteps.class);

    @Given("^frontend is up and running$")
    public void frontend_running() {
        LOGGER.info("Front-end port: {}", frontendPort);
        assertEquals(true, frontendPort != 0);
    }

    @Given("^an aggregated object is created$")
    public void aggregated_object_created() throws IOException {
        LOGGER.debug("Sending Eiffel events for aggregation.");
        List<String> eventNames = getEventNamesToSend();
        String eventsFilePath = this.getClass().getResource(EIFFEL_EVENTS_JSON_PATH).getFile();
        String eventsFileContent = FileUtils.readFileToString(new File(eventsFilePath), "UTF-8");
        JsonNode eventsNode = new ObjectMapper().readTree(eventsFileContent);

        int port = Integer.parseInt(System.getenv("RABBITMQ_AMQP_PORT"));
        AMQPCommunication amqp = new AMQPCommunication(HOST, port);
        amqp.setCredentials(RABBIT_USERNAME, RABBIT_PASSWORD);
        for (String eventName : eventNames) {
            String message = eventsNode.get(eventName).toString();
            assertEquals(true, amqp.produceMessage(message, RABBIT_EXCHANGE, RABBIT_KEY));
        }
        LOGGER.debug("Eiffel events sent.");
    }

    @When("^a \'(\\w+)\' request is prepared for REST API \'(.*)\'$")
    public void request_to_rest_api(String method, String endpoint) throws Throwable {
        LOGGER.info("Method: {}, Endpoint: {}", method, endpoint);
        httpRequest = new HttpRequest(HttpMethod.valueOf(method));
        httpRequest.setHost(HOST).setPort(frontendPort).setEndpoint(endpoint);
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
        String filePath = this.getClass().getResource(BODIES_PATH + filename).getFile();
        String fileContent = FileUtils.readFileToString(new File(filePath), "UTF-8");
        httpRequest.addHeader("Content-type", "application/json").setBody(fileContent);
    }

    @When("^aggregation is prepared with rules file \'(.*)\' and events file \'(.*)\'$")
    public void aggregation_is_prepared(String rulesFileName, String eventsFileName) throws Throwable {
        String rulesPath = this.getClass().getResource(BODIES_PATH + rulesFileName).getFile();
        String eventsPath = this.getClass().getResource(BODIES_PATH + eventsFileName).getFile();
        String rules = FileUtils.readFileToString(new File(rulesPath), "UTF-8");
        String events = FileUtils.readFileToString(new File(eventsPath), "UTF-8");
        String body = new JSONObject().put("listRulesJson", new JSONArray(rules))
                .put("listEventsJson", new JSONArray(events)).toString();
        httpRequest.setBody(body);
    }

    @When("^username \"(\\w+)\" and password \"(\\w+)\" is used as credentials$")
    public void with_credentials(String username, String password) throws Throwable {
        String auth = username + ":" + password;
        String encodedAuth = new String(Base64.encodeBase64(auth.getBytes()), "UTF-8");
        httpRequest.addHeader("Authorization", "Basic " + encodedAuth);
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
        String filePath = this.getClass().getResource(RESPONSES_PATH + filename).getFile();
        String fileContent = FileUtils.readFileToString(new File(filePath), "UTF-8");
        LOGGER.info("File path: {}", filePath);
        LOGGER.info("Response body: {}", response.getBody());
        assertEquals(fileContent.replaceAll("\\s+", ""), response.getBody().replaceAll("\\s+", ""));
    }

    @Then("^body contains \'(.*)\'$")
    public void response_body_contains(String contains) throws Throwable {
        LOGGER.info("Response body: {}", response.getBody());
        assertEquals(true, response.getBody().contains(contains));
    }

    /**
     * Events used in the aggregation.
     */
    protected List<String> getEventNamesToSend() {
        List<String> eventNames = new ArrayList<>();
        eventNames.add("event_EiffelArtifactCreatedEvent_3");
        eventNames.add("event_EiffelTestCaseTriggeredEvent_3");
        eventNames.add("event_EiffelTestCaseStartedEvent_3");
        eventNames.add("event_EiffelTestCaseFinishedEvent_3");
        return eventNames;
    }
}
