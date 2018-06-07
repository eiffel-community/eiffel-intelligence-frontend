package com.ericsson.ei.frontend;

import org.junit.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Sleeper;
import org.openqa.selenium.support.ui.WebDriverWait;

import static org.junit.Assert.*;
import com.ericsson.ei.frontend.pageobjects.IndexPage;
import com.ericsson.ei.frontend.pageobjects.TestRulesPage;

public class TemplateTestCase extends SeleniumBaseClass{

    @Test
    public void testTemplateTestCase() throws Exception {
        IndexPage indexPageObject = new IndexPage(mockEIRequestsController, driver, baseUrl);
        indexPageObject.loadPage();
        assertEquals("Eiffel Intelligence", indexPageObject.getTitle());

        TestRulesPage testRulesPage = indexPageObject.clickTestRulesPage();
        assertEquals("Test Rules", testRulesPage.getMainHeader());

    }

}
