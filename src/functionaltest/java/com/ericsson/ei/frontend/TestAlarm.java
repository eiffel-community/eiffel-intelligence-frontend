package com.ericsson.ei.frontend;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.Test;
import org.openqa.selenium.By;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.ericsson.ei.frontend.pageobjects.IndexPage;
import com.ericsson.ei.frontend.pageobjects.TestRulesPage;

public class TestAlarm extends SeleniumBaseClass {

    @MockBean
    protected CloseableHttpClient mockedHttpClient;

    @Test
    public void testAlarm() throws IOException {
        initBaseMocks(mockedHttpClient);

        //Load index page
        IndexPage indexPageObject = new IndexPage(null, driver, baseUrl);
        indexPageObject.loadPage();

        //Generate exception
        TestRulesPage testRulesPage = indexPageObject.clickTestRulesPage();
        testRulesPage.clickRemoveRuleNumber(0);
        testRulesPage.clickRemoveEventNumber(0);

        //Click alarm button few times
        for (int i = 0; i < 5; i++) {
            indexPageObject.clickAlarmButton();
        }
        assertTrue(driver.findElements(By.cssSelector(".alert-list .dropdown-item")).size() >= 2);
    }
}