package com.ericsson.ei.frontend;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.Test;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.ericsson.ei.config.SeleniumConfig;
import com.ericsson.ei.frontend.pageobjects.IndexPage;
import com.ericsson.ei.frontend.pageobjects.SubscriptionPage;

public class SubscriptionHandlingFunctionality extends SeleniumBaseClass {

    @MockBean
    protected CloseableHttpClient mockedHttpClient;

    private static final String DOWNLOADED_TEMPLATE_FILE_PATH = String.join(File.separator,
            SeleniumConfig.getTempDownloadDirectory().getPath(), "subscriptionsTemplate.json");
    private static final String SUBSCRIPTION_TEMPLATE_FILE_PATH = String.join(File.separator, "src", "functionaltest",
            "resources", "responses", "SubscriptionTemplate.json");
    private static final String SUBSCRIPTION_FOR_RELOAD_TEST_FILE_PATH = String.join(File.separator, "src",
            "functionaltest", "resources", "responses", "SubscriptionForUploadCase.json");
    private static final String SUBSCRIPTION_FOR_RELOAD_TEST_FILE_PATH_LDAP = String.join(File.separator, "src",
            "functionaltest", "resources", "responses", "SubscriptionForUploadLDAP.json");
    private static final String SUBSCRIPTION_FOR_SAVE_TEST_FILE_PATH = String.join(File.separator, "src",
            "functionaltest", "resources", "responses", "SubscriptionForSaveCase.json");
    private static final String SUBSCRIPTION_FOR_UPLOAD_FILE_PATH = String.join(File.separator, "src", "functionaltest",
            "resources", "responses", "SubscriptionForUploadCase.json");

    private JavascriptExecutor js;

    @Test
    public void testSubscription() throws Exception {
        // Open index page.
        IndexPage indexPageObject = new IndexPage(mockedHttpClient, driver, baseUrl);
        indexPageObject.loadPage();

        // Click on Subscription Handling page button and verify that it is open
        String subscriptionHeaderID = "subData";
        SubscriptionPage subscriptionPage = indexPageObject.clickSubscriptionPage();
        assert (new WebDriverWait(driver, 10)
                .until((webdriver) -> subscriptionPage.presenceOfHeader(subscriptionHeaderID)));

        // Press "Reload" button without enabling LDAP and verify that two
        // subscriptions with names "Subscription1" and "Subscription2" are
        // present AND there exists "edit" and" delete buttons" for unauthorized
        // user "ABCD"
        String response = getJSONStringFromFile(SUBSCRIPTION_FOR_RELOAD_TEST_FILE_PATH);
        String viewButtonXPath = "(//button[@id='view-Subscription1'])[2]";
        String editButtonXPath = "(//button[@id='edit-Subscription1'])[2]";
        String deleteButtonXPath = "(//button[@id='delete-Subscription1'])[2]";
        String expandButtonXPath = "//tr[contains(.,'Subscription1')]/td[1]";

        subscriptionPage.clickReload(response);

        assert (subscriptionPage.textExistsInTable("Subscription1"));
        assert (subscriptionPage.textExistsInTable("Subscription2"));
        assert (subscriptionPage.clickElementByXPath(expandButtonXPath));
        assert (subscriptionPage.buttonExist(deleteButtonXPath));
        assert (subscriptionPage.buttonExist(editButtonXPath));
        assert (subscriptionPage.buttonExist(viewButtonXPath));

        // Given LDAP is enabled, "Reload" subscriptions and then click
        // subscription page with LDAP enabled with unauthorized user names
        // Verify that subscriptions exists but only with "View" button
        String responseSub = getJSONStringFromFile(SUBSCRIPTION_FOR_RELOAD_TEST_FILE_PATH_LDAP);
        String responseAuth = "{\"security\":true}";

        subscriptionPage.clickReloadLDAP(responseSub, responseAuth);
        indexPageObject.clickSubscriptionPage();

        assert (subscriptionPage.clickElementByXPath(expandButtonXPath));
        assert (!subscriptionPage.buttonExist(deleteButtonXPath));
        assert (!subscriptionPage.buttonExist(editButtonXPath));
        assert (subscriptionPage.buttonExist(viewButtonXPath));

        // Given LDAP is enabled, "Reload" subscriptions and then click
        // subscription page with LDAP enabled with both unauthorized and
        // unauthorized user names (in this case authorized user is "ABCD" with
        // subscriptions, "subscription1" and "subscription2") Verify that
        // current user can see only their own subscriptions' edit and delete
        // buttons.
        String keyForUser = "currentUser";
        String valueForUser = "ABCD";

        js = (driver);
        js.executeScript(String.format("window.localStorage.setItem('%s','%s');", keyForUser, valueForUser));
        indexPageObject.clickSubscriptionPage();

        assert (subscriptionPage.textExistsInTable("Subscription1"));
        assert (subscriptionPage.clickElementByXPath(expandButtonXPath));
        assert (subscriptionPage.buttonExist(deleteButtonXPath));
        assert (subscriptionPage.buttonExist(editButtonXPath));
        assert (subscriptionPage.buttonExist(viewButtonXPath));

        // Now, path for "subscriptions2" with user name "DEF", so user "ABCD"
        // is unauthorized for this subscription
        String viewButtonXPath2 = "(//button[@id='view-Subscription2'])[2]";
        String editButtonXPath2 = "(//button[@id='edit-Subscription2'])[2]";
        String deleteButtonXPath2 = "(//button[@id='delete-Subscription2'])[2]";
        String expandButtonXPath2 = "//tr[contains(.,'Subscription2')]/td[1]";

        assert (subscriptionPage.clickElementByXPath(expandButtonXPath2));
        assert (subscriptionPage.buttonExist(viewButtonXPath2));
        assert (!subscriptionPage.buttonExist(editButtonXPath2));
        assert (!subscriptionPage.buttonExist(deleteButtonXPath2));

        // Test view button
        subscriptionPage.clickElementByXPath(viewButtonXPath2);
        assert (new WebDriverWait(driver, 10)
                .until((webdriver) -> driver.getPageSource().contains("View Subscription")));
        subscriptionPage.clickFormCloseBtn();

        // Again setting up the page status
        indexPageObject.loadPage();
        indexPageObject.clickSubscriptionPage().clickReload(response);

        // Delete all subscriptions with "Bulk Delete" button and verify that
        // all subscriptions are deleted
        String mockedDeleteResponse = "";
        subscriptionPage.clickBulkDelete(mockedDeleteResponse);
        assert (!subscriptionPage.textExistsInTable("Subscription1"));
        assert (!subscriptionPage.textExistsInTable("Subscription2"));

        // Verify that "get template" button works
        String mockedTemplateResponse = getJSONStringFromFile(SUBSCRIPTION_TEMPLATE_FILE_PATH);
        new WebDriverWait(driver, 10).until((webdriver) -> subscriptionPage.presenceOfClickGetTemplateButton());
        subscriptionPage.clickDownloadGetTemplate(mockedTemplateResponse);
        new WebDriverWait(driver, 10).until((webdriver) -> Files.exists(Paths.get(DOWNLOADED_TEMPLATE_FILE_PATH)));
        String getSubscriptionsTemplate = getJSONStringFromFile(DOWNLOADED_TEMPLATE_FILE_PATH);
        assertEquals(mockedTemplateResponse, getSubscriptionsTemplate);

        // Upload a subscription, name as "Subscription_uploaded" with "Upload
        // SUbscriptions" button and verify
        String mockedUploadResponse = getJSONStringFromFile(SUBSCRIPTION_FOR_UPLOAD_FILE_PATH);
        subscriptionPage.clickUploadSubscriptionFunctionality(DOWNLOADED_TEMPLATE_FILE_PATH, mockedUploadResponse);
        assert (subscriptionPage.textExistsInTable("Subscription_uploaded"));

        // Click "Add Subscription" button and verify that "Subscription Form"
        // is open
        subscriptionPage.clickAddSubscription();
        String formHeaderID = "formHeader";
        assert ((new WebDriverWait(driver, 10).until((webdriver) -> subscriptionPage.presenceOfHeader(formHeaderID))));

        // On subscription form, select the template as "Mail Trigger" and
        // verify Test form "Cancel" button:Click "Cancel" button and verify
        // that "Subscription Form" is closed
        subscriptionPage.clickFormsCancelBtn();
        assert (!subscriptionPage.presenceOfHeader(formHeaderID));

        // Again, click "Add Subscription" button and verify that "Subscription
        // Form" is open
        subscriptionPage.clickAddSubscription();
        assert ((new WebDriverWait(driver, 10).until((webdriver) -> subscriptionPage.presenceOfHeader(formHeaderID))));

        // On subscription form, select the template as "Mail Trigger" and
        // verify
        String selectID = "selectTemplate";
        String mailRadio = "mailRadio";
        String tempMail = "Mail Trigger";
        subscriptionPage.selectDropdown(selectID, tempMail);
        assert (subscriptionPage.isRadioCheckboxSelected(mailRadio));
        assertEquals ("mymail@company.com", subscriptionPage.getValueFromElement("metaData"));

        // On subscription form, select the template as "REST POST (Raw
        // Body:JSON)" and verify
        String tempPost = "REST POST (Raw Body : JSON)";
        String restPostRadio = "restPostRadio";
        String keyValueRadio = "keyValueRadio";
        String appJsonRadio = "appJsonRadio";
        subscriptionPage.selectDropdown(selectID, tempPost);
        assert (subscriptionPage.isRadioCheckboxSelected(restPostRadio));
        assert (subscriptionPage.isRadioCheckboxSelected(appJsonRadio));
        assert (!subscriptionPage.isRadioCheckboxSelected(keyValueRadio));
        assertEquals ("http://<MyHost:port>/api/doit", subscriptionPage.getValueFromElement("metaData"));

        // On subscription form, select the template as "Jenkins Pipeline
        // Parameterized Job Trigger" and verify RawBody unselected.
        String tempJenkins = "Jenkins Pipeline Parameterized Job Trigger";
        subscriptionPage.selectDropdown(selectID, tempJenkins);
        assert (subscriptionPage.isRadioCheckboxSelected(restPostRadio));
        assert (subscriptionPage.isRadioCheckboxSelected(keyValueRadio));
        assert (!subscriptionPage.isRadioCheckboxSelected(appJsonRadio));
        assertEquals("http://<JenkinsHost:port>/job/<JobName>/job/<branch>/build",
                subscriptionPage.getValueFromElement("metaData"));

        // Choose Authorization as "Basic_AUTH" ===> input User Name as "ABCD"
        // and Token as "EFGH" ===> click "Generate Key/Value Pair", verify the
        // basic authentication is generated
        String selectAuthID = "selectAuth";
        String authValue = "BASIC_AUTH";
        String userName = "ABCD";
        String userNameID = "userNameInput";
        String token = "EFGH";
        String tokenID = "tokenInput";

        subscriptionPage.selectDropdown(selectAuthID, authValue);
        subscriptionPage.addFieldValue(userNameID, userName);
        subscriptionPage.addFieldValue(tokenID, token);
        String kvID = "kvID";
        subscriptionPage.clickKVbtn(kvID);
        assert (new WebDriverWait(driver, 10).until((webdriver) -> driver.getPageSource().contains("Authorization")));

        // Test "Repeat" checkbox: verify unchecked, then checked.
        // NOTE: repeat checkbox is covered by a span, we click span
        String checkboxRepeatID = "repeatCheckbox";
        String spanId = "repeatCheckboxSpan";
        assert (!subscriptionPage.isCheckboxSelected(checkboxRepeatID));
        subscriptionPage.clickSpanAroundCheckbox(checkboxRepeatID, spanId);
        assert (subscriptionPage.isCheckboxSelected(checkboxRepeatID));

        // Test "Add Condition" button: click add condition button and check that it adds an additional "condition" field
        String conditionFieldID = "conditionID";
        subscriptionPage.clickAddConditionBtn();
        assertEquals(2, subscriptionPage.countElements(conditionFieldID));

        // Test "Add Requirement" button: click the button and assert that it adds an additional "requirement" field
        String requirementFieldID = "requirementID";
        subscriptionPage.clickAddRequirementBtn();
        assertEquals(2, subscriptionPage.countElements(requirementFieldID));

        // Test save subscription form: add subscription name
        // as "selenium_test_subscription" and then click "save" button
        // verification that subscription is added in the datatable (and is
        // displayed on the main page)
        String subName = "Selenium_test_subscription";
        String subNameID = "subscriptionNameInput";
        String responseSave = getJSONStringFromFile(SUBSCRIPTION_FOR_SAVE_TEST_FILE_PATH);
        subscriptionPage.addFieldValue(subNameID, subName);
        subscriptionPage.clickFormsSaveBtn(responseSave);
        assert (subscriptionPage.textExistsInTable("Selenium_test_subscription"));
    }
}
