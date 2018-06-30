package com.ericsson.ei.frontend;

import org.junit.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Sleeper;
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
    
    private static final String DOWNLOADEDTEMPLATEFILEPATH = String.join(
            File.separator, SeleniumConfig.getTempDownloadDirectory().getPath(), "subscriptionsTemplate.json");
    private static final String SUBSCRIPTIONTEMPLATEFILEPATH = String.join(File.separator, "src", "functionaltest", "resources",
            "responses", "SubscriptionTemplate.json");
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
//        String headerPath = "//div[@class='container pull-left']//h1";
        SubscriptionPage subscriptionPage = indexPageObject.clickSubscriptionPage();
//        assert (subscriptionPage.getMainHeader(headerPath).contains("Eiffel Intelligence Subscription Handling"));
//
//        // Press "Reload" button and verify that two subscriptions with names
//        // "Subscription1" and "Subscription2" are present
//        String response = this.getJSONStringFromFile(RELOADTESTFILEPATH);
//        subscriptionPage.clickReload(response);
//        assert (driver.getPageSource().contains("Subscription1"));
//        assert (driver.getPageSource().contains("Subscription2"));
//
//        // Click "Add Subscription" button and verify that "Subscription Form" is open
//        subscriptionPage.clickAddSubscription();
//        String xPath = "//*[@id='formHeader']";
//        new WebDriverWait(driver, 10)
//                .until((webdriver) -> (subscriptionPage.getMainHeader(xPath).equals("Add Subscription")));
//
//        // Click "cancel" button in subscription form and verify that it is close
//        subscriptionPage.clickFormsCancelBtn();
//        TimeUnit.SECONDS.sleep(5);
//
//        // Now, again open "Subscription Form"
//        subscriptionPage.clickAddSubscription();
//        new WebDriverWait(driver, 10)
//                .until((webdriver) -> (subscriptionPage.getMainHeader(xPath).equals("Add Subscription")));
//
//        // On subscription form, select the template as "Mail Trigger" and verify
//        String tempPath = "//select[contains(@title,'Choose a Subscription Template')]";
//        String tempMail = "Mail Trigger";
//        subscriptionPage.selectDropdown(tempPath, tempMail);
//        TimeUnit.SECONDS.sleep(2);
//        new WebDriverWait(driver, 10).until((webdriver) -> (subscriptionPage.getValueFromSelect().equals("MAIL")));
//        new WebDriverWait(driver, 10)
//                .until((webdriver) -> (subscriptionPage.getValueFromElement().equals("mymail@company.com")));
//        //
//        // On subscription form, select the template as "REST POST (Raw Body :JSON)"
//        // and verify
//        String tempPost = "REST POST (Raw Body : JSON)";
//        subscriptionPage.selectDropdown(tempPath, tempPost);
//        new WebDriverWait(driver, 10).until((webdriver) -> (subscriptionPage.getValueFromSelect().equals("REST_POST")));
//        new WebDriverWait(driver, 10)
//                .until((webdriver) -> (subscriptionPage.getValueFromElement().equals("http://<MyHost:port>/api/doit")));
//
//        // On subscription form, select the template as "Jenkins Pipeline
//        // Parameterized Job Trigger" and verify
//        String tempJenkins = "Jenkins Pipeline Parameterized Job Trigger";
//        subscriptionPage.selectDropdown(tempPath, tempJenkins);
//        TimeUnit.SECONDS.sleep(2);
//        assertEquals("REST_POST", subscriptionPage.getValueFromSelect());
//        assertEquals("http://<JenkinsHost:port>/job/<JobName>/job/<branch>/build",
//                subscriptionPage.getValueFromElement());
//
//        // ===================================================================================================
//        // Choose Authorization as "Basic_AUTH" ===> input User Name as "ABCD" and Token
//        // as "EFGH" ===> click "Generate Key/Value Pair", verify the basic authentication is generated
//        String authPath = "//select[contains(@title,'Choose an authentication type')]";
//        String authValue = "BASIC_AUTH";
//        subscriptionPage.selectDropdown(authPath, authValue);
//        TimeUnit.SECONDS.sleep(2);
//
//        String userName = "ABCD";
//        String userNamePath = "//input[contains(@title,'Enter user name')]";
//        String token = "EFGH";
//        String tokenPath = "//input[contains(@title,'Enter token')]";
//        String subName = "Selenium_test_subscription";
//        String subNamePath = "//input[contains(@title,'Specify a SubsciptionName')]";
//
//        subscriptionPage.addFieldValue(userNamePath, userName);
//        TimeUnit.SECONDS.sleep(2);
//
//        subscriptionPage.addFieldValue(tokenPath, token);
//        TimeUnit.SECONDS.sleep(2);
//
//        String kvPath = "//button[contains(@title,'Generate Key/Value Pair')]";
//        subscriptionPage.clickKVbtn(kvPath);
//        TimeUnit.SECONDS.sleep(4);
//        assert (driver.getPageSource().contains("Authorization"));
//
//        // Test save subscription form: add subscription name as
//        // "selenium_test_subscription" and then click "save" button verification that
//        // subscription is added in the datatable (and is displayed on
//        // the main page)
//        String responseSave = this.getJSONStringFromFile(SAVETESTFILEPATH);
//        subscriptionPage.addFieldValue(subNamePath, subName);
//        TimeUnit.SECONDS.sleep(2);
//        subscriptionPage.clickFormsSaveBtn(responseSave);
//        TimeUnit.SECONDS.sleep(8);
//        assert (driver.getPageSource().contains("Selenium_test_subscription"));
//        TimeUnit.SECONDS.sleep(2);
        
        
        // // Upload a subscription, name as "Subscription_uploaded" with "Upload SUbscriptions" button and verify
         String uploadFilePath = new File("src/test/resources/Subscription_upload.txt").getAbsolutePath();
         String mockedUploadResponse = this.getJSONStringFromFile(UPLOADFILEPATH);
//         "[{\"aggregationtype\":\"eiffel-intelligence\",\"created\":1224037895323,\"notificationMeta\":\"http://eiffel-jenkins1:8080/job/ei-artifact-triggered-job/build\",\"notificationType\":\"REST_POST\",\"restPostBodyMediaType\":\"application/x-www-form-urlencoded\",\"notificationMessageKeyValues\":[{\"formkey\":\"json\",\"formvalue\":\"{parameter:[{name: 'jsonparams', value : to_string(@)}]}\"}],\"repeat\":false,\"requirements\":[{\"conditions\":[{\"jmespath\":\"gav.groupId=='com.othercompany.library'\"}]}],\"subscriptionName\":\"Subscription_uploaded\",\"_id\":{\"$oid\":\"5ad6f907c242af3f1469751d\"}}]";
        // subscriptionPage.clickUploadSubscriptions(filePath, subUploadResponse);
        // TimeUnit.SECONDS.sleep(10);
        //// assert (driver.getPageSource().contains("Subscription_uploaded"));
//         TimeUnit.SECONDS.sleep(5);
         subscriptionPage.clickUploadSubscriptionFunctionality(uploadFilePath, mockedUploadResponse);
         
         new WebDriverWait(driver, 10).until((webdriver) -> (driver.getPageSource().contains("Subscription_uploaded")));
         TimeUnit.SECONDS.sleep(5);
        
        
        
        
        // Delete all subscriptions with "Bulk Delete" button and verify that all
        // subscriptions are deleted
        String mockedDeleteResponse = "";
        subscriptionPage.clickBulkDelete(mockedDeleteResponse);
        TimeUnit.SECONDS.sleep(2);
        assertFalse(driver.getPageSource().contains("Subscription1"));
        assertFalse(driver.getPageSource().contains("Subscription2"));
        TimeUnit.SECONDS.sleep(5);
        //
        //// String responseTemp =
        // "[{\"aggregationtype\":\"eiffel-intelligence\",\"created\":1224037895323,\"notificationMeta\":\"http://eiffel-jenkins1:8080/job/ei-artifact-triggered-job/build\",\"notificationType\":\"REST_POST\",\"restPostBodyMediaType\":\"application/x-www-form-urlencoded\",\"notificationMessageKeyValues\":[{\"formkey\":\"json\",\"formvalue\":\"{parameter:[{
        // name: 'jsonparams', value :
        // to_string(@)}]}\"}],\"repeat\":false,\"requirements\":[{\"conditions\":[{\"jmespath\":\"gav.groupId=='com.othercompany.library'\"}]}],\"subscriptionName\":\"Subscription1\",\"_id\":{\"$oid\":\"5ad6f907c242af3f1469751d\"}}]";
        //// subscriptionPage.clickGetTemplate(response);
        //// TimeUnit.SECONDS.sleep(5);
        //// driver.findElement(By.xpath("//button[contains(text(),'Avbryt')]")).click();
        //// TimeUnit.SECONDS.sleep(30);
        
        // Verify that "download rules template" button works
        String mockedResponse = this.getJSONStringFromFile(SUBSCRIPTIONTEMPLATEFILEPATH);
        new WebDriverWait(driver, 10).until((webdriver) -> subscriptionPage.presenceOfClickGetTemplateButton());
        subscriptionPage.clickDownloadGetTemplate(mockedResponse);
        new WebDriverWait(driver, 10).until((webdriver) -> Files.exists(Paths.get(DOWNLOADEDTEMPLATEFILEPATH)));
        String getSubscriptionsTemplate = this.getJSONStringFromFile(DOWNLOADEDTEMPLATEFILEPATH);
        assertEquals(mockedResponse, getSubscriptionsTemplate);

    }

}
