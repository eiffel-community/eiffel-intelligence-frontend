package com.ericsson.ei.frontend;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.Test;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.ericsson.ei.config.SeleniumConfig;
import com.ericsson.ei.frontend.pageobjects.IndexPage;
import com.ericsson.ei.frontend.pageobjects.TestRulesPage;

public class TestRulesFunctionality extends SeleniumBaseClass {

    @MockBean
    protected CloseableHttpClient mockedHttpClient;

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

    private static final Logger LOG = LoggerFactory.getLogger(TestRulesFunctionality.class);

    @Test
    public void testJourneyToFindAggregatedObjectButton() throws Exception {
        // Load index page and wait for it to load
        IndexPage indexPageObject = new IndexPage(mockedHttpClient, driver, baseUrl);
        indexPageObject.loadPage();

        // Verify that we can navigate to test rules page
        TestRulesPage testRulesPage = indexPageObject.clickTestRulesPage();
        assert(new WebDriverWait(driver, 10).until((webdriver) -> testRulesPage.presenceOfTestRulesHeader()));

        // Verify that "download rules template" button works
        String downloadedRulesTemplate = "";
        String mockedResponse = getJSONStringFromFile(RULES_TEMPLATE_FILE_PATH);

        // On windows this may require more then one try due to timing issues.
        int i = 0;
        while (!downloadedRulesTemplate.equals(mockedResponse) && i <= 5) {
            i++;
            testRulesPage.clickDownloadRulesTemplate(mockedResponse);
            new WebDriverWait(driver, 10).until((webdriver) -> Files.exists(Paths.get(DOWNLOADED_RULES_TEMPLATE_FILE_PATH)));
            downloadedRulesTemplate = getJSONStringFromFile(DOWNLOADED_RULES_TEMPLATE_FILE_PATH);
        }

        assertEquals(mockedResponse, downloadedRulesTemplate);

        // Verify that uploading the downloaded template file works.
        testRulesPage.uploadRulesTemplate(DOWNLOADED_RULES_TEMPLATE_FILE_PATH);
        String firstRule = testRulesPage.getFirstRuleText();
        assertEquals(true, downloadedRulesTemplate.contains(firstRule));

        // Verify that it is possible to download rules
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
        testRulesPage.clickDownloadEventsTemplate(downloadEventsTemplateMockedResponse);
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
        testRulesPage.clickFindAggregatedObject(findAggregatedObjectResponse);
        assertEquals(findAggregatedObjectResponse, testRulesPage.getAggregatedResultData());
    }
}