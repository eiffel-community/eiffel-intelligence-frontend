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

    private static final String DOWNLOADED_TEMPLATE_FILE_PATH = String.join(File.separator,
            SeleniumConfig.getTempDownloadDirectory().getPath(), "subscriptionsTemplate.json");
    private static final String SUBSCRIPTION_TEMPLATE_FILE_PATH = String.join(File.separator, "src", "functionaltest",
            "resources", "responses", "SubscriptionTemplate.json");
    private static final String RELOAD_TEST_FILE_PATH = String.join(File.separator, "src", "functionaltest",
            "resources", "responses", "SubscriptionForUploadCase.json");
    private static final String SAVE_TEST_FILE_PATH = String.join(File.separator, "src", "functionaltest", "resources",
            "responses", "SubscriptionForSaveCase.json");
    private static final String UPLOAD_FILE_PATH = String.join(File.separator, "src", "functionaltest", "resources",
            "responses", "SubscriptionForUploadCase.json");

    @Test
    public void testSubscription() throws Exception {

        // Open index page
        IndexPage indexPageObject = new IndexPage(mockedHttpClient, driver, baseUrl);
        indexPageObject.loadPage();

        // Click on Subscription Handling page button and verify that it is open
        String subscriptionHeaderID = "subData";
        SubscriptionPage subscriptionPage = indexPageObject.clickSubscriptionPage();
        assert(new WebDriverWait(driver, 10).until((webdriver) -> subscriptionPage.presenceOfHeader(subscriptionHeaderID)));

        // // Press "Reload" button and verify that two subscriptions with names
        // "Subscription1" and "Subscription2" are present
        String response = this.getJSONStringFromFile(RELOAD_TEST_FILE_PATH);
        subscriptionPage.clickReload(response);        
        assert(new WebDriverWait(driver, 10).until((webdriver) -> ((driver.getPageSource().contains("Subscription1")))));
        assert(new WebDriverWait(driver, 10).until((webdriver) -> ((driver.getPageSource().contains("Subscription2")))));
        
     // Test view button 
        subscriptionPage.clickViewBtn();        
        assert (new WebDriverWait(driver, 10).until((webdriver) -> driver.getPageSource().contains("View Subscription")));
        subscriptionPage.clickFormCloseBtn();
        
        // Delete all subscriptions with "Bulk Delete" button and verify that all
        // subscriptions are deleted
        String mockedDeleteResponse = "";
        subscriptionPage.clickBulkDelete(mockedDeleteResponse);
        assert(new WebDriverWait(driver, 10).until((webdriver) -> ((driver.getPageSource().contains("Subscription1"))==false)));
        assert(new WebDriverWait(driver, 10).until((webdriver) -> ((driver.getPageSource().contains("Subscription2"))==false)));

        // Verify that "get template" button works
        String mockedTemplateResponse = this.getJSONStringFromFile(SUBSCRIPTION_TEMPLATE_FILE_PATH);
        new WebDriverWait(driver, 10).until((webdriver) -> subscriptionPage.presenceOfClickGetTemplateButton());
        subscriptionPage.clickDownloadGetTemplate(mockedTemplateResponse);
        new WebDriverWait(driver, 10).until((webdriver) -> Files.exists(Paths.get(DOWNLOADED_TEMPLATE_FILE_PATH)));
        String getSubscriptionsTemplate = this.getJSONStringFromFile(DOWNLOADED_TEMPLATE_FILE_PATH);
        assertEquals(mockedTemplateResponse, getSubscriptionsTemplate);

        // Upload a subscription, name as "Subscription_uploaded" with "Upload
        // SUbscriptions" button and verify
        String mockedUploadResponse = this.getJSONStringFromFile(UPLOAD_FILE_PATH);
        subscriptionPage.clickUploadSubscriptionFunctionality(DOWNLOADED_TEMPLATE_FILE_PATH, mockedUploadResponse);
        assert(new WebDriverWait(driver, 10).until((webdriver) -> (driver.getPageSource().contains("Subscription_uploaded"))));

        // Click "Add Subscription" button and verify that "Subscription Form" is open
        subscriptionPage.clickAddSubscription();
        String formHeaderID = "formHeader";
        assert((new WebDriverWait(driver, 10)
                .until((webdriver) ->subscriptionPage.presenceOfHeader(formHeaderID))));

        // On subscription form, select the template as "Mail Trigger" and verify
        String selectID = "selectTemplate";
        String tempMail = "Mail Trigger";
        subscriptionPage.selectDropdown(selectID, tempMail);
        assert(new WebDriverWait(driver, 10).until((webdriver) -> (subscriptionPage.getValueFromSelect().equals("MAIL"))));
        assert(new WebDriverWait(driver, 10)
                .until((webdriver) -> (subscriptionPage.getValueFromElement().equals("mymail@company.com"))));

        // On subscription form, select the template as "REST POST (Raw Body :JSON)"
        // and verify
        String tempPost = "REST POST (Raw Body : JSON)";
        subscriptionPage.selectDropdown(selectID, tempPost);
        assert(new WebDriverWait(driver, 10).until((webdriver) -> (subscriptionPage.getValueFromSelect().equals("REST_POST"))));
        assert(new WebDriverWait(driver, 10)
                .until((webdriver) -> (subscriptionPage.getValueFromElement().equals("http://<MyHost:port>/api/doit"))));

        // On subscription form, select the template as "Jenkins Pipeline Parameterized
        // Job Trigger" and verify
        String tempJenkins = "Jenkins Pipeline Parameterized Job Trigger";
        subscriptionPage.selectDropdown(selectID, tempJenkins);
        assertEquals("REST_POST", subscriptionPage.getValueFromSelect());
        assertEquals("http://<JenkinsHost:port>/job/<JobName>/job/<branch>/build",
                subscriptionPage.getValueFromElement());

        // Choose Authorization as "Basic_AUTH" ===> input User Name as "ABCD" and Token
        // as "EFGH" ===> click "Generate Key/Value Pair", verify the basic
        // authentication is generated
        String selectAuthID = "selectAuth";
        String authValue = "BASIC_AUTH";
        String userName = "ABCD";
        String userNameID = "userNameInput";
        String token = "EFGH";
        String tokenID = "tokenInput";
        String subName = "Selenium_test_subscription";
        String subNameID = "subscriptionNameInput";

        subscriptionPage.selectDropdown(selectAuthID, authValue);
        subscriptionPage.addFieldValue(userNameID, userName);
        subscriptionPage.addFieldValue(tokenID, token);
        String kvID = "kvID";
        subscriptionPage.clickKVbtn(kvID);
        assert (new WebDriverWait(driver, 10)
                .until((webdriver) ->driver.getPageSource().contains("Authorization")));

        // Test save subscription form: add subscription name as
        // "selenium_test_subscription" and then click "save" button verification
        // that subscription is added in the datatable (and is displayed on the main
        // page)
        String responseSave = this.getJSONStringFromFile(SAVE_TEST_FILE_PATH);
        subscriptionPage.addFieldValue(subNameID, subName);
        subscriptionPage.clickFormsSaveBtn(responseSave);
        assert (new WebDriverWait(driver, 10)
                .until((webdriver) ->driver.getPageSource().contains("Selenium_test_subscription")));
    }
}
