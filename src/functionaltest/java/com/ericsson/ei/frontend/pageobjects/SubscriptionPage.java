package com.ericsson.ei.frontend.pageobjects;

import java.io.IOException;
<<<<<<< HEAD
import org.apache.http.client.ClientProtocolException;
=======
>>>>>>> 0e89adf4b5a783ced84288409197965f5515665f
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestBase;

import org.apache.http.impl.client.CloseableHttpClient;
import org.mockito.Mockito;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

public class SubscriptionPage extends PageBaseClass {
<<<<<<< HEAD
    WebDriverWait wait = new WebDriverWait(driver, 10);

    public SubscriptionPage(CloseableHttpClient mockedHttpClient, FirefoxDriver driver, String baseUrl)
            throws ClientProtocolException, IOException {

=======
    public SubscriptionPage(CloseableHttpClient mockedHttpClient, FirefoxDriver driver, String baseUrl)
            throws IOException {
>>>>>>> 0e89adf4b5a783ced84288409197965f5515665f
        super(mockedHttpClient, driver, baseUrl);
    }

    public boolean presenceOfHeader(String loc) {
        try {
            new WebDriverWait(driver, TIMEOUT_TIMER).until(ExpectedConditions.elementToBeClickable(By.id(loc)));
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    public void clickAddSubscription() {
<<<<<<< HEAD
        new WebDriverWait(driver, TIMEOUT_TIMER).until(ExpectedConditions.elementToBeClickable(By.id("addSubscription")));
        WebElement addSubscriptionBtn = driver.findElement(By.id("addSubscription"));

=======
        WebElement addSubscriptionBtn = new WebDriverWait(driver, TIMEOUT_TIMER)
                .until(ExpectedConditions.elementToBeClickable(By.id("addSubscription")));
>>>>>>> 0e89adf4b5a783ced84288409197965f5515665f
        addSubscriptionBtn.click();
    }

    public void clickFormsCancelBtn() {
        new WebDriverWait(driver, TIMEOUT_TIMER).until(ExpectedConditions.elementToBeClickable(By.id("cancelButton")));
        WebElement cancelBtn = driver.findElement(By.id("cancelButton"));
        cancelBtn.click();
    }

    public void clickBulkDelete(String response) throws IOException {
        CloseableHttpResponse responseData = this.createMockedHTTPResponse(response, 200);
        Mockito.doReturn(responseData).when(mockedHttpClient)
                .execute(Mockito.argThat(request -> (request).getURI().toString().contains("subscriptions")));
        new WebDriverWait(driver, TIMEOUT_TIMER).until(ExpectedConditions.elementToBeClickable(By.id("check-all")));
        WebElement checkbox = driver.findElement(By.id("check-all"));
        checkbox.click();

        new WebDriverWait(driver, TIMEOUT_TIMER).until(ExpectedConditions.elementToBeClickable(By.id("bulkDelete")));
        WebElement bulkDeleteBtn = driver.findElement(By.id("bulkDelete"));
        bulkDeleteBtn.click();
        // Click confirm button to confirm delete
        new WebDriverWait(driver, TIMEOUT_TIMER)
                .until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(text(),'confirm')]")));
        WebElement confirmBtn = driver.findElement(By.xpath("//button[contains(text(),'confirm')]"));
        confirmBtn.click();

    }

    public void clickReload(String response) throws IOException {
        CloseableHttpResponse responseData = this.createMockedHTTPResponse(response, 200);
        Mockito.doReturn(responseData).when(mockedHttpClient)
                .execute(Mockito.argThat(request -> (request).getURI().toString().contains("subscriptions")));
        new WebDriverWait(driver, TIMEOUT_TIMER).until(ExpectedConditions.elementToBeClickable(By.id("reloadButton")));
        WebElement reloadBtn = driver.findElement(By.id("reloadButton"));
        reloadBtn.click();
    }

    public void clickGetTemplate(String response) throws IOException {
        CloseableHttpResponse responseData = this.createMockedHTTPResponse(response, 200);
        Mockito.doReturn(responseData).when(mockedHttpClient)
                .execute(Mockito.argThat(request -> (request).getURI().toString().contains("subscriptions")));
        new WebDriverWait(driver, TIMEOUT_TIMER)
                .until(ExpectedConditions.elementToBeClickable(By.id("getTemplateButton")));
        WebElement getTemplateBtn = driver.findElement(By.id("getTemplateButton"));
        getTemplateBtn.click();
    }

    public void clickFormsSaveBtn(String response) throws IOException {
        CloseableHttpResponse responseData = this.createMockedHTTPResponse(response, 200);
<<<<<<< HEAD

=======
>>>>>>> 0e89adf4b5a783ced84288409197965f5515665f
        Mockito.doReturn(responseData).when(mockedHttpClient)
                .execute(Mockito.argThat(request -> (request).getURI().toString().contains("subscriptions")));
        new WebDriverWait(driver, TIMEOUT_TIMER).until(ExpectedConditions.elementToBeClickable(By.id("btnSave")));
        WebElement saveBtn = driver.findElement(By.id("btnSave"));
        saveBtn.click();
    }

    public void clickKVbtn(String loc) {
        new WebDriverWait(driver, TIMEOUT_TIMER).until(ExpectedConditions.elementToBeClickable(By.id(loc)));
        WebElement kvBtn = driver.findElement(By.id(loc));
        kvBtn.click();
    }

    public void selectDropdown(String loc, String value) {
        new WebDriverWait(driver, TIMEOUT_TIMER).until(ExpectedConditions.elementToBeClickable(By.id(loc)));
        WebElement selectEle = driver.findElement(By.id(loc));
        Select dropdown = new Select(selectEle);
        dropdown.selectByVisibleText(value);
    }

    public String getValueFromSelect() {
        new WebDriverWait(driver, TIMEOUT_TIMER)
                .until(ExpectedConditions.elementToBeClickable(By.id("notificationType")));
        WebElement selectNotificationType = driver.findElement(By.id("notificationType"));
        Select dropdown = new Select(selectNotificationType);
        return dropdown.getFirstSelectedOption().getText();
    }

    public String getValueFromElement() {
        new WebDriverWait(driver, TIMEOUT_TIMER).until(ExpectedConditions.elementToBeClickable(By.id("metaData")));
        WebElement metaTxt = driver.findElement(By.id("metaData"));
        return metaTxt.getAttribute("value");
    }

    public void addFieldValue(String loc, String value) {
        new WebDriverWait(driver, TIMEOUT_TIMER).until(ExpectedConditions.elementToBeClickable(By.id(loc)));
        WebElement ele = driver.findElement(By.id(loc));
        ele.clear();
        ele.sendKeys(value);
    }

    public void clickUploadSubscriptionFunctionality(String filePath, String subUploadResponse) throws IOException {
        CloseableHttpResponse responseData = this.createMockedHTTPResponse(subUploadResponse, 200);
        Mockito.doReturn(responseData).when(mockedHttpClient)
                .execute(Mockito.argThat(request -> (request).getURI().toString().contains("subscriptions")));

        new WebDriverWait(driver, TIMEOUT_TIMER)
                .until(ExpectedConditions.presenceOfElementLocated(By.id("upload_sub")));
<<<<<<< HEAD
        // >>>>>>> fe413762aead0916f28b6d8e890a22455b9f999d
=======
>>>>>>> 0e89adf4b5a783ced84288409197965f5515665f
        WebElement uploadInputField = driver.findElement(By.id("upload_sub"));
        uploadInputField.sendKeys(filePath);
    }


    public boolean presenceOfClickGetTemplateButton() {
        try {
            driver.findElement(By.className("get_subscription_template"));
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    // <<<<<<< HEAD
    // public void clickDownloadGetTemplate(String responseData)
    // throws ClientProtocolException, IOException, InterruptedException {
    // CloseableHttpResponse response = this.createMockedHTTPResponse(responseData,
    // 200);
    // Mockito.doReturn(response).when(mockedHttpClient).execute(Mockito.argThat(
    // request -> ((HttpRequestBase)
    // request).getURI().toString().contains("/subscriptionsTemplate")));
    // =======
    public void clickDownloadGetTemplate(String responseData) throws IOException {
        CloseableHttpResponse response = this.createMockedHTTPResponse(responseData, 200);
        Mockito.doReturn(response).when(mockedHttpClient)
                .execute(Mockito.argThat(request -> (request).getURI().toString().contains("/subscriptionsTemplate")));
        new WebDriverWait(driver, TIMEOUT_TIMER)
                .until(ExpectedConditions.elementToBeClickable(By.className("get_subscription_template")));
<<<<<<< HEAD
        // >>>>>>> fe413762aead0916f28b6d8e890a22455b9f999d
=======
>>>>>>> 0e89adf4b5a783ced84288409197965f5515665f
        WebElement getTemplateButton = driver.findElement(By.className("get_subscription_template"));
        getTemplateButton.click();
    }

    public void clickViewBtn() {
<<<<<<< HEAD
        // <<<<<<< HEAD
        // WebElement viewBtn = wait
        // .until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(text(),'View')]")));
        // =======
=======
>>>>>>> 0e89adf4b5a783ced84288409197965f5515665f
        new WebDriverWait(driver, TIMEOUT_TIMER)
                .until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(text(),'View')]")));
        WebElement viewBtn = driver.findElement(By.xpath("//button[contains(text(),'View')]"));
        // >>>>>>> fe413762aead0916f28b6d8e890a22455b9f999d
        viewBtn.click();
    }

    public void clickFormCloseBtn() {

        new WebDriverWait(driver, TIMEOUT_TIMER).until(ExpectedConditions.elementToBeClickable(By.className("close")));
        WebElement viewBtn = driver.findElement(By.className("close"));
        viewBtn.click();
    }

    public String getSubscriptionNameFromSubscription() {
        new WebDriverWait(driver, TIMEOUT_TIMER)
                .until(ExpectedConditions.elementToBeClickable(By.xpath("//tr[@class='odd']/td[2]")));
        WebElement subscriptionNameElement = driver.findElement(By.xpath("//tr[@class='odd']/td[2]"));
        return subscriptionNameElement.getText();
    }

<<<<<<< HEAD

    public Boolean buttonExist(String loc) {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(By.xpath(loc)));
=======
    public Boolean buttonExist(String loc) {
        try {
            new WebDriverWait(driver, TIMEOUT_TIMER).until(ExpectedConditions.elementToBeClickable(By.xpath(loc)));
>>>>>>> 0e89adf4b5a783ced84288409197965f5515665f
        } catch (Exception e) {
            return false;
        }
        return true;
    }

<<<<<<< HEAD
    public void clickReloadLDAP(String response, String responseAuth)
            throws ClientProtocolException, IOException, InterruptedException {
=======
    public void clickReloadLDAP(String response, String responseAuth) throws IOException {
>>>>>>> 0e89adf4b5a783ced84288409197965f5515665f
        CloseableHttpResponse responseData = this.createMockedHTTPResponse(response, 200);
        CloseableHttpResponse responseDataAuth = this.createMockedHTTPResponse(responseAuth, 200);
        Mockito.doReturn(responseData).when(mockedHttpClient).execute(
                Mockito.argThat(request -> ((HttpRequestBase) request).getURI().toString().contains("subscriptions")));
        Mockito.doReturn(responseDataAuth).when(mockedHttpClient)
                .execute(Mockito.argThat(request -> ((HttpRequestBase) request).getURI().toString().contains("auth")));
<<<<<<< HEAD
        WebElement reloadBtn = wait.until(ExpectedConditions.elementToBeClickable(By.id("reloadButton")));
=======
        WebElement reloadBtn = new WebDriverWait(driver, TIMEOUT_TIMER)
                .until(ExpectedConditions.elementToBeClickable(By.id("reloadButton")));
>>>>>>> 0e89adf4b5a783ced84288409197965f5515665f
        reloadBtn.click();
    }
}
