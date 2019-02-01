package com.ericsson.ei.frontend;

import static org.junit.Assert.assertEquals;
import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

import java.io.File;
import java.io.IOException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockserver.client.MockServerClient;
import org.mockserver.integration.ClientAndServer;

import com.ericsson.ei.frontend.pageobjects.AddBackendPage;
import com.ericsson.ei.frontend.pageobjects.IndexPage;
import com.ericsson.ei.frontend.pageobjects.SubscriptionPage;
import com.ericsson.ei.frontend.pageobjects.SwitchBackendPage;

public class TestSwitchBackend extends SeleniumBaseClass {
    private static MockServerClient mockClient1;
    private static MockServerClient mockClient2;
    private static ClientAndServer mockServer1;
    private static ClientAndServer mockServer2;

    private static final String BASE_URL = "localhost";

    private static final String NEW_INSTANCE_SUBSCRIPTION_RESPONSE_FILEPATH = String.join(File.separator, "src",
            "functionaltest", "resources", "responses", "NewInstanceSubscriptionResponse.json");
    private static final String DEFAULT_INSTANCE_SUBSCRIPTION_RESPONSE_FILEPATH = String.join(File.separator, "src",
            "functionaltest", "resources", "responses", "DefaultInstanceSubscriptionResponse.json");
    private static final String INFORMATION_RESPONSE_FILEPATH = String.join(File.separator, "src", "functionaltest",
            "resources", "responses", "InformationResponse.json");

    @Test
    public void testSwitchBackend() throws Exception {
        // Set up
        int portServer1 = mockServer1.getLocalPort();
        int portServer2 = mockServer2.getLocalPort();

        backEndInstancesUtils.setDefaultBackEndInstanceToNull();
        backEndInstancesUtils.setDefaultBackEndInstance("new_instance_default", "localhost", portServer1, "", true);

        // Open indexpage and verify that it is opened
        IndexPage indexPageObject = new IndexPage(null, driver, baseUrl);
        indexPageObject.loadPage();
        assertEquals("Eiffel Intelligence", indexPageObject.getTitle());

        // Test add backend instance
        indexPageObject.clickAdminBackendInstancesBtn();
        AddBackendPage addBackendPage = indexPageObject.clickAddBackendInstanceBtn();
        SwitchBackendPage switchBackendPage = addBackendPage.addBackendInstance("new_instance", BASE_URL, portServer2,
                "");

        assertEquals("new_instance", switchBackendPage.getInstanceNameAtPosition(1));

        // Test switch to the newly added instance
        switchBackendPage.switchToBackendInstance(1);

        // Test that different set of subscriptions are available for each
        // instance
        SubscriptionPage subscriptionPage = indexPageObject.clickSubscriptionPage();
        assertEquals("new_instance_subscription", subscriptionPage.getSubscriptionNameFromSubscription());

        indexPageObject.clickSwitchBackendInstanceBtn();
        switchBackendPage.switchToBackendInstance(0);
        indexPageObject.clickSubscriptionPage();
        assertEquals("default_instance_subscription", subscriptionPage.getSubscriptionNameFromSubscription());

        // Test that backend instance can be removed
        indexPageObject.clickSwitchBackendInstanceBtn();
        switchBackendPage.removeInstanceNumber(1);
        assertEquals("switchBackendPage.presenceOfInstance returned true when it should have been false", false,
                switchBackendPage.presenceOfInstance(1));
    }

    @BeforeClass
    public static void setUpMocks() throws IOException {
        mockServer1 = startClientAndServer();
        mockServer2 = startClientAndServer();

        mockClient1 = new MockServerClient(BASE_URL, mockServer1.getLocalPort());
        mockClient2 = new MockServerClient(BASE_URL, mockServer2.getLocalPort());

        String informationResponse = getJSONStringFromFile(INFORMATION_RESPONSE_FILEPATH);
        mockClient2.when(request().withMethod("GET").withPath("/information"))
                .respond(response().withStatusCode(200).withBody(informationResponse));

        mockClient2.when(request().withMethod("GET").withPath("/auth/checkStatus"))
                .respond(response().withStatusCode(200).withBody(""));

        String newInstanceSubscriptionResponse = getJSONStringFromFile(NEW_INSTANCE_SUBSCRIPTION_RESPONSE_FILEPATH);
        mockClient2.when(request().withMethod("GET").withPath("/subscriptions"))
                .respond(response().withStatusCode(200).withBody(newInstanceSubscriptionResponse));

        String defaultInstanceSubscriptionResponse = getJSONStringFromFile(
                DEFAULT_INSTANCE_SUBSCRIPTION_RESPONSE_FILEPATH);
        mockClient1.when(request().withMethod("GET").withPath("/subscriptions"))
                .respond(response().withStatusCode(200).withBody(defaultInstanceSubscriptionResponse));
    }

    @AfterClass
    public static void tearDownMocks() throws IOException {
        mockClient1.stop();
        mockClient2.stop();

        mockServer1.stop();
        mockServer2.stop();
    }
}
