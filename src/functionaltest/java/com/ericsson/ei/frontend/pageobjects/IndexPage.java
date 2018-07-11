package com.ericsson.ei.frontend.pageobjects;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;

import org.mockito.Mockito;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.io.IOException;

public class IndexPage extends PageBaseClass {

    public IndexPage(CloseableHttpClient mockedHttpClient, FirefoxDriver driver,
                     String baseUrl) throws IOException {
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

    public TestRulesPage clickTestRulesPage() throws IOException {
        WebElement testRulesBtn = driver.findElement(By.id("testRulesBtn"));
        testRulesBtn.click();

        TestRulesPage testRulesPage = new TestRulesPage(mockedHttpClient, driver, baseUrl);
        waitForJQueryToLoad();

        return testRulesPage;
    }
    
    public SubscriptionPage clickSubscriptionPage() throws IOException {
      WebElement subscriptionBtn = driver.findElement(By.id("subscriptionBtn"));
      subscriptionBtn.click();
      SubscriptionPage subscriptionPage = new SubscriptionPage(mockedHttpClient, driver, baseUrl);
      return subscriptionPage;
  }

    public void clickReloadButton(String responseData) throws IOException {
        CloseableHttpResponse response = this.createMockedHTTPResponse(responseData, 200);
        //Checks that argument in the request contains "subscriptions" endpoint
        Mockito.doReturn(response).when(mockedHttpClient).execute(Mockito.argThat(request ->
                (request).getURI().toString().contains("subscriptions")));

        WebElement reloadButton = driver.findElement(By.className("table_reload"));
        reloadButton.click();
    }


    public Object presenceOfReloadButton() {
        try {
            driver.findElement(By.className("table_reload"));
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }

}
