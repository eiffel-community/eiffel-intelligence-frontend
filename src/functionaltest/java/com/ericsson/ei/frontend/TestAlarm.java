package com.ericsson.ei.frontend;

import com.ericsson.ei.frontend.pageobjects.IndexPage;
import com.ericsson.ei.frontend.pageobjects.TestRulesPage;
import org.junit.Test;
import org.openqa.selenium.By;

import java.io.IOException;
import java.util.stream.IntStream;

import static org.junit.Assert.assertEquals;

public class TestAlarm extends SeleniumBaseClass {

    @Test
    public void testAlarm() throws IOException {
        //Load index page
        IndexPage indexPageObject = new IndexPage(null, driver, baseUrl);
        indexPageObject.loadPage();

        //Generate exception
        TestRulesPage testRulesPage = indexPageObject.clickTestRulesPage();
        testRulesPage.clickRemoveRuleNumber(0);
        testRulesPage.clickRemoveEventNumber(0);

        //Click alarm button few times
        IntStream.range(0, 5).forEachOrdered(i -> indexPageObject.clickAlarmButton());
        assertEquals(2, driver.findElements(By.className("dropdown-item")).size());
    }
}