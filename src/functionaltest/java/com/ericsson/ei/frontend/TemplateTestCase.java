package com.ericsson.ei.frontend;

import org.junit.*;

import static org.junit.Assert.*;
import com.ericsson.ei.frontend.pageobjects.IndexPage;

public class TemplateTestCase extends SeleniumBaseClass{

    @Test
    public void testTemplateTestCase() throws Exception {

        //Open indexpage and verify that it is opened
        IndexPage indexPageObject = new IndexPage(mockedHttpClient, mockedHttpResponse, driver, baseUrl);
        indexPageObject.loadPage();
        assertEquals("Eiffel Intelligence", indexPageObject.getTitle());

        indexPageObject.clickReloadButton();
        //Click on test rules page button and verify that it is opened
        //TestRulesPage testRulesPage = indexPageObject.clickTestRulesPage();
        //assertEquals("Test Rules", testRulesPage.getMainHeader());
        //TestRulesPage testRulesPage = indexPageObject.clickTestRulesPage();
    }

}
