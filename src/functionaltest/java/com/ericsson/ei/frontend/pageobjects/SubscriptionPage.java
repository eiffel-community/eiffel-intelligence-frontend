package com.ericsson.ei.frontend.pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.ericsson.ei.frontend.EIRequestsController;

public class SubscriptionPage extends PageBaseClass {
    
    public SubscriptionPage(EIRequestsController mockEIRequestsController,
            WebDriver driver, String baseUrl) {
        super(mockEIRequestsController, driver, baseUrl);
    }
    

    public String getMainHeader() {
        WebElement mainHeader = driver.findElement(By.xpath("//div[@class='container pull-left']//h1"));
        return mainHeader.getText();
    }
    
    public void clickAddSubscription(){
//      waitForJQueryToLoad();
      WebElement addSubscriptionBtn = driver.findElement(By.xpath("//button[contains(@title,'Add a new subscription to EI')]"));
      addSubscriptionBtn.click();
  }

}
