package com.ericsson.ei.frontend;

import org.junit.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Sleeper;
import org.openqa.selenium.support.ui.WebDriverWait;

import static org.junit.Assert.*;

import java.util.concurrent.TimeUnit;

import com.ericsson.ei.frontend.pageobjects.IndexPage;
import com.ericsson.ei.frontend.pageobjects.SubscriptionPage;

public class SubscriptionHandlingFunctionality extends SeleniumBaseClass {
    
    @Test
    public void testTemplateTestCase() throws Exception {

        //Open indexpage and verify that it is opened
        IndexPage indexPageObject = new IndexPage(mockedHttpClient, driver, baseUrl);
        indexPageObject.loadPage();
        assertEquals("Eiffel Intelligence", indexPageObject.getTitle());
        

        //Click on test subscription handling page button and verify that it is opened
        SubscriptionPage subscriptionPage = indexPageObject.clickSubscriptionPage();
        assert(subscriptionPage.getMainHeader().contains("Eiffel Intelligence Subscription Handling"));
        
        subscriptionPage.clickReload();
        TimeUnit.SECONDS.sleep(2);
//        subscriptionPage.clickBulkDelete();
//        TimeUnit.SECONDS.sleep(2);
        subscriptionPage.clickGetTemplate();
        TimeUnit.SECONDS.sleep(2);       
        subscriptionPage.clickUploadSubscription();
        TimeUnit.SECONDS.sleep(2);
        
        subscriptionPage.clickAddSubscription();  
        TimeUnit.SECONDS.sleep(2); 
        
        subscriptionPage.clickFormsCancelBtn();  
        TimeUnit.SECONDS.sleep(2);   
        
        subscriptionPage.clickAddSubscription();  
        TimeUnit.SECONDS.sleep(2);   
        
        subscriptionPage.clickFormsSaveBtn();
        TimeUnit.SECONDS.sleep(2);
        
        subscriptionPage.selectTemplate("Mail Trigger");
        TimeUnit.SECONDS.sleep(2);        
        assertEquals("MAIL",subscriptionPage.getValueFromSelect());
        assertEquals("mymail@company.com",subscriptionPage.getValueFromElement());
        
        
        
        subscriptionPage.selectTemplate("REST POST (Raw Body : JSON)");
        TimeUnit.SECONDS.sleep(2);
      
        subscriptionPage.selectTemplate("Jenkins Pipeline Parameterized Job Trigger");
        TimeUnit.SECONDS.sleep(2);
        
        subscriptionPage.addSubscriptionName("selenium_test_subscription");
        TimeUnit.SECONDS.sleep(2);
        
        System.out.println("Just for debugging");
    }

}
