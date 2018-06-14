package com.ericsson.ei.frontend.pageobjects;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.mockito.Mockito;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;

import org.hamcrest.beans.HasProperty;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;

import java.io.IOException;

public class IndexPage extends PageBaseClass {
    public IndexPage(CloseableHttpClient mockedHttpClient, WebDriver driver,
            String baseUrl) throws ClientProtocolException, IOException {
        super(mockedHttpClient, driver, baseUrl);
    }

    public IndexPage loadPage() {
        driver.get(baseUrl);
        waitForJQueryToLoad();
        return this;
    }

    public String getTitle() {
        return driver.getTitle();
    }

    public TestRulesPage clickTestRulesPage() throws ClientProtocolException, IOException {
    	WebElement testRulesBtn = driver.findElement(By.id("testRulesBtn"));
        testRulesBtn.click();

        TestRulesPage testRulesPage = new TestRulesPage(mockedHttpClient, driver, baseUrl);
        return testRulesPage;
    }


    public void clickReloadButton(String responseData) throws ClientProtocolException, IOException {
        CloseableHttpResponse response = this.createMockedHTTPResponse(responseData, 200);
        //If request contains "subscriptions" in the URI it will return a specific response
        when(this.mockedHttpClient.execute(Mockito.argThat(request -> ((HttpRequestBase)request).getURI().toString().contains("subscriptions")))).thenReturn(response);

        WebElement reloadButton = driver.findElement(By.className("table_reload"));
        reloadButton.click();
    }
}
