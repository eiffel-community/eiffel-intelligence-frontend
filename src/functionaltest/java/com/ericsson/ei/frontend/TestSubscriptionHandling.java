package com.ericsson.ei.frontend;

import static org.junit.Assert.assertEquals;
import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockserver.client.MockServerClient;
import org.mockserver.integration.ClientAndServer;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.ericsson.ei.config.SeleniumConfig;
import com.ericsson.ei.frontend.pageobjects.IndexPage;
import com.ericsson.ei.frontend.pageobjects.SubscriptionPage;

public class TestSubscriptionHandling extends SeleniumBaseClass {

    private static final String DOWNLOADED_TEMPLATE_FILE_PATH = String.join(File.separator,
            SeleniumConfig.getTempDownloadDirectory().getPath(), "subscriptionsTemplate.json");
    private static final String SUBSCRIPTION_TEMPLATE_FILE_PATH = String.join(File.separator, "src", "functionaltest",
            "resources", "responses", "SubscriptionTemplate.json");
    private static final String SUBSCRIPTION_FOR_RELOAD_TEST_FILE_PATH_LDAP = String.join(File.separator, "src",
            "functionaltest", "resources", "responses", "SubscriptionForUploadLDAP.json");

    private static final String EXPAND_BUTTON_XPATH = "//tr[contains(.,'Subscription1')]/td[1]";
    private static final String VIEW_BUTTON_XPATH = "(//button[@id='view-Subscription1'])";
    private static final String EDIT_BUTTON_XPATH = "(//button[@id='edit-Subscription1'])";
    private static final String DELETE_BUTTON_XPATH = "(//button[@id='delete-Subscription1'])";

    private static final String VIEW_BUTTON_XPATH2 = "(//button[@id='view-Subscription2'])";
    private static final String EDIT_BUTTON_XPATH2 = "(//button[@id='edit-Subscription2'])";
    private static final String DELETE_BUTTON_XPATH2 = "(//button[@id='delete-Subscription2'])";
    private static final String EXPAND_BUTTON_XPATH2 = "//tr[contains(.,'Subscription2')]/td[1]";

    private static MockServerClient mockClient;
    private static ClientAndServer mockServer;
    private static final String BASE_URL = "localhost";

    @MockBean
    protected CloseableHttpClient mockedHttpClient;

    @BeforeClass
    public static void setUpMocks() throws IOException {
        mockServer = startClientAndServer();
        mockClient = new MockServerClient(BASE_URL, mockServer.getLocalPort());
    }

    @Test
    public void testSubscriptionButtons() throws Exception {
        // Tests Bulk Delete, Get Template and Upload Subscriptions
        // Set up
        int portServer = mockServer.getLocalPort();
        backEndInstancesUtils.setDefaultBackEndInstanceToNull();
        backEndInstancesUtils.setDefaultBackEndInstance("new_instance_default", "localhost", portServer, "", true);
        setupMockEndpoints(false, "");

        // Open subscription page.
        IndexPage indexPageObject = openIndexPage();
        SubscriptionPage subscriptionPage = openSubscriptionPage(indexPageObject);

        // Setup mocks for "Bulk Delete" and get Template
        String mockedTemplateResponse = getJSONStringFromFile(SUBSCRIPTION_TEMPLATE_FILE_PATH);
        mockClient
                .when(request().withMethod("DELETE")
                        .withPath("/subscriptions/Subscription1,Subscription2,Subscription3"))
                .respond(response().withStatusCode(200).withBody(""));
        mockClient.when(request().withMethod("GET").withPath("/download/subscriptionsTemplate"))
                .respond(response().withStatusCode(200).withBody(mockedTemplateResponse));

        // Delete all subscriptions before continuing (This does not delete
        // subscriptions as subscriptions are mocked and cannot be deleted, we check mock
        // server for correct call at the end of the test)
        subscriptionPage.clickBulkDelete();

        // Verify that "get template" button works
        // Download subscription template
        subscriptionPage.refreshPage();
        new WebDriverWait(driver, 10).until((webdriver) -> subscriptionPage.presenceOfClickGetTemplateButton());
        subscriptionPage.clickGetTemplate();

        new WebDriverWait(driver, 10).until((webdriver) -> Files.exists(Paths.get(DOWNLOADED_TEMPLATE_FILE_PATH)));
        String getSubscriptionsTemplate = getJSONStringFromFile(DOWNLOADED_TEMPLATE_FILE_PATH);
        assertEquals(mockedTemplateResponse, getSubscriptionsTemplate);

        // Upload a subscription
        subscriptionPage.clickUploadSubscriptionFunctionality(DOWNLOADED_TEMPLATE_FILE_PATH);


        // Verify that subscriptions where deleted and added via calls to mocked
        // server
        Thread.sleep(1000);
        mockClient.verify(request()
                .withMethod("DELETE").withPath("/subscriptions/Subscription1,Subscription2,Subscription3"));
        mockClient.verify(request().withMethod("POST").withPath("/subscriptions").withBody(getSubscriptionsTemplate));
    }

    @Test
    public void testAddSubscriptionAndVerifyForm() throws Exception {
        // Set up
        int portServer = mockServer.getLocalPort();
        backEndInstancesUtils.setDefaultBackEndInstanceToNull();
        backEndInstancesUtils.setDefaultBackEndInstance("new_instance_default", "localhost", portServer, "", true);
        setupMockEndpoints(false, "");

        // Open subscription page.
        IndexPage indexPageObject = openIndexPage();
        SubscriptionPage subscriptionPage = openSubscriptionPage(indexPageObject);

        // Click "Add Subscription" button and verify that "Subscription Form"
        // is open
        subscriptionPage.clickAddSubscription();
        String formHeaderID = "formHeader";
        assert (subscriptionPage.presenceOfHeader(formHeaderID));

        // Verify Test form "Cancel" button:Click "Cancel" button and verify
        // that "Subscription Form" is closed
        subscriptionPage.clickFormsCancelBtn();
        assert (!subscriptionPage.presenceOfHeader(formHeaderID));

        // Again, click "Add Subscription" button and verify that "Subscription
        // Form" is open
        subscriptionPage.clickAddSubscription();
        assert (subscriptionPage.presenceOfHeader(formHeaderID));

        // On subscription form, select the template as "Mail Trigger" and
        // verify
        String selectID = "selectTemplate";
        String mailRadioID = "mailRadio";
        String notificationMetaInputID = "notificationMeta";
        String tempMail = "Mail Trigger";
        subscriptionPage.selectDropdown(selectID, tempMail);
        assert (subscriptionPage.isRadioCheckboxSelected(mailRadioID));
        assertEquals("mymail@company.com", subscriptionPage.getValueFromElement(notificationMetaInputID));

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
        assertEquals("http://<MyHost:port>/api/doit", subscriptionPage.getValueFromElement(notificationMetaInputID));

        // On subscription form, select the template as "Jenkins Pipeline
        // Parameterized Job Trigger" and verify RawBody unselected.
        String tempJenkins = "Jenkins Pipeline Parameterized Job Trigger";
        subscriptionPage.selectDropdown(selectID, tempJenkins);
        assert (subscriptionPage.isRadioCheckboxSelected(restPostRadio));
        assert (subscriptionPage.isRadioCheckboxSelected(keyValueRadio));
        assert (!subscriptionPage.isRadioCheckboxSelected(appJsonRadio));
        assertEquals("http://<JenkinsHost:port>/job/<JobName>/job/<branch>/build",
                subscriptionPage.getValueFromElement(notificationMetaInputID));

        // Choose Authorization as "Basic_AUTH" ===> input User Name as
        // "ABCD" and Token as "EFGH" ===> click "Generate Key/Value Pair", verify
        // the basic authentication is generated
        String selectAuthID = "selectAuth";
        String authValue = "BASIC_AUTH";
        String userName = "ABCD";
        String userNameID = "userNameInput";
        String password = "password";
        String passwordID = "passwordInput";
        subscriptionPage.selectDropdown(selectAuthID, authValue);
        subscriptionPage.addFieldValue(userNameID, userName);
        subscriptionPage.addFieldValue(passwordID, password);

        String foundUsername = subscriptionPage.getValueFromElement(userNameID);
        assertEquals(userName, foundUsername);
        String foundPassword = subscriptionPage.getValueFromElement(passwordID);
        assertEquals(password, foundPassword);

        // Test "Repeat" checkbox: verify unchecked, then checked.
        // NOTE: repeat checkbox is covered by a span, we click span
        String checkboxRepeatID = "repeatCheckbox";
        String spanId = "repeatCheckboxSpan";
        assert (!subscriptionPage.isCheckboxSelected(checkboxRepeatID));
        subscriptionPage.clickSpanAroundCheckbox(checkboxRepeatID, spanId);
        assert (subscriptionPage.isCheckboxSelected(checkboxRepeatID));
        //
        // Test "Add Condition" button: click add condition button and check
        // that it adds an additional "condition" field
        String conditionFieldID = "conditionID";
        subscriptionPage.clickAddConditionBtn();
        assertEquals(2, subscriptionPage.countElements(conditionFieldID));
        //
        // Test "Add Requirement" button: click the button and assert that it
        // adds an additional "requirement" field
        String requirementFieldID = "requirementID";
        subscriptionPage.clickAddRequirementBtn();
        assertEquals(2, subscriptionPage.countElements(requirementFieldID));

        subscriptionPage.selectDropdown(selectID, tempMail);
        assert (subscriptionPage.isRadioCheckboxSelected(mailRadioID));
        assertEquals("mymail@company.com", subscriptionPage.getValueFromElement(notificationMetaInputID));

        // Save and verify that form was closed, if form was not closed something went wrong.
        subscriptionPage.clickFormsSaveBtn();
        assert (subscriptionPage.noPresenceOfHeader(formHeaderID));
    }

    @Test
    public void testSubscriptionHandlingWithLDAPDisabled() throws Exception {
        // Set up
        int portServer = mockServer.getLocalPort();
        backEndInstancesUtils.setDefaultBackEndInstanceToNull();
        backEndInstancesUtils.setDefaultBackEndInstance("new_instance_default",
        "localhost", portServer, "", true);
        setupMockEndpoints(false, "");

        // Open subscription page.
        IndexPage indexPageObject = openIndexPage();
        SubscriptionPage subscriptionPage =
        openSubscriptionPage(indexPageObject);

        // Press "Reload" button without enabling LDAP and verify that two
        // subscriptions with names "Subscription1" and "Subscription2" are
        // present AND there exists "edit" and" delete buttons" for
        // unauthorized user "ABCD"

        assert (subscriptionPage.clickExpandButtonByXPath(EXPAND_BUTTON_XPATH));
        assert (subscriptionPage.buttonExistByXPath(DELETE_BUTTON_XPATH));

        // Test View button on a subscription
        subscriptionPage.clickViewButtonByXPath(VIEW_BUTTON_XPATH2);
        assert (new WebDriverWait(driver, 10)
                .until((webdriver) -> driver.getPageSource().contains("View Subscription")));
        subscriptionPage.clickFormCloseBtn();
    }

    @Test
    public void testSubscriptionHandlingWithLDAPEnabledInvalidUser() throws Exception {
        // Set up
        int portServer = mockServer.getLocalPort();
        backEndInstancesUtils.setDefaultBackEndInstanceToNull();
        backEndInstancesUtils.setDefaultBackEndInstance("new_instance_default",
        "localhost", portServer, "", true);
        setupMockEndpoints(true, "");

        // Open subscription page.
        IndexPage indexPageObject = openIndexPage();
        SubscriptionPage subscriptionPage =
        openSubscriptionPage(indexPageObject);

        // Given LDAP is enabled, "Reload" subscriptions and reload
        // subscription page with LDAP enabled with unauthorized user names
        // Verify that subscriptions exists but only with "View" button
        assert (subscriptionPage.clickExpandButtonByXPath(EXPAND_BUTTON_XPATH));
        assert (subscriptionPage.buttonDoesNotExistByXPath(DELETE_BUTTON_XPATH));
        assert (subscriptionPage.buttonDoesNotExistByXPath(EDIT_BUTTON_XPATH));
        assert (subscriptionPage.buttonExistByXPath(VIEW_BUTTON_XPATH));
    }

    @Test
    public void testSubscriptionHandlingWithLDAPEnabled() throws Exception {
        // Set up
        int portServer = mockServer.getLocalPort();
        backEndInstancesUtils.setDefaultBackEndInstanceToNull();
        backEndInstancesUtils.setDefaultBackEndInstance("new_instance_default",
        "localhost", portServer, "", true);
        setupMockEndpoints(true, "ABCD");

        // Open subscription page.
        IndexPage indexPageObject = openIndexPage();
        SubscriptionPage subscriptionPage =
        openSubscriptionPage(indexPageObject);

        // Given LDAP is enabled, reload the index page and mock the user
        // response as user 'ABCD'. Verify that current user can see only
        // their own subscriptions' edit and delete buttons.
        subscriptionPage.refreshPage();
        assert (subscriptionPage.textExistsInTable("Subscription1"));
        assert (subscriptionPage.clickExpandButtonByXPath(EXPAND_BUTTON_XPATH));
        assert (subscriptionPage.buttonExistByXPath(DELETE_BUTTON_XPATH));
        assert (subscriptionPage.buttonExistByXPath(EDIT_BUTTON_XPATH));
        assert (subscriptionPage.buttonExistByXPath(VIEW_BUTTON_XPATH));

        // Now, path for "subscriptions2" with user name "DEF", so user "ABCD"
        // is unauthorized for this subscription
        assert (subscriptionPage.clickExpandButtonByXPath(EXPAND_BUTTON_XPATH2));
        assert (subscriptionPage.buttonExistByXPath(VIEW_BUTTON_XPATH2));
        assert (subscriptionPage.buttonDoesNotExistByXPath(EDIT_BUTTON_XPATH2));
        assert (subscriptionPage.buttonDoesNotExistByXPath(DELETE_BUTTON_XPATH2));

    }

    private IndexPage openIndexPage() {
        IndexPage indexPageObject = new IndexPage(mockedHttpClient, driver, baseUrl);
        indexPageObject.loadPage();
        return indexPageObject;
    }

    private SubscriptionPage openSubscriptionPage(IndexPage indexPageObject) throws IOException {
        // Click on Subscription Handling page button and verify that it is open
        String subscriptionHeaderID = "subData";
        SubscriptionPage subscriptionPage = indexPageObject.clickSubscriptionPage();
        assert (new WebDriverWait(driver, 10)
                .until((webdriver) -> subscriptionPage.presenceOfHeader(subscriptionHeaderID)));

        return subscriptionPage;
    }

    private static void setupMockEndpoints(boolean security, String user) throws IOException {
        mockClient.clear(request());
        String subscriptionResponse = getJSONStringFromFile(SUBSCRIPTION_FOR_RELOAD_TEST_FILE_PATH_LDAP);
        mockClient.when(request().withMethod("GET").withPath("/subscriptions"))
                .respond(response().withStatusCode(200).withBody(subscriptionResponse));
        mockClient.when(request().withMethod("DELETE").withPath("/subscriptions"))
                .respond(response().withStatusCode(200).withBody(""));
        mockClient.when(request().withMethod("POST").withPath("/subscriptions"))
                .respond(response().withStatusCode(200).withBody(""));

        String subscriptionResponse2 = getJSONStringFromFile(SUBSCRIPTION_TEMPLATE_FILE_PATH);
        mockClient.when(request().withMethod("GET").withPath("/subscriptions/Subscription2"))
                .respond(response().withStatusCode(200).withBody(subscriptionResponse2));

        String responseStatus = "{\"status\":\"OK\"}";
        mockClient.when(request().withMethod("GET").withPath("/auth/checkStatus"))
                .respond(response().withStatusCode(200).withBody(responseStatus));

        String responseAuth = "{\"security\":" + security + "}";
        String responseUser = "{\"user\":\"" + user + "\"}";
        mockClient.when(request().withMethod("GET").withPath("/auth"))
                .respond(response().withStatusCode(200).withBody(responseAuth));
        mockClient.when(request().withMethod("GET").withPath("/auth/login"))
                .respond(response().withStatusCode(200).withBody(responseUser));

    }

    @AfterClass
    public static void tearDownMocks() throws IOException {
        mockClient.stop();
    }

}
