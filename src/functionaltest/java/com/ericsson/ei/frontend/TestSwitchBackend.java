package com.ericsson.ei.frontend;

import static org.junit.Assert.assertEquals;
import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

import java.io.File;
import java.io.IOException;

import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockserver.client.MockServerClient;
import org.mockserver.integration.ClientAndServer;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.ericsson.ei.frontend.model.BackendInstance;
import com.ericsson.ei.frontend.pageobjects.SubscriptionPage;
import com.ericsson.ei.frontend.pageobjects.SwitchBackendPage;
import com.google.gson.JsonArray;

public class TestSwitchBackend extends SeleniumBaseClass {
    private static MockServerClient mockClient1;
    private static MockServerClient mockClient2;
    private static ClientAndServer mockServer1;
    private static ClientAndServer mockServer2;

    private static final String BASE_URL = "localhost";

    private static final String NEW_INSTANCE_SUBSCRIPTION_RESPONSE_FILEPATH = String.join(File.separator, "src", "functionaltest", "resources",
            "responses", "NewInstanceSubscriptionResponse.json");
    private static final String DEFAULT_INSTANCE_SUBSCRIPTION_RESPONSE_FILEPATH = String.join(File.separator, "src", "functionaltest", "resources",
            "responses", "DefaultInstanceSubscriptionResponse.json");

    @MockBean
    protected CloseableHttpClient mockedHttpClient;

    private SwitchBackendPage switchBackendPage;
    private SubscriptionPage subscriptionPage;

    @Before
    public void before() throws IOException {
        addBackendInstances();
        switchBackendPage = new SwitchBackendPage(mockedHttpClient, driver, baseUrl);
        subscriptionPage = new SubscriptionPage(mockedHttpClient, driver, baseUrl);
    }

    @Test
    public void testSwitchBackend() throws Exception {
        switchToSecondBackendInstance();
        verifySubscriptionForSecondBackendInstance();
        switchToFirstBackendInstance();
        verifySubscriptionForFirstBackendInstance();
    }

    @BeforeClass
    public static void setUpMocks() throws IOException {
        mockServer1 = startClientAndServer();
        mockServer2 = startClientAndServer();

        mockClient1 = new MockServerClient(BASE_URL, mockServer1.getLocalPort());
        mockClient2 = new MockServerClient(BASE_URL, mockServer2.getLocalPort());

        setupMockEndpoints();
    }

    @AfterClass
    public static void tearDownMocks() throws IOException {
        mockClient1.stop();
        mockClient2.stop();

        mockServer1.stop();
        mockServer2.stop();
    }

    private void switchToSecondBackendInstance() {
        switchBackendInstance(1);
    }

    private void switchToFirstBackendInstance() {
        switchBackendInstance(0);
    }

    private void switchBackendInstance(int instance) {
        switchBackendPage.loadPage();
        switchBackendPage.switchToBackendInstance(instance);
    }

    private void verifySubscriptionForFirstBackendInstance() {
        verifySubscription("default_instance_subscription");
    }

    private void verifySubscriptionForSecondBackendInstance() {
        verifySubscription("new_instance_subscription");
    }

    private void verifySubscription(String subscription) {
        subscriptionPage.loadPage();
        assertEquals(subscription, subscriptionPage.getSubscriptionNameFromSubscription());
    }

    private void addBackendInstances() {
        int server1Port = mockServer1.getLocalPort();

        BackendInstance backend1 = new BackendInstance();
        backend1.setName("new_instance_default");
        backend1.setHost("localhost");
        backend1.setPort(Integer.toString(server1Port));
        backend1.setContextPath("");
        backend1.setDefaultBackend(true);

        int server2Port = mockServer2.getLocalPort();
        BackendInstance backend2 = new BackendInstance();
        backend2.setName("new_instance");
        backend2.setHost(BASE_URL);
        backend2.setPort(Integer.toString(server2Port));
        backend2.setContextPath("");
        backend2.setDefaultBackend(true);

        JsonArray backendInstances = new JsonArray();
        backendInstances.add(backend1.getAsJsonObject());
        backendInstances.add(backend2.getAsJsonObject());
        setBackendInstances(backendInstances);
    }

    private static void setupMockEndpoints() throws IOException {
        mockClient2.when(request().withMethod("GET").withPath("/status")).respond(response().withStatusCode(200).withBody("{\"eiffelIntelligenceStatus\" : \"AVAILABLE\"}"));

        mockClient2.when(request().withMethod("GET").withPath("/authentication")).respond(response().withStatusCode(200).withBody("{\"security\":false}"));

        String newInstanceSubscriptionResponse = getJSONStringFromFile(NEW_INSTANCE_SUBSCRIPTION_RESPONSE_FILEPATH);
        mockClient2.when(request().withMethod("GET").withPath("/subscriptions"))
                .respond(response().withStatusCode(200).withBody(newInstanceSubscriptionResponse));

        String defaultInstanceSubscriptionResponse = getJSONStringFromFile(DEFAULT_INSTANCE_SUBSCRIPTION_RESPONSE_FILEPATH);
        mockClient1.when(request().withMethod("GET").withPath("/subscriptions"))
                .respond(response().withStatusCode(200).withBody(defaultInstanceSubscriptionResponse));

        mockClient1.when(request().withMethod("GET").withPath("/status")).respond(response().withStatusCode(200).withBody("{\"eiffelIntelligenceStatus\" : \"AVAILABLE\"}"));

        mockClient1.when(request().withMethod("GET").withPath("/authentication")).respond(response().withStatusCode(200).withBody("{\"security\":false}"));
    }
}
