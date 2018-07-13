package com.ericsson.ei.frontend.pageobjects;

import java.io.IOException;
import org.apache.http.client.ClientProtocolException;
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
    WebDriverWait wait = new WebDriverWait(driver, 10);

    public SubscriptionPage(CloseableHttpClient mockedHttpClient, FirefoxDriver driver, String baseUrl)
            throws ClientProtocolException, IOException {

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
        WebElement addSubscriptionBtn = wait.until(ExpectedConditions.elementToBeClickable(By.id("addSubscription")));
        addSubscriptionBtn.click();
    }

    public void clickFormsCancelBtn() {
        // <<<<<<< HEAD
        // WebElement cancelBtn =
        // wait.until(ExpectedConditions.elementToBeClickable(By.id("cancelButton")));
        // cancelBtn.click();
        // }
        //
        // public void clickBulkDelete(String response) throws ClientProtocolException,
        // IOException, InterruptedException {
        // CloseableHttpResponse responseData = this.createMockedHTTPResponse(response,
        // 200);
        // Mockito.doReturn(responseData).when(mockedHttpClient).execute(
        // Mockito.argThat(request -> ((HttpRequestBase)
        // request).getURI().toString().contains("subscriptions")));
        // WebElement checkbox =
        // wait.until(ExpectedConditions.elementToBeClickable(By.id("check-all")));
        // checkbox.click();
        // WebElement bulkDeleteBtn =
        // wait.until(ExpectedConditions.elementToBeClickable(By.id("bulkDelete")));
        // bulkDeleteBtn.click();
        // // Click confirm button to confirm delete
        // wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(text(),'confirm')]"))).click();
        //
        // }
        //
        // public void clickReloadLDAP(String response, String responseAuth)
        // throws ClientProtocolException, IOException, InterruptedException {
        // CloseableHttpResponse responseData = this.createMockedHTTPResponse(response,
        // 200);
        // CloseableHttpResponse responseDataAuth =
        // this.createMockedHTTPResponse(responseAuth, 200);
        // Mockito.doReturn(responseData).when(mockedHttpClient).execute(
        // Mockito.argThat(request -> ((HttpRequestBase)
        // request).getURI().toString().contains("subscriptions")));
        // Mockito.doReturn(responseDataAuth).when(mockedHttpClient)
        // .execute(Mockito.argThat(request -> ((HttpRequestBase)
        // request).getURI().toString().contains("auth")));
        // WebElement reloadBtn =
        // wait.until(ExpectedConditions.elementToBeClickable(By.id("reloadButton")));
        // reloadBtn.click();
        // }
        //
        // public void clickReload(String response) throws ClientProtocolException,
        // IOException, InterruptedException {
        // CloseableHttpResponse responseData = this.createMockedHTTPResponse(response,
        // 200);
        // Mockito.doReturn(responseData).when(mockedHttpClient).execute(
        // Mockito.argThat(request -> ((HttpRequestBase)
        // request).getURI().toString().contains("subscriptions")));
        // WebElement reloadBtn =
        // wait.until(ExpectedConditions.elementToBeClickable(By.id("reloadButton")));
        // reloadBtn.click();
        // }
        //
        // public void clickGetTemplate(String response) throws ClientProtocolException,
        // IOException, InterruptedException {
        // CloseableHttpResponse responseData = this.createMockedHTTPResponse(response,
        // 200);
        // Mockito.doReturn(responseData).when(mockedHttpClient).execute(
        // Mockito.argThat(request -> ((HttpRequestBase)
        // request).getURI().toString().contains("subscriptions")));
        // WebElement getTemplateBtn =
        // wait.until(ExpectedConditions.elementToBeClickable(By.id("getTemplateButton")));
        // getTemplateBtn.click();
        // }
        //
        // public void clickFormsSaveBtn(String response) throws
        // ClientProtocolException, IOException, InterruptedException {
        // CloseableHttpResponse responseData = this.createMockedHTTPResponse(response,
        // 200);
        //
        // Mockito.doReturn(responseData).when(mockedHttpClient).execute(
        // Mockito.argThat(request -> ((HttpRequestBase)
        // request).getURI().toString().contains("subscriptions")));
        // WebElement saveBtn =
        // wait.until(ExpectedConditions.elementToBeClickable(By.id("btnSave")));
        // =======
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

        Mockito.doReturn(responseData).when(mockedHttpClient)
                .execute(Mockito.argThat(request -> (request).getURI().toString().contains("subscriptions")));
        new WebDriverWait(driver, TIMEOUT_TIMER).until(ExpectedConditions.elementToBeClickable(By.id("btnSave")));
        WebElement saveBtn = driver.findElement(By.id("btnSave"));
        // >>>>>>> fe413762aead0916f28b6d8e890a22455b9f999d
        saveBtn.click();
    }

    public void clickKVbtn(String loc) {
        // <<<<<<< HEAD
        // WebElement kvBtn =
        // wait.until(ExpectedConditions.elementToBeClickable(By.id(loc)));
        // =======
        new WebDriverWait(driver, TIMEOUT_TIMER).until(ExpectedConditions.elementToBeClickable(By.id(loc)));
        WebElement kvBtn = driver.findElement(By.id(loc));
        // >>>>>>> fe413762aead0916f28b6d8e890a22455b9f999d
        kvBtn.click();
    }

    public void selectDropdown(String loc, String value) {
        // <<<<<<< HEAD
        // WebElement selectEle =
        // wait.until(ExpectedConditions.elementToBeClickable(By.id(loc)));
        // =======
        new WebDriverWait(driver, TIMEOUT_TIMER).until(ExpectedConditions.elementToBeClickable(By.id(loc)));
        WebElement selectEle = driver.findElement(By.id(loc));
        // >>>>>>> fe413762aead0916f28b6d8e890a22455b9f999d
        Select dropdown = new Select(selectEle);
        dropdown.selectByVisibleText(value);
    }

    public String getValueFromSelect() {
        // <<<<<<< HEAD
        // WebElement selectNotificationType = wait
        // .until(ExpectedConditions.elementToBeClickable(By.id("notificationType")));
        // =======
        new WebDriverWait(driver, TIMEOUT_TIMER)
                .until(ExpectedConditions.elementToBeClickable(By.id("notificationType")));
        WebElement selectNotificationType = driver.findElement(By.id("notificationType"));
        // >>>>>>> fe413762aead0916f28b6d8e890a22455b9f999d
        Select dropdown = new Select(selectNotificationType);
        return dropdown.getFirstSelectedOption().getText();
    }

    public String getValueFromElement() {
        // <<<<<<< HEAD
        // WebElement metaTxt =
        // wait.until(ExpectedConditions.elementToBeClickable(By.id("metaData")));
        // =======
        new WebDriverWait(driver, TIMEOUT_TIMER).until(ExpectedConditions.elementToBeClickable(By.id("metaData")));
        WebElement metaTxt = driver.findElement(By.id("metaData"));
        // >>>>>>> fe413762aead0916f28b6d8e890a22455b9f999d
        return metaTxt.getAttribute("value");
    }

    public void addFieldValue(String loc, String value) {
        // <<<<<<< HEAD
        // WebElement ele =
        // wait.until(ExpectedConditions.elementToBeClickable(By.id(loc)));
        // =======
        new WebDriverWait(driver, TIMEOUT_TIMER).until(ExpectedConditions.elementToBeClickable(By.id(loc)));
        WebElement ele = driver.findElement(By.id(loc));
        // >>>>>>> fe413762aead0916f28b6d8e890a22455b9f999d
        ele.clear();
        ele.sendKeys(value);
    }

    // <<<<<<< HEAD
    // public void clickUploadSubscriptionFunctionality(String filePath, String
    // subUploadResponse)
    // throws ClientProtocolException, IOException, InterruptedException {
    // CloseableHttpResponse responseData =
    // this.createMockedHTTPResponse(subUploadResponse, 200);
    // Mockito.doReturn(responseData).when(mockedHttpClient).execute(
    // Mockito.argThat(request -> ((HttpRequestBase)
    // request).getURI().toString().contains("subscriptions")));
    //
    // =======
    public void clickUploadSubscriptionFunctionality(String filePath, String subUploadResponse) throws IOException {
        CloseableHttpResponse responseData = this.createMockedHTTPResponse(subUploadResponse, 200);
        Mockito.doReturn(responseData).when(mockedHttpClient)
                .execute(Mockito.argThat(request -> (request).getURI().toString().contains("subscriptions")));

        new WebDriverWait(driver, TIMEOUT_TIMER)
                .until(ExpectedConditions.presenceOfElementLocated(By.id("upload_sub")));
        // >>>>>>> fe413762aead0916f28b6d8e890a22455b9f999d
        WebElement uploadInputField = driver.findElement(By.id("upload_sub"));
        uploadInputField.sendKeys(filePath);
    }

    // <<<<<<< HEAD
    // public Boolean presenceOfClickGetTemplateButton() {
    // =======
    public boolean presenceOfClickGetTemplateButton() {
        // >>>>>>> fe413762aead0916f28b6d8e890a22455b9f999d
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
        // >>>>>>> fe413762aead0916f28b6d8e890a22455b9f999d
        WebElement getTemplateButton = driver.findElement(By.className("get_subscription_template"));
        getTemplateButton.click();
    }

    public void clickViewBtn() {
        // <<<<<<< HEAD
        // WebElement viewBtn = wait
        // .until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(text(),'View')]")));
        // =======
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

    // >>>>>>> fe413762aead0916f28b6d8e890a22455b9f999d

    // <<<<<<< HEAD

    public Boolean buttonExist(String loc) {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(By.xpath(loc)));
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public void clickReloadLDAP(String response, String responseAuth)
            throws ClientProtocolException, IOException, InterruptedException {
        CloseableHttpResponse responseData = this.createMockedHTTPResponse(response, 200);
        CloseableHttpResponse responseDataAuth = this.createMockedHTTPResponse(responseAuth, 200);
        Mockito.doReturn(responseData).when(mockedHttpClient).execute(
                Mockito.argThat(request -> ((HttpRequestBase) request).getURI().toString().contains("subscriptions")));
        Mockito.doReturn(responseDataAuth).when(mockedHttpClient)
                .execute(Mockito.argThat(request -> ((HttpRequestBase) request).getURI().toString().contains("auth")));
        WebElement reloadBtn = wait.until(ExpectedConditions.elementToBeClickable(By.id("reloadButton")));
        reloadBtn.click();
    }

}
