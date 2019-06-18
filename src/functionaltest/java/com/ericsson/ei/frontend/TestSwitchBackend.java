package com.ericsson.ei.frontend;

import static org.junit.Assert.assertEquals;
import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

import java.io.File;
import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockserver.client.MockServerClient;
import org.mockserver.integration.ClientAndServer;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.ericsson.ei.frontend.pageobjects.AddBackendPage;
import com.ericsson.ei.frontend.pageobjects.SubscriptionPage;
import com.ericsson.ei.frontend.pageobjects.SwitchBackendPage;

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

    private AddBackendPage addBackendPage;
    private SwitchBackendPage switchBackendPage;
    private SubscriptionPage subscriptionPage;

    @Before
    public void before() throws IOException {
        int portServer1 = mockServer1.getLocalPort();
        backEndInstancesUtils.setDefaultBackEndInstanceToNull();
        backEndInstancesUtils.setDefaultBackEndInstance("new_instance_default", "localhost", portServer1, "", true);
        addBackendPage = new AddBackendPage(mockedHttpClient, driver, baseUrl);
        switchBackendPage = new SwitchBackendPage(mockedHttpClient, driver, baseUrl);
        subscriptionPage = new SubscriptionPage(mockedHttpClient, driver, baseUrl);
    }

    @Test
    public void testSwitchBackend() throws Exception {
        addAndVerifySecondBackendInstance();
        switchToSecondBackendInstance();
        verifySubscriptionForSecondBackendInstance();
        switchToFirstBackendInstance();
        verifySubscriptionForFirstBackendInstance();
        removeAndVerifySecondBackendInstance();
    }

    private void addAndVerifySecondBackendInstance() throws ClientProtocolException, IOException {
        addBackendPage.loadPage();
        addSecondBackendInstance();
        switchBackendPage.loadPage();
        verifyAddedBackendInstance();
    }

    private void removeAndVerifySecondBackendInstance() {
        switchBackendPage.loadPage();
        switchBackendPage.removeInstanceNumber(1);
        assertEquals("switchBackendPage.presenceOfInstance returned true when it should have been false", false,
                switchBackendPage.presenceOfInstance(1));
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

    private void verifyAddedBackendInstance() {
        assertEquals("new_instance", switchBackendPage.getInstanceNameAtPosition(1));
    }

    private void addSecondBackendInstance() throws ClientProtocolException, IOException {
        int portServer2 = mockServer2.getLocalPort();
        addBackendPage.addBackendInstance("new_instance", BASE_URL, portServer2, "");
    }

    private static void setupMockEndpoints() throws IOException {
        mockClient2.when(request().withMethod("GET").withPath("/auth/checkStatus")).respond(response().withStatusCode(200).withBody(""));

        mockClient2.when(request().withMethod("GET").withPath("/auth")).respond(response().withStatusCode(200).withBody("{\"security\":false}"));

        String newInstanceSubscriptionResponse = getJSONStringFromFile(NEW_INSTANCE_SUBSCRIPTION_RESPONSE_FILEPATH);
        mockClient2.when(request().withMethod("GET").withPath("/subscriptions"))
                .respond(response().withStatusCode(200).withBody(newInstanceSubscriptionResponse));

        String defaultInstanceSubscriptionResponse = getJSONStringFromFile(DEFAULT_INSTANCE_SUBSCRIPTION_RESPONSE_FILEPATH);
        mockClient1.when(request().withMethod("GET").withPath("/subscriptions"))
                .respond(response().withStatusCode(200).withBody(defaultInstanceSubscriptionResponse));

        mockClient1.when(request().withMethod("GET").withPath("/auth/checkStatus")).respond(response().withStatusCode(200).withBody(""));

        mockClient1.when(request().withMethod("GET").withPath("/auth")).respond(response().withStatusCode(200).withBody("{\"security\":false}"));
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
}
