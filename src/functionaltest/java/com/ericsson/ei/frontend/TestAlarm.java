package com.ericsson.ei.frontend;

import com.ericsson.ei.frontend.pageobjects.IndexPage;
import com.ericsson.ei.frontend.pageobjects.TestRulesPage;
import org.junit.Test;
import org.openqa.selenium.By;

import java.io.IOException;
import java.util.stream.IntStream;

import static org.junit.Assert.assertEquals;

public class TestAlarm extends SeleniumBaseClass {
    private static final String alarmResult = "Deleted all events, but we need atleast one event.\nDeleted all rule types, but we need atleast one Rule type, Here add default rule type";

    @Test
    public void testAlarm() throws IOException {
        //Load index page
        IndexPage indexPageObject = new IndexPage(null, driver, baseUrl);
        indexPageObject.loadPage();

        //Generate exception
        TestRulesPage testRulesPage = indexPageObject.clickTestRulesPage();
        testRulesPage.clickRemoveRuleNumber(0);
        testRulesPage.clickRemoveEventNumber(0);

        //Click alarm button
        indexPageObject.clickAlarmButton();
        assertEquals(alarmResult, driver.findElement(By.id("alerts")).getText());

        //Click alarm button five times
        IntStream.range(0, 6).forEachOrdered(i -> indexPageObject.clickAlarmButton());
        assertEquals(alarmResult, driver.findElement(By.id("alerts")).getText());
    }
}