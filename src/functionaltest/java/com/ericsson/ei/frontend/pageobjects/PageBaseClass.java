package com.ericsson.ei.frontend.pageobjects;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.ericsson.ei.frontend.EIRequestsController;

public class PageBaseClass {
    EIRequestsController mockEIRequestsController;

    protected WebDriver driver;
    protected String baseUrl;

    public PageBaseClass(EIRequestsController mockEIRequestsController, WebDriver driver,
            String baseUrl) {
        super();
        this.mockEIRequestsController = mockEIRequestsController;
        this.driver = driver;
        this.baseUrl = baseUrl;
        PageFactory.initElements(driver, this);
    }

    public void waitForJQueryToLoad() {
        WebDriverWait webDriverWait = new WebDriverWait(driver, 30);
        webDriverWait.until((ExpectedCondition<Boolean>) wd ->
                ((JavascriptExecutor) wd).executeScript("return document.readyState").equals("complete"));

        webDriverWait.until((ExpectedCondition<Boolean>) wd ->
        ((JavascriptExecutor) wd).executeScript("return jQuery.active==0").equals(true));
    }
}
