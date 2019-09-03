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
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockserver.client.MockServerClient;
import org.mockserver.integration.ClientAndServer;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.ericsson.ei.config.SeleniumConfig;
import com.ericsson.ei.frontend.pageobjects.SubscriptionPage;

public class TestSubscriptionHandling extends SeleniumBaseClass {

    private static final String DOWNLOADED_TEMPLATE_FILE_PATH = String.join(File.separator, SeleniumConfig.getTempDownloadDirectory().getPath(),
            "subscriptionsTemplate.json");
    private static final String DOWNLOADED_BULK_SUBSCRIPTIONS_FILE_PATH = String.join(File.separator, SeleniumConfig.getTempDownloadDirectory().getPath(),
            "subscriptionsData.json");
    private static final String SUBSCRIPTION_TEMPLATE_FILE_PATH = String.join(File.separator, "src", "functionaltest", "resources", "responses",
            "SubscriptionTemplate.json");
    private static final String SUBSCRIPTION_FOR_RELOAD_TEST_FILE_PATH_LDAP = String.join(File.separator, "src", "functionaltest", "resources",
            "responses", "SubscriptionForUploadLDAP.json");

    private static final String EXPAND_BUTTON_XPATH = "//tr[contains(.,'Subscription1')]/td[1]";
    private static final String VIEW_BUTTON_XPATH = "(//button[@id='view-Subscription1'])";
    private static final String CLONE_BUTTON_XPATH = "(//button[@id='clone-Subscription1'])";
    private static final String EDIT_BUTTON_XPATH = "(//button[@id='edit-Subscription1'])";
    private static final String DELETE_BUTTON_XPATH = "(//button[@id='delete-Subscription1'])";

    private static final String VIEW_BUTTON_XPATH2 = "(//button[@id='view-Subscription2'])";
    private static final String EDIT_BUTTON_XPATH2 = "(//button[@id='edit-Subscription2'])";
    private static final String DELETE_BUTTON_XPATH2 = "(//button[@id='delete-Subscription2'])";
    private static final String EXPAND_BUTTON_XPATH2 = "//tr[contains(.,'Subscription2')]/td[1]";

    private static final String FORM_HEADER = "formHeader";
    private static final String SELECT_ID = "selectTemplate";
    private static final String TEMPLATE_MAIL = "Mail Trigger";
    private static final String RADIO_BUTTON_MAIL = "mailRadio";
    private static final String NOTIFICATION_META_ID = "notificationMeta";
    private static final String EXPECTED_EMAIL = "mymail@company.com";
    private static final String TEMPLATE_POST = "REST POST (Raw Body : JSON)";
    private static final String RADIO_BUTTON_REST = "restPostRadio";
    private static final String RADIO_BUTTON_KEY_VALUE = "keyValueRadio";
    private static final String RADIO_BUTTON_JSON = "appJsonRadio";
    private static final String EXPECTED_REST_URL = "http://<MyHost:port>/api/doit";
    private static final String TEMPLATE_JENKINS = "Jenkins Pipeline Parameterized Job Trigger";
    private static final String EXPECTED_JENKINS_URL = "http://<JenkinsHost:port>/job/<JobName>/job/<branch>/build";

    private static MockServerClient mockClient;
    private static ClientAndServer mockServer;
    private static final String BASE_URL = "localhost";

    @MockBean
    protected CloseableHttpClient mockedHttpClient;

    private SubscriptionPage subscriptionPage;

    @BeforeClass
    public static void setUpMocks() throws IOException {
        mockServer = startClientAndServer();
        mockClient = new MockServerClient(BASE_URL, mockServer.getLocalPort());
    }

    @Before
    public void before() throws IOException {
        int portServer = mockServer.getLocalPort();
        backEndInstancesUtils.setDefaultBackEndInstanceToNull();
        backEndInstancesUtils.setDefaultBackEndInstance("new_instance_default", "localhost", portServer, "", true);
        subscriptionPage = new SubscriptionPage(mockedHttpClient, driver, baseUrl);
    }

    @Test
    public void testSubscriptionButtons() throws Exception {
        final boolean security = false;
        final String username = "";
        setupMockEndpoints(security, username);
        removeAllSubscriptions();
        clickAndVerifyGetTemplateButton();
        clickAndVerifyBulkDownloadButton();
        uploadSubscriptions();
        verifySubscriptionsRequestDeleteAndPost();
    }

    @Test
    public void testAddSubscriptionAndVerifyForm() throws Exception {
        final boolean security = false;
        final String username = "";
        setupMockEndpoints(security, username);
        loadAndRefreshSubscriptionPage();
        clickAddSubscriptionAndVerifyFormOpen();
        clickFormCancelAndVerifyFormClosed();
        clickCloneSubscriptionAndVerifyFormOpen();
        clickFormCancelAndVerifyFormClosed();
        clickAddSubscriptionAndVerifyFormOpen();
        verifySelectTemplateMail();
        verifySelectTemplateREST();
        verifySelectTemplateJenkins();
        fillAndVerifyUsernameAndPassword();
        clickAndVerifyRepeatRadioButton();
        clickAndVerifyAddConditionButton();
        clickAndVerifyAddRequirementButton();
        verifySelectTemplateMail();
        clickSaveButtonAndVerifyFormClosed();
    }

    @Test
    public void testSubscriptionHandlingWithLDAPDisabled() throws Exception {
        final boolean security = false;
        final String username = "";
        setupMockEndpoints(security, username);
        loadAndRefreshSubscriptionPage();
        verifyAuthorizedSubscriptionCRUD();
        verifyViewButtonOnSubscription();
    }

    @Test
    public void testSubscriptionHandlingWithLDAPEnabledInvalidUser() throws Exception {
        final boolean security = true;
        final String username = "";
        setupMockEndpoints(security, username);
        loadAndRefreshSubscriptionPage();
        verifyUnauthorizedSubscriptionCRUD();
    }

    @Test
    public void testSubscriptionHandlingWithLDAPEnabled() throws Exception {
        final boolean security = true;
        final String username = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        setupMockEndpoints(security, username);
        loadAndRefreshSubscriptionPage();
        verifyAuthorizedSubscriptionCRUD();
        verifyUnauthorizedSubscriptionCRUD();
    }

    @AfterClass
    public static void tearDownMocks() throws IOException {
        mockClient.stop();
    }

    private void loadAndRefreshSubscriptionPage() {
        subscriptionPage.loadPage();
        subscriptionPage.refreshPage();
    }

    private void uploadSubscriptions() throws IOException {
        subscriptionPage.clickUploadSubscriptionFunctionality(DOWNLOADED_TEMPLATE_FILE_PATH);
    }

    private void removeAllSubscriptions() throws IOException {
        subscriptionPage.loadPage();
        subscriptionPage.clickCheckAll();
        subscriptionPage.clickBulkDelete();
        subscriptionPage.refreshPage();
    }

    private void verifySubscriptionsRequestDeleteAndPost() throws InterruptedException, IOException {
        Thread.sleep(1000);
        String downloadedSubscriptionsTemplate = getJSONStringFromFile(DOWNLOADED_TEMPLATE_FILE_PATH);
        mockClient.verify(request().withMethod("DELETE").withPath("/subscriptions?subscriptionName=Subscription1,Subscription2,Subscription3"));
        mockClient.verify(request().withMethod("POST").withPath("/subscriptions").withBody(downloadedSubscriptionsTemplate));
    }

    private void clickAndVerifyBulkDownloadButton() throws IOException {
        subscriptionPage.loadPage();
        subscriptionPage.clickCheckAll();
        subscriptionPage.clickBulkDownload();
        new WebDriverWait(driver, 10).until((webdriver) -> Files.exists(Paths.get(DOWNLOADED_BULK_SUBSCRIPTIONS_FILE_PATH)));
        String downloadedSubscriptionsTemplate = getJSONStringFromFile(DOWNLOADED_BULK_SUBSCRIPTIONS_FILE_PATH);
        String subscriptions = getJSONStringFromFile(SUBSCRIPTION_FOR_RELOAD_TEST_FILE_PATH_LDAP);
        assertEquals(subscriptions, downloadedSubscriptionsTemplate);
    }

    private void clickAndVerifyGetTemplateButton() throws IOException {
        new WebDriverWait(driver, 10).until((webdriver) -> subscriptionPage.presenceOfClickGetTemplateButton());
        subscriptionPage.clickGetTemplate();
        new WebDriverWait(driver, 10).until((webdriver) -> Files.exists(Paths.get(DOWNLOADED_TEMPLATE_FILE_PATH)));
        String subscriptionTemplate = getJSONStringFromFile(SUBSCRIPTION_TEMPLATE_FILE_PATH);
        String downloadedSubscriptionsTemplate = getJSONStringFromFile(DOWNLOADED_TEMPLATE_FILE_PATH);
        assertEquals(subscriptionTemplate, downloadedSubscriptionsTemplate);
    }

    private void clickSaveButtonAndVerifyFormClosed() throws IOException {
        subscriptionPage.clickFormsSaveBtn();
        assert (subscriptionPage.noPresenceOfHeader(FORM_HEADER));
    }

    private void clickAndVerifyAddRequirementButton() {
        String requirementFieldID = "requirementID";
        subscriptionPage.clickAddRequirementBtn();
        assertEquals(2, subscriptionPage.countElements(requirementFieldID));
    }

    private void clickAndVerifyAddConditionButton() {
        String conditionFieldID = "conditionID";
        subscriptionPage.clickAddConditionBtn();
        assertEquals(2, subscriptionPage.countElements(conditionFieldID));
    }

    private void clickAndVerifyRepeatRadioButton() {
        String checkboxRepeatID = "repeatCheckbox";
        String spanId = "repeatCheckboxSpan";
        assert (!subscriptionPage.isCheckboxSelected(checkboxRepeatID));
        subscriptionPage.clickSpanAroundCheckbox(checkboxRepeatID, spanId);
        assert (subscriptionPage.isCheckboxSelected(checkboxRepeatID));
    }

    private void fillAndVerifyUsernameAndPassword() {
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
    }

    private void verifySelectTemplateJenkins() {
        subscriptionPage.selectDropdown(SELECT_ID, TEMPLATE_JENKINS);
        assert (subscriptionPage.isRadioCheckboxSelected(RADIO_BUTTON_REST));
        assert (!subscriptionPage.isRadioCheckboxSelected(RADIO_BUTTON_JSON));
        assert (subscriptionPage.isRadioCheckboxSelected(RADIO_BUTTON_KEY_VALUE));
        assertEquals(EXPECTED_JENKINS_URL, subscriptionPage.getValueFromElement(NOTIFICATION_META_ID));
    }

    private void verifySelectTemplateREST() {
        subscriptionPage.selectDropdown(SELECT_ID, TEMPLATE_POST);
        assert (subscriptionPage.isRadioCheckboxSelected(RADIO_BUTTON_REST));
        assert (subscriptionPage.isRadioCheckboxSelected(RADIO_BUTTON_JSON));
        assert (!subscriptionPage.isRadioCheckboxSelected(RADIO_BUTTON_KEY_VALUE));
        assertEquals(EXPECTED_REST_URL, subscriptionPage.getValueFromElement(NOTIFICATION_META_ID));
    }

    private void verifySelectTemplateMail() {
        subscriptionPage.selectDropdown(SELECT_ID, TEMPLATE_MAIL);
        assert (subscriptionPage.isRadioCheckboxSelected(RADIO_BUTTON_MAIL));
        assertEquals(EXPECTED_EMAIL, subscriptionPage.getValueFromElement(NOTIFICATION_META_ID));
    }

    private void clickFormCancelAndVerifyFormClosed() {
        subscriptionPage.clickFormsCancelBtn();
        assert (!subscriptionPage.presenceOfHeader(FORM_HEADER));
    }

    private void clickAddSubscriptionAndVerifyFormOpen() {
        subscriptionPage.clickAddSubscription();
        assert (subscriptionPage.presenceOfHeader(FORM_HEADER));
    }

    private void clickCloneSubscriptionAndVerifyFormOpen() {
        subscriptionPage.clickExpandButtonByXPath(EXPAND_BUTTON_XPATH);
        subscriptionPage.clickButtonByXPath(CLONE_BUTTON_XPATH);
        assert (subscriptionPage.presenceOfHeader(FORM_HEADER));
    }

    private void verifyViewButtonOnSubscription() {
        subscriptionPage.clickExpandButtonByXPath(EXPAND_BUTTON_XPATH2);
        subscriptionPage.clickButtonByXPath(VIEW_BUTTON_XPATH2);
        assert (new WebDriverWait(driver, 10).until((webdriver) -> driver.getPageSource().contains("View Subscription")));
        subscriptionPage.clickFormCloseBtn();
    }

    private void verifyAuthorizedSubscriptionCRUD() {
        assert (subscriptionPage.clickExpandButtonByXPath(EXPAND_BUTTON_XPATH));
        assert (subscriptionPage.buttonExistByXPath(VIEW_BUTTON_XPATH));
        assert (subscriptionPage.buttonExistByXPath(EDIT_BUTTON_XPATH));
        assert (subscriptionPage.buttonExistByXPath(DELETE_BUTTON_XPATH));
    }

    private void verifyUnauthorizedSubscriptionCRUD() {
        assert (subscriptionPage.clickExpandButtonByXPath(EXPAND_BUTTON_XPATH2));
        assert (subscriptionPage.buttonExistByXPath(VIEW_BUTTON_XPATH2));
        assert (subscriptionPage.buttonDisabledByXPath(EDIT_BUTTON_XPATH2));
        assert (subscriptionPage.buttonDisabledByXPath(DELETE_BUTTON_XPATH2));
    }

    private void setupMockEndpoints(boolean security, String user) throws IOException {
        mockClient.clear(request());
        String subscriptionResponse = getJSONStringFromFile(SUBSCRIPTION_FOR_RELOAD_TEST_FILE_PATH_LDAP);
        String downloadBulkSubscriptionResponse = "{\"foundSubscriptions\": " + subscriptionResponse + "}";
        mockClient.when(request().withMethod("GET").withPath("/subscriptions"))
                .respond(response().withStatusCode(200).withBody(subscriptionResponse));
        mockClient.when(request().withMethod("GET").withPath("/subscriptions/Subscription1,Subscription2,Subscription3"))
                .respond(response().withStatusCode(200).withBody(downloadBulkSubscriptionResponse));
        mockClient.when(request().withMethod("DELETE").withPath("/subscriptions")).respond(response().withStatusCode(200).withBody(""));
        mockClient.when(request().withMethod("POST").withPath("/subscriptions")).respond(response().withStatusCode(200).withBody(""));

        String subscriptionResponse2 = getJSONStringFromFile(SUBSCRIPTION_TEMPLATE_FILE_PATH);
        String downloadSubscriptionResponse = "{\"foundSubscriptions\": " + subscriptionResponse2 + "}";
        mockClient.when(request().withMethod("GET").withPath("/subscriptions/Subscription2"))
                .respond(response().withStatusCode(200).withBody(subscriptionResponse2));
        mockClient.when(request().withMethod("GET").withPath("/subscriptions/Subscription1"))
                .respond(response().withStatusCode(200).withBody(downloadSubscriptionResponse));

        String responseStatus = "{\"status\":\"OK\"}";
        mockClient.when(request().withMethod("GET").withPath("/auth/checkStatus")).respond(response().withStatusCode(200).withBody(responseStatus));

        String mockedTemplateResponse = getJSONStringFromFile(SUBSCRIPTION_TEMPLATE_FILE_PATH);
        mockClient.when(request().withMethod("DELETE").withPath("/subscriptions/Subscription1,Subscription2,Subscription3"))
                .respond(response().withStatusCode(200).withBody(""));
        mockClient.when(request().withMethod("GET").withPath("/download/subscriptionsTemplate"))
                .respond(response().withStatusCode(200).withBody(mockedTemplateResponse));

        String responseAuth = "{\"security\":" + security + "}";
        String responseUser = "{\"user\":\"" + user + "\"}";
        mockClient.when(request().withMethod("GET").withPath("/auth")).respond(response().withStatusCode(200).withBody(responseAuth));
        mockClient.when(request().withMethod("GET").withPath("/auth/login")).respond(response().withStatusCode(200).withBody(responseUser));
    }
}
