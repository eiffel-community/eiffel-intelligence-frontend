package com.ericsson.ei.frontend.pageobjects;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

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
}
