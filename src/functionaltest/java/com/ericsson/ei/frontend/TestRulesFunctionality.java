package com.ericsson.ei.frontend;

import static org.junit.Assert.assertEquals;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.ericsson.ei.config.SeleniumConfig;
import com.ericsson.ei.frontend.pageobjects.IndexPage;
import com.ericsson.ei.frontend.pageobjects.TestRulesPage;

public class TestRulesFunctionality extends SeleniumBaseClass {

    @Test
    public void testJourneyToFindAggregatedObjectButton() throws Exception {
        // Load index page and wait for it to load
        IndexPage indexPageObject = new IndexPage(mockedHttpClient, driver, baseUrl);
        indexPageObject.loadPage();
        TimeUnit.SECONDS.sleep(3);

        // Verify that we can navigate to test rules page
        TestRulesPage testRulesPage = indexPageObject.clickTestRulesPage();
        new WebDriverWait(driver, 10).until((webdriver) -> testRulesPage.presenceOfTestRulesHeader());

        // Verify that "download rules template" button works
        String mockedResponse = this.getJSONStringFromFile(
                "src/functionaltest/resources/responses/RulesTemplateObject.json");
        TimeUnit.SECONDS.sleep(1);
        testRulesPage.clickDownloadRulesTemplate(mockedResponse);
        String rulesTemplateFilePath = SeleniumConfig.getTempDownloadDirectory().getPath() + "/rulesTemplate.json";
        new WebDriverWait(driver, 10).until((webdriver) -> Files.exists(Paths.get(rulesTemplateFilePath)));
        String downloadedRulesTemplate = this.getJSONStringFromFile(rulesTemplateFilePath);
        assertEquals(mockedResponse, downloadedRulesTemplate);

        // Verify that uploading the downloaded template file works.
        testRulesPage.uploadRulesTemplate(rulesTemplateFilePath);
        new WebDriverWait(driver, 10).until((webdriver) -> testRulesPage.presenceOfRuleNumber(2));
        String firstRule = testRulesPage.getFirstRuleText();
        assertEquals(true, downloadedRulesTemplate.contains(firstRule));

        // Verify that it is possible to download rules
        testRulesPage.clickDownloadRulesButton();
        String rulesFilePath = SeleniumConfig.getTempDownloadDirectory().getPath() + "/rules.json";
        new WebDriverWait(driver, 10).until((webdriver) -> Files.exists(Paths.get(rulesFilePath)));
        String downloadedRules = this.getJSONStringFromFile(rulesFilePath);
        assertEquals(downloadedRulesTemplate, downloadedRules);

        // Verify that add rule button works
        testRulesPage.clickAddRuleButton();
        new WebDriverWait(driver, 10).until((webdriver) -> testRulesPage.presenceOfRuleNumber(3));

        // Verify that removing a rule works
        testRulesPage.clickRemoveRuleNumber(3);
        new WebDriverWait(driver, 10).until((webdriver) -> testRulesPage.presenceOfRuleNumber(3) == false);

        // Verify that "download events template" button works
        String downloadEventsTemplateMockedResponse = this.getJSONStringFromFile(
                "src/functionaltest/resources/responses/EventsTemplateObject.json");
        testRulesPage.clickDownloadEventsTemplate(downloadEventsTemplateMockedResponse);
        String eventsTemplateFilePath = SeleniumConfig.getTempDownloadDirectory().getPath() + "/eventsTemplate.json";
        new WebDriverWait(driver, 10).until((webdriver) -> Files.exists(Paths.get(eventsTemplateFilePath)));
        String downloadedEventsTemplate = this.getJSONStringFromFile(eventsTemplateFilePath);
        assertEquals(downloadEventsTemplateMockedResponse, downloadedEventsTemplate);

        // Verify that uploading the downloaded template file works.
        testRulesPage.uploadEventsTemplate(eventsTemplateFilePath);
        new WebDriverWait(driver, 10).until((webdriver) -> testRulesPage.presenceOfEventNumber(2));
        String firstEvent = testRulesPage.getFirstEventText();
        assertEquals(true, downloadedEventsTemplate.contains(firstEvent));

        // Verify that add rule button works
        testRulesPage.clickAddEventButton();
        new WebDriverWait(driver, 10).until((webdriver) -> testRulesPage.presenceOfEventNumber(3));

        // Verify that removing a rule works
        testRulesPage.clickRemoveEventNumber(3);
        new WebDriverWait(driver, 10).until((webdriver) -> testRulesPage.presenceOfEventNumber(3) == false);

        // Verify that find aggregated object button works
        String findAggregatedObjectResponse = this.getJSONStringFromFile(
                "src/functionaltest/resources/responses/AggregatedObjectResponse.json");
        testRulesPage.clickFindAggregatedObject(findAggregatedObjectResponse);
        assertEquals(findAggregatedObjectResponse, testRulesPage.getAggregatedResultData());
    }
}
