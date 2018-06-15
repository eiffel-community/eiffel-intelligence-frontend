package com.ericsson.ei.frontend.pageobjects;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.impl.client.CloseableHttpClient;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class TestRulesPage extends PageBaseClass {

    public TestRulesPage(CloseableHttpClient mockedHttpClient,
            WebDriver driver, String baseUrl) throws ClientProtocolException, IOException {
        super(mockedHttpClient, driver, baseUrl);
    }

    public String getTestRulesHeader() {
        WebElement testRulesHeader = driver.findElement(By.id("test_rules_header"));
        return testRulesHeader.getText();
    }

}
