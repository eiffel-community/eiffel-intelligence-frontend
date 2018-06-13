package com.ericsson.ei.frontend;

import org.junit.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Sleeper;
import org.openqa.selenium.support.ui.WebDriverWait;

import static org.junit.Assert.*;
import com.ericsson.ei.frontend.pageobjects.IndexPage;
import com.ericsson.ei.frontend.pageobjects.SubscriptionPage;

public class SubscriptionHandlingFunctionality extends SeleniumBaseClass {
    
    @Test
    public void testTemplateTestCase() throws Exception {

        //Open indexpage and verify that it is opened
        IndexPage indexPageObject = new IndexPage(mockEIRequestsController, driver, baseUrl);
        indexPageObject.loadPage();
        assertEquals("Eiffel Intelligence", indexPageObject.getTitle());
        

        //Click on test subscription handling page button and verify that it is opened
        SubscriptionPage subscriptionPage = indexPageObject.clickSubscriptionPage();
        assert(subscriptionPage.getMainHeader().contains("Eiffel Intelligence Subscription Handling"));
        
        subscriptionPage.clickAddSubscription();  
        
        System.out.println("Just for debugging");
    }

}
