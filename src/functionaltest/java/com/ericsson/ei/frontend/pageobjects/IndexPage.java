package com.ericsson.ei.frontend.pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.ericsson.ei.frontend.EIRequestsController;

public class IndexPage extends PageBaseClass {
    public IndexPage(EIRequestsController mockEIRequestsController, WebDriver driver,
            String baseUrl) {
        super(mockEIRequestsController, driver, baseUrl);
    }

    public IndexPage loadPage() {
        driver.get(baseUrl);
        return this;
    }

    public String getTitle() {
        return driver.getTitle();
    }

    public TestRulesPage clickTestRulesPage() {
        waitForJQueryToLoad();
        WebElement testRulesBtn = driver.findElement(By.id("testRulesBtn"));
        testRulesBtn.click();

        TestRulesPage testRulesPage = new TestRulesPage(mockEIRequestsController, driver, baseUrl);
        return testRulesPage;
    }
    
    
    public SubscriptionPage clickSubscriptionPage() {
//        waitForJQueryToLoad();
        WebElement subscriptionBtn = driver.findElement(By.id("subscriptionBtn"));
        subscriptionBtn.click();

        SubscriptionPage subscriptionPage = new SubscriptionPage(mockEIRequestsController, driver, baseUrl);
        return subscriptionPage;
    }
        
    public SubscriptionPage clickAddSubscription(){
//      waitForJQueryToLoad();
      WebElement addSubscriptionBtn = driver.findElement(By.id("subscriptionBtn"));
      addSubscriptionBtn.click();

      SubscriptionPage addSubscriptionPage = new SubscriptionPage(mockEIRequestsController, driver, baseUrl);
      return addSubscriptionPage;
  }
}
