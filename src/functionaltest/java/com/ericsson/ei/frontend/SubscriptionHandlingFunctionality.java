package com.ericsson.ei.frontend;

import org.junit.*;
import org.openqa.selenium.support.ui.WebDriverWait;

import static org.junit.Assert.*;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

import com.ericsson.ei.config.SeleniumConfig;
import com.ericsson.ei.frontend.pageobjects.IndexPage;
import com.ericsson.ei.frontend.pageobjects.SubscriptionPage;

public class SubscriptionHandlingFunctionality extends SeleniumBaseClass {

    private static final String DOWNLOADEDTEMPLATEFILEPATH = String.join(File.separator,
            SeleniumConfig.getTempDownloadDirectory().getPath(), "subscriptionsTemplate.json");
    private static final String SUBSCRIPTIONTEMPLATEFILEPATH = String.join(File.separator, "src", "functionaltest",
            "resources", "responses", "SubscriptionTemplate.json");
    private static final String RELOADTESTFILEPATH = String.join(File.separator, "src", "functionaltest", "resources",
            "responses", "SubscriptionForUploadCase.json");
    private static final String SAVETESTFILEPATH = String.join(File.separator, "src", "functionaltest", "resources",
            "responses", "SubscriptionForSaveCase.json");
    private static final String UPLOADFILEPATH = String.join(File.separator, "src", "functionaltest", "resources",
            "responses", "SubscriptionForUploadCase.json");

    @Test
    public void testSubscription() throws Exception {

        // Open index page
        IndexPage indexPageObject = new IndexPage(mockedHttpClient, driver, baseUrl);
        indexPageObject.loadPage();

        // Click on Subscription Handling page button and verify that it is open
        String headerPath = "//div[@class='container pull-left']//h1";
        SubscriptionPage subscriptionPage = indexPageObject.clickSubscriptionPage();
        new WebDriverWait(driver, 10).until((webdriver) -> subscriptionPage.presenceOfHeader(headerPath));
        
        // // Press "Reload" button and verify that two subscriptions with names "Subscription1" and "Subscription2" are present
         String response = this.getJSONStringFromFile(RELOADTESTFILEPATH);
         subscriptionPage.clickReload(response);
         assert (driver.getPageSource().contains("Subscription1"));
         assert (driver.getPageSource().contains("Subscription2"));
        
        // Click "Add Subscription" button and verify that "Subscription Form" is open
         subscriptionPage.clickAddSubscription();
         String xPath = "//*[@id='formHeader']";
         new WebDriverWait(driver, 10).until((webdriver) -> (subscriptionPage.presenceOfHeader(xPath)));
    
         // On subscription form, select the template as "Mail Trigger" and verify
         String tempPath = "//select[contains(@title,'Choose a Subscription Template')]";
         String tempMail = "Mail Trigger";
         subscriptionPage.selectDropdown(tempPath, tempMail);
         TimeUnit.SECONDS.sleep(2);
         new WebDriverWait(driver, 10).until((webdriver) ->
         (subscriptionPage.getValueFromSelect().equals("MAIL")));
         new WebDriverWait(driver, 10)
         .until((webdriver) ->
         (subscriptionPage.getValueFromElement().equals("mymail@company.com")));
         
         // On subscription form, select the template as "REST POST (Raw Body :JSON)"
         // and verify
         String tempPost = "REST POST (Raw Body : JSON)";
         subscriptionPage.selectDropdown(tempPath, tempPost);
         new WebDriverWait(driver, 10).until((webdriver) ->
         (subscriptionPage.getValueFromSelect().equals("REST_POST")));
         new WebDriverWait(driver, 10)
         .until((webdriver) ->
         (subscriptionPage.getValueFromElement().equals("http://<MyHost:port>/api/doit")));
        
         // On subscription form, select the template as "Jenkins Pipeline
         // Parameterized Job Trigger" and verify
         String tempJenkins = "Jenkins Pipeline Parameterized Job Trigger";
         subscriptionPage.selectDropdown(tempPath, tempJenkins);
         TimeUnit.SECONDS.sleep(2);
         assertEquals("REST_POST", subscriptionPage.getValueFromSelect());
         assertEquals("http://<JenkinsHost:port>/job/<JobName>/job/<branch>/build",
         subscriptionPage.getValueFromElement());        
         
         // Choose Authorization as "Basic_AUTH" ===> input User Name as "ABCD" and Token
         // as "EFGH" ===> click "Generate Key/Value Pair", verify the basic authentication is generated
         String authPath = "//select[contains(@title,'Choose an authentication type')]";
         String authValue = "BASIC_AUTH";        
         String userName = "ABCD";
         String userNamePath = "//input[contains(@title,'Enter user name')]";
         String token = "EFGH";
         String tokenPath = "//input[contains(@title,'Enter token')]";
         String subName = "Selenium_test_subscription";
         String subNamePath = "//input[contains(@title,'Specify a SubsciptionName')]";
         
         subscriptionPage.selectDropdown(authPath, authValue);
         TimeUnit.SECONDS.sleep(2);
        
         subscriptionPage.addFieldValue(userNamePath, userName);
         TimeUnit.SECONDS.sleep(2);
        
         subscriptionPage.addFieldValue(tokenPath, token);
         TimeUnit.SECONDS.sleep(2);
        
         String kvPath = "//button[contains(@title,'Generate Key/Value Pair')]";
         subscriptionPage.clickKVbtn(kvPath);
         TimeUnit.SECONDS.sleep(2);
         assert (driver.getPageSource().contains("Authorization"));
        
         // Test save subscription form: add subscription name as "selenium_test_subscription" and then click "save" button verification
         // that subscription is added in the datatable (and is displayed on the main page)
         String responseSave = this.getJSONStringFromFile(SAVETESTFILEPATH);
         subscriptionPage.addFieldValue(subNamePath, subName);
         TimeUnit.SECONDS.sleep(2);
         subscriptionPage.clickFormsSaveBtn(responseSave);
         TimeUnit.SECONDS.sleep(6);
         assert (driver.getPageSource().contains("Selenium_test_subscription"));
         TimeUnit.SECONDS.sleep(2);
         
         // Delete all subscriptions with "Bulk Delete" button and verify that all subscriptions are deleted
         String mockedDeleteResponse = "";
         subscriptionPage.clickBulkDelete(mockedDeleteResponse);
         assertFalse(driver.getPageSource().contains("Subscription1"));
         assertFalse(driver.getPageSource().contains("Subscription2"));

        // Verify that "get template" button works
        String mockedTemplateResponse = this.getJSONStringFromFile(SUBSCRIPTIONTEMPLATEFILEPATH);
        new WebDriverWait(driver, 10).until((webdriver) -> subscriptionPage.presenceOfClickGetTemplateButton());
        subscriptionPage.clickDownloadGetTemplate(mockedTemplateResponse);
        new WebDriverWait(driver, 10).until((webdriver) -> Files.exists(Paths.get(DOWNLOADEDTEMPLATEFILEPATH)));
        String getSubscriptionsTemplate = this.getJSONStringFromFile(DOWNLOADEDTEMPLATEFILEPATH);
        assertEquals(mockedTemplateResponse, getSubscriptionsTemplate);
        
        // Upload a subscription, name as "Subscription_uploaded" with "Upload SUbscriptions" button and verify
        String uploadFilePath = new File("src/test/resources/Subscription_upload.txt").getAbsolutePath();
        String mockedUploadResponse = this.getJSONStringFromFile(UPLOADFILEPATH);
        subscriptionPage.clickUploadSubscriptionFunctionality(DOWNLOADEDTEMPLATEFILEPATH, mockedUploadResponse);
        new WebDriverWait(driver, 10).until((webdriver) -> (driver.getPageSource().contains("Subscription_uploaded")));        
    }
}
