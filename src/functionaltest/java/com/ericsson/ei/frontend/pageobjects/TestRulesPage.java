package com.ericsson.ei.frontend.pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.ericsson.ei.frontend.EIRequestsController;

public class TestRulesPage extends PageBaseClass{

    public TestRulesPage(EIRequestsController mockEIRequestsController,
            WebDriver driver, String baseUrl) {
        super(mockEIRequestsController, driver, baseUrl);
    }

    public String getMainHeader() {
        WebElement mainHeader = driver.findElement(By.xpath("/html/body/div[3]/div[1]/div[2]/div[1]/h1"));
        return mainHeader.getText();
    }

}
