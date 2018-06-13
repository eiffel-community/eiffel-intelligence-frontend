package com.ericsson.ei.frontend.pageobjects;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class TestRulesPage extends PageBaseClass {

    public TestRulesPage(CloseableHttpClient mockedHttpClient, CloseableHttpResponse mockedHttpResponse,
            WebDriver driver, String baseUrl) {
        super(mockedHttpClient, mockedHttpResponse, driver, baseUrl);
    }

    public String getMainHeader() {
        WebElement mainHeader = driver.findElement(By.xpath("/html/body/div[3]/div[1]/div[2]/div[1]/h1"));
        return mainHeader.getText();
    }

}
