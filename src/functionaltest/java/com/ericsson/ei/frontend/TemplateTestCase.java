package com.ericsson.ei.frontend;

import org.junit.*;
import org.openqa.selenium.support.ui.WebDriverWait;

import static org.junit.Assert.*;

import com.ericsson.ei.frontend.pageobjects.IndexPage;
import com.ericsson.ei.frontend.pageobjects.TestRulesPage;

public class TemplateTestCase extends SeleniumBaseClass {

    @Test
    public void testTemplateTestCase() throws Exception {

        // Open indexpage and verify that it is opened
        IndexPage indexPageObject = new IndexPage(mockedHttpClient, driver, baseUrl);
        indexPageObject.loadPage();
        assertEquals("Eiffel Intelligence", indexPageObject.getTitle());

        // The lines below contains selenium interaction with mocking
        String response = this.getJSONStringFromFile("src/functionaltest/resources/responses/SubscriptionObjects.json");
        new WebDriverWait(driver, 10).until((webdriver) -> indexPageObject.presenceOfReloadButton());
        indexPageObject.clickReloadButton(response);

        // Click on test rules page button and verify that it is opened
        TestRulesPage testRulesPage = indexPageObject.clickTestRulesPage();
        new WebDriverWait(driver, 10).until((webdriver) -> testRulesPage.presenceOfTestRulesHeader());
    }

}