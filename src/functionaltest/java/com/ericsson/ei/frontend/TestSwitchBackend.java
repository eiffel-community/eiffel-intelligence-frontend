package com.ericsson.ei.frontend;

import org.junit.*;
import org.mockserver.client.MockServerClient;
import org.mockserver.integration.ClientAndServer;

import static org.mockserver.integration.ClientAndServer.startClientAndServer;

import static org.junit.Assert.*;

import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

import java.io.File;
import java.io.IOException;

import com.ericsson.ei.frontend.pageobjects.AddBackendPage;
import com.ericsson.ei.frontend.pageobjects.IndexPage;
import com.ericsson.ei.frontend.pageobjects.InfoPage;
import com.ericsson.ei.frontend.pageobjects.SubscriptionPage;
import com.ericsson.ei.frontend.pageobjects.SwitchBackendPage;

public class TestSwitchBackend extends SeleniumBaseClass{
    private static MockServerClient mockClient1;
    private static MockServerClient mockClient2;
    private static ClientAndServer mockServer1;
    private static ClientAndServer mockServer2;


    private static final String BASE_URL = "localhost";
    private static final int PORTSERVER1 = 8090;
    private static final int PORTSERVER2 = 8091;

    private static final String NEW_INSTANCE_SUBSCRIPTION_RESPONSE_FILEPATH = String.join(
        File.separator, "src", "functionaltest", "resources", "responses", "NewInstanceSubscriptionResponse.json");
    private static final String DEFAULT_INSTANCE_SUBSCRIPTION_RESPONSE_FILEPATH = String.join(
        File.separator, "src", "functionaltest", "resources", "responses", "DefaultInstanceSubscriptionResponse.json");
    private static final String INFORMATION_RESPONSE_FILEPATH = String.join(
        File.separator, "src", "functionaltest", "resources", "responses", "InformationResponse.json");

    @Test
    public void testSwitchBackend() throws Exception {
        // Open indexpage and verify that it is opened
        IndexPage indexPageObject = new IndexPage(null, driver, baseUrl);
        indexPageObject.loadPage();
        assertEquals("Eiffel Intelligence", indexPageObject.getTitle());

        // Test add backend instance
        indexPageObject.clickAdminBackendInstancesBtn();
        AddBackendPage addBackendPage = indexPageObject.clickAddBackendInstanceBtn();
        SwitchBackendPage switchBackendPage = addBackendPage.addBackendInstance("new_instance", BASE_URL, PORTSERVER2, "");
        assertEquals("new_instance", switchBackendPage.getNewInstanceName());

        // Test switch to the newly added instance
        switchBackendPage.switchToBackendInstance(1);
        InfoPage infoPage = indexPageObject.clickEiInfoBtn();
        assertEquals("http://localhost:" + PORTSERVER2, infoPage.getConnectedBackend());

        // Test that different set of subscriptions are available for each instance
        SubscriptionPage subscriptionPage = indexPageObject.clickSubscriptionPage();
        assertEquals("new_instance_subscription", subscriptionPage.getSubscriptionNameFromSubscription());

        indexPageObject.clickAdminBackendInstancesBtn();
        indexPageObject.clickSwitchBackendButton();
        switchBackendPage.switchToBackendInstance(0);
        indexPageObject.clickSubscriptionPage();
        assertEquals("default_instance_subscription", subscriptionPage.getSubscriptionNameFromSubscription());

        // Test that backend instance can be removed
        indexPageObject.clickAdminBackendInstancesBtn();
        indexPageObject.clickSwitchBackendButton();
        switchBackendPage.removeInstanceNumber(1);
        assertEquals("switchBackendPage.presenceOfInstance returned true when it should have been false",
            false, switchBackendPage.presenceOfInstance(1));
    }

    @BeforeClass
    public static void setUpMocks() throws IOException {
        mockServer1 = startClientAndServer(PORTSERVER1);
        mockServer2 = startClientAndServer(PORTSERVER2);

        mockClient1 = new MockServerClient(BASE_URL, PORTSERVER1);
        mockClient2 = new MockServerClient(BASE_URL, PORTSERVER2);

        String informationResponse = getJSONStringFromFile(INFORMATION_RESPONSE_FILEPATH);
        mockClient2
            .when(
                request()
                    .withMethod("GET")
                    .withPath("/information"))
            .respond(
                response()
                    .withStatusCode(200)
                    .withBody(informationResponse));

        mockClient2
            .when(
                request()
                    .withMethod("GET")
                    .withPath("/auth/checkStatus"))
            .respond(
                response()
                    .withStatusCode(200)
                    .withBody(""));

        String newInstanceSubscriptionResponse = getJSONStringFromFile(NEW_INSTANCE_SUBSCRIPTION_RESPONSE_FILEPATH);
        mockClient2
            .when(
                request()
                    .withMethod("GET")
                    .withPath("/subscriptions"))
            .respond(
                response().withStatusCode(200)
                    .withBody(newInstanceSubscriptionResponse));

        String defaultInstanceSubscriptionResponse = getJSONStringFromFile(DEFAULT_INSTANCE_SUBSCRIPTION_RESPONSE_FILEPATH);
        mockClient1
            .when(
                request()
                    .withMethod("GET")
                    .withPath("/subscriptions"))
            .respond(
                response()
                    .withStatusCode(200)
                    .withBody(defaultInstanceSubscriptionResponse));
    }

    @AfterClass
    public static void tearDownMocks() {
        mockServer1.stop();
        mockServer2.stop();

        mockClient1.stop();
        mockClient2.stop();
    }
}
