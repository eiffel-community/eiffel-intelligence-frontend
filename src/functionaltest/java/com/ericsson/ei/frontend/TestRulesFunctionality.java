package com.ericsson.ei.frontend;

import static org.junit.Assert.assertEquals;
import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockserver.client.MockServerClient;
import org.mockserver.integration.ClientAndServer;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.ericsson.ei.config.SeleniumConfig;
import com.ericsson.ei.frontend.pageobjects.IndexPage;
import com.ericsson.ei.frontend.pageobjects.TestRulesPage;

public class TestRulesFunctionality extends SeleniumBaseClass {

    private static MockServerClient mockClient;
    private static ClientAndServer mockServer;
    private static final String BASE_URL = "localhost";

    private static final String DOWNLOADED_RULES_TEMPLATE_FILE_PATH = String.join(
            File.separator, SeleniumConfig.getTempDownloadDirectory().getPath(), "rulesTemplate.json");
    private static final String RULES_TEMPLATE_FILE_PATH = String.join(
            File.separator, "src", "functionaltest", "resources", "responses", "RulesTemplateObject.json");
    private static final String DOWNLOADED_RULES_FILE_PATH = String.join(
            File.separator, SeleniumConfig.getTempDownloadDirectory().getPath(), "rules.json");
    private static final String DOWNLOADED_EVENTS_TEMPLATE_FILE_PATH = String.join(
            File.separator, SeleniumConfig.getTempDownloadDirectory().getPath(), "eventsTemplate.json");
    private static final String EVENTS_TEMPLATE_FILE_PATH = String.join(
            File.separator, "src", "functionaltest", "resources", "responses", "EventsTemplateObject.json");
    private static final String AGGREGATED_OBJECT_FILE_PATH = String.join(
            File.separator, "src", "functionaltest", "resources", "responses", "AggregatedObjectResponse.json");

    @MockBean
    protected CloseableHttpClient mockedHttpClient;

    @Test
    public void testJourneyToFindAggregatedObjectButton() throws Exception {
        // Set up
        int portServer = mockServer.getLocalPort();
        backEndInstancesUtils.setDefaultBackEndInstanceToNull();
        backEndInstancesUtils.setDefaultBackEndInstance("new_instance_default", "localhost", portServer, "", true);

        // Open indexpage and verify that it is opened
        IndexPage indexPageObject = new IndexPage(null, driver, baseUrl);
        indexPageObject.loadPage();

        // Verify that we can navigate to test rules page
        TestRulesPage testRulesPage = indexPageObject.clickTestRulesPage();
        assert(new WebDriverWait(driver, 10).until((webdriver) -> testRulesPage.presenceOfTestRulesHeader()));

        // Verify that "download rules template" button works
        String downloadedRulesTemplate = "";
        String mockedResponse = getJSONStringFromFile(RULES_TEMPLATE_FILE_PATH);
        mockClient.when(request().withMethod("GET").withPath("/download/rulesTemplate"))
                .respond(response().withStatusCode(200).withBody(mockedResponse));

        testRulesPage.clickDownloadRulesTemplate();
        new WebDriverWait(driver, 10).until((webdriver) -> Files.exists(Paths.get(DOWNLOADED_RULES_TEMPLATE_FILE_PATH)));
        downloadedRulesTemplate = getJSONStringFromFile(DOWNLOADED_RULES_TEMPLATE_FILE_PATH);

        assertEquals(mockedResponse, downloadedRulesTemplate);

        // Verify that uploading the downloaded template file works.
        testRulesPage.uploadRulesTemplate(DOWNLOADED_RULES_TEMPLATE_FILE_PATH);
        String firstRule = testRulesPage.getFirstRuleText();
        assertEquals(true, downloadedRulesTemplate.contains(firstRule));

        // Verify that it is possible to download rules
        mockClient.when(request().withMethod("GET").withPath("/download/rules"))
                .respond(response().withStatusCode(200).withBody(downloadedRulesTemplate));
        testRulesPage.clickDownloadRulesButton();
        new WebDriverWait(driver, 10).until((webdriver) -> Files.exists(Paths.get(DOWNLOADED_RULES_FILE_PATH)));
        String downloadedRules = getJSONStringFromFile(DOWNLOADED_RULES_FILE_PATH);
        assertEquals(downloadedRulesTemplate, downloadedRules);

        // Verify that add rule button works
        testRulesPage.clickAddRuleButton();

        // Verify that removing a rule works
        testRulesPage.clickRemoveRuleNumber(3);
        assert(new WebDriverWait(driver, 10).until((webdriver) -> !testRulesPage.presenceOfRuleNumber(3)));

        // Verify that "download events template" button works
        String downloadEventsTemplateMockedResponse = getJSONStringFromFile(EVENTS_TEMPLATE_FILE_PATH);
        mockClient.when(request().withMethod("GET").withPath("/download/eventsTemplate"))
                .respond(response().withStatusCode(200).withBody(downloadEventsTemplateMockedResponse));

        testRulesPage.clickDownloadEventsTemplate();
        new WebDriverWait(driver, 10).until((webdriver) -> Files.exists(Paths.get(DOWNLOADED_EVENTS_TEMPLATE_FILE_PATH)));
        String downloadedEventsTemplate = getJSONStringFromFile(DOWNLOADED_EVENTS_TEMPLATE_FILE_PATH);
        assertEquals(downloadEventsTemplateMockedResponse, downloadedEventsTemplate);

        // Verify that uploading the downloaded template file works.
        testRulesPage.uploadEventsTemplate(DOWNLOADED_EVENTS_TEMPLATE_FILE_PATH);
        String firstEvent = testRulesPage.getFirstEventText();
        assertEquals(true, downloadedEventsTemplate.contains(firstEvent));

        // Verify that add rule button works
        testRulesPage.clickAddEventButton();

        // Verify that removing a rule works
        testRulesPage.clickRemoveEventNumber(3);
        assert(new WebDriverWait(driver, 10).until((webdriver) -> !testRulesPage.presenceOfEventNumber(3)));

        // Verify that find aggregated object button works
        String findAggregatedObjectResponse = getJSONStringFromFile(AGGREGATED_OBJECT_FILE_PATH);
        mockClient.when(request().withMethod("POST").withPath("/rules/rule-check/aggregation"))
                .respond(response().withStatusCode(200).withBody(findAggregatedObjectResponse));

        testRulesPage.clickFindAggregatedObject();
        assertEquals(findAggregatedObjectResponse, testRulesPage.getAggregatedResultData());
    }

    @BeforeClass
    public static void setUpMocks() throws IOException {
        mockServer = startClientAndServer();
        mockClient = new MockServerClient(BASE_URL, mockServer.getLocalPort());
    }

    @AfterClass
    public static void tearDownMocks() throws IOException {
        mockClient.stop();
        //mockServer.stop();
    }

}