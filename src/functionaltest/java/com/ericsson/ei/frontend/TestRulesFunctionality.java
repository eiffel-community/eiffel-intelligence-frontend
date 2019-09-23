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
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockserver.client.MockServerClient;
import org.mockserver.integration.ClientAndServer;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.ericsson.ei.config.SeleniumConfig;
import com.ericsson.ei.frontend.pageobjects.TestRulesPage;


public class TestRulesFunctionality extends SeleniumBaseClass {

    private static MockServerClient mockClient;
    private static ClientAndServer mockServer;
    private static final String BASE_URL = "localhost";

    private static final String DOWNLOADED_RULES_TEMPLATE_FILE_PATH = String.join(File.separator, SeleniumConfig.getTempDownloadDirectory().getPath(),
            "rules.json");
    private static final String RULES_TEMPLATE_FILE_PATH = String.join(File.separator, "src", "functionaltest", "resources", "responses",
            "RulesTemplateObject.json");
    private static final String DOWNLOADED_RULES_FILE_PATH = String.join(File.separator, SeleniumConfig.getTempDownloadDirectory().getPath(),
            "rules.json");
    private static final String DOWNLOADED_EVENTS_TEMPLATE_FILE_PATH = String.join(File.separator,
            SeleniumConfig.getTempDownloadDirectory().getPath(), "eventsTemplate.json");
    private static final String EVENTS_TEMPLATE_FILE_PATH = String.join(File.separator, "src", "functionaltest", "resources", "responses",
            "EventsTemplateObject.json");
    private static final String AGGREGATED_OBJECT_FILE_PATH = String.join(File.separator, "src", "functionaltest", "resources", "responses",
            "AggregatedObjectResponse.json");

    @MockBean
    protected CloseableHttpClient mockedHttpClient;

    private TestRulesPage testRulesPage;
    private String downloadedRulesTemplate = "";
    private String downloadedEventsTemplate = "";

    @Before
    public void before() throws IOException {
        int serverPort = mockServer.getLocalPort();
        setBackendInstance("new_instance_default", "localhost", serverPort, "", true);

        testRulesPage = new TestRulesPage(null, driver, baseUrl);
        testRulesPage.loadPage();
    }

    @Test
    public void testJourneyToFindAggregatedObjectButton() throws Exception {
        enableTestRulesButtons();
        verifyDownloadRulesTemplateButton();
        verifyUploadRulesFile();
        verifyDownloadRulesButton();
        verifyAddAndRemoveRuleButton();
        verifyDownloadEventsTemplateButton();
        verifyUploadEventsFile();
        verifyAddAndRemoveEventButton();
        verifyAggregatedObjectButton();
    }

    @BeforeClass
    public static void setUpMocks() throws IOException {
        mockServer = startClientAndServer();
        mockClient = new MockServerClient(BASE_URL, mockServer.getLocalPort());
        mockClient.when(request().withMethod("GET").withPath("/auth/checkStatus"))
            .respond(response().withStatusCode(200));
    }

    @AfterClass
    public static void tearDownMocks() throws IOException {
        mockClient.stop();
    }

    private void verifyAggregatedObjectButton() throws IOException {
        String findAggregatedObjectResponse = getJSONStringFromFile(AGGREGATED_OBJECT_FILE_PATH);
        mockClient.when(request().withMethod("POST").withPath("/rules/rule-check/aggregation"))
                .respond(response().withStatusCode(200).withBody(findAggregatedObjectResponse));

        testRulesPage.clickFindAggregatedObject();
        assertEquals(findAggregatedObjectResponse, testRulesPage.getAggregatedResultData());
    }

    private void verifyAddAndRemoveEventButton() {
        testRulesPage.clickAddEventButton();
        testRulesPage.clickRemoveEventNumber(3);
        assert (new WebDriverWait(driver, 10).until((webdriver) -> !testRulesPage.presenceOfEventNumber(3)));
    }

    private void verifyUploadEventsFile() {
        testRulesPage.uploadEventsTemplate(DOWNLOADED_EVENTS_TEMPLATE_FILE_PATH);
        String firstEvent = testRulesPage.getFirstEventText();
        assertEquals(true, downloadedEventsTemplate.contains(firstEvent));
    }

    private void verifyDownloadEventsTemplateButton() throws IOException {
        String downloadEventsTemplateMockedResponse = getJSONStringFromFile(EVENTS_TEMPLATE_FILE_PATH);
        mockClient.when(request().withMethod("GET").withPath("/templates/events"))
                .respond(response().withStatusCode(200).withBody(downloadEventsTemplateMockedResponse));

        testRulesPage.clickDownloadEventsTemplate();
        new WebDriverWait(driver, 10).until((webdriver) -> Files.exists(Paths.get(DOWNLOADED_EVENTS_TEMPLATE_FILE_PATH)));
        downloadedEventsTemplate = getJSONStringFromFile(DOWNLOADED_EVENTS_TEMPLATE_FILE_PATH);
        assertEquals(downloadEventsTemplateMockedResponse, downloadedEventsTemplate);
    }

    private void verifyAddAndRemoveRuleButton() {
        testRulesPage.clickAddRuleButton();
        testRulesPage.clickRemoveRuleNumber(3);
        assert (new WebDriverWait(driver, 10).until((webdriver) -> !testRulesPage.presenceOfRuleNumber(3)));
    }

    private void verifyDownloadRulesButton() throws IOException {
        mockClient.when(request().withMethod("GET").withPath("/templates/rules"))
                .respond(response().withStatusCode(200).withBody(downloadedRulesTemplate));
        testRulesPage.clickDownloadRulesButton();
        new WebDriverWait(driver, 10).until((webdriver) -> Files.exists(Paths.get(DOWNLOADED_RULES_FILE_PATH)));
        String downloadedRules = getJSONStringFromFile(DOWNLOADED_RULES_FILE_PATH);
        assertEquals(downloadedRulesTemplate, downloadedRules);
    }

    private void verifyUploadRulesFile() {
        testRulesPage.uploadRulesTemplate(DOWNLOADED_RULES_TEMPLATE_FILE_PATH);
        String firstRule = testRulesPage.getFirstRuleText();
        assertEquals(true, downloadedRulesTemplate.contains(firstRule));
    }

    private void verifyDownloadRulesTemplateButton() throws IOException {
        String mockedResponse = getJSONStringFromFile(RULES_TEMPLATE_FILE_PATH);
        mockClient.when(request().withMethod("GET").withPath("/templates/rules"))
                .respond(response().withStatusCode(200).withBody(mockedResponse));
        testRulesPage.clickDownloadRulesTemplate();
        new WebDriverWait(driver, 10).until((webdriver) -> Files.exists(Paths.get(DOWNLOADED_RULES_TEMPLATE_FILE_PATH)));
        downloadedRulesTemplate = getJSONStringFromFile(DOWNLOADED_RULES_TEMPLATE_FILE_PATH);
        assertEquals(mockedResponse, downloadedRulesTemplate);
    }

    private void enableTestRulesButtons() {
        driver.executeScript("$('button.btn').prop(\"disabled\", false);");
    }
}