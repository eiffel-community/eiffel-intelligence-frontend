package com.ericsson.ei.frontend;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.ericsson.ei.frontend.pageobjects.TestRulesPage;

public class TestAlarm extends SeleniumBaseClass {

    @MockBean
    protected CloseableHttpClient mockedHttpClient;

    JavascriptExecutor js;
    TestRulesPage testRulesPage;

    @Before
    public void beforeTest() throws IOException {
        initBaseMocks(mockedHttpClient);
        js = driver;
        testRulesPage = new TestRulesPage(null, driver, baseUrl);
    }

    @Test
    public void testAlarm() throws IOException {
        testRulesPage.loadPage();
        enableTestRulesButtons();
        clickTestRulesButtons();
        clickAlarmButton();
        assertTrue(driver.findElements(By.cssSelector(".alert-list .dropdown-item")).size() >= 2);
    }

    private void enableTestRulesButtons() {
        js.executeScript("$('button.btn').prop(\"disabled\", false);");
    }

    private void clickTestRulesButtons() {
        testRulesPage.clickRemoveRuleNumber(0);
        testRulesPage.clickRemoveEventNumber(0);
    }

    private void clickAlarmButton() {
        for (int i = 0; i < 5; i++) {
            testRulesPage.clickAlarmButton();
        }
    }
}